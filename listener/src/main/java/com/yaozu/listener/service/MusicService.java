package com.yaozu.listener.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.yaozu.listener.YaozuApplication;
import com.yaozu.listener.constant.DataInterface;
import com.yaozu.listener.constant.IntentKey;
import com.yaozu.listener.listener.PersonState;
import com.yaozu.listener.playlist.model.Song;
import com.yaozu.listener.utils.NetUtil;
import com.yaozu.listener.utils.User;
import com.yaozu.listener.utils.VolleyHelper;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MusicService extends Service {

    private MediaPlayer mMediaPlayer;
    private YaozuApplication app;

    private AudioManager mAudioManager;
    private final static int SWITCH_NEXT = 2;
    private Song mCurrentSong;
    private int mCurrentIndex;
    private ArrayList<Song> mSongs;
    private PlayState currentState;
    private int NOTIFICATION_ID = -1;
    private User user;

    public MusicService() {

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SWITCH_NEXT:
                    int pos = msg.arg1;
                    playCurrentIndexSong(pos);
                    break;
            }
        }
    };

    private AudioManager.OnAudioFocusChangeListener mAudioFocusListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            //mMediaplayerHandler.obtainMessage(FOCUSCHANGE, focusChange, 0).sendToTarget();
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerConnectReceiver();
        initAuidoData(intent);
        user = new User(app);
        return super.onStartCommand(intent, flags, startId);
    }

    private void initAuidoData(Intent intent) {
        if (intent != null) {
            mCurrentIndex = intent.getIntExtra(IntentKey.MEDIA_CURRENT_INDEX, 0);
            mSongs = (ArrayList<Song>) intent.getSerializableExtra(IntentKey.MEDIA_FILE_LIST);
            playCurrentIndexSong(mCurrentIndex);
        }
    }

    private void playCurrentIndexSong(int currentpos) {
        if (mSongs == null || mSongs.size() <= 0) {
            Toast.makeText(this.getApplication(), "播放列表为空", Toast.LENGTH_SHORT).show();
            return;
        }
        mCurrentIndex = currentpos;
        mCurrentSong = mSongs.get(currentpos);
        prepareToPlay();
    }

    private void prepareToPlay() {
        synchronized (this) {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setWakeMode(MusicService.this, PowerManager.PARTIAL_WAKE_LOCK);
            }
            try {
                String playMediaPath = mCurrentSong.getFileUrl();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setDataSource(playMediaPath);
                mMediaPlayer.prepareAsync();
                notifyState(PlayState.prepareing);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    notifyState(PlayState.prepared);
                    start();
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    notifyState(PlayState.completed);
                }
            });

            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    switch (what) {
                        case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                            mMediaPlayer.release();
                            mMediaPlayer = new MediaPlayer();
                            mMediaPlayer.setWakeMode(MusicService.this, PowerManager.PARTIAL_WAKE_LOCK);
                            break;
                        default:
                            Log.d("MultiPlayer", "Error: " + what + "," + extra);
                            break;
                    }
                    return false;
                }
            });
        }
    }

    protected void notifyState(PlayState state) {
        currentState = state;
        switch (state) {
            case prepareing:
                Intent intent = new Intent(IntentKey.NOTIFY_CURRENT_SONG_MSG);
                intent.putExtra(IntentKey.MEDIA_FILE_SONG_NAME, mCurrentSong.getTitle());
                intent.putExtra(IntentKey.MEDIA_FILE_SONG_SINGER, mCurrentSong.getSinger());
                intent.putExtra(IntentKey.MEDIA_CURRENT_INDEX, mCurrentIndex);
                intent.putExtra(IntentKey.MEDIA_FILE_SONG_ALBUMID, mCurrentSong.getAlbumid());
                //intent.putExtra(IntentKey.MEDIA_FILE_SONG_ALBUM, mCurrentSong.getAlbum());
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(app);
                localBroadcastManager.sendBroadcast(intent);
                break;
            case prepared:
                break;
            case playing:
                Intent playingintent = new Intent(IntentKey.NOTIFY_SONG_PLAYING);
                LocalBroadcastManager playinglocalBroadcastManager = LocalBroadcastManager.getInstance(app);
                playinglocalBroadcastManager.sendBroadcast(playingintent);

                notification();

                //TODO 通知服务器当前用户正在播放歌曲的信息
                notificationServerPersonState(PersonState.PLAYING);
                //保存退出时的歌曲信息
                user.storeQuitSongInfo(mCurrentSong.getTitle(), mCurrentSong.getSinger(), mCurrentIndex);
                break;
            case pause:
                Intent pauseintent = new Intent(IntentKey.NOTIFY_SONG_PAUSE);
                LocalBroadcastManager pauselocalBroadcastManager = LocalBroadcastManager.getInstance(app);
                pauselocalBroadcastManager.sendBroadcast(pauseintent);

                //TODO 通知服务器当前用户正在播放歌曲的信息
                notificationServerPersonState(PersonState.PAUSE);
                break;
            case completed:
                playNextSong();
                break;
        }
    }

    /**
     * 1、包括当前歌曲信息（歌曲名、歌手，歌曲ID），播放状态
     * 2、服务器会通过推送服务把这些状态通知给当前用户的好友们
     *
     * @param state
     */
    private void notificationServerPersonState(PersonState state) {
        String url = null;
        try {
            String songname = mCurrentSong.getTitle();
            String singer = mCurrentSong.getSinger();
            url = DataInterface.getUpdatePersonStateUrl() + "?songname=" + URLEncoder.encode(songname == null ? "" : songname, "UTF-8") + "&singer=" + URLEncoder.encode(singer == null ? "" : singer, "UTF-8") +
                    "&userid=" + User.getUserAccount() + "&songid=" + mCurrentSong.getId() + "&state=" + state.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.d("url", "url===>" + url);
        //TODO
        VolleyHelper.getRequestQueue().add(new JsonObjectRequest(Request.Method.GET,
                url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("MusicService", "response====>" + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("MusicService", "error====>" + error.toString());
                    }
                }));
    }

    private void playNextSong() {
        mCurrentIndex++;
        if (mCurrentIndex >= mSongs.size()) {
            mCurrentIndex = 0;
        }
        stopPlayBack();
        playCurrentIndexSong(mCurrentIndex);
    }

    private void notification() {
/*        String songName;
        // assign the song name to songName
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), HomeMainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification();
        notification.tickerText = "text";
        notification.icon = R.drawable.ic_launcher;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.setLatestEventInfo(getApplicationContext(), mCurrentSong.getTitle(), mCurrentSong.getSinger(), pi);
        startForeground(NOTIFICATION_ID, notification);*/
    }

    public Song getmCurrentSong() {
        return mCurrentSong;
    }

    public int getmCurrentIndex() {
        return mCurrentIndex;
    }

    public ArrayList<Song> getmSongs() {
        return mSongs;
    }

    public void setmSongs(ArrayList<Song> songs) {
        this.mSongs = songs;
    }

    public void start() {
        if (mCurrentSong != null) {
            mMediaPlayer.start();
            notifyState(PlayState.playing);
        }
    }

    public void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            notifyState(PlayState.pause);
        }
    }

    public void stopPlayBack() {
        synchronized (this) {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.reset();

                stopForeground(true);
            }
        }
    }

    public void killMyself() {
        stopPlayBack();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        stopSelf();
    }

    public void switchNextSong(int pos) {
        stopPlayBack();

        mHandler.removeMessages(SWITCH_NEXT);
        Message msg = mHandler.obtainMessage();
        msg.arg1 = pos;
        msg.what = SWITCH_NEXT;
        mHandler.sendMessageDelayed(msg, 500);
    }

    public boolean isPlaying() {
        if (currentState == PlayState.playing) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 得到当前播放器的进度
     *
     * @return
     */
    public int getCurrentPlayPosition() {
        if (currentState == PlayState.playing || currentState == PlayState.pause) {
            return mMediaPlayer.getCurrentPosition();
        }
        return -1;
    }

    /**
     * 获取播放时长
     *
     * @return
     */
    public int getDuration() {
        if (currentState == PlayState.playing || currentState == PlayState.pause) {
            return mMediaPlayer.getDuration();
        }
        return -1;
    }

    public void seekto(int pos) {
        if (currentState == PlayState.playing || currentState == PlayState.pause) {
            if (mMediaPlayer != null) {
                mMediaPlayer.seekTo(pos);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = YaozuApplication.getIntance();
        app.setMusicService(this);
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(mAudioFocusListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        Log.d(this.getClass().getSimpleName(), "=====onDestroy======>");
        //TODO 通知服务器当前用户正在播放歌曲的信息
        notificationServerPersonState(PersonState.PAUSE);
        unregisterReceiver();
        YaozuApplication.getIntance().cleanMusicService();
        super.onDestroy();
    }

    /**
     * 网络变化的监听
     */
    private ConnectionChangeReceiver myReceiver;

    private void registerConnectReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        myReceiver = new ConnectionChangeReceiver();
        this.registerReceiver(myReceiver, filter);
    }

    private void unregisterReceiver() {
        this.unregisterReceiver(myReceiver);
    }

    private boolean hasRegister = false;

    public class ConnectionChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!hasRegister) {
                hasRegister = true;
                return;
            }
            if (NetUtil.hasNetwork(context)) {
                if (isPlaying()) {
                    notificationServerPersonState(PersonState.PLAYING);
                } else {
                    notificationServerPersonState(PersonState.PAUSE);
                }
                //改变背景或者 处理网络的全局变量
            } else {
                //改变背景或者 处理网络的全局变量
            }
        }
    }
}
