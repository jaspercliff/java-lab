package com.jasper.distributedLock.redis;

import java.time.Duration;

/**
 * @author jasper
 * @since 2026-08-30 <br>
 */
public interface DistributedLock {

    boolean tryLock(String key, String requestId, Duration leaseTime);

    /**
     * <br>
     * 误删问题： 如果a获取锁成功了 但是还没有释放 线程暂停了 此时锁过期了 但是b线程删除锁的时候根据key直接误删了 <br>
     * 所以拼接 requestid 删除时判断是否相等 <br>
     * 生成唯一reqId 单机jvm加线程id 分布式环境可以使用uuid（超级小概率重复） 也可以自己生成全局唯一id <br>
     * 删除的时候 判断和删除必须原子执行 使用lua脚本
     */
    boolean unlock(String key, String requestId);

    boolean renew(String key, String requestId, Duration leaseTime);
}
