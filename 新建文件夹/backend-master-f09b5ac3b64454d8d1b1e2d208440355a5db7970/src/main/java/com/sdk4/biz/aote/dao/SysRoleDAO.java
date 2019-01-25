package com.sdk4.biz.aote.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.sdk4.biz.aote.bean.SysRole;

public interface SysRoleDAO {

	SysRole get(@Param("id") String id);

	List<SysRole> query(@Param("type") String type);

	int insert(SysRole role);

	int update(SysRole role);

	int delete(@Param("id") String role_id);

}
