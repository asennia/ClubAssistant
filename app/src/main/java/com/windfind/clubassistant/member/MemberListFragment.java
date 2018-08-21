package com.windfind.clubassistant.member;

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
import com.windfind.clubassistant.R;
import com.windfind.clubassistant.common.PageFragment;

import java.util.ArrayList;

public class MemberListFragment extends PageFragment {

	private Context mContext;
	private RecyclerView mMemberList;
	private MemberListAdapter mAdapter;
	private DataModel mModel;
	private ViewGroup mEmptyContent;

	public MemberListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getContext().getApplicationContext();
		mModel = ((CAApplication) mContext).getModel();
		mAdapter = new MemberListAdapter(mContext);
		mAdapter.setActivity(getActivity());
	}

	@Override
	public int getTitleId() {
		return R.string.text_members;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.member_list, container, false);
		mMemberList = rootView.findViewById(R.id.member_container);
		mMemberList.setAdapter(mAdapter);
		LayoutManager lm = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
		mMemberList.setLayoutManager(lm);

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
		ArrayList<MemberBean> data = mModel.getAllMembers();
		int count = data.size();
		if (count == 0) {
			mMemberList.setVisibility(View.GONE);
			mEmptyContent.setVisibility(View.VISIBLE);
		} else {
			mMemberList.setVisibility(View.VISIBLE);
			mEmptyContent.setVisibility(View.GONE);
			mAdapter.setData(data);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)  {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_member_list, menu);
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
		Intent intent = new Intent(mContext, EditMemberActivity.class);
		intent.addFlags(EditMemberActivity.MODE_NEW);
		getActivity().startActivity(intent);
	}
}