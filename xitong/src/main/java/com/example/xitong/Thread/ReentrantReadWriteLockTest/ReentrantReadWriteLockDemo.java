package com.example.xitong.Thread.ReentrantReadWriteLockTest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁，对读开放，对写闭锁
 * 1.读锁与读锁可以共享
 * 2. 读锁与写锁不可以共享（排他）
 * 3. 写锁与写锁不可以共享（排他）
 */
public class ReentrantReadWriteLockDemo {

    static Map<String, Object> cacheMap = new HashMap<>();//模拟内存缓存
    static ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();//获取读写锁
    static Lock read = rwl.readLock();//读锁
    static Lock write = rwl.writeLock();//写锁

    static {
        //初始化操作
        cacheMap.put("key1", "value1");
        cacheMap.put("key2", "value2");
    }

    public static final Object get(String key) {
        read.lock(); //读锁
        System.out.println("开始读取数据");
        return cacheMap.get(key);
    }

    public static void Unlock() {
        read.unlock(); //读锁
        System.out.println("释放锁");
    }

    public static final Object put(String key, Object value) {
        write.lock();
        System.out.println("开始写数据");
        return cacheMap.put(key, value);
    }

    public static void UnlockWriete() {
        write.unlock();
        System.out.println("释放锁");
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            if (i == 0) {
                new Thread(() -> {
                    get("key1");//key1拿到锁之后休息5秒，但是读锁的话最后释放是没有影响的
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        Unlock();
                    }
                }).start();
            } else {
                new Thread(() -> {
                    get("key2");
                    try {
                    } finally {
                        Unlock();
                    }
                }).start();

            }

        }
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /*休息5秒之后开始写，写锁就不一样了，必须等到线程0释放锁才可以继续*/
        for (int i = 0; i < 10; i++) {
            if (i == 0) {
                new Thread(() -> {
                    put("key3", "value3");//key1拿到锁之后休息2秒，但是读锁的话最后释放是没有影响的
                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        UnlockWriete();
                    }
                }).start();
            } else {
                new Thread(() -> {
                    get("key2");
                    try {
                    } finally {
                        Unlock();
                    }
                }).start();

            }

        }
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
