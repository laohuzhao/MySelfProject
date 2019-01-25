package com.sdk4.biz.aote.api.user;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.sdk4.biz.aote.api.ApiResponse;
import com.sdk4.biz.aote.api.ApiService;
import com.sdk4.biz.aote.bean.LoginUser;
import com.sdk4.biz.aote.service.PushService;
import com.sdk4.biz.aote.var.ClientType;

@Service
public class RegClient implements ApiService {
    public final static String METHOD = "reg.client";

    @Autowired
    PushService pushService;
    
    @Override
    public String method() {
        return METHOD;
    }

    @Override
    public void service(JSONObject params, ApiResponse response, LoginUser loginUser) {
        String client_type = params.getString("client_type");
        String client_id = params.getString("client_id");
        
        if (StringUtils.isEmpty(client_type)) {
            response.setCode(40001);
            response.setMessage("客户端类型不能为空");
        } else if (StringUtils.isEmpty(client_id)) {
            response.setCode(40001);
            response.setMessage("客户端id不能为空");
        } else {
            ClientType clientType = ClientType.UNKNOWN;
            if (StringUtils.isNotEmpty(client_type)) {
                try {
                    clientType = ClientType.valueOf(client_type.toUpperCase().trim());
                } catch (Exception e) {
                }
            }
            pushService.reg(loginUser.getUser_id(), client_type, client_id);
            response.setCode(0);
            response.setMessage("OK");
        }
    }
}
