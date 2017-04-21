package com.winksoft.yzsmk.public_define;

import java.util.List;

import com.winksoft.yzsmk.db.MySQLiteHelper;
import com.winksoft.yzsmk.home.MyGridView;

import android.support.v4.view.ViewPager;

public class public_define {

	public static boolean YF_INSTALL=true;
	public static boolean YF_UNSTALL=false;
	public static int total_page = 0;
	public static ViewPager vp = null;
	public static List<MyGridView> myView = null;
	public static int postion = -1;
	public static int pageId = 0;
	public static int width;
	public static int hight;
	public final static int packet_add = 1;
	public static MySQLiteHelper my_sqlite = null;
	
	public final static String safeName="com.yfcomm.yf_desk";
	public final static String defendName="com.yf.defender";
	public final static String otaPacketname="com.yfcomm.otaupdate";
	
	
	public final static String settingname="com.android.settings";
	public final static String customerservice="com.yfcomm.yf_customerservice";
	public final static String chrome="com.android.chrome";
	public static final String yfdb = "yfhome.db";
	public static final int ver=25;
	public static final String TBNAME = "appinfo";

	public static final String packetname = "packetname"; 
	public static final String isnull = "pic"; 
	public static final String curpageid = "pageid"; 
	public static final String curItemindex = "itemID"; 
	public static final String icon="icon";
	public static final String title="title";
}