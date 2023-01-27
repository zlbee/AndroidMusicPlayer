package cn.edu.nottingham.sid20028336.cw2.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cn.edu.nottingham.sid20028336.cw2.database.DBHelper;
import cn.edu.nottingham.sid20028336.cw2.R;
import cn.edu.nottingham.sid20028336.cw2.adapters.PlaylistsAdapter;
import cn.edu.nottingham.sid20028336.cw2.dialogs.PlaylistNamePromptDialog;

public class PlaylistsActivity extends AppCompatActivity implements PlaylistNamePromptDialog.DialogListener {
    private static final int DELETE_PLAYLIST_REQUEST = 1;
    ListView playlistsListViewUI;

    PlaylistsAdapter adapter;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // delete: update the DB and update the list view
        if (requestCode == DELETE_PLAYLIST_REQUEST) {
            if (resultCode == RESULT_OK) {
                // acquire playlist positions
                Bundle bundle = data.getExtras();
                ArrayList<Integer> playlistPositions = (ArrayList<Integer>) bundle.getSerializable(String.valueOf(R.string.playlist_positions_key));

                // delete the tables with the acquired playlist positions
                DBHelper DBHelper = new DBHelper(this);
                for (int position: playlistPositions) {
                    TextView textView = playlistsListViewUI.getChildAt(position).findViewById(R.id.card_playlistNameTextView);
                    String tableName = (String) textView.getText();
                    DBHelper.deleteTableByTableName(tableName);
                }

                // update the list view
                playlistsListViewUI.setAdapter(adapter);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlists);

        // initialize
        init();

        // set adapter for the list view
        playlistsListViewUI.setAdapter(adapter);

        // listen to list view clicks
        listenListViewItemClick(playlistsListViewUI);
    }

    private void init() {
        // initialize UI components
        playlistsListViewUI = findViewById(R.id.playlistListView);

        // initialize adapter
        adapter = new PlaylistsAdapter(this);
    }

    // view the clicked playlist
    private void listenListViewItemClick(ListView playlistsListViewUI) {
        playlistsListViewUI.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView cardPlaylistNameTextViewUI = view.findViewById(R.id.card_playlistNameTextView);
                String playlistName = cardPlaylistNameTextViewUI.getText().toString();

                Intent intent = new Intent(PlaylistsActivity.this, SinglePlaylistActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(String.valueOf(R.string.table_name_key), playlistName);
                intent.putExtras(bundle);

                // send broadcast to main activity to configure current playlist
                intent.setAction("@string/update_current_playlist_filter");
                sendBroadcast(intent);

                // send intent with playlistName (tableName) to SinglePlaylistActivity
                startActivity(intent);
            }
        });
    }

    // open playlist name dialog
    public void openDialog(View view) {
        PlaylistNamePromptDialog dialog = new PlaylistNamePromptDialog();
        dialog.show(getSupportFragmentManager(), "playlist name prompt dialog");
    }

    // create new playlist
    @Override
    public void createPlaylist(String playlistName) {
        DBHelper DBHelper = new DBHelper(this);
        boolean response = DBHelper.createNewTable(playlistName);
        if (!response) {
            Toast.makeText(this, "Playlist with the same name already exist.", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Successfully created a playlist.", Toast.LENGTH_SHORT).show();
        }

        // update UI
        playlistsListViewUI.setAdapter(adapter);
    }

    // delete one or more playlists
    public void deletePlaylists(View view) {
        Intent intent = new Intent(this, SelectPlaylistsActivity.class);
        intent.setAction("@string/delete_playlist_filter");
        startActivityForResult(intent, DELETE_PLAYLIST_REQUEST);
    }
}