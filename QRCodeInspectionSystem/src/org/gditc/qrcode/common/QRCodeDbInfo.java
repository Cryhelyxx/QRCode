package org.gditc.qrcode.common;

public class QRCodeDbInfo {

	// 表名， 使用一维数组， 使用全局变量(static)
	private static String TableNames[] = {
		"tbl_materials_info", 			// 物资信息表
		"tbl_ledger_info",				// 台账信息表
		"tbl_card_info"					// 卡片信息表
	};
	
	// 与表对应的字段， 使用二维数组， 使用全局变量(static)
	private static String FieldNames[][] = {
		{"_id",						// 行id
			"materialsNo",			// 物资编码
			"ledgerId",				// 台账信息类的id
			"cardId",				// 卡片信息类的id
			"note"},				// 备 注
		{"_id",						// 行id
			"cardNo",				// 卡片编号
			"devicesNo",			// 设备编号
			"commissioningDate",	// 投运日期
			"manufacturer",			// 生产厂家
			"remark",				// 备注
			"cost"},				// 原值
		{"_id",						// 行id
			"FID",					
			"assetsName",			// 资产名称
			"specification",		// 规格型号
			"manufacturer",			// 制造商
			"commissioningDate",	// 投运日期
			"propertyRight"}		// 产权
	};
	
	// 与表的字段对应的字段类型
	private static String FieldTypes[][] = {
		{"INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL",
			"TEXT UNIQUE NOT NULL",
			"TEXT",
			"TEXT",
			"TEXT"},
		{"INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL",
			"TEXT",
			"TEXT",
			"TEXT",
			"TEXT",
			"TEXT",
			"TEXT"},
		{"INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL",
			"TEXT",
			"TEXT",
			"TEXT",
			"TEXT",
			"TEXT",
			"TEXT"}
	};

	public static String[] getTableNames() {
		return TableNames;
	}

	public static String[][] getFieldNames() {
		return FieldNames;
	}

	public static String[][] getFieldTypes() {
		return FieldTypes;
	}
}
