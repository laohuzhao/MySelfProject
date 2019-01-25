package com.sdk4.biz.aote.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.sdk4.biz.aote.bean.Custom;
import com.sdk4.biz.aote.bean.CustomType;
import com.sdk4.biz.aote.bean.SysUser;
import com.sdk4.biz.aote.dao.CustomDAO;
import com.sdk4.biz.aote.dao.CustomTypeDAO;
import com.sdk4.biz.aote.dao.SysUserDAO;
import com.sdk4.biz.aote.service.CustomService;
import com.sdk4.biz.aote.var.CustomStatus;
import com.sdk4.biz.aote.var.UserStatus;
import com.sdk4.biz.aote.var.UserType;
import com.sdk4.common.CallResult;
import com.sdk4.common.exception.DbException;
import com.sdk4.common.util.ExceptionUtils;
import com.sdk4.common.util.IdUtils;
import com.sdk4.common.util.PageUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomServiceImpl implements CustomService {

	@Autowired
	CustomDAO customDAO;

	@Autowired
	CustomTypeDAO customTypeDAO;

	@Autowired
	SysUserDAO sysUserDAO;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public CallResult<Custom> save(Custom custom, String custom_type_) throws DbException {
		CallResult<Custom> result = new CallResult<Custom>();

		// - 参数判断
		String pwd = null;
		String custom_user_id = null;
		boolean isnew = false;
		if (StringUtils.isEmpty(custom.getId())) {
			isnew = true;
			if (StringUtils.isEmpty(custom.getPassword())) {
				result.setCode(400);
				result.setMessage("账号密码不能为空");
			} else {
				custom.setId(IdUtils.newStrId());
				custom.setAdd_time(new Date());
			}
		} else {
			Custom custom_ = customDAO.get(custom.getId());
			if (custom_ != null) {
				custom_user_id = custom_.getUser_id();
			}
		}
		if (StringUtils.isEmpty(custom_user_id)) {
			custom_user_id = IdUtils.newStrId();
		}
		custom.setUser_id(custom_user_id);

		if (StringUtils.isNotEmpty(custom.getPassword())) {
			pwd = new Md5Hash(custom.getPassword()).toHex();
		}
		custom.setPassword(pwd);
		if (custom.getStatus() == null || custom.getStatus() == 0) {
			custom.setStatus(CustomStatus.NORMAL.getCode());
		}
		if (custom.getSms_push() == null) {
			custom.setSms_push(0);
		}
		if (custom.getDevice_count() == null) {
			custom.setDevice_count(0);
		}
		// 自动生成编号
		if (StringUtils.isEmpty(custom.getCode())) {
			String maxcode = customDAO.getMaxCode();
			if (StringUtils.isEmpty(maxcode)) {
				maxcode = "0001";
			} else {
				long l = Long.parseLong(maxcode, 16);
				maxcode = String.format("%04x", ++l).toUpperCase();
			}
			custom.setCode(maxcode);
		}
		// 客户信息
		if (result.success()) {
			int n;
			try {
				if (isnew) {
					n = customDAO.insert(custom);
				} else {
					n = customDAO.update(custom);
				}
				if (n != 1) {
					result.setCode(400);
					result.setMessage("客户信息保存失败");
				}
			} catch (DuplicateKeyException e) {
				throw new DbException("客户名称/手机号码/登录账号已经存在");
			} catch (Exception e) {
				result.setCode(400);
				result.setMessage("客户信息保存错误");

				log.error("客户信息保存错误:{}:{}", JSON.toJSONString(custom), ExceptionUtils.toString(e));
			}
		}
		// 客户超级管理员登录账号
		if (result.success()) {
			isnew = false;
			String user_id = custom.getUser_id();
			SysUser su = null;
			if (StringUtils.isNotEmpty(user_id)) {
				su = this.sysUserDAO.get(user_id);
			} else {
				user_id = IdUtils.newStrId();
			}
			if (su == null) {
				isnew = true;
				su = new SysUser();
				su.setId(user_id);
			}
			su.setName(custom.getAccount());
			su.setMobile(custom.getMobile());
			su.setPassword(custom.getPassword());
			su.setAdd_time(new Date());
			su.setRole_id(UserType.CUSTOM.name());
			su.setSms_push(0);
			su.setStatus(UserStatus.NORMAL.getCode());
			su.setCustom(UserType.CUSTOM.getCode());
			su.setCustom_id(custom.getId());

			int n = 0;
			try {
				if (isnew) {
					n = this.sysUserDAO.insert(su);
				} else {
					n = this.sysUserDAO.update(su);
				}
			} catch (DuplicateKeyException e) {
				throw new DbException("客户账号手机号码或账号已经存在");
			} catch (Exception e) {
				result.setCode(400);
				result.setMessage("客户账号信息保存错误");

				log.error("客户账号信息保存错误:{}:{}", JSON.toJSONString(custom), ExceptionUtils.toString(e));
			}
			if (n != 1) {
				result.setCode(400);
				result.setMessage("客户账号信息保存失败");
			}
		}
		// 客户类型子表
		if (result.success()) {
			if (StringUtils.isNotEmpty(custom_type_)) {
				this.customTypeDAO.deleteAll(custom.getId());
				String[] arr = custom_type_.split("[,]");
				for (String a : arr) {
					CustomType custom_type = new CustomType();
					custom_type.setCustom_id(custom.getId());
					custom_type.setType_code(a);
					this.customTypeDAO.insert(custom_type);
				}
			}
			result.setMessage("保存成功");
		}

		return result;
	}

	@Override
	public int changeStatus(String id, String type, int status) {
		int result = 0;

		Custom custom = this.customDAO.get(id);

		if (custom != null) {
			if ("status".equals(type)) {
				status = status == CustomStatus.NORMAL.getCode() ? status : CustomStatus.STOP.getCode();
				custom.setStatus(status);
			} else if ("sms".equals(type)) {
				int sms_alarm = status == 0 ? status : 1;
				custom.setSms_alarm(sms_alarm);
			}
			result = customDAO.update(custom);
		}

		return result;
	}

	@Override
	public Custom get(String id) {
		Custom custom = customDAO.get(id);
		if (custom != null) {
			custom.setCustom_type(this.customTypeDAO.query(id));
		}
		return custom;
	}

	@Override
	public Custom getByMobile(String mobile) {
		Custom custom = customDAO.getByMobile(mobile);
		if (custom != null) {
			custom.setCustom_type(this.customTypeDAO.query(custom.getId()));
		}
		return custom;
	}

	@Override
	public Custom getByAccount(String account) {
		Custom custom = customDAO.getByAccount(account);
		if (custom != null) {
			custom.setCustom_type(this.customTypeDAO.query(custom.getId()));
		}
		return custom;
	}

	@Override
	public int count(String keyword) {
		if (StringUtils.isEmpty(keyword)) {
			keyword = null;
		} else {
			keyword = "%" + keyword.trim() + "%";
		}
		return customDAO.count(keyword);
	}

	@Override
	public List<Custom> query(String keyword, int page_index, int page_size) {
		if (StringUtils.isEmpty(keyword)) {
			keyword = null;
		} else {
			keyword = "%" + keyword.trim() + "%";
		}
		List<Custom> list = customDAO.query(keyword, PageUtils.calcStart(page_index, page_size), page_size);
		for (Custom custom : list) {
			if (custom != null) {
				custom.setCustom_type(this.customTypeDAO.query(custom.getId()));
			}
		}
		return list;
	}

	@Override
	public List<Custom> search(String keyword) {
		List<Custom> result;

		if (StringUtils.isEmpty(keyword)) {
			result = new ArrayList<Custom>(0);
		} else {
			keyword = "%" + keyword.trim() + "%";
			result = customDAO.search(keyword);
			for (Custom custom : result) {
				if (custom != null) {
					custom.setCustom_type(this.customTypeDAO.query(custom.getId()));
				}
			}
		}
		return result;
	}

	@Override
	public int changeSmsPushChange(String custom_id, int sms_push) {
		int result = 0;

		Custom custom = this.customDAO.get(custom_id);

		if (custom != null) {
			custom.setSms_push(sms_push);
			result = customDAO.update(custom);
		}

		return result;
	}

}
