package com.xinyanyuan.project.at15.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bigkoo.convenientbanner.CBPageAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

public class NetworkImageHolderView implements CBPageAdapter.Holder<String> {

	private ImageView imageView;

	@Override
	public View createView(Context context) {
		// �����ͨ��layout�ļ���������Ҳ��������һ���ô��봴������һ����Image���κοؼ������Խ��з�ҳ
		imageView = new ImageView(context);
		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		return imageView;
	}

	@Override
	public void UpdateUI(final Context context, final int position, String data) {
		ImageLoader.getInstance().displayImage(data, imageView);
		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast.makeText(context, "" + position, Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

}
