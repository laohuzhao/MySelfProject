package com.sdk4.biz.aote.bean;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SysUser implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private String mobile;
	private String password;
	private Date add_time;
	private String role_id;
	private Integer sms_push;
	private Integer status;
	private int custom; // 0 - 非客户；1 - 客户超级管理员；2 - 客户用户
	private String custom_id; // 归属客户

	//
	private String role_id_;
}
