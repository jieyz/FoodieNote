package com.yaozu.listener.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yaozu.listener.R;
import com.yaozu.listener.constant.IntentKey;
import com.yaozu.listener.widget.cropview.CropImageView;
import com.yaozu.listener.widget.cropview.HighlightView;

/**
 * Created by jieyz on 2016/3/8.
 */
public class CropImageActivity extends BaseActivity implements View.OnClickListener {
    private CropImageView mImageView;
    private final Handler mHandler = new Handler();

    //源图片
    private Bitmap mBitmap;

    //裁剪后的图片
    private Bitmap croppedImage;
    private boolean mCircleCrop = false;
    private HighlightView mCrop;
    //后退键
    private ImageView mBack;
    //使用键
    private TextView mUse;

    //是否裁剪过
    private boolean hasCut = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        mImageView = (CropImageView) findViewById(R.id.activity_crop_imageview);
        mBack = (ImageView) findViewById(R.id.activity_crop_back);
        mUse = (TextView) findViewById(R.id.activity_crop_use);
        mBack.setOnClickListener(this);
        mUse.setOnClickListener(this);


        //TODO
        mBitmap = IntentKey.cropBitmap;
        IntentKey.cropBitmap = null;
        mImageView.setShow(true);
        mImageView.setImageBitmapResetBase(mBitmap, true);
        mRunFaceDetection.run();
    }

    Runnable mRunFaceDetection = new Runnable() {

        Matrix mImageMatrix;

        // Create a default HightlightView if we found no face in the picture.
        private void makeDefault() {
            HighlightView hv = new HighlightView(mImageView);
            //设置最小的裁剪宽度
            hv.setWidthCap(mBitmap.getWidth() * 0.2f);

            int width = mBitmap.getWidth();
            int height = mBitmap.getHeight();

            Rect imageRect = new Rect(0, 0, width, height);

            // CR: sentences!
            // make the default size about 4/5 of the width or height
            int cropWidth = Math.min(width, height) * 4 / 5;
            int cropHeight = cropWidth;

            int x = (width - cropWidth) / 2;
            int y = (height - cropHeight) / 2;

            RectF cropRect = new RectF(x, y, x + cropWidth, y + cropHeight);
            hv.setup(mImageMatrix, imageRect, cropRect, mCircleCrop, false);
            mImageView.add(hv);
        }

        public void run() {
            mImageMatrix = mImageView.getImageMatrix();
            mHandler.post(new Runnable() {
                public void run() {
                    makeDefault();
                    mImageView.invalidate();
                    if (mImageView.mHighlightViews.size() == 1) {
                        mCrop = mImageView.mHighlightViews.get(0);
                        mCrop.setFocus(true);
                    }
                }
            });
        }
    };

    /**
     * 确定剪切
     */
    private void onEnter() {
        if (mCrop == null) {
            return;
        }
        Rect r = mCrop.getCropRect();

        int width = r.width(); // CR: final == happy panda!
        int height = r.height();

        // If we are circle cropping, we want alpha channel, which is the
        // third param here.
        croppedImage = Bitmap.createBitmap(width, height,
                mCircleCrop ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        {
            Canvas canvas = new Canvas(croppedImage);
            Rect dstRect = new Rect(0, 0, width, height);
            // r = new Rect(r.left - (widths - width)/2, r.top - (heights -
            // height)/2, r.right + (widths - width)/2, r.bottom + (heights -
            // height)/2);
            canvas.drawBitmap(mBitmap, r, dstRect, null);
        }
        //mImageView.setImageBitmap(croppedImage);
        hasCut = true;
        IntentKey.cropBitmap = croppedImage;
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!hasCut) {
            IntentKey.cropBitmap = null;
        }
    }

    @Override
    public void notifyCurrentSongMsg(String name, String singer, long album_id, int currentPos) {

    }

    @Override
    public void notifySongPlaying() {

    }

    @Override
    public void notifySongPause() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_crop_back:
                finish();
                break;
            case R.id.activity_crop_use:
                mImageView.removeAll();
                onEnter();
                mImageView.invalidate();
                break;
        }
    }
}
