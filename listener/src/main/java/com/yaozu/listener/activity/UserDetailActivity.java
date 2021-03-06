package com.yaozu.listener.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.yaozu.listener.R;
import com.yaozu.listener.YaozuApplication;
import com.yaozu.listener.constant.Constant;
import com.yaozu.listener.constant.DataInterface;
import com.yaozu.listener.constant.IntentKey;
import com.yaozu.listener.dao.NetDao;
import com.yaozu.listener.dao.UserState;
import com.yaozu.listener.db.dao.FriendDao;
import com.yaozu.listener.db.model.Person;
import com.yaozu.listener.listener.PersonState;
import com.yaozu.listener.listener.PersonStateInterface;
import com.yaozu.listener.playlist.model.Song;
import com.yaozu.listener.service.MusicService;
import com.yaozu.listener.service.SupportMusicService;
import com.yaozu.listener.utils.IntentUtil;
import com.yaozu.listener.utils.NetUtil;
import com.yaozu.listener.utils.Order;
import com.yaozu.listener.utils.User;
import com.yaozu.listener.widget.RoundCornerImageView;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.message.TextMessage;

/**
 * Created by jieyaozu on 2016/2/20.
 */
public class UserDetailActivity extends SwipeBackBaseActivity implements View.OnClickListener, PersonStateInterface {
    private TextView userBeizhuName, userId, userName;
    private TextView addOrSend;
    private ImageView actionBack;
    private RoundCornerImageView userIcon;

    //放弃跟随播放
    private ImageView notFollow;

    private String name, str_userid, token, iconUrl;
    private Dialog dialog;
    //通讯录的数据库
    private FriendDao friendDao;
    private String state;
    private String currentSongInfo;
    private RelativeLayout r_playingInfo;
    private TextView t_curentflag, t_currentsong, t_playtogether;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        YaozuApplication.personStateInstances.add(this);
        setContentView(R.layout.activity_user_detail);
        findViewByIds();

        Intent intent = getIntent();
        name = intent.getStringExtra(IntentKey.USER_NAME);
        str_userid = intent.getStringExtra(IntentKey.USER_ID);
        token = intent.getStringExtra(IntentKey.USER_TOKEN);
        iconUrl = intent.getStringExtra(IntentKey.USER_ICON_URL);
        //当前正在播放信息
        state = intent.getStringExtra(IntentKey.CURRENT_SONG_STATE);
        currentSongInfo = intent.getStringExtra(IntentKey.CURRENT_SONG_INFO);

        userName.setText(name);
        userId.setText("账号: " + str_userid);
        userBeizhuName.setText(name);
        NetUtil.setImageIcon(str_userid, userIcon, true, false);

        initData();
        getUserState();
    }

    //对方的信息
    private boolean isfollow;
    private String followid;

    private void getUserState() {
        NetDao.getUserState(str_userid, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                UserState userState = JSON.parseObject(response.toString(), UserState.class);
                if (userState != null) {
                    Person person = new Person();
                    person.setId(str_userid);
                    person.setState(userState.getState());
                    person.setCurrentSong(userState.getSongname() + "--" + userState.getSinger());
                    Order.notifyPersonState(person);

                    isfollow = Boolean.parseBoolean(userState.getIsfollow());
                    followid = userState.getFollowid();
                }
                initData();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    private void findViewByIds() {
        userBeizhuName = (TextView) findViewById(R.id.user_detail_beizhuname);
        userId = (TextView) findViewById(R.id.user_detail_userid);
        userName = (TextView) findViewById(R.id.user_detail_name);
        addOrSend = (TextView) findViewById(R.id.user_detail_add_or_send);
        actionBack = (ImageView) findViewById(R.id.user_detail_back);
        userIcon = (RoundCornerImageView) findViewById(R.id.activity_userdetail_usericon);
        r_playingInfo = (RelativeLayout) findViewById(R.id.user_detail_playing_info);
        t_curentflag = (TextView) findViewById(R.id.user_detail_flag);
        t_currentsong = (TextView) findViewById(R.id.user_detail_info);
        t_playtogether = (TextView) findViewById(R.id.user_detail_play_together);
        notFollow = (ImageView) findViewById(R.id.user_detail_not_follow);

        actionBack.setOnClickListener(this);
        addOrSend.setOnClickListener(this);
        userIcon.setOnClickListener(this);
        t_playtogether.setOnClickListener(this);
        notFollow.setOnClickListener(this);
    }

    private void initData() {
        friendDao = new FriendDao(this);
        if (friendDao.isHavePerson(str_userid)) {
            addOrSend.setText("发消息");
        }

        if (PersonState.PLAYING.toString().equals(state)) {
            r_playingInfo.setVisibility(View.VISIBLE);
            t_currentsong.setText(currentSongInfo);
        } else {
            r_playingInfo.setVisibility(View.GONE);
        }

        if (YaozuApplication.isFollowPlay && YaozuApplication.followUserid.equals(str_userid)) {
            t_playtogether.setVisibility(View.GONE);
            notFollow.setVisibility(View.VISIBLE);
        } else {
            if (isfollow && followid.equals(User.getUserAccount())) {
                t_playtogether.setVisibility(View.GONE);
                notFollow.setVisibility(View.GONE);
            }
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
                IntentUtil.toUserIconActivity(this,str_userid);
                break;
            case R.id.user_detail_play_together:
                final MusicService service = YaozuApplication.getIntance().getMusicService();
                final SupportMusicService spservice = YaozuApplication.getIntance().getSupportMusicService();
                final Song song = new Song();
                String str[] = currentSongInfo.split("--");
                final String songname = str[0];
                final String singer = str[1];
                song.setTitle(songname);
                song.setSinger(singer);

                if (service != null) {
                    service.playSongFromServer(song);
                }
                if (spservice != null) {
                    spservice.playSongFromServer(song);
                }

                YaozuApplication.setFollowPlayInfo(str_userid, name);
                t_playtogether.setVisibility(View.GONE);
                notFollow.setVisibility(View.VISIBLE);
                break;
            case R.id.user_detail_not_follow:
                YaozuApplication.clearFollowInfo();
                t_playtogether.setVisibility(View.VISIBLE);
                notFollow.setVisibility(View.GONE);
                final MusicService service2 = YaozuApplication.getIntance().getMusicService();
                if (service2 != null) {
                    service2.pause();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        YaozuApplication.personStateInstances.remove(this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.left_enter_page, R.anim.right_quit_page);
    }

    @Override
    public void updatePersonState(Person person) {
        currentSongInfo = person.getCurrentSong();
        t_currentsong.setText(person.getCurrentSong());
        if ("playing".equals(person.getState())) {
            r_playingInfo.setVisibility(View.VISIBLE);
            t_curentflag.setText("正在播放: ");
            t_curentflag.setTextColor(getResources().getColor(R.color.playing_color));
        } else {
            t_curentflag.setText("暂停播放: ");
            t_curentflag.setTextColor(getResources().getColor(R.color.pause_color));
        }
    }
}
