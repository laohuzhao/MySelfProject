package com.sdk4.biz.aote.var;

import lombok.Getter;

/**
 * 
 * @author CNJUN
 */
@Getter
public enum UserType {

	SYS(0, "奥特管理员"), CUSTOM(1, "客户管理员"), CUSTOM_USER(2, "客户账号");

	private int code;
	private String text;

	UserType(int code, String text) {
		this.code = code;
		this.text = text;
	}
}
