package com.xinyanyuan.project.at15.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;

import com.xinyanyuan.project.at15.App;
import com.xinyanyuan.project.at15.R;

public class WelcomeActivity extends InstrumentedActivity {
	public static final String FIRST_USE = "first_use";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		if (!App.getApp().isOWA()) {
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(App.getApp());
		SharedPreferences sp = App.getApp().getSP();
		if (sp.getBoolean(FIRST_USE, true)) {
			startActivity(new Intent(this, GuidanceActivity.class));
			finish();
			return;
		}
		h.sendEmptyMessage(0);
	}

	private int i = 0;
	private Handler h = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				i++;
				if (i == 3) {
					App.getApp().hasWelcomeActivity();
					startActivity(new Intent(WelcomeActivity.this,
							LoginActivity.class));
					finish();

				} else {
					h.sendEmptyMessageDelayed(0, 1000);
				}

				break;

			}
		};
	};

	@Override
	protected void onPause() {
		super.onPause();
		JPushInterface.onPause(App.getApp());
		finish();
	};

}
