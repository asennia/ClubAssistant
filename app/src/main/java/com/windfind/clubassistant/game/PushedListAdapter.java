package com.windfind.clubassistant.game;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.windfind.clubassistant.R;
import com.windfind.clubassistant.member.MemberBean;

import java.util.ArrayList;

public class PushedListAdapter extends RecyclerView.Adapter<PushedItemHolder> {

	private LayoutInflater mLayoutInflater;
	private ArrayList<MemberBean> mPlayers = new ArrayList<>();

	public PushedListAdapter(Context context) {
		mLayoutInflater = LayoutInflater.from(context);
	}

	public void push(MemberBean bean) {
		if (mPlayers.contains(bean)) {
			return;
		}

		mPlayers.add(bean);
		notifyDataSetChanged();
	}

	public void pop(MemberBean bean) {
		if (mPlayers.contains(bean)) {
			mPlayers.remove(bean);
			notifyDataSetChanged();
		}
	}

	@Override
	public PushedItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = mLayoutInflater.inflate(R.layout.pushed_item, parent, false);
		return new PushedItemHolder(view);
	}

	@Override
	public void onBindViewHolder(PushedItemHolder holder, int position) {
		View item = holder.itemView;
		final MemberBean data = mPlayers.get(position);
		holder.nameView.setText(data.mName);

		item.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mOnItemClickedListener != null) {
					mOnItemClickedListener.onItemClicked(data);
				}
			}
		});
	}

	@Override
	public int getItemCount() {
		return mPlayers.size();
	}

	public interface OnItemClickedListener {
		void onItemClicked(MemberBean data);
	}

	private OnItemClickedListener mOnItemClickedListener = null;

	public void setOnItemClickedListener(OnItemClickedListener listener) {
		mOnItemClickedListener = listener;
	}
}
