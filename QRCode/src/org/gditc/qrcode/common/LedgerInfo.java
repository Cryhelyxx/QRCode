package org.gditc.qrcode.common;

import java.io.Serializable;

/**
 * 台账信息类
 */
public class LedgerInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8353496919967793118L;
	
	private Integer id;
	private String cardNo;					// 卡片编号
	private String devicesNo;				// 设备编号
	private String commissioningDate;		// 投运日期
	private String manufacturer;			// 生产厂家
	private String remark;					// 备 注
	private String cost;					// 原 值
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getDevicesNo() {
		return devicesNo;
	}
	public void setDevicesNo(String devicesNo) {
		this.devicesNo = devicesNo;
	}
	public String getCommissioningDate() {
		return commissioningDate;
	}
	public void setCommissioningDate(String commissioningDate) {
		this.commissioningDate = commissioningDate;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getCost() {
		return cost;
	}
	public void setCost(String cost) {
		this.cost = cost;
	}
}
