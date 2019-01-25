package com.sdk4.biz.aote.api.user;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.sdk4.biz.aote.api.ApiResponse;
import com.sdk4.biz.aote.api.ApiService;
import com.sdk4.biz.aote.bean.LoginUser;
import com.sdk4.biz.aote.service.CustomService;
import com.sdk4.biz.aote.service.LoginService;
import com.sdk4.biz.aote.service.PushService;
import com.sdk4.biz.aote.service.UserService;
import com.sdk4.biz.aote.var.ClientType;

@Service
public class Login implements ApiService {
	public final static String METHOD = "user.login";

	@Autowired
	LoginService loginService;

	@Autowired
	UserService userService;

	@Autowired
	CustomService customService;

	@Autowired
	PushService pushService;

	@Override
	public String method() {
		return METHOD;
	}

	@Override
	public void service(JSONObject params, ApiResponse response, LoginUser loginUser) {
		String client_type = params.getString("client_type");
		String client_id = params.getString("client_id");
		String username = params.getString("username");
		String password = params.getString("password");

		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
			response.setCode(40001);
			response.setMessage("账号或密码不能为空");
		} else {
			ClientType clientType = ClientType.UNKNOWN;
			if (StringUtils.isNotEmpty(client_type)) {
				try {
					clientType = ClientType.valueOf(client_type.toUpperCase().trim());
				} catch (Exception e) {
				}
			}
			loginUser = loginService.login(username, password, clientType, "");
			if (loginUser.isLogined()) {
				response.setCode(0);
				response.setMessage("登录成功");

				response.putData("token", loginUser.getToken());
				// 账号信息
				if (loginUser.isSysUser()) {
					Account.responseUserInfo(response, userService.getUser(loginUser.getUser_id()));
				} else if (loginUser.isCustom()) {
					Account.responseUserInfo(response, customService.get(loginUser.getCustom_id()));
				}

				pushService.reg(loginUser.getUser_id(), client_type, client_id);
			} else if (loginUser.getLogin_result() == 404) {
				response.setCode(40004);
				response.setMessage("账号不存在");
			} else if (loginUser.getLogin_result() == 401) {
				response.setCode(40001);
				response.setMessage("账号被禁用请联系管理员");
			} else {
				response.setCode(40002);
				response.setMessage("账号或密码错误");
			}
		}
	}
}
