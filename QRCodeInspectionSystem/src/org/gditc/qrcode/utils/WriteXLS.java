package org.gditc.qrcode.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.gditc.qrcode.dao.QRCodeDbHelper;

import android.content.Context;
import android.database.Cursor;

public class WriteXLS {

	private static QRCodeDbHelper db = null;

	private static WritableWorkbook wwb = null;

	private static WriteXLS instance;
	private static Context mCtx = null;

	public WriteXLS(Context context) {
		super();
		mCtx = context;
	}

	public static WriteXLS getInstance(Context context) {
		if (null == instance) {
			instance = new WriteXLS(context);
		}
		return instance;
	}




	public static void writeDataToXLS(String fileDir, String filename) throws IOException, RowsExceededException, WriteException {
		/*// 创建可写入的Excel工作薄
		File file = new File(targetFile);
		wwb = Workbook.createWorkbook(file);*/
		//File sdCardDir = Environment.getExternalStorageDirectory();
		//String fileType =  fileName.substring(fileName.lastIndexOf('.') + 1);
		// 将WritableWorkbook直接写入到输出流
		File xlsDir = new File(fileDir);
		if (!xlsDir.exists()) {
			xlsDir.mkdir();
		}
		File file = new File(fileDir, filename);
		if (!file.exists()) {
			file.createNewFile();
			file.setWritable(Boolean.TRUE);
		}
		OutputStream os = new FileOutputStream(file);
		wwb = Workbook.createWorkbook(os);
		// 创建工作表
		WritableSheet ws = wwb.createSheet("sheet", 0);		//创建sheet
		Label lableC = null;
		//sheet头1-----------------------------------------------		
		WritableFont wf01 = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD, false,
				UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.RED);
		WritableCellFormat wcf01 = new WritableCellFormat(wf01);
		wcf01.setAlignment(Alignment.CENTRE); // 设置居中
		wcf01.setBorder(Border.ALL, BorderLineStyle.THIN); // 设置边框线
		wcf01.setBackground(jxl.format.Colour.YELLOW); // 设置单元格的背景颜色

		ws.mergeCells(0, 0, 5, 0);
		lableC = new Label(0, 0, "台账", wcf01);
		ws.addCell(lableC);

		ws.mergeCells(6, 0, 11, 0);
		lableC = new Label(6, 0, "卡片", wcf01);
		ws.addCell(lableC);

		lableC = new Label(12, 0, "物资编码", wcf01);
		ws.addCell(lableC);
		//sheet头2--------------------------------------------------------------	
		WritableFont wf02 = new WritableFont(WritableFont.ARIAL, 9, WritableFont.BOLD, false,
				UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK);
		WritableCellFormat wcf02 = new WritableCellFormat(wf02);
		wcf02.setAlignment(Alignment.CENTRE); // 设置居中
		wcf02.setBorder(Border.ALL, BorderLineStyle.THIN); // 设置边框线
		wcf02.setBackground(jxl.format.Colour.BLUE); // 设置单元格的背景颜色
		// 台账信息
		lableC = new Label(0, 1, "卡片编号", wcf02);
		ws.addCell(lableC);
		lableC = new Label(1, 1, "设备型号", wcf02);
		ws.addCell(lableC);
		lableC = new Label(2, 1, "投运日期", wcf02);
		ws.addCell(lableC);
		lableC = new Label(3, 1, "生产厂家", wcf02);
		ws.addCell(lableC);
		lableC = new Label(4, 1, "备注", wcf02);
		ws.addCell(lableC);
		lableC = new Label(5, 1, "原值", wcf02);
		ws.addCell(lableC);
		// 卡片信息
		lableC = new Label(6, 1, "GIS系统G3E_FID", wcf02);
		ws.addCell(lableC);
		lableC = new Label(7, 1, "资产名称", wcf02);
		ws.addCell(lableC);
		lableC = new Label(8, 1, "规格型号", wcf02);
		ws.addCell(lableC);
		lableC = new Label(9, 1, "制造商", wcf02);
		ws.addCell(lableC);
		lableC = new Label(10, 1, "投运日期", wcf02);
		ws.addCell(lableC);
		lableC = new Label(11, 1, "产权", wcf02);
		ws.addCell(lableC);
		lableC = new Label(12, 1, "物资编码", wcf02);
		ws.addCell(lableC);
		// 物资单备注
		lableC = new Label(13, 1, "备注", wcf02);
		ws.addCell(lableC);
		//记录数据---------------------------------------------
		WritableFont wf03 = new WritableFont(WritableFont.ARIAL, 9, WritableFont.NO_BOLD, false,
				UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK);
		WritableCellFormat wcf03 = new WritableCellFormat(wf03);
		wcf03.setAlignment(Alignment.CENTRE); // 设置居中
		wcf03.setBorder(Border.ALL, BorderLineStyle.THIN); // 设置边框线
		//wcf03.setBackground(jxl.format.Colour.WHITE); // 设置单元格的背景颜色
		
		db = QRCodeDbHelper.getInstance(mCtx);
		db.open();

		Cursor cursor = db.findAllMaterialsInfo();

		for (int i = 2; i < cursor.getCount() + 2; i++) {
			if (cursor.moveToNext()) {
				lableC = new Label(12, i, cursor.getString(1), wcf03);
				ws.addCell(lableC);
			}
			String ledgerId = cursor.getString(2);
			String cardId = cursor.getString(3);
			String note = cursor.getString(4);

			if (ledgerId != null) {
				Cursor ledgerCursor = db.findLedgerInfoById(ledgerId);
				if (ledgerCursor.moveToNext()) {
					if (ledgerCursor.getString(1) != null && !"".equals(ledgerCursor.getString(1))) {
						lableC = new Label(0, i, ledgerCursor.getString(1), wcf03);
						ws.addCell(lableC);
					}
					if (ledgerCursor.getString(2) != null && !"".equals(ledgerCursor.getString(2))) {
						lableC = new Label(1, i, ledgerCursor.getString(2), wcf03);
						ws.addCell(lableC);
					}
					if (ledgerCursor.getString(3) != null && !"".equals(ledgerCursor.getString(2))) {
						lableC = new Label(2, i, ledgerCursor.getString(3), wcf03);
						ws.addCell(lableC);
					}
					if (ledgerCursor.getString(4) != null && !"".equals(ledgerCursor.getString(3))) {
						lableC = new Label(3, i, ledgerCursor.getString(4), wcf03);
						ws.addCell(lableC);
					}
					if (ledgerCursor.getString(5) != null && !"".equals(ledgerCursor.getString(4))) {
						lableC = new Label(4, i, ledgerCursor.getString(5), wcf03);
						ws.addCell(lableC);
					}
					if (ledgerCursor.getString(6) != null && !"".equals(ledgerCursor.getString(5))) {
						lableC = new Label(5, i, ledgerCursor.getString(6), wcf03);
						ws.addCell(lableC);
					}
				}
				ledgerCursor.close();
			}
			if (cardId != null) {
				Cursor cardCursor = db.findCardInfoById(cardId);
				if (cardCursor.moveToNext()) {
					if (cardCursor.getString(1) != null && !"".equals(cardCursor.getString(1))) {
						lableC = new Label(6, i, cardCursor.getString(1), wcf03);
						ws.addCell(lableC);
					}
					if (cardCursor.getString(2) != null && !"".equals(cardCursor.getString(2))) {
						lableC = new Label(7, i, cardCursor.getString(2), wcf03);
						ws.addCell(lableC);
					}
					if (cardCursor.getString(3) != null && !"".equals(cardCursor.getString(3))) {
						lableC = new Label(8, i, cardCursor.getString(3), wcf03);
						ws.addCell(lableC);
					}
					if (cardCursor.getString(4) != null && !"".equals(cardCursor.getString(4))) {
						lableC = new Label(9, i, cardCursor.getString(4), wcf03);
						ws.addCell(lableC);
					}
					if (cardCursor.getString(5) != null && !"".equals(cardCursor.getString(5))) {
						lableC = new Label(10, i, cardCursor.getString(5), wcf03);
						ws.addCell(lableC);
					}
					if (cardCursor.getString(6) != null && !"".equals(cardCursor.getString(6))) {
						lableC = new Label(11, i, cardCursor.getString(6), wcf03);
						ws.addCell(lableC);
					}
				}
			}
			if (note != null) {
				lableC = new Label(13, i, note, wcf03);
				ws.addCell(lableC);
			}
			
		}
		cursor.close();
		wwb.write();
		wwb.close();


	}
}
