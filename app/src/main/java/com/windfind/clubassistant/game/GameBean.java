package com.windfind.clubassistant.game;

import android.os.Parcel;
import android.os.Parcelable;

import com.windfind.clubassistant.R;

import java.util.ArrayList;

public class GameBean implements Parcelable {

	private static final int TYPE_TRAIN = 1;
	private static final int TYPE_MATCH = 2;

	public long mId;
	public String mDate;
	public ArrayList<PlayerDataBean> mPlayers;
	public String mAddress;
	public int mType;
	public float mTotalCost;
	public String mCostDetail;

	public GameBean() {
		mPlayers = new ArrayList<>();
	}

	private GameBean(Parcel in) {
		mId = in.readLong();
		mDate = in.readString();
		mPlayers = in.createTypedArrayList(PlayerDataBean.CREATOR);
		mAddress = in.readString();
		mType = in.readInt();
		mTotalCost = in.readFloat();
		mCostDetail = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(mId);
		dest.writeString(mDate);
		dest.writeTypedList(mPlayers);
		dest.writeString(mAddress);
		dest.writeInt(mType);
		dest.writeFloat(mTotalCost);
		dest.writeString(mCostDetail);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<GameBean> CREATOR = new Creator<GameBean>() {
		@Override
		public GameBean createFromParcel(Parcel in) {
			return new GameBean(in);
		}

		@Override
		public GameBean[] newArray(int size) {
			return new GameBean[size];
		}
	};

	int getTypeStringId() {
		int ret = R.string.text_game_type_unknown;

		switch (mType) {
			case TYPE_TRAIN:
				ret = R.string.text_game_type_train;
				break;
			case TYPE_MATCH:
				ret = R.string.text_game_type_match;
				break;
			default:
				break;
		}

		return ret;
	}
}