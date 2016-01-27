package com.yaozu.listener.fragment.social;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yaozu.listener.R;
import com.yaozu.listener.activity.LoginActivity;
import com.yaozu.listener.constant.Constant;
import com.yaozu.listener.fragment.BaseFragment;
import com.yaozu.listener.utils.User;

/**
 * Created by 耀祖 on 2015/12/5.
 */
public class MyselfFragment extends BaseFragment implements View.OnClickListener {
    private RelativeLayout mQuit;
    //昵称
    private RelativeLayout nickName;
    private SharedPreferences sp;
    private User mUser;
    private TextView mAccount_view;
    private RelativeLayout userInfoRl;
    private Dialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mQuit = (RelativeLayout) view.findViewById(R.id.fragment_social_mine_quit);
        mAccount_view = (TextView) view.findViewById(R.id.fragment_social_mine_account);
        userInfoRl = (RelativeLayout) view.findViewById(R.id.fragment_social_mine_userinfo_rl);
        nickName = (RelativeLayout) view.findViewById(R.id.fragment_social_mine_nickname_rl);
        initData();
    }

    private void initData() {
        mUser = new User(getActivity());
        mAccount_view.setText(mUser.getUserAccount());
        mQuit.setOnClickListener(this);
        userInfoRl.setOnClickListener(this);
        nickName.setOnClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sp = getActivity().getSharedPreferences(Constant.LOGIN_MSG, Context.MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_social_my, container, false);
    }

    private void showDialog(){
        dialog = new Dialog(this.getActivity(), R.style.NobackDialog);
        View view = View.inflate(getActivity(),R.layout.quit_dialog,null);
        LinearLayout quitAccount = (LinearLayout) view.findViewById(R.id.quit_dialog_quitaccount);
        LinearLayout quitApp = (LinearLayout) view.findViewById(R.id.quit_dialog_quitapp);
        quitAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mUser.quitLogin();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });
        quitApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }

    @Override
    public void notifyCurrentSongMsg(String name, String singer, long album_id, int currentPos) {

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
            case R.id.fragment_social_mine_userinfo_rl:

                break;
            case R.id.fragment_social_mine_quit:
                showDialog();
                break;
            case R.id.fragment_social_mine_nickname_rl:

                break;
        }
    }
}
