package com.xinyanyuan.project.at15.activity;

import android.content.Intent;
import android.view.View;

import com.xinyanyuan.project.at15.R;

public class SettingActivity extends BaseActivity {

	@Override
	protected int getLayoutResID() {
		return R.layout.activity_settings;
	}

	@Override
	protected String getActivityTitle() {
		// TODO Auto-generated method stub
		return "œµÕ≥…Ë÷√";
	}

	@Override
	protected void initView() {
		findViewById(R.id.rl_accountSetting).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.rl_accountSetting:
			startActivity(new Intent(this, SettingAccount.class));
			break;

		}
	}
}
