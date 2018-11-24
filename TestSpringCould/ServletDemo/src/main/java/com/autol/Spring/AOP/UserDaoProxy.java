package com.autol.Spring.AOP;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 *
 * 静态代理
 * */
public class UserDaoProxy {
   /* //接收保存目标对象
    private IUserDao target;

    *//*构造函数*//*
    public UserDaoProxy(IUserDao target){
        this.target=target;
    }

    public void save() {
        System.out.println("开始事务...");
        target.s();//执行目标对象的方法
        System.out.println("提交事务...");
    }*/
    /*1.可以做到在不修改目标对象的功能前提下,对目标功能扩展.
2.缺点:
因为代理对象需要与目标对象实现一样的接口,所以会有很多代理类,类太多.同时,一旦接口增加方法,目标对象与代理对象都要维护.
*/
    /***
     * 一下是动态代理
     * */
    //维护一个目标对象

    private Object target1;
    public UserDaoProxy(Object target){
        this.target1=target;
    }

    //给目标对象生成代理对象
    public Object getProxyInstance(){
        return Proxy.newProxyInstance(
                target1.getClass().getClassLoader(),
                target1.getClass().getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println("开始事务2");
                        //运用反射执行目标对象方法
                        Object returnValue = method.invoke(target1, args);
                        System.out.println("提交事务2");
                        return returnValue;
                    }
                }
        );
    }
}
