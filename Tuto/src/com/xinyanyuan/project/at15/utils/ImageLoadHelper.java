package com.xinyanyuan.project.at15.utils;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration.Builder;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.xinyanyuan.project.at15.R;

public class ImageLoadHelper {
	// ��ȡ�������Ŀ¼��ַ
	private static File cacheDir = null;

	public static void init(Context context) {
		if (mImageLoader == null) {
			cacheDir = StorageUtils.getOwnCacheDirectory(context,
					"ImageLoader/Cache");
			mImageLoader = ImageLoader.getInstance();
			Builder builder = new ImageLoaderConfiguration.Builder(context);
			// �����ļ�����󳤿�
			builder.memoryCacheExtraOptions(480, 800);
			// �̳߳��ڼ��ص�����
			builder.threadPoolSize(3);
			// �߳����ȼ�
			builder.threadPriority(Thread.NORM_PRIORITY - 2);
			builder.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024));
			builder.memoryCacheSize(2 * 1024 * 1024);
			// Ӳ�̻���50MB
			builder.diskCacheSize(50 * 1024 * 1024);
			builder.diskCacheFileNameGenerator(new HashCodeFileNameGenerator());
			// �������ʱ���URI������MD5
			builder.tasksProcessingOrder(QueueProcessingType.LIFO);
			builder.diskCacheFileCount(100);// �����File����
			builder.diskCache(new UnlimitedDiskCache(cacheDir));// �Զ��建��·��
			builder.imageDownloader(new BaseImageDownloader(context, 5000,
					10000));
			ImageLoaderConfiguration build = builder.build();
			mImageLoader.init(build);
		}
	}

	public static void displayImage2Url(String uri, ImageView imageView) {
		mImageLoader.displayImage(uri, imageView, getDefaultFadeOptions());
	}

	/**
	 * ��ȡĬ�ϵĽ���Ч��
	 * 
	 * @return
	 */
	public static DisplayImageOptions getDefaultFadeOptions() {
		return getFadeOptions(R.drawable.loading, R.drawable.err,
				R.drawable.urlerr);
	}

	/**
	 * ��ȡ������ʾѡ��
	 * 
	 * @param loadingImageResId
	 *            �����ڼ���ʾ��ͼƬ
	 * @param errorImageResid
	 *            ���ش���ʱ��ʾ��ͼƬ
	 * @param emptyImageResId
	 *            ��ͼƬ���߽���ͼƬ����ʱ��ʾ��ͼƬ
	 * @return
	 */
	public static DisplayImageOptions getFadeOptions(int loadingImageResId,
			int errorImageResid, int emptyImageResId) {
		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
		// ����ͼƬ�������ڼ���ʾ��ͼƬ
		builder.showImageOnLoading(loadingImageResId);
		// ����ͼƬ����/��������д���ʱ����ʾ��ͼƬ
		builder.showImageOnFail(errorImageResid);
		// ����ͼƬUriΪ�ջ��Ǵ����ʱ����ʾ��ͼƬ
		builder.showImageForEmptyUri(emptyImageResId);
		// �������ص�ͼƬ�Ƿ񻺴����ڴ���
		builder.cacheInMemory(true);
		// �������ص�ͼƬ�Ƿ񻺴���SD����
		builder.cacheOnDisk(true);

		/**
		 * ����ͼƬ���ŷ�ʽ�� EXACTLY :ͼ����ȫ��������С��Ŀ���С EXACTLY_STRETCHED:ͼƬ�����ŵ�Ŀ���С��ȫ
		 * IN_SAMPLE_INT:ͼ�񽫱����β�����������
		 * IN_SAMPLE_POWER_OF_2:ͼƬ������2����ֱ����һ���ٲ��裬ʹͼ���С��Ŀ���С NONE:ͼƬ�������
		 ***/
		builder.imageScaleType(ImageScaleType.EXACTLY_STRETCHED);
		// ����ͼƬ�Ľ�������
		builder.bitmapConfig(Bitmap.Config.ARGB_4444);
		// ����ͼƬ����ǰ���ӳ�
		builder.delayBeforeLoading(100);
		/**
		 * ͼƬ��ʾ��ʽ�� RoundedBitmapDisplayer��int roundPixels������Բ��ͼƬ
		 * FakeBitmapDisplayer���������ʲô��û�� FadeInBitmapDisplayer��int
		 * durationMillis������ͼƬ���Ե�ʱ�� �������� *��SimpleBitmapDisplayer()������ʾһ��ͼƬ
		 **/
		builder.displayer(new FadeInBitmapDisplayer(2000));// ����--����ͼƬ���Ե�ʱ��
		return builder.build();
	}

	private static ImageLoader mImageLoader = null;

	public static ImageLoader getInstance() {

		return mImageLoader;
	}

	/**
	 * �������
	 */
	public static void clearCache() {
		mImageLoader.clearMemoryCache();
		mImageLoader.clearDiskCache();
	}
}
