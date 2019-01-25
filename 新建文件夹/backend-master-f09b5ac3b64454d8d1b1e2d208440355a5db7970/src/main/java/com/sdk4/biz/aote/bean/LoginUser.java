package com.sdk4.biz.aote.bean;

import java.util.Date;

import com.sdk4.biz.aote.var.UserType;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginUser implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private String token;
	private String client_type;
	private String client_key;
	private UserType user_type;
	private String user_id;
	private Date login_time;

	private int login_result; // 200 - 登录成功; 401 - 账号禁用; 404 - 账号不存在; 500 - 未设置密码; 501 - 密码错误
	private String custom_id; // 客户ID

	com.sdk4.biz.aote.Config config = new com.sdk4.biz.aote.Config();
	
	public boolean isLogined() {
		return login_result == 200;
	}
	
	public boolean isSuperManager() {
		return user_id != null && com.sdk4.biz.aote.Config.super_manager_id.contains(user_id);
	}

	public boolean isSysUser() {
		return UserType.SYS == user_type;
	}

	public boolean isSuperCustom() {
		return UserType.CUSTOM == user_type;
	}
	
	public boolean isCustom() {
		return UserType.CUSTOM == user_type || UserType.CUSTOM_USER == user_type;
	}
}
