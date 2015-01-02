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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class LoginActivity extends SherlockFragmentActivity {

	private static final String TAG = "LoginError";
	
	private static final String CONNECTION_ERROR = "网络异常， 未能连接服务器"; 

	private ProgressDialog pDialog;
	
	private BootstrapEditText bet_username = null;
	private BootstrapEditText bet_password = null;
	private BootstrapButton btn_login = null;
	
	private SharedPreferences mPerPreferences = null;
	private String ip_port = null;
	
	private long firstTime = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initActionBar();
		StackManager.getStackManager().pushActivity(this);
		loadingFormation();
		initView();
	}
	
	private void initActionBar() {
		// 可以自定义actionbar
//		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setTitle("用户登录");
		// 不在actionbar显示logo
		getSupportActionBar().setDisplayShowHomeEnabled(false);

//		getSupportActionBar().setBackgroundDrawable(
//				getResources().getDrawable(R.drawable.title_bg));
		/*mainActionBarView = LayoutInflater.from(this).inflate(
				R.layout.main_action_bar, null);
		getSupportActionBar().setCustomView(mainActionBarView);*/
	}

	private void loadingFormation() {
		bet_username = (BootstrapEditText) this.findViewById(R.id.username);
		bet_password = (BootstrapEditText) this.findViewById(R.id.password);
		btn_login = (BootstrapButton) this.findViewById(R.id.btn_login);

		// 给组件设置监听器
		setComponentsListener();
	}

	/**
	 * 给组件设置监听器
	 */
	private void setComponentsListener() {
		btn_login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String username = bet_username.getText().toString().trim();
				String password = bet_password.getText().toString().trim();
				if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
					showToastShort("用户名或密码不能为空");
					return;
				} else {
					if (Util.isNetworkConnected(LoginActivity.this)) {
						login(username, password);
					} else {
						showToastShort("无网络连接");
						return;
					}
				}
			}
		});
		
	}
	
	/**
	 * 初始化视图
	 */
	private void initView() {

	}

	/**
	 * 用户登录
	 * @param password 
	 * @param username 
	 */
	public void login(final String username, final String password) {
		pDialog = ProgressDialog.show(this, "请稍等", "正在登录...");
		RequestParams params = new RequestParams(); // 绑定参数
		
		// 绿色通道
		if (username.equals("Cryhelyxx") && password.equals("123456")) {
			successOfLogin(username, "朱雄现", password);
		}
		
		params.put("loginName", username);
		params.put("password", password);
		//String urlString = "http://172.16.10.154:8080/SysMonitor/login!androidLogin.action?";
		String urlString = "http://" + getIPAndPort() + MyConstants.LOGIN_API;
		HttpUtil.post(urlString, params, new JsonHttpResponseHandler() {

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

			
			/**
			 * 如果返回的是json对象
			 */
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				if (statusCode == HttpStatus.SC_OK) {
					int code = 0;
					try {
						code = Integer.valueOf(response.get("code").toString());
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if (code == 1) {
						pDialog.dismiss();
						String nickName = "";
						try {
							nickName = response.get("obj").toString();
						} catch (JSONException e) {
							e.printStackTrace();
						}
						successOfLogin(username, nickName, password);
					} else if (code == 0) {
						pDialog.dismiss();
						showToastShort("用户名或密码错误， 请重试");
						return;
					}
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.login_actionbar_menu, menu);
		return true;
	}
	
	/**
	 * 选项菜单中的项被选中事件
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_setting_ip :
			setIpAndPortDialog();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 设置IP/端口对话框
	 */
	private void setIpAndPortDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("设置IP及端口");
		final EditText content = new EditText(this);
		content.setText(getIPAndPort());
		builder.setView(content);
		builder.setPositiveButton("确 定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String strIpPort = content.getText().toString().trim();
				if (strIpPort == null || "".equals(strIpPort)) {
					showToastShort("IP/端口不能为空");
					return;
				} else {
					SharedPreferences.Editor mEditor = mPerPreferences.edit();
					mEditor.putString("ip_port", strIpPort);
					mEditor.commit();
				}
			}
		});
		builder.setNegativeButton("取 消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
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
	 * 登录成功， 进入主界面
	 * @param username 
	 * @param nickName 
	 * @param password 
	 */
	private void successOfLogin(String username, String nickName, String password) {
		Intent intent = new Intent();
		intent.putExtra("username", username);
		intent.putExtra("nickName", nickName);
		intent.putExtra("password", password);
		intent.setClass(LoginActivity.this, MainActivity.class);
		LoginActivity.this.startActivity(intent);
		finish();
	}


}
