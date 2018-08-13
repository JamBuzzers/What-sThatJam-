package com.jambuzzers.whatsthatjam;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.jambuzzers.whatsthatjam.model.SocketPlayer;
import com.jambuzzers.whatsthatjam.model.User;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Handles all the game view. Checks the edit text and gets a track from the array 20 to play
 * Should handle when the song plays and stops the song
 */
public class GameFragment extends Fragment implements SocketPlayer.SocketPlayerListener{

    private SocketPlayer mSocketPlayer; //check for null pointers


    @BindView(R.id.etSongTitle) EditText etSong;
    @BindView(R.id.tvRound) TextView tvRound;
    @BindView(R.id.tvTime) TextView tvTime;
    @BindView(R.id.tvScore) TextView tvScore;
    @BindView(R.id.ivStop) ImageView ivStop;
    @BindView(R.id.ivAlbum) ImageView ivAlbum;
    @BindView(R.id.tvInfo) TextView tvInfo;

//    @BindView(R.id.hscroll) LinearLayout nHscroll;

//    ArrayList<User> uPlaying;

    private int round=1;
    boolean visible = false;
    MainActivity activity;
    boolean buttonEnabled = true;
//    User muser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_game, container, false);
        ButterKnife.bind(this,view);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        ivStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!buttonEnabled)
                    return;
                mSocketPlayer.pause();
                enableText();
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
        disableText();
//        View child = getChildView(muser);
//        nHscroll.addView(child);
//        for(User u : uPlaying) {
//            View child = getChildView(u);
//            nHscroll.addView(child);
//        }

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

//    private View getChildView(User u) {
//        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_playing, null);
//        TextView textView = view.findViewById(R.id.tvName);
//        ImageView imageView = view.findViewById(R.id.ivSearchProfPic);
//        textView.setText(u.username);
//
//        GlideApp.with(getContext())
//                .load(u.url)
//                .centerCrop()
//                .transform(new RoundedCorners(100))
//                .circleCrop()
//                .into(imageView);
//
//        return view;
//    }

    //Listeners
    @Override
    public void onPlayerResume() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                enableButton();
            }
        });

    }
    @Override
    public void onPlay() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!visible){
                    activity.startGame();
                    visible = true;
                }
                disableText();
                enableButton();
                hide();
            }
        });
    }

    @Override
    public void onPlayerPause() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                disableButton();
            }
        });

    }
    @Override
    public void onReceiveId(final String name, final String id){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity,"Welcome back " + name, Toast.LENGTH_SHORT).show();
                activity.setUpProfile(id,name);
            }
        });
    }
    @Override
    public void onInvite(final int gameId, final String creator) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.MyAlertDialogTheme);
                builder.setMessage("You've been invited to play a game by "+creator);
                builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity.acceptGame(gameId);
                        dialog.cancel();
                    }
                });
                builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        Toast.makeText(activity, "You Declined game invite", Toast.LENGTH_SHORT).show();
                        dialog.cancel();

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }
    @Override
    public void onResult(final String result){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvInfo.setText(result);
            }
        });
    }
    @Override
    public void onScore(final int score){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(tvScore == null)
                    return;
                etSong.setFocusable(false);
                tvScore.setText(Integer.toString(score));
                round++;
                tvRound.setText(Integer.toString(round));
            }
        });
    }
    @Override
    public void onFinalScore(final int score, final boolean won){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });
    }
    @Override
    public void onTimer(int time){
        if(null ==tvTime)
            return;
        tvTime.setText(Integer.toString(time));
    }
    @Override
    public void onNextRound(ArrayList<Pair<String,String>> standing, final String title, final String image, final Boolean timeout){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(timeout)
                    tvInfo.setText("Timeout");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        reveal(title,image);
                    }
                }, 2000);
            }
        });
    }
    //Public Methods

    public void setListener(SocketPlayer listener) {
        mSocketPlayer = listener;
    }
    // UI
    public void reveal(String title, String url){
        tvInfo.setText("");
        GlideApp.with(getContext())
                .load(url)
                .into(ivAlbum);
        Animation aniSlide = AnimationUtils.loadAnimation(getContext(),R.anim.zoom_in);
        ivAlbum.startAnimation(aniSlide);
        etSong.setText(title);
    }
    public void hide(){
        etSong.setText("");
        GlideApp.with(getContext()).load("").into(ivAlbum);
    }
    public void disableButton(){
        buttonEnabled = false;
    }
    public void enableButton(){
        buttonEnabled = true;

    }
    public void disableText(){
        etSong.setFocusable(false);
    }
    public void enableText(){
        etSong.setClickable(true);
        etSong.setFocusableInTouchMode(true);
        etSong.setFocusable(true);
        etSong.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
    public static GameFragment newInstance(MainActivity act){
        GameFragment gf = new GameFragment();
        gf.activity = act;
        return gf;
    }
}