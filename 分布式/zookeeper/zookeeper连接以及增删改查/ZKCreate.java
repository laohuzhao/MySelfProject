package com.example.xitong.zookeeper.test4;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * 创建节点
 */
public class ZKCreate {
    // create static instance for zookeeper class.
    private static ZooKeeper zk;

    // create static instance for ZooKeeperConnection class.
    private static ZooKeeperConnection conn;

    // Method to create znode in zookeeper ensemble
    public static void create(String path, byte[] data) throws
            KeeperException, InterruptedException {
        String a = null;
        Stat stat = zk.exists(path, null);
        if (stat == null) {
            a = zk.create(path, null, ZooDefs.Ids.OPEN_ACL_UNSAFE,//权限
                    CreateMode.EPHEMERAL_SEQUENTIAL);//节点类型，持久、临时或者和顺序节点结合
            System.out.println(a);
        } else {
            System.out.println("节点存在" + stat.toString());
        }

        /**
         *     PERSISTENT  持久
         *     PERSISTENT_SEQUENTIAL 持久顺序
         *     EPHEMERAL(1, true, false),短暂
         *     EPHEMERAL_SEQUENTIAL(3, true, true);短暂顺序
         * */
    }

    public static void main(String[] args) {

        // znode path
        String path = "/My"; // Assign path to znode

        // data in byte array
        byte[] data = "App".getBytes(); // Declare data

        try {
            conn = new ZooKeeperConnection();
            zk = conn.connect("localhost");
            create(path, data); // Create the data to the specified path
            conn.close();
        } catch (Exception e) {
            System.out.println(e.getMessage()); //Catch error message
        }
    }
}
