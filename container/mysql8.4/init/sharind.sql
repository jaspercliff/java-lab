CREATE DATABASE IF NOT EXISTS `sharding-order-1` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `sharding-order-1`;
CREATE TABLE t_order_1 (
                           id BIGINT NOT NULL COMMENT '订单ID',
                           user_id BIGINT NOT NULL COMMENT '用户ID',
                           order_no VARCHAR(64) NOT NULL COMMENT '订单号',
                           amount DECIMAL(10, 2) NOT NULL COMMENT '订单金额',
                           status TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态',
                           create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                               ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

                           PRIMARY KEY (id),
                           UNIQUE KEY uk_order_no (order_no),
                           KEY idx_user_id (user_id),
                           KEY idx_create_time (create_time)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
    COMMENT = '订单表-1';

CREATE TABLE t_order_2 (
                           id BIGINT NOT NULL COMMENT '订单ID',
                           user_id BIGINT NOT NULL COMMENT '用户ID',
                           order_no VARCHAR(64) NOT NULL COMMENT '订单号',
                           amount DECIMAL(10, 2) NOT NULL COMMENT '订单金额',
                           status TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态',
                           create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                               ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

                           PRIMARY KEY (id),
                           UNIQUE KEY uk_order_no (order_no),
                           KEY idx_user_id (user_id),
                           KEY idx_create_time (create_time)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
    COMMENT = '订单表-2';

CREATE DATABASE IF NOT EXISTS `sharding-order-2` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `sharding-order-2`;
CREATE TABLE t_order_1 (
                           id BIGINT NOT NULL COMMENT '订单ID',
                           user_id BIGINT NOT NULL COMMENT '用户ID',
                           order_no VARCHAR(64) NOT NULL COMMENT '订单号',
                           amount DECIMAL(10, 2) NOT NULL COMMENT '订单金额',
                           status TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态',
                           create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                               ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

                           PRIMARY KEY (id),
                           UNIQUE KEY uk_order_no (order_no),
                           KEY idx_user_id (user_id),
                           KEY idx_create_time (create_time)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
    COMMENT = '订单表-1';

CREATE TABLE t_order_2 (
                           id BIGINT NOT NULL COMMENT '订单ID',
                           user_id BIGINT NOT NULL COMMENT '用户ID',
                           order_no VARCHAR(64) NOT NULL COMMENT '订单号',
                           amount DECIMAL(10, 2) NOT NULL COMMENT '订单金额',
                           status TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态',
                           create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                               ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

                           PRIMARY KEY (id),
                           UNIQUE KEY uk_order_no (order_no),
                           KEY idx_user_id (user_id),
                           KEY idx_create_time (create_time)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
    COMMENT = '订单表-2';


CREATE DATABASE IF NOT EXISTS `sharding-user` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `sharding-user`;

CREATE TABLE `t_user` (
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