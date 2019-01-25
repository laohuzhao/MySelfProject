package com.sdk4.biz.aote.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.sdk4.biz.aote.Config;
import com.sdk4.biz.aote.bean.SysRole;
import com.sdk4.biz.aote.bean.SysUser;
import com.sdk4.biz.aote.dao.SysRoleDAO;
import com.sdk4.biz.aote.dao.SysUserDAO;
import com.sdk4.biz.aote.service.UserService;
import com.sdk4.common.CallResult;
import com.sdk4.common.util.IdUtils;
import com.sdk4.common.util.PageUtils;
import com.sdk4.common.util.ValueUtils;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	SysRoleDAO sysRoleDAO;

	@Autowired
	SysUserDAO sysUserDAO;

	Config config = new Config();

	@Override
	public SysRole getRole(String role_id) {
		return sysRoleDAO.get(role_id);
	}

	@Override
	public List<SysRole> queryRole(String type) {
		if (StringUtils.isEmpty(type)) {
			type = null;
		}
		return sysRoleDAO.query(type);
	}

	@Override
	public int saveRole(SysRole role) {
		int result = 0;

		try {
			if (StringUtils.isEmpty(role.getId())) {
				role.setId(IdUtils.newStrId());
				result = sysRoleDAO.insert(role);
			} else {
				result = sysRoleDAO.update(role);
			}
		} catch (DuplicateKeyException e) {
			result = -1;
		} catch (Exception e) {
			result = -4;
		}

		return result;
	}

	@Override
	public CallResult<Void> deleteRole(String role_id) {
		CallResult<Void> result = new CallResult<Void>();
		
		int n = this.sysUserDAO.count(role_id, null, null);
		if (n > 0) {
			result.setCode(400);
			result.setMessage("该角色下有用户，不能进行删除");
		} else if ("SYS".equals(role_id) || "CUSTOM".equals(role_id)) {
			result.setCode(400);
			result.setMessage("内置角色不能删除");
		} else {
			n = sysRoleDAO.delete(role_id);
			if (n > 0) {
				result.setCode(0);
				result.setMessage("删除成功");
			} else {
				result.setCode(400);
				result.setMessage("删除失败");
			}
		}

		return result;
	}

	@Override
	public SysUser getUser(String id) {
		return sysUserDAO.get(id);
	}

	@Override
	public int countUser(String role_id, Integer custom, String custom_id) {
		role_id = ValueUtils.getString(role_id, null);
		custom_id = ValueUtils.getString(custom_id, null);

		return sysUserDAO.count(role_id, custom, custom_id);
	}

	@Override
	public List<SysUser> queryUser(String role_id, Integer custom, String custom_id, int page_index, int page_size) {
		role_id = ValueUtils.getString(role_id, null);
		custom_id = ValueUtils.getString(custom_id, null);

		return sysUserDAO.query(role_id, custom, custom_id, PageUtils.calcStart(page_index, page_size), page_size);
	}

	@Override
	public int saveUser(SysUser user) {
		int result = 0;

		try {
			if (StringUtils.isEmpty(user.getId())) {
				user.setId(IdUtils.newStrId());
				user.setAdd_time(new Date());
				user.setStatus(20);
				if (user.getSms_push() == null) {
					user.setSms_push(0);
				}
				result = sysUserDAO.insert(user);
			} else {
				if (StringUtils.isEmpty(user.getPassword())) {
					user.setPassword(null);
				}
				user.setAdd_time(null);
				if (user.getStatus() != null && user.getStatus() == 0) {
					user.setStatus(null);
				}
				result = sysUserDAO.update(user);
			}
		} catch (DuplicateKeyException e) {
			result = -1;
		} catch (Exception e) {
			e.printStackTrace();
			result = -4;
		}

		return result;
	}

	@Override
	public CallResult<Void> deleteUser(String id) {
		CallResult<Void> result = new CallResult<Void>();

		if (Config.super_manager_id.contains(id)) {
			result.setCode(400);
			result.setMessage("不能删除超级管理员");
		} else {
			int n = sysUserDAO.delete(id);
			if (n > 0) {
				result.setCode(0);
				result.setMessage("删除成功");
			} else {
				result.setCode(400);
				result.setMessage("删除失败");
			}
		}

		return result;
	}

	@Override
	public int changeSmsPushChange(String user_id, int sms_push) {
		SysUser su = new SysUser();

		su.setId(user_id);
		su.setSms_push(sms_push);

		return sysUserDAO.update(su);
	}

}
