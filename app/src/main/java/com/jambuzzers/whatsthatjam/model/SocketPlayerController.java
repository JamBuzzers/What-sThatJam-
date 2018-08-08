package com.jambuzzers.whatsthatjam.model;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.jambuzzers.whatsthatjam.MainActivity;
import com.jambuzzers.whatsthatjam.R;

public class SocketPlayerController implements SocketPlayer.SocketPlayerListener {

    private MainActivity activity;

    public SocketPlayerController(MainActivity act){
        activity = act;
    }
    @Override
    public void onPlayerResume() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, "Resumed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onPlay() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, "Played", Toast.LENGTH_SHORT).show();
                activity.startGame();
            }
        });
    }

    @Override
    public void onPlayerPause() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, "Pause", Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public void onReceiveId(final String id){
        Log.d("ID IS,",id);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, id, Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onInvite(final int gameId, final String creator) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, "Invited ", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.MyAlertDialogTheme);
                // Add the buttons
                builder.setMessage("You've been invited to play a game by "+creator);
                builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        Toast.makeText(activity, "You Accepted game invite", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onScore(final int score){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, ""+score, Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onFinalScore(final int score, final boolean won){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, "Final Score: "+score, Toast.LENGTH_SHORT).show();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(won)
                    Toast.makeText(activity, "You won", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(activity, "You lost", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
