package com.example.xitong.Thread.Lock;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * 使用 lockInterruptibly() 响应中断
 * */
public class test4 {
    private Lock lock = new ReentrantLock();
    public static void main(String[] args)  {
        test4 test = new test4();
        MyThread1 thread1 = new MyThread1(test,"A");
        MyThread1 thread2 = new MyThread1(test,"B");
        thread1.start();
        thread2.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread2.interrupt();
    }

    public void insert(Thread thread) throws InterruptedException{
        //注意，如果需要正确中断等待锁的线程，必须将获取锁放在外面，然后将 InterruptedException 抛出
        lock.lockInterruptibly();
        try {
            System.out.println("线程 " + thread.getName()+"得到了锁...");
            long startTime = System.currentTimeMillis();
            for(    ;     ; ) {              // 耗时操作
                if(System.currentTimeMillis() - startTime >= 6000)
                    break;
                //插入数据
            }
        }finally {
            System.out.println(Thread.currentThread().getName()+"执行finally...");
            lock.unlock();
            System.out.println("线程 " + thread.getName()+"释放了锁");
        }
        System.out.println("over");
    }
}

class MyThread1 extends Thread {
    private test4 test = null;

    public MyThread1(test4 test,String name) {
        super(name);
        this.test = test;
    }

    @Override
    public void run() {
        try {
            test.insert(Thread.currentThread());
        } catch (InterruptedException e) {
            System.out.println("线程 " + Thread.currentThread().getName() + "被中断...");
        }
    }
}
