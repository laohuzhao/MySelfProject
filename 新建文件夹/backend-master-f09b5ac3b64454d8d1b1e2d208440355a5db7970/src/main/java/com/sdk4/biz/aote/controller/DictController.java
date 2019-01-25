package com.sdk4.biz.aote.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sdk4.biz.aote.bean.Dict;
import com.sdk4.biz.aote.dao.DeviceDAO;
import com.sdk4.biz.aote.dao.DictDAO;
import com.sdk4.biz.aote.util.Tools;
import com.sdk4.common.util.IdUtils;

@RequestMapping("dict")
@Controller
public class DictController {
	@Autowired
	DeviceDAO deviceDAO;
	
	@Autowired
	DictDAO dictDAO;

	@ResponseBody
	@RequestMapping(value = "/list", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dict_list(@RequestBody Map<String, String> reqMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		String type = reqMap.get("type");
		String parent_code = reqMap.get("parent_code");
		String extra = reqMap.get("extra");
		
		List list = null;

		if ("dtype".equals(type)) {
			list = dictDAO.queryDeviceType();
		} else if ("manufacturer".equals(type)) {
			if (StringUtils.isEmpty(extra)) {
				extra = null;
			} else {
				extra = "%" + extra.trim() + "%";
			}
			list = dictDAO.queryManufacturer(extra);
		} else if ("custom".equals(type)) {
			list = dictDAO.queryCustom();
		} else if ("areacode".equals(type)) {
			if (StringUtils.isEmpty(parent_code)) {
				//parent_code = "";
			}
			list = dictDAO.queryAreaCode(parent_code);
		} else if ("areacode_select".equals(type)) {
			list = dictDAO.getAllCity();
		} else if ("areacode_tree".equals(type)) {
			list = Lists.newArrayList();
			
			List<Dict> root = dictDAO.queryAreaCode("");
			for (Dict dict : root) {
				Map<String, Object> item = Maps.newHashMap();
				item.put("id", dict.getId());
				item.put("code", dict.getCode());
				item.put("value", dict.getId());
				item.put("label", dict.getName());
				item.put("parent_id", dict.getParent_id());
				List<Map<String, Object>> children = Lists.newArrayList();
				List<Dict> chld_list = dictDAO.queryAreaCode(dict.getId());
				for (Dict chld : chld_list) {
					Map<String, Object> row = Maps.newHashMap();
					row.put("id", chld.getId());
					row.put("code", chld.getCode());
					row.put("value", chld.getId());
					row.put("label", chld.getName());
					item.put("parent_id", dict.getParent_id());
					children.add(row);
				}
				item.put("children", children);
				
				list.add(item);
			}
		} else {
			list = Lists.newArrayList();
		}

		data.put("total", list.size());
		data.put("items", list);

		result.put("code", 20000);
		result.put("data", data);

		return JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
	}

	@ResponseBody
	@RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dict_save(@RequestBody Map<String, String> reqMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Map<String, Object> result = new HashMap<String, Object>();

		String type = reqMap.get("type");
		String id = reqMap.get("id");
		String code = reqMap.get("code");
		String name = reqMap.get("name");
		String parent_id = reqMap.get("parent_id");
		String parent_code = reqMap.get("parent_code");
		String extra = reqMap.get("extra");
		
		String op = reqMap.get("op");
		
		// 测试代码范围
		boolean testhex4 = true;
		if ("areacode".equals(type)) {
			if (StringUtils.isNotEmpty(parent_id)) {
				if (!Tools.testHex4(code)) {
					testhex4 = false;
				}
			}
		} else if (!Tools.testHex4(code)) {
			testhex4 = false;
		}
		
		if (StringUtils.isEmpty(name)) {
			result.put("code", 40001);
			result.put("data", "名称不能为空");
		} else if (!testhex4) {
			result.put("code", 40001);
			result.put("data", "代码范围为0000~FFFF");
		} else {
			int n = 0;
			
			Dict dict = new Dict();
			if (StringUtils.isEmpty(id)) {
				id = IdUtils.newStrId();
			}
			dict.setId(id);
			dict.setCode(code);
			dict.setName(name);
			if (StringUtils.isEmpty(parent_id)) {
				parent_id = "";
			}
			dict.setParent_id(parent_id);
			if (StringUtils.isEmpty(parent_code)) {
				parent_code = "";
			}
			dict.setParent_code(parent_code);
			dict.setExtra(extra);
			
			try {
				if ("dtype".equals(type)) {
					if ("1".equals(op)) {
						n = dictDAO.insertDeviceType(dict);
					} else {
						n = dictDAO.updateDeviceType(dict);
					}
				} else if ("manufacturer".equals(type)) {
					if ("1".equals(op)) {
						n = dictDAO.insertManufacturer(dict);
					} else {
						n = dictDAO.updateManufacturer(dict);
					}
				} else if ("areacode".equals(type)) {
					if ("1".equals(op)) {
						n = dictDAO.insertAreaCode(dict);
					} else {
						dict.setParent_id(null);
						n = dictDAO.updateAreaCode(dict);
					}
				}
				
				if (n == 1) {
					result.put("code", 20000);
					result.put("data", "保存成功");
				} else {
					result.put("code", 40000);
					result.put("data", "保存失败");
				}
			} catch (DuplicateKeyException e) {
				result.put("code", 40000);
				result.put("data", "保存失败：代码重复");
			} catch (Exception e) {
				result.put("code", 40000);
				result.put("data", "保存失败");
			}
		}

		return JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
	}
	
	@ResponseBody
	@RequestMapping(value = "/delete", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dict_delete(@RequestBody Map<String, String> reqMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Map<String, Object> result = new HashMap<String, Object>();

		String type = reqMap.get("type");
		String code = reqMap.get("code");
		String id = reqMap.get("id");
		
		if (StringUtils.isEmpty(code) && StringUtils.isEmpty(id)) {
			result.put("code", 40001);
			result.put("data", "请先选择要删除的内容");
		} else {
			int n = 0;
			if ("dtype".equals(type)) {
				if (deviceDAO.count(null, null, null, null, code, null, null, null, null, null, null, null, null, null) > 0) {
					n = -1;
				} else {
					n = dictDAO.deleteDeviceType(code);
				}
			} else if ("manufacturer".equals(type)) {
				if (deviceDAO.count(null, null, null, null, null, code, null, null, null, null, null, null, null, null) > 0) {
					n = -1;
				} else {
					n = dictDAO.deleteManufacturer(code);
				}
			} else if ("areacode".equals(type)) {
				if (deviceDAO.count(null, null, null, null, null, null, id, null, null, null, null, null, null, null) > 0) {
					n = -1;
				} else {
					n = dictDAO.deleteAreaCode(id);
				}
			}
			
			if (n == -1) {
				result.put("code", 40000);
				result.put("data", "已经关联使用，不允许删除");
			} else if (n == 1) {
				result.put("code", 20000);
				result.put("data", "删除成功");
			} else {
				result.put("code", 40000);
				result.put("data", "删除失败");
			}
		}

		return JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
	}
}
