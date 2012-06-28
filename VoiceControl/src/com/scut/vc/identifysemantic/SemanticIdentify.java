package com.scut.vc.identifysemantic;

import java.util.ArrayList;

import android.app.Activity;
import android.util.Log;

import com.scut.vc.ui.MainActivity;
import com.scut.vc.utility.AppsManager;
import com.scut.vc.utility.AppsManager.Package_Info;
import com.scut.vc.utility.Contact;
import com.scut.vc.utility.DeviceControl;
import com.scut.vc.utility.Task;
import com.scut.vc.utility.WebSearch;

public class SemanticIdentify {

	final static int OPENAPP = 0;
	final static int CALL = 1;
	final static int MESSAGE = 2;
	final static int SEARCH = 3;
	final static int SETNOTIFICATION = 4;
	final static int SETSYSTEM = 5;

	private Activity mActivity;// 自己的activity

	private AppsManager mAppManager;
	private Contact mContact;
	private DeviceControl mDevCon;
	private WebSearch mWebSearch;

	static String mSystemKey[] = { "wifi", "gprs", "gps", "bluetooh", "flash",
			"airplanemode" };

	static String mKeyWord[][] = { { "打开", "运行", "启动" },// 打开应用的关键字
			{ "打电话", "打电话给", "电话", "拨打", "打给", "接通" }, // 打电话的关键字
			{ "发短信给", "短信" }, // 发短信的关键字
			{ "什么", "搜索", "查找" }, // 网络搜索的关键字
			{ "闹钟", "提醒", "点钟", "点", "小时", "分钟" }, // 设置提醒的关键字
	// {"设置", "打开", "关闭", "关上", "关掉"}, //设置系统的关键字
	};

	public SemanticIdentify(Activity activity) {
		mActivity = activity;
		inital();// 初始化
	}

	public Task Identify(String strVoice) {

		Command command;
		command = new Command();

		Task task = null;// 一个返回任务的Task引用

		boolean identifyFinish = false;
		int type = 0;
		for (type = 0; type < mKeyWord.length; type++) {
			for (int k = 0; k < mKeyWord[type].length; k++) {
				if (strVoice.contains(mKeyWord[type][k])) {
					if (type != SEARCH && type != SETNOTIFICATION)
						strVoice = strVoice.replaceAll(mKeyWord[type][k], "");
					identifyFinish = true;
					break;
				}
				if (k == mKeyWord[type].length) {
					type = -1;
					identifyFinish = true;
					break;
				}
			}
			if (identifyFinish)
				break;
		}
		if (!(strSystemKey(strVoice).equals(""))&& type !=4) {
			type = SETSYSTEM;
		}

		switch (type) {
		case OPENAPP: {
			// 创建新的AppsManager实例
			ArrayList<Package_Info> appsList = mAppManager
					.getInstalledAppsList();
			int appsSize = appsList.size();
			double maxScore = 0;
			int flag = -1;//标识哪个是需打开程序
			System.out.println("strVoice= " + strVoice);
			for (int i = 0; i < appsSize; i++) {
				Package_Info pi = appsList.get(i);
				String strAppName = pi.mAppName;		
				double score = strPYScore(strVoice, strAppName);
				System.out.println("1 = " + pi.mAppName);
				System.out.println("2 = " + pi.mPackageName);
				System.out.println("score=" + score);
				if (score >= maxScore) {
					command.mAppName = strAppName;
					flag = i;
					maxScore = score;
					System.out.println("maxScore=" + maxScore + "  = "
							+ pi.mAppName + "  = " + pi.mPackageName);
				}
			}
			if (maxScore == 0) {
				System.out.println("没有对应的命令！");
				task = new Task(Task.IdentifyError, null);
			} else {
				System.out.println("打开应用" + command.mAppName);
				String packName = ((Package_Info) appsList.get(flag)).mPackageName;
				task = new Task(Task.OpenApp, packName);
			}

			break;
		}
		case CALL: {
			ArrayList<Contact.ContactPerson> contactPersonList = new ArrayList<Contact.ContactPerson>();
			contactPersonList = mContact.GetPersonList();

			ArrayList<Contact.ContactPerson> callTarget = new ArrayList<Contact.ContactPerson>();// 打电话列表

			// 判断strVoice是否含数字串
			// 如果含有数字串，则拨打该数字串
			// 如果不含有数字串，则寻找联系人
			String strNum;
			// System.out.println("!!!!!!!");
			double maxScore = 0;
			if (!(strNum = containNum(strVoice)).equals("")) {
				command.mCallNum = strNum;
			} else {
				// System.out.println(strVoice);
				for (int i = 0; i < contactPersonList.size(); i++) {
					Contact.ContactPerson contactPerson = contactPersonList
							.get(i);
					if (contactPerson.GetName() != null
							&& contactPerson.GetNumber() != null) {
						// System.out.println(contactPerson.mContactsName);
						// System.out.println(contactPerson.mContactsNumber);
						double score = strPYScore(strVoice,
								contactPerson.GetName());
						// System.out.println(score);
						if (score >= maxScore) {
							command.mCallNum = contactPerson.GetNumber();
							command.mContactName = contactPerson.GetName();
							maxScore = score;
						}
					}
					// System.out.println(maxScore);
				}
			}

			if (maxScore == 0) {
				System.out.println("没有对应的命令");
				task = new Task(Task.IdentifyError, null);
			} else {
				System.out.println("拨打电话" + command.mCallNum);
				Contact.ContactPerson contactPerson = mContact.new ContactPerson(command.mContactName
						, command.mCallNum);
				callTarget.add(contactPerson);
				task = new Task(Task.CALL, callTarget);
			}

			break;
		}

		case MESSAGE: {
			ArrayList<Contact.ContactPerson> contactPersonList = new ArrayList<Contact.ContactPerson>();
			contactPersonList = mContact.GetPersonList();

			ArrayList<Contact.ContactPerson> messageTarget = new ArrayList<Contact.ContactPerson>();// 发信息列表

			double maxScore = 0;
			System.out.println(strVoice);
			for (int i = 0; i < contactPersonList.size(); i++) {
				Contact.ContactPerson contactPerson = contactPersonList.get(i);
				if (contactPerson.GetName() != null
						&& contactPerson.GetNumber() != null) {
					System.out.println(contactPerson.GetName());
					System.out.println(contactPerson.GetNumber());
					double score = strPYScore(strVoice, contactPerson.GetName());
					System.out.println(score);
					if (score >= maxScore) {
						command.mMsgNum = contactPerson.GetNumber();
						command.mContactName = contactPerson.GetName();
						maxScore = score;
					}
				}
				System.out.println(maxScore);
			}
			if (maxScore == 0) {
				System.out.println("没有对应的命令");
				task = new Task(Task.IdentifyError, null);
			} else {
				System.out.println("发短信" + command.mMsgNum);
				Contact.ContactPerson contactPerson = mContact.new ContactPerson(
						command.mCallNum, command.mContactName);
				messageTarget.add(contactPerson);
				task = new Task(Task.SendMessage, command.mMsgNum);
			}
			break;
		}
		case SEARCH: {
			String strWwwEngine = "http://www.baidu.com/s?wd=";// 桌面版的搜索引擎
			String strWapEngine = "http://m.baidu.com/s?word=";// 移动版的搜索引擎
			String strSearch = strVoice;// 要搜索的关键字
			System.out.println("上网搜索" + strWapEngine + strSearch);
			// command.mSerKeyWord = strSearch;
			task = new Task(Task.Search, strVoice);
			break;
		}
		case SETNOTIFICATION: {
			String time[] = getTimeFromStr(strVoice);
			boolean delay;
			if (strVoice.contains("后"))
				delay = true;
			if (time[0].equals("") && time[1].equals("")) {
				System.out.println("没有对应的命令");
				task = new Task(Task.IdentifyError, null);
			} else {
				System.out.println("设置备忘:" + time[0] + ":" + time[1] + "事件："
						+ strVoice);

				Log.v("Work", "hour " + time[0] + " minute " + time[1]);

				task = new Task(Task.SetAlarm,  strVoice);
			}
			//task = new Task(Task.SetAlarm,  strVoice);
			
			break;
		}
		case SETSYSTEM: {
			// {"设置", "打开", "关闭", "关上", "关掉"},
			command.mHardwareName = strSystemKey(strVoice);
			if (strVoice.contains("设置") || strVoice.contains("打开")) {// 打开系统硬件
				command.mHardware = true;
			} else {// 关闭系统硬件
				command.mHardware = false;
			}
			if (command.mHardwareName.equals("")) {
				System.out.println("没有对应的命令");
				task = new Task(Task.IdentifyError, null);
			} else {
				System.out.println("strVoice = " + strVoice);
				System.out.println("hardware = " + command.mHardwareName);
				task = new Task(Task.SwitchOnDevice, command.mHardwareName);
			}
			// return command;
			break;
		}
		default: {
			System.out.println("匹配错误");
			task = new Task(Task.IdentifyError, null);
			return task;// 无匹配时，返回一个空的
		}
		}

		return task;
	}

	// 若含有数字串，则返回表示数字串的string
	// 若不含有数字串，则返回表示空的string串""
	public static String containNum(String str) {
		int length = str.length();
		int begin = 0;
		int end = 0;
		boolean strBegin = false;
		for (int i = 0; i < length; i++) {
			if (str.charAt(i) < '0' || str.charAt(i) > '9') {
				if (begin != 0) {// 第一个数字串已经结束
					break;
				} else {// 还没碰到数字串或者扔在第一个数字串中
					continue;
				}
			} else {
				if (strBegin == false) {
					begin = i;
					strBegin = true;
				}
				end = i;
			}
		}
		if (strBegin == true) {
			return str.substring(begin, end + 1);
		} else {
			return "";
		}
	}

	public static double strPYScore(String base, String var) {
		int denominator = var.length();// 得分的分母
		int numerator = 0;// 得分的分子
		base = base.toLowerCase();
		String PY1 = CnToSpell.getPingYin(base);// 把str1转化为拼音的字符串
		for (int i = 0; i < denominator; i++) {
			if (PY1.contains(CnToSpell.getPingYin(var.substring(i, i + 1)
					.toLowerCase()))) {
				numerator++;
				// System.out.println("PY1="+PY1);
			}
		}
		double score = numerator * 1.0 / denominator;
		return score;
	}

	public static String strSystemKey(String strVoice) {
		// 目前包含的硬件模块的识别内容包括：WIFI,GPRS,GPS,蓝牙，手电筒，飞行模式
		String[][] systemKey = {// 包含硬件名的关键字的数组
		{ "wifi", "无线" },// "无线"表示"无线网络"，即"wifi"
				{ "gprs", "移动" },// "移动"用于匹配"移动数据网络"，即"gprs"
				{ "gps", "定位" },// "定位"用于匹配"卫星定位"，即"gps"
				{ "蓝牙" }, { "手电筒", "闪光灯", "led" },// led用于匹配"闪光灯"，即"手电筒"
				{ "飞行模式" },

		};
		String[][] initSystemKeyPY = {// 包含硬件名的关键字的首字母的数组
		{ "w", "f" }, { "g", "p", "r", "s" }, { "g", "p", "s" }, { "l", "y" },
				{ "s", "d", "t" }, { "f", "x", "m", "s" } };
		// 1、尝试匹配完整的关键字
		for (int type = 0; type < systemKey.length; type++) {
			for (int i = 0; i < systemKey[type].length; i++) {
				if (strVoice.contains(systemKey[type][i])) {
					return mSystemKey[type];
				}
			}
		}
		// 2、尝试进行首拼音的匹配
		String initStrVoicePY = "";
		for (int i = 0; i < strVoice.length(); i++) {// 获取strVoice中各个字符的首拼音
			initStrVoicePY += (CnToSpell.getPingYin(strVoice
					.substring(i, i + 1)).substring(0, 1));
		}
		double numerator = 0;
		double[] score = new double[systemKey.length];// 以系统的关键字数组的大小建立计分数组
		for (int type = 0; type < initSystemKeyPY.length; type++) {// 获取各个系统关键字的拼音数组的评分，其中最高分的识别到的系统关键字即为用户所想要的系统关键字
			for (int i = 0; i < initSystemKeyPY[type].length; i++) {
				if (initStrVoicePY.contains(initSystemKeyPY[type][i])) {
					numerator++;
				}
			}
			score[type] = numerator / (initSystemKeyPY[type].length);
			numerator = 0;// 将分子重新置零，以为了下一次计算
		}
		double maxScore = 0;
		int type = 0;
		for (int i = 0; i < initSystemKeyPY.length; i++) {// 获取得分最大的那个系统关键字
			if (score[i] > maxScore && score[i] > 0.4) {// 0.4为一个最低标准，低于其下算作错误
				maxScore = score[i];
				type = i;
			}
		}
		if (maxScore < 0.8) {
			return "";// 返回空字符串表示没有成功的识别
		} else {
			return mSystemKey[type];// 返回表示指定的系统硬件的关键字
		}

	}

	public static String[] getTimeFromStr(String str) {
		String num[] = new String[2];
		num[1] = num[0] = "";
		boolean aNum = false;
		int k = 0;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '一' || str.charAt(i) == '1') {
				num[k] += "1";
				aNum = true;
			} else {
				if (str.charAt(i) == '二' || str.charAt(i) == '两' || str.charAt(i) == '2') {
					num[k] += "2";
					aNum = true;
				} else {
					if (str.charAt(i) == '三' || str.charAt(i) == '3') {
						num[k] += "3";
						aNum = true;
					} else {
						if (str.charAt(i) == '三' || str.charAt(i) == '3') {
							num[k] += "3";
							aNum = true;
						} else {
							if (str.charAt(i) == '四' || str.charAt(i) == '4') {
								num[k] += "4";
								aNum = true;
							} else {
								if (str.charAt(i) == '五' || str.charAt(i) == '5') {
									num[k] += "5";
									aNum = true;
								} else {
									if (str.charAt(i) == '六' || str.charAt(i) == '6') {
										num[k] += "6";
										aNum = true;
									} else {
										if (str.charAt(i) == '七' || str.charAt(i) == '7') {
											num[k] += "7";
											aNum = true;
										} else {
											if (str.charAt(i) == '八' || str.charAt(i) == '8') {
												num[k] += "8";
												aNum = true;
											} else {
												if (str.charAt(i) == '九' || str.charAt(i) == '9') {
													num[k] += "9";
													aNum = true;
												} else {
													if (str.charAt(i) == '0'){
														num[k] += "0";
														aNum = true;
													}
													else{
														if (str.charAt(i) == '十') {
															if (num[k].length() > 0) {
																char t = str
																		.charAt(i + 1);
																if (t != '一'
																		&& t != '二'
																		&& t != '三'
																		&& t != '四'
																		&& t != '五'
																		&& t != '六'
																		&& t != '七'
																		&& t != '八'
																		&& t != '九') {
																	num[k] += "0";
																}
															} else {
																num[k] = "1";
																char t = str
																		.charAt(i + 1);
																if (t != '一'
																		&& t != '二'
																		&& t != '三'
																		&& t != '四'
																		&& t != '五'
																		&& t != '六'
																		&& t != '七'
																		&& t != '八'
																		&& t != '九') {
																	num[k] = "10";
																}
															}
															aNum = true;
														} else {
															if (str.charAt(i) == '半') {
																num[1] = "30";
																break;
															}
															if (aNum && k == 0) {
																k++;
																aNum = false;
															}
															if (aNum && k == 1)
																break;
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		if (str.contains("后") && str.contains("分")) {
			num[1] = num[0];
			num[0] = "";
		}
		return num;
	}

	public class Command {

		String mCallNum;

		String mAppName;

		String mMsgNum;

		String mSerKeyWord;

		String mNotificationTime;

		String mContactName;

		boolean mHardware;

		String mHardwareName;

	}

	private void inital() {
		mAppManager = new AppsManager(mActivity);
		mContact = new Contact(mActivity);

	}
}