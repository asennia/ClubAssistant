package com.windfind.clubassistant.member;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.windfind.clubassistant.R;
import com.windfind.clubassistant.view.ListItemViewHolder;

import java.util.ArrayList;

class MemberDetailAdapter extends RecyclerView.Adapter<ListItemViewHolder> {

	private ArrayList<MemberPlayedData> mPlayedData;
	private LayoutInflater mInflater;

	MemberDetailAdapter(Context context) {
		mInflater = LayoutInflater.from(context);

		mPlayedData = new ArrayList<>();
	}

	void setData(ArrayList<MemberPlayedData> data) {
		if (data == null || data.isEmpty()) {
			return;
		}

		mPlayedData.clear();
		mPlayedData.addAll(data);
		notifyDataSetChanged();
	}

	@SuppressLint("InflateParams")
	@Override
	public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		ViewGroup vg = (ViewGroup) mInflater.inflate(R.layout.member_detail_data_item, null);
		return new ListItemViewHolder(vg);
	}

	@Override
	public void onBindViewHolder(ListItemViewHolder holder, int position) {
		ViewGroup vg = (ViewGroup) holder.mContent;
		MemberPlayedData data =  mPlayedData.get(position);

		TextView dateView = vg.findViewById(R.id.date_view);
		dateView.setText(data.mDate);

		TextView costView = vg.findViewById(R.id.cost_content);
		costView.setText(data.mCost);

		TextView goalView = vg.findViewById(R.id.goal_content);
		goalView.setText(data.mGoal);
	}

	@Override
	public int getItemCount() {
		return mPlayedData.size();
	}

	final static class MemberPlayedData {
		String mDate;
		String mCost;
		String mGoal;
	}
}
