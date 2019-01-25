package com.sdk4.biz.aote.service;

import java.util.List;

import com.sdk4.biz.aote.bean.SysRole;
import com.sdk4.biz.aote.bean.SysUser;
import com.sdk4.common.CallResult;

public interface UserService {
	SysRole getRole(String role_id);

	List<SysRole> queryRole(String type);

	int saveRole(SysRole role);

	CallResult<Void> deleteRole(String role_id);

	//
	SysUser getUser(String id);

	int countUser(String role_id, Integer custom, String custom_id);

	List<SysUser> queryUser(String role_id, Integer custom, String custom_id, int page_index, int page_size);

	int saveUser(SysUser user);

	CallResult<Void> deleteUser(String id);

	int changeSmsPushChange(String user_id, int sms_push);

}
