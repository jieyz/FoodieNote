package com.yaozu.listener;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.yaozu.listener.listener.MyConnectionStatusListener;
import com.yaozu.listener.service.MusicService;
import com.yaozu.listener.utils.VolleyHelper;

import java.util.HashMap;

import io.rong.imkit.RongIM;

/**
 * Created by jieyaozu on 2015/10/31.
 */
public class YaozuApplication extends Application {
    private static YaozuApplication app;

    private final int MUSIC_SERVICE = 0;
    private HashMap<Integer, MusicService> musicService = new HashMap<>();
    private final int CONNECTED = 0;
    private final int DISCONNECTED = 1;
    private final int CONNECTING = 2;
    private final int NETWORK_UNAVAILABLE = 3;
    private final int KICKED_OFFLINE_BY_OTHER_CLIENT = 4;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case CONNECTED://连接成功。
                    Toast.makeText(app, "连接成功", Toast.LENGTH_SHORT).show();
                    break;
                case DISCONNECTED://断开连接。
                    Toast.makeText(app, "断开连接", Toast.LENGTH_SHORT).show();
                    break;
                case CONNECTING://连接中。
                    Toast.makeText(app, "连接中", Toast.LENGTH_SHORT).show();
                    break;
                case NETWORK_UNAVAILABLE://网络不可用。
                    Toast.makeText(app, "网络不可用", Toast.LENGTH_SHORT).show();
                    break;
                case KICKED_OFFLINE_BY_OTHER_CLIENT://用户账户在其他设备登录，本机会被踢掉线
                    Toast.makeText(app, "你已在另一台设备上登录", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        VolleyHelper.init(this);
        /**
         * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIM 的进程和 Push 进程执行了 init。
         * io.rong.push 为融云 push 进程名称，不可修改。
         */
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ||
                "io.rong.push".equals(getCurProcessName(getApplicationContext()))) {
            /**
             * IMKit SDK调用第一步 初始化
             */
            RongIM.init(this);
            /**
             * 设置连接状态变化的监听器.
             */
            RongIM.getInstance().getRongIMClient().setConnectionStatusListener(new MyConnectionStatusListener(YaozuApplication.getIntance(),mHandler));
        }
    }

    public static YaozuApplication getIntance() {
        return app;
    }

    public MusicService getMusicService() {
        return musicService.get(MUSIC_SERVICE);
    }

    public void cleanMusicService(){
        musicService.remove(MUSIC_SERVICE);
    }

    public void setMusicService(MusicService service){
        musicService.put(MUSIC_SERVICE, service);
    }

    /**
     * 获得当前进程的名字
     *
     * @param context
     * @return 进程号
     */
    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

}
