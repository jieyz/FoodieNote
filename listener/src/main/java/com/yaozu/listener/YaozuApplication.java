package com.yaozu.listener;

import android.app.Application;

import com.yaozu.listener.service.MusicService;

import java.util.HashMap;

/**
 * Created by jieyaozu on 2015/10/31.
 */
public class YaozuApplication extends Application {
    private final static YaozuApplication app = new YaozuApplication();

    private final int MUSIC_SERVICE = 0;
    private HashMap<Integer, MusicService> musicService = new HashMap<>();

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
        musicService.put(MUSIC_SERVICE,service);
    }
}
