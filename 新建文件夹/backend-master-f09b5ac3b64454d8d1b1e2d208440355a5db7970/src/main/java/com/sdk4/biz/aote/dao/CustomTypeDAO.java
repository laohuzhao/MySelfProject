package com.sdk4.biz.aote.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.sdk4.biz.aote.bean.CustomType;

public interface CustomTypeDAO {

	//
	List<CustomType> query(@Param("custom_id") String custom_id);

	int insert(CustomType custom_type);

	int delete(@Param("custom_id") String custom_id, @Param("type_code") String type_code);

	int deleteAll(@Param("custom_id") String custom_id);

}
