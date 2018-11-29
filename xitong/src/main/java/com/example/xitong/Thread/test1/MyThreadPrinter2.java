package com.example.xitong.Thread.test1;

import com.example.xitong.common.pojo.User;

public class MyThreadPrinter2 implements Runnable {

    private String name;
    private Object prev;
    private Object self;

    private MyThreadPrinter2(String name, Object prev, Object self) {
        this.name = name;
        this.prev = prev;
        this.self = self;
    }

    @Override
    public void run() {
        int count =3;
        while (count > 0) {
            synchronized (prev) {
                System.out.println(name+"来了，先锁住"+((User)prev).getName());
                synchronized (self) {
                    System.out.println("在锁住"+((User)self).getName());
                    System.out.println("输出内容"+name);
                    count--;
                    self.notify();//释放自身对象，唤醒下一个线程
                    System.out.println("释放"+((User)self).getName());
                }
                try {
                    prev.wait();//释放prev对象锁
                    System.out.println("释放"+((User)prev).getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    /*程序运行的主要过程就是A线程最先运行，持有C,A对象锁，后释放A,C锁，唤醒B。
    线程B等待A锁，再申请B锁，后打印B，
    再释放B，A锁，唤醒C，    线程C等待B锁，再申请C锁，后打印C，再释放C,B锁，唤醒A。*/
    public static void main(String[] args) throws Exception {
        User a = new User();
        User b = new User();
        User c = new User();
        a.setName("a");
        b.setName("b");
        c.setName("c");
        MyThreadPrinter2 pa = new MyThreadPrinter2("A", c, a);
        MyThreadPrinter2 pb = new MyThreadPrinter2("B", a, b);
        MyThreadPrinter2 pc = new MyThreadPrinter2("C", b, c);


        new Thread(pa).start();
        Thread.sleep(100);  //确保按顺序A、B、C执行
        new Thread(pb).start();
        Thread.sleep(100);
        new Thread(pc).start();
    }
}
