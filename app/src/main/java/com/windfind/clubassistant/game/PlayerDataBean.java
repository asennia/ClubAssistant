package com.windfind.clubassistant.game;

import android.os.Parcel;
import android.os.Parcelable;

public class PlayerDataBean implements Parcelable {

	public long mId;
	public long mGameId;
	public long mMemberId;
	public String mName;   // Available only while mMemberId is -1
	public float mCost;
	public int mGoals;

	public PlayerDataBean() {
	}

	private PlayerDataBean(Parcel in) {
		mId = in.readLong();
		mGameId = in.readLong();
		mMemberId = in.readLong();
		mName = in.readString();
		mCost = in.readFloat();
		mGoals = in.readInt();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(mId);
		dest.writeLong(mGameId);
		dest.writeLong(mMemberId);
		dest.writeString(mName);
		dest.writeFloat(mCost);
		dest.writeInt(mGoals);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<PlayerDataBean> CREATOR = new Creator<PlayerDataBean>() {
		@Override
		public PlayerDataBean createFromParcel(Parcel in) {
			return new PlayerDataBean(in);
		}

		@Override
		public PlayerDataBean[] newArray(int size) {
			return new PlayerDataBean[size];
		}
	};
}