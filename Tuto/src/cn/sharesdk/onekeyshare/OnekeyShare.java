/*
 * ������վ:http://www.mob.com
 * ����֧��QQ: 4006852216
 * �ٷ�΢��:ShareSDK   ����������°汾�Ļ������ǽ����һʱ��ͨ��΢�Ž��汾�����������͸��������ʹ�ù��������κ����⣬Ҳ����ͨ��΢��������ȡ����ϵ�����ǽ�����24Сʱ�ڸ���ظ���
 *
 * Copyright (c) 2013�� mob.com. All rights reserved.
 */

package cn.sharesdk.onekeyshare;

import static com.mob.tools.utils.BitmapHelper.captureView;
import static com.mob.tools.utils.R.getStringRes;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import cn.sharesdk.framework.CustomPlatform;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;

import com.mob.tools.utils.UIHandler;

/**
 * ��ݷ�������
 * <p>
 * ͨ����ͬ��setter���ò�����Ȼ�����{@link #show(Context)}����������ݷ���
 */
public class OnekeyShare implements PlatformActionListener, Callback
{
	private static final int MSG_TOAST = 1;
	private static final int MSG_ACTION_CCALLBACK = 2;
	private static final int MSG_CANCEL_NOTIFY = 3;

	private HashMap<String, Object> shareParamsMap;
	private ArrayList<CustomerLogo> customers;
	private boolean silent;
	private PlatformActionListener callback;
	private ShareContentCustomizeCallback customizeCallback;
	private boolean dialogMode = false;
	private boolean disableSSO;
	private HashMap<String, String> hiddenPlatforms;
	private View bgView;
	private OnekeyShareTheme theme;

	private Context context;
	private PlatformListFakeActivity.OnShareButtonClickListener onShareButtonClickListener;

	public OnekeyShare()
	{
		shareParamsMap = new HashMap<String, Object>();
		customers = new ArrayList<CustomerLogo>();
		callback = this;
		hiddenPlatforms = new HashMap<String, String>();
	}

	public void show(Context context)
	{
		ShareSDK.initSDK(context);
		this.context = context;

		// �򿪷���˵���ͳ��
		ShareSDK.logDemoEvent(1, null);

		// ��ʾ��ʽ����platform��silent�����ֶο��Ƶ�
		// ���platform�����ˣ���������ʾ�Ź��񣬷��򶼻���ʾ��
		// ���silentΪtrue����ʾ������༭ҳ�棬�������롣
		// ����ֻ�ж�platform����Ϊ�Ź�����ʾ�Ժ��¼�����PlatformGridView����
		// ��platform��silent��Ϊtrue����ֱ�ӽ������
		// ��platform�����ˣ�����silentΪfalse�����ж��Ƿ��ǡ�ʹ�ÿͻ��˷�����ƽ̨��
		// ��Ϊ��ʹ�ÿͻ��˷�����ƽ̨����ֱ�ӷ����������༭ҳ��
		if (shareParamsMap.containsKey("platform"))
		{
			String name = String.valueOf(shareParamsMap.get("platform"));
			Platform platform = ShareSDK.getPlatform(name);

			if (silent || ShareCore.isUseClientToShare(name) || platform instanceof CustomPlatform)
			{
				HashMap<Platform, HashMap<String, Object>> shareData = new HashMap<Platform, HashMap<String, Object>>();
				shareData.put(ShareSDK.getPlatform(name), shareParamsMap);
				share(shareData);
				return;
			}
		}

		PlatformListFakeActivity platformListFakeActivity;
		try
		{
			if (OnekeyShareTheme.SKYBLUE == theme)
			{
				platformListFakeActivity = (PlatformListFakeActivity) Class.forName(
						"cn.sharesdk.onekeyshare.theme.skyblue.PlatformListPage").newInstance();
			} else
			{
				platformListFakeActivity = (PlatformListFakeActivity) Class.forName(
						"cn.sharesdk.onekeyshare.theme.classic.PlatformListPage").newInstance();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			return;
		}

		platformListFakeActivity.setDialogMode(dialogMode);
		platformListFakeActivity.setShareParamsMap(shareParamsMap);
		platformListFakeActivity.setSilent(silent);
		platformListFakeActivity.setCustomerLogos(customers);
		platformListFakeActivity.setBackgroundView(bgView);
		platformListFakeActivity.setHiddenPlatforms(hiddenPlatforms);
		platformListFakeActivity.setOnShareButtonClickListener(onShareButtonClickListener);
		platformListFakeActivity.setThemeShareCallback(new ThemeShareCallback()
		{

			@Override
			public void doShare(HashMap<Platform, HashMap<String, Object>> shareData)
			{
				share(shareData);
			}
		});
		if (shareParamsMap.containsKey("platform"))
		{
			String name = String.valueOf(shareParamsMap.get("platform"));
			Platform platform = ShareSDK.getPlatform(name);
			platformListFakeActivity.showEditPage(context, platform);
			return;
		}
		platformListFakeActivity.show(context, null);
	}

	public void setTheme(OnekeyShareTheme theme)
	{
		this.theme = theme;
	}

	/** address�ǽ����˵�ַ��������Ϣ���ʼ�ʹ�ã�������Բ��ṩ */
	public void setAddress(String address)
	{
		shareParamsMap.put("address", address);
	}

	/**
	 * title���⣬��ӡ��ʼǡ����䡢��Ϣ��΢�ţ��������ѡ�����Ȧ���ղأ��� ���ţ��������ѡ�����Ȧ������������QQ�ռ�ʹ�ã�������Բ��ṩ
	 */
	public void setTitle(String title)
	{
		shareParamsMap.put("title", title);
	}

	/** titleUrl�Ǳ�����������ӣ�������������QQ�ռ�ʹ�ã�������Բ��ṩ */
	public void setTitleUrl(String titleUrl)
	{
		shareParamsMap.put("titleUrl", titleUrl);
	}

	/** text�Ƿ����ı�������ƽ̨����Ҫ����ֶ� */
	public void setText(String text)
	{
		shareParamsMap.put("text", text);
	}

	/** ��ȡtext�ֶε�ֵ */
	public String getText()
	{
		return shareParamsMap.containsKey("text") ? String.valueOf(shareParamsMap.get("text")) : null;
	}

	/** imagePath�Ǳ��ص�ͼƬ·������Linked-In�������ƽ̨��֧������ֶ� */
	public void setImagePath(String imagePath)
	{
		if (!TextUtils.isEmpty(imagePath))
			shareParamsMap.put("imagePath", imagePath);
	}

	/** imageUrl��ͼƬ������·��������΢������������QQ�ռ��Linked-In֧�ִ��ֶ� */
	public void setImageUrl(String imageUrl)
	{
		if (!TextUtils.isEmpty(imageUrl))
			shareParamsMap.put("imageUrl", imageUrl);
	}

	/** url��΢�ţ��������ѡ�����Ȧ�ղأ������ţ��������Ѻ�����Ȧ����ʹ�ã�������Բ��ṩ */
	public void setUrl(String url)
	{
		shareParamsMap.put("url", url);
	}

	/** filePath�Ǵ�����Ӧ�ó���ı���·��������΢�ţ����ţ����Ѻ�Dropbox��ʹ�ã�������Բ��ṩ */
	public void setFilePath(String filePath)
	{
		shareParamsMap.put("filePath", filePath);
	}

	/** comment���Ҷ�������������ۣ�������������QQ�ռ�ʹ�ã�������Բ��ṩ */
	public void setComment(String comment)
	{
		shareParamsMap.put("comment", comment);
	}

	/** site�Ƿ�������ݵ���վ���ƣ�����QQ�ռ�ʹ�ã�������Բ��ṩ */
	public void setSite(String site)
	{
		shareParamsMap.put("site", site);
	}

	/** siteUrl�Ƿ�������ݵ���վ��ַ������QQ�ռ�ʹ�ã�������Բ��ṩ */
	public void setSiteUrl(String siteUrl)
	{
		shareParamsMap.put("siteUrl", siteUrl);
	}

	/** foursquare����ʱ�ĵط��� */
	public void setVenueName(String venueName)
	{
		shareParamsMap.put("venueName", venueName);
	}

	/** foursquare����ʱ�ĵط����� */
	public void setVenueDescription(String venueDescription)
	{
		shareParamsMap.put("venueDescription", venueDescription);
	}

	/** �����γ�ȣ�����΢������Ѷ΢����foursquare֧�ִ��ֶ� */
	public void setLatitude(float latitude)
	{
		shareParamsMap.put("latitude", latitude);
	}

	/** ����ؾ��ȣ�����΢������Ѷ΢����foursquare֧�ִ��ֶ� */
	public void setLongitude(float longitude)
	{
		shareParamsMap.put("longitude", longitude);
	}

	/** �Ƿ�ֱ�ӷ��� */
	public void setSilent(boolean silent)
	{
		this.silent = silent;
	}

	/** ���ñ༭ҳ�ĳ�ʼ��ѡ��ƽ̨ */
	public void setPlatform(String platform)
	{
		shareParamsMap.put("platform", platform);
	}

	/** ����KakaoTalk��Ӧ�����ص�ַ */
	public void setInstallUrl(String installurl)
	{
		shareParamsMap.put("installurl", installurl);
	}

	/** ����KakaoTalk��Ӧ�ô򿪵�ַ */
	public void setExecuteUrl(String executeurl)
	{
		shareParamsMap.put("executeurl", executeurl);
	}

	/** ����΢�ŷ�������ֵĵ�ַ */
	public void setMusicUrl(String musicUrl)
	{
		shareParamsMap.put("musicUrl", musicUrl);
	}

	/** �����Զ�����ⲿ�ص� */
	public void setCallback(PlatformActionListener callback)
	{
		this.callback = callback;
	}

	/** ���ز����ص� */
	public PlatformActionListener getCallback()
	{
		return callback;
	}

	/** �������ڷ�������У����ݲ�ͬƽ̨�Զ���������ݵĻص� */
	public void setShareContentCustomizeCallback(ShareContentCustomizeCallback callback)
	{
		customizeCallback = callback;
	}

	/** �����Զ���������ݵĻص� */
	public ShareContentCustomizeCallback getShareContentCustomizeCallback()
	{
		return customizeCallback;
	}

	/** �����Լ�ͼ��͵���¼��������ظ�������Ӷ�� */
	public void setCustomerLogo(Bitmap enableLogo, Bitmap disableLogo, String label, OnClickListener ocListener)
	{
		CustomerLogo cl = new CustomerLogo();
		cl.label = label;
		cl.enableLogo = enableLogo;
		cl.disableLogo = disableLogo;
		cl.listener = ocListener;
		customers.add(cl);
	}

	/** ����һ���ܿ��أ������ڷ���ǰ����Ҫ��Ȩ�������sso���� */
	public void disableSSOWhenAuthorize()
	{
		disableSSO = true;
	}

	/** ���ñ༭ҳ�����ʾģʽΪDialogģʽ */
	public void setDialogMode()
	{
		dialogMode = true;
		shareParamsMap.put("dialogMode", dialogMode);
	}

	/** ���һ�����ص�platform */
	public void addHiddenPlatform(String platform)
	{
		hiddenPlatforms.put(platform, platform);
	}

	/** ����һ��������ͼ�����View , surfaceView�ǽز���ͼƬ�� */
	public void setViewToShare(View viewToShare)
	{
		try
		{
			Bitmap bm = captureView(viewToShare, viewToShare.getWidth(), viewToShare.getHeight());
			shareParamsMap.put("viewToShare", bm);
		} catch (Throwable e)
		{
			e.printStackTrace();
		}
	}

	/** ��Ѷ΢���������ͼƬ */
	public void setImageArray(String[] imageArray)
	{
		shareParamsMap.put("imageArray", imageArray);
	}

	public void setEditPageBackground(View bgView)
	{
		this.bgView = bgView;
	}

	public void setOnShareButtonClickListener(
			PlatformListFakeActivity.OnShareButtonClickListener onShareButtonClickListener)
	{
		this.onShareButtonClickListener = onShareButtonClickListener;
	}

	/** ѭ��ִ�з��� */
	public void share(HashMap<Platform, HashMap<String, Object>> shareData)
	{
		boolean started = false;
		for (Entry<Platform, HashMap<String, Object>> ent : shareData.entrySet())
		{
			Platform plat = ent.getKey();
			plat.SSOSetting(disableSSO);
			String name = plat.getName();

			boolean isGooglePlus = "GooglePlus".equals(name);
			if (isGooglePlus && !plat.isClientValid())
			{
				Message msg = new Message();
				msg.what = MSG_TOAST;
				int resId = getStringRes(context, "google_plus_client_inavailable");
				msg.obj = context.getString(resId);
				UIHandler.sendMessage(msg, this);
				continue;
			}

			boolean isAlipay = "Alipay".equals(name);
			if (isAlipay && !plat.isClientValid())
			{
				Message msg = new Message();
				msg.what = MSG_TOAST;
				int resId = getStringRes(context, "alipay_client_inavailable");
				msg.obj = context.getString(resId);
				UIHandler.sendMessage(msg, this);
				continue;
			}

			boolean isKakaoTalk = "KakaoTalk".equals(name);
			if (isKakaoTalk && !plat.isClientValid())
			{
				Message msg = new Message();
				msg.what = MSG_TOAST;
				int resId = getStringRes(context, "kakaotalk_client_inavailable");
				msg.obj = context.getString(resId);
				UIHandler.sendMessage(msg, this);
				continue;
			}

			boolean isKakaoStory = "KakaoStory".equals(name);
			if (isKakaoStory && !plat.isClientValid())
			{
				Message msg = new Message();
				msg.what = MSG_TOAST;
				int resId = getStringRes(context, "kakaostory_client_inavailable");
				msg.obj = context.getString(resId);
				UIHandler.sendMessage(msg, this);
				continue;
			}

			boolean isLine = "Line".equals(name);
			if (isLine && !plat.isClientValid())
			{
				Message msg = new Message();
				msg.what = MSG_TOAST;
				int resId = getStringRes(context, "line_client_inavailable");
				msg.obj = context.getString(resId);
				UIHandler.sendMessage(msg, this);
				continue;
			}

			boolean isWhatsApp = "WhatsApp".equals(name);
			if (isWhatsApp && !plat.isClientValid())
			{
				Message msg = new Message();
				msg.what = MSG_TOAST;
				int resId = getStringRes(context, "whatsapp_client_inavailable");
				msg.obj = context.getString(resId);
				UIHandler.sendMessage(msg, this);
				continue;
			}

			boolean isPinterest = "Pinterest".equals(name);
			if (isPinterest && !plat.isClientValid())
			{
				Message msg = new Message();
				msg.what = MSG_TOAST;
				int resId = getStringRes(context, "pinterest_client_inavailable");
				msg.obj = context.getString(resId);
				UIHandler.sendMessage(msg, this);
				continue;
			}

			if ("Instagram".equals(name) && !plat.isClientValid())
			{
				Message msg = new Message();
				msg.what = MSG_TOAST;
				int resId = getStringRes(context, "instagram_client_inavailable");
				msg.obj = context.getString(resId);
				UIHandler.sendMessage(msg, this);
				continue;
			}

			boolean isLaiwang = "Laiwang".equals(name);
			boolean isLaiwangMoments = "LaiwangMoments".equals(name);
			if (isLaiwang || isLaiwangMoments)
			{
				if (!plat.isClientValid())
				{
					Message msg = new Message();
					msg.what = MSG_TOAST;
					int resId = getStringRes(context, "laiwang_client_inavailable");
					msg.obj = context.getString(resId);
					UIHandler.sendMessage(msg, this);
					continue;
				}
			}

			boolean isYixin = "YixinMoments".equals(name) || "Yixin".equals(name);
			if (isYixin && !plat.isClientValid())
			{
				Message msg = new Message();
				msg.what = MSG_TOAST;
				int resId = getStringRes(context, "yixin_client_inavailable");
				msg.obj = context.getString(resId);
				UIHandler.sendMessage(msg, this);
				continue;
			}

			HashMap<String, Object> data = ent.getValue();
			int shareType = Platform.SHARE_TEXT;
			String imagePath = String.valueOf(data.get("imagePath"));
			if (imagePath != null && (new File(imagePath)).exists())
			{
				shareType = Platform.SHARE_IMAGE;
				if (imagePath.endsWith(".gif"))
				{
					shareType = Platform.SHARE_EMOJI;
				} else if (data.containsKey("url") && !TextUtils.isEmpty(data.get("url").toString()))
				{
					shareType = Platform.SHARE_WEBPAGE;
					if (data.containsKey("musicUrl") && !TextUtils.isEmpty(data.get("musicUrl").toString()))
					{
						shareType = Platform.SHARE_MUSIC;
					}
				}
			} else
			{
				Bitmap viewToShare = (Bitmap) data.get("viewToShare");
				if (viewToShare != null && !viewToShare.isRecycled())
				{
					shareType = Platform.SHARE_IMAGE;
					if (data.containsKey("url") && !TextUtils.isEmpty(data.get("url").toString()))
					{
						shareType = Platform.SHARE_WEBPAGE;
						if (data.containsKey("musicUrl") && !TextUtils.isEmpty(data.get("musicUrl").toString()))
						{
							shareType = Platform.SHARE_MUSIC;
						}
					}
				} else
				{
					Object imageUrl = data.get("imageUrl");
					if (imageUrl != null && !TextUtils.isEmpty(String.valueOf(imageUrl)))
					{
						shareType = Platform.SHARE_IMAGE;
						if (String.valueOf(imageUrl).endsWith(".gif"))
						{
							shareType = Platform.SHARE_EMOJI;
						} else if (data.containsKey("url") && !TextUtils.isEmpty(data.get("url").toString()))
						{
							shareType = Platform.SHARE_WEBPAGE;
							if (data.containsKey("musicUrl") && !TextUtils.isEmpty(data.get("musicUrl").toString()))
							{
								shareType = Platform.SHARE_MUSIC;
							}
						}
					}
				}
			}
			data.put("shareType", shareType);

			if (!started)
			{
				started = true;
				// if (this == callback) {
				int resId = getStringRes(context, "sharing");
				if (resId > 0)
				{
					showNotification(context.getString(resId));
				}
				// }
			}
			plat.setPlatformActionListener(callback);
			ShareCore shareCore = new ShareCore();
			shareCore.setShareContentCustomizeCallback(customizeCallback);
			shareCore.share(plat, data);
		}
	}

	public void onComplete(Platform platform, int action, HashMap<String, Object> res)
	{
		Message msg = new Message();
		msg.what = MSG_ACTION_CCALLBACK;
		msg.arg1 = 1;
		msg.arg2 = action;
		msg.obj = platform;
		UIHandler.sendMessage(msg, this);
	}

	public void onError(Platform platform, int action, Throwable t)
	{
		t.printStackTrace();

		Message msg = new Message();
		msg.what = MSG_ACTION_CCALLBACK;
		msg.arg1 = 2;
		msg.arg2 = action;
		msg.obj = t;
		UIHandler.sendMessage(msg, this);

		// ����ʧ�ܵ�ͳ��
		ShareSDK.logDemoEvent(4, platform);
	}

	public void onCancel(Platform platform, int action)
	{
		Message msg = new Message();
		msg.what = MSG_ACTION_CCALLBACK;
		msg.arg1 = 3;
		msg.arg2 = action;
		msg.obj = platform;
		UIHandler.sendMessage(msg, this);

		// ����ʧ�ܵ�ͳ��
		ShareSDK.logDemoEvent(5, platform);
	}

	public boolean handleMessage(Message msg)
	{
		switch (msg.what)
		{
		case MSG_TOAST:
		{
			String text = String.valueOf(msg.obj);
			Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
		}
			break;
		case MSG_ACTION_CCALLBACK:
		{
			switch (msg.arg1)
			{
			case 1:
			{
				// �ɹ�
				int resId = getStringRes(context, "share_completed");
				if (resId > 0)
				{
					showNotification(context.getString(resId));
				}
			}
				break;
			case 2:
			{
				// ʧ��
				String expName = msg.obj.getClass().getSimpleName();
				if ("WechatClientNotExistException".equals(expName)
						|| "WechatTimelineNotSupportedException".equals(expName)
						|| "WechatFavoriteNotSupportedException".equals(expName))
				{
					int resId = getStringRes(context, "wechat_client_inavailable");
					if (resId > 0)
					{
						showNotification(context.getString(resId));
					}
				} else if ("GooglePlusClientNotExistException".equals(expName))
				{
					int resId = getStringRes(context, "google_plus_client_inavailable");
					if (resId > 0)
					{
						showNotification(context.getString(resId));
					}
				} else if ("QQClientNotExistException".equals(expName))
				{
					int resId = getStringRes(context, "qq_client_inavailable");
					if (resId > 0)
					{
						showNotification(context.getString(resId));
					}
				} else if ("YixinClientNotExistException".equals(expName)
						|| "YixinTimelineNotSupportedException".equals(expName))
				{
					int resId = getStringRes(context, "yixin_client_inavailable");
					if (resId > 0)
					{
						showNotification(context.getString(resId));
					}
				} else if ("KakaoTalkClientNotExistException".equals(expName))
				{
					int resId = getStringRes(context, "kakaotalk_client_inavailable");
					if (resId > 0)
					{
						showNotification(context.getString(resId));
					}
				} else if ("KakaoStoryClientNotExistException".equals(expName))
				{
					int resId = getStringRes(context, "kakaostory_client_inavailable");
					if (resId > 0)
					{
						showNotification(context.getString(resId));
					}
				} else if ("WhatsAppClientNotExistException".equals(expName))
				{
					int resId = getStringRes(context, "whatsapp_client_inavailable");
					if (resId > 0)
					{
						showNotification(context.getString(resId));
					}
				} else
				{
					int resId = getStringRes(context, "share_failed");
					if (resId > 0)
					{
						showNotification(context.getString(resId));
					}
				}
			}
				break;
			case 3:
			{
				// ȡ��
				int resId = getStringRes(context, "share_canceled");
				if (resId > 0)
				{
					showNotification(context.getString(resId));
				}
			}
				break;
			}
		}
			break;
		case MSG_CANCEL_NOTIFY:
		{
			NotificationManager nm = (NotificationManager) msg.obj;
			if (nm != null)
			{
				nm.cancel(msg.arg1);
			}
		}
			break;
		}
		return false;
	}

	// ��״̬����ʾ�������
	private void showNotification(String text)
	{
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	/** �Ƿ�֧��QQ,QZone��Ȩ��¼��΢�� */
	public void setShareFromQQAuthSupport(boolean shareFromQQLogin)
	{
		shareParamsMap.put("isShareTencentWeibo", shareFromQQLogin);
	}
}
