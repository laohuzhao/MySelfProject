package com.sdk4.biz.aote.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.sdk4.biz.aote.bean.DataUpdateTime;
import com.sdk4.biz.aote.bean.Device;
import com.sdk4.biz.aote.bean.DeviceStateTotal;

public interface DeviceDAO {

	Device getByDeviceId(String device_id);

	Device getByPhoneNumber(String phone_number);

	List<Device> getByGenerateId(String generate_id);

	DeviceStateTotal totalState(@Param("custom_id") String custom_id);

	int totalCustomDeviceCount(@Param("custom_id") String custom_id);

	int count(@Param("custom_id") String custom_id, @Param("keyword") String keyword,
			@Param("device_id") String device_id, @Param("corp_name") String corp_name,
			@Param("device_type") String device_type, @Param("manufacturer") String manufacturer,
			@Param("area_id") String area_id, @Param("area_code") String area_code,
			@Param("working_state") Integer working_state, @Param("processing_state") Integer processing_state,
			@Param("fuel_level_state") Integer fuel_level_state,
			@Param("fuel_pressure_state") Integer fuel_pressure_state, @Param("status") List<Integer> status,
			@Param("tag") String tag);

	List<Device> query(@Param("custom_id") String custom_id, @Param("keyword") String keyword,
			@Param("device_id") String device_id, @Param("corp_name") String corp_name,
			@Param("device_type") String device_type, @Param("manufacturer") String manufacturer,
			@Param("area_id") String area_id, @Param("area_code") String area_code,
			@Param("working_state") Integer working_state, @Param("processing_state") Integer processing_state,
			@Param("fuel_level_state") Integer fuel_level_state,
			@Param("fuel_pressure_state") Integer fuel_pressure_state, @Param("status") List<Integer> status,
			@Param("tag") String tag, @Param("start") Integer start, @Param("size") int size);

	List<Device> queryIDS(@Param("device_ids") List<String> device_ids);

	int insert(Device device);

	int updateParams(@Param("device_id") String device_id, @Param("p1") String p1, @Param("p2") String p2,
			@Param("p3") String p3, @Param("p4") String p4, @Param("version") String version,
			@Param("upload_period") String upload_period);

	int updateCustom(@Param("device_id") String device_id, @Param("custom_id") String custom_id,
			@Param("device_user") String device_user, @Param("owner_time") Date owner_time);

	int updateInitData(@Param("device_id") String device_id, @Param("generate_id") String generate_id,
			@Param("device_type") String device_type, @Param("manufacturer") String manufacturer,
			@Param("device_user") String device_user, @Param("area_code") String area_code,
			@Param("area_id") String area_id, @Param("local_address") String local_address, @Param("p1") String p1,
			@Param("p2") String p2, @Param("p3") String p3, @Param("p4") String p4, @Param("version") String version,
			@Param("upload_period") String upload_period);

	int syncData(Device device);

	int updateStatus(@Param("device_id") String device_id, @Param("status") int status);

	int updateTag(@Param("device_id") String device_id, @Param("tag") String tag);

	int delete(@Param("device_id") String device_id);

	// -
	int insertHIS(Device device);

	// -
	int countDataUpdate(@Param("custom_id") String custom_id);

	List<DataUpdateTime> queryDataUpdate(@Param("custom_id") String custom_id, @Param("start") Integer start,
			@Param("size") int size);

}
