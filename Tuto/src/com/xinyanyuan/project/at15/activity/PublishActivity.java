package com.xinyanyuan.project.at15.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.AlertView.Style;
import com.loopj.android.http.RequestParams;
import com.xinyanyuan.project.at15.App;
import com.xinyanyuan.project.at15.R;
import com.xinyanyuan.project.at15.adapter.ListItemAdapter;
import com.xinyanyuan.project.at15.utils.BitmapLinUtils;
import com.xinyanyuan.project.at15.utils.ConnUtils;
import com.xinyanyuan.project.at15.utils.ConnUtils.OnHttpCallBack;
import com.xinyanyuan.project.at15.utils.SysooLin;
import com.xinyanyuan.project.at15.utils.Tools;
import com.xinyanyuan.project.at15.view.GridViewLin;

public class PublishActivity extends BaseActivity {

	private EditText et_publishContent;
	private TextView setMenuText;
	private GridViewLin gv_speekImage;
	private AlertView alertView;;
	private SpeekImageAdapter adapter = null;

	@Override
	protected int getLayoutResID() {

		return R.layout.activity_publish;
	}

	@Override
	protected String getActivityTitle() {

		return "写说说";
	}

	@Override
	protected void initView() {
		et_publishContent = (EditText) findViewById(R.id.et_publishContent);
		setMenuText = setMenuText("发布");
		setMenuText.setOnClickListener(this);
		gv_speekImage = (GridViewLin) findViewById(R.id.gv_speekImage);
		gv_speekImage.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == gv_speekImage.getCount() - 1) {
					// 点击了最后一张图片。去添加
					showPop();
				}

			}
		});
		adapter = new SpeekImageAdapter();
		gv_speekImage.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == setMenuText) {
			postSpeek();
		}
	}

	private void postSpeek() {
		Tools.showProgressDialog(base, "正在发布");
		RequestParams params = new RequestParams();
		params.put("userId", App.getApp().getUser().getUserId());
		params.put("content", et_publishContent.getText().toString());
		params.put("mobileType", Build.MODEL);
		try {
			List<File> list = adapter.getmList();
			for (int i = 0; i < list.size(); i++) {
				params.put("image" + i, list.get(i));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		ConnUtils.post(ConnUtils.URL_PUBLISH_SPEAK, params,
				new OnHttpCallBack() {

					@Override
					public void onOk(JSONObject json) {
						if (!Tools.isPdShow())
							return;
						int code = json.optInt("code");
						String message = json.optString("message");
						if (code == 1) {
							finish();

						}
						showToast(message);
					}

					@Override
					public void onFail(int code, String err) {
						if (!Tools.isPdShow())
							return;
						showToast(code + "网络连接超时..." + err);

					}
				});
	}

	protected void showPop() {
		alertView = new AlertView("请选择", null, "取消", null, new String[] {
				"从相册中选择", "拍照" }, base, Style.ActionSheet,
				new com.bigkoo.alertview.OnItemClickListener() {

					@Override
					public void onItemClick(Object o, int position) {
						if (position == 0) {
							// 相册
							getFromPictureHome();
						} else if (position == 1) {
							startCamera();
						}

					}
				});
		alertView.show();
	}

	@Override
	public void onBackPressed() {
		// 当调用这个alertview时想按返回键时，dismiss掉alertview，防止按返回键直接退出程序
		if (alertView != null && alertView.isShowing()) {
			alertView.dismiss();
		} else {
			super.onBackPressed();
		}
	}

	private class SpeekImageAdapter extends ListItemAdapter<File> {

		public SpeekImageAdapter() {
			super(base);
		}

		private ImageView imageViewSpeek;

		@Override
		public int getCount() {
			return super.getCount() + 1;// 刚开始进来的时候getcount为0，要想得到最后一个必须减一，这时为负的不行，所以需加一
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View
						.inflate(base, R.layout.view_image_item, null);
				imageViewSpeek = (ImageView) convertView
						.findViewById(R.id.iv_photo);
				int widthPixes = base.getResources().getDisplayMetrics().widthPixels;// 得到屏幕的宽度
				int gvWidth = widthPixes - BitmapLinUtils.dp2px(base, 29);// 得到个girdview的宽度
				int imageViewWidth = gvWidth / 4;// 计算出每张图片应有的宽度
				LayoutParams layoutParams = (LayoutParams) imageViewSpeek
						.getLayoutParams();// 图片的参数
				layoutParams.width = imageViewWidth;
				layoutParams.height = imageViewWidth;
				imageViewSpeek.setLayoutParams(layoutParams);
				convertView.setTag(imageViewSpeek);
			} else {
				imageViewSpeek = (ImageView) convertView.getTag();
			}
			if (position == getCount() - 1) {
				// 最后一个
				imageViewSpeek.setImageResource(R.drawable.image_add);
			} else {
				imageViewSpeek.setImageBitmap(BitmapLinUtils.getImage(
						getItem(position).getPath(), imageViewSpeek));
			}
			return convertView;
		}
	}

	/****************************************************************************************/
	private static final int TACK_PICTURE = 0x001;// 打开相机的请求码
	private static final int JIANJI = 0x002;// 打开剪辑的请求码

	private boolean isIconImage;// 是否是要按一比一裁剪。一般头像都是一比一
	private Uri imageUri = null;// 裁剪之后的路径
	private Uri picUri;

	/**
	 * 打开相机
	 */
	private void startCamera() {
		picUri = Uri.parse("file://"
				+ Environment.getExternalStorageDirectory().getPath()
				+ "/paizhao.jpg");
		System.out.println("----------" + picUri.toString());
		// 调用系统的拍照功能
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);// 拍照完成后图片保存的位置
		intent.putExtra("noFaceDetection", false);// 是否打开人脸
		startActivityForResult(intent, TACK_PICTURE);
	}

	/**
	 * 打开相册
	 */
	private void getFromPictureHome() {
		imageUri = Uri.parse("file://"
				+ Environment.getExternalStorageDirectory().getPath() + "/"
				+ System.currentTimeMillis() + ".jpg");
		System.out.println("----------" + imageUri.toString());

		Intent intent = new Intent(Intent.ACTION_PICK, null);
		// 如果外面调用此类new出来的实例标签是TYPE_REGIST的话就按1比1剪辑。
		if (isIconImage) {
			// aspectX aspectY 是宽高的比例
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			// outputX outputY 是裁剪图片宽高
			intent.putExtra("outputX", 80);
			intent.putExtra("outputY", 80);
		}
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		intent.putExtra("outputFormat", "JPEG");
		startActivityForResult(intent, JIANJI);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		SysooLin.i("onActivityResult onActivityResultonActivityResult");
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {// 如果相机拍照成功。返回的结果必RESULT_OK
			switch (requestCode) {
			case TACK_PICTURE://
				imageUri = Uri.parse("file://"
						+ Environment.getExternalStorageDirectory().getPath()
						+ "/" + System.currentTimeMillis() + ".jpg");
				System.out.println("----------" + imageUri.toString());
				// 将拍照之后的图像进行裁剪，这里启动裁剪的Activity
				Intent intent = new Intent("com.android.camera.action.CROP");
				if (isIconImage) {
					// aspectX aspectY 是宽高的比例
					intent.putExtra("aspectX", 1);
					intent.putExtra("aspectY", 1);
					// outputX outputY 是裁剪图片宽高
					intent.putExtra("outputX", 100);
					intent.putExtra("outputY", 100);
				}
				intent.setDataAndType(picUri, "image/*");// 关联将要裁剪的图片Uri
				intent.putExtra("crop", "true");
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);// 图片剪辑保存的位置Uri
				intent.putExtra("outputFormat", "JPEG");// 图片的输出格式
				intent.putExtra("return-data", false);
				// 拍照完成后。调用了自己的Result的方法。到requestCode==CHOOSE_BIG_PICTURE
				startActivityForResult(intent, JIANJI);
				break;
			case JIANJI:// 剪辑成功
				try {
					File file = new File(new URI(imageUri.toString()));
					System.out.println("----file-" + file.getPath());
					adapter.addItem(file);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				break;
			}
		} else {
			System.out.println("-----取消拍照");
		}
	}
}
