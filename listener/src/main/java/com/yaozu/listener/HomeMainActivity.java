package com.yaozu.listener;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.yaozu.listener.constant.IntentKey;
import com.yaozu.listener.fragment.LocalFragment;
import com.yaozu.listener.fragment.OnFragmentInteractionListener;
import com.yaozu.listener.playlist.model.SongList;
import com.yaozu.listener.service.MusicService;

import org.json.JSONObject;


public class HomeMainActivity extends Activity implements View.OnClickListener, OnFragmentInteractionListener {
    private RadioButton mRadioFirst, mRadioSecond, mRadioThird, mRadioFour;
    private FragmentManager mFragmentManager;
    private String mCurrentFragmentTag;
    private ImageButton music_home;
    //虏楼路脜禄貌脮脽脭脻脥拢掳麓脜楼
    private ImageView mPlayPause;
    private YaozuApplication app;

    private TextView mCurrentSongName;
    private TextView mCurrentSinger;
    private RelativeLayout mShowController;
    private ImageView mMusicPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = YaozuApplication.getIntance();
        setContentView(R.layout.activity_home_main);
        mFragmentManager = getFragmentManager();
        findViewByIds();
        setOnclickLisener();

        FragmentTransaction tr = mFragmentManager.beginTransaction();
        tr.add(R.id.main_fragment_container, new LocalFragment(), LocalFragment.class.getSimpleName());
        tr.commit();

/*        MusicService service = app.getMusicService();
        if (service == null) {
            Intent intent = new Intent(this, MusicService.class);
            startService(intent);
        }*/
        registerPushReceiver();
    }

    private void findViewByIds() {
        mPlayPause = (ImageView) findViewById(R.id.mediaplay_play_pause);
        mCurrentSongName = (TextView) findViewById(R.id.current_songname);
        mCurrentSinger = (TextView) findViewById(R.id.current_songsinger);
        mShowController = (RelativeLayout) findViewById(R.id.main_play_layout);
        mMusicPhoto = (ImageView) findViewById(R.id.main_music_photo);
    }

    private void setOnclickLisener() {
        mPlayPause.setOnClickListener(this);
        mShowController.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mediaplay_play_pause:
                MusicService service = app.getMusicService();
                if (service == null) {
                    Intent intent = new Intent(this, MusicService.class);
                    startService(intent);
                } else {
                    if (service.isPlaying()) {
                        service.pause();
                    } else {
                        service.start();
                    }
                }
                break;
            case R.id.main_play_layout:
                RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
                mQueue.add(new JsonObjectRequest(Request.Method.GET,
                        "http://120.27.129.229:8080/MyApp/index.html",
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //Log.d(TAG, "response : " + response.toString());
                                SongList songList = JSON.parseObject(response.toString(), SongList.class);
                                Toast.makeText(HomeMainActivity.this, songList.getTotalcount(), Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }));
                mQueue.start();
                break;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(app != null && app.getMusicService() != null){
            app.getMusicService().killMyself();
            app.cleanMusicService();
        }
        unRegisterPushRecevier();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * @Description: 脳垄虏谩脧没脧垄鹿茫虏楼陆脫脢脮脌脿
     * @author 陆脪脪芦脳忙
     * @date 2013-10-28 脡脧脦莽10:30:27
     */

    public void registerPushReceiver() {
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
     * @Description: 脳垄脧煤脥脝脣脥陆脫脢脺脮脽
     * @author 陆脪脪芦脳忙
     * @date 2013-10-28 脡脧脦莽10:17:28
     */
    private void unRegisterPushRecevier() {
        if (musicServiceBroadcastReceiver != null) {
            localBroadcastManager = LocalBroadcastManager.getInstance(this);
            localBroadcastManager.unregisterReceiver(musicServiceBroadcastReceiver);
            musicServiceBroadcastReceiver = null;
        }
    }

    private MusicServiceBroadcastReceiver musicServiceBroadcastReceiver;
    /**
     * 卤戮碌脴鹿茫虏楼鹿脺脌铆脌脿
     */
    private LocalBroadcastManager localBroadcastManager;

    /**
     * 脌脿脙猫脢枚拢潞 脫脙脫脷脧没脧垄脥脝脣脥鹿茫虏楼碌脛陆脫脢脮 麓麓陆篓脠脣拢潞 陆脪脪芦脳忙 麓麓陆篓脢卤录盲拢潞 2015-11-5
     */
    private class MusicServiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (IntentKey.NOTIFY_CURRENT_SONG_MSG.equals(intent.getAction())) {
                String songName = intent.getStringExtra(IntentKey.MEDIA_FILE_SONG_NAME);
                String songSinger = intent.getStringExtra(IntentKey.MEDIA_FILE_SONG_SINGER);
                int currentPos = intent.getIntExtra(IntentKey.MEDIA_CURRENT_INDEX, -1);
                mCurrentSongName.setText(songName);
                mCurrentSinger.setText(songSinger);
                LocalFragment localFragment = (LocalFragment) mFragmentManager.findFragmentByTag(LocalFragment.class.getSimpleName());
                localFragment.highLightPlayingItem(currentPos);
//                long songid = intent.getLongExtra(IntentKey.MEDIA_FILE_SONG_ID, -1);
//                long albumid = intent.getLongExtra(IntentKey.MEDIA_FILE_SONG_ALBUMID, -1);
//                Bitmap bmp = AudioProvider.getArtwork(HomeMainActivity.this, songid, albumid);
//                mMusicPhoto.setImageBitmap(bmp);
            }else if(IntentKey.NOTIFY_SONG_PLAYING.equals(intent.getAction())){
                LocalFragment localFragment = (LocalFragment) mFragmentManager.findFragmentByTag(LocalFragment.class.getSimpleName());
                localFragment.start();
                mPlayPause.setImageResource(R.drawable.phone_playing_pressed);
            }else if(IntentKey.NOTIFY_SONG_PAUSE.equals(intent.getAction())){
                LocalFragment localFragment = (LocalFragment) mFragmentManager.findFragmentByTag(LocalFragment.class.getSimpleName());
                localFragment.pause();
                mPlayPause.setImageResource(R.drawable.phone_play_pressed);
            }
        }

    }
}
