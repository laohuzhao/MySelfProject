package com.autol.ThreadTest.Test2;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 第二种实现，利用一个线程实现
 */
public class SaveProcessor extends Thread implements RequestProcessor {
    LinkedBlockingQueue<Request> requests = new
            LinkedBlockingQueue<Request>();

    @Override
    public void run() {
        //线程一直在跑着，传进来request就打印出来
        try {
            Request request = requests.take();
            System.out.println("begin save request info:" + request.getName());
        } catch (InterruptedException e) {
            System.out.println("不符合条件保存线程中断了");
        }
    }

    @Override
    public void processRequest(Request request) {
        requests.add(request);
    }

}
