package org.gditc.qrcode.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.read.biff.BiffException;

import org.gditc.qrcode.common.MaterialsInfo;
import org.gditc.qrcode.dao.QRCodeDbHelper;

import android.content.Context;
import android.content.SharedPreferences;

public class PrepareDbData {

	private final static String DbDataName = "db.xls";
	private QRCodeDbHelper db = null;

	private SharedPreferences mPerPreferences = null;
	private static PrepareDbData instance;
	private Context context;

	public PrepareDbData(Context context) {
		super();
		this.context = context;
	}

	/**
	 * 单例模式中获取唯一的PrepareDbData实例
	 * @return PrepareDbData实例
	 */
	public static PrepareDbData getInstance(Context context) {
		if (null == instance) {
			instance = new PrepareDbData(context);
		}
		return instance;
	}

	/**
	 * 初始化数据库数据
	 */
	public void initDbData() {

		boolean flag = dbDataHasInitialized();
		if (!flag) {
			// 得到  assets 目录下我们实现准备好的文件作为输入流
			InputStream is = null;
			try {
				is = context.getAssets().open(DbDataName);
			} catch (IOException e) {
				e.printStackTrace();
			}
			readDataFromFileToDb(is);
			// 用db_data_exist.xml文件作为日志标记已初始化数据库数据
			mPerPreferences = context.getSharedPreferences("db_data_exist", Context.MODE_PRIVATE);
			SharedPreferences.Editor mEditor = mPerPreferences.edit();
			mEditor.putString("completed", "初始化数据库数据已完成");
			mEditor.commit();

			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 是否已初始化数据库数据
	 * @return
	 */
	private boolean dbDataHasInitialized() {
		// 如果不存在则创建db_data_exist.xml文件
		mPerPreferences = context.getSharedPreferences("db_data_exist", Context.MODE_PRIVATE);
		SharedPreferences.Editor mEditor = mPerPreferences.edit();
		mEditor.commit();

		boolean Existed = mPerPreferences.contains("completed");
		return Existed;
	}

	/**
	 * 从文件中读取数据进数据库
	 */
	private void readDataFromFileToDb(InputStream is) {
		List<MaterialsInfo> materialsInfoList = new ArrayList<MaterialsInfo>();
		try {
			materialsInfoList = ReadXLS.getXLSData(is);
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (BiffException | IOException e) {
			e.printStackTrace();
		}

		db = QRCodeDbHelper.getInstance(context);
		db.open();

		db.insertAllData(materialsInfoList);
		//db.close();

	}
	
	/**
	 * 更新数据库数据
	 */
	public void updateDbData(String excelFilePath) {
		InputStream is = null;
		
		try {
			is = new FileInputStream(new File(excelFilePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		readDataFromFileToDb(is);
	}


}
