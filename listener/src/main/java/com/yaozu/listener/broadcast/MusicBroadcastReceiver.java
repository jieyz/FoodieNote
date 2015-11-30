package com.yaozu.listener.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by 耀祖 on 2015/9/22.
 */
public class MusicBroadcastReceiver extends BroadcastReceiver {
    private String artistName;
    private String album;
    private String track;
    private boolean playing;
    private long duration;
    private long position;

    @Override
    public void onReceive(Context context, Intent intent) {
        artistName = intent.getStringExtra("artist");
        album = intent.getStringExtra("album");
        track = intent.getStringExtra("track");
        playing = intent.getBooleanExtra("playing", false);
        duration = intent.getLongExtra("duration", 3000);
        position = intent.getLongExtra("position",1000);
        Toast.makeText(context, "playing=" + playing+" album="+album+" artistName="+artistName, Toast.LENGTH_SHORT).show();
    }
}
