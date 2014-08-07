package org.gditc.qrcode.app;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.gditc.qrcode.R;
import org.gditc.qrcode.common.MyConstants;
import org.gditc.qrcode.common.QRCodeDbInfo;
import org.gditc.qrcode.dao.QRCodeDbHelper;
import org.gditc.qrcode.utils.FileUtils;
import org.gditc.qrcode.utils.MyApplication;
import org.gditc.qrcode.utils.PrepareDbData;
import org.gditc.qrcode.utils.StackManager;
import org.gditc.qrcode.utils.WriteXLS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String TAG = "QRCode";
	private static final int SCANNIN_GREQUEST_CODE = 1;
	private static final int FILE_SELECT_CODE = 3;

	private static final String EXPORT_XLS_NAME = "materials";


	private static final String TIP_TO_SCAN = "扫一扫";
	private static final String NO_DATA = "暂无填写";
	private static final String IS_NULL = "无";

	private QRCodeDbHelper db = null;
	private Cursor cursor = null;

	private Button btnMore = null;
	private Button btn_scan = null;
	private LinearLayout btn_materialsNo = null;

	private Button btn_edit_ledger_info = null;
	private Button btn_edit_card_info = null;
	private Button btn_edit_note = null;

	/**
	 * 显示扫描结果
	 */
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

	// Excel文件路径
	private String excelFilePath = null;

	//表名
	private static String TableNames[] = QRCodeDbInfo.getTableNames();
	//字段名
	//private static String FieldNames[][] = QRCodeDbInfo.getFieldNames();


	private long firstTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);		// 不显示标题
		setContentView(R.layout.activity_main);
		// 方案一：
		//MyApplication.getInstance().addActivity(this);
		// 方案二：
		StackManager.getStackManager().pushActivity(this);

		// 往数据库中装载数据
		PrepareDbData.getInstance(this).initDbData();
		loadingFormation();

		db = QRCodeDbHelper.getInstance(this);
		db.open();

		loadData();		// 扫描二维码后， 进行装载数据
	}

	/**
	 * 加载组件
	 */
	private void loadingFormation() {
		btnMore = (Button) this.findViewById(R.id.title_bar_btn_more);
		btn_scan = (Button) findViewById(R.id.btn_scan);
		btn_materialsNo = (LinearLayout) this.findViewById(R.id.btn_materialsNo);

		btn_edit_ledger_info = (Button) this.findViewById(R.id.btn_edit_ledger_info);
		btn_edit_card_info = (Button) this.findViewById(R.id.btn_edit_card_info);
		btn_edit_note = (Button) this.findViewById(R.id.btn_edit_note);

		tv_materialsNo = (TextView) this.findViewById(R.id.materialsNo);

		ledgerInfo_cardNo = (TextView) this.findViewById(R.id.cardNo);
		ledgerInfo_devicesNo = (TextView) this.findViewById(R.id.devicesNo);
		ledgerInfo_commissioningDate = (TextView) this.findViewById(R.id.commissioningDate);
		ledgerInfo_manufacturer = (TextView) this.findViewById(R.id.manufacturer);
		ledgerInfo_remark = (TextView) this.findViewById(R.id.remark);
		ledgerInfo_cost = (TextView) this.findViewById(R.id.cost);

		cardInfo_FID = (TextView) this.findViewById(R.id.FID);
		cardInfo_assetsName = (TextView) this.findViewById(R.id.assetsName);
		cardInfo_specification = (TextView) this.findViewById(R.id.specification);
		cardInfo_manufacturer = (TextView) this.findViewById(R.id.manufacturer1);
		cardInfo_commissioningDate = (TextView) this.findViewById(R.id.commissioningDate01);
		cardInfo_propertyRight = (TextView) this.findViewById(R.id.propertyRight);

		tv_note = (TextView) this.findViewById(R.id.notes);
		// 内容过多可滚动
		//ledgerInfo_remark.setMovementMethod(ScrollingMovementMethod.getInstance());
		// 给组件设置监听器
		setComponentsListener();
	}

	/**
	 * 扫描二维码后， 进行装载数据
	 */
	private void loadData() {
		materialsNo = tv_materialsNo.getText().toString();
		if (materialsNo != null && !TIP_TO_SCAN.equals(materialsNo.trim())) {
			getBaseInfo(materialsNo);
		} else {
			Intent intent = getIntent();
			materialsNo = intent.getStringExtra("strMaterialsNo");
			if (materialsNo != null && !TIP_TO_SCAN.equals(materialsNo.trim())) {
				getBaseInfo(materialsNo);
			} 
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
		} else {
			showToast("所扫描的数据不在数据库内， 请重试");

			tv_materialsNo.setText(TIP_TO_SCAN);

			ledgerInfo_cardNo.setText(TIP_TO_SCAN);
			ledgerInfo_devicesNo.setText(TIP_TO_SCAN);
			ledgerInfo_commissioningDate.setText(TIP_TO_SCAN);
			ledgerInfo_manufacturer.setText(TIP_TO_SCAN);
			ledgerInfo_remark.setText(TIP_TO_SCAN);
			ledgerInfo_cost.setText(TIP_TO_SCAN);

			cardInfo_FID.setText(TIP_TO_SCAN);
			cardInfo_assetsName.setText(TIP_TO_SCAN);
			cardInfo_manufacturer.setText(TIP_TO_SCAN);
			cardInfo_commissioningDate.setText(TIP_TO_SCAN);
			cardInfo_propertyRight.setText(TIP_TO_SCAN);

			return;
		}
		if (ledgerId != null) {
			cursor = db.findLedgerInfoById(ledgerId);
			if (cursor.moveToNext()) {
				if (cursor.getString(1) != null && !"".equals(cursor.getString(1))) {
					ledgerInfo_cardNo.setText(cursor.getString(1));
				} else {
					ledgerInfo_cardNo.setText(IS_NULL);
				}
				if (cursor.getString(2) != null && !"".equals(cursor.getString(2))) {
					ledgerInfo_devicesNo.setText(cursor.getString(2));
				} else {
					ledgerInfo_devicesNo.setText(IS_NULL);
				}
				if (cursor.getString(3) != null && !"".equals(cursor.getString(3))) {
					ledgerInfo_commissioningDate.setText(cursor.getString(3));
				} else {
					ledgerInfo_commissioningDate.setText(IS_NULL);
				}
				if (cursor.getString(4) != null && !"".equals(cursor.getString(4))) {
					ledgerInfo_manufacturer.setText(cursor.getString(4));
				} else {
					ledgerInfo_manufacturer.setText(IS_NULL);
				}
				if (cursor.getString(5) != null && !"".equals(cursor.getString(5))) {
					ledgerInfo_remark.setText(cursor.getString(5));
				} else {
					ledgerInfo_remark.setText(IS_NULL);
				}
				if (cursor.getString(6) != null && !"".equals(cursor.getString(6))) {
					ledgerInfo_cost.setText(cursor.getString(6));
				} else {
					ledgerInfo_cost.setText(IS_NULL);
				}
			}
		}
		if (cardId != null) {
			cursor = db.findCardInfoById(cardId);
			if (cursor.moveToNext()) {
				if (cursor.getString(1) != null && !"".equals(cursor.getString(1))) {
					cardInfo_FID.setText(cursor.getString(1));
				} else {
					cardInfo_FID.setText(IS_NULL);
				}
				if (cursor.getString(2) != null && !"".equals(cursor.getString(2))) {
					cardInfo_assetsName.setText(cursor.getString(2));
				} else {
					cardInfo_assetsName.setText(IS_NULL);
				}
				if (cursor.getString(3) != null && !"".equals(cursor.getString(3))) {
					cardInfo_specification.setText(cursor.getString(3));
				} else {
					cardInfo_specification.setText(IS_NULL);
				}
				if (cursor.getString(4) != null && !"".equals(cursor.getString(4))) {
					cardInfo_manufacturer.setText(cursor.getString(4));
				} else {
					cardInfo_manufacturer.setText(IS_NULL);
				}
				if (cursor.getString(5) != null && !"".equals(cursor.getString(5))) {
					cardInfo_commissioningDate.setText(cursor.getString(5));
				} else {
					cardInfo_commissioningDate.setText(IS_NULL);
				}
				if (cursor.getString(6) != null && !"".equals(cursor.getString(6))) {
					cardInfo_propertyRight.setText(cursor.getString(6));
				} else {
					cardInfo_propertyRight.setText(IS_NULL);
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
		btnMore.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 创建PopupMenu对象
				PopupMenu popup = new PopupMenu(MainActivity.this, v);
				// 将R.menu.popup_menu菜单资源加载到popup菜单中
				getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
				// 为popup菜单的菜单项单击事件绑定事件监听器
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){

					@Override
					public boolean onMenuItemClick(MenuItem item){
						switch (item.getItemId()) {
						case R.id.popup_menu_update_database_from_excel:
							showFileChooser();
							break;
						case R.id.popup_menu_export_database_data_to_Excel:
							exportDbDataToXLS();
							break;
						case R.id.popup_menu_about_us:
							createAboutUs().show();
							break;
						case R.id.popup_menu_exit:
							createExit().show();
							break;
						}
						return true;
					}
				});
				popup.show();
			}
		});

		/*
		 * 点击按钮跳转到二维码扫描界面，这里用的是startActivityForResult跳转
		 * 扫描完了之后调到该界面
		 */
		btn_scan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, MipcaActivityCapture.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
				//				showFileChooser();
			}
		});
		btn_materialsNo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				materialsNo = tv_materialsNo.getText().toString().trim();
				if (materialsNo.equals(TIP_TO_SCAN)) {
					showToast("请先扫描二维码获取数据");
					return;
				} else {
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, EditMaterialsNoActivity.class);
					intent.putExtra("materialsNo", materialsNo);
					MainActivity.this.startActivity(intent);
				}
			}
		});
		btn_edit_ledger_info.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				materialsNo = tv_materialsNo.getText().toString().trim();
				if (materialsNo.equals(TIP_TO_SCAN)) {
					showToast("请先扫描二维码获取数据");
					return;
				} else {
					String ledgerId = null;
					cursor = db.findMaterialsInfoByMaterialsNo(materialsNo);
					if (cursor.moveToNext()) {
						ledgerId = cursor.getString(2);
					}
					if (ledgerId != null) {			// 该物资表存在台账信息时
						// 编辑台账信息
						editLedgerInfo(ledgerId);
					} else {						// 该物资表不存在台账信息时
						// 是否新增台账信息对话框
						createAddLedgerInfoDialog().show();
					}
				}
			}
		});
		btn_edit_card_info.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				materialsNo = tv_materialsNo.getText().toString().trim();
				if (materialsNo.equals(TIP_TO_SCAN)) {
					showToast("请先扫描二维码获取数据");
					return;
				} else {
					String cardId = null;
					cursor = db.findMaterialsInfoByMaterialsNo(materialsNo);
					if (cursor.moveToNext()) {
						cardId = cursor.getString(3);
					}
					if (cardId != null) {			// 该物资表存在卡片信息时
						// 编辑卡片信息
						editCardInfo(cardId);
					} else {						// 该物资表不存在卡片信息时
						// 是否新增卡片信息对话框
						createAddCardInfoDialog().show();
					}
				}
			}
		});
		btn_edit_note.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				materialsNo = tv_materialsNo.getText().toString().trim();
				if (materialsNo.equals(TIP_TO_SCAN)) {
					showToast("请先扫描二维码获取数据");
					return;
				}
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, EditNoteActivity.class);
				intent.putExtra("materialsNo", materialsNo);
				MainActivity.this.startActivity(intent);
			}
		});
	}

	/**
	 * 创建是否新增台账信息对话框
	 * @return
	 */
	protected Dialog createAddLedgerInfoDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle("提 示");
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setMessage("该物资表不存在台账信息！\n是否要在该物资表新增台账信息？");
		builder.setPositiveButton("确 定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				addLedgerInfo();
			}
		});
		builder.setNegativeButton("取 消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		return builder.create();
	}
	
	/**
	 * 创建是否新增卡片信息对话框
	 * @return
	 */
	protected Dialog createAddCardInfoDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle("提 示");
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setMessage("该物资表不存在卡片信息！\n是否要在该物资表新增卡片信息？");
		builder.setPositiveButton("确 定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				addLedgerInfo();
			}
		});
		builder.setNegativeButton("取 消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		return builder.create();
	}

	/**
	 * 编辑台账信息
	 * @param ledgerId
	 */
	protected void editLedgerInfo(String ledgerId) {
		Intent intent = new Intent();
		intent.putExtra("ledgerId", ledgerId);
		intent.setAction(Intent.ACTION_EDIT);
		intent.setDataAndType(Uri.parse(MyConstants.CONTENT_URI),
				MyConstants.LEDGER_INFO_CONTENT_TYPE_EDIT);
		startActivity(intent);
	}
	
	/**
	 * 编辑卡片信息
	 * @param cardId
	 */
	protected void editCardInfo(String cardId) {
		Intent intent = new Intent();
		intent.putExtra("cardId", cardId);
		intent.setAction(Intent.ACTION_EDIT);
		intent.setDataAndType(Uri.parse(MyConstants.CONTENT_URI),
				MyConstants.CARD_INFO_CONTENT_TYPE_EDIT);
		startActivity(intent);
	}

	/**
	 * 新增台账信息
	 */
	private void addLedgerInfo() {
		Intent intent = new Intent();
		intent.putExtra("materialsNo", materialsNo);
		intent.setAction(Intent.ACTION_INSERT);
		intent.setDataAndType(Uri.parse(MyConstants.CONTENT_URI),
				MyConstants.CARD_INFO_CONTENT_TYPE_INSERT);
		startActivity(intent);
	}

	/**
	 * 打开文件选择器
	 */
	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		//		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("*/*");
		//		intent.setType("application/vnd.ms-excel");

		try {
			startActivityForResult(Intent.createChooser(intent, "选择Excel文件"), FILE_SELECT_CODE);
		} catch (android.content.ActivityNotFoundException ex) {
			showToast("请先安装文件管理器");
		}
	}

	/**
	 * 选择一个准备好的Excel文件更新数据库数据
	 * @param excelFilePath 
	 */
	private void updateDbDataFromExcel(String excelFilePath) {
		deleteTablesDataInDb();
		// 往数据库中装载数据
		PrepareDbData.getInstance(this).updateDbData(excelFilePath);
		showToast("成功更新数据库数据");
	}

	/**
	 * 导出数据库数据到格式为XLS的Excel文件
	 */
	private void exportDbDataToXLS() {
		String status = Environment.getExternalStorageState();
		// 判断是否有SD卡
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			// SD卡路径
			String sdCardDir = Environment.getExternalStorageDirectory().toString();
			String exportXLSPath = sdCardDir + "/" + "qrcode";
			String fileName = getFileName();
			if (exportXLSPath != null) {
				Log.i(TAG, exportXLSPath + "/" + fileName);
			}
			try {
				WriteXLS.writeDataToXLS(exportXLSPath, fileName);
				Toast.makeText(this, "成功将数据导出到Excel", Toast.LENGTH_SHORT).show();
				showToast("位置：" + exportXLSPath + "/" + fileName);
			} catch (RowsExceededException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			showToast("没有SD卡， 请插入SD卡并重启设备让SD卡生效");
		}

	}

	/**
	 * 生成文件名
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	private String getFileName() {
		Date time = new Date(System.currentTimeMillis());
		DateFormat dateFormat = new SimpleDateFormat("_yyyyMMdd_HHmmss");
		return EXPORT_XLS_NAME + dateFormat.format(time) + ".xls";
	}

	/**
	 * 清空数据库中所有表的数据
	 */
	private void deleteTablesDataInDb() {
		for (int i = 0; i < 3; i++) {
			db.delete(TableNames[i], null, null);
		}

	}

	/**
	 * 显示Toast信息
	 * @param string
	 */
	private void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	/**
	 * 刷新界面(手动刷新)
	 */
	public void refresh() {
		loadData();
	}

	/**
	 * 创建"关于我们"对话框
	 * @return
	 */
	private Dialog createAboutUs() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle("版权所有");
		builder.setMessage("Copyright © 2014 广州荔湾供电局");
		builder.setNegativeButton("关 闭", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		return builder.create();
	}

	/**
	 * 创建"退出程序"对话框
	 * @return
	 */
	private Dialog createExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle("提 示");
		builder.setMessage("确定要退出程序吗？");
		builder.setPositiveButton("确 定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//完全退出程序
				MyApplication.getInstance().exit();
			}
		});
		builder.setNegativeButton("关 闭", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		return builder.create();
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//super.onActivityResult(requestCode, resultCode, data);
		Log.i(TAG, "onActivityResult执行了");
		if (resultCode != RESULT_OK) {
			return;
		}

		Bundle bundle = data.getExtras();
		switch (requestCode) {
		case SCANNIN_GREQUEST_CODE:
			//显示扫描到的物资编号
			materialsNo = bundle.getString("result");
			tv_materialsNo.setText(materialsNo);
			break;
		case FILE_SELECT_CODE:
			// Get the Uri of the selected file 
			Uri uri = data.getData();
			excelFilePath = FileUtils.getPath(MainActivity.this, uri);
			String fileType =  excelFilePath.substring(excelFilePath.lastIndexOf('.') + 1);
			if (!"xls".equals(fileType)) {
				showToast("格式错误， 请选择格式为xls的Excel文件");
				return;
			} else {
				updateDbDataFromExcel(excelFilePath);
				if (excelFilePath != null) {
					Log.i(TAG, excelFilePath);
				}
			}

			break;
		}
	}

	/**
	 * 任何的按键都是 onKeyDown() 先接收的
	 * 如果按的是 menu 键，应该返回 false ，表示让后面需要接收 menu 键的事件继续处理。
	 * 返回 true 就表示这个事件到我这里就完结了，返回false表示继续传递给后面想要接收这个事件的地方
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch(keyCode) {  
		case KeyEvent.KEYCODE_MENU: 
			return false;
		case KeyEvent.KEYCODE_BACK:  
			long secondTime = System.currentTimeMillis(); 
			// 如果弹出的窗口还显示， 则将其关闭
			if (secondTime - firstTime > 2000) {   //如果两次按键时间间隔大于2秒，则不退出  
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();   
				firstTime = secondTime;//更新firstTime  
				return true;   
			} else {
				//完全退出程序
				// 方案一：
				//MyApplication.getInstance().exit();
				// 方案二：
				StackManager.getStackManager().popAllActivitys();
			}   
			break;  
		}

		return super.onKeyDown(keyCode, event); 
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 关闭游标对象
		if (cursor != null) {
			cursor.close();
		}
		// 关闭数据库对象
		if(db != null){
			db.close();
		}
	}

	/*@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);


	    // Checks the orientation of the screen
	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	        Log.i(TAG, "横屏模式");
	        loadData();
	    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
	    	Log.i(TAG, "竖屏模式");
	    	loadData();
	    }
	}*/

}