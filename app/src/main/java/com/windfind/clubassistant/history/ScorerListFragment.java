package com.windfind.clubassistant.history;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.windfind.clubassistant.R;
import com.windfind.clubassistant.common.PageFragment;

import java.util.ArrayList;

public class ScorerListFragment extends PageFragment {

	private Context mContext;

	private ViewGroup mDataView;
	private ViewGroup mEmptyView;

	private ArrayList<Scorer> mScorerList;
	private ScorerListAdapter mAdapter;
	private boolean mIsReady = false;

	public static ScorerListFragment newInstance() {
		return new ScorerListFragment();
	}

	public ScorerListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = getContext().getApplicationContext();
		if (mScorerList == null) {
			mScorerList = new ArrayList<>();
		}
		mAdapter = new ScorerListAdapter(mContext);
		mIsReady = true;
	}

	@Override
	public int getTitleId() {
		return R.string.text_scorer_list;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.scorer_list, container, false);

		RecyclerView listView = rootView.findViewById(R.id.scorer_list);
		listView.setAdapter(mAdapter);
		RecyclerView.LayoutManager lm = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
		listView.setLayoutManager(lm);

		mDataView = rootView.findViewById(R.id.data_content);
		mEmptyView = rootView.findViewById(R.id.empty_content);

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();

		refreshList();
	}

	public void setData(ArrayList<Scorer> data) {
		if (data == null || data.isEmpty()) {
			return;
		}

		if (mScorerList == null) {
			mScorerList = new ArrayList<>();
		}
		mScorerList.clear();
		mScorerList.addAll(data);

		if (mIsReady) {
			refreshList();
		}
	}

	private void refreshList() {
		if (mScorerList == null || mScorerList.isEmpty()) {
			mEmptyView.setVisibility(View.VISIBLE);
			mDataView.setVisibility(View.GONE);
		} else {
			mEmptyView.setVisibility(View.GONE);
			mDataView.setVisibility(View.VISIBLE);
			mAdapter.setData(mScorerList);
		}
	}
}
