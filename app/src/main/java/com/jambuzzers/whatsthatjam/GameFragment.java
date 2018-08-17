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
import android.util.Log;
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

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jambuzzers.whatsthatjam.model.FirebaseQueries;
import com.jambuzzers.whatsthatjam.model.SocketPlayer;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class GameFragment extends Fragment implements SocketPlayer.SocketPlayerListener{

    private SocketPlayer mSocketPlayer; //check for null pointers
    private GameListener listener;

    Context context;

    public  interface GameListener{
        void onEnd(ArrayList<Pair<String,String>> standing);
    }


    @BindView(R.id.etSongTitle) EditText etSong;
    @BindView(R.id.tvRound) TextView tvRound;
    @BindView(R.id.tvTime) TextView tvTime;
    @BindView(R.id.tvScore) TextView tvScore;
//    @BindView(R.id.ivStop) ImageView ivStop;
    @BindView(R.id.ivAlbum) ImageView ivAlbum;
    @BindView(R.id.tvInfo) TextView tvInfo;

    @BindView(R.id.hscroll2) LinearLayout nHscroll;

    ArrayList<String> uPlaying;

    private int round=1;
    boolean visible = false;
    MainActivity activity;
    boolean buttonEnabled = true;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        View view =  inflater.inflate(R.layout.fragment_game, container, false);

        ButterKnife.bind(this,view);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();


        for(String u : uPlaying){
            View child = getChildView(u);
            nHscroll.addView(child);

        }

        ivAlbum.setOnClickListener(new View.OnClickListener() {
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

//        etSong.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                if(!buttonEnabled)
//                    return;
//                mSocketPlayer.pause();
//                enableText();
//
//
//            }
//        });

        disableText();


        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private View getChildView(final String mUsername) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_playing, null);
        TextView textView = view.findViewById(R.id.tvName);
        final ImageView imageView = view.findViewById(R.id.ivSearchProfPic);
        ((TextView)view.findViewById(R.id.tvScore1)).setText("0");
        textView.setText(mUsername);

        FirebaseQueries.queryUserName(mUsername, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("tag", "it was successful");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getData().get("name") != null && ((String) document.getData().get("name")).contains(mUsername)) {
                            String url = document.getData().get("profileurl").toString();
                            GlideApp.with(context)
                                    .load(url)
                                    .centerCrop()
                                    .transform(new RoundedCorners(100))
                                    .circleCrop()
                                    .into(imageView);
                        } else {
                            Log.d("tag", "Error getting document: ", task.getException());
                        }
                    }
                }
            }
        });
        return view;
    }


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
                while(getActivity() == null) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
                String s = result.substring(0, 1).toUpperCase() + result.substring(1);
                tvInfo.setText(s);
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
    public void onFinalScore(final int score, final boolean won,final ArrayList<Pair<String,String>> standing){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                visible = false;
                listener.onEnd(standing);
            }
        });
    }
    @Override
    public void onTimer(final int time){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(null ==tvTime)
                    return;
                tvTime.setText(Integer.toString(time));
            }
        });

    }
    @Override
    public void onNextRound(final ArrayList<Pair<String,String>> standing, final String title, final String image, final Boolean timeout){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(timeout)
                    tvInfo.setText("Time's out!");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        reveal(title,image);
                    }
                }, 2000);
                for(int i = 0 ; i <standing.size();i++)
                    ((TextView)nHscroll.getChildAt(i).findViewById(R.id.tvScore1)).setText(standing.get(i).second);
            }
        });
    }
    @Override
    public void onFirstRound(ArrayList<String> names){
       uPlaying = names;
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
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GameListener) {
            listener = (GameListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
}