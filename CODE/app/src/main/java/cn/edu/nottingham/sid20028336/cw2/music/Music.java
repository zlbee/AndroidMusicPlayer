package cn.edu.nottingham.sid20028336.cw2.music;

import android.graphics.Bitmap;

import java.io.File;
import java.io.Serializable;

public class Music implements Serializable {
    private String url;
    private File musicFile;
    private String musicName;
    private String artist;
    private String album;
    private Bitmap albumCover;
    private int musicProgress;
    private int duration;

    public Music(String url, String musicName, String artist, int duration) {
        this.setUrl(url);
        this.setMusicName(musicName);
        this.setArtist(artist);
        this.setMusicProgress(0);
        this.setDuration(duration);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Bitmap getAlbumCover() {
        return albumCover;
    }

    public void setAlbumCover(Bitmap albumCover) {
        this.albumCover = albumCover;
    }

    public File getMusicFile() {
        return musicFile;
    }

    public void setMusicFile(File musicFile) {
        this.musicFile = musicFile;
    }

    public int getMusicProgress() {
        return musicProgress;
    }

    public void setMusicProgress(int musicProgress) {
        this.musicProgress = musicProgress;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
