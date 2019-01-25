package com.sdk4.biz.aote.service.impl;

import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sdk4.biz.aote.bean.Custom;
import com.sdk4.biz.aote.bean.LoginUser;
import com.sdk4.biz.aote.bean.SysUser;
import com.sdk4.biz.aote.dao.CustomDAO;
import com.sdk4.biz.aote.dao.SysTokenDAO;
import com.sdk4.biz.aote.dao.SysUserDAO;
import com.sdk4.biz.aote.service.CustomService;
import com.sdk4.biz.aote.service.LoginService;
import com.sdk4.biz.aote.service.UserService;
import com.sdk4.biz.aote.var.ClientType;
import com.sdk4.biz.aote.var.UserStatus;
import com.sdk4.biz.aote.var.UserType;
import com.sdk4.common.util.IdUtils;

@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	UserService userService;

	@Autowired
	CustomService customService;

	@Autowired
	SysUserDAO sysUserDAO;

	@Autowired
	CustomDAO customDAO;

	@Autowired
	SysTokenDAO sysTokenDAO;

	@Override
	public LoginUser login(String username, String password, ClientType client_type, String ua) {
		LoginUser result = new LoginUser();

		String pwd = new Md5Hash(password).toHex();

		SysUser suser = sysUserDAO.getByName(username);
		if (suser != null) {
			boolean custom_stop = false;
			if (StringUtils.isNotEmpty(suser.getCustom_id())) {
				Custom custom = customService.get(suser.getCustom_id());
				if (custom == null || custom.getStatus() != 20) {
					custom_stop = true;
				}
			}
			if (StringUtils.isEmpty(suser.getPassword())) {
				// 未设置登录密码
				result.setLogin_result(500);
			} else if (suser.getStatus() != UserStatus.NORMAL.getCode() || custom_stop) {
				result.setLogin_result(401);
			} else {
				if (StringUtils.equals(pwd, suser.getPassword())) {
					result.setLogin_result(200);
					result.setToken("");
					result.setClient_type(client_type.name());
					result.setClient_key("");
					
					result = this.toLoginUser(result, suser);
				} else {
					result.setLogin_result(501);
				}
			}
		}

		// 发放 TOKEN
		if (result.isLogined()) {
			String token = IdUtils.newStrId();
			result.setToken(token);

			sysTokenDAO.deleteByUserId(result.getUser_id());
			sysTokenDAO.insert(result);

			String token_ = Base64.encodeBase64String(token.getBytes());
			result.setToken(token_);
		}

		return result;
	}

	@Override
	public LoginUser getByToken(String token) {
		LoginUser loginUser = sysTokenDAO.getByToken(token);
		if (loginUser != null) {
			loginUser.setLogin_result(200);
			
			loginUser = this.toLoginUser(loginUser, null);
		} else {
			loginUser = new LoginUser();
			loginUser.setLogin_result(404);
		}
		return loginUser;
	}

	@Override
	public LoginUser getLoginUser() {
		LoginUser loginUser = null;

		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated()) {
			PrincipalCollection pc = subject.getPrincipals();
			if (pc != null && pc.getRealmNames().size() > 0) {
				String token = pc.getRealmNames().iterator().next();
				String token_ = new String(Base64.decodeBase64(token));
				loginUser = getByToken(token_);
			}
		}

		return loginUser;
	}
	
	private LoginUser toLoginUser(LoginUser loginUser, SysUser suser) {
		if (loginUser == null) {
			loginUser = new LoginUser();
		}
		
		if (suser == null) {
			suser = this.sysUserDAO.get(loginUser.getUser_id());
		}
		
		loginUser.setUser_id(suser.getId());
		loginUser.setLogin_time(new Date());
		loginUser.setCustom_id(suser.getCustom_id());
		
		if (suser.getCustom() == 0 && StringUtils.isEmpty(suser.getCustom_id())) {
			loginUser.setUser_type(UserType.SYS);
		} else if (suser.getCustom() == 1 && StringUtils.isNotEmpty(suser.getCustom_id())) {
			loginUser.setUser_type(UserType.CUSTOM);
		} else if (suser.getCustom() == 2 && StringUtils.isNotEmpty(suser.getCustom_id())) {
			loginUser.setUser_type(UserType.CUSTOM_USER);
		} else {
			loginUser.setLogin_result(401);
		}
		
		return loginUser;
	}

}
