package cn.edu.nottingham.sid20028336.cw2.adapters;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import cn.edu.nottingham.sid20028336.cw2.database.DBHelper;
import cn.edu.nottingham.sid20028336.cw2.activities.SinglePlaylistActivity;
import cn.edu.nottingham.sid20028336.cw2.R;

public class SinglePlaylistAdapter extends BaseAdapter {
    private final SinglePlaylistActivity singlePlaylistActivity;
    DBHelper DBHelper;
    String tableName;

    public SinglePlaylistAdapter(SinglePlaylistActivity singlePlaylistActivity, String tableName) {
        this.singlePlaylistActivity = singlePlaylistActivity;
        this.DBHelper = new DBHelper(singlePlaylistActivity);
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint({"ViewHolder", "InflateParams"}) View view = singlePlaylistActivity.getLayoutInflater().inflate(R.layout.music_card_layout, null);

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
