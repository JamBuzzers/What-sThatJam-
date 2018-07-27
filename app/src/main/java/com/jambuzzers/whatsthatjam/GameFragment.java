package com.jambuzzers.whatsthatjam;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Handles all the game view. Checks the edit text and gets a track from the array 20 to play
 * Should handle when the song plays and stops the song
 */
public class GameFragment extends Fragment {


    @BindView(R.id.guess_btn) Button stopBtn;
    @BindView(R.id.etGuess) EditText etSongGuess;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_game, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initGuessAccess();
                //TODO: Stop music after designated time...call function that handles that...
            }
        });
        etSongGuess.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if(i == EditorInfo.IME_ACTION_DONE){}
                //TODO send to response to server

                return handled;
            }
        });

    }

    public void initGuessAccess() {
        stopBtn.setEnabled(false);
        etSongGuess.setEnabled(true);
    }

    public static GameFragment newInstance(String text) {
        GameFragment frag = new GameFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);
        frag.setArguments(b);

        return frag;
    }
}