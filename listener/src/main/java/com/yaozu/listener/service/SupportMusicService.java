package com.yaozu.listener.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.yaozu.listener.YaozuApplication;
import com.yaozu.listener.constant.DataInterface;
import com.yaozu.listener.dao.NetDao;
import com.yaozu.listener.playlist.model.Song;

import org.json.JSONObject;

import java.io.IOException;

import io.vov.vitamio.MediaPlayer;


/**
 * Created by jieyaozu on 2016/3/27.
 */
public class SupportMusicService extends Service {
    private Song mCurrentSong;
    private MediaPlayer mMediaPlayer;
    private String TAG = this.getClass().getSimpleName();

    public SupportMusicService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        YaozuApplication.getIntance().setSupportMusicService(this);
    }

    private void prepareToPlay() {
        synchronized (this) {
            mMediaPlayer = new MediaPlayer(this);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    Log.d("SupportMusicService", "====onPrepared===");
                    mMediaPlayer.start();
                }
            });
            try {
                String playMediaPath = mCurrentSong.getFileUrl();
                //mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                Log.d("SupportMusicService", "playMediaPath: " + playMediaPath);
                mMediaPlayer.setDataSource(playMediaPath);
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public long getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public void stopPlayBack() {
        Log.d("SupportMusicService", "====stopPlayBack===");
        synchronized (this) {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.reset();

                stopForeground(true);
            }
        }
    }

    /**
     * 播放指定的歌曲
     * 有可能是本地的也有可能是网络上的
     * 需要清空播放列表
     *
     * @param song
     */
    public void playSong(Song song) {
        stopPlayBack();
        this.mCurrentSong = song;
        prepareToPlay();
    }

    /**
     * 播放非本地的歌曲
     *
     * @param song 只需要保证title、singer有值就可以了
     */
    public void playSongFromServer(final Song song) {
        NetDao.getPlaySongEncodeFileName(song, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(response.toString());
                int code = jsonObject.getIntValue("code");
                if (code == 0) {//歌曲存在
                    String encodeFileName = jsonObject.getString("encodefilename");
                    Log.d(TAG, "=====playUrl=====>" + (DataInterface.getPlaySongEncodeUrl() + encodeFileName));
                    song.setFileUrl(DataInterface.getPlaySongEncodeUrl() + encodeFileName);
                    playSong(song);
                } else {//歌曲不存在

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    /**
     * 播放非本地的歌曲
     *
     * @param songname
     * @param singer
     */
    public void playSongFromServer(String songname, String singer) {
        Song song = new Song();
        song.setTitle(songname);
        song.setSinger(singer);
        playSongFromServer(song);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
