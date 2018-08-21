package com.windfind.clubassistant;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.windfind.clubassistant.common.CommonPagerAdapter;
import com.windfind.clubassistant.common.MyBottomBar;
import com.windfind.clubassistant.game.GameListFragment;
import com.windfind.clubassistant.history.HistoryListFragment;
import com.windfind.clubassistant.member.MemberListFragment;

public class MainActivity extends AppCompatActivity {

	public static final int ACTION_IMPORT = 1;
	public static final int ACTION_EXPORT = 2;

	private DrawerLayout mDrawer;
	private MyBottomBar mBottomBar;

	private String mVersionCode = "Unknown";

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		mDrawer = findViewById(R.id.main_content);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
		mDrawer.setDrawerListener(toggle);
		toggle.syncState();

		mBottomBar = findViewById(R.id.bottom_bar);
		mBottomBar.setContainer(R.id.fl_container);
		mBottomBar.setTitleBeforeAndAfterColor("#333333", "#ff3d3e");

		WindowManager wm = getWindowManager();
		CAApplication.sDeviceWidth = wm.getDefaultDisplay().getWidth();

		createAndSetFragments();

		Button btnImport = findViewById(R.id.btn_import);
		btnImport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mDrawer.closeDrawer(GravityCompat.START, false);
				startPortingData(ACTION_IMPORT);
			}
		});

		Button btnExport = findViewById(R.id.btn_export);
		btnExport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mDrawer.closeDrawer(GravityCompat.START, false);
				startPortingData(ACTION_EXPORT);
			}
		});

		getVersionCode(getApplicationContext());
		TextView verCodeView = findViewById(R.id.version_code);
		verCodeView.setText(mVersionCode);
	}

	private void createAndSetFragments() {
		mBottomBar.addItem(
				MemberListFragment.class,
				getString(R.string.text_members),
				R.drawable.item1_before,
				R.drawable.item1_after);
		mBottomBar.addItem(
				GameListFragment.class,
				getString(R.string.text_games),
				R.drawable.item2_before,
				R.drawable.item2_after);
		mBottomBar.addItem(
				HistoryListFragment.class,
				getString(R.string.text_history),
				R.drawable.item3_before,
				R.drawable.item3_after);

		mBottomBar.build();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		if (mDrawer.isDrawerOpen(GravityCompat.START)) {
			mDrawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	public void startPortingData(int action) {
		Intent intent;
		switch (action) {
			case ACTION_IMPORT:
			case ACTION_EXPORT:
				intent = new Intent(this, DataPortActivity.class);
				intent.putExtra("Action", action);
				startActivity(intent);
				break;
			default:
				break;
		}
	}

	private void getVersionCode(Context context) {
		try {
			mVersionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
	}
}