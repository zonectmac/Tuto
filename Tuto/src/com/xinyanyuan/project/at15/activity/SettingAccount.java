package com.xinyanyuan.project.at15.activity;

import org.json.JSONObject;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.AlertView.Style;
import com.bigkoo.alertview.OnItemClickListener;
import com.loopj.android.http.RequestParams;
import com.xinyanyuan.project.at15.App;
import com.xinyanyuan.project.at15.R;
import com.xinyanyuan.project.at15.utils.ConnUtils;
import com.xinyanyuan.project.at15.utils.ConnUtils.OnHttpCallBack;
import com.zcw.togglebutton.ToggleButton;
import com.zcw.togglebutton.ToggleButton.OnToggleChanged;

public class SettingAccount extends BaseActivity {

	private ToggleButton tb_find;

	@Override
	protected int getLayoutResID() {
		return R.layout.activity_setting_account;
	}

	@Override
	protected String getActivityTitle() {
		return "账号设置";
	}

	@Override
	protected void initView() {
		findViewById(R.id.tv_safetyExit).setOnClickListener(this);
		findViewById(R.id.ll_changePassword).setOnClickListener(this);
		tb_find = (ToggleButton) findViewById(R.id.tb_find);
		open = App.getApp().isAllowFind();
		tb_find.toggle(open);
		System.out.println("-----open1" + open);
	}

	boolean open;

	@Override
	protected void onPause() {
		super.onPause();
		App.getApp().setAllowFind(open);
		if (open == false) {
			RequestParams params = new RequestParams();
			params.put("userId", App.getApp().getUser().getUserId());
			ConnUtils.post(ConnUtils.URL_CLOSE_ALLOW_FIND, params,
					new OnHttpCallBack() {

						@Override
						public void onOk(JSONObject json) {
							if (json.optInt("code") == 1)
								Toast.makeText(base, "关闭可被发现成功",
										Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onFail(int code, String err) {

						}
					});
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		tb_find.setOnToggleChanged(new OnToggleChanged() {

			@Override
			public void onToggle(boolean on) {
				open = on;
			}
		});
		System.out.println("-----open2" + open);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.tv_safetyExit:
			new AlertView("退出登陆", "就这么忍心的离开我吗？要不在考虑一下．．．", null,
					new String[] { "确定" }, new String[] { "取消" }, this,
					Style.Alert, new OnItemClickListener() {
						@Override
						public void onItemClick(Object o, int position) {
							if (position == 0)// 确定退出
							{
								boolean outLogin = App.getApp().logOut();// 退出登录
								if (outLogin) {
									startActivity(new Intent(base,
											LoginActivity.class));
								}
							}
						}
					}).show();
			break;
		case R.id.ll_changePassword:
			startActivity(new Intent(this, ChangPswActivity.class));
			break;
		}
	}

}
