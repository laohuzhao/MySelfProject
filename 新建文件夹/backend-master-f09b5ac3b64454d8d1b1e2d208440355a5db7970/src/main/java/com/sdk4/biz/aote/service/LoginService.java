package com.sdk4.biz.aote.service;

import com.sdk4.biz.aote.bean.LoginUser;
import com.sdk4.biz.aote.var.ClientType;

public interface LoginService {

    LoginUser login(String username, String password, ClientType client_type, String ua);

    LoginUser getByToken(String token);

    LoginUser getLoginUser();

}
