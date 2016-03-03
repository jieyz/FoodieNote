package com.yaozu.listener.db.model;

/**
 * Created by jieyz on 2016/2/15.
 */
public class Person {
    private String id;
    private String name;
    private String beizhuname;
    private String isNew;
    private String currentSong;
    private String state;
    private boolean change = false;
    private Long changetiem;

    public Long getChangetiem() {
        return changetiem;
    }

    public void setChangetiem(Long changetiem) {
        this.changetiem = changetiem;
    }

    public boolean isChange() {
        return change;
    }

    public void setChange(boolean change) {
        this.change = change;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(String currentSong) {
        this.currentSong = currentSong;
    }

    public String getIsNew() {
        return isNew;
    }

    public void setIsNew(String isNew) {
        this.isNew = isNew;
    }

    public String getBeizhuname() {
        return beizhuname;
    }

    public void setBeizhuname(String beizhuname) {
        this.beizhuname = beizhuname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
