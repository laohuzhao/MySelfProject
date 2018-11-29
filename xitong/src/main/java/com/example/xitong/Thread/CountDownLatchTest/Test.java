package com.example.xitong.Thread.CountDownLatchTest;

import java.util.concurrent.CountDownLatch;

/**
 * countdownlatch是一个同步工具类，它允许一个或多个线程一直等待，直到其他线程的操作执行完毕再执行。从
 * 命名可以解读到countdown是倒数的意思，类似于我们倒计时的概念。
 * countdownlatch提供了两个方法，一个是countDown，一个是await， countdownlatch初始化的时候需要传入一
 * 个整数，在这个整数倒数到0之前，调用了await方法的程序都必须要等待，然后通过countDown来倒数。
 *
 * */
public class Test {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch=new CountDownLatch(3);//三个线程执行完毕就代表不用等待了，如果大于三个，那么久一直等下去
        new Thread(()->{
            countDownLatch.countDown();
        },"t1").start();
        new Thread(()->{
            countDownLatch.countDown();
        },"t2").start();
        new Thread(()->{
            countDownLatch.countDown();
        },"t3").start();
        countDownLatch.await();
        System.out.println("所有程序执行完毕");
    }
}
