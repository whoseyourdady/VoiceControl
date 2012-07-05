package com.scut.vc.identifysemantic;



import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

public class ContactObsever extends ContentObserver {


	public ContactObsever(Handler handler) {
		super(handler);

		// TODO Auto-generated constructor stub
	}

	@Override
	public void onChange(boolean selfChange) {
		// TODO Auto-generated method stub
		SemanticIdentify.contactLock = true;
		Log.d("app", "new");
		super.onChange(selfChange);
	}
	
	

}
