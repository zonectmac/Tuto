package com.xinyanyuan.project.at15.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.xinyanyuan.project.at15.R;
import com.xinyanyuan.project.at15.adapter.BaseFragmentAdapter;
import com.xinyanyuan.project.at15.fragment.GuiDance1Fragment;
import com.xinyanyuan.project.at15.fragment.GuiDance2Fragment;
import com.xinyanyuan.project.at15.fragment.GuiDance3Fragment;

public class GuidanceActivity extends BaseActivity {

	private RadioGroup radioGroup1;
	private ViewPager vp;

	@Override
	protected int getLayoutResID() {
		// TODO Auto-generated method stub
		return R.layout.activity_guidance;
	}

	@Override
	protected String getActivityTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void initView() {
		vp = (ViewPager) findViewById(R.id.vp);
		vp.setAdapter(new BaseFragmentAdapter(getSupportFragmentManager(),
				new Fragment[] { new GuiDance1Fragment(),
						new GuiDance2Fragment(), new GuiDance3Fragment() }));
		vp.setOnPageChangeListener(listener);
		radioGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
	}

	private OnPageChangeListener listener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
			RadioButton childAt = (RadioButton) radioGroup1.getChildAt(arg0);
			childAt.setChecked(true);

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}
	};

}
