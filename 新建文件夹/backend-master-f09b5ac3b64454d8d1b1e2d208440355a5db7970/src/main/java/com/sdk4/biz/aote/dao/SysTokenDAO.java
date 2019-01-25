package com.sdk4.biz.aote.dao;

import org.apache.ibatis.annotations.Param;

import com.sdk4.biz.aote.bean.LoginUser;

public interface SysTokenDAO {

    LoginUser getByToken(String token);

    int insert(LoginUser token);

    int deleteByToken(@Param("token") String token);

    int deleteByUserId(@Param("user_id") String user_id);

}
