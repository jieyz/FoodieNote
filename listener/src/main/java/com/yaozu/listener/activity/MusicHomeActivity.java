package com.yaozu.listener.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.SeekBar;

import com.yaozu.listener.R;
import com.yaozu.listener.YaozuApplication;
import com.yaozu.listener.service.MusicService;

/**
 * Created by jieyz on 2015/9/29.
 */
public class MusicHomeActivity extends Activity {
    private SeekBar mSeekBar;
    private MediaSeekBarChangeListener seekBarChangeListener;
    private MusicService mService;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setProgress();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_home);
        findviewByids();
        mService = YaozuApplication.getIntance().getMusicService();
        setProgress();
    }

    private void findviewByids() {
        mSeekBar = (SeekBar) findViewById(R.id.media_seekbar);
        seekBarChangeListener = new MediaSeekBarChangeListener();
        mSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
    }

    private class MediaSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int pos = (int) (((float) seekBar.getProgress() / 1000f) * mService.getDuration());
            mService.seekto(pos);
        }
    }

    private void setProgress() {
        if (mService == null) {
            return;
        }
        int currentpos = mService.getCurrentPlayPosition();
        int duration = mService.getDuration();
        if (duration != -1) {
            int pos = (int) (((float) currentpos / (float) duration) * 1000);
            mSeekBar.setProgress(pos);
        }

        mHandler.sendMessageDelayed(mHandler.obtainMessage(0), 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(0);
    }
}
