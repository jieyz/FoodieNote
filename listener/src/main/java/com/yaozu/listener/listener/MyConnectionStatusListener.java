package com.yaozu.listener.listener;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import io.rong.imlib.RongIMClient;

/**
 * Created by 耀祖 on 2016/1/30.
 */
public class MyConnectionStatusListener implements RongIMClient.ConnectionStatusListener {
    private Context mContext;
    private Handler mHandler;
    public MyConnectionStatusListener(Context context,Handler handler) {
        this.mContext = context;
        mHandler = handler;
    }

    @Override
    public void onChanged(ConnectionStatus connectionStatus) {

        switch (connectionStatus) {
            case CONNECTED://连接成功。
                sendMessage(0);
                break;
            case DISCONNECTED://断开连接。
                sendMessage(1);
                break;
            case CONNECTING://连接中。
                sendMessage(2);
                break;
            case NETWORK_UNAVAILABLE://网络不可用。
                sendMessage(3);
                break;
            case KICKED_OFFLINE_BY_OTHER_CLIENT://用户账户在其他设备登录，本机会被踢掉线
                sendMessage(4);
                break;
        }
    }

    private void sendMessage(int what){
        Message msg = mHandler.obtainMessage();
        msg.what = what;
        mHandler.sendMessage(msg);
    }
}
