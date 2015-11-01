package com.yaozu.foodienote.playlist.model;

/**
 * Created by ҫ�� on 2015/11/1.
 */
public class Song {
    private int id;
    private String fileName;
    private String title;
    private int duration;
    private String singer;
    private String album;
    private String type;
    private String size;
    private String fileUrl;


    public Song(int id, String title, String album, String singer, String fileUrl, String fileName, String type,
                int duration, String size) {
        super();
        this.id = id;
        this.fileName = fileName;
        this.title = title;
        this.duration = duration;
        this.singer = singer;
        this.album = album;
        this.type = type;
        this.size = size;
        this.fileUrl = fileUrl;
    }

    @Override
    public String toString() {
        return "Song [fileName=" + fileName + ", title=" + title
                + ", duration=" + duration + ", singer=" + singer + ", album="
                + album + ", type=" + type + ", size="
                + size + ", fileUrl=" + fileUrl + "]";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}