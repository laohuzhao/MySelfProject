package com.autol.Spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) {
        // 创建IOC容器
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        //从容器中获取bean实例
        Person person = (Person) ac.getBean("person");
        //使用bean
        System.out.println(person.getName());
    }
}
