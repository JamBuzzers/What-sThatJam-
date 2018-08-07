package com.jambuzzers.whatsthatjam.model;

import android.content.Context;
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

import java.net.URISyntaxException;
import java.util.concurrent.locks.ReentrantLock;

public class SpotifySocketPlayer implements SocketPlayer {

    private static String SERVER_URL = "https://murmuring-dusk-18271.herokuapp.com/";
    private SpotifyPlayer spotifyplayer;
    private Socket mSocket;
    private SocketPlayerListener listener;
    private String token;
    private String LOCK = "LOCK=";
    ReentrantLock lock = new ReentrantLock();

    public SpotifySocketPlayer(AuthenticationResponse response, Context context, SocketPlayerListener l){
        lock.lock();
        Log.d(LOCK,"lock 1");
        if (response.getType() == AuthenticationResponse.Type.TOKEN) {
            Log.d("TOKEN",response.getAccessToken());
            token = response.getAccessToken();
            Config playerConfig = new Config(context, response.getAccessToken(), MainActivity.CLIENT_ID);
            Spotify.getPlayer(playerConfig, context, new SpotifyPlayer.InitializationObserver() {
                @Override
                public void onInitialized(SpotifyPlayer spotifyPlayer) {
                    spotifyplayer = spotifyPlayer;
                    lock.unlock();
                    Log.d(LOCK,"unlock 1");

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
        lock.lock();
        Log.d(LOCK,"lock 2");

        login();
        lock.unlock();
        Log.d(LOCK,"unlock 2");

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
                String id  = (String) args[0];
                listener.onReceiveId(id);
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
    }
}



