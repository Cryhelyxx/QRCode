package org.gditc.qrcode.app;

import java.util.Calendar;

import org.gditc.qrcode.R;
import org.gditc.qrcode.common.LedgerInfo;
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

public class EditOrAddLedgerInfoActivity extends Activity {

	private static final String TAG = "EditOrAddLedgerInfo";

	private QRCodeDbHelper db = null;
	private Cursor cursor = null;

	// 该Activity处于两情景状态， 插入状态或编辑状态
	private static final int STATE_INSERT = 0;			// 插入状态
	private static final int STATE_EDIT = 1;			// 编辑状态
	// 记录当前情景的状态(插入或编辑)
	private int state;

	private LedgerInfo ledgerInfoCache = null;

	private Button btn_back = null;
	private TextView tv_title = null;
	private Button btn_save = null;

	private EditText et_cardNo = null;
	private EditText et_devicesNo = null;
	private Button btn_commissioningDate = null;
	private EditText et_manufacturer = null;
	private EditText et_remark = null;
	private EditText et_cost = null;
	// 台账信息
	private String ledger_info_cardNo = null; 				// 卡片编号
	private String ledger_info_devicesNo = null; 			// 设备编号
	private String ledger_info_commissioningDate = null; 	// 投运日期
	private String ledger_info_manufacturer = null; 		// 生产厂家
	private String ledger_info_remark = null; 				// 备 注
	private String ledger_info_cost = null; 				// 原 值


	// 年-月-日
	private int mYear;
	private int mMonth;
	private int mDay;
	
	String ledgerId = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);		// 不显示标题
		setContentView(R.layout.activity_edit_add_ledger_info);
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
			tv_title.setText("新增台账信息");
		} else if (action.equals(Intent.ACTION_EDIT)) {
			state = STATE_EDIT;
			tv_title.setText("编辑台账信息");
			// 读取数据库里面的数据
			readDataFromDb(intent);
		} else {
			Log.e(TAG, "未知错误, 程序正在退出...");
			finish();
		}

	}

	/**
	 * 加载组件
	 */
	private void loadingFormation() {
		btn_back = (Button) this.findViewById(R.id.btn_back_ledger_info);
		tv_title = (TextView) this.findViewById(R.id.title_ledger_info);
		btn_save = (Button) this.findViewById(R.id.btn_save_ledger_info);

		et_cardNo = (EditText) this.findViewById(R.id.ledger_info_cardNo);
		et_devicesNo = (EditText) this.findViewById(R.id.ledger_info_devicesNo);
		btn_commissioningDate = (Button) this.findViewById(R.id.ledger_info_commissioningDate);
		et_manufacturer = (EditText) this.findViewById(R.id.ledger_info_manufacturer);
		et_remark = (EditText) this.findViewById(R.id.ledger_info_remark);
		et_cost = (EditText) this.findViewById(R.id.ledger_info_cost);


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
		btn_commissioningDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String commissioningDate = btn_commissioningDate.getText().toString();
				createDatePickerDialog(commissioningDate).show();
			}
		});
	}

	/**
	 * 验证数据， 并插入到数据库
	 */
	protected void verifyData() {
		ledger_info_cardNo = et_cardNo.getText().toString().trim();
		ledger_info_devicesNo = et_devicesNo.getText().toString().trim();
		ledger_info_commissioningDate = btn_commissioningDate.getText().toString();
		ledger_info_manufacturer = et_manufacturer.getText().toString().trim();
		ledger_info_remark = et_remark.getText().toString().trim();
		ledger_info_cost = et_cost.getText().toString().trim();
		// 检验数据的合法性
		if ("".equals(ledger_info_cardNo) || null == ledger_info_cardNo) {
			showToast("卡片编号不能为空");
			return;
		}
		ledgerInfoCache = new LedgerInfo();
		ledgerInfoCache.setCardNo(ledger_info_cardNo);
		ledgerInfoCache.setDevicesNo(ledger_info_devicesNo);
		ledgerInfoCache.setCommissioningDate(ledger_info_commissioningDate);
		ledgerInfoCache.setManufacturer(ledger_info_manufacturer);
		ledgerInfoCache.setRemark(ledger_info_remark);
		ledgerInfoCache.setCost(ledger_info_cost);

		if (state == STATE_INSERT) {
			long count = db.addLedgerInfo(ledgerInfoCache);
			if (count > 0) {
				showToast("新建台账信息成功");
				finish();
			} else {
				showToast("新建台账信息失败");
				finish();
			}
		} else {
			int count = db.updateLedgerInfoByLedgerId(ledgerInfoCache, ledgerId);
			if (count > 0) {
				showToast("台账信息更新成功");
				finish();
			} else {
				showToast("台账信息更新失败");
				finish();
			}
		}
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
		ledgerId = intent.getStringExtra("ledgerId");
		cursor = db.findLedgerInfoById(ledgerId);
		if (cursor.moveToNext()) {
			if (ledgerInfoCache == null) {
				ledger_info_cardNo = cursor.getString(1);
				ledger_info_devicesNo = cursor.getString(2);
				ledger_info_commissioningDate = cursor.getString(3);
				ledger_info_manufacturer = cursor.getString(4);
				ledger_info_remark = cursor.getString(5);
				ledger_info_cost = cursor.getString(6);
				initCommissioningDate(ledger_info_commissioningDate);
			} else {
				ledger_info_cardNo = ledgerInfoCache.getCardNo();
				ledger_info_devicesNo = ledgerInfoCache.getDevicesNo();
				ledger_info_commissioningDate = ledgerInfoCache.getCommissioningDate();
				ledger_info_manufacturer = ledgerInfoCache.getManufacturer();
				ledger_info_remark = ledgerInfoCache.getRemark();
				ledger_info_cost = ledgerInfoCache.getCost();
			}

			if (ledger_info_cardNo != null) {
				et_cardNo.setTextKeepState(ledger_info_cardNo);	// 保持光标原先的位置
			} else {
				et_cardNo.setText(ledger_info_cardNo);			// 光标跑到最后
			}
			if (ledger_info_devicesNo != null) {
				et_devicesNo.setTextKeepState(ledger_info_devicesNo);
			} else {
				et_devicesNo.setText(ledger_info_devicesNo);
			}
			btn_commissioningDate.setText(ledger_info_commissioningDate);
			if (ledger_info_manufacturer != null) {
				et_manufacturer.setTextKeepState(ledger_info_manufacturer);
			} else {
				et_manufacturer.setText(ledger_info_manufacturer);
			}
			if (ledger_info_remark != null) {
				et_remark.setTextKeepState(ledger_info_remark);
			} else {
				et_remark.setText(ledger_info_remark);
			}
			if (ledger_info_cost != null) {
				et_cost.setTextKeepState(ledger_info_cost);
			} else {
				et_cost.setText(ledger_info_cost);
			}
		}
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

	/**
	 * 当应用遇到意外情况（如：内存不足、用户直接按Home键）由系统销毁一个Activity时，onSaveInstanceState() 会被调用.
	 * 通常onSaveInstanceState()只适合用于保存一些临时性的状态
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// 缓存数据
		ledgerInfoCache = new LedgerInfo();
		ledgerInfoCache.setCardNo(et_cardNo.getText().toString().trim());
		ledgerInfoCache.setDevicesNo(et_devicesNo.getText().toString().trim());
		ledgerInfoCache.setCommissioningDate(btn_commissioningDate.getText().toString());
		ledgerInfoCache.setManufacturer(et_manufacturer.getText().toString().trim());
		ledgerInfoCache.setRemark(et_remark.getText().toString().trim());
		ledgerInfoCache.setCost(et_cost.getText().toString().trim());

		outState.putSerializable("originalData", ledgerInfoCache);
	}

	public DatePickerDialog createDatePickerDialog(String commissioningDate) {
		initCommissioningDate(commissioningDate);
		DatePickerDialog datePickerDialog =
				new DatePickerDialog(EditOrAddLedgerInfoActivity.this, new OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,
							int dayOfMonth) {
						String date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
						btn_commissioningDate.setText(date);
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
