package com.windfind.clubassistant.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.windfind.clubassistant.R;

public class CandidateListItemHolder extends RecyclerView.ViewHolder {
	private static final int[] itemIds = {R.id.candidate_1, R.id.candidate_2, R.id.candidate_3};
	private static final int[] labelIds = {R.id.candidate_1_name, R.id.candidate_2_name, R.id.candidate_3_name};
	public View[] items = new View[3];
	public TextView[] labels = new TextView[3];

	public CandidateListItemHolder(View itemView) {
		super(itemView);
		for (int i = 0; i < 3; i++) {
			items[i] = itemView.findViewById(itemIds[i]);
			labels[i] = itemView.findViewById(labelIds[i]);
		}
	}
}
