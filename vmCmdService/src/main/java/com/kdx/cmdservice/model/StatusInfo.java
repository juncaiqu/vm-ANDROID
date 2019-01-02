package com.kdx.cmdservice.model;

public class StatusInfo {
	private String operator; // 运营商名字
	private String srri; // 信号强度
	private String serial; // 设备唯一标识
	private String sdRoom; // sd卡容量
	private String sdState; // sd卡测试
	private String memory; // 内存占用
	private String cpu; // cpu占用
	private String pix; // 
	private String systemV; // 系统版本
	private String device; //
	private String display; //

	public StatusInfo(String operator, String srri, String serial, String sdRoom, String sdState, String memory, String cpu, String pix, String systemV, String device, String display) {
		this.operator = operator;
		this.srri = srri;
		this.serial = serial;
		this.sdRoom = sdRoom;
		this.sdState = sdState;
		this.memory = memory;
		this.cpu = cpu;
		this.pix = pix;
		this.systemV = systemV;
		this.device = device;
		this.display = display;
	}


	@Override
	public String toString() {
		return "运营商:" + operator + ", 信号强度:" + srri
				+ ", 工控标识:" + serial + ", sdRoom:" + sdRoom +", sdState:" + sdState + ", 内存占用:"
				+ memory + ", cpu:" + cpu + ", 屏幕分辨率:" + pix + ", android版本:" + systemV+", 固件版本:" + display+", 设备厂商:" + device;
	}
	
}
