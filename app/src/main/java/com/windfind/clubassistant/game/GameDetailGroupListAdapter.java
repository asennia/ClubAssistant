package com.windfind.clubassistant.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.windfind.clubassistant.R;
import com.windfind.clubassistant.member.MemberBean;
import com.windfind.clubassistant.view.ListItemViewHolder;

import java.util.ArrayList;

class GameDetailGroupListAdapter extends RecyclerView.Adapter<ListItemViewHolder> {

	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<GroupDetail> mGroups;
	private int[] mLogos = {
			R.drawable.flag_icon_1,
			R.drawable.flag_icon_2,
			R.drawable.flag_icon_3,
			R.drawable.flag_icon_4};
	private String mSymbol;

	GameDetailGroupListAdapter(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mGroups = new ArrayList<>();
		mSymbol = context.getString(R.string.text_join_symbol);
	}

	void setData(ArrayList<GroupDetail> data) {
		mGroups.clear();
		mGroups.addAll(data);
		notifyDataSetChanged();
	}

	@SuppressLint("InflateParams")
	@Override
	public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View group = mInflater.inflate(R.layout.game_detail_group_item, null);
		return new ListItemViewHolder(group);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onBindViewHolder(ListItemViewHolder holder, int position) {
		GroupDetail group = mGroups.get(position);
		ViewGroup vg = (ViewGroup) holder.mContent;

		ImageView groupLogo = vg.findViewById(R.id.group_logo);
		int index = position % (mLogos.length);
		int logo = mLogos[index];
		groupLogo.setImageDrawable(mContext.getResources().getDrawable(logo));

		TextView groupName = vg.findViewById(R.id.group_name);
		String title = mContext.getString(R.string.text_group_name) + group.mGroupIndex;
		groupName.setText(title);

		ArrayList<String> playerNameList = new ArrayList<>();
		for (MemberBean player : group.mGroupMember) {
			if (player == null) {
				continue;
			}

			if (TextUtils.isEmpty(player.mName)) {
				playerNameList.add(mContext.getResources().getString(R.string.text_unknown));
			} else {
				playerNameList.add(player.mName);
			}
		}

		TextView groupMember = vg.findViewById(R.id.group_member);
		groupMember.setText(TextUtils.join(mSymbol, playerNameList));
	}

	@Override
	public int getItemCount() {
		return mGroups.size();
	}
}
