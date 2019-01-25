package com.sdk4.biz.aote;

import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

public class Config {

	public static String push_getui_AppID;
	public static String push_getui_AppSecret;
	public static String push_getui_AppKey;
	public static String push_getui_MasterSecret;
	public static List<String> super_manager_id = Lists.newArrayList();

	static {
		ResourceBundle rb = ResourceBundle.getBundle("config");
		if (rb != null) {
			push_getui_AppID = rb.getString("push.getui.AppID");
			push_getui_AppSecret = rb.getString("push.getui.AppSecret");
			push_getui_AppKey = rb.getString("push.getui.AppKey");
			push_getui_MasterSecret = rb.getString("push.getui.MasterSecret");

			String temp = rb.getString("super.manager.id");
			if (StringUtils.isNotEmpty(temp)) {
				for (String str : temp.split("[,]")) {
					super_manager_id.add(str.trim());
				}
			}
		}
	}
}
