package com.jambuzzers.whatsthatjam.model;

import android.content.Context;
import android.util.Pair;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.jambuzzers.whatsthatjam.MainActivity;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class SpotifySocketPlayer implements SocketPlayer {

    private static String SERVER_URL = "https://murmuring-dusk-18271.herokuapp.com/";
    private SpotifyPlayer spotifyplayer;
    private Socket mSocket;
    private SocketPlayerListener listener;
    private String token;

    public SpotifySocketPlayer(AuthenticationResponse response, Context context, SocketPlayerListener l){
        if (response.getType() == AuthenticationResponse.Type.TOKEN) {
            Log.d("TOKEN",response.getAccessToken());
            token = response.getAccessToken();
            Config playerConfig = new Config(context, response.getAccessToken(), MainActivity.CLIENT_ID);
            Spotify.getPlayer(playerConfig, context, new SpotifyPlayer.InitializationObserver() {
                @Override
                public void onInitialized(SpotifyPlayer spotifyPlayer) {
                    spotifyplayer = spotifyPlayer;
                }
                @Override
                public void onError(Throwable throwable) {
                    Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                }
            });
        }
        else{
            Log.d("ERROR","LOGIN FAILED");
        }
        listener = l;
        try {
            mSocket = IO.socket(SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        mSocket.connect();
        startSocketListening();
        login();
    }

    public void pause(){
        mSocket.emit("pause");
    }
    public void answer(String answer){
        mSocket.emit("submit",answer);
    }
    public void initiateGame(JSONArray invitees){
        mSocket.emit("create",invitees);
    }
    public void acceptGame(int gameId){
        mSocket.emit("accept", gameId);
    }
    private void login(){
        mSocket.emit("login",token);
    }
    private void startSocketListening(){
        mSocket.on("play", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String title = (String) args[0];
                spotifyplayer.playUri(null, "spotify:track:"+title, 0, 0);
                listener.onPlay();
            }
        });
        mSocket.on("pause", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                spotifyplayer.pause(null);
                listener.onPlayerPause();
            }
        });
        mSocket.on("invite", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                int gameId = (int) args[0];
                String creator = (String) args[1];
                listener.onInvite(gameId,creator);
            }
        });
        mSocket.on("resume", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                spotifyplayer.resume(null);
                listener.onPlayerResume();
            }
        });
        mSocket.on("queue", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String title  = (String) args[0];
                spotifyplayer.queue(null,"spotify:track:"+title );
            }
        });
        mSocket.on("next", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                spotifyplayer.skipToNext(null);
            }
        });
        mSocket.on("message", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String string  = (String) args[0];
                Log.d("LOGGING_SERVER", string);
            }
        });
        mSocket.on("id", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String name  = (String) args[1];

                String id = (String ) args[0];
                if(name == null)
                    name =id;
                listener.onReceiveId(name,id);
            }
        });
        mSocket.on("result", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String result = (String) args[0];
                listener.onResult(result);
            }
        });
        mSocket.on("score", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                int score = (int) args[0];
                listener.onScore(score);
            }
        });
        mSocket.on("final score", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                int score = (int) args[0];
                boolean won;
                if((int) args[1]==1)
                    won= true;
                else
                    won = false;
                listener.onFinalScore(score,won);
            }
        });
        mSocket.on("timer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                int time = (int) args[0];
                listener.onTimer(time);
            }
        });
        mSocket.on("next round", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONArray names = (JSONArray) args[0];
                JSONArray scores = (JSONArray) args[1];
                ArrayList<Pair<String,String>> pairs = new ArrayList<>();
                for(int i = 0; i <names.length();i++) {
                    try {
                        pairs.add(new Pair<String,String>(names.getString(i),scores.getString(i)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                String name = (String) args[2];
                String image = (String) args[3];
                Boolean timeout = (Boolean) args[4];
                listener.onNextRound(pairs,name,image,timeout);

            }
        });

    }
}