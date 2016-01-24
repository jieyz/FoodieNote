package com.yaozu.listener.db.model;

import java.io.Serializable;

/**
 * Created by 耀祖 on 2016/1/17.
 */
public class ChatListInfo implements Serializable{
    private String userid;
    private String username;
    private String lastchatcontent;
    private String iconcacheurl;
    private String unreadcount;

    public String getUnreadcount() {
        return unreadcount;
    }

    public void setUnreadcount(String unreadcount) {
        this.unreadcount = unreadcount;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastchatcontent() {
        return lastchatcontent;
    }

    public void setLastchatcontent(String lastchatcontent) {
        this.lastchatcontent = lastchatcontent;
    }

    public String getIconcacheurl() {
        return iconcacheurl;
    }

    public void setIconcacheurl(String iconcacheurl) {
        this.iconcacheurl = iconcacheurl;
    }
}
