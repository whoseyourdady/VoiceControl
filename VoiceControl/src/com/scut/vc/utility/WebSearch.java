package com.scut.vc.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

public class WebSearch {
	private String strUrl = "";
	
	private String strBaiduEngine1 = "http://m.baidu.com/s?word=";// 百度移动版的搜索引擎
	private String strBaiduEngine2 = "http://www.baidu.com/s?wd=";// 百度桌面版的搜索引擎

	private String strGoogleEngine1 = "http://www.google.com.hk/search?q=";// 谷歌香港版的搜索引擎
	private String strGoogleEngine2 = "http://www.google.com/search?q=";// 谷歌国际版的搜索引擎

	private String strSougouEngine1 = "http://wap.sogou.com/web/searchList.jsp?keyword=";// 谷歌香港版的搜索引擎

	private Activity mActivity;

	public WebSearch(Activity tempActivity) {
		mActivity = tempActivity;
	}

	// 进行网页搜索,参数为指定搜索引擎加上关键字的URL
	public int Execute(String keyword) {
		
		// 获取语音引擎选项的数据
		// 下一句需做版本上的兼容
		// SharedPreferences sharedata = getSharedPreferences(
		// "voiceEngine", MODE_WORLD_READABLE | MODE_MULTI_PROCESS);
		SharedPreferences sharedata = mActivity.getSharedPreferences(
				"searchEngine", Context.MODE_WORLD_READABLE);
		String searchEngine = sharedata.getString("searchEngine", "1");// 如果不能正确获取语义引擎选项的数据，则以第一项为值
		System.out.println("execute_SearchEngine = " + searchEngine);

		if(searchEngine.equals("1")){//选择为百度搜索引擎的情况，此同时为默认情况
			strUrl = strBaiduEngine1;
		}
		else if(searchEngine.equals("2")){//选择为谷歌搜索引擎的情况
			strUrl = strGoogleEngine1;
		}
		else if(searchEngine.equals("3")){//选择为搜狗搜索引擎的情况
			strUrl = strSougouEngine1;
		}
		else{//如果出错的话，选择一个默认的搜索引擎选项
			strUrl = strBaiduEngine1;
		}
		
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		strUrl += keyword;
		final Uri uri = Uri.parse(strUrl);
		intent.setData(uri);
		mActivity.startActivity(intent);
		return 0;
	}
}
