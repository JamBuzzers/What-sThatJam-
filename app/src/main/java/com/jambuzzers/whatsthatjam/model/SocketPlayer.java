package com.jambuzzers.whatsthatjam.model;

import android.content.Context;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.jambuzzers.whatsthatjam.LoginFragment;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.net.URISyntaxException;

public abstract class SocketPlayer {

    private static String SERVER_URL = "https://murmuring-dusk-18271.herokuapp.com/";
    private SpotifyPlayer spotifyplayer;
    private Socket mSocket;
    private SocketPlayerListener listener;

    public interface SocketPlayerListener {
         void onResume();
         void onPlay();
         void onPause();
    }
    public SocketPlayer(Player player, AuthenticationResponse response, Context context, SocketPlayerListener l){
        if (response.getType() == AuthenticationResponse.Type.TOKEN) {
            Log.d("TOKEN",response.getAccessToken());
            Config playerConfig = new Config(context, response.getAccessToken(), LoginFragment.CLIENT_ID);
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
        listener = l;
        try {
             mSocket = IO.socket(SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        mSocket.connect();
        mSocket.emit("token",response.getAccessToken());
    }
    public void pause(){
        mSocket.emit("pause");
    }
    public void answer(String answer){
        mSocket.emit("answer",answer);
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
                listener.onPause();
            }
        });
        mSocket.on("resume", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                spotifyplayer.resume(null);
                listener.onResume();
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
    }
}



