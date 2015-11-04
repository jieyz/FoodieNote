package com.yaozu.foodienote.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import com.yaozu.foodienote.YaozuApplication;
import com.yaozu.foodienote.constant.IntentKey;
import com.yaozu.foodienote.playlist.model.Song;
import com.yaozu.foodienote.playlist.provider.AudioProvider;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MusicService extends Service {

    private MediaPlayer mMediaPlayer;
    private String mCurrentMediaPath;
    private YaozuApplication app;

    private AudioManager am;
    private int mVolume = 10;
    private final static int MUSIC_START = 0;
    private final static int MUSIC_PAUSE = 1;

    public MusicService() {

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MUSIC_START:
                    if (mVolume < 10) {
                        mVolume++;
                        float volume = (float) mVolume / 10.0f;
                        mMediaPlayer.setVolume(volume, volume);
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MUSIC_START), 100);
                    }
                    break;
                case MUSIC_PAUSE:
                    if (mVolume > 0) {
                        mVolume--;
                        float volume = (float) mVolume / 10.0f;
                        mMediaPlayer.setVolume(volume, volume);
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MUSIC_PAUSE), 100);
                    } else {
                        mMediaPlayer.pause();
                    }
                    break;
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("=============onStartCommand==============>");
        initAuidoData(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void initAuidoData(Intent intent) {
        mCurrentMediaPath = intent.getStringExtra(IntentKey.MEDIA_FILE_URL);
        if (!TextUtils.isEmpty(mCurrentMediaPath)) {
            prepareToPlay();
        }
    }

    public void setMediaPathAndPrepare(String mediaPath) {
        mCurrentMediaPath = mediaPath;
        prepareToPlay();
    }

    private void prepareToPlay() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }
        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(mCurrentMediaPath);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mMediaPlayer.start();
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mMediaPlayer.release();
            }
        });
    }

    public void start() {
        if (!TextUtils.isEmpty(mCurrentMediaPath)) {
            mMediaPlayer.start();
            Message msg = mHandler.obtainMessage();
            msg.what = MUSIC_START;
            mHandler.sendMessageDelayed(msg, 50);
        }
    }

    public void pause() {
        mVolume = 10;
        Message msg = mHandler.obtainMessage();
        msg.what = MUSIC_PAUSE;
        mHandler.sendMessageDelayed(msg, 50);
    }

    public void stopPlayBack(){
        if(mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public boolean isPlaying() {
        if (!TextUtils.isEmpty(mCurrentMediaPath)) {
            return mMediaPlayer.isPlaying();
        } else {
            return false;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = YaozuApplication.getIntance();
        app.setMusicService(this);
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
