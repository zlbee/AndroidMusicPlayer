package cn.edu.nottingham.sid20028336.cw2.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.ArrayList;

import cn.edu.nottingham.sid20028336.cw2.music.Music;
import cn.edu.nottingham.sid20028336.cw2.R;
import cn.edu.nottingham.sid20028336.cw2.adapters.PlaylistDialogAdapter;

public class PlaylistDialog extends AppCompatDialogFragment {
    ListView playlistListViewUI;

    private DialogListener listener;

    private PlaylistDialogAdapter adapter;

    private String playlistName;

    public PlaylistDialog(String playlistName) {
        this.playlistName = playlistName;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (PlaylistDialog.DialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement dialog listener.");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // initialize dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_playlist, null);

        adapter = new PlaylistDialogAdapter(this, playlistName);

        // bind UI variables
        playlistListViewUI = view.findViewById(R.id.playlistListView);

        // set adapter to the list view
        playlistListViewUI.setAdapter(adapter);

        // configure dialog functions
        builder.setView(view)
                .setTitle("Current Playlist")
                .setPositiveButton("next music", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.indexToNextMusic();
                    }
                })
                .setNegativeButton("previous music", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.indexToPrevMusic();
                    }
        });
        return builder.create();
    }

    public interface DialogListener {
        public void indexToNextMusic();
        public void indexToPrevMusic();
    }
}
