package org.gditc.qrcode.dao;

import java.util.Iterator;
import java.util.List;

import org.gditc.qrcode.common.CardInfo;
import org.gditc.qrcode.common.LedgerInfo;
import org.gditc.qrcode.common.MaterialsInfo;
import org.gditc.qrcode.common.QRCodeDbInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * @File QRCodeDbHelper.java
 * @Package org.gditc.qrcode.dao
 * @Description TODO
 * @Copyright Copyright © 2014
 * @Site https://github.com/Cryhelyxx
 * @Blog http://blog.csdn.net/Cryhelyxx
 * @Email cryhelyxx@gmail.com
 * @Company 广东轻工职业技术学院计算机工程系
 * @Date 2014年7月22日 下午5:45:10
 * @author Cryhelyxx
 * @version 1.0
 */
public class QRCodeDbHelper {

	//private static final String TAG = "QRCodeDbHelper";
	// SQLiteOpenHelper实例对象
	private DatabaseHelper mDbHelper;
	// 数据库实例对象
	private SQLiteDatabase mDb;
	// 数据库调用实例
	private static QRCodeDbHelper openHelper = null;
	// 数据库名称
	private static final String DATABASE_NAME = "qrcode.db";
	// 数据库版本
	private static int DATABASE_VERSION = 1;
	// 上下文对象
	private Context mCtx;

	//表名
	private static String TableNames[];
	//字段名
	private static String FieldNames[][];
	//字段类型
	private static String FieldTypes[][];

	// 构造方法
	public QRCodeDbHelper(Context ctx) {
		super();
		this.mCtx = ctx;
	}

	/**
	 * 获取数据库调用实例
	 * @param context
	 * @return 数据库调用实例
	 */
	public static QRCodeDbHelper getInstance(Context context) {
		if (openHelper == null) {
			openHelper = new QRCodeDbHelper(context);
			TableNames = QRCodeDbInfo.getTableNames();
			FieldNames = QRCodeDbInfo.getFieldNames();
			FieldTypes = QRCodeDbInfo.getFieldTypes();
		}
		return openHelper;
	}

	// 数据库辅助类(内部类)
	private static class DatabaseHelper extends SQLiteOpenHelper {

		// 构造方法
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		/**
		 * 创建数据库后，对数据库的操作
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {

			if (TableNames == null) {
				return;
			}
			for (int i = 0; i < TableNames.length; i++) {
				String sql = "CREATE TABLE " + TableNames[i] + " (";
				for(int j = 0; j < FieldNames[i].length; j++){
					sql += FieldNames[i][j] + " " + FieldTypes[i][j] + ",";
				}
				sql = sql.substring(0, sql.length() - 1);		//去掉最后的","
				sql += ")";
				db.execSQL(sql);
			}
		}

		/**
		 * 更改数据库版本的操作
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			for (int i = 0; i < TableNames[i].length(); i++) {
				String sql = "DROP TABLE IF EXISTS " + TableNames[i];
				db.execSQL(sql);
			}
			onCreate(db);
		}

		/**
		 * 打开数据库后首先被执行
		 */
		@Override
		public void onOpen(SQLiteDatabase db) {
			super.onOpen(db);
		}
	}

	/**
	 * 打开数据库
	 * @return
	 * @throws SQLException
	 */
	public QRCodeDbHelper open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	/**
	 * 关闭数据库
	 */
	public void close() {
		mDbHelper.close();
	}

	/**
	 * sql查询语句
	 * @param sql
	 * @param selectionArgs
	 * @return
	 */
	public Cursor rawQuery(String sql, String[] selectionArgs) {
		Cursor cursor = mDb.rawQuery(sql, selectionArgs);
		return cursor;
	}

	/**
	 * 查询数据
	 * @param table
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return
	 */
	public Cursor select(String table, String[] columns,
			String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy) {
		Cursor cursor = mDb.query(
				table, columns, selection, selectionArgs,
				groupBy, having, orderBy);
		return cursor;
	}

	/**
	 * 添加数据
	 * @param table
	 * @param fields
	 * @param values
	 * @return
	 */
	public long insert(String table, String fields[], String values[]) {
		ContentValues cv = new ContentValues();
		for (int i = 0; i < fields.length; i++) {
			cv.put(fields[i], values[i]);
		}
		return mDb.insert(table, null, cv);
	}

	/**
	 * 删除数据
	 * @param table
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	public int delete(String table, String whereClause, String[] whereArgs) {
		return mDb.delete(table, whereClause, whereArgs);
	}

	/**
	 * 更新数据
	 * @param table
	 * @param updateFields
	 * @param updateValues
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	public int update(String table, String updateFields[], String updateValues[],
			String whereClause, String[] whereArgs) {
		ContentValues cv = new ContentValues();
		for (int i = 0; i < updateFields.length; i++) {
			cv.put(updateFields[i], updateValues[i]);
		}
		return mDb.update(table, cv, whereClause, whereArgs);
	}

	/**
	 * 批量插入数据
	 * @param materialsInfoList
	 */
	@SuppressWarnings("rawtypes")
	public void insertAllData(List<MaterialsInfo> materialsInfoList) {
		Iterator it = materialsInfoList.iterator();
		while (it.hasNext()) {
			MaterialsInfo materialsInfo = (MaterialsInfo) it.next();
			LedgerInfo ledgerInfo = materialsInfo.getLedgerInfo();
			CardInfo cardInfo = materialsInfo.getCardInfo();

			addLedgerInfo(ledgerInfo);
			addCardInfo(cardInfo);

			String ledgerId = null;
			String cardId = null;
			Cursor cursor = null;

			cursor = findLedgerIdByLedgerInfo(ledgerInfo);
			if (cursor.getCount() > 1) {		// 保存一张物资表对应的ledgerId是独一无二的
				cursor.moveToLast();
				ledgerId = cursor.getString(0);
			} else {
				cursor.moveToFirst();
				ledgerId = cursor.getString(0);
			}
			
			cursor = findCardIdByCardInfo(cardInfo);
			if (cursor.getCount() > 1) {		// 保存一张物资表对应的cardId是独一无二的
				cursor.moveToLast();
				cardId = cursor.getString(0);
			} else {
				cursor.moveToFirst();
				cardId = cursor.getString(0);
			}
			
			cursor.close();
			
			ContentValues values0 = new ContentValues();
			if (ledgerId != null || cardId != null) {
				if (materialsInfo.getMaterialsNo() != null && !"".equals(materialsInfo.getMaterialsNo().trim())) {
					values0.put(FieldNames[0][1], materialsInfo.getMaterialsNo());
					values0.put(FieldNames[0][2], ledgerId);
					values0.put(FieldNames[0][3], cardId);
					mDb.insert(TableNames[0], null, values0);
				}

			}
		}
	}

	/**
	 * 根据物资编号获取相关的物资信息
	 * @param materialsNo
	 * @return 游标
	 */
	public Cursor findMaterialsInfoByMaterialsNo(String materialsNo) {
		String sql = "SELECT * FROM " + TableNames[0] + " WHERE " + FieldNames[0][1] + "=?";
		String[] selectionArgs = {materialsNo};
		return mDb.rawQuery(sql, selectionArgs);
	}

	/**
	 * 根据id查找台账信息
	 * @param ledgerId
	 * @return
	 */
	public Cursor findLedgerInfoById(String ledgerId) {
		String sql = "SELECT * FROM " + TableNames[1] + " WHERE " + FieldNames[1][0] + "=?";
		String[] selectionArgs = {ledgerId};
		return mDb.rawQuery(sql, selectionArgs);
	}


	/**
	 * 根据id查找卡片信息
	 * @param cardId
	 * @return
	 */
	public Cursor findCardInfoById(String cardId) {
		String sql = "SELECT * FROM " + TableNames[2] + " WHERE " + FieldNames[2][0] + "=?";
		String[] selectionArgs = {cardId};
		return mDb.rawQuery(sql, selectionArgs);
	}

	/**
	 * 编辑备注信息
	 * @param strNote
	 * @param materialsNo
	 */
	public int updateNote(String strNote, String materialsNo) {
		ContentValues values = new ContentValues();
		values.put(FieldNames[0][4], strNote);
		return mDb.update(TableNames[0], values, FieldNames[0][1] + "=?", new String[]{materialsNo});
	}

	/**
	 * 根据物资编号获取相关的备注信息
	 * @param materialsNo
	 * @return 游标
	 */
	public Cursor getNoteByMaterialsNo(String materialsNo) {
		String sql = "SELECT " + FieldNames[0][4] + " FROM " + TableNames[0] + " WHERE " + FieldNames[0][1] + "=?";
		String[] selectionArgs = {materialsNo};
		return mDb.rawQuery(sql, selectionArgs);
	}

	/**
	 * 获取所有物资信息
	 * @return 游标
	 */
	public Cursor findAllMaterialsInfo() {
		String sql = "SELECT * " + " FROM " + TableNames[0];
		return mDb.rawQuery(sql, null);
	}

	/**
	 * 新增台账信息
	 * @param ledgerInfoCache
	 * @return
	 */
	public long addLedgerInfo(LedgerInfo ledgerInfoCache) {
		ContentValues values = new ContentValues();
		values.put(FieldNames[1][1], ledgerInfoCache.getCardNo());
		values.put(FieldNames[1][2], ledgerInfoCache.getDevicesNo());
		values.put(FieldNames[1][3], ledgerInfoCache.getCommissioningDate());
		values.put(FieldNames[1][4], ledgerInfoCache.getManufacturer());
		values.put(FieldNames[1][5], ledgerInfoCache.getRemark());
		values.put(FieldNames[1][6], ledgerInfoCache.getCost());

		return mDb.insert(TableNames[1], null, values);
	}

	/**
	 * 根据台账信息的id更新台账信息
	 * @param ledgerInfoCache
	 * @param ledgerId
	 * @return
	 */
	public int updateLedgerInfoByLedgerId(LedgerInfo ledgerInfoCache, String ledgerId) {
		ContentValues values = new ContentValues();
		values.put(FieldNames[1][1], ledgerInfoCache.getCardNo());
		values.put(FieldNames[1][2], ledgerInfoCache.getDevicesNo());
		values.put(FieldNames[1][3], ledgerInfoCache.getCommissioningDate());
		values.put(FieldNames[1][4], ledgerInfoCache.getManufacturer());
		values.put(FieldNames[1][5], ledgerInfoCache.getRemark());
		values.put(FieldNames[1][6], ledgerInfoCache.getCost());

		return mDb.update(TableNames[1], values, FieldNames[1][0] + "=?", new String[]{ledgerId});
	}

	/**
	 * 新增卡片信息
	 * @param cardInfoCache
	 * @return
	 */
	public long addCardInfo(CardInfo cardInfoCache) {
		ContentValues values = new ContentValues();
		values.put(FieldNames[2][1], cardInfoCache.getFID());
		values.put(FieldNames[2][2], cardInfoCache.getAssetsName());
		values.put(FieldNames[2][3], cardInfoCache.getSpecification());
		values.put(FieldNames[2][4], cardInfoCache.getManufacturer());
		values.put(FieldNames[2][5], cardInfoCache.getCommissioningDate());
		values.put(FieldNames[2][6], cardInfoCache.getPropertyRight());

		return mDb.insert(TableNames[2], null, values);
	}

	/**
	 * 更新卡片信息
	 * @param cardInfoCache
	 * @param cardId
	 * @return
	 */
	public int updateCardInfoByCardId(CardInfo cardInfoCache, String cardId) {
		ContentValues values = new ContentValues();
		values.put(FieldNames[2][1], cardInfoCache.getFID());
		values.put(FieldNames[2][2], cardInfoCache.getAssetsName());
		values.put(FieldNames[2][3], cardInfoCache.getSpecification());
		values.put(FieldNames[2][4], cardInfoCache.getManufacturer());
		values.put(FieldNames[2][5], cardInfoCache.getCommissioningDate());
		values.put(FieldNames[2][6], cardInfoCache.getPropertyRight());

		return mDb.update(TableNames[2], values, FieldNames[2][0] + "=?", new String[]{cardId});
	}

	/**
	 * 更新物资编号
	 * @param strMaterialsNo
	 * @param materialsNo
	 * @return
	 */
	public int updateMaterialsNo(String strMaterialsNo, String materialsNo) {
		ContentValues values = new ContentValues();
		values.put(FieldNames[0][1], strMaterialsNo);
		return mDb.update(TableNames[0], values, FieldNames[0][1] + "=?", new String[]{materialsNo});
	}

	public Cursor findLedgerIdByLedgerInfo(LedgerInfo ledgerInfo) {
		String sql = "SELECT " + FieldNames[1][0] + " FROM " + TableNames[1] + " WHERE " 
				+ FieldNames[1][1] + "=? AND "
				+ FieldNames[1][2] + "=? AND "
				+ FieldNames[1][3] + "=? AND "
				+ FieldNames[1][4] + "=? AND "
				+ FieldNames[1][5] + "=? AND "
				+ FieldNames[1][6] + "=?";
		String[] selectionArgs = {ledgerInfo.getCardNo(),
				ledgerInfo.getDevicesNo(),
				ledgerInfo.getCommissioningDate(),
				ledgerInfo.getManufacturer(),
				ledgerInfo.getRemark(),
				ledgerInfo.getCost()};
		return mDb.rawQuery(sql, selectionArgs);
	}

	public Cursor findCardIdByCardInfo(CardInfo cardInfo) {
		String sql = "SELECT " + FieldNames[2][0] + " FROM " + TableNames[2] + " WHERE " 
				+ FieldNames[2][1] + "=? AND "
				+ FieldNames[2][2] + "=? AND "
				+ FieldNames[2][3] + "=? AND "
				+ FieldNames[2][4] + "=? AND "
				+ FieldNames[2][5] + "=? AND "
				+ FieldNames[2][6] + "=?";
		String[] selectionArgs = {cardInfo.getFID(),
				cardInfo.getAssetsName(),
				cardInfo.getSpecification(),
				cardInfo.getManufacturer(),
				cardInfo.getCommissioningDate(),
				cardInfo.getPropertyRight()};
		return mDb.rawQuery(sql, selectionArgs);
	}

	/**
	 * 新增物资信息
	 * @param materialsInfoCache
	 * @return
	 */
	public long addMaterialsInfo(MaterialsInfo materialsInfoCache) {
		ContentValues values = new ContentValues();
		values.put(FieldNames[0][1], materialsInfoCache.getMaterialsNo());
		values.put(FieldNames[0][2], materialsInfoCache.getLedgerId());
		values.put(FieldNames[0][3], materialsInfoCache.getCardId());
		values.put(FieldNames[0][4], materialsInfoCache.getNote());

		return mDb.insert(TableNames[0], null, values);
	}

	/**
	 * 根据物资编号更新物资信息
	 * @param materialsInfo
	 * @param materialsNo
	 * @return
	 */
	public int updateMaterialsInfoByMaterialsNo(MaterialsInfo materialsInfo, String materialsNo) {
		ContentValues values = new ContentValues();
		values.put(FieldNames[0][1], materialsInfo.getMaterialsNo());
		//values.put(FieldNames[0][2], materialsInfo.getLedgerId());
		//values.put(FieldNames[0][3], materialsInfo.getCardId());
		values.put(FieldNames[0][4], materialsInfo.getNote());
		return mDb.update(TableNames[0], values, FieldNames[0][1] + "=?", new String[]{materialsNo});
	}

	/**
	 * 获取所有物资编码
	 * @return
	 */
	public Cursor getAllMaterialsNo() {
		String sql = "SELECT * FROM " + TableNames[0];
		return mDb.rawQuery(sql, null);
	}

	/**
	 * 根据关键字搜索物资编码
	 * @param keywords
	 * @return
	 */
	public Cursor findMaterialsNoByKeywords(String keywords) {
		String sql = "SELECT * FROM " + TableNames[0] + " WHERE " + FieldNames[0][1] + " LIKE ?";
		String[] selectionArgs = {keywords};
		return mDb.rawQuery(sql, selectionArgs);
	}

	/**
	 * 根据台账Id删除相应的台账信息
	 * @param ledgerId
	 * @return
	 */
	public int deleteLedgerInfoByLedgerId(String ledgerId) {
		return mDb.delete(TableNames[1], FieldNames[1][0] + "=?", new String[]{ledgerId});
	}

	/**
	 * 根据卡片Id删除相应的卡片信息
	 * @param cardId
	 * @return
	 */
	public int deleteCardInfoByCardId(String cardId) {
		return mDb.delete(TableNames[2], FieldNames[2][0] + "=?", new String[]{cardId});
	}

	/**
	 * 根据物资编码删除相应的物资信息
	 * @param materialsNo
	 * @return
	 */
	public int deleteMaterialsInfoByMaterialsNo(String materialsNo) {
		return mDb.delete(TableNames[0], FieldNames[0][1] + "=?", new String[]{materialsNo});
	}


}
