package com.mahkey.lc;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.mahkey.lc.receiver.LockScreenReceiver;
import com.mahkey.lc.utils.UiUtils;

/**
 * 锁屏app 主界面
 * 
 * @author maoah
 * 
 */
public class MainActivity extends PreferenceActivity implements OnClickListener {

	private DevicePolicyManager policyManager;

	private ComponentName componentName;

	private CheckBoxPreference activityDevice;
	private Preference uninstall;
	private Preference sendShortcut;
	private Preference donate;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		initView();
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		activityDevice = (CheckBoxPreference) findPreference(getString(R.string.activate_devicemanager_key));
		uninstall = findPreference(getString(R.string.uninstall_key));
		sendShortcut = findPreference(getString(R.string.send_shortcut_key));
		donate = findPreference(getString(R.string.donate_key));

		activityDevice.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				startDevice();
				return true;
			}
		});

		uninstall.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				uninstallAPK();
				return false;
			}
		});

		sendShortcut.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				addShortCut(getString(R.string.shortcut_name));
				Toast.makeText(MainActivity.this, "已发送快捷方式到桌面", Toast.LENGTH_LONG).show();
				return false;
			}
		});
		donate.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				UiUtils.startBrowse(MainActivity.this, "https://me.alipay.com/maoah");
				return false;
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lock_btn:
			startDevice();
			break;

		default:
			break;
		}
	}

	public void startDevice() {
		policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		componentName = new ComponentName(this, LockScreenReceiver.class);
		if (!policyManager.isAdminActive(componentName)) {// 判断是否有权限(激活了设备管理器)
			activeManager();// 激活设备管理器获取权限
		} else {
			bind();
			activityDevice.setChecked(false);
		}
	}

	// 解除绑定
	public void bind() {
		if (componentName != null) {
			policyManager.removeActiveAdmin(componentName);
		}
	}

	// 重写此方法用来在第一次激活设备管理器之后锁定屏幕
	@Override
	protected void onResume() {
		super.onResume();
		if (policyManager != null) {
			if (policyManager.isAdminActive(componentName)) {// 判断是否有权限(激活了设备管理器)
				activityDevice.setChecked(true);
			} else {
				activityDevice.setChecked(false);
			}
		}
	}

	// 使用隐式意图调用系统方法来激活指定的设备管理器
	private void activeManager() {
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.app_name));
		startActivity(intent);
	}

	/**
	 * uninstall apk file
	 * 
	 * @param packageName
	 */
	public void uninstallAPK() {
		try {
			PackageInfo info = getPackageManager().getPackageInfo(this.getPackageName(), 0);
			Uri uri = Uri.parse("package:" + info.packageName);
			Intent intent = new Intent(Intent.ACTION_DELETE, uri);
			this.startActivity(intent);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void addShortCut(String tName) {
		// 安装的Intent
		Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");

		// 快捷名称
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, tName);
		// 快捷图标是允许重复
		shortcut.putExtra("duplicate", false);

		Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
		shortcutIntent.putExtra("tName", tName);
		// shortcutIntent.setClassName("com.mahkey.lc",
		// "com.mahkey.lc.LockScreenActivity");
		shortcutIntent.setClass(this, LockScreenActivity.class);
		shortcutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);

		// 快捷图标
		ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_launcher);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

		// 发送广播
		sendBroadcast(shortcut);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_share:
			share();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void share() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
		intent.putExtra(Intent.EXTRA_TEXT, "I would like to share this with you...");
		startActivity(Intent.createChooser(intent, getTitle()));
	}
}
