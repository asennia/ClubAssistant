package com.windfind.clubassistant.game;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.windfind.clubassistant.CAApplication;
import com.windfind.clubassistant.DataModel;
import com.windfind.clubassistant.R;
import com.windfind.clubassistant.common.CommonPagerAdapter;

public class GameDetailActivity extends AppCompatActivity {

	public static final String GAME_ID = "game_id";

	private DataModel mModel;
	private GameBean mCurGame;
	private long mCurGameId;
	private GameDetailPlayerListFragment mPlayerFragment;
	private GameDetailGroupListFragment mGroupFragment;

	private TextView mDateView;
	private TextView mAddressView;
	private TextView mGameTypeView;
	private TextView mTotalCostView;
	private TextView mBtnShowCostDetail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mModel = ((CAApplication) getApplicationContext()).getModel();
		setContentView(R.layout.game_detail_layout);

		Toolbar toolbar = findViewById(R.id.toolbar);
		if (toolbar != null) {
			toolbar.setTitle("");
			setSupportActionBar(toolbar);
		}

		mGroupFragment = GameDetailGroupListFragment.newInstance();
		mPlayerFragment = GameDetailPlayerListFragment.newInstance();

		initData();
		initViews();
	}

	private void initViews() {
		Button btnEdit = findViewById(R.id.btn_edit);
		if (btnEdit != null) {
			btnEdit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(GameDetailActivity.this, EditGameActivity.class);
					intent.putExtra(EditGameActivity.GAME_DATA, mCurGame);
					intent.putExtra(EditGameActivity.MODE, EditGameActivity.MODE_EDIT);
					startActivity(intent);
				}
			});
		}

		mDateView = findViewById(R.id.date_content);
		mAddressView = findViewById(R.id.address_content);
		mGameTypeView = findViewById(R.id.game_type_content);
		mTotalCostView = findViewById(R.id.total_cost_content);
		mBtnShowCostDetail = findViewById(R.id.btn_show_reader_detail);
		mBtnShowCostDetail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showCostDetail();
			}
		});

		CommonPagerAdapter pageAdapter = new CommonPagerAdapter(this, getSupportFragmentManager());
		ViewPager viewPager = findViewById(R.id.pager);
		if (viewPager != null) {
			viewPager.setAdapter(pageAdapter);
		}

		TabLayout tabLayout = findViewById(R.id.tabs);
		if (tabLayout != null) {
			tabLayout.setupWithViewPager(viewPager);
		}
		pageAdapter.addItem(mPlayerFragment);
		pageAdapter.addItem(mGroupFragment);
		pageAdapter.notifyDataSetChanged();
	}

	private void initData() {
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}

		long id = intent.getLongExtra(GAME_ID, -1);
		if (id == -1) {
			finish();
			return;
		}

		mCurGameId = id;
	}

	@Override
	protected void onResume() {
		super.onResume();

		mCurGame = mModel.getGameById(mCurGameId);
		if (mCurGame == null || mCurGame.mPlayers == null || mCurGame.mPlayers.isEmpty()) {
			finish();
			return;
		}

		mGroupFragment.setGameData(mCurGame);

		updateViews();

		mPlayerFragment.setData(mCurGame.mPlayers);
	}

	private void updateViews() {
		mDateView.setText(mCurGame.mDate);
		mAddressView.setText(mCurGame.mAddress);
		mGameTypeView.setText(mCurGame.getTypeStringId());

		String totalCost = "￥" + mCurGame.mTotalCost;
		mTotalCostView.setText(totalCost);

		int visible = (mCurGame.mTotalCost == 0) ? View.INVISIBLE : View.VISIBLE;
		mBtnShowCostDetail.setVisibility(visible);
	}

	private void showCostDetail() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.text_cost_detail);
		builder.setMessage(mCurGame.mCostDetail);
		builder.setPositiveButton(R.string.text_cancel, null);
		builder.create().show();
	}
}