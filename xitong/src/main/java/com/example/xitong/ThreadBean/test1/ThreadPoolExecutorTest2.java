package com.example.xitong.ThreadBean.test1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolExecutorTest2  {
    static int j=0;
    public static void main(String[] args) {
        /**
         * 最多只有一个线程工作的线程池
         * */

        ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();
        for (int i = 1; i <= 50; i++) {
           singleThreadPool.execute(new Thread(()->{
               System.out.println(Thread.currentThread().getName()+j++);
           }));
        }
    }
}
