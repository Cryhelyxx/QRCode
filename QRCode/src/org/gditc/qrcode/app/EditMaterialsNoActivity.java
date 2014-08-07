package org.gditc.qrcode.app;

import org.gditc.qrcode.R;
import org.gditc.qrcode.dao.QRCodeDbHelper;
import org.gditc.qrcode.utils.StackManager;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
/**
 * 编辑备注
 */
public class EditMaterialsNoActivity extends Activity {


	private QRCodeDbHelper db = null;
	private Cursor cursor = null;

	private Button btn_back = null;
	private Button btn_save = null;
	private EditText et_materialsNo = null;

	private String materialsNo = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);		// 不显示标题
		setContentView(R.layout.activity_edit_materialsno);
		// 方案一：
		//MyApplication.getInstance().addActivity(this);
		// 方案二：
		StackManager.getStackManager().pushActivity(this);
		loadingFormation();

		db = QRCodeDbHelper.getInstance(this);
		db.open();

		Intent intent = getIntent();
		materialsNo = intent.getStringExtra("materialsNo");
		initData();
	}

	/**
	 * 加载组件
	 */
	private void loadingFormation() {
		btn_back = (Button) this.findViewById(R.id.btn_back_edit_materialsNo);
		btn_save = (Button) this.findViewById(R.id.btn_save_materialsNo);
		et_materialsNo = (EditText) this.findViewById(R.id.et_materialsNo);

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
				String strMaterialsNo = et_materialsNo.getText().toString().trim();
				if (strMaterialsNo != null && !"".equals(strMaterialsNo)) {
					int retVal = db.updateMaterialsNo(strMaterialsNo, materialsNo);
					if (retVal > 0)
						showToast("保存成功");
					else
						showToast("保存失败");
					Intent intent = new Intent();
					intent.setClass(getApplicationContext(), MainActivity.class);
					intent.putExtra("strMaterialsNo", strMaterialsNo);
					startActivity(intent);
					finish();
				} else {
					showToast("物资编号不能为空");
				}
				
			}
		});
	}

	/**
	 * 显示Toast信息
	 * @param string
	 */
	private void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		if (materialsNo != null) {
			et_materialsNo.setText(materialsNo);
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
