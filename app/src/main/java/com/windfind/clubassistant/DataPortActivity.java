package com.windfind.clubassistant;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.SparseArray;
import android.widget.TextView;
import android.widget.Toast;

import com.windfind.clubassistant.game.GameBean;
import com.windfind.clubassistant.game.GroupDetail;
import com.windfind.clubassistant.game.PlayerDataBean;
import com.windfind.clubassistant.member.MemberBean;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public class DataPortActivity extends AppCompatActivity {

	public static final String DIR_NAME = "ClubAssistant";
	public static final String DIR_NAME_1 = "ClubAssistant/";
	public static final String MEMBER_FILE_NAME = "member";
	public static final String GAME_FILE_NAME = "game";

	public static final String LABEL_MEMBER = "member";
	public static final String LABEL_GAME = "game";
	public static final String LABEL_PLAYER_DATA = "player_data";
	public static final String LABEL_GROUP = "group";

	public static final int REQ_CODE_READ_EXTERNAL_STORAGE = 1;

	enum MemberDataIndex {Label, Name, Position, IsVip, Speed, Strength, Defence, Tech, Pass, Shoot}
	@SuppressWarnings("unused")
	enum GameDataIndex {Label, Date, Address, Type, TotalCost, CostDetail}
	@SuppressWarnings("unused")
	enum PlayerDataIndex {Label, GameId, MemberId, Name, Cost, Goals}
	@SuppressWarnings("unused")
	enum GroupDataIndex {Label, GameId, GroupIndex, PlayerCount}

	private DataModel mModel;
	private MyHandler mHandler;
	private Intent mIntent;
	private Runnable mActionRunnable;

	private TextView mTitleView;
	private TextView mProgressTextView;
	private StringBuffer mProgressText;

	public static final int MSG_NO_PERMISSION = 100;
	public static final int MSG_DATA_NOT_FOUND = 101;
	public static final int MSG_STORAGE_NOT_READY = 102;
	public static final int MSG_PORT_FINISH = 103;
	public static final int MSG_STEP_TO = 104;

	private static class MyHandler extends Handler {
		private WeakReference<DataPortActivity> mActivity;

		MyHandler(DataPortActivity activity) {
			mActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			DataPortActivity activity = mActivity.get();
			if (activity != null) {
				switch (msg.what) {
					case MSG_NO_PERMISSION:
					case MSG_DATA_NOT_FOUND:
					case MSG_STORAGE_NOT_READY:
						activity.noticeError(msg.what);
						activity.finish();
						break;
					case MSG_STEP_TO:
						String step = msg.getData().getString("step");
						activity.flushStep(step);
						break;
					case MSG_PORT_FINISH:
						activity.finish();
						break;
					default:
						break;
				}
			}
		}
	}

	private void noticeError(int error) {
		int strId = -1;
		switch (error) {
			case MSG_NO_PERMISSION:
				strId = R.string.text_can_not_read_storage;
				break;
			case MSG_DATA_NOT_FOUND:
				strId = R.string.text_data_not_found;
				break;
			case MSG_STORAGE_NOT_READY:
				strId = R.string.text_storage_not_ready;
				break;
			default:
				break;
		}

		if (strId > 0) {
			Toast.makeText(this, strId, Toast.LENGTH_SHORT).show();
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mModel = ((CAApplication) getApplicationContext()).getModel();
		mHandler = new MyHandler(this);

		setContentView(R.layout.activity_data_port);
		initView();

		mIntent = getIntent();
		if (mIntent != null) {
			int action = mIntent.getIntExtra("Action", -1);
			switch (action) {
				case MainActivity.ACTION_EXPORT:
					mTitleView.setText(R.string.text_data_export);
					break;
				case MainActivity.ACTION_IMPORT:
					mTitleView.setText(R.string.text_data_import);
					break;
				default:
					break;
			}
		}
	}

	private void initView() {
		mTitleView = findViewById(R.id.title);
		mProgressTextView = findViewById(R.id.progress_text);
		mProgressText = new StringBuffer("");
	}

	private void stepTo(String step) {
		Bundle bundle = new Bundle();
		bundle.putString("step", step);

		Message msg = new Message();
		msg.what = MSG_STEP_TO;
		msg.setData(bundle);

		mHandler.sendMessage(msg);
	}

	private void flushStep(String step) {
		mProgressText.append("\r\n").append(step);
		mProgressTextView.setText(mProgressText.toString());
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mIntent != null) {
			int action = mIntent.getIntExtra("Action", -1);
			switch (action) {
				case MainActivity.ACTION_EXPORT:
					onDoExportAction();
					mIntent = null;
					break;
				case MainActivity.ACTION_IMPORT:
					onDoImportAction();
					mIntent = null;
					break;
				default:
					break;
			}
		}
	}

	private void checkPermission(Runnable r) {
		stepTo("检测是否已获取读取文件系统的权限...");
		int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
		if (result != PackageManager.PERMISSION_GRANTED) {
			stepTo("尚未获取读取文件系统的权限，需要进行申请.");
			mActionRunnable = r;
			stepTo("申请权限...");
			ActivityCompat.requestPermissions(this,
					new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
					REQ_CODE_READ_EXTERNAL_STORAGE);
		} else {
			stepTo("已获取读取文件系统的权限.");
			Thread thread = new Thread(r);
			thread.start();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
										   @NonNull int[] grantResults) {
		switch (requestCode) {
			case REQ_CODE_READ_EXTERNAL_STORAGE:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					if (mActionRunnable != null) {
						stepTo("权限获取成功.");
						Thread thread = new Thread(mActionRunnable);
						thread.start();
						mActionRunnable = null;
					}
				} else {
					stepTo("权限获取失败.");
					mHandler.sendEmptyMessage(MSG_NO_PERMISSION);
				}
				break;
			default:
				break;
		}
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	private void onDoExportAction() {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				stepTo("检测存储设备是否已经初始化完成...");
				String state = Environment.getExternalStorageState();
				if (Environment.MEDIA_MOUNTED.equals(state)) {
					stepTo("存储设备已经初始化完成.");
					File rootDir = Environment.getExternalStorageDirectory();
					File dir = new File(rootDir, DIR_NAME);
					if (!dir.exists()) {
						stepTo("创建数据目录...");
						dir.mkdir();
					}

					// 导出球员列表
					stepTo("开始导出成员数据...");
					ArrayList<MemberBean> memberList = mModel.getAllMembers();
					if (memberList != null && !memberList.isEmpty()) {
						File memberFile = new File(dir, MEMBER_FILE_NAME);
						if (!memberFile.exists()) {
							try {
								memberFile.createNewFile();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

						try {
							FileOutputStream fos = new FileOutputStream(memberFile.getCanonicalPath(), false);
							OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
							BufferedWriter bw = new BufferedWriter(osw);

							for (MemberBean member : memberList) {
								String content = LABEL_MEMBER +
										"," + member.mName +
										"," + member.mPosition +
										"," + (member.mIsVip ? 1 : 0) +
										"," + member.mSpeed +
										"," + member.mStrength +
										"," + member.mDefence +
										"," + member.mTech +
										"," + member.mPass +
										"," + member.mShoot +
										"\r\n";
								bw.write(content);
							}

							bw.close();
							osw.close();
							fos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					stepTo("导出成员数据完成.");

					// 导出活动列表
					stepTo("开始导出活动数据...");
					ArrayList<GameBean> gameList = mModel.getAllGames();
					if (gameList != null && !gameList.isEmpty()) {
						for (GameBean game : gameList) {
							String fileName = GAME_FILE_NAME + game.mId;
							File gameFile = new File(dir, fileName);
							if (!gameFile.exists()) {
								try {
									gameFile.createNewFile();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}

							try {
								FileOutputStream fos = new FileOutputStream(gameFile.getCanonicalPath(), false);
								OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
								BufferedWriter bw = new BufferedWriter(osw);

								StringBuilder content = new StringBuilder(LABEL_GAME +
										"," + game.mDate +
										"," + game.mAddress +
										"," + game.mType +
										"," + game.mTotalCost +
										"," + game.mCostDetail +
										"\r\n");
								bw.write(content.toString());

								ArrayList<PlayerDataBean> playerDataList = mModel.getPlayerDataByGame(game.mId);
								if (playerDataList != null && !playerDataList.isEmpty()) {
									for (PlayerDataBean playerData : playerDataList) {
										content = new StringBuilder(LABEL_PLAYER_DATA +
												"," + playerData.mGameId +
												"," + playerData.mMemberId +
												"," + playerData.mName +
												"," + playerData.mCost +
												"," + playerData.mGoals +
												"\r\n");
										bw.write(content.toString());
									}
								}

								ArrayList<GroupDetail> groupList = mModel.getGroupsByGame(game.mId);
								if (groupList != null && !groupList.isEmpty()) {
									for (GroupDetail group : groupList) {
										content = new StringBuilder(LABEL_GROUP +
												"," + game.mId +
												"," + group.mGroupIndex +
												"," + group.mGroupMember.size());
										for (MemberBean player : group.mGroupMember) {
											if (player == null) {
												continue;
											}

											if (player.mId >= 0) {
												content.append(",id=").append(player.mId);
											} else {
												content.append(",name=").append(player.mName);
											}
										}
										content.append("\r\n");
										bw.write(content.toString());
									}
								}

								bw.close();
								osw.close();
								fos.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					stepTo("导出活动数据完成.");

					mHandler.sendEmptyMessage(MSG_PORT_FINISH);
				} else {
					stepTo("存储设备尚未初始化.");
					mHandler.sendEmptyMessage(MSG_STORAGE_NOT_READY);
				}
			}
		};

		checkPermission(r);
	}

	private void onDoImportAction() {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				stepTo("检测存储设备是否已经初始化完成...");
				String state = Environment.getExternalStorageState();
				if (Environment.MEDIA_MOUNTED.equals(state)) {
					stepTo("存储设备已经初始化完成.");
					stepTo("读取数据目录...");
					File rootDir = Environment.getExternalStorageDirectory();
					File dir = new File(rootDir, DIR_NAME_1);
					if (!dir.exists()) {
						stepTo("未发现数据目录.");
						mHandler.sendEmptyMessage(MSG_DATA_NOT_FOUND);
						return;
					}

					stepTo("读取数据文件...");
					File[] fileList = dir.listFiles();
					if (fileList == null || fileList.length == 0) {
						stepTo("未发现数据文件.");
						mHandler.sendEmptyMessage(MSG_DATA_NOT_FOUND);
						return;
					}

					stepTo("清除原有数据...");
					mModel.clearMember();
					mModel.clearGame();
					mModel.clearPlayerData();
					mModel.clearGroup();

					HashMap<String, File> fileMap = new HashMap<>();
					SparseArray<String> fileNameSparseArray = new SparseArray<>();
					for (File f : fileList) {
						String name = f.getName();

						if (MEMBER_FILE_NAME.equals(name)) {
							stepTo("开始导入成员数据...");
							importMemberData(f);
							stepTo("导入成员数据完成.");
						} else if (name.contains(GAME_FILE_NAME)) {
							fileMap.put(name, f);
							String idxStr = name.replaceAll(GAME_FILE_NAME, "");
							int idx = Integer.valueOf(idxStr);
							fileNameSparseArray.put(idx, name);
						}
					}

					stepTo("开始导入活动数据...");
					for (int i = 1; i <= fileNameSparseArray.size(); i++) {
						String name = fileNameSparseArray.get(i);
						if (TextUtils.isEmpty(name)) {
							continue;
						}

						File f = fileMap.get(name);
						if (f == null) {
							continue;
						}

						importGameData(f);
					}
					stepTo("导入活动数据完成.");

					mHandler.sendEmptyMessage(MSG_PORT_FINISH);
				} else {
					stepTo("存储设备尚未初始化.");
					mHandler.sendEmptyMessage(MSG_STORAGE_NOT_READY);
				}
			}
		};

		checkPermission(r);
	}

	private void importGameData(File f) {
		if (f == null) {
			return;
		}

		try {
			FileInputStream fis = new FileInputStream(f);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader reader = new BufferedReader(isr);

			boolean quit = false;
			GameBean game = null;
			do {
				String line = reader.readLine();
				if (!TextUtils.isEmpty(line)) {
					String[] data = TextUtils.split(line, ",");
					if (data != null && data.length > 0) {
						if (data[0].equals(LABEL_GAME) && data.length > GameDataIndex.CostDetail.ordinal()) {
							game = new GameBean();
							game.mDate = data[GameDataIndex.Date.ordinal()];
							game.mAddress = data[GameDataIndex.Address.ordinal()];
							game.mType = Integer.valueOf(data[GameDataIndex.Type.ordinal()]);
							game.mTotalCost = Float.valueOf(data[GameDataIndex.TotalCost.ordinal()]);
							game.mCostDetail = data[GameDataIndex.CostDetail.ordinal()];
						} else if (data[0].equals(LABEL_PLAYER_DATA) && data.length > PlayerDataIndex.Goals.ordinal()) {
							if (game != null) {
								PlayerDataBean playerData = new PlayerDataBean();

								playerData.mGameId = Long.valueOf(data[PlayerDataIndex.GameId.ordinal()]);
								playerData.mMemberId = Long.valueOf(data[PlayerDataIndex.MemberId.ordinal()]);
								playerData.mName = data[PlayerDataIndex.Name.ordinal()];
								playerData.mCost = Float.valueOf(data[PlayerDataIndex.Cost.ordinal()]);
								playerData.mGoals = Integer.valueOf(data[PlayerDataIndex.Goals.ordinal()]);

								game.mPlayers.add(playerData);
							}
						} else if (data[0].equals(LABEL_GROUP) && data.length > GroupDataIndex.PlayerCount.ordinal()) {
							int count = Integer.valueOf(data[GroupDataIndex.PlayerCount.ordinal()]);
							if (data.length > GroupDataIndex.PlayerCount.ordinal() + count) {
								GroupDetail group = new GroupDetail();
								long gameId = Long.valueOf(data[GroupDataIndex.GameId.ordinal()]);

								group.mGroupIndex = Integer.valueOf(data[GroupDataIndex.GroupIndex.ordinal()]);
								for (int i = 1; i <= count; i++) {
									MemberBean player = new MemberBean();

									String item = data[GroupDataIndex.PlayerCount.ordinal() + i];
									if (item.startsWith("id=")) {
										player.mId = Long.valueOf(item.substring("id=".length()));
									} else if (item.startsWith("name=")) {
										player.mId = -1;
										player.mName = item.substring("name=".length());
									} else {
										continue;
									}
									group.mGroupMember.add(player);
								}

								mModel.addGroup(group, gameId);
							}
						}
					}
				} else {
					quit = true;
				}
			} while (!quit);

			if (game != null) {
				mModel.addNewGame(game);
			}

			reader.close();
			isr.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void importMemberData(File f) {
		if (f == null) {
			return;
		}

		try {
			FileInputStream fis = new FileInputStream(f);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader reader = new BufferedReader(isr);

			boolean quit = false;
			do {
				String line = reader.readLine();
				if (!TextUtils.isEmpty(line)) {
					String[] data = TextUtils.split(line, ",");
					if (data != null && data.length > MemberDataIndex.Shoot.ordinal()) {
						if (data[MemberDataIndex.Label.ordinal()].equals(LABEL_MEMBER)) {
							MemberBean member = new MemberBean();

							member.mName = data[MemberDataIndex.Name.ordinal()];
							member.mPosition = Integer.valueOf(data[MemberDataIndex.Position.ordinal()]);
							member.mIsVip = Integer.valueOf(data[MemberDataIndex.IsVip.ordinal()]) == 1;
							member.mSpeed = Integer.valueOf(data[MemberDataIndex.Speed.ordinal()]);
							member.mStrength = Integer.valueOf(data[MemberDataIndex.Strength.ordinal()]);
							member.mDefence = Integer.valueOf(data[MemberDataIndex.Defence.ordinal()]);
							member.mTech = Integer.valueOf(data[MemberDataIndex.Tech.ordinal()]);
							member.mPass = Integer.valueOf(data[MemberDataIndex.Pass.ordinal()]);
							member.mShoot = Integer.valueOf(data[MemberDataIndex.Shoot.ordinal()]);

							mModel.addMember(member);
						}
					}
				} else {
					quit = true;
				}
			} while (!quit);

			reader.close();
			isr.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}