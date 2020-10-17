package com.windfind.clubassistant;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.windfind.clubassistant.common.CommonPagerAdapter;
import com.windfind.clubassistant.game.GameListFragment;
import com.windfind.clubassistant.history.HistoryListFragment;
import com.windfind.clubassistant.member.MemberListFragment;

public class MainActivity extends AppCompatActivity {

	public static final int ACTION_IMPORT = 1;
	public static final int ACTION_EXPORT = 2;

	private DrawerLayout mDrawer;
	private BottomNavigationView mBottomBar;
	private ViewPager mViewPager;
	private CommonPagerAdapter mAdapter;

	private String mVersionCode = "Unknown";
	private boolean mIsScrolledByUser;

	OnNavigationItemSelectedListener  mListener = new OnNavigationItemSelectedListener() {
		@Override
		public boolean onNavigationItemSelected(@NonNull MenuItem item) {
			boolean ret = false;

			switch (item.getItemId()) {
				case R.id.tab_member:
					mViewPager.setCurrentItem(0);
					ret = true;
					break;
				case R.id.tab_game:
					mViewPager.setCurrentItem(1);
					ret = true;
					break;
				case R.id.tab_record:
					mViewPager.setCurrentItem(2);
					ret = true;
					break;
			}

			return ret;
		}
	};

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Context context = getApplicationContext();
		setContentView(R.layout.activity_main);
		mDrawer = findViewById(R.id.main_content);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
		mDrawer.setDrawerListener(toggle);
		toggle.syncState();

		mBottomBar = findViewById(R.id.bottom_bar);
		mBottomBar.setOnNavigationItemSelectedListener(mListener);

		mAdapter = new CommonPagerAdapter(context, getSupportFragmentManager());
		mViewPager = findViewById(R.id.container);
		mViewPager.setAdapter(mAdapter);
		mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				if (!mIsScrolledByUser) {
					return;
				}
				switch (position) {
					case 0:
						mBottomBar.setSelectedItemId(R.id.tab_member);
						break;
					case 1:
						mBottomBar.setSelectedItemId(R.id.tab_game);
						break;
					case 2:
						mBottomBar.setSelectedItemId(R.id.tab_record);
						break;
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				switch (state) {
					case ViewPager.SCROLL_STATE_IDLE:
						mIsScrolledByUser = false;
						break;
					case ViewPager.SCROLL_STATE_DRAGGING:
						mIsScrolledByUser = true;
						break;
				}
			}
		});

		WindowManager wm = getWindowManager();
		CAApplication.sDeviceWidth = wm.getDefaultDisplay().getWidth();

		createAndSetFragments();

		View btnImport = findViewById(R.id.menu_import);
		btnImport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mDrawer.closeDrawer(GravityCompat.START, false);
				startPortingData(ACTION_IMPORT);
			}
		});

        View btnExport = findViewById(R.id.menu_export);
		btnExport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mDrawer.closeDrawer(GravityCompat.START, false);
				startPortingData(ACTION_EXPORT);
			}
		});

		getVersionCode(getApplicationContext());
		TextView verCodeView = findViewById(R.id.main_title_view);
		String value = getString(R.string.text_drawer_main_title, mVersionCode);
		verCodeView.setText(value);
	}

	private void createAndSetFragments() {
		MemberListFragment memberList = MemberListFragment.newInstance();
		GameListFragment gameList = GameListFragment.newInstance();
		HistoryListFragment recordList = HistoryListFragment.newInstance();

		mAdapter.addItem(memberList);
		mAdapter.addItem(gameList);
		mAdapter.addItem(recordList);

		mAdapter.notifyDataSetChanged();
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