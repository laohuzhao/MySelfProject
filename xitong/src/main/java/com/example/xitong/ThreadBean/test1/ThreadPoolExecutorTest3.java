package com.example.xitong.ThreadBean.test1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorTest3 {
    public static void main(String[] args) {
        /**
         * 定时线程池,定时周期性执行
         * */
        ScheduledExecutorService scheduledThreadPool= Executors.newScheduledThreadPool(3);
        scheduledThreadPool.scheduleAtFixedRate(new Runnable(){
            @Override
            public void run() {
                System.out.println("延迟1秒后每三秒执行一次");
            }
        },1,3, TimeUnit.SECONDS);
    }
    public  void peee(){
        new Thread("a"){
            @Override
            public void run() {
                super.run();
            }
        }.start();
    }
}
