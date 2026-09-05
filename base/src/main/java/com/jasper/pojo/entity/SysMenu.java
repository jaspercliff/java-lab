package com.jasper.pojo.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SysMenu {
    private Long id;
    private Long parentId;
    private String menuName;
    private String permissionKey;
    private Integer type; // 1-目录，2-菜单，3-按钮
    private String path;
    private String component;
    private String icon;
    private Integer sortOrder;
    private Integer status;
    private Integer isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}