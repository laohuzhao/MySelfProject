package com.example.xitong.Spring.test;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private Context1   s;//因为这里有多个对象的实现，所以这里不能用这个对象来获取不同的内容，这里要使用Context1对象来实现
    @RequestMapping(value = "/abc")
    public String getUserId(String id) throws Exception{
        /*传入一个参数int id,去调用implement类，通过调用实现类来得到不同的数据*/
        int i=Integer.parseInt(id);
        String  a= null;
        try {
            a = s.cal(i);
            System.out.println(a+"----");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "你好";
    }
}
