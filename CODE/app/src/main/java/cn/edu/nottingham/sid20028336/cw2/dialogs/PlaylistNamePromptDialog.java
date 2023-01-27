package cn.edu.nottingham.sid20028336.cw2.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import cn.edu.nottingham.sid20028336.cw2.R;

// reference: https://www.youtube.com/watch?v=ARezg1D9Zd0
public class PlaylistNamePromptDialog extends AppCompatDialogFragment {
        private EditText namePromptUI;
        private DialogListener listener;

        @Override
        public void onAttach(@NonNull Context context) {
            super.onAttach(context);

            try {
                listener = (DialogListener) context;
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
            View view = inflater.inflate(R.layout.dialog_playlist_name_prompt, null);

            // bind UI variables
            namePromptUI = view.findViewById(R.id.namePrompt);

            // configure dialog functions
            builder.setView(view)
                    .setTitle("Input your playlist name:")
                    .setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String playlistName = namePromptUI.getText().toString();
                            // create playlist
                            listener.createPlaylist(playlistName);
                        }
                    });
            return builder.create();
        }

        // listen to events and execute commands
        public interface DialogListener {
            void createPlaylist(String playlistName);
        }

}
