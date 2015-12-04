package com.yaozu.listener.activity;

import android.app.Activity;
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
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yaozu.listener.R;
import com.yaozu.listener.YaozuApplication;
import com.yaozu.listener.service.MusicService;

import java.io.InputStream;
import java.util.Locale;

/**
 * Created by jieyz on 2015/9/29.
 */
public class MusicHomeActivity extends Activity {
    private SeekBar mSeekBar;
    private MediaSeekBarChangeListener seekBarChangeListener;
    private MusicService mService;
    private TextView media_current_position, media_duration;
    private RelativeLayout background;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setProgress();
        }
    };
    private int mDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_home);
        findviewByids();
        mService = YaozuApplication.getIntance().getMusicService();
        setProgress();
    }

    private void findviewByids() {
        mSeekBar = (SeekBar) findViewById(R.id.media_seekbar);
        seekBarChangeListener = new MediaSeekBarChangeListener();
        mSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        media_current_position = (TextView) findViewById(R.id.media_current_position);
        media_duration = (TextView) findViewById(R.id.media_duration);
        background = (RelativeLayout) findViewById(R.id.music_background);
        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.background);
        mBitmap = getAeroBitmap(mBitmap);
        BitmapDrawable drawable = new BitmapDrawable(mBitmap);
        drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        background.setBackground(drawable);
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

        mHandler.sendMessageDelayed(mHandler.obtainMessage(0), 1000);
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
        mHandler.removeMessages(0);
    }
}
