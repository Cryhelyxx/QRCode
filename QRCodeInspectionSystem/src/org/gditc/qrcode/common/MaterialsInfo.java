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
	private LedgerInfo ledgerInfo = new LedgerInfo();			// 台账信息对象
	private CardInfo cardInfo = new CardInfo();				// 卡片信息对象
	private String ledgerId;
	private String cardId;
	
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
	public String getLedgerId() {
		return ledgerId;
	}
	public void setLedgerId(String ledgerId) {
		this.ledgerId = ledgerId;
	}
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
}
