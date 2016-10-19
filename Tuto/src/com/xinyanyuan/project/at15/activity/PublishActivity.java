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

		return "д˵˵";
	}

	@Override
	protected void initView() {
		et_publishContent = (EditText) findViewById(R.id.et_publishContent);
		setMenuText = setMenuText("����");
		setMenuText.setOnClickListener(this);
		gv_speekImage = (GridViewLin) findViewById(R.id.gv_speekImage);
		gv_speekImage.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == gv_speekImage.getCount() - 1) {
					// ��������һ��ͼƬ��ȥ���
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
		Tools.showProgressDialog(base, "���ڷ���");
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
						showToast(code + "�������ӳ�ʱ..." + err);

					}
				});
	}

	protected void showPop() {
		alertView = new AlertView("��ѡ��", null, "ȡ��", null, new String[] {
				"�������ѡ��", "����" }, base, Style.ActionSheet,
				new com.bigkoo.alertview.OnItemClickListener() {

					@Override
					public void onItemClick(Object o, int position) {
						if (position == 0) {
							// ���
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
		// ���������alertviewʱ�밴���ؼ�ʱ��dismiss��alertview����ֹ�����ؼ�ֱ���˳�����
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
			return super.getCount() + 1;// �տ�ʼ������ʱ��getcountΪ0��Ҫ��õ����һ�������һ����ʱΪ���Ĳ��У��������һ
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View
						.inflate(base, R.layout.view_image_item, null);
				imageViewSpeek = (ImageView) convertView
						.findViewById(R.id.iv_photo);
				int widthPixes = base.getResources().getDisplayMetrics().widthPixels;// �õ���Ļ�Ŀ��
				int gvWidth = widthPixes - BitmapLinUtils.dp2px(base, 29);// �õ���girdview�Ŀ��
				int imageViewWidth = gvWidth / 4;// �����ÿ��ͼƬӦ�еĿ��
				LayoutParams layoutParams = (LayoutParams) imageViewSpeek
						.getLayoutParams();// ͼƬ�Ĳ���
				layoutParams.width = imageViewWidth;
				layoutParams.height = imageViewWidth;
				imageViewSpeek.setLayoutParams(layoutParams);
				convertView.setTag(imageViewSpeek);
			} else {
				imageViewSpeek = (ImageView) convertView.getTag();
			}
			if (position == getCount() - 1) {
				// ���һ��
				imageViewSpeek.setImageResource(R.drawable.image_add);
			} else {
				imageViewSpeek.setImageBitmap(BitmapLinUtils.getImage(
						getItem(position).getPath(), imageViewSpeek));
			}
			return convertView;
		}
	}

	/****************************************************************************************/
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
					adapter.addItem(file);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				break;
			}
		} else {
			System.out.println("-----ȡ������");
		}
	}
}
