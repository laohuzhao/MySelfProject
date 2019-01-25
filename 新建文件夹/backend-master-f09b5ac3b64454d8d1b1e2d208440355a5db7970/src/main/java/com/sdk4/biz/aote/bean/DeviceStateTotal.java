package com.sdk4.biz.aote.bean;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DeviceStateTotal {

	private int fuel_level_fault; // 油位故障数
	private int fuel_pressure_fault; // 油压故障数
	private int shutting; // 设备休止数
	private int working; // 正在润滑数
	private int online; // 设备在线数
	private int offline; // 设备离线数

}
