package com.jasper.service.impl;

import com.jasper.anno.ClassLoggable;
import com.jasper.anno.Loggable;
import com.jasper.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ClassLoggable
public class UserServiceImpl implements UserService {

    /**
     * 连接点：程序执行过程中，可以被 AOP 插入逻辑的位置
     */
    @Loggable("新增用户")
    public void createUser(String id) {
        log.info("createUser");
    }
}
