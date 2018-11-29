package com.example.xitong.common.dao;

import com.example.xitong.common.pojo.User;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface UserMapper {
    public List<User> findAll();

    public int updateUser(User user);

    public String get();
}
