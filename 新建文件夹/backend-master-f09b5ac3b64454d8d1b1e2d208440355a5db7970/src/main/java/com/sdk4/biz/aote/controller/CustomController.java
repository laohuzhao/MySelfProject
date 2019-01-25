package com.sdk4.biz.aote.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.sdk4.biz.aote.bean.Custom;
import com.sdk4.biz.aote.bean.LoginUser;
import com.sdk4.biz.aote.service.CustomService;
import com.sdk4.biz.aote.service.LoginService;
import com.sdk4.common.CallResult;
import com.sdk4.common.exception.DbException;
import com.sdk4.common.util.BeanUtils;
import com.sdk4.common.util.PageUtils;

@RequestMapping("custom")
@Controller
public class CustomController {

	@Autowired
	CustomService customService;

	@Autowired
	LoginService loginService;

	@ResponseBody
	@RequestMapping(value = "/list", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String list(@RequestBody Map<String, String> reqMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		String page_ = reqMap.get("page");
		String limit_ = reqMap.get("limit");
		String keyword = reqMap.get("keyword");

		int page = 1, limit = 10;
		if (StringUtils.isNotEmpty(page_)) {
			page = Integer.parseInt(page_);
		}
		if (StringUtils.isNotEmpty(limit_)) {
			limit = Integer.parseInt(limit_);
		}

		String custom_id = null;
		LoginUser loginUser = loginService.getLoginUser();
		if (loginUser.isCustom()) {
			custom_id = loginUser.getCustom_id();
			Custom custom = customService.get(custom_id);
			custom.setPassword("");

			List<Custom> list = Lists.newArrayList();
			list.add(custom);

			data.put("items", list);
			data.put("total", list.size());

			result.put("code", 20000);
			result.put("data", data);
		} else {
			int total = customService.count(keyword);
			data.put("total", total);

			int page_count = PageUtils.pageCount(total, limit);
			if (page > page_count) {
				page = page_count;
			}

			List<Custom> list = customService.query(keyword, page, limit);

			for (Custom c : list) {
				c.setPassword(null);
			}
			data.put("items", list);

			result.put("code", 20000);
			result.put("data", data);
		}

		return JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
	}

	@ResponseBody
	@RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String save(@RequestBody Map<String, Object> reqMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Map<String, Object> result = new HashMap<String, Object>();

		if (reqMap.containsKey("add_time")) {
			reqMap.remove("add_time");
		}
		String custom_type_ = "";
		if (reqMap.containsKey("custom_type_")) {
			custom_type_ = reqMap.remove("custom_type_").toString();
		}

		Custom custom = BeanUtils.toObject(reqMap, Custom.class);

		String pwd = custom == null ? "" : custom.getPassword();

		if (custom == null) {
			result.put("code", 40001);
			result.put("data", "请求参数不正确");
		} else if (StringUtils.isNotEmpty(pwd) && (pwd.length() < 8 || pwd.length() > 20)) {
			result.put("code", 40001);
			result.put("data", "密码长度为8-20位");
		} else {
			try {
				CallResult<Custom> callr = customService.save(custom, custom_type_);

				if (callr.success()) {
					result.put("code", 20000);
				} else {
					result.put("code", 40000);
				}
				result.put("data", callr.getMessage());
			} catch (DbException e) {
				result.put("code", 40000);
				result.put("data", "保存失败:" + e.getMessage());
			}
		}

		return JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
	}

	@ResponseBody
	@RequestMapping(value = "/change_status", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String change_status(@RequestBody Map<String, String> reqMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Map<String, Object> result = new HashMap<String, Object>();

		String id = reqMap.get("id");
		String type = reqMap.get("type");
		String status = reqMap.get("status");
		if (StringUtils.isEmpty(id) || StringUtils.isEmpty(type) || StringUtils.isEmpty(status)) {
			result.put("code", 40001);
			result.put("data", "请求参数不正确");
		} else {
			int n = customService.changeStatus(id, type, Integer.parseInt(status));

			if (n == 1) {
				result.put("code", 20000);
				result.put("data", "success");
			} else if (n == -1) {
				result.put("code", 40000);
				result.put("data", "状态修改失败");
			} else {
				result.put("code", 40000);
				result.put("data", "状态修改失败");
			}
		}

		return JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
	}
}
