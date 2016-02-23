package com.yaozu.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.yaozu.listener.activity.BaseActivity;
import com.yaozu.listener.activity.MusicLyricActivity;
import com.yaozu.listener.constant.DataInterface;
import com.yaozu.listener.constant.IntentKey;
import com.yaozu.listener.fragment.HomeFragment;
import com.yaozu.listener.fragment.music.MusicLocalFragment;
import com.yaozu.listener.fragment.OnFragmentInteractionListener;
import com.yaozu.listener.listener.MyReceiveMessageListener;
import com.yaozu.listener.listener.MyReceivePushMessageListener;
import com.yaozu.listener.listener.MySendMessageListener;
import com.yaozu.listener.playlist.model.SongList;
import com.yaozu.listener.playlist.provider.JavaMediaScanner;
import com.yaozu.listener.playlist.provider.NativeMediaScanner;
import com.yaozu.listener.service.MusicService;
import com.yaozu.listener.utils.PhoneInfoUtil;
import com.yaozu.listener.utils.User;
import com.yaozu.listener.utils.VolleyHelper;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;


public class HomeMainActivity extends BaseActivity implements View.OnClickListener, OnFragmentInteractionListener, Infointerface {
    private String TAG = this.getClass().getSimpleName();
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
    private final int HAS_OTHERLOGIN = 1;
    private final int NOT_OTHERLOGIN = 0;
    //是否已连接到融云服务器上
    private boolean hasConnectToRongIM = false;

    static {
        System.loadLibrary("mediascanner");
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HAS_OTHERLOGIN:
                    Intent loginOutIntent = new Intent(IntentKey.NOTIFY_LOGIN_OUT);
                    LocalBroadcastManager playinglocalBroadcastManager = LocalBroadcastManager.getInstance(app);
                    playinglocalBroadcastManager.sendBroadcast(loginOutIntent);
                    break;
                case NOT_OTHERLOGIN:
                    if (!hasConnectToRongIM) {
                        /**
                         *  设置接收消息的监听器。
                         */
                        RongIM.setOnReceiveMessageListener(new MyReceiveMessageListener(HomeMainActivity.this));
                        /**
                         * 设置接收 push 消息的监听器。
                         */
                        RongIM.setOnReceivePushMessageListener(new MyReceivePushMessageListener(HomeMainActivity.this));

                        connect(getIntent().getStringExtra("token"));
                        hasConnectToRongIM = true;
                    }
                    break;
            }
        }
    };

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

        /////////动态注册广播
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(myNetReceiver, mFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //先从服务器上去判断是不是在另一台设备上登录
        isOtherLogin(mUser.getUserAccount());
    }

    private ConnectivityManager mConnectivityManager;

    private NetworkInfo netInfo;

    /////////////监听网络状态变化的广播接收器
    private BroadcastReceiver myNetReceiver = new BroadcastReceiver() {
        private int count = 0;

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

                mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                netInfo = mConnectivityManager.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isAvailable()) {
                    Log.d(TAG, "========网络连接=========>");
                    if (count != 0) {
                        isOtherLogin(mUser.getUserAccount());
                    }
                    count++;
                    /////////////网络连接
                    String name = netInfo.getTypeName();
                    if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        /////WiFi网络

                    } else if (netInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
                        /////有线网络

                    } else if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        /////////3g网络
                    }
                } else {
                    ////////网络断开
                    Log.d(TAG, "========网络断开=========>");
                }
            }
        }
    };

    /**
     * 建立与融云服务器的连接
     *
     * @param token
     */
    private void connect(String token) {

        if (getApplicationInfo().packageName.equals(YaozuApplication.getCurProcessName(getApplicationContext()))) {

            /**
             * IMKit SDK调用第二步,建立与服务器的连接
             */
            RongIM.connect(token, new RongIMClient.ConnectCallback() {

                /**
                 * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
                 */
                @Override
                public void onTokenIncorrect() {

                    Log.d("LoginActivity", "--onTokenIncorrect");
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token
                 */
                @Override
                public void onSuccess(String userid) {
                    //设置自己发出的消息监听器。
                    RongIM.getInstance().setSendMessageListener(new MySendMessageListener());
                    Log.d("HomeMainActivity", "--onSuccess" + userid);
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                    Log.d("LoginActivity", "--onError" + errorCode);
                }
            });
        }
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
        if (mCurrentFragment != mFragment) {
            mFragmentTransaction.hide(mCurrentFragment);
        }
        mFragmentTransaction.add(R.id.main_fragment_container, mFragment);
        mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.commit();
    }

    public void showTopFragment() {
        List<Fragment> fragments = mFragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            mFragmentTransaction.show(fragment);
        }
    }

    /**
     * 是否是在另一台设备上登录
     */
    private boolean isOtherLogin(String userid) {
        boolean isOtherLogin = false;
        PhoneInfoUtil phoneInfoUtil = new PhoneInfoUtil(this);
        String url = DataInterface.getIsOtherLoginUrl() + "?userid=" + userid + "&deviceid=" + phoneInfoUtil.getDeviceId();
        Log.d(TAG, "isOtherLogin url : " + url);
        Request request = new JsonObjectRequest(Request.Method.GET,
                url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "response : " + response.toString());
                        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(response.toString());
                        int code = jsonObject.getIntValue("code");
                        if (code == HAS_OTHERLOGIN) {
                            Log.d(TAG, "已在其它设备上登录过了，需要重新登录");
                            Message msg = mHandler.obtainMessage();
                            msg.what = HAS_OTHERLOGIN;
                            mHandler.sendMessage(msg);
                        } else {
                            Log.d(TAG, "没有在其它设备上登录过");
                            Message msg = mHandler.obtainMessage();
                            msg.what = NOT_OTHERLOGIN;
                            mHandler.sendMessage(msg);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(HomeMainActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                Message msg = mHandler.obtainMessage();
                msg.what = NOT_OTHERLOGIN;
                mHandler.sendMessage(msg);
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyHelper.getRequestQueue().add(request);
        return isOtherLogin;
    }

    @Override
    protected void onDestroy() {
        if (app != null && app.getMusicService() != null) {
            app.getMusicService().killMyself();
            app.cleanMusicService();
        }

        if (myNetReceiver != null) {
            unregisterReceiver(myNetReceiver);
        }

        if (headsetPlugReceiver != null) {
            unregisterReceiver(headsetPlugReceiver);
        }
        super.onDestroy();
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
                if (service.isPlaying()) {
                    service.pause();
                }
            }
        }

    };

    @Override
    public void notifyCurrentSongMsg(String name, String singer, long album_id, int currentPos) {
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
