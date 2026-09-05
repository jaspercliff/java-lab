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