package com.autol.ThreadTest.Test2;

/**
 * 优雅使用一步线程方式来搞定用户请求，类似zookeeper源码设计
 * */
public class Request {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toSring() {
        return "Request{" + "name=" + name + "}";
    }
}