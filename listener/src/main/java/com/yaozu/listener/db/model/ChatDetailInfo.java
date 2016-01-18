package com.yaozu.listener.db.model;

import java.io.Serializable;

/**
 * Created by 耀祖 on 2016/1/18.
 */
public class ChatDetailInfo implements Serializable {
    private String userid;
    private String username;
    private String chatcontent;
    private String time;
    private String issender;

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

    public String getChatcontent() {
        return chatcontent;
    }

    public void setChatcontent(String chatcontent) {
        this.chatcontent = chatcontent;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIssender() {
        return issender;
    }

    public void setIssender(String issender) {
        this.issender = issender;
    }
}
