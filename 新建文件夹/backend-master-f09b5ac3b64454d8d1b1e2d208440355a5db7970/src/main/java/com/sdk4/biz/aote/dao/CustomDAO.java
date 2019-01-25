package com.sdk4.biz.aote.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.sdk4.biz.aote.bean.Custom;

public interface CustomDAO {

	Custom get(String id);

	Custom getByMobileOrAccount(String mobile_or_account);

	Custom getByMobile(String mobile);

	Custom getByAccount(String account);

	String getMaxCode();

	int count(@Param("keyword") String keyword);

	List<Custom> query(@Param("keyword") String keyword, @Param("start") Integer start, @Param("size") int size);

	List<Custom> search(@Param("keyword") String keyword);

	int insert(Custom custom);

	int update(Custom custom);

	int updateDeviceCount(@Param("id") String id, @Param("device_count") int device_count);
}
