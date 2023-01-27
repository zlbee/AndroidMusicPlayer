package cn.edu.nottingham.sid20028336.cw2.activities;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import cn.edu.nottingham.sid20028336.cw2.R;
import cn.edu.nottingham.sid20028336.cw2.adapters.SinglePlaylistAdapter;
import cn.edu.nottingham.sid20028336.cw2.database.DBHelper;
import cn.edu.nottingham.sid20028336.cw2.music.Music;

public class SinglePlaylistActivity extends AppCompatActivity {
    private static final int INSERT_REQUEST = 1;
    private static final int DELETE_REQUEST = 2;

    ListView singlePlaylistListViewUI;
    TextView currentMusicTextViewUI;
    TextView currentPlaylistTextViewUI;

    SinglePlaylistAdapter adapter;

    String tableName;

    Music currentMusic;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // insert: update the DB and update the list view
        if (requestCode == INSERT_REQUEST) {
            if (resultCode == RESULT_OK) {
                // acquire music list
                assert data != null;
                Bundle bundle = data.getExtras();
                ArrayList<Music> selectedMusics = (ArrayList<Music>) bundle.getSerializable(String.valueOf(R.string.music_list_key));

                // insert musics to the DB
                DBHelper DBHelper = new DBHelper(this);
                for (Music music: selectedMusics) {
                    ContentValues values = new ContentValues();
                    values.put(getString(R.string.db_musicName), music.getMusicName());
                    values.put(getString(R.string.db_artist), music.getArtist());
                    values.put(getString(R.string.db_url), music.getUrl());
                    values.put(getString(R.string.db_duration), music.getDuration());
                    DBHelper.insertItemToTable(tableName, values);
                }

                // update the list view
                singlePlaylistListViewUI.setAdapter(adapter);
            }
        }

        // delete: update the DB and update the list view
        if (requestCode == DELETE_REQUEST) {
            if (resultCode == RESULT_OK) {
                // acquire music list
                assert data != null;
                Bundle bundle = data.getExtras();
                ArrayList<Music> selectedMusics = (ArrayList<Music>) bundle.getSerializable(String.valueOf(R.string.music_list_key));

                // delete music from the DB
                DBHelper DBHelper = new DBHelper(SinglePlaylistActivity.this);
                for (Music music: selectedMusics) {
                    String url = music.getUrl();
                    DBHelper.deleteItemFromTableByUrl(tableName, url);
                }

                // update list view
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_play_list);

        // initialize
        init();

        // set adapter for the list view
        singlePlaylistListViewUI.setAdapter(adapter);

        // listen to list view clicks
        listenListViewItemClick(singlePlaylistListViewUI);
    }

    @SuppressLint("SetTextI18n")
    private void init() {

        tableName = getTableNameFromIntent();

        // initialize UI components
        singlePlaylistListViewUI = findViewById(R.id.playlistListView);
        currentMusicTextViewUI = findViewById(R.id.currentMusicTextVIew);
        currentPlaylistTextViewUI = findViewById(R.id.currentPlaylistTextView1);
        currentPlaylistTextViewUI.setText("Current Playlist: " + tableName);

        // initialize adapter
        adapter = new SinglePlaylistAdapter(this, tableName);

        currentMusic = null;
    }

    private String getTableNameFromIntent() {
        Intent inIntent = getIntent();
        Bundle inBundle = inIntent.getExtras();
        return inBundle.getString(String.valueOf(R.string.table_name_key));
    }

    // listen to list view clicks
    private void listenListViewItemClick(ListView singlePlaylistListViewUI) {
        singlePlaylistListViewUI.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint({"ResourceAsColor", "SetTextI18n"})
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // select current music
                DBHelper DBHelper = new DBHelper(SinglePlaylistActivity.this);
                Cursor c = DBHelper.queryItemFromTableByIndex(tableName, position);
                if (c.moveToNext()) {
                    String musicName = c.getString(1);
                    String artist = c.getString(2);
                    String url = c.getString(3);
                    int duration = c.getInt(4);
                    currentMusic = new Music(url, musicName, artist, duration);

                    // stress selected music (clear all first)
                    for (int i = 0; i < singlePlaylistListViewUI.getChildCount(); i++) {
                        TextView tvi = singlePlaylistListViewUI.getChildAt(i).findViewById(R.id.card_musicNameTextView);
                        tvi.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                    }
                    TextView tv = view.findViewById(R.id.card_musicNameTextView);
                    tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    currentMusicTextViewUI.setText("Current Music: " + currentMusic.getMusicName());
                }
            }
        });
    }

    // insert music to the playlist
    public void insertMusic(View view) {
        Intent intent = new Intent(this, SelectMusicsActivity.class);
        intent.setAction("@string/insert_music_filter");
        startActivityForResult(intent, INSERT_REQUEST);
    }

    // delete music from the playlist
    public void deleteMusic(View view) {
        Intent intent = new Intent(this, SelectMusicsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(String.valueOf(R.string.table_name_key), tableName);
        intent.putExtras(bundle);
        intent.setAction("@string/delete_music_filter");
        startActivityForResult(intent, DELETE_REQUEST);
    }

    // send selected music to main activity to play
    public void toMainPlayMusic(View view) {
        Bundle bundle = new Bundle();
        if (currentMusic == null) {
            // select current music
            DBHelper DBHelper = new DBHelper(SinglePlaylistActivity.this);
            Cursor c = DBHelper.queryItemFromTableByIndex(tableName, 0);
            if (c.moveToNext()) {
                Toast.makeText(this, "You haven't selected a music! Play the first music in " + tableName + " by default.", Toast.LENGTH_LONG).show();
                String musicName = c.getString(1);
                String artist = c.getString(2);
                String url = c.getString(3);
                int duration = c.getInt(4);
                currentMusic = new Music(url, musicName, artist, duration);
                bundle.putSerializable(String.valueOf(R.string.music_object_key), currentMusic); // send current music to main activity
                bundle.putString(String.valueOf(R.string.table_name_key), tableName); // send current playlist to main activity

                Intent intent = new Intent(SinglePlaylistActivity.this, MainActivity.class);
                intent.putExtras(bundle);
                intent.setAction("@string/play_posted_music_filter");
                startActivity(intent);
            }
            else {
                Toast.makeText(this, "There is no music in the playlist to play!", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(this, "Play music in " + tableName + "!", Toast.LENGTH_LONG).show();
            bundle.putSerializable(String.valueOf(R.string.music_object_key), currentMusic); // send current music to main activity
            bundle.putString(String.valueOf(R.string.table_name_key), tableName); // send current playlist to main activity

            Intent intent = new Intent(SinglePlaylistActivity.this, MainActivity.class);
            intent.putExtras(bundle);
            intent.setAction("@string/play_posted_music_filter");
            startActivity(intent);
        }
    }
}