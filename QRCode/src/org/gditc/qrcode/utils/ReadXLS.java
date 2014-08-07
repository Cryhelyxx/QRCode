package org.gditc.qrcode.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.gditc.qrcode.common.CardInfo;
import org.gditc.qrcode.common.LedgerInfo;
import org.gditc.qrcode.common.MaterialsInfo;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ReadXLS {
	
	private static List<MaterialsInfo> materialsInfoList = null;

	public static List<MaterialsInfo> getXLSData(InputStream is) throws BiffException, IOException {

		/*InputStream is = ReadXLS.class.getClassLoader().getResourceAsStream(
				fileName);*/
		Workbook workBook = Workbook.getWorkbook(is);

		Sheet sheets[] = workBook.getSheets();
		
		materialsInfoList = new ArrayList<MaterialsInfo>();

		//遍历工作表
		for (Sheet sheet : sheets) {
			//遍历每张工作表的每一个表格
			for (int i = 2; i < sheet.getRows(); i++) {
				
				LedgerInfo ledgerInfo = new LedgerInfo();
				CardInfo cardInfo = new CardInfo();
				MaterialsInfo materialsInfo = new MaterialsInfo();

				//获取第i行，12列的单元格，i从2开始。
				String LedgerInfo_cardNo = sheet.getCell(0, i).getContents();
				String LedgerInfo_devicesNo = sheet.getCell(1, i).getContents();
				String LedgerInfo_commissioningDate = sheet.getCell(2, i).getContents();
				String LedgerInfo_manufacturer = sheet.getCell(3, i).getContents();
				String LedgerInfo_remark = sheet.getCell(4, i).getContents();
				String LedgerInfo_cost = sheet.getCell(5, i).getContents();
				ledgerInfo.setCardNo(LedgerInfo_cardNo);
				ledgerInfo.setDevicesNo(LedgerInfo_devicesNo);
				ledgerInfo.setCommissioningDate(LedgerInfo_commissioningDate);
				ledgerInfo.setManufacturer(LedgerInfo_manufacturer);
				ledgerInfo.setRemark(LedgerInfo_remark);
				ledgerInfo.setCost(LedgerInfo_cost);
				
				String cardInfo_FID = sheet.getCell(6, i).getContents();
				String cardInfo_assetsName = sheet.getCell(7, i).getContents();
				String cardInfo_specification = sheet.getCell(8, i).getContents();
				String cardInfo_manufacturer = sheet.getCell(9, i).getContents();
				String cardInfo_commissioningDate = sheet.getCell(10, i).getContents();
				String cardInfo_propertyRight = sheet.getCell(11, i).getContents();
				cardInfo.setFID(cardInfo_FID);
				cardInfo.setAssetsName(cardInfo_assetsName);
				cardInfo.setSpecification(cardInfo_specification);
				cardInfo.setManufacturer(cardInfo_manufacturer);
				cardInfo.setCommissioningDate(cardInfo_commissioningDate);
				cardInfo.setPropertyRight(cardInfo_propertyRight);
				
				String MaterialsInfo_materialsNo = sheet.getCell(12, i).getContents();
				materialsInfo.setMaterialsNo(MaterialsInfo_materialsNo);
				materialsInfo.setLedgerInfo(ledgerInfo);
				materialsInfo.setCardInfo(cardInfo);
				
				materialsInfoList.add(materialsInfo);
			}
		}
		try {
			if (is != null)
				is.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (workBook != null)
				workBook.close();
		}
		return materialsInfoList;
	}
}
