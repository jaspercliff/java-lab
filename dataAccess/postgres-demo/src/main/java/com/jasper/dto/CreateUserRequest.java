package com.jasper.dto;

public record CreateUserRequest(
        Long deptId,
        String username,
        String password,
        String nickname,
        String avatar,
        String email,
        String phone
) {
}