package com.yaozu.listener.listener;

/**
 * Created by jieyaozu on 2016/2/28.
 */
public enum PersonState {
    PLAYING("playing"), PAUSE("pause");

    private String name;

    private PersonState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
