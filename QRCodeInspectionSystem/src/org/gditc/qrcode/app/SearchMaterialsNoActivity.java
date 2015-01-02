package org.gditc.qrcode.app;

import java.util.ArrayList;
import java.util.List;

import org.gditc.qrcode.R;
import org.gditc.qrcode.common.MyConstants;
import org.gditc.qrcode.dao.QRCodeDbHelper;
import org.gditc.qrcode.utils.StackManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class SearchMaterialsNoActivity extends Activity {

	private QRCodeDbHelper db = null;
	private Cursor cursor = null;

	private SearchEditText et_searchMaterialsNo = null;
	private ListView materialsNoList = null;
	private Button btn_back = null;
	private Button btn_add = null;

	private List<String> list = null;
	private ArrayAdapter<String> aa = null;

	private String keywords = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);		// 不显示标题
		setContentView(R.layout.activity_search_materialsno);
		// 方案一：
		//MyApplication.getInstance().addActivity(this);
		// 方案二：
		StackManager.getStackManager().pushActivity(this);
		loadingFormation();

		db = QRCodeDbHelper.getInstance(this);
		db.open();

		initData();
	}

	/**
	 * 加载组件
	 */
	private void loadingFormation() {
		btn_back = (Button) this.findViewById(R.id.btn_back_search_materialsNo);
		btn_add = (Button) this.findViewById(R.id.search_title_bar_btn_add_materials_info);

		et_searchMaterialsNo = (SearchEditText) this.findViewById(R.id.search_materialNo);
		materialsNoList = (ListView) this.findViewById(R.id.materialsNo_list);

		setComponentsListener();
	}

	/**
	 * 为组件设置监听器
	 */
	private void setComponentsListener() {
		btn_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		btn_add.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				addMaterialsInfo();
			}
		});
		// 快速搜索
		et_searchMaterialsNo.addTextChangedListener(new TextWatcher() {

			/**
			 * 文件变化时
			 */
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				keywords = et_searchMaterialsNo.getText().toString().trim();
				keywords += "%";
				cursor = db.findMaterialsNoByKeywords(keywords);
				list = new ArrayList<String>();
				while (cursor.moveToNext()) {
					list.add(cursor.getString(1));
				}
				loadDataToAdapter();
			}

			/**
			 * 文本变化前
			 */
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			/**
			 * 文本变化后
			 */
			@Override
			public void afterTextChanged(Editable s) {

			}
		});
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
	 * 初始化数据
	 */
	private void initData() {
		// 注册上下文菜单
		registerForContextMenu(materialsNoList);

		cursor = db.getAllMaterialsNo();
		list = new ArrayList<String>();
		while (cursor.moveToNext()) {
			list.add(cursor.getString(1));
		}
		loadDataToAdapter();
		materialsNoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				lookMaterialsInfo(position);
			}
		});

	}

	/**
	 * 加载数据到适配器
	 */
	private void loadDataToAdapter() {
		aa = new ArrayAdapter<String>(this, R.layout.materialsno_list_item, R.id.tv_materialsNo, list);
		materialsNoList.setAdapter(aa);
	}
	
	/**
	 * 跳转到查看物资信息
	 * @param position
	 */
	private void lookMaterialsInfo(int position) {
		String materialsNo = list.get(position);
		Intent  intent = new Intent();
		intent.putExtra("materialsNo", materialsNo);
		intent.setClass(SearchMaterialsNoActivity.this, LookMaterialsInfoActivity.class);
		SearchMaterialsNoActivity.this.startActivity(intent);
	}
	
	/**
	 * 显示Toast信息
	 * @param string
	 */
	public void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * 创建上下文菜单时触发该方法
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		MenuInflater inflator = new MenuInflater(this);
		//装填R.menu.context对应的菜单， 并添加到menu中
		inflator.inflate(R.menu.contextmenu01, menu);
	}

	/**
	 * 上下文菜单中菜单项被单击时触发该方法
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.item_look_materials_info:
			lookMaterialsInfo(menuInfo.position);
			break;
		case R.id.item_delete_materials_info:
			deleteMaterialsInfoDialog(menuInfo.position);
			break;
		case R.id.item_edit_materials_info:
			editMaterialsInfo(menuInfo.position);
			break;

		}
		//return super.onContextItemSelected(item);
		return true;
	}
	
	/**
	 * 删除物资信息对话框
	 * @param position
	 */
	private void deleteMaterialsInfoDialog(int position) {
		final String materialsNo = list.get(position);
		AlertDialog.Builder builder = new AlertDialog.Builder(SearchMaterialsNoActivity.this);
		builder.setTitle("删除物资信息");
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setMessage("确定要删除该物资信息吗？");
		builder.setPositiveButton("确 定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteMaterialsInfo(materialsNo);
				refresh();
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
	 * 删除物资信息
	 * @param materialsNo
	 */
	protected void deleteMaterialsInfo(String materialsNo) {
		cursor = db.findMaterialsInfoByMaterialsNo(materialsNo);
		String ledgerId = null;
		String cardId = null;
		if (cursor.moveToNext()) {
			ledgerId = cursor.getString(2);
			cardId = cursor.getString(3);
		}
		int count01 = db.deleteLedgerInfoByLedgerId(ledgerId);
		int count02 = db.deleteCardInfoByCardId(cardId);
		int count03 = db.deleteMaterialsInfoByMaterialsNo(materialsNo);
		if (count01 > 0 && count02 > 0 && count03 > 0) {
			showToast("该物资信息删除成功");
		} else {
			showToast("该物资信息删除失败");
		}
	}

	/**
	 * 编辑物资信息
	 * @param position
	 */
	private void editMaterialsInfo(int position) {
		final String materialsNo = list.get(position);
		Intent intent = new Intent();
		intent.putExtra("materialsNo", materialsNo);
		intent.setAction(Intent.ACTION_EDIT);
		intent.setDataAndType(Uri.parse(MyConstants.CONTENT_URI),
				MyConstants.MATERIALS_INFO_CONTENT_TYPE_EDIT);
		startActivity(intent);
	}
	
	/**
	 * 刷新, 这种刷新方法，只有一个Activity实例。
	 */
	public void refresh() {
		initData();
	}
	
	/**
	 * Activity被覆盖后重新显示出来时自动刷新
	 */
	@Override
	protected void onResume() {
		super.onResume();
		cursor = db.getAllMaterialsNo();
		list = new ArrayList<String>();
		while (cursor.moveToNext()) {
			list.add(cursor.getString(1));
		}
		loadDataToAdapter();
		
		keywords = et_searchMaterialsNo.getText().toString().trim();
		keywords += "%";
		cursor = db.findMaterialsNoByKeywords(keywords);
		list = new ArrayList<String>();
		while (cursor.moveToNext()) {
			list.add(cursor.getString(1));
		}
		loadDataToAdapter();
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
