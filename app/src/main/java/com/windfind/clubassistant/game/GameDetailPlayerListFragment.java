package com.windfind.clubassistant.game;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.windfind.clubassistant.CAApplication;
import com.windfind.clubassistant.DataModel;
import com.windfind.clubassistant.R;
import com.windfind.clubassistant.common.PageFragment;
import com.windfind.clubassistant.game.GameDetailPlayerListAdapter.PlayerDetailBean;
import com.windfind.clubassistant.member.MemberBean;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class GameDetailPlayerListFragment extends PageFragment {

	public static final String CLICKED_ID = "clicked_id";
	public static final String PLAYER_DATA_ID = "player_data_id";

	private Context mContext;
	private DataModel mModel;
	private ArrayList<PlayerDataBean> mMainPlayerData;
	private LongSparseArray<MemberBean> mPlayers;
	private LongSparseArray<PlayerDataBean> mPlayerData;
	private LongSparseArray<PlayerDataBean> mExtraPlayerData;
	private ArrayList<Long> mPlayerIds;
	private GameDetailPlayerListAdapter mAdapter;

	private int mGoalCount;
	private float mCost;

	private boolean mIsReady = false;

	public static final int MSG_PLAYER_CLICKED = 100;

	private static class MyHandler extends Handler {

		private WeakReference<GameDetailPlayerListFragment> mFragment;

		MyHandler(GameDetailPlayerListFragment fragment) {
			mFragment = new WeakReference<>(fragment);
		}

		@Override
		public void handleMessage(Message msg) {
			GameDetailPlayerListFragment fragment = mFragment.get();
			if (fragment != null) {
				switch (msg.what) {
					case MSG_PLAYER_CLICKED:
						Bundle data = msg.getData();
						if (data != null) {
							long id = data.getLong(CLICKED_ID);
							long player_data_id = data.getLong(PLAYER_DATA_ID);
							fragment.onPlayerClicked(id, player_data_id);
						}
						break;
					default:
						break;
				}
			}
		}
	}


	public static GameDetailPlayerListFragment newInstance() {
		return new GameDetailPlayerListFragment();
	}

	public GameDetailPlayerListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getContext().getApplicationContext();
		mModel = ((CAApplication) mContext.getApplicationContext()).getModel();
		mPlayerData = new LongSparseArray<>();
		mPlayerIds = new ArrayList<>();
		if (mMainPlayerData == null) {
			mMainPlayerData = new ArrayList<>();
		}
		if (mExtraPlayerData == null) {
			mExtraPlayerData = new LongSparseArray<>();
		}
		mAdapter = new GameDetailPlayerListAdapter(mContext, new MyHandler(this));
		mIsReady = true;
	}

	@Override
	public void onResume() {
		super.onResume();

		refreshList();
	}

	@Override
	public int getTitleId() {
		return R.string.text_players;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.game_detail_player_list, container, false);
		RecyclerView playerList = rootView.findViewById(R.id.player_list);
		playerList.setAdapter(mAdapter);
		RecyclerView.LayoutManager lm = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
		playerList.setLayoutManager(lm);

		return rootView;
	}

	public void setData(ArrayList<PlayerDataBean> playerData) {
		if (playerData == null || playerData.isEmpty()) {
			return;
		}

		if (mMainPlayerData == null) {
			mMainPlayerData = new ArrayList<>();
		}
		if (mExtraPlayerData == null) {
			mExtraPlayerData = new LongSparseArray<>();
		}
		mMainPlayerData.clear();
		mMainPlayerData.addAll(playerData);

		if (mIsReady) {
			refreshList();
		}
	}

	private void refreshList() {
		if (mMainPlayerData == null || mMainPlayerData.isEmpty()) {
			return;
		}

		mPlayerData.clear();
		mPlayerIds.clear();
		mExtraPlayerData.clear();
		for (PlayerDataBean data : mMainPlayerData) {
			if (data.mMemberId > 0) {
				mPlayerData.put(data.mMemberId, data);
				mPlayerIds.add(data.mMemberId);
			} else {
				mExtraPlayerData.put(data.mId, data);
			}
		}

		mPlayers = mModel.getMembersById(mPlayerIds);
		if (mPlayers == null || mPlayers.size() == 0) {
			return;
		}

		ArrayList<PlayerDetailBean> playerDetailList = new ArrayList<>();
		for (long id : mPlayerIds) {
			PlayerDetailBean bean = new PlayerDetailBean();

			bean.mMemberId = id;
			bean.mPlayerDataId = mPlayerData.get(id).mId;
			bean.mMemberName = mPlayers.get(id).mName;
			bean.mGoals = mPlayerData.get(id).mGoals;
			bean.mCost = mPlayerData.get(id).mCost;

			playerDetailList.add(bean);
		}

		for (int i = 0; i < mExtraPlayerData.size(); i++) {
			PlayerDataBean extraPlayer = mExtraPlayerData.valueAt(i);
			PlayerDetailBean bean = new PlayerDetailBean();

			bean.mMemberId = extraPlayer.mMemberId;
			bean.mPlayerDataId = extraPlayer.mId;
			bean.mMemberName = extraPlayer.mName;
			bean.mGoals = extraPlayer.mGoals;
			bean.mCost = extraPlayer.mCost;

			playerDetailList.add(bean);
		}

		mAdapter.setData(playerDetailList);
	}

	@SuppressLint("InflateParams")
	private void onPlayerClicked(long id, long playerDataId) {
		MemberBean player;
		PlayerDataBean playerData;
		if (id <= 0) {
			playerData = mExtraPlayerData.get(playerDataId);
			if (playerData == null) {
				return;
			}

			player = new MemberBean();
			player.mId = playerData.mMemberId;
			player.mName = playerData.mName;
		} else {
			player = mPlayers.get(id);
			playerData = mPlayerData.get(id);
		}

		if (player == null || playerData == null) {
			return;
		}

		final PlayerDataBean finalData = playerData;

		mGoalCount = playerData.mGoals;
		mCost = playerData.mCost;

		Activity activity = getActivity();
		LayoutInflater inflater = LayoutInflater.from(activity);
		ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.game_detail_player_detail, null);

		final TextView goalCountView = vg.findViewById(R.id.goal_count);
		String goalStr = mGoalCount + "";
		goalCountView.setText(goalStr);

		final ImageButton btnDec = vg.findViewById(R.id.btn_dec);
		btnDec.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mGoalCount--;
				String goalStr = mGoalCount + "";
				goalCountView.setText(goalStr);
				btnDec.setEnabled(mGoalCount > 0);
			}
		});
		btnDec.setEnabled(mGoalCount > 0);

		final ImageButton btnInc = vg.findViewById(R.id.btn_inc);
		btnInc.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mGoalCount++;
				String goalStr = mGoalCount + "";
				goalCountView.setText(goalStr);
				btnDec.setEnabled(mGoalCount > 0);
			}
		});

		final EditText costEditor = vg.findViewById(R.id.cost_input);
		String costStr = mCost + "";
		costEditor.setText(costStr);
		costEditor.setSelectAllOnFocus(true);

		costEditor.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String content = s.toString();
				mCost = TextUtils.isEmpty(s.toString()) ? 0 : Float.parseFloat(content);
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(player.mName);
		builder.setView(vg);
		builder.setNegativeButton(R.string.text_cancel, null);
		builder.setPositiveButton(R.string.text_save, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				finalData.mGoals = mGoalCount;
				finalData.mCost = mCost;
				mModel.updatePlayerDataById(finalData.mId, finalData);

				refreshList();
			}
		});
		builder.create().show();
	}
}