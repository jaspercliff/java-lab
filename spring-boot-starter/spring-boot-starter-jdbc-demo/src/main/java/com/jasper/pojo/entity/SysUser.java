package com.jasper.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysUser {

    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String email;

    private String phone;

    /**
     * 0-禁用，1-正常
     */
    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}