package com.windfind.clubassistant.member;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.windfind.clubassistant.CAApplication;
import com.windfind.clubassistant.view.ListItemViewHolder;
import com.windfind.clubassistant.R;

import java.util.ArrayList;

class MemberListAdapter extends RecyclerView.Adapter<ListItemViewHolder> {

	private static final int COUNT_PER_ROW = 2;

	private ArrayList<MemberBean> mMemberData;
	private Context mContext;
	private Activity mActivity;
	private LayoutInflater mLayoutInflater;
	private int mCellWidth;
	private int mCellHeight;
	private int mCellGap;

	MemberListAdapter(Context context) {
		mContext = context;
		Resources res = context.getResources();
		mMemberData = new ArrayList<>();
		mLayoutInflater = LayoutInflater.from(context);
		mCellGap = res.getDimensionPixelSize(R.dimen.member_view_gap);
		int screenWidth = CAApplication.sDeviceWidth;
		int totalGap = mCellGap * (COUNT_PER_ROW + 1);
		mCellWidth = (screenWidth - totalGap) / COUNT_PER_ROW;
		mCellHeight = res.getDimensionPixelSize(R.dimen.member_view_height);
	}

	void setActivity(Activity activity) {
		mActivity = activity;
	}

	public void setData(ArrayList<MemberBean> data) {
		mMemberData.clear();
		mMemberData.addAll(data);
		notifyDataSetChanged();
	}

	@SuppressLint("InflateParams")
	@Override
	public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		FrameLayout layout = new FrameLayout(mContext);
		FrameLayout.LayoutParams lp;
		for (int i = 0; i < COUNT_PER_ROW; i++) {
			View member = mLayoutInflater.inflate(R.layout.member_list_item, null);
			member.setFocusable(true);

			lp = new FrameLayout.LayoutParams(mCellWidth, mCellHeight);
			lp.leftMargin = mCellWidth * i + mCellGap * (i + 1);
			lp.topMargin = mCellGap / 2;
			member.setLayoutParams(lp);
			member.setVisibility(View.INVISIBLE);

			layout.addView(member);
		}

		return new ListItemViewHolder(layout);
	}

	@Override
	public void onBindViewHolder(ListItemViewHolder holder, int position) {
		FrameLayout layout = (FrameLayout) holder.mContent;
		MemberBean item;
		View member;

		TextView nameView;
		TextView posView;
		ImageView vipIcon;
		int index;
		for (int i = 0; i < COUNT_PER_ROW; i++) {
			index = COUNT_PER_ROW * position + i;
			member = layout.getChildAt(i);
			if (index >= mMemberData.size()) {
				member.setVisibility(View.INVISIBLE);
				break;
			}

			item = mMemberData.get(index);

			nameView = member.findViewById(R.id.member_name);
			nameView.setText(item.mName);

			posView = member.findViewById(R.id.member_position);
			ArrayList<String> posStringIdArray = item.getPosStringArray(mContext.getResources());
			String pos = posStringIdArray.size() > 0 ? TextUtils.join("/", posStringIdArray) : "";
			posView.setText(pos);

			vipIcon = member.findViewById(R.id.vip_icon);
			vipIcon.setVisibility(item.mIsVip ? View.VISIBLE : View.INVISIBLE);

			member.setVisibility(View.VISIBLE);

			final long curMemberId = item.mId;
			member.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, MemberDetailActivity.class);
					intent.putExtra("member_id", curMemberId);
					mActivity.startActivity(intent);
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
}
