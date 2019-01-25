package com.sdk4.biz.aote.service.impl;

import java.util.HashSet;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sdk4.biz.aote.bean.LoginUser;
import com.sdk4.biz.aote.service.LoginService;
import com.sdk4.biz.aote.var.ClientType;

@Service
public class SystemAuthorizingRealm extends AuthorizingRealm {
    
    @Autowired
    LoginService loginService;

    // 授权信息：获取角色/权限信息用于授权验证
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String) principals.getPrimaryPrincipal();
        
        //System.out.println("授权>" + username);
        //System.out.println("授权>" + JSON.toJSONString(principals.getRealmNames()));
        
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.setRoles(new HashSet<String>(0));
        authorizationInfo.setStringPermissions(new HashSet<String>(0));
        
        return authorizationInfo;
    }
    
    // 身份验证：
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken uptoken = (UsernamePasswordToken) token;
        
        String username = uptoken.getUsername();
        String password = new String(uptoken.getPassword());
        
        LoginUser loginUser = loginService.login(username, password, ClientType.PC, "");
        
        if (loginUser.isLogined()) {
            return new SimpleAuthenticationInfo(username, password, loginUser.getToken());
        } else if (loginUser.getLogin_result() == 404) {
            throw new UnknownAccountException();
        } else if (loginUser.getLogin_result() == 401) {
            throw new DisabledAccountException();
        }
        
        throw new UnauthenticatedException();
    }

}
