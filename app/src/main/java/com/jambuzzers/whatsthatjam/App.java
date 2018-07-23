package com.jambuzzers.whatsthatjam;

import android.app.Application;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import java.net.URISyntaxException;

public class App extends Application {
    private Socket mSocket;
    private static final String URL = "http://chat.socket.io";
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            mSocket = IO.socket("https://chat-tester-callum.herokuapp.com/");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    public Socket getmSocket(){
        return mSocket;
    }
}