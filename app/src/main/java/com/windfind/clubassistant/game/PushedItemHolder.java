package com.windfind.clubassistant.game;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.windfind.clubassistant.R;

public class PushedItemHolder extends RecyclerView.ViewHolder {

	public TextView nameView;

	public PushedItemHolder(View itemView) {
		super(itemView);

		nameView = itemView.findViewById(R.id.pushed_player_name);
	}
}
