package com.scut.vc.utility;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class Contact {

	private Activity mActivity;
	private ArrayList<ContactPerson> mContactsPersonList;

	public Contact(Activity activity) {
		mActivity = activity;
		mContactsPersonList = null;
	}

	public void CallPerson(String phoneNum) {

		Uri uri = Uri.parse("tel:" + phoneNum);
		Intent intent = new Intent(Intent.ACTION_CALL, uri);
		mActivity.startActivity(intent);

	}

	public void SendMsg(String phoneNum, String strMsg) {
		Uri smsToUri = Uri.parse("smsto:" + phoneNum);
		Intent mIntent = new Intent(android.content.Intent.ACTION_SENDTO,
				smsToUri);
		mIntent.putExtra("sms_body", strMsg);
		mActivity.startActivity(mIntent);
	}

	private void GetContactInfo() {

		mContactsPersonList = new ArrayList<ContactPerson>();
		
		Cursor cursor = mActivity.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		String contactsName;
		String contactsNumber;
		while (cursor.moveToNext()) {
			contactsName = "null";
			contactsNumber = "null";
			ContactPerson cp = new ContactPerson();
	
			String contactId = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts._ID));
	
			contactsName = cursor
					.getString(cursor
							.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
			
			String hasPhone = cursor
					.getString(cursor
							.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
			if (hasPhone.equalsIgnoreCase("1")) {
				hasPhone = "true";
			} else {
				hasPhone = "false";
			}

		
			if (Boolean.parseBoolean(hasPhone)) {
				Cursor phones = mActivity.getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = " + contactId, null, null);
				while (phones.moveToNext()) {
					contactsNumber = phones
							.getString(phones
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				}
				phones.close();
			}
			cp.SetName(contactsName);
			cp.SetNumber(contactsNumber);
			mContactsPersonList.add(cp);
		}
		cursor.close();
	}


	public class ContactPerson {

		private String mContactsName;
		private String mContactsNumber;

		public ContactPerson() {

		}
		
		public ContactPerson(String name, String number) {
			mContactsName = name;
			mContactsNumber = number;
		}

		public String GetName() {
			return mContactsName;
		}

		public String GetNumber() {
			return mContactsNumber;
		}

		public void SetName(String name) {
			mContactsName = name;
		}

		public void SetNumber(String number) {
			mContactsNumber = number;
		}

	}

	public ArrayList<ContactPerson> GetPersonList() {
		if(mContactsPersonList == null) GetContactInfo();
		return mContactsPersonList;
	}
}
