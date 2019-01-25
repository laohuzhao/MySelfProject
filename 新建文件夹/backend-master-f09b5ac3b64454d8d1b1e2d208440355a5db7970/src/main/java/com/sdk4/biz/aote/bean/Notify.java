package com.sdk4.biz.aote.bean;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Notify {

	private String id;
	private String device_id;
	private String type;
	private String date_str;
	private String content;
	private Date add_time;
	private Date read_time;
	private int sms_send;
	private String sms_mobile;
	private Date sms_time;

}
