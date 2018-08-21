package com.windfind.clubassistant;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.windfind.clubassistant.DatabaseHelper.Member;
import com.windfind.clubassistant.DatabaseHelper.Game;
import com.windfind.clubassistant.DatabaseHelper.PlayerData;
import com.windfind.clubassistant.DatabaseHelper.Group;

public class ClubProvider extends ContentProvider {

	public static final int DB_VERSION = 3;
	public static final String AUTHORITY = "com.windfind.clubassistant";

	public static final String TABLE_MEMBERS = "member";
	public static final String MEMBERS_URI = "content://" + AUTHORITY + "/" + TABLE_MEMBERS;
	public static final String MEMBER_ITEM_URI = "content://" + AUTHORITY + "/" + TABLE_MEMBERS + "/";

	public static final String TABLE_GAMES = "game";
	public static final String GAMES_URI = "content://" + AUTHORITY + "/" + TABLE_GAMES;
	public static final String GAMES_ITEM_URI = "content://" + AUTHORITY + "/" + TABLE_GAMES + "/";

	public static final String TABLE_PLAYER_DATA = "player_data";
	public static final String PLAYER_DATA_URI = "content://" + AUTHORITY + "/" + TABLE_PLAYER_DATA;
	public static final String PLAYER_DATA_ITEM_URI = "content://" + AUTHORITY + "/" + TABLE_PLAYER_DATA + "/";

	public static final String TABLE_GROUP = "player_group";
	public static final String GROUP_URI = "content://" + AUTHORITY + "/" + TABLE_GROUP;
	public static final String GROUP_ITEM_URI = "content://" + AUTHORITY + "/" + TABLE_GROUP + "/";

	private static final int MEMBERS = 1;
	private static final int MEMBER_ID = 2;
	private static final int GAMES = 3;
	private static final int GAME_ID = 4;
	private static final int PLAYER_DATA = 5;
	private static final int PLAYER_DATA_ID = 6;
	private static final int GROUP = 7;
	private static final int GROUP_ID = 8;

	private static final UriMatcher sMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sMatcher.addURI(AUTHORITY, TABLE_MEMBERS, MEMBERS);
		sMatcher.addURI(AUTHORITY, TABLE_MEMBERS + "/#", MEMBER_ID);

		sMatcher.addURI(AUTHORITY, TABLE_GAMES, GAMES);
		sMatcher.addURI(AUTHORITY, TABLE_GAMES + "/#", GAME_ID);

		sMatcher.addURI(AUTHORITY, TABLE_PLAYER_DATA, PLAYER_DATA);
		sMatcher.addURI(AUTHORITY, TABLE_PLAYER_DATA + "/#", PLAYER_DATA_ID);

		sMatcher.addURI(AUTHORITY, TABLE_GROUP, GROUP);
		sMatcher.addURI(AUTHORITY, TABLE_GROUP + "/#", GROUP_ID);
	}

	private DatabaseHelper mDBHelper;

	@Override
	public boolean onCreate() {
		final Context context = getContext();
		mDBHelper = new DatabaseHelper(context);
		return false;
	}

	@Nullable
	@Override
	public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		int match = sMatcher.match(uri);
		switch (match) {
			case MEMBERS:
				qb.setTables(TABLE_MEMBERS);
				break;
			case MEMBER_ID:
				qb.setTables(TABLE_MEMBERS);
				qb.appendWhere(Member._ID + "=" + ContentUris.parseId(uri));
				break;
			case GAMES:
				qb.setTables(TABLE_GAMES);
				break;
			case GAME_ID:
				qb.setTables(TABLE_GAMES);
				qb.appendWhere(Game._ID + "=" + ContentUris.parseId(uri));
				break;
			case PLAYER_DATA:
				qb.setTables(TABLE_PLAYER_DATA);
				break;
			case PLAYER_DATA_ID:
				qb.setTables(TABLE_PLAYER_DATA);
				qb.appendWhere(PlayerData._ID + "=" + ContentUris.parseId(uri));
				break;
			case GROUP:
				qb.setTables(TABLE_GROUP);
				break;
			case GROUP_ID:
				qb.setTables(TABLE_GROUP);
				qb.appendWhere(Group._ID + "=" + ContentUris.parseId(uri));
				break;
			default:
				break;
		}
		return qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
	}

	@Nullable
	@Override
	public String getType(@NonNull Uri uri) {
		return null;
	}

	@Nullable
	@Override
	public Uri insert(@NonNull Uri uri, ContentValues values) {
		if (values == null) {
			return null;
		}

		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		int match = sMatcher.match(uri);
		Uri ret = null;
		long rawId;
		switch (match) {
			case MEMBERS:
				ContentValues cv = new ContentValues(values);
				String name = cv.getAsString(Member.NAME);
				if (TextUtils.isEmpty(name)) {
					cv.put(Member.NAME, "Unknown");
				}

				rawId = db.insert(TABLE_MEMBERS, null, cv);
				ret = rawId == -1 ? null : ContentUris.withAppendedId(uri, rawId);
				break;
			case GAMES:
				rawId = db.insert(TABLE_GAMES, null, values);
				ret = rawId == -1 ? null : ContentUris.withAppendedId(uri, rawId);
				break;
			case PLAYER_DATA:
				rawId = db.insert(TABLE_PLAYER_DATA, null, values);
				ret = rawId == -1 ? null : ContentUris.withAppendedId(uri, rawId);
				break;
			case GROUP:
				rawId = db.insert(TABLE_GROUP, null, values);
				ret = rawId == -1 ? null : ContentUris.withAppendedId(uri, rawId);
				break;
			default:
				break;
		}
		return ret;
	}

	@Override
	public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		int match = sMatcher.match(uri);
		String where;
		String[] args;
		int ret = 0;
		int size;

		switch (match) {
			case MEMBERS:
				where = Member._ID + " IS NOT NULL";
				ret = db.delete(TABLE_MEMBERS, where, null);
				break;
			case MEMBER_ID:
				size = uri.getPathSegments().size();
				where = size > 1 ? Member._ID + "=" + ContentUris.parseId(uri) : selection;
				args = size > 1 ? null : selectionArgs;
				ret = db.delete(TABLE_MEMBERS, where, args);
				break;
			case GAMES:
				where = Game._ID + " IS NOT NULL";
				ret = db.delete(TABLE_GAMES, where, null);
				break;
			case GAME_ID:
				size = uri.getPathSegments().size();
				where = size > 1 ? Game._ID + "=" + ContentUris.parseId(uri) : selection;
				args = size > 1 ? null : selectionArgs;
				ret = db.delete(TABLE_GAMES, where, args);
				break;
			case PLAYER_DATA:
				where = PlayerData._ID + " IS NOT NULL";
				ret = db.delete(TABLE_PLAYER_DATA, where, null);
				break;
			case PLAYER_DATA_ID:
				size = uri.getPathSegments().size();
				where = size > 1 ? PlayerData._ID + "=" + ContentUris.parseId(uri) : selection;
				args = size > 1 ? null : selectionArgs;
				ret = db.delete(TABLE_PLAYER_DATA, where, args);
				break;
			case GROUP:
				where = Group._ID + " IS NOT NULL";
				ret = db.delete(TABLE_GROUP, where, null);
				break;
			case GROUP_ID:
				size = uri.getPathSegments().size();
				where = size > 1 ? Group._ID + "=" + ContentUris.parseId(uri) : selection;
				args = size > 1 ? null : selectionArgs;
				ret = db.delete(TABLE_GROUP, where, args);
				break;
			default:
				break;
		}
		return ret;
	}

	@Override
	public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		if (values == null) {
			return 0;
		}

		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		int match = sMatcher.match(uri);
		int ret = 0;
		int size = uri.getPathSegments().size();
		String where;
		String[] args = size > 1 ? null : selectionArgs;
		switch (match) {
			case MEMBER_ID:
				where = size > 1 ? Member._ID + "=" + ContentUris.parseId(uri) : selection;
				ret = db.update(TABLE_MEMBERS, values, where, args);
				break;
			case GAME_ID:
				where = size > 1 ? Game._ID + "=" + ContentUris.parseId(uri) : selection;
				ret = db.update(TABLE_GAMES, values, where, args);
				break;
			case PLAYER_DATA_ID:
				where = size > 1 ? PlayerData._ID + "=" + ContentUris.parseId(uri) : selection;
				ret = db.update(TABLE_PLAYER_DATA, values, where, args);
				break;
			case GROUP_ID:
				where = size > 1 ? Group._ID + "=" + ContentUris.parseId(uri) : selection;
				ret = db.update(TABLE_GROUP, values, where, args);
				break;
			default:
				break;
		}
		return ret;
	}
}
