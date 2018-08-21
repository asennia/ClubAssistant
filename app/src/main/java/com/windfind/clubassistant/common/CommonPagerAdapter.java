package com.windfind.clubassistant.common;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class CommonPagerAdapter extends FragmentPagerAdapter {

	private Context mContext;
	private ArrayList<PageFragment> mFragments = new ArrayList<>();

	public CommonPagerAdapter(Context context, FragmentManager fm) {
		super(fm);
		mContext = context;
	}

	public void addItem(PageFragment f) {
		mFragments.add(f);
	}

	@Override
	public PageFragment getItem(int position) {
		return mFragments.get(position);
	}

	@Override
	public int getCount() {
		return mFragments.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mContext.getString(getItem(position).getTitleId());
	}
}
