package com.example.xitong.common.service;

import com.example.xitong.common.pojo.User;

import java.util.List;

public interface UserService {
    /**
     * 根据接口查询所用的用户
     */
    public List<User> findAllUser();
    /**插入用户*/
    public int addUser(User user);
}
