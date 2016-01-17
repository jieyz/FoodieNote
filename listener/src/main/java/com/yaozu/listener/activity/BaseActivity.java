package com.yaozu.listener.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import com.yaozu.listener.constant.IntentKey;

/**
 * Created by 耀祖 on 2015/12/5.
 */
public abstract class BaseActivity extends FragmentActivity {
    protected String mSongName;
    protected String mSinger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerPushReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterPushRecevier();
    }

    /**
     * @Description:
     * @author
     * @date 2013-10-28 jieyaozu 10:30:27
     */
    protected void registerPushReceiver() {
        if (musicServiceBroadcastReceiver == null) {
            musicServiceBroadcastReceiver = new MusicServiceBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(IntentKey.NOTIFY_CURRENT_SONG_MSG);
            filter.addAction(IntentKey.NOTIFY_SONG_PAUSE);
            filter.addAction(IntentKey.NOTIFY_SONG_PLAYING);
            localBroadcastManager = LocalBroadcastManager.getInstance(this);
            localBroadcastManager.registerReceiver(musicServiceBroadcastReceiver, filter);
        }
    }

    /**
     * @Description:
     * @author
     * @date 2013-10-28 jieyaozu10:17:28
     */
    protected void unRegisterPushRecevier() {
        if (musicServiceBroadcastReceiver != null) {
            localBroadcastManager = LocalBroadcastManager.getInstance(this);
            localBroadcastManager.unregisterReceiver(musicServiceBroadcastReceiver);
            musicServiceBroadcastReceiver = null;
        }
    }

    private MusicServiceBroadcastReceiver musicServiceBroadcastReceiver;
    /**
     *
     */
    private LocalBroadcastManager localBroadcastManager;

    /**
     * 2015-11-5
     */
    private class MusicServiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (IntentKey.NOTIFY_CURRENT_SONG_MSG.equals(intent.getAction())) {
                mSongName = intent.getStringExtra(IntentKey.MEDIA_FILE_SONG_NAME);
                mSinger = intent.getStringExtra(IntentKey.MEDIA_FILE_SONG_SINGER);
                long album_id = intent.getLongExtra(IntentKey.MEDIA_FILE_SONG_ALBUMID,0);
                int currentPos = intent.getIntExtra(IntentKey.MEDIA_CURRENT_INDEX, -1);
                notifyCurrentSongMsg(mSongName,mSinger,album_id,currentPos);
            } else if (IntentKey.NOTIFY_SONG_PLAYING.equals(intent.getAction())) {
                notifySongPlaying();
            } else if (IntentKey.NOTIFY_SONG_PAUSE.equals(intent.getAction())) {
                notifySongPause();
            }
        }

    }

    public abstract void notifyCurrentSongMsg(String name,String singer,long album_id,int currentPos);
    public abstract void notifySongPlaying();
    public abstract void notifySongPause();
}
