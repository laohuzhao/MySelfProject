package com.sdk4.biz.aote.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.sdk4.biz.aote.bean.PushCID;
import com.sdk4.biz.aote.service.AtService;
import com.sdk4.biz.aote.service.PushService;
import com.sdk4.biz.aote.var.AtCmd;

@RequestMapping("/test")
@Controller
public class TestController {

	@Autowired
	AtService atService;

	@Autowired
	PushService pushService;

	@ResponseBody
	@RequestMapping(value = "at", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
	public String at(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> result = new HashMap<String, Object>();

		String phoneNumber = "1310000001";
		String generateID = "0000111100001111";

		// 打油
		Map<String, String> params = Maps.newHashMap();
		atService.exec(AtCmd.LUBE, phoneNumber, generateID, params);

		// 设备初始化
		params.clear();
		params.put("P1", "10");
		params.put("P2", "20");
		params.put("P3", "30");
		params.put("P4", "40");
		params.put("uploadPeriod", "30");
		params.put("version", "0");
		atService.exec(AtCmd.INIT, phoneNumber, generateID, params);

		// 设置运行参数
		params.clear();
		params.put("P1", "10");
		params.put("P2", "20");
		params.put("P3", "30");
		params.put("P4", "40");
		atService.exec(AtCmd.SET_PARAMS, phoneNumber, generateID, params);

		// 设置版本
		params.clear();
		params.put("version", "0");
		atService.exec(AtCmd.SET_VERSION, phoneNumber, generateID, params);

		// 停止运行
		params.clear();
		atService.exec(AtCmd.STOP, phoneNumber, generateID, params);

		// 更新当前状态
		params.clear();
		atService.exec(AtCmd.UPDATE, phoneNumber, generateID, params);

		// 设置主动上传周期
		params.clear();
		params.put("uploadPeriod", "30");
		atService.exec(AtCmd.SET_UPLOAD_PERIOD, phoneNumber, generateID, params);

		// 监听数据上传
		params.clear();
		atService.exec(AtCmd.LISTEN, phoneNumber, generateID, params);

		// 预警短信发送功能
		params.clear();
		params.put("userPhoneNumber", "13112345678");
		params.put("errorInfo", "错误报警-1");
		atService.exec(AtCmd.SMS, phoneNumber, generateID, params);

		return "" + JSON.toJSONString(result);
	}

	@ResponseBody
	@RequestMapping(value = "push", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
	public String push(HttpServletRequest request, HttpServletResponse response) {
		String result = "";
		String title = "设备报警";
		String content = "设备abc在2017-10-10 10:10:10出现低温保护，请及时处理。【" + System.currentTimeMillis() + "】";
		Map<String, Object> extra = Maps.newHashMap();
		extra.put("device_id", "1");
		extra.put("alarm_type", "31");

		PushCID pcid = new PushCID();
		pcid.setUser_id("2fbb43f5d612eefab4d08059b3a1afec");
		pcid.setType("GETUI");
		pcid.setCid("bb3223b846a82bb77d57a5745235c038");
		pcid.setPlat("ios");
		PushService.PushResult push_re = pushService.push_message(pcid, title, content, extra);
		if (push_re.getCode() == 0) {
			result = "推送成功:" + push_re.getTask_id();
		} else {
			result = "推送失败:" + push_re.getError();
		}

		return result;
	}

	@ResponseBody
	@RequestMapping(value = "exception", method = RequestMethod.GET, produces = "text/html;charset=utf-8")
	public String exception(HttpServletRequest request, HttpServletResponse response) {
		String result = "is ok";

		int n = 10 / 0;

		return result;
	}
}
