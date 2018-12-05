package com.example.redisbootdemo;

import com.example.redisbootdemo.Test1.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisbootdemoApplicationTests {
    /*操作对象*/
    @Autowired
    private RedisTemplate redisTemplate;
    /*操作字符串*/
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void contextLoads() {
    }

    /**
     * redisTemplate.opsForValue() ：操作字符串
     * redisTemplate.opsForHash() ：操作hash
     * redisTemplate.opsForList() ：操作list
     * redisTemplate.opsForSet() ：操作set
     * redisTemplate.opsForZSet() ：操作有序set
     */
    @Test
    public void Test1() {
      User user = new User();
      user.setUserName("张三");
      user.setAge(23);
      redisTemplate.opsForValue().set("user",user);
        System.out.println(redisTemplate.opsForValue().get("user").toString());
    }
    @Test
    public void Test2() {
        System.out.println(redisTemplate.opsForValue().get("user").toString());
        
    }
}
