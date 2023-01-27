package cn.edu.nottingham.sid20028336.cw2.adapters;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import cn.edu.nottingham.sid20028336.cw2.activities.SelectMusicsActivity;
import cn.edu.nottingham.sid20028336.cw2.music.Music;
import cn.edu.nottingham.sid20028336.cw2.R;

public class SelectMusicsAdapter extends BaseAdapter {
    SelectMusicsActivity selectMusicsActivity;
    ArrayList<Music> localMusicList;

    public SelectMusicsAdapter(SelectMusicsActivity selectMusicsActivity, ArrayList<Music> localMusicList) {
        super();

        this.selectMusicsActivity = selectMusicsActivity;
        this.localMusicList = localMusicList;
    }

    @Override
    public int getCount() {
        return localMusicList.size();
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
        @SuppressLint({"ViewHolder", "InflateParams"}) View view = selectMusicsActivity.getLayoutInflater().inflate(R.layout.music_card_layout, null);

        // set attributes of the list item to the music information
        Music currentMusic = localMusicList.get(position);

        TextView musicNameTextView = view.findViewById(R.id.card_musicNameTextView);
        TextView artistTextView = view.findViewById(R.id.card_artistTextView);

        musicNameTextView.setText(currentMusic.getMusicName());
        artistTextView.setText(currentMusic.getArtist());
        return view;
    }
}
