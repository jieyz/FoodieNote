package com.yaozu.listener.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yaozu.listener.R;
import com.yaozu.listener.adapter.ChatDetailListViewAdapter;
import com.yaozu.listener.constant.IntentKey;
import com.yaozu.listener.db.dao.ChatDetailInfoDao;
import com.yaozu.listener.db.dao.ChatListInfoDao;
import com.yaozu.listener.db.model.ChatDetailInfo;
import com.yaozu.listener.db.model.ChatListInfo;

import java.util.Date;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.message.TextMessage;

/**
 * Created by 耀祖 on 2016/1/18.
 */
public class ChatDetailActivity extends BaseActivity implements View.OnClickListener {
    private ListView mListView;
    private TextView user;
    private EditText mEditText;
    private ImageView back;
    private Button mSend;
    private ChatDetailListViewAdapter mListAdapter;
    private ChatDetailInfoDao mChatDetailDao;
    private ChatListInfoDao chatListInfoDao;
    private String mOtherUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_chatdetail);
        mOtherUserId = getIntent().getStringExtra(IntentKey.CHAT_USERID);
        findViews();
        mChatDetailDao = new ChatDetailInfoDao(this);
        chatListInfoDao = new ChatListInfoDao(this);
        mListAdapter = new ChatDetailListViewAdapter(this, mOtherUserId, mChatDetailDao.findAllChatDetailInfoByUserid(mOtherUserId));
        mListView.setAdapter(mListAdapter);
        user.setText(mOtherUserId);

        mListView.setSelection(mListAdapter.getCount() - 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        makeUnreadTohadread();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        makeUnreadTohadread();
    }

    /**
     * 把未读置为已读
     */
    private void makeUnreadTohadread() {
        chatListInfoDao.updateChatListUnreadsByid("0", mOtherUserId);
        ChatListInfo chatListInfo = new ChatListInfo();
        chatListInfo.setOtherUserid(mOtherUserId);
        chatListInfo.setUnreadcount("0");
        chatListInfo.setLastchatcontent(((ChatDetailInfo) mListAdapter.getItem(mListAdapter.getCount() - 1)).getChatcontent());
        sendBroadCastToupdateChatlist(chatListInfo, null);
    }

    private void findViews() {
        mListView = (ListView) findViewById(R.id.activity_social_chatdetail_listview);
        user = (TextView) findViewById(R.id.activity_chatdetail_user);
        mEditText = (EditText) findViewById(R.id.activity_social_chatdetail_edittext);
        back = (ImageView) findViewById(R.id.activity_register_back);
        mSend = (Button) findViewById(R.id.activity_social_chatdetail_send);
        mSend.setOnClickListener(this);
        back.setOnClickListener(this);
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
        }
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
}
