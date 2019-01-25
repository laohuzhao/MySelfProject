package com.sdk4.biz.aote.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.sdk4.biz.aote.service.impl.BizConfig;
import com.sdk4.biz.aote.service.impl.BizConfig.KEY;

@RequestMapping("")
@Controller
public class HomeController {
	
	@Autowired
	BizConfig bizConfig;

    @ResponseBody
    @RequestMapping(value = "version")
    public String version(HttpServletRequest request, HttpServletResponse response) {

        return "v1.0.0";
    }

    @ResponseBody
	@RequestMapping(value = "/home/info", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String setting(@RequestBody Map<String, String> reqMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		Map<String, Object> data = new HashMap<String, Object>();

		data.put(KEY.APP_DOWN_URL.name(), bizConfig.getString(KEY.APP_DOWN_URL));
		
		result.put("code", 20000);
		result.put("data", data);

		return JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
	}
}
