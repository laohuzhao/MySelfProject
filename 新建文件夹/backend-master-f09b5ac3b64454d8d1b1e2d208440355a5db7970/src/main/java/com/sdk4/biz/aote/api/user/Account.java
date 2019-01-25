package com.sdk4.biz.aote.api.user;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.sdk4.biz.aote.api.ApiResponse;
import com.sdk4.biz.aote.api.ApiService;
import com.sdk4.biz.aote.bean.Custom;
import com.sdk4.biz.aote.bean.LoginUser;
import com.sdk4.biz.aote.bean.SysUser;
import com.sdk4.biz.aote.service.CustomService;
import com.sdk4.biz.aote.service.UserService;
import com.sdk4.biz.aote.var.UserType;

@Service
public class Account implements ApiService {
	public final static String METHOD = "user.account";

	@Autowired
	UserService userService;

	@Autowired
	CustomService customService;

	@Override
	public String method() {
		return METHOD;
	}

	@Override
	public void service(JSONObject params, ApiResponse response, LoginUser loginUser) {
		if (loginUser.isSysUser()) {
			SysUser suser = userService.getUser(loginUser.getUser_id());
			responseUserInfo(response, suser);
		} else if (loginUser.isCustom()) {
			Custom custom = customService.get(loginUser.getCustom_id());
			responseUserInfo(response, custom);
		} else {
		}
	}

	public static void responseUserInfo(ApiResponse response, SysUser suser) {
		response.putData("user_type", UserType.SYS.name());
		response.putData("account", suser.getName());
		response.putData("corp_name", "平台运维人员");
		response.putData("mobile", suser.getMobile());
		response.putData("role_name", suser.getRole_id_());
	}

	public static void responseUserInfo(ApiResponse response, Custom custom) {
		response.putData("user_type", UserType.CUSTOM.name());
		response.putData("account", custom.getAccount());
		response.putData("corp_name", custom.getName());
		response.putData("mobile", custom.getMobile());
		response.putData("role_name", "维护公司");
	}
}
