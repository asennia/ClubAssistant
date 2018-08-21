package com.windfind.clubassistant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DB_FILE = "ClubAssistant.db";

	DatabaseHelper(Context context) {
		super(context, DB_FILE, null, ClubProvider.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createAndInitMemberTable(db);
		createAndInitGameTable(db);
		createAndInitPlayerDataTable(db);
		createAndInitGroupTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion == 2) {
			switch (newVersion) {
				case 3:
					db.execSQL("DROP TABLE IF EXISTS " + ClubProvider.TABLE_GROUP);
					createAndInitGroupTable(db);
					break;
				default:
					break;
			}
		}
	}

	private void createAndInitMemberTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS member (" +
				"_id INTEGER PRIMARY KEY," +
				"name TEXT NOT NULL," +
				"position INTEGER," +
				"isVip INTEGER," +
				"speed INTEGER," +
				"strength INTEGER," +
				"defence INTEGER," +
				"tech INTEGER," +
				"pass INTEGER," +
				"shoot INTEGER" +
				");");

		/*String cmd = "INSERT INTO member (name, position, isVip, speed, strength, defence, tech, pass, shoot) VALUES ";
		String[] prevMembers = {
				"('草头', 10, 1, 14, 13, 13, 11, 13, 12);",
				"('UU', 12, 1, 12, 15, 14, 14, 14, 15);",
				"('老孙', 24, 1, 14, 14, 15, 14, 14, 15);",
				"('玉帝', 8, 1, 8, 11, 11, 10, 11, 11);",
				"('何靖', 12, 1, 12, 13, 14, 12, 15, 12);",

				"('聂春', 3, 1, 14, 12, 11, 13, 12, 12);",
				"('王强', 6, 1, 14, 12, 13, 15, 13, 13);",
				"('开爷', 17, 1, 8, 13, 11, 11, 12, 11);",
				"('老赵', 1, 1, 7, 12, 10, 12, 11, 13);",
				"('谢老板', 10, 1, 13, 12, 12, 13, 11, 12);",

				"('fish', 3, 1, 10, 8, 11, 14, 12, 11);",
				"('小岱', 17, 1, 7, 10, 11, 11, 10, 11);",
				"('小朱', 3, 1, 15, 13, 12, 14, 12, 13);",
				"('老梁', 10, 0, 15, 13, 13, 14, 10, 12);",
				"('李总监', 6, 0, 13, 12, 13, 13, 13, 11);",

				"('相伟', 10, 0, 14, 14, 12, 10, 8, 10);",
				"('老宋', 10, 0, 12, 13, 14, 15, 14, 13);",
				"('欧泊', 8, 0, 14, 13, 14, 14, 13, 13);",
				"('Tommy', 3, 0, 14, 14, 13, 13, 13, 12);"
		};

		for (String str : prevMembers) {
			db.execSQL(cmd + str);
		}*/
	}

	private void createAndInitGameTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS game (" +
				"_id INTEGER PRIMARY KEY," +
				"date TEXT NOT NULL," +
				"address TEXT," +
				"type INTEGER," +
				"total_cost REAL," +
				"cost_detail TEXT" +
				");");
	}

	private void createAndInitPlayerDataTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS player_data (" +
				"_id INTEGER PRIMARY KEY," +
				"game_id INTEGER NOT NULL," +
				"member_id INTEGER NOT NULL," +
				"name TEXT," +
				"cost REAL," +
				"goals INTEGER" +
				");");
	}

	private void createAndInitGroupTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS player_group (" +
				"_id INTEGER PRIMARY KEY," +
				"game_id INTEGER NOT NULL," +
				"group_idx INTEGER NOT NULL," +
				"player_cnt INTEGER NOT NULL," +
				"p1 TEXT," +
				"p2 TEXT," +
				"p3 TEXT," +
				"p4 TEXT," +
				"p5 TEXT," +
				"p6 TEXT," +
				"p7 TEXT," +
				"p8 TEXT," +
				"p9 TEXT," +
				"p10 TEXT," +
				"p11 TEXT" +
				");");
	}

	interface Member {
		String _ID = "_id";
		String NAME = "name";
		String POS = "position";
		String IS_VIP = "isVip";
		String SPEED = "speed";
		String STRENGTH = "strength";
		String DEFENCE = "defence";
		String TECH = "tech";
		String PASS = "pass";
		String SHOOT = "shoot";

		int INDEX_ID = 0;
		int INDEX_NAME = 1;
		int INDEX_POS = 2;
		int INDEX_IS_VIP = 3;
		int INDEX_SPEED = 4;
		int INDEX_STRENGTH = 5;
		int INDEX_DEFENCE = 6;
		int INDEX_TECH = 7;
		int INDEX_PASS = 8;
		int INDEX_SHOOT = 9;
	}

	interface Game {
		String _ID = "_id";
		String DATE = "date";
		String ADDRESS = "address";
		String TYPE = "type";
		String TOTAL_COST = "total_cost";
		String COST_DETAIL = "cost_detail";

		int INDEX_ID = 0;
		int INDEX_DATE = 1;
		int INDEX_ADDRESS = 2;
		int INDEX_TYPE = 3;
		int INDEX_TOTAL_COST = 4;
		int INDEX_COST_DETAIL = 5;
	}

	interface PlayerData {
		String _ID = "_id";
		String GAME_ID = "game_id";
		String MEMBER_ID = "member_id";
		String NAME = "name";
		String COST = "cost";
		String GOALS = "goals";

		int INDEX_ID = 0;
		int INDEX_GAME_ID = 1;
		int INDEX_MEMBER_ID = 2;
		int INDEX_NAME = 3;
		int INDEX_COST = 4;
		int INDEX_GOALS = 5;
	}

	interface Group {
		String _ID = "_id";
		String GAME_ID = "game_id";
		String GROUP_IDX = "group_idx";
		String PLAYER_CNT = "player_cnt";
		String P1_ID = "p1";
		String P2_ID = "p2";
		String P3_ID = "p3";
		String P4_ID = "p4";
		String P5_ID = "p5";
		String P6_ID = "p6";
		String P7_ID = "p7";
		String P8_ID = "p8";
		String P9_ID = "p9";
		String P10_ID = "p10";
		String P11_ID = "p11";

		int INDEX_ID = 0;
		int INDEX_GAME_ID = 1;
		int INDEX_GROUP_IDX = 2;
		int INDEX_PLAYER_CNT = 3;
		int INDEX_P1_ID = 4;
		int INDEX_P2_ID = 5;
		int INDEX_P3_ID = 6;
		int INDEX_P4_ID = 7;
		int INDEX_P5_ID = 8;
		int INDEX_P6_ID = 9;
		int INDEX_P7_ID = 10;
		int INDEX_P8_ID = 11;
		int INDEX_P9_ID = 12;
		int INDEX_P10_ID = 13;
		int INDEX_P11_ID = 14;
	}
}