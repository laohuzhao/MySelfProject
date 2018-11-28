package com.autol.ThreadTest.Test2;


import java.util.concurrent.TimeUnit;
/**
 * 实例说明，保证线程顺序执行，一个线程依赖另一个线程的执行结果来选择自己的执行方法，使用多线程的方式可以提高性能，不必
 * 非要等到一个程序执行完了，在执行另外一个程序，这样可以使两个线程都先跑到即将执行的方法那开始等着，只要参数（条件）满足立刻执行方法
 * */
public class Demo {
    PrintProcessor printProcessor;

    public void doPrint(Request request) throws InterruptedException {
        SaveProcessor  saveProcessor = new SaveProcessor();
        printProcessor = new PrintProcessor(saveProcessor);//穿进去实现类（子类）完成方法操作
        printProcessor.start();
        saveProcessor.start();
        printProcessor.processRequest(request);//线程一直在跑着，只要传参数进去就出结果，先传后传都是一样的结果，使线程顺序执行

    }
    public static void main(String[] args) throws InterruptedException {
        Request request = new Request();
        request.setName("no");
        Demo a = new Demo();
        a.doPrint(request);
        TimeUnit.DAYS.sleep(1);
    }

}
