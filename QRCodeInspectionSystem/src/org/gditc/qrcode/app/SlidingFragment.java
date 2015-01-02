package org.gditc.qrcode.app;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.gditc.qrcode.R;
import org.gditc.qrcode.common.MyConstants;
import org.gditc.qrcode.utils.HttpUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.loopj.android.http.JsonHttpResponseHandler;


public class SlidingFragment extends Fragment {

	private static final String TAG = "QRCode";

	private TextView tv_nickName = null;
	private BootstrapButton btn_signout = null;

	private RelativeLayout main_interface_layout = null;
	private RelativeLayout update_password_layout = null;
	private RelativeLayout setting_layout = null;
	

	private String username = null;
	private String nickName = null;
	private String password = null;
	
	//private SharedPreferences mPerPreferences = null;
	//private String ip_port = null;
	private String Url = "";

	public SlidingFragment(String username, String nickName, String password, String ip_port) {
		this.username = username;
		this.nickName = nickName;
		this.password = password;
		Url = "http://" + ip_port + MyConstants.LOGOUT_API;
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.sliding_behind_layout, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
	}

	private void initView() {
		tv_nickName = (TextView) getActivity().findViewById(R.id.nickname);
		tv_nickName.setText(nickName);
		main_interface_layout = (RelativeLayout) this.getActivity().findViewById(R.id.main_interface_layout);
		update_password_layout = (RelativeLayout) getActivity().findViewById(R.id.update_password_layout);
		setting_layout = (RelativeLayout) getActivity().findViewById(R.id.setting_layout);
		btn_signout = (BootstrapButton) getActivity().findViewById(R.id.btn_sign_out);
		// 给组件设置监听器
		setComponentsListener();
	}

	private void setComponentsListener() {
		btn_signout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				logout();
			}
		});
		main_interface_layout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((SlidingFragmentActivity) getActivity()).toggle();
			}
		});
		update_password_layout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				goToEditPassword();
			}
		});
		setting_layout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goToSetting();
			}
		});
	}

	/**
	 * 跳转到设置界面
	 */
	protected void goToSetting() {
		Intent intent = new Intent();
		intent.putExtra("username", username);
		intent.putExtra("nickName", nickName);
		intent.setClass(getActivity(), SettingActivity.class);
		SlidingFragment.this.startActivity(intent);
	}

	/**
	 * 跳转到修改密码界面
	 */
	protected void goToEditPassword() {
		Intent intent = new Intent();
		intent.putExtra("username", username);
		intent.setClass(getActivity(), EditPasswordActivity.class);
		SlidingFragment.this.startActivity(intent);
	}

	/**
	 * 跳转到登录界面
	 */
	public void logout() {
		HttpUtil.post(Url, null , new JsonHttpResponseHandler(){
			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				Log.i(TAG, throwable.toString());
				showToastShort("网络异常， 请在良好的网络下操作");
				return;
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONArray errorResponse) {
				Log.i(TAG, throwable.toString());
				showToastShort("网络异常， 请在良好的网络下操作");
				return;
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				Log.i(TAG, throwable.toString());
				showToastShort("网络异常， 请在良好的网络下操作");
				return;
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				if (statusCode == HttpStatus.SC_OK) {
					try {
						if (response.getInt("code") == 1){
							showToastShort(response.getString("msg"));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
		goToLogin();
	}

	private void goToLogin() {
		//Intent intent = new Intent(getActivity(), LoginActivity.class);
		Intent intent = new Intent();
		intent.setClass(getActivity(), LoginActivity.class);
		SlidingFragment.this.startActivity(intent);
	}

	/**
	 * 显示Toast长信息
	 * @param msg
	 */
	public void showToastLong(String msg) {
		Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
	}
	/**
	 * 显示Toast短信息
	 * @param msg
	 */
	public void showToastShort(String msg) {
		/*if (getActivity() == null) {
			Log.i(TAG, "getActivity为null");
			return;
		}*/
		Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 获取IP/端口
	 * @return
	 */
	/*public String getIPAndPort() {
		mPerPreferences = getActivity().getApplicationContext().getSharedPreferences("ip_port_setting", Context.MODE_PRIVATE);
		ip_port = mPerPreferences.getString("ip_port", null);
		if (null == ip_port || "".equals(ip_port)) {
			return "";
		} else {
			return ip_port;
		}
	}*/


}
