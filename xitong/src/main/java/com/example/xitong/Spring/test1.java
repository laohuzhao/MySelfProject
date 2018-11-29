package com.example.xitong.Spring;

import org.aspectj.lang.annotation.Before;
import org.springframework.aop.MethodBeforeAdvice;

import java.lang.annotation.Target;
import java.lang.reflect.Method;

public class test1 implements MethodBeforeAdvice {
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("i am  before strong");
    }



        @Before(value = "")
        public  void say() {
            System.out.println("我需要被增强！");
        }


    public static void main(String[] args) {
       new test1().say();

    }
}
