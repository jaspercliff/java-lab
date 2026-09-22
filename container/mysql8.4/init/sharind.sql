CREATE DATABASE IF NOT EXISTS `horizontal-table` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `horizontal-table`;
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