// SimpleApp
// Created by Spotify on 02/08/17.
// Copyright (c) 2017 Spotify. All rights reserved.
package com.jambuzzers.whatsthatjam;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import android.view.MenuItem;
import android.widget.Toast;


import com.jambuzzers.whatsthatjam.model.SocketPlayer;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Spotify;


public class MainActivity extends AppCompatActivity{

    final FragmentManager fragmentManager = getSupportFragmentManager();
    final Fragment loginFrag = new LoginFragment();


    final Fragment gFrag = new GameFragment();
    final Fragment pFrag = new ProfileFragment();
    final Fragment sFrag = new SearchableFragment();



    GameFragment gameFragment;
    SocketPlayer player;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.search:
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.fragment , sFrag).commit();
                            return true;
                        case R.id.play:
                            FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();
                            fragmentTransaction2.replace(R.id.fragment, gFrag).commit();
                            return true;
                        case R.id.profile:
                            FragmentTransaction fragmentTransaction3 = fragmentManager.beginTransaction();
                            fragmentTransaction3.replace(R.id.fragment, pFrag).commit();
                            return true;
                    }
                    return false;
                }
            };



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment, loginFrag).commit();


        ViewPager pager = findViewById(R.id.view_pager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
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
                            Toast.makeText(MainActivity.this, "Resumed",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                @Override
                public void onPlay() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Played",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                @Override
                public void onPause() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Pause",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                @Override
                public void onInvite(int gameId) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Invited ",Toast.LENGTH_SHORT).show();
                        }
                    });
                    player.acceptGame(gameId);
                }
            }) ;
            gameFragment.setListener(player);
        }

    }
    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        private MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

                //TODO: When the activity gets recreated (e.g. on orientation change) so do the ViewPager's fragments.
                //TODO: Recycler view check... and scrolling
               // case 0: return LoginFragment.newInstance("loginFragment, Instance 1");
                case 0: return SearchableFragment.newInstance("browseFragment, instance1");
                case 1:
                    if (gameFragment == null) {
                        gameFragment = GameFragment.newInstance("gameFragment, Instance 1");
                    }
                    return gameFragment;
                case 2: return ProfileFragment.newInstance("profileFragment, Instance 1");
                default: return GameFragment.newInstance("gameFragment, Instance 2");
            }
        }
        @Override
        public int getCount() {
            return 3;
        }

    }



}
