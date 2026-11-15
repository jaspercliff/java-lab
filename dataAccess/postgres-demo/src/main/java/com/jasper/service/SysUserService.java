package com.jasper.service;

import com.jasper.convert.SysUserConvert;
import com.jasper.dto.CreateUserRequest;
import com.jasper.dto.UpdateUserRequest;
import com.jasper.dto.UserResponse;
import com.jasper.pojo.entity.SysUser;
import com.jasper.repository.SysUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysUserService {

    private final SysUserRepository repository;

    @Transactional
    public UserResponse create(CreateUserRequest request) {

        SysUser sysUser = SysUserConvert.INSTANCE.toSysUser(request);
        return toResponse(repository.insert(sysUser));
    }

    public UserResponse findById(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
    }

    public List<UserResponse> findPage(int page, int size) {
        return repository.findPage(page, size)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {

        SysUser user = new SysUser(
                id,
                request.deptId(),
                null,
                null,
                request.nickname(),
                request.avatar(),
                request.email(),
                request.phone(),
                request.status(),
                null,
                null,
                null
        );

        return repository.update(id, user)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.deleteById(id)) {
            throw new IllegalArgumentException("用户不存在");
        }
    }

    private UserResponse toResponse(SysUser user) {
       return SysUserConvert.INSTANCE.toUserResponse(user);
    }
}