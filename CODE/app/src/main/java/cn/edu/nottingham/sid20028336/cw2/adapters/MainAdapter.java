package cn.edu.nottingham.sid20028336.cw2.adapters;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import cn.edu.nottingham.sid20028336.cw2.R;
import cn.edu.nottingham.sid20028336.cw2.activities.MainActivity;
import cn.edu.nottingham.sid20028336.cw2.database.DBHelper;

// music list adapter for list view
public class MainAdapter extends BaseAdapter {
    private final MainActivity mainActivity;
    DBHelper DBHelper;
    String tableName;

    public MainAdapter(MainActivity mainActivity, String tableName) {
        this.mainActivity = mainActivity;
        this.DBHelper = new DBHelper(mainActivity);
        this.tableName = tableName;
    }

    @Override
    public int getCount() {
        return DBHelper.countTableRowNumber(tableName);
    }

    @Override
    public Object getItem(int position) {
        return mainActivity.localMusicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ResourceType")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint({"ViewHolder", "InflateParams"}) View view = mainActivity.getLayoutInflater().inflate(R.layout.music_card_layout, null);

        // set attributes of the list item according to the DB
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
