package com.scut.vc.utility;

import java.util.Map;

/**
 * 当解析语义模块后
 * 生成任务
 * 给主程序执行
 * @author fatboy
 *
 */
public class Task {

	private int mTaskId =0;
	/**
	 * 打电话和发信息的参数是List<Contact.ContactPerson>
	 * 设备控制参数是String
	 * 打开应用的参数是String
	 * Websearch的参数是String
	 */
	private Object mTaskParam = null;
	
	public final static int CALL = 1;//打电话
	public final static int SendMessage = 2;//发信息
	public final static int OpenApp = 3;//打开一个应用 
	public final static int SwitchOnDevice = 4;//打开一个硬件
	public final static int Search = 5;//搜索
	public final static int SetAlarm = 6;//设置闹钟
	public final static int IdentifyError = -1;//匹配错误
	
	public final static int ShowProcess = -2;//显示识别中的进度条

	public Task(int task, Object param) {
		mTaskId = task;
		mTaskParam = param;
	}
	

	
	public int getTaskID() {
		return mTaskId;
	}

	public void setTaskID(int taskID) {
		mTaskId = taskID;
	}

	public Object getTaskParam() {
		return mTaskParam;
	}

	public void setTaskParam(Object taskParam) {
		mTaskParam = taskParam;
	}
}
