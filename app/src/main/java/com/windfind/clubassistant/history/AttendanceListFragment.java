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

public class AttendanceListFragment extends PageFragment {

	private Context mContext;
	private ArrayList<Attendance> mAttendanceList;
	private AttendanceListAdapter mAdapter;

	private ViewGroup mDataView;
	private ViewGroup mEmptyView;

	private boolean mIsReady = false;

	public static AttendanceListFragment newInstance() {
		return new AttendanceListFragment();
	}

	public AttendanceListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = getContext().getApplicationContext();
		if (mAttendanceList == null) {
			mAttendanceList = new ArrayList<>();
		}
		mAdapter = new AttendanceListAdapter(mContext);
		mIsReady = true;
	}

	@Override
	public int getTitleId() {
		return R.string.text_attendance_list;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.attendance_list, container, false);

		RecyclerView listView = rootView.findViewById(R.id.attendance_list);
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

	private void refreshList() {
		if (mAttendanceList == null || mAttendanceList.isEmpty()) {
			mEmptyView.setVisibility(View.VISIBLE);
			mDataView.setVisibility(View.GONE);
		} else {
			mEmptyView.setVisibility(View.GONE);
			mDataView.setVisibility(View.VISIBLE);
			mAdapter.setData(mAttendanceList);
		}
	}

	public void setData(ArrayList<Attendance> data) {
		if (data == null || data.isEmpty()) {
			return;
		}

		if (mAttendanceList == null) {
			mAttendanceList = new ArrayList<>();
		}
		mAttendanceList.clear();
		mAttendanceList.addAll(data);

		if (mIsReady) {
			refreshList();
		}
	}
}
