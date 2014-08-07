package org.gditc.qrcode.app;

import java.util.Calendar;

import org.gditc.qrcode.R;
import org.gditc.qrcode.common.CardInfo;
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

public class EditOrAddCardInfoActivity extends Activity {

	private static final String TAG = "EditOrAddCardInfo";

	private QRCodeDbHelper db = null;
	private Cursor cursor = null;

	// 该Activity处于两情景状态， 插入状态或编辑状态
	private static final int STATE_INSERT = 0;			// 插入状态
	private static final int STATE_EDIT = 1;			// 编辑状态
	// 记录当前情景的状态(插入或编辑)
	private int state;

	private CardInfo cardInfoCache = null;

	private Button btn_back = null;
	private TextView tv_title = null;
	private Button btn_save = null;

	private EditText et_FID = null;
	private EditText et_assetsName = null;
	private EditText et_specification = null;
	private EditText et_manufacturer = null;
	private Button btn_commissioningDate = null;
	private EditText et_propertyRight = null;

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

	private String cardId = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);		// 不显示标题
		setContentView(R.layout.activity_edit_add_card_info);
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
			tv_title.setText("新增卡片信息");
		} else if (action.equals(Intent.ACTION_EDIT)) {
			state = STATE_EDIT;
			tv_title.setText("编辑卡片信息");
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
		btn_back = (Button) this.findViewById(R.id.btn_back_card_info);
		tv_title = (TextView) this.findViewById(R.id.title_card_info);
		btn_save = (Button) this.findViewById(R.id.btn_save_card_info);

		et_FID = (EditText) this.findViewById(R.id.card_info_FID);
		et_assetsName = (EditText) this.findViewById(R.id.card_info_assetsName);
		et_specification = (EditText) this.findViewById(R.id.card_info_specification);
		et_manufacturer = (EditText) this.findViewById(R.id.card_info_manufacturer);
		btn_commissioningDate = (Button) this.findViewById(R.id.card_info_commissioningDate);
		et_propertyRight = (EditText) this.findViewById(R.id.card_info_propertyRight);

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
		card_info_FID = et_FID.getText().toString().trim();
		card_info_assetsName = et_assetsName.getText().toString().trim();
		card_info_specification = et_specification.getText().toString().trim();
		card_info_manufacturer = et_manufacturer.getText().toString().trim();
		card_info_commissioningDate = btn_commissioningDate.getText().toString();
		card_info_propertyRight = et_propertyRight.getText().toString().trim();
		// 检验数据的合法性
		if ("".equals(card_info_FID) || null == card_info_FID) {
			showToast("FID不能为空");
			return;
		}
		cardInfoCache = new CardInfo();
		cardInfoCache.setFID(card_info_FID);
		cardInfoCache.setAssetsName(card_info_assetsName);
		cardInfoCache.setSpecification(card_info_specification);
		cardInfoCache.setManufacturer(card_info_manufacturer);
		cardInfoCache.setCommissioningDate(card_info_commissioningDate);
		cardInfoCache.setPropertyRight(card_info_propertyRight);
		
		if (state == STATE_INSERT) {
			long count = db.addCardInfo(cardInfoCache);
			if (count > 0) {
				showToast("新建卡片信息成功");
				finish();
			} else {
				showToast("新建卡片信息失败");
				finish();
			}
		} else {
			int count = db.updateCardInfo(cardInfoCache, cardId);
			if (count > 0) {
				showToast("卡片信息更新成功");
				finish();
			} else {
				showToast("卡片信息更新失败");
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
		cardId = intent.getStringExtra("cardId");
		cursor = db.findCardInfoById(cardId);
		if (cursor.moveToNext()) {
			if (cardInfoCache == null) {
				card_info_FID = cursor.getString(1);
				card_info_assetsName = cursor.getString(2);
				card_info_specification = cursor.getString(3);
				card_info_manufacturer = cursor.getString(4);
				card_info_commissioningDate = cursor.getString(5);
				card_info_propertyRight = cursor.getString(6);
			} else {
				card_info_FID = cardInfoCache.getFID();
				card_info_assetsName = cardInfoCache.getAssetsName();
				card_info_specification = cardInfoCache.getSpecification();
				card_info_manufacturer = cardInfoCache.getManufacturer();
				card_info_commissioningDate = cardInfoCache.getCommissioningDate();
				card_info_propertyRight = cardInfoCache.getPropertyRight();
			}

			if (card_info_FID != null) {
				et_FID.setTextKeepState(card_info_FID);	// 保持光标原先的位置
			} else {
				et_FID.setText(card_info_FID);			// 光标跑到最后
			}
			if (card_info_assetsName != null) {
				et_assetsName.setTextKeepState(card_info_assetsName);
			} else {
				et_assetsName.setText(card_info_assetsName);
			}
			if (card_info_specification != null) {
				et_specification.setTextKeepState(card_info_specification);
			} else {
				et_specification.setText(card_info_specification);
			}
			if (card_info_manufacturer != null) {
				et_manufacturer.setTextKeepState(card_info_manufacturer);
			} else {
				et_manufacturer.setText(card_info_manufacturer);
			}
			btn_commissioningDate.setText(card_info_commissioningDate);
			if (card_info_propertyRight != null) {
				et_propertyRight.setTextKeepState(card_info_propertyRight);
			} else {
				et_propertyRight.setText(card_info_propertyRight);
			}
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
		cardInfoCache = new CardInfo();
		cardInfoCache.setFID(et_FID.getText().toString().trim());
		cardInfoCache.setAssetsName(et_assetsName.getText().toString().trim());
		cardInfoCache.setSpecification(et_specification.getText().toString().trim());
		cardInfoCache.setManufacturer(et_manufacturer.getText().toString().trim());
		cardInfoCache.setCommissioningDate(btn_commissioningDate.getText().toString());
		cardInfoCache.setPropertyRight(et_propertyRight.getText().toString().trim());

		outState.putSerializable("originalData", cardInfoCache);
	}

	/**
	 * 日期选择器对话框
	 * @param commissioningDate
	 * @return
	 */
	public DatePickerDialog createDatePickerDialog(String commissioningDate) {
		initCommissioningDate(commissioningDate);
		DatePickerDialog datePickerDialog =
				new DatePickerDialog(EditOrAddCardInfoActivity.this, new OnDateSetListener() {

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
