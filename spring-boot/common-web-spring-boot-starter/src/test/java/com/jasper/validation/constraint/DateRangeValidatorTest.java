package com.jasper.validation.constraint;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

class DateRangeValidatorTest {

    private static final String END_FIELD = "end";

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void startBeforeEndPasses() {
        Period period = new Period(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31));
        assertThat(validator.validate(period)).isEmpty();
    }

    @Test
    void startEqualsEndPasses() {
        LocalDate day = LocalDate.of(2026, 6, 1);
        assertThat(validator.validate(new Period(day, day))).isEmpty();
    }

    @Test
    void nullFieldIsIgnored() {
        assertThat(validator.validate(new Period(null, LocalDate.of(2026, 1, 1)))).isEmpty();
        assertThat(validator.validate(new Period(LocalDate.of(2026, 1, 1), null))).isEmpty();
    }

    @Test
    void startAfterEndFailsOnEndField() {
        Period period = new Period(LocalDate.of(2026, 2, 1), LocalDate.of(2026, 1, 1));
        Set<ConstraintViolation<Period>> violations = validator.validate(period);

        assertThat(violations).hasSize(1);
        ConstraintViolation<Period> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo(END_FIELD);
        assertThat(violation.getMessage()).isNotBlank();
    }

    @DateRange(start = "start", end = "end")
    private record Period(LocalDate start, LocalDate end) {}
}
