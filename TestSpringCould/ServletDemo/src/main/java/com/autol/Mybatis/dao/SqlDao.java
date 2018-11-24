package com.autol.Mybatis.dao;


import com.autol.Mybatis.entry.Student;
import com.autol.Mybatis.sqlSession.SqlSessionDaoSupport;
import com.autol.Mybatis.sqlSession.SqlSessionFactory;

public class SqlDao extends SqlSessionDaoSupport {
    public SqlDao(SqlSessionFactory sqlSessionFactory) {
        super(sqlSessionFactory);
    }

    public Student getStudentById(String id){
        return this.getSqlSession().selectOne("getStudent",id);
    }
}
