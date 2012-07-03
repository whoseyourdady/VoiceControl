package com.scut.vc.identifysemantic;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;

import com.scut.vc.location.GetLocation;
import com.scut.vc.ui.MainActivity;
import com.scut.vc.utility.AppsManager;
import com.scut.vc.utility.AppsManager.Package_Info;
import com.scut.vc.utility.Contact;
import com.scut.vc.utility.DeviceControl;
import com.scut.vc.utility.Task;
import com.scut.vc.utility.Weather;
import com.scut.vc.utility.WebSearch;

public class SemanticIdentify {

	final static int OPENAPP = 0;
	final static int CALL = 1;
	final static int MESSAGE = 2;
	final static int SEARCH = 3;
	final static int SETNOTIFICATION = 4;
	final static int WEATHER = 5;
	final static int SETSYSTEM = 6;

	final static String OPENCALL = "com.android.contacts";// 打开联系人程序的包名
	final static String OPENMMS = "com.android.mms";// 打开短信程序的包名

	private Activity mActivity;// 自己的activity

	private AppsManager mAppManager;
	private Contact mContact;
	private DeviceControl mDevCon;
	private WebSearch mWebSearch;
	private Weather mWeather;

	static String mSystemKey[] = { "wifi", "gprs", "gps", "bluetooh", "flash",
			"airplanemode" };

	static String mKeyWord[][] = { { "打开", "运行", "启动" },// 打开应用的关键字
			{ "打电话给", "打电话", "电话", "拨打", "打给", "接通" }, // 打电话的关键字
			{ "发短信给", "发短信到", "发短信", "短信给", "短信" }, // 发短信的关键字
			{ "什么", "搜索", "查找" }, // 网络搜索的关键字
			{ "闹钟", "提醒", "点钟", "点", "小时", "分钟" }, // 设置提醒的关键字
			{ "天气怎么样", "天气"}, // 天气的关键字
	// {"设置", "打开", "关闭", "关上", "关掉"}, //设置系统的关键字
	// {"网易","腾讯","搜狐"},//要识别出来的知名网站
	};

	public SemanticIdentify(Activity activity) {
		mActivity = activity;
		inital();// 初始化
	}

	@SuppressLint({ "ParserError", "ParserError" })
	public Task Identify(String strVoice) {

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
		if (!(strSystemKey(strVoice).equals("")) && type != 1 && type != 2 && type != 3 && type != 4 ) {
			type = SETSYSTEM;
		}
		//由于strWeb函数返回的是此strVoice字符串中是否含有名站的判断
		//故如果strVoice中含有名站的关键字时，就进行搜索case，在webSearch.java中在地名站的url进行识别
		if ((strWeb(strVoice)) && type != 1 && type != 2 && type != 3 && type != 4 && type!=5){
			type = SEARCH;
		}

		switch (type) {
		case OPENAPP: {
			// 创建新的AppsManager实例
			ArrayList<Package_Info> appsList = mAppManager
					.getInstalledAppsList();
			//创建识别出来的候选程序列表
			ArrayList<Package_Info> resultsList = new ArrayList<Package_Info>();
			int appsSize = appsList.size();
			double maxScore = 0;
			System.out.println("strVoice= " + strVoice);
			
			for (int i = 0; i < appsSize; i++) {
				Package_Info pi = appsList.get(i);
				String strAppName = pi.mAppName;
				String strPackageName = pi.mPackageName;
				double score = strAppScore(strVoice, strAppName);
				/*System.out.println("strVoice     ==     " + CnToSpell.getPingYin(strVoice) + "      strAppName     "+CnToSpell.getPingYin(strAppName));
				System.out.println("1 = " + pi.mAppName);
				System.out.println("2 = " + pi.mPackageName);
				System.out.println("score=" + score);
				System.out.println("1 = " + pi.mAppName +"             length =  "+strAppName.length());*/
				//分两种情况
				//1、与最高评分的应用评分一致，那么把该应用添加进候选程序列表
				//2、评分比最高评分的应用高，那么清空原来的候选程序列表，同时吧该应用添加进列表
				if ( (score >= maxScore-0.2) && (score <= maxScore+0.2) ){
					resultsList.add(mAppManager.new Package_Info(strAppName, strPackageName));
					maxScore = score;
					/*System.out.println("maxScore=" + maxScore + "  = "
							+ pi.mAppName + "  = " + pi.mPackageName);			
					for(int j=0;j<resultsList.size();j++){
						System.out.println("候选应用列表       " + resultsList.get(j));
					}*/
				}
				if( score > maxScore+0.2 ){
					//清空原来的候选程序列表
					resultsList.clear();
					resultsList.add(mAppManager.new Package_Info(strAppName, strPackageName));
					maxScore = score;
					/*System.out.println("maxScore=" + maxScore + "  = "
							+ pi.mAppName + "  = " + pi.mPackageName);	
					for(int j=0;j<resultsList.size();j++){
						System.out.println("候选应用列表       " + resultsList.get(j));
					}*/
				}
			}
			if (maxScore == 0) {
				System.out.println("没有对应的命令！");
				task = new Task(Task.IdentifyError, null);
			} else {				
				for(int i=0;i<resultsList.size();i++){
					System.out.println("打开应用       " + resultsList.get(i).GetAppName() + "             " + resultsList.get(i).GetPackageName());					
				}
				task = new Task(Task.IdentifyError,null);
			}

			break;
		}
		case CALL: {

			// 当没有剩余的参数时，即没有任何可判断的数据时，默认打开联系人的程序
			if (strVoice.length() == 0) {
				task = new Task(Task.OpenApp, OPENCALL);
				break;
			}
			ArrayList<Contact.ContactPerson> contactPersonList = mContact
					.GetPersonList();

			ArrayList<Contact.ContactPerson> callTarget = new ArrayList<Contact.ContactPerson>();// 打电话列表

			// 判断strVoice是否含数字串
			// 如果含有数字串，则拨打该数字串
			// 如果不含有数字串，则寻找联系人
			String strName = "";
			String strNum = "";
			// System.out.println("!!!!!!!");
			double maxScore = 0;
			// strVoice中含有电话号码的情况
			if (!(strNum = containNum(strVoice)).equals("")) {
				// 既然strVoice中含有电话号码，而且以该电话号码进行拨号，所以评分maxScore应为最高评分值1
				maxScore = 1;
				// 获取该电话号码对应的联系人名字，如果联系人中不存在该号码则以该号码为联系人名称
				for (int i = 0; i < contactPersonList.size(); i++) {
					Contact.ContactPerson contactPerson = contactPersonList
							.get(i);
					if (contactPerson.GetName() != null
							&& contactPerson.GetNumber() != null) {
						// System.out.println(contactPerson.mContactsName);
						// System.out.println(contactPerson.mContactsNumber);
						if (contactPerson.GetNumber().equals(strNum)) {
							strName = contactPerson.GetName();
						}
					}
				}
				// 如果不存在联系人，则以号码为联系人
				if (strName.equals("")) {
					strName = strNum;
				}
				Contact.ContactPerson tempContactPerson = mContact.new ContactPerson(
						strName, strNum);
				callTarget.add(tempContactPerson);
			}
			// strVoice中不含有电话号码的情况，此时搜索可能的联系人号码
			// 此时可能存在多个联系人名称的评分相等的情况，那么就以一个列表列出来
			else {
				// System.out.println("strVoice = " + strVoice);
				for (int i = 0; i < contactPersonList.size(); i++) {
					Contact.ContactPerson contactPerson = contactPersonList
							.get(i);
					if (contactPerson.GetName() != null
							&& contactPerson.GetNumber() != null) {
						double score = strContactScore(strVoice,
								contactPerson.GetName());
						// 评分相等的情况，那么就把评分相当的联系人全部加入到拨号列表中
						// System.out.println(contactPerson.GetName()
						// +"         " + contactPerson.GetNumber() +
						// "           "+score);
						if (score == maxScore) {
							strNum = contactPerson.GetNumber();
							strName = contactPerson.GetName();
							Contact.ContactPerson tempContactPerson = mContact.new ContactPerson(
									strName, strNum);
							callTarget.add(tempContactPerson);
							// System.out.println("size =  " +
							// callTarget.size());
							/*
							 * for (int j = 0; j < callTarget.size(); j++) {
							 * System.out.println(callTarget.get(j).GetName()
							 * +"         " + callTarget.get(j).GetNumber()
							 * +"maxScore:    " + maxScore); }
							 */

						}
						// 有新的最优评分的情况，那么就把新的评分最优的联系人加入到拨号列表中
						if (score > maxScore) {
							callTarget.clear();// 清楚旧的列表
							strNum = contactPerson.GetNumber();
							strName = contactPerson.GetName();
							Contact.ContactPerson tempContactPerson = mContact.new ContactPerson(
									strName, strNum);
							callTarget.add(tempContactPerson);
							maxScore = score;
						}
					}
					// System.out.println(maxScore);
				}
			}

			// System.out.println("结果预留：  "+ callTarget.get(0).GetName() +
			// "          " + callTarget.get(0).GetNumber());

			// callTarget在上面的执行过程中至少会被赋有一个值，故不会出现越界错误
			// 同时，此处的if判断是为了确认返回有效的识别结果
			// 1、如果maxScore==0,那么在匹配联系人的时候，就没有任何一个联系人可以匹配，故没有对应可呼叫的联系人，出错
			// 2、如果callTarget的第一项的号码为空的话，那么callTarget第一项之后的都为空，即callTarget内部不含任何数据，
			// 故没有对应可呼叫的联系人，出错(可能是由于对应的联系人没有储存对应的联系号码所致)
			if (maxScore == 0
					|| containNum(callTarget.get(0).GetNumber()) == "") {
				System.out.println("没有对应的call命令");
				task = new Task(Task.IdentifyError, null);
			} else {
				System.out.println("size = " + callTarget.size());
				for (int i = 0; i < callTarget.size(); i++) {
					System.out.println("结果：         拨打电话"
							+ callTarget.get(i).GetName());
					System.out.println("结果：         拨打电话"
							+ callTarget.get(i).GetNumber());
				}
				/*
				 * //for test callTarget.clear(); strNum = "13592799412";
				 * strName = "张"; Contact.ContactPerson tempContactPerson =
				 * mContact.new ContactPerson( strName, strNum);
				 * callTarget.add(tempContactPerson);
				 */
				task = new Task(Task.CALL, callTarget);
			}

			break;
		}

		case MESSAGE: {

			// 当没有剩余的参数时，即没有任何可判断的数据时，默认打开短信的程序
			if (strVoice.length() == 0) {
				task = new Task(Task.OpenApp, OPENMMS);
				break;
			}

			ArrayList<Contact.ContactPerson> contactPersonList = mContact
					.GetPersonList();

			ArrayList<Contact.ContactPerson> msgTarget = new ArrayList<Contact.ContactPerson>();// 打电话列表

			// 判断strVoice是否含数字串
			// 如果含有数字串，则拨打该数字串
			// 如果不含有数字串，则寻找联系人
			String strName = "";
			String strNum = "";
			// System.out.println("!!!!!!!");
			double maxScore = 0;
			// strVoice中含有电话号码的情况
			if (!(strNum = containNum(strVoice)).equals("")) {
				// 既然strVoice中含有电话号码，而且以该电话号码进行拨号，所以评分maxScore应为最高评分值1
				maxScore = 1;
				// 获取该电话号码对应的联系人名字，如果联系人中不存在该号码则以该号码为联系人名称
				for (int i = 0; i < contactPersonList.size(); i++) {
					Contact.ContactPerson contactPerson = contactPersonList
							.get(i);
					if (contactPerson.GetName() != null
							&& contactPerson.GetNumber() != null) {
						// System.out.println(contactPerson.mContactsName);
						// System.out.println(contactPerson.mContactsNumber);
						if (contactPerson.GetNumber().equals(strNum)) {
							strName = contactPerson.GetName();
						}
					}
				}
				// 如果不存在联系人，则以号码为联系人
				if (strName.equals("")) {
					strName = strNum;
				}
				Contact.ContactPerson tempContactPerson = mContact.new ContactPerson(
						strName, strNum);
				msgTarget.add(tempContactPerson);
			}
			// strVoice中不含有电话号码的情况，此时搜索可能的联系人号码
			// 此时可能存在多个联系人名称的评分相等的情况，那么就以一个列表列出来
			else {
				// System.out.println("strVoice = " + strVoice);
				for (int i = 0; i < contactPersonList.size(); i++) {
					Contact.ContactPerson contactPerson = contactPersonList
							.get(i);
					if (contactPerson.GetName() != null
							&& contactPerson.GetNumber() != null) {
						double score = strContactScore(strVoice,
								contactPerson.GetName());
						// 评分相等的情况，那么就把评分相当的联系人全部加入到短信列表中
						// System.out.println(contactPerson.GetName()
						// +"         " + contactPerson.GetNumber() +
						// "           "+score);
						if (score == maxScore) {
							strNum = contactPerson.GetNumber();
							strName = contactPerson.GetName();
							Contact.ContactPerson tempContactPerson = mContact.new ContactPerson(
									strName, strNum);
							msgTarget.add(tempContactPerson);
							// System.out.println("size =  " +
							// callTarget.size());
							/*
							 * for (int j = 0; j < callTarget.size(); j++) {
							 * System.out.println(callTarget.get(j).GetName()
							 * +"         " + callTarget.get(j).GetNumber()
							 * +"maxScore:    " + maxScore); }
							 */

						}
						// 有新的最优评分的情况，那么就把新的评分最优的联系人加入到短信列表中
						if (score > maxScore) {
							msgTarget.clear();// 清楚旧的列表
							strNum = contactPerson.GetNumber();
							strName = contactPerson.GetName();
							Contact.ContactPerson tempContactPerson = mContact.new ContactPerson(
									strName, strNum);
							msgTarget.add(tempContactPerson);
							maxScore = score;
						}
					}
					// System.out.println(maxScore);
				}
			}

			// System.out.println("结果预留：  "+ callTarget.get(0).GetName() +
			// "          " + callTarget.get(0).GetNumber());

			// callTarget在上面的执行过程中至少会被赋有一个值，故不会出现越界错误
			// 同时，此处的if判断是为了确认返回有效的识别结果
			// 如果maxScore==0,那么在匹配联系人的时候，就没有任何一个联系人可以匹配，故没有对应可短信的联系人，出错
			// 如果callTarget的第一项的号码为空的话，那么callTarget第一项之后的都为空，即callTarget内部不含任何数据，故没有对应可短信的联系人，出错
			if (maxScore == 0 || containNum(msgTarget.get(0).GetNumber()) == "") {
				System.out.println("没有对应的SendMessage命令");
				task = new Task(Task.IdentifyError, null);
			} else {
				System.out.println("size = " + msgTarget.size());
				for (int i = 0; i < msgTarget.size(); i++) {
					System.out.println("结果：         发送短信"
							+ msgTarget.get(i).GetName());
					System.out.println("结果：         发送短信"
							+ msgTarget.get(i).GetNumber());
				}
				/*
				 * //for test callTarget.clear(); strNum = "13592799412";
				 * strName = "张"; Contact.ContactPerson tempContactPerson =
				 * mContact.new ContactPerson( strName, strNum);
				 * callTarget.add(tempContactPerson);
				 */
				task = new Task(Task.SendMessage, msgTarget);
			}

			break;
		}

		case SEARCH: {
			String strWwwEngine = "http://www.baidu.com/s?wd=";// 桌面版的搜索引擎
			String strWapEngine = "http://m.baidu.com/s?word=";// 移动版的搜索引擎
			System.out.println("上网搜索" + strWapEngine + strVoice);
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
				task = new Task(Task.SetAlarm, strVoice);

			}
		}
			break;
		case SETSYSTEM: {
			// {"设置", "打开", "关闭", "关上", "关掉"},
			String strHW = strSystemKey(strVoice);// 识别出要打开的硬件
			boolean flag = false;

			if (strVoice.contains("灭") || strVoice.contains("关")) {// 判断是打开还是关闭
				flag = false;
			} else {// 关闭系统硬件
				flag = true;
			}

			if (strHW.equals("")) {
				System.out.println("没有对应的命令");
				task = new Task(Task.IdentifyError, null);
			} else {
				System.out.println("strVoice = " + strVoice);
				System.out.println("hardware = " + strHW);
				DeviceControl.Device device = mDevCon.new Device(strHW, flag);
				task = new Task(Task.SwitchOnDevice, device);
			}
			// return command;
			break;
		}
		case WEATHER:{
			String City;
			int day;
			GetLocation g = new GetLocation(this.mActivity);
			City = g.getCity().replace("市", "");
			if(strVoice.contains("今天")){
				day = 0;
			}else if(strVoice.contains("明天")){
				day = 1;
			}else if(strVoice.contains("后天")){
				day = 2;
			}else if(strVoice.contains("大后天")){
				day = 3;
			}else{
				day = 0;
			}
			String[][] allCity = mWeather.getAllCity();
			for(int i=0; i<allCity.length; i++ ){
				for(int j=0; j<allCity[i].length; j++){
					if(strVoice.contains(allCity[i][j])&&allCity[i][j].length()!=0){
						City = allCity[i][j];
						break;
					}
				}
			}
			HashMap weatherInfos = new HashMap();
			weatherInfos.put("city", City);
			weatherInfos.put("day", day);
			task = new Task(Task.Weather, weatherInfos);
			System.out.println("天气查询");
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
	
	// 拼音得分函数，采用了比较整个字符的拼音的方法来比较得分
		public static double strAppScore(String base, String var) {
			String basePY = CnToSpell.getPingYin(base).toLowerCase();// base变量的拼音
			
			int denominator = var.length();// 得分的分母
			int numerator = 0;// 得分的分子

			for (int i = 0; i < denominator; i++) {
				if (basePY.contains(CnToSpell.getPingYin(var.substring(i,i+1).toLowerCase()))) {
					numerator++;
				}
			}
			double score = numerator * 1.0 / denominator;
			return score;
		}

	// 拼音得分函数，采用了比较拼音首字母的方法来比较得分
	public static double strAppScore_1(String base, String var) {
		String baseHead = CnToSpell.getPinYinHeadChar(base).toLowerCase();// base变量的拼音首字母
		String varHead = CnToSpell.getPinYinHeadChar(var).toLowerCase();// var变量的拼音首字母
		int denominator = varHead.length();// 得分的分母
		int numerator = 0;// 得分的分子

		for (int i = 0; i < denominator; i++) {
			if (baseHead.contains(varHead.substring(i, i + 1))) {
				numerator++;
			}
		}
		double score = numerator * 1.0 / denominator;
		return score;
	}

	public static double strContactScore(String base, String var) {
		int denominator = var.length();// 得分的分母
		double numerator = 0;// 得分的分子
		boolean containEnglish = false;
		if (var != null) {
			// 判断字符串中是否含有英文字母
			for (int i = 0; i < denominator; i++) {
				if ((CnToSpell.getCnASCII(var.substring(0, 1)).compareTo(
						CnToSpell.getCnASCII("z")) <= 0)
						&& (CnToSpell.getCnASCII(var.substring(0, 1))
								.compareTo(CnToSpell.getCnASCII("A")) >= 0)) {
					containEnglish = true;
				}
			}
			// System.out.println("containEngilsh  =   " + containEnglish);
			// 1、字符串中不含有英文字符的情况
			if (!containEnglish) {
				String PY1 = CnToSpell.getPingYin(base).toLowerCase();// 把str1转化为拼音的字符串
				// System.out.println("PY1=   " + PY1);
				for (int i = 0; i < denominator; i++) {
					String PY2 = CnToSpell.getPingYin(var.substring(i, i + 1)
							.toLowerCase());
					if (PY1.contains(PY2)) {
						// System.out.println(CnToSpell.getPingYin(var.substring(i,i+1).toLowerCase()));
						numerator += 1;
						if (PY2.length() == 1) {
							numerator -= 0.4;
						}
						if (PY2.length() == 2) {
							numerator -= 0.2;
						}
					}
				}
			}
			// 2、字符串中含有英文字符的情况
			else {
				// 当字符串中含有英文字符时的匹配思路:
				// 因为var变量为联系人名称，所以此时var中只含有英文字符串
				// 所以只需看看var中的英文字符串在base中出现了的程度(以评分来衡量)即可
				String PY1 = base.toLowerCase();
				for (int i = 0; i < denominator; i++) {
					if (PY1.contains(var.substring(i, i + 1).toLowerCase())) {
						numerator += 1;
					}
				}
			}
		}
		double score = numerator / denominator;
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
	
	public static boolean strWeb(String strVoice) {
		String[] webKey = WebSearch.webKey;
		int length = webKey.length;
		for(int i=0;i<length;i++){
			if(strVoice.contains(webKey[i])){
				return true;
			}
		}
		return false;
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
				if (str.charAt(i) == '二' || str.charAt(i) == '两'
						|| str.charAt(i) == '2') {
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
								if (str.charAt(i) == '五'
										|| str.charAt(i) == '5') {
									num[k] += "5";
									aNum = true;
								} else {
									if (str.charAt(i) == '六'
											|| str.charAt(i) == '6') {
										num[k] += "6";
										aNum = true;
									} else {
										if (str.charAt(i) == '七'
												|| str.charAt(i) == '7') {
											num[k] += "7";
											aNum = true;
										} else {
											if (str.charAt(i) == '八'
													|| str.charAt(i) == '8') {
												num[k] += "8";
												aNum = true;
											} else {
												if (str.charAt(i) == '九'
														|| str.charAt(i) == '9') {
													num[k] += "9";
													aNum = true;
												} else {
													if (str.charAt(i) == '0') {
														num[k] += "0";
														aNum = true;
													} else {
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

	private void inital() {
		mAppManager = new AppsManager(mActivity);
		mContact = new Contact(mActivity);
		mDevCon = ((MainActivity) mActivity).getDevice();
		mWeather = new Weather(null, mActivity);
	}
}