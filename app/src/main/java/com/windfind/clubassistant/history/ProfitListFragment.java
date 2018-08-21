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

public class ProfitListFragment extends PageFragment {

	private Context mContext;
	private ArrayList<ProfitForGame> mProfitList;
	private ProfitListAdapter mAdapter;
	private boolean mIsReady = false;

	private ViewGroup mDataView;
	private ViewGroup mEmptyView;

	public static ProfitListFragment newInstance() {
		return new ProfitListFragment();
	}

	public ProfitListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = getContext().getApplicationContext();
		if (mProfitList == null) {
			mProfitList = new ArrayList<>();
		}
		mAdapter = new ProfitListAdapter(mContext);

		mIsReady = true;
	}

	@Override
	public int getTitleId() {
		return R.string.text_profit_detail;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.profit_list, container, false);

		RecyclerView listView = rootView.findViewById(R.id.profit_list);
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

	public void setData(ArrayList<ProfitForGame> data) {
		if (data == null || data.isEmpty()) {
			return;
		}

		if (mProfitList == null) {
			mProfitList = new ArrayList<>();
		}
		mProfitList.clear();
		mProfitList.addAll(data);

		if (mIsReady) {
			refreshList();
		}
	}

	private void refreshList() {
		if (mProfitList == null || mProfitList.isEmpty()) {
			mEmptyView.setVisibility(View.VISIBLE);
			mDataView.setVisibility(View.GONE);
		} else {
			mEmptyView.setVisibility(View.GONE);
			mDataView.setVisibility(View.VISIBLE);
			mAdapter.setData(mProfitList);
		}
	}
}
