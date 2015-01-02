package org.gditc.qrcode.common;

public class MyConstants {
	public static final String MATERIALS_INFO_CONTENT_TYPE_INSERT = "org.gditc.qrcode/materialsinfo.insert";
	public static final String MATERIALS_INFO_CONTENT_TYPE_EDIT = "org.gditc.qrcode/materialsinfo.edit";
	public static final String CONTENT_URI = "content://org.gditc.qrcode";
	
	public static final String LOGIN_API = "/SysPolling/login!androidLogin.action";
	public static final String LOGOUT_API = "/SysPolling/login!androidLogout.action";
	public static final String INSERT_EDIT_API = "/SysPolling/materials!save.action";
	public static final String SEARCH_DATA_API = "/SysPolling/materials!scanQRCode.action";
	public static final String UPDATE_PASSWORD_API = "/SysPolling/user!updatePassword.action";
	
	
	// 下面4句已废除不用， 暂时保留
	public static final String LEDGER_INFO_CONTENT_TYPE_INSERT = "org.gditc.qrcode/ledgerinfo.insert";
	public static final String LEDGER_INFO_CONTENT_TYPE_EDIT = "org.gditc.qrcode/ledgerinfo.edit";
	public static final String CARD_INFO_CONTENT_TYPE_INSERT = "org.gditc.qrcode/cardinfo.insert";
	public static final String CARD_INFO_CONTENT_TYPE_EDIT = "org.gditc.qrcode/cardinfo.edit";
	
}
