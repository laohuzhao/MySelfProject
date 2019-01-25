package com.sdk4.biz.aote.controller;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sdk4.biz.aote.service.DeviceService;
import com.sdk4.common.util.ExceptionUtils;
import com.sdk4.common.util.RequestUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("data")
@Controller
public class DataSyncController {
	
	@Autowired
	DeviceService deviceService;

	@ResponseBody
	@RequestMapping(value = { "/sync" })
	public String sync(HttpServletRequest request, HttpServletResponse response) {
		/*
		Map<String, String> params = Maps.newHashMap();

		Enumeration<String> reqNames = request.getParameterNames();
		while (reqNames.hasMoreElements()) {
			String key = reqNames.nextElement();
			String val = request.getParameter(key);
			params.put(key, val);
		}*/
		String dataString = "";
		try {
			String reqString = RequestUtils.getStreamAsString(request.getInputStream(), "utf-8");
			dataString = URLDecoder.decode(reqString, "UTF-8");
			JSONObject jo = JSON.parseObject(dataString);
			Boolean code = jo.getBoolean("code");
			if (code != null && code) {
				String phone = jo.getString("phone");
				String generateID = jo.getString("generateID");
				String message = jo.getString("message");
				JSONObject data = jo.getJSONObject("data");
				deviceService.syncData(phone, generateID, data);
			}
			log.info("数据同步:{}:{}", request.getMethod(), dataString);
		} catch (Exception e) {
			String exString = ExceptionUtils.toString(e);
			log.info("数据同步失败:{}:{}:{}", request.getMethod(), dataString, exString);
		}

		return "success";
	}
}
