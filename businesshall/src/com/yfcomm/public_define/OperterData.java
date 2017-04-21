package com.yfcomm.public_define;

import java.util.List;

public class OperterData {

	public List<Operplannames> plannames;// 运营商套数据
	public List<numberpool> numberpool;// 号码池数据

	public static class Operplannames {

		public String planname; // 套餐名称
		public String prcie; // 套餐价格
		public String note; // 套餐备注
		public boolean is_sel = false;// 是否被选中
	}

	public static class numberpool {

		public String phonenum; // 号码
		public String prcie; // 价格
		public boolean is_sel = false;// 是否被选中
		public boolean isUsed = false; // 是否被占用
	}

}
