package cn.sharesdk.onekeyshare;

import android.os.Handler.Callback;
import android.os.Message;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;

import com.mob.tools.utils.UIHandler;

/**
 * ������Onekeyshare�в�����;��ֻ����Socialization�п��ǵ���϶ȣ���Ҫͨ������ķ�ʽ����Onekeyshare�� ��ԭ�ȵ�
 * {@link ShareContentCustomizeCallback}�޷���ɴ����󣬹ʴ������࣬�Թ��ⲿ���ò����ص���
 *
 * @author Brook
 */
public class ReflectableShareContentCustomizeCallback implements ShareContentCustomizeCallback
{
	private int onShareWhat;
	private Callback onShareCallback;

	public void setOnShareCallback(int what, Callback callback)
	{
		onShareWhat = what;
		onShareCallback = callback;
	}

	@Override
	public void onShare(Platform platform, ShareParams paramsToShare)
	{
		if (onShareCallback != null)
		{
			Message msg = new Message();
			msg.what = onShareWhat;
			msg.obj = new Object[] { platform, paramsToShare };
			UIHandler.sendMessage(msg, onShareCallback);
		}
	}

}
