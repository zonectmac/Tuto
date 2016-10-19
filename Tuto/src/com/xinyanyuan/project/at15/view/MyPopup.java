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
		setContentView(view);// ����Popupwindow����ͼ
		setWidth(LayoutParams.WRAP_CONTENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setFocusable(true);// �õ�����
		setOutsideTouchable(true);// ��������λ�ò��ڵ�ǰ��ͼ�ϵ�ʱ�򣬹ر��Լ�
		// ��back���������ط�ʹ����ʧ,������������ܴ���OnDismisslistener�ص�����
		setBackgroundDrawable(new ColorDrawable(Color.WHITE));// ����PopupWindow�ı�����ɫ
		setAnimationStyle(R.style.MyPopupAnim);// ����popup�Ķ���
		setOnDismissListener(null);// ����PopupWindow��ȡ���ļ���
	}

	@Override
	public void setOnDismissListener(OnDismissListener onDismissListener) {

		super.setOnDismissListener(new OnDismissListener() {// ֱ�Ӵ���һ���ӿڻص�������,���������������ü���

			@Override
			public void onDismiss() {
				h.sendEmptyMessageDelayed(0, 200);// �˳�popupwindow���͵�0

			}
		});
	}

	private Handler h = new Handler() {
		public void handleMessage(android.os.Message msg) {
			LayoutParams attributes = context.getWindow().getAttributes();// ��ȡActivity�Ĵ�������
			switch (msg.what) {
			case 0:
				attributes.alpha = 1f;// �ı䴰��Ϊ��͸��
				break;
			case 1:
				attributes.alpha = 0.5f;// �ı䴰��Ϊ͸��
				break;
			}
			context.getWindow().setAttributes(attributes);// �����ύActivity���������
		};
	};

	public void showCenterBottom(View v) {
		// showAsDropDown(v);// ��ʾ��ĳ���ؼ������·�
		// showAsDropDown(v, 0, 12);// ��ʾ��ĳ���ؼ����·�������ƫ��
		showAtLocation(v, Gravity.BOTTOM, 0, 0);
		h.sendEmptyMessageDelayed(1, 200);
	}

	@Override
	public void onClick(View v) {

	}
}
