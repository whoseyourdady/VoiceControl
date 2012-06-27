package com.scut.vc.alarm;

import java.util.Calendar;
/**
 * 将输入的字符串转化为具体的时间格式
 * @author Administrator
 *
 */

public class Str2DateTime {
	private String alarmStr;                 //传入的字符串
	private Calendar cal;                     
	private int year;
	private int month;
	private static int day;
	private static int hour;
	private static int minute;
	
	public Str2DateTime(String alarmStr){
		this.alarmStr = alarmStr;
		cal = Calendar.getInstance();
		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH)+1; 
		day = cal.get(Calendar.DAY_OF_MONTH);
		hour = cal.get(Calendar.HOUR_OF_DAY);
		minute = cal.get(Calendar.MINUTE);
		
	}


	/**
	 * 将输入的字符串通过解析，形成0000/00/00 00:00格式的字符串
	 * @return analysis，返回的是转化好的格式
	 */
	public String Analysis(){
		
		String analysis = "";
		if(alarmStr.length()!=0){
			//对于含有“明天”关键字的解析
			if(alarmStr.indexOf("明天")!=-1){
				String time[] = new String[2];
				String date = date2Tomorrow();		
				time = formatTime(alarmStr);
				if(alarmStr.contains("下午")||alarmStr.contains("晚上")){
					analysis = date + " " + (Integer.parseInt(time[0])+ 12) + ":" + time[1];
				}else analysis = date + " " + time[0] + ":" + time[1];
				
				//对于含有后天”关键字的解析
			}else if(alarmStr.indexOf("后天")!=-1){
				String time[] = new String[2];
				String date = dateAfterTomorrow();
				time = formatTime(alarmStr);
				if(alarmStr.contains("下午")||alarmStr.contains("晚上")){
					analysis = date + " " + (Integer.parseInt(time[0])+ 12) + ":" + time[1];
				}else analysis = date + " " + time[0] + ":" + time[1];

			}else{

				//Toast.makeText(context, "输入错误，请重新输入", Toast.LENGTH_SHORT);
				String date = formatDate(year,month,day);
				String time[] = new String[2];
				time = formatTime(alarmStr);
				if(alarmStr.contains("下午")||alarmStr.contains("晚上")){
					analysis = date + " " + (Integer.parseInt(time[0])+ 12) + ":" + time[1];
				}else analysis = date + " " + time[0] + ":" + time[1];
			}
		}else{
			//当输入为空时，返回延迟一小时的字符串
			String date = formatDate(year,month,day);
			analysis = date + " " + (hour +1) + ":" +minute;
		}
		 
		return analysis;

	}


	/**
	 * 对传入的字符串的数字进行解析，形成相对应的数字，同时将“半”作为30分钟
	 * @param alarmStr  传入的字符串
	 * @return String num[]； 返回带有小时和分钟的数组
	 */
	public static String[] formatTime(String alarmStr){
		String num[] = new String[2];
		num [1] = num[0] = "";
		boolean aNum = false;
		int k = 0;
		for(int i=0; i<alarmStr.length(); i++){
			if(alarmStr.charAt(i)=='一'){
				num[k] += "1";
				aNum = true;
			}else{
				if(alarmStr.charAt(i)=='二'||alarmStr.charAt(i)=='两'){
					num[k] += "2";
					aNum = true;
				}else{
					if(alarmStr.charAt(i)=='三'){
						num[k] += "3";
						aNum = true;
					}else{
						if(alarmStr.charAt(i)=='三'){
							num[k] += "3";
							aNum = true;
						}else{
							if(alarmStr.charAt(i)=='四'){
								num[k] += "4";
								aNum = true;
							}else{
								if(alarmStr.charAt(i)=='五'){
									num[k] += "5";
									aNum = true;
								}else{
									if(alarmStr.charAt(i)=='六'){
										num[k] += "6";
										aNum = true;
									}else{
										if(alarmStr.charAt(i)=='七'){
											num[k] += "7";
											aNum = true;
										}else{
											if(alarmStr.charAt(i)=='八'){
												num[k] += "8";
												aNum = true;
											}else{
												if(alarmStr.charAt(i)=='九'){
													num[k] += "9";
													aNum = true;
												}else{
													if(alarmStr.charAt(i)=='十'){
														if(num[k].length()>0) {
															char t = alarmStr.charAt(i+1);
															if(t!='一'&&t!='二'&&t!='三'&&
																	t!='四'&&t!='五'&&t!='六'&&
																	t!='七'&&t!='八'&&t!='九'){
																num[k] += "0";
															}
														}
														else {
															num[k] = "1";
															char t = alarmStr.charAt(i+1);
															if(t!='一'&&t!='二'&&t!='三'&&
																	t!='四'&&t!='五'&&t!='六'&&
																	t!='七'&&t!='八'&&t!='九'){
																num[k] = "10";
															}
														}
														aNum = true;
													}else{
														if(alarmStr.charAt(i)=='半'){
															num[1] = "30";
															break;
														}
														if(aNum&&k==0){
															k++;
															aNum = false;
														}
														if(aNum&&k==1)break;
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
		if(alarmStr.contains("后")&&alarmStr.contains("分")){			
			num[1] = Integer.parseInt(num[0])+ minute +"";
			num[0] = hour +"" ;
		}
		if(num[1].equals("")){
			num[1] ="00";
		}
		return num;
	}

/**
 * 对与“明天”的日期进行解析，对各个月份的设置
 * @return date
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
    
    /**
     * 对与“后天”的日期进行解析，对各个月份的设置
     * @return date
     */
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
				date = formatDate(year, month, day);
			}
		}
    	return date;
    }
    
	/**
	 * 对具体年份的格式设置
	 * @param y
	 * @param m
	 * @param d
	 * @return
	 */
	private String formatDate(int y,int m, int d){
		StringBuffer sb = new StringBuffer();
		sb.append(y);
		sb.append("/");
		sb.append( m<10 ? "0" + m : ""+ m );
		sb.append("/");
		sb.append( d< 10 ? "0"+ d: "" + d);
		return sb.toString();

	}
}
