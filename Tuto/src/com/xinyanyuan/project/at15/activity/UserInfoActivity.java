package com.xinyanyuan.project.at15.activity;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONObject;

import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPopupWindow;
import com.bigkoo.pickerview.OptionsPopupWindow.OnOptionsSelectListener;
import com.bigkoo.pickerview.TimePopupWindow;
import com.bigkoo.pickerview.TimePopupWindow.OnTimeSelectListener;
import com.bigkoo.pickerview.TimePopupWindow.Type;
import com.loopj.android.http.RequestParams;
import com.xinyanyuan.project.at15.App;
import com.xinyanyuan.project.at15.R;
import com.xinyanyuan.project.at15.model.User;
import com.xinyanyuan.project.at15.utils.ConnUtils;
import com.xinyanyuan.project.at15.utils.ConnUtils.OnHttpCallBack;
import com.xinyanyuan.project.at15.utils.Tools;

public class UserInfoActivity extends BaseActivity {
	private EditText et_nickName, et_xingZuo, et_personExplain, et_realName,
			et_hometown, et_location, et_mobilePhone, et_email;
	private TextView tv_editDetail, tv_editBase, tv_birthDay, tv_sex;
	private TimePopupWindow pwTime;
	private OptionsPopupWindow pwOptions;

	@Override
	protected int getLayoutResID() {
		return R.layout.activity_userinfo;
	}

	@Override
	protected String getActivityTitle() {
		return "我的";
	}

	@Override
	protected void initView() {
		initPickView();
		tv_editBase = (TextView) findViewById(R.id.tv_editBase);
		tv_editBase.setOnClickListener(this);
		tv_editDetail = (TextView) findViewById(R.id.tv_editDetail);
		tv_editDetail.setOnClickListener(this);
		et_nickName = (EditText) findViewById(R.id.et_nickName);
		tv_birthDay = (TextView) findViewById(R.id.tv_birthDay);
		tv_birthDay.setOnClickListener(this);
		et_xingZuo = (EditText) findViewById(R.id.et_xingZuo);
		tv_sex = (TextView) findViewById(R.id.tv_sex);
		tv_sex.setOnClickListener(this);
		et_personExplain = (EditText) findViewById(R.id.et_personExplain);
		et_realName = (EditText) findViewById(R.id.et_realName);
		et_hometown = (EditText) findViewById(R.id.et_hometown);
		et_location = (EditText) findViewById(R.id.et_location);
		et_mobilePhone = (EditText) findViewById(R.id.et_mobilePhone);
		et_email = (EditText) findViewById(R.id.et_email);
	}

	private void initPickView() {
		// 时间选择器
		pwTime = new TimePopupWindow(this, Type.YEAR_MONTH_DAY);
		pwTime.setTime(new Date());
		// 时间选择后回调
		pwTime.setOnTimeSelectListener(new OnTimeSelectListener() {

			@Override
			public void onTimeSelect(Date date) {
				tv_birthDay.setText(Tools.date2String(date));

			}
		});
		// 选项选择器
		pwOptions = new OptionsPopupWindow(this);
		// 选项1
		options1Items.add("男");
		options1Items.add("女");
		pwOptions.setPicker(options1Items);
		// 设置默认选中的三级项目
		pwOptions.setSelectOptions(0);
		// 监听确定选择按钮
		pwOptions.setOnoptionsSelectListener(new OnOptionsSelectListener() {

			@Override
			public void onOptionsSelect(int options1, int option2, int options3) {
				tv_sex.setText(options1Items.get(options1));

			}
		});
	}

	private ArrayList<String> options1Items = new ArrayList<String>();

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.tv_editBase:
			baseEdit(isBaseEdit);
			isBaseEdit = !isBaseEdit;
			break;
		case R.id.tv_editDetail:
			detailEdit(isDetailEdit);
			isDetailEdit = !isDetailEdit;
			break;
		case R.id.tv_birthDay:
			pwTime.showAtLocation(tv_birthDay, Gravity.BOTTOM, 0, 0, new Date());
			break;
		case R.id.tv_sex:
			pwOptions.showAtLocation(tv_sex, Gravity.BOTTOM, 0, 0);
			break;
		}
	}

	private boolean isDetailEdit = true;
	private boolean isBaseEdit = true;

	private void baseEdit(boolean a) {
		et_nickName.setEnabled(a);
		tv_birthDay.setEnabled(a);
		tv_sex.setEnabled(a);
		tv_editBase.setText(a ? "保存" : "编辑");
		if (!a) {
			// post数据
			String nickName = et_nickName.getText().toString().trim();
			String birthDay = tv_birthDay.getText().toString().trim();
			String sex = tv_sex.getText().toString().trim();
			RequestParams params = new RequestParams();
			params.put("nickName", nickName);
			params.put("sex", sex);
			params.put("birthDay", Tools.string2Long(birthDay));
			postUserInfo(params);
		}

	}

	private void detailEdit(boolean a) {
		et_personExplain.setEnabled(a);
		et_realName.setEnabled(a);
		et_hometown.setEnabled(a);
		et_location.setEnabled(a);
		et_mobilePhone.setEnabled(a);
		et_email.setEnabled(a);
		tv_editDetail.setText(a ? "保存" : "编辑");
		if (!a) {
			// post数据
			String personExplain = et_personExplain.getText().toString().trim();
			String realName = et_realName.getText().toString().trim();
			String hometown = et_hometown.getText().toString().trim();
			String location = et_location.getText().toString().trim();
			String mobilePhone = et_mobilePhone.getText().toString().trim();
			String email = et_email.getText().toString().trim();

			RequestParams params = new RequestParams();
			params.put("email", email);
			params.put("location", location);
			params.put("hometown", hometown);
			params.put("personExplain", personExplain);
			params.put("mobilePhone", mobilePhone);
			params.put("realName", realName);
			postUserInfo(params);
		}
	}

	private void postUserInfo(RequestParams params) {
		params.put("userId", App.getApp().getUser().getUserId());
		ConnUtils.post(ConnUtils.URL_UPDATE_USER_INFO, params,
				new OnHttpCallBack() {

					@Override
					public void onOk(JSONObject json) {
						int code = json.optInt("code");
						showToast(json.optString("message"));
						if (code == 1) {
							JSONObject obj = json.optJSONArray("list")
									.optJSONObject(0);
							String nickName = obj.optString("nickName");
							String realName = obj.optString("realName");
							String constellation = obj
									.optString("constellation");
							String personExplain = obj
									.optString("personExplain");
							String sex = obj.optString("sex");
							String age = obj.optString("age");
							String hometown = obj.optString("hometown");
							String location = obj.optString("location");
							String email = obj.optString("email");
							String mobilePhone = obj.optString("mobilePhone");
							long birthDay = obj.optLong("birthDay");
							User user = App.getApp().getUser();
							user.setAge(age);
							user.setConstellation(constellation);
							user.setNickName(nickName);
							user.setPersonExplain(personExplain);
							user.setSex(sex);
							user.setBirthDay(birthDay);
							user.setLocation(location);
							user.setEmail(email);
							user.setHometown(hometown);
							user.setRealName(realName);
							user.setMobilePhone(mobilePhone);
						}

					}

					@Override
					public void onFail(int code, String err) {
						// TODO Auto-generated method stub

					}
				});
	}

	@Override
	protected void onResume() {
		super.onResume();
		changeInfoUI();
	}

	private void changeInfoUI() {
		User user = App.getApp().getUser();
		et_nickName.setText(user.getNickName());
		tv_birthDay.setText(user.getBirthDay());
		et_xingZuo.setText(user.getConstellation());
		tv_sex.setText(user.getSex());
		et_personExplain.setText(user.getPersonExplain());
		et_realName.setText(user.getRealName());
		et_hometown.setText(user.getHometown());
		et_location.setText(user.getLocation());
		et_mobilePhone.setText(user.getMobilePhone());
		et_email.setText(user.getEmail());

	}
}
