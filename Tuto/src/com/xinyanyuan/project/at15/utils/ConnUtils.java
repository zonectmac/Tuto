package com.xinyanyuan.project.at15.utils;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.xinyanyuan.project.at15.App;

public class ConnUtils {
	public static final String URL_BASE = "http://192.168.6.138:8080/XinYanYuan/";
	public static final String URL_REGISTER = URL_BASE + "userRegister";
	public static final String URL_LOGIN = URL_BASE + "userLogin";
	public static final String URL_UPDATE_USERPASSWORD = URL_BASE
			+ "updateUserPassWord";
	public static final String URL_UPDATEICON = URL_BASE + "updateIcon";
	public static final String URL_UPDATE_BACKGROUND = URL_BASE
			+ "updateBackground";
	public static final String URL_UPDATE_USER_INFO = URL_BASE
			+ "updateUserInfo";
	public static final String URL_GET_USER_INFO = URL_BASE + "getUserInfo";
	public static final String URL_PUBLISH_SPEAK = URL_BASE + "publishSpeak";
	public static final String URL_GETSPEAK_LIST = URL_BASE + "getSpeakList";
	public static final String URL_COMMENT_SPEAK = URL_BASE + "commentSpeak";
	public static final String URL_REPLY_SPEAK = URL_BASE + "replySpeak";
	public static final String URL_GET_SPEAK_INFO = URL_BASE + "getSpeakInfo";
	public static final String URL_PRAISES_SPEAK = URL_BASE + "praisesSpeak";
	public static final String URL_OPEN_ALLOW_FIND = URL_BASE + "openAllowFind";
	public static final String URL_CLOSE_ALLOW_FIND = URL_BASE
			+ "closeAllowFind";
	public static final String URL_GET_NEARBY_PROGRAMMER = URL_BASE
			+ "getNearbyProgrammer";

	public static void post(String url, RequestParams params,
			final OnHttpCallBack onHttpCallBack) {
		App.getApp().getHttp()
				.post(url, params, new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						String jsonStr = new String(responseBody);
						SysooLin.i(jsonStr);
						try {
							JSONObject jsonObject = new JSONObject(jsonStr);
							if (onHttpCallBack != null) {
								onHttpCallBack.onOk(jsonObject);
							}
						} catch (JSONException e) {
							if (onHttpCallBack != null) {
								onHttpCallBack.onFail(e.hashCode(),
										"json∏Ò ΩªØ ß∞‹ " + e.getMessage());
							}
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						SysooLin.i(statusCode);
						if (onHttpCallBack != null) {
							onHttpCallBack.onFail(statusCode, "Õ¯¬Á«Î«Û ß∞‹ ");
						}
					}
				});
	}

	public interface OnHttpCallBack {
		void onOk(JSONObject json);

		void onFail(int code, String err);
	}

}
