package com.yaozu.listener.playlist.provider;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.yaozu.listener.db.dao.SongInfoDao;
import com.yaozu.listener.playlist.model.Song;
import com.yaozu.listener.utils.EncodingConvert;
import com.yaozu.listener.utils.UploadSongsUtil;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 揭耀祖 on 2015/11/1.
 */
public class JavaMediaScanner {
    private Context context;
    private SongInfoDao mSongInfoDao;

    public JavaMediaScanner(Context context) {
        this.context = context;
        mSongInfoDao = new SongInfoDao(context);
    }

    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
    private static final BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();

    /**
     * 获取指定目录下的音频信息
     *
     * @param path
     * @return
     */
    public List<Song> getSongListFromPath(String path) {
        List<Song> pathList = new ArrayList<>();
/*        List<Song> list = (List<Song>) scannerMedia();
        for (int i = 0; i < list.size(); i++) {
            Song song = list.get(i);
            if (song.getFileUrl().contains(path)) {
                pathList.add(song);
            }
        }*/
        return pathList;
    }

    /**
     * 首先从系统提供的MediaStore中获取媒体文件的信息
     * 为了防止乱码然后再通过NativeMediaScanner去解析媒体文件中的title、album、artist信息
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void scannerMedia() {
        if (context != null) {
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                    null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String displayName = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                    if (mSongInfoDao.isHaveSong(displayName)) {
                        continue;
                    }
                    int id = cursor.getInt(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    String title = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    String album = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                    long albumid = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                    String artist = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    String path = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
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
                            displayName, mimeType, duration, size + "", albumid);
/*                    try {
                        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                        mmr.setDataSource(path);
                        String _album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                        if (_album != null) {
                            byte[] bs = _album.getBytes("iso-8859-1");
                            System.out.println("===bs====>" + Arrays.toString(bs));
                            System.out.println("======_album====>" + _album);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }*/
                    //插入数据库
                    if (!mSongInfoDao.isHaveSong(displayName)) {
                        if (path.endsWith(".mp3")) {
                            NativeMediaScanner scanner = new NativeMediaScanner();
                            scanner.processFile(path, scanner);
                            if (TextUtils.isEmpty(scanner.getmTitle())) {
                                parserMediaMetaByRetriever(path, song);
                            } else {
                                song.setTitle(scanner.getmTitle());
                                song.setSinger(scanner.getmArtist());
                                song.setAlbum(scanner.getmAlbum());
                            }
                        } else {
                            parserMediaMetaByRetriever(path, song);
                        }
                        if (!TextUtils.isEmpty(song.getTitle())) {
                            if (TextUtils.isEmpty(song.getAlbum())) {
                                song.setAlbum("未知专辑");
                            }

                            if (TextUtils.isEmpty(song.getSinger())) {
                                song.setSinger("未知歌手");
                            }
                            mSongInfoDao.add(song);
                        }
                    }
                }
                cursor.close();
            }
        }
    }

    private void parserMediaMetaByRetriever(String path, Song song){
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            mmr.setDataSource(path);
        }catch (IllegalArgumentException e){
            song.setTitle("");
            return;
        }
        String _artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String _title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String _album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        _artist = EncodingConvert.toUtf(_artist);
        _title = EncodingConvert.toUtf(_title);
        _album = EncodingConvert.toUtf(_album);
        song.setTitle(_title);
        song.setSinger(_artist);
        song.setAlbum(_album);
    }

    private String getAlbumArt(Context context, int album_id) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = context.getContentResolver().query(
                Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)),
                projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        cur = null;
        return album_art;
    }

    public Bitmap getImage(Context context, int album_id) {
        String albumArt = getAlbumArt(context, album_id);
        Bitmap bm = null;
        if (albumArt == null) {

        } else {
            bm = BitmapFactory.decodeFile(albumArt);
        }
        return bm;
    }

    private static boolean isChinese(char paramChar) {
        Character.UnicodeBlock localUnicodeBlock = Character.UnicodeBlock.of(paramChar);
        return (localUnicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) || (localUnicodeBlock == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS) || (localUnicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A) || (localUnicodeBlock == Character.UnicodeBlock.GENERAL_PUNCTUATION) || (localUnicodeBlock == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION) || (localUnicodeBlock == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS);
    }
}
