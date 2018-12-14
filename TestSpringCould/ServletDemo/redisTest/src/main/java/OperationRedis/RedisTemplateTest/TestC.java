package OperationRedis.RedisTemplateTest;

import com.alibaba.fastjson.JSONObject;
import redis.clients.jedis.Jedis;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TestC {
    public static void main(String[] args) throws InterruptedException {
        generateTestData();
        geterateTestData();
        TimeUnit.SECONDS.sleep(50);

    }

    /*放红包*/
    static public void generateTestData() throws InterruptedException {
        Jedis jedis = new Jedis("127.0.0.1");
        jedis.flushAll();
        jedis.set("hongbao", "1000");

    }

    /**
     * 抢红包
     */
    static public void geterateTestData() throws InterruptedException {
        Jedis jedis = new Jedis("127.0.0.1");
        Lock lock = new ReentrantLock();
        for (int i = 0; i <1200; ++i) {
            Thread thread = new Thread("线程"+i) {
                public void run() {
                    lock.lock();
                    Jedis jedis = new Jedis("127.0.0.1");
                    String str = jedis.get("hongbao");
                    int b = Integer.valueOf(str) - 1;
                    if (b == 0) {
                        System.out.println("the end");
                    } else {
                        String name=Thread.currentThread().getName();
                        System.out.println(name+"---get it");
                        jedis.set("hongbao", String.valueOf(b));
                    }
                    lock.unlock();
                }
            };
            thread.start();
        }
    }

}
