package com.yaozu.listener.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yaozu.listener.R;
import com.yaozu.listener.adapter.ChatDetailListViewAdapter;
import com.yaozu.listener.constant.IntentKey;
import com.yaozu.listener.db.dao.ChatDetailInfoDao;
import com.yaozu.listener.db.model.ChatDetailInfo;

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
    private Button mSend;
    private ChatDetailListViewAdapter mListAdapter;
    private ChatDetailInfoDao mChatDetailDao;
    private String mUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_chatdetail);
        mUserId = getIntent().getStringExtra(IntentKey.CHAT_USERID);
        findViews();
        mChatDetailDao = new ChatDetailInfoDao(this);
        mListAdapter = new ChatDetailListViewAdapter(this, mChatDetailDao.findAllChatDetailInfoByUserid(mUserId));
        mListView.setAdapter(mListAdapter);
        user.setText(mUserId);
    }

    private void findViews(){
        mListView = (ListView) findViewById(R.id.activity_social_chatdetail_listview);
        user = (TextView) findViewById(R.id.activity_chatdetail_user);
        mEditText = (EditText) findViewById(R.id.activity_social_chatdetail_edittext);
        mSend = (Button) findViewById(R.id.activity_social_chatdetail_send);
        mSend.setOnClickListener(this);
    }

    @Override
    public void updateChatDetailInfo(ChatDetailInfo chatDetailInfo) {
        mListAdapter.updateAddData(chatDetailInfo);
        mListAdapter.notifyDataSetChanged();
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
        switch (view.getId()){
            case R.id.activity_social_chatdetail_send:
                sendMessage();
                break;
        }
    }

    private void sendMessage(){
        final String msg = mEditText.getText().toString().trim();
        if(TextUtils.isEmpty(msg)){
            Toast.makeText(this,"发送的消息内容不以能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        mEditText.setText("");
        RongIM.getInstance().getRongIMClient().sendMessage(Conversation.ConversationType.PRIVATE, mUserId, TextMessage.obtain(msg), "", "", new RongIMClient.SendMessageCallback() {
            @Override
            public void onError(Integer messageId, RongIMClient.ErrorCode e) {

            }

            @Override
            public void onSuccess(Integer integer) {
                Log.d("MusicHomeFragment", "=========onSuccess==========>" + integer);
                ChatDetailInfo chatdetailInfo = new ChatDetailInfo();
                chatdetailInfo.setUserid(mUserId);
                chatdetailInfo.setChatcontent(msg);
                chatdetailInfo.setTime((new Date().getTime()) + "");
                chatdetailInfo.setIssender("false");
                mChatDetailDao.add(chatdetailInfo);
                //更新ListView的界面
                updateChatDetailInfo(chatdetailInfo);
            }
        }, null);
    }
}
