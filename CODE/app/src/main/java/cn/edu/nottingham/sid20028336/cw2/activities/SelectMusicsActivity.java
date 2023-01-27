package cn.edu.nottingham.sid20028336.cw2.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import cn.edu.nottingham.sid20028336.cw2.R;
import cn.edu.nottingham.sid20028336.cw2.adapters.SelectMusicsAdapter;
import cn.edu.nottingham.sid20028336.cw2.database.DBHelper;
import cn.edu.nottingham.sid20028336.cw2.music.Music;
import cn.edu.nottingham.sid20028336.cw2.music.MusicLoader;

public class SelectMusicsActivity extends AppCompatActivity {
    ListView localMusicListListViewUI;

    ArrayList<Music> musicList;
    ArrayList<Music> selectedMusics;

    SelectMusicsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_musics);

        // init
        init();

        // set adapter for list
        localMusicListListViewUI.setAdapter(adapter);

        // listen to list view item click event
        listenListViewItemClick(localMusicListListViewUI);
    }

    @Override
    public void onBackPressed() {
        goBack(null);
        super.onBackPressed();

    }

    private void init() {
        localMusicListListViewUI = findViewById(R.id.localMusicList);

        // present music list according to action
        String action = getIntent().getAction();
        if (action == "@string/insert_music_filter") {
            musicList = new MusicLoader(this).musicList;
        }
        else if (action == "@string/delete_music_filter") {
            DBHelper DBHelper = new DBHelper(this);
            musicList = new ArrayList<>();
            Cursor c = DBHelper.queryAllItemsFromTable(getIntent().getExtras().getString(String.valueOf(R.string.table_name_key)));
            if (c.moveToNext()) {
                do {
                    String musicName = c.getString(1);
                    String artist = c.getString(2);
                    String url = c.getString(3);
                    int duration = c.getInt(4);
                    Music music = new Music(url, musicName, artist, duration);
                    musicList.add(music);
                } while (c.moveToNext());
            }
        }

        selectedMusics = new ArrayList<>();

        adapter = new SelectMusicsAdapter(this, musicList);
    }

    // listen to list view item click event
    public void listenListViewItemClick(ListView musicListViewUI) {

        // put the item to the selected music list
        musicListViewUI.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Music selectedMusic = musicList.get(position);
                selectedMusics.add(selectedMusic);

                view.setBackgroundColor(getResources().getColor(R.color.purple_700));
            }
        });
    }

    // send selected musics back
    public void goBack(View view) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(String.valueOf(R.string.music_list_key), selectedMusics);

        Intent intent = new Intent(SelectMusicsActivity.this, SinglePlaylistActivity.class);
        intent.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    // clear selected items
    @SuppressLint("ResourceAsColor")
    public void clearSelection(View view) {
        selectedMusics.clear();
        for (int i = 0; i < localMusicListListViewUI.getChildCount(); i++) {
            localMusicListListViewUI.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.purple_500));
        }
    }
}