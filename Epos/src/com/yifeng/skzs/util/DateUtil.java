package com.yifeng.skzs.util;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import java.util.Map;
import java.util.Calendar;
import java.util.HashMap;
import java.sql.Timestamp;
import java.util.regex.Pattern;

/**
 * DateUtil类完成对日期的基本操作
 * 
 */
public class DateUtil { 

	public static final int HOUR = 1129 + 1;
	public static final int DATE = 1129 + 2;
	public static final int WEEK = 1129 + 3;
	public static final int MONTH = 1129 + 4;
	public static final int SEASON = 1129 + 5;
	public static final int YEAR = 1129 + 6;
	public static final String BEGIN = "begin";
	public static final String END = "end";

	/**
	 * 得到当前的时间，时间格式为：01:01:01
	 * 
	 * @return String
	 */
	public static String getStrCurrentTime() {
		Date utilDate = new Date();
		String strDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		strDate = sdf.format(utilDate);
		return strDate;
	}

	/**
	 * 得到当前的日期时间，时间格式为：2000-01-01 01:01:01
	 * 
	 * @return String
	 */
	public static String getStrCurrentDateTime() {
		Date utilDate = new Date();
		String strDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		strDate = sdf.format(utilDate);
		return strDate;
	}

	/**
	 * 得到当前的日期，日期格式为：2000-01-01
	 * 
	 * @return String
	 */
	public static String getStrCurrentDate() {
		Date utilDate = new Date();
		String strDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		strDate = sdf.format(utilDate);
		return strDate;
	}

	/**
	 * 得到当前的月分,格式为:一月份
	 */
	public static String getStrCurrentMonth() {
		String month = "";
		int m = getCurrentMonth();
		switch (m) {
		case 1:
			month = "一月份";
			break;
		case 2:
			month = "二月份";
			break;
		case 3:
			month = "三月份";
			break;
		case 4:
			month = "四月份";
			break;
		case 5:
			month = "五月份";
			break;
		case 6:
			month = "六月份";
			break;
		case 7:
			month = "七月份";
			break;
		case 8:
			month = "八月份";
			break;
		case 9:
			month = "九月份";
			break;
		case 10:
			month = "十月份";
			break;
		case 11:
			month = "十一月份";
			break;
		case 12:
			month = "十二月份";
			break;
		}
		return month;
	}

	/**
	 * 计算日期时间，参数utilDate为Date型，
	 * 
	 * @param type为要计算的类型
	 *            ，其中有：year,month,day,hour,minute,second，
	 * @param amount为要增加或减少的值
	 *            ，增加输入正数，减少输入负数，如-1
	 * @return String
	 */
	public static String getStrAccountDateTime(Date utilDate, String type,
			int amount) {
		String strDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		GregorianCalendar gc = new GregorianCalendar();
		gc.setGregorianChange(utilDate);
		if ("year".equals(type)) {
			gc.add(GregorianCalendar.YEAR, amount);
		} else if ("month".equals(type)) {
			gc.add(GregorianCalendar.MONTH, amount);
		} else if ("day".equals(type)) {
			gc.add(GregorianCalendar.DATE, amount);
		} else if ("hour".equals(type)) {
			gc.add(GregorianCalendar.HOUR_OF_DAY, amount);
		} else if ("minute".equals(type)) {
			gc.add(GregorianCalendar.MINUTE, amount);
		} else if ("second".equals(type)) {
			gc.add(GregorianCalendar.SECOND, amount);
		}
		strDate = sdf.format(gc.getTime());
		System.out.println(strDate);
		return strDate;
	}

	/**
	 * 计算日期，参数utilDate为Date型，
	 * 
	 * @param type
	 *            为要计算的类型，其中有：year,month,day,hour,minute,second，
	 * @param amount
	 *            为要增加或减少的值，增加输入正数，减少输入负数，如-1
	 * @return String
	 */
	public static String getStrAccountDate(Date utilDate, String type,
			int amount) {
		String strDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		GregorianCalendar gc = new GregorianCalendar();
		gc.setGregorianChange(utilDate);
		if ("year".equals(type)) {
			gc.add(GregorianCalendar.YEAR, amount);
		} else if ("month".equals(type)) {
			gc.add(GregorianCalendar.MONTH, amount);
		} else if ("day".equals(type)) {
			gc.add(GregorianCalendar.DATE, amount);
		}
		strDate = sdf.format(gc.getTime());
		// System.out.println(strDate);
		return strDate;
	}

	/**
	 * 得到当前年
	 * 
	 * @return int
	 */
	public static int getCurrentYear() {
		int year = 0;
		Date utilDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		year = Integer.parseInt(sdf.format(utilDate));
		return year;
	}

	/**
	 * 得到当前月
	 * 
	 * @return int
	 */
	public static int getCurrentMonth() {
		int month = 0;
		Date utilDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM");
		month = Integer.parseInt(sdf.format(utilDate));
		return month;
	}

	/**
	 * 得到当前日
	 * 
	 * @return int
	 */
	public static int getCurrentDay() {
		int date = 0;
		Date utilDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		date = Integer.parseInt(sdf.format(utilDate));
		return date;
	}

	/**
	 * 得到当前时
	 * 
	 * @return int
	 */
	public static int getCurrentHour() {
		int hour = 0;
		Date utilDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("HH");
		hour = Integer.parseInt(sdf.format(utilDate));
		return hour;
	}

	/**
	 * 得到当前分
	 * 
	 * @return int
	 */
	public static int getCurrentMinute() {
		int minute = 0;
		Date utilDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("mm");
		minute = Integer.parseInt(sdf.format(utilDate));
		return minute;
	}

	/**
	 * 得到当前秒
	 * 
	 * @return int
	 */
	public static int getCurrentSecond() {
		int second = 0;
		Date utilDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("ss");
		second = Integer.parseInt(sdf.format(utilDate));
		return second;
	}

	/**
	 * 得到输入日期和当前日期相差的天数
	 * 
	 * @param strDate
	 *            输入的日期 格式：2005-01-01 01:01:01
	 * @return long
	 */
	public static long getSubtractDate(String strDate) {
		long i = 0;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date uDate = sdf.parse(strDate);
			Date date = new Date();
			i = date.getTime() - uDate.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return i / (3600 * 24 * 1000);
	}

	/**
	 * 得到两个日期时间差
	 * 
	 * @param startDate
	 *            ,endDate 输入的日期 格式：2005-01-01
	 * @return long
	 */
	public static long getSubtractDate(String startDate, String endDate) {
		long i = 0;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date sd = sdf.parse(startDate);
			Date ed = sdf.parse(endDate);
			i = ed.getTime() - sd.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return i / (3600 * 24 * 1000);
	}
	
	/**
	 * 格式化时间
	 * 
	 * @param startDate
	 *            ,endDate 输入的日期 格式：2005-01-01
	 * @return long
	 */
	public static String formatDate(String date) {
		try {
		    //
			
		} catch (Exception e) {
			return date;
		}
		return date;
	}
	/**
	 * 时间计算
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static String getDate(String startDate,String noDate){
		String new_date=""; 
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try{
		Date now = df.parse(noDate);//当前日期
		Date date=df.parse(startDate);
		long l=now.getTime()-date.getTime();
		long day=l/(24*60*60*1000);
		long hour=(l/(60*60*1000)-day*24);
		long min=((l/(60*1000))-day*24*60-hour*60);
		long s=(l/1000-day*24*60*60-hour*60*60-min*60);
		if(Math.abs(day)>5){
		   return new_date=startDate.substring(0,10);
		}else if(Math.abs(day)==1){
		     return new_date="昨天";
		}else if(Math.abs(day)==2){
		     return new_date="前天";
		}else if(Math.abs(day)>2&Math.abs(day)<=5){
		     return new_date=Math.abs(day)+"天前";
		}else if(Math.abs(day)==0){
			 if(Math.abs(hour)>0){
				return new_date=Math.abs(hour)+"小时前";
			 }else{
				 if(Math.abs(min)>0){
				    return new_date=Math.abs(min)+"分钟前";
				 }else{
					return new_date=Math.abs(s)+"秒钟前"; 
				 }
			 }
		}
		}catch(Exception e){
			return "格式错";
		}
		return new_date;
	}

	/**
	 *get the specified date range based on ref. the range may be today, this
	 * week, this month, this season or this year.
	 * 
	 * @param ref
	 *            : the benchmark of the range.
	 * @param range
	 *            : the range rounding the ref. DAY, WEEK, MONTH, SEASON or
	 *            YEAR.
	 * 
	 * @return a map with two entries, keyed BEGIN and END, whose values are of
	 *         Date type and indicate the begin date and the end date of the
	 *         range.
	 */
	public static Map getDateRange(Date ref, int range) {
		@SuppressWarnings("rawtypes")
		Map m = new HashMap();
		Calendar cal = Calendar.getInstance();
		cal.setTime(ref);

		// this week
		if (range == WEEK) {
			int cnt = 0;
			for (; cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY; cnt++) {
				cal.add(Calendar.DATE, -1);
			}
			m.put(BEGIN, cal.getTime());
			cal.add(Calendar.DATE, cnt);
			while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
				cal.add(Calendar.DATE, 1);
			}
			m.put(END, cal.getTime());

			// this month
		} else if (range == MONTH) {
			cal.set(Calendar.DATE, 1);
			m.put(BEGIN, cal.getTime());

			int month = cal.get(Calendar.MONTH) + 1;
			switch (month) {
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				cal.set(Calendar.DATE, 31);
				break;
			case 4:
			case 6:
			case 9:
			case 11:
				cal.set(Calendar.DATE, 30);
				break;
			case 2:
				int year = cal.get(Calendar.YEAR);
				if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
					cal.set(Calendar.DATE, 29);
				} else {
					cal.set(Calendar.DATE, 28);
				}
				break;
			}
			m.put(END, cal.getTime());

			// this season
		} else if (range == SEASON) {
			// int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			switch (month / 3) {
			case 0: // 01-01 to 03-31
				cal.set(Calendar.MONTH, 0);
				cal.set(Calendar.DATE, 1);
				m.put(BEGIN, cal.getTime());
				cal.set(Calendar.MONTH, 2);
				cal.set(Calendar.DATE, 31);
				m.put(END, cal.getTime());
				break;
			case 1: // 04-01 to 06-30
				cal.set(Calendar.MONTH, 3);
				cal.set(Calendar.DATE, 1);
				m.put(BEGIN, cal.getTime());
				cal.set(Calendar.MONTH, 5);
				cal.set(Calendar.DATE, 30);
				m.put(END, cal.getTime());
				break;
			case 2: // 07-01 to 09-30
				cal.set(Calendar.MONTH, 6);
				cal.set(Calendar.DATE, 1);
				m.put(BEGIN, cal.getTime());
				cal.set(Calendar.MONTH, 8);
				cal.set(Calendar.DATE, 30);
				m.put(END, cal.getTime());
				break;
			case 3: // 10-01 to 12-31
				cal.set(Calendar.MONTH, 9);
				cal.set(Calendar.DATE, 1);
				m.put(BEGIN, cal.getTime());
				cal.set(Calendar.MONTH, 11);
				cal.set(Calendar.DATE, 31);
				m.put(END, cal.getTime());
				break;
			}

			// this year
		} else if (range == YEAR) {
			cal.set(Calendar.MONTH, 0);
			cal.set(Calendar.DATE, 1);
			m.put(BEGIN, cal.getTime());
			cal.set(Calendar.MONTH, 11);
			cal.set(Calendar.DATE, 31);
			m.put(END, cal.getTime());
			// default is DAY
		} else {
			m.put(BEGIN, cal.getTime());
			m.put(END, cal.getTime());
		}
		setTime(m); // optional
		return m;
	}

	/**
	 * get the specified date range based on ref. the range may be this week,
	 * this month, this season or this year.
	 * 
	 * @param ref
	 *            : the benchmark of the range. a String of "yyyy-mm-dd" format.
	 * @param range
	 *            : the range rounding the ref. WEEK, MONTH, SEASON or YEAR.
	 * 
	 * @return a map with two entries, keyed BEGIN and END, whose values are of
	 *         Date type and indicate the begin date and the end date of the
	 *         range. if the input string dosen't match "yyyy-M[M]-d[d]" format,
	 *         null will be returned.
	 */
	public static Map getDateRange(String ref, int range) {
		if (!ref.matches("\\d{4}-\\d{1,2}-\\d{1,2}"))
			return null;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, Integer.parseInt(ref.split("-")[0]));
		cal.set(Calendar.MONTH, Integer.parseInt(ref.split("-")[1]) - 1);
		cal.set(Calendar.DATE, Integer.parseInt(ref.split("-")[2]));
		Date d = cal.getTime();
		return getDateRange(d, range);
	}

	/**
	 * convert a Date type variable to String type, applying the specified
	 * format.
	 * 
	 * @param d
	 * @param format
	 * @return
	 */
	public static String dateToStr(Date d, String format) {
		SimpleDateFormat df = (SimpleDateFormat) SimpleDateFormat
				.getDateInstance();
		df.applyPattern(format);
		return df.format(d);
	}

	/**
	 * convert a Date type variable to String, applying the "yyyy-MM-dd" format.
	 * 
	 * @param d
	 * @return
	 */
	public static String dateToStr(Date d) {
		return dateToStr(d, "yyyy-MM-dd");
	}

	/**
	 * convert a String type variable to Date type. this method only support the
	 * "yyyy-MM-dd" format, if dosen't match, null will be returned.
	 * 
	 * @param strDate
	 * @return
	 */
	public static Date strToDate(String strDate) {
		if (!strDate.matches("\\d{4}-\\d{1,2}-\\d{1,2}"))
			return null;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, Integer.parseInt(strDate.split("-")[0]));
		cal.set(Calendar.MONTH, Integer.parseInt(strDate.split("-")[1]) - 1);
		cal.set(Calendar.DATE, Integer.parseInt(strDate.split("-")[2]));
		return cal.getTime();
	}

	/**
	 * set the begin time of the date range to 00:00:00, and the end time of the
	 * date range to 23:59:59.
	 * 
	 * @param m
	 *            : the date range map.
	 */
	private static void setTime(Map m) {
		Calendar cal = Calendar.getInstance();
		cal.setTime((Date) m.get(BEGIN));
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		m.put(BEGIN, cal.getTime());

		cal.setTime((Date) m.get(END));
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		m.put(END, cal.getTime());
	}

	/*-------------以下是向数据库中存入日期和时间的方法-------------*/

	/**
	 * 向oracle中插入时间
	 * 
	 * @param utilDate
	 *            Date日期类型
	 * @return Timestamp
	 */
	public static Timestamp setSqlCurrentDateTime(Date utilDate) {
		Timestamp ts = new Timestamp(utilDate.getTime());
		return ts;
	}

	/**
	 * 向oracle中插入时间
	 * 
	 * @param year
	 *            int型
	 * @param month
	 *            int型
	 * @param date
	 *            int型
	 * @return Timestamp
	 */
	public static Timestamp setSqlDate(int year, int month, int date) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, date);
		Date utilDate = cal.getTime();
		Timestamp ts = new Timestamp(utilDate.getTime());
		return ts;
	}

	/**
	 * 向oracle中插入时间
	 * 
	 * @param year
	 *            int型
	 * @param month
	 *            int型
	 * @param date
	 *            int型
	 * @param hour
	 *            int型
	 * @param minute
	 *            int型
	 * @param second
	 *            int型
	 * @return Timestamp
	 */
	public static Timestamp setSqlDateTime(int year, int month, int date,
			int hour, int minute, int second) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, date, hour, minute, second);
		Date utilDate = cal.getTime();
		Timestamp ts = new Timestamp(utilDate.getTime());
		return ts;
	}

	/**
	 * 向oracle中插入时间
	 * 
	 * @param strDate
	 *            String型 yyyy-MM-dd hh24:mi:ss
	 * @return Timestamp
	 */
	public static Timestamp setSqlDateTime(String strDate) {
		Pattern p = null;
		p = Pattern.compile("[- :]");
		String[] dates = p.split(strDate);

		int year = Integer.parseInt(dates[0]);
		int month = Integer.parseInt(dates[1]);
		int date = Integer.parseInt(dates[2]);
		int hour = 0;
		int min = 0;
		int sec = 0;
		if (dates.length == 6) {
			hour = Integer.parseInt(dates[3]);
			min = Integer.parseInt(dates[4]);
			sec = Integer.parseInt(dates[5]);
		}
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, date, hour, min, sec);
		Date utilDate = cal.getTime();
		Timestamp ts = new Timestamp(utilDate.getTime());
		return ts;
	}

	/**
	 * 向oracle中插入时间
	 * 
	 * @param strDate
	 *            String型 yyyy-MM-dd
	 * @return Timestamp
	 */
	public static Timestamp setSqlDate(String strDate) {
		String[] dates = strDate.split("-");
		int year = Integer.parseInt(dates[0]);
		int month = Integer.parseInt(dates[1]);
		int date = Integer.parseInt(dates[2]);

		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, date);
		Date utilDate = cal.getTime();
		Timestamp ts = new Timestamp(utilDate.getTime());
		return ts;
	}

	/*-------------以下是得到从数据库中取出日期和时间的方法-------------*/

	/**
	 * 从oracle中得到时间的年
	 * 
	 * @param ts
	 *            Timestamp型
	 * @return int
	 */
	public static int getUtilYear(Timestamp ts) {
		int year = 0;
		Date utilDate = new Date(ts.getTime());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		year = Integer.parseInt(sdf.format(utilDate));
		return year;
	}

	/**
	 * 从oracle中得到时间的月
	 * 
	 * @param ts
	 *            Timestamp型
	 * @return int
	 */
	public static int getUtilMonth(Timestamp ts) {
		int month = 0;
		Date utilDate = new Date(ts.getTime());
		SimpleDateFormat sdf = new SimpleDateFormat("MM");
		month = Integer.parseInt(sdf.format(utilDate));
		return month;
	}

	/**
	 * 从oracle中得到时间的日
	 * 
	 * @param ts
	 *            Timestamp型
	 * @return int
	 */
	public static int getUtilDay(Timestamp ts) {
		int date = 0;
		Date utilDate = new Date(ts.getTime());
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		date = Integer.parseInt(sdf.format(utilDate));
		return date;
	}

	/**
	 * 从oracle中得到时间的时
	 * 
	 * @param ts
	 *            Timestamp型
	 * @return int
	 */
	public static int getUtilHour(Timestamp ts) {
		int hour = 0;
		Date utilDate = new Date(ts.getTime());
		SimpleDateFormat sdf = new SimpleDateFormat("kk");
		hour = Integer.parseInt(sdf.format(utilDate));
		return hour;
	}

	/**
	 * 从oracle中得到时间的分
	 * 
	 * @param ts
	 *            Timestamp型
	 * @return int
	 */
	public static int getUtilMinute(Timestamp ts) {
		int minute = 0;
		Date utilDate = new Date(ts.getTime());
		SimpleDateFormat sdf = new SimpleDateFormat("mm");
		minute = Integer.parseInt(sdf.format(utilDate));
		return minute;
	}

	/**
	 * 从oracle中得到时间的秒
	 * 
	 * @param ts
	 *            Timestamp型
	 * @return int
	 */
	public static int getUtilSecond(Timestamp ts) {
		int second = 0;
		Date utilDate = new Date(ts.getTime());
		SimpleDateFormat sdf = new SimpleDateFormat("ss");
		second = Integer.parseInt(sdf.format(utilDate));
		return second;
	}

	/**
	 * 从oracle中得到日期
	 * 
	 * @param ts
	 *            Timestamp型
	 * @return String 得到yyyy-MM-dd
	 */
	public static String getUtilDate(Timestamp ts) {
		String strDate = null;
		Date utilDate = new Date(ts.getTime());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		strDate = sdf.format(utilDate);
		return strDate;
	}

	/**
	 * 从oracle中得到时间
	 * 
	 * @param ts
	 *            Timestamp型
	 * @return String 得到hh24:mi:ss
	 */
	public static String getUtilTime(Timestamp ts) {
		String strTime = null;
		Date utilDate = new Date(ts.getTime());
		SimpleDateFormat sdf = new SimpleDateFormat("kk:mm:ss");
		strTime = sdf.format(utilDate);
		return strTime;
	}

	/**
	 * 从oracle中得到日期时间
	 * 
	 * @param ts
	 *            Timestamp型
	 * @return String 得到yyyy-MM-dd hh24:mi:ss
	 */
	public static String getUtilDateTime(Timestamp ts) {
		String strDateTime = null;
		Date utilDate = new Date(ts.getTime());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
		strDateTime = sdf.format(utilDate);
		return strDateTime;
	}

	/**
	 * 得到当前月份天数
	 * 
	 * @return days
	 */
	public static double getDaysOfCurrentMonth() {
		double days = 0;
		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH) + 1;
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			days = 31;
			break;
		case 4:
		case 6:
		case 9:
		case 11:
			days = 30;
			break;
		case 2:
			int year = cal.get(Calendar.YEAR);
			if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
				days = 29;
			} else {
				days = 28;
			}
			break;
		}
		return days;
	}

	/**
	 * 由当期日期时间生成主键ID
	 * 
	 * @return id
	 */
	public static String getIdByDate() {
		String id = "";
		String year = String.valueOf(DateUtil.getCurrentYear());
		String month = DateUtil.getCurrentMonth() < 10 ? ("0" + String
				.valueOf(DateUtil.getCurrentMonth())) : String.valueOf(DateUtil
				.getCurrentMonth());
		String day = DateUtil.getCurrentDay() < 10 ? ("0" + String
				.valueOf(DateUtil.getCurrentDay())) : String.valueOf(DateUtil
				.getCurrentDay());
		String hour = DateUtil.getCurrentHour() < 10 ? ("0" + String
				.valueOf(DateUtil.getCurrentHour())) : String.valueOf(DateUtil
				.getCurrentHour());
		String minute = DateUtil.getCurrentMinute() < 10 ? ("0" + String
				.valueOf(DateUtil.getCurrentMinute())) : String
				.valueOf(DateUtil.getCurrentMinute());
		String second = DateUtil.getCurrentSecond() < 10 ? ("0" + String
				.valueOf(DateUtil.getCurrentSecond())) : String
				.valueOf(DateUtil.getCurrentSecond());
		id = year + month + day + hour + minute + second;
		return id;
	}
	
	/**
	 * 时间计算
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static String getDate(String startDate){
		String new_date=""; 
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try{
		Date now = new Date();
		Date date=(Date)df.parse(startDate);
		long l=now.getTime()-date.getTime();
		long day=l/(24*60*60*1000);
		long hour=(l/(60*60*1000)-day*24);
		long min=((l/(60*1000))-day*24*60-hour*60);
		long s=(l/1000-day*24*60*60-hour*60*60-min*60);
		if(Math.abs(day)>5){
		   return new_date=startDate.substring(0,10);
		}else if(Math.abs(day)==1){
		     return new_date="昨天";
		}else if(Math.abs(day)==2){
		     return new_date="前天";
		}else if(Math.abs(day)>2&Math.abs(day)<=5){
		     return new_date=Math.abs(day)+"天前";
		}else if(Math.abs(day)==0){
			 if(Math.abs(hour)>0){
				return new_date=Math.abs(hour)+"小时前";
			 }else{
				 if(Math.abs(min)>0){
				    return new_date=Math.abs(min)+"分钟前";
				 }else{
					return new_date=Math.abs(s)+"秒钟前"; 
				 }
			 }
		}
		}catch(Exception e){
			return "格式错";
		}
		return new_date;
	}
}
