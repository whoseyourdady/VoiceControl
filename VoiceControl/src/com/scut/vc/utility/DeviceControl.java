/**
 * 该类需要打开如下permission * 
 */
// <uses-permission android:name="android.permission.CAMERA" />
//    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
//    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
//     <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE"/>
//     <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
//    <uses-permission android:name="android.permission.WAKE_LOCK" />
//    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
//    <uses-permission android:name="android.permission.BLUETOOTH" />
//    <uses-permission android:name="android.permission.WRITE_SETTINGS"/>

package com.scut.vc.utility;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.Settings;

/**
 * 这个类是做设备控制的 为了适应android2.1 所以用的都是很老的办法 没有用到SETTING类
 * 
 * @author fatboy
 * 
 */
public class DeviceControl {
	/**
	 * 获得主activity
	 */
	private Activity mActivity;

	private Camera mCamera;// 手机筒
	private Camera.Parameters parameter; // 手电筒参数
	private WifiManager mWifiManager;// wifi管理器
	private BluetoothAdapter mBlueTooth;// 蓝牙管理器
	private ConnectivityManager mConnMan;// 手机连接管理器

	private Intent mGpsIntent;

	public DeviceControl(Activity activity) {
		mActivity = activity;

		initial();// 初始化
	}

	private void initial() {
		/**
		 * 手电筒初始化
		 */
		//mCamera = Camera.open(Camera.getNumberOfCameras() - 1);
		mCamera = Camera.open();
		parameter = mCamera.getParameters();

		/**
		 * GPS
		 */
		mGpsIntent = new Intent();
		mGpsIntent.setClassName("com.android.settings",
				"com.android.settings.widget.SettingsAppWidgetProvider");
		mGpsIntent.addCategory("android.intent.category.ALTERNATIVE");
		mGpsIntent.setData(Uri.parse("custom:3"));

		// try {
		// PendingIntent.getBroadcast(this, 0, mGpsIntent, 0).send();
		// } catch (CanceledException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		/**
		 * WIFI
		 * 
		 */
		mWifiManager = (WifiManager) mActivity
				.getSystemService(Context.WIFI_SERVICE);

		/**
		 * GPRS
		 */
		mConnMan = (ConnectivityManager) mActivity
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		/**
		 * 蓝牙
		 */
		mBlueTooth = BluetoothAdapter.getDefaultAdapter();
	}

	/**
	 * 开关蓝牙
	 * 
	 * @param enable
	 */
	private void EnableBlueTooth(boolean enable) {
		if (enable) {
			mBlueTooth.enable();
		} else {
			mBlueTooth.disable();
		}
	}

	/**
	 * 开关WIFI
	 */
	private void EnableWiFi(boolean enable) {
		if (enable) {
			mWifiManager.setWifiEnabled(true);
		} else {
			mWifiManager.setWifiEnabled(false);
		}
	}

	/**
	 * 开关GPS
	 */
	private void EnableGps() {
		try {
			PendingIntent.getBroadcast(mActivity, 0, mGpsIntent, 0).send();
		} catch (CanceledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 开关手电筒
	 * 
	 * @param enable
	 */
	private void EnableTorch(boolean enable) {
		if (enable) {
			parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
			mCamera.setParameters(parameter);
		} else {
			parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
			mCamera.setParameters(parameter);
		}
	}

	/**
	 * 开关GPRS
	 * 
	 * @param enable
	 */
	private void EnableGprs(boolean enable) {
		Class<?> conMgrClass = null; // ConnectivityManager类
		Field iConMgrField = null; // ConnectivityManager类中的字段
		Object iConMgr = null; // IConnectivityManager类的引用
		Class<?> iConMgrClass = null; // IConnectivityManager类
		Method setMobileDataEnabledMethod = null; // setMobileDataEnabled方法

		// 取得ConnectivityManager类
		try {
			conMgrClass = Class.forName(mConnMan.getClass().getName());
			// 取得ConnectivityManager类中的对象mService
			iConMgrField = conMgrClass.getDeclaredField("mService");
			// 设置mService可访问
			iConMgrField.setAccessible(true);
			// 取得mService的实例化类IConnectivityManager
			iConMgr = iConMgrField.get(mConnMan);
			// 取得IConnectivityManager类
			iConMgrClass = Class.forName(iConMgr.getClass().getName());
			// 取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法
			setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod(
					"setMobileDataEnabled", Boolean.TYPE);
			// 设置setMobileDataEnabled方法可访问
			setMobileDataEnabledMethod.setAccessible(true);
			// 调用setMobileDataEnabled方法
			setMobileDataEnabledMethod.invoke(iConMgr, enable);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 开关飞行模式
	 * 
	 * @param enable
	 */
	private void EnableFlyingMode(boolean enable) {
		if (enable) {
			Settings.System.putInt(mActivity.getContentResolver(),
					Settings.System.AIRPLANE_MODE_ON, 1);
			Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
			intent.putExtra("state", true);
			mActivity.sendBroadcast(intent);
		} else {
			Settings.System.putInt(mActivity.getContentResolver(),
					Settings.System.AIRPLANE_MODE_ON, 0);
			Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
			intent.putExtra("state", false);
			mActivity.sendBroadcast(intent);
		}
	}

	/**
	 * 很老的方法 用来测试GPS是否存在
	 * 
	 * @return
	 */
	private boolean isGpsEnabled() {
		String str = Settings.Secure.getString(mActivity.getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (str != null) {
			return str.contains("gps");
		} else {
			return false;
		}
	}

	public void Release() {
		mCamera.release();
	}

	public void EnableDevice(String device) {
		if (device.equals("wifi")) {
			EnableWiFi(true);
		} else if (device.equals("gprs")) {
			EnableGprs(true);
		} else if (device.equals("gps")) {
			EnableGps();
		} else if (device.equals("bluetooh")) {
			EnableBlueTooth(true);
		} else if (device.equals("flash")) {
			EnableTorch(true);
		} else if (device.equals("airplanemode")) {
			EnableFlyingMode(true);
		}

	}

	public void DisableDevice(String device) {
		if (device.equals("wifi")) {
			EnableWiFi(false);
		} else if (device.equals("gprs")) {
			EnableGprs(false);
		} else if (device.equals("gps")) {
			EnableGps();
		} else if (device.equals("bluetooh")) {
			EnableBlueTooth(false);
		} else if (device.equals("flash")) {
			EnableTorch(false);
		} else if (device.equals("airplanemode")) {
			EnableFlyingMode(false);
		}
	}

}
