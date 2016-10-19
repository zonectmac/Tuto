package com.xinyanyuan.project.at15.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.AlertView.Style;
import com.bigkoo.alertview.OnItemClickListener;
import com.loopj.android.http.RequestParams;
import com.xinyanyuan.project.at15.App;
import com.xinyanyuan.project.at15.R;
import com.xinyanyuan.project.at15.activity.MainActivity;
import com.xinyanyuan.project.at15.activity.MainActivity.OnBaseBack;
import com.xinyanyuan.project.at15.activity.SettingActivity;
import com.xinyanyuan.project.at15.activity.UserInfoActivity;
import com.xinyanyuan.project.at15.model.User;
import com.xinyanyuan.project.at15.utils.ConnUtils;
import com.xinyanyuan.project.at15.utils.ConnUtils.OnHttpCallBack;
import com.xinyanyuan.project.at15.utils.ImageLoadHelper;
import com.xinyanyuan.project.at15.utils.SysooLin;
import com.xinyanyuan.project.at15.utils.Tools;
import com.xinyanyuan.project.at15.view.CircleImageView;

public class MeFragment extends BaseFragment implements OnClickListener {

	private CircleImageView civ_cion;
	private ImageView iv_background;
	private TextView tv_nickName_me, tv_sexAndage, tv_constellation,
			tv_personExplain;
	private AlertView alertView;
	private MainActivity activity;
	private ImageView iv_setting_me;

	@Override
	protected int getLayoutResID() {

		return R.layout.activity_me;
	}

	@Override
	protected void initView() {
		activity = (MainActivity) getActivity();
		activity.setOnBaseBack(new OnBaseBack() {

			@Override
			public void OnBack() {
				if (alertView != null && alertView.isShowing()) {
					alertView.dismiss();
				} else {
					activity.finish();
				}
			}
		});
		findViewById(R.id.tv_moreInfo).setOnClickListener(this);
		civ_cion = (CircleImageView) findViewById(R.id.civ_cion);
		civ_cion.setOnClickListener(this);
		iv_background = (ImageView) findViewById(R.id.iv_background);
		iv_background.setOnClickListener(this);
		tv_nickName_me = (TextView) findViewById(R.id.tv_nickName_me);
		tv_sexAndage = (TextView) findViewById(R.id.tv_sexAndage);
		tv_constellation = (TextView) findViewById(R.id.tv_constellation);
		tv_personExplain = (TextView) findViewById(R.id.tv_personExplain);
		iv_setting_me = (ImageView) findViewById(R.id.iv_setting_me);
		iv_setting_me.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		changeInfoUI();
	}

	private void changeInfoUI() {
		User user = App.getApp().getUser();
		tv_nickName_me.setText(user.getNickName());
		tv_sexAndage.setText(user.getSex() + " " + user.getAge());
		tv_constellation.setText(user.getConstellation());
		tv_personExplain.setText(user.getPersonExplain());
		ImageLoadHelper.displayImage2Url(user.getIcon(), civ_cion);
		ImageLoadHelper.displayImage2Url(user.getBackground(), iv_background);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_moreInfo:
			getActivity().startActivity(
					new Intent(getActivity(), UserInfoActivity.class));
			break;
		case R.id.civ_cion:
			getPic();
			isIconImage = true;
			break;
		case R.id.iv_background:
			getPic();
			isIconImage = false;
			break;
		case R.id.iv_setting_me:
			getActivity().startActivity(
					new Intent(getActivity(), SettingActivity.class));
			break;
		}
	}

	private void getPic() {
		alertView = new AlertView("请选择", null, "取消", null, new String[] {
				"从相册中选择", "拍照" }, getActivity(), Style.ActionSheet,
				new OnItemClickListener() {

					@Override
					public void onItemClick(Object o, int position) {
						if (position == 0) {// 相册
							getFromPictureHome();
						} else if (position == 1) {// 拍照
							startCamera();
						}

					}
				});
		alertView.show();
	}

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
					postUserIcon(file);// 上传到服务器
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				break;
			}
		} else {
			System.out.println("-----取消拍照");
		}
	}

	private void postUserIcon(File fileIcon) {
		Tools.showProgressDialog(getActivity(), "正在提交...");
		RequestParams params = new RequestParams();
		params.put("userId", App.getApp().getUser().getUserId());
		try {
			params.put("icon", fileIcon);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		ConnUtils.post(isIconImage ? ConnUtils.URL_UPDATEICON
				: ConnUtils.URL_UPDATE_BACKGROUND, params,
				new OnHttpCallBack() {

					@Override
					public void onOk(JSONObject json) {
						if (!Tools.isPdShow())
							return;
						int code = json.optInt("code");
						if (code == 1) {
							String imageUrl = json.optJSONArray("list")
									.optString(0);
							SysooLin.i(imageUrl);
							if (isIconImage) {
								App.getApp().getUser().setIcon(imageUrl);
								SysooLin.i(App.getApp().getUser().getIcon());
								ImageLoadHelper.getInstance().displayImage(
										imageUrl, civ_cion);
							} else {
								App.getApp().getUser().setBackground(imageUrl);
								ImageLoadHelper.getInstance().displayImage(
										imageUrl, iv_background);
							}
						}
						Toast.makeText(getActivity(),
								json.optString("message"), Toast.LENGTH_SHORT)
								.show();
						Tools.dismissPd();
					}

					@Override
					public void onFail(int code, String err) {
						Toast.makeText(getActivity(), "图片上传失败 " + err,
								Toast.LENGTH_SHORT).show();
					}
				});
	}
}
