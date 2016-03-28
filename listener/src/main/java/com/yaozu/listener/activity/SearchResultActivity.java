package com.yaozu.listener.activity;

import android.os.Bundle;

/**
 * Created by jieyaozu on 2016/2/20.
 */
public class SearchResultActivity extends SwipeBackBaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void notifyCurrentSongMsg(String name, String singer, long album_id, int currentPos) {

    }

    @Override
    public void notifySongPlaying() {

    }

    @Override
    public void notifySongPause() {

    }
}
