package com.yaozu.listener.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yaozu.listener.db.AppDbHelper;
import com.yaozu.listener.db.model.Person;
import com.yaozu.listener.utils.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jieyaozu on 2016/2/20.
 */
public class FriendDao {
    private AppDbHelper helper;
    private Context context;

    public FriendDao(Context context) {
        this.context = context;
        helper = new AppDbHelper(context);
    }

    /**
     * 添加
     *
     * @param person
     */
    public void add(Person person) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("insert into friend (userid,beizhuname,username,isnew) values (?,?,?,?)",
                    new Object[]{person.getId(), person.getBeizhuname(), person.getName(), person.getIsNew()});
        }
        db.close();
    }

    public void update(Person person) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("update friend set isnew=?,beizhuname=?,username=? where userid=?",
                    new Object[]{person.getIsNew(), person.getBeizhuname(), person.getName(), person.getId(),});
        }
        db.close();
    }

    /**
     * 查询一条是否存在
     *
     * @param userid
     * @return
     */
    public boolean isHavePerson(String userid) {
        boolean result = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery(
                    "select * from friend where userid=?",
                    new String[]{userid});
            if (cursor.moveToNext()) {
                result = true;
            }
            cursor.close();
        }
        db.close();
        return result;
    }

    /**
     * 删除
     */
    public void deleteFriendById(Person person) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("delete from friend where userid=?",
                    new Object[]{person.getId()});
        }
        db.close();
    }

    public List<Person> findAllFriends() {
        List<Person> persons = new ArrayList<Person>();
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from friend", null);
            while (cursor.moveToNext()) {
                Person person = new Person();
                String userid = cursor.getString(cursor.getColumnIndex("userid"));
                String username = cursor.getString(cursor.getColumnIndex("username"));
                String beizhuname = cursor.getString(cursor.getColumnIndex("beizhuname"));
                String isnew = cursor.getString(cursor.getColumnIndex("isnew"));

                person.setId(userid);
                person.setName(username);
                person.setBeizhuname(beizhuname);
                person.setIsNew(isnew);
                persons.add(person);
            }
        }
        db.close();
        return persons;
    }

    public void clear(){
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("delete from friend where 1=?",new Object[]{1});
        }
        db.close();
    }
}
