package com.sdk4.biz.aote.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvReader;
import com.google.common.collect.Lists;
import com.sdk4.biz.aote.bean.Custom;
import com.sdk4.biz.aote.bean.Device;
import com.sdk4.biz.aote.bean.HomeDashboard;
import com.sdk4.biz.aote.bean.LoginUser;
import com.sdk4.biz.aote.service.AtService;
import com.sdk4.biz.aote.service.CustomService;
import com.sdk4.biz.aote.service.DeviceService;
import com.sdk4.biz.aote.service.LoginService;
import com.sdk4.biz.aote.service.impl.BizConfig;
import com.sdk4.biz.aote.service.impl.BizConfig.KEY;
import com.sdk4.biz.aote.util.Tools;
import com.sdk4.biz.aote.var.AtCmd;
import com.sdk4.biz.aote.var.DeviceStatus;
import com.sdk4.common.CallResult;
import com.sdk4.common.util.CSVUtils;
import com.sdk4.common.util.ExcelUtils;
import com.sdk4.common.util.IdUtils;
import com.sdk4.common.util.PageUtils;

/**
 * 设备管理
 * 
 * @author CNJUN
 */
@RequestMapping("device")
@Controller
public class DeviceController {
	@Autowired
	LoginService loginService;

	@Autowired
	DeviceService deviceService;

	@Autowired
	CustomService customService;

	@Autowired
	BizConfig bizConfig;

	SimpleDateFormat sdf_datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@ResponseBody
	@RequestMapping(value = "/dashboard", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String dashboard(@RequestBody Map<String, String> reqMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		String custom_id = null;
		LoginUser loginUser = loginService.getLoginUser();
		if (loginUser != null && loginUser.isCustom()) {
			custom_id = loginUser.getCustom_id();
		}

		HomeDashboard hd = deviceService.dashboard(custom_id);

		data.put("fuel_level_fault", hd.getFuel_level_fault());
		data.put("fuel_pressure_fault", hd.getFuel_pressure_fault());
		data.put("shutting", hd.getShutting());
		data.put("working", hd.getWorking());
		data.put("online", hd.getOnline());
		data.put("offline", hd.getOffline());

		result.put("code", 20000);
		result.put("data", data);

		return JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
	}

	@ResponseBody
	@RequestMapping(value = "/list", produces = "application/json;charset=utf-8")
	public String list(@RequestBody Map<String, String> reqMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		String page_ = reqMap.get("page");
		String limit_ = reqMap.get("limit");

		int page = 1, limit = 10;
		if (StringUtils.isNotEmpty(page_)) {
			page = Integer.parseInt(page_);
		}
		if (StringUtils.isNotEmpty(limit_)) {
			limit = Integer.parseInt(limit_);
		}

		String device_id = reqMap.get("device_id");
		String corp_name = reqMap.get("corp_name");
		String device_type = reqMap.get("device_type");
		String area_code = reqMap.get("area_code");
		String working_state = reqMap.get("working_state");
		String processing_state = reqMap.get("processing_state");
		String fuel_level_state = reqMap.get("fuel_level_state");
		String fuel_pressure_fault = reqMap.get("fuel_pressure_fault");
		String status_ = reqMap.get("status");
		String tag = reqMap.get("tag");

		String custom_id = null;
		List<Integer> status = null;
		LoginUser loginUser = loginService.getLoginUser();
		if (loginUser != null && loginUser.isCustom()) {
			custom_id = loginUser.getCustom_id();
			status = Lists.newArrayList();
			if ("20".equals(status_)) {
				status.add(DeviceStatus.ONLINE.getCode());
			} else if ("30".equals(status_)) {
				status.add(DeviceStatus.OFFLINE.getCode());
			} else {
				status.add(DeviceStatus.ONLINE.getCode());
				status.add(DeviceStatus.OFFLINE.getCode());
			}
		} else if (StringUtils.isNotEmpty(status_)) {
			status = Lists.newArrayList();
			if ("10".equals(status_)) {
				status.add(DeviceStatus.UNINIT.getCode());
			} else if ("20".equals(status_)) {
				status.add(DeviceStatus.ONLINE.getCode());
			} else if ("30".equals(status_)) {
				status.add(DeviceStatus.OFFLINE.getCode());
			}
		}

		int total = deviceService.count(custom_id, null, device_id, corp_name, device_type, null, area_code,
				working_state, processing_state, fuel_level_state, fuel_pressure_fault, status, tag);
		data.put("total", total);

		int page_count = PageUtils.pageCount(total, limit);
		if (page > page_count) {
			page = page_count;
		}

		List<Device> list = deviceService.query(custom_id, null, device_id, corp_name, device_type, null, area_code,
				working_state, processing_state, fuel_level_state, fuel_pressure_fault, status, tag, page, limit);

		for (Device d : list) {
			if (StringUtils.isEmpty(d.getDevice_type_())) {
				d.setDevice_type_(d.getDevice_type());
			}
			if (StringUtils.isEmpty(d.getManufacturer_())) {
				d.setManufacturer_(d.getManufacturer());
			}
			if (StringUtils.isEmpty(d.getCustom_id_())) {
				d.setCustom_id_(d.getDevice_user());
			}
			if (StringUtils.isEmpty(d.getArea_code_())) {
				d.setArea_code_(d.getArea_code());
			}
			if (StringUtils.isEmpty(d.getArea_id())) {
				d.setArea_id(d.getArea_code());
			}
		}

		data.put("items", list);
		data.put("refresh_seconds", bizConfig.getInt(KEY.DEVICE_LIST_REFRESH));

		result.put("code", 20000);
		result.put("data", data);

		return JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
	}

	@ResponseBody
	@RequestMapping(value = "/down")
	public String down(HttpServletRequest request, HttpServletResponse response, Model model) {
		String strIDS = request.getParameter("ids");

		try {
			List<Device> list;
			if (StringUtils.isNotEmpty(strIDS)) {
				if ("ALL".equals(strIDS)) {
					String custom_id = null;
					LoginUser loginUser = loginService.getLoginUser();
					if (loginUser != null && loginUser.isCustom()) {
						custom_id = loginUser.getCustom_id();
					}
					list = deviceService.query(custom_id, null, null, null, null, null, null, null, null, null, null,
							null, null, null, 0);
				} else {
					String[] arr = strIDS.split("[,]");
					List<String> idList = Arrays.asList(arr);
					list = deviceService.query(idList);
				}
			} else {
				list = new ArrayList<Device>(0);
			}
			InputStream is_config = getClass().getResourceAsStream("/export_device_list.json");
			String config_str = IOUtils.toString(is_config, "UTF-8");
			JSONObject jo_config = JSON.parseObject(config_str);
			JSONArray data_out = new JSONArray();
			for (Device device : list) {
				JSONObject row = new JSONObject();
				row.put("device_id", device.getDevice_id());
				row.put("phone_number", device.getPhone_number());
				row.put("custom_id_", device.getCustom_id_());
				if (StringUtils.isEmpty(device.getDevice_type_())) {
					row.put("device_type_", device.getDevice_type());
				} else {
					row.put("device_type_", device.getDevice_type_());
				}
				if (StringUtils.isEmpty(device.getArea_code_())) {
					row.put("area_code", device.getArea_code());
				} else {
					row.put("area_code_", device.getArea_code_());
				}
				row.put("local_address", device.getLocal_address());
				if (StringUtils.isEmpty(device.getManufacturer_())) {
					row.put("manufacturer_", device.getManufacturer());
				} else {
					row.put("manufacturer_", device.getManufacturer_());
				}
				row.put("tag", device.getTag());
				row.put("temperature", device.getTemperature());
				if (device.getWorking_state() == null) {
					row.put("working_state", "未知");
				} else {
					switch (device.getWorking_state()) {// 工作状态：1-休止；2-正常；3-油箱故障；4-低温保护；5-设置参数
					case 1:
						row.put("working_state", "休止");
						break;
					case 2:
						row.put("working_state", "正常");
						break;
					case 3:
						row.put("working_state", "油箱故障");
						break;
					case 4:
						row.put("working_state", "低温保护");
						break;
					case 5:
						row.put("working_state", "设置参数");
						break;
					default:
						row.put("working_state", "未知-" + device.getWorking_state());
						break;
					}
				}
				if (device.getProcessing_state() == null) {
					row.put("processing_state", "未知");
				} else {
					switch (device.getProcessing_state()) {// 运行状态：1-休止；2-润滑状态；4-低温保护；5-状态设置
					case 1:
						row.put("processing_state", "休止参数");
						break;
					case 2:
						row.put("processing_state", "润滑状态");
						break;
					case 4:
						row.put("processing_state", "低温保护");
						break;
					case 5:
						row.put("processing_state", "状态设置");
						break;
					default:
						row.put("processing_state", "未知-" + device.getProcessing_state());
						break;
					}
				}
				if (device.getFuel_level_state() == null) {
					row.put("fuel_level_state", "未知");
				} else {
					switch (device.getFuel_level_state()) {// 油位状态：0-正常液位；1-预警液位；2-低液位
					case 0:
						row.put("fuel_level_state", "正常液位");
						break;
					case 1:
						row.put("fuel_level_state", "预警液位");
						break;
					case 2:
						row.put("fuel_level_state", "低液位");
						break;
					default:
						row.put("fuel_level_state", "未知-" + device.getFuel_level_state());
						break;
					}
				}
				if (device.getFuel_pressure_state() == null) {
					row.put("fuel_pressure_state", "未知");
				} else {
					switch (device.getFuel_pressure_state()) {// 油压状态：0-正常；1-报警；2-润滑到压；3-润滑不到压
					case 0:
						row.put("fuel_pressure_state", "正常");
						break;
					case 1:
						row.put("fuel_pressure_state", "报警");
						break;
					case 2:
						row.put("fuel_pressure_state", "润滑到压");
						break;
					case 3:
						row.put("fuel_pressure_state", "润滑不到压");
						break;
					default:
						row.put("fuel_pressure_state", "未知-" + device.getFuel_pressure_state());
						break;
					}
				}
				row.put("count_num", device.getCount_num());
				row.put("low_count_num", device.getLow_count_num());
				row.put("loop_time", device.getLoop_time());
				row.put("stop_time", device.getStop_time());
				row.put("run_time", device.getRun_time());
				if (device.getPressure_state() == null || device.getPressure_state() == 0) {
					row.put("pressure_state", "不到压");
				} else {
					row.put("pressure_state", "到压");
				}
				row.put("pressure_time", device.getPressure_time());
				row.put("old_pressure_time", device.getOld_pressure_time());
				row.put("pressure_minute", device.getPressure_minute());
				row.put("pressure_second", device.getPressure_second());
				row.put("longitude_degree", device.getLongitude_degree());
				row.put("longitude_minute", device.getLongitude_minute());
				row.put("longitude_second", device.getLongitude_second());
				row.put("latitude_degree", device.getLatitude_degree());
				row.put("latitude_minute", device.getLatitude_minute());
				row.put("latitude_second", device.getLatitude_second());
				if (device.getLatest_time() == null) {
					row.put("latest_time", "");
				} else {
					row.put("latest_time", sdf_datetime.format(device.getLatest_time()));
				}
				row.put("p1", device.getP1());
				row.put("p2", device.getP2());
				row.put("p3", device.getP3());
				row.put("p4", device.getP4());
				row.put("version", device.getVersion());
				row.put("upload_period", device.getUpload_period());
				row.put("custom_id", device.getCustom_id());
				// row.put("custom_code", device.getCustom_code());

				data_out.add(row);
			}
			JSONArray out_list = new JSONArray();
			out_list.add(data_out);
			byte[] buff = ExcelUtils.exportExcelToBytes(jo_config, out_list);

			response.setContentType("application/msexcel");
			response.setHeader("Content-Disposition", "attachment;filename=" + IdUtils.newStrId() + ".xls");
			OutputStream out = response.getOutputStream();
			out.write(buff);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@ResponseBody
	@RequestMapping(value = "/map", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String map(@RequestBody Map<String, String> reqMap, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		String page_ = reqMap.get("page");
		String limit_ = reqMap.get("limit");

		int page = 1, limit = 10;
		if (StringUtils.isNotEmpty(page_)) {
			page = Integer.parseInt(page_);
		}
		if (StringUtils.isNotEmpty(limit_)) {
			limit = Integer.parseInt(limit_);
		}

		String device_id = reqMap.get("device_id");
		String corp_name = reqMap.get("corp_name");
		String state_x = reqMap.get("state_x");

		String custom_id = null;
		List<Integer> status = null;
		LoginUser loginUser = loginService.getLoginUser();
		if (loginUser != null && loginUser.isCustom()) {
			custom_id = loginUser.getCustom_id();
			status = Lists.newArrayList();
			status.add(DeviceStatus.ONLINE.getCode());
			status.add(DeviceStatus.OFFLINE.getCode());
		}

		String device_type = reqMap.get("device_type");
		String area_code = reqMap.get("area_code");
		String tag = reqMap.get("tag");
		String working_state = null; // 工作状态：1-休止；2-正常；3-油箱故障；4-低温保护；5-设置参数
		String processing_state = null; // 运行状态：1-休止；2-润滑状态；4-低温保护；5-状态设置
		String fuel_level_state = null; // 油位状态：0-正常液位；1-预警液位；2-低液位
		String fuel_pressure_state = null; // 油压状态：0-正常；1-报警；2-润滑到压；3-润滑不到压

		if (StringUtils.isNotEmpty(state_x)) {
			switch (Integer.parseInt(state_x)) {
			case 1: // 油位故障
				fuel_level_state = "1";
				break;
			case 2: // 油压故障
				fuel_pressure_state = "1";
				break;
			case 3: // 设备休止
				working_state = "1";
				break;
			case 4: // 正在润滑
				processing_state = "2";
				break;
			case 5: // 设备在线
				if (status == null) {
					status = Lists.newArrayList();
				}
				status.clear();
				status.add(DeviceStatus.ONLINE.getCode());
				break;
			case 6: // 设备离线
				if (status == null) {
					status = Lists.newArrayList();
				}
				status.clear();
				status.add(DeviceStatus.OFFLINE.getCode());
				break;
			}
		}

		int total = deviceService.count(custom_id, null, device_id, corp_name, device_type, null, area_code,
				working_state, processing_state, fuel_level_state, fuel_pressure_state, status, tag);
		data.put("total", total);

		int page_count = PageUtils.pageCount(total, limit);
		if (page > page_count) {
			page = page_count;
		}

		// 加载所有设备
		List<Device> list = deviceService.query(custom_id, null, device_id, corp_name, device_type, null, area_code,
				working_state, processing_state, fuel_level_state, fuel_pressure_state, status, tag, null, limit);

		for (Device d : list) {
			if (StringUtils.isEmpty(d.getDevice_type_())) {
				d.setDevice_type_(d.getDevice_type());
			}

			if (StringUtils.isEmpty(d.getManufacturer_())) {
				d.setManufacturer_(d.getManufacturer());
			}
			if (StringUtils.isEmpty(d.getCustom_id_())) {
				d.setCustom_id_(d.getDevice_user());
			}
			if (StringUtils.isEmpty(d.getArea_code_())) {
				d.setArea_code_(d.getArea_code());
			}
			if (StringUtils.isEmpty(d.getArea_id())) {
				d.setArea_id(d.getArea_code());
			}
		}

		data.put("items", list);

		result.put("code", 20000);
		result.put("data", data);

		return JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
	}

	@ResponseBody
	@RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String save(@RequestBody Map<String, String> reqMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Map<String, Object> result = new HashMap<String, Object>();

		String device_id = reqMap.get("device_id");
		String phone_number = reqMap.get("phone_number");
		String custom_id = reqMap.get("custom_id");
		String tag = reqMap.get("tag");
		
		if (StringUtils.isEmpty(phone_number)) {
			result.put("code", 40001);
			result.put("data", "设备手机号不能为空");
		} else {
			int n;
			
			if (StringUtils.isEmpty(device_id)) {
				Device device = new Device();
				device.setPhone_number(phone_number);
				device.setCustom_id(custom_id);
				device.setTag(tag);
				
				n = deviceService.add(device);
			} else {
				n = deviceService.updateTag(device_id, tag);
			}
			
			if (n == 1) {
				result.put("code", 20000);
				result.put("data", "保存成功");
			} else if (n == -1) {
				result.put("code", 40000);
				result.put("data", "设备编号或设备手机号已经存在");
			} else {
				result.put("code", 40000);
				result.put("data", "保存失败");
			}
		}

		return JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
	}

	@ResponseBody
	@RequestMapping(value = "/select_custom", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String select_custom(@RequestBody Map<String, String> reqMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		String keyword = reqMap.get("keyword");

		List<Custom> list = customService.search(keyword);

		data.put("items", list);

		result.put("code", 20000);
		result.put("data", data);

		return JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
	}

	@ResponseBody
	@RequestMapping(value = "/set_params", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String set_params(@RequestBody Map<String, String> reqMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Map<String, Object> result = new HashMap<String, Object>();

		String id = reqMap.get("id");
		Device device = deviceService.getByDeviceId(id);
		if (StringUtils.isEmpty(id)) {
			result.put("code", 40001);
			result.put("data", "未选择要修改的设备");
		} else if (device == null) {
			result.put("code", 40000);
			result.put("data", "设备不存在:" + id);
		} else if (device.getStatus() == DeviceStatus.UNINIT.getCode()) {
			result.put("code", 40000);
			result.put("data", "设备未初始化:" + id);
		} else {
			int n = deviceService.updateParams(id, reqMap);
			if (n == 1) {
				result.put("code", 20000);
				result.put("data", "修改成功");
			} else {
				result.put("code", 40001);
				result.put("data", "修改失败");
			}
		}

		return JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
	}

	@ResponseBody
	@RequestMapping(value = "/alloc", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String alloc(@RequestBody Map<String, String> reqMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Map<String, Object> result = new HashMap<String, Object>();

		String id = reqMap.get("id");
		String alloc_type = reqMap.get("alloc_type");
		String custom_id = reqMap.get("custom_id");
		String reset_data = reqMap.get("reset_data");

		boolean isok = true, change_custom = false;

		if ("cancel".equals(alloc_type)) {
			change_custom = true;
			custom_id = null;
		} else if ("to".equals(alloc_type)) {
			if (StringUtils.isEmpty(custom_id)) {
				result.put("code", 40001);
				result.put("data", "未选择要分配的客户");
			} else {
				change_custom = true;
			}
		} else {
			result.put("code", 40001);
			result.put("data", "请选择操作类型");
		}

		if (StringUtils.isEmpty(id)) {
			result.put("code", 40001);
			result.put("data", "未选择要分配的设备");
		} else if (change_custom) {
			CallResult<Device> callr = deviceService.alloc(id, custom_id);
			if (callr.success()) {
				result.put("code", 20000);
			} else {
				result.put("code", 40000);
			}
			result.put("data", callr.getMessage());
		}

		return JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
	}

	@ResponseBody
	@RequestMapping(value = "/init", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String init(@RequestBody Map<String, String> reqMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Map<String, Object> result = new HashMap<String, Object>();

		String id = reqMap.get("id");
		if (StringUtils.isEmpty(id)) {
			result.put("code", 40001);
			result.put("data", "未选择要初始化的设备");
		} else {
			if (StringUtils.isEmpty(reqMap.get("device_type"))) {
				result.put("code", 40000);
				result.put("data", "设备类型不能为空");
			} else if (StringUtils.isEmpty(reqMap.get("device_user"))) {
				result.put("code", 40000);
				result.put("data", "用户编码不能为空");
			} else if (StringUtils.isEmpty(reqMap.get("area_code"))) {
				result.put("code", 40000);
				result.put("data", "地域不能为空");
			} else if (StringUtils.isEmpty(reqMap.get("local_address"))) {
				result.put("code", 40000);
				result.put("data", "设备流水号不能为空");
			} else if (!Tools.testHex4(reqMap.get("local_address"))) {
				result.put("code", 40000);
				result.put("data", "设备流水号范围应为0000~FFFF");
			} else {
				int n = deviceService.init(id, reqMap);
				if (n == 1) {
					result.put("code", 20000);
					result.put("data", "初始化成功");
				} else if (n == -1) {
					result.put("code", 40000);
					result.put("data", "生成ID已经存在，不能进行初始化");
				} else {
					result.put("code", 40000);
					result.put("data", "初始化失败");
				}
			}
		}

		return JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
	}

	// 重新设置ID
	@ResponseBody
	@RequestMapping(value = "/setid", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String setid(@RequestBody Map<String, String> reqMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Map<String, Object> result = new HashMap<String, Object>();

		String id = reqMap.get("id");
		if (StringUtils.isEmpty(id)) {
			result.put("code", 40001);
			result.put("data", "未选择要初始化的设备");
		} else {
			String device_type = reqMap.get("device_type");
			String device_user = reqMap.get("device_user");
			String manufacturer = reqMap.get("manufacturer");
			String area_code = reqMap.get("area_code");
			String local_address = reqMap.get("local_address");

			CallResult<Device> callr = deviceService.setid(id, device_type, manufacturer, device_user, area_code,
					local_address);
			if (callr.success()) {
				result.put("code", 20000);
				result.put("data", "设置成功");
			} else {
				result.put("code", 40000);
				result.put("data", callr.getMessage());
			}
		}

		return JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
	}

	@ResponseBody
	@RequestMapping(value = "/change_custom", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String change_custom(@RequestBody Map<String, String> reqMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		String keyword = reqMap.get("keyword");

		List<Custom> list = customService.search(keyword);

		data.put("items", list);

		result.put("code", 20000);
		result.put("data", data);

		return JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
	}

	@ResponseBody
	@RequestMapping(value = "/at", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String at(@RequestBody Map<String, String> reqMap, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		try {
			String type = reqMap.get("type");
			String id = reqMap.get("id");
			String ids = reqMap.get("ids");

			AtCmd cmd = AtCmd.valueOf(type);

			// 是否批量打油
			if (cmd == AtCmd.LUBE && StringUtils.isNotEmpty(ids)) {
				int nok = 0, nfail = 0, ntimeout = 0;
				String[] arr = ids.split("[,]");
				for (String id_ : arr) {
					if (StringUtils.isNotEmpty(id_)) {
						Device device = deviceService.getByDeviceId(id_);
						if (device != null) {
							AtService.Result res = deviceService.execAtCommand(cmd, device);
							if (res.isCode()) {
								nok++;
							} else {
								nfail++;
							}
						}
					}
				}
				result.put("code", 20000);
				result.put("data", nok + "个成功" + (nfail > 0 ? "，" + nfail + "个失败" : ""));
			} else {
				Device device = deviceService.getByDeviceId(id);

				if (StringUtils.isEmpty(id)) {
					result.put("code", 40000);
					result.put("data", "未选择要操作的设备");
				} else if (device == null) {
					result.put("code", 40000);
					result.put("data", "设备不存在:" + id);
				} else if (cmd == null) {
					result.put("code", 40000);
					result.put("data", "命令参数不存在:" + type);
				} else if (device.getStatus() == DeviceStatus.UNINIT.getCode()) {
					result.put("code", 40000);
					result.put("data", "设备未初始化:" + id);
				} else {
					if (cmd == AtCmd.LUBE) { // 打油
						AtService.Result res = deviceService.execAtCommand(cmd, device);
						if (res.isCode()) {
							result.put("code", 20000);
							result.put("data", "打油成功");
						} else {
							result.put("code", 40000);
							result.put("data", "打油失败");
						}
					} else if (cmd == AtCmd.UPDATE) {
						Device device_ = deviceService.syncData(id);
						if (device_ == null) {
							result.put("code", 40000);
							result.put("data", "同步失败");
						} else {
							result.put("code", 20000);
							result.put("data", "同步成功");
						}
					} else if (cmd == AtCmd.STOP) {
						AtService.Result res = deviceService.execAtCommand(cmd, device);
						if (res.isCode()) {
							result.put("code", 20000);
							result.put("data", "停止运行成功");
						} else {
							result.put("code", 40000);
							result.put("data", "停止运行失败");
						}
					} else {
						result.put("code", 40000);
						result.put("data", "未识别操作:" + cmd.name());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();

			result.put("code", 40000);
			result.put("data", "操作失败");
		}

		return JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
	}

	@ResponseBody
	@RequestMapping(value = "/setting", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String setting(@RequestBody Map<String, String> reqMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		data.put(KEY.MAX_WAIT_TIME.name(), bizConfig.getInt(KEY.MAX_WAIT_TIME));
		data.put(KEY.DEVICE_OFFLINE_TIME.name(), bizConfig.getInt(KEY.DEVICE_OFFLINE_TIME));
		data.put(KEY.SMS_SEND_INTERVAL.name(), bizConfig.getInt(KEY.SMS_SEND_INTERVAL));
		data.put(KEY.AT_SERVER_URL.name(), bizConfig.getString(KEY.AT_SERVER_URL));
		data.put(KEY.AT_CMD_TIMEOUT.name(), bizConfig.getString(KEY.AT_CMD_TIMEOUT));
		data.put(KEY.APP_DOWN_URL.name(), bizConfig.getString(KEY.APP_DOWN_URL));
		data.put(KEY.DEVICE_LIST_REFRESH.name(), bizConfig.getInt(KEY.DEVICE_LIST_REFRESH));

		result.put("code", 20000);
		result.put("data", data);

		return JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
	}

	@ResponseBody
	@RequestMapping(value = "/setting_save", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	public String setting_save(@RequestBody Map<String, Object> reqMap, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>(0);

		bizConfig.put(reqMap);

		result.put("code", 20000);
		result.put("data", data);

		return JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
	}

	@ResponseBody
	@RequestMapping(value = "/import", produces = "application/json;charset=utf-8")
	public String upload(@RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest request,
			ModelMap model) {
		Map<String, Object> result = new HashMap<String, Object>();

		if (file != null) {
			try {
				String type = file.getContentType();
				if (type != null && type.indexOf("csv") >= 0) {
					CsvReader reader = new CsvReader(file.getInputStream(), ',', Charsets.UTF_8);
					if (reader != null) {
						CSVUtils.Data data = CSVUtils.parse(reader);
						CallResult<List<String>> res = deviceService.importDevice(data);

						result.put("code", 20000);
						result.put("data", res);
					}
				} else {
					result.put("code", 40000);
					result.put("data", "目前仅支持导入csv格式的文件");
				}
			} catch (IOException e) {
				e.printStackTrace();
				result.put("code", 40000);
				result.put("data", "导入过程发送错误");
			}
		} else {
			result.put("code", 40000);
			result.put("data", "未选择要导入的设备列表文件");
		}

		return JSON.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss");
	}
}
