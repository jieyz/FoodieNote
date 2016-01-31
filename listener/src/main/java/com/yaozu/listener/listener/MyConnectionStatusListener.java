package com.yaozu.listener.listener;

import android.content.Context;
import android.widget.Toast;

import io.rong.imlib.RongIMClient;

/**
 * Created by 耀祖 on 2016/1/30.
 */
public class MyConnectionStatusListener implements RongIMClient.ConnectionStatusListener {
    private Context mContext;

    public MyConnectionStatusListener(Context context) {
        this.mContext = context;
    }

    @Override
    public void onChanged(ConnectionStatus connectionStatus) {

        switch (connectionStatus) {
            case CONNECTED://连接成功。
                Toast.makeText(mContext, "连接成功", Toast.LENGTH_SHORT).show();
                break;
            case DISCONNECTED://断开连接。
                Toast.makeText(mContext, "断开连接", Toast.LENGTH_SHORT).show();
                break;
            case CONNECTING://连接中。
                Toast.makeText(mContext, "连接中", Toast.LENGTH_SHORT).show();
                break;
            case NETWORK_UNAVAILABLE://网络不可用。
                Toast.makeText(mContext, "网络不可用", Toast.LENGTH_SHORT).show();
                break;
            case KICKED_OFFLINE_BY_OTHER_CLIENT://用户账户在其他设备登录，本机会被踢掉线
                Toast.makeText(mContext, "你已在另一台设备上登录", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
