package com.sdk4.biz.aote.service;

import java.util.Map;

import com.sdk4.biz.aote.bean.PushCID;

import lombok.Getter;
import lombok.Setter;

/**
 * 推送服务
 * 
 * @author sh
 *
 */
public interface PushService {

	PushCID reg(String user_id, String client_type, String client_id);

	PushCID cid(String user_id);

	PushResult push_message(PushCID cid, String title, String content, Map<String, Object> extra);

	@Setter
	@Getter
	public static class PushResult {
		private int code;
		private String task_id;
		private String error;
	}

}
