package com.windfind.clubassistant;

import android.app.Application;

public class CAApplication extends Application {

	private DataModel mModel;
	public static int sDeviceWidth;

	@Override
	public void onCreate() {
		super.onCreate();
		mModel = new DataModel(this);
	}

	public DataModel getModel() {
		return mModel;
	}
}
