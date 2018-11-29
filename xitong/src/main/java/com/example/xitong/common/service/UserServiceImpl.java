package com.example.xitong.common.service;

import com.example.xitong.common.dao.UserMapper;
import com.example.xitong.common.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "UserService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    @Cacheable(value = "User")
    public List<User> findAllUser() {
        return userMapper.findAll();
    }

    @Override
    @CacheEvict(value = "User",allEntries=true)
    public int addUser(User user) {
        return userMapper.updateUser(user);
    }
}
