package com.windfind.clubassistant.member;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.windfind.clubassistant.CAApplication;
import com.windfind.clubassistant.DataModel;
import com.windfind.clubassistant.R;
import com.windfind.clubassistant.game.GameBean;
import com.windfind.clubassistant.game.PlayerDataBean;
import com.windfind.clubassistant.member.MemberDetailAdapter.MemberPlayedData;

import java.util.ArrayList;

public class MemberDetailActivity extends AppCompatActivity {

	private DataModel mModel;
	private Resources mRes;

	private TextView mName;
	private ImageView mVipIcon;
	private TextView mPos;
	private TextView mPlayedTimes;
	private TextView mGoals;
	private ViewGroup mHistoryView;
	private ViewGroup mEmptyView;

	private MemberDetailAdapter mAdapter;
	private long mMemberId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mModel = ((CAApplication) getApplicationContext()).getModel();
		mRes = getResources();

		setContentView(R.layout.member_detail_layout);

		initView();

		Intent intent = getIntent();
		long id = intent.getLongExtra("member_id", -1);
		if (id == -1) {
			finish();
		} else {
			mMemberId = id;
		}
	}

	private void initView() {
		Button btnEdit = findViewById(R.id.btn_edit);
		btnEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MemberDetailActivity.this, EditMemberActivity.class);
				intent.putExtra("mode",  EditMemberActivity.MODE_EDIT);
				intent.putExtra("member_id", mMemberId);
				MemberDetailActivity.this.startActivity(intent);
			}
		});

		mName = findViewById(R.id.name);
		mVipIcon = findViewById(R.id.vip);
		mPos = findViewById(R.id.pos);
		mPlayedTimes = findViewById(R.id.played_times);
		mGoals = findViewById(R.id.goals);
		RecyclerView historyList = findViewById(R.id.history_list);
		mHistoryView = findViewById(R.id.history_container);
		mEmptyView = findViewById(R.id.empty_content);

		LayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
		historyList.setLayoutManager(lm);
		mAdapter = new MemberDetailAdapter(this);
		historyList.setAdapter(mAdapter);
	}

	@Override
	protected void onResume() {
		super.onResume();

		MemberBean member = mModel.getMemberById(mMemberId);
		if (member == null) {
			finish();
			return;
		}

		mName.setText(member.mName);
		mVipIcon.setVisibility(member.mIsVip ? View.VISIBLE : View.GONE);

		ArrayList<String> posStringIdArray = member.getPosStringArray(mRes);
		String pos = posStringIdArray.size() > 0 ? TextUtils.join("/", posStringIdArray) : "";
		mPos.setText(pos);

		refreshPlayedList();
	}

	private void refreshPlayedList() {
		ArrayList<PlayerDataBean> playedList = mModel.getPlayerDataByMember(mMemberId);
		if (playedList == null || playedList.isEmpty()) {
			mEmptyView.setVisibility(View.VISIBLE);
			mHistoryView.setVisibility(View.GONE);
			return;
		} else {
			mEmptyView.setVisibility(View.GONE);
			mHistoryView.setVisibility(View.VISIBLE);
		}

		mPlayedTimes.setText(String.valueOf(playedList.size()));

		int goalCnt = 0;
		for (PlayerDataBean data : playedList) {
			goalCnt += data.mGoals;
		}
		mGoals.setText(String.valueOf(goalCnt));

		ArrayList<Long> ids = new ArrayList<>();
		for (PlayerDataBean data : playedList) {
			ids.add(data.mGameId);
		}

		LongSparseArray<GameBean> gameArray = mModel.getGameById(ids);
		if (gameArray == null || gameArray.size() == 0) {
			return;
		}

		ArrayList<MemberPlayedData> dataList = new ArrayList<>();
		for (PlayerDataBean data : playedList) {
			GameBean game = gameArray.get(data.mGameId);
			if (game == null) {
				continue;
			}

			MemberPlayedData mpd = new MemberPlayedData();
			mpd.mDate = game.mDate;
			mpd.mCost = String.valueOf(data.mCost);
			mpd.mGoal = String.valueOf(data.mGoals);
			dataList.add(mpd);
		}
		mAdapter.setData(dataList);
	}
}