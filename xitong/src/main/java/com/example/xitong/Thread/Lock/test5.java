package com.example.xitong.Thread.Lock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class test5 {
    private ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private ConcurrentHashMap<String,String> map=new ConcurrentHashMap();
    public static void main(String[] args) {
        final test5 test = new test5();

        new Thread("A") {
            public void run() {
                test.get(Thread.currentThread());
            }
        }.start();

        new Thread("B") {
            public void run() {
                test.get(Thread.currentThread());//返回当前线程的引用对象
            }
        }.start();
    }

    public void get(Thread thread) {
        rwl.readLock().lock(); // 在外面获取锁
        try {
            long start = System.currentTimeMillis();
            System.out.println("线程" + thread.getName() + "开始读操作...");
            while (System.currentTimeMillis() - start <= 1) {
                System.out.println("线程" + thread.getName() + "正在进行读操作...");
            }
            System.out.println("线程" + thread.getName() + "读操作完毕...");
        } finally {
            rwl.readLock().unlock();
        }
    }
}
