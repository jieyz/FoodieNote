package com.yaozu.listener.playlist.provider;

import android.util.Log;

/**
 * Created by 耀祖 on 2016/1/10.
 */
public class NativeMediaScanner {
    private final String TAG = this.getClass().getSimpleName();
    private String mTitle;
    private String mArtist;
    private String mAlbum;
    static{
        System.loadLibrary("mediascanner");
    }
    public native void processFile(String path,NativeMediaScanner client);

    /**
     * Called by native code to return metadata extracted from media files.
     */
    public void handleStringTag(String name, String value){
        Log.d(TAG,"====java====>"+"name == "+name+"  value=="+value);
        if(name.equalsIgnoreCase("title") || name.startsWith("title;")){
            mTitle = value;
        }else if(name.equalsIgnoreCase("artist") || name.startsWith("artist;")){
            mArtist = value.trim();
        }else if(name.equalsIgnoreCase("album") || name.startsWith("album;")){
            mAlbum = value.trim();
        }
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmArtist() {
        return mArtist;
    }

    public String getmAlbum() {
        return mAlbum;
    }
}
