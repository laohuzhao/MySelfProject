package com.sdk4.biz.aote.service;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.sdk4.biz.aote.var.AtCmd;

import lombok.Getter;
import lombok.Setter;

public interface AtService {

    Result exec(AtCmd cmd, String phoneNumber, String generateID, Map<String, String> params);
    
    @Setter
    @Getter
    public static class Result {
        private boolean code;
        private String message;
        private String phone;
        private String generateID;
        private JSONObject data;
    }

}
