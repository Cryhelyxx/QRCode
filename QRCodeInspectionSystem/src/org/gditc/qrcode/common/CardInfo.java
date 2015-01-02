package org.gditc.qrcode.common;

import java.io.Serializable;

/**
 * 卡片信息类
 */
public class CardInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2614557316109307639L;
	
	private Integer id;
	private String FID;
	private String assetsName;				// 资产名称
	private String specification;			// 规格型号
	private String manufacturer;			// 制造商
	private String commissioningDate;		// 投运日期
	private String propertyRight;			// 产权
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getFID() {
		return FID;
	}
	public void setFID(String fID) {
		FID = fID;
	}
	public String getAssetsName() {
		return assetsName;
	}
	public void setAssetsName(String assetsName) {
		this.assetsName = assetsName;
	}
	public String getSpecification() {
		return specification;
	}
	public void setSpecification(String specification) {
		this.specification = specification;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public String getCommissioningDate() {
		return commissioningDate;
	}
	public void setCommissioningDate(String commissioningDate) {
		this.commissioningDate = commissioningDate;
	}
	public String getPropertyRight() {
		return propertyRight;
	}
	public void setPropertyRight(String propertyRight) {
		this.propertyRight = propertyRight;
	}
}
