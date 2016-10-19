package com.xinyanyuan.project.at15.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xinyanyuan.project.at15.App;
import com.xinyanyuan.project.at15.R;
import com.xinyanyuan.project.at15.activity.anim.ActivityAmin;

public abstract class BaseActivity extends FragmentActivity implements
		OnClickListener {
	private ImageView iv_back;
	private TextView tv_title;
	protected BaseActivity base;
	private TextView tv_menu;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		base = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(getLayoutResID());
	}

	@Override
	protected void onStart() {
		super.onStart();
		App.getApp().addActivity(this);
	}

	protected abstract int getLayoutResID();

	protected abstract String getActivityTitle();

	protected abstract void initView();

	@Override
	public void setContentView(int layoutResID) {
		View activity_base = View.inflate(this, R.layout.activity_base, null);
		FrameLayout fl_baseContent = (FrameLayout) activity_base
				.findViewById(R.id.fl_baseContent);
		getLayoutInflater().inflate(layoutResID, fl_baseContent, true);
		super.setContentView(activity_base);
		String title = getActivityTitle();
		if (!TextUtils.isEmpty(title)) {
			iv_back = (ImageView) findViewById(R.id.iv_back);
			iv_back.setOnClickListener(this);
			tv_title = (TextView) findViewById(R.id.tv_title);
			tv_title.setText(title);
			tv_menu = (TextView) findViewById(R.id.iv_menu);
		} else
			findViewById(R.id.rl_Bar).setVisibility(View.GONE);
		initView();
	}

	protected TextView setMenuText(String text) {
		tv_menu.setText(text);
		tv_menu.setBackgroundResource(0);
		tv_menu.setVisibility(View.VISIBLE);
		return tv_menu;
	}

	protected TextView setMenuImage(int resId) {
		tv_menu.setBackgroundResource(resId);
		tv_menu.setText("");
		tv_menu.setVisibility(View.VISIBLE);
		return tv_menu;
	}

	protected void hideTitleBar() {
		findViewById(R.id.rl_Bar).setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			this.finish();
			break;
		}
	}

	/**
	 * Activity ¿¡»ÀToast
	 * 
	 * @param text
	 */
	protected void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		ActivityAmin.startActivityAmin(base);
	}

	@Override
	public void finish() {
		super.finish();
	}

	protected void finishAnim() {
		finish();
		ActivityAmin.finishActivityAmin(base);
	}
}
