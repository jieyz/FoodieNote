package com.yaozu.listener.widget.cropview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;

/**
 * Created by jieyz on 2016/3/8.
 */
public class CropImageView extends ImageViewTouchBase {

    public ArrayList<HighlightView> mHighlightViews = new ArrayList<HighlightView>();
    HighlightView mMotionHighlightView = null;
    float mLastX, mLastY;
    float movex, movey;
    int mMotionEdge;
    private boolean isShow = true;
    private int MODE;

    public void setShow(boolean is) {
        isShow = is;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mBitmapDisplayed.getBitmap() != null) {
            for (HighlightView hv : mHighlightViews) {
                hv.mMatrix.set(getImageMatrix());
                hv.invalidate();
                if (hv.mIsFocused) {
                    centerBasedOnHighlightView(hv);
                }
            }
        }
    }

    public CropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public int[] getWandH() {
        int[] wh = new int[2];
        wh[0] = getWidth();
        wh[1] = getHeight();
        return wh;
    }

    @Override
    protected void zoomTo(float scale, float centerX, float centerY) {
        super.zoomTo(scale, centerX, centerY);
        for (HighlightView hv : mHighlightViews) {
            hv.mMatrix.set(getImageMatrix());
            hv.invalidate();
        }
    }

    @Override
    protected void zoomIn() {
        super.zoomIn();
        for (HighlightView hv : mHighlightViews) {
            hv.mMatrix.set(getImageMatrix());
            hv.invalidate();
        }
    }

    @Override
    protected void zoomOut() {
        super.zoomOut();
        for (HighlightView hv : mHighlightViews) {
            hv.mMatrix.set(getImageMatrix());
            hv.invalidate();
        }
    }

    @Override
    protected void postTranslate(float deltaX, float deltaY) {
        super.postTranslate(deltaX, deltaY);
        for (int i = 0; i < mHighlightViews.size(); i++) {
            HighlightView hv = mHighlightViews.get(i);
            hv.mMatrix.postTranslate(deltaX, deltaY);
            hv.invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // CR: inline case blocks.
                for (int i = 0; i < mHighlightViews.size(); i++) { // CR:
                    // iterator
                    // for; if
                    // not, then
                    // i++ =>
                    // ++i.
                    HighlightView hv = mHighlightViews.get(i);
                    int edge = hv.getHit(event.getX(), event.getY());
                    if (edge != HighlightView.GROW_NONE) {
                        mMotionEdge = edge;
                        mMotionHighlightView = hv;
                        mLastX = event.getX();
                        mLastY = event.getY();
                        // CR: get rid of the extraneous parens below.
                        mMotionHighlightView
                                .setMode((edge == HighlightView.MOVE) ? HighlightView.ModifyMode.Move
                                        : HighlightView.ModifyMode.Grow);
                        break;
                    }
                }
                invalidate();
                break;
            // CR: vertical space before case blocks.
            case MotionEvent.ACTION_UP:
                if (mMotionHighlightView != null) {
                    // centerBasedOnHighlightView(mMotionHighlightView);
                    mMotionHighlightView.setMode(HighlightView.ModifyMode.None);
                }
                mMotionHighlightView = null;
                MODE = 4;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mMotionHighlightView != null) {
                    float dx = event.getX() - mLastX;
                    float dy = event.getY() - mLastY;

                    mMotionHighlightView.handleMotion(mMotionEdge, dx, dy);
                    mLastX = event.getX();
                    mLastY = event.getY();

                    if (true) {
                        // This section of code is optional. It has some user
                        // benefit in that moving the crop rectangle against
                        // the edge of the screen causes scrolling but it means
                        // that the crop rectangle is no longer fixed under
                        // the user's finger.
                        ensureVisible(mMotionHighlightView);
                    }
                }
                movex = event.getX();
                movey = event.getY();
                invalidate();
                break;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                center(true, true);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                // if we're not zoomed then there's no point in even allowing
                // the user to move the image around. This call to center puts
                // it back to the normalized location (with false meaning don't
                // animate).
                if (getScale() == 1F) {
                    center(true, true);
                }
                break;
        }

        return true;
    }

    // Pan the displayed image to make sure the cropping rectangle is visible.
    private void ensureVisible(HighlightView hv) {
        Rect r = hv.mDrawRect;

        int panDeltaX1 = Math.max(0, getLeft() - r.left);
        int panDeltaX2 = Math.min(0, getRight() - r.right);

        int panDeltaY1 = Math.max(0, getTop() - r.top);
        int panDeltaY2 = Math.min(0, getBottom() - r.bottom);

        int panDeltaX = panDeltaX1 != 0 ? panDeltaX1 : panDeltaX2;
        int panDeltaY = panDeltaY1 != 0 ? panDeltaY1 : panDeltaY2;

        if (panDeltaX != 0 || panDeltaY != 0) {
            panBy(panDeltaX, panDeltaY);
        }
    }

    // If the cropping rectangle's size changed significantly, change the
    // view's center and scale according to the cropping rectangle.
    private void centerBasedOnHighlightView(HighlightView hv) {
        Rect drawRect = hv.mDrawRect;

        float width = drawRect.width();
        float height = drawRect.height();

        float thisWidth = getWidth();
        float thisHeight = getHeight();

        float z1 = thisWidth / width * .6F;
        float z2 = thisHeight / height * .6F;

        float zoom = Math.min(z1, z2);
        zoom = zoom * this.getScale();
        zoom = Math.max(1F, zoom);

        if ((Math.abs(zoom - getScale()) / zoom) > .1) {
            float[] coordinates = new float[]{hv.mCropRect.centerX(),
                    hv.mCropRect.centerY()};
            getImageMatrix().mapPoints(coordinates);
            zoomTo(zoom, coordinates[0], coordinates[1], 300F); // CR: 300.0f.
        }

        ensureVisible(hv);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isShow) {
            for (int i = 0; i < mHighlightViews.size(); i++) {
                mHighlightViews.get(i).draw(canvas);
            }
        }
    }

    public void removeAll() {
        mHighlightViews.removeAll(mHighlightViews);
        invalidate();
    }

    public void add(HighlightView hv) {
        //设置最小的裁剪宽度
        hv.setWidthCap(this.getRotateBitmap().getBitmap().getWidth() * 0.2f);
        mHighlightViews.add(hv);
        invalidate();
    }
}
