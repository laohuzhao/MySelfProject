package com.example.xitong.Thread.Lock;

import java.util.concurrent.ConcurrentHashMap;

public class test6 {
    private ConcurrentHashMap<String,String> map=new ConcurrentHashMap();
    public static void main(String[] args) {
        test6 pp=new test6();
        new Thread("c"){
            public void run() {
                pp.get(Thread.currentThread());
            }
        }.start();
        new Thread("d"){
            public void run() {
                pp.get(Thread.currentThread());
            }
        }.start();

    }
    public void get(Thread x){
        long time=System.currentTimeMillis();
        map.put("s","aa");
        while (System.currentTimeMillis()-time<2000){
            //等待时间
        }
        System.out.println("线程"+x.getName()+"获取内容是"+map.get("s"));
    }
}
