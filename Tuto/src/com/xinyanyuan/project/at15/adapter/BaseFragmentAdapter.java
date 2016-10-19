package com.xinyanyuan.project.at15.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class BaseFragmentAdapter extends FragmentPagerAdapter {
	private Fragment[] mFragment = null;

	public BaseFragmentAdapter(FragmentManager fm, Fragment[] mFragment) {
		super(fm);
		this.mFragment = mFragment;
	}

	@Override
	public Fragment getItem(int position) {

		return mFragment[position];
	}

	@Override
	public int getCount() {

		return mFragment == null ? 0 : mFragment.length;
	}

}
