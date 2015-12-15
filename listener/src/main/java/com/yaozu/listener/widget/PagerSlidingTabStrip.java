package com.yaozu.listener.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by 耀祖 on 2015/12/15.
 */
public class PagerSlidingTabStrip extends View {

    private Context mContext;
    private ViewPager mViewPager;
    private int mTabWidth = 0;
    private Paint paint;
    private ViewPager.OnPageChangeListener onPageChangeListener;
    private int currentPosition = 0;
    private float mPositionOffset = 0.0f;

    public PagerSlidingTabStrip(Context context) {
        super(context);
        mContext = context;
        paint = new Paint();
        paint.setColor(Color.parseColor("#7EC0EE"));
    }

    public PagerSlidingTabStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        paint = new Paint();
        paint.setColor(Color.parseColor("#7EC0EE"));
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
    }

    public void setViewPager(ViewPager pager) {
        mViewPager = pager;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        mTabWidth = width / mViewPager.getAdapter().getCount();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
                currentPosition = position;
                mPositionOffset = positionOffset;
                invalidate();
            }

            @Override
            public void onPageSelected(int position) {
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrollStateChanged(state);
                }
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.FILL);
        float left = currentPosition * mTabWidth + mPositionOffset * mTabWidth;
        float right = left + mTabWidth;
        canvas.drawRect(left, 0, right, getHeight(), paint);
    }
}
