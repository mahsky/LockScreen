package com.mahkey.lc;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.mahkey.lc.receiver.LockScreenReceiver;

public class LockScreenActivity extends Activity {

	private DevicePolicyManager policyManager;

	private ComponentName componentName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lockScreen();
	}

	public void lockScreen() {
		policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		componentName = new ComponentName(this, LockScreenReceiver.class);
		if (policyManager.isAdminActive(componentName)) {// 判断是否有权限(激活了设备管理器)
			policyManager.lockNow();// 直接锁屏
			android.os.Process.killProcess(android.os.Process.myPid());

		} else {
			activeManager();// 激活设备管理器获取权限
		}
	}

	@Override
	protected void onResume() {// 重写此方法用来在第一次激活设备管理器之后锁定屏幕
		if (policyManager != null && policyManager.isAdminActive(componentName)) {
			policyManager.lockNow();
			android.os.Process.killProcess(android.os.Process.myPid());
		}
		super.onResume();
	}

	// 使用隐式意图调用系统方法来激活指定的设备管理器
	private void activeManager() {
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.app_name));
		startActivity(intent);
	}

}
