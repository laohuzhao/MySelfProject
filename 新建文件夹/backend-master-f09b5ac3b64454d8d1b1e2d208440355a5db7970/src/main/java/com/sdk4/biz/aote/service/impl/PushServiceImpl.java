package com.sdk4.biz.aote.service.impl;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.gexin.rp.sdk.template.style.Style6;
import com.google.common.collect.Maps;
import com.sdk4.biz.aote.Config;
import com.sdk4.biz.aote.bean.PushCID;
import com.sdk4.biz.aote.dao.PushCidDAO;
import com.sdk4.biz.aote.service.PushService;
import com.sdk4.biz.aote.var.PusherType;
import com.sdk4.common.util.ValueUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PushServiceImpl implements PushService {
	@Autowired
	PushCidDAO pushCidDAO;
	
	final static String getui_host = "http://sdk.open.api.igexin.com/apiex.htm";
	Config config = new Config();

	@Override
	public PushCID reg(String user_id, String client_type, String client_id) {
		String type = "GETUI";

		boolean isnew = false;
		PushCID pushCID = pushCidDAO.get(user_id, type);
		if (pushCID == null) {
			isnew = true;
			pushCID = new PushCID();
		}
		pushCID.setUser_id(user_id);
		pushCID.setType(type);
		pushCID.setCid(client_id);
		pushCID.setPlat(client_type);

		int n = 0;
		try {
			if (isnew) {
				n = pushCidDAO.insert(pushCID);
			} else {
				n = pushCidDAO.update(pushCID.getUser_id(), type, client_id, pushCID.getPlat());
			}
		} catch (Exception e) {
		}
		
		return pushCID;
	}

	@Override
	public PushCID cid(String user_id) {
		String cid = null;
        
        PushCID pushcid = pushCidDAO.get(user_id, PusherType.GETUI.name());
        if (pushcid != null) {
            cid = pushcid.getCid();
        }
        
        if (StringUtils.isEmpty(cid)) {
            pushcid = null;
        }
        
        return pushcid;
	}

	@Override
	public PushResult push_message(PushCID cid, String title, String content, Map<String, Object> extra) {
		PushResult push_result = new PushResult();
        
        IGtPush push = new IGtPush(getui_host, config.push_getui_AppKey, config.push_getui_MasterSecret);
        
        SingleMessage message = new SingleMessage();
        message.setOffline(true);
        // 离线有效时间，单位为毫秒，可选
        message.setOfflineExpireTime(24 * 3600 * 1000);
        
        if (extra == null) {
            extra = Maps.newHashMap();
        }
        
        extra.put("title", title);
        extra.put("content", content);
        
        if ("ios".equalsIgnoreCase(cid.getPlat())) {
            TransmissionTemplate template = createTransmissionTemplate(title, content, extra, true);
            message.setData(template);
        } else {
            TransmissionTemplate template = createTransmissionTemplate(title, content, extra, false);
            message.setData(template);
        }
        // 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
        message.setPushNetWorkType(0); 
        Target target = new Target();
        target.setAppId(config.push_getui_AppID);
        target.setClientId(cid.getCid());
        //target.setAlias(Alias);
        IPushResult ret = null;
        
        try {
            ret = push.pushMessageToSingle(message, target);
        } catch (RequestException e) {
            ret = push.pushMessageToSingle(message, target, e.getRequestId());
        }
        
        if (ret != null) {
            Map<String, Object> response = ret.getResponse();
            String result = ValueUtils.getString(response, "result", null);
            if (result != null && "ok".equals(result)) {
                String taskId = ValueUtils.getString(response, "taskId", "");
                String status = ValueUtils.getString(response, "status", "");
                push_result.setCode(0);
                push_result.setTask_id(taskId);
                push_result.setError(status);
            } else {
                push_result.setCode(500);
                push_result.setError("PUSH_FAIL:" + result);
            }
        } else {
            push_result.setCode(500);
            push_result.setError("服务器响应异常");
        }
        
        return push_result;
	}
	
	private NotificationTemplate createNotificationTemplate(String title, String content, Map<String, Object> extra) {
        NotificationTemplate template = new NotificationTemplate();

        // 设置APPID与APPKEY
        template.setAppId(config.push_getui_AppID);
        template.setAppkey(config.push_getui_AppKey);
        // 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
        template.setTransmissionType(2);
        template.setTransmissionContent(JSON.toJSONString(extra));
        // 设置定时展示时间
        // template.setDuration("2015-01-16 11:40:00", "2015-01-16 12:24:00");

        Style6 style = new Style6();
        // 设置通知栏标题与内容
        style.setTitle(title);
        style.setText(content);
        style.setBigStyle2(content);
        // 配置通知栏图标
        style.setLogo("icon.png");
        // 配置通知栏网络图标
        style.setLogoUrl("");
        // 设置通知是否响铃，震动，或者可清除
        style.setRing(true);
        style.setVibrate(true);
        style.setClearable(true);
        template.setStyle(style);

        return template;
    }
    
    // 透传消息
    private TransmissionTemplate createTransmissionTemplate(String title, String content, Map<String, Object> extra, boolean isIOS) {
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(config.push_getui_AppID);
        template.setAppkey(config.push_getui_AppKey);
        template.setTransmissionContent(JSON.toJSONString(extra));
        template.setTransmissionType(2);
        
        if (isIOS) {
            APNPayload payload = new APNPayload();
            //在已有数字基础上加1显示，设置为-1时，在已有数字上减1显示，设置为数字时，显示指定数字
            payload.setAutoBadge("+1");
            payload.setContentAvailable(1);
            payload.setSound("default");
            //payload.setCategory("$由客户端定义");
            for (Entry<String, Object> entry : extra.entrySet()) {
                payload.addCustomMsg(entry.getKey(), entry.getValue());
            }
    
            //简单模式APNPayload.SimpleMsg
            //payload.setAlertMsg(new APNPayload.SimpleAlertMsg("hello"));
    
            //字典模式使用APNPayload.DictionaryAlertMsg
            //payload.setAlertMsg(getDictionaryAlertMsg());
            APNPayload.DictionaryAlertMsg alertMsg = new APNPayload.DictionaryAlertMsg();
            alertMsg.setBody(content);
            //alertMsg.setActionLocKey("ActionLockey");
            //alertMsg.setLocKey("LocKey");
            //alertMsg.addLocArg("loc-args");
            //alertMsg.setLaunchImage("launch-image");
            // iOS8.2以上版本支持
            alertMsg.setTitle(title);
            //alertMsg.setTitleLocKey("TitleLocKey");
            //alertMsg.addTitleLocArg("TitleLocArg");
    
            payload.setAlertMsg(alertMsg);
            
            // 添加多媒体资源
            /*
            payload.addMultiMedia(new MultiMedia().setResType(MultiMedia.MediaType.video)
                        .setResUrl("http://ol5mrj259.bkt.clouddn.com/test2.mp4")
                        .setOnlyWifi(true));*/
    
            template.setAPNInfo(payload);
        }
        
        return template;
    }

}
