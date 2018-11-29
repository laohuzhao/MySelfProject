package com.example.xitong.zookeeper.test1;

import java.util.Date;

public class OrderMakeNum {
    int i=1;
    public String  getNum(){
        Date date =new Date();
        String s=date.toString()+(i++);
        return s;
    }
}
