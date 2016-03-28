package com.yaozu.listener.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yaozu.listener.R;

/**
 * Created by jieyaozu on 2016/2/19.
 */
public class AddNewFriendActivity extends SwipeBackBaseActivity implements View.OnClickListener {

    private ImageView actionBack;
    private RelativeLayout add_new_friend_search;
    private RelativeLayout activity_add_phone_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_friend);
        actionBack = (ImageView) findViewById(R.id.add_new_friend_back);
        add_new_friend_search = (RelativeLayout) findViewById(R.id.add_new_friend_search);
        activity_add_phone_user = (RelativeLayout) findViewById(R.id.activity_add_phone_user);
        actionBack.setOnClickListener(this);
        add_new_friend_search.setOnClickListener(this);
        activity_add_phone_user.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_new_friend_back:
                finish();
                break;
            case R.id.add_new_friend_search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.activity_add_phone_user:
                break;
        }
    }
}
