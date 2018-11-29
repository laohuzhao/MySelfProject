package com.example.xitong.zookeeper.test1;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 *
 * 多线程模拟多个请求服务请求数据
 * */
public class test1 {
    private static int thread_num = 50;//线程数,设置同时并发线程数
    private static CyclicBarrier cb = new CyclicBarrier(thread_num);
    public static void main(String[] args) {

        List list=new ArrayList();
        list.add(1);
        list.add(0);
        list.add(10);
        list.add(5);
        Collections.sort(list, new Comparator<String>() {
            public int compare(String lhs, String rhs) {
                return 0;
            }
        });


        /*for(int i=0;i<thread_num;i++){
            new Thread("a+"+i){
                @Override
                public void run() {
                    try {
                        cb.await();
                        OrderImpl  ol=new OrderImpl();
                        ol.getOrderNum();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }

                }
            }.start();
        }
        try {
            TimeUnit.DAYS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }
}
