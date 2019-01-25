package com.sdk4.biz.aote.api;

import com.alibaba.fastjson.JSONObject;
import com.sdk4.biz.aote.bean.LoginUser;

/**
 * 对外提供 API 服务接口
 * 
 * @author CNJUN
 */
public interface ApiService {
    
    /**
     * 对外提供 API 的服务名
     * 
     * @return
     */
    String method();

    /**
     * API 服务接口
     * 
     * @param params HTTP请求参数
     * @return
     */
    void service(JSONObject params, ApiResponse response, LoginUser loginUser);

}
