package cn.edu.nottingham.sid20028336.cw2.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.util.Objects;

import cn.edu.nottingham.sid20028336.cw2.broadcast_receivers.MainActivityReceiver;
import cn.edu.nottingham.sid20028336.cw2.broadcast_receivers.MusicPlayerServiceReceiver;
import cn.edu.nottingham.sid20028336.cw2.R;
import cn.edu.nottingham.sid20028336.cw2.activities.MainActivity;
import cn.edu.nottingham.sid20028336.cw2.music.Music;

public class MusicPlayerService extends Service implements MusicPlayerServiceReceiver.MusicPlayInterface {
    private final String TAG = this.getClass().getName();

    private MusicPlayerServiceReceiver receiver;

    public MediaPlayer mediaPlayer;
    private String prevUrl; // previous url

    private Thread thread;
    private Boolean exit;

    // notification tags
    private final int NOTIFICATION_ID = 001; // a unique int for each notification
    private final String CHANNEL_ID = "100";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Music service started!");

        // initialize
        mediaPlayer = new MediaPlayer();
        prevUrl = null;
        exit = false;

        // start broadcast receiver
        receiver = new MusicPlayerServiceReceiver(this);

        // create a notification
        createNotification();

        return super.onStartCommand(intent, flags, startId);
    }

    // destroy the service
    @Override
    public void onDestroy() {
        // unregister receiver
        unregisterReceiver(receiver);

        // close notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);

        // stop thread and release music player
        exit = true;
        thread.interrupt();
        thread = null;
        mediaPlayer.stop();
        mediaPlayer.release();

        Log.i(TAG, "Music service destroyed!");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // create a notification
    // reference: Week 3 lecture materials
    private void createNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel name";
            String description = "channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(MusicPlayerService.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Intent actionIntent = new Intent("@string/stop_service_filter");
        PendingIntent pendingActionIntent = PendingIntent.getBroadcast(MusicPlayerService.this, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("MusicPlayerService")
                .setContentText("Return to CW2 - Music Player Application")
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_launcher_foreground, "Destroy Service", pendingActionIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        //notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        startForeground(NOTIFICATION_ID, mBuilder.build());
    }

    // broadcast back music progress
    private void broadcastBackProgress(Music music) {
        Intent outIntent = new Intent();
        outIntent.setAction("@string/seek_position_filter");

        Bundle outBundle = new Bundle();
        outBundle.putSerializable(this.getString(R.string.music_object_key), music);

        outIntent.putExtras(outBundle);
        this.sendBroadcast(outIntent);
//            Log.i(TAG, "Music progress broadcast is sent back!");
    }

    // https://developer.android.com/reference/android/media/MediaPlayer
    public void stopMusic() {
        // paused/started -> stopped
        mediaPlayer.seekTo(0);
        mediaPlayer.stop();
    }

    public void restartMusic() {
        // paused/started -> started
        mediaPlayer.seekTo(0);
        mediaPlayer.start();
    }

    public void pauseMusic(Music music) {
        // started -> paused
        mediaPlayer.pause();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void playMusic(Music music) {
        // -> idle
        mediaPlayer.reset();

        // idle -> initialized
        int musicProgress = music.getMusicProgress();
        try {
            mediaPlayer.setDataSource(music.getUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // initialized -> prepared
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // prepared -> started
        if (Objects.equals(prevUrl, music.getUrl())) {
            mediaPlayer.seekTo(musicProgress);
//                mediaPlayer.seekTo(music.getMusicProgress());
        } else {
            mediaPlayer.seekTo(0);
        }
        mediaPlayer.start();

        // record previous url
        prevUrl = music.getUrl();
    }

    // start broadcast back music progress
    @Override
    public void startBroadcastBack(Music music) {
        // update music progress and broadcast back to main
        if (thread == null) {
            thread = new Thread(() -> {
                while (!exit) {
                    // update progress
                    music.setMusicProgress(mediaPlayer.getCurrentPosition());

                    // broadcast back to main activity
                    broadcastBackProgress(music);
                }
            });

            // start thread
            if (!thread.isAlive()) {
                thread.start();
            }
        }
    }

    // stop self
    @Override
    public void stopMusicService() {
        try {
            this.onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
