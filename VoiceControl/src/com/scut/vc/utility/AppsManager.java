package com.scut.vc.utility;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;

public class AppsManager {

	private Activity mActivity;//引用主Activity
	private static int NULL_PACKAGE = -1;// 没有传入包名
	private static int PACKAGE_FOUND = 0;// 成功启动
	private ArrayList<Package_Info> appsList;// 返回程序安装列表
	
	public AppsManager(Activity activity) {
		mActivity = activity;
	}
	

	/**
	 * 扫描系统已经安装软件 如果参数为false则不返回系统包 如果参数为true则返回系统包 返回值就是包含了程序清单的列表
	 * 默认全部获取，即获取包含系统包
	 * 
	 * @param getSysPackages
	 * @return
	 */
	public void ScanAppsList(boolean getSysPackages) {
		appsList = new ArrayList<Package_Info>();
		List<PackageInfo> packsList = mActivity.getPackageManager()
				.getInstalledPackages(0);
		for (int i = 0; i < packsList.size(); i++) {
			PackageInfo packInfo = packsList.get(i);
			if ((!getSysPackages) && (packInfo.versionName == null)) {
				continue;
			}
			Package_Info newInfo = new Package_Info();
			newInfo.mAppName = packInfo.applicationInfo.loadLabel(
					mActivity.getPackageManager()).toString();
			newInfo.mPackageName = packInfo.packageName;
			newInfo.mVersionName = packInfo.versionName;
			newInfo.mVersionCode = packInfo.versionCode;
			newInfo.mIcon = packInfo.applicationInfo.loadIcon(mActivity
					.getPackageManager());
			appsList.add(newInfo);
		}
	}

	/**
	 * 返回程序安装列表
	 * @return
	 */
	public ArrayList<Package_Info> getInstalledAppsList() {
		ScanAppsList(true);
		return appsList;
	}
	
	/**
	 * 返回包的信息
	 * 
	 * @author fatboy
	 * 
	 */
	public class Package_Info {
		public String mAppName = "";
		public String mPackageName = "";
		public String mVersionName = "";
		public int mVersionCode = 0;
		public Drawable mIcon;
		
		public Package_Info(){
		}
		
		public Package_Info(String strAppName, String strPackageName){
			this.mAppName = strAppName;
			this.mPackageName = strPackageName;
		}
		
		public String GetAppName() {
			return this.mAppName;
		}

		public String GetPackageName() {
			return this.mPackageName;
		}

		public void SetAppName(String strAppName) {
			this.mAppName = strAppName;
		}

		public void SetPackageName(String strPackageName) {
			this.mPackageName = strPackageName;
		}
		
	}

	// 打开应用,参数为指定的包名
	public int Execute(String packname) {
		if (packname.equals("")) {
			return NULL_PACKAGE;// 未设置要查询的包名
		}
		Intent intent = new Intent();
		intent = mActivity.getPackageManager().getLaunchIntentForPackage(
				packname);
		mActivity.startActivity(intent);
		return PACKAGE_FOUND;
	}

}
