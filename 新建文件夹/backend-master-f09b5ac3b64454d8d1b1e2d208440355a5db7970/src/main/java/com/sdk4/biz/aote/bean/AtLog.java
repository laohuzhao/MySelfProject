package com.sdk4.biz.aote.bean;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AtLog {

	private String id;
	private String device_id;
	private String phone_number;
	private String generate_id;
	private String type;
	private Date req_time;
	private String req_content;
	private Date resp_time;
	private String resp_content;
	private String resp_code;
	private String resp_message;
	private String exception;

}
