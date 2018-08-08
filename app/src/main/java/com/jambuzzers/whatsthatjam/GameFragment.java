package com.jambuzzers.whatsthatjam;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jambuzzers.whatsthatjam.model.SocketPlayer;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Handles all the game view. Checks the edit text and gets a track from the array 20 to play
 * Should handle when the song plays and stops the song
 */
public class GameFragment extends Fragment {

    private SocketPlayer mSocketPlayer; //check for null pointers


    @BindView(R.id.etSongTitle) EditText etSong;
    @BindView(R.id.tvRound) TextView tvRound;
    @BindView(R.id.tvTime) TextView tvTime;
    @BindView(R.id.tvScore) TextView tvScore;
    @BindView(R.id.ivStop) ImageView ivStop;
    @BindView(R.id.ivAlbum) ImageView ivAlbum;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_game, container, false);
        ButterKnife.bind(this,view);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        ivStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSocketPlayer.pause();
                etSong.requestFocus();
            }
        });
        etSong.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){
                    String songGuess = textView.getText().toString();
                    mSocketPlayer.answer(songGuess);
                }
                return false;
            }
        });
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        GlideApp.with(getContext())
                .load("https://i.scdn.co/image/be0a1502eef1b896e63fd10d9bb6dcf8aa8b007b")
                .into(ivAlbum);
        /*etSong.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if(i == EditorInfo.IME_ACTION_DONE){
                    // socketPlayer.onPlayerPause();
                    //etSongGuess.setEnabled(false);
                    String songGuess = textView.getText().toString();
                    mSocketPlayer.answer(songGuess);
                    stopBtn.setEnabled(true);
                    etSongGuess.setEnabled(false);
                }
                return handled;
            }
        });
       */
    }
    public void setTime(int time){
        tvTime.setText(Integer.toString(time));
    }
    public void nextRound(int n){

    }
    public void setListener(SocketPlayer listener) {
        mSocketPlayer = listener;
    }
}