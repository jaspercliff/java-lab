package com.jasper.pojo.dto;

import com.jasper.pojo.entity.SysRole;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SysUserRoleDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String nickname;
    private Integer status;
    private LocalDateTime createdAt;
    private List<SysRole> roles;
}