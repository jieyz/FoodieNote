package com.yaozu.listener.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yaozu.listener.db.AppDbHelper;
import com.yaozu.listener.db.model.ChatDetailInfo;
import com.yaozu.listener.db.model.ChatListInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 耀祖 on 2016/1/18.
 */
public class ChatDetailInfoDao {
    private AppDbHelper helper;
    private Context context;

    public ChatDetailInfoDao(Context context) {
        this.context = context;
        helper = new AppDbHelper(context);
    }

    /**
     * 添加
     *
     * @param info
     */
    public void add(ChatDetailInfo info) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("insert into chatdetailinfo (userid,username,chatcontent,time,issender) values (?,?,?,?,?)",
                    new Object[]{info.getUserid(), info.getUsername(), info.getChatcontent(), info.getTime(),info.getIssender()});
        }
        db.close();
    }

    public List<ChatDetailInfo> findAllChatDetailInfoByUserid(String userid) {
        List<ChatDetailInfo> chatDetailinfos = new ArrayList<ChatDetailInfo>();
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from chatdetailinfo where userid=?", new String[] { userid });
            while (cursor.moveToNext()) {
                ChatDetailInfo info = new ChatDetailInfo();
                String username = cursor.getString(cursor.getColumnIndex("username"));
                String chatcontent = cursor.getString(cursor.getColumnIndex("chatcontent"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                String issender = cursor.getString(cursor.getColumnIndex("issender"));

                info.setUserid(userid);
                info.setUsername(username);
                info.setChatcontent(chatcontent);
                info.setTime(time);
                info.setIssender(issender);

                chatDetailinfos.add(info);
            }
        }
        db.close();
        return chatDetailinfos;
    }
}
