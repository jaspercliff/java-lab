package com.jasper.validation;

import com.jasper.validation.constraint.FatalPayload;
import com.jasper.validation.constraint.group.CreateGroup;
import com.jasper.validation.constraint.group.OrderedChecks;
import com.jasper.validation.constraint.group.UpdateGroup;
import com.jasper.validation.enums.GenderEnum;
import com.jasper.validation.model.Address;
import com.jasper.validation.model.UserCreateRequest;
import com.jasper.validation.service.MethodValidatedService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 直接用 jakarta Validator 验证各知识点（不经过 HTTP），断言「约束确实生效」。
 */
@SpringBootTest
class ValidatorKnowledgeTest {

    @Autowired
    private Validator validator;

    @Autowired
    private MethodValidatedService methodValidatedService;

    private static final Locale JVM_DEFAULT = Locale.getDefault();

    @BeforeEach
    void useChineseAsJvmDefault() {
        // 直接调用 validator.validate()（不经过 Spring MVC）时，HV 用 JVM 默认 locale 做消息插值，
        // LocaleContextHolder 只在 MVC 路径生效。测试里显式设中文，保证断言稳定。
        Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
    }

    @AfterEach
    void resetLocale() {
        Locale.setDefault(JVM_DEFAULT);
        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    void builtInConstraintsAndCascade() {
        UserCreateRequest req = validRequest();
        req.setPhone("123");           // @Phone
        req.setEmail("not-an-email");  // @Email
        req.setAge(200);               // @Max
        req.setBirthday(LocalDate.now());// @Past（今天不算过去）
        req.setAgreed(false);          // @AssertTrue
        req.setAddress(new Address("", "x")); // @Valid 级联 → province 为空
        req.setShippingAddresses(List.of(new Address("A", "B"), new Address("", ""))); // 集合元素级联

        Set<String> fields = validator.validate(req).stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertThat(fields).contains("phone", "email", "age", "birthday", "agreed", "address.province",
                "shippingAddresses[1].province");
    }

    @Test
    void createGroupOnlyChecksCreateFields() {
        UserCreateRequest req = validRequest();
        req.setUsername("ad"); // 违反 @Size(3~20)，只在 CreateGroup 生效
        req.setPhone("123");   // Default 组的错误：校验 CreateGroup 时不应出现

        Set<String> fields = validator.validate(req, CreateGroup.class).stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertThat(fields).containsExactly("username");
    }

    @Test
    void uniqueUsernameValidatorUsesSpringBean() {
        UserCreateRequest req = validRequest();
        req.setUsername("admin"); // UserService 里已占用

        List<ConstraintViolation<UserCreateRequest>> vs = validator.validate(req, CreateGroup.class).stream().toList();

        assertThat(vs).hasSize(1);
        assertThat(vs.get(0).getPropertyPath().toString()).isEqualTo("username");
        assertThat(vs.get(0).getMessage()).contains("占用");
    }

    @Test
    void updateGroupOnlyChecksId() {
        UserCreateRequest req = validRequest(); // id 为 null，违反了 UpdateGroup 的 @NotNull
        Set<String> fields = validator.validate(req, UpdateGroup.class).stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());
        assertThat(fields).containsExactly("id");
    }

    @Test
    void groupSequenceStopsAtFirstFailingGroup() {
        UserCreateRequest req = validRequest();
        req.setPhone("123");   // Default 组错误
        req.setUsername("ad"); // CreateGroup 组错误

        Set<String> fields = validator.validate(req, OrderedChecks.class).stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());

        // Default 组有错 → CreateGroup 不再执行
        assertThat(fields).contains("phone").doesNotContain("username");
    }

    @Test
    void classLevelConstraintAttachesToEndFieldAndCarriesPayload() {
        UserCreateRequest req = validRequest();
        req.setActiveFrom(LocalDate.of(2026, 2, 1));
        req.setActiveTo(LocalDate.of(2026, 1, 1)); // 反了

        ConstraintViolation<UserCreateRequest> dateRange = validator.validate(req).stream()
                .filter(v -> v.getMessage().contains("生效日期"))
                .findFirst().orElseThrow();

        assertThat(dateRange.getPropertyPath().toString()).isEqualTo("activeTo"); // 错误挂到具体字段

        req.setPhone("123");
        ConstraintViolation<UserCreateRequest> phone = validator.validate(req).stream()
                .filter(v -> v.getPropertyPath().toString().equals("phone"))
                .findFirst().orElseThrow();
        assertThat(phone.getConstraintDescriptor().getPayload()).contains(FatalPayload.class);
    }

    @Test
    void messageIsResolvedFromSpringMessageSourceByLocale() {
        UserCreateRequest req = validRequest();
        req.setPhone("123");

        Locale.setDefault(Locale.ENGLISH);
        String en = validator.validate(req).stream()
                .filter(v -> v.getPropertyPath().toString().equals("phone"))
                .findFirst().orElseThrow().getMessage();
        assertThat(en).isEqualTo("invalid phone number");

        Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
        String zh = validator.validate(req).stream()
                .filter(v -> v.getPropertyPath().toString().equals("phone"))
                .findFirst().orElseThrow().getMessage();
        assertThat(zh).isEqualTo("手机号格式不正确");
    }

    @Test
    void methodValidationThrowsConstraintViolationException() {
        assertThatThrownBy(() -> methodValidatedService.query("  ", 1, 10))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("关键字");

        assertThat(methodValidatedService.query("ok", 1, 10)).contains("keyword=ok");
    }

    @Test
    void methodParameterCollectionCascade() {
        assertThatThrownBy(() -> methodValidatedService.countAddresses(
                List.of(new MethodValidatedService.AddressForValidation(""))))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("城市");
    }

    @Test
    void validatePropertyAndValidateValue() {
        UserCreateRequest req = validRequest();
        req.setEmail("bad");
        assertThat(validator.validateProperty(req, "email")).hasSize(1);
        assertThat(validator.validateValue(UserCreateRequest.class, "age", -1)).hasSize(1);
        // 只校验 email 属性时，其他字段（包括 username 这种分组约束）不参与
        assertThat(validator.validateProperty(req, "email").stream()
                .allMatch(v -> v.getPropertyPath().toString().equals("email"))).isTrue();
    }

    private static UserCreateRequest validRequest() {
        Address address = new Address("浙江省", "杭州市");
        return UserCreateRequest.builder()
                .username("newuser")
                .phone("13800138000")
                .email("a@b.com")
                .password("abc12345")
                .gender(GenderEnum.MALE)
                .age(25)
                .birthday(LocalDate.of(2000, 1, 1))
                .address(address)
                .shippingAddresses(List.of(address))
                .activeFrom(LocalDate.of(2026, 1, 1))
                .activeTo(LocalDate.of(2026, 12, 31))
                .agreed(true)
                .build();
    }
}
