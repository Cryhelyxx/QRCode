package org.gditc.qrcode.app;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.gditc.qrcode.R;
import org.gditc.qrcode.common.MyConstants;
import org.gditc.qrcode.utils.HttpUtil;
import org.gditc.qrcode.utils.MyApplication;
import org.gditc.qrcode.utils.StackManager;
import org.gditc.qrcode.utils.Util;
import org.gditc.qrcode.utils.WriteXLS;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class MainActivity extends SlidingFragmentActivity {

	private static final String TAG = "QRCode";
	private static final int SCANNIN_GREQUEST_CODE = 1;
	private static final int FILE_SELECT_CODE = 3;
	
	private static final String CONNECTION_ERROR = "网络异常， 未能连接服务器";

	private ProgressDialog pDialog;

	private static final String EXPORT_XLS_NAME = "materials";


	private static final String TIP_TO_SCAN = "扫一扫";
	//private static final String NO_DATA = "暂无填写";
	private static final String IS_NULL = "无";


	private Button btn_scan = null;
	private BootstrapButton btn_edit_materials_info = null;
	//private LinearLayout btn_materialsNo = null;

	private Long MI_ID;								//主键
	private String MI_MATERIALS_NO;					//物资编号
	private String MI_LEDGER_CARD_NO;				//卡片编号
	private String MI_LEDGER_DEVICES_NO;			//设备型号
	private String MI_LEDGER_COMMISSIONING_DATE;	//投运日期
	private String MI_LEDGER_MANUFACTURER;			//生产厂家
	private String MI_LEDGER_REMARK;				//备注
	private String MI_LEDGER_COST;					//原值
	private String MI_CARD_FID;						//GIS系统G3E_FID
	private String MI_CARD_ASSETSNAME;				//资产名称
	private String MI_CARD_SPECIFICATION;			//规格型号
	private String MI_CARD_MANUFACTURER;			//制造商
	private String MI_CARD_COMMISSIONING_DATE;		//投运日期
	private String MI_CARD_PROPERTYRIGHT;			//产权
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

	//private TextView tv_note = null;

	private String materialsNo = null;

	private String username = null;
	private String nickName = null;
	private String password = null;

	// Excel文件路径
	//private String excelFilePath = null;

	//表名
	//private static String TableNames[] = QRCodeDbInfo.getTableNames();
	//字段名
	//private static String FieldNames[][] = QRCodeDbInfo.getFieldNames();


	private long firstTime = 0;

	private SlidingFragment sf = null;

	private SharedPreferences mPerPreferences = null;
	private String ip_port = null;
	private String Url = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 方案一：
		//MyApplication.getInstance().addActivity(this);
		// 方案二：
		StackManager.getStackManager().pushActivity(this);
		loadingFormation();

		Url = "http://" + getIPAndPort() + MyConstants.SEARCH_DATA_API;

		loadData();		// 扫描二维码后， 进行装载数据

		Intent intent = getIntent();
		username = intent.getStringExtra("username");
		nickName = intent.getStringExtra("nickName");
		password = intent.getStringExtra("password");
		if (savedInstanceState == null) {//== null的时候新建Fragment1  
			sf = new SlidingFragment(username, nickName, password, getIPAndPort());
		} else {//不等于null，直接get出来  
			//不等于null，找出之前保存的当前Activity显示的Fragment  
			sf = (SlidingFragment) this.getSupportFragmentManager().getFragment(savedInstanceState, "contentFragment");  
		}  

		setBehindContentView(R.layout.content_frame);		//设置SlidingMenu使用的布局，同样是一个全屏的FrameLayout
		FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();  
		t.replace(R.id.content_frame, sf);  
		t.commit();  

		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);		//设置阴影的图片资源
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		//sm.setBehindOffset(300);		//设置SlidingMenu打开后，右边留下的宽度, 与上句等效
		sm.setFadeDegree(0.35f);
		//sm.setBehindWidth(700);	//设置菜单的宽
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);		//设置滑动的区域
		//sm.setMode(SlidingMenu.LEFT);		// 滑动方向(LEFT,RIGHT,LEFT_RIGHT)

		getSupportActionBar().setHomeButtonEnabled(true);//actionbar主按键可以被点击
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);//显示向左的图标
		setSlidingActionBarEnabled(true);//左右两侧slidingmenu的fragment是否显示标题栏
	}

	/**
	 * 加载组件
	 */
	private void loadingFormation() {
		btn_scan = (Button) this.findViewById(R.id.btn_scan);
		btn_edit_materials_info = (BootstrapButton) this.findViewById(R.id.btn_edit_materials_info);

		/*btn_edit_ledger_info = (Button) this.findViewById(R.id.btn_edit_ledger_info);
		btn_edit_card_info = (Button) this.findViewById(R.id.btn_edit_card_info);
		btn_edit_note = (Button) this.findViewById(R.id.btn_edit_note);*/

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

		//tv_note = (TextView) this.findViewById(R.id.notes);
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
			//getBaseInfo(materialsNo);
			if (Util.isNetworkConnected(MainActivity.this)) {
				scanQRCode(materialsNo);
			} else {
				showToastShort("无网络连接");
				return;
			}
		} else {
			Intent intent = getIntent();
			materialsNo = intent.getStringExtra("strMaterialsNo");
			if (materialsNo != null && !TIP_TO_SCAN.equals(materialsNo.trim())) {
				//getBaseInfo(materialsNo);
				
				if (Util.isNetworkConnected(MainActivity.this)) {
					scanQRCode(materialsNo);
				} else {
					showToastShort("无网络连接");
					return;
				}
			} 
		}
	}

	/**
	 * 二维码扫描
	 * @param materialsNo 
	 */
	public void scanQRCode(final String materialsNo){
		pDialog = ProgressDialog.show(this, "请稍等", "正在提交数据...");
		RequestParams params = new RequestParams(); // 绑定参数
		params.add("MI_MATERIALS_NO", materialsNo);
		HttpUtil.post(Url, params , new JsonHttpResponseHandler(){

			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				Log.i(TAG, throwable.toString());
				showToastShort(CONNECTION_ERROR);
				pDialog.dismiss();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONArray errorResponse) {
				Log.i(TAG, throwable.toString());
				showToastShort(CONNECTION_ERROR);
				pDialog.dismiss();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				Log.i(TAG, throwable.toString());
				showToastShort(CONNECTION_ERROR);
				pDialog.dismiss();
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				if (statusCode == HttpStatus.SC_OK) {
					JSONObject jsonObj = null;
					try {
						if (response.getInt("code") == 1){
							pDialog.dismiss();
							showToastShort(response.getString("msg"));

							jsonObj = (JSONObject) response.get("obj");

							MI_ID = jsonObj.getLong("MI_ID");

							MI_MATERIALS_NO = jsonObj.getString("MI_MATERIALS_NO");

							MI_LEDGER_CARD_NO = jsonObj.getString("MI_LEDGER_CARD_NO");
							MI_LEDGER_DEVICES_NO = jsonObj.getString("MI_LEDGER_DEVICES_NO");
							MI_LEDGER_COMMISSIONING_DATE = jsonObj.getString("MI_LEDGER_COMMISSIONING_DATE");
							MI_LEDGER_MANUFACTURER = jsonObj.getString("MI_LEDGER_MANUFACTURER");
							MI_LEDGER_REMARK = jsonObj.getString("MI_LEDGER_REMARK");
							MI_LEDGER_COST = jsonObj.getString("MI_LEDGER_COST");

							MI_CARD_FID = jsonObj.getString("MI_CARD_FID");
							MI_CARD_ASSETSNAME = jsonObj.getString("MI_CARD_ASSETSNAME");
							MI_CARD_SPECIFICATION = jsonObj.getString("MI_CARD_SPECIFICATION");
							MI_CARD_MANUFACTURER = jsonObj.getString("MI_CARD_MANUFACTURER");
							MI_CARD_COMMISSIONING_DATE = jsonObj.getString("MI_CARD_COMMISSIONING_DATE");
							MI_CARD_PROPERTYRIGHT = jsonObj.getString("MI_CARD_PROPERTYRIGHT");

							/*物资编码===================*/
							tv_materialsNo.setText(MI_MATERIALS_NO);

							/*台账信息===================*/
							if (MI_LEDGER_CARD_NO != null) {
								ledgerInfo_cardNo.setText(MI_LEDGER_CARD_NO);
							} else {
								ledgerInfo_cardNo.setText(IS_NULL);
							}
							if (MI_LEDGER_DEVICES_NO != null) {
								ledgerInfo_devicesNo.setText(MI_LEDGER_DEVICES_NO);
							} else {
								ledgerInfo_devicesNo.setText(IS_NULL);
							}
							if (MI_LEDGER_COMMISSIONING_DATE != null) {
								ledgerInfo_commissioningDate.setText(MI_LEDGER_COMMISSIONING_DATE);
							} else {
								ledgerInfo_commissioningDate.setText(IS_NULL);
							}
							if (MI_LEDGER_MANUFACTURER != null) {
								ledgerInfo_manufacturer.setText(MI_LEDGER_MANUFACTURER);
							} else {
								ledgerInfo_manufacturer.setText(IS_NULL);
							}
							if (MI_LEDGER_REMARK != null) {
								ledgerInfo_remark.setText(MI_LEDGER_REMARK);
							} else {
								ledgerInfo_remark.setText(IS_NULL);
							}
							if (MI_LEDGER_COST != null) {
								ledgerInfo_cost.setText(MI_LEDGER_COST);
							} else {
								ledgerInfo_cost.setText(IS_NULL);
							}
							/*卡片信息===================*/
							if (MI_CARD_FID != null) {
								cardInfo_FID.setText(MI_CARD_FID);
							} else {
								cardInfo_FID.setText(IS_NULL);
							}
							if (MI_CARD_ASSETSNAME != null) {
								cardInfo_assetsName.setText(MI_CARD_ASSETSNAME);
							} else {
								cardInfo_assetsName.setText(IS_NULL);
							}
							if (MI_CARD_SPECIFICATION != null) {
								cardInfo_specification.setText(MI_CARD_SPECIFICATION);
							} else {
								cardInfo_specification.setText(IS_NULL);
							}
							if (MI_CARD_MANUFACTURER != null) {
								cardInfo_manufacturer.setText(MI_CARD_MANUFACTURER);
							} else {
								cardInfo_manufacturer.setText(IS_NULL);
							}
							if (MI_CARD_COMMISSIONING_DATE != null) {
								cardInfo_commissioningDate.setText(MI_CARD_COMMISSIONING_DATE);
							} else {
								cardInfo_commissioningDate.setText(IS_NULL);
							}
							if (MI_CARD_PROPERTYRIGHT != null) {
								cardInfo_propertyRight.setText(MI_CARD_PROPERTYRIGHT);
							} else {
								cardInfo_propertyRight.setText(IS_NULL);
							}
						} else if (response.getInt("code") == 0) {
							pDialog.dismiss();
							showToastShort(response.getString("msg") + "\n物资编码[" + materialsNo + "]找不到");

							tv_materialsNo.setText(TIP_TO_SCAN);

							ledgerInfo_cardNo.setText(TIP_TO_SCAN);
							ledgerInfo_devicesNo.setText(TIP_TO_SCAN);
							ledgerInfo_commissioningDate.setText(TIP_TO_SCAN);
							ledgerInfo_manufacturer.setText(TIP_TO_SCAN);
							ledgerInfo_remark.setText(TIP_TO_SCAN);
							ledgerInfo_cost.setText(TIP_TO_SCAN);

							cardInfo_FID.setText(TIP_TO_SCAN);
							cardInfo_assetsName.setText(TIP_TO_SCAN);
							cardInfo_specification.setText(TIP_TO_SCAN);
							cardInfo_manufacturer.setText(TIP_TO_SCAN);
							cardInfo_commissioningDate.setText(TIP_TO_SCAN);
							cardInfo_propertyRight.setText(TIP_TO_SCAN);

							return;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	/**
	 * 给组件设置监听器
	 */
	private void setComponentsListener() {
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
			}
		});
		btn_edit_materials_info.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				materialsNo = tv_materialsNo.getText().toString().trim();
				if (materialsNo.equals(TIP_TO_SCAN)) {
					showToastLong("请先扫描二维码获取数据");
					return;
				} else {
					editMaterialsInfo();
				}
			}
		});
	}

	/**
	 * 编辑物资信息
	 */
	private void editMaterialsInfo() {
		Intent intent = new Intent();
		intent.putExtra("materialsNo", materialsNo);
		intent.setAction(Intent.ACTION_EDIT);
		intent.setDataAndType(Uri.parse(MyConstants.CONTENT_URI),
				MyConstants.MATERIALS_INFO_CONTENT_TYPE_EDIT);
		startActivity(intent);
	}

	/**
	 * 添加物资信息
	 */
	private void addMaterialsInfo() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_INSERT);
		intent.setDataAndType(Uri.parse(MyConstants.CONTENT_URI),
				MyConstants.MATERIALS_INFO_CONTENT_TYPE_INSERT);
		startActivity(intent);
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
	public void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		//		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("*/*");
		//		intent.setType("application/vnd.ms-excel");

		try {
			startActivityForResult(Intent.createChooser(intent, "选择Excel文件"), FILE_SELECT_CODE);
		} catch (android.content.ActivityNotFoundException ex) {
			showToastLong("请先安装文件管理器");
		}
	}

	/**
	 * 选择一个准备好的Excel文件更新数据库数据
	 * @param excelFilePath 
	 */
	/*private void updateDbDataFromExcel(String excelFilePath) {
		deleteTablesDataInDb();
		// 往数据库中装载数据
		PrepareDbData.getInstance(this).updateDbData(excelFilePath);
		showToast("成功更新数据库数据");
	}*/

	/**
	 * 导出数据库数据到格式为XLS的Excel文件
	 */
	public void exportDbDataToXLS() {
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
				showToastLong("位置：" + exportXLSPath + "/" + fileName);
			} catch (RowsExceededException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			showToastLong("没有SD卡， 请插入SD卡并重启设备让SD卡生效");
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
	/*private void deleteTablesDataInDb() {
		for (int i = 0; i < 3; i++) {
			db.delete(TableNames[i], null, null);
		}

	}*/

	/**
	 * 显示Toast长信息
	 * @param string
	 */
	public void showToastLong(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
	/**
	 * 显示Toast短信息
	 * @param string
	 */
	public void showToastShort(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
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
		builder.setMessage("Copyright © 2014 广州市供电局");
		builder.setNegativeButton("关 闭", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		return builder.create();
	}

	/**
	 * 获取IP/端口
	 * @return
	 */
	public String getIPAndPort() {
		mPerPreferences = this.getSharedPreferences("ip_port_setting", MODE_PRIVATE);
		ip_port = mPerPreferences.getString("ip_port", null);
		if (null == ip_port || "".equals(ip_port)) {
			return "";
		} else {
			return ip_port;
		}
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
				StackManager.getStackManager().popAllActivitys();
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
	/*@Override
	protected void onResume() {
		super.onResume();
		loadData();
	}*/

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
			refresh();
			break;
			/*case FILE_SELECT_CODE:
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

			break;*/
		}
	}

	/**
	 * 搜索物资编码
	 */
	public void searchMaterialsNo() {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, SearchMaterialsNoActivity.class);
		MainActivity.this.startActivity(intent);
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

	/**
	 * 选项菜单中的项被选中事件
	 *//*
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_search_materialsNo :
			searchMaterialsNo();
			return true;
		case R.id.action_logout :
			logout();
			finish();
			return true;
		case R.id.action_add_materials_info :
			addMaterialsInfo();
			return true;
		case R.id.action_update_database_from_excel :
			showFileChooser();
			return true;
		case R.id.action_export_database_data_to_Excel :
			exportDbDataToXLS();
			return true;
		case R.id.action_copyright :
			createAboutUs().show();
			return true;
		case R.id.action_exit :
			createExit().show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}*/

	/**
	 * 跳转到登录界面
	 */
	public void logout() {
		Intent intent = new Intent();
		intent.setClass(this, LoginActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu01, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return true;
			/*case R.id.action_logout :
			logout();
			finish();
			return true;*/
		case R.id.action_add_materials_info :
			addMaterialsInfo();
			return true;

		case R.id.action_copyright :
			createAboutUs().show();
			return true;
		case R.id.action_exit :
			createExit().show();
			return true;
			/*case R.id.github:
			Util.goToGitHub(this);
			return true;*/
		}
		return super.onOptionsItemSelected(item);
	}

	/*@Override
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
	}*/
}