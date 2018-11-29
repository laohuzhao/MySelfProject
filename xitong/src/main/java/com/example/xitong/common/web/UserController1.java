package com.example.xitong.common.web;

import com.example.xitong.common.pojo.User;
import com.example.xitong.common.service.UserService;
import com.example.xitong.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
/**自动返回的是json格式数据***/
public class UserController1 {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtil redisUtil;

    @RequestMapping("/list")
    public List<User> list(){
        List<User> list = userService.findAllUser();
        return list;
    }
    @RequestMapping("/list1")
    public HashMap<Object,Object> list1(){
        HashMap<Object,Object> map =redisUtil.findallkey();
        return map;
    }
    @RequestMapping("/list2")
    public int TestRedisxx(){
        User user=new User();
        user.setAddress("喝吧");
        user.setAge(19);
        user.setId(8);
        user.setName("大器");
        int a = userService.addUser(user);
        return a;
    }
}
