package com.yaozu.listener.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SongDbHelper extends SQLiteOpenHelper {

	public SongDbHelper(Context context){
		super(context, "song.db", null,1);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table songinfo (_id integer primary key autoincrement," +
				"                         songid integer, " +
				"                         fileName varchar(32), " +
				"                         title varchar(32), " +
				"                         duration integer, " +
				"                         singer varchar(16), " +
				"                         album varchar(16), " +
				"                         type varchar(512), " +
				"                         size varchar(32)," +
				"                         fileurl varchar(32)," +
				"                         downloadurl varchar(32)," +
				"                         albumid integer)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		if(oldVersion == 1){
//			db.execSQL("alter table noteinfo add notegroup varchar(32)");
//			db.execSQL("alter table noteinfo add papercolor integer default 0");
//		}
//		if(oldVersion == 1 || oldVersion == 2){
//			db.execSQL("create table noteinfogroup (_id integer primary key autoincrement," +
//					"                         groupname varchar(32), " +
//					"                         groupcode varchar(16))");
//		}
//
//		if(oldVersion == 1 || oldVersion == 2 || oldVersion == 3){
//			db.execSQL("alter table noteinfo add createtime varchar(16)");
//		}
	}

}
