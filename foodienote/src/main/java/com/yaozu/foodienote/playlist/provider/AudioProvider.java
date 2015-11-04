package com.yaozu.foodienote.playlist.provider;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.yaozu.foodienote.playlist.model.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 耀祖 on 2015/11/1.
 */
public class AudioProvider {
    private Context context;

    public AudioProvider(Context context) {
        this.context = context;
    }

    /**
     * 获得指定目录下的歌曲信息
     * @param path
     * @return
     */
    public List<Song> getSongListFromPath(String path){
        List<Song> pathList = new ArrayList<>();
        List<Song> list = (List<Song>) getList();
        for(int i = 0;i<list.size();i++){
            Song song = list.get(i);
            if(song.getFileUrl().contains(path)){
                pathList.add(song);
            }
        }
        return pathList;
    }

    public List<?> getList() {
        List<Song> list = null;
        if (context != null) {
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                    null, null);
            if (cursor != null) {
                list = new ArrayList<Song>();
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    String title = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    String album = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                    String artist = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    String path = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    String displayName = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                    String mimeType = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE));
                    int duration = cursor
                            .getInt(cursor
                                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                    long size = cursor
                            .getLong(cursor
                                    .getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                    Song song = new Song(id, title, album, artist, path,
                            displayName, mimeType, duration, size+"");
                    list.add(song);
                }
                cursor.close();
            }
        }
        return list;
    }

}
