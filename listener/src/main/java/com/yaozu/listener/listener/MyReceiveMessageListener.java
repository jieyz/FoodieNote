package com.yaozu.listener.listener;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yaozu.listener.R;
import com.yaozu.listener.YaozuApplication;
import com.yaozu.listener.activity.ChatDetailActivity;
import com.yaozu.listener.constant.Constant;
import com.yaozu.listener.constant.IntentKey;
import com.yaozu.listener.db.dao.ChatDetailInfoDao;
import com.yaozu.listener.db.dao.ChatListInfoDao;
import com.yaozu.listener.db.dao.FriendDao;
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
    private FriendDao mFriendDao;

    public MyReceiveMessageListener(Context context) {
        mContext = context;
        chatListInfoDao = new ChatListInfoDao(mContext);
        mChatDetailInfoDao = new ChatDetailInfoDao(mContext);
        mFriendDao = new FriendDao(mContext);
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
        JSONObject object = JSON.parseObject(new String(message.getContent().encode()));
        StringBuilder content = new StringBuilder(object.getString("content"));
        if (isVerifyMsg(message)) {
            return true;
        }
        if (isVerifyMsgAgree(message, content)) {

        }

        if (!YaozuApplication.isAppTopRunning()) {
            sendNotifycation(message.getSenderUserId(), content.toString());
        }
        //更新或者插入聊天列表
        ChatListInfo chatListInfo = new ChatListInfo();
        chatListInfo.setOtherUserid(message.getSenderUserId());
        chatListInfo.setLastchatcontent(content.toString());
        if (chatListInfoDao.find(chatListInfo.getOtherUserid())) {
            chatListInfoDao.updateChatListInfoByid(chatListInfo.getLastchatcontent(), chatListInfo.getOtherUserid());
        } else {
            chatListInfoDao.add(chatListInfo);
        }
        //更新未读数
        String ureads = chatListInfoDao.getChatListUnreadsByid(chatListInfo.getOtherUserid());
        if (!TextUtils.isEmpty(ureads)) {
            int count = Integer.parseInt(ureads);
            ++count;
            chatListInfo.setUnreadcount(count + "");
            chatListInfoDao.updateChatListUnreadsByid(count + "", chatListInfo.getOtherUserid());
        } else {
            chatListInfo.setUnreadcount("1");
            chatListInfoDao.updateChatListUnreadsByid("1", chatListInfo.getOtherUserid());
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

    /**
     * 是否是通讯录好友确认信息
     *
     * @param message
     * @return
     */
    private boolean isVerifyMsg(Message message) {
        boolean is = false;
        JSONObject object = JSON.parseObject(new String(message.getContent().encode()));
        String msg = object.getString("content");
        if (msg.contains(Constant.VERIFY_PREFIX)) {
            is = true;
            //通知通讯录列表页面
            Intent intent = new Intent(IntentKey.NOTIFY_VERIFY_FRIEND);
            intent.putExtra(IntentKey.USER_ID, message.getSenderUserId());
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
            localBroadcastManager.sendBroadcast(intent);
        }
        return is;
    }

    /**
     * 是否是通讯录好友申请通过信息
     *
     * @param message
     * @return
     */
    private boolean isVerifyMsgAgree(Message message, StringBuilder content) {
        boolean is = false;
        JSONObject object = JSON.parseObject(new String(message.getContent().encode()));
        String msg = object.getString("content");
        if (msg.contains(Constant.VERIFY_PREFIX_AGREET)) {
            content.replace(0, Constant.VERIFY_PREFIX_AGREET.length(), "");
            is = true;
            //通知通讯录列表页面
            Intent intent = new Intent(IntentKey.NOTIFY_VERIFY_AGREE_FRIEND);
            intent.putExtra(IntentKey.USER_ID, message.getSenderUserId());
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
            localBroadcastManager.sendBroadcast(intent);
        }
        return is;
    }

    private void sendNotifycation(String userid, String content) {
        //消息通知栏
        //定义NotificationManager
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(ns);
        //定义通知栏展现的内容信息
        Intent notificationIntent = new Intent(mContext, ChatDetailActivity.class);
        notificationIntent.putExtra(IntentKey.CHAT_USERID, userid);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, (int) System.currentTimeMillis(),
                notificationIntent, 0);
        int icon = R.drawable.ic_launcher;
        Notification notification = new Notification.Builder(mContext)
                .setContentTitle(mFriendDao.findFriend(userid).getName())
                .setContentText(content)
                .setTicker(content)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setSmallIcon(icon)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();

        //用mNotificationManager的notify方法通知用户生成标题栏消息通知
        mNotificationManager.notify(1, notification);
    }
}
