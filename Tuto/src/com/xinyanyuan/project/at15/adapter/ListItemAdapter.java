package com.xinyanyuan.project.at15.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.BaseAdapter;

public abstract class ListItemAdapter<T> extends BaseAdapter {
	protected List<T> mList = null;
	protected Context mContext = null;

	public ListItemAdapter(Context context) {
		mContext = context;
	}

	public List<T> getmList() {
		return mList;
	}

	public void setmList(List<T> mList) {
		this.mList = mList;
		notifyDataSetChanged();
	}

	public void addList(List<T> list) {
		if (mList == null) {
			mList = new ArrayList<T>();
		}
		mList.addAll(list);
		notifyDataSetChanged();
	}

	public void addItem(T t) {
		if (mList == null) {
			mList = new ArrayList<T>();
		}
		mList.add(t);
		notifyDataSetChanged();
	}

	public void removeItem(int position) {
		if (mList == null) {
			mList = new ArrayList<T>();
		}
		mList.remove(position);
		notifyDataSetChanged();
	}

	public void removeItem(T t) {
		if (mList == null) {
			mList = new ArrayList<T>();
		}
		mList.remove(t);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {

		return mList == null ? 0 : mList.size();
	}

	public void setItem(int position, T t) {
		mList.set(position, t);
		notifyDataSetChanged();
	}

	@Override
	public T getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

}
