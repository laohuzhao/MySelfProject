package com.autol.Mybatis;


import com.autol.Mybatis.dao.MapperDao;
import com.autol.Mybatis.dao.SqlDao;
import com.autol.Mybatis.sqlSession.SqlSessionFactory;
import com.autol.Mybatis.sqlSession.SqlSessionFactoryBuilder;

import java.lang.ref.WeakReference;

public class MainClass {
    public static void main(String[] args){
        WeakReference<SqlSessionFactoryBuilder> reference = new WeakReference<SqlSessionFactoryBuilder>(new SqlSessionFactoryBuilder());
        //使用弱引用创建SqlSessionFactoryBuilder,保证下次GC时回收该对象。
        SqlSessionFactory sqlSessionFactory = reference.get().build("com.monetto.mapper");
        //指定mapper包。
        MapperDao mapperDao = sqlSessionFactory.getMapper(MapperDao.class);
        SqlDao sqlDao = new SqlDao(sqlSessionFactory);
        System.out.println(mapperDao.getStudent("2").getPassword());
        System.out.println(sqlDao.getStudentById("3").getPassword());
    }
}