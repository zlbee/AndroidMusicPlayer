package cn.edu.nottingham.sid20028336.cw2.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.util.Objects;

import cn.edu.nottingham.sid20028336.cw2.R;
import cn.edu.nottingham.sid20028336.cw2.music.Music;
import cn.edu.nottingham.sid20028336.cw2.services.MusicPlayerService;

// broadcast receiver for this service
public class MusicPlayerServiceReceiver extends BroadcastReceiver {
    private final String TAG = this.getClass().getName();

    public MusicPlayerServiceReceiver(Context context) {
        super();
        // configure filters
        IntentFilter filter = new IntentFilter();
        filter.addAction("@string/play_filter");
        filter.addAction("@string/pause_filter");
        filter.addAction("@string/restart_filter");
        filter.addAction("@string/stop_filter");
        filter.addAction("@string/stop_service_filter");
        context.registerReceiver(this, filter);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Intent received!");

        // extract bundle
        Bundle inBundle = intent.getExtras();

        // actions
        MusicPlayerService musicPlayerServiceContext = (MusicPlayerService) context;
        if (intent.getAction().equals("@string/stop_service_filter")) {
            musicPlayerServiceContext.stopMusicService();
        }
        else {
            // do not execute if no music is selected
            if (inBundle == null) {
                return;
            }
            // extract music from bundle
            Music music = (Music) inBundle.getSerializable(context.getString(R.string.music_object_key));
            switch (intent.getAction()) {
                case "@string/play_filter":
                    musicPlayerServiceContext.playMusic(music);
                    break;
                case "@string/pause_filter":
                    musicPlayerServiceContext.pauseMusic(music);
                    break;
                case "@string/restart_filter":
                    musicPlayerServiceContext.restartMusic();
                    break;
                case "@string/stop_filter":
                    musicPlayerServiceContext.stopMusic();
                    break;
            }

            // send music progress back
            musicPlayerServiceContext.startBroadcastBack(music);
        }
    }

    public interface MusicPlayInterface {
        void playMusic(Music music);
        void pauseMusic(Music music);
        void restartMusic();
        void stopMusic();
        void startBroadcastBack(Music music);
        void stopMusicService();
    }

}
