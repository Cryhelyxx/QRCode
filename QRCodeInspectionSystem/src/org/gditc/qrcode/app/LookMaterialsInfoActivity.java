package org.gditc.qrcode.app;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.gditc.qrcode.R;
import org.gditc.qrcode.common.MyConstants;
import org.gditc.qrcode.utils.HttpUtil;
import org.gditc.qrcode.utils.StackManager;
import org.gditc.qrcode.utils.Util;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class LookMaterialsInfoActivity extends Activity {

	private static final String TAG = "LookMaterialsInfo";
	private static final String CONNECTION_ERROR = "网络异常， 未能连接服务器";

	private static final String NO_DATA = "暂无填写";
	
	private ProgressDialog pDialog;

	private Button btn_back = null;
	private Button btn_edit = null;

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

	private String materialsNo = null;
	
	private SharedPreferences mPerPreferences = null;
	private String ip_port = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);		// 不显示标题
		setContentView(R.layout.activity_look_materials_info);
		
		StackManager.getStackManager().pushActivity(this);
		loadingFormation();

		loadData();		// 装载数据
	}

	/**
	 * 加载组件
	 */
	private void loadingFormation() {
		btn_back = (Button) this.findViewById(R.id.btn_back_materials_info_look);
		btn_edit = (Button) this.findViewById(R.id.btn_edit_materials_info_look);
		
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
			if (Util.isNetworkConnected(LookMaterialsInfoActivity.this)) {
				getBaseInfo();
			} else {
				showToastShort("无网络连接");
				return;
			}
		} 
	}

	/**
	 * 获取基本信息
	 */
	private void getBaseInfo() {
		pDialog = ProgressDialog.show(this, "请稍等", "正在刷新数据...");
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
							tv_materialsNo.setText(MI_MATERIALS_NO);

							/*台账信息===================*/
							if (MI_LEDGER_CARD_NO != null && !"null".equals(MI_LEDGER_CARD_NO)) {
								ledgerInfo_cardNo.setText(MI_LEDGER_CARD_NO);
							} else {
								ledgerInfo_cardNo.setText(NO_DATA);
							}
							if (MI_LEDGER_DEVICES_NO != null && !"null".equals(MI_LEDGER_DEVICES_NO)) {
								ledgerInfo_devicesNo.setText(MI_LEDGER_DEVICES_NO);
							} else {
								ledgerInfo_devicesNo.setText(NO_DATA);
							}
							if (MI_LEDGER_COMMISSIONING_DATE != null && !"null".equals(MI_LEDGER_COMMISSIONING_DATE)) {
								ledgerInfo_commissioningDate.setText(MI_LEDGER_COMMISSIONING_DATE);
							} else {
								ledgerInfo_commissioningDate.setText(NO_DATA);
							}
							if (MI_LEDGER_MANUFACTURER != null && !"null".equals(MI_LEDGER_MANUFACTURER)) {
								ledgerInfo_manufacturer.setText(MI_LEDGER_MANUFACTURER);
							} else {
								ledgerInfo_manufacturer.setText(NO_DATA);
							}
							if (MI_LEDGER_REMARK != null && !"null".equals(MI_LEDGER_REMARK)) {
								ledgerInfo_remark.setText(MI_LEDGER_REMARK);
							} else {
								ledgerInfo_remark.setText(NO_DATA);
							}
							if (MI_LEDGER_COST != null && !"null".equals(MI_LEDGER_COST)) {
								ledgerInfo_cost.setText(MI_LEDGER_COST);
							} else {
								ledgerInfo_cost.setText(NO_DATA);
							}
							/*卡片信息===================*/
							if (MI_CARD_FID != null && !"null".equals(MI_CARD_FID)) {
								cardInfo_FID.setText(MI_CARD_FID);
							} else {
								cardInfo_FID.setText(NO_DATA);
							}
							if (MI_CARD_ASSETSNAME != null && !"null".equals(MI_CARD_ASSETSNAME)) {
								cardInfo_assetsName.setText(MI_CARD_ASSETSNAME);
							} else {
								cardInfo_assetsName.setText(NO_DATA);
							}
							if (MI_CARD_SPECIFICATION != null && !"null".equals(MI_CARD_SPECIFICATION)) {
								cardInfo_specification.setText(MI_CARD_SPECIFICATION);
							} else {
								cardInfo_specification.setText(NO_DATA);
							}
							if (MI_CARD_MANUFACTURER != null && !"null".equals(MI_CARD_MANUFACTURER)) {
								cardInfo_manufacturer.setText(MI_CARD_MANUFACTURER);
							} else {
								cardInfo_manufacturer.setText(NO_DATA);
							}
							if (MI_CARD_COMMISSIONING_DATE != null && !"null".equals(MI_CARD_COMMISSIONING_DATE)) {
								cardInfo_commissioningDate.setText(MI_CARD_COMMISSIONING_DATE);
							} else {
								cardInfo_commissioningDate.setText(NO_DATA);
							}
							if (MI_CARD_PROPERTYRIGHT != null && !"null".equals(MI_CARD_PROPERTYRIGHT)) {
								cardInfo_propertyRight.setText(MI_CARD_PROPERTYRIGHT);
							} else {
								cardInfo_propertyRight.setText(NO_DATA);
							}
						} else {
							pDialog.dismiss();
							showToastShort(response.getString("msg") + "\n物资编码[" + materialsNo + "]找不到");

							tv_materialsNo.setText(NO_DATA);

							ledgerInfo_cardNo.setText(NO_DATA);
							ledgerInfo_devicesNo.setText(NO_DATA);
							ledgerInfo_commissioningDate.setText(NO_DATA);
							ledgerInfo_manufacturer.setText(NO_DATA);
							ledgerInfo_remark.setText(NO_DATA);
							ledgerInfo_cost.setText(NO_DATA);

							cardInfo_FID.setText(NO_DATA);
							cardInfo_assetsName.setText(NO_DATA);
							cardInfo_specification.setText(NO_DATA);
							cardInfo_manufacturer.setText(NO_DATA);
							cardInfo_commissioningDate.setText(NO_DATA);
							cardInfo_propertyRight.setText(NO_DATA);

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
		btn_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		btn_edit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				editMaterialsInfo();
			}
		});
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
	 * 刷新界面(手动刷新)
	 */
	public void refresh() {
		loadData();
	}

	/**
	 * 自动刷新
	 */
	/*@Override
	protected void onResume() {
		super.onResume();
		loadData();
	}*/
	
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
}