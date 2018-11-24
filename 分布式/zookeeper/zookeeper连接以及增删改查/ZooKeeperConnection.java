package com.example.xitong.zookeeper.test4;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 *
 * 连接zookeeper
 * */
public class ZooKeeperConnection {
    private ZooKeeper zoo;
    final CountDownLatch connectedSignal = new CountDownLatch(1);

    // Method to connect zookeeper ensemble.
    public ZooKeeper connect(String host) throws IOException,InterruptedException {

        zoo = new ZooKeeper(host,5000,new Watcher() {
            /**
             * 监控所有被触发的时间
             * */
            public void process(WatchedEvent we) {
                System.out.println("触发++");
                /**
                 * 连接状态，如果连接counDown()计数减去1，就不用再等了，如果没有连接就等待连接
                 * 一旦客户端与ZooKeeper集合连接，监视器回调就会被调用，并且监视器回调函数调用CountDownLatch的countDown方法来释放锁，在主进程中await。
                 * */
                /*回调判断是否连接成功*/
                if (we.getState() == Event.KeeperState.SyncConnected) {
                    connectedSignal.countDown();
                    System.out.println("释放锁");
                }
            }
        });
        connectedSignal.await();
        return zoo;
    }

    // Method to disconnect from zookeeper server
    public void close() throws InterruptedException {
        zoo.close();
    }
}
