package com.jambuzzers.whatsthatjam.model;

import android.util.Pair;

import org.json.JSONArray;

import java.util.ArrayList;

public interface SocketPlayer  {
    void pause();
    void answer(String answer);
    void initiateGame(JSONArray invitees);
    void acceptGame(int gameId);
    //TODO: Move onDestroy
    interface SocketPlayerListener {
        void onPlayerResume();
        void onPlay();
        void onPlayerPause();
        void onInvite(int gameId, String creator);
        void onReceiveId(String id);
        void onResult(String result);
        void onScore(int score);
        void onFinalScore(int score, boolean won);
        void onTimer(int time);
        void onNextRound(ArrayList<Pair<String, String>> standings);
    }
}