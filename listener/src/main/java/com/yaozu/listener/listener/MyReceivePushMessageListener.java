package com.yaozu.listener.listener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.yaozu.listener.R;
import com.yaozu.listener.YaozuApplication;
import com.yaozu.listener.activity.LoginActivity;
import com.yaozu.listener.adapter.MailListAdapter;
import com.yaozu.listener.constant.IntentKey;
import com.yaozu.listener.db.model.Person;
import com.yaozu.listener.utils.Order;
import com.yaozu.listener.utils.PhoneInfoUtil;
import com.yaozu.listener.utils.User;

import io.rong.imlib.RongIMClient;
import io.rong.notification.PushNotificationMessage;

/**
 * Created by 耀祖 on 2016/1/30.
 */
public class MyReceivePushMessageListener implements RongIMClient.OnReceivePushMessageListener {
    private String TAG = this.getClass().getSimpleName();
    private Context mContext;
    private Dialog dialog;
    private User mUser;

    public MyReceivePushMessageListener(Context context) {
        this.mContext = context;
        mUser = new User(mContext);
    }

    /**
     * 收到 push 消息的处理。
     *
     * @param pushNotificationMessage push 消息实体。
     * @return true 自己来弹通知栏提示，false 融云 SDK 来弹通知栏提示。
     */
    @Override
    public boolean onReceivePushMessage(PushNotificationMessage pushNotificationMessage) {
        String content = pushNotificationMessage.getPushContent();
        String extra = pushNotificationMessage.getExtra();
        Log.d(TAG, "=====content=====>" + content + "  ===extra===>" + extra);
        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(extra);
        //通知用户当前状态
        if (content.equals("updatepersonstate")) {
            String songInfo = jsonObject.getString("songname");
            String singer = jsonObject.getString("singer");
            String userid = jsonObject.getString("userid");
            String state = jsonObject.getString("state");
            Person person = new Person();
            person.setId(userid);
            person.setState(state);
            person.setCurrentSong(songInfo + "--" + singer);
            Order.notifyPersonState(person);
            return true;
        }
        //确保唯一设备登陆
        PhoneInfoUtil phoneInfoUtil = new PhoneInfoUtil(mContext);
        Log.d(TAG, "=====content=====>" + content + "  ===DeviceId===>" + phoneInfoUtil.getDeviceId());
        if (phoneInfoUtil.getDeviceId().equals(content)) {
            //弹出对话框 把广播发到BaseActivity去
            Intent loginOutIntent = new Intent(IntentKey.NOTIFY_LOGIN_OUT);
            LocalBroadcastManager loginOutBroadcastManager = LocalBroadcastManager.getInstance(YaozuApplication.getIntance());
            loginOutBroadcastManager.sendBroadcast(loginOutIntent);
        }
        return true;
    }


}