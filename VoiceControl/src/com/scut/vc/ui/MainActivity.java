package com.scut.vc.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechConfig.RATE;
import com.iflytek.speech.SpeechError;
import com.iflytek.ui.RecognizerDialog;
import com.iflytek.ui.RecognizerDialogListener;
import com.scut.vc.identifysemantic.IdentifyThread;
import com.scut.vc.identifysemantic.SemanticIdentify;
import com.scut.vc.utility.Alarm;
import com.scut.vc.utility.AppsManager;
import com.scut.vc.utility.Contact;
import com.scut.vc.utility.DeviceControl;
import com.scut.vc.utility.Task;
import com.scut.vc.utility.WebSearch;
import com.scut.vc.utility.Contact.ContactPerson;
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
	private SemanticIdentify mIdentify;

	private ArrayList<ChatEng> list;
	private ChatAdapter cad;
	private ListView chatList;

	private SharedPreferences mSharedPreferences;
	private RecognizerDialog iatDialog;
	private String infos = null;
	public static String voiceString = "";// 语音服务提供商返回的处理字符串
	public ProgressDialog pd;// 识别中进度条
	private boolean showProgressDiaglog = false;
	public static boolean EnableGoogleVoice = false;// 使用google API
	public static boolean EnableXunfeiVoice = true;// 使用讯飞 API

	private IdentifyThread mThread;// 语义识别的多线程

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		inital();
		Thread thread = new Thread((mThread = new IdentifyThread(this)));
		thread.start();
		


		/**
		 * 测度代码;
		 */
		ArrayList<Contact.ContactPerson> callTarget = new ArrayList<Contact.ContactPerson>();// 打电话列表
		Contact.ContactPerson contactPerson1 = mContact.new ContactPerson(
				"中国移动A", "10086");
		
		Contact.ContactPerson contactPerson2 = mContact.new ContactPerson(
				"中国移动B", "13800138000");
		
		callTarget.add(contactPerson1);
		
		callTarget.add(contactPerson2);
		// Task task = new Task(Task.OpenApp, "com.ihandysoft.alarmclock");
		//Task task = new Task(Task.Search, "com.android.soundrecorder");

		Task task = new Task(Task.SendMessage, callTarget);
		//Task task = new Task(Task.SetAlarm, "大闹天宫闹钟");
		Test(task);
		
		//voiceString = "大闹天宫闹钟";


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/*
		 * add()方法的四个参数，依次是： 1、组别，如果不分组的话就写Menu.NONE,
		 * 2、Id，这个很重要，Android根据这个Id来确定不同的菜单 3、顺序，那个菜单现在在前面由这个参数的大小决定
		 * 4、文本，菜单的显示文本
		 */
		menu.add(Menu.NONE, Menu.FIRST + 1, 1, "帮助");
		menu.add(Menu.NONE, Menu.FIRST + 2, 1, "设置");
		menu.add(Menu.NONE, Menu.FIRST + 3, 3, "退出");
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
			Toast.makeText(this, "打开帮助界面", Toast.LENGTH_SHORT).show();
			Intent intent1 = new Intent();
			intent1.setClass(this, HelpActivity.class);
			startActivity(intent1);
			break;
		case Menu.FIRST + 2:
			Toast.makeText(this, "打开设置界面", Toast.LENGTH_SHORT).show();
			Intent intent2 = new Intent();
			intent2.setClass(this, SettingActivity.class);
			startActivity(intent2);
			break;
		case Menu.FIRST + 3:
			Toast.makeText(this, "退出应用程序", Toast.LENGTH_SHORT).show();
			Intent intent3 = new Intent();
			intent3.setClass(this, HelpActivity.class);
			startActivity(intent3);
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
		mIdentify = new SemanticIdentify(this);

		list = new ArrayList<ChatEng>();
		cad = new ChatAdapter(MainActivity.this, list);
		chatList = (ListView) findViewById(R.id.chatlist);
		ImageButton ib = (ImageButton) findViewById(R.id.helper_voice);

		/**
		 * 语义解析时的progressBar显示
		 */
		pd = new ProgressDialog(this);
		pd.setMessage("正在解析...");

		/**
		 * 讯飞窗口初始化
		 */
		iatDialog = new RecognizerDialog(this, "appid="
				+ getString(R.string.app_id));
		iatDialog.setListener(this);

		/**
		 * 对话筒按钮的响应
		 */
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

				if (voiceEngine.equals("1")) {// EnableGoogleVoice
					startVoiceRecognitionActivity();
//					voiceString = "23点开会";
//					updateListView(R.layout.chat_user, voiceString);
				} else if (voiceEngine.equals("2")) {// EnableXunfeiVoice
					showIatDialog();
//					voiceString = "23点半开会";
//					updateListView(R.layout.chat_user, voiceString);
				}
			}

		});

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
				if (0 == callList.size()) {
					mAppManager.Execute("com.android.contacts");
				} else if (1 == callList.size()) {
					String phoneNum = callList.get(0).GetNumber();
					mContact.CallPerson(phoneNum);
				} else if (1 < callList.size()) {
					ShowSelectDialog(callList, task);
				}
			}
				break;
			case Task.SendMessage: {
			
				
				@SuppressWarnings("unchecked")
				ArrayList<Contact.ContactPerson> msgList = (ArrayList<Contact.ContactPerson>) task
						.getTaskParam();
				if (0 == msgList.size()) {
					//mAppManager.Execute("com.android.contacts");
				} else if (1 == msgList.size()) {
					String phoneNum = msgList.get(0).GetNumber();
					mContact.SendMsg(phoneNum, "");
				} else if (1 < msgList.size()) {
					ShowSelectDialog(msgList, task);
				}
			}
				break;
			case Task.OpenApp: {
				String packname = (String) task.getTaskParam();
				mAppManager.Execute(packname);
			}
				break;
			case Task.Search: {
				String search = (String) task.getTaskParam();
				mWebSearch.Execute(search);
			}
				break;
			case Task.SwitchOnDevice: {
				DeviceControl.Device device = (DeviceControl.Device) task.getTaskParam();
				mDevCon.Execute(device);
			}
				break;
			case Task.SetAlarm: {
				String strvoice = (String) task.getTaskParam();
				Alarm alarm = new Alarm(MainActivity.this, strvoice);
				alarm.Execute();
			}
				break;
			case Task.ShowProcess: {
				if (!showProgressDiaglog) {
					pd.show();
					showProgressDiaglog = true;
				} else {
					pd.cancel();
					showProgressDiaglog = false;
				}
			}
				break;
			case Task.IdentifyError: {
				updateListView(R.layout.chat_helper, "对不起哦，找不到你的命令");
			}
			default: {
				// updateListView("对不起哦，找不到你的");
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
		//voiceString = "";
		iatDialog.setEngine(engine, area, null);
		iatDialog.setSampleRate(RATE.rate8k);
		infos = null;
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

	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	public void onEnd(SpeechError arg0) {
		// TODO Auto-generated method stub
		updateListView(R.layout.chat_user, voiceString);
	}

	/**
	 * 讯飞传回的结果
	 */
	public void onResults(ArrayList<RecognizerResult> arg0, boolean arg1) {
		// TODO Auto-generated method stub
		//voiceString = "";
		for (int i = 0; i < arg0.size(); i++) {
			RecognizerResult recognizerResult = arg0.get(i);
			voiceString += recognizerResult.text;
		}
//		if (voiceString.equals("。") == false) {
//			updateListView(R.layout.chat_user, voiceString);
//		}
		//voiceString += arg0.get(0).text;
		
	}

	/**
	 * 对列表框的重用
	 * 
	 * @param items
	 * @param task
	 */
	public void ShowSelectDialog(final ArrayList<Contact.ContactPerson> list,
			final Task task) {
		final String[] items = new String[list.size()];
		for (int n = 0; n < list.size(); n++) {
			items[n] = ((Contact.ContactPerson) list.get(n)).GetName();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("请选择").setItems(items,
				new android.content.DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						ArrayList<Contact.ContactPerson> _list = new ArrayList<Contact.ContactPerson> ();
						_list.add(list.get(which));
						Task _task = new Task(task.getTaskID(), _list);
						Message msg = new Message();
						msg.obj = _task;
						mhandler.sendMessage(msg);
					}
				});

		AlertDialog dialog = builder.create();
		dialog.show();

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
		mIdentify = null;
		list = null;
		cad = null;
		chatList = null;
		mSharedPreferences = null;
		iatDialog = null;
		voiceString = "";// 语音服务提供商返回的处理字符串
		pd = null;
		mThread = null;// 语义识别的多线程
		super.onDestroy();
	}

	private void Test(Task task) {
		Message msg = new Message();
		msg.obj = task;
		mhandler.sendMessage(msg);
	}
}