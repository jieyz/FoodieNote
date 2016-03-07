package com.yaozu.listener.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import com.yaozu.listener.R;
import com.yaozu.listener.constant.IntentKey;
import com.yaozu.listener.utils.NetUtil;
import com.yaozu.listener.widget.polites.GestureImageView;

/**
 * Created by 耀祖 on 2016/1/28.
 */
public class UserIconDetail extends BaseActivity implements View.OnClickListener {
    private String iconPath;
    private GestureImageView gestureImageView;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usericon_detail);
        gestureImageView = (GestureImageView) findViewById(R.id.activity_usericon_detail);
        iconPath = getIntent().getStringExtra(IntentKey.USER_ICON_PATH);
        userid = getIntent().getStringExtra(IntentKey.USER_ID);
        Bitmap bmp = BitmapFactory.decodeFile(iconPath);
        if (bmp != null) {
            gestureImageView.setImageBitmap(bmp);
        } else {
            NetUtil.setImageIcon(userid, gestureImageView, true, true);
        }
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
            case R.id.activity_usericon_detail:
                finish();
                break;
        }
    }
}
