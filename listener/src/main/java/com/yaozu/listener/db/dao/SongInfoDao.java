package com.yaozu.listener.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yaozu.listener.db.AppDbHelper;
import com.yaozu.listener.playlist.model.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 耀祖 on 2015/11/28.
 */
public class SongInfoDao {
    private AppDbHelper helper;
    private Context context;

    public SongInfoDao(Context context){
        this.context = context;
        helper = new AppDbHelper(context);
    }

    /**
     * 添加一首歌到数据库中
     * @param songinfo
     */
    public void add(Song songinfo) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("insert into songinfo (songid,fileName,title,duration,singer,album,type,size,fileurl,downloadurl,albumid) values (?,?,?,?,?,?,?,?,?,?,?)",
                    new Object[] { songinfo.getId(), songinfo.getFileName(),songinfo.getTitle(),songinfo.getDuration(),songinfo.getSinger(),
                            songinfo.getAlbum(),songinfo.getType(),songinfo.getSize(),songinfo.getFileUrl(),songinfo.getDownloadurl(),(int)songinfo.getAlbumid()});
        }
        db.close();
    }

    /**
     * 删除
     */
    public void deleteSongById(Song songinfo){
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("delete from songinfo where songid = ?",
                    new Object[] {songinfo.getId()});
        }
        db.close();
    }

    public List<Song> findAllSongInfo(){
        List<Song> songinfos = new ArrayList<Song>();
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from songinfo",null);
            while (cursor.moveToNext()) {
                Song info = new Song();
                int songid = cursor.getInt(cursor.getColumnIndex("songid"));
                String fileName = cursor.getString(cursor.getColumnIndex("fileName"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                int duration = cursor.getInt(cursor.getColumnIndex("duration"));
                String singer = cursor.getString(cursor.getColumnIndex("singer"));
                String album = cursor.getString(cursor.getColumnIndex("album"));
                String type = cursor.getString(cursor.getColumnIndex("type"));
                String  size = cursor.getString(cursor.getColumnIndex("size"));
                String  fileurl = cursor.getString(cursor.getColumnIndex("fileurl"));
                String  downloadurl = cursor.getString(cursor.getColumnIndex("downloadurl"));
                int  albumid = cursor.getInt(cursor.getColumnIndex("albumid"));

                info.setId(songid);
                info.setFileName(fileName);
                info.setTitle(title);
                info.setDuration(duration);
                info.setSinger(singer);
                info.setAlbum(album);
                info.setType(type);
                info.setSize(size);
                info.setFileUrl(fileurl);
                info.setDownloadurl(downloadurl);
                info.setAlbumid(albumid);

                songinfos.add(info);
            }
        }
        db.close();
        return songinfos;
    }

    /**
     * is have this song
     * @param fileName
     * @return
     */
    public boolean isHaveSong(String fileName){
        boolean have = false;
        if(fileName == null){
            return true;
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from songinfo where fileName = ?",new String[]{fileName});
            if (cursor.moveToFirst()) {
                have = true;
            }
        }
        db.close();
        return have;
    }
}
