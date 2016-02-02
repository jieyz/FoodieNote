package com.yaozu.listener.listener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.yaozu.listener.R;
import com.yaozu.listener.activity.LoginActivity;
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
        PhoneInfoUtil phoneInfoUtil = new PhoneInfoUtil(mContext);
        Log.d(TAG,"=====content=====>"+content+"  ===DeviceId===>"+phoneInfoUtil.getDeviceId());
        if (phoneInfoUtil.getDeviceId().equals(content)) {
            //弹出对话框
            showLoginOutDialog();
        }
        return true;
    }

    private void showLoginOutDialog() {
        dialog = new Dialog(mContext, R.style.NobackDialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_SEARCH) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        View view = View.inflate(mContext, R.layout.loginout_dialog, null);
        LinearLayout quitApp = (LinearLayout) view.findViewById(R.id.loginout_dialog_quitapp);
        quitApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mUser.quitLogin();
                Intent intent = new Intent(mContext, LoginActivity.class);
                mContext.startActivity(intent);
                ((Activity) mContext).finish();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }
}