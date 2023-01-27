package cn.edu.nottingham.sid20028336.cw2.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import cn.edu.nottingham.sid20028336.cw2.R;
import cn.edu.nottingham.sid20028336.cw2.adapters.SelectPlaylistsAdapter;

public class SelectPlaylistsActivity extends AppCompatActivity {
    ListView playlistsListViewUI;

    SelectPlaylistsAdapter adapter;

    ArrayList<Integer> playlistPositions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_playlists);

        // initialize
        init();

        // bind list view to the adapter
        playlistsListViewUI.setAdapter(adapter);

        // listen to list view item click event
        listenListViewItemClick(playlistsListViewUI);
    }

    private void init() {
        playlistsListViewUI = findViewById(R.id.playlistsListView);

        adapter = new SelectPlaylistsAdapter(this);

        playlistPositions = new ArrayList<>();
    }

    @Override
    public void onBackPressed() {
        goBack(null);
        super.onBackPressed();
    }

    // listen to list view item click event
    public void listenListViewItemClick(ListView playlistsListViewUI) {

        // put the item to the selected music list
        playlistsListViewUI.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playlistPositions.add(position);

                view.setBackgroundColor(getResources().getColor(R.color.purple_700));
            }
        });
    }

    // send playlist positions back
    public void goBack(View view) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(String.valueOf(R.string.playlist_positions_key), playlistPositions);

        Intent intent = new Intent(SelectPlaylistsActivity.this, SinglePlaylistActivity.class);
        intent.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    // clear selected items
    @SuppressLint("ResourceAsColor")
    public void clearSelection(View view) {
        playlistPositions.clear();
        for (int i = 0; i < playlistsListViewUI.getChildCount(); i++) {
            playlistsListViewUI.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.purple_500));
        }
    }
}