package com.xinyanyuan.project.at15.view;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;

import com.xinyanyuan.project.at15.R;

public class MyPopup extends PopupWindow implements OnClickListener {
	private Activity context;

	public MyPopup(Activity context) {
		this.context = context;
		View view = View.inflate(context, R.layout.popup_xieyi, null);
		setContentView(view);// 关联Popupwindow的视图
		setWidth(LayoutParams.WRAP_CONTENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setFocusable(true);// 得到焦点
		setOutsideTouchable(true);// 当你点击的位置不在当前视图上的时候，关闭自己
		// 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener回调监听
		setBackgroundDrawable(new ColorDrawable(Color.WHITE));// 设置PopupWindow的背景颜色
		setAnimationStyle(R.style.MyPopupAnim);// 设置popup的动画
		setOnDismissListener(null);// 调用PopupWindow被取消的监听
	}

	@Override
	public void setOnDismissListener(OnDismissListener onDismissListener) {

		super.setOnDismissListener(new OnDismissListener() {// 直接创建一个接口回调给父类,类似于在外面设置监听

			@Override
			public void onDismiss() {
				h.sendEmptyMessageDelayed(0, 200);// 退出popupwindow后发送到0

			}
		});
	}

	private Handler h = new Handler() {
		public void handleMessage(android.os.Message msg) {
			LayoutParams attributes = context.getWindow().getAttributes();// 获取Activity的窗体属性
			switch (msg.what) {
			case 0:
				attributes.alpha = 1f;// 改变窗体为不透明
				break;
			case 1:
				attributes.alpha = 0.5f;// 改变窗体为透明
				break;
			}
			context.getWindow().setAttributes(attributes);// 重新提交Activity窗体的属性
		};
	};

	public void showCenterBottom(View v) {
		// showAsDropDown(v);// 显示在某个控件的正下方
		// showAsDropDown(v, 0, 12);// 显示在某个控件的下方，可以偏移
		showAtLocation(v, Gravity.BOTTOM, 0, 0);
		h.sendEmptyMessageDelayed(1, 200);
	}

	@Override
	public void onClick(View v) {

	}
}
