package com.yaozu.listener.utils;

import com.yaozu.listener.YaozuApplication;
import com.yaozu.listener.db.model.Person;
import com.yaozu.listener.listener.PersonState;
import com.yaozu.listener.playlist.model.Song;
import com.yaozu.listener.service.MusicService;

/**
 * 跟随的用户发来的指令
 * Created by jieyaozu on 2016/3/19.
 */
public class Order {
    /**
     * 如果是跟随目标person播放，将会切换到目标的下一首歌曲,或者播放暂停
     *
     * @param person 跟随的目标
     */
    public static void switchToNextSongOrPlayPause(Person person) {
        if (YaozuApplication.isFollowPlay && YaozuApplication.followUserid.equals(person.getId())) {
            final MusicService service = YaozuApplication.getIntance().getMusicService();
            String str[] = person.getCurrentSong().split("--");
            final String songname = str[0];
            final String singer = str[1];
            System.out.println("===Order=========personState================>" + person.getState());
            if (service != null) {
                if (PersonState.PAUSE.toString().equals(person.getState())) {
                    service.pause();
                } else if (PersonState.PLAYING.toString().equals(person.getState())) {
                    Song song = service.getmCurrentSong();
                    //是同一首歌曲
                    if (song != null && song.getTitle().equals(songname) && song.getSinger().equals(singer)) {
                        service.start();
                    } else {
                        //是切换下一首歌曲
                        service.playSongFromServer(songname, singer);
                    }
                }
            }
        }
    }

    public static void seekToPlay(Person person) {

    }
}
