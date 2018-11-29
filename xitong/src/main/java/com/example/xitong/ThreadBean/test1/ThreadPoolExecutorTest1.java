package com.example.xitong.ThreadBean.test1;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorTest1 {
    public static void main(String[] args) {
        /**
         * 固定线程数量线程池，延迟执行
         * */
        ScheduledExecutorService scheduledThreadPool= Executors.newScheduledThreadPool(3);
        scheduledThreadPool.schedule(new Thread(()->{
            System.out.println("延迟");
        }),2,TimeUnit.SECONDS);
    }
}
