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
	// 获取到缓存的目录地址
	private static File cacheDir = null;

	public static void init(Context context) {
		if (mImageLoader == null) {
			cacheDir = StorageUtils.getOwnCacheDirectory(context,
					"ImageLoader/Cache");
			mImageLoader = ImageLoader.getInstance();
			Builder builder = new ImageLoaderConfiguration.Builder(context);
			// 缓存文件的最大长宽
			builder.memoryCacheExtraOptions(480, 800);
			// 线程池内加载的数量
			builder.threadPoolSize(3);
			// 线程优先级
			builder.threadPriority(Thread.NORM_PRIORITY - 2);
			builder.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024));
			builder.memoryCacheSize(2 * 1024 * 1024);
			// 硬盘缓存50MB
			builder.diskCacheSize(50 * 1024 * 1024);
			builder.diskCacheFileNameGenerator(new HashCodeFileNameGenerator());
			// 将保存的时候的URI名称用MD5
			builder.tasksProcessingOrder(QueueProcessingType.LIFO);
			builder.diskCacheFileCount(100);// 缓存的File数量
			builder.diskCache(new UnlimitedDiskCache(cacheDir));// 自定义缓存路径
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
	 * 获取默认的渐显效果
	 * 
	 * @return
	 */
	public static DisplayImageOptions getDefaultFadeOptions() {
		return getFadeOptions(R.drawable.loading, R.drawable.err,
				R.drawable.urlerr);
	}

	/**
	 * 获取渐现显示选项
	 * 
	 * @param loadingImageResId
	 *            加载期间显示的图片
	 * @param errorImageResid
	 *            加载错误时显示的图片
	 * @param emptyImageResId
	 *            空图片或者解析图片出错时显示的图片
	 * @return
	 */
	public static DisplayImageOptions getFadeOptions(int loadingImageResId,
			int errorImageResid, int emptyImageResId) {
		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
		// 设置图片在下载期间显示的图片
		builder.showImageOnLoading(loadingImageResId);
		// 设置图片加载/解码过程中错误时候显示的图片
		builder.showImageOnFail(errorImageResid);
		// 设置图片Uri为空或是错误的时候显示的图片
		builder.showImageForEmptyUri(emptyImageResId);
		// 设置下载的图片是否缓存在内存中
		builder.cacheInMemory(true);
		// 设置下载的图片是否缓存在SD卡中
		builder.cacheOnDisk(true);

		/**
		 * 设置图片缩放方式： EXACTLY :图像将完全按比例缩小到目标大小 EXACTLY_STRETCHED:图片会缩放到目标大小完全
		 * IN_SAMPLE_INT:图像将被二次采样的整数倍
		 * IN_SAMPLE_POWER_OF_2:图片将降低2倍，直到下一减少步骤，使图像更小的目标大小 NONE:图片不会调整
		 ***/
		builder.imageScaleType(ImageScaleType.EXACTLY_STRETCHED);
		// 设置图片的解码类型
		builder.bitmapConfig(Bitmap.Config.ARGB_4444);
		// 设置图片下载前的延迟
		builder.delayBeforeLoading(100);
		/**
		 * 图片显示方式： RoundedBitmapDisplayer（int roundPixels）设置圆角图片
		 * FakeBitmapDisplayer（）这个类什么都没做 FadeInBitmapDisplayer（int
		 * durationMillis）设置图片渐显的时间 　　　　 *　SimpleBitmapDisplayer()正常显示一张图片
		 **/
		builder.displayer(new FadeInBitmapDisplayer(2000));// 渐显--设置图片渐显的时间
		return builder.build();
	}

	private static ImageLoader mImageLoader = null;

	public static ImageLoader getInstance() {

		return mImageLoader;
	}

	/**
	 * 清除缓存
	 */
	public static void clearCache() {
		mImageLoader.clearMemoryCache();
		mImageLoader.clearDiskCache();
	}
}
