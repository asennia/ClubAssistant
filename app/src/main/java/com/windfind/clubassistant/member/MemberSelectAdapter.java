package com.windfind.clubassistant.member;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.windfind.clubassistant.CAApplication;
import com.windfind.clubassistant.view.ListItemViewHolder;
import com.windfind.clubassistant.R;

import java.util.ArrayList;

class MemberSelectAdapter extends RecyclerView.Adapter<ListItemViewHolder> {

	private static final int COUNT_PER_ROW = 4;

	private Context mContext;
	private Handler mHandler;
	private LayoutInflater mLayoutInflater;
	private ArrayList<MemberBean> mMemberData;
	private ArrayList<MemberBean> mSelected;
	private ArrayList<MemberBean> mPrevSelected;
	private Drawable mNormalBg;
	private Drawable mSelectedBg;
	private int mCellWidth;
	private int mCellHeight;
	private int mCellGap;

	@SuppressWarnings("deprecation")
	MemberSelectAdapter(Context context, Handler handler) {
		mContext = context;
		mHandler = handler;
		mLayoutInflater = LayoutInflater.from(context);
		mMemberData = new ArrayList<>();
		mSelected = new ArrayList<>();
		mPrevSelected = new ArrayList<>();
		Resources res = context.getResources();
		mNormalBg = res.getDrawable(R.drawable.player_selector_normal_bg);
		mSelectedBg = res.getDrawable(R.drawable.player_selector_selected_bg);
		mCellGap = res.getDimensionPixelSize(R.dimen.member_selector_view_gap);
		int screenWidth = CAApplication.sDeviceWidth;
		int totalGap = mCellGap * (COUNT_PER_ROW + 1);
		int sideSize = (screenWidth - totalGap) / COUNT_PER_ROW;
		mCellWidth = sideSize;
		mCellHeight = sideSize * 2 / 3;
	}

	public void setData(ArrayList<MemberBean> data) {
		mMemberData.clear();
		mSelected.clear();
		mMemberData.addAll(data);

		ArrayList<Long> prevSelectedIds = new ArrayList<>();
		ArrayList<MemberBean> extraPlayer = new ArrayList<>();
		for (MemberBean bean : mPrevSelected) {
			if (bean.mId <= 0) {
				extraPlayer.add(bean);
			} else {
				prevSelectedIds.add(bean.mId);
			}
		}

		for (MemberBean bean : mMemberData) {
			if (prevSelectedIds.contains(bean.mId)) {
				mSelected.add(bean);
			}
		}

		if (extraPlayer.size() > 0) {
			for (MemberBean bean : extraPlayer) {
				mMemberData.add(bean);
				mSelected.add(bean);
			}
		}

		if (mSelected.size() > 0) {
			Message msg = new Message();
			msg.what = MemberSelectActivity.MSG_SELECTED_CHANGED;
			msg.arg1 = mSelected.size();
			mHandler.sendMessage(msg);
		}

		notifyDataSetChanged();
	}

	void addPlayer(MemberBean player) {
		if (player ==  null) {
			return;
		}

		mMemberData.add(player);
		if (!mSelected.contains(player)) {
			mSelected.add(player);
		}
		notifyDataSetChanged();

		Message msg = new Message();
		msg.what = MemberSelectActivity.MSG_SELECTED_CHANGED;
		msg.arg1 = mSelected.size();
		mHandler.sendMessage(msg);
	}

	@SuppressLint("InflateParams")
	@Override
	public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		FrameLayout layout = new FrameLayout(mContext);
		FrameLayout.LayoutParams lp;
		for (int i = 0; i < COUNT_PER_ROW; i++) {
			View member = mLayoutInflater.inflate(R.layout.member_selector_item, null);
			member.setFocusable(true);

			lp = new FrameLayout.LayoutParams(mCellWidth, mCellHeight);
			lp.leftMargin = mCellWidth * i + mCellGap * (i + 1);
			lp.topMargin = mCellGap;
			member.setLayoutParams(lp);
			member.setVisibility(View.INVISIBLE);

			layout.addView(member);
		}

		return new ListItemViewHolder(layout);
	}

	@Override
	public void onBindViewHolder(ListItemViewHolder holder, int position) {
		FrameLayout layout = (FrameLayout) holder.mContent;
		ViewGroup itemView;

		int index;
		for (int i = 0; i < COUNT_PER_ROW; i++) {
			index = COUNT_PER_ROW * position + i;
			itemView = (ViewGroup) layout.getChildAt(i);
			if (index >= mMemberData.size()) {
				itemView.setVisibility(View.INVISIBLE);
				break;
			}

			itemView.setVisibility(View.VISIBLE);
			final MemberBean member = mMemberData.get(index);

			TextView nameView = itemView.findViewById(R.id.member_name);
			nameView.setText(member.mName);

			boolean isSelected = mSelected.contains(member);
			Drawable bg = isSelected ? mSelectedBg : mNormalBg;
			itemView.setBackground(bg);

			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mSelected.contains(member)) {
						mSelected.remove(member);
					} else {
						mSelected.add(member);
					}

					Message msg = new Message();
					msg.what = MemberSelectActivity.MSG_SELECTED_CHANGED;
					msg.arg1 = mSelected.size();
					mHandler.sendMessage(msg);
				}
			});
		}
	}

	@Override
	public int getItemCount() {
		int div = mMemberData.size() / COUNT_PER_ROW;
		int mod = mMemberData.size() % COUNT_PER_ROW;

		return div + (mod > 0 ? 1 : 0);
	}

	ArrayList<MemberBean> getSelected() {
		return mSelected;
	}

	void setSelectAll(boolean selectAll) {
		mSelected.clear();
		if (selectAll) {
			mSelected.addAll(mMemberData);
		}
		notifyDataSetChanged();
	}

	void setPrevSelected(ArrayList<MemberBean> list) {
		if (list == null || list.isEmpty()) {
			return;
		}

		mPrevSelected.clear();
		mPrevSelected.addAll(list);
	}
}