package com.yfcomm.public_define;

import java.util.List;

public class WinInfo extends AppInfo{

	public int left;   
	public int top;
	public int right; 
	public int bottom;
	public int id;
	public int pageId;
	public List<Postion>  child_view; 
	public List<Postion>  child_text;

	
	public void setWinCoordinate(int xcoordinate,int ycoordinate,int with,int hight){
		this.left=xcoordinate;
		this.top=ycoordinate;
		this.right=with;
		this.bottom=hight;
	}
	
	public static class Postion extends WinInfo{
		public String buff;
		public int  diplaymode;
		public int  size;
		public int  color;
	}

}