package com.sdk4.biz.aote.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.sdk4.biz.aote.bean.Dict;

public interface DictDAO {

	//
	List<Dict> queryCustom();

	//
	List<Dict> queryDeviceType();

	int insertDeviceType(Dict dict);

	int updateDeviceType(Dict dict);

	int deleteDeviceType(@Param("code") String code);

	//
	String getManufacturerMaxCode();

	List<Dict> queryManufacturer(@Param("extra") String extra);

	int insertManufacturer(Dict dict);

	int updateManufacturer(Dict dict);

	int deleteManufacturer(@Param("code") String code);

	//
	List<Dict> queryAreaCode(@Param("parent_id") String parent_id);

	List<Dict> getAllCity();

	int insertAreaCode(Dict dict);

	int updateAreaCode(Dict dict);

	int deleteAreaCode(@Param("id") String id);

}
