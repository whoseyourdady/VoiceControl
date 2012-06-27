package com.scut.vc.utility;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class WebSearch {
	private String strUrl = "";
	private String strWwwEngine = "http://www.baidu.com/s?wd=";// 桌面版的搜索引擎
	private String strWapEngine = "http://m.baidu.com/s?word=";// 移动版的搜索引擎
	private Activity mActivity;

	public WebSearch(Activity tempActivity) {
		mActivity = tempActivity;
	}

	// 进行网页搜索,参数为指定搜索引擎加上关键字的URL
	public int Execute(String keyword) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		strUrl = strWapEngine + keyword;
		final Uri uri = Uri.parse(strUrl);
		intent.setData(uri);
		mActivity.startActivity(intent);
		return 0;
	}
}
