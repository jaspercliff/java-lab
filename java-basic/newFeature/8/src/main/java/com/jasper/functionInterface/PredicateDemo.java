package com.jasper.functionInterface;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Predicate;

@Slf4j
public class PredicateDemo {
    public static void main(String[] args) {

        Predicate<Integer> isEven = number -> number % 2 == 0;
        Predicate<Integer> isGreaterThanTen = number -> number > 10;
        log.info("isEven.test(4): {}", isEven.test(4)); // true
        log.info("isEven.test(5): {}", isEven.test(5)); // false
        log.info("isGreaterThanTen.test(15): {}", isGreaterThanTen.test(15)); // true
        log.info("isGreaterThanTen.test(5): {}", isGreaterThanTen.test(5)); // false

        // and
         Predicate<Integer> isEvenAndGreaterThanTen = isEven.and(isGreaterThanTen);
         log.info("isEvenAndGreaterThanTen.test(12): {}", isEvenAndGreaterThanTen.test(12)); // true
         log.info("isEvenAndGreaterThanTen.test(8): {}", isEvenAndGreaterThanTen.test(8)); // false

         //or
        Predicate<Integer> isEvenOrGreaterThanTen = isEven.or(isGreaterThanTen);
        log.info("isEvenOrGreaterThanTen.test(12): {}", isEvenOrGreaterThanTen.test(12)); // true
        log.info("isEvenOrGreaterThanTen.test(8): {}", isEvenOrGreaterThanTen.test(8)); // false

        //negate
        Predicate<Integer> isNotEven = isEven.negate();
        log.info("isNotEven.test(4): {}", isNotEven.test(4)); // false
        log.info("isNotEven.test(5): {}", isNotEven.test(5)); // true
    }
}
