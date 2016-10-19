package com.xinyanyuan.project.at15.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment {
	private View layout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		layout = inflater.inflate(getLayoutResID(), null);
		initView();
		return layout;
	}

	protected View findViewById(int id) {
		return layout.findViewById(id);
	}

	protected abstract int getLayoutResID();

	protected abstract void initView();
}
