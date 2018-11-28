package OperationRedis;

import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Test1 {
    public void RedisKeys() {
        /**
         * 分布式锁三点
         *  1.排他性
         *  2.避免死锁
         *  3.高可用
         * */
        Jedis jedis = new Jedis("localhost");
        // 获取数据并输出
        /*分布式锁的实现*/
        boolean flag = true;
        int c=0;
        while (flag) {
            c++;
            String lockName = "lock";
            String a = jedis.set(lockName, "value", "NX", "PX", 19000);//没有key返回1，有key返回0
            if ("OK".equals(a)) {
                try {
                    System.out.println(Thread.currentThread().getName() + "do something");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } finally {
                    jedis.del(lockName);
                    System.out.println(Thread.currentThread().getName() + "release lock successful !");
                    flag = false;
                }
            } else {
                System.out.println(Thread.currentThread().getName() + "go on....");
            }
        }
    }

    @Test
    public void Trh() {
        /*多线程调试工具 类 栅栏，等待线程都准备到位之后开始同时发出*/
        CyclicBarrier cyclicBarrier = new CyclicBarrier(9);
        for (int i = 0; i < 9; i++) {
            new Thread("name" + i) {
                @Override
                public void run() {
                    try {
                        cyclicBarrier.await();
                        RedisKeys();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }

                }
            }.start();
        }
        try {
            Thread.sleep(60*5);//休息五分钟保证其他线程运行完毕
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
