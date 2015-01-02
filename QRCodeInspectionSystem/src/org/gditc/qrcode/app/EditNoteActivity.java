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
public class EditNoteActivity extends Activity {

	private static final String NO_DATA = "暂无填写";
	
	private QRCodeDbHelper db = null;
	private Cursor cursor = null;

	private Button btn_back = null;
	private Button btn_save = null;
	private EditText noteInfo = null;
	
	private String materialsNo = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);		// 不显示标题
		setContentView(R.layout.activity_edit_note);
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
		btn_back = (Button) this.findViewById(R.id.btn_back_edit_note);
		btn_save = (Button) this.findViewById(R.id.btn_save_note);
		noteInfo = (EditText) this.findViewById(R.id.note);

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
				String strNote = noteInfo.getText().toString().trim();
				int retVal = db.updateNote(strNote, materialsNo);
				if (retVal > 0)
					showToast("保存成功");
				else
					showToast("保存失败");
				
				finish();
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
		cursor = db.getNoteByMaterialsNo(materialsNo);
		String notes = null;
		if (cursor.moveToNext()) {
			notes = cursor.getString(0);
		}
		if (notes != null) {
			if (notes.equals(NO_DATA)) {
				noteInfo.setText("");
			} else {
				noteInfo.setText(notes);
			}
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
