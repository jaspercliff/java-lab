CREATE DATABASE IF NOT EXISTS `java-lab`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE `java-lab`;

DROP TABLE IF EXISTS `sys_role_menu`;
DROP TABLE IF EXISTS `sys_user_role`;
DROP TABLE IF EXISTS `sys_menu`;
DROP TABLE IF EXISTS `sys_role`;
DROP TABLE IF EXISTS `sys_user`;
DROP TABLE IF EXISTS `sys_dept`;

-- 1. 用户表（生产级完整字段：含 dept_id 部门隔离与 is_deleted 逻辑删除）
CREATE TABLE `sys_user` (
                            `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                            `dept_id` BIGINT DEFAULT NULL COMMENT '部门ID',
                            `username` VARCHAR(64) NOT NULL COMMENT '用户名',
                            `password` VARCHAR(255) NOT NULL COMMENT '密码',
                            `nickname` VARCHAR(64) DEFAULT NULL COMMENT '昵称',
                            `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
                            `email` VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
                            `phone` VARCHAR(32) DEFAULT NULL COMMENT '手机号',
                            `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
                            `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
                            `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_username` (`username`),
                            UNIQUE KEY `uk_email` (`email`),
                            UNIQUE KEY `uk_phone` (`phone`),
                            KEY `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 2. 角色表
CREATE TABLE `sys_role` (
                            `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
                            `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
                            `role_key` VARCHAR(50) NOT NULL COMMENT '角色权限字符串',
                            `sort_order` INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
                            `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
                            `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
                            `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_role_key` (`role_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 3. 菜单权限表
CREATE TABLE `sys_menu` (
                            `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
                            `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父菜单ID',
                            `menu_name` VARCHAR(50) NOT NULL COMMENT '菜单名称',
                            `permission_key` VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
                            `type` TINYINT NOT NULL COMMENT '类型：1-目录，2-菜单，3-按钮',
                            `path` VARCHAR(200) DEFAULT NULL COMMENT '路由地址',
                            `component` VARCHAR(200) DEFAULT NULL COMMENT '组件路径',
                            `icon` VARCHAR(100) DEFAULT NULL COMMENT '图标',
                            `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
                            `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
                            `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
                            `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            PRIMARY KEY (`id`),
                            KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单权限表';

-- 4. 用户与角色关联表
CREATE TABLE `sys_user_role` (
                                 `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                 `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                 `role_id` BIGINT NOT NULL COMMENT '角色ID',
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
                                 KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户和角色关联表';

-- 5. 角色与菜单关联表
CREATE TABLE `sys_role_menu` (
                                 `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                 `role_id` BIGINT NOT NULL COMMENT '角色ID',
                                 `menu_id` BIGINT NOT NULL COMMENT '菜单/权限ID',
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`),
                                 KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色和菜单关联表';

CREATE TABLE `sys_dept` (
                            `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '部门ID',
                            `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父部门ID',
                            `ancestors` VARCHAR(500) DEFAULT '' COMMENT '祖级列表',
                            `dept_name` VARCHAR(50) NOT NULL COMMENT '部门名称',
                            `sort_order` INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
                            `leader` VARCHAR(50) DEFAULT NULL COMMENT '负责人',
                            `phone` VARCHAR(32) DEFAULT NULL COMMENT '联系电话',
                            `email` VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
                            `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
                            `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
                            `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            PRIMARY KEY (`id`),
                            KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';