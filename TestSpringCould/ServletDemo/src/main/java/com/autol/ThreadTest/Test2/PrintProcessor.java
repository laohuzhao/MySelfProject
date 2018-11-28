package com.autol.ThreadTest.Test2;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 第一种请求处理方式，利用一个线程去处理
 */
public class PrintProcessor extends Thread implements RequestProcessor {
    LinkedBlockingQueue<Request> requests = new
            LinkedBlockingQueue<Request>();
    /*java.util.concurrent包下的新类。LinkedBlockingQueue就是其中之一，是一个阻塞的线程安全的队列，底层采用链表实现。
      LinkedBlockingQueue构造的时候若没有指定大小，则默认大小为Integer.MAX_VALUE，当然也可以在构造函数的参数中指定大小。LinkedBlockingQueue不接受null。
    添加元素的方法有三个：add,put,offer,且这三个元素都是向队列尾部添加元素的意思。
    区别:
        add方法在添加元素的时候，若超出了度列的长度会直接抛出异常：
        put方法，若向队尾添加元素的时候发现队列已经满了会发生阻塞一直等待空间，以加入元素。
         offer方法在添加元素时，如果发现队列已满无法添加的话，会直接返回false。
        poll: 若队列为空，返回null。
        remove:若队列为空，抛出NoSuchElementException异常。
        take:若队列为空，发生阻塞，等待有元素。*/
    private final RequestProcessor nextProcessor;

    public PrintProcessor(RequestProcessor nextProcessor) {
        this.nextProcessor = nextProcessor;
    }

    @Override
    public void processRequest(Request request) {
        requests.add(request);
    }

    public void run() {
        //线程一直在跑着，传进来request就打印出来
        try {
            Request request = requests.take();
            System.out.println("print data:" + request.getName());
            if ("ok".equals(request.getName())){
                request.setName("上一步执行结果是ok");
                nextProcessor.processRequest(request);
            }else if ("no".equals(request.getName())){
                System.out.println("上一步执行结果是no,不符合条件线程结束");
                ((SaveProcessor)nextProcessor).interrupt();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
