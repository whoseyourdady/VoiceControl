package com.scut.vc.xflib;

public class ChatEng {


	private String info;
	private int layoutID;
	
	public ChatEng( String info, int layoutID){
		this.info = info;

		this.layoutID = layoutID;
	}
	
	public String getInfo(){
		return info;
	}
	public int getLayoutID(){
		return layoutID;
	}
	
	public void setInfo( String info){
		this.info = info;
	}
	public void setLayoutID(int layoutID){
		this.layoutID = layoutID;
	}
	
}
