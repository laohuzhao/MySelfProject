package com.example.xitong.Thread.lockTest;
/**
 *
 * wait和notify是用来让线程进入等待状态以及使得线程唤醒的两个操作
 * */
public class ThreadWait extends Thread{
    private Object lock;
    public ThreadWait(Object lock) {
        this.lock = lock;
    }
    @Override
    public void run() {
        synchronized (lock){
            System.out.println("开始执行 thread wait");
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("执行结束 thread wait");
        }
    }

}
