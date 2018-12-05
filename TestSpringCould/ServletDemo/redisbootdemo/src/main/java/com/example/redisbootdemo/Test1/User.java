package com.example.redisbootdemo.Test1;

import java.io.Serializable;

public class User implements Serializable {
    private String UserName;
    private Integer age;

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
    @Override
    public String toString(){
        return "UserName:"+this.UserName+"---age:"+this.age;
    }
}
