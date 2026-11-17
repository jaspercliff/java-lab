package com.jasper.validation;

import static org.assertj.core.api.Assertions.assertThat;

import com.jasper.validation.model.Address;
import com.jasper.validation.model.UserCreateRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/** fail-fast 开关：用独立 Spring 上下文覆盖 validation.fail-fast=true 验证「遇到第一个错就停」。 */
@SpringBootTest(properties = "validation.fail-fast=true")
class FailFastTest {

    @Autowired
    private Validator validator;

    @Test
    void onlyFirstViolationIsCollected() {
        UserCreateRequest req = UserCreateRequest.builder()
                .username("newuser")
                .phone("123")   // 错 1
                .email("bad")   // 错 2
                .age(200)       // 错 3
                .gender(null)   // 错 4
                .birthday(LocalDate.now()) // 错 5
                .address(new Address("", "")) // 级联错 6/7
                .shippingAddresses(List.of())
                .activeFrom(LocalDate.of(2026, 2, 1))
                .activeTo(LocalDate.of(2026, 1, 1))
                .agreed(false)
                .build();

        var violations = validator.validate(req);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next()).isInstanceOf(ConstraintViolation.class);
    }
}
