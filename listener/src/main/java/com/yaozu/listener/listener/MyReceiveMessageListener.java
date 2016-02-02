package com.yaozu.listener.listener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yaozu.listener.constant.IntentKey;
import com.yaozu.listener.db.dao.ChatDetailInfoDao;
import com.yaozu.listener.db.dao.ChatListInfoDao;
import com.yaozu.listener.db.model.ChatDetailInfo;
import com.yaozu.listener.db.model.ChatListInfo;

import java.util.Date;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;

/**
 * Created by 耀祖 on 2016/1/17.
 */
public class MyReceiveMessageListener implements RongIMClient.OnReceiveMessageListener {
    private String TAG = this.getClass().getSimpleName();
    private Context mContext;
    private ChatListInfoDao chatListInfoDao;
    private ChatDetailInfoDao mChatDetailInfoDao;

    public MyReceiveMessageListener(Context context) {
        mContext = context;
        chatListInfoDao = new ChatListInfoDao(mContext);
        mChatDetailInfoDao = new ChatDetailInfoDao(mContext);
    }

    /**
     * 收到消息的处理。
     *
     * @param message 收到的消息实体。
     * @param left    剩余未拉取消息数目。
     * @return 收到消息是否处理完成，true 表示走自已的处理方式，false 走融云默认处理方式。
     */
    @Override
    public boolean onReceived(Message message, int left) {
        //更新或者插入聊天列表
        ChatListInfo chatListInfo = new ChatListInfo();
        chatListInfo.setOtherUserid(message.getSenderUserId());
        JSONObject object = JSON.parseObject(new String(message.getContent().encode()));
        chatListInfo.setLastchatcontent(object.getString("content"));
        if (chatListInfoDao.find(chatListInfo.getOtherUserid())) {
            chatListInfoDao.updateChatListInfoByid(chatListInfo.getLastchatcontent(), chatListInfo.getOtherUserid());
        } else {
            chatListInfoDao.add(chatListInfo);
        }
        //更新未读数
        String ureads = chatListInfoDao.getChatListUnreadsByid(chatListInfo.getOtherUserid());
        if(!TextUtils.isEmpty(ureads)){
            int count = Integer.parseInt(ureads);
            ++count;
            chatListInfo.setUnreadcount(count+"");
            chatListInfoDao.updateChatListUnreadsByid(count+"",chatListInfo.getOtherUserid());
        }else{
            chatListInfo.setUnreadcount("1");
            chatListInfoDao.updateChatListUnreadsByid("1",chatListInfo.getOtherUserid());
        }

        //更新或者插入聊天详情记录
        ChatDetailInfo chatdetailInfo = inserChatDetaildb(message);

        Intent playingintent = new Intent(IntentKey.NOTIFY_CHAT_LIST_INFO);
        Bundle bundle = new Bundle();
        bundle.putSerializable(IntentKey.SEND_BUNDLE_CHATLISTINFO, chatListInfo);
        bundle.putSerializable(IntentKey.SEND_BUNDLE_CHATDETAILINFO, chatdetailInfo);
        playingintent.putExtra(IntentKey.SEND_BUNDLE, bundle);
        LocalBroadcastManager playinglocalBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        playinglocalBroadcastManager.sendBroadcast(playingintent);

        //开发者根据自己需求自行处理
        Log.d(TAG, "=======onReceived=========>" + new String(message.getContent().encode()));
        return true;
    }

    private ChatDetailInfo inserChatDetaildb(Message message) {
        ChatDetailInfo chatdetailInfo = new ChatDetailInfo();
        chatdetailInfo.setOtherUserid(message.getSenderUserId());
        JSONObject object = JSON.parseObject(new String(message.getContent().encode()));
        chatdetailInfo.setChatcontent(object.getString("content"));
        chatdetailInfo.setTime((new Date().getTime()) + "");
        chatdetailInfo.setIssender("true");
        mChatDetailInfoDao.add(chatdetailInfo);
        return chatdetailInfo;
    }
}
