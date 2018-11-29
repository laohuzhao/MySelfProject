package com.example.xitong.Thread.ConditionTest;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ConditionDemoSignal implements Runnable {
    private Lock lock;
    private Condition condition;

    protected ConditionDemoSignal(Lock lock, Condition condition) {
        this.condition = condition;
        this.lock = lock;
    }

    @Override
    public void run() {
        lock.lock();
        System.out.println("begin -ConditionDemoSignal");
        condition.signal();
        System.out.println("end -ConditionDemoSignal");
        try {
            System.out.println("我等个1秒在释放锁");
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
            System.out.println("释放锁，唤醒线程");
        }
    }
}
