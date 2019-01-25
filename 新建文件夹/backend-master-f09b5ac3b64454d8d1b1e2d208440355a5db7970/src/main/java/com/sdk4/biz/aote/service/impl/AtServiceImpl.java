package com.sdk4.biz.aote.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.sdk4.biz.aote.bean.AtLog;
import com.sdk4.biz.aote.dao.AtLogDAO;
import com.sdk4.biz.aote.service.AtService;
import com.sdk4.biz.aote.var.AtCmd;
import com.sdk4.common.util.ExceptionUtils;
import com.sdk4.common.util.HttpUtils;
import com.sdk4.common.util.IdUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AtServiceImpl implements AtService {
    
    @Autowired
    AtLogDAO atLogDAO;
    
    @Autowired
    BizConfig bizConfig;
    
    public AtService.Result exec(AtCmd cmd, String phoneNumber, String generateID, Map<String, String> params) {
        AtService.Result result = new AtService.Result();
        
        String url = bizConfig.getString(BizConfig.KEY.AT_SERVER_URL) + cmd.getUrl();
        
        AtLog atLog = new AtLog();
        // 参数
        if (params == null) {
            params = Maps.newHashMap();
        }
        if (StringUtils.isNotEmpty(phoneNumber)) {
            params.put("phoneNumber", phoneNumber);
        }
        if (StringUtils.isNotEmpty(generateID)) {
            params.put("generateID", generateID);
        }
        
        atLog.setId(IdUtils.newStrId());
        atLog.setDevice_id(generateID);
        atLog.setPhone_number(phoneNumber);
        atLog.setGenerate_id(generateID);
        atLog.setType(cmd.name());
        atLog.setReq_time(new Date());
        atLog.setReq_content(JSON.toJSONString(params));
        
        try {
            int timeout = bizConfig.getInt(BizConfig.KEY.AT_CMD_TIMEOUT);
            if (timeout < 1) {
                timeout = 60000;
            } else {
                timeout = timeout * 1000;
            }
            
            String resp = HttpUtils.doGet(url, params, "UTF-8", 10000, timeout);
            atLog.setResp_time(new Date());
            atLog.setResp_content(resp);
            
            JSONObject jo = JSON.parseObject(resp);
            if (jo.containsKey("code")) {
                if (jo.getBooleanValue("code")) {
                    result.setCode(true);
                } else {
                    result.setCode(false);
                }
            } else {
                result.setCode(false);
            }
            result.setPhone(jo.getString("phone"));
            result.setGenerateID(jo.getString("generateID"));
            if (jo.containsKey("message")) {
                result.setMessage(jo.getString("message"));
            }
            if (jo.containsKey("data")) {
                result.setData(jo.getJSONObject("data"));
            }
        } catch (IOException e) {
        		String exString = ExceptionUtils.toString(e);
        		atLog.setException(exString);
        		
            result.setCode(false);
            result.setMessage("通信错误");
        }
        
        atLog.setResp_code(String.valueOf(result.isCode()));
        atLog.setResp_message(result.getMessage());
        
        try {
            if (atLog.getResp_message().length() > 100) {
                atLog.setResp_message(atLog.getResp_message().substring(0, 99));
            }
            if (atLog.getException().length() > 5000) {
                atLog.setException(atLog.getException().substring(0, 5000));
            }
            
            atLogDAO.insert(atLog);
        } catch (Exception e) {
        }
        
        if (result.isCode()) {
            log.info("send at command({}): {}, {}", cmd.name(), atLog.getReq_content(), atLog.getResp_content());
        } else {
            log.error("send at command({}): {}, {}", cmd.name(), atLog.getReq_content(), atLog.getResp_message());
        }
        
        return result;
    }

}
