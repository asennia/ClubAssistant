package com.windfind.clubassistant.game;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.windfind.clubassistant.CAApplication;
import com.windfind.clubassistant.DataModel;
import com.windfind.clubassistant.R;
import com.windfind.clubassistant.member.MemberBean;
import com.windfind.clubassistant.member.MemberSelectActivity;
import com.windfind.clubassistant.view.BottomBar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EditGameActivity extends AppCompatActivity {

	public static final String GAME_DATA = "game_data";
	public static final String MODE = "mode";
	public static final String SELECTED_PLAYER_ID = "selected_player_id";

	public static final int REQUEST_CODE_SELECT_PLAYER = 200;
	public static final int MODE_NEW = 0;
	public static final int MODE_EDIT = 1;

	private DataModel mModel;

	private BottomBar mBottomBar;
	private TextView mDateContent;
	private EditText mAddressContent;
	private TextView mTypeContent;
	private TextView mPlayerLabel;
	private TextView mPlayerContent;
	private EditText mTotalCostContent;
	private TextView mCostDetailContent;

	private Date mGameDate = new Date();

	private String[] mTypeList;
	private int mType = 1;

	private ArrayList<MemberBean> mSelectedPlayers;

	private String mCostDetailString = "水";

	private GameBean mGame;
	private int mMode;

	public static final int MSG_DATE_SELECTED = 100;
	public static final int MSG_TYPE_SELECTED = 101;
	public static final int MSG_PLAYER_SELECTED = 102;
	public static final int MSG_COST_DETAIL_READY = 103;

	private MyHandler mHandler;

	private static class MyHandler extends Handler {

		private WeakReference<EditGameActivity> mActivity;

		MyHandler(EditGameActivity activity) {
			mActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			EditGameActivity activity = mActivity.get();
			if (activity != null) {
				switch (msg.what) {
					case MSG_DATE_SELECTED:
						activity.onDateSelected();
						activity.checkAndUpdateBottomBar();
						break;
					case MSG_TYPE_SELECTED:
						activity.onGameTypeSelected();
						break;
					case MSG_PLAYER_SELECTED:
						activity.onPlayerSelected();
						activity.checkAndUpdateBottomBar();
						break;
					case MSG_COST_DETAIL_READY:
						activity.onCostDetailReady();
						break;
					default:
						break;
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mModel = ((CAApplication) getApplicationContext()).getModel();
		mHandler = new MyHandler(this);
		mTypeList = getResources().getStringArray(R.array.game_type_list);
		mSelectedPlayers = new ArrayList<>();

		Calendar calendar = Calendar.getInstance();
		mGameDate.setYear(calendar.get(Calendar.YEAR));
		mGameDate.setMonth(calendar.get(Calendar.MONTH));
		mGameDate.setDate(calendar.get(Calendar.DAY_OF_MONTH));

		setContentView(R.layout.edit_game_layout);

		initViews();

		Intent intent = getIntent();
		int mode = intent.getIntExtra(MODE, MODE_NEW);
		if (mode == MODE_EDIT) {
			mMode = MODE_EDIT;
			GameBean game = intent.getParcelableExtra(GAME_DATA);
			if (game == null || game.mId <= 0 || game.mPlayers == null || game.mPlayers.isEmpty()) {
				finish();
			} else {
				mGame = game;
				bindData();
				mBottomBar.setRightButtonEnable(true);
			}
		} else {
			mMode = MODE_NEW;
			setDefaultData();
		}
	}

	private void initViews() {
		initBottomBar();
		initContentView();
	}

	private void initBottomBar() {
		mBottomBar = findViewById(R.id.bottom_bar);
		mBottomBar.setLeftButtonListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditGameActivity.this.finish();
			}
		});

		mBottomBar.setRightButtonEnable(false);
		mBottomBar.setRightButtonListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doSaveGame();
				EditGameActivity.this.finish();
			}
		});
	}

	private void initContentView() {
		mDateContent = findViewById(R.id.date_content);
		mDateContent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doSelectDate();
			}
		});

		mAddressContent = findViewById(R.id.address_content);

		mTypeContent = findViewById(R.id.type_content);
		mTypeContent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doSelectGameType();
		}
		});

		mPlayerLabel = findViewById(R.id.Player_label);
		mPlayerContent = findViewById(R.id.Player_content);
		mPlayerContent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doSelectPlayer();
			}
		});

		mTotalCostContent = findViewById(R.id.total_cost_content);

		mCostDetailContent = findViewById(R.id.cost_detail_content);
		mCostDetailContent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doEditCostDetail();
			}
		});
	}

	private void setDefaultData() {
		String dateStr = generateDateString(mGameDate);
		mDateContent.setText(dateStr);

		mAddressContent.setText("诺宝");

		mTypeContent.setText(mTypeList[mType]);

		mCostDetailContent.setText(mCostDetailString);
	}

	private void bindData() {
		mGameDate = getDateFromDateString(mGame.mDate);
		mDateContent.setText(mGame.mDate);

		mAddressContent.setText(mGame.mAddress);

		mType = mGame.mType;
		mTypeContent.setText(mTypeList[mType]);

		mSelectedPlayers.clear();
		ArrayList<Long> ids = new ArrayList<>();
		ArrayList<MemberBean> extraPlayers = new ArrayList<>();
		for (PlayerDataBean playerData : mGame.mPlayers) {
			if (playerData.mMemberId > 0) {
				ids.add(playerData.mMemberId);
			} else {
				MemberBean bean = new MemberBean();
				bean.mId = playerData.mMemberId;
				bean.mName = playerData.mName;
				extraPlayers.add(bean);
			}
		}

		LongSparseArray<MemberBean> players = mModel.getMembersById(ids);
		for (int i = 0; i < players.size(); i++) {
			mSelectedPlayers.add(players.valueAt(i));
		}
		if (extraPlayers.size() > 0) {
			mSelectedPlayers.addAll(extraPlayers);
		}
		onPlayerSelected();

		String totalCost = mGame.mTotalCost + "";
		mTotalCostContent.setText(totalCost);

		mCostDetailString = mGame.mCostDetail;
		mCostDetailContent.setText(mGame.mCostDetail);
	}

	private void doSaveGame() {
		ArrayList<PlayerDataBean> players = new ArrayList<>();

		for (MemberBean bean : mSelectedPlayers) {
			PlayerDataBean player = new PlayerDataBean();

			player.mMemberId = bean.mId;
			player.mName = (bean.mId > 0) ? "" : bean.mName;
			player.mCost = 0;
			player.mGoals = 0;

			players.add(player);
		}

		GameBean game = new GameBean();

		game.mDate = mDateContent.getText().toString();
		game.mPlayers = players;
		game.mAddress = mAddressContent.getText().toString();
		game.mType = mType;
		if (TextUtils.isEmpty(mTotalCostContent.getText().toString())) {
			game.mTotalCost = 0;
		} else {
			game.mTotalCost = Float.parseFloat(mTotalCostContent.getText().toString());
		}
		game.mCostDetail = mCostDetailString;

		if (mMode == MODE_NEW) {
			mModel.addNewGame(game);
		} else {
			mModel.updateGame(mGame.mId, game);
		}
	}

	@SuppressWarnings("deprecation")
	private void doSelectDate() {
		DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker datePicker, int year, int month, int day) {
				mGameDate.setYear(year);
				mGameDate.setMonth(month);
				mGameDate.setDate(day);

				mHandler.sendEmptyMessage(MSG_DATE_SELECTED);
			}
		};

		int year = mGameDate.getYear();
		int month = mGameDate.getMonth();
		int day = mGameDate.getDate();
		DatePickerDialog dialog = new DatePickerDialog(this, listener, year, month, day);
		dialog.show();
	}

	private void onDateSelected() {
		String dateStr = generateDateString(mGameDate);
		mDateContent.setText(dateStr);
	}

	@SuppressWarnings("deprecation")
	public static String generateDateString(Date date) {
		if (date == null) {
			return "";
		}

		return date.getYear() + "." + (date.getMonth() + 1) + "." + date.getDate();
	}

	@SuppressWarnings("deprecation")
	public static Date getDateFromDateString(String dateString) {
		if (TextUtils.isEmpty(dateString)) {
			return null;
		}

		String[] children = TextUtils.split(dateString, "\\.");
		if (children == null || children.length != 3) {
			return null;
		}

		Date date = new Date();
		date.setYear(Integer.parseInt(children[0]));
		date.setMonth(Integer.parseInt(children[1]) - 1);
		date.setDate(Integer.parseInt(children[2]));

		return date;
	}

	private void doSelectGameType() {
		Builder builder = new Builder(this);
		builder.setTitle(R.string.text_type_select);
		builder.setSingleChoiceItems(mTypeList, mType, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				mType = i;
			}
		});
		builder.setPositiveButton(R.string.text_save, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				mHandler.sendEmptyMessage(MSG_TYPE_SELECTED);
			}
		});
		builder.create().show();
	}

	private void onGameTypeSelected() {
		mTypeContent.setText(mTypeList[mType]);
	}

	private void doSelectPlayer() {
		Intent intent = new Intent(this, MemberSelectActivity.class);
		intent.putParcelableArrayListExtra(SELECTED_PLAYER_ID, mSelectedPlayers);
		startActivityForResult(intent, REQUEST_CODE_SELECT_PLAYER);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_CODE_SELECT_PLAYER:
				if (resultCode == RESULT_OK && data != null) {
					ArrayList<MemberBean> list = data.getParcelableArrayListExtra(SELECTED_PLAYER_ID);
					if (list != null && !list.isEmpty()) {
						mSelectedPlayers.clear();
						mSelectedPlayers.addAll(list);

						mHandler.sendEmptyMessage(MSG_PLAYER_SELECTED);
					}
				}
				break;
			default:
				break;
		}
	}

	private void onPlayerSelected() {
		ArrayList<String> nameList = new ArrayList<>();
		for (MemberBean bean : mSelectedPlayers) {
			nameList.add(bean.mName);
		}

		String symbol = getString(R.string.text_join_symbol);
		String players = TextUtils.join(symbol, nameList);
		mPlayerContent.setText(players);

		String label = getString(R.string.text_players) + "(" + mSelectedPlayers.size() + ")";
		mPlayerLabel.setText(label);
	}

	@SuppressLint("InflateParams")
	private void doEditCostDetail() {
		LayoutInflater inflater = LayoutInflater.from(this);
		ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.cost_detail_editor, null);
		final EditText input = vg.findViewById(R.id.input_cost_detail);
		if (!TextUtils.isEmpty(mCostDetailString)) {
			input.setText(mCostDetailString);
		}

		Builder builder = new Builder(this);
		builder.setTitle(R.string.text_cost_detail);
		builder.setView(vg);
		builder.setPositiveButton(R.string.text_save, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				mCostDetailString = input.getText().toString();
				mHandler.sendEmptyMessage(MSG_COST_DETAIL_READY);
			}
		});
		builder.create().show();
	}

	private void onCostDetailReady() {
		mCostDetailContent.setText(mCostDetailString);
	}

	private boolean checkIsReady() {
		boolean isDateReady = !TextUtils.isEmpty(mDateContent.getText());
		boolean isPlayerReady = !TextUtils.isEmpty(mPlayerContent.getText());

		return isDateReady && isPlayerReady;
	}

	private void checkAndUpdateBottomBar() {
		if (checkIsReady()) {
			mBottomBar.setRightButtonEnable(true);
		} else {
			mBottomBar.setRightButtonEnable(false);
		}
	}
}