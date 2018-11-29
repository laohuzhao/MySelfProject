package com.example.xitong.Spring.test.impl;


import com.example.xitong.Spring.test.Pay;
import com.example.xitong.Spring.test.Strategy;
import org.springframework.stereotype.Service;

@Service
public class StrategyImpl3 implements Strategy {
    @Override
    @Pay(id = 3)
    public String GetAll(int id) {

        /*这里需要调用一个方法，这个方法根据id的不同能够获取到不用的数据*/
        return " My"+id;
    }
}
