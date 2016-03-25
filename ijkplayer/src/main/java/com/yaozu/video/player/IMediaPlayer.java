package com.yaozu.video.player;

import android.content.Context;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.IOException;


/**
 * Common IMediaPlayer implement
 *
 * @author huhailong
 */
public interface IMediaPlayer {

    /**
     * 播放过程中开始卡顿事件
     * Info接口用到的常量，可扩充
     * Do not change these values without updating their counterparts in native
     */
    int MEDIA_INFO_WHAT_BUFFERING_START = 701;

    /**
     * 播放过程中结束卡顿事件
     * Info接口用到的常量，可扩充
     */
    int MEDIA_INFO_WHAT_BUFFERING_END = 702;

    /**
     * 每一个视频开始播放时（画面出现第一帧）的回调事件
     * Info接口用到的常量，可扩充
     */
    int MEDIA_INFO_WHAT_PLAY_START = 703;

    /**
     * 不能SEEK的媒体（如：实时流）
     * Info接口用到的常量，可扩充
     */
    int MEDIA_INFO_WHAT_NOT_SEEKABLE = 801;

    /**
     * 下载速度（KB/s）
     * 每隔2秒回调一次
     */
    int MEDIA_INFO_DOWNLOAD_RATE_CHANGED = 802;


    /**
     * The audio stream for phone calls
     */
    int STREAM_VOICE_CALL = 900;

    /**
     * The audio stream for system sounds
     */
    int STREAM_SYSTEM = 901;

    /**
     * The audio stream for the phone ring
     */
    int STREAM_RING = 902;

    /**
     * The audio stream for music playback
     * 目前用到这个类型
     */
    int STREAM_MUSIC = 903;

    /**
     * The audio stream for alarms
     */
    int STREAM_ALARM = 904;

    /**
     * The audio stream for notifications
     */
    int STREAM_NOTIFICATION = 905;

    /**
     * The audio stream for phone calls when connected to bluetooth
     */
    int STREAM_BLUETOOTH_SCO = 906;

    /**
     * The audio stream for enforced system sounds in certain countries (e.g camera in Japan)
     */
    int STREAM_SYSTEM_ENFORCED = 907;

    /**
     * The audio stream for DTMF Tones
     */
    int STREAM_DTMF = 908;

    /**
     * The audio stream for text to speech (TTS)
     */
    int STREAM_TTS = 909;


    /**
     * 未知错误
     * Error接口用到的常量，可扩充
     */
    int MEDIA_ERROR_WHAT_UNKNOWN = 1;

    /**
     * IO错误
     * Error接口用到的常量，可扩充
     */
    int MEDIA_ERROR_WHAT_IO = -1004;


    /**
     * 播放器不支持
     * Error接口用到的常量，可扩充
     */
    int MEDIA_ERROR_WHAT_UNSUPPORTED = -1010;


    /**
     * 媒体资源类
     */
    class MediaSource {
        /**
         * 透传字段，即当回调onPrepared,onCompletion,onError,onInfo等方法时，extra字段回传该值。
         */
        public int id;
        /**
         * 播放地址
         */
        public String playUrl;

        public MediaSource() {

        }
    }

    /**
     * 设定播放该Video的媒体播放器的SurfaceHolder
     *
     * @param sh
     */
    public abstract void setDisplay(SurfaceHolder sh);

    /**
     * 设定使用的数据源(文件路径或http/rtsp地址)
     *
     * @param path
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @throws IllegalStateException
     */
//    public abstract void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException;

    /**
     * 让播放器处于准备状态(异步的)
     *
     * @throws IllegalStateException
     */
    public abstract void prepareAsync() throws IllegalStateException;

    /**
     * 开始或恢复播放
     * 注：此方法只是播放器启动播放时调用一次，后面播放完一个视频后底层需要自动播放下一个视频
     *
     * @throws IllegalStateException
     */
    public abstract void start() throws IllegalStateException;

    /**
     * 停止播放
     *
     * @throws IllegalStateException
     */
    public abstract void stop() throws IllegalStateException;

    /**
     * 暂停播放
     *
     * @throws IllegalStateException
     */
    public abstract void pause() throws IllegalStateException;

    /**
     * 控制当视频播放发生时是否使用SurfaceHolder来保持屏幕
     *
     * @param screenOn
     */
    public abstract void setScreenOnWhilePlaying(boolean screenOn);

    /**
     * 获得视频的宽度
     *
     * @return
     */
    public abstract int getVideoWidth();

    /**
     * 获得视频的高度
     *
     * @return
     */
    public abstract int getVideoHeight();

    /**
     * 检查MedioPlayer是否在播放
     *
     * @return
     */
    public abstract boolean isPlaying();

    /**
     * 搜寻指定的时间位置(ms)
     *
     * @param msec
     * @throws IllegalStateException
     */
    public abstract void seekTo(long msec) throws IllegalStateException;

    /**
     * 获得当前播放的位置,(ms)
     *
     * @return
     */
    public abstract long getCurrentPosition();

    /**
     * 获得视频总时长(ms)
     *
     * @return
     */
    public abstract long getDuration();

    /**
     * 释放与MediaPlayer相关的资源
     */
    public abstract void release();

    /**
     * 重置MediaPlayer到初始化状态
     */
    public abstract void reset();

    /**
     * 注册一个当媒体资源加载并准备完成时候唤起的播放事件
     *
     * @param listener
     */
    public abstract void setOnPreparedListener(OnPreparedListener listener);

    /**
     * 注册一个当媒体资源在播放的时候到达终点时唤起的播放事件
     *
     * @param listener
     */
    public abstract void setOnCompletionListener(OnCompletionListener listener);

    /**
     * 注册一个当网络缓冲数据流变化的时候唤起的播放事件
     *
     * @param listener
     */
    public abstract void setOnBufferingUpdateListener(
            OnBufferingUpdateListener listener);

    /**
     * 注册一个当搜寻操作完成后唤起的播放事件
     *
     * @param listener
     */
    public abstract void setOnSeekCompleteListener(
            OnSeekCompleteListener listener);

    /**
     * 注册一个当视频大小知晓或更新后唤起的播放事件
     *
     * @param listener
     */
    public abstract void setOnVideoSizeChangedListener(
            OnVideoSizeChangedListener listener);

    /**
     * 注册一个当在异步操作过程中发生错误的时候唤起的播放事件
     *
     * @param listener
     */
    public abstract void setOnErrorListener(OnErrorListener listener);

    /**
     * 注册一个当有信息/警告出现的时候唤起的播放事件
     *
     * @param listener
     */
    public abstract void setOnInfoListener(OnInfoListener listener);


    /**
     * 从不能播放到能播放过程中的缓冲事件
     */
    public static interface OnBufferingUpdateListener {
        /**
         * 视频加载或者卡顿过程中的缓冲回调
         *
         * @param mp
         * @param percent 视频加载或者卡顿过程中的缓冲百分比（0-100）
         *                调用时机：一般是在onInfo方法抛出MEDIA_INFO_WHAT_BUFFERING_START事件后开始回调，每当数值变化触发回调
         *                在onInfo方法抛出MEDIA_INFO_WHAT_BUFFERING_END事件后停止回调
         */
        void onBufferingUpdate(IMediaPlayer mp, int percent);
    }


    /**
     * 搜寻操作完成后唤起的播放事件回调
     */
    interface OnSeekCompleteListener {
        void onSeekComplete(IMediaPlayer mp);
    }

    /**
     * 未订制，同系统机制。
     * 视频大小知晓或更新后唤起的播放事件回调
     */
    interface OnVideoSizeChangedListener {
        void onVideoSizeChanged(IMediaPlayer mp, int width, int height,
                                int sar_num, int sar_den);
    }


    /**
     * 为MediaPlayer设定音频流类型
     *
     * @param streamtype
     */
    public abstract void setAudioStreamType(int streamtype);


    /**
     * 为MediaPlayer设置低等级的电源管理状态
     */
    public abstract void setWakeMode(Context context, int mode);

    /**
     * 设置surface为media视频的皮肤
     *
     * @param surface
     */
    public abstract void setSurface(Surface surface);


    /*** 拓展接口 ***/


    /**
     * 设定使用的数据源(文件路径或http/rtsp地址)
     *
     * @param mediaSource 播放列表
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @throws IllegalStateException
     */
    public abstract void setDataSource(MediaSource[] mediaSource) throws IOException,
            IllegalArgumentException, SecurityException, IllegalStateException;


    /**
     * 媒体资源加载并准备完成时唤起的播放事件回调
     */
    public static interface OnPreparedListener {
        /**
         * 媒体资源加载并准备完成时唤起的播放事件回调
         *
         * @param mp
         * @param extra 透传字段，通过setDataSource传入的MediaSource中的每个url都有其对应的id，解码对应的URL时使用对应的id.
         *              调用时机：同系统原生时机
         */
        void onPrepared(IMediaPlayer mp, int extra);
    }

    /**
     * 媒体资源在播放的时候到达终点时唤起的播放事件回调
     */
    public static interface OnCompletionListener {
        /**
         * 媒体资源在播放的时候到达终点时唤起的播放事件回调
         *
         * @param mp
         * @param extra 透传字段，通过setDataSource传入的MediaSource中的每个url都有其对应的id，解码对应的URL时使用对应的id.
         *              调用时机：同系统原生时机
         */
        void onCompletion(IMediaPlayer mp, int extra);
    }

    /**
     * 在异步操作过程中发生错误的时候唤起的播放事件回调
     */
    public static interface OnErrorListener {
        /**
         * 在异步操作过程中发生错误的时候唤起的播放事件回调
         *
         * @param mp
         * @param what       错误事件类型
         * @param extra      透传字段，通过setDataSource传入的MediaSource中的每个url都有其对应的id，解码对应的URL时使用对应的id.
         * @param jsonReport json类型的数据上报{"msgType":"301000","cdn":"xxxxxx",.........}
         * @return 返回true:此方法处理了error事件，返回false或者此方法没有被重写：底层将触发OnCompletionListener回调
         */
        boolean onError(IMediaPlayer mp, int what, int extra, String jsonReport);
    }

    /**
     * 当有信息/警告出现的时候唤起的播放事件回调
     */
    public static interface OnInfoListener {
        /**
         * 当有信息/警告出现的时候唤起的播放事件回调
         *
         * @param mp
         * @param what       信息/警告类型
         * @param extra      透传字段，通过setDataSource传入的MediaSource中的每个url都有其对应的id，解码对应的URL时使用对应的id.
         * @param jsonReport json类型的数据上报，例如：{"msgType":"301000","cdn":"xxxxxx",.........}
         * @return
         */
        boolean onInfo(IMediaPlayer mp, int what, int extra, String jsonReport);
    }

    /**
     * 获得全局缓冲的位置
     *
     * @return 返回当前缓冲时间ms
     */
    public abstract long getBufferedPosition();

}
