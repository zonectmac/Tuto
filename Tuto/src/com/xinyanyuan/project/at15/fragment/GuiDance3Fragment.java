package com.xinyanyuan.project.at15.fragment;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.View.OnClickListener;

import com.xinyanyuan.project.at15.App;
import com.xinyanyuan.project.at15.R;
import com.xinyanyuan.project.at15.activity.LoginActivity;
import com.xinyanyuan.project.at15.activity.WelcomeActivity;

public class GuiDance3Fragment extends BaseFragment implements OnClickListener {

	@Override
	protected int getLayoutResID() {

		return R.layout.fragment_guidance3;
	}

	@Override
	protected void initView() {
		findViewById(R.id.btn_startTiYan).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		startActivity(new Intent(getActivity(), LoginActivity.class));
		Editor edit = App.getApp().getSP().edit();
		edit.putBoolean(WelcomeActivity.FIRST_USE, false);
		edit.commit();
		getActivity().finish();
	}

}
