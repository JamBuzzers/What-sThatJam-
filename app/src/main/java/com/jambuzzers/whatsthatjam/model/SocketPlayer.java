package com.jambuzzers.whatsthatjam.model;

import org.json.JSONArray;

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
        void onInvite(int gameId);
        void onReceiveId(String id);
    }
}