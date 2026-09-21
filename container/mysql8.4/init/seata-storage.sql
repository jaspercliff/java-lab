CREATE DATABASE IF NOT EXISTS `seata_storage` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `seata_storage`;

-- 创建 Seata AT 模式必须的 undo_log 表
CREATE TABLE IF NOT EXISTS `undo_log` (
                                          `branch_id`     BIGINT       NOT NULL COMMENT 'branch transaction id',
                                          `xid`           VARCHAR(128) NOT NULL COMMENT 'global transaction id',
    `context`       VARCHAR(128) NOT NULL COMMENT 'undo_log context,such as serialization',
    `rollback_info` LONGBLOB     NOT NULL COMMENT 'rollback info',
    `log_status`    INT(11)      NOT NULL COMMENT '0:normal status,1:defense status',
    `log_created`   DATETIME(6)  NOT NULL COMMENT 'create datetime',
    `log_modified`  DATETIME(6)  NOT NULL COMMENT 'modify datetime',
    UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`),
    INDEX `ix_log_created` (`log_created`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='AT transaction mode undo table';

-- 创建库存表
CREATE TABLE IF NOT EXISTS `storage_tbl` (
                                             `id`             INT(11) NOT NULL AUTO_INCREMENT,
    `commodity_code` VARCHAR(255) DEFAULT NULL,
    `count`          INT(11) DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY (`commodity_code`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 预埋测试数据
INSERT INTO `storage_tbl` (`commodity_code`, `count`) VALUES ('ITEM_001', 1000) ON DUPLICATE KEY UPDATE `count`=1000;