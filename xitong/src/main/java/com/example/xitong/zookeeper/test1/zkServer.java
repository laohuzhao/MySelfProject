package com.example.xitong.zookeeper.test1;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;

public interface zkServer {
    boolean createPath(String path,String data) throws KeeperException, InterruptedException;//创建指定路径的临时目录
    boolean deletePath(String path) throws KeeperException, InterruptedException;//删除指定路径的目录
    boolean isExists(String path) throws KeeperException, InterruptedException;//判断某目录是否存在并进行监听
    void connect(String localhost) throws Exception;
}
