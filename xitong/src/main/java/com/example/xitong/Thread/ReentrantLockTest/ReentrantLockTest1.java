package com.example.xitong.Thread.ReentrantLockTest;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 重入锁，表示支持重新进入的锁，也就是说，如果当前线程t1通过调用lock方法获取了锁之后，再次调用lock，是
 * 不会再阻塞去获取锁的，直接增加重试次数就行了。
 */
public class ReentrantLockTest1 {

    private static int count = 0;
    static Lock lock = new ReentrantLock();//声明可重入锁

    public static void inc() {
        lock.lock();
        try {
           TimeUnit.MILLISECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        count++;
        lock.unlock();
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 1000; i++) {
            new Thread(() -> ReentrantLockTest1.inc()).start();
        }
        Thread.sleep(3000);
        System.out.println("result:"+count);
    }
}
