package com.sdk4.biz.aote.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.alibaba.fastjson.JSONObject;
import com.sdk4.biz.aote.bean.DataUpdateTime;
import com.sdk4.biz.aote.bean.Device;
import com.sdk4.biz.aote.bean.HomeDashboard;
import com.sdk4.biz.aote.service.AtService.Result;
import com.sdk4.biz.aote.var.AtCmd;
import com.sdk4.common.CallResult;
import com.sdk4.common.util.CSVUtils;

public interface DeviceService {

	int add(Device device);

	Device getByDeviceId(String device_id);

	Device getByPhoneNumber(String phone_number);

	List<Device> getByGenerateId(String generate_id);

	Device syncData(String device_id);

	Device syncData(String phone_number, String generate_id, JSONObject jo);

	HomeDashboard dashboard(String custom_id);

	int count(String custom_id, String keyword, String device_id, String corp_name, String device_type,
			String manufacturer, String area_code, String working_state, String processing_state,
			String fuel_level_state, String fuel_pressure_state, List<Integer> status, String tag);

	List<Device> query(String custom_id, String keyword, String device_id, String corp_name, String device_type,
			String manufacturer, String area_code, String working_state, String processing_state,
			String fuel_level_state, String fuel_pressure_state, List<Integer> status, String tag, Integer page_index,
			int page_size);

	List<Device> query(List<String> device_ids);

	int updateParams(String device_id, Map<String, String> params);

	int updateTag(String device_id, String tag);

	CallResult<Device> alloc(String device_id, @Param("custom_id") String custom_id);

	int init(String device_id, Map<String, String> params);

	CallResult<Device> setid(String device_id, String device_type, String manufacturer, String device_user,
			String area_code, String local_address);

	Result execAtCommand(AtCmd cmd, Device device);

	int insertHIS(Device device);

	int countDataUpdate(String custom_id);

	List<DataUpdateTime> queryDataUpdate(String custom_id, int page_index, int page_size);

	CallResult<List<String>> importDevice(CSVUtils.Data data);

}
