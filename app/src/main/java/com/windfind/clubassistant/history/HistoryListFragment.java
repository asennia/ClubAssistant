package com.windfind.clubassistant.history;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.windfind.clubassistant.CAApplication;
import com.windfind.clubassistant.DataModel;
import com.windfind.clubassistant.R;
import com.windfind.clubassistant.common.CommonPagerAdapter;
import com.windfind.clubassistant.common.PageFragment;
import com.windfind.clubassistant.game.GameBean;
import com.windfind.clubassistant.game.PlayerDataBean;
import com.windfind.clubassistant.member.MemberBean;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class HistoryListFragment extends PageFragment {

	private DataModel mModel;
	private MyHandler mHandler;
	private TextView mGameCountView;
	private TextView mTotalGoalsView;
	private TextView mRemainProfitView;

	private ScorerListFragment mScorerListFragment;
	private AttendanceListFragment mAttendanceListFragment;
	private ProfitListFragment mProfitListFragment;

	private ArrayList<Scorer> mScorersArray = new ArrayList<>();
	private ArrayList<Attendance> mAttendanceArray = new ArrayList<>();
	private ArrayList<ProfitForGame> mProfitData = new ArrayList<>();
	private int mGameCount = 0;
	private int mTotalGoal = 0;
	private float mRemainProfit = 0;

	private static final int MSG_LOAD_FINISH = 100;

	public static HistoryListFragment newInstance() {
		return new HistoryListFragment();
	}

	private static class MyHandler extends Handler {
		private static WeakReference<HistoryListFragment> mFragment;

		MyHandler(HistoryListFragment fragment) {
			mFragment = new WeakReference<>(fragment);
		}

		@Override
		public void handleMessage(Message msg) {
			HistoryListFragment fragment = mFragment.get();
			if (fragment != null) {
				switch (msg.what) {
					case MSG_LOAD_FINISH:
						fragment.updateUI();
						break;
					default:
						break;
				}
			}
		}
	}

	public HistoryListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Context context = getContext().getApplicationContext();
		mModel = ((CAApplication) context).getModel();
		mHandler = new MyHandler(this);

		mScorerListFragment = ScorerListFragment.newInstance();
		mAttendanceListFragment = AttendanceListFragment.newInstance();
		mProfitListFragment = ProfitListFragment.newInstance();
	}

	@Override
	public int getTitleId() {
		return R.string.text_history;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.history_list, container, false);

		mGameCountView = rootView.findViewById(R.id.game_count_value);
		mTotalGoalsView = rootView.findViewById(R.id.total_goals_value);
		mRemainProfitView = rootView.findViewById(R.id.remain_capital_value);

		CommonPagerAdapter adapter = new CommonPagerAdapter(getActivity(), getChildFragmentManager());
		ViewPager viewPager = rootView.findViewById(R.id.pager);
		if (viewPager != null) {
			viewPager.setAdapter(adapter);
		}

		TabLayout tabLayout = rootView.findViewById(R.id.tabs);
		if (tabLayout != null) {
			tabLayout.setupWithViewPager(viewPager);
		}

		adapter.addItem(mScorerListFragment);
		adapter.addItem(mAttendanceListFragment);
		adapter.addItem(mProfitListFragment);
		adapter.notifyDataSetChanged();

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();

		refreshList();
	}

	private void updateUI() {
		mGameCountView.setText(String.valueOf(mGameCount));
		mTotalGoalsView.setText(String.valueOf(mTotalGoal));
		mRemainProfitView.setText(String.valueOf(mRemainProfit));

		if (mScorersArray.size() > 0) {
			mScorerListFragment.setData(mScorersArray);
		}

		if (mAttendanceArray.size() > 0) {
			mAttendanceListFragment.setData(mAttendanceArray);
		}

		if (mProfitData.size() > 0) {
			mProfitListFragment.setData(mProfitData);
		}
	}

	private void refreshList() {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				ArrayList<GameBean> gameList = mModel.getAllGames();
				if (gameList != null && !gameList.isEmpty()) {
					// 统计累计场次
					mGameCount = gameList.size();

					ArrayList<PlayerDataBean> playerDataList = mModel.getAllPlayerData();
					if (playerDataList != null && !playerDataList.isEmpty()) {
						// 统计总进球数
						int totalGoals = 0;
						for (PlayerDataBean playerData : playerDataList) {
							totalGoals += playerData.mGoals;
						}
						mTotalGoal = totalGoals;

						// 统计剩余资金
						int totalProfit = 0;
						for (PlayerDataBean playerData : playerDataList) {
							totalProfit += playerData.mCost;
						}
						int totalSpend = 0;
						for (GameBean game : gameList) {
							totalSpend += game.mTotalCost;
						}
						mRemainProfit = totalProfit - totalSpend;

						// 统计射手榜
						LongSparseArray<MemberBean> players = mModel.getAllMembersEx();
						generateScorerList(playerDataList, players);
						generateAttendanceList(playerDataList, players);
						generateProfitList(gameList, playerDataList);
					}
				}

				mHandler.sendEmptyMessage(MSG_LOAD_FINISH);
			}
		};

		Thread thread = new Thread(r);
		thread.start();
	}

	private void generateScorerList(ArrayList<PlayerDataBean> playerDataList, LongSparseArray<MemberBean> players) {
		if (playerDataList == null || playerDataList.size() == 0 || players == null || players.size() == 0) {
			return;
		}

		// 获取所有进过球的人员
		LongSparseArray<Scorer> scorers = new LongSparseArray<>();
		ArrayList<Long> scorerIds = new ArrayList<>();
		for (PlayerDataBean playerData : playerDataList) {
			if (playerData.mGoals == 0 || playerData.mMemberId == -1) {
				continue;
			}

			MemberBean member = players.get(playerData.mMemberId);
			if (member == null) {
				continue;
			}

			if (!scorerIds.contains(playerData.mMemberId)) {
				scorerIds.add(playerData.mMemberId);

				Scorer scorer = new Scorer();
				scorer.mName = member.mName;
				scorer.mGoals = playerData.mGoals;
				scorers.put(playerData.mMemberId, scorer);
			} else {
				Scorer scorer = scorers.get(playerData.mMemberId);

				if (scorer == null) {
					scorer = new Scorer();
					scorer.mName = member.mName;
					scorer.mGoals = playerData.mGoals;
					scorers.put(playerData.mMemberId, scorer);
				} else {
					scorer.mGoals += playerData.mGoals;
				}
			}
		}

		// 进行排序
		mScorersArray.clear();
		int mostGoal;
		int lastMostGoal = 0;
		int mostGoalScorerIndex;
		int len = scorers.size();
		int index = 0;
		while(len > 0) {
			mostGoalScorerIndex = -1;
			mostGoal = 0;
			for (int i = 0; i < len; i++) {
				Scorer scorer = scorers.valueAt(i);
				if (scorer.mGoals > mostGoal) {
					mostGoal = scorer.mGoals;
					mostGoalScorerIndex = i;
				}
			}

			if (mostGoalScorerIndex != -1) {
				Scorer scorer = scorers.valueAt(mostGoalScorerIndex);
				if (mostGoal != lastMostGoal) {
					scorer.mIndex = index + 1;
					index++;
				} else {
					scorer.mIndex = index;
				}
				mScorersArray.add(scorer);
				scorers.removeAt(mostGoalScorerIndex);

				lastMostGoal = mostGoal;
			}

			len = scorers.size();
		}
	}

	private void generateAttendanceList(ArrayList<PlayerDataBean> playerDataList, LongSparseArray<MemberBean> players) {
		if (playerDataList == null || playerDataList.size() == 0 || players == null || players.size() == 0) {
			return;
		}

		// 获取所有出过勤的人员
		LongSparseArray<Attendance> attendanceList = new LongSparseArray<>();
		ArrayList<Long> playerIds = new ArrayList<>();
		for (PlayerDataBean playerData : playerDataList) {
			if (playerData.mMemberId == -1) {
				continue;
			}

			MemberBean member = players.get(playerData.mMemberId);
			if (member == null) {
				continue;
			}

			if (playerIds.contains(playerData.mMemberId)) {
				Attendance a = attendanceList.get(playerData.mMemberId);

				if (a == null) {
					a = new Attendance();
					a.mAttendance = 1;
					a.mName = member.mName;
					attendanceList.put(playerData.mMemberId, a);
				} else {
					a.mAttendance++;
				}
			} else {
				playerIds.add(playerData.mMemberId);

				Attendance a = new Attendance();
				a.mAttendance = 1;
				a.mName = member.mName;
				attendanceList.put(playerData.mMemberId, a);
			}
		}

		// 进行排序
		mAttendanceArray.clear();
		int mostAttendance;
		int lastMostAttendance = 0;
		int mostAttendancePlayerIndex;
		int len = attendanceList.size();
		int index = 0;
		while(len > 0) {
			mostAttendancePlayerIndex = -1;
			mostAttendance = 0;
			for (int i = 0; i < len; i++) {
				Attendance attendance = attendanceList.valueAt(i);
				if (attendance.mAttendance > mostAttendance) {
					mostAttendance = attendance.mAttendance;
					mostAttendancePlayerIndex = i;
				}
			}

			if (mostAttendancePlayerIndex != -1) {
				Attendance attendance = attendanceList.valueAt(mostAttendancePlayerIndex);
				if (mostAttendance != lastMostAttendance) {
					attendance.mIndex = index + 1;
					index++;
				} else {
					attendance.mIndex = index;
				}
				mAttendanceArray.add(attendance);
				attendanceList.removeAt(mostAttendancePlayerIndex);

				lastMostAttendance = mostAttendance;
			}

			len = attendanceList.size();
		}
	}

	private void generateProfitList(ArrayList<GameBean> gameList, ArrayList<PlayerDataBean> playerDataList) {
		if (gameList == null || gameList.size() == 0 || playerDataList == null || playerDataList.size() == 0) {
			return;
		}

		LongSparseArray<ProfitForGame> profitList = new LongSparseArray<>();
		for (GameBean game : gameList) {
			ProfitForGame profit = new ProfitForGame();
			profit.mDate = game.mDate;
			profit.mAddress = game.mAddress;
			profit.mCostOut = game.mTotalCost;
			profit.mCostIn = 0;

			profitList.put(game.mId, profit);
		}

		for (PlayerDataBean playerData : playerDataList) {
			if (playerData.mCost == 0) {
				continue;
			}

			ProfitForGame profit = profitList.get(playerData.mGameId);
			if (profit == null) {
				continue;
			}

			profit.mCostIn += playerData.mCost;
		}

		mProfitData.clear();
		for (GameBean game : gameList) {
			ProfitForGame profit = profitList.get(game.mId);
			if (profit == null) {
				continue;
			}

			mProfitData.add(profit);
		}
	}
}
