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
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jambuzzers.whatsthatjam.model.FirebaseQueries;
import com.jambuzzers.whatsthatjam.model.SocketPlayer;
import com.jambuzzers.whatsthatjam.model.SpotifySocketPlayer;
import com.jambuzzers.whatsthatjam.model.User;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Spotify;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SocketPlayer.SocketPlayerListener{

    private BottomNavigationView navigation;
    private ViewPager viewPager;

    MyPagerAdapter adapter;

    //define fragments
    SearchableFragment searchFragment;
    GameFragment gameFragment;
    ProfileFragment profileFragment;
    MenuItem prevMenuItem;
    final Fragment loginFrag = new LoginFragment();

    SocketPlayer player;

    //current user
    User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment, loginFrag).commit();



        //Initializing viewPager
        viewPager = findViewById(R.id.view_pager);

        //Initializing the bottomNavigationView
        navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.search:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.play:
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.profile:
                                viewPager.setCurrentItem(2);
                                break;

                            default:

                        }
                        return false;
                    }
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: "+position);
                navigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navigation.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setupViewPager(viewPager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == LoginFragment.REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            player = new SpotifySocketPlayer(response, this, this);
            gameFragment.setListener(player);
            ///fired off request for current user
            FirebaseQueries.queryAllUsernames(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d("tag", "task was successful");
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.getData().get("token").equals(player.getToken())) {
                                mCurrentUser = new User(document);
                                // update fragment for current user
                                profileFragment.setUser(mCurrentUser);
                            }
                        }
                    } else {
                        Log.d("tag", "Error getting document: ", task.getException());
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onPlayerResume() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Resumed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onPlay() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Played", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onPlayerPause() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Pause", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onInvite(final int gameId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Invited ", Toast.LENGTH_SHORT).show();
                player.acceptGame(gameId);
            }
        });

    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();

        private MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }
        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        private void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }
        private void removeFragment(Fragment fragment) {
            mFragmentList.remove(fragment);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        if(gameFragment == null) gameFragment = new GameFragment();
        if(searchFragment == null) searchFragment = new SearchableFragment();
        if(profileFragment == null ) {
            profileFragment =  ProfileFragment.newProfFrag(mCurrentUser);
        }
        adapter.addFragment(searchFragment);
        adapter.addFragment(gameFragment);
        adapter.addFragment(profileFragment);
        viewPager.setAdapter(adapter);
    }
}