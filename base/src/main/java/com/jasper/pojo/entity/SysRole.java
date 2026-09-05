package com.jasper.pojo.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SysRole implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String roleName;
    private String roleKey;
    private Integer sortOrder;
    private Integer status;
    private Integer isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}