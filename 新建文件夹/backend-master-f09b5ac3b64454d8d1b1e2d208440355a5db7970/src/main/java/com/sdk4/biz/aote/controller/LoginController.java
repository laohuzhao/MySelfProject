package com.sdk4.biz.aote.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.sdk4.biz.aote.bean.Custom;
import com.sdk4.biz.aote.bean.LoginUser;
import com.sdk4.biz.aote.bean.NotifyDaysTotal;
import com.sdk4.biz.aote.bean.SysRole;
import com.sdk4.biz.aote.bean.SysUser;
import com.sdk4.biz.aote.service.CustomService;
import com.sdk4.biz.aote.service.LoginService;
import com.sdk4.biz.aote.service.NotifyService;
import com.sdk4.biz.aote.service.UserService;


@RequestMapping("/user")
@Controller
public class LoginController {
    @Autowired
    LoginService loginService;
    
    @Autowired
    UserService userService;
    
    @Autowired
    CustomService customService;
    
    @Autowired
    NotifyService notifyService;
    
    @ResponseBody
    @RequestMapping(value = "login", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public String login(@RequestBody Map<String, String> reqMap, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<String, Object>();
        
        if (reqMap == null || !reqMap.containsKey("username") || !reqMap.containsKey("password")) {
            result.put("code", 40001);
            result.put("data", "登录请求参数错误");
        } else {
            String username = reqMap.get("username");
            String password = reqMap.get("password");
            
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            token.setRememberMe(true);
            Subject subject = SecurityUtils.getSubject();
            try {
                subject.login(token);
                if (subject.isAuthenticated()) {
                    result.put("code", 20000);
                    
                    Map<String, Object> data = new HashMap<String, Object>();
                    data.put("token", token.getCredentials().toString());
                    
                    result.put("data", data);
                } else {
                    result.put("code", 40003);
                    result.put("data", "用户名或密码错误");
                }
            } catch (IncorrectCredentialsException e) {
                result.put("code", 40003);
                result.put("data", "登录密码错误");
            } catch (ExcessiveAttemptsException e) {
                result.put("code", 40003);
                result.put("data", "登录失败次数过多");
            } catch (LockedAccountException e) {
                result.put("code", 40003);
                result.put("data", "帐号已被锁定");
            } catch (DisabledAccountException e) {
                result.put("code", 40003);
                result.put("data", "帐号已被禁用");
            } catch (ExpiredCredentialsException e) {
                result.put("code", 40003);
                result.put("data", "帐号已过期");
            } catch (UnknownAccountException e) {
                result.put("code", 40003);
                result.put("data", "帐号不存在");
            } catch (UnauthorizedException e) {
                result.put("code", 40003);
                result.put("data", "您没有得到相应的授权");
            } catch (AuthenticationException e) {
                result.put("code", 40003);
                result.put("data", "用户名或密码错误");
            } catch (Exception e) {
                result.put("code", 40003);
                result.put("data", "登录失败");
            }
        }
        
        return "" + JSON.toJSONString(result);
    }
    
    @ResponseBody
    @RequestMapping(value = "info", produces = "application/json;charset=utf-8")
    public String info(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<String, Object>();
        
        LoginUser loginUser = loginService.getLoginUser();
        
        if (loginUser != null && loginUser.isLogined()) {
            Map<String, Object> data = new HashMap<String, Object>();
            
            List<String> default_roles = new ArrayList<String>();
            default_roles.add("default");
            
            SysUser suser = userService.getUser(loginUser.getUser_id());
            data.put("name", suser.getName());
            
            String rolelist = "[]";
            if (loginUser.isSysUser()) {
                data.put("role_id_", suser.getRole_id_());
                data.put("corp_name", "总公司");
                
            } else if (loginUser.isCustom()) {
                Custom custom = customService.get(loginUser.getCustom_id());
                data.put("role_id_", suser.getRole_id_());
                data.put("corp_name", custom.getName());
            }
            
            if (StringUtils.isNotEmpty(suser.getRole_id())) {
                SysRole role = userService.getRole(suser.getRole_id());
                if (role != null && StringUtils.isNotEmpty(role.getAuth_list())) {
                    rolelist = role.getAuth_list();
                }
            }
            
            data.put("user_type", loginUser.getUser_type());
            data.put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
            
            // - 角色
            List<String> roles = new ArrayList<String>();
            JSONArray ja = JSON.parseArray(rolelist);
            for (int i = 0; i < ja.size(); i++) {
                String str = ja.getString(i);
                String[] arr = str.split("[:]");
                if (arr.length > 1) {
                    String m = arr[0];
                    if (!roles.contains(m)) {
                        roles.add(m);
                    }
                }
                roles.add(str);
            }
            
            // - 添加默认
            roles.addAll(default_roles);
            
            data.put("role", roles);
            
            // - 未读消息数
            String custom_id = null;
            if (loginUser != null && loginUser.isCustom()) {
                custom_id = loginUser.getCustom_id();
            }
            int unread = 0;
            List<NotifyDaysTotal> list = notifyService.totalDays(custom_id, null, 10);
            for (NotifyDaysTotal ndt : list) {
                unread += ndt.getUnread();
            }
            data.put("unReadMsg", unread);
            
            result.put("code", 20000);
            result.put("data", data);
        } else {
            result.put("code", 40000);
            result.put("data", "未登录，请重新登录");
        }
        
        return "" + JSON.toJSONString(result);
    }

    @ResponseBody
    @RequestMapping(value = "logout", produces = "application/json;charset=utf-8")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<String, Object>();
        
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            subject.logout();
        }
        result.put("code", 20000);
        result.put("data", "退出成功");
        
        return "" + JSON.toJSONString(result);
    }
}
