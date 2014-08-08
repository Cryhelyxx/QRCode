package org.gditc.qrcode.app;

import org.gditc.qrcode.R;
import org.gditc.qrcode.dao.QRCodeDbHelper;
import org.gditc.qrcode.utils.PrepareDbData;
import org.gditc.qrcode.utils.StackManager;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class LookMaterialsInfoActivity extends Activity {

	private static final String TAG = "LookMaterialsInfo";

	private static final String NO_DATA = "暂无填写";

	private QRCodeDbHelper db = null;
	private Cursor cursor = null;
	
	private Button btn_back = null;

	private TextView  tv_materialsNo= null;
	private TextView  ledgerInfo_cardNo = null;
	private TextView  ledgerInfo_devicesNo = null;
	private TextView  ledgerInfo_commissioningDate = null;
	private TextView  ledgerInfo_manufacturer = null;
	private TextView  ledgerInfo_remark = null;
	private TextView  ledgerInfo_cost = null;

	private TextView  cardInfo_FID = null;
	private TextView  cardInfo_assetsName = null;
	private TextView  cardInfo_specification = null;
	private TextView  cardInfo_manufacturer = null;
	private TextView  cardInfo_commissioningDate = null;
	private TextView  cardInfo_propertyRight = null;

	private TextView tv_note = null;

	private String materialsNo = null;

	//表名
	//private static String TableNames[] = QRCodeDbInfo.getTableNames();
	//字段名
	//private static String FieldNames[][] = QRCodeDbInfo.getFieldNames();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);		// 不显示标题
		setContentView(R.layout.activity_look_materials_info);
		// 方案一：
		//MyApplication.getInstance().addActivity(this);
		// 方案二：
		StackManager.getStackManager().pushActivity(this);

		// 往数据库中装载数据
		PrepareDbData.getInstance(this).initDbData();
		loadingFormation();

		db = QRCodeDbHelper.getInstance(this);
		db.open();

		loadData();		// 装载数据
	}

	/**
	 * 加载组件
	 */
	private void loadingFormation() {
		btn_back = (Button) this.findViewById(R.id.btn_back_materials_info_look);
		
		tv_materialsNo = (TextView) this.findViewById(R.id.materialsNo_look);

		ledgerInfo_cardNo = (TextView) this.findViewById(R.id.cardNo_look);
		ledgerInfo_devicesNo = (TextView) this.findViewById(R.id.devicesNo_look);
		ledgerInfo_commissioningDate = (TextView) this.findViewById(R.id.commissioningDate_look);
		ledgerInfo_manufacturer = (TextView) this.findViewById(R.id.manufacturer_look);
		ledgerInfo_remark = (TextView) this.findViewById(R.id.remark_look);
		ledgerInfo_cost = (TextView) this.findViewById(R.id.cost_look);

		cardInfo_FID = (TextView) this.findViewById(R.id.FID_look);
		cardInfo_assetsName = (TextView) this.findViewById(R.id.assetsName_look);
		cardInfo_specification = (TextView) this.findViewById(R.id.specification_look);
		cardInfo_manufacturer = (TextView) this.findViewById(R.id.manufacturer1_look);
		cardInfo_commissioningDate = (TextView) this.findViewById(R.id.commissioningDate01_look);
		cardInfo_propertyRight = (TextView) this.findViewById(R.id.propertyRight_look);

		tv_note = (TextView) this.findViewById(R.id.notes_look);
		// 给组件设置监听器
		setComponentsListener();
	}

	/**
	 * 扫描二维码后， 进行装载数据
	 */
	private void loadData() {
		Intent intent = getIntent();
		materialsNo = intent.getStringExtra("materialsNo");
		if (materialsNo != null) {
			getBaseInfo(materialsNo);
		} 
	}

	/**
	 * 获取基本信息(备注信息除外)
	 * @param materialsNo
	 */
	private void getBaseInfo(String materialsNo) {
		tv_materialsNo.setText(materialsNo);

		String ledgerId = null;
		String cardId = null;

		cursor = db.findMaterialsInfoByMaterialsNo(materialsNo);
		if (cursor.moveToNext()) {
			ledgerId = cursor.getString(2);
			cardId = cursor.getString(3);
		}
		if (ledgerId != null) {
			cursor = db.findLedgerInfoById(ledgerId);
			if (cursor.moveToNext()) {
				if (cursor.getString(1) != null && !"".equals(cursor.getString(1))) {
					ledgerInfo_cardNo.setText(cursor.getString(1));
				} else {
					ledgerInfo_cardNo.setText(NO_DATA);
				}
				if (cursor.getString(2) != null && !"".equals(cursor.getString(2))) {
					ledgerInfo_devicesNo.setText(cursor.getString(2));
				} else {
					ledgerInfo_devicesNo.setText(NO_DATA);
				}
				if (cursor.getString(3) != null && !"".equals(cursor.getString(3))) {
					ledgerInfo_commissioningDate.setText(cursor.getString(3));
				} else {
					ledgerInfo_commissioningDate.setText(NO_DATA);
				}
				if (cursor.getString(4) != null && !"".equals(cursor.getString(4))) {
					ledgerInfo_manufacturer.setText(cursor.getString(4));
				} else {
					ledgerInfo_manufacturer.setText(NO_DATA);
				}
				if (cursor.getString(5) != null && !"".equals(cursor.getString(5))) {
					ledgerInfo_remark.setText(cursor.getString(5));
				} else {
					ledgerInfo_remark.setText(NO_DATA);
				}
				if (cursor.getString(6) != null && !"".equals(cursor.getString(6))) {
					ledgerInfo_cost.setText(cursor.getString(6));
				} else {
					ledgerInfo_cost.setText(NO_DATA);
				}
			}
		}
		if (cardId != null) {
			cursor = db.findCardInfoById(cardId);
			if (cursor.moveToNext()) {
				if (cursor.getString(1) != null && !"".equals(cursor.getString(1))) {
					cardInfo_FID.setText(cursor.getString(1));
				} else {
					cardInfo_FID.setText(NO_DATA);
				}
				if (cursor.getString(2) != null && !"".equals(cursor.getString(2))) {
					cardInfo_assetsName.setText(cursor.getString(2));
				} else {
					cardInfo_assetsName.setText(NO_DATA);
				}
				if (cursor.getString(3) != null && !"".equals(cursor.getString(3))) {
					cardInfo_specification.setText(cursor.getString(3));
				} else {
					cardInfo_specification.setText(NO_DATA);
				}
				if (cursor.getString(4) != null && !"".equals(cursor.getString(4))) {
					cardInfo_manufacturer.setText(cursor.getString(4));
				} else {
					cardInfo_manufacturer.setText(NO_DATA);
				}
				if (cursor.getString(5) != null && !"".equals(cursor.getString(5))) {
					cardInfo_commissioningDate.setText(cursor.getString(5));
				} else {
					cardInfo_commissioningDate.setText(NO_DATA);
				}
				if (cursor.getString(6) != null && !"".equals(cursor.getString(6))) {
					cardInfo_propertyRight.setText(cursor.getString(6));
				} else {
					cardInfo_propertyRight.setText(NO_DATA);
				}
			}
		}

		cursor = db.getNoteByMaterialsNo(materialsNo);
		String note = null;
		if (cursor.moveToNext()) {
			note = cursor.getString(0);
		}
		if (note != null) {
			Log.i(TAG, note);
			if ("".equals(note)) {
				tv_note.setText(NO_DATA);
			} else {
				tv_note.setText(note);
			}
		} else {
			tv_note.setText(NO_DATA);
		}
	}

	/**
	 * 给组件设置监听器
	 */
	private void setComponentsListener() {
		btn_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	/**
	 * 刷新界面(手动刷新)
	 */
	public void refresh() {
		loadData();
	}

	/**
	 * 自动刷新
	 */
	@Override
	protected void onResume() {
		super.onResume();
		loadData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 关闭游标对象
		if (cursor != null) {
			cursor.close();
		}
		// 关闭数据库对象
		/*if(db != null){
			db.close();
		}*/
	}

}