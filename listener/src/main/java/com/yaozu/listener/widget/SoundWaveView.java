package com.yaozu.listener.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

/**
 * Created by 耀祖 on 2015/11/29.
 */
public class SoundWaveView extends View {
    private Paint mPaint;
    /**
     * 上下文对像
     *
     * @param context
     */
    private Context mContext;

    /**
     * is runing to draw
     */
    private boolean runing = true;

    private Thread mThread;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            invalidate();
        }
    };

    public SoundWaveView(Context context) {
        super(context);
        mContext = context;
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#363636"));
    }

    public SoundWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#363636"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Path path = new Path();
        Random random = new Random();
        int rvalue = random.nextInt(getHeight());
        if (rvalue >= (getHeight() - 20)) {
            rvalue = (getHeight() - 20);
        }
//        path.addRect(new RectF(60, rvalue, getWidth()-60, getHeight()), Path.Direction.CCW);
//        mPaint.setStyle(Paint.Style.FILL);
//        mPaint.setStrokeWidth(2);
//        mPaint.setColor(Color.BLUE);
//        canvas.drawPath(path, mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(24);
        if (runing) {
            canvas.drawLine(12, getHeight(), 12, rvalue, mPaint);
            int rvalue2 = random.nextInt(getHeight());
            canvas.drawLine(42, getHeight(), 42, rvalue2, mPaint);
            int rvalue3 = random.nextInt(getHeight());
            canvas.drawLine(72, getHeight(), 72, rvalue3, mPaint);
        } else {
            canvas.drawLine(12, getHeight(), 12, getHeight() - 10, mPaint);
            canvas.drawLine(42, getHeight(), 42, getHeight() - 10, mPaint);
            canvas.drawLine(72, getHeight(), 72, getHeight() - 10, mPaint);
        }

    }

    public void start() {
        runing = true;
        if (mThread == null) {
            mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (runing) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Message msg = mHandler.obtainMessage();
                        mHandler.sendMessage(msg);
                    }
                    mThread = null;
                }
            });
            mThread.start();
        }
    }

    public void stop() {
        runing = false;
        invalidate();
    }
}
