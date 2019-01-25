package com.sdk4.biz.aote.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.sdk4.biz.aote.bean.SysUser;

public interface SysUserDAO {

	SysUser get(@Param("id") String id);

	SysUser getByName(@Param("name") String name);

	SysUser getByMobileOrName(@Param("username_or_mobile") String username_or_mobile);

	int count(@Param("role_id") String role_id, @Param("custom") Integer custom, @Param("custom_id") String custom_id);

	List<SysUser> query(@Param("role_id") String role_id, @Param("custom") Integer custom,
			@Param("custom_id") String custom_id, @Param("start") Integer start, @Param("size") int size);

	int insert(SysUser user);

	int update(SysUser user);

	int delete(@Param("id") String role_id);
}
