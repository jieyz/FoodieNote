package com.yaozu.listener.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.yaozu.listener.R;
import com.yaozu.listener.constant.IntentKey;
import com.yaozu.listener.db.model.ChatListInfo;

/**
 * Created by 耀祖 on 2015/11/28.
 */
public abstract class BaseFragment extends Fragment {
    private View progressBar;

    public BaseFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerPushReceiver();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.base_layout);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterPushRecevier();
    }

    protected void showProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    protected void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
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
            localBroadcastManager = LocalBroadcastManager.getInstance(this.getActivity());
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
            localBroadcastManager = LocalBroadcastManager.getInstance(this.getActivity());
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
                String mSongName = intent.getStringExtra(IntentKey.MEDIA_FILE_SONG_NAME);
                String mSinger = intent.getStringExtra(IntentKey.MEDIA_FILE_SONG_SINGER);
                int currentPos = intent.getIntExtra(IntentKey.MEDIA_CURRENT_INDEX, -1);
                long album_id = intent.getLongExtra(IntentKey.MEDIA_FILE_SONG_ALBUMID, 0);
                notifyCurrentSongMsg(mSongName, mSinger, album_id, currentPos);
            } else if (IntentKey.NOTIFY_SONG_PLAYING.equals(intent.getAction())) {
                notifySongPlaying();
            } else if (IntentKey.NOTIFY_SONG_PAUSE.equals(intent.getAction())) {
                notifySongPause();
            } else if (IntentKey.NOTIFY_CHAT_LIST_INFO.equals(intent.getAction())) {
                Bundle bundle = intent.getBundleExtra(IntentKey.SEND_BUNDLE);
                ChatListInfo chatListInfo = (ChatListInfo) bundle.getSerializable(IntentKey.SEND_BUNDLE_CHATLISTINFO);
                updateChatListInfo(chatListInfo);
            }
        }

    }

    public abstract void notifyCurrentSongMsg(String name, String singer, long album_id, int currentPos);

    public abstract void notifySongPlaying();

    public abstract void notifySongPause();

    public void updateChatListInfo(ChatListInfo info) {

    }
}
