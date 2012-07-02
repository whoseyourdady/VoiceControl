package com.scut.vc.utility;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.scut.vc.weather.LifeCurrentCondition;
import com.scut.vc.weather.SinaLifeHandler;

/**
 * 天气类，返回一个天气信息的字符串数组
 * @author Administrator
 *
 */

public class Weather {

	private String city;
	private int Day;
	private String[] WeatherInfo;
	public Weather(String city,int Day){
		this.city = city;
		this.Day = Day;
	}
	public Weather(String city){
		this.city = city;
		this.Day = 0;
	}
/**
 * 
 * @return 含有天气预报的信息
 * @throws IOException
 * @throws SAXException
 * @throws ParserConfigurationException
 */
	public String[] getWeatherInfo() throws IOException, SAXException, ParserConfigurationException{
		String tempCity = null;
		String[] WeatherInfo ;
		tempCity = URLEncoder.encode(city.trim(), "GBK");
		String strUrl = "http://php.weather.sina.com.cn/xml.php?city="+tempCity+"&password=DJOYnieT8234jlsK&day="+Day;
		SAXParserFactory sax = SAXParserFactory.newInstance();
		XMLReader  xmlReader = sax.newSAXParser().getXMLReader();
		LifeCurrentCondition myLCC = new LifeCurrentCondition();
		SinaLifeHandler mySax = new SinaLifeHandler(myLCC);
		xmlReader.setContentHandler(mySax);
		xmlReader.parse(returnInputSource(strUrl));
		LifeCurrentCondition lcc = mySax.getInfo();		
		WeatherInfo = lcc.listString();
		return WeatherInfo;
	}	
	/**
	 * 将字符串转化为url，同时对url链接进行编码规范
	 * @param strUrl
	 * @return
	 * @throws IOException
	 */
	public InputSource returnInputSource(String strUrl) throws IOException{
		InputSource myIS = null;


		URL url = new URL(strUrl);
		InputStream inputStream = url.openStream();

		InputStreamReader inputSR = new InputStreamReader(inputStream,"UTF-8");
		myIS = new InputSource(inputSR);

		return myIS;
	}
	
	public String execute() throws IOException, SAXException, ParserConfigurationException{
		WeatherInfo = getWeatherInfo();
		String day = "";
		if(0 == Day){
			day = "今天";
		}else if(1 == Day){
			day = "明天";
		}else if(2 == Day){
			day = "后天";
		}else if(3 == Day){
			day = "大后天";
		}else if(4 == Day){
			day = "未来第四天";
		}else{
			day = "今天";
		}
		String WeatherInfos = day + WeatherInfo[5] + WeatherInfo[7] + WeatherInfo[9] 
				+" " + WeatherInfo[6] + WeatherInfo[8] + WeatherInfo[10]+ 
				WeatherInfo[0] + WeatherInfo[1]+ WeatherInfo[2]+ WeatherInfo[3]+ WeatherInfo[4];
		return WeatherInfos;
	}
	
}
