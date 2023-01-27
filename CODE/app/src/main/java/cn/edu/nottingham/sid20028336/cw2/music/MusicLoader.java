package cn.edu.nottingham.sid20028336.cw2.music;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

import cn.edu.nottingham.sid20028336.cw2.music.Music;

public class MusicLoader {
    Context context;
    public ArrayList<Music> musicList;

    public MusicLoader(Context context) {
        this.context = context;
        this.musicList = loadMusics(context);
    }

    // load music from sdcard/Music/
    // reference: https://stackoverflow.com/questions/10227895/android-songs-fetching-from-sd-card
    private ArrayList<Music> loadMusics(Context context) {
        ArrayList<Music> musicList = new ArrayList<Music>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        @SuppressLint("Recycle") Cursor c = context.getContentResolver().query(uri, null, selection, null, null);
        if(c != null) {
            while(c.moveToNext()) {
                // create music object and add music object to the list
                String url = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
                String artist = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String musicName = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                int duration = c.getInt(c.getColumnIndex(MediaStore.Audio.Media.DURATION));

                Music music = new Music(url, musicName, artist, duration);
                musicList.add(music);
            }
        }
        return musicList;
    }

    // load music from external storage by url
    public Music loadMusicByUrl(Uri uri) {
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        @SuppressLint("Recycle") Cursor c = this.context.getContentResolver().query(uri, null, selection, null, null);
        if (c != null) {
            if (c.moveToNext()) {
                // create music object
                String url = c.getString(0);
                String musicName = c.getString(2);

                return new Music(url, musicName, "Anonymous", 0);
            }
        }
        return null;
    }
}
