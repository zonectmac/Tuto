package com.xinyanyuan.project.at15.interfaces;

import java.util.Set;

import android.util.Log;
import cn.jpush.android.api.TagAliasCallback;

public class MyTagAliasCallback implements TagAliasCallback
{
	private final String TAG = "MyTagAliasCallback";

	@Override
	public void gotResult(int code, String alias, Set<String> tags)
	{
		String logs;
		switch (code)
		{
		case 0:
			logs = "Set tag and alias success";
			Log.i(TAG, logs);
			break;

		case 6002:
			logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
			Log.i(TAG, logs);
			// if (ExampleUtil.isConnected(App.getApp()))
			// {
			// mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS,
			// alias), 1000 * 60);
			// } else
			// {
			// Log.i(TAG, "No network");
			// }
			break;

		default:
			logs = "Failed with errorCode = " + code;
			Log.e(TAG, logs);
		}
	}

}
