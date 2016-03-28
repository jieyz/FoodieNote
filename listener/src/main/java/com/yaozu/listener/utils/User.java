package com.yaozu.listener.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.yaozu.listener.constant.Constant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import io.rong.imkit.RongIM;

/**
 * Created by jieyz on 2016/1/26.
 */
public class User {
    private static String mAccount, mUserName, mToken;
    private Context mCotext;
    private static SharedPreferences preferences;

    public User(Context context) {
        this.mCotext = context;
        preferences = mCotext.getSharedPreferences(Constant.LOGIN_MSG, Context.MODE_PRIVATE);
    }

    public void storeLoginUserInfo(boolean isLogin, String account, String userName, String token) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear().commit();
        editor.putBoolean(Constant.IS_LOGINING, isLogin);
        editor.putString(Constant.USER_TOKEN, token);
        editor.putString(Constant.USER_ACCOUNT, account);
        editor.putString(Constant.USER_NAME, userName);
        editor.commit();
        readUserInfoToMemory();
    }

    /**
     * 更新用户名
     *
     * @param userName
     */
    public void updateUserName(String userName) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constant.USER_NAME, userName);
        editor.commit();
        mUserName = userName;
    }

    /**
     * 用户信息读到内存中
     */
    public static void readUserInfoToMemory() {
        mAccount = preferences.getString(Constant.USER_ACCOUNT, "");
        mUserName = preferences.getString(Constant.USER_NAME, "");
        mToken = preferences.getString(Constant.USER_TOKEN, "");
    }

    public static String getUserAccount() {
        return mAccount;//preferences.getString(Constant.USER_ACCOUNT, "");
    }

    public static String getUserName() {
        return mUserName;//preferences.getString(Constant.USER_NAME, "");
    }

    public String getUserToken() {
        return mToken;//preferences.getString(Constant.USER_TOKEN, "");
    }

    public void quitLogin() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constant.IS_LOGINING, false);
        editor.putString(Constant.USER_TOKEN, "");
        editor.commit();
        //断开聊天服务器
        RongIM.getInstance().logout();
    }

    public boolean isLogining() {
        boolean islogin = preferences.getBoolean(Constant.IS_LOGINING, false);
        return islogin;
    }

    /**
     * 保存退出时播放的歌曲
     *
     * @param songname
     * @param singer
     */
    public void storeQuitSongInfo(String songname, String singer, int index) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constant.QUIT_SONG_NAME, songname);
        editor.putString(Constant.QUIT_SONG_SINGER, singer);
        editor.putInt(Constant.QUIT_MEDIA_CURRENT_INDEX, index);
        editor.commit();
    }

    /**
     * 获得退出时播放的歌曲名
     *
     * @return
     */
    public String getQuitSongName() {
        return preferences.getString(Constant.QUIT_SONG_NAME, "");
    }

    /**
     * 获得退出时播放的歌手名
     *
     * @return
     */
    public String getQuitSinger() {
        return preferences.getString(Constant.QUIT_SONG_SINGER, "");
    }

    /**
     * 退出时的下标
     *
     * @return
     */
    public int getQuitIndex() {
        return preferences.getInt(Constant.QUIT_MEDIA_CURRENT_INDEX, 0);
    }
}
