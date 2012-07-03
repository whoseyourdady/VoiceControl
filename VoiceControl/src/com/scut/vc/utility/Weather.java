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

import android.app.Activity;
import android.content.res.Resources;

import com.scut.vc.ui.R;
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
	private String[][] allCity; 
	private Activity activity;
	public Weather(String city,int Day, Activity activity){
		this.city = city;
		this.Day = Day;
		this.activity = activity;
	}
	public Weather(String city, Activity activity){
		this.city = city;
		this.Day = 0;
		this.activity = activity;
	}
	public String[][] getAllCity(){
		if(allCity==null){
			allCity = new String[31][];
			Resources res = activity.getResources();
			allCity[0] = res.getStringArray(R.array.anhui_array);
			allCity[1] = res.getStringArray(R.array.aomen_array);
			allCity[2] = res.getStringArray(R.array.beijing_array);			
			allCity[3] = res.getStringArray(R.array.chongqing_array);
			allCity[4] = res.getStringArray(R.array.fujian_array);
			allCity[5] = res.getStringArray(R.array.gansu_array);
			allCity[6] = res.getStringArray(R.array.guangdong_array);
			allCity[7] = res.getStringArray(R.array.guangxi_array);
			allCity[8] = res.getStringArray(R.array.guizhou_array);
			allCity[9] = res.getStringArray(R.array.hainan_array);
			allCity[10] = res.getStringArray(R.array.hebei_array);
			allCity[11] = res.getStringArray(R.array.heilongjiang_array);
			allCity[12] = res.getStringArray(R.array.henan_array);
			allCity[13] = res.getStringArray(R.array.hubei_array);
			allCity[14] = res.getStringArray(R.array.hunan_array);
			allCity[15] = res.getStringArray(R.array.jiangsu_array);
			allCity[16] = res.getStringArray(R.array.jiangxi_array);
			allCity[17] = res.getStringArray(R.array.jilin_array);
			allCity[18] = res.getStringArray(R.array.liaoning_array);
			allCity[19] = res.getStringArray(R.array.neimenggu_array);
			allCity[20] = res.getStringArray(R.array.ningxia_array);
			allCity[21] = res.getStringArray(R.array.shandong_array);
			allCity[22] = res.getStringArray(R.array.shanghai_array);
			allCity[23] = res.getStringArray(R.array.sichuan_array);
			allCity[24] = res.getStringArray(R.array.taiwan_array);
			allCity[25] = res.getStringArray(R.array.tianjin_array);
			allCity[26] = res.getStringArray(R.array.xianggang_array);
			allCity[27] = res.getStringArray(R.array.xinjiang_array);
			allCity[28] = res.getStringArray(R.array.xizang_array);
			allCity[29] = res.getStringArray(R.array.yunnan_array);
			allCity[30] = res.getStringArray(R.array.zhejiang_array);
		}
		return allCity;
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
		String WeatherInfos = WeatherInfo[5] + WeatherInfo[7] + WeatherInfo[9] 
				+"\n" + WeatherInfo[6] + WeatherInfo[8] + WeatherInfo[10]+ "\n" +
				WeatherInfo[0] + WeatherInfo[1]+ WeatherInfo[2]+ WeatherInfo[3]+ WeatherInfo[4];
		return WeatherInfos;
	}
	
}
