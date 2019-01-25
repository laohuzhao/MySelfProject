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

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.sdk4.biz.aote.bean.Custom;
import com.sdk4.biz.aote.bean.LoginUser;
import com.sdk4.biz.aote.bean.SysUser;
import com.sdk4.biz.aote.service.CustomService;
import com.sdk4.biz.aote.service.LoginService;
import com.sdk4.biz.aote.service.UserService;

@RequestMapping("setting")
@Controller
public class SettingController {

	@Autowired
	LoginService loginService;

	@Autowired
	CustomService customService;

	@Autowired
	UserService userService;

	@ResponseBody
	@RequestMapping(value = "/index", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String index(@RequestBody Map<String, String> reqMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		String custom_id = null;
		LoginUser loginUser = loginService.getLoginUser();
		if (loginUser != null && loginUser.isCustom()) {
			custom_id = loginUser.getCustom_id();
		}

		SysUser suser = userService.getUser(loginUser.getUser_id());
		data.put("name", suser.getName());
		data.put("mobile", suser.getMobile());
		data.put("sms_push", suser.getSms_push());
		if (loginUser.isSysUser()) {
			data.put("role_name", suser.getRole_id_());
			data.put("corp_name", "总公司");
		} else if (loginUser.isCustom()) {
			Custom custom = customService.get(loginUser.getCustom_id());
			data.put("role_name", suser.getRole_id_());
			data.put("corp_name", custom.getName());
		}

		result.put("code", 20000);
		result.put("data", data);

		return JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
	}

	@ResponseBody
	@RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String save(@RequestBody Map<String, String> reqMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Map<String, Object> result = new HashMap<String, Object>();

		String sms_push_ = reqMap.get("sms_push");

		if (StringUtils.isEmpty(sms_push_)) {
			result.put("code", 40000);
			result.put("data", "参数不正确");
		} else {
			int n = 0;
			LoginUser loginUser = loginService.getLoginUser();
			if (loginUser.isSysUser()) {
				n = this.userService.changeSmsPushChange(loginUser.getUser_id(), Integer.parseInt(sms_push_));
			} else if (loginUser.isCustom()) {
				n = this.customService.changeSmsPushChange(loginUser.getCustom_id(), Integer.parseInt(sms_push_));
			}
			if (n == 1) {
				result.put("code", 20000);
				result.put("data", "保存成功");
			} else {
				result.put("code", 40000);
				result.put("data", "保存失败");
			}
		}

		return JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
	}
}
