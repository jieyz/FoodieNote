package com.yaozu.listener.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yaozu.listener.R;
import com.yaozu.listener.YaozuApplication;
import com.yaozu.listener.constant.IntentKey;
import com.yaozu.listener.playlist.lyric.LRCUtils;
import com.yaozu.listener.service.MusicService;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by jieyz on 2015/9/29.
 */
public class MusicLyricActivity extends SwipeBackActivity implements View.OnClickListener {
    private SeekBar mSeekBar;
    private MediaSeekBarChangeListener seekBarChangeListener;
    private MusicService mService;
    private TextView media_current_position, media_duration;
    private TextView lyricTitle;
    private TextView lyricSinger;
    private RelativeLayout background;
    private ImageView mPlay;
    private ListView mShowLyricView;
    private LyricAdapter mAdapter;
    private ArrayList<LRCUtils.timelrc> lyricData;

    private final int SET_PROGRESS = 0;
    private final int GET_CURRENT_PLAY_POSTTION = 1;

    private int notifyPostion = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SET_PROGRESS:
                    setProgress();
                    break;
                case GET_CURRENT_PLAY_POSTTION:
                    if(mService == null){
                        return;
                    }
                    int currentposition = mService.getCurrentPlayPosition();
                    if (lyricData != null && lyricData.size() > 0) {
                        for (int i = 0; i < lyricData.size(); i++) {
                            LRCUtils.timelrc nexttimelrc = lyricData.get(i);
                            if (currentposition < nexttimelrc.getTimePoint()) {
                                if (i == 0 || currentposition > lyricData.get(i - 1).getTimePoint()) {
                                    int position = (i - 1) >= 0 ? (i - 1) : 0;
                                    if (notifyPostion != position) {
                                        LRCUtils.timelrc currenttimelrc = lyricData.get(position);
                                        mAdapter.setCurrentPosition(position);
                                        mAdapter.notifyDataSetChanged();
                                        mShowLyricView.smoothScrollToPositionFromTop(position, mShowLyricView.getHeight() / 2);
                                        notifyPostion = position;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    sendMessageDelayed(this.obtainMessage(GET_CURRENT_PLAY_POSTTION), 100);
                    break;
            }
        }
    };
    private int mDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_home);
        findviewByids();
        setOnclickListener();
        initDate();
        mService = YaozuApplication.getIntance().getMusicService();
        setProgress();
        if (mService != null && mService.isPlaying()) {
            mPlay.setImageResource(R.drawable.play_btn_pause);
        }
        registerPushReceiver();

        SwipeBackLayout swipeBackLayout = getSwipeBackLayout();
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        swipeBackLayout.setEdgeSize(width / 2);
    }

    private void findviewByids() {
        mSeekBar = (SeekBar) findViewById(R.id.media_seekbar);
        seekBarChangeListener = new MediaSeekBarChangeListener();
        mSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        media_current_position = (TextView) findViewById(R.id.media_current_position);
        media_duration = (TextView) findViewById(R.id.media_duration);
        background = (RelativeLayout) findViewById(R.id.music_background);
        mPlay = (ImageView) findViewById(R.id.play_btn_play);
        lyricTitle = (TextView) findViewById(R.id.music_lyric_title);
        lyricSinger = (TextView) findViewById(R.id.music_lyric_singer);
        mShowLyricView = (ListView) findViewById(R.id.music_lyric_show);

        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        mBitmap = getAeroBitmap(mBitmap);
        BitmapDrawable drawable = new BitmapDrawable(mBitmap);
        drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        background.setBackground(drawable);
    }

    private void initDate() {
        Intent intent = getIntent();
        String name = intent.getStringExtra(IntentKey.MEDIA_FILE_SONG_NAME);
        String singer = intent.getStringExtra(IntentKey.MEDIA_FILE_SONG_SINGER);
        lyricTitle.setText(name);
        lyricSinger.setText(singer);

        showMusicLyric(name);
    }

    /**
     * 显示歌词
     */
    private void showMusicLyric(String name){
        LRCUtils utils = new LRCUtils();
        String path = Environment.getExternalStorageDirectory().getPath();
        path = path + File.separator + "KuwoMusic" + File.separator + "lyrics" + File.separator + name + ".lrc";

        lyricData = new ArrayList<LRCUtils.timelrc>();
        addEmptyDatatolyricData(lyricData,false);
        utils.ReadLRC(new File(path),lyricData);
        addEmptyDatatolyricData(lyricData,true);
        mAdapter = new LyricAdapter();
        mShowLyricView.setAdapter(mAdapter);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(GET_CURRENT_PLAY_POSTTION), 100);
    }

    /**
     * 在lyricData的前后加入空的数据
     */
    private void addEmptyDatatolyricData(ArrayList<LRCUtils.timelrc> lyricData,boolean islast){
       for(int i=0;i<6;i++){
           LRCUtils.timelrc timelrc = new LRCUtils.timelrc();
           if(islast && i == 0){
               timelrc.setTimePoint(999999);
           }
           lyricData.add(timelrc);
       }
    }

    private void setOnclickListener() {
        mPlay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_btn_play:
                if (mService.isPlaying()) {
                    mService.pause();
                    mPlay.setImageResource(R.drawable.play_btn_play);
                } else {
                    mService.start();
                    mPlay.setImageResource(R.drawable.play_btn_pause);
                }
                break;
        }
    }

    private class MediaSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            long newPosition = (mDuration * progress) / 1000;
            String time = generateTime(newPosition);
            if (media_current_position != null) {
                media_current_position.setText(time);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int pos = (int) (((float) seekBar.getProgress() / 1000f) * mService.getDuration());
            mService.seekto(pos);
        }
    }

    private class LyricAdapter extends BaseAdapter {
        private int mPosition = 0;

        public void setCurrentPosition(int position) {
            mPosition = position;
        }

        @Override
        public int getCount() {
            if (lyricData != null) {
                return lyricData.size();
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View view = View.inflate(MusicLyricActivity.this, R.layout.activity_lyric_item, null);
            TextView textView = (TextView) view.findViewById(R.id.music_lyric_list_item);
            if (lyricData != null && lyricData.size() > 0) {
                String lyric = lyricData.get(i).getLrcString();
                textView.setText(lyric);
                if (mPosition == i) {
                    textView.setTextColor(getResources().getColor(R.color.appthemecolor));
                } else {
                    textView.setTextColor(getResources().getColor(R.color.gray_white));
                }
            }
            return textView;
        }
    }

    private void setProgress() {
        if (mService == null) {
            return;
        }
        int currentpos = mService.getCurrentPlayPosition();
        int duration = mService.getDuration();
        if (mSeekBar != null) {
            if (duration != -1) {
                int pos = (int) (((float) currentpos / (float) duration) * 1000);
                mSeekBar.setProgress(pos);
            }
        }
        mDuration = duration;
        if (media_duration != null) {
            media_duration.setText(generateTime(duration));
        }

        if (media_current_position != null) {
            media_current_position.setText(generateTime(currentpos));
        }

        mHandler.sendMessageDelayed(mHandler.obtainMessage(SET_PROGRESS), 1000);
    }

    private static String generateTime(long position) {
        int totalSeconds = (int) (position / 1000);

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return String.format(Locale.US, "%02d:%02d", minutes, seconds).toString();
        }
    }

    private Bitmap getAeroBitmap(Bitmap sentBitmap) {
        if (Build.VERSION.SDK_INT > 16) {
            Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

            final RenderScript rs = RenderScript.create(this);
            final Allocation input = Allocation.createFromBitmap(rs, sentBitmap, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            final Allocation output = Allocation.createTyped(rs, input.getType());
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setRadius(25.0f /* e.g. 3.f */);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(bitmap);
            return bitmap;
        }
        return sentBitmap;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(SET_PROGRESS);
        mHandler.removeMessages(GET_CURRENT_PLAY_POSTTION);
        unRegisterPushRecevier();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.music_lyric_out, R.anim.music_lyric_bottom_out);
    }

    /**
     * @Description:
     * @author
     * @date 2013-10-28 jieyaozu 10:30:27
     */

    protected void registerPushReceiver() {
        if (musicServiceBroadcastReceiver == null) {
            musicServiceBroadcastReceiver = new MusicServiceBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(IntentKey.NOTIFY_CURRENT_SONG_MSG);
            filter.addAction(IntentKey.NOTIFY_SONG_PAUSE);
            filter.addAction(IntentKey.NOTIFY_SONG_PLAYING);
            localBroadcastManager = LocalBroadcastManager.getInstance(this);
            localBroadcastManager.registerReceiver(musicServiceBroadcastReceiver, filter);
        }
    }

    /**
     * @Description:
     * @author
     * @date 2013-10-28 jieyaozu10:17:28
     */
    protected void unRegisterPushRecevier() {
        if (musicServiceBroadcastReceiver != null) {
            localBroadcastManager = LocalBroadcastManager.getInstance(this);
            localBroadcastManager.unregisterReceiver(musicServiceBroadcastReceiver);
            musicServiceBroadcastReceiver = null;
        }
    }

    private MusicServiceBroadcastReceiver musicServiceBroadcastReceiver;
    /**
     *
     */
    private LocalBroadcastManager localBroadcastManager;

    /**
     * 2015-11-5
     */
    private class MusicServiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (IntentKey.NOTIFY_CURRENT_SONG_MSG.equals(intent.getAction())) {
                String mSongName = intent.getStringExtra(IntentKey.MEDIA_FILE_SONG_NAME);
                String mSinger = intent.getStringExtra(IntentKey.MEDIA_FILE_SONG_SINGER);
                int currentPos = intent.getIntExtra(IntentKey.MEDIA_CURRENT_INDEX, -1);
                notifyCurrentSongMsg(mSongName, mSinger, currentPos);
            } else if (IntentKey.NOTIFY_SONG_PLAYING.equals(intent.getAction())) {
                notifySongPlaying();
            } else if (IntentKey.NOTIFY_SONG_PAUSE.equals(intent.getAction())) {
                notifySongPause();
            }
        }

    }

    public void notifyCurrentSongMsg(String name, String singer, int currentPos) {
        lyricTitle.setText(name);
        lyricSinger.setText(singer);
        showMusicLyric(name);
    }

    public void notifySongPlaying() {

    }

    public void notifySongPause() {

    }
}
