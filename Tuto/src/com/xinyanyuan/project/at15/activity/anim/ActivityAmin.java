package com.xinyanyuan.project.at15.activity.anim;

import android.app.Activity;

import com.xinyanyuan.project.at15.R;

/**
 * �Զ����װ �Ķ����ࡣĿǰֻ������Ч��
 * 
 * @author �֍���
 * @phone 18576711027
 */
public class ActivityAmin {
	public static void startActivityAmin(Activity activity) {
		activity.overridePendingTransition(R.anim.slide_right_in,
				R.anim.slide_left_out);
	}

	public static void finishActivityAmin(Activity activity) {
		activity.overridePendingTransition(R.anim.slide_left_in,
				R.anim.slide_right_out);
	}
}
