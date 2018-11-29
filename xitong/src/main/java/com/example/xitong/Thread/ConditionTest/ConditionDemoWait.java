package com.example.xitong.Thread.ConditionTest;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;


public class ConditionDemoWait implements Runnable {

    /**
     * JUC包提供了Condition来对锁进行精准控制，Condition是一个多线程协调通信的工具类，可以让某些线程一起等
     * 待某个条件（condition），只有满足条件时，线程才会被唤醒。
     */
    private Lock lock;
    private Condition condition;

    protected ConditionDemoWait(Lock lock, Condition condition) {
        this.lock = lock;
        this.condition = condition;
    }

    @Override
    public void run() {
        System.out.println("begin -ConditionDemoWait");
        lock.lock();
        try {
            condition.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("end - ConditionDemoWait");
            lock.unlock();
        }
    }
}

