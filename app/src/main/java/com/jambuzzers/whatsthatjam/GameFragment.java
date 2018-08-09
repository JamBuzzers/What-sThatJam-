package com.jambuzzers.whatsthatjam;

import android.app.Activity;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
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

    private int round=1;

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
                //etSong.requestFocus();
                etSong.setClickable(true);
                etSong.setFocusableInTouchMode(true);
                etSong.setFocusable(true);
                etSong.requestFocus();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        });
        etSong.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){
                    String songGuess = textView.getText().toString();
                    mSocketPlayer.answer(songGuess);
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etSong.getWindowToken(), 0);
                }
                return false;
            }
        });
        etSong.setFocusable(false);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GlideApp.with(getContext())
                .load("https://i.scdn.co/image/be0a1502eef1b896e63fd10d9bb6dcf8aa8b007b")
                .into(ivAlbum);

    }
    //Public Methods
    public void setTime(int time){
        if(null ==tvTime)
            return;
        tvTime.setText(Integer.toString(time));
    }
    public void setStanding(){

    }
    public void setScore(int score){
        if(tvScore == null)
            return;
        etSong.setFocusable(false);
        tvScore.setText(Integer.toString(score));
        round++;
        tvRound.setText(Integer.toString(round));
    }
    public void setListener(SocketPlayer listener) {
        mSocketPlayer = listener;
    }
    public void enableText(){
        etSong.setFocusable(true);
    }
}