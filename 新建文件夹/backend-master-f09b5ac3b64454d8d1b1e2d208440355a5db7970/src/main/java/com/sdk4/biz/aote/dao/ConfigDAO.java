package com.sdk4.biz.aote.dao;

import org.apache.ibatis.annotations.Param;

import com.sdk4.biz.aote.bean.Config;

public interface ConfigDAO {

    Config get(String key);

    int insert(Config config);

    int update(Config config);

    int delete(@Param("key") String key);

}
