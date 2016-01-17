package com.yaozu.listener.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yaozu.listener.db.SongDbHelper;
import com.yaozu.listener.db.model.ChatListInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 耀祖 on 2016/1/17.
 */
public class ChatListInfoDao {
    private SongDbHelper helper;
    private Context context;

    public ChatListInfoDao(Context context) {
        this.context = context;
        helper = new SongDbHelper(context);
    }

    /**
     * 添加
     *
     * @param info
     */
    public void add(ChatListInfo info) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("insert into chatlistinfo (userid,username,lastchatcontent,iconcacheurl) values (?,?,?,?)",
                    new Object[]{info.getUserid(), info.getUsername(), info.getLastchatcontent(), info.getIconcacheurl()});
        }
        db.close();
    }

    /**
     * 删除
     */
    public void deleteChatListById(ChatListInfo info) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("delete from chatlistinfo where userid = ?",
                    new Object[]{info.getUserid()});
        }
        db.close();
    }

    public void updateChatListInfoByid(String lastChatContent, String userid) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("update chatlistinfo set lastchatcontent=? where userid=?",
                    new Object[]{lastChatContent, userid});
        }
        db.close();
    }

    /**
     * 查询一条是否存在
     *
     * @param userid
     * @return
     */
    public boolean find(String userid) {
        boolean result = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery(
                    "select * from chatlistinfo where userid=?",
                    new String[] { userid });
            if (cursor.moveToNext()) {
                result = true;
            }
            cursor.close();
        }
        db.close();
        return result;
    }

    public List<ChatListInfo> findAllChatListInfo() {
        List<ChatListInfo> chatlistinfos = new ArrayList<ChatListInfo>();
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from chatlistinfo", null);
            while (cursor.moveToNext()) {
                ChatListInfo info = new ChatListInfo();
                String userid = cursor.getString(cursor.getColumnIndex("userid"));
                String username = cursor.getString(cursor.getColumnIndex("username"));
                String lastchatcontent = cursor.getString(cursor.getColumnIndex("lastchatcontent"));
                String iconcacheurl = cursor.getString(cursor.getColumnIndex("iconcacheurl"));

                info.setUserid(userid);
                info.setUsername(username);
                info.setLastchatcontent(lastchatcontent);
                info.setIconcacheurl(iconcacheurl);

                chatlistinfos.add(info);
            }
        }
        db.close();
        return chatlistinfos;
    }
}
