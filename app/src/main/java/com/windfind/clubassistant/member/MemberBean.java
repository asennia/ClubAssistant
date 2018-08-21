package com.windfind.clubassistant.member;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

import com.windfind.clubassistant.R;

import java.util.ArrayList;

public class MemberBean implements Parcelable{

	public  static final String WITH_ID = "id=";
	public  static final String WITH_NAME = "name=";

	static final int POS_FW = 1;
	static final int POS_WG = 2;
	static final int POS_DM = 4;
	static final int POS_DC = 8;
	static final int POS_GK = 16;

	public long mId;
	public String mName;
	public int mPosition;
	public boolean mIsVip;
	public float mAbility;

	public int mSpeed;
	public int mStrength;
	public int mDefence;
	public int mTech;
	public int mPass;
	public int mShoot;

	public MemberBean() {
	}

	public static MemberBean buildExtraPlayer(Context context, String name) {
		MemberBean bean = new MemberBean();
		bean.mId = -1;
		bean.mName = name;
		bean.mPosition = POS_GK;
		bean.mIsVip = false;
		bean.mAbility = context.getResources().getInteger(R.integer.default_ability_value);

		return bean;
	}

	protected MemberBean(Parcel in) {
		mId = in.readLong();
		mName = in.readString();
		mPosition = in.readInt();
		mIsVip = in.readByte() != 0;
		mAbility = in.readFloat();
		mSpeed = in.readInt();
		mStrength = in.readInt();
		mDefence = in.readInt();
		mTech = in.readInt();
		mPass = in.readInt();
		mShoot = in.readInt();
	}

	public static final Creator<MemberBean> CREATOR = new Creator<MemberBean>() {
		@Override
		public MemberBean createFromParcel(Parcel in) {
			return new MemberBean(in);
		}

		@Override
		public MemberBean[] newArray(int size) {
			return new MemberBean[size];
		}
	};

	public void calculateAbility() {
		int sum = mSpeed + mStrength + mDefence + mTech + mPass + mShoot;
		mAbility = sum / 6;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(mId);
		dest.writeString(mName);
		dest.writeInt(mPosition);
		dest.writeByte((byte) (mIsVip ? 1 : 0));
		dest.writeFloat(mAbility);
		dest.writeInt(mSpeed);
		dest.writeInt(mStrength);
		dest.writeInt(mDefence);
		dest.writeInt(mTech);
		dest.writeInt(mPass);
		dest.writeInt(mShoot);
	}

	ArrayList<String> getPosStringArray(Resources res) {
		if (res == null) {
			return new ArrayList<>();
		}

		int[] positions = {
				MemberBean.POS_FW,
				MemberBean.POS_WG,
				MemberBean.POS_DM,
				MemberBean.POS_DC,
				MemberBean.POS_GK};

		int[] posValue = {
				R.string.text_pos_fw,
				R.string.text_pos_wg,
				R.string.text_pos_dm,
				R.string.text_pos_dc,
				R.string.text_pos_gk};

		ArrayList<String> posArray = new ArrayList<>();

		for (int i = 0; i < positions.length; i++) {
			if ((mPosition & positions[i]) == positions[i]) {
				posArray.add(res.getString(posValue[i]));
			}
		}

		return posArray;
	}
}