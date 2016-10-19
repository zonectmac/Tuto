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
		String userName = sp.getString(RegisterActivity.USER_PHONE, null);// �õ���¼����û���
		String psw = sp.getString(RegisterActivity.PASSWORD, null);// �õ���½�������
		et_userName_login.setText(userName);
		et_psw_login.setText(psw);
		if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(psw)) {

			onClick(findViewById(R.id.btn_logIn));// �����ٴ�loginActivity������û��������붼��Ϊ�վ�ֱ�ӵ�¼
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
			// �������startActivityForResult������ת���˴���LOGIN2REGISTERΪһ�����ݣ�����д������ֵ����һ��Ҫ>=0,
			// ��register����finish����Զ���ת��login����
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
		case R.id.iv_qqLogin:// qq��¼
			showToast("qqdenglu");
			// QQ�ռ�
			Platform qzone = ShareSDK.getPlatform(QQ.NAME);
			authorize(qzone);
			break;
		case R.id.iv_weinLogin:// ΢�ŵ�¼
			showToast("weixindenglu");
			// ΢�ŵ�¼
			// ����ʱ����Ҫ���ǩ����sample����ʱ������Ŀ�����demokey.keystore
			// ���ǩ��apk,Ȼ����ܲ���΢�ŵĵ�¼
			Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
			authorize(wechat);
			break;

		}
	}

	// ִ����Ȩ,��ȡ�û���Ϣ
	private void authorize(Platform plat) {
		if (plat == null)
			return;
		plat.setPlatformActionListener(mPlatformActionListener);
		// �ر�SSO��Ȩ
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
				String userId = db.getUserId();// �������û����˺�
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
				Toast.makeText(base, "��Ȩ������ȡ��", Toast.LENGTH_SHORT).show();
				break;
			case MSG_AUTH_ERROR:
				Toast.makeText(base, "��Ȩ���������������Ķ�Logcat���; \n ��΢�ŵ�¼����Ҫ΢�ſͻ���",
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
	 * �ı������(EditText)�л��������ʾ������
	 */
	private void pswShow() {
		if (isSeepsw) {
			// ����EditText�ı�Ϊ�ɼ���
			et_psw_login
					.setTransformationMethod(HideReturnsTransformationMethod
							.getInstance());
		} else {
			// ����EditText�ı�Ϊ���ص�
			et_psw_login.setTransformationMethod(PasswordTransformationMethod
					.getInstance());
		}
		isSeepsw = !isSeepsw;
		et_psw_login.postInvalidate();
		// �л���EditText�������ĩβ
		CharSequence charSequence = et_psw_login.getText();
		if (charSequence instanceof Spannable) {
			Spannable spanText = (Spannable) charSequence;
			Selection.setSelection(spanText, charSequence.length());
		}
	}

	private boolean isPdShow;

	private void postLogin(final String userPhone, final String password) {
		pd = new ProgressDialog(this);
		pd.setMessage("���ڵ�½...");
		pd.setCancelable(true);
		pd.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				isPdShow = false;
			}
		});
		pd.show();
		isPdShow = true;
		/* �����ϴ�* */
		RequestParams params = new RequestParams();
		params.put("userPhone", userPhone);
		params.put("password", password);
		ConnUtils.post(ConnUtils.URL_LOGIN, params, new OnHttpCallBack() {

			@Override
			public void onOk(JSONObject json) {// �ϴ��ɹ�
				if (!isPdShow) {
					return;
				}
				int code = json.optInt("code");
				if (code == 1) {
					JSONObject obj = json.optJSONArray("list").optJSONObject(0);// ���list���obj
					String userId = obj.optString("userId");
					String nickName = obj.optString("nickName");
					String icon = obj.optString("icon");
					App.getApp().setUser(
							new User(userId, nickName, icon, userPhone));// �����û�����
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
			public void onFail(int code, String err) {// �ϴ�ʧ��
				if (pd != null) {
					pd.dismiss();
				}
			}
		});
	}

	/**
	 * ��½�����û���ϸ��Ϣ
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
			String userPhone = data.getStringExtra(RegisterActivity.USER_PHONE);// �õ�ע����ֻ�����
			String password = data.getStringExtra(RegisterActivity.PASSWORD);// �õ�ע�������
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
			showToast("�ٰ�һ���Ƴ�");
		}
	}
}
