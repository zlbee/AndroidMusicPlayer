package cn.edu.nottingham.sid20028336.cw2.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.Objects;

import cn.edu.nottingham.sid20028336.cw2.R;
import cn.edu.nottingham.sid20028336.cw2.activities.MainActivity;
import cn.edu.nottingham.sid20028336.cw2.music.Music;

public class MainActivityReceiver extends BroadcastReceiver {
    private final String TAG = this.getClass().getName();

    public MainActivityReceiver(Context context) {
        super();
        // configure filters
        IntentFilter filter = new IntentFilter();
        filter.addAction("@string/seek_position_filter");
        filter.addAction("@string/update_current_playlist_filter");
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
        MainActivity mainActivityContext = (MainActivity) context;
        if (intent.getAction().equals("@string/stop_service_filter")) {
            mainActivityContext.stopMainActivity();
        }
        else {
            if (inBundle == null) {
                return;
            }
            switch (intent.getAction()) {
                case "@string/seek_position_filter":
                    Music music = (Music) inBundle.getSerializable(context.getString(R.string.music_object_key));
                    int progress = music.getMusicProgress();
                    mainActivityContext.setSeekBarProgress(progress);
                    mainActivityContext.setTextViewProgress(progress);
                    break;
                case "@string/update_current_playlist_filter":
                    String playlistName = inBundle.getString(String.valueOf(R.string.table_name_key));
                    mainActivityContext.setCurrentPlaylist(playlistName);
                    break;
            }
        }
    }

    public interface MainActivityReceiverInterface {
        void setSeekBarProgress(int progress);
        void setTextViewProgress(int progress);
        void setCurrentPlaylist(String playlistName);
        void stopMainActivity();
    }
}
