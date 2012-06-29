package com.scut.vc.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

public class WebSearch {

	public static String[] webKey = { "网易", "163", "搜狐", "souhu", "新浪", "sina",
			"腾讯", "百度", "baidu", "谷歌", "google", "淘宝", "taobao", "新华网", "人民网",
			"环球网" };

	private String strUrl = "";

	private String strBaiduEngine1 = "http://m.baidu.com/s?word=";// 百度移动版的搜索引擎
	private String strBaiduEngine2 = "http://www.baidu.com/s?wd=";// 百度桌面版的搜索引擎

	private String strGoogleEngine1 = "http://www.google.cn/search?q=";// 谷歌的搜索引擎

	private Activity mActivity;

	public WebSearch(Activity tempActivity) {
		mActivity = tempActivity;
	}

	// 进行网页搜索,参数为指定搜索引擎加上关键字的URL
	public int Execute(String keyword) {

		SharedPreferences sharedata = mActivity.getSharedPreferences(
				"searchEngine", Context.MODE_WORLD_READABLE);
		String searchEngine = sharedata.getString("searchEngine", "1");// 如果不能正确获取语义引擎选项的数据，则以第一项为值
		System.out.println("execute_SearchEngine  =  " + searchEngine);

		if (searchEngine.equals("1")) {// 选择为百度搜索引擎的情况，此同时为默认情况
			strUrl = strBaiduEngine1;
		} else if (searchEngine.equals("2")) {// 选择为谷歌搜索引擎的情况
			strUrl = strGoogleEngine1;
		} else {// 如果出错的话，选择一个默认的搜索引擎选项
			strUrl = strBaiduEngine1;
		}

		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		strUrl += keyword;

		if (keyword.contains("网易") || keyword.contains("163")) {
			strUrl = "http://3g.163.com";
		} else if (keyword.contains("搜狐") || keyword.contains("shouhu")) {
			strUrl = "http://wap.sohu.com";
		} else if (keyword.contains("新浪") || keyword.contains("sina")) {
			strUrl = "http://3g.sina.cn";
		} else if (keyword.contains("腾讯")) {
			strUrl = "http://3g.qq.com";
		} else if (keyword.contains("百度") || keyword.contains("baidu")) {
			strUrl = "http://m.baidu.com";
		} else if (keyword.contains("谷歌") || keyword.contains("google")) {
			strUrl = "http://www.google.cn";
		} else if (keyword.contains("淘宝") || keyword.contains("taobao")) {
			strUrl = "http://m.taobao.com";
		} else if (keyword.contains("新华网")) {
			strUrl = "http://www.xinhuanet.com";
		} else if (keyword.contains("人民网")) {
			strUrl = "http://wap.people.com.cn";
		} else if (keyword.contains("环球网")) {
			strUrl = "http://wap.huanqiu.com";
		}

		final Uri uri = Uri.parse(strUrl);
		intent.setData(uri);
		mActivity.startActivity(intent);
		return 0;
	}
}
