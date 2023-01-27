package cn.edu.nottingham.sid20028336.cw2.adapters;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import cn.edu.nottingham.sid20028336.cw2.database.DBHelper;
import cn.edu.nottingham.sid20028336.cw2.music.Music;
import cn.edu.nottingham.sid20028336.cw2.R;
import cn.edu.nottingham.sid20028336.cw2.dialogs.PlaylistDialog;

public class PlaylistDialogAdapter extends BaseAdapter {
    PlaylistDialog playlistDialog;
    DBHelper DBHelper;
    String tableName;

    public PlaylistDialogAdapter(PlaylistDialog playlistDialog, String tableName) {
        this.playlistDialog = playlistDialog;
        this.DBHelper = new DBHelper(playlistDialog.getContext());
        this.tableName = tableName;
    }

    @Override
    public int getCount() {
        return DBHelper.countTableRowNumber(tableName);
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint({"ViewHolder", "InflateParams"}) View view = playlistDialog.getLayoutInflater().inflate(R.layout.music_card_layout, null);

        // query the table and update text views
        Cursor c = DBHelper.queryItemFromTableByIndex(tableName, position);
        TextView cardMusicNameTextViewUI = view.findViewById(R.id.card_musicNameTextView);
        TextView cardArtistTextViewUI = view.findViewById(R.id.card_artistTextView);
        if (c.moveToNext()) {
            String musicName = c.getString(1);
            String artistName = c.getString(2);
            cardMusicNameTextViewUI.setText(musicName);
            cardArtistTextViewUI.setText(artistName);
        }
        return view;
    }
}
