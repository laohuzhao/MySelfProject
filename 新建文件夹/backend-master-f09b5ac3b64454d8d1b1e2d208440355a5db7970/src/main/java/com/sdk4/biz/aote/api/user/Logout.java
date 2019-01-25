package com.sdk4.biz.aote.api.user;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.sdk4.biz.aote.api.ApiResponse;
import com.sdk4.biz.aote.api.ApiService;
import com.sdk4.biz.aote.bean.LoginUser;
import com.sdk4.biz.aote.dao.SysTokenDAO;

@Service
public class Logout implements ApiService {
    public final static String METHOD = "user.logout";

    @Autowired
    SysTokenDAO sysTokenDAO;

    @Override
    public String method() {
        return METHOD;
    }

    @Override
    public void service(JSONObject params, ApiResponse response, LoginUser loginUser) {
        if (loginUser != null && StringUtils.isNotEmpty(loginUser.getUser_id())) {
            sysTokenDAO.deleteByUserId(loginUser.getUser_id());
        }

        response.setMessage("退出成功");
    }
}
