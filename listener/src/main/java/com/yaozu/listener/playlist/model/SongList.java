package com.yaozu.listener.playlist.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ò«×æ on 2015/11/28.
 */
public class SongList {
    private String totalcount;
    private List<SongModel> songlist = new ArrayList<SongModel>();

    public String getTotalcount() {
        return totalcount;
    }

    public void setTotalcount(String totalcount) {
        this.totalcount = totalcount;
    }

    public List<SongModel> getSonglist() {
        return songlist;
    }

    public void setSonglist(List<SongModel> songlist) {
        this.songlist = songlist;
    }

    public class SongModel{
        private String id;
        private String filename;
        private String title;
        private String duration;
        private String singer;
        private String album;
        private String downloadurl;
        private String albumid;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getSinger() {
            return singer;
        }

        public void setSinger(String singer) {
            this.singer = singer;
        }

        public String getAlbum() {
            return album;
        }

        public void setAlbum(String album) {
            this.album = album;
        }

        public String getAlbumid() {
            return albumid;
        }

        public void setAlbumid(String albumid) {
            this.albumid = albumid;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getDownloadurl() {
            return downloadurl;
        }

        public void setDownloadurl(String downloadurl) {
            this.downloadurl = downloadurl;
        }
    }
}
