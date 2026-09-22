# readme 

```zsh 
docker exec -it mysql-slave mysql -uroot -p

STOP REPLICA;
CHANGE REPLICATION SOURCE TO
    SOURCE_HOST='mysql-master',
    SOURCE_PORT=3306,
    SOURCE_USER='repl',
    SOURCE_PASSWORD='passwd',
    SOURCE_AUTO_POSITION=1,
    GET_SOURCE_PUBLIC_KEY = 1;

# 使用 GTID 自动定位复制位置 

START REPLICA;

SHOW REPLICA STATUS;
```
