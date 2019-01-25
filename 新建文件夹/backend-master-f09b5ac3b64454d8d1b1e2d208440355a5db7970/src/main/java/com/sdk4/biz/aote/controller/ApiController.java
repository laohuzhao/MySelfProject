package com.sdk4.biz.aote.controller;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sdk4.biz.aote.api.ApiFactory;
import com.sdk4.biz.aote.api.ApiResponse;
import com.sdk4.biz.aote.api.ApiService;
import com.sdk4.biz.aote.bean.LoginUser;
import com.sdk4.biz.aote.bean.SysApiLog;
import com.sdk4.biz.aote.dao.SysApiLogDAO;
import com.sdk4.biz.aote.service.LoginService;
import com.sdk4.common.util.RequestUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * API接口接入
 * 
 * @author CNJUN
 */
@Slf4j
@RestController
@RequestMapping("api")
public class ApiController {
    @Autowired
    LoginService loginService;
    
    @Autowired
    SysApiLogDAO sysApiLogDAO;
    
    @RequestMapping(value = { "/v1", "/v1/{p1}", "/v1/{p1}/{p2}" })
    public ApiResponse api(HttpServletRequest request, HttpServletResponse response) {
        ApiResponse result = new ApiResponse();
        
        String url = request.getServletPath();
        
        String ip = RequestUtils.getRemoteAddress(request);
        String ua = request.getHeader("user-agent");
        String authorization = request.getHeader("authorization");
        String httpMethod = request.getMethod();
        
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            //System.out.println(name + "=" + request.getHeader(name));
        }
        
        String token = "";
        if (StringUtils.isNotEmpty(authorization) && authorization.startsWith("Token ")) {
            token = authorization.substring(6);
        }
        
        // 调用方法是否在 URL 上
        String urlMethod = "";
        if (url.startsWith("/api/v1/") && url.length() > 8) {
            urlMethod = url.substring(8);
            urlMethod = urlMethod.replace('/', '.');
        }
        
        SysApiLog apiLog = new SysApiLog();
        apiLog.setFrom_ip(ip);
        apiLog.setReq_time(new Date());
        apiLog.setToken(token);
        
        try {
            String reqJSON = RequestUtils.getStreamAsString(request.getInputStream(), "UTF-8");
            
            apiLog.setReq_content(reqJSON);
            
            if (StringUtils.isEmpty(reqJSON) && StringUtils.isEmpty(urlMethod)) {
                result.setCode(0);
                result.setMessage("server is running.");
                
                result.putData("v1", "http://127.0.0.1:8080/aote/api/v1");
            } else {
                String reqJSON_ = StringUtils.isEmpty(reqJSON) ? "{}" : reqJSON.trim();
                if (reqJSON_.startsWith("{") && reqJSON_.endsWith("}")) {
                    boolean go = true;
                    JSONObject obj = null;
                    try {
                        obj = JSON.parseObject(reqJSON);
                    } catch (Exception e) {
                        go = false;
                        result.setCode(40001);
                        result.setMessage("请求内容不是一个有效的JSON串");
                    }
                    if (go) {
                        String method = StringUtils.isNotEmpty(urlMethod) ? urlMethod : obj.getString("method");
                        if (StringUtils.isEmpty(method)) {
                            result.setCode(40002);
                            result.setMessage("method参数不能为空");
                        } else {
                            apiLog.setMethod(method);
                            
                            ApiService service = ApiFactory.getApi(method);
                            if (service == null) {
                                result.setCode(40003);
                                result.setMessage("请求method方法(" + method + ")不存在");
                            } else {
                                boolean ok = true;
                                
                                // 登录验证
                                LoginUser loginUser = null;
                                if (ApiFactory.requiredLogin(method)) {
                                    if (StringUtils.isEmpty(token)) {
                                        result.setCode(40001);
                                        result.setMessage("登录认证token为空");
                                        
                                        ok = false;
                                    } else {
                                        String token_ = new String(Base64.decodeBase64(token));
                                        loginUser = loginService.getByToken(token_);
                                        if (loginUser == null || !loginUser.isLogined()) {
                                            result.setCode(40000);
                                            result.setMessage("未登录");
                                            
                                            ok = false;
                                        } else {
                                            apiLog.setClient_type(loginUser.getClient_type());
                                            apiLog.setUser_type(loginUser.getUser_type().name());
                                            apiLog.setUser_id(loginUser.getUser_id());
                                        }
                                    }
                                }
                                
                                if (ok) {
                                    try {
                                        service.service(obj, result, loginUser);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        result.setCode(40004);
                                        result.setMessage("接口调用失败:" + method);
                                    }
                                    if (result.getCode() == null) {
                                        result.setCode(0);
                                    }
                                    if (StringUtils.isEmpty(result.getMessage())) {
                                        result.setMessage("success");
                                    }
                                }
                            }
                        }
                    }
                } else {
                    result.setCode(40000);
                    result.setMessage("请求内容不是一个有效的JSON串");
                }
            }
        } catch (IOException e) {
            result.setCode(50000);
            result.setMessage("请求失败");
        }
        
        if (StringUtils.isEmpty(apiLog.getMethod())) {
            apiLog.setMethod("NO");
        }
        
        apiLog.setResp_time(new Date());
        apiLog.setResp_content(JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss"));
        apiLog.setResp_code(result.getCode());
        apiLog.setResp_message(result.getMessage());
        
        try {
            this.sysApiLogDAO.insert(apiLog);
        } catch (Exception e) {
        }
        
        return result;
    }
    
}
