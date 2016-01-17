package com.yaozu.listener.listener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.yaozu.listener.constant.IntentKey;
import com.yaozu.listener.db.dao.ChatListInfoDao;
import com.yaozu.listener.db.model.ChatListInfo;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;

/**
 * Created by 耀祖 on 2016/1/17.
 */
public class MyReceiveMessageListener implements RongIMClient.OnReceiveMessageListener {
    private String TAG = this.getClass().getSimpleName();
    private Context mContext;
    private Handler mHandler;
    private ChatListInfoDao chatListInfoDao;

    public MyReceiveMessageListener(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
        chatListInfoDao = new ChatListInfoDao(mContext);
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
        chatListInfo.setUserid(message.getSenderUserId());
        chatListInfo.setLastchatcontent(new String(message.getContent().encode()));
        if (chatListInfoDao.find(chatListInfo.getUserid())) {
            chatListInfoDao.updateChatListInfoByid(chatListInfo.getLastchatcontent(), chatListInfo.getUserid());
        } else {
            chatListInfoDao.add(chatListInfo);
        }
        Intent playingintent = new Intent(IntentKey.NOTIFY_CHAT_LIST_INFO);
        Bundle bundle = new Bundle();
        bundle.putSerializable(IntentKey.SEND_BUNDLE_CHATLISTINFO,chatListInfo);
        playingintent.putExtra(IntentKey.SEND_BUNDLE,bundle);
        LocalBroadcastManager playinglocalBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        playinglocalBroadcastManager.sendBroadcast(playingintent);
        //开发者根据自己需求自行处理
        Log.d(TAG, "=======onReceived=========>" + new String(message.getContent().encode()));
        android.os.Message msg = mHandler.obtainMessage();
        msg.obj = message;
        mHandler.sendMessage(msg);
        return false;
    }

}
