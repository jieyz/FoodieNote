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
    private String state;
    private String songinfo;

    public String getSonginfo() {
        return songinfo;
    }

    public void setSonginfo(String songinfo) {
        this.songinfo = songinfo;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUnreadcount() {
        return unreadcount;
    }

    public void setUnreadcount(String unreadcount) {
        this.unreadcount = unreadcount;
    }

    public String getOtherUserid() {
        return userid;
    }

    public void setOtherUserid(String userid) {
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
