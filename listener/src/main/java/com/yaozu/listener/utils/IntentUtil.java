package com.yaozu.listener.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.yaozu.listener.R;
import com.yaozu.listener.YaozuApplication;
import com.yaozu.listener.activity.MusicLyricActivity;
import com.yaozu.listener.activity.UserDetailActivity;
import com.yaozu.listener.constant.IntentKey;
import com.yaozu.listener.service.MusicService;

/**
 * Created by jieyaozu on 2016/3/19.
 */
public class IntentUtil {
    /**
     * 跳到歌词展示页面
     *
     * @param context
     */
    public static void toMusicLyric(Context context) {
        Intent intent = new Intent(context, MusicLyricActivity.class);
        MusicService service = YaozuApplication.getIntance().getMusicService();
        if (service != null && service.getmCurrentSong() != null) {
            intent.putExtra(IntentKey.MEDIA_FILE_SONG_NAME, service.getmCurrentSong().getTitle());
            intent.putExtra(IntentKey.MEDIA_FILE_SONG_SINGER, service.getmCurrentSong().getSinger());
        }
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.music_lyric_bottom_in, R.anim.music_lyric_out);
    }

    /**
     * 跳到用户详情页面
     *
     * @param context
     * @param username
     * @param userid
     * @param state
     * @param songinfo
     */
    public static void toUserDetail(Context context, String username, String userid, String state, String songinfo) {
        Intent intent = new Intent(context, UserDetailActivity.class);
        intent.putExtra(IntentKey.USER_NAME, username);
        intent.putExtra(IntentKey.USER_ID, userid);
        intent.putExtra(IntentKey.CURRENT_SONG_STATE, state);
        intent.putExtra(IntentKey.CURRENT_SONG_INFO, songinfo);
        //intent.putExtra(IntentKey.USER_ICON_URL, iconurl);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.right_enter_page, R.anim.left_quit_page);
    }
}
