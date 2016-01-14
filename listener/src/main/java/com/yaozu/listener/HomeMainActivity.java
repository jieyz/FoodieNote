package com.yaozu.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.yaozu.listener.activity.BaseActivity;
import com.yaozu.listener.activity.MusicLyricActivity;
import com.yaozu.listener.constant.IntentKey;
import com.yaozu.listener.fragment.HomeFragment;
import com.yaozu.listener.fragment.music.MusicLocalFragment;
import com.yaozu.listener.fragment.OnFragmentInteractionListener;
import com.yaozu.listener.playlist.model.SongList;
import com.yaozu.listener.playlist.provider.JavaMediaScanner;
import com.yaozu.listener.playlist.provider.NativeMediaScanner;
import com.yaozu.listener.service.MusicService;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;


public class HomeMainActivity extends BaseActivity implements View.OnClickListener, OnFragmentInteractionListener, Infointerface {
    private RadioButton mRadioFirst, mRadioSecond, mRadioThird, mRadioFour;
    private FragmentManager mFragmentManager;
    private String mCurrentFragmentTag;
    private ImageButton music_home;
    //播放或者暂停的按钮
    private ImageView mPlayPause;
    private YaozuApplication app;

    private TextView mCurrentSongName;
    private TextView mCurrentSinger;
    private RelativeLayout mShowController;
    private ImageView mMusicPhoto;
    private ImageView mActionbarShadow;
    private FragmentTransaction mFragmentTransaction;
    private Fragment mCurrentFragment;
    private JavaMediaScanner mMediaScanner;

    static{
        System.loadLibrary("mediascanner");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = YaozuApplication.getIntance();
        setContentView(R.layout.activity_home_main);
        mFragmentManager = getSupportFragmentManager();
        findViewByIds();
        setOnclickLisener();
        //注册监听耳机拔除的广播
        registerHeadsetPlugReceiver();
        try {
            NativeMediaScanner scanner = new NativeMediaScanner();
            scanner.processFile(new String("/storage/emulated/0/KuwoMusic/music/红日(Live)-李克勤_谭咏麟.mp3".getBytes(), "UTF-8"), scanner);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mFragmentTransaction = mFragmentManager.beginTransaction();
        Fragment currentFragment = new HomeFragment();
        mFragmentTransaction.add(R.id.main_fragment_container, currentFragment, MusicLocalFragment.class.getSimpleName());
        mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.commit();
        mCurrentFragment = currentFragment;

        mMediaScanner = new JavaMediaScanner(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mMediaScanner.scannerMedia();
            }
        }).start();
/*        MusicService service = app.getMusicService();
        if (service == null) {
            Intent intent = new Intent(this, MusicService.class);
            startService(intent);
        }*/
    }

    private void findViewByIds() {
        mPlayPause = (ImageView) findViewById(R.id.mediaplay_play_pause);
        mCurrentSongName = (TextView) findViewById(R.id.current_songname);
        mCurrentSinger = (TextView) findViewById(R.id.current_songsinger);
        mShowController = (RelativeLayout) findViewById(R.id.main_play_layout);
        mMusicPhoto = (ImageView) findViewById(R.id.main_music_photo);
        mActionbarShadow = (ImageView) findViewById(R.id.main_actionbar_shadow);
    }

    private void setOnclickLisener() {
        mPlayPause.setOnClickListener(this);
        mShowController.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        MusicService service = app.getMusicService();
        switch (v.getId()) {
            case R.id.mediaplay_play_pause:
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
                Intent intent = new Intent(HomeMainActivity.this, MusicLyricActivity.class);
                if (service != null) {
                    intent.putExtra(IntentKey.MEDIA_FILE_SONG_NAME, service.getmCurrentSong().getTitle());
                    intent.putExtra(IntentKey.MEDIA_FILE_SONG_SINGER, service.getmCurrentSong().getSinger());
                }
                startActivity(intent);
                overridePendingTransition(R.anim.music_lyric_bottom_in, R.anim.music_lyric_out);
                break;
        }
    }

    private void requestNet() {
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
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void repalace(Fragment mFragment) {
        mFragmentTransaction = mFragmentManager.beginTransaction();
        if(mCurrentFragment != mFragment){
            mFragmentTransaction.hide(mCurrentFragment);
        }
        mFragmentTransaction.add(R.id.main_fragment_container, mFragment);
        mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.commit();
    }

    public void showTopFragment(){
        List<Fragment> fragments = mFragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            mFragmentTransaction.show(fragment);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (app != null && app.getMusicService() != null) {
            app.getMusicService().killMyself();
            app.cleanMusicService();
        }
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
        if (this.getSupportFragmentManager().getBackStackEntryCount() == 1) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                moveTaskToBack(false);
                return true;
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 监听耳机的拔除的广播
     */
    private void registerHeadsetPlugReceiver() {
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(headsetPlugReceiver, intentFilter);
    }

    private BroadcastReceiver headsetPlugReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(action)) {
                MusicService service = app.getMusicService();
                if(service.isPlaying()){
                    service.pause();
                }
            }
        }

    };

    @Override
    public void notifyCurrentSongMsg(String name, String singer, long album_id,int currentPos) {
        mCurrentSongName.setText(name);
        mCurrentSinger.setText(singer);
        //Bitmap bitmap = mMediaScanner.getImage(this, (int) album_id);
    }

    @Override
    public void notifySongPlaying() {
        mPlayPause.setImageResource(R.drawable.playbar_btn_pause);
    }

    @Override
    public void notifySongPause() {
        mPlayPause.setImageResource(R.drawable.playbar_btn_play);
    }
}
