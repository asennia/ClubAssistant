package com.windfind.clubassistant.history;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.windfind.clubassistant.R;
import com.windfind.clubassistant.view.ListItemViewHolder;

import java.util.ArrayList;

class AttendanceListAdapter extends RecyclerView.Adapter<ListItemViewHolder> {

	private LayoutInflater mInflater;
	private ArrayList<Attendance> mAttendanceList;

	AttendanceListAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		mAttendanceList = new ArrayList<>();
	}

	public void setData(ArrayList<Attendance> data) {
		if (data == null || data.isEmpty()) {
			return;
		}

		mAttendanceList.clear();
		mAttendanceList.addAll(data);
		notifyDataSetChanged();
	}

	@SuppressLint("InflateParams")
	@Override
	public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View group = mInflater.inflate(R.layout.attendance_list_item, null);
		return new ListItemViewHolder(group);
	}

	@Override
	public void onBindViewHolder(ListItemViewHolder holder, int position) {
		ViewGroup vg = (ViewGroup) holder.mContent;
		Attendance attendance = mAttendanceList.get(position);

		TextView indexView = vg.findViewById(R.id.index);
		indexView.setText(String.valueOf(attendance.mIndex));

		TextView nameView = vg.findViewById(R.id.name);
		nameView.setText(attendance.mName);

		TextView attendanceView = vg.findViewById(R.id.attendance);
		attendanceView.setText(String.valueOf(attendance.mAttendance));
	}

	@Override
	public int getItemCount() {
		return mAttendanceList.size();
	}
}
