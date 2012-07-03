package com.scut.vc.weather;

import java.io.Serializable;
/**
 * 
 * @author Administrator
 *获取当前生活指数，其中
 *cy 表示穿衣指数
 *yd 表示运动指数
 *kt 表示空调指数
 *gm 表示感冒指数
 *feel 表示体感指数
 *Direction  风向
 *Weather  天气状况
 *Temp    温度
 */
public class LifeCurrentCondition implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String dayWeather;
	private String nightWeather;
	private String dayTemp;
	private String nightTemp;
	private String dayDirection;
	private String nightDirection;
	private String cy;
	private String yd;
	private String kt;
	private String gm;
	private String feel;

	public LifeCurrentCondition(){
		
	}
	
	
	public String getCY()
	{
		return cy;
	}
	
	public void setCY(String cy)
	{
		this.cy = cy;
		
	}
	public String getYD()
	{
		return yd;
	}
	
	public void setYD(String yd)
	{
		this.yd = yd;
		
	}
	public String getKT()
	{
		return kt;
	}
	
	public void setKT(String kt)
	{
		this.kt = kt;
		
	}
	public String getGM()
	{
		return gm;
	}
	
	public void setGM(String gm)
	{
		this.gm = gm;
		
	}
	public String getFEEL()
	{
		return feel;
	}
	
	public void setFEEL(String feel)
	{
		this.feel= feel;
		
	}
	public void setDayWeather(String dayWeather){
		this.dayWeather = dayWeather;
	}
	
	public String getDayWeather(){
		return dayWeather;
	}
	
	public void setNightWeather(String nightWeather){
		this.nightWeather =nightWeather;
	}
	
	public String getNightWeather(){
		return nightWeather;
	}
	
	public void setDayTemp(String dayTemp){
		this.dayTemp = dayTemp;
	}
	
	public String getDayTemp(){
		return dayTemp;
	}
	
	public void setNightTemp(String nightTemp){
		this.nightTemp = nightTemp;
	}
	
	public String getNightTemp(){
		return nightTemp;
	}
	
	public void setDayDirection(String dayDirection){
		this.dayDirection = dayDirection;
	}
	
	public String getDayDirection(){
		return dayDirection;
	}
	public void setNightDirection(String nightDirection){
		this.nightDirection = nightDirection;
	}
	
	public String getNightDirection(){
		return nightDirection;
	}
	
	public String[] listString()
	{
		String[] str = new String[11];
		str[0] = " 穿衣指数:"+cy;
		str[1] = " 运动指数:"+yd;
		str[2] = " 感冒指数:" +gm;
		str[3] = " 空调指数:" +kt;
		str[4] = " 体感指数：" + feel;
		str[5] = "白天： " + dayWeather;
		str[6] = "晚上： " + nightWeather;
		str[7] = " " + dayTemp + "℃";
		str[8] = " " + nightTemp + "℃";
		str[9] = " " + dayDirection;
		str[10] = " " + nightDirection;
		return str;
		
	}
	
	
	
}



