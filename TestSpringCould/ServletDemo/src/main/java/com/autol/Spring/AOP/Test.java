package com.autol.Spring.AOP;


public class Test {

    public static void main(String[] args) {
      /*  //目标对象
        UserDaoImpl target = new UserDaoImpl();

        //代理对象,把目标对象传给代理对象,建立代理关系
        UserDaoProxy proxy = new UserDaoProxy(target);

        proxy.save();//执行的是代理的方法*/


        System.out.println("华丽的分割线----------------------");
        // 目标对象
        IUserDao target1 = new UserDaoImpl();
        // 【原始的类型 class cn.itcast.b_dynamic.UserDao】
        System.out.println(target1.getClass());

        // 给目标对象，创建代理对象
        IUserDao proxy1 = (IUserDao) new UserDaoProxy(target1).getProxyInstance();
        // class $Proxy0   内存中动态生成的代理对象
        System.out.println(proxy1.getClass());

        // 执行方法   【代理对象】
        proxy1.s();
    }
}
