package com.jasper.vt;

import lombok.extern.slf4j.Slf4j;

/**
 * 虚拟线程的核心价值：让大量以阻塞式方式编写的任务能够以较低的线程开销并发执行。
 * eg:大量 HTTP 请求等待远程服务响应
 * 虚拟线程由 JVM 管理，可以在适当的时候挂起，并在之后恢复执行，而不必在整个等待期间一直占用一个操作系统线程
 */
@Slf4j
public class Demo1 {
    public static void main(String[] args) throws InterruptedException {
        Thread vt = Thread.startVirtualThread(()->{
            try {
                log.info("start");
                Thread.sleep(2000);
                log.info("end");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
//        虚拟线程是守护线程（Daemon Thread），且主线程执行完代码后会立即退出，不会等待虚拟线程结束
        vt.join();
    }
}