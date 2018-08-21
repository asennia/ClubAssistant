package com.windfind.clubassistant.game;

import android.os.Parcel;
import android.os.Parcelable;

import com.windfind.clubassistant.member.MemberBean;

import java.util.ArrayList;

public class GroupDetail implements Parcelable{
	public int mGroupIndex;
	public ArrayList<MemberBean> mGroupMember;

	public GroupDetail() {
		mGroupMember = new ArrayList<>();
	}

	private GroupDetail(Parcel in) {
		mGroupIndex = in.readInt();
		mGroupMember = in.createTypedArrayList(MemberBean.CREATOR);
	}

	public static final Creator<GroupDetail> CREATOR = new Creator<GroupDetail>() {
		@Override
		public GroupDetail createFromParcel(Parcel in) {
			return new GroupDetail(in);
		}

		@Override
		public GroupDetail[] newArray(int size) {
			return new GroupDetail[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mGroupIndex);
		dest.writeTypedList(mGroupMember);
	}
}
