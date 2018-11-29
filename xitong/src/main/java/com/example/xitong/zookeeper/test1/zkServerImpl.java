package com.example.xitong.zookeeper.test1;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class zkServerImpl implements zkServer, Watcher {
    private ZooKeeper zk = null;
    final CountDownLatch connectedSignal = new CountDownLatch(1);

    /**
     * 创建节点
     */
    @Override
    public boolean createPath(String path, String data) throws KeeperException, InterruptedException {
        Stat stat = zk.exists(path, null);
        if (stat == null) {
            zk.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,//权限
                    CreateMode.EPHEMERAL);
            return true;
        }
        return false;
    }

    /**
     * 删除节点
     */
    @Override
    public boolean deletePath(String path) throws KeeperException, InterruptedException {
        Stat stat = zk.exists(path, null);
        if (stat != null) {
            zk.delete(path, zk.exists(path, true).getVersion());
            return true;
        } else {
            throw new RuntimeException("节点不存在");
        }
    }

    /**
     * 判断节点是否存在
     */
    @Override
    public boolean isExists(String path) throws KeeperException, InterruptedException {
        Stat stat = zk.exists(path, null);
        if (stat != null) {//节点存在
            return true;
        }
        return false;
    }

    @Override
    public void connect(String localhost) throws Exception {
        zk = new ZooKeeper(localhost, 5000, new Watcher() {
            public void process(WatchedEvent we) {
                if (we.getState() == Event.KeeperState.SyncConnected) {
                    connectedSignal.countDown();
                    System.out.println("释放锁");
                }
            }
        });
    }

    /**
     * 监听
     */
    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
            System.out.println("有变化+1");
        }
    }
}
