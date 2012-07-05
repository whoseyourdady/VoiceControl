package com.scut.vc.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechConfig.RATE;
import com.iflytek.speech.SpeechError;
import com.iflytek.speech.SynthesizerPlayer;
import com.iflytek.ui.RecognizerDialog;
import com.iflytek.ui.RecognizerDialogListener;
import com.scut.vc.alarm.AlarmService;
import com.scut.vc.identifysemantic.ContactObsever;
import com.scut.vc.identifysemantic.IdentifyThread;
import com.scut.vc.identifysemantic.UtilityThread;
import com.scut.vc.utility.Alarm;
import com.scut.vc.utility.AppsManager;
import com.scut.vc.utility.Contact;
import com.scut.vc.utility.DeviceControl;
import com.scut.vc.utility.Task;
import com.scut.vc.utility.Weather;
import com.scut.vc.utility.WebSearch;
import com.scut.vc.xflib.ChatAdapter;
import com.scut.vc.xflib.ChatEng;

public class MainActivity extends Activity implements RecognizerDialogListener,
		OnClickListener {
	/** Called when the activity is first created. */
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

	private AppsManager mAppManager;
	private Contact mContact;
	private DeviceControl mDevCon;
	private WebSearch mWebSearch;

	private Weather mWeather;

	private ArrayList<ChatEng> list;
	private ChatAdapter cad;
	private ListView chatList;

	private RecognizerDialog iatDialog;
	private String infos;

	public static String voiceString = "";// 语音服务提供商返回的处理字符串
	public static String voiceTempString = ""; // 讯飞语音返回临时存放的字符串

	public ProgressBar pd;// 识别中进度条


	private ImageButton ib; // 识别按钮

	private boolean showProgressDiaglog = false;
	public static boolean EnableGoogleVoice = false;// 使用google API
	public static boolean EnableXunfeiVoice = true;// 使用讯飞 API

	private IdentifyThread mIdentifyThread;// 语义识别多线程
	private UtilityThread mUtilityThread;// 扫描程序，联系人线程
	
	private ContactObsever mContactObserver;// 监听联系人更改


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Thread thread2 = new Thread((mUtilityThread = new UtilityThread(this)));
		thread2.start();

		inital();

		Thread thread1 = new Thread(
				(mIdentifyThread = new IdentifyThread(this)));
		thread1.start();

		/**
		 * 测度代码;
		 */
		updateListView(R.layout.chat_helper, "有什么可以帮到您？");

		// 自启动识别
		SharedPreferences sharedata_start = getSharedPreferences("startTurn",
				MODE_WORLD_READABLE);
		boolean startTurn = sharedata_start.getBoolean("startTurn", false);// 如果不能正确获取自启动识别的选项，则以“0”为默认值，表示不自启动识别
		System.out.println("startTurn = " + startTurn);

		if (startTurn) {
			SharedPreferences sharedata1 = getSharedPreferences("voiceEngine",
					MODE_WORLD_READABLE);
			String voiceEngine = sharedata1.getString("voiceEngine", "1");// 如果不能正确获取语义引擎选项的数据，则以第一项为值
			System.out.println("voiceEngine = " + voiceEngine);

			// 由于不是所有人的手机都有谷歌自带的语音库，所以这里默认以科大讯飞启动
			if (voiceEngine.equals("1")) {// EnableXunfeiVoice
				showIatDialog();
			
			} else if (voiceEngine.equals("2")) {// EnableGoogleVoice
				startVoiceRecognitionActivity();
	
			}
		}


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/*
		 * add()方法的四个参数，依次是： 1、组别，如果不分组的话就写Menu.NONE,
		 * 2、Id，这个很重要，Android根据这个Id来确定不同的菜单 3、顺序，那个菜单现在在前面由这个参数的大小决定
		 * 4、文本，菜单的显示文本
		 */
		menu.add(Menu.NONE, Menu.FIRST + 1, 1, "设置");
		menu.add(Menu.NONE, Menu.FIRST + 2, 2, "帮助");
		menu.add(Menu.NONE, Menu.FIRST + 3, 3, "闹钟列表");
		menu.add(Menu.NONE, Menu.FIRST + 4, 4, "退出");

		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		/*
		 * // 获取可用于全局的句柄来操作Preference SharedPreferences sharedata1 =
		 * getSharedPreferences("list1",MODE_WORLD_READABLE); String data =
		 * sharedata1.getString("item", null); System.out.println("data = " +
		 * data);
		 */

		switch (item.getItemId()) {
		case Menu.FIRST + 1:
			Toast.makeText(this, "打开设置界面", Toast.LENGTH_SHORT).show();
			Intent intent2 = new Intent();
			intent2.setClass(this, SettingActivity.class);
			startActivity(intent2);
			break;
		case Menu.FIRST + 2:
			Toast.makeText(this, "打开帮助界面", Toast.LENGTH_SHORT).show();
			Intent intent1 = new Intent();
			intent1.setClass(this, HelpActivity.class);
			startActivity(intent1);
			break;

		case Menu.FIRST + 3:
			Toast.makeText(this, "打开闹钟列表", Toast.LENGTH_SHORT).show();
			Intent intent3 = new Intent();
			intent3.setClass(this, AlarmActivity.class);
			startActivity(intent3);
			break;
		case Menu.FIRST + 4:
			Toast.makeText(this, "退出应用程序", Toast.LENGTH_SHORT).show();
			Intent i = new Intent(MainActivity.this, AlarmService.class);
			stopService(i);
			android.os.Process.killProcess(android.os.Process.myPid());

			break;
		}
		return false;
	}

	/**
	 * 初始化实体类
	 */
	private void inital() {

		/**
		 * 初始化一些控制对象
		 */
		mAppManager = new AppsManager(this);
		mContact = new Contact(this);
		mDevCon = new DeviceControl(this);
		mWebSearch = new WebSearch(this);

		list = new ArrayList<ChatEng>();
		cad = new ChatAdapter(MainActivity.this, list);
		chatList = (ListView) findViewById(R.id.chatlist);
		ib = (ImageButton) findViewById(R.id.helper_voice);

		/**
		 * 语义解析时的progressBar显示和文字说明
		 */

		pd = (ProgressBar) findViewById(R.id.progressBar2);


		pd.setVisibility(View.INVISIBLE);
	

		/**
		 * 讯飞窗口初始化
		 */
		iatDialog = new RecognizerDialog(this, "appid="
				+ getString(R.string.app_id));
		iatDialog.setListener(this);

		/**
		 * 对话筒按钮的响应
		 */
		ib.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				return false;
			}
		});

		ib.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				// 获取语音引擎选项的数据
				// 下一句需做版本上的兼容
				// SharedPreferences sharedata1 = getSharedPreferences(
				// "voiceEngine", MODE_WORLD_READABLE | MODE_MULTI_PROCESS);
				SharedPreferences sharedata1 = getSharedPreferences(
						"voiceEngine", MODE_WORLD_READABLE);
				String voiceEngine = sharedata1.getString("voiceEngine", "1");// 如果不能正确获取语义引擎选项的数据，则以第一项为值
				System.out.println("voiceEngine = " + voiceEngine);

				// 由于不是所有人的手机都有谷歌自带的语音库，所以这里默认以科大讯飞启动
				if (voiceEngine.equals("1")) {// EnableXunfeiVoice
					showIatDialog();
		
				} else if (voiceEngine.equals("2")) {// EnableGoogleVoice
					startVoiceRecognitionActivity();
			

				}
			}

		});

		mContactObserver = new ContactObsever( null);
		registerObsever();

	}

	/**
	 * 事务的处理
	 */
	public Handler mhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			Task task = (Task) msg.obj;
			switch (task.getTaskID()) {
			case Task.CALL: {
				@SuppressWarnings("unchecked")
				ArrayList<Contact.ContactPerson> callList = (ArrayList<Contact.ContactPerson>) task
						.getTaskParam();
				ShowContactSelectDialog(callList, task);

			}
				break;
			case Task.SendMessage: {

				@SuppressWarnings("unchecked")
				ArrayList<Contact.ContactPerson> msgList = (ArrayList<Contact.ContactPerson>) task
						.getTaskParam();

				ShowContactSelectDialog(msgList, task);

			}
				break;
			case Task.OpenApp: {

				@SuppressWarnings("unchecked")
				ArrayList<AppsManager.Package_Info> appList = (ArrayList<AppsManager.Package_Info>) task
						.getTaskParam();
				ShowAppSelectDialog(appList, task);

			}
				break;
			case Task.Search: {
				String search = (String) task.getTaskParam();
				mWebSearch.Execute(search);
			}
				break;
			case Task.SwitchOnDevice: {
				DeviceControl.Device device = (DeviceControl.Device) task
						.getTaskParam();
				mDevCon.Execute(device);
			}
				break;
			case Task.SetAlarm: {
				String strvoice = (String) task.getTaskParam();
				Alarm alarm = new Alarm(MainActivity.this, strvoice);
				alarm.Execute();
			}
				break;
			case Task.Weather: {
				String weatherInfos = (String) task.getTaskParam();
				updateListView(R.layout.chat_helper, weatherInfos);
			}
				break;
			case Task.ShowProcess: {
				if (!showProgressDiaglog) {
					pd.setVisibility(View.VISIBLE);
				
					ib.setClickable(false);

					showProgressDiaglog = true;
				} else {
					
					ib.setClickable(true);
					showProgressDiaglog = false;
					pd.setVisibility(View.INVISIBLE);
				}
			}
				break;
			case Task.IdentifyError: {

	

				updateListView(R.layout.chat_helper2, "对不起哦，找不到你的命令");

			}
			default: {
			
			}
			}

			super.handleMessage(msg);
		}
	};

	/**
	 * 如果是机器讲话，resId就赋值R.layout.chat_helper; 如果是人讲话，resId就赋值R.layout.chat_user
	 * 
	 * @param resId
	 * @param speekInfo
	 */
	public void updateListView(int resId, String speekInfo) {

		ChatEng ce = new ChatEng(speekInfo, resId);
		list.add(ce);
		chatList.setAdapter(cad);
		// cad.notify();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * Fire an intent to start the speech recognition activity.
	 */
	private void startVoiceRecognitionActivity() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				"Speech recognition demo");
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}

	/**
	 * Handle the results from the recognition activity. 谷歌API返回的结果
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE
				&& resultCode == RESULT_OK) {
			// Fill the list view with the strings the recognizer thought it
			// could have heard
			ArrayList matches = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

			voiceString = matches.get(0).toString();
			updateListView(R.layout.chat_user, voiceString);
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	public void showIatDialog() {
		// TODO Auto-generated method stub
		String engine = "sms";
		String area = null;
		// voiceString = "";
		iatDialog.setEngine(engine, area, null);
		iatDialog.setSampleRate(RATE.rate8k);

		iatDialog.show();

	}

	@Override
	public void onStart() {
		super.onStart();

		String engine = "sms";
		String[] engineEntries = getResources().getStringArray(
				R.array.preference_entries_iat_engine);
		String[] engineValues = getResources().getStringArray(
				R.array.preference_values_iat_engine);
		for (int i = 0; i < engineValues.length; i++) {
			if (engineValues[i].equals(engine)) {
				infos = engineEntries[i];
				break;
			}
		}

	}

	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	public void onEnd(SpeechError arg0) {
		// TODO Auto-generated method stub
		if (!voiceTempString.equals("")) {
			voiceString = voiceTempString.substring(0,
					voiceTempString.length() - 1);
			updateListView(R.layout.chat_user, voiceString);
			voiceTempString = "";
		}
	}

	/**
	 * 讯飞传回的结果
	 */
	public void onResults(ArrayList<RecognizerResult> arg0, boolean arg1) {
		// TODO Auto-generated method stub
		// voiceString = "";
		for (int i = 0; i < arg0.size(); i++) {
			RecognizerResult recognizerResult = arg0.get(i);
			voiceTempString += recognizerResult.text;
		}

	}

	/**
	 * 讯飞语音合成
	 * 
	 * @param helperStr
	 */
	public void speakString(String helperStr) {

		SynthesizerPlayer player = SynthesizerPlayer.createSynthesizerPlayer(
				this, "appid=" + getString(R.string.app_id));
		player.setVoiceName(getString(R.string.preference_default_tts_role));
		player.setSampleRate(RATE.rate16k);

		player.setSpeed(75);
		player.setVolume(75);
		player.playText(helperStr, "ent=vivi21,bft=2", null);

	}

	/**
	 * 联系人对列表框的重用
	 * 
	 * @param items
	 * @param task
	 */
	public void ShowContactSelectDialog(
			final ArrayList<Contact.ContactPerson> list, final Task task) {
		final String[] items = new String[list.size()];
		for (int n = 0; n < list.size(); n++) {
			items[n] = ((Contact.ContactPerson) list.get(n)).GetName();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("请选择").setItems(items,
				new android.content.DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String num = ((Contact.ContactPerson) list.get(which))
								.GetNumber();
						if (task.getTaskID() == Task.CALL) {
							mContact.CallPerson(num);
						} else if (task.getTaskID() == Task.SendMessage) {
							mContact.SendMsg(num, "");
						}

					}
				});

		AlertDialog dialog = builder.create();
		dialog.show();

	}

	/**
	 * 应用程序对列表框的重用
	 * 
	 * @param items
	 * @param task
	 */
	public void ShowAppSelectDialog(
			final ArrayList<AppsManager.Package_Info> list, final Task task) {
		final String[] items = new String[list.size()];
		for (int n = 0; n < list.size(); n++) {
			items[n] = ((AppsManager.Package_Info) list.get(n)).GetAppName();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("请选择").setItems(items,
				new android.content.DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						AppsManager.Package_Info packInfo = list.get(which);
						mAppManager.Execute(packInfo.GetPackageName());
					}
				});

		AlertDialog dialog = builder.create();
		dialog.show();

	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		DeviceControl.Device device = mDevCon.new Device("flash", false);
		mDevCon.Execute(device);
		super.onStop();
	}

	/**
	 * 清空内存
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mAppManager = null;
		mContact = null;
		mDevCon.Release();
		mDevCon = null;
		mWebSearch = null;

		list = null;
		cad = null;
		chatList = null;

		iatDialog = null;
		voiceString = "";// 语音服务提供商返回的处理字符串
		pd = null;
		android.os.Process.killProcess(android.os.Process.myPid());
		mUtilityThread = null;// 语义识别的多线程
		mIdentifyThread = null;
		super.onDestroy();
	}

	private void Test(Task task) {
		Message msg = new Message();
		msg.obj = task;
		mhandler.sendMessage(msg);
	}

	public DeviceControl getDevice() {
		return mDevCon;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			MainActivity.this.finish();
			Intent i = new Intent(MainActivity.this, AlarmService.class);
			stopService(i);

			Log.v("Work", "MainActivity End");
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void registerObsever() {
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		this.getContentResolver().registerContentObserver(uri, false,
				mContactObserver);
	}
	
}