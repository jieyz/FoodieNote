package com.yaozu.listener.fragment.music;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.yaozu.listener.Infointerface;
import com.yaozu.listener.R;
import com.yaozu.listener.fragment.BaseFragment;

import java.io.File;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.message.TextMessage;

/**
 * Created by 耀祖 on 2016/1/2.
 */
public class MusicHomeFragment extends BaseFragment implements View.OnClickListener {
    /**
     * 管理Fragment的对象
     */
    private FragmentTransaction mFragmentTransaction;
    private RelativeLayout local, recently, favorate;
    private Infointerface mInfointerface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_home, container, false);
        local = (RelativeLayout) view.findViewById(R.id.fragment_music_home_local_rl);
        recently = (RelativeLayout) view.findViewById(R.id.fragment_music_home_recently_rl);
        favorate = (RelativeLayout) view.findViewById(R.id.fragment_music_home_favorite_rl);

        local.setOnClickListener(this);
        recently.setOnClickListener(this);
        favorate.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mInfointerface = (Infointerface) context;
    }

    @Override
    public void notifyCurrentSongMsg(String name, String singer,long album_id, int currentPos) {

    }

    @Override
    public void notifySongPlaying() {

    }

    @Override
    public void notifySongPause() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_music_home_local_rl:
                mInfointerface.repalace(new MusicLocalFragment());
                break;
            case R.id.fragment_music_home_recently_rl:
                systemScan();
                break;
            case R.id.fragment_music_home_favorite_rl:
                String userid = "jieyaozu";
                RongIM.getInstance().getRongIMClient().sendMessage(Conversation.ConversationType.PRIVATE, userid, TextMessage.obtain("哈哈哈哈！"), "","", new RongIMClient.SendMessageCallback() {
                    @Override
                    public void onError(Integer messageId, RongIMClient.ErrorCode e) {

                    }

                    @Override
                    public void onSuccess(Integer integer) {

                        Log.d("MusicHomeFragment","=========onSuccess==========>"+integer);
                    }
                },null);
                break;
        }
    }

    public void systemScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            folderScan(Environment.getExternalStorageDirectory().getPath() + File.separator + "KuwoMusic" + File.separator + "music");
        } else {
            ((Activity) mInfointerface).sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
                    + Environment.getExternalStorageDirectory())));
        }
    }

    public void fileScan(String file) {
        Uri data = Uri.parse("file://" + file);
        System.out.println("===file===>" + file);
        ((Activity) mInfointerface).sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
    }

    public void folderScan(String path) {
        File file = new File(path);

        if (file.isDirectory()) {
            File[] array = file.listFiles();

            for (int i = 0; i < array.length; i++) {
                File f = array[i];

                if (f.isFile()) {//FILE TYPE
                    String name = f.getName();

                    if (name.contains(".mp3") || name.contains(".acc")) {
                        fileScan(f.getAbsolutePath());
                    }
                } else {//FOLDER TYPE
                    folderScan(f.getAbsolutePath());
                }
            }
        }
    }

}
