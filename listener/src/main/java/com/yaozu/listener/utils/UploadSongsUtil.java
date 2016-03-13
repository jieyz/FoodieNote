package com.yaozu.listener.utils;

import android.content.Context;
import android.util.Log;

import com.yaozu.listener.db.dao.SongInfoDao;
import com.yaozu.listener.listener.UploadListener;
import com.yaozu.listener.playlist.model.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jieyaozu on 2016/3/11.
 */
public class UploadSongsUtil {
    private Context mContext;
    private LinkedList<Thread> threads = new LinkedList<Thread>();
    private SongInfoDao songInfoDao;
    private boolean isRunning = false;
    private static UploadSongsUtil instance;
    private static List<String> songtags = new ArrayList<String>();

    public UploadSongsUtil(Context context) {
        mContext = context;
        songInfoDao = new SongInfoDao(mContext);
    }

    public static UploadSongsUtil getInstance(Context context) {
        if (instance == null) {
            instance = new UploadSongsUtil(context);
        }
        return instance;
    }

    public void addUploadThread(final Song song) {
        if (songtags.contains(song.getTitle())) {
            Log.d("UploadSongsUtil", "===上传任务已经存在=======>" + song.getTitle());
            return;
        } else {
            songtags.add(song.getTitle());
        }
        synchronized (threads) {
            Thread thread = NetUtil.uploadFile(mContext, song, new File(song.getFileUrl()), new UploadListener() {
                @Override
                public void uploadSuccess() {
                    songInfoDao.updateSongHaveUpload(song);
                    isRunning = false;
                    onStart();
                    Log.d("UploadSongsUtil", "===上传成功====title=======>" + song.getTitle());
                }

                @Override
                public void uploadFailed() {
                    Log.e("UploadSongsUtil", "===上传失败====title=======>" + song.getTitle());
                }
            });
            thread.setName(song.getTitle());
            threads.add(thread);
        }
    }

    public void onStart() {
        synchronized (this) {
            if (!isRunning) {
                Thread t = threads.poll();
                if (t != null) {
                    t.start();
                    isRunning = true;
                }
            }
        }
    }
}
