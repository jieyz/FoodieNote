package com.yaozu.listener.fragment.music;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.yaozu.listener.Infointerface;
import com.yaozu.listener.R;
import com.yaozu.listener.fragment.BaseFragment;

/**
 * Created by 耀祖 on 2016/1/2.
 */
public class MusicHomeFragment extends BaseFragment implements View.OnClickListener {
    /** 管理Fragment的对象 */
    private FragmentTransaction mFragmentTransaction;
    private RelativeLayout local,recently,favorate;
    private Infointerface mInfointerface;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_music_home, container, false);
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
    public void notifyCurrentSongMsg(String name, String singer, int currentPos) {

    }

    @Override
    public void notifySongPlaying() {

    }

    @Override
    public void notifySongPause() {

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.fragment_music_home_local_rl:
                mInfointerface.repalace(new MusicLocalFragment());
                break;
            case R.id.fragment_music_home_recently_rl:
                break;
            case R.id.fragment_music_home_favorite_rl:
                break;
        }
    }
}
