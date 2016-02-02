package com.yaozu.listener.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yaozu.listener.db.AppDbHelper;
import com.yaozu.listener.db.model.ChatListInfo;
import com.yaozu.listener.utils.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 耀祖 on 2016/1/17.
 */
public class ChatListInfoDao {
    private AppDbHelper helper;
    private Context context;

    public ChatListInfoDao(Context context) {
        this.context = context;
        helper = new AppDbHelper(context);
    }

    /**
     * 添加
     *
     * @param info
     */
    public void add(ChatListInfo info) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("insert into chatlistinfo (otheruserid,thisuserid,username,lastchatcontent,unreadcount,iconcacheurl) values (?,?,?,?,?,?)",
                    new Object[]{info.getOtherUserid(), User.getUserAccount(),info.getUsername(), info.getLastchatcontent(), info.getUnreadcount(), info.getIconcacheurl()});
        }
        db.close();
    }

    /**
     * 删除
     */
    public void deleteChatListById(ChatListInfo info) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("delete from chatlistinfo where otheruserid=? and thisuserid=?",
                    new Object[]{info.getOtherUserid(),User.getUserAccount()});
        }
        db.close();
    }

    public void updateChatListInfoByid(String lastChatContent, String userid) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("update chatlistinfo set lastchatcontent=? where otheruserid=? and thisuserid=?",
                    new Object[]{lastChatContent, userid,User.getUserAccount()});
        }
        db.close();
    }

    public void updateChatListUnreadsByid(String unreadcount, String userid) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("update chatlistinfo set unreadcount=? where otheruserid=? and thisuserid=?",
                    new Object[]{unreadcount, userid,User.getUserAccount()});
        }
        db.close();
    }

    public String getChatListUnreadsByid(String userid) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String unreadcount = null;
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery(
                    "select * from chatlistinfo where otheruserid=? and thisuserid=?",
                    new String[]{userid,User.getUserAccount()});
            while(cursor.moveToNext()){
                 unreadcount = cursor.getString(cursor.getColumnIndex("unreadcount"));
            }
        }
        db.close();
        return unreadcount;
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
                    "select * from chatlistinfo where otheruserid=? and thisuserid=?",
                    new String[]{userid,User.getUserAccount()});
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
            Cursor cursor = db.rawQuery("select * from chatlistinfo where thisuserid=?", new String[]{User.getUserAccount()});
            while (cursor.moveToNext()) {
                ChatListInfo info = new ChatListInfo();
                String userid = cursor.getString(cursor.getColumnIndex("otheruserid"));
                if(userid.equals(User.getUserAccount())){
                   continue;
                }
                String username = cursor.getString(cursor.getColumnIndex("username"));
                String lastchatcontent = cursor.getString(cursor.getColumnIndex("lastchatcontent"));
                String unreadcount = cursor.getString(cursor.getColumnIndex("unreadcount"));
                String iconcacheurl = cursor.getString(cursor.getColumnIndex("iconcacheurl"));

                info.setOtherUserid(userid);
                info.setUsername(username);
                info.setLastchatcontent(lastchatcontent);
                info.setUnreadcount(unreadcount);
                info.setIconcacheurl(iconcacheurl);

                chatlistinfos.add(info);
            }
        }
        db.close();
        return chatlistinfos;
    }
}
