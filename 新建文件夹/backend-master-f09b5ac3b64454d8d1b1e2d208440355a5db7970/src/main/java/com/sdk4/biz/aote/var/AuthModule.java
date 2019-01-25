package com.sdk4.biz.aote.var;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

/**
 * 权限模块
 * 
 * @author CNJUN
 */
@SuppressWarnings("serial")
@Getter
public enum AuthModule {

    DEVICE("设备管理", new HashMap<String, List<String>>() {{
        // 查看
        put("device:list", new ArrayList<String>() {{
            add("/device/list");// 查询列表
        }});
        
        // 编辑
        put("device:edit", new ArrayList<String>() {{
            add("/device/save");// 新增保存
            add("/device/set_params");// 设置参数
            add("/device/alloc");// 分配
            add("/device/init");// 初始化
            add("/device/select_custom");// 选择客户
            add("/device/dict_list");// 获取字典表
            
            add("/device/setting");// 获取配置信息
            add("/device/setting_save");// 保存配置信息
        }});
        
        // 执行指令
        put("device:at", new ArrayList<String>() {{
            add("/device/at");// 指令执行
        }});
    }}),
    
    CUSTOM("客户管理", new HashMap<String, List<String>>() {
        {
            // 查看
            put("custom:list", new ArrayList<String>() {{
                add("/custom/list");// 客户列表
                add("/custom/save");// 新增客户保存
                add("/custom/change_status");// 设置状态
            }});
            
            // 编辑
            put("custom:edit", new ArrayList<String>() {{
                add("/custom/save");// 新增客户保存
                add("/custom/change_status");// 设置状态
            }});
        }
    }),
    
    AUTH("权限管理", new HashMap<String, List<String>>() {
        {
            // 查看
            put("auth:list", new ArrayList<String>() {{
                add("/auth/user_list");// 用户列表
                add("/auth/role_list");// 角色列表
            }});
            
            // 编辑
            put("auth:edit", new ArrayList<String>() {{
                add("/auth/user_save");// 新增用户保存
                add("/auth/user_delete");// 删除用户
                add("/auth/select_role");// 选择角色
                
                add("/auth/role_save");// 新增角色保存
                add("/auth/role_delete");// 删除角色                
            }});
        }
    }),
    
    DEFAULT("默认权限", new HashMap<String, List<String>>() {
        {
            // 通知管理
            // 账户设置
        }
    }),
    
    ;
    
    private String module;
    private Map<String, List<String>> funcUrls;
    
    AuthModule(String module, Map<String, List<String>> funcUrls) {
        this.module = module;
        this.funcUrls = funcUrls;
    }
}
