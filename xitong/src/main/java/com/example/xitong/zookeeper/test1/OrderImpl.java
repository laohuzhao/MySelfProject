package com.example.xitong.zookeeper.test1;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OrderImpl implements OrderServer {

    private static OrderMakeNum omn = new OrderMakeNum();
    private Lock l=new ReentrantLock();
    private static zkServer zs=new zkServerImpl();
    @Override
    public void getOrderNum() {
        try {
            l.lock();
            zs.connect("localhost");
            if(zs.isExists("/Hello")){

            }else {
                try {
                    zs.createPath("/Hello",null);
                    String ordeNum = omn.getNum();
                    System.out.println(Thread.currentThread().getName()+"----获取的订单编号为" + ordeNum);

                }catch (Exception e){

                }finally {
                    zs.deletePath("/Hello");
                }

            }

        } catch (Exception e) {

        } finally {
            l.unlock();
        }

    }
}
