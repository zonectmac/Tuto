package com.xinyanyuan.project.at15.activity;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;

import org.json.JSONObject;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.xinyanyuan.project.at15.App;
import com.xinyanyuan.project.at15.R;
import com.xinyanyuan.project.at15.utils.ConnUtils;
import com.xinyanyuan.project.at15.utils.ConnUtils.OnHttpCallBack;
import com.xinyanyuan.project.at15.view.MyPopup;

public class RegisterActivity extends BaseActivity {

	private EditText et_userName_register, et_psw1_register, et_psw2_register;
	private Button btn_getcode, btn_register;
	private CheckBox cb_agree;
	private String userPhone;

	@Override
	protected int getLayoutResID() {

		return R.layout.activity_register;
	}

	@Override
	protected String getActivityTitle() {

		return null;
	}

	@Override
	protected void initView() {
		et_userName_register = (EditText) findViewById(R.id.et_userName_register);
		et_psw1_register = (EditText) findViewById(R.id.et_psw1_register);
		et_psw2_register = (EditText) findViewById(R.id.et_psw2_register);
		btn_getcode = (Button) findViewById(R.id.btn_getcode);
		btn_getcode.setOnClickListener(this);
		btn_register = (Button) findViewById(R.id.btn_register);
		btn_register.setOnClickListener(this);
		cb_agree = (CheckBox) findViewById(R.id.cb_agree);
		cb_agree.setOnClickListener(this);
		findViewById(R.id.tv_xieYi).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_getcode:
			userPhone = et_userName_register.getText().toString().trim();
			// 打开注册页面
			RegisterPage registerPage = new RegisterPage();
			registerPage.setPhoneNumber(userPhone);
			registerPage.setRegisterCallback(new EventHandler() {
				public void afterEvent(int event, int result, Object data) {
					// 解析注册结果
					if (result == SMSSDK.RESULT_COMPLETE) {
						@SuppressWarnings("unchecked")
						HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
						String country = (String) phoneMap.get("country");
						String phone = (String) phoneMap.get("phone");
						// 提交用户信息
						// registerUser(country, phone);
						et_userName_register.setEnabled(false);
						userPhone = phone;
					}
				}
			});
			registerPage.show(this);
			break;
		case R.id.cb_agree:
			if (cb_agree.isChecked()) {
				btn_register.setEnabled(true);
			} else {
				btn_register.setEnabled(false);
			}
			break;
		case R.id.btn_register:

			comitRegister();
			break;
		case R.id.tv_xieYi:
			new MyPopup(this).showCenterBottom(v);
			break;
		}
	}

	/**
	 * 提交注册
	 */
	private String psw1, psw2;

	private void comitRegister() {
		psw1 = et_psw1_register.getText().toString().trim();
		psw2 = et_psw2_register.getText().toString().trim();
		if (!TextUtils.equals(psw1, psw2)) {
			showToast("两次密码不一致");
		}
		postRegister();
	}

	public static final String USER_PHONE = "userPhone";
	public static final String PASSWORD = "password";

	private void postRegister() {
		// 请求网络。开始注册
		AsyncHttpClient http = App.getApp().getHttp();
		RequestParams params = new RequestParams();
		params.put(USER_PHONE, userPhone);
		params.put(PASSWORD, psw1);
		params.put("channel", "ADR");// 注册渠道
		params.put("termno", getMAC());// 终端
		ConnUtils.post(ConnUtils.URL_REGISTER, params, new OnHttpCallBack() {// 提交数据

					@Override
					public void onOk(JSONObject json) {
						int code = json.optInt("code");
						String message = json.optString("message");
						showToast(message);
						if (code == 1) {
							Intent data = new Intent();
							data.putExtra(USER_PHONE, userPhone);
							data.putExtra(PASSWORD, psw1);
							setResult(RESULT_OK, data);// intent为A传来的带有Bundle的intent，当然也可以自己定义新的Bundle
						}
						finish();// 此处一定要调用finish()方法

					}

					@Override
					public void onFail(int code, String err) {
						finish();
					}
				});
	}

	/**
	 * @author MrWang 获取客户端MAC地址
	 */
	private String getMAC() {
		Enumeration<NetworkInterface> el;
		String mac_s = "";
		try {
			el = NetworkInterface.getNetworkInterfaces();
			while (el.hasMoreElements()) {
				byte[] mac = el.nextElement().getHardwareAddress();
				if (mac == null)
					continue;
				mac_s = hexByte(mac[0]) + "-" + hexByte(mac[1]) + "-"
						+ hexByte(mac[2]) + "-" + hexByte(mac[3]) + "-"
						+ hexByte(mac[4]) + "-" + hexByte(mac[5]);
				System.out.println(mac_s + "MAC地址");
			}
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		return mac_s;
	}

	private String hexByte(byte b) {
		String s = "000000" + Integer.toHexString(b);
		return s.substring(s.length() - 2);
	}
}
