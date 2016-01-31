package com.yaozu.listener;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.yaozu.listener.listener.MyConnectionStatusListener;
import com.yaozu.listener.service.MusicService;

import java.util.HashMap;

import io.rong.imkit.RongIM;

/**
 * Created by jieyaozu on 2015/10/31.
 */
public class YaozuApplication extends Application {
    private static YaozuApplication app;

    private final int MUSIC_SERVICE = 0;
    private HashMap<Integer, MusicService> musicService = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
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
