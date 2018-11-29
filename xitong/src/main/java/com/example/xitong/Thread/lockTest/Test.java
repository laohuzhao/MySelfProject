package com.example.xitong.Thread.lockTest;

import java.util.concurrent.TimeUnit;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        Object c=new Object();

        ThreadNotify a=new ThreadNotify(c);
        ThreadWait b=new ThreadWait(c);

        b.start();
        TimeUnit.SECONDS.sleep(1);
        //保证b线程先启动进入等待，然后a线程去唤醒b线程，如果是a线程先去唤醒，那么b线程将无限期等待
        //当b线程被唤醒之后不会马上执行，因为锁在a线程哪里正在被执行
        a.start();
        TimeUnit.SECONDS.sleep(10);
    }
}
