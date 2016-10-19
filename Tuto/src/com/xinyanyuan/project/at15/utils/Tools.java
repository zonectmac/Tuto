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

		return new SimpleDateFormat("yyyy��MM��dd��").format(d);
	}

	public static long string2Long(String time) {
		long t = 0L;
		try {
			t = new SimpleDateFormat("yyyy��MM��dd��").parse(time).getTime();
		} catch (ParseException e) {
			SysooLin.i("ʱ���ʽ���쳣 error" + e.getMessage());
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
	// // ��ȡ������賿00:00
	// long cl = 0L;
	// try {
	// cl = new SimpleDateFormat(FORMAT_DATE).parse(
	// new SimpleDateFormat(FORMAT_DATE).format(new Date(time)))
	// .getTime();
	// } catch (ParseException e) {
	// e.printStackTrace();
	// }
	// if (time >= cl) {// ����
	// return new SimpleDateFormat("���� HH:mm").format(new Date(time));
	// } else if (time < cl && time > cl - 24 * 60 * 1000 * 60) {// ����
	// return new SimpleDateFormat("���� HH:mm").format(new Date(time));
	// } else if (time < cl - 24 * 60 * 1000 * 60
	// && time > cl - 24 * 60 * 1000 * 60 * 2) {// ǰ��
	// return new SimpleDateFormat("ǰ�� HH:mm").format(new Date(time));
	// } else {
	// return new SimpleDateFormat("MM��dd�� HH:mm").format(new Date(time));
	// }
	// }
	/***********************************************/
	// /**
	// * ��ʽ��ʱ�䣨��������� �ո�, 4����ǰ, һСʱǰ, ����������ʱ�䣩
	// *
	// * @param time ��Ҫ��ʽ����ʱ�� ��"2014-07-14 19:01:45"
	// * @param pattern �������time��ʱ���ʽ ��:"yyyy-MM-dd HH:mm:ss"
	// * <p/>���Ϊ����Ĭ��ʹ��"yyyy-MM-dd HH:mm:ss"��ʽ
	// * @return timeΪnull������ʱ���ʽ��ƥ�䣬������ַ�""
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
	// SimpleDateFormat halfDf = new SimpleDateFormat("MM��dd��");
	// long dTime = today.getTime() - tDate.getTime();
	// if (tDate.before(thisYear)) {
	// display = new SimpleDateFormat("yyyy��MM��dd��").format(tDate);
	// } else {
	//
	// if (dTime < tMin) {
	// display = "�ո�";
	// } else if (dTime < tHour) {
	// display = (int) <a
	// href="https://www.baidu.com/s?wd=Math.ceil&tn=44039180_cpr&fenlei=mv6quAkxTZn0IZRqIHckPjm4nH00T1Y4mynkmvu9PWb1rARkrHm30ZwV5Hcvrjm3rH6sPfKWUMw85HfYnjn4nH6sgvPsT6K1TL0qnfK1TL0z5HD0IgF_5y9YIZ0lQzqlpA-bmyt8mh7GuZR8mvqVQL7dugPYpyq8Q1RvnjfYn1n1PWDzrjD4njf3P0"
	// target="_blank" class="baidu-highlight">Math.ceil</a>(dTime / tMin) +
	// "����ǰ";
	// } else if (dTime < tDay && tDate.after(yesterday)) {
	// display = (int) <a
	// href="https://www.baidu.com/s?wd=Math.ceil&tn=44039180_cpr&fenlei=mv6quAkxTZn0IZRqIHckPjm4nH00T1Y4mynkmvu9PWb1rARkrHm30ZwV5Hcvrjm3rH6sPfKWUMw85HfYnjn4nH6sgvPsT6K1TL0qnfK1TL0z5HD0IgF_5y9YIZ0lQzqlpA-bmyt8mh7GuZR8mvqVQL7dugPYpyq8Q1RvnjfYn1n1PWDzrjD4njf3P0"
	// target="_blank" class="baidu-highlight">Math.ceil</a>(dTime / tHour) +
	// "Сʱǰ";
	// } else if (tDate.after(beforeYes) && tDate.before(yesterday)) {
	// display = "����" + new SimpleDateFormat("HH:mm").format(tDate);
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
	/** ʱ���еķ֡������ֵ��Ϊ60 */
	private final static int TIME_NUMBERS = 60;
	/** ʱ���е�Сʱ���ֵ */
	private final static int TIME_HOURSES = 24;
	/** ��ʽ�����ڵı�׼�ַ��� */
	private final static String FORMAT = "yyyy-MM-dd HH:mm:ss";

	/**
	 * ��ȡ��ǰʱ�����ָ������ʱ��Ĵ��±����ʽ
	 * 
	 * @param long date ����
	 * @return ʱ��Ĵ��±����ʽ
	 * */
	public static String formatSpeekTime(long date) {
		String strTime = "�ܾúܾ���ǰ";
		long time = Math.abs(new Date().getTime() - date);
		// һ��������
		if (time < TIME_NUMBERS * TIME_MILLISECONDS) {
			strTime = "�ո�";
		} else {
			int min = (int) (time / TIME_MILLISECONDS / TIME_NUMBERS);
			if (min < TIME_NUMBERS) {
				if (min < 15) {
					strTime = "һ����ǰ";
				} else if (min < 30) {
					strTime = "��Сʱǰ";
				} else {
					strTime = "1Сʱǰ";
				}
			} else {
				int hh = min / TIME_NUMBERS;
				if (hh < TIME_HOURSES) {
					strTime = hh + "Сʱǰ";
				} else {
					int days = hh / TIME_HOURSES;
					if (days <= 6) {
						strTime = days + "��ǰ";
					} else {
						int weeks = days / 7;
						if (weeks < 3) {
							strTime = weeks + "��ǰ";
						}
					}
				}
			}
		}

		return strTime;

	}
}
