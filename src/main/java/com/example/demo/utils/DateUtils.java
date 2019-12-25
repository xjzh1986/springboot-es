package com.example.demo.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class DateUtils {
	
	/**
	   * 将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss
	   * 
	   * @param dateDate
	   * @return
	   */
	public static String dateToStr(Date dateDate) {
	   SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   String dateString = formatter.format(dateDate);
	   return dateString;
	}
	
	/**
	   * 将长时间格式时间转换为字符串 yyyy-MM-dd
	   * 
	   * @param dateDate
	   * @return
	   */
	public static String dateToYMDStr(Date dateDate) {
	   SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	   String dateString = formatter.format(dateDate);
	   return dateString;
	}
	
	public static String longToString(long currentTime){
        Date date = new Date(currentTime);
        String strTime = dateToStr(date);
		return strTime;
	}

	/**
	 * 两个时间相差距离多少天多少小时多少分多少秒
	 * @param starttime 时间参数 1 格式：1990-01-01 12:00:00
	 * @param endtime 时间参数 2 格式：2009-01-01 12:00:00
	 * @return String 返回值为：xx天xx小时xx分xx秒
	 */
	public static String getDistanceTime(String starttime, String endtime) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date one;
		Date two;
		long day = 0;
		long hour = 0;
		long min = 0;
		long sec = 0;
		try {
			one = df.parse(starttime);
			two = df.parse(endtime);
			long time1 = one.getTime();
			long time2 = two.getTime();
			long diff ;
			if(time1<time2) {
				diff = time2 - time1;
			} else {
				diff = time1 - time2;
			}
			day = diff / (24 * 60 * 60 * 1000);
			hour = (diff / (60 * 60 * 1000) - day * 24);
			min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
			sec = (diff/1000-day*24*60*60-hour*60*60-min*60);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return day + "天" + hour + "小时" + min + "分" + sec + "秒";
	}

	/**
	 * 两个时间相差距离多少天多少小时多少分多少秒
	 * @param starttime 时间参数 1 格式：1990-01-01 12:00:00
	 * @param endtime 时间参数 2 格式：2009-01-01 12:00:00
	 * @return String 返回值为：xx天xx小时xx分xx秒
	 */
	public static String getDistanceTime(Date starttime, Date endtime) {

		long day = 0;
		long hour = 0;
		long min = 0;
		long sec = 0;
		try {

			long time1 = starttime.getTime();
			long time2 = endtime.getTime();
			long diff ;
			if(time1<time2) {
				diff = time2 - time1;
			} else {
				diff = time1 - time2;
			}
			day = diff / (24 * 60 * 60 * 1000);
			hour = (diff / (60 * 60 * 1000) - day * 24);
			min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
			sec = (diff/1000-day*24*60*60-hour*60*60-min*60);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return day + "天" + hour + "小时" + min + "分" + sec + "秒";
	}

	public static Date strToDate(String date){
		Date temp = null;
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			temp = df.parse(date);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return temp;
	}



	public static void main(String[] args){
//		System.out.println(strToDate("2019-12-25 10:00:00"));
//		System.out.println(getDistanceTime(strToDate("2019-12-25 10:00:00"),strToDate("2019-12-30 9:8:6")));

		String code = UUID.randomUUID().toString().replace("-","");
		System.out.println(code);


	}

}
