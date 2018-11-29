package com.example.xitong.Thread.test1;

import java.util.concurrent.locks.Lock;

public class Thread1 extends Thread {
    private String name;
    public Thread1(String name) {
        this.name=name;
    }
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println(name + "运行  :  " + i);
            try {
                sleep((int) Math.random() * 10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("主线程开始");
        Thread1 mTh1=new Thread1("A");
        Thread1 mTh2=new Thread1("B");
        mTh1.start();
        mTh2.start();
       /* mTh2.join();
        mTh1.join();*/
        System.out.println("主线程结束");

    }
    /**
     * 如果一个类继承Thread，则不适合资源共享。但是如果实现了Runable接口的话，则很容易的实现资源共享。
     * 实现Runnable接口比继承Thread类所具有的优势：
     * 1）：适合多个相同的程序代码的线程去处理同一个资源
     * 2）：可以避免java中的单继承的限制
     * 3）：增加程序的健壮性，代码可以被多个线程共享，代码和数据独立
     * 4）：线程池只能放入实现Runable或callable类线程，不能直接放入继承Thread的类
     * 提醒一下大家：main方法其实也是一个线程。在java中所以的线程都是同时启动的，至于什么时候，哪个先执行，完全看谁先得到CPU的资源。
     * 在java中，每次程序运行至少启动2个线程。一个是main线程，一个是垃圾收集线程。因为每当使用java命令执行一个类的时候，
     * 实际上都会启动一个ＪＶＭ，每一个ｊＶＭ实习在就是在操作系统中启动了一个进程。
     * */
}
