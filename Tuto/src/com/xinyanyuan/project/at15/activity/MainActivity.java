package com.xinyanyuan.project.at15.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;

import com.bm.photoview.libraries.Info;
import com.bm.photoview.libraries.PhotoView;
import com.xinyanyuan.project.at15.App;
import com.xinyanyuan.project.at15.R;
import com.xinyanyuan.project.at15.activity.anim.ActivityAmin;
import com.xinyanyuan.project.at15.fragment.HomeFragment;
import com.xinyanyuan.project.at15.fragment.InfoFragment;
import com.xinyanyuan.project.at15.fragment.MeFragment;
import com.xinyanyuan.project.at15.fragment.NearbyFragment;

public class MainActivity extends BaseActivity implements OnClickListener {
	private static final int TAB_NUM = 4;// 选项卡的数量
	private TextView tv_me, tv_message, tv_nearby, tv_home, tv_mainTitle;
	private ImageView iv_me, iv_message, iv_nearby, iv_home;
	private Fragment[] fragments = new Fragment[TAB_NUM];// 选项卡的碎片集
	// private DragLayout dl_main;
	public static boolean isForeground = false;
	private PhotoView pv_showPhoto = null;

	@Override
	protected void onStart() {
		super.onStart();
		App.getApp().addActivity(this);
	}

	public interface OnBaseBack {
		void OnBack();
	}

	private OnBaseBack onBaseBack = null;

	public void setOnBaseBack(OnBaseBack onBaseBack) {
		this.onBaseBack = onBaseBack;
	}

	@Override
	public void onBackPressed() {
		if (onBaseBack != null) {
			onBaseBack.OnBack();
		} else
			super.onBackPressed();
	}

	@Override
	protected void onResume() {
		super.onResume();
		isForeground = true;
		JPushInterface.onResume(App.getApp());
		selectTab(lastIndex == -1 ? 0 : lastIndex);

	}

	@Override
	protected void onPause() {
		super.onPause();
		JPushInterface.onPause(App.getApp());
		isForeground = false;
	}

	@Override
	protected int getLayoutResID() {
		return R.layout.activity_main;
	}

	@Override
	protected String getActivityTitle() {

		return null;
	}

	@Override
	protected void initView() {
		tv_mainTitle = (TextView) findViewById(R.id.tv_mainTitle);
		tv_me = (TextView) findViewById(R.id.tv_me);
		tv_message = (TextView) findViewById(R.id.tv_message);
		tv_nearby = (TextView) findViewById(R.id.tv_nearby);
		tv_home = (TextView) findViewById(R.id.tv_home);
		iv_me = (ImageView) findViewById(R.id.iv_me);
		iv_message = (ImageView) findViewById(R.id.iv_message);
		iv_nearby = (ImageView) findViewById(R.id.iv_nearby);
		iv_home = (ImageView) findViewById(R.id.iv_home);
		findViewById(R.id.ll_me).setOnClickListener(this);
		findViewById(R.id.ll_message).setOnClickListener(this);
		findViewById(R.id.ll_nearby).setOnClickListener(this);
		findViewById(R.id.ll_home).setOnClickListener(this);
		// dl_main = (DragLayout) findViewById(R.id.dl_main);
		// dl_main.setDragListener(dragListener);
		/**************************************/
		pv_showPhoto = (PhotoView) findViewById(R.id.pv_showPhoto);
		// 需要启动缩放需要手动开启
		pv_showPhoto.enable();
		pv_showPhoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 让img2从自身位置变换到原来img1图片的位置大小
				pv_showPhoto.animaTo(lastPhotoViewInfo, new Runnable() {

					@Override
					public void run() {
						pv_showPhoto.setVisibility(View.GONE);

					}
				});
			}
		});

	}

	public PhotoView getPv_showPhoto() {
		return pv_showPhoto;
	}

	private Info lastPhotoViewInfo = null;

	public void setLastPhotoViewInfo(Info lastPhotoViewInfo) {
		this.lastPhotoViewInfo = lastPhotoViewInfo;
	}

	// private DragListener dragListener = new DragListener() {
	//
	// @Override
	// public void onOpen() {
	// }
	//
	// @Override
	// public void onDrag(float percent) {
	// }
	//
	// @Override
	// public void onClose() {
	// }
	// };

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_home:
			selectTab(0);
		case R.id.ll_nearby:
			selectTab(1);
			break;
		case R.id.ll_message:
			selectTab(2);
			break;
		case R.id.ll_me:
			selectTab(3);
			break;

		}

	}

	private void selectTab(int position) {
		switch (position) {
		case 0:
			changeState(true, false, false, false);
			break;
		case 1:
			changeState(false, true, false, false);
			break;
		case 2:
			changeState(false, false, true, false);
			break;
		case 3:
			changeState(false, false, false, true);
			break;

		}
		showFragment(position);
	}

	private void changeState(boolean tab1, boolean tab2, boolean tab3,
			boolean tab4) {
		iv_home.setSelected(tab1);
		tv_home.setSelected(tab1);
		iv_nearby.setSelected(tab2);
		tv_nearby.setSelected(tab2);
		iv_message.setSelected(tab3);
		tv_message.setSelected(tab3);
		iv_me.setSelected(tab4);
		tv_me.setSelected(tab4);
	}

	private int lastIndex = -1;// 上一个选项卡的位置

	/** 显示碎片 **/
	private void showFragment(int index) {
		if (index == lastIndex) {
			return;
		}
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (lastIndex != -1) {
			ft.detach(fragments[lastIndex]);
		}
		if (fragments[index] == null) {
			fragments[index] = createFragment(index);
			ft.add(R.id.fl_mainContent, fragments[index]);
		} else {
			ft.attach(fragments[index]);
		}
		ft.commitAllowingStateLoss();
		lastIndex = index;
	}

	private Fragment createFragment(int index) {
		switch (index) {
		case 0:
			return new HomeFragment();
		case 1:
			return new NearbyFragment();
		case 2:
			return new InfoFragment();
		case 3:
			return new MeFragment();
		}
		return null;
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		ActivityAmin.startActivityAmin(this);
	}

}
