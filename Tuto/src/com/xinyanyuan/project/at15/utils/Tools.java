package com.xinyanyuan.project.at15.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

public class Tools {
	private static ProgressDialog pd;
	private static boolean isPdShow = true;

	public static String long2String(long time) {
		return date2String(new Date(time));
	}

	public static String date2String(Date d) {

		return new SimpleDateFormat("yyyy年MM月dd日").format(d);
	}

	public static long string2Long(String time) {
		long t = 0L;
		try {
			t = new SimpleDateFormat("yyyy年MM月dd日").parse(time).getTime();
		} catch (ParseException e) {
			SysooLin.i("时间格式化异常 error" + e.getMessage());
		}
		return t;
	}

	public static void showProgressDialog(Context context, String message) {

		isPdShow = true;
		pd = new ProgressDialog(context);
		pd.setCancelable(false);
		pd.setMessage(message);
		pd.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				isPdShow = false;
			}
		});
		pd.show();
	}

	public static boolean isPdShow() {
		return isPdShow;
	}

	public static void dismissPd() {
		if (pd != null) {
			pd.dismiss();
		}
	}

	// @SuppressLint("SimpleDateFormat")
	// public static String formatSpeekTime(long time) {
	// final String FORMAT_DATE = "yyyy-MM-dd";
	// // 获取当天的凌晨00:00
	// long cl = 0L;
	// try {
	// cl = new SimpleDateFormat(FORMAT_DATE).parse(
	// new SimpleDateFormat(FORMAT_DATE).format(new Date(time)))
	// .getTime();
	// } catch (ParseException e) {
	// e.printStackTrace();
	// }
	// if (time >= cl) {// 今天
	// return new SimpleDateFormat("今天 HH:mm").format(new Date(time));
	// } else if (time < cl && time > cl - 24 * 60 * 1000 * 60) {// 昨天
	// return new SimpleDateFormat("昨天 HH:mm").format(new Date(time));
	// } else if (time < cl - 24 * 60 * 1000 * 60
	// && time > cl - 24 * 60 * 1000 * 60 * 2) {// 前天
	// return new SimpleDateFormat("前天 HH:mm").format(new Date(time));
	// } else {
	// return new SimpleDateFormat("MM月dd日 HH:mm").format(new Date(time));
	// }
	// }
	/***********************************************/
	// /**
	// * 格式化时间（输出类似于 刚刚, 4分钟前, 一小时前, 昨天这样的时间）
	// *
	// * @param time 需要格式化的时间 如"2014-07-14 19:01:45"
	// * @param pattern 输入参数time的时间格式 如:"yyyy-MM-dd HH:mm:ss"
	// * <p/>如果为空则默认使用"yyyy-MM-dd HH:mm:ss"格式
	// * @return time为null，或者时间格式不匹配，输出空字符""
	// */
	// public static String formatDisplayTime(String time, String pattern) {
	// String display = "";
	// int tMin = 60 * 1000;
	// int tHour = 60 * tMin;
	// int tDay = 24 * tHour;
	//
	// if (time != null) {
	// try {
	// Date tDate = new SimpleDateFormat(pattern).parse(time);
	// Date today = new Date();
	// SimpleDateFormat thisYearDf = new SimpleDateFormat("yyyy");
	// SimpleDateFormat todayDf = new SimpleDateFormat("yyyy-MM-dd");
	// Date thisYear = new
	// Date(thisYearDf.parse(thisYearDf.format(today)).getTime());
	// Date yesterday = new
	// Date(todayDf.parse(todayDf.format(today)).getTime());
	// Date beforeYes = new Date(yesterday.getTime() - tDay);
	// if (tDate != null) {
	// SimpleDateFormat halfDf = new SimpleDateFormat("MM月dd日");
	// long dTime = today.getTime() - tDate.getTime();
	// if (tDate.before(thisYear)) {
	// display = new SimpleDateFormat("yyyy年MM月dd日").format(tDate);
	// } else {
	//
	// if (dTime < tMin) {
	// display = "刚刚";
	// } else if (dTime < tHour) {
	// display = (int) <a
	// href="https://www.baidu.com/s?wd=Math.ceil&tn=44039180_cpr&fenlei=mv6quAkxTZn0IZRqIHckPjm4nH00T1Y4mynkmvu9PWb1rARkrHm30ZwV5Hcvrjm3rH6sPfKWUMw85HfYnjn4nH6sgvPsT6K1TL0qnfK1TL0z5HD0IgF_5y9YIZ0lQzqlpA-bmyt8mh7GuZR8mvqVQL7dugPYpyq8Q1RvnjfYn1n1PWDzrjD4njf3P0"
	// target="_blank" class="baidu-highlight">Math.ceil</a>(dTime / tMin) +
	// "分钟前";
	// } else if (dTime < tDay && tDate.after(yesterday)) {
	// display = (int) <a
	// href="https://www.baidu.com/s?wd=Math.ceil&tn=44039180_cpr&fenlei=mv6quAkxTZn0IZRqIHckPjm4nH00T1Y4mynkmvu9PWb1rARkrHm30ZwV5Hcvrjm3rH6sPfKWUMw85HfYnjn4nH6sgvPsT6K1TL0qnfK1TL0z5HD0IgF_5y9YIZ0lQzqlpA-bmyt8mh7GuZR8mvqVQL7dugPYpyq8Q1RvnjfYn1n1PWDzrjD4njf3P0"
	// target="_blank" class="baidu-highlight">Math.ceil</a>(dTime / tHour) +
	// "小时前";
	// } else if (tDate.after(beforeYes) && tDate.before(yesterday)) {
	// display = "昨天" + new SimpleDateFormat("HH:mm").format(tDate);
	// } else {
	// display = halfDf.format(tDate);
	// }
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	//
	// return display;
	// }
	/******************************************/
	/** 1s==1000ms */
	private final static int TIME_MILLISECONDS = 1000;
	/** 时间中的分、秒最大值均为60 */
	private final static int TIME_NUMBERS = 60;
	/** 时间中的小时最大值 */
	private final static int TIME_HOURSES = 24;
	/** 格式化日期的标准字符串 */
	private final static String FORMAT = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 获取当前时间距离指定日期时差的大致表达形式
	 * 
	 * @param long date 日期
	 * @return 时差的大致表达形式
	 * */
	public static String formatSpeekTime(long date) {
		String strTime = "很久很久以前";
		long time = Math.abs(new Date().getTime() - date);
		// 一分钟以内
		if (time < TIME_NUMBERS * TIME_MILLISECONDS) {
			strTime = "刚刚";
		} else {
			int min = (int) (time / TIME_MILLISECONDS / TIME_NUMBERS);
			if (min < TIME_NUMBERS) {
				if (min < 15) {
					strTime = "一刻钟前";
				} else if (min < 30) {
					strTime = "半小时前";
				} else {
					strTime = "1小时前";
				}
			} else {
				int hh = min / TIME_NUMBERS;
				if (hh < TIME_HOURSES) {
					strTime = hh + "小时前";
				} else {
					int days = hh / TIME_HOURSES;
					if (days <= 6) {
						strTime = days + "天前";
					} else {
						int weeks = days / 7;
						if (weeks < 3) {
							strTime = weeks + "周前";
						}
					}
				}
			}
		}

		return strTime;

	}
}
