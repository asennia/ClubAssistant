package com.windfind.clubassistant.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
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

class GameDetailPlayerListAdapter extends RecyclerView.Adapter<ListItemViewHolder> {

	private static final int COUNT_PER_ROW = 2;

	private Context mContext;
	private Handler mHandler;
	private LayoutInflater mInflater;
	private int mCellWidth;
	private int mCellHeight;
	private int mCellGap;

	private ArrayList<PlayerDetailBean> mPlayerDetail;

	GameDetailPlayerListAdapter(Context context, Handler handler) {
		mContext = context;
		mHandler = handler;
		Resources res = context.getResources();
		mInflater = LayoutInflater.from(context);
		mPlayerDetail = new ArrayList<>();
		mCellGap = res.getDimensionPixelSize(R.dimen.member_view_gap);
		int screenWidth = CAApplication.sDeviceWidth;
		int totalGap = mCellGap * (COUNT_PER_ROW + 1);
		mCellWidth = (screenWidth - totalGap) / COUNT_PER_ROW;
		mCellHeight = res.getDimensionPixelSize(R.dimen.member_view_height);
	}

	void setData(ArrayList<PlayerDetailBean> data) {
		mPlayerDetail.clear();
		mPlayerDetail.addAll(data);
		notifyDataSetChanged();
	}

	@SuppressLint("InflateParams")
	@Override
	public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		FrameLayout layout = new FrameLayout(mContext);
		FrameLayout.LayoutParams lp;
		for (int i = 0; i < COUNT_PER_ROW; i++) {
			View member = mInflater.inflate(R.layout.game_detail_player_item, null);
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
		PlayerDetailBean item;
		View member;

		TextView nameView;
		TextView goalContent;
		TextView costContent;
		int index;
		for (int i = 0; i < COUNT_PER_ROW; i++) {
			member = layout.getChildAt(i);
			index = COUNT_PER_ROW * position + i;
			if (index >= mPlayerDetail.size()) {
				member.setVisibility(View.INVISIBLE);
				break;
			}

			item = mPlayerDetail.get(index);

			nameView = member.findViewById(R.id.player_name);
			nameView.setText(item.mMemberName);

			goalContent = member.findViewById(R.id.goal_content);
			String goalStr = item.mGoals + "";
			goalContent.setText(goalStr);

			costContent = member.findViewById(R.id.cost_content);
			String costStr = item.mCost + "";
			costContent.setText(costStr);

			member.setVisibility(View.VISIBLE);

			final PlayerDetailBean curPlayer = item;
			member.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Bundle bundle = new Bundle();
					bundle.putLong(GameDetailPlayerListFragment.CLICKED_ID, curPlayer.mMemberId);
					bundle.putLong(GameDetailPlayerListFragment.PLAYER_DATA_ID, curPlayer.mPlayerDataId);

					Message msg = new Message();
					msg.what = GameDetailPlayerListFragment.MSG_PLAYER_CLICKED;
					msg.setData(bundle);

					mHandler.sendMessage(msg);
				}
			});
		}
	}

	@Override
	public int getItemCount() {
		int div = mPlayerDetail.size() / COUNT_PER_ROW;
		int mod = mPlayerDetail.size() % COUNT_PER_ROW;

		return div + (mod > 0 ? 1 : 0);
	}

	static class PlayerDetailBean {

		long mMemberId;
		long mPlayerDataId;
		String mMemberName;
		int mGoals;
		float mCost;

		PlayerDetailBean() {
		}
	}
}