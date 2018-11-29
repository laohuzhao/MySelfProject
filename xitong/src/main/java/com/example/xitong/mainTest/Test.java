package com.example.xitong.mainTest;

public class Test {
/*
* 类的生命周期-加载-验证-准备-解析-初始化-使用-卸载
* 只有在准备阶段和初始化阶段才会设计类变量的初始化和赋值，
*
* */
    int a=110;
    static int b=112;
    public static void main(String[] args) {
        staticFunction();
    }
    static Test t=new Test();
    static {
        System.out.println("1");
    }
    {
        System.out.println("2");
    }
    Test(){
        System.out.println("3");
        System.out.println("a="+a+","+"b="+b);
    }
    private static void staticFunction() {
        System.out.println("4");
    }

}
