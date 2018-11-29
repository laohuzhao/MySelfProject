package com.example.xitong;

import com.example.xitong.common.pojo.User;
import com.example.xitong.common.service.UserService;
import com.example.xitong.common.service.UserServiceImpl;
import com.example.xitong.redis.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class XitongApplicationTests {
	 @Autowired
	 private UserService u;
	 @Autowired
	 private RedisUtil redisUtil;
	@Autowired
	 private RedisTemplate<String,String> redisTemplate;
	@Test
	public void contextLoads() {
		System.out.println("hello");
		Object name=redisTemplate.opsForValue().get("User");
		System.out.println("获取到的数据为"+name);
	}
	@Test
	public void contextLoads1() {
		redisUtil.set("name","王帅");
		Object name=redisUtil.get("name");
		System.out.println("获取到的数据为"+name);
	}
}
