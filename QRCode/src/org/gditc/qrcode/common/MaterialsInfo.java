package org.gditc.qrcode.common;

import java.io.Serializable;

/**
 * 物资信息类
 */
public class MaterialsInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6407510506457131755L;
	
	private Integer id;
	private String materialsNo;				// 物资编码
	private LedgerInfo ledgerInfo;			// 台账信息对象
	private CardInfo cardInfo;				// 卡片信息对象
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getMaterialsNo() {
		return materialsNo;
	}
	public void setMaterialsNo(String materialsNo) {
		this.materialsNo = materialsNo;
	}
	public LedgerInfo getLedgerInfo() {
		return ledgerInfo;
	}
	public void setLedgerInfo(LedgerInfo ledgerInfo) {
		this.ledgerInfo = ledgerInfo;
	}
	public CardInfo getCardInfo() {
		return cardInfo;
	}
	public void setCardInfo(CardInfo cardInfo) {
		this.cardInfo = cardInfo;
	}
}
