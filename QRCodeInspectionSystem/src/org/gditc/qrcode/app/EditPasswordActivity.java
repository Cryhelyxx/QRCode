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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
/**
 * 编辑备注
 */
public class EditPasswordActivity extends Activity {

	private static final String TAG = "EditPassword";
	
	private static final String CONNECTION_ERROR = "网络异常， 未能连接服务器";
	
	private ProgressDialog pDialog;
	
	private String username = null;
	
	private BootstrapEditText bet_oldPwd = null;
	private BootstrapEditText bet_newPwd = null;
	private BootstrapEditText bet_newPwd2 = null;
	

	private Button btn_back = null;
	private BootstrapButton bb_save = null;
	
	private SharedPreferences mPerPreferences = null;
	private String ip_port = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);		// 不显示标题
		setContentView(R.layout.activity_edit_password);

		StackManager.getStackManager().pushActivity(this);
		loadingFormation();
		
		Intent intent = getIntent();
		username = intent.getStringExtra("username");
	}

	/**
	 * 加载组件
	 */
	private void loadingFormation() {
		btn_back = (Button) this.findViewById(R.id.btn_back_edit_password);
		bb_save = (BootstrapButton) this.findViewById(R.id.btn_save_password);
		
		bet_oldPwd = (BootstrapEditText) this.findViewById(R.id.old_password);
		bet_newPwd = (BootstrapEditText) this.findViewById(R.id.new_password);
		bet_newPwd2 = (BootstrapEditText) this.findViewById(R.id.new_password2);
		
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
		bb_save.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String oldPwd = bet_oldPwd.getText().toString().trim();
				String newPwd = bet_newPwd.getText().toString().trim();
				String newPwd2 = bet_newPwd2.getText().toString().trim();
				if (oldPwd == null || "".equals(oldPwd)) {
					showToastShort("旧密码不能为空");
					return;
				} else if (newPwd == null || "".equals(newPwd)) {
					showToastShort("新密码不能为空");
					return;
				} else if (newPwd2 == null || "".equals(newPwd2)) {
					showToastShort("确认密码不能为空");
					return;
				} else if (newPwd.length() < 6) {
					showToastShort("密码长度至少6个字符以上");
					return;
				} else if (!newPwd.equals(newPwd2)) {
					showToastShort("密码不一致, 请重试");
					return;
				} else {
					if (Util.isNetworkConnected(EditPasswordActivity.this)) {
						updatePassword(oldPwd, newPwd);
					} else {
						showToastShort("无网络连接");
						return;
					}
				}
			}
		});
	}

	/**
	 * 更新密码
	 * @param oldPwd
	 * @param newPwd
	 */
	protected void updatePassword(String oldPwd, String newPwd) {
		pDialog = ProgressDialog.show(this, "请稍等", "正在提交数据到服务器...");
		RequestParams params = new RequestParams(); // 绑定参数
		params.put("username", username);
		params.put("oldPassword", oldPwd);
		params.put("newPassword", newPwd);
		//String urlString = "http://172.16.10.13:8080/SysMonitor/user!updatePassword.action?";
		String urlString = "http://" + getIPAndPort() + MyConstants.UPDATE_PASSWORD_API;
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
					try {
						if (response.getInt("code") == 1){
							showToastShort("密码修改成功");
							finish();
							pDialog.dismiss();
						} else {
							showToastShort("旧密码输入错误， 请重试");
							bet_oldPwd.setText("");
							pDialog.dismiss();
							
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login_options_menu, menu);
		return true;
	}
	
	/**
	 * 选项菜单中的项被选中事件
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.ip_port_setting :
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
}
