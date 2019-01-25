package com.sdk4.biz.aote.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sdk4.biz.aote.bean.Custom;
import com.sdk4.biz.aote.bean.DataUpdateTime;
import com.sdk4.biz.aote.bean.Device;
import com.sdk4.biz.aote.bean.DeviceStateTotal;
import com.sdk4.biz.aote.bean.HomeDashboard;
import com.sdk4.biz.aote.bean.Notify;
import com.sdk4.biz.aote.dao.CustomDAO;
import com.sdk4.biz.aote.dao.DeviceDAO;
import com.sdk4.biz.aote.dao.DictDAO;
import com.sdk4.biz.aote.service.AtService;
import com.sdk4.biz.aote.service.AtService.Result;
import com.sdk4.biz.aote.service.DeviceService;
import com.sdk4.biz.aote.service.NotifyService;
import com.sdk4.biz.aote.util.Tools;
import com.sdk4.biz.aote.var.AtCmd;
import com.sdk4.biz.aote.var.DeviceStatus;
import com.sdk4.common.CallResult;
import com.sdk4.common.util.CSVUtils.Data;
import com.sdk4.common.util.GPSFormatUtils;
import com.sdk4.common.util.IdUtils;
import com.sdk4.common.util.PageUtils;
import com.sdk4.common.util.ValueUtils;

@Service
public class DeviceServiceImpl implements DeviceService {

	@Autowired
	DeviceDAO deviceDAO;

	@Autowired
	CustomDAO customDAO;

	@Autowired
	DictDAO dictDAO;

	@Autowired
	AtService atService;

	@Autowired
	BizConfig bizConfig;

	@Autowired
	NotifyService notifyService;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat sdf_date = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public int add(Device device) {
		int result = 0;

		try {
			if (device.getAdd_time() == null) {
				device.setAdd_time(new Date());
			}
			if (StringUtils.isEmpty(device.getGenerate_id())) {
				device.setGenerate_id("0000000000000000");
			}
			if (StringUtils.isEmpty(device.getDevice_type())) {
				device.setDevice_type("0000");
			}
			if (StringUtils.isEmpty(device.getDevice_user())) {
				device.setDevice_user("0000");
			}
			if (StringUtils.isEmpty(device.getManufacturer())) {
				device.setManufacturer("0000");
			}
			if (StringUtils.isEmpty(device.getArea_code())) {
				device.setArea_code("0000");
			}
			if (StringUtils.isEmpty(device.getLocal_address())) {
				device.setLocal_address("0000");
			}
			if (device.getCount_num() == null) {
				device.setCount_num(0);
			}
			if (device.getStatus() == null || device.getStatus() == 0) {
				device.setStatus(DeviceStatus.UNINIT.getCode());
			}
			if (StringUtils.isEmpty(device.getDevice_id())) {
				device.setDevice_id(device.getPhone_number());
			}
			if (StringUtils.isEmpty(device.getVersion())) {
				device.setVersion("0");
			}
			if (StringUtils.isEmpty(device.getUpload_period())) {
				device.setUpload_period("30");
			}
			result = deviceDAO.insert(device);
			if (result == 1) {
				if (StringUtils.isNotEmpty(device.getCustom_id())) {
					customDeviceCountChange(device.getCustom_id());
				}
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
	public Device getByDeviceId(String device_id) {
		return deviceDAO.getByDeviceId(device_id);
	}

	@Override
	public Device getByPhoneNumber(String phone_number) {
		return deviceDAO.getByPhoneNumber(phone_number);
	}

	@Override
	public List<Device> getByGenerateId(String generate_id) {
		return deviceDAO.getByGenerateId(generate_id);
	}

	@Override
	public Device syncData(String device_id) {
		Device device = deviceDAO.getByDeviceId(device_id);

		if (device != null) {
			AtService.Result res = atService.exec(AtCmd.UPDATE, device.getPhone_number(), device.getGenerate_id(),
					null);
			if (res.isCode()) {
				// 更新本地
				if (res.getData() != null) {
					JSONObject jo = res.getData();

					device = updateDeviceData(device, jo);
				}
			} else {
				testDeivceOffline(device, true);
				device = null;
			}
		} else {
			device = null;
		}

		return device;
	}

	@Override
	public Device syncData(String phone_number, String generate_id, JSONObject jo) {
		Device device = deviceDAO.getByPhoneNumber(phone_number);

		if (device == null) {
			// 如果设备不存在，创建
			device = this.parseDevice(device, jo);
			if (device != null) {
				device.setGenerate_id(generate_id);
				this.add(device);
			}
		} else {
			device.setGenerate_id(generate_id);
			device = updateDeviceData(device, jo);
		}

		return device;
	}

	private Device parseDevice(Device device, JSONObject jo) {
		String p1 = ValueUtils.getString(jo, "P1", "");
		String p2 = ValueUtils.getString(jo, "P2", "");
		String p3 = ValueUtils.getString(jo, "P3", "");
		String p4 = ValueUtils.getString(jo, "P4", "");
		String temperature = ValueUtils.getString(jo, "Temperature", "");

		int oldProcessingState = device.getProcessing_state() == null ? 0 : device.getProcessing_state();
		int workingState = ValueUtils.getInt(jo, "WorkingState", 0); // 工作状态：1-休止；2-正常；3-油箱故障；4-低温保护；5-设置参数
		int processingState = ValueUtils.getInt(jo, "ProcessingState", 0); // 运行状态：1-休止；2-润滑状态；4-低温保护；5-状态设置
		int pressureState = ValueUtils.getInt(jo, "PressureState", 0); // 到压状态：0-非到压；非0-到压
		int fuelLevelState = ValueUtils.getInt(jo, "FuelLevelState", 0); // 油位状态：0-正常液位；1-预警液位；2-低液位
		int fuelPressureState = ValueUtils.getInt(jo, "FuelPressureState", 0); // 油压状态：0-正常；1-报警；2-润滑到压；3-润滑不到压

		String longitude_degree = ValueUtils.getString(jo, "LongitudeDegree", "");
		String longitude_minute = ValueUtils.getString(jo, "LongitudeMinute", "");
		String longitude_second = ValueUtils.getString(jo, "LongitudeSecond", "");
		String latitude_degree = ValueUtils.getString(jo, "LatitudeDegree", "");
		String latitude_minute = ValueUtils.getString(jo, "LatitudeMinute", "");
		String latitude_second = ValueUtils.getString(jo, "LatitudeSecond", "");

		int pressureMinute = ValueUtils.getInt(jo, "PressureMinute", 0);
		int pressureSecond = ValueUtils.getInt(jo, "PressureSecond", 0);
		String pressureTime = ValueUtils.getString(jo, "PressureTime", "");

		int countNum = ValueUtils.getInt(jo, "CountNum", 0);
		String loopTime = ValueUtils.getString(jo, "LoopTime", "");
		int lowCountNum = ValueUtils.getInt(jo, "LowCountNum", 0);
		String runTime = ValueUtils.getString(jo, "RunTime", "");
		String stopTime = ValueUtils.getString(jo, "StopTime", "");
		String currentTime = ValueUtils.getString(jo, "CurrentTime", "");

		String longitude = GPSFormatUtils.format(longitude_degree, longitude_minute, longitude_second);
		String latitude = GPSFormatUtils.format(latitude_degree, latitude_minute, latitude_second);

		Date latest_time = null;
		try {
			latest_time = sdf.parse(currentTime);
		} catch (Exception e) {
		}

		// 设备在线/离线
		testDeivceOffline(device, false);

		if (StringUtils.isNotEmpty(device.getGenerate_id()) && device.getGenerate_id().length() == 16) {
			String gen_id = device.getGenerate_id();
			device.setDevice_type(gen_id.substring(0, 4));
			//device.setDevice_user(gen_id.substring(4, 8));
			device.setManufacturer(gen_id.substring(4, 8));
			device.setArea_code(gen_id.substring(8, 12));
			device.setLocal_address(gen_id.substring(12, 16));
		}

		device.setP1(p1);
		device.setP2(p2);
		device.setP3(p3);
		device.setP4(p4);
		device.setTemperature(temperature);
		device.setWorking_state(workingState);

		device.setProcessing_state(processingState);
		device.setPressure_state(pressureState);
		device.setFuel_level_state(fuelLevelState);
		device.setFuel_pressure_state(fuelPressureState);

		device.setLongitude_degree(longitude_degree);
		device.setLongitude_minute(longitude_minute);
		device.setLongitude_second(longitude_second);
		device.setLongitude(longitude);
		device.setLatitude_degree(latitude_degree);
		device.setLatitude_minute(latitude_minute);
		device.setLatitude_second(latitude_second);
		device.setLatitude(latitude);

		// 设备表新增一个“历史到压时间”字段，
		// 更新规则为“当心跳信息接收到新的到压时间，且工作状态不为润滑和报警时，赋值为原先的到压时间，
		// 而到压时间字段则赋值为这个新的到压时间”，请注意，“历史到压时间”字段数据，心跳信息并不返回
		if (workingState != 2 && workingState != 3) {
			device.setOld_pressure_time(device.getPressure_time());
		}
		device.setPressure_time(pressureTime);
		device.setPressure_minute(pressureMinute);
		device.setPressure_second(pressureSecond);
		device.setCount_num(countNum);
		device.setLoop_time(loopTime);
		device.setLow_count_num(lowCountNum);
		// 润滑时间应该不为空，在接收到心跳数据的时候，如果该心跳数据没有润滑时间，则不更新，
		// 同理还有休止时间。这两个时间都需要保存最近一次的历史数据
		if (StringUtils.isNotEmpty(runTime)) {
			device.setRun_time(runTime);
		}
		if (StringUtils.isNotEmpty(stopTime)) {
			device.setStop_time(stopTime);
		}
		device.setLatest_time(latest_time);

		// 报警逻辑：油压故障/油位故障/油压油位故障/低温保护
		Notify notify = null;
		if (fuelPressureState == 1 && fuelLevelState == 1) {
			// 若接收到心跳信息状态位为“0x33”双油压油位报警，则将“油压状态”置为“报警”，“液位状态”置为“预警液位”，且系统报警（油压油位故障，既属于油压故障，也属于油位故障）
			notify = new Notify();
			notify.setId(IdUtils.newStrId());
			notify.setDevice_id(device.getDevice_id());
			notify.setType("油压油位故障");
			notify.setDate_str(sdf_date.format(device.getLatest_time()));
			notify.setContent("出现油压油位故障，请及时处理。");
			notify.setAdd_time(device.getLatest_time());
			notify.setSms_send(0);
		} else if (fuelPressureState == 1) {
			// 若接收到心跳信息状态位为“0x31”油压报警，则将“油压状态”置为“报警”，且系统报警（油压故障，属于油压故障类别）
			notify = new Notify();
			notify.setId(IdUtils.newStrId());
			notify.setDevice_id(device.getDevice_id());
			notify.setType("油压故障");
			notify.setDate_str(sdf_date.format(device.getLatest_time()));
			notify.setContent("出现油压故障，请及时处理。");
			notify.setAdd_time(device.getLatest_time());
			notify.setSms_send(0);
		} else if (fuelLevelState == 1) {
			// 若接收到心跳信息状态位为“0x32”油位报警，则将“液位状态”置为“预警液位”，且系统报警（油位故障，属于油位故障类别）
			notify = new Notify();
			notify.setId(IdUtils.newStrId());
			notify.setDevice_id(device.getDevice_id());
			notify.setType("油位故障");
			notify.setDate_str(sdf_date.format(device.getLatest_time()));
			notify.setContent("出现油位故障，请及时处理。");
			notify.setAdd_time(device.getLatest_time());
			notify.setSms_send(0);
		} else if (processingState == 4) {
			// 若接收到心跳信息状态位为“0x40”或者“0x50”，则将“运行状态”置为“低温保护”或者“状态设置”，若为“低温保护”，系统应报警（低温保护，属于低温保护）
			notify = new Notify();
			notify.setId(IdUtils.newStrId());
			notify.setDevice_id(device.getDevice_id());
			notify.setType("低温保护");
			notify.setDate_str(sdf_date.format(device.getLatest_time()));
			notify.setContent("出现低温保护，请及时处理。");
			notify.setAdd_time(device.getLatest_time());
			notify.setSms_send(0);
		} else if (lowCountNum > 6 && fuelLevelState == 2) {
			// 若接收到心跳信息低液位运行次数大于6，则将“液位状态”置为“正常”，小于等于6，则将“液位状态”置为“低液位”，且系统报警（液位低下，属于油位故障）
			notify = new Notify();
			notify.setId(IdUtils.newStrId());
			notify.setDevice_id(device.getDevice_id());
			notify.setType("油位故障");
			notify.setDate_str(sdf_date.format(device.getLatest_time()));
			notify.setContent("出现液位低下，请及时处理。");
			notify.setAdd_time(device.getLatest_time());
			notify.setSms_send(0);
		} else if (oldProcessingState == 2 && processingState == 1 && pressureState == 0 && pressureTime == "NO") {
			// 若最近一次状态为0x2x润滑，且当前接收到心跳信息为”0x10”终止，且PressureState为0不到压，且PressureTime为NO，则系统报警（没有到压信号，属于油压故障）
			notify = new Notify();
			notify.setId(IdUtils.newStrId());
			notify.setDevice_id(device.getDevice_id());
			notify.setType("油压故障");
			notify.setDate_str(sdf_date.format(device.getLatest_time()));
			notify.setContent("没有到压信号，请及时处理。");
			notify.setAdd_time(device.getLatest_time());
			notify.setSms_send(0);
		}

		if (notify != null) {
			notifyService.insert(notify);
		}

		return device;
	}

	private Device updateDeviceData(Device device, JSONObject jo) {
		device = this.parseDevice(device, jo);

		int n = deviceDAO.syncData(device);

		if (n != 1) {
			device = null;
		}

		return device;
	}

	@Override
	public HomeDashboard dashboard(String custom_id) {
		HomeDashboard result = new HomeDashboard();

		if (StringUtils.isEmpty(custom_id)) {
			custom_id = null;
		}

		DeviceStateTotal stateTotal = deviceDAO.totalState(custom_id);

		if (stateTotal == null) {
			result.setFuel_level_fault(0);
			result.setFuel_pressure_fault(0);
			result.setShutting(0);
			result.setWorking(0);
			result.setOnline(0);
			result.setOffline(0);
		} else {
			result.setFuel_level_fault(stateTotal.getFuel_level_fault());
			result.setFuel_pressure_fault(stateTotal.getFuel_pressure_fault());
			result.setShutting(stateTotal.getShutting());
			result.setWorking(stateTotal.getWorking());
			result.setOnline(stateTotal.getOnline());
			result.setOffline(stateTotal.getOffline());
		}

		return result;
	}

	@Override
	public int count(String custom_id, String keyword, String device_id, String corp_name, String device_type,
			String manufacturer, String area_code, String working_state, String processing_state,
			String fuel_level_state, String fuel_pressure_state, List<Integer> status, String tag) {
		if (StringUtils.isEmpty(custom_id)) {
			custom_id = null;
		}
		if (StringUtils.isEmpty(keyword)) {
			keyword = null;
			if (StringUtils.isEmpty(device_id)) {
				device_id = null;
			} else {
				device_id = "%" + device_id.trim() + "%";
			}
			if (StringUtils.isEmpty(corp_name)) {
				corp_name = null;
			} else {
				corp_name = "%" + corp_name.trim() + "%";
			}
		} else {
			if (StringUtils.isEmpty(device_id)) {
				device_id = keyword;
			}
			keyword = "%" + keyword.trim() + "%";
		}
		if (StringUtils.isEmpty(device_type)) {
			device_type = null;
		}
		if (StringUtils.isEmpty(area_code)) {
			area_code = null;
		} else {
			String[] arr = area_code.split("[,]");
			if (arr.length > 1) {
				area_code = arr[1].trim();
			}
			if (StringUtils.isEmpty(area_code)) {
				area_code = null;
			}
		}

		Integer working_state_ = null;
		Integer processing_state_ = null;
		Integer fuel_level_state_ = null;
		Integer fuel_pressure_state_ = null;
		if (StringUtils.isNotEmpty(working_state)) {
			working_state_ = Integer.parseInt(working_state);
		}
		if (StringUtils.isNotEmpty(processing_state)) {
			processing_state_ = Integer.parseInt(processing_state);
		}
		if (StringUtils.isNotEmpty(fuel_level_state)) {
			fuel_level_state_ = Integer.parseInt(fuel_level_state);
		}
		if (StringUtils.isNotEmpty(fuel_pressure_state)) {
			fuel_pressure_state_ = Integer.parseInt(fuel_pressure_state);
		}

		manufacturer = ValueUtils.getString(manufacturer, null);
		if (StringUtils.isEmpty(tag)) {
			tag = null;
		} else {
			tag = "%" + tag + "%";
		}

		return deviceDAO.count(custom_id, keyword, device_id, corp_name, device_type, manufacturer, null, area_code,
				working_state_, processing_state_, fuel_level_state_, fuel_pressure_state_, status, tag);
	}

	@Override
	public List<Device> query(String custom_id, String keyword, String device_id, String corp_name, String device_type,
			String manufacturer, String area_code, String working_state, String processing_state,
			String fuel_level_state, String fuel_pressure_state, List<Integer> status, String tag, Integer page_index,
			int page_size) {
		if (StringUtils.isEmpty(custom_id)) {
			custom_id = null;
		}
		if (StringUtils.isEmpty(keyword)) {
			keyword = null;
			if (StringUtils.isEmpty(device_id)) {
				device_id = null;
			} else {
				device_id = "%" + device_id.trim() + "%";
			}
			if (StringUtils.isEmpty(corp_name)) {
				corp_name = null;
			} else {
				corp_name = "%" + corp_name.trim() + "%";
			}
		} else {
			if (StringUtils.isEmpty(device_id)) {
				device_id = keyword;
			}
			keyword = "%" + keyword.trim() + "%";
		}
		if (StringUtils.isEmpty(device_type)) {
			device_type = null;
		}
		if (StringUtils.isEmpty(area_code)) {
			area_code = null;
		} else {
			String[] arr = area_code.split("[,]");
			if (arr.length > 1) {
				area_code = arr[1].trim();
			}
			if (StringUtils.isEmpty(area_code)) {
				area_code = null;
			}
		}

		Integer working_state_ = null;
		Integer processing_state_ = null;
		Integer fuel_level_state_ = null;
		Integer fuel_pressure_state_ = null;
		if (StringUtils.isNotEmpty(working_state)) {
			working_state_ = Integer.parseInt(working_state);
		}
		if (StringUtils.isNotEmpty(processing_state)) {
			processing_state_ = Integer.parseInt(processing_state);
		}
		if (StringUtils.isNotEmpty(fuel_level_state)) {
			fuel_level_state_ = Integer.parseInt(fuel_level_state);
		}
		if (StringUtils.isNotEmpty(fuel_pressure_state)) {
			fuel_pressure_state_ = Integer.parseInt(fuel_pressure_state);
		}

		manufacturer = ValueUtils.getString(manufacturer, null);
		if (StringUtils.isEmpty(tag)) {
			tag = null;
		} else {
			tag = "%" + tag + "%";
		}
		
		List<Device> list = deviceDAO.query(custom_id, keyword, device_id, corp_name, device_type, manufacturer, null,
				area_code, working_state_, processing_state_, fuel_level_state_, fuel_pressure_state_, status, tag,
				page_index == null ? null : PageUtils.calcStart(page_index, page_size), page_size);

		for (Device device : list) {
			testDeivceOffline(device, true);
		}

		return list;
	}

	@Override
	public List<Device> query(List<String> device_ids) {
		return deviceDAO.queryIDS(device_ids);
	}

	@Override
	public int updateParams(String device_id, Map<String, String> params) {
		int result = 0;

		Device device = deviceDAO.getByDeviceId(device_id);
		if (device != null) {
			String type = params.get("type");

			String p1 = ValueUtils.getString(params, "p1", device.getP1());
			String p2 = ValueUtils.getString(params, "p2", device.getP2());
			String p3 = ValueUtils.getString(params, "p3", device.getP3());
			String p4 = ValueUtils.getString(params, "p4", device.getP4());
			String version = ValueUtils.getString(params, "version", device.getVersion());
			String upload_period = ValueUtils.getString(params, "upload_period", device.getUpload_period());

			boolean setok = false;
			if ("p".equals(type)) {// 设置运行参数
				Map<String, String> atParams = Maps.newHashMap();
				atParams.put("P1", p1);
				atParams.put("P2", p2);
				atParams.put("P3", p3);
				atParams.put("P4", p4);
				AtService.Result res = atService.exec(AtCmd.SET_PARAMS, device.getPhone_number(),
						device.getGenerate_id(), atParams);
				if (res.isCode()) {
					setok = true;
				}
			} else if ("v".equals(type)) {// 设置版本
				Map<String, String> atParams = Maps.newHashMap();
				atParams.put("version", version);
				AtService.Result res = atService.exec(AtCmd.SET_VERSION, device.getPhone_number(),
						device.getGenerate_id(), atParams);
				if (res.isCode()) {
					setok = true;
				}
			} else if ("u".equals(type)) {// 设置主动上传周期
				Map<String, String> atParams = Maps.newHashMap();
				atParams.put("uploadPeriod", upload_period);
				AtService.Result res = atService.exec(AtCmd.SET_UPLOAD_PERIOD, device.getPhone_number(),
						device.getGenerate_id(), atParams);
				if (res.isCode()) {
					setok = true;
				}
			}

			if (setok) {
				result = deviceDAO.updateParams(device_id, p1, p2, p3, p4, version, upload_period);
			}
		}

		return result;
	}

	@Override
	public int updateTag(String device_id, String tag) {
		return this.deviceDAO.updateTag(device_id, tag);
	}
	
	@Override
	public CallResult<Device> alloc(String device_id, String custom_id) {
		CallResult<Device> result = new CallResult<Device>();

		Device device = this.getByDeviceId(device_id);
		if (device == null) {
			result.setCode(404);
			result.setMessage("未找到设备信息");
		} else if (device.getStatus() == DeviceStatus.UNINIT.getCode()) {
			result.setCode(404);
			result.setMessage("设备未初始化");
		} else {
			String old_custom_id = device.getCustom_id();
			String new_device_user = "";
			if (StringUtils.isEmpty(custom_id)) {
				// 取消关联
				new_device_user = "0000";
				if (StringUtils.isEmpty(device.getCustom_id())) {
					result.setCode(404);
					result.setMessage("设备未关联不需要取消");
				}
			} else if (StringUtils.equals(custom_id, device.getCustom_id())) {
				result.setCode(400);
				result.setMessage("和上次分配客户一样");
			} else {
				// 关联
				Custom custom = this.customDAO.get(custom_id);
				if (custom == null) {
					result.setCode(404);
					result.setMessage("未找到客户信息");
				} else if (StringUtils.isEmpty(custom.getCode())) {
					result.setCode(404);
					result.setMessage("未设置客户编码");
				} else {
					new_device_user = custom.getCode();
				}
			}
			if (result.success()) {
				/*
				Map<String, String> atParams = Maps.newHashMap();
				atParams.put("originID", device.getGenerate_id());
				atParams.put("deviceType", device.getDevice_type());
				atParams.put("deviceUser", new_device_user);
				atParams.put("areaCode", device.getArea_code());
				atParams.put("localAddress", device.getLocal_address());
				
				AtService.Result res = atService.exec(AtCmd.SETID, device.getPhone_number(), null, atParams);

				if (res.isCode()) {
					deviceDAO.updateCustom(device_id, custom_id, new_device_user, new Date());

					result.setCode(0);
					result.setMessage("操作成功");

					device.setCustom_id(custom_id);
					device.setDevice_user(new_device_user);
					result.setData(device);

					if (StringUtils.isNotEmpty(old_custom_id)) {
						customDeviceCountChange(old_custom_id);
					}
					if (StringUtils.isNotEmpty(custom_id)) {
						customDeviceCountChange(custom_id);
					}
				} else {
					result.setCode(300);
					result.setMessage("操作失败:" + res.getMessage());
				}*/
				
				deviceDAO.updateCustom(device_id, custom_id, new_device_user, new Date());
				
				if (StringUtils.isNotEmpty(old_custom_id)) {
					customDeviceCountChange(old_custom_id);
				}
				if (StringUtils.isNotEmpty(custom_id)) {
					customDeviceCountChange(custom_id);
				}
			}
		}

		return result;
	}

	@Override
	public int init(String device_id, Map<String, String> params) {
		int result = 0;

		Device device = deviceDAO.getByDeviceId(device_id);
		if (device != null) {
			String device_type = ValueUtils.getString(params, "device_type", "");
			String manufacturer = ValueUtils.getString(params, "manufacturer", "");
			String device_user = ValueUtils.getString(params, "device_user", "");
			String area_code = ValueUtils.getString(params, "area_code", "");
			String area_name = ValueUtils.getString(params, "area_name", "");
			String local_address = ValueUtils.getString(params, "local_address", "");
			String p1 = ValueUtils.getString(params, "p1", "");
			String p2 = ValueUtils.getString(params, "p2", "");
			String p3 = ValueUtils.getString(params, "p3", "");
			String p4 = ValueUtils.getString(params, "p4", "");
			String version = ValueUtils.getString(params, "version", "");
			String upload_period = ValueUtils.getString(params, "upload_period", "");

			String gen_id = device_type + device_user + area_code + local_address;

			// 生成ID是否重复
			boolean re = false;
			List<Device> device_list = this.getByGenerateId(gen_id);
			if (device_list.size() > 0) {
				for (Device d : device_list) {
					if (!StringUtils.equals(d.getDevice_id(), device.getDevice_id())) {
						re = true;
						break;
					}
				}
			}

			if (re) {
				result = -1;
			} else {
				// 设备初始化
				Map<String, String> atParams = Maps.newHashMap();
				atParams.put("P1", p1);
				atParams.put("P2", p2);
				atParams.put("P3", p3);
				atParams.put("P4", p4);
				atParams.put("version", version);
				atParams.put("uploadPeriod", upload_period);

				AtService.Result res = atService.exec(AtCmd.INIT, device.getPhone_number(), gen_id, atParams);

				if (res.isCode()) {
					result = deviceDAO.updateInitData(device_id, gen_id, device_type, manufacturer, device_user,
							area_code, area_name, local_address, p1, p2, p3, p4, version, upload_period);
					deviceDAO.updateStatus(device_id, DeviceStatus.ONLINE.getCode());
					result = 1;
				} else {
					result = 0;
				}
			}
		}

		return result;
	}

	@Override
	public CallResult<Device> setid(String device_id, String device_type, String manufacturer, String device_user,
			String area_code, String local_address) {
		CallResult<Device> result = new CallResult<Device>();

		Device device = deviceDAO.getByDeviceId(device_id);
		if (StringUtils.isEmpty(device_type)) {
			device_type = device.getDevice_type();
		}
		if (StringUtils.isEmpty(device_user)) {
			device_user = device.getDevice_user();
		}
		if (StringUtils.isEmpty(area_code)) {
			area_code = device.getArea_code();
		}
		if (StringUtils.isEmpty(local_address)) {
			local_address = device.getLocal_address();
		}
		if (device == null) {
			result.setCode(404);
			result.setMessage("设备不存在");
		} else if (!Tools.testHex4(device_type)) {
			result.setCode(404);
			result.setMessage("设备类别编码范围为0000~FFFF");
		} else if (!Tools.testHex4(device_user)) {
			result.setCode(404);
			result.setMessage("用户编码范围为0000~FFFF");
		} else if (!Tools.testHex4(area_code)) {
			result.setCode(404);
			result.setMessage("地域码范围为0000~FFFF");
		} else if (!Tools.testHex4(local_address)) {
			result.setCode(404);
			result.setMessage("设备流水号范围为0000~FFFF");
		} else {
			Map<String, String> atParams = Maps.newHashMap();
			atParams.put("originID", device.getGenerate_id());
			atParams.put("deviceType", device_type);
			atParams.put("manufacturer", manufacturer);
			//atParams.put("deviceUser", device_user);
			atParams.put("areaCode", area_code);
			atParams.put("localAddress", local_address);

			AtService.Result res = atService.exec(AtCmd.SETID, device.getPhone_number(), null, atParams);

			if (res.isCode()) {
				deviceDAO.updateInitData(device_id, res.getGenerateID(), device_type, manufacturer, device_user, area_code, null,
						local_address, null, null, null, null, null, null);
				deviceDAO.updateStatus(device_id, DeviceStatus.ONLINE.getCode());
				result.setCode(0);
				result.setMessage(res.getMessage());

				device = deviceDAO.getByDeviceId(device_id);

				result.setData(device);
			} else {
				result.setCode(300);
				result.setMessage("设置失败:" + res.getMessage());
			}
		}

		return result;
	}

	@Override
	public Result execAtCommand(AtCmd cmd, Device device) {
		Map<String, String> params = Maps.newHashMap();

		AtService.Result res = atService.exec(cmd, device.getPhone_number(), device.getGenerate_id(), params);

		if (res.isCode()) {
			boolean update = false;
			Device device_u = new Device();
			device_u.setPhone_number(device.getPhone_number());

			JSONObject data = res.getData();
			if (data == null) {
				data = new JSONObject();
			}

			if (cmd == AtCmd.LUBE) {
				update = true;
				if (data.containsKey("ProcessingState")) {
					device_u.setProcessing_state(ValueUtils.getInt(data, "ProcessingState", 0));
				}
				if (data.containsKey("FuelPressureState")) {
					device_u.setFuel_pressure_state(ValueUtils.getInt(data, "FuelPressureState", 0));
				}
				if (data.containsKey("WorkingState")) {
					device_u.setWorking_state(ValueUtils.getInt(data, "WorkingState", 0));
				}
			} else if (cmd == AtCmd.STOP) {
				update = true;
				if (data.containsKey("ProcessingState")) {
					device_u.setProcessing_state(ValueUtils.getInt(data, "ProcessingState", 0));
				}
			} else if (cmd == AtCmd.INIT || cmd == AtCmd.SET_PARAMS || cmd == AtCmd.SET_VERSION
					|| cmd == AtCmd.SET_UPLOAD_PERIOD) {
				update = true;
				if (data.containsKey("P1")) {
					device_u.setP1(ValueUtils.getString(data, "P1", null));
				}
				if (data.containsKey("P2")) {
					device_u.setP1(ValueUtils.getString(data, "P2", null));
				}
				if (data.containsKey("P3")) {
					device_u.setP1(ValueUtils.getString(data, "P3", null));
				}
				if (data.containsKey("P4")) {
					device_u.setP1(ValueUtils.getString(data, "P4", null));
				}
				if (data.containsKey("UploadPeriod")) {
					device_u.setUpload_period(ValueUtils.getString(data, "UploadPeriod", null));
				}
				if (data.containsKey("Version")) {
					device_u.setVersion(ValueUtils.getString(data, "Version", null));
				}
			}
			if (update) {
				this.deviceDAO.syncData(device_u);
			}
		}

		return res;
	}

	@Override
	public int insertHIS(Device device) {
		return deviceDAO.insertHIS(device);
	}

	@Override
	public int countDataUpdate(String custom_id) {
		if (StringUtils.isEmpty(custom_id)) {
			custom_id = null;
		}
		return deviceDAO.countDataUpdate(custom_id);
	}

	@Override
	public List<DataUpdateTime> queryDataUpdate(String custom_id, int page_index, int page_size) {
		if (StringUtils.isEmpty(custom_id)) {
			custom_id = null;
		}
		return deviceDAO.queryDataUpdate(custom_id, PageUtils.calcStart(page_index, page_size), page_size);
	}

	private void customDeviceCountChange(String custom_id) {
		int count = deviceDAO.totalCustomDeviceCount(custom_id);
		customDAO.updateDeviceCount(custom_id, count);
	}

	@Override
	public CallResult<List<String>> importDevice(Data data) {
		CallResult<List<String>> result = new CallResult<List<String>>();

		String info = "";

		List<String> list = Lists.newArrayList();
		int count = data.rows.size();
		int fail = 0;
		if (count > 0) {
			// 设备ID,用户ID,手机号,设备类别,用户厂家,地域码,组内地址,生成ID,参数P1,参数P2,参数P3,参数P4,当前版本号,上传周期
			int n_device_id = ArrayUtils.indexOf(data.headers, "设备ID");
			int n_custom_id = ArrayUtils.indexOf(data.headers, "用户ID");
			int n_phone_number = ArrayUtils.indexOf(data.headers, "手机号");
			int n_device_type = ArrayUtils.indexOf(data.headers, "设备类别");
			int n_device_user = ArrayUtils.indexOf(data.headers, "用户厂家");
			int n_area_code = ArrayUtils.indexOf(data.headers, "地域码");
			int n_local_address = ArrayUtils.indexOf(data.headers, "组内地址");
			int n_generate_id = ArrayUtils.indexOf(data.headers, "生成ID");
			int n_p1 = ArrayUtils.indexOf(data.headers, "参数P1");
			int n_p2 = ArrayUtils.indexOf(data.headers, "参数P2");
			int n_p3 = ArrayUtils.indexOf(data.headers, "参数P3");
			int n_p4 = ArrayUtils.indexOf(data.headers, "参数P4");
			int n_version = ArrayUtils.indexOf(data.headers, "当前版本号");
			int n_upload_period = ArrayUtils.indexOf(data.headers, "上传周期");

			Iterator<String[]> iter = data.rows.iterator();
			while (iter.hasNext()) {
				try {
					String[] row = iter.next();

					String phone_number = row[n_phone_number];

					Device device = this.getByPhoneNumber(phone_number);

					if (device != null) {
						continue;
					}

					device = new Device();

					if (n_device_id > -1) {
						device.setDevice_id(row[n_device_id]);
					}
					if (n_custom_id > -1) {
						device.setCustom_id(row[n_custom_id]);
					}
					if (n_phone_number > -1) {
						device.setPhone_number(row[n_phone_number]);
					}
					if (n_device_type > -1) {
						device.setDevice_type(row[n_device_type]);
					}
					if (n_device_user > -1) {
						device.setDevice_user(row[n_device_user]);
					}
					if (n_area_code > -1) {
						device.setArea_code(row[n_area_code]);
					}
					if (n_local_address > -1) {
						device.setLocal_address(row[n_local_address]);
					}
					if (n_generate_id > -1) {
						device.setGenerate_id(row[n_generate_id]);
					}
					if (n_p1 > -1) {
						device.setP1(row[n_p1]);
					}
					if (n_p2 > -1) {
						device.setP1(row[n_p2]);
					}
					if (n_p3 > -1) {
						device.setP1(row[n_p3]);
					}
					if (n_p4 > -1) {
						device.setP1(row[n_p4]);
					}
					if (n_version > -1) {
						device.setVersion(row[n_version]);
					}
					if (n_upload_period > -1) {
						device.setUpload_period(row[n_upload_period]);
					}

					device.setStatus(DeviceStatus.OFFLINE.getCode());
					if (this.add(device) == 1) {
						list.add(device.getDevice_id());
					} else {
						fail++;
					}
				} catch (Exception e) {
					e.printStackTrace();
					fail++;
				}
			}
		}

		if (fail == 0) {
			info = "导入成功";
		} else {
			info = "部分导入成功，失败" + fail + "条";
		}
		result.setMessage(info);

		return result;
	}

	// 判断设备是否离线
	private void testDeivceOffline(Device device, boolean save) {
		int timeout = bizConfig.getInt(BizConfig.KEY.DEVICE_OFFLINE_TIME);

		int old_status = device.getStatus();
		if (device.getLatest_time() != null) {
			if (System.currentTimeMillis() - device.getLatest_time().getTime() > timeout * 1000) {
				device.setStatus(DeviceStatus.OFFLINE.getCode());
			} else {
				device.setStatus(DeviceStatus.ONLINE.getCode());
			}
			if (save && old_status != device.getStatus()) {
				deviceDAO.updateStatus(device.getDevice_id(), device.getStatus());
			}
		}
	}

	private int parseStatus(int type, String val) {
		int result = 0;

		try {
			switch (type) {
			case 1:// 工作状态
				result = val.equals("休止") ? 1
						: val.equals("正常") ? 2
								: val.equals("油箱故障") ? 3
										: val.equals("低温保护") ? 4 : val.equals("设置参数") ? 5 : Integer.parseInt(val);
				break;
			case 2:// 运行状态
				result = val.equals("休止") ? 1
						: val.equals("润滑状态") ? 2
								: val.equals("低温保护") ? 4 : val.equals("状态设置") ? 5 : Integer.parseInt(val);
				break;
			case 3:// 油位状态
				result = val.equals("正常液位") ? 0
						: val.equals("预警液位") ? 1 : val.equals("低液位") ? 2 : Integer.parseInt(val);
				break;
			case 4:// 油压状态
				result = val.equals("正常") ? 0
						: val.equals("报警") ? 1
								: val.equals("润滑到压") ? 2 : val.equals("润滑不到压") ? 3 : Integer.parseInt(val);
				break;
			case 5:// 到压状态
				result = val.equals("非到压") ? 0 : val.equals("不到压") ? 0 : Integer.parseInt(val);
				break;
			}
		} catch (Exception e) {
		}

		return result;
	}

}
