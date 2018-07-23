package com.jambuzzers.whatsthatjam;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {


    private Socket mSocket;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView tvBlank = findViewById(R.id.tvBlank);
        Button btClick = findViewById(R.id.btClick);
        App app = (App) getApplication();

        mSocket = app.getmSocket();
        mSocket.connect();
        if (mSocket.connected()){
            Toast.makeText(MainActivity.this, "Connected!!",Toast.LENGTH_SHORT).show();
            Log.d("HERE","here");
        }
        btClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("time",System.nanoTime());
                    mSocket.emit("chat message",obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        mSocket.on("chat message", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final long time = System.nanoTime();
                final JSONObject data = (JSONObject)args[0];
                try {
                    Log.d("HERE",Long.toString((time-data.getLong("time"))/(long)1e6));
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                tvBlank.setText(Long.toString((time-data.getLong("time"))/(long)1e6));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Toast.makeText(MainActivity.this, data.toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
    }
}
