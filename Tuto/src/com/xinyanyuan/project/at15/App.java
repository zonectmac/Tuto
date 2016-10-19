package com.xinyanyuan.project.at15;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import cn.jpush.android.api.JPushInterface;
import cn.smssdk.SMSSDK;

import com.baidu.mapapi.SDKInitializer;
import com.loopj.android.http.AsyncHttpClient;
import com.xinyanyuan.project.at15.activity.RegisterActivity;
import com.xinyanyuan.project.at15.model.User;
import com.xinyanyuan.project.at15.utils.ImageLoadHelper;

public class App extends Application {

	private static App app = null;
	private User user = null;

	private boolean isOpenWelcomeActivity = true;

	public void hasWelcomeActivity() {
		isOpenWelcomeActivity = false;
	}

	public boolean isOWA() {
		return isOpenWelcomeActivity;
	}

	private List<Activity> activities = new ArrayList<Activity>();

	public void addActivity(Activity a) {
		if (!activities.contains(a)) {
			activities.add(a);
		}
	}

	private boolean isAllowFind = false;// 是否打开可被发现

	public boolean isAllowFind() {
		return isAllowFind;
	}

	public void setAllowFind(boolean isAllowFind) {
		this.isAllowFind = isAllowFind;
	}

	public static App getApp() {
		return app;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean logOut() {
		for (Activity activity : activities) {
			if (!activity.isFinishing()) {
				activity.finish();
			}
		}
		return sp.edit().remove(RegisterActivity.PASSWORD).commit();// 退出登陆
	}

	public boolean isLogin() {
		return user != null;
	}

	private AsyncHttpClient asyncHttpClient = null;
	private SharedPreferences sp;

	public AsyncHttpClient getHttp() {
		return asyncHttpClient;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		app = this;
		sp = getSharedPreferences(getApplicationInfo().name, MODE_PRIVATE);
		SMSSDK.initSDK(this, "b509722f3b19", "89d5a2c3112f8ceb378721fee5d524bd");
		asyncHttpClient = new AsyncHttpClient();
		ImageLoadHelper.init(this);
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		// JPush SDK 初始化SDK
		JPushInterface.init(this);
		// JPush SDK 设置调试模式
		JPushInterface.setDebugMode(true);
	}

	public SharedPreferences getSP() {
		return sp;
	}
}
