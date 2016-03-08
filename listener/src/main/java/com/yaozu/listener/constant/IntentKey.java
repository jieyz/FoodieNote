package com.yaozu.listener.constant;

import android.graphics.Bitmap;

/**
 * Created by 耀祖 on 2015/11/4.
 */
public class IntentKey {
    public static String MEDIA_FILE_URL = "media_file_url";
    public static String MEDIA_FILE_LIST = "media_file_list";
    public static String MEDIA_CURRENT_INDEX = "media_current_index";
    public static String MEDIA_FILE_SONG_NAME = "media_file_song_name";
    public static String MEDIA_FILE_SONG_SINGER = "media_file_song_singer";
    public static String MEDIA_FILE_SONG_ID = "media_file_song_id";
    public static String MEDIA_FILE_SONG_ALBUMID = "media_file_song_albumid";
    public static String MEDIA_FILE_SONG_ALBUM = "media_file_song_album";
    public static String SEND_BUNDLE = "send_bundle";
    public static String SEND_BUNDLE_CHATLISTINFO = "send_bundle_chatlistinfo";
    public static String SEND_BUNDLE_CHATDETAILINFO = "send_bundle_chatdetailinfo";
    public static String CHAT_USERID = "chat_userid";
    public static String USER_ICON_PATH = "user_icon_path";
    //用户名
    public static String USER_NAME = "user_name";
    //用户TOKEN
    public static String USER_TOKEN = "user_token";
    //用户id
    public static String USER_ID = "user_id";
    //用户头像url
    public static String USER_ICON_URL = "user_icon_url";

    /**
     * 广播Action
     */
    public static String NOTIFY_CURRENT_SONG_MSG = "notify.current.song.msg";
    public static String NOTIFY_SONG_PLAYING = "notify.current.song.playing";
    public static String NOTIFY_SONG_PAUSE = "notify.current.song.pause";
    public static String NOTIFY_CHAT_LIST_INFO = "notify.chat.list.info";
    public static String NOTIFY_LOGIN_OUT = "notify.loginout";

    //通讯录好友验证
    public static String NOTIFY_VERIFY_FRIEND = "notify_verify_friend";

    //正在播放歌曲信息
    public static String CURRENT_SONG_INFO = "current_song_info";
    public static String CURRENT_SONG_STATE = "current_song_state";

    //从图册里选取的位图
    public static Bitmap cropBitmap;
}
