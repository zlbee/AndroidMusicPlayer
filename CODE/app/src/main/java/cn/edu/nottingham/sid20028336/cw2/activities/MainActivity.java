package cn.edu.nottingham.sid20028336.cw2.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import cn.edu.nottingham.sid20028336.cw2.R;
import cn.edu.nottingham.sid20028336.cw2.adapters.MainAdapter;
import cn.edu.nottingham.sid20028336.cw2.broadcast_receivers.MainActivityReceiver;
import cn.edu.nottingham.sid20028336.cw2.database.DBHelper;
import cn.edu.nottingham.sid20028336.cw2.music.Music;
import cn.edu.nottingham.sid20028336.cw2.music.MusicLoader;
import cn.edu.nottingham.sid20028336.cw2.services.MusicPlayerService;

public class MainActivity extends AppCompatActivity implements MainActivityReceiver.MainActivityReceiverInterface {
    private final String TAG = this.getClass().getName();

    private ListView musicListViewUI;
    private SeekBar seekBarUI;
    private TextView progressTextViewUI;
    private TextView musicNameTextViewUI;
    private TextView currentPlaylistTextViewUI;

    private MainAdapter adapter;

    private MainActivityReceiver receiver;

    public ArrayList<Music> localMusicList;
    private Music currentMusic;
    private String currentPlaylistName;

    private DBHelper DBHelper;

    // detect anc change orientations
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.land_activity_main);
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            setContentView(R.layout.activity_main);
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "MainActivity destroyed.");
        // unregister receiver
        this.unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize
        init();

        // request permission and determine next moves
        checkPermission();

    }

    // restore instant states
    @SuppressLint("SetTextI18n")
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.currentPlaylistName = savedInstanceState.getString(String.valueOf(R.string.table_name_key));

    }

    // save instant states
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(String.valueOf(R.string.table_name_key), currentPlaylistName);
    }

    private void init() {
        // initialize UI components
        musicListViewUI = findViewById(R.id.musicListView);
        seekBarUI = findViewById(R.id.seekBar);
        progressTextViewUI = findViewById(R.id.progressTextView);
        musicNameTextViewUI = findViewById(R.id.musicNameTextView);
        currentPlaylistTextViewUI = findViewById(R.id.currentPlaylistTextView);

        // initialize DB helper
        DBHelper = new DBHelper(this);

        // initialize default playlist name
        currentPlaylistName = "localplaylist";

    }

    // check permission in runtime and determine next moves
    protected void checkPermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @SuppressLint("SetTextI18n")
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        // load local music
                        localMusicList = new MusicLoader(getBaseContext()).musicList;

                        // insert local musics to default playlist
                        ContentValues values = new ContentValues();
                        for (Music music: localMusicList) {
                            values.put(getString(R.string.db_musicName), music.getMusicName());
                            values.put(getString(R.string.db_artist), music.getArtist());
                            values.put(getString(R.string.db_url), music.getUrl());
                            values.put(getString(R.string.db_duration), music.getDuration());
                            DBHelper.insertItemToTable("localPlaylist", values);
                        }

                        // update UI
                        adapter = new MainAdapter(MainActivity.this, currentPlaylistName);
                        musicListViewUI.setAdapter(adapter);
                        currentPlaylistTextViewUI.setText("Current Playlist: " + currentPlaylistName);

                        // start service
                        Intent intent = new Intent(MainActivity.this, MusicPlayerService.class);
                        startService(intent);

                        // start broadcast receiver
                        receiver = new MainActivityReceiver(MainActivity.this);

                        // listen to click in the list view
                        listenListViewItemClick(musicListViewUI);

                        // listen to seek bar change in the seek bar
                        listenSeekBarChange(seekBarUI);

                        // listen to implicit intent
                        Intent inIntent = getIntent();
                        listenImplicitIntent(inIntent);

                        // listen to play music intent from SinglePlaylistActivity
                        listenPlaySelectedMusicIntent(inIntent);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // terminate application
                        MainActivity.this.finishAffinity();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    // start playing music
    @SuppressLint("ShowToast")
    public void startPlay(View view) {
        // broadcast
        sendMusicBroadcast("@string/play_filter");

        // update music name UI
        if (currentMusic != null) {
            musicNameTextViewUI.setText(currentMusic.getMusicName());
        }
        else {
            Toast.makeText(this, "Please select a music to play!", Toast.LENGTH_SHORT).show();
        }
    }

    // pause playing music
    public void pausePlay(View view) {
        sendMusicBroadcast("@string/pause_filter");
    }

    // restart playing music
    public void restartPlay(View view) {
        sendMusicBroadcast("@string/restart_filter");
    }

    // stop playing music
    public void stopPlay(View view) {
        sendMusicBroadcast("@string/stop_filter");
    }

    // send broadcast to music service
    private void sendMusicBroadcast(String filterName) {
        Intent intent = new Intent();
        intent.setAction(filterName);

        Bundle bundle = new Bundle();
        if (currentMusic != null) {
            bundle.putSerializable(getString(R.string.music_object_key), currentMusic);
            intent.putExtras(bundle);
        }

        sendBroadcast(intent);
        Log.i(TAG, filterName + " broadcast is sent!");
    }


    // listen to list view item click event
    public void listenListViewItemClick(ListView musicListViewUI) {
        // listen to click in the list view
        musicListViewUI.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                stopPlay(null);

                // select music
                DBHelper DBHelper = new DBHelper(MainActivity.this);
                Cursor c = DBHelper.queryItemFromTableByIndex(currentPlaylistName, position);
                if (c.moveToNext()) {
                    String musicName = c.getString(1);
                    String artist = c.getString(2);
                    String url = c.getString(3);
                    int duration = c.getInt(4);
                    currentMusic = new Music(url, musicName, artist, duration);
//                Log.i(TAG, "Current music selected! Position: " + currentMusic.getMusicProgress());
                }

                // update current music in the text view
                musicNameTextViewUI.setText(currentMusic.getMusicName());
                // configure seek bar maximum value
                seekBarUI.setMax(currentMusic.getDuration());
                // stress selected music (clear all first)
                for (int i = 0; i < musicListViewUI.getChildCount(); i++) {
                    TextView tvi = musicListViewUI.getChildAt(i).findViewById(R.id.card_musicNameTextView);
                    tvi.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                }
                TextView tv = view.findViewById(R.id.card_musicNameTextView);
                tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }
        });
    }

    // listen to seek bar change event
    public void listenSeekBarChange(SeekBar seekBarUI) {
        seekBarUI.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                Log.d(TAG,"onProgressChanged = " + progress);
                if (currentMusic != null) {
                    // set music progress according to the seek bar
                    currentMusic.setMusicProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i(TAG,"onStartTrackingTouch");
                pausePlay(null);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i(TAG,"onStopTrackingTouch");
                if (seekBar.getProgress() != seekBar.getMax()) {
                    currentMusic.setMusicProgress(seekBar.getProgress());
                    startPlay(null);
                }
            }
        });
    }

    // set seek bar position
    @Override
    public void setSeekBarProgress(int progress) {
        seekBarUI.setProgress(progress);
    }

    // set text view progress in "mm:ss" format
    @Override
    public void setTextViewProgress(int progress) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        String progressTimeFormat = sdf.format(new Date(progress));
        progressTextViewUI.setText(progressTimeFormat);
    }

    // set current playlist name
    @Override
    public void setCurrentPlaylist(String playlistName) {
        this.currentPlaylistName = playlistName;
    }

    // destroy self
    @Override
    public void stopMainActivity() {
        // terminate application
        MainActivity.this.finishAffinity();
    }

    // go to playlists activity
    public void toPlaylistsActivity(View view) {
        Intent intent = new Intent(this, PlaylistsActivity.class);
        startActivity(intent);
    }

    // switch to next music
    @SuppressLint("ShowToast")
//    @Override
    public void indexToNextMusic(View view) {
        if (currentMusic != null) {
            DBHelper DBHelper = new DBHelper(this);

            // select current music in the table
            Cursor c = DBHelper.queryAllItemsFromTable(currentPlaylistName);
            if (c.moveToNext()) {
                do {
                    if (c.getString(1).equals(currentMusic.getMusicName()))
                        break;
                } while (c.moveToNext());
            }
            // change currentMusic to next music in the database
            if (c.moveToNext()) {
                // stop current playing music
                stopPlay(null);

                // index to next music
                String musicName = c.getString(1);
                String artist = c.getString(2);
                String url = c.getString(3);
                int duration = c.getInt(4);
                currentMusic = new Music(url, musicName, artist, duration);

                // update music name text view
                musicNameTextViewUI.setText(currentMusic.getMusicName());
                // stress selected music (clear all first)
                int pos = 0;
                for (int i = 0; i < musicListViewUI.getChildCount(); i++) {
                    TextView tvi = musicListViewUI.getChildAt(i).findViewById(R.id.card_musicNameTextView);
                    tvi.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    if (tvi.getText().toString().equals(currentMusic.getMusicName())) {
                        pos = i;
                    }
                }
                TextView tv = musicListViewUI.getChildAt(pos).findViewById(R.id.card_musicNameTextView);
                tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }
            else {
                Toast.makeText(this, "It is the end of the playlist!", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(this, "You haven't selected a music!", Toast.LENGTH_LONG).show();
        }
    }

    // switch to previous music
    @SuppressLint("ShowToast")
//    @Override
    public void indexToPrevMusic(View view) {
        if (currentMusic != null) {
            DBHelper DBHelper = new DBHelper(this);

            // select current music in the table
            Cursor c = DBHelper.queryAllItemsFromTable(currentPlaylistName);
            if (c.moveToNext()) {
                do {
                    if (c.getString(1).equals(currentMusic.getMusicName()))
                        break;
                } while (c.moveToNext());
            }
            // change currentMusic to next music in the database
            if (c.moveToPrevious()) {
                // stop current playing music
                stopPlay(null);

                // index to next music
                String musicName = c.getString(1);
                String artist = c.getString(2);
                String url = c.getString(3);
                int duration = c.getInt(4);
                currentMusic = new Music(url, musicName, artist, duration);

                // update music name text view
                musicNameTextViewUI.setText(currentMusic.getMusicName());
                // stress selected music (clear all first)
                int pos = 0;
                for (int i = 0; i < musicListViewUI.getChildCount(); i++) {
                    TextView tvi = musicListViewUI.getChildAt(i).findViewById(R.id.card_musicNameTextView);
                    tvi.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    if (tvi.getText().toString().equals(currentMusic.getMusicName())) {
                        pos = i;
                    }
                }
                TextView tv = musicListViewUI.getChildAt(pos).findViewById(R.id.card_musicNameTextView);
                tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }
            else {
                Toast.makeText(this, "It is the origin of the playlist!", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(this, "You haven't selected a music!", Toast.LENGTH_LONG).show();
        }
    }

    // listen to play selected music intent
    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void listenPlaySelectedMusicIntent(Intent inIntent) {
        if (Objects.equals(inIntent.getAction(), "@string/play_posted_music_filter")) {
            // get posted music and playlist name
            Bundle b = inIntent.getExtras();

            // set current playlist
            this.currentPlaylistName = b.getString(String.valueOf(R.string.table_name_key));

            // play music
            this.currentMusic = (Music) b.getSerializable(String.valueOf(R.string.music_object_key));
            startPlay(null);

            // update UI
            adapter = new MainAdapter(MainActivity.this, currentPlaylistName);
            musicListViewUI.setAdapter(adapter);
            musicNameTextViewUI.setText(currentMusic.getMusicName());
            currentPlaylistTextViewUI.setText("Current Playlist: " + currentPlaylistName);

            // configure seek bar maximum value
            seekBarUI.setMax(currentMusic.getDuration());
        }
    }

    // listen to implicit intent
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void listenImplicitIntent(Intent inIntent) {
        if (Objects.equals(inIntent.getAction(), "android.intent.action.VIEW")) {

            // get posted music
            Uri uri = inIntent.getData();
            MusicLoader musicLoader = new MusicLoader(this);
            Music postedMusic = musicLoader.loadMusicByUrl(uri);

            if (postedMusic != null) {
                 // play music
                String postedMusicName = postedMusic.getMusicName();
                for (Music music: localMusicList) {
                    if (music.getMusicName().equals(postedMusicName)) {
                        currentMusic = music;
                    }
                }
                startPlay(null);

                // update music name text view
                musicNameTextViewUI.setText(currentMusic.getMusicName());

                // configure seek bar maximum value
                seekBarUI.setMax(currentMusic.getDuration());
            }
        }
    }
}