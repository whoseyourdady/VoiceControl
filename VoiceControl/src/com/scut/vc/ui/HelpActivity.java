package com.scut.vc.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.widget.SimpleExpandableListAdapter;

public class HelpActivity extends ExpandableListActivity   {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        
        //创建二个一级条目标题   
        Map<String, String> title_1 = new HashMap<String, String>();  
        Map<String, String> title_2 = new HashMap<String, String>();  
        Map<String, String> title_3 = new HashMap<String, String>(); 
        Map<String, String> title_4 = new HashMap<String, String>(); 
        Map<String, String> title_5 = new HashMap<String, String>(); 
        Map<String, String> title_6 = new HashMap<String, String>(); 
          
        title_1.put("group", "拨打电话");  
        title_2.put("group", "发送短信"); 
        title_3.put("group", "打开应用");
        title_4.put("group", "网络搜索");
        title_5.put("group", "设置提醒");
        title_6.put("group", "设置系统");
          
        //创建一级条目容器   
        List<Map<String, String>> gruops = new ArrayList<Map<String,String>>();  
          
        gruops.add(title_1);  
        gruops.add(title_2); 
        gruops.add(title_3);  
        gruops.add(title_4);  
        gruops.add(title_5);  
        gruops.add(title_6);  
          
        //创建二级条目内容   
          
        //内容一   
        Map<String, String> content_11 = new HashMap<String, String>();  
        Map<String, String> content_12 = new HashMap<String, String>();  
        Map<String, String> content_13 = new HashMap<String, String>(); 
        Map<String, String> content_14 = new HashMap<String, String>(); 
        Map<String, String> content_15 = new HashMap<String, String>(); 
          
        content_11.put("child", "命令示例：");  
        content_12.put("child", "打给张三");  
        content_13.put("child", "打电话给张三");
        content_14.put("child", "接通张三的电话"); 
        content_15.put("child", "拨打张三的电话"); 
          
        List<Map<String, String>> childs_1 = new ArrayList<Map<String,String>>();  
        childs_1.add(content_11);  
        childs_1.add(content_12); 
        childs_1.add(content_13);
        childs_1.add(content_14);
        childs_1.add(content_15);
        
          
        //内容二   
        Map<String, String> content_21 = new HashMap<String, String>();  
        Map<String, String> content_22 = new HashMap<String, String>();  
        Map<String, String> content_23 = new HashMap<String, String>(); 
          
        content_21.put("child", "命令示例：");  
        content_22.put("child", "短信张三");  
        content_23.put("child", "发短信给张三");  
          
        List<Map<String, String>> childs_2 = new ArrayList<Map<String,String>>();  
        childs_2.add(content_21);  
        childs_2.add(content_22);  
        childs_2.add(content_23);  
          
        //内容三   
        Map<String, String> content_31 = new HashMap<String, String>();  
        Map<String, String> content_32 = new HashMap<String, String>();  
        Map<String, String> content_33 = new HashMap<String, String>(); 
          
        content_31.put("child", "命令示例：");  
        content_32.put("child", "打开UC浏览器");  
        content_33.put("child", "打开便签");  
          
        List<Map<String, String>> childs_3 = new ArrayList<Map<String,String>>();  
        childs_3.add(content_31);  
        childs_3.add(content_32);  
        childs_3.add(content_33);
        
      //内容四   
        Map<String, String> content_41 = new HashMap<String, String>();  
        Map<String, String> content_42 = new HashMap<String, String>();  
        Map<String, String> content_43 = new HashMap<String, String>(); 
        Map<String, String> content_44 = new HashMap<String, String>(); 
        Map<String, String> content_45 = new HashMap<String, String>(); 
          
        content_41.put("child", "命令示例：");  
        content_42.put("child", "搜索外星人");  
        content_43.put("child", "查找外星人");  
        content_44.put("child", "什么是外星人");  
        content_45.put("child", "外星人是什么");  
          
        List<Map<String, String>> childs_4 = new ArrayList<Map<String,String>>();  
        childs_4.add(content_41);  
        childs_4.add(content_42);  
        childs_4.add(content_43);
        childs_4.add(content_44);
        childs_4.add(content_45);
               
        //内容五   
        Map<String, String> content_51 = new HashMap<String, String>();  
        Map<String, String> content_52 = new HashMap<String, String>();  
        Map<String, String> content_53 = new HashMap<String, String>(); 
        Map<String, String> content_54 = new HashMap<String, String>(); 
          
        content_51.put("child", "命令示例：");  
        content_52.put("child", "提醒我九点十分开会");  
        content_53.put("child", "设置一个九点钟的闹钟");  
        content_54.put("child", "设置一个九点十分的闹钟"); 
          
        List<Map<String, String>> childs_5 = new ArrayList<Map<String,String>>();  
        childs_5.add(content_51);  
        childs_5.add(content_52);  
        childs_5.add(content_53);
        childs_5.add(content_54);
        
        //内容六   
        Map<String, String> content_61 = new HashMap<String, String>();  
        Map<String, String> content_62 = new HashMap<String, String>();  
        Map<String, String> content_63 = new HashMap<String, String>(); 
        Map<String, String> content_64 = new HashMap<String, String>(); 
        Map<String, String> content_65 = new HashMap<String, String>(); 
        Map<String, String> content_66 = new HashMap<String, String>(); 
        Map<String, String> content_67 = new HashMap<String, String>(); 
        
        content_61.put("child", "命令示例：");  
        content_62.put("child", "设置蓝牙");  
        content_63.put("child", "打开蓝牙");  
        content_64.put("child", "开启蓝牙");  
        content_65.put("child", "启动蓝牙");  
        content_66.put("child", "关闭蓝牙");  
        content_67.put("child", "关掉蓝牙");  
          
        List<Map<String, String>> childs_6 = new ArrayList<Map<String,String>>();  
        childs_6.add(content_61);  
        childs_6.add(content_62);  
        childs_6.add(content_63);
        childs_6.add(content_64);
        childs_6.add(content_65);
        childs_6.add(content_66);
        childs_6.add(content_67);
        
        //存放两个内容, 以便显示在列表中   
        List<List<Map<String, String>>> childs = new ArrayList<List<Map<String,String>>>();  
        childs.add(childs_1);  
        childs.add(childs_2);
        childs.add(childs_3); 
        childs.add(childs_4); 
        childs.add(childs_5); 
        childs.add(childs_6); 
        //创建ExpandableList的Adapter容器   
        //参数: 1.上下文    2.一级集合   3.一级样式文件 4. 一级条目键值      5.一级显示控件名   
        //      6. 二级集合 7. 二级样式 8.二级条目键值    9.二级显示控件名   
        SimpleExpandableListAdapter sela = new SimpleExpandableListAdapter(  
                this, gruops, R.layout.groups, new String[]{"group"}, new int[]{R.id.textGroup},   
                childs, R.layout.childs, new String[]{"child"}, new int[]{R.id.textChild}  
                );  
          
        //加入列表   
        setListAdapter(sela);  
    }  
}  