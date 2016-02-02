package com.yaozu.listener.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.yaozu.listener.R;
import com.yaozu.listener.YaozuApplication;
import com.yaozu.listener.constant.IntentKey;
import com.yaozu.listener.db.model.ChatDetailInfo;
import com.yaozu.listener.db.model.ChatListInfo;
import com.yaozu.listener.utils.User;
import com.yaozu.listener.utils.VolleyHelper;

import java.util.Map;
import java.util.Set;

/**
 * Created by 耀祖 on 2015/12/5.
 */
public abstract class BaseActivity extends FragmentActivity {
    protected String mSongName;
    protected String mSinger;
    private Dialog dialog;
    public User mUser;
    private boolean isVisibilty = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerPushReceiver();
        mUser = new User(this);
        YaozuApplication.mActivitys.put(this, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        YaozuApplication.mActivitys.remove(this);
        unRegisterPushRecevier();
        VolleyHelper.getRequestQueue().cancelAll(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        YaozuApplication.mActivitys.put(this, true);
        isVisibilty = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        YaozuApplication.mActivitys.put(this, false);
        isVisibilty = false;
    }

    /**
     * 弹出登出对话框
     */
    private void showLoginOutDialog() {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new Dialog(this, R.style.NobackDialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_SEARCH) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        View view = View.inflate(this, R.layout.loginout_dialog, null);
        LinearLayout quitApp = (LinearLayout) view.findViewById(R.id.loginout_dialog_quitapp);
        quitApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mUser.quitLogin();
                Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                startActivity(intent);
                //关掉所有的其它Activity
                Set<Map.Entry<BaseActivity, Boolean>> entries = YaozuApplication.mActivitys.entrySet();
                for (Map.Entry<BaseActivity, Boolean> entry : entries) {
                    BaseActivity activity = entry.getKey();
                    if (!(activity instanceof LoginActivity)) {
                        activity.finish();
                    }
                }
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }

    /**
     * 退出应用
     */
    protected void exitApp() {
        Set<Map.Entry<BaseActivity, Boolean>> entries = YaozuApplication.mActivitys.entrySet();
        for (Map.Entry<BaseActivity, Boolean> entry : entries) {
            BaseActivity activity = entry.getKey();
            activity.finish();
        }
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
            filter.addAction(IntentKey.NOTIFY_CHAT_LIST_INFO);
            filter.addAction(IntentKey.NOTIFY_LOGIN_OUT);
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
                long album_id = intent.getLongExtra(IntentKey.MEDIA_FILE_SONG_ALBUMID, 0);
                int currentPos = intent.getIntExtra(IntentKey.MEDIA_CURRENT_INDEX, -1);
                notifyCurrentSongMsg(mSongName, mSinger, album_id, currentPos);
            } else if (IntentKey.NOTIFY_SONG_PLAYING.equals(intent.getAction())) {
                notifySongPlaying();
            } else if (IntentKey.NOTIFY_SONG_PAUSE.equals(intent.getAction())) {
                notifySongPause();
            } else if (IntentKey.NOTIFY_CHAT_LIST_INFO.equals(intent.getAction())) {
                Bundle bundle = intent.getBundleExtra(IntentKey.SEND_BUNDLE);
                ChatDetailInfo chatDetailInfo = (ChatDetailInfo) bundle.getSerializable(IntentKey.SEND_BUNDLE_CHATDETAILINFO);
                updateChatDetailInfo(chatDetailInfo);
            } else if (IntentKey.NOTIFY_LOGIN_OUT.equals(intent.getAction())) {
                if (isVisibilty) {
                    showLoginOutDialog();
                }
            }
        }

    }

    public abstract void notifyCurrentSongMsg(String name, String singer, long album_id, int currentPos);

    public abstract void notifySongPlaying();

    public abstract void notifySongPause();

    public void updateChatDetailInfo(ChatDetailInfo chatDetailInfo) {

    }
}
