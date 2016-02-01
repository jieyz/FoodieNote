package com.yaozu.listener.listener;

import android.graphics.Bitmap;

/**
 * Created by 耀祖 on 2016/1/31.
 */
public interface DownLoadIconListener {
    public void downLoadSuccess(Bitmap bitmap);

    public void downLoadFailed();
}
