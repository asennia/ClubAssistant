package com.windfind.clubassistant.game;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.windfind.clubassistant.R;
import com.windfind.clubassistant.member.MemberBean;
import com.windfind.clubassistant.view.CandidateListItemHolder;

import java.util.ArrayList;

public class CandidateListAdapter extends RecyclerView.Adapter<CandidateListItemHolder> {

	private LayoutInflater mLayoutInflater;
	private ArrayList<MemberBean> mCandidates = new ArrayList<>();
	private ArrayList<Integer> mAlreadyPushIndex = new ArrayList<>();
	private int mSelectedIndex = -1;

	CandidateListAdapter(Context context) {
		mLayoutInflater = LayoutInflater.from(context);
	}

	void setData(ArrayList<MemberBean> data) {
		mCandidates.clear();
		if (data != null) {
			mCandidates.addAll(data);
		}
		notifyDataSetChanged();
	}

	@Override
	public CandidateListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = mLayoutInflater.inflate(R.layout.candidate_list_item_layout, parent, false);
		return new CandidateListItemHolder(view);
	}

	@Override
	public void onBindViewHolder(CandidateListItemHolder holder, int position) {
		for (int i = 0; i < 3; i++) {
			final int index = position * 3 + i;
			View item = holder.items[i];
			if (index >= mCandidates.size()) {
				item.setVisibility(View.INVISIBLE);
				continue;
			}

			if (mAlreadyPushIndex.contains(index)) {
				item.setVisibility(View.INVISIBLE);
				continue;
			}

			item.setVisibility(View.VISIBLE);

			MemberBean data = mCandidates.get(index);
			TextView label = holder.labels[i];
			label.setText(data.mName);
			if (mSelectedIndex == index) {
				item.setBackgroundResource(R.drawable.player_selector_selected_bg);
			} else {
				item.setBackgroundResource(R.drawable.candidate_bg);
			}

			item.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (mSelectedIndex != index) {
						// 先将之前高亮的球员设置为非高亮，然后将当前设置为高亮
						mSelectedIndex = index;
						notifyDataSetChanged();

						if (mOnCandidateClickListener != null) {
							mOnCandidateClickListener.onCandidateClick(index);
						}
					}
				}
			});
		}
	}

	public void onCandidatePush(Integer index) {
		if (index < 0 || index >= mCandidates.size()) {
			return;
		}

		if (mAlreadyPushIndex.contains(index)) {
			return;
		}

		mSelectedIndex = -1;
		mAlreadyPushIndex.add(index);
		notifyDataSetChanged();
	}

	public void onCandidatePop(Integer index) {
		if (index < 0 || index >= mCandidates.size()) {
			return;
		}

		if (!mAlreadyPushIndex.contains(index)) {
			return;
		}

		mSelectedIndex = -1;
		mAlreadyPushIndex.remove(index);
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount() {
		int cnt = mCandidates.size() / 3;
		int extra = (mCandidates.size() % 3) > 0 ? 1 : 0;
		return cnt + extra;
	}

	public interface OnCandidateSelectListener {
		void onCandidateClick(int index);
	}

	private OnCandidateSelectListener mOnCandidateClickListener;

	public void setOnCandidateClickListener(OnCandidateSelectListener listener) {
		mOnCandidateClickListener = listener;
	}
}
