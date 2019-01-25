package com.sdk4.biz.aote.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.sdk4.biz.aote.bean.LoginUser;
import com.sdk4.biz.aote.bean.SysRole;
import com.sdk4.biz.aote.bean.SysUser;
import com.sdk4.biz.aote.service.LoginService;
import com.sdk4.biz.aote.service.UserService;
import com.sdk4.common.CallResult;
import com.sdk4.common.util.PageUtils;

@RequestMapping("auth")
@Controller
public class UserRoleController {
	@Autowired
	UserService userService;

	@Autowired
	LoginService loginService;

	@ResponseBody
	@RequestMapping(value = "/user_list", produces = "application/json;charset=utf-8")
	public String user_list(HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		String page_ = request.getParameter("page");
		String limit_ = request.getParameter("limit");
		String custom_id_x = request.getParameter("custom_id");

		int page = 1, limit = 10;
		if (StringUtils.isNotEmpty(page_)) {
			page = Integer.parseInt(page_);
		}
		if (StringUtils.isNotEmpty(limit_)) {
			limit = Integer.parseInt(limit_);
		}

		Integer custom = null;
		String custom_id = null;
		LoginUser loginUser = loginService.getLoginUser();
		if (loginUser.isCustom()) {
			custom_id = loginUser.getCustom_id();
		} else {
			if (StringUtils.isNotEmpty(custom_id_x)) {
				custom_id = custom_id_x;
			} else {
				custom = 0;
			}
		}
		
		List<SysUser> userList;
		int total = userService.countUser(null, custom, custom_id);
		int page_count = PageUtils.pageCount(total, limit);
		if (page > page_count) {
			page = page_count;
		}

		userList = userService.queryUser(null, custom, custom_id, page, limit);
		
		for (SysUser user : userList) {
			user.setPassword(null);
		}
		data.put("items", userList);
		data.put("total", total);

		result.put("code", 20000);
		result.put("data", data);

		return JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
	}

	@ResponseBody
	@RequestMapping(value = "/user_save", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String user_save(@RequestBody Map<String, String> reqMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Map<String, Object> result = new HashMap<String, Object>();

		String id = reqMap.get("id");
		String name = reqMap.get("name");
		String mobile = reqMap.get("mobile");
		String password = reqMap.get("password");
		String role_id = reqMap.get("role_id");
		String custom_id = reqMap.get("custom_id");

		if (StringUtils.isEmpty(name)) {
			result.put("code", 40000);
			result.put("data", "账号不能为空");
		} else if (StringUtils.isEmpty(id) && StringUtils.isEmpty(password)) {
			result.put("code", 40000);
			result.put("data", "登录密码不能为空");
		} else if (StringUtils.isNotEmpty(password) && (password.length() < 8 || password.length() > 20)) {
			result.put("code", 40001);
			result.put("data", "密码长度为8-20位");
		} else {
			SysUser user = new SysUser();
			user.setId(id);
			user.setName(name);
			user.setMobile(mobile);

			LoginUser loginUser = loginService.getLoginUser();
			if (loginUser.isCustom()) {
				user.setCustom_id(loginUser.getCustom_id());
			} else {
				if (StringUtils.isEmpty(custom_id)) {
					user.setCustom_id("");
				} else {
					user.setCustom_id(custom_id);
				}
			}
			if (StringUtils.isEmpty(user.getCustom_id())) {
				user.setCustom(0);
			} else {
				user.setCustom(2);
			}

			if (StringUtils.isNotEmpty(password)) {
				String pwd = new Md5Hash(password).toHex();
				user.setPassword(pwd);
			}
			user.setRole_id(role_id);
			int n = userService.saveUser(user);

			if (n == 1) {
				result.put("code", 20000);
				result.put("data", "success");
			} else if (n == -1) {
				result.put("code", 40000);
				result.put("data", "账号或手机号码已经存在：" + name);
			} else {
				result.put("code", 40000);
				result.put("data", "保存失败");
			}
		}

		return JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
	}

	@ResponseBody
	@RequestMapping(value = "/user_delete", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String user_delete(@RequestBody Map<String, String> reqMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Map<String, Object> result = new HashMap<String, Object>();

		String id = reqMap.get("id");

		if (StringUtils.isEmpty(id)) {
			result.put("code", 40000);
			result.put("data", "请选择要删除的用户");
		} else {
			CallResult<Void> callr = userService.deleteUser(id);

			if (callr.success()) {
				result.put("code", 20000);
			} else {
				result.put("code", 40000);
			}
			result.put("data", callr.getMessage());
		}

		return JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
	}

	@ResponseBody
	@RequestMapping(value = "/select_role", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String select_role(@RequestBody Map<String, String> reqMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		String type = reqMap.get("type");

		List<SysRole> roleList = userService.queryRole(type);
		for (SysRole role : roleList) {
			role.setAuth_list(null);
		}
		data.put("items", roleList);

		result.put("code", 20000);
		result.put("data", data);

		return JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
	}

	@ResponseBody
	@RequestMapping(value = "/role_list", produces = "application/json;charset=utf-8")
	public String role_list(@RequestBody Map<String, String> reqMap, HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		String type = reqMap.get("type");
		
		List<SysRole> roleList = userService.queryRole(type);
		data.put("items", roleList);

		result.put("code", 20000);
		result.put("data", data);

		return JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
	}

	@ResponseBody
	@RequestMapping(value = "/role_save", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String role_save(@RequestBody Map<String, String> reqMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Map<String, Object> result = new HashMap<String, Object>();

		String id = reqMap.get("id");
		String type = reqMap.get("type");
		String name = reqMap.get("name");
		String auth_list = reqMap.get("auth_list");

		if (StringUtils.isEmpty(name)) {
			result.put("code", 40000);
			result.put("data", "角色名称不能为空");
		} else if (StringUtils.isEmpty(type)) {
			result.put("code", 40000);
			result.put("data", "角色类型不能为空");
		} else {
			String custom_id = null;
			LoginUser loginUser = loginService.getLoginUser();
			if (loginUser.isCustom()) {
				custom_id = loginUser.getCustom_id();
			}
			
			SysRole role = new SysRole();
			role.setId(id);
			role.setType(type);
			role.setName(name);
			role.setAuth_list(auth_list);
			
			int n = userService.saveRole(role);

			if (n == 1) {
				result.put("code", 20000);
				result.put("data", "保存成功");
			} else if (n == -1) {
				result.put("code", 40000);
				result.put("data", "角色名称已经存在：" + name);
			} else {
				result.put("code", 40000);
				result.put("data", "保存失败");
			}
		}

		return JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
	}

	@ResponseBody
	@RequestMapping(value = "/role_delete", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String role_delete(@RequestBody Map<String, String> reqMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Map<String, Object> result = new HashMap<String, Object>();

		String id = reqMap.get("id");

		if (StringUtils.isEmpty(id)) {
			result.put("code", 40000);
			result.put("data", "请选择要删除的角色");
		} else {
			CallResult<Void> callr = userService.deleteRole(id);

			if (callr.success()) {
				result.put("code", 20000);
			} else {
				result.put("code", 40000);
			}
			result.put("data", callr.getMessage());
		}

		return JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
	}

}
