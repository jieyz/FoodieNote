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
import android.os.Handler;
import android.os.Message;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yaozu.listener.R;
import com.yaozu.listener.YaozuApplication;
import com.yaozu.listener.constant.IntentKey;
import com.yaozu.listener.listener.PersonState;
import com.yaozu.listener.playlist.lyric.LRCUtils;
import com.yaozu.listener.service.MusicService;
import com.yaozu.listener.utils.DownLoadUtil;
import com.yaozu.listener.utils.FileUtil;
import com.yaozu.listener.utils.IntentUtil;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by jieyz on 2015/9/29.
 */
public class MusicLyricActivity extends SwipeBackActivity implements View.OnClickListener {
    //?songname=爷爷泡的茶&singer=周杰伦"
    private static final String downloadUrl = "http://120.27.129.229:8080/TestServers/servlet/DownLoadServlet";
    private static final String baiduGetLrcIdUrl = "http://box.zhangmen.baidu.com/x?op=12&count=1";//&title=我是一只鱼$$任贤齐$$$$";
    private static final String baiduDownLoadLrc = "http://box.zhangmen.baidu.com/bdlrc/";
    private SeekBar mSeekBar;
    private MediaSeekBarChangeListener seekBarChangeListener;
    private MusicService mService;
    private TextView media_current_position, media_duration;
    private TextView lyricTitle;
    private TextView lyricSinger;
    private RelativeLayout background;
    //播放暂停
    private ImageView mPlay;
    //上一首和下一首
    private ImageView mPrev, mNext;
    //跟随播放的用户
    private TextView mFollowUser;
    private ListView mShowLyricView;
    private LyricAdapter mAdapter;
    private ArrayList<LRCUtils.timelrc> lyricData;
    private boolean mFirstEnter = true;

    private final int SET_PROGRESS = 0;
    private final int GET_CURRENT_PLAY_POSTTION = 1;
    private final int FILE_DOWNLOAD = 2;

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
                    if (mService == null) {
                        return;
                    }
                    long currentposition = mService.getCurrentPlayPosition();
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
                                        if (mFirstEnter) {
                                            mFirstEnter = false;
                                            mShowLyricView.setSelectionFromTop(position, mShowLyricView.getHeight() / 2);
                                        } else {
                                            mShowLyricView.smoothScrollToPositionFromTop(position, mShowLyricView.getHeight() / 2);
                                        }
                                        notifyPostion = position;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    sendMessageDelayed(this.obtainMessage(GET_CURRENT_PLAY_POSTTION), 100);
                    break;
                case FILE_DOWNLOAD:
                    File file = (File) msg.obj;
                    LRCUtils utils = new LRCUtils();
                    utils.ReadLRC(file, lyricData);
                    addEmptyDatatolyricData(lyricData, true);
                    mAdapter = new LyricAdapter();
                    mShowLyricView.setAdapter(mAdapter);
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(GET_CURRENT_PLAY_POSTTION), 100);
                    break;
            }
        }
    };
    private int mDuration;
    private String name;
    private String singer;
    private FileUtil fileUtil;
    private String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_home);
        findviewByids();
        setOnclickListener();
        initDate();
        mService = YaozuApplication.getIntance().getMusicService();
        setProgress();
        //initFollowUserState();
        //changeMenuState();

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
        mPrev = (ImageView) findViewById(R.id.play_btn_prev);
        mNext = (ImageView) findViewById(R.id.play_btn_next);
        mFollowUser = (TextView) findViewById(R.id.music_lyric_follow_user);

        //封面背景
        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.common_bg);
        mBitmap = getAeroBitmap(mBitmap);
        BitmapDrawable drawable = new BitmapDrawable(mBitmap);
        //drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        background.setBackground(drawable);
    }

    private void initDate() {
        Intent intent = getIntent();
        name = intent.getStringExtra(IntentKey.MEDIA_FILE_SONG_NAME);
        singer = intent.getStringExtra(IntentKey.MEDIA_FILE_SONG_SINGER);
        lyricTitle.setText(name);
        lyricSinger.setText(singer);

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(singer)) {
            showMusicLyric(name);
        }
    }

    /**
     * 显示歌词
     */
    private void showMusicLyric(String name) {
        LRCUtils utils = new LRCUtils();
        fileUtil = new FileUtil();
        String path = fileUtil.getSDPATH();
        path = path + singer + File.separator + name + ".lrc";

        lyricData = new ArrayList<LRCUtils.timelrc>();
        addEmptyDatatolyricData(lyricData, false);
        File file = new File(path);
        if (!file.exists()) {
            downLoadLyric();
            return;
        } else {
            utils.ReadLRC(file, lyricData);
            addEmptyDatatolyricData(lyricData, true);
            mAdapter = new LyricAdapter();
            mShowLyricView.setAdapter(mAdapter);
            mHandler.sendMessageDelayed(mHandler.obtainMessage(GET_CURRENT_PLAY_POSTTION), 100);
        }
    }

    File file = null;

    private void downLoadLyric() {
        try {
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(singer)) {
                return;
            }
            final String url = downloadUrl + "?songname=" + URLEncoder.encode(name, "UTF-8") + "&singer=" + URLEncoder.encode(singer, "UTF-8");
            //Log.d(TAG,"====url====>"+url);
            //创建歌词目录(以歌手名为目录名)
            fileUtil.creatSDDir(singer.trim());
            try {
                file = fileUtil.creatSDFile(singer + File.separator + name + ".lrc");
            } catch (IOException e) {
                e.printStackTrace();
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DownLoadUtil.download(fileUtil, url, file);
                    String downLrcUrl = null;
                    if (getFileSize(file) <= 0) {
                        file.delete();
                        //Log.d(TAG, "=======文件大小为0，已删除=======>");
                        //&title=我是一只鱼$$任贤齐$$$$"
                        String url = baiduGetLrcIdUrl + "&title=" + name + "$$" + singer + "$$$$";
                        String lrcid = DownLoadUtil.baiduDownLoadLrc(url);
                        if (TextUtils.isEmpty(lrcid)) {
                            //Log.d(TAG, "=======获取的歌词为空!=======>");
                            return;
                        }
                        downLrcUrl = baiduDownLoadLrc + Integer.parseInt(lrcid) / 100 + "/" + lrcid + ".lrc";
                        //Log.d(TAG, "======downLrcUrl=======>" + downLrcUrl);
                    }
                    DownLoadUtil.download(fileUtil, downLrcUrl, file);
                    if (getFileSize(file) <= 0) {
                        file.delete();
                    }
                    Message msg = mHandler.obtainMessage();
                    msg.what = FILE_DOWNLOAD;
                    msg.obj = file;
                    mHandler.sendMessage(msg);
                }
            }).start();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) {
        long size = 0;
        try {
            if (file.exists()) {
                FileInputStream fis = null;
                fis = new FileInputStream(file);
                size = fis.available();
            } else {
                file.createNewFile();
                Log.e("获取文件大小", "文件不存在!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return size;
    }


    /**
     * 在lyricData的前后加入空的数据
     */
    private void addEmptyDatatolyricData(ArrayList<LRCUtils.timelrc> lyricData, boolean islast) {
        for (int i = 0; i < 6; i++) {
            LRCUtils.timelrc timelrc = new LRCUtils.timelrc();
            if (islast && i == 0) {
                timelrc.setTimePoint(999999);
            }
            lyricData.add(timelrc);
        }
    }

    private void setOnclickListener() {
        mPlay.setOnClickListener(this);
        mFollowUser.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_btn_play:
                if (mService != null) {
                    if (mService.isPlaying()) {
                        mService.pause();
                        mPlay.setImageResource(R.drawable.play_btn_play);
                    } else {
                        mService.start();
                        mPlay.setImageResource(R.drawable.play_btn_pause);
                    }
                }
                break;
            case R.id.music_lyric_follow_user:
                IntentUtil.toUserDetail(MusicLyricActivity.this, YaozuApplication.followUserName, YaozuApplication.followUserid,
                        PersonState.PLAYING.toString(), lyricTitle.getText().toString() + "--" + lyricSinger.getText().toString());
                break;
        }
    }

    private boolean seekTracking = false;

    private class MediaSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        private int newPosition;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            newPosition = (mDuration * progress) / 1000;
            String time = generateTime(newPosition);
            if (media_current_position != null) {
                media_current_position.setText(time);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            seekTracking = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            seekTracking = false;
            if (mService != null) {
                //int pos = (int) (((float) seekBar.getProgress() / 1000f) * mService.getDuration());
                mService.seekto(newPosition);
            }
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
            return view;
        }
    }

    private void setProgress() {
        if (mService == null) {
            return;
        }
        long currentpos = mService.getCurrentPlayPosition();
        int duration = mService.getDuration();
        if (!seekTracking) {
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
            script.setRadius(3.0f /* e.g. 3.f */);
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
        this.name = name;
        lyricSinger.setText(singer);
        this.singer = singer;

        lyricData.clear();
        showMusicLyric(name);
    }

    public void notifySongPlaying() {
        //changeMenuState();
        if (YaozuApplication.isFollowPlay) {
            mPlay.setImageResource(R.drawable.play_btn_pause_not_available);
        } else {
            mPlay.setImageResource(R.drawable.play_btn_pause);
        }
    }

    public void notifySongPause() {
        //changeMenuState();
        if (YaozuApplication.isFollowPlay) {
            mPlay.setImageResource(R.drawable.play_btn_play_not_available);
        } else {
            mPlay.setImageResource(R.drawable.play_btn_play);
        }
    }

    /**
     * 改变操作栏的状态，变为可操作或者不可操作
     */
    private void changeMenuState() {
        if (YaozuApplication.isFollowPlay) {
            mPlay.setClickable(false);
            mSeekBar.setEnabled(false);
            mPrev.setImageResource(R.drawable.play_btn_prev_not_available);
            mNext.setImageResource(R.drawable.play_btn_next_not_available);

            mFollowUser.setVisibility(View.VISIBLE);
            mFollowUser.setText(YaozuApplication.followUserName);
        } else {
            mPlay.setClickable(true);
            mSeekBar.setEnabled(true);
            mPrev.setImageResource(R.drawable.play_btn_prev);
            mNext.setImageResource(R.drawable.play_btn_next);

            mFollowUser.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化一起听的用户状态
     */
    private void initFollowUserState() {
        if (mService != null && mService.isPlaying()) {
            if (YaozuApplication.isFollowPlay) {
                mPlay.setImageResource(R.drawable.play_btn_pause_not_available);
            } else {
                mPlay.setImageResource(R.drawable.play_btn_pause);
            }
        } else {
            if (YaozuApplication.isFollowPlay) {
                mPlay.setImageResource(R.drawable.play_btn_play_not_available);
            } else {
                mPlay.setImageResource(R.drawable.play_btn_play);
            }
        }
    }
}
