package com.yaozu.listener.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.yaozu.listener.constant.Constant;

import io.rong.imkit.RongIM;

/**
 * Created by jieyz on 2016/1/26.
 */
public class User {
    private String mAccount, mUserName, mToken;
    private Context mCotext;
    private SharedPreferences preferences;
    public User(Context context) {
        this.mCotext = context;
        preferences = mCotext.getSharedPreferences(Constant.LOGIN_MSG, Context.MODE_PRIVATE);
    }

    public void storeLoginUserInfo(boolean isLogin,String account, String userName, String token){
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear().commit();
        editor.putBoolean(Constant.IS_LOGINING, isLogin);
        editor.putString(Constant.USER_TOKEN, token);
        editor.putString(Constant.USER_ACCOUNT, account);
        editor.putString(Constant.USER_NAME, userName);
        editor.commit();
    }

    public String getUserAccount(){
        return preferences.getString(Constant.USER_ACCOUNT,"");
    }

    public String getUserToken(){
        return preferences.getString(Constant.USER_TOKEN,"");
    }

    public void quitLogin(){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constant.IS_LOGINING, false);
        editor.putString(Constant.USER_TOKEN, "");
        editor.commit();
        //断开聊天服务器
        RongIM.getInstance().logout();
    }

    public boolean isLogining(){
        boolean islogin = preferences.getBoolean(Constant.IS_LOGINING,false);
        return islogin;
    }
}
