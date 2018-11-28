package com.autol.ThreadTest.Test1;

import java.util.concurrent.*;

/**
 * 开始一个线程的四种方法
 * 1.extends Thread
 * 2.implements Runnable
 * 3.线程池
 * 4.实现callable
 * 实现 Callable 接口通过 FutureTask 包装器来创建
 * Thread 线程 ,带返回值，根据返回值来进行下一步操作
 */
public class ThreadMethed implements Callable<String> {

    public static void main(String[] args) throws Exception {
        /**
         * newCachedThreadPool创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
         * newFixedThreadPool 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
         * newScheduledThreadPool 创建一个定长线程池，支持定时及周期性任务执行。
         * newSingleThreadExecutor 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。
         * */


        ExecutorService executorService =
                Executors.newFixedThreadPool(1);
        ThreadMethed callableDemo = new ThreadMethed();
        String b = callableDemo.call();//执行线程并返回值
        Future<String> future = executorService.submit(callableDemo);
        System.out.println(future.get());
        executorService.shutdown();
    }

    @Override
    public String call() throws Exception {
        int a = 1;
        int b = 2;
        System.out.println(a + b);
        return "执行结果:" + (a + b);
    }
}
