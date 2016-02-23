package com.yaozu.listener.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yaozu.listener.R;
import com.yaozu.listener.constant.Constant;
import com.yaozu.listener.constant.IntentKey;
import com.yaozu.listener.db.dao.FriendDao;
import com.yaozu.listener.utils.NetUtil;
import com.yaozu.listener.widget.RoundCornerImageView;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.message.TextMessage;

/**
 * Created by jieyaozu on 2016/2/20.
 */
public class UserDetailActivity extends BaseActivity implements View.OnClickListener {
    private TextView userBeizhuName, userId, userName;
    private TextView addOrSend;
    private ImageView actionBack;
    private RoundCornerImageView userIcon;

    private String name, str_userid, token, iconUrl;
    private Dialog dialog;
    //通讯录的数据库
    private FriendDao friendDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        findViewByIds();

        Intent intent = getIntent();
        name = intent.getStringExtra(IntentKey.USER_NAME);
        str_userid = intent.getStringExtra(IntentKey.USER_ID);
        token = intent.getStringExtra(IntentKey.USER_TOKEN);
        iconUrl = intent.getStringExtra(IntentKey.USER_ICON_URL);

        userName.setText(name);
        userId.setText("账号: " + str_userid);
        userBeizhuName.setText(name);
        NetUtil.setImageIcon(str_userid, userIcon, true, false);

        initData();
    }

    private void findViewByIds() {
        userBeizhuName = (TextView) findViewById(R.id.user_detail_beizhuname);
        userId = (TextView) findViewById(R.id.user_detail_userid);
        userName = (TextView) findViewById(R.id.user_detail_name);
        addOrSend = (TextView) findViewById(R.id.user_detail_add_or_send);
        actionBack = (ImageView) findViewById(R.id.user_detail_back);
        userIcon = (RoundCornerImageView) findViewById(R.id.activity_userdetail_usericon);
        actionBack.setOnClickListener(this);
        addOrSend.setOnClickListener(this);
        userIcon.setOnClickListener(this);
    }

    private void initData() {
        friendDao = new FriendDao(this);
        if (friendDao.isHavePerson(str_userid)) {
            addOrSend.setText("发消息");
        }
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

    private void showDialogSendVerify() {
        dialog = new Dialog(this, R.style.NobackDialog);
        View view = View.inflate(this, R.layout.dialog_send_friend_validate, null);
        final EditText editText = (EditText) view.findViewById(R.id.dialog_send_friend_edittext);
        TextView cancel = (TextView) view.findViewById(R.id.dialog_send_friend_cancel);
        TextView send = (TextView) view.findViewById(R.id.dialog_send_friend_ok);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                String verifyMsg = Constant.VERIFY_PREFIX + editText.getText().toString().trim();
                RongIM.getInstance().getRongIMClient().sendMessage(Conversation.ConversationType.PRIVATE, str_userid, TextMessage.obtain(verifyMsg), "", "", new RongIMClient.SendMessageCallback() {
                    @Override
                    public void onSuccess(Integer integer) {
                        Toast.makeText(UserDetailActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {

                    }
                }, null);
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_detail_add_or_send:
                if (friendDao.isHavePerson(str_userid)) {
                    Intent chat = new Intent(this, ChatDetailActivity.class);
                    chat.putExtra(IntentKey.CHAT_USERID, str_userid);
                    startActivity(chat);
                    finish();
                } else {
                    showDialogSendVerify();
                }
                break;
            case R.id.user_detail_back:
                finish();
                break;
            case R.id.activity_userdetail_usericon:
                Intent intent = new Intent(UserDetailActivity.this, UserIconDetail.class);
                intent.putExtra(IntentKey.USER_ID, str_userid);
                startActivity(intent);
                break;
        }
    }
}
