package com.example.xitong.Thread.lockTest;

import java.util.concurrent.TimeUnit;

public class ThreadNotify extends Thread {
    private Object lock;
    public ThreadNotify(Object lock){
        this.lock=lock;
    }

    @Override
    public void run() {
        synchronized (lock){
            System.out.println("开始ThreadNotify");
            lock.notify();
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("结束ThreadNotify");
        }
    }
}
