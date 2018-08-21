package com.windfind.clubassistant.member;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

class EditMemberAdapter extends PagerAdapter {

	private ArrayList<ViewGroup> mItems;

	EditMemberAdapter() {
		mItems = new ArrayList<>();
	}

	public void addItem(ViewGroup item) {
		if (!mItems.contains(item)) {
			mItems.add(item);
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ViewGroup vg = mItems.get(position);
		container.addView(vg);
		return vg;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}
}
