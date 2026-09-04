package com.jasper;

import com.jasper.convert.PersonMapper;
import com.jasper.pojo.Person;
import com.jasper.pojo.PersonDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * @version 1.0
 * @Author jasper
 * @Date 2024-09-30
 */
@Slf4j
public class Demo {
    public static void main(String[] args) {
        Person jasper = new Person("jasper", 20, Arrays.asList("smoke","swim"));

        PersonDTO personDTO = PersonMapper.INSTANCE.toPersonDTO(jasper);
        log.info("personDTO = {}", personDTO);
    }
}
