package com.sdk4.common;

import com.alibaba.fastjson.JSON;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CallResult<T> {
	private int code;
	private String message;
	private T data;

	public boolean success() {
		return code == 0;
	}

	public String toJSONString() {
		return JSON.toJSONStringWithDateFormat(this, "yyyy-MM-dd HH:mm:ss");
	}
}
