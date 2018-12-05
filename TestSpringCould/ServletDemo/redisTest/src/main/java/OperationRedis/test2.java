package OperationRedis;

import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

public class test2 {

    public static void main(String[] args) throws InterruptedException {
        get6377();
    }

    public static void get6379() throws InterruptedException {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        //jedis.set("aaa","key1");
        List<String> listkey =new ArrayList<>();
        listkey.add("key1");
        Object a = jedis.eval("");//lua脚本操作redis保证原子性
        /*return redis.call('get',KEYS[1])*/
        System.out.println(a.toString());
    }

    public static void get6377() {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
      /*  for (int i=0;i<9;i++){
            jedis.set("key"+i,"num"+i);
        }*/

        String str = jedis.get("key2");
        System.out.println(str + "----");


    }

    public static void get6378() {
        Jedis jedis = new Jedis("127.0.0.1", 6377);
        String str = jedis.get("key2");
        System.out.println(str + "22");
    }

    public static int sss(int a) {
        if (a == 0) {
            return 1;
        } else {
            return a * sss(a - 1);
        }
    }
}
