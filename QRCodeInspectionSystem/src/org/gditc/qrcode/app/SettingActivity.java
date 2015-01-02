package org.gditc.qrcode.app;

import org.gditc.qrcode.R;
import org.gditc.qrcode.utils.StackManager;
import org.gditc.qrcode.utils.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 编辑备注
 */
public class SettingActivity extends Activity {

	private static final String TAG = "SettingActivity";
	private ProgressDialog pDialog;

	private String username = null;
	private String nickName = null;
	
	private SharedPreferences mPerPreferences = null;
	private String ip_port = null;
	
	private RelativeLayout rl_usermessage = null;
	private RelativeLayout ip_port__layout = null;
	private RelativeLayout about_us_layout = null;

	private TextView tv_nickName = null;


	private Button btn_back = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);		// 不显示标题
		setContentView(R.layout.activity_setting);

		StackManager.getStackManager().pushActivity(this);
		loadingFormation();

		Intent intent = getIntent();
		username = intent.getStringExtra("username");
		nickName = intent.getStringExtra("nickName");

		initView();
	}

	/**
	 * 加载组件
	 */
	private void loadingFormation() {
		btn_back = (Button) this.findViewById(R.id.btn_back_setting);
		rl_usermessage = (RelativeLayout) this.findViewById(R.id.usermessage);
		ip_port__layout = (RelativeLayout) this.findViewById(R.id.ip_port__layout);
		about_us_layout = (RelativeLayout) this.findViewById(R.id.about_us_layout);
		tv_nickName = (TextView) this.findViewById(R.id.tv_nickname);

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
		rl_usermessage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 该干嘛干嘛
			}
		});
		ip_port__layout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setIpAndPortDialog();
			}
		});
		about_us_layout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Util.goToCSG(SettingActivity.this);
			}
		});
	}

	private void initView() {
		tv_nickName.setText(nickName);
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
