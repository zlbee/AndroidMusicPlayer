package cn.edu.nottingham.sid20028336.cw2.adapters;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import cn.edu.nottingham.sid20028336.cw2.database.DBContract;
import cn.edu.nottingham.sid20028336.cw2.database.DBHelper;
import cn.edu.nottingham.sid20028336.cw2.activities.PlaylistsActivity;
import cn.edu.nottingham.sid20028336.cw2.R;

public class PlaylistsAdapter extends BaseAdapter {
    private final PlaylistsActivity playlistsActivity;
    DBHelper DBHelper;

    public PlaylistsAdapter(PlaylistsActivity playlistsActivity) {
        this.playlistsActivity = playlistsActivity;
        this.DBHelper = new DBHelper(playlistsActivity);
    }

    @Override
    public int getCount() {
        return DBHelper.countTableNumber();
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
        @SuppressLint({"ViewHolder", "InflateParams"}) View view = playlistsActivity.getLayoutInflater().inflate(R.layout.playlist_card_layout, null);

        TextView cardPlaylistNameTextViewUI = view.findViewById(R.id.card_playlistNameTextView);
        Cursor c = DBHelper.queryTableByIndex(position);
        if (c.moveToNext()) {
            String tableName = c.getString(0).replaceAll(DBContract.TABLE_PREFIX, "");
            cardPlaylistNameTextViewUI.setText(tableName);
        }

        return view;
    }
}
