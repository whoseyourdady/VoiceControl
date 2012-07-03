package com.scut.vc.alarm;

import android.annotation.SuppressLint;
import java.util.Calendar;
/**
 * 将输入的字符串转化为具体的时间格式
 * @author Administrator
 *
 */

public class Str2DateTime {
	private String str;
	private Calendar cal;
	private int year;
	private int month;
	private static int day;
	private static int hour;
	private static int minute;
	//private Context context;
	public Str2DateTime(String str){
		this.str = str;
		//this.context = context;
		cal = Calendar.getInstance();
		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH)+1;
		day = cal.get(Calendar.DAY_OF_MONTH);
		hour = cal.get(Calendar.HOUR_OF_DAY);
		minute = cal.get(Calendar.MINUTE);
	}
/**
 * 根据字符串中的时间生成特定的时间
 * @return String analysis：特定的时间，具有一定的格式
 */
	public String Analysis(){

		String analysis = "";
		//str.indexOf("今天")!=-1||(str.contains("后")&&str.contains("分"))
		if(str.length()>0){
			if(str.indexOf("明天")!=-1){
				String time[] = new String[2];
				time = formatTime(str);
				String date = date2Tomorrow();					
				if(str.contains("下午")){
					analysis = date + " " + (Integer.parseInt(time[0])+ 12) + ":" + time[1];
				}else analysis = date + " " + time[0] + ":" + time[1];


			}else if(str.indexOf("后天")!=-1){
				String time[] = new String[2];
				time = formatTime(str);
				String date = dateAfterTomorrow();				
				if(str.contains("下午")){
					analysis = date + " " + (Integer.parseInt(time[0])+ 12) + ":" + time[1];
				}else analysis = date + " " + time[0] + ":" + time[1];

			}else{

				//Toast.makeText(context, "输入错误，请重新输入", Toast.LENGTH_SHORT);
				String time[] = new String[2];	
				time = formatTime(str);
				String date = formatDate(year,month,day);							
				if(str.contains("下午")||str.contains("晚上")){
					analysis = date + " " + (Integer.parseInt(time[0])+ 12) + ":" + time[1];
				}else analysis = date + " " + time[0] + ":" + time[1];
			}
		}else{
			String date = formatDate(year,month,day);
			analysis = date + " " + (hour +1) + ":" +minute;
		}

		return analysis;

	}


	/**
	 * 对字符串中的时间信息生成数组形式，
	 * @param str    字符串
	 * @return  num[]   字符串数组，num[0]小时数， num[1]分钟数
	 */
	public static String[] formatTime(String str){
		String num[] = new String[2];
		num [1] = num[0] = "";
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
		
		/*
		 * 当字符串含有一下关键字时，将会在当前时间的基础上加上这些时间间隔
		 */
		if(str.contains("后")&&str.contains("分")){

			int tempMinute = Integer.parseInt(num[0])+ minute;			
			if(tempMinute >= 60){
				num[1] = (tempMinute - 60) +"";
				num[0] = (hour + 1)+"";
			}else{
				num[1] = tempMinute +"";
				num[0] = hour +"" ;
			}
		}
		if(str.contains("后")&&str.contains("小时")){
			if(str.contains("半")){
				int tempMinute = minute + 30;			
				if(tempMinute >= 60){
					num[1] = (tempMinute - 60) +"";
					num[0] = (hour + 1)+"";
				}else{
					num[1] = tempMinute +"";
					num[0] = hour +"" ;
				}
			}

			int tempHour = Integer.parseInt(num[0])+ hour;			

			num[1] = formatTime(minute);
			num[0] = formatTime(tempHour);
		}


		if(num[1].equals("")){
			num[1] ="00";
		}
		if(Integer.parseInt(num[0])>24){
			num[0] = (Integer.parseInt(num[0])-24) + "";
			day  = day + 1;
		}
		return num;
	}


	/**
	 * 根据月份的不同，设置“明天”关键词设置
	 * @return
	 */
	private String date2Tomorrow(){
		String date = "";
		if(month == 2){
			if(day < 28){
				date = formatDate(year,month,day +1);
			}else{
				date = formatDate(year,month + 1,1);
			}
		}else if(month == 4|| month == 6|| month == 9 || month == 11){
			if(day < 30){
				date = formatDate(year, month, day +1);
			}else{
				date = formatDate(year,month + 1,1);
			}

		}else{
			if(day < 31){
				date = formatDate(year, month, day +1 );
			}else{
				if(month == 12){
					date = formatDate(year+1,1,1);
				}
				date = formatDate(year,month + 1,1);
			}
		}
		return date;
	}


	private String dateAfterTomorrow(){
		String date = "";
		if(month == 2){
			if(day == 27){
				date = formatDate(year,month + 1,1);
			}else if(day == 28){
				date = formatDate(year,month + 1,2);
			}else{

				date = formatDate(year,month,day +2);
			}
		}else if(month == 4|| month == 6|| month == 9 || month == 11){
			if(day == 29){
				date = formatDate(year,month + 1,1);

			}else if(day == 30){
				date = formatDate(year,month + 1,2);
			}
			else{
				date = formatDate(year, month, day + 2);
			}

		}else{
			if(day == 30){
				if(month == 12){
					date = formatDate(year+1,1,1);
				}
				date = formatDate(year,month + 1,1);
			}else if(day == 31){
				if(month == 12){
					date = formatDate(year+1,1,2);
				}
				date = formatDate(year,month + 1,2);							
			}else{
				date = formatDate(year, month, day+2);
			}
		}
		return date;
	}


	private String formatDate(int y,int m, int d){
		StringBuffer sb = new StringBuffer();
		sb.append(y);
		sb.append("/");
		sb.append( m<10 ? "0" + m : ""+ m );
		sb.append("/");
		sb.append( d< 10 ? "0"+ d: "" + d);
		return sb.toString();

	}


	private static String formatTime(int x)
	{
		String s=""+x;
		if(s.length()==1) s="0"+s;
		return s;
	}
}
