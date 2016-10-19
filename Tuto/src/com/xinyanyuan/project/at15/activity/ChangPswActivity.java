package com.xinyanyuan.project.at15.activity;

import org.json.JSONObject;

import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.loopj.android.http.RequestParams;
import com.xinyanyuan.project.at15.App;
import com.xinyanyuan.project.at15.R;
import com.xinyanyuan.project.at15.utils.ConnUtils;
import com.xinyanyuan.project.at15.utils.ConnUtils.OnHttpCallBack;

public class ChangPswActivity extends BaseActivity {

	private EditText et_newPsw, et_oldPsw;

	@Override
	protected int getLayoutResID() {
		// TODO Auto-generated method stub
		return R.layout.activity_changpsw;
	}

	@Override
	protected String getActivityTitle() {

		return "ÐÞ¸ÄµÇÂ¼ÃÜÂë";
	}

	@Override
	protected void initView() {
		findViewById(R.id.tv_changPswOk).setOnClickListener(this);
		et_newPsw = (EditText) findViewById(R.id.et_newPsw);
		et_oldPsw = (EditText) findViewById(R.id.et_oldPsw);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.tv_changPswOk:
			updateUserPassword();
			break;
		}
	}

	private void updateUserPassword() {
		String oldPsw = et_oldPsw.getText().toString();
		final String newPsw = et_newPsw.getText().toString();
		if (TextUtils
				.equals(oldPsw,
						App.getApp().getSP()
								.getString(RegisterActivity.PASSWORD, null))) {
			RequestParams params = new RequestParams();
			params.put("userId", App.getApp().getUser().getUserId());
			params.put("oldPsw", oldPsw);
			params.put("newPsw", newPsw);
			ConnUtils.post(ConnUtils.URL_UPDATE_USERPASSWORD, params,
					new OnHttpCallBack() {

						@Override
						public void onOk(JSONObject json) {
							if (json.optInt("code") == 1) {

								Editor edit = App.getApp().getSP().edit();
								edit.putString(RegisterActivity.PASSWORD,
										newPsw);
								edit.commit();
							}
							showToast(json.optString("message"));
						}

						@Override
						public void onFail(int code, String err) {
						}
					});
		}
	}
}
