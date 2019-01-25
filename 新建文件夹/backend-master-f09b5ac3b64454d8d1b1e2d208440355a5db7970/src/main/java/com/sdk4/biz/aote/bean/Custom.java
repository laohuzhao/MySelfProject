package com.sdk4.biz.aote.bean;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Custom {

	private String id;
	private String code;
	private String name;
	private String contact;
	private String mobile;
	private String address;
	private String account;
	private String password;
	private Date add_time;
	private Integer sms_push;
	private Integer sms_alarm;
	private Integer device_count;
	private Integer status;
	private String user_id;

	// - 以下为未非数据库字段
	List<CustomType> custom_type;

}
