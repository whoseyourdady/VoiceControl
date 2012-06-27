package com.scut.vc.xflib;
//package com.scut.vc.utility;
//
//import java.util.ArrayList;
//
//
//
//import com.iflytek.speech.RecognizerResult;
//import com.iflytek.speech.SpeechError;
//import com.iflytek.speech.SpeechConfig.RATE;
//import com.iflytek.ui.RecognizerDialog;
//import com.iflytek.ui.RecognizerDialogListener;
//import com.scut.vc.ui.R;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.TextView;
//
//public class XFVoiceActivity extends Activity implements
//RecognizerDialogListener, OnClickListener {
//	private String info;
//	private TextView infos;
//	private Button speak_btn;
//	private Button set_btn;
//	private SharedPreferences mSharedPreferences;
//	private RecognizerDialog iatDialog;
//	/** Called when the activity is first created. */
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		//setContentView(R.layout.main);
//		//infos = (TextView)findViewById(R.id.xunfei_infos);
//		//speak_btn = (Button)findViewById(R.id.speak_xunfei);
//		//set_btn = (Button)findViewById(R.id.set);
//		speak_btn.setOnClickListener(this);
//		set_btn.setOnClickListener(this);
//		iatDialog = new RecognizerDialog(this, "appid=" +getString(R.string.app_id));
//		iatDialog.setListener(this);
//		mSharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
//
//	}
//
//	public void onStart(){
//		super.onStart();
//		String engine = mSharedPreferences.getString(
//				getString(R.string.preference_key_iat_engine),
//				getString(R.string.preference_default_iat_engine));
//		String[] engineEntries = getResources().getStringArray(
//				R.array.preference_entries_iat_engine);
//		String[] engineValues = getResources().getStringArray(
//				R.array.preference_values_iat_engine);
//				for (int i = 0; i < engineValues.length; i++) {
//					if (engineValues[i].equals(engine)) {
//						infos.setText(engineEntries[i]);
//						break;
//					}
//				}
//
//	}
//
//
//
//
//	@Override
//	public void onEnd(SpeechError arg0) {
//		// TODO Auto-generated method stub
//
//	}
//	@Override
//	public void onResults(ArrayList<RecognizerResult> results, boolean arg1) {
//		// TODO Auto-generated method stub
//		StringBuilder builder = new StringBuilder();
//		for (RecognizerResult recognizerResult : results) {
//			builder.append(recognizerResult.text);
//		}
//		infos.append(builder);
//		//infos.setSelection(infos.length());
//	}
//	
//
//	@Override
//	public void onClick(View v) {
//		// TODO Auto-generated method stub
//		switch(v.getId()){
//		case R.id.speak_xunfei:
//			showIatDialog();
//			break;
//		case R.id.set:
//			//startActivity(new Intent(this,IatPreferenceActivity.class));
//			break;
//		default:
//			break;
//		}
//
//	}
//
//	public void showIatDialog() {
//		// TODO Auto-generated method stub
//		String engine = mSharedPreferences.getString(
//				getString(R.string.preference_key_iat_engine),
//				getString(R.string.preference_default_iat_engine));
//
//		String area = null;
//		if (IatPreferenceActivity.ENGINE_POI.equals(engine)) {
//			final String defaultProvince = getString(R.string.preference_default_poi_province);
//			String province = mSharedPreferences.getString(
//					getString(R.string.preference_key_poi_province),
//					defaultProvince);
//			final String defaultCity = getString(R.string.preference_default_poi_city);
//			String city = mSharedPreferences.getString(
//					getString(R.string.preference_key_poi_city),
//					defaultCity);
//
//			if (!defaultProvince.equals(province)) {
//				area = "area=" + province;
//				if (!defaultCity.equals(city)) {
//					area += city;
//				}
//			}
//		}
//
//		iatDialog.setEngine(engine, area, null);
//
//		String rate = mSharedPreferences.getString(
//				getString(R.string.preference_key_iat_rate),
//				getString(R.string.preference_default_iat_rate));
//		if(rate.equals("rate8k"))
//			iatDialog.setSampleRate(RATE.rate8k);
//		else if(rate.equals("rate11k"))
//			iatDialog.setSampleRate(RATE.rate11k);
//		else if(rate.equals("rate16k"))
//			iatDialog.setSampleRate(RATE.rate16k);
//		else if(rate.equals("rate22k"))
//			iatDialog.setSampleRate(RATE.rate22k);
//		infos.setText(null);
//		
//		iatDialog.show();
//	}
//}