package org.gditc.qrcode.app;

import org.gditc.qrcode.R;
import org.gditc.qrcode.utils.StackManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

public class WelcomActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StackManager.getStackManager().pushActivity(this);
		/*if (isAddShortCut()) {
			goToLogin();
		} else {
			createShortCut();
		}*/
		createShortCut();
	}
	
	private void goToLogin() {
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), LoginActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 快捷方式是否已创建
	 * @return
	 */
	/*public boolean isAddShortCut() {

        boolean isInstallShortcut = false;
        final ContentResolver cr = this.getContentResolver();

        int versionLevel = android.os.Build.VERSION.SDK_INT;
        String AUTHORITY = "com.android.launcher2.settings";
        
        //2.2以上的系统的文件文件名字是不一样的
        if (versionLevel >= 8) {
            AUTHORITY = "com.android.launcher2.settings";
        } else {
            AUTHORITY = "com.android.launcher.settings";
        }

        final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
                + "/favorites?notify=true");
        Cursor c = cr.query(CONTENT_URI,
                new String[] {"title", "iconResource" }, "title=?",
                new String[] {getString(R.string.app_name)}, null);

        if (c != null && c.getCount() > 0) {
            isInstallShortcut = true;
            c.close();
        }
       
        return isInstallShortcut;
    }*/

	/**
	 * 创建应用的快捷方式
	 */
	public void createShortCut(){
		// 创建快捷方式的Intent
		Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		// 不允许重复创建
		shortcutintent.putExtra("duplicate", false);
		// 需要现实的名称
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
		// 快捷图片
		Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.icon);
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
		// 设置意图和快捷方式关联程序
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(getApplicationContext(), LoginActivity.class));
		//shortcutintent.setAction(Intent.ACTION_MAIN);
		// 发送广播。OK
		sendBroadcast(shortcutintent);
		
	}
	
}
