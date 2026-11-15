package com.jasper.dto;

public record UpdateUserRequest(
        Long deptId,
        String nickname,
        String avatar,
        String email,
        String phone,
        Short status
) {
}