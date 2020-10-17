package com.windfind.clubassistant.game;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.windfind.clubassistant.CAApplication;
import com.windfind.clubassistant.DataModel;
import com.windfind.clubassistant.R;
import com.windfind.clubassistant.common.PageFragment;
import com.windfind.clubassistant.member.MemberBean;
import com.windfind.clubassistant.view.BottomBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class GameDetailGroupListFragment extends PageFragment {

	private Context mContext;
	private DataModel mModel;
	private ArrayList<GroupDetail> mMainData;
	private RecyclerView mGroupList;
	private ViewGroup mEmptyView;
	private GameDetailGroupListAdapter mAdapter;
	private BottomBar mBottomBar;
	private boolean mIsReady = false;

	private GameBean mGameData;
	private ArrayList<GroupDetail> mGroups;

	private String[] mCapacityList;
	private int mCapacityIndex = 1;
	private int mGroupMemberSize = 5;

	private String[] mCountList;
	private int mGroupCntIndex = 0;
	private int mGroupCnt = 2;
	private int mCurSelectedIndex = -1;

	private boolean mIsAutoGroup = true;

	public static GameDetailGroupListFragment newInstance() {
		return new GameDetailGroupListFragment();
	}

	public GameDetailGroupListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getContext().getApplicationContext();
		mModel = ((CAApplication) mContext.getApplicationContext()).getModel();
		if (mMainData == null) {
			mMainData = new ArrayList<>();
		}
		mGroups = new ArrayList<>();
		mAdapter = new GameDetailGroupListAdapter(mContext);
		mIsReady = true;

		mCapacityList = getResources().getStringArray(R.array.group_capacity_list);
		mCountList = getResources().getStringArray(R.array.group_count_list);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (mGroups != null && !mGroups.isEmpty())
		{
			onGroupFinish();
		}
		else
		{
			refreshList();
		}
	}

	@Override
	public int getTitleId() {
		return R.string.text_group;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.game_detail_group_list, container, false);
		mGroupList = rootView.findViewById(R.id.group_list);
		mGroupList.setAdapter(mAdapter);
		RecyclerView.LayoutManager lm = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
		mGroupList.setLayoutManager(lm);

		mEmptyView = rootView.findViewById(R.id.empty_content);

		mBottomBar = rootView.findViewById(R.id.bottom_bar);
		mBottomBar.setCenterButtonVisible(true);
		if (mBottomBar != null) {
			mBottomBar.setLeftButtonListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					doManualDivideToGroup();
					mIsAutoGroup = false;
				}
			});

			mBottomBar.setCenterButtonListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					doAutoDivideToGroup();
				}
			});

			mBottomBar.setRightButtonVisible(false);
			mBottomBar.setRightButtonListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					doSaveGroup();
					mBottomBar.setVisibility(View.GONE);
				}
			});
		}

		return rootView;
	}

	private void refreshList() {
		if (mGameData == null || mGameData.mId <= 0) {
			mMainData.clear();
		} else {
			ArrayList<GroupDetail> groups = mModel.getGroupsByGame(mGameData.mId);
			mMainData.clear();
			if (groups != null && !groups.isEmpty()) {
				mMainData.addAll(groups);
			} else {
				mMainData.clear();
			}
		}

		mAdapter.setData(mMainData);
		if (mMainData.isEmpty()) {
			mEmptyView.setVisibility(View.VISIBLE);
			mGroupList.setVisibility(View.GONE);
			mBottomBar.setVisibility(View.VISIBLE);
		} else {
			mEmptyView.setVisibility(View.GONE);
			mGroupList.setVisibility(View.VISIBLE);
			mBottomBar.setVisibility(View.GONE);
		}
	}

	void setData(ArrayList<GroupDetail> data) {
		if (mMainData == null) {
			mMainData = new ArrayList<>();
		}
		mMainData.clear();
		mMainData.addAll(data);

		if (mIsReady) {
			mAdapter.setData(mMainData);
			if (mMainData.isEmpty()) {
				mEmptyView.setVisibility(View.VISIBLE);
				mGroupList.setVisibility(View.GONE);
			} else {
				mEmptyView.setVisibility(View.GONE);
				mGroupList.setVisibility(View.VISIBLE);
			}
		}
	}

	void setGameData(GameBean game) {
		mGameData = game;
	}

	private void doAutoDivideToGroup() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.text_set_capacity_of_group);
		builder.setSingleChoiceItems(mCapacityList, mCapacityIndex, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mCapacityIndex = which;
			}
		});
		builder.setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mGroupMemberSize = Integer.parseInt(mCapacityList[mCapacityIndex]);
				startAutoDivideToGroup();
			}
		});
		builder.setNegativeButton(R.string.text_cancel, null);
		builder.create().show();
	}

	private void startAutoDivideToGroup() {
		if (mGameData == null || mGameData.mPlayers == null || mGameData.mPlayers.isEmpty()) {
			return;
		}

		mGroups.clear();

		ArrayList<Long> playerIds = new ArrayList<>();
		ArrayList<MemberBean> extraPlayers = new ArrayList<>();
		for (PlayerDataBean data : mGameData.mPlayers) {
			if (data.mMemberId > 0) {
				playerIds.add(data.mMemberId);
			} else {
				MemberBean bean = MemberBean.buildExtraPlayer(getActivity(), data.mName);
				extraPlayers.add(bean);
			}
		}

		LongSparseArray<MemberBean> players = mModel.getMembersById(playerIds);
		if (players == null || players.size() == 0) {
			return;
		}

		int playerCount = players.size();

		// 按球员平均实力从高到低排序
		Comparator<MemberBean> comparator = new Comparator<MemberBean>() {
			@Override
			public int compare(MemberBean lhs, MemberBean rhs) {
				return (lhs.mAbility > rhs.mAbility) ? 1 : 0;
			}
		};
		ArrayList<MemberBean> playersWithOrder = new ArrayList<>();
		for (int i = 0; i < playerCount; i++) {
			playersWithOrder.add(players.valueAt(i));
		}
		if (!extraPlayers.isEmpty()) {
			playersWithOrder.addAll(extraPlayers);
		}
		Collections.sort(playersWithOrder, comparator);

		int totalCount = playersWithOrder.size();

		if (totalCount % mGroupMemberSize == 0) {
			ArrayList<GroupDetail> gd = doAutoDivideToGroupWithNoRemain(playersWithOrder);
			mGroups.addAll(gd);
		} else {
			// 分完之后会有未分组人员，首先抽出多出来的人员，然后再抽出各组
			// 先计算会多出来几个，例如n个，然后将所有人分成n档，每档抽1个

			// 多出来几个人就要分几档
			int countOfBracket = totalCount % mGroupMemberSize;

			// 计算每档人数，最后一档的人数可能超过这个数，没关系，把多出来的人放在最后一档里
			int sizeOfBracket = totalCount / countOfBracket;
			// 执行分档
			ArrayList<ArrayList<MemberBean>> brackets = new ArrayList<>();
			for (int i = 0; i < countOfBracket; i++) {
				ArrayList<MemberBean> bracket = new ArrayList<>();
				for (int j = 0; j < sizeOfBracket; j++) {
					bracket.add(playersWithOrder.get(i * sizeOfBracket + j));
				}
				brackets.add(bracket);
			}

			// 获取最后一个分档，将剩余人员加进去
			ArrayList<MemberBean> lastBracket = brackets.get(brackets.size() - 1);
			for (int i = countOfBracket * sizeOfBracket; i < totalCount; i++) {
				lastBracket.add(playersWithOrder.get(i));
			}

			// 将多出来的抽出来，放到一个分组中
			GroupDetail excludeGroup = new GroupDetail();

			Random random = new Random();
			// 每个组里抽一个
			for (ArrayList<MemberBean> bracket : brackets) {
				int curCount = bracket.size();
				int num = random.nextInt(curCount);
				MemberBean player = bracket.get(num);
				excludeGroup.mGroupMember.add(player);

				// 从球员池中将抽出来的球员remove掉，后续要对剩余球员重新分档抽取
				playersWithOrder.remove(player);
			}

			ArrayList<GroupDetail> gd = doAutoDivideToGroupWithNoRemain(playersWithOrder);
			if (gd != null) {
				mGroups.addAll(gd);
				excludeGroup.mGroupIndex = gd.size() + 1;
			} else {
				excludeGroup.mGroupIndex = 1;
			}
			mGroups.add(excludeGroup);

			// 将各组球员顺序打乱，因为可能涉及谁先守门的问题
			ArrayList<GroupDetail> tempGroups = new ArrayList<>();
			for (GroupDetail group : mGroups) {
				GroupDetail g = new GroupDetail();
				g.mGroupIndex = group.mGroupIndex;

				int size = group.mGroupMember.size();
				for (int i = 0; i < size; i++) {
					int curCount = group.mGroupMember.size();
					int idx = random.nextInt(curCount);
					g.mGroupMember.add(group.mGroupMember.get(idx));
					group.mGroupMember.remove(idx);
				}

				tempGroups.add(g);
			}

			mGroups.clear();
			mGroups.addAll(tempGroups);
		}

		onGroupFinish();
	}

	private ArrayList<GroupDetail> doAutoDivideToGroupWithNoRemain(ArrayList<MemberBean> list) {
		if (list == null || list.isEmpty()) {
			return null;
		}

		int playerCount = list.size();
		if (playerCount % mGroupMemberSize != 0) {
			return null;
		}

		// 刚好能分完
		ArrayList<GroupDetail> groups = new ArrayList<>();
		int groupCount = playerCount / mGroupMemberSize;

		// 首先按平均实力分档，每组有多少人，就要分多少档，所以档的数量 = 组的size
		int countOfBracket = mGroupMemberSize;
		// 每档人数其实就是组的总数
		int sizeOfBracket = playerCount / mGroupMemberSize;

		// 执行分档
		ArrayList<ArrayList<MemberBean>> brackets = new ArrayList<>();
		for (int i = 0; i < countOfBracket; i++) {
			ArrayList<MemberBean> bracket = new ArrayList<>();
			for (int j = 0; j < sizeOfBracket; j++) {
				bracket.add(list.get(i * sizeOfBracket + j));
			}
			brackets.add(bracket);
		}

		// 准备抽签，每档有多少人，就需要抽多少轮签，当然，最后一轮其实没的抽，因为那时候各档只剩一个人
		Random random = new Random();
		for (int i = 0; i < groupCount; i++) {
			// 创建一个分组，设置组名
			GroupDetail group = new GroupDetail();
			group.mGroupIndex = i + 1;

			// 每个组里抽一个
			for (ArrayList<MemberBean> bracket : brackets) {
				int curCount = bracket.size();
				if (curCount == 1) {
					MemberBean player = bracket.get(0);
					group.mGroupMember.add(player);
					bracket.remove(player);
				} else if (curCount > 1) {
					int num = random.nextInt(curCount);
					MemberBean player = bracket.get(num);
					group.mGroupMember.add(player);
					bracket.remove(player);
				}
			}

			groups.add(group);
		}

		return groups;
	}

	private void doManualDivideToGroup() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.text_set_count_of_group);
		builder.setSingleChoiceItems(mCountList, mGroupCntIndex, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mGroupCntIndex = which;
			}
		});
		builder.setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mGroupCnt = Integer.parseInt(mCountList[mGroupCntIndex]);
				startManualDivideToGroup();
			}
		});
		builder.setNegativeButton(R.string.text_cancel, null);
		builder.create().show();
	}

	@SuppressWarnings("ConstantConditions")
	private void startManualDivideToGroup() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(R.layout.manual_group_layout);
//		builder.setTitle(R.string.text_action_group_manual);
//		builder.setSingleChoiceItems(mCapacityList, mCapacityIndex, new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				mCapacityIndex = which;
//			}
//		});
//		builder.setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				mGroupMemberSize = Integer.parseInt(mCapacityList[mCapacityIndex]);
//				startAutoDivideToGroup();
//			}
//		});
		builder.setNegativeButton(R.string.text_cancel, null);
		builder.setPositiveButton(R.string.text_save, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				onGroupFinish();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();

		final ArrayList<MemberBean> candidates = getAllCandidates();

		final View groupAContainer = dialog.findViewById(R.id.group_a_container);
		final View groupBContainer = dialog.findViewById(R.id.group_b_container);
		final View groupCContainer = dialog.findViewById(R.id.group_c_container);
		final View groupDContainer = dialog.findViewById(R.id.group_d_container);
		if (mGroupCnt == 2) {
			groupCContainer.setVisibility(View.GONE);
			groupDContainer.setVisibility(View.GONE);
		} else if (mGroupCnt == 3) {
			groupDContainer.setVisibility(View.GONE);
		}

		for (int i = 0; i < mGroupCnt; i++) {
			GroupDetail group = new GroupDetail();
			mGroups.add(group);
		}

		RecyclerView groupAList = dialog.findViewById(R.id.group_a);
		final PushedListAdapter groupAAdapter = new PushedListAdapter(mContext);
		groupAList.setAdapter(groupAAdapter);
		RecyclerView.LayoutManager lm = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
		groupAList.setLayoutManager(lm);

		RecyclerView groupBList = dialog.findViewById(R.id.group_b);
		final PushedListAdapter groupBAdapter = new PushedListAdapter(mContext);
		groupBList.setAdapter(groupBAdapter);
		lm = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
		groupBList.setLayoutManager(lm);

		RecyclerView groupCList = dialog.findViewById(R.id.group_c);
		final PushedListAdapter groupCAdapter = new PushedListAdapter(mContext);
		groupCList.setAdapter(groupCAdapter);
		lm = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
		groupCList.setLayoutManager(lm);

		RecyclerView groupDList = dialog.findViewById(R.id.group_d);
		final PushedListAdapter groupDAdapter = new PushedListAdapter(mContext);
		groupDList.setAdapter(groupDAdapter);
		lm = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
		groupDList.setLayoutManager(lm);


		RecyclerView candidateList = dialog.findViewById(R.id.candidates);
		final CandidateListAdapter adapter = new CandidateListAdapter(mContext);
		candidateList.setAdapter(adapter);
		lm = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
		candidateList.setLayoutManager(lm);

		adapter.setOnCandidateClickListener(new CandidateListAdapter.OnCandidateSelectListener() {
			@Override
			public void onCandidateClick(int index) {
				mCurSelectedIndex = index;
			}
		});

		groupAAdapter.setOnItemClickedListener(new PushedListAdapter.OnItemClickedListener() {
			@Override
			public void onItemClicked(MemberBean data) {
				groupAAdapter.pop(data);
                mGroups.get(0).mGroupMember.remove(data);

				int index = candidates.indexOf(data);
				adapter.onCandidatePop(index);
			}
		});

		groupBAdapter.setOnItemClickedListener(new PushedListAdapter.OnItemClickedListener() {
			@Override
			public void onItemClicked(MemberBean data) {
				groupBAdapter.pop(data);
                mGroups.get(1).mGroupMember.remove(data);

				int index = candidates.indexOf(data);
				adapter.onCandidatePop(index);
			}
		});

		groupCAdapter.setOnItemClickedListener(new PushedListAdapter.OnItemClickedListener() {
			@Override
			public void onItemClicked(MemberBean data) {
				groupCAdapter.pop(data);
                mGroups.get(2).mGroupMember.remove(data);

				int index = candidates.indexOf(data);
				adapter.onCandidatePop(index);
			}
		});

		groupDAdapter.setOnItemClickedListener(new PushedListAdapter.OnItemClickedListener() {
			@Override
			public void onItemClicked(MemberBean data) {
				groupDAdapter.pop(data);
                mGroups.get(3).mGroupMember.remove(data);

				int index = candidates.indexOf(data);
				adapter.onCandidatePop(index);
			}
		});

		groupAContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.i("Andy", "mCurSelectedIndex = " + mCurSelectedIndex);
				if (mCurSelectedIndex >= 0 && mCurSelectedIndex < candidates.size()) {
					MemberBean bean = candidates.get(mCurSelectedIndex);
					mGroups.get(0).mGroupMember.add(bean);
					adapter.onCandidatePush(mCurSelectedIndex);
					mCurSelectedIndex = -1;

					groupAAdapter.push(bean);
				}
			}
		});

		groupBContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mCurSelectedIndex >= 0 && mCurSelectedIndex < candidates.size()) {
					MemberBean bean = candidates.get(mCurSelectedIndex);
					mGroups.get(1).mGroupMember.add(bean);
					adapter.onCandidatePush(mCurSelectedIndex);
					mCurSelectedIndex = -1;

					groupBAdapter.push(bean);
				}
			}
		});

		groupCContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mCurSelectedIndex >= 0 && mCurSelectedIndex < candidates.size()) {
					MemberBean bean = candidates.get(mCurSelectedIndex);
					mGroups.get(2).mGroupMember.add(bean);
					adapter.onCandidatePush(mCurSelectedIndex);
					mCurSelectedIndex = -1;

					groupCAdapter.push(bean);
				}
			}
		});

		groupDContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mCurSelectedIndex >= 0 && mCurSelectedIndex < candidates.size()) {
					MemberBean bean = candidates.get(mCurSelectedIndex);
					mGroups.get(3).mGroupMember.add(bean);
					adapter.onCandidatePush(mCurSelectedIndex);
					mCurSelectedIndex = -1;

					groupDAdapter.push(bean);
				}
			}
		});

		if (candidates != null) {
			adapter.setData(candidates);
		}
	}

	private ArrayList<MemberBean> getAllCandidates() {
		if (mGameData == null || mGameData.mPlayers == null || mGameData.mPlayers.isEmpty()) {
			return null;
		}

		ArrayList<Long> playerIds = new ArrayList<>();
		ArrayList<MemberBean> extraPlayers = new ArrayList<>();
		for (PlayerDataBean data : mGameData.mPlayers) {
			if (data.mMemberId > 0) {
				playerIds.add(data.mMemberId);
			} else {
				MemberBean bean = MemberBean.buildExtraPlayer(getActivity(), data.mName);
				extraPlayers.add(bean);
			}
		}

		LongSparseArray<MemberBean> players = mModel.getMembersById(playerIds);
		if (players == null || players.size() == 0) {
			return null;
		}

		int playerCount = players.size();
		ArrayList<MemberBean> candidates = new ArrayList<>();
		for (int i = 0; i < playerCount; i++) {
			candidates.add(players.valueAt(i));
		}

		if (!extraPlayers.isEmpty()) {
			candidates.addAll(extraPlayers);
		}

		return candidates;
	}

	private void onGroupFinish() {
		if (mGroups == null || mGroups.isEmpty()) {
			return;
		}

		setData(mGroups);

		mBottomBar.setLeftButtonVisible(false);
		mBottomBar.setRightButtonVisible(true);
		if (mIsAutoGroup) {
			mBottomBar.setCenterButtonText(getString(R.string.text_redo_group));
		} else {
			mBottomBar.setCenterButtonVisible(false);
		}
	}

	private void doSaveGroup() {
		if (mGroups == null || mGroups.isEmpty()) {
			return;
		}

		for (GroupDetail group : mGroups) {
			mModel.addGroup(group, mGameData.mId);
		}
	}
}
