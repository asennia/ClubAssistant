package com.windfind.clubassistant.game;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.util.LongSparseArray;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.windfind.clubassistant.view.ListItemViewHolder;
import com.windfind.clubassistant.member.MemberBean;
import com.windfind.clubassistant.R;

import java.util.ArrayList;

class GameListAdapter extends RecyclerView.Adapter<ListItemViewHolder> {

	private ArrayList<GameBean> mGameData;
	private LongSparseArray<String> mMemberData = new LongSparseArray<>();
	private Context mContext;
	private Activity mActivity;
	private LayoutInflater mLayoutInflater;

	GameListAdapter(Context context) {
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
		mGameData = new ArrayList<>();
	}

	void setGameData(ArrayList<GameBean> data) {
		mGameData.clear();
		mGameData.addAll(data);
		notifyDataSetChanged();
	}

	void setMemberData(ArrayList<MemberBean> data) {
		if (data != null && !data.isEmpty()) {
			mMemberData.clear();
			for (MemberBean bean : data) {
				mMemberData.put(bean.mId, bean.mName);
			}
		}
	}

	void setActivity(Activity activity) {
		mActivity = activity;
	}

	@SuppressLint("InflateParams")
	@Override
	public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		ViewGroup vg = (ViewGroup) mLayoutInflater.inflate(R.layout.game_list_item, null);
		return new ListItemViewHolder(vg);
	}

	@Override
	public void onBindViewHolder(ListItemViewHolder holder, int position) {
		ViewGroup vg = (ViewGroup) holder.mContent;
		final GameBean item = mGameData.get(position);

		TextView dateView = vg.findViewById(R.id.game_date);
		dateView.setText(item.mDate);

		TextView addressView = vg.findViewById(R.id.game_address);
		addressView.setText(item.mAddress);

		TextView typeView = vg.findViewById(R.id.game_type);
		typeView.setText(mContext.getString(item.getTypeStringId()));

		TextView playersView = vg.findViewById(R.id.players_list);
		ArrayList<PlayerDataBean> playerList = item.mPlayers;
		if (playerList != null && !playerList.isEmpty()) {
			ArrayList<String> playerNameList = new ArrayList<>();
			for (PlayerDataBean player : playerList) {
				if (player.mMemberId > 0) {
					String name = mMemberData.get(player.mMemberId, mContext.getString(R.string.text_unknown));
					playerNameList.add(name);
				} else {
					playerNameList.add(player.mName);
				}
			}

			String symbol = mContext.getString(R.string.text_join_symbol);
			String players = TextUtils.join(symbol, playerNameList);
			playersView.setText(players);
		} else {
			playersView.setText(mContext.getString(R.string.text_no_player));
		}

		View seperatingLine = vg.findViewById(R.id.separating_line);
		if (position >= getItemCount() - 1) {
			seperatingLine.setVisibility(View.INVISIBLE);
		} else {
			seperatingLine.setVisibility(View.VISIBLE);
		}

		vg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, GameDetailActivity.class);
				intent.putExtra(GameDetailActivity.GAME_ID, item.mId);
				mActivity.startActivity(intent);
			}
		});
	}

	@Override
	public int getItemCount() {
		return mGameData.size();
	}
}
