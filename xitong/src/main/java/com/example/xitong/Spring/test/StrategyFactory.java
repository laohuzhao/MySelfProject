package com.example.xitong.Spring.test;

import org.reflections.Reflections;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;

public class StrategyFactory {
    private static StrategyFactory s;
    private static HashMap<Integer, String> map = new HashMap<>();

    static {
        s = new StrategyFactory();
        //反射工具包，指明扫描路径,这个路径最好是从配置文件中获取，将这个过程写成反射框架
        Reflections reflections = new Reflections("com.example.xitong.Spring.Test.impl");
        //获取带Pay注解的类的set集合
        Set<Class<?>> classList = reflections.getTypesAnnotatedWith(Service.class);//service从配置文件中获取
        // 获取注解对应的方法
        for (Class s : classList) {
            /*获取类中所有的方法*/
            Method[] methods = s.getDeclaredMethods();
            for (Method m : methods) {
                Pay t = m.getAnnotation(Pay.class);//pay从配置文件中获取
                map.put(t.id(), s.getName());
            }
        }
    }

    //恶汉模式，构造私有方法，不允许其他类创建实例
    private StrategyFactory() {
    }

    public static StrategyFactory getInstance() {
        return s;
    }

    /*核心内容生产对象的方法*/
    public Strategy create(int id) throws Exception {
        //通过map生产对象
        String clazz = map.get(id);
        //通过反射创建具体的实现类
        if(clazz !=null){
            Class  clazz_ = Class.forName(clazz);
            return (Strategy) clazz_.newInstance();
        }else {
            return null;//这里应该抛出异常没有找到对应的
        }

    }


}
