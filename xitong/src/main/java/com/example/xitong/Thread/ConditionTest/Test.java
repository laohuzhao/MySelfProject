package com.example.xitong.Thread.ConditionTest;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        Lock lock=new ReentrantLock();
        Condition condition=lock.newCondition();
        ConditionDemoSignal cds=new ConditionDemoSignal(lock,condition);

        ConditionDemoWait cdw=new ConditionDemoWait(lock,condition);
        Thread a=new Thread(cdw);
        a.start();
        TimeUnit.SECONDS.sleep(1);//保证启动顺序
        Thread b=new Thread(cds);
        b.start();
       // TimeUnit.SECONDS.sleep(5);
    }
}
