package org.gditc.qrcode.utils;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

public class demo {

	
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
	
	private String Url = "http://172.16.10.154:8080/SysMonitor/materials!save.action";
	
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
		doWork(Url, params, new MyHandler("修改成功！", "网络异常！"));
		
		
	}
	
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
			ResponseHandlerInterface responseHandler){
		
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(url, params, responseHandler);
	}
	
	public class MyHandler extends JsonHttpResponseHandler{
		private String successMsg;
		private String failureMsg;
		/**
		 * @param successMsg 操作成功的提示信息
		 * @param failureMsg 访问网络失败的提示信息。
		 */
		public MyHandler(String successMsg, String failureMsg){
			this.successMsg = successMsg;
			this.failureMsg = failureMsg;
		}
		
		@Override
		public void onSuccess(int statusCode, Header[] headers,
				JSONObject response) {
			try {
				if (response.getInt("code") == 1){
					Toast.makeText(null, successMsg, 0).show();
				}else{
					//成功获取到返回值，但是操作失败，显示返回的错误信息。
					Toast.makeText(null, response.getString("msg"), 0).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		@Override
		public void onFailure(int statusCode, Header[] headers,
				String responseString, Throwable throwable) {
			Toast.makeText(null, failureMsg, 0).show();
		}
	}
	
	/**
	 * 二维码扫描
	 */
	public void scanQRCode(){
		AsyncHttpClient client = new AsyncHttpClient();
		
		RequestParams params = new RequestParams();
		params.add("MI_MATERIALS_NO", "08040100000001");
		client.post("http://172.16.10.154:8080/SysMonitor/materials!scanQRCode.action", 
				params , new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				
				try {
					if (response.getInt("code") == 1){
						JSONObject jsonObj = (JSONObject) response.get("obj");
						
						String MI_ID = jsonObj.getString("MI_ID");
						//	...
						//	...
						String MI_CARD_PROPERTYRIGHT = jsonObj.getString("MI_CARD_PROPERTYRIGHT");
						
						//获取数据后该干嘛干嘛...
						//....
						
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
		});
		
	}
}
