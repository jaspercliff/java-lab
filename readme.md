# readme 

## base 

- 整个工程通用类

## build logic

gradle 依赖管理 

## common 

- 单独一个工程 通用模块 其他项目也可以导入

## container 

- docker-compose-support使用的容器

## dataAccess 

- jdbc 
- mybatis 
- sharding

## dataStructure 

- 数据结构

## infra-redis 

- jedis-spring-data-redis 
- jedisDemo 使用的testContainer(运行一个 JUnit 测试类 → 自动启动 Redis Docker 容器 → 测试 → 自动停止/清理容器 )

## quartz-demo

- Quartz 全量实用知识点：Job/Trigger/Listener/Calendar/Misfire/动态 REST 管理/Spring 注入
- 同时提供 RAMJobStore 与 JDBCJobStore（MySQL 持久化 + 集群）两套配置
- 详细知识点地图、curl 示例与生产实践见 [quartz-demo/README.md](spring-boot/spring-boot-starter-quartz-demo/README.md)
