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
import com.sdk4.biz.aote.service.AtService;
import com.sdk4.biz.aote.service.DeviceService;
import com.sdk4.biz.aote.var.AtCmd;
import com.sdk4.biz.aote.var.DeviceStatus;
import com.sdk4.common.util.PageUtils;
import com.sdk4.common.util.ValueUtils;

@Service
public class DeviceCommand implements ApiService {
	public final static String METHOD = "device.command";

	SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	DeviceService deviceService;

	@Override
	public String method() {
		return METHOD;
	}

	@Override
	public void service(JSONObject params, ApiResponse response, LoginUser loginUser) {
		String device_id = params.getString("device_id");
		String cmd = params.getString("cmd");
		Device device = StringUtils.isEmpty(device_id) ? null : this.deviceService.getByDeviceId(device_id);

		AtCmd atcmd = null;
		try {
			atcmd = AtCmd.valueOf(cmd);
		} catch (Exception e) {
		}

		if (StringUtils.isEmpty(device_id)) {
			response.setCode(400);
			response.setMessage("未选择设备");
		} else if (device == null) {
			response.setCode(400);
			response.setMessage("设备不存在");
		} else if (StringUtils.isEmpty(cmd)) {
			response.setCode(400);
			response.setMessage("指令不能为空");
		} else if (atcmd == null) {
			response.setCode(400);
			response.setMessage("指令不存在");
		} else {
			AtService.Result res = deviceService.execAtCommand(atcmd, device);
			if (res.isCode()) {
				response.setCode(0);
				response.setMessage(atcmd == AtCmd.LUBE ? "打油成功" : atcmd == AtCmd.STOP ? "停止润滑成功" : "指令操作成功");

				device = this.deviceService.getByDeviceId(device_id);
				if (device != null) {
					response.putData("device_id", device.getDevice_id());
					response.putData("custom_name", ValueUtils.getString(device.getCustom_id_(), "未分配"));
					response.putData("working_state", device.getWorking_state());
					response.putData("processing_state", device.getProcessing_state());
					response.putData("fuel_level_state", device.getFuel_level_state());
					response.putData("fuel_pressure_state", device.getFuel_pressure_state());
					response.putData("temperature", device.getTemperature());
					response.putData("longitude", device.getLongitude());
					response.putData("latitude", device.getLatitude());
					response.putData("p1", device.getP1());
					response.putData("p2", device.getP2());
					response.putData("p3", device.getP3());
					response.putData("p4", device.getP4());
					response.putData("time", SDF.format(new Date()));
					response.putData("status", device.getStatus());
				}
			} else {
				response.setCode(400);
				response.setMessage(atcmd == AtCmd.LUBE ? "打油失败" : atcmd == AtCmd.STOP ? "停止润滑失败" : "指令操作失败");
			}
		}
	}
}
