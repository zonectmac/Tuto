package com.xinyanyuan.project.at15.activity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

import com.loopj.android.http.RequestParams;
import com.xinyanyuan.project.at15.App;
import com.xinyanyuan.project.at15.R;
import com.xinyanyuan.project.at15.interfaces.MyTagAliasCallback;
import com.xinyanyuan.project.at15.model.User;
import com.xinyanyuan.project.at15.utils.ConnUtils;
import com.xinyanyuan.project.at15.utils.ConnUtils.OnHttpCallBack;
import com.xinyanyuan.project.at15.utils.SysooLin;

public class LoginActivity extends BaseActivity {

	private EditText et_userName_login, et_psw_login;
	private final int LOGIN2REGISTER = 0x222;
	private ProgressDialog pd;

	@Override
	protected int getLayoutResID() {
		// TODO Auto-generated method stub
		return R.layout.activity_login;
	}

	@Override
	protected String getActivityTitle() {

		return null;
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences sp = App.getApp().getSP();
		String userName = sp.getString(RegisterActivity.USER_PHONE, null);// 拿到登录后的用户名
		String psw = sp.getString(RegisterActivity.PASSWORD, null);// 拿到登陆后的密码
		et_userName_login.setText(userName);
		et_psw_login.setText(psw);
		if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(psw)) {

			onClick(findViewById(R.id.btn_logIn));// 当我再打开loginActivity后如果用户名和密码都不为空就直接登录
		}
		ShareSDK.initSDK(App.getApp());
	}

	@Override
	protected void initView() {
		findViewById(R.id.tv_register).setOnClickListener(this);
		et_userName_login = (EditText) findViewById(R.id.et_userName_login);
		et_psw_login = (EditText) findViewById(R.id.et_psw_login);
		findViewById(R.id.iv_clear).setOnClickListener(this);
		findViewById(R.id.iv_showpsw).setOnClickListener(this);
		findViewById(R.id.btn_logIn).setOnClickListener(this);
		findViewById(R.id.iv_qqLogin).setOnClickListener(this);
		findViewById(R.id.iv_weinLogin).setOnClickListener(this);
	}

	String userPhone, password;
	boolean isSeepsw = true;

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.tv_register:
			// 这里采用startActivityForResult来做跳转，此处的LOGIN2REGISTER为一个依据，可以写其他的值，但一定要>=0,
			// 当register界面finish后会自动跳转到login界面
			startActivityForResult(new Intent(this, RegisterActivity.class),
					LOGIN2REGISTER);
			break;
		case R.id.iv_clear:
			et_userName_login.setText("");
			break;
		case R.id.iv_showpsw:
			pswShow();
			break;
		case R.id.btn_logIn:
			userPhone = et_userName_login.getText().toString().trim();
			password = et_psw_login.getText().toString().trim();
			postLogin(userPhone, password);
			break;
		case R.id.iv_qqLogin:// qq登录
			showToast("qqdenglu");
			// QQ空间
			Platform qzone = ShareSDK.getPlatform(QQ.NAME);
			authorize(qzone);
			break;
		case R.id.iv_weinLogin:// 微信登录
			showToast("weixindenglu");
			// 微信登录
			// 测试时，需要打包签名；sample测试时，用项目里面的demokey.keystore
			// 打包签名apk,然后才能产生微信的登录
			Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
			authorize(wechat);
			break;

		}
	}

	// 执行授权,获取用户信息
	private void authorize(Platform plat) {
		if (plat == null)
			return;
		plat.setPlatformActionListener(mPlatformActionListener);
		// 关闭SSO授权
		plat.SSOSetting(false);
		plat.showUser(null);
	}

	private PlatformActionListener mPlatformActionListener = new PlatformActionListener() {

		@Override
		public void onError(Platform platform, int action, Throwable t) {
			if (action == Platform.ACTION_USER_INFOR) {
				h.sendEmptyMessage(MSG_AUTH_ERROR);
			}
			t.printStackTrace();
		}

		@Override
		public void onComplete(Platform platform, int action,
				HashMap<String, Object> res) {

			if (action == Platform.ACTION_USER_INFOR) {
				PlatformDb db = platform.getDb();
				String userId = db.getUserId();// 第三方用户的账号
				System.out.println(userId);
				Message msg = new Message();
				msg.what = MSG_AUTH_COMPLETE;
				msg.obj = new Object[] { platform.getName(), res };
				h.sendMessage(msg);
			}

		}

		@Override
		public void onCancel(Platform platform, int action) {
			if (action == Platform.ACTION_USER_INFOR)
				h.sendEmptyMessage(MSG_AUTH_CANCEL);
		}
	};
	private final int MSG_AUTH_CANCEL = 9;
	private final int MSG_AUTH_ERROR = -9;
	private final int MSG_AUTH_COMPLETE = 1;

	private Handler h = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case MSG_AUTH_CANCEL:
				Toast.makeText(base, "授权操作以取消", Toast.LENGTH_SHORT).show();
				break;
			case MSG_AUTH_ERROR:
				Toast.makeText(base, "授权操作遇到错误，请阅读Logcat输出; \n 如微信登录，需要微信客户端",
						Toast.LENGTH_SHORT).show();
				break;
			case MSG_AUTH_COMPLETE:
				Object[] objs = (Object[]) msg.obj;
				String name = String.valueOf(objs[0]);
				HashMap<String, Object> res = (HashMap<String, Object>) objs[1];
				SysooLin.i(name);
				Set<Entry<String, Object>> set = res.entrySet();
				Iterator<Entry<String, Object>> it = set.iterator();
				while (it.hasNext()) {
					Entry<String, Object> entry = it.next();
					String key = entry.getKey();
					Object value = entry.getValue();
					SysooLin.i("key=" + key + "   value=" + value);
				}
				break;

			}
		};
	};

	/**
	 * 文本输入框(EditText)切换密码的显示与隐藏
	 */
	private void pswShow() {
		if (isSeepsw) {
			// 设置EditText文本为可见的
			et_psw_login
					.setTransformationMethod(HideReturnsTransformationMethod
							.getInstance());
		} else {
			// 设置EditText文本为隐藏的
			et_psw_login.setTransformationMethod(PasswordTransformationMethod
					.getInstance());
		}
		isSeepsw = !isSeepsw;
		et_psw_login.postInvalidate();
		// 切换后将EditText光标置于末尾
		CharSequence charSequence = et_psw_login.getText();
		if (charSequence instanceof Spannable) {
			Spannable spanText = (Spannable) charSequence;
			Selection.setSelection(spanText, charSequence.length());
		}
	}

	private boolean isPdShow;

	private void postLogin(final String userPhone, final String password) {
		pd = new ProgressDialog(this);
		pd.setMessage("正在登陆...");
		pd.setCancelable(true);
		pd.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				isPdShow = false;
			}
		});
		pd.show();
		isPdShow = true;
		/* 数据上传* */
		RequestParams params = new RequestParams();
		params.put("userPhone", userPhone);
		params.put("password", password);
		ConnUtils.post(ConnUtils.URL_LOGIN, params, new OnHttpCallBack() {

			@Override
			public void onOk(JSONObject json) {// 上传成功
				if (!isPdShow) {
					return;
				}
				int code = json.optInt("code");
				if (code == 1) {
					JSONObject obj = json.optJSONArray("list").optJSONObject(0);// 获得list这个obj
					String userId = obj.optString("userId");
					String nickName = obj.optString("nickName");
					String icon = obj.optString("icon");
					App.getApp().setUser(
							new User(userId, nickName, icon, userPhone));// 保存用户资料
					Editor edit = App.getApp().getSP().edit();
					edit.putString(RegisterActivity.USER_PHONE, userPhone);
					edit.putString(RegisterActivity.PASSWORD, password);
					edit.commit();
					getUserInfo();
					JPushInterface.setAliasAndTags(getApplicationContext(),
							userId + "", null, new MyTagAliasCallback());
					startActivity(new Intent(base, MainActivity.class));
					finish();
				}
				String message = json.optString("message");
				showToast(message);
				if (pd != null) {
					pd.dismiss();
				}
			}

			@Override
			public void onFail(int code, String err) {// 上传失败
				if (pd != null) {
					pd.dismiss();
				}
			}
		});
	}

	/**
	 * 登陆后获得用户详细信息
	 */
	protected void getUserInfo() {
		RequestParams params = new RequestParams();
		params.put("userPhone", App.getApp().getUser().getUserPhone());
		ConnUtils.post(ConnUtils.URL_GET_USER_INFO, params,
				new OnHttpCallBack() {
					@Override
					public void onOk(JSONObject json) {
						int code = json.optInt("code");
						showToast(json.optString("message"));
						if (code == 1) {
							JSONObject obj = json.optJSONArray("list")
									.optJSONObject(0);
							String constellation = obj
									.optString("constellation");
							String realName = obj.optString("realName");
							String personExplain = obj
									.optString("personExplain");
							String sex = obj.optString("sex");
							long birthDay = obj.optLong("birthDay");
							String hometown = obj.optString("hometown");
							String mobilePhone = obj.optString("mobilePhone");
							String location = obj.optString("location");
							String email = obj.optString("email");
							String age = obj.optString("age");
							String background = obj.optString("background");
							User user = App.getApp().getUser();
							user.setAge(age);
							user.setBackground(background);
							user.setBirthDay(birthDay);
							user.setConstellation(constellation);
							user.setRealName(realName);
							user.setPersonExplain(personExplain);
							user.setSex(sex);
							user.setHometown(hometown);
							user.setMobilePhone(mobilePhone);
							user.setLocation(location);
							user.setEmail(email);
						}
					}

					@Override
					public void onFail(int code, String err) {
					}
				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == LOGIN2REGISTER) {
			if (data == null) {
				return;
			}
			String userPhone = data.getStringExtra(RegisterActivity.USER_PHONE);// 拿到注册的手机号码
			String password = data.getStringExtra(RegisterActivity.PASSWORD);// 拿到注册的密码
			et_userName_login.setText(userPhone);
			et_psw_login.setText(password);
		}
	}

	private long lastTime = 0;

	@Override
	public void onBackPressed() {
		if (System.currentTimeMillis() - lastTime < 3000) {
			finish();
		} else {
			lastTime = System.currentTimeMillis();
			showToast("再按一次推出");
		}
	}
}
