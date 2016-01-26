package com.yaozu.listener.fragment.social;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yaozu.listener.R;
import com.yaozu.listener.activity.LoginActivity;
import com.yaozu.listener.constant.Constant;
import com.yaozu.listener.fragment.BaseFragment;
import com.yaozu.listener.utils.User;

/**
 * Created by 耀祖 on 2015/12/5.
 */
public class MyselfFragment extends BaseFragment {
    private Button mQuit;
    private SharedPreferences sp;
    private User mUser;
    private TextView mAccount_view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mQuit = (Button) view.findViewById(R.id.fragment_social_mine_quit);
        mAccount_view = (TextView) view.findViewById(R.id.fragment_social_mine_account);
        initData();
    }

    private void initData(){
        mUser = new User(getActivity());
        mAccount_view.setText(mUser.getUserAccount());
        mQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUser.quitLogin();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sp = getActivity().getSharedPreferences(Constant.LOGIN_MSG, Context.MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_social_my, container, false);
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
}
