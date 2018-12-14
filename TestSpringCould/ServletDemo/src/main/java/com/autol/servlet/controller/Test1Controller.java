package com.autol.servlet.controller;

import com.autol.servlet.annotation.MyAutowired;
import com.autol.servlet.annotation.MyController;
import com.autol.servlet.annotation.MyRequestMapping;
import com.autol.servlet.annotation.MyRequestParam;
import com.autol.servlet.service.TestService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@MyController
@MyRequestMapping("test1")
public class Test1Controller {
    @MyAutowired
    private TestService testService;

    @MyRequestMapping("test")
    public void myTest(HttpServletRequest request, HttpServletResponse response,
                       @MyRequestParam("param") String param){
        try {
            response.getWriter().write( "Test1Controller:the param you main is :"+param);
            testService.printParam(param);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
