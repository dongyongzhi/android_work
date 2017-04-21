package com.yfcomm.public_define;

import java.util.List;

public class WinInfo {

	public int left;   
	public int top;
	public int right; 
	public int bottom;
	public int id;
	public String packetname;
	public String title;
	public List<Postion>  icon; 
	public List<Postion>  text;
	public int color;

	public static class Postion{
		public int left; 
		public int top; 
		public int right;
		public int bottom;
		public String buff;
		public int  diplaymode;
		public int  size;
		public int  color;
	}

}