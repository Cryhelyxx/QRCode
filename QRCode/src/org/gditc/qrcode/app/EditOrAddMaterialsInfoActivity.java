package org.gditc.qrcode.app;

import java.util.Calendar;

import org.gditc.qrcode.R;
import org.gditc.qrcode.common.MaterialsInfo;
import org.gditc.qrcode.dao.QRCodeDbHelper;
import org.gditc.qrcode.utils.StackManager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditOrAddMaterialsInfoActivity extends Activity {

	private static final String TAG = "EditOrAddMaterialsInfo";

	private QRCodeDbHelper db = null;
	private Cursor cursor = null;

	// 该Activity处于两情景状态， 插入状态或编辑状态
	private static final int STATE_INSERT = 0;			// 插入状态
	private static final int STATE_EDIT = 1;			// 编辑状态
	// 记录当前情景的状态(插入或编辑)
	private int state;

	private MaterialsInfo materialsInfoCache = null;

	private Button btn_back = null;
	private TextView tv_title = null;
	private Button btn_save = null;

	private EditText et_materials_info_materialsNo = null;
	private EditText et_materials_info_note = null;

	private EditText et_ledger_info_cardNo = null;
	private EditText et_ledger_info_devicesNo = null;
	private Button btn_ledger_info_commissioningDate = null;
	private EditText et_ledger_info_manufacturer = null;
	private EditText et_ledger_info_remark = null;
	private EditText et_ledger_info_cost = null;

	private EditText et_card_info_FID = null;
	private EditText et_card_info_assetsName = null;
	private EditText et_card_info_specification = null;
	private EditText et_card_info_manufacturer = null;
	private Button btn_card_info_commissioningDate = null;
	private EditText et_card_info_propertyRight = null;

	//---------物资信息-------------------------
	private String materials_info_materialsNo = null;		// 物资编号
	private String materials_info_note = null;				// 物资信息的备注
	// 台账信息
	private String ledger_info_cardNo = null; 				// 卡片编号
	private String ledger_info_devicesNo = null; 			// 设备编号
	private String ledger_info_commissioningDate = null; 	// 投运日期
	private String ledger_info_manufacturer = null; 		// 生产厂家
	private String ledger_info_remark = null; 				// 备 注
	private String ledger_info_cost = null; 				// 原 值

	// 卡片信息
	private String card_info_FID = null; 					// FID
	private String card_info_assetsName = null; 			// 资产型号
	private String card_info_specification = null; 			// 规格型号
	private String card_info_manufacturer = null; 			// 制造商
	private String card_info_commissioningDate = null; 		// 投运日期
	private String card_info_propertyRight = null; 			// 产 权

	// 年-月-日
	private int mYear;
	private int mMonth;
	private int mDay;

	//private String cardId = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);		// 不显示标题
		setContentView(R.layout.activity_edit_add_materials_info);
		// 方案一：
		//MyApplication.getInstance().addActivity(this);
		// 方案二：
		StackManager.getStackManager().pushActivity(this);
		loadingFormation();
		initData();

		db = QRCodeDbHelper.getInstance(this);
		db.open();

		Intent intent = getIntent();
		String action = intent.getAction();

		if (action.equals(Intent.ACTION_INSERT)) {
			state = STATE_INSERT;
			tv_title.setText("新增物资信息");
			String date = mYear + "-" + mMonth + "-" + mDay;
			btn_ledger_info_commissioningDate.setText(date);
			btn_card_info_commissioningDate.setText(date);
		} else if (action.equals(Intent.ACTION_EDIT)) {
			state = STATE_EDIT;
			tv_title.setText("编辑物资信息");
			// 读取数据库里面的数据
			//readDataFromDb(intent);
		} else {
			Log.e(TAG, "未知错误, 程序正在退出...");
			finish();
		}



	}

	/**
	 * 加载组件
	 */
	private void loadingFormation() {
		btn_back = (Button) this.findViewById(R.id.btn_back_materials_info);
		tv_title = (TextView) this.findViewById(R.id.title_materials_info);
		btn_save = (Button) this.findViewById(R.id.btn_save_materials_info);

		et_materials_info_materialsNo = (EditText) this.findViewById(R.id.materials_info_materialsNo);
		et_materials_info_note = (EditText) this.findViewById(R.id.materials_info_note);

		et_ledger_info_cardNo = (EditText) this.findViewById(R.id.materials_info_ledger_info_cardNo);
		et_ledger_info_devicesNo = (EditText) this.findViewById(R.id.materials_info_ledger_info_devicesNo);
		btn_ledger_info_commissioningDate = (Button) this.findViewById(R.id.materials_info_ledger_info_commissioningDate);
		et_ledger_info_manufacturer = (EditText) this.findViewById(R.id.materials_info_ledger_info_manufacturer);
		et_ledger_info_remark = (EditText) this.findViewById(R.id.materials_info_ledger_info_remark);
		et_ledger_info_cost = (EditText) this.findViewById(R.id.materials_info_ledger_info_cost);

		et_card_info_FID = (EditText) this.findViewById(R.id.materials_info_card_info_FID);
		et_card_info_assetsName = (EditText) this.findViewById(R.id.materials_info_card_info_assetsName);
		et_card_info_specification = (EditText) this.findViewById(R.id.materials_info_card_info_specification);
		et_card_info_manufacturer = (EditText) this.findViewById(R.id.materials_info_card_info_manufacturer);
		btn_card_info_commissioningDate = (Button) this.findViewById(R.id.materials_info_card_info_commissioningDate);
		et_card_info_propertyRight = (EditText) this.findViewById(R.id.materials_info_card_info_propertyRight);

		// 给组件设置监听器
		setComponentsListener();
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
		btn_save.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				verifyData();
			}
		});
		btn_ledger_info_commissioningDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String commissioningDate = btn_card_info_commissioningDate.getText().toString();
				createDatePickerDialog(v, commissioningDate).show();
			}
		});
		btn_card_info_commissioningDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String commissioningDate = btn_card_info_commissioningDate.getText().toString();
				createDatePickerDialog(v, commissioningDate).show();
			}
		});
	}



	/**
	 * 验证数据， 并插入到数据库
	 */
	private void verifyData() {
		ledger_info_cardNo = et_ledger_info_cardNo.getText().toString().trim();
		ledger_info_devicesNo = et_ledger_info_devicesNo.getText().toString().trim();
		ledger_info_commissioningDate = btn_ledger_info_commissioningDate.getText().toString();
		ledger_info_manufacturer = et_ledger_info_manufacturer.getText().toString().trim();
		ledger_info_remark = et_ledger_info_remark.getText().toString().trim();
		ledger_info_cost = et_ledger_info_cost.getText().toString().trim();
		// 检验数据的合法性
		/*if ("".equals(ledger_info_cardNo) || null == ledger_info_cardNo) {
			showToast("卡片编号不能为空");
			return;
		}*/
		materialsInfoCache = new MaterialsInfo();
		materialsInfoCache.getLedgerInfo().setCardNo(ledger_info_cardNo);
		materialsInfoCache.getLedgerInfo().setDevicesNo(ledger_info_devicesNo);
		materialsInfoCache.getLedgerInfo().setCommissioningDate(ledger_info_commissioningDate);
		materialsInfoCache.getLedgerInfo().setManufacturer(ledger_info_manufacturer);
		materialsInfoCache.getLedgerInfo().setRemark(ledger_info_remark);
		materialsInfoCache.getLedgerInfo().setCost(ledger_info_cost);

		card_info_FID = et_card_info_FID.getText().toString().trim();
		card_info_assetsName = et_card_info_assetsName.getText().toString().trim();
		card_info_specification = et_card_info_specification.getText().toString().trim();
		card_info_manufacturer = et_card_info_manufacturer.getText().toString().trim();
		card_info_commissioningDate = btn_card_info_commissioningDate.getText().toString();
		card_info_propertyRight = et_card_info_propertyRight.getText().toString().trim();
		// 检验数据的合法性
		/*if ("".equals(card_info_FID) || null == card_info_FID) {
			showToast("FID不能为空");
			return;
		}*/
		materialsInfoCache.getCardInfo().setFID(card_info_FID);
		materialsInfoCache.getCardInfo().setAssetsName(card_info_assetsName);
		materialsInfoCache.getCardInfo().setSpecification(card_info_specification);
		materialsInfoCache.getCardInfo().setManufacturer(card_info_manufacturer);
		materialsInfoCache.getCardInfo().setCommissioningDate(card_info_commissioningDate);
		materialsInfoCache.getCardInfo().setPropertyRight(card_info_propertyRight);

		if (state == STATE_INSERT) {
			db.addLedgerInfo(materialsInfoCache.getLedgerInfo());
			db.addCardInfo(materialsInfoCache.getCardInfo());

			String ledgerId = null;
			String cardId = null;
			
			cursor = db.findLedgerIdByLedgerInfo(materialsInfoCache.getLedgerInfo());
			if (cursor.moveToNext()) {
				ledgerId = cursor.getString(0);
			}
			cursor = db.findCardIdByCardInfo(materialsInfoCache.getCardInfo());
			if (cursor.moveToNext()) {
				cardId = cursor.getString(0);
			}
			materials_info_materialsNo = et_materials_info_materialsNo.getText().toString().trim();
			materials_info_note = et_materials_info_note.getText().toString();
			// 检验数据的合法性
			if ("".equals(materials_info_materialsNo) || null == materials_info_materialsNo) {
				showToast("物资编号不能为空");
				return;
			}
			
			materialsInfoCache = new MaterialsInfo();
			materialsInfoCache.setMaterialsNo(materials_info_materialsNo);
			materialsInfoCache.setNote(materials_info_note);
			materialsInfoCache.setLedgerId(ledgerId);
			materialsInfoCache.setCardId(cardId);
			
			long count = db.addMaterialsInfo(materialsInfoCache);
			if (count > 0) {
				showToast("新建物资信息成功");
				goToLookMaterialsInfo(materials_info_materialsNo);
				finish();
			} else {
				showToast("新建物资信息失败");
				finish();
			}
		} else {
			/*int count = db.updateCardInfo(cardInfoCache, cardId);
			if (count > 0) {
				showToast("卡片信息更新成功");
				finish();
			} else {
				showToast("卡片信息更新失败");
				finish();
			}*/
		}
	}

	/**
	 * 跳转到查看物资信息Activity
	 * @param materialsNo
	 */
	private void goToLookMaterialsInfo(String materialsNo) {
		Intent intent = new Intent();
		intent.putExtra("materialsNo", materialsNo);
		intent.setClass(this, LookMaterialsInfoActivity.class);
		startActivity(intent);
	}

	/**
	 * 显示Toast信息
	 * @param string
	 */
	public void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		getCurrentDate();
	}

	/**
	 * 读取数据库里面的数据
	 * @param intent 
	 */
	private void readDataFromDb(Intent intent) {

	}

	/**
	 * 当应用遇到意外情况（如：内存不足、用户直接按Home键）由系统销毁一个Activity时，onSaveInstanceState() 会被调用.
	 * 通常onSaveInstanceState()只适合用于保存一些临时性的状态
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// 缓存数据

		outState.putSerializable("originalData", materialsInfoCache);
	}

	/**
	 * 日期选择器对话框
	 * @param v 
	 * @param commissioningDate
	 * @return
	 */
	public DatePickerDialog createDatePickerDialog(final View v, String commissioningDate) {
		initCommissioningDate(commissioningDate);
		DatePickerDialog datePickerDialog =
				new DatePickerDialog(EditOrAddMaterialsInfoActivity.this, new OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,
							int dayOfMonth) {
						String date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
						((Button) v).setText(date);
					}
				}, mYear, mMonth - 1, mDay);
		return datePickerDialog;
	}

	/**
	 * 获取当前年月日
	 */
	private void getCurrentDate() {
		Calendar time = Calendar.getInstance();
		mYear = time.get(Calendar.YEAR);
		mMonth = time.get(Calendar.MONTH) + 1;
		mDay = time.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 初始化投运日期
	 * @param strCommissioningDate
	 */
	private void initCommissioningDate(String strCommissioningDate) {
		if (strCommissioningDate != null) {
			String args[] = strCommissioningDate.split("-");

			mYear = Integer.valueOf(args[0]);
			mMonth = Integer.valueOf(args[1]);
			mDay = Integer.valueOf(args[2]);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 关闭游标对象
		if (cursor != null) {
			cursor.close();
		}
		/*// 关闭数据库对象
		if(db != null){
			db.close();
		}*/
	}


}
