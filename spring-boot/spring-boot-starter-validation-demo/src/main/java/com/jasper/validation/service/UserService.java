package com.jasper.validation.service;

import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 维护一个「已占用用户名」集合，供 {@code @UniqueUsername} 校验器查重。
 */
@Service
public class UserService {

    private static final Set<String> TAKEN = Set.of("admin", "root", "jasper");

    public boolean existsByUsername(String username) {
        return TAKEN.contains(username);
    }
}
