package com.windfind.clubassistant.game;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.windfind.clubassistant.CAApplication;
import com.windfind.clubassistant.DataModel;
import com.windfind.clubassistant.common.PageFragment;
import com.windfind.clubassistant.R;
import com.windfind.clubassistant.member.EditMemberActivity;
import com.windfind.clubassistant.member.MemberBean;

import java.util.ArrayList;

public class GameListFragment extends PageFragment {

	private Context mContext;
	private RecyclerView mGameList;
	private GameListAdapter mAdapter;
	private ViewGroup mEmptyContent;
	private DataModel mModel;

	public GameListFragment() {
	}

	public static GameListFragment newInstance() {
		return new GameListFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getContext().getApplicationContext();
		mModel = ((CAApplication) mContext).getModel();
		mAdapter = new GameListAdapter(mContext);
		mAdapter.setActivity(getActivity());
	}

	@Override
	public int getTitleId() {
		return R.string.text_games;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.game_list, container, false);
		mGameList = rootView.findViewById(R.id.game_container);
		mGameList.setAdapter(mAdapter);
		LayoutManager lm = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
		mGameList.setLayoutManager(lm);

		mEmptyContent = rootView.findViewById(R.id.empty_content);
		setHasOptionsMenu(true);
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();

		refreshList();
	}

	public void refreshList() {
		ArrayList<GameBean> gameList = mModel.getAllGames();
		if (gameList != null && !gameList.isEmpty()) {
			ArrayList<MemberBean> memberList = mModel.getAllMembers();
			mAdapter.setMemberData(memberList);

			mGameList.setVisibility(View.VISIBLE);
			mEmptyContent.setVisibility(View.GONE);
			mAdapter.setGameData(gameList);
		} else {
			mGameList.setVisibility(View.GONE);
			mEmptyContent.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)  {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_game_list, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch (id) {
			case R.id.action_add:
				onDoAddAction();
				break;
			default:
				break;
		}

		return true;
	}

	private void onDoAddAction() {
		Intent intent = new Intent(mContext, EditGameActivity.class);
		intent.addFlags(EditMemberActivity.MODE_NEW);
		getActivity().startActivity(intent);
	}
}
