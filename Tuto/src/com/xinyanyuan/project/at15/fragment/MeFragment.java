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
		alertView = new AlertView("��ѡ��", null, "ȡ��", null, new String[] {
				"�������ѡ��", "����" }, getActivity(), Style.ActionSheet,
				new OnItemClickListener() {

					@Override
					public void onItemClick(Object o, int position) {
						if (position == 0) {// ���
							getFromPictureHome();
						} else if (position == 1) {// ����
							startCamera();
						}

					}
				});
		alertView.show();
	}

	private static final int TACK_PICTURE = 0x001;// �������������

	private static final int JIANJI = 0x002;// �򿪼�����������

	private boolean isIconImage;// �Ƿ���Ҫ��һ��һ�ü���һ��ͷ����һ��һ
	private Uri imageUri = null;// �ü�֮���·��
	private Uri picUri;

	/**
	 * �����
	 */
	private void startCamera() {
		picUri = Uri.parse("file://"
				+ Environment.getExternalStorageDirectory().getPath()
				+ "/paizhao.jpg");
		System.out.println("----------" + picUri.toString());
		// ����ϵͳ�����չ���
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);// ������ɺ�ͼƬ�����λ��
		intent.putExtra("noFaceDetection", false);// �Ƿ������
		startActivityForResult(intent, TACK_PICTURE);
	}

	/**
	 * �����
	 */
	private void getFromPictureHome() {
		imageUri = Uri.parse("file://"
				+ Environment.getExternalStorageDirectory().getPath() + "/"
				+ System.currentTimeMillis() + ".jpg");
		System.out.println("----------" + imageUri.toString());

		Intent intent = new Intent(Intent.ACTION_PICK, null);
		// ���������ô���new������ʵ����ǩ��TYPE_REGIST�Ļ��Ͱ�1��1������
		if (isIconImage) {
			// aspectX aspectY �ǿ�ߵı���
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			// outputX outputY �ǲü�ͼƬ���
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
		if (resultCode == Activity.RESULT_OK) {// ���������ճɹ������صĽ����RESULT_OK
			switch (requestCode) {
			case TACK_PICTURE://
				imageUri = Uri.parse("file://"
						+ Environment.getExternalStorageDirectory().getPath()
						+ "/" + System.currentTimeMillis() + ".jpg");
				System.out.println("----------" + imageUri.toString());
				// ������֮���ͼ����вü������������ü���Activity
				Intent intent = new Intent("com.android.camera.action.CROP");
				if (isIconImage) {
					// aspectX aspectY �ǿ�ߵı���
					intent.putExtra("aspectX", 1);
					intent.putExtra("aspectY", 1);
					// outputX outputY �ǲü�ͼƬ���
					intent.putExtra("outputX", 100);
					intent.putExtra("outputY", 100);
				}
				intent.setDataAndType(picUri, "image/*");// ������Ҫ�ü���ͼƬUri
				intent.putExtra("crop", "true");
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);// ͼƬ���������λ��Uri
				intent.putExtra("outputFormat", "JPEG");// ͼƬ�������ʽ
				intent.putExtra("return-data", false);
				// ������ɺ󡣵������Լ���Result�ķ�������requestCode==CHOOSE_BIG_PICTURE
				startActivityForResult(intent, JIANJI);
				break;
			case JIANJI:// �����ɹ�
				try {
					File file = new File(new URI(imageUri.toString()));
					System.out.println("----file-" + file.getPath());
					postUserIcon(file);// �ϴ���������
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				break;
			}
		} else {
			System.out.println("-----ȡ������");
		}
	}

	private void postUserIcon(File fileIcon) {
		Tools.showProgressDialog(getActivity(), "�����ύ...");
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
						Toast.makeText(getActivity(), "ͼƬ�ϴ�ʧ�� " + err,
								Toast.LENGTH_SHORT).show();
					}
				});
	}
}
