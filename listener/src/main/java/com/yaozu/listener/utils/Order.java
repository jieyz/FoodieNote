package com.yaozu.listener.utils;

import android.content.Context;
import android.widget.Toast;

import com.yaozu.listener.YaozuApplication;
import com.yaozu.listener.db.model.Person;
import com.yaozu.listener.listener.PersonState;
import com.yaozu.listener.playlist.model.Song;
import com.yaozu.listener.service.MusicService;
import com.yaozu.listener.service.SupportMusicService;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.message.TextMessage;

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
            final SupportMusicService spservice = YaozuApplication.getIntance().getSupportMusicService();
            String str[] = person.getCurrentSong().split("--");
            final String songname = str[0];
            final String singer = str[1];
            if (service != null && spservice != null) {
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
                        spservice.playSongFromServer(songname, singer);
                    }
                }
            }
        }
    }

    /**
     * 通知用户的当前状态
     * 当一个用户的状态发生变化时，需要在相应的界面刷新显示
     * @param person 此用户
     */
    public static void notifyPersonState(Person person) {
        for (int i = 0; i < YaozuApplication.personStateInstances.size(); i++) {
            YaozuApplication.personStateInstances.get(i).updatePersonState(person);
        }
    }

    public static void sendAgreeFriendMsg(String userid,String msg){
        RongIM.getInstance().getRongIMClient().sendMessage(Conversation.ConversationType.PRIVATE, userid, TextMessage.obtain(msg), "", "", new RongIMClient.SendMessageCallback() {
            @Override
            public void onSuccess(Integer integer) {

            }

            @Override
            public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {

            }
        }, null);
    }
}
