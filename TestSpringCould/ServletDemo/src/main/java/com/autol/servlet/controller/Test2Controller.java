package com.autol.servlet.controller;

import com.autol.servlet.annotation.MyController;
import com.autol.servlet.annotation.MyRequestMapping;
import com.autol.servlet.annotation.MyRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@MyController
@MyRequestMapping("test2")
public class Test2Controller {
    @MyRequestMapping("test")
    public void myTest(HttpServletRequest request, HttpServletResponse response,
                       @MyRequestParam("param") String param){
        try {
            response.getWriter().write( "Test2Controller:the param you send is :"+param);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
