package com.kun.musicappdemo.bean;

import androidx.annotation.NonNull;

public class Song {
    //歌曲ID：MediaStore.Audio.Media._ID
    // Int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
    // 歌曲的名称 ：MediaStore.Audio.Media.TITLE
    // String tilte = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
    // 歌曲的专辑名：MediaStore.Audio.Media.ALBUM
    // String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
    // 歌曲的歌手名： MediaStore.Audio.Media.ARTIST
    // String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
    // 歌曲文件的全路径：MediaStore.Audio.Media.DATA
    // String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
    // 歌曲文件的名称：MediaStroe.Audio.Media.DISPLAY_NAME
    // String display_name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
    // 歌曲文件的发行日期：MediaStore.Audio.Media.YEAR
    // String year = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR));
    // 歌曲的总播放时长：MediaStore.Audio.Media.DURATION
    // Int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
    // 歌曲文件的大小：MediaStore.Audio.Media.SIZE
    // Int size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
    private int songID;//歌曲ID：
    private String title;// 歌曲的名称
    private String album;// 歌曲的专辑名：
    private int albumID;//专辑图片ID
    private String artist; // 歌曲的歌手名：
    private String url;// 歌曲文件的全路径：
    private String year;// 歌曲文件的发行日期
    private int duration;// 歌曲的总播放时长
    private Long size;// 歌曲文件的大小

    public Song() {
    }

    public Song(int songID, String title, String album, int albumID, String artist, String url, String year, int duration, Long size) {
        this.songID = songID;
        this.title = title;
        this.album = album;
        this.albumID = albumID;
        this.artist = artist;
        this.url = url;
        this.year = year;
        this.duration = duration;
        this.size = size;
    }

    public int getSongID() {
        return songID;
    }

    public void setSongID(int songID) {
        this.songID = songID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getAlbumID() {
        return albumID;
    }

    public void setAlbumID(int albumID) {
        this.albumID = albumID;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "Song{" +
                "songID=" + songID +
                ", title='" + title + '\'' +
                ", album='" + album + '\'' +
                ", albumID='" + albumID + '\'' +
                ", artist='" + artist + '\'' +
                ", url='" + url + '\'' +
                ", year='" + year + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                '}';
    }
}


