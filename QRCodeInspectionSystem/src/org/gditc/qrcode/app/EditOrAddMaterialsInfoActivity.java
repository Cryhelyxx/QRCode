package org.gditc.qrcode.app;

import java.util.Calendar;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.gditc.qrcode.R;
import org.gditc.qrcode.common.MaterialsInfo;
import org.gditc.qrcode.common.MyConstants;
import org.gditc.qrcode.utils.HttpUtil;
import org.gditc.qrcode.utils.StackManager;
import org.gditc.qrcode.utils.Util;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class EditOrAddMaterialsInfoActivity extends Activity {

	private static final String TAG = "EditOrAddMaterialsInfo";
	private static final String CONNECTION_ERROR = "网络异常， 未能连接服务器";

	// 该Activity处于两情景状态， 插入状态或编辑状态
	private static final int STATE_INSERT = 0;			// 插入状态
	private static final int STATE_EDIT = 1;			// 编辑状态
	// 记录当前情景的状态(插入或编辑)
	private int state;

	private ProgressDialog pDialog;

	private MaterialsInfo materialsInfoCache = null;

	private Button btn_back = null;
	private TextView tv_title = null;
	private Button btn_save = null;

	private EditText et_materials_info_materialsNo = null;

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

	// 年-月-日
	private int mYear;
	private int mMonth;
	private int mDay;

	private SharedPreferences mPerPreferences = null;
	private String ip_port = null;
	private String Url = "";
	
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

		Url = "http://" + getIPAndPort() + MyConstants.INSERT_EDIT_API;

		Intent intent = getIntent();
		String action = intent.getAction();

		if (action.equals(Intent.ACTION_INSERT)) {
			state = STATE_INSERT;
			tv_title.setText("新增物资信息");
			/*String date = mYear + "-" + mMonth + "-" + mDay;
			btn_ledger_info_commissioningDate.setText(date);
			btn_card_info_commissioningDate.setText(date);*/
		} else if (action.equals(Intent.ACTION_EDIT)) {
			state = STATE_EDIT;
			tv_title.setText("编辑物资信息");
			if (Util.isNetworkConnected(EditOrAddMaterialsInfoActivity.this)) {
				getDataFromServer(intent);
			} else {
				showToastShort("无网络连接");
				return;
			}
		} else {
			Log.e(TAG, "未知错误, 程序正在退出...");
			finish();
		}
	}

	/**
	 * 从服务端获取数据
	 * @param intent 
	 */
	private void getDataFromServer(Intent intent) {
		String materialsNo = intent.getStringExtra("materialsNo");
		et_materials_info_materialsNo.setText(materialsNo);
		pDialog = ProgressDialog.show(this, "请稍等", "正在从服务端获取数据...");
		RequestParams params = new RequestParams(); // 绑定参数
		params.add("MI_MATERIALS_NO", materialsNo);
		String urlString = "http://" + getIPAndPort() + MyConstants.SEARCH_DATA_API;
		HttpUtil.post(urlString, params , new JsonHttpResponseHandler(){

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
							et_materials_info_materialsNo.setText(MI_MATERIALS_NO);

							/*台账信息===================*/
							if (MI_LEDGER_CARD_NO != null && !"null".equals(MI_LEDGER_CARD_NO)) {
								et_ledger_info_cardNo.setText(MI_LEDGER_CARD_NO);
							} else {
								et_ledger_info_cardNo.setText("");
							}
							if (MI_LEDGER_DEVICES_NO != null && !"null".equals(MI_LEDGER_DEVICES_NO)) {
								et_ledger_info_devicesNo.setText(MI_LEDGER_DEVICES_NO);
							} else {
								et_ledger_info_devicesNo.setText("");
							}
							if (MI_LEDGER_COMMISSIONING_DATE != null && !"null".equals(MI_LEDGER_COMMISSIONING_DATE)) {
								btn_ledger_info_commissioningDate.setText(MI_LEDGER_COMMISSIONING_DATE);
							} else {
								btn_ledger_info_commissioningDate.setText("");
							}
							if (MI_LEDGER_MANUFACTURER != null && !"null".equals(MI_LEDGER_MANUFACTURER)) {
								et_ledger_info_manufacturer.setText(MI_LEDGER_MANUFACTURER);
							} else {
								et_ledger_info_manufacturer.setText("");
							}
							if (MI_LEDGER_REMARK != null && !"null".equals(MI_LEDGER_REMARK)) {
								et_ledger_info_remark.setText(MI_LEDGER_REMARK);
							} else {
								et_ledger_info_remark.setText("");
							}
							if (MI_LEDGER_COST != null && !"null".equals(MI_LEDGER_COST)) {
								et_ledger_info_cost.setText(MI_LEDGER_COST);
							} else {
								et_ledger_info_cost.setText("");
							}
							/*卡片信息===================*/
							if (MI_CARD_FID != null && !"null".equals(MI_CARD_FID)) {
								et_card_info_FID.setText(MI_CARD_FID);
							} else {
								et_card_info_FID.setText("");
							}
							if (MI_CARD_ASSETSNAME != null && !"null".equals(MI_CARD_ASSETSNAME)) {
								et_card_info_assetsName.setText(MI_CARD_ASSETSNAME);
							} else {
								et_card_info_assetsName.setText("");
							}
							if (MI_CARD_SPECIFICATION != null && !"null".equals(MI_CARD_SPECIFICATION)) {
								et_card_info_specification.setText(MI_CARD_SPECIFICATION);
							} else {
								et_card_info_specification.setText("");
							}
							if (MI_CARD_MANUFACTURER != null && !"null".equals(MI_CARD_MANUFACTURER)) {
								et_card_info_manufacturer.setText(MI_CARD_MANUFACTURER);
							} else {
								et_card_info_manufacturer.setText("");
							}
							if (MI_CARD_COMMISSIONING_DATE != null && !"null".equals(MI_CARD_COMMISSIONING_DATE)) {
								btn_card_info_commissioningDate.setText(MI_CARD_COMMISSIONING_DATE);
							} else {
								btn_card_info_commissioningDate.setText("");
							}
							if (MI_CARD_PROPERTYRIGHT != null && !"null".equals(MI_CARD_PROPERTYRIGHT)) {
								et_card_info_propertyRight.setText(MI_CARD_PROPERTYRIGHT);
							} else {
								et_card_info_propertyRight.setText("");
							}
						} else if (response.getInt("code") == 0) {
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	/**
	 * 加载组件
	 */
	private void loadingFormation() {
		btn_back = (Button) this.findViewById(R.id.btn_back_materials_info);
		tv_title = (TextView) this.findViewById(R.id.title_materials_info);
		btn_save = (Button) this.findViewById(R.id.btn_save_materials_info);

		et_materials_info_materialsNo = (EditText) this.findViewById(R.id.materials_info_materialsNo);

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
				//String commissioningDate = btn_ledger_info_commissioningDate.getText().toString();
				String commissioningDate = ((Button) v).getText().toString();
				createDatePickerDialog(v, commissioningDate).show();
			}
		});
		btn_card_info_commissioningDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//String commissioningDate = btn_card_info_commissioningDate.getText().toString();
				String commissioningDate = ((Button) v).getText().toString();
				createDatePickerDialog(v, commissioningDate).show();
			}
		});
	}



	/**
	 * 验证数据， 并插入到数据库
	 */
	private void verifyData() {
		MI_MATERIALS_NO = et_materials_info_materialsNo.getText().toString().trim();

		MI_LEDGER_CARD_NO = et_ledger_info_cardNo.getText().toString().trim();
		MI_LEDGER_DEVICES_NO = et_ledger_info_devicesNo.getText().toString().trim();
		MI_LEDGER_COMMISSIONING_DATE = btn_ledger_info_commissioningDate.getText().toString();
		MI_LEDGER_MANUFACTURER = et_ledger_info_manufacturer.getText().toString().trim();
		MI_LEDGER_REMARK = et_ledger_info_remark.getText().toString().trim();
		MI_LEDGER_COST = et_ledger_info_cost.getText().toString().trim();

		MI_CARD_FID = et_card_info_FID.getText().toString().trim();
		MI_CARD_ASSETSNAME = et_card_info_assetsName.getText().toString().trim();
		MI_CARD_SPECIFICATION = et_card_info_specification.getText().toString().trim();
		MI_CARD_MANUFACTURER = et_card_info_manufacturer.getText().toString().trim();
		MI_CARD_COMMISSIONING_DATE = btn_card_info_commissioningDate.getText().toString();
		MI_CARD_PROPERTYRIGHT = et_card_info_propertyRight.getText().toString().trim();

		materialsInfoCache = new MaterialsInfo();

		materialsInfoCache.setMaterialsNo(MI_MATERIALS_NO);

		materialsInfoCache.getLedgerInfo().setCardNo(MI_LEDGER_CARD_NO);
		materialsInfoCache.getLedgerInfo().setDevicesNo(MI_LEDGER_DEVICES_NO);
		materialsInfoCache.getLedgerInfo().setCommissioningDate(MI_LEDGER_COMMISSIONING_DATE);
		materialsInfoCache.getLedgerInfo().setManufacturer(MI_LEDGER_MANUFACTURER);
		materialsInfoCache.getLedgerInfo().setRemark(MI_LEDGER_REMARK);
		materialsInfoCache.getLedgerInfo().setCost(MI_LEDGER_COST);

		materialsInfoCache.getCardInfo().setFID(MI_CARD_FID);
		materialsInfoCache.getCardInfo().setAssetsName(MI_CARD_ASSETSNAME);
		materialsInfoCache.getCardInfo().setSpecification(MI_CARD_SPECIFICATION);
		materialsInfoCache.getCardInfo().setManufacturer(MI_CARD_MANUFACTURER);
		materialsInfoCache.getCardInfo().setCommissioningDate(MI_CARD_COMMISSIONING_DATE);
		materialsInfoCache.getCardInfo().setPropertyRight(MI_CARD_PROPERTYRIGHT);

		// 检验数据的合法性
		if ("".equals(MI_MATERIALS_NO) || null == MI_MATERIALS_NO) {
			showToastShort("物资编码不能为空");
			return;
		}

		if (state == STATE_INSERT) {
			search(MI_MATERIALS_NO);
		} else if (state == STATE_EDIT) {
			Intent intent = getIntent();
			if (MI_MATERIALS_NO.equals(intent.getStringExtra("materialsNo"))) {
				if (Util.isNetworkConnected(EditOrAddMaterialsInfoActivity.this)) {
					update();
				} else {
					showToastShort("无网络连接");
					return;
				}
			} else {
				if (Util.isNetworkConnected(EditOrAddMaterialsInfoActivity.this)) {
					search(MI_MATERIALS_NO);
				} else {
					showToastShort("无网络连接");
					return;
				}
			}
		}
	}

	/**
	 * 查询数据
	 * @param  materialsNo
	 */
	public void search(final String materialsNo ) {
		pDialog = ProgressDialog.show(EditOrAddMaterialsInfoActivity.this, "请稍等", "正在查询数据...");
		RequestParams params = new RequestParams(); // 绑定参数
		params.add("MI_MATERIALS_NO", materialsNo);
		String urlString = "http://" + getIPAndPort() + MyConstants.SEARCH_DATA_API;
		HttpUtil.post(urlString, params , new JsonHttpResponseHandler(){

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
					try {
						if (response.getInt("code") == 1){
							pDialog.dismiss();
							//showToastShort("[" + materialsNo + "]已存在， 请重试");
							materialsInfoAlreadyExistsDialog(materialsNo);
							return;
						} else {
							if (state == STATE_INSERT) {
								add();
							} else if (state == STATE_EDIT) {
								update();
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	/**
	 * 当某物资信息已存在时弹出对话框
	 * @param materialsNo 
	 */
	protected void materialsInfoAlreadyExistsDialog(String materialsNo) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle("提 示");
		builder.setMessage("物资编码[" + materialsNo + "]已存在!\n是否要查看该物资信息？");
		builder.setPositiveButton("确 定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				goToLookMaterialsInfo();
			}
		});
		builder.setNegativeButton("取 消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();;
	}

	/**
	 * 更新数据
	 */
	public void update(){
		RequestParams params = new RequestParams();

		//物资编号不能为空！
		params.add("MI_MATERIALS_NO", MI_MATERIALS_NO);

		params.add("MI_LEDGER_CARD_NO", MI_LEDGER_CARD_NO);
		params.add("MI_LEDGER_DEVICES_NO", MI_LEDGER_DEVICES_NO);
		params.add("MI_LEDGER_COMMISSIONING_DATE", MI_LEDGER_COMMISSIONING_DATE);
		params.add("MI_LEDGER_MANUFACTURER", MI_LEDGER_MANUFACTURER);
		params.add("MI_LEDGER_REMARK", MI_LEDGER_REMARK);
		params.add("MI_LEDGER_COST", MI_LEDGER_COST);
		params.add("MI_CARD_FID", MI_CARD_FID);
		params.add("MI_CARD_ASSETSNAME", MI_CARD_ASSETSNAME);
		params.add("MI_CARD_SPECIFICATION", MI_CARD_SPECIFICATION);
		params.add("MI_CARD_MANUFACTURER", MI_CARD_MANUFACTURER);
		params.add("MI_CARD_COMMISSIONING_DATE", MI_CARD_COMMISSIONING_DATE);
		params.add("MI_CARD_PROPERTYRIGHT", MI_CARD_PROPERTYRIGHT);

		//修改物资信息需要传递id值
		params.put("MI_ID", MI_ID);

		//新增和修改的地址都一样
		doWork(Url, params, new MyHandler("修改成功", "网络异常"));
	}

	/**
	 * 插入数据
	 */
	public void add(){
		RequestParams params = new RequestParams();

		//物资编号不能为空！
		params.add("MI_MATERIALS_NO", MI_MATERIALS_NO);
		//这里的key值直接复制就行了。
		params.add("MI_LEDGER_CARD_NO", MI_LEDGER_CARD_NO);
		params.add("MI_LEDGER_DEVICES_NO", MI_LEDGER_DEVICES_NO);
		params.add("MI_LEDGER_COMMISSIONING_DATE", MI_LEDGER_COMMISSIONING_DATE);
		params.add("MI_LEDGER_MANUFACTURER", MI_LEDGER_MANUFACTURER);
		params.add("MI_LEDGER_REMARK", MI_LEDGER_REMARK);
		params.add("MI_LEDGER_COST", MI_LEDGER_COST);
		params.add("MI_CARD_FID", MI_CARD_FID);
		params.add("MI_CARD_ASSETSNAME", MI_CARD_ASSETSNAME);
		params.add("MI_CARD_SPECIFICATION", MI_CARD_SPECIFICATION);
		params.add("MI_CARD_MANUFACTURER", MI_CARD_MANUFACTURER);
		params.add("MI_CARD_COMMISSIONING_DATE", MI_CARD_COMMISSIONING_DATE);
		params.add("MI_CARD_PROPERTYRIGHT", MI_CARD_PROPERTYRIGHT);

		//新增物资信息不需要传递id值
		//params.put("MI_ID", MI_ID);
		doWork(Url, params, new MyHandler("新增成功！", "网络异常！！"));
	}

	public void doWork(String url, RequestParams params, 
			JsonHttpResponseHandler res){
		HttpUtil.post(url, params, res);
	}

	public class MyHandler extends JsonHttpResponseHandler{
		private String successMsg;
		private String failureMsg;
		/**
		 * @param successMsg 操作成功的提示信息
		 * @param failureMsg 访问网络失败的提示信息。
		 */
		public MyHandler(String successMsg, String failureMsg){
			pDialog = ProgressDialog.show(EditOrAddMaterialsInfoActivity.this, "请稍等", "正在保存数据...");
			this.successMsg = successMsg;
			this.failureMsg = failureMsg;
		}

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				JSONObject response) {
			try {
				if (response.getInt("code") == 1){
					showToastShort(successMsg);
					pDialog.dismiss();
					goToLookMaterialsInfo();
				}else{
					//成功获取到返回值，但是操作失败，显示返回的错误信息。
					showToastShort(response.getString("msg"));
					pDialog.dismiss();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		@Override
		public void onFailure(int statusCode, Header[] headers,
				String responseString, Throwable throwable) {
			showToastShort(failureMsg);
		}
	}



	/**
	 * 跳转到查看物资信息Activity
	 */
	private void goToLookMaterialsInfo() {
		Intent intent = new Intent();
		intent.putExtra("materialsNo", MI_MATERIALS_NO);
		intent.setClass(this, LookMaterialsInfoActivity.class);
		startActivity(intent);
		finish();
	}

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
	 * 初始化数据
	 */
	private void initData() {
		getCurrentDate();
	}

	/**
	 * 当应用遇到意外情况（如：内存不足、用户直接按Home键）由系统销毁一个Activity时，onSaveInstanceState() 会被调用.
	 * 通常onSaveInstanceState()只适合用于保存一些临时性的状态
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// 缓存数据
		materialsInfoCache = new MaterialsInfo();

		materialsInfoCache.setMaterialsNo(et_materials_info_materialsNo.getText().toString().trim());

		materialsInfoCache.getLedgerInfo().setCardNo(et_ledger_info_cardNo.getText().toString().trim());
		materialsInfoCache.getLedgerInfo().setDevicesNo(et_ledger_info_devicesNo.getText().toString().trim());
		materialsInfoCache.getLedgerInfo().setCommissioningDate(btn_ledger_info_commissioningDate.getText().toString());
		materialsInfoCache.getLedgerInfo().setManufacturer(et_ledger_info_manufacturer.getText().toString().trim());
		materialsInfoCache.getLedgerInfo().setRemark(et_ledger_info_remark.getText().toString().trim());
		materialsInfoCache.getLedgerInfo().setCost(et_ledger_info_cost.getText().toString().trim());

		materialsInfoCache.getCardInfo().setFID(et_card_info_FID.getText().toString().trim());
		materialsInfoCache.getCardInfo().setAssetsName(et_card_info_assetsName.getText().toString().trim());
		materialsInfoCache.getCardInfo().setSpecification(et_card_info_specification.getText().toString().trim());
		materialsInfoCache.getCardInfo().setManufacturer(et_card_info_manufacturer.getText().toString().trim());
		materialsInfoCache.getCardInfo().setCommissioningDate(btn_card_info_commissioningDate.getText().toString());
		materialsInfoCache.getCardInfo().setPropertyRight(et_card_info_propertyRight.getText().toString().trim());

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
		if (strCommissioningDate != null && !"".equals(strCommissioningDate)) {
			String args[] = strCommissioningDate.split("-");

			mYear = Integer.valueOf(args[0]);
			mMonth = Integer.valueOf(args[1]);
			mDay = Integer.valueOf(args[2]);
		}
	}
}
