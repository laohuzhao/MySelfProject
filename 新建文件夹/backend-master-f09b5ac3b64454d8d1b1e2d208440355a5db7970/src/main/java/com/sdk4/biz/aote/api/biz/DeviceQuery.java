package com.sdk4.biz.aote.api.biz;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sdk4.biz.aote.api.ApiResponse;
import com.sdk4.biz.aote.api.ApiService;
import com.sdk4.biz.aote.bean.Device;
import com.sdk4.biz.aote.bean.LoginUser;
import com.sdk4.biz.aote.service.DeviceService;
import com.sdk4.biz.aote.var.DeviceStatus;
import com.sdk4.common.util.PageUtils;
import com.sdk4.common.util.ValueUtils;

@Service
public class DeviceQuery implements ApiService {
	public final static String METHOD = "device.query";

	SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	DeviceService deviceService;

	@Override
	public String method() {
		return METHOD;
	}

	@Override
	public void service(JSONObject params, ApiResponse response, LoginUser loginUser) {
		int page_index = params.getIntValue("page_index");
		int page_size = params.getIntValue("page_size");

		if (page_index < 1) {
			page_index = 1;
		}
		if (page_size < 1) {
			page_size = 10;
		}

		String keyword = params.getString("keyword");
		String working_state = params.getString("working_state");
		String processing_state = params.getString("processing_state");
		String fuel_level_state = params.getString("fuel_level_state");
		String fuel_pressure_fault = params.getString("fuel_pressure_fault");
		String status = params.getString("status");

		List<Integer> status_ = Lists.newArrayList();
		if (StringUtils.isNotEmpty(status)) {
			status_.add(Integer.parseInt(status));
		} else {
			status_.add(DeviceStatus.ONLINE.getCode());
			status_.add(DeviceStatus.OFFLINE.getCode());
		}

		String custom_id = null;
		if (loginUser != null && loginUser.isCustom()) {
			custom_id = loginUser.getCustom_id();
		}

		int total_count = deviceService.count(custom_id, keyword, null, null, null, null, null, working_state,
				processing_state, fuel_level_state, fuel_pressure_fault, status_, null);
		int page_count = PageUtils.pageCount(total_count, page_size);

		response.putData("page_index", page_index);
		response.putData("page_size", page_size);
		response.putData("page_count", page_count);
		response.putData("total_count", total_count);

		List<Map<String, Object>> listData = Lists.newArrayList();

		List<Device> list = deviceService.query(custom_id, keyword, null, null, null, null, null, working_state,
				processing_state, fuel_level_state, fuel_pressure_fault, status_, null, page_index, page_size);
		for (Device device : list) {
			Map<String, Object> item = Maps.newHashMap();
			item.put("device_id", device.getDevice_id());
			item.put("custom_name", ValueUtils.getString(device.getCustom_id_(), "未分配"));
			item.put("working_state", device.getWorking_state());
			item.put("processing_state", device.getProcessing_state());
			item.put("fuel_level_state", device.getFuel_level_state());
			item.put("fuel_pressure_state", device.getFuel_pressure_state());
			item.put("temperature", device.getTemperature());
			item.put("longitude", device.getLongitude());
			item.put("latitude", device.getLatitude());
			item.put("p1", device.getP1());
			item.put("p2", device.getP2());
			item.put("p3", device.getP3());
			item.put("p4", device.getP4());
			item.put("time", SDF.format(new Date()));
			item.put("status", device.getStatus());

			listData.add(item);
		}

		response.putData("device_list", listData);
	}
}
