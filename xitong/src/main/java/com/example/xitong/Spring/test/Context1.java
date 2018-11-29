package com.example.xitong.Spring.test;

import org.springframework.stereotype.Service;

@Service
public class Context1 {
    /**
     * 根据上下文获取对象，并利用对象得到结果
     */
    public String cal(int id) throws Exception {
        //需要帮我去返回具体的实现类方法，不需要对外暴露具体的实现，
        //需要专门负责生产实现类即可，传入id生产对象，对象调用方法生产结果返回
        StrategyFactory s = StrategyFactory.getInstance();
        Strategy sy = s.create(id);
        return sy != null ? sy.GetAll(id) : "没有找到对应数据";
    }
    /*
    * 一个接口多种实现，利用java反射动态获取实现类对象来进行操作方法
    * 这里是实现类的上下文对象，可以利用这个对象获取到真正的对象来进行操作接口方法
    * 工厂模式返回对象
    * 不需要创建好多的实现类对象，只需要创建一个Context1对象就可以代替不同的实现类
    * 简洁的代码，更好的维护
    * 如果需要增加不同的实现，只需要在实现上加上注解方法就可以了，不用更改大量的代码
    * */
}
