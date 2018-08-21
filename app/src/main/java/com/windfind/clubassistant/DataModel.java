package com.windfind.clubassistant;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.LongSparseArray;

import com.windfind.clubassistant.DatabaseHelper.Member;
import com.windfind.clubassistant.DatabaseHelper.Game;
import com.windfind.clubassistant.DatabaseHelper.PlayerData;
import com.windfind.clubassistant.DatabaseHelper.Group;
import com.windfind.clubassistant.game.GameBean;
import com.windfind.clubassistant.game.GroupDetail;
import com.windfind.clubassistant.game.PlayerDataBean;
import com.windfind.clubassistant.member.MemberBean;

import java.util.ArrayList;
import java.util.HashMap;

public class DataModel {

	private static final Uri MEMBER_URI = Uri.parse(ClubProvider.MEMBERS_URI);
	private static final String[] MEMBER_PROJECTION = new String[] {
			Member._ID,
			Member.NAME,
			Member.POS,
			Member.IS_VIP,
			Member.SPEED,
			Member.STRENGTH,
			Member.DEFENCE,
			Member.TECH,
			Member.PASS,
			Member.SHOOT
	};

	private static final Uri GAME_URI = Uri.parse(ClubProvider.GAMES_URI);
	private static final String[] GAME_PROJECTION = new String[] {
			Game._ID,
			Game.DATE,
			Game.ADDRESS,
			Game.TYPE,
			Game.TOTAL_COST,
			Game.COST_DETAIL
	};

	private static final Uri PLAYER_DATA_URI = Uri.parse(ClubProvider.PLAYER_DATA_URI);
	private static final String[] PLAYER_DATA_PROJECTION = new String[] {
			PlayerData._ID,
			PlayerData.GAME_ID,
			PlayerData.MEMBER_ID,
			PlayerData.NAME,
			PlayerData.COST,
			PlayerData.GOALS
	};

	private static final Uri GROUP_URI = Uri.parse(ClubProvider.GROUP_URI);
	private static final String[] GROUP_PROJECTION = new String[] {
			Group._ID,
			Group.GAME_ID,
			Group.GROUP_IDX,
			Group.PLAYER_CNT,
			Group.P1_ID,
			Group.P2_ID,
			Group.P3_ID,
			Group.P4_ID,
			Group.P5_ID,
			Group.P6_ID,
			Group.P7_ID,
			Group.P8_ID,
			Group.P9_ID,
			Group.P10_ID,
			Group.P11_ID
	};

	private Context mContext;
	private ContentResolver mResolver;

	DataModel(Context context) {
		mContext = context;
		mResolver = context.getContentResolver();
	}

	/*------------------------------------ Members ------------------------------------*/
	public ArrayList<MemberBean> getAllMembers() {
		ArrayList<MemberBean> members = new ArrayList<>();

		Cursor c = mResolver.query(MEMBER_URI, MEMBER_PROJECTION, null, null, null);

		if (c != null) {
			MemberBean bean;

			while (c.moveToNext()) {
				bean = new MemberBean();

				bean.mId = c.getInt(Member.INDEX_ID);
				bean.mName = c.getString(Member.INDEX_NAME);
				bean.mPosition = c.getInt(Member.INDEX_POS);
				bean.mIsVip = (c.getInt(Member.INDEX_IS_VIP) == 1);
				bean.mSpeed = c.getInt(Member.INDEX_SPEED);
				bean.mStrength = c.getInt(Member.INDEX_STRENGTH);
				bean.mDefence = c.getInt(Member.INDEX_DEFENCE);
				bean.mTech = c.getInt(Member.INDEX_TECH);
				bean.mPass = c.getInt(Member.INDEX_PASS);
				bean.mShoot = c.getInt(Member.INDEX_SHOOT);
				bean.calculateAbility();

				members.add(bean);
			}

			c.close();
		}

		return members;
	}

	public LongSparseArray<MemberBean> getAllMembersEx() {
		LongSparseArray<MemberBean> members = new LongSparseArray<>();

		Cursor c = mResolver.query(MEMBER_URI, MEMBER_PROJECTION, null, null, null);
		if (c != null) {
			MemberBean bean;

			while (c.moveToNext()) {
				bean = new MemberBean();

				bean.mId = c.getInt(Member.INDEX_ID);
				bean.mName = c.getString(Member.INDEX_NAME);
				bean.mPosition = c.getInt(Member.INDEX_POS);
				bean.mIsVip = (c.getInt(Member.INDEX_IS_VIP) == 1);
				bean.mSpeed = c.getInt(Member.INDEX_SPEED);
				bean.mStrength = c.getInt(Member.INDEX_STRENGTH);
				bean.mDefence = c.getInt(Member.INDEX_DEFENCE);
				bean.mTech = c.getInt(Member.INDEX_TECH);
				bean.mPass = c.getInt(Member.INDEX_PASS);
				bean.mShoot = c.getInt(Member.INDEX_SHOOT);
				bean.calculateAbility();

				members.put(bean.mId, bean);
			}

			c.close();
		}

		return members;
	}

	public MemberBean getMemberById(long id) {
		if (id <= 0) {
			return null;
		}

		MemberBean bean = null;

		String selection = Member._ID + "=" + id;
		Cursor c = mResolver.query(MEMBER_URI, MEMBER_PROJECTION, selection, null, null);
		if (c != null) {

			while (c.moveToNext()) {
				bean = new MemberBean();

				bean.mId = c.getInt(Member.INDEX_ID);
				bean.mName = c.getString(Member.INDEX_NAME);
				bean.mPosition = c.getInt(Member.INDEX_POS);
				bean.mIsVip = (c.getInt(Member.INDEX_IS_VIP) == 1);
				bean.mSpeed = c.getInt(Member.INDEX_SPEED);
				bean.mStrength = c.getInt(Member.INDEX_STRENGTH);
				bean.mDefence = c.getInt(Member.INDEX_DEFENCE);
				bean.mTech = c.getInt(Member.INDEX_TECH);
				bean.mPass = c.getInt(Member.INDEX_PASS);
				bean.mShoot = c.getInt(Member.INDEX_SHOOT);
				bean.calculateAbility();
			}

			c.close();
		}

		return bean;
	}

	public LongSparseArray<MemberBean> getMembersById(ArrayList<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return null;
		}

		LongSparseArray<MemberBean> members = new LongSparseArray<>();
		String selection = Member._ID + " in (" + TextUtils.join(",", ids) + ")";

		Cursor c = mResolver.query(MEMBER_URI, MEMBER_PROJECTION, selection, null, null);
		if (c != null) {
			MemberBean bean;

			while (c.moveToNext()) {
				bean = new MemberBean();

				bean.mId = c.getInt(Member.INDEX_ID);
				bean.mName = c.getString(Member.INDEX_NAME);
				bean.mPosition = c.getInt(Member.INDEX_POS);
				bean.mIsVip = (c.getInt(Member.INDEX_IS_VIP) == 1);
				bean.mSpeed = c.getInt(Member.INDEX_SPEED);
				bean.mStrength = c.getInt(Member.INDEX_STRENGTH);
				bean.mDefence = c.getInt(Member.INDEX_DEFENCE);
				bean.mTech = c.getInt(Member.INDEX_TECH);
				bean.mPass = c.getInt(Member.INDEX_PASS);
				bean.mShoot = c.getInt(Member.INDEX_SHOOT);
				bean.calculateAbility();

				members.put(bean.mId, bean);
			}

			c.close();
		}

		return members;
	}

	public void addMember(MemberBean bean) {
		if (bean == null) {
			return;
		}

		ContentValues values = new ContentValues();
		values.put(Member.NAME, bean.mName);
		values.put(Member.POS, bean.mPosition);
		values.put(Member.IS_VIP, bean.mIsVip ? 1 : 0);
		values.put(Member.SPEED, bean.mSpeed);
		values.put(Member.STRENGTH, bean.mStrength);
		values.put(Member.DEFENCE, bean.mDefence);
		values.put(Member.TECH, bean.mTech);
		values.put(Member.PASS, bean.mPass);
		values.put(Member.SHOOT, bean.mShoot);

		mResolver.insert(MEMBER_URI, values);
	}

	public void updateMember(long id, MemberBean bean) {
		if (id <= 0 || bean == null) {
			return;
		}

		ContentValues values = new ContentValues();
		values.put(Member.NAME, bean.mName);
		values.put(Member.POS, bean.mPosition);
		values.put(Member.IS_VIP, bean.mIsVip ? 1 : 0);
		values.put(Member.SPEED, bean.mSpeed);
		values.put(Member.STRENGTH, bean.mStrength);
		values.put(Member.DEFENCE, bean.mDefence);
		values.put(Member.TECH, bean.mTech);
		values.put(Member.PASS, bean.mPass);
		values.put(Member.SHOOT, bean.mShoot);

		Uri uri = Uri.parse(ClubProvider.MEMBER_ITEM_URI + id);
		mResolver.update(uri, values, null, null);
	}

	void clearMember() {
		mResolver.delete(MEMBER_URI, null, null);
	}

	/*------------------------------------ Games ------------------------------------*/
	public ArrayList<GameBean> getAllGames() {
		ArrayList<GameBean> games = new ArrayList<>();

		String order = Game._ID + " DESC";
		Cursor cursor = mResolver.query(GAME_URI, GAME_PROJECTION, null, null, order);
		if (cursor != null) {
			GameBean bean;

			while (cursor.moveToNext()) {
				bean = new GameBean();

				bean.mId = cursor.getInt(Game.INDEX_ID);
				bean.mDate = cursor.getString(Game.INDEX_DATE);
				bean.mAddress = cursor.getString(Game.INDEX_ADDRESS);
				bean.mType = cursor.getInt(Game.INDEX_TYPE);
				bean.mTotalCost = cursor.getFloat(Game.INDEX_TOTAL_COST);
				bean.mCostDetail = cursor.getString(Game.INDEX_COST_DETAIL);
				bean.mPlayers = getPlayerDataByGame(bean.mId);

				games.add(bean);
			}

			cursor.close();
		}

		return games;
	}

	public GameBean getGameById(long id) {
		String selection = Game._ID + "=" + id;
		Cursor c = mResolver.query(GAME_URI, GAME_PROJECTION, selection, null, null);
		if (c != null) {
			GameBean bean = new GameBean();

			c.moveToNext();
			bean.mId = c.getInt(Game.INDEX_ID);
			bean.mDate = c.getString(Game.INDEX_DATE);
			bean.mAddress = c.getString(Game.INDEX_ADDRESS);
			bean.mType = c.getInt(Game.INDEX_TYPE);
			bean.mTotalCost = c.getFloat(Game.INDEX_TOTAL_COST);
			bean.mCostDetail = c.getString(Game.INDEX_COST_DETAIL);
			bean.mPlayers = getPlayerDataByGame(bean.mId);

			c.close();

			return bean;
		}

		return null;
	}

	public LongSparseArray<GameBean> getGameById(ArrayList<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return null;
		}

		LongSparseArray<GameBean> data = new LongSparseArray<>();
		String selection = Game._ID + " in (" + TextUtils.join(",", ids) + ")";
		Cursor c = mResolver.query(GAME_URI, GAME_PROJECTION, selection, null, null);
		if (c != null) {
			while (c.moveToNext()) {
				GameBean bean = new GameBean();

				bean.mId = c.getInt(Game.INDEX_ID);
				bean.mDate = c.getString(Game.INDEX_DATE);
				bean.mAddress = c.getString(Game.INDEX_ADDRESS);
				bean.mType = c.getInt(Game.INDEX_TYPE);
				bean.mTotalCost = c.getFloat(Game.INDEX_TOTAL_COST);
				bean.mCostDetail = c.getString(Game.INDEX_COST_DETAIL);
				bean.mPlayers = getPlayerDataByGame(bean.mId);

				data.put(bean.mId, bean);
			}

			c.close();
		}

		return data;
	}

	public void addNewGame(GameBean bean) {
		if (bean == null || bean.mPlayers == null || bean.mPlayers.isEmpty()) {
			return;
		}

		// Insert game data
		ContentValues values = new ContentValues();
		values.put(Game.DATE, bean.mDate);
		values.put(Game.ADDRESS, bean.mAddress);
		values.put(Game.TYPE, bean.mType);
		values.put(Game.TOTAL_COST, bean.mTotalCost);
		values.put(Game.COST_DETAIL, bean.mCostDetail);

		Uri uri = mResolver.insert(GAME_URI, values);
		long gameId = ContentUris.parseId(uri);
		if (gameId <= 0) {
			return;
		}

		// Insert players data
		for (PlayerDataBean player : bean.mPlayers) {
			values = new ContentValues();
			values.put(PlayerData.GAME_ID, gameId);
			if (player.mMemberId > 0) {
				values.put(PlayerData.MEMBER_ID, player.mMemberId);
				values.put(PlayerData.NAME, "");
			} else {
				values.put(PlayerData.MEMBER_ID, -1);
				values.put(PlayerData.NAME, player.mName);
			}
			values.put(PlayerData.COST, player.mCost);
			values.put(PlayerData.GOALS, player.mGoals);

			mResolver.insert(PLAYER_DATA_URI, values);
		}
	}

	public void updateGame(long id, GameBean bean) {
		if (id <= 0 || bean == null || bean.mPlayers.isEmpty()) {
			return;
		}

		GameBean game = getGameById(id);
		if (game == null) {
			return;
		}

		ContentValues values = new ContentValues();
		values.put(Game.DATE, bean.mDate);
		values.put(Game.ADDRESS, bean.mAddress);
		values.put(Game.TYPE, bean.mType);
		values.put(Game.TOTAL_COST, bean.mTotalCost);
		values.put(Game.COST_DETAIL, bean.mCostDetail);
		Uri uri = Uri.parse(ClubProvider.GAMES_ITEM_URI + id);
		mResolver.update(uri, values, null, null);

		LongSparseArray<PlayerDataBean> oldNormalPlayer = new LongSparseArray<>();
		ArrayList<Long> oldNormalPlayerId = new ArrayList<>();
		HashMap<String, PlayerDataBean> oldExtraPlayer = new HashMap<>();
		ArrayList<String> oldExtraPlayerName = new ArrayList<>();
		for (PlayerDataBean playerData : game.mPlayers) {
			if (playerData.mMemberId > 0) {
				oldNormalPlayer.put(playerData.mMemberId, playerData);
				oldNormalPlayerId.add(playerData.mMemberId);
			} else {
				oldExtraPlayer.put(playerData.mName, playerData);
				oldExtraPlayerName.add(playerData.mName);
			}
		}

		LongSparseArray<PlayerDataBean> newNormalPlayer = new LongSparseArray<>();
		ArrayList<Long> newNormalPlayerId = new ArrayList<>();
		HashMap<String, PlayerDataBean> newExtraPlayer = new HashMap<>();
		ArrayList<String> newExtraPlayerName = new ArrayList<>();
		for (PlayerDataBean playerData : bean.mPlayers) {
			if (playerData.mMemberId > 0) {
				newNormalPlayer.put(playerData.mMemberId, playerData);
				newNormalPlayerId.add(playerData.mMemberId);
			} else {
				newExtraPlayer.put(playerData.mName, playerData);
				newExtraPlayerName.add(playerData.mName);
			}
		}

		ArrayList<Long> toRemovedDataId = new ArrayList<>();
		for (long playerId : oldNormalPlayerId) {
			if (!newNormalPlayerId.contains(playerId)) {
				// 这表示需要移除的playerData
				PlayerDataBean toRemovedData = oldNormalPlayer.get(playerId);
				toRemovedDataId.add(toRemovedData.mId);
			}
		}
		for (String name : oldExtraPlayerName) {
			if (!newExtraPlayerName.contains(name)) {
				// 这表示需要移除的playerData
				PlayerDataBean toRemovedData = oldExtraPlayer.get(name);
				toRemovedDataId.add(toRemovedData.mId);
			}
		}

		ArrayList<PlayerDataBean> toAddedData = new ArrayList<>();
		for (long playerId : newNormalPlayerId) {
			if (!oldNormalPlayerId.contains(playerId)) {
				// 这表示需要新增的playerData
				PlayerDataBean data = newNormalPlayer.get(playerId);
				toAddedData.add(data);
			}
		}
		for (String name : newExtraPlayerName) {
			if (!oldExtraPlayerName.contains(name)) {
				// 这表示需要新增的playerData
				PlayerDataBean data = newExtraPlayer.get(name);
				toAddedData.add(data);
			}
		}

		for (long toRemovedId : toRemovedDataId) {
			uri = Uri.parse(ClubProvider.PLAYER_DATA_ITEM_URI + toRemovedId);
			mResolver.delete(uri, null, null);
		}

		for (PlayerDataBean data : toAddedData) {
			values = new ContentValues();
			values.put(PlayerData.GAME_ID, id);
			if (data.mMemberId > 0) {
				values.put(PlayerData.MEMBER_ID, data.mMemberId);
				values.put(PlayerData.NAME, "");
			} else {
				values.put(PlayerData.MEMBER_ID, -1);
				values.put(PlayerData.NAME, data.mName);
			}
			values.put(PlayerData.COST, data.mCost);
			values.put(PlayerData.GOALS, data.mGoals);

			mResolver.insert(PLAYER_DATA_URI, values);
		}
	}

	void clearGame() {
		mResolver.delete(GAME_URI, null, null);
	}

	/*------------------------------------ Player Data ------------------------------------*/
	public ArrayList<PlayerDataBean> getAllPlayerData() {
		ArrayList<PlayerDataBean> data = new ArrayList<>();

		Cursor c = mResolver.query(PLAYER_DATA_URI, PLAYER_DATA_PROJECTION, null, null, null);

		if (c != null) {
			PlayerDataBean bean;

			while (c.moveToNext()) {
				bean = new PlayerDataBean();

				bean.mId = c.getInt(PlayerData.INDEX_ID);
				bean.mGameId = c.getInt(PlayerData.INDEX_GAME_ID);
				bean.mMemberId = c.getInt(PlayerData.INDEX_MEMBER_ID);
				bean.mName = c.getString(PlayerData.INDEX_NAME);
				bean.mCost = c.getFloat(PlayerData.INDEX_COST);
				bean.mGoals = c.getInt(PlayerData.INDEX_GOALS);

				data.add(bean);
			}

			c.close();
		}

		return data;
	}

	ArrayList<PlayerDataBean> getPlayerDataByGame(long gameId) {
		ArrayList<PlayerDataBean> data = new ArrayList<>();

		String where = PlayerData.GAME_ID + "=" + gameId;
		Cursor c = mResolver.query(PLAYER_DATA_URI, PLAYER_DATA_PROJECTION, where, null, null);

		if (c != null) {
			PlayerDataBean bean;

			while (c.moveToNext()) {
				bean = new PlayerDataBean();

				bean.mId = c.getInt(PlayerData.INDEX_ID);
				bean.mGameId = c.getInt(PlayerData.INDEX_GAME_ID);
				bean.mMemberId = c.getInt(PlayerData.INDEX_MEMBER_ID);
				bean.mName = c.getString(PlayerData.INDEX_NAME);
				bean.mCost = c.getFloat(PlayerData.INDEX_COST);
				bean.mGoals = c.getInt(PlayerData.INDEX_GOALS);

				data.add(bean);
			}

			c.close();
		}

		return data;
	}

	public ArrayList<PlayerDataBean> getPlayerDataByMember(long memberId) {
		ArrayList<PlayerDataBean> data = new ArrayList<>();

		String where = PlayerData.MEMBER_ID + "=" + memberId;
		Cursor c = mResolver.query(PLAYER_DATA_URI, PLAYER_DATA_PROJECTION, where, null, null);

		if (c != null) {
			PlayerDataBean bean;

			while (c.moveToNext()) {
				bean = new PlayerDataBean();

				bean.mId = c.getInt(PlayerData.INDEX_ID);
				bean.mGameId = c.getInt(PlayerData.INDEX_GAME_ID);
				bean.mMemberId = c.getInt(PlayerData.INDEX_MEMBER_ID);
				bean.mName = c.getString(PlayerData.INDEX_NAME);
				bean.mCost = c.getFloat(PlayerData.INDEX_COST);
				bean.mGoals = c.getInt(PlayerData.INDEX_GOALS);

				data.add(bean);
			}

			c.close();
		}

		return data;
	}

	public void updatePlayerDataById(long id, PlayerDataBean data) {
		if (id <= 0 || data == null) {
			return;
		}

		ContentValues values = new ContentValues();

		values.put(PlayerData.GAME_ID, data.mGameId);
		values.put(PlayerData.MEMBER_ID, data.mMemberId);
		values.put(PlayerData.NAME, data.mName);
		values.put(PlayerData.COST, data.mCost);
		values.put(PlayerData.GOALS, data.mGoals);

		Uri uri = Uri.parse(ClubProvider.PLAYER_DATA_ITEM_URI + id);
		mResolver.update(uri, values, null, null);
	}

	void clearPlayerData() {
		mResolver.delete(PLAYER_DATA_URI, null, null);
	}

	/*------------------------------------ Group ------------------------------------*/
	public ArrayList<GroupDetail> getGroupsByGame(long gameId) {
		ArrayList<GroupDetail> groups = new ArrayList<>();

		String where = Group.GAME_ID + "=" + gameId;
		Cursor c = mResolver.query(GROUP_URI, GROUP_PROJECTION, where, null, null);

		if (c != null) {
			GroupDetail group;

			while (c.moveToNext()) {
				int count = c.getInt(Group.INDEX_PLAYER_CNT);
				if (count <= 0) {
					continue;
				}

				String playerInGroup;
				String idStr;
				String name;
				int idx;
				long playerId;
				MemberBean extraPlayer;
				ArrayList<Long> playerIds = new ArrayList<>();
				ArrayList<MemberBean> extraPlayerList = new ArrayList<>();
				for (int i = 0; i < count; i++) {
					idx = Group.INDEX_P1_ID + i;
					playerInGroup = c.getString(idx);
					if (playerInGroup.startsWith(MemberBean.WITH_ID)) {
						// 存放的是人员ID
						idStr = playerInGroup.substring(MemberBean.WITH_ID.length());
						if (!TextUtils.isEmpty(idStr)) {
							playerId = Integer.parseInt(idStr);
							playerIds.add(playerId);
						}
					} else if (playerInGroup.startsWith(MemberBean.WITH_NAME)) {
						// 存放的是人员名字
						name = playerInGroup.substring(MemberBean.WITH_NAME.length());
						if (!TextUtils.isEmpty(name)) {
							extraPlayer = MemberBean.buildExtraPlayer(mContext, name);
							extraPlayerList.add(extraPlayer);
						}
					}
				}

				LongSparseArray<MemberBean> players = getMembersById(playerIds);
				if (players == null || players.size() == 0) {
					continue;
				}

				group = new GroupDetail();
				group.mGroupIndex = c.getInt(Group.INDEX_GROUP_IDX);

				// 添加带ID的人员
				for (long id : playerIds) {
					group.mGroupMember.add(players.get(id));
				}

				// 添加只有名字的人员
				group.mGroupMember.addAll(extraPlayerList);

				// 把整理好的组加入组列表
				groups.add(group);
			}

			c.close();
		}

		return groups;
	}

	public void addGroup(GroupDetail group, long gameId) {
		if (group == null || group.mGroupMember == null || gameId <= 0) {
			return;
		}

		String[] playerKeys = {
				Group.P1_ID,
				Group.P2_ID,
				Group.P3_ID,
				Group.P4_ID,
				Group.P5_ID,
				Group.P6_ID,
				Group.P7_ID,
				Group.P8_ID,
				Group.P9_ID,
				Group.P10_ID,
				Group.P11_ID
		};

		int cnt = group.mGroupMember.size();
		if (cnt > playerKeys.length) {
			cnt = playerKeys.length;
		}

		ContentValues values = new ContentValues();
		values.put(Group.GAME_ID, gameId);
		values.put(Group.GROUP_IDX, group.mGroupIndex);
		values.put(Group.PLAYER_CNT, cnt);

		for (int i = 0; i < cnt; i++) {
			if (group.mGroupMember.get(i).mId >= 0) {
				values.put(playerKeys[i], MemberBean.WITH_ID + group.mGroupMember.get(i).mId);
			} else {
				values.put(playerKeys[i], MemberBean.WITH_NAME + group.mGroupMember.get(i).mName);
			}
		}

		mResolver.insert(GROUP_URI, values);
	}

	void clearGroup() {
		mResolver.delete(GROUP_URI, null, null);
	}
}
