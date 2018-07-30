package com.jambuzzers.whatsthatjam;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jambuzzers.whatsthatjam.model.SocketPlayer;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.json.JSONArray;

import butterknife.BindView;
import butterknife.ButterKnife;



public class DemoActivity extends AppCompatActivity {


    final FragmentManager fragmentManager = getSupportFragmentManager();
    final Fragment loginFrag = new LoginFragment();
    SocketPlayer player;

    @BindView(R.id.btInitiate)
    Button btInitiate;
    @BindView(R.id.btPause) Button btPause;
    @BindView(R.id.btAnswer) Button btAnswer;
    @BindView(R.id.etAnswer)
    EditText etAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        ButterKnife.bind(this);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment, loginFrag).commit();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == LoginFragment.REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            player = new SocketPlayer(response, this, new SocketPlayer.SocketPlayerListener() {
                @Override
                public void onResume() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DemoActivity.this, "Resumed",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                @Override
                public void onPlay() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DemoActivity.this, "Played",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                @Override
                public void onPause() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DemoActivity.this, "Pause",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                @Override
                public void onInvite(int gameId) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DemoActivity.this, "Invited ",Toast.LENGTH_SHORT).show();
                        }
                    });
                    player.acceptGame(gameId);
                }
            }) ;
            btInitiate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(DemoActivity.this, "Making",Toast.LENGTH_SHORT).show();
                    JSONArray array = new JSONArray();
                    array.put("hfs42yo2hhyypxlcff8ddtrsu");
                    player.initiateGame(array);
                }
            });
            btAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(DemoActivity.this, "Answering",Toast.LENGTH_SHORT).show();
                    String answer = etAnswer.getText().toString();
                    player.answer(answer);
                }
            });
            btPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(DemoActivity.this, "Pausing",Toast.LENGTH_SHORT).show();

                    player.pause();
                }
            });
            }
        }
}
