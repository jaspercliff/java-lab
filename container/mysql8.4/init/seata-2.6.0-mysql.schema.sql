--
-- licensed to the apache software foundation (asf) under one or more
-- contributor license agreements.  see the notice file distributed with
-- this work for additional information regarding copyright ownership.
-- the asf licenses this file to you under the apache license, version 2.0
-- (the "license"); you may not use this file except in compliance with
-- the license.  you may obtain a copy of the license at
--
--     http://www.apache.org/licenses/license-2.0
--
-- unless required by applicable law or agreed to in writing, software
-- distributed under the license is distributed on an "as is" basis,
-- without warranties or conditions of any kind, either express or implied.
-- see the license for the specific language governing permissions and
-- limitations under the license.
--
CREATE DATABASE IF NOT EXISTS `seata`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE `seata`;
-- -------------------------------- the script used when storemode is 'db' --------------------------------
-- the table to store globalsession data
create table if not exists `global_table`
(
    `xid`                       varchar(128) not null,
    `transaction_id`            bigint,
    `status`                    tinyint      not null,
    `application_id`            varchar(32),
    `transaction_service_group` varchar(32),
    `transaction_name`          varchar(128),
    `timeout`                   int,
    `begin_time`                bigint,
    `application_data`          varchar(2000),
    `gmt_create`                datetime,
    `gmt_modified`              datetime,
    primary key (`xid`),
    key `idx_status_gmt_modified` (`status` , `gmt_modified`),
    key `idx_transaction_id` (`transaction_id`)
) engine = innodb
  default charset = utf8mb4;

-- the table to store branchsession data
create table if not exists `branch_table`
(
    `branch_id`         bigint       not null,
    `xid`               varchar(128) not null,
    `transaction_id`    bigint,
    `resource_group_id` varchar(32),
    `resource_id`       varchar(256),
    `branch_type`       varchar(8),
    `status`            tinyint,
    `client_id`         varchar(64),
    `application_data`  varchar(2000),
    `gmt_create`        datetime(6),
    `gmt_modified`      datetime(6),
    primary key (`branch_id`),
    key `idx_xid` (`xid`)
) engine = innodb
  default charset = utf8mb4;

-- the table to store lock data
create table if not exists `lock_table`
(
    `row_key`        varchar(128) not null,
    `xid`            varchar(128),
    `transaction_id` bigint,
    `branch_id`      bigint       not null,
    `resource_id`    varchar(256),
    `table_name`     varchar(32),
    `pk`             varchar(36),
    `status`         tinyint      not null default '0' comment '0:locked ,1:rollbacking',
    `gmt_create`     datetime,
    `gmt_modified`   datetime,
    primary key (`row_key`),
    key `idx_status` (`status`),
    key `idx_branch_id` (`branch_id`),
    key `idx_xid` (`xid`)
) engine = innodb
  default charset = utf8mb4;

create table if not exists `distributed_lock`
(
    `lock_key`       char(20) not null,
    `lock_value`     varchar(20) not null,
    `expire`         bigint,
    primary key (`lock_key`)
) engine = innodb
  default charset = utf8mb4;

insert into `distributed_lock` (lock_key, lock_value, expire) values ('asynccommitting', ' ', 0);
insert into `distributed_lock` (lock_key, lock_value, expire) values ('retrycommitting', ' ', 0);
insert into `distributed_lock` (lock_key, lock_value, expire) values ('retryrollbacking', ' ', 0);
insert into `distributed_lock` (lock_key, lock_value, expire) values ('txtimeoutcheck', ' ', 0);


create table if not exists `vgroup_table`
(
    `vgroup`    varchar(255),
    `namespace` varchar(255),
    `cluster`   varchar(255),
    unique key `idx_vgroup_namespace_cluster` (`vgroup`,`namespace`,`cluster`)
) engine = innodb
  default charset = utf8mb4;
