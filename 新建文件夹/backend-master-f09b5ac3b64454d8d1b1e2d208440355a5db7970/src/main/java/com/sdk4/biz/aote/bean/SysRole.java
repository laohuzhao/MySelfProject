package com.sdk4.biz.aote.bean;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SysRole implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
	private String type; // 类型：SYS-系统；CUSTOM-客户角色
	private String name;
	private String auth_list;
	
}
