package com.jasper.dto;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        Long deptId,
        String username,
        String nickname,
        String avatar,
        String email,
        String phone,
        Short status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}