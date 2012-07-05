package com.scut.vc.weather;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
/**
 * 将网页的XML中通过标签获取标签里的信息，并将之复制到
 * LifeCurrentCondition的对象中储存转化
 * 标签中1表示白天，2表示夜间
 * <status> 天气情况中文
 * <temperature> 温度
 * <direction> 风向
 * 还有其他不一一表述
 * @author Administrator
 *
 */

public class SinaLifeHandler extends DefaultHandler{


	//生活指数
	private LifeCurrentCondition lccinfo = null;
	private String tagName;
	public SinaLifeHandler(LifeCurrentCondition lccinfo){
		super();
		this.lccinfo = lccinfo;

	}

	public void endDocument() throws SAXException
	{
		super.endDocument();
	}


	public void endElement(String uri, String localName, String name) throws SAXException
	{

		tagName = "";
		super.endElement(uri, localName, name);
	}

	public void startDocument() throws SAXException
	{
		
		super.startDocument();
	}

	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
	{
		this.tagName = localName;

		if(localName.equals("Weather"))
		{
                    lccinfo = new LifeCurrentCondition();
		}
		super.startElement(uri, localName, name, attributes);
	}
	public void characters(char ch[], int start, int length)  throws SAXException 
	{
		/*
		 * Would be called on the following structure:
		 * <element>characters</element>
		 */
		String tmp = new String(ch, start, length);
		if(tagName.equals("status1")){
			lccinfo.setDayWeather(tmp);
		}else if(tagName.equals("status2")){
			lccinfo.setNightWeather(tmp);
		}else if(tagName.equals("temperature1")){
			lccinfo.setDayTemp(tmp);
		}else if(tagName.equals("temperature2")){
			lccinfo.setNightTemp(tmp);
		}else if(tagName.equals("direction1")){
			lccinfo.setDayDirection(tmp);
		}else if(tagName.equals("direction2")){
			lccinfo.setNightDirection(tmp);
		}
		else if(tagName.equals("chy_shuoming"))
		{
			lccinfo.setCY(tmp);
		}
		else if (tagName.equals("ssd_s"))
		{
			lccinfo.setFEEL(tmp);
		}

		else if(tagName.equals("ktk_s"))
		{
			lccinfo.setKT(tmp);
		}

		else if(tagName.equals("gm_s"))
		{
			lccinfo.setGM(tmp);
		}
		else if(this.tagName.equals("yd_s"))
		{
			lccinfo.setYD(tmp);
			
		}
		super.characters(ch, start, length);
	}
	public LifeCurrentCondition getInfo()
	{
		return lccinfo;
	}




}
