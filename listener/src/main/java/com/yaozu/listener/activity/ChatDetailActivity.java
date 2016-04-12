package com.yaozu.listener.activity;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.yaozu.listener.R;
import com.yaozu.listener.YaozuApplication;
import com.yaozu.listener.adapter.ChatDetailListViewAdapter;
import com.yaozu.listener.constant.DataInterface;
import com.yaozu.listener.constant.IntentKey;
import com.yaozu.listener.dao.NetDao;
import com.yaozu.listener.dao.UserState;
import com.yaozu.listener.db.dao.ChatDetailInfoDao;
import com.yaozu.listener.db.dao.ChatListInfoDao;
import com.yaozu.listener.db.model.ChatDetailInfo;
import com.yaozu.listener.db.model.ChatListInfo;
import com.yaozu.listener.db.model.Person;
import com.yaozu.listener.listener.PersonState;
import com.yaozu.listener.listener.PersonStateInterface;
import com.yaozu.listener.service.MusicService;
import com.yaozu.listener.utils.IntentUtil;
import com.yaozu.listener.utils.Order;
import com.yaozu.listener.utils.User;
import com.yaozu.listener.utils.VolleyHelper;
import com.yaozu.listener.widget.ResizeLayout;

import org.json.JSONObject;

import java.util.Date;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.message.TextMessage;

/**
 * Created by 耀祖 on 2016/1/18.
 */
public class ChatDetailActivity extends SwipeBackBaseActivity implements View.OnClickListener, PersonStateInterface {
    private ListView mListView;
    private TextView user;
    private EditText mEditText;
    private ImageView back;
    private Button mSend;
    private ChatDetailListViewAdapter mListAdapter;
    private ChatDetailInfoDao mChatDetailDao;
    private ChatListInfoDao chatListInfoDao;
    private String mOtherUserId;
    private TextView mFollowSong;

    private ResizeLayout chatRoot;
    private String songInfo, songstate;

    //对方的信息
    private boolean isfollow;
    private String followid;

    /**
     * 对话框
     */
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        YaozuApplication.personStateInstances.add(this);
        setContentView(R.layout.activity_social_chatdetail);
        mOtherUserId = getIntent().getStringExtra(IntentKey.CHAT_USERID);
        songInfo = getIntent().getStringExtra(IntentKey.CURRENT_SONG_INFO);
        songstate = getIntent().getStringExtra(IntentKey.CURRENT_SONG_STATE);
        findViews();
        initData();
        mChatDetailDao = new ChatDetailInfoDao(this);
        chatListInfoDao = new ChatListInfoDao(this);
        mListAdapter = new ChatDetailListViewAdapter(this, mOtherUserId, mChatDetailDao.findAllChatDetailInfoByUserid(mOtherUserId));
        mListView.setAdapter(mListAdapter);
        user.setText(mOtherUserId);
        RongIM.getInstance().getRongIMClient().clearConversations(Conversation.ConversationType.PRIVATE);
        mListView.setSelection(mListAdapter.getCount() - 1);


        //获取用户信息
        requestCheckUserInfo(mOtherUserId);
        getUserState();
    }

    private void initData() {
        if (songstate != null && PersonState.PLAYING.toString().equals(songstate)) {
            if (songInfo != null) {
                mFollowSong.setVisibility(View.VISIBLE);
                findViewById(R.id.activity_chatdetail_songinfo_ll).setVisibility(View.VISIBLE);
                mFollowSong.setText(songInfo.split("--")[0]);
            }
        }
    }

    private void getUserState() {
        NetDao.getUserState(mOtherUserId, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                UserState userState = JSON.parseObject(response.toString(), UserState.class);
                if (userState != null) {
                    Person person = new Person();
                    person.setId(mOtherUserId);
                    person.setState(userState.getState());
                    person.setCurrentSong(userState.getSongname() + "--" + userState.getSinger());
                    Order.notifyPersonState(person);

                    isfollow = Boolean.parseBoolean(userState.getIsfollow());
                    followid = userState.getFollowid();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    private void requestCheckUserInfo(String userid) {
        String url = DataInterface.getCheckUserInfoUrl() + "?userid=" + userid;
        VolleyHelper.getRequestQueue().add(new JsonObjectRequest(Request.Method.GET,
                url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(response.toString());
                int code = jsonObject.getIntValue("code");
                String msg = jsonObject.getString("message");
                String token = jsonObject.getString("token");
                String username = jsonObject.getString("username");
                String iconurl = jsonObject.getString("iconurl");
                if (code == 1) {
                    user.setText(username);
                    //更新数据库
                    chatListInfoDao.updateChatListNameByid(username, mOtherUserId);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }));
    }

    @Override
    protected void onResume() {
        super.onResume();
        makeUnreadTohadread();
        //取消通知消息
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        makeUnreadTohadread();
        YaozuApplication.personStateInstances.remove(this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.left_enter_page, R.anim.right_quit_page);
    }

    /**
     * 把未读置为已读
     */
    private void makeUnreadTohadread() {
        if (mListAdapter.getCount() > 0) {
            chatListInfoDao.updateChatListUnreadsByid("0", mOtherUserId);
            ChatListInfo chatListInfo = new ChatListInfo();
            chatListInfo.setOtherUserid(mOtherUserId);
            chatListInfo.setUnreadcount("0");
            chatListInfo.setLastchatcontent(((ChatDetailInfo) mListAdapter.getItem(mListAdapter.getCount() - 1)).getChatcontent());
            sendBroadCastToupdateChatlist(chatListInfo, null);
        }
    }

    private void findViews() {
        chatRoot = (ResizeLayout) findViewById(R.id.chat_root);
        mListView = (ListView) findViewById(R.id.activity_social_chatdetail_listview);
        user = (TextView) findViewById(R.id.activity_chatdetail_user);
        mEditText = (EditText) findViewById(R.id.activity_social_chatdetail_edittext);
        back = (ImageView) findViewById(R.id.activity_register_back);
        mSend = (Button) findViewById(R.id.activity_social_chatdetail_send);
        mFollowSong = (TextView) findViewById(R.id.activity_chatdetail_songinfo);
        mSend.setOnClickListener(this);
        back.setOnClickListener(this);
        mFollowSong.setOnClickListener(this);

        chatRoot.setCallBackListener(new ResizeLayout.OnLayoutCallBack() {
            @Override
            public void onLayoutCall() {
                mListView.smoothScrollToPosition(mListAdapter.getCount() - 1);
            }
        });
    }

    @Override
    public void updateChatDetailInfo(ChatDetailInfo chatDetailInfo) {
        if (chatDetailInfo != null) {
            mListAdapter.updateAddData(chatDetailInfo);
            mListAdapter.notifyDataSetChanged();
            mListView.smoothScrollToPosition(mListAdapter.getCount() - 1);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_social_chatdetail_send:
                sendMessage();
                break;
            case R.id.activity_register_back:
                finish();
                break;
            case R.id.activity_chatdetail_songinfo:
                //TODO
                showDialog();
                break;
        }
    }

    /**
     * 显示对话框
     */
    private void showDialog() {
        getUserState();
        dialog = new Dialog(this, R.style.NobackDialog);
        View view = View.inflate(this, R.layout.dialog_select_followsong, null);
        //正在听
        TextView listening = (TextView) view.findViewById(R.id.dialog_song_info_t);
        //歌曲详情
        TextView songinfo = (TextView) view.findViewById(R.id.dialog_song_info);
        songinfo.setText(songInfo);

        final TextView with = (TextView) view.findViewById(R.id.dialog_follow_or_lyric);
        RelativeLayout withll = (RelativeLayout) view.findViewById(R.id.dialog_follow_or_lyric_rl);
        RelativeLayout cancel = (RelativeLayout) view.findViewById(R.id.dialog_follow_cancel_rl);
        if (!YaozuApplication.isFollowPlay) {
            //对方是否跟着听
            if (isfollow && followid.equals(User.getUserAccount())) {
                listening.setText("TA跟着你在听:");
                with.setText("去播放页");
            } else {
                listening.setText("正在听:");
                with.setText("和 " + user.getText().toString() + " 一起听");
            }
        } else {
            //对方是否跟着听
            if (isfollow && followid.equals(User.getUserAccount())) {
                listening.setText("你们正在听:");
                with.setText("去播放页");
            } else {
                if (YaozuApplication.followUserid.equals(mOtherUserId)) {
                    listening.setText("你们正在听:");
                    with.setText("去播放页");
                } else {
                    listening.setText("正在听:");
                    with.setText("和 " + user.getText().toString() + " 一起听");
                }
            }
        }
        withll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if ("去播放页".equals(with.getText().toString())) {
                    IntentUtil.toMusicLyric(ChatDetailActivity.this);
                } else {
                    final MusicService service = YaozuApplication.getIntance().getMusicService();
                    if (service != null) {
                        String str[] = songInfo.split("--");
                        final String songname = str[0];
                        final String singer = str[1];
                        service.playSongFromServer(songname, singer);
                        YaozuApplication.setFollowPlayInfo(mOtherUserId, user.getText().toString());
                    }
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }

    private void sendMessage() {
        final String msg = mEditText.getText().toString().trim();
        if (TextUtils.isEmpty(msg)) {
            Toast.makeText(this, "发送的消息内容不以能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        mEditText.setText("");
        RongIM.getInstance().getRongIMClient().sendMessage(Conversation.ConversationType.PRIVATE, mOtherUserId, TextMessage.obtain(msg), "", "", new RongIMClient.SendMessageCallback() {
            @Override
            public void onError(Integer messageId, RongIMClient.ErrorCode e) {

            }

            @Override
            public void onSuccess(Integer integer) {
                Log.d("MusicHomeFragment", "=========onSuccess==========>" + integer);
                //更新或者插入聊天列表
                ChatListInfo chatListInfo = new ChatListInfo();
                chatListInfo.setOtherUserid(mOtherUserId);
                chatListInfo.setLastchatcontent(msg);
                if (chatListInfoDao.find(chatListInfo.getOtherUserid())) {
                    chatListInfoDao.updateChatListInfoByid(chatListInfo.getLastchatcontent(), chatListInfo.getOtherUserid());
                } else {
                    chatListInfoDao.add(chatListInfo);
                }

                //更新或者插入聊天详情记录
                ChatDetailInfo chatdetailInfo = new ChatDetailInfo();
                chatdetailInfo.setOtherUserid(mOtherUserId);
                chatdetailInfo.setChatcontent(msg);
                chatdetailInfo.setTime((new Date().getTime()) + "");
                chatdetailInfo.setIssender("false");
                mChatDetailDao.add(chatdetailInfo);
                //更新ListView的界面
                updateChatDetailInfo(chatdetailInfo);

                //发送广播更新聊天列表界面
                sendBroadCastToupdateChatlist(chatListInfo, chatdetailInfo);
            }
        }, null);
    }

    /**
     * 发送广播更新聊天列表界面
     *
     * @param chatListInfo
     * @param chatdetailInfo
     */
    private void sendBroadCastToupdateChatlist(ChatListInfo chatListInfo, ChatDetailInfo chatdetailInfo) {
        Intent playingintent = new Intent(IntentKey.NOTIFY_CHAT_LIST_INFO);
        Bundle bundle = new Bundle();
        bundle.putSerializable(IntentKey.SEND_BUNDLE_CHATLISTINFO, chatListInfo);
        playingintent.putExtra(IntentKey.SEND_BUNDLE, bundle);
        LocalBroadcastManager playinglocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        playinglocalBroadcastManager.sendBroadcast(playingintent);
    }

    @Override
    public void updatePersonState(Person person) {
        songInfo = person.getCurrentSong();
        if (PersonState.PLAYING.toString().equals(person.getState())) {
            mFollowSong.setVisibility(View.VISIBLE);
            findViewById(R.id.activity_chatdetail_songinfo_ll).setVisibility(View.VISIBLE);
            mFollowSong.setText(person.getCurrentSong().split("--")[0]);
        } else {
            mFollowSong.setVisibility(View.GONE);
            findViewById(R.id.activity_chatdetail_songinfo_ll).setVisibility(View.GONE);
        }
    }
}
