package com.windfind.clubassistant.history;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.windfind.clubassistant.R;
import com.windfind.clubassistant.view.ListItemViewHolder;

import java.util.ArrayList;

class ProfitListAdapter extends RecyclerView.Adapter<ListItemViewHolder> {

	private LayoutInflater mInflater;
	private ArrayList<ProfitForGame> mProfitList;

	ProfitListAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		mProfitList = new ArrayList<>();
	}

	public void setData(ArrayList<ProfitForGame> data) {
		if (data == null || data.isEmpty()) {
			return;
		}

		mProfitList.clear();
		mProfitList.addAll(data);
		notifyDataSetChanged();
	}

	@SuppressLint("InflateParams")
	@Override
	public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View group = mInflater.inflate(R.layout.profit_list_item, null);
		return new ListItemViewHolder(group);
	}

	@Override
	public void onBindViewHolder(ListItemViewHolder holder, int position) {
		ViewGroup vg = (ViewGroup) holder.mContent;
		ProfitForGame profit = mProfitList.get(position);

		TextView costOutView = vg.findViewById(R.id.cost_out);
		costOutView.setText(String.valueOf(profit.mCostOut));

		TextView costInVIew = vg.findViewById(R.id.cost_in);
		costInVIew.setText(String.valueOf(profit.mCostIn));

		TextView remainView = vg.findViewById(R.id.remain);
		remainView.setText(String.valueOf(profit.mCostIn - profit.mCostOut));

		TextView dateView = vg.findViewById(R.id.date);
		dateView.setText(profit.mDate);

		TextView addressView = vg.findViewById(R.id.address);
		addressView.setText(profit.mAddress);
	}

	@Override
	public int getItemCount() {
		return mProfitList.size();
	}
}
