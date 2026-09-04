package com.jasper.pojo;

import lombok.Data;

import java.util.List;

/**
 * @version 1.0
 * @Author jasper
 * @Date 2024-09-30
 */
@Data
public class PersonDTO {

	private String name;

	private Integer age;

	private String high;

	private List<String> hobbies;

}
