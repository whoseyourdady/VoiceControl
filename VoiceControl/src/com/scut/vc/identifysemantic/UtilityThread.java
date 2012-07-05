package com.scut.vc.identifysemantic;

import android.app.Activity;

import com.scut.vc.utility.AppsManager;
import com.scut.vc.utility.Contact;

public class UtilityThread implements Runnable {

	private Activity mActivity ;
	private AppsManager mAppsManager;
	private Contact mContact;
	
	public UtilityThread(Activity activity) {
		mActivity = activity;
		mAppsManager = new AppsManager(mActivity);
		mContact = new Contact(mActivity);
	}
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			try {
				if (SemanticIdentify.appLock) {
					SemanticIdentify.appsList = mAppsManager.getInstalledAppsList();
					SemanticIdentify.appLock = false;
				}
				if (SemanticIdentify.contactLock) {
					SemanticIdentify.contactPersonList = mContact.GetPersonList();
					SemanticIdentify.contactLock = false;
				}
				Thread.sleep(2000L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block			
				e.printStackTrace();
			}
		}
		
	}

}
