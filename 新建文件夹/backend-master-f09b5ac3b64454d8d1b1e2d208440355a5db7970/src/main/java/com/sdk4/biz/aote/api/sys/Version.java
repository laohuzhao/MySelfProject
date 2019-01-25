package com.sdk4.biz.aote.api.sys;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.sdk4.biz.aote.Constants;
import com.sdk4.biz.aote.api.ApiResponse;
import com.sdk4.biz.aote.api.ApiService;
import com.sdk4.biz.aote.bean.LoginUser;

/**
 * 获取API版本号
 * 
 * @author CNJUN
 */
@Service
public class Version implements ApiService {
    public final static String METHOD = "version";
    
    @Override
    public String method() {
        return METHOD;
    }

    @Override
    public void service(JSONObject params, ApiResponse response, LoginUser loginUser) {
        response.putData("version", Constants.API_VERSION);
    }

}