package com.yifeng.commonutil;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

public class MyObject {
	public String getFb_city() {
		return fb_city;
	}

	public void setFb_city(String fb_city) {
		this.fb_city = fb_city;
	}

	public String getZhu() {
		return zhu;
	}

	public void setZhu(String zhu) {
		this.zhu = zhu;
	}

	public String getFb_xian() {
		return fb_xian;
	}

	public void setFb_xian(String fb_xian) {
		this.fb_xian = fb_xian;
	}

	public String getDd_city() {
		return dd_city;
	}

	public void setDd_city(String dd_city) {
		this.dd_city = dd_city;
	}

	public String getDd_xian() {
		return dd_xian;
	}

	public void setDd_xian(String dd_xian) {
		this.dd_xian = dd_xian;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	private String fb_city;
	private String zhu;
	private String fb_xian;
	private String dd_city;
	private String dd_xian;
	private String content;
	private String id;
	private String type;

	private String phone1;
	private String phone2;
	private String have1;
	private String have2;
	private String have3;
	private String need1;
	private String need2;
	private String need3;
	private String k1;
	private String k2;

	public String getPhone1() {
		return phone1;
	}

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	public String getPhone2() {
		return phone2;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	public String getHave1() {
		return have1;
	}

	public void setHave1(String have1) {
		this.have1 = have1;
	}

	public String getHave2() {
		return have2;
	}

	public void setHave2(String have2) {
		this.have2 = have2;
	}

	public String getHave3() {
		return have3;
	}

	public void setHave3(String have3) {
		this.have3 = have3;
	}

	public String getNeed1() {
		return need1;
	}

	public void setNeed1(String need1) {
		this.need1 = need1;
	}

	public String getNeed2() {
		return need2;
	}

	public void setNeed2(String need2) {
		this.need2 = need2;
	}

	public String getNeed3() {
		return need3;
	}

	public void setNeed3(String need3) {
		this.need3 = need3;
	}

	public String getK1() {
		return k1;
	}

	public void setK1(String k1) {
		this.k1 = k1;
	}

	public String getK2() {
		return k2;
	}

	public void setK2(String k2) {
		this.k2 = k2;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public static List<MyObject> getByCursor(Cursor cursor) {

		List<MyObject> list = new ArrayList<MyObject>();

		while (cursor.moveToNext()) {
			MyObject m = new MyObject();
			int nameColumnIndex = cursor.getColumnIndex("fb_city");
			m.setFb_city(cursor.getString(nameColumnIndex));
			nameColumnIndex = cursor.getColumnIndex("zhu");
			m.setZhu(cursor.getString(nameColumnIndex));
			nameColumnIndex = cursor.getColumnIndex("id");
			m.setId(cursor.getString(nameColumnIndex));
			nameColumnIndex = cursor.getColumnIndex("fb_xian");
			m.setFb_xian(cursor.getString(nameColumnIndex));
			nameColumnIndex = cursor.getColumnIndex("dd_city");
			m.setDd_city(cursor.getString(nameColumnIndex));
			nameColumnIndex = cursor.getColumnIndex("dd_xian");
			m.setDd_xian(cursor.getString(nameColumnIndex));
			nameColumnIndex = cursor.getColumnIndex("content");
			m.setContent(cursor.getString(nameColumnIndex));
			nameColumnIndex = cursor.getColumnIndex("type");
			m.setType(cursor.getString(nameColumnIndex));

			nameColumnIndex = cursor.getColumnIndex("phone1");
			m.setPhone1(cursor.getString(nameColumnIndex));

			nameColumnIndex = cursor.getColumnIndex("phone2");
			m.setPhone2(cursor.getString(nameColumnIndex));
			nameColumnIndex = cursor.getColumnIndex("have1");
			m.setHave1(cursor.getString(nameColumnIndex));
			nameColumnIndex = cursor.getColumnIndex("have2");
			m.setHave2(cursor.getString(nameColumnIndex));
			nameColumnIndex = cursor.getColumnIndex("have3");
			m.setHave3(cursor.getString(nameColumnIndex));
			nameColumnIndex = cursor.getColumnIndex("need1");
			m.setNeed1(cursor.getString(nameColumnIndex));
			nameColumnIndex = cursor.getColumnIndex("need2");
			m.setNeed2(cursor.getString(nameColumnIndex));
			nameColumnIndex = cursor.getColumnIndex("need3");
			m.setNeed3(cursor.getString(nameColumnIndex));
			nameColumnIndex = cursor.getColumnIndex("k1");
			m.setK1(cursor.getString(nameColumnIndex));
			nameColumnIndex = cursor.getColumnIndex("k2");
			m.setK2(cursor.getString(nameColumnIndex));

			list.add(m);
		}
		return list;

	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
