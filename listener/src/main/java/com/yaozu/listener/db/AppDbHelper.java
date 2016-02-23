package com.yaozu.listener.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppDbHelper extends SQLiteOpenHelper {

    public AppDbHelper(Context context) {
        super(context, "song.db", null, 1);
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

        db.execSQL("create table friend (_id integer primary key autoincrement," +
                "                         userid varchar(32) unique, " +
                "                         beizhuname varchar(32), " +
                "                         isnew varchar(32), " +
                "                         username varchar(32))");

        db.execSQL("create table chatlistinfo (_id integer primary key autoincrement," +
                "                         otheruserid varchar(32), " +
                "                         thisuserid varchar(32), " +
                "                         username varchar(32), " +
                "                         lastchatcontent varchar(32), " +
                "                         unreadcount varchar(16), " +
                "                         iconcacheurl varchar(32))");

        db.execSQL("create table chatdetailinfo (_id integer primary key autoincrement," +
                "                         otheruserid varchar(32), " +
                "                         thisuserid varchar(32), " +
                "                         username varchar(32), " +
                "                         chatcontent varchar(32), " +
                "                         time varchar(16), " +
                "                         issender varchar(16))");
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
