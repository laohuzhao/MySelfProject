package com.autol.Mybatis.sqlSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MapperProxy implements InvocationHandler {
    SqlSession sqlSessionProxy;

    public MapperProxy(SqlSession sqlSessionProxy){
        this.sqlSessionProxy = sqlSessionProxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return this.sqlSessionProxy.selectOne(method.getName(), String.valueOf(args[0]));
    }
}
