package com.example.xitong.Thread.test1;

public class ThreadYield extends Thread{
    public ThreadYield(String name) {
       super(name);
    }



    @Override
    public void run() {
        for (int i = 1; i <= 50; i++) {
            System.out.println("" + this.getName() + "-----" + i);
            // 当i为30时，该线程就会把CPU时间让掉，让其他或者自己的线程执行（也就是谁先抢到谁执行）
            if (i ==30) {
                this.yield();
            }
        }
    }

    public static void main(String[] args) {
        ThreadYield yt1 = new ThreadYield("张三");
        ThreadYield yt2 = new ThreadYield("李四");
        yt1.run();//和普通调用方法是一样的，没有起到启动线程的目的。等待run()方法结束之后才会进行下一步yt1.start();
        yt1.start();//是启动线程，不管有没有执行完run()方法里的代码，程序都是要往下走继续启动yt2.start线程。
        yt2.start();
    }
}
