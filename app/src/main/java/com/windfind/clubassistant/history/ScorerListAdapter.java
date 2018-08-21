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

class ScorerListAdapter extends RecyclerView.Adapter<ListItemViewHolder> {

	private LayoutInflater mInflater;
	private ArrayList<Scorer> mScorerList;

	ScorerListAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		mScorerList = new ArrayList<>();
	}

	public void setData(ArrayList<Scorer> data) {
		if (data == null || data.isEmpty()) {
			return;
		}

		mScorerList.clear();
		mScorerList.addAll(data);
		notifyDataSetChanged();
	}

	@SuppressLint("InflateParams")
	@Override
	public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View group = mInflater.inflate(R.layout.scorer_list_item, null);
		return new ListItemViewHolder(group);
	}

	@Override
	public void onBindViewHolder(ListItemViewHolder holder, int position) {
		ViewGroup vg = (ViewGroup) holder.mContent;
		Scorer scorer = mScorerList.get(position);

		TextView indexView = vg.findViewById(R.id.index);
		indexView.setText(String.valueOf(scorer.mIndex));

		TextView nameView = vg.findViewById(R.id.name);
		nameView.setText(scorer.mName);

		TextView goalCountView = vg.findViewById(R.id.goal_count);
		goalCountView.setText(String.valueOf(scorer.mGoals));
	}

	@Override
	public int getItemCount() {
		return mScorerList.size();
	}
}
