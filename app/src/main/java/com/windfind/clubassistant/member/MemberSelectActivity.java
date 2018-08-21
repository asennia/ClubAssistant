package com.windfind.clubassistant.member;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.windfind.clubassistant.CAApplication;
import com.windfind.clubassistant.DataModel;
import com.windfind.clubassistant.R;
import com.windfind.clubassistant.game.EditGameActivity;
import com.windfind.clubassistant.view.BottomBar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MemberSelectActivity extends AppCompatActivity {

	private DataModel mModel;
	private MemberSelectAdapter mAdapter;

	private BottomBar mBottomBar;
	private ViewGroup mNoneEmptyContainer;
	private ViewGroup mEmptyContainer;
	private Button mTopBtn;
	private boolean mIsAllSelected;

	public static final int MSG_SELECTED_CHANGED = 100;

	private static class MyHandler extends Handler {

		private WeakReference<MemberSelectActivity> mActivity;

		MyHandler(MemberSelectActivity activity) {
			mActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			MemberSelectActivity activity = mActivity.get();
			if (activity != null) {
				switch (msg.what) {
					case MSG_SELECTED_CHANGED:
						activity.onSelectedChanged(msg.arg1);
						break;
					default:
						break;
				}
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mModel = ((CAApplication) getApplicationContext()).getModel();
		MyHandler handler = new MyHandler(this);
		mAdapter = new MemberSelectAdapter(this, handler);

		setContentView(R.layout.member_select_layout);

		Toolbar toolbar = findViewById(R.id.toolbar);
		if (toolbar != null) {
			toolbar.setTitle("");
			setSupportActionBar(toolbar);
		}

		initView();
		initIntent();
	}

	private void initView() {
		mNoneEmptyContainer = findViewById(R.id.none_empty_content);
		mEmptyContainer = findViewById(R.id.empty_content);

		RecyclerView list = findViewById(R.id.member_list);
		if (list != null) {
			list.setAdapter(mAdapter);
			RecyclerView.LayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
			list.setLayoutManager(lm);
		}

		mBottomBar = findViewById(R.id.bottom_bar);
		mBottomBar.setLeftButtonListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				MemberSelectActivity.this.finish();
			}
		});
		updateBottomBar(0);
		mBottomBar.setRightButtonListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ArrayList<MemberBean> list = mAdapter.getSelected();
				if (list == null || list.isEmpty()) {
					setResult(RESULT_CANCELED);
				} else {
					Intent intent = new Intent();
					intent.putParcelableArrayListExtra(EditGameActivity.SELECTED_PLAYER_ID, list);
					setResult(RESULT_OK, intent);
				}

				MemberSelectActivity.this.finish();
			}
		});
		mBottomBar.setCenterButtonListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doAddExtraPlayer();
			}
		});

		mTopBtn = findViewById(R.id.btn_select_all);
		mTopBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mAdapter.setSelectAll(!mIsAllSelected);
				int count = !mIsAllSelected ? mAdapter.getItemCount() : 0;
				onSelectedChanged(count);
			}
		});
	}

	private void initIntent() {
		Intent intent = getIntent();
		if (intent == null) {
			return;
		}

		ArrayList<MemberBean> list = intent.getParcelableArrayListExtra(EditGameActivity.SELECTED_PLAYER_ID);
		if (list == null || list.isEmpty()) {
			return;
		}

		mAdapter.setPrevSelected(list);
	}

	@Override
	protected void onResume() {
		super.onResume();

		ArrayList<MemberBean> list = mModel.getAllMembers();
		if (list == null || list.isEmpty()) {
			mNoneEmptyContainer.setVisibility(View.GONE);
			mEmptyContainer.setVisibility(View.VISIBLE);
		} else {
			mNoneEmptyContainer.setVisibility(View.VISIBLE);
			mEmptyContainer.setVisibility(View.GONE);
			mAdapter.setData(list);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		ArrayList<MemberBean> list = mAdapter.getSelected();
		mAdapter.setPrevSelected(list);
	}

	private void onSelectedChanged(int selectedCount) {
		updateBottomBar(selectedCount);

		if (selectedCount == mAdapter.getItemCount() && !mIsAllSelected) {
			mIsAllSelected = true;
			mTopBtn.setText(R.string.text_unselect_all);
		} else if (selectedCount < mAdapter.getItemCount() && mIsAllSelected) {
			mIsAllSelected = false;
			mTopBtn.setText(R.string.text_select_all);
		}

		mAdapter.notifyDataSetChanged();
	}

	private void updateBottomBar(int count) {
		String text = getString(R.string.text_ok) + " ( " + count + " )";
		mBottomBar.setRightButtonText(text);
		mBottomBar.setRightButtonEnable(count > 0);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("InflateParams")
	private void doAddExtraPlayer() {
		LayoutInflater inflater = LayoutInflater.from(this);
		ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.add_extra_editor, null);
		final EditText input = vg.findViewById(R.id.extra_editor);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.text_add_extra);
		builder.setView(vg);
		builder.setNegativeButton(R.string.text_cancel, null);
		builder.setPositiveButton(R.string.text_add, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String name = (input.getText() == null || TextUtils.isEmpty(input.getText())) ?
						null : input.getText().toString();
				if (name !=null) {
					MemberBean player = MemberBean.buildExtraPlayer(MemberSelectActivity.this, name);
					mAdapter.addPlayer(player);
				}
			}
		});
		builder.create().show();
	}
}