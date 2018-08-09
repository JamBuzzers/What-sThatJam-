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
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.jambuzzers.whatsthatjam.model.FirebaseQueries;
import com.jambuzzers.whatsthatjam.model.SocketPlayer;
import com.jambuzzers.whatsthatjam.model.SpotifySocketPlayer;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Spotify;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements GameLandingFragment.GameLandingListener,CreateGameFragment.CreateGameListener {

    private BottomNavigationView navigation;
    private ViewPager viewPager;
    cAdapter adapter;
    //define fragments
    SearchableFragment searchFragment;
    public GameFragment gameFragment;
    ProfileFragment profileFragment;
    GameLandingFragment gameLanding;
    CreateGameFragment createGame;
    MenuItem prevMenuItem;

    SocketPlayer player;
    String id =null;

    public static final String CLIENT_ID = "cb1084779ae74d51becf812efa34c4c8";
    private static final String REDIRECT_URI = "https://www.google.com/";
    public static final int REQUEST_CODE = 1337;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseQueries.removeError();


        if (gameFragment == null) gameFragment = GameFragment.newInstance(this);
        if (searchFragment == null) searchFragment = new SearchableFragment();
        if (profileFragment == null) profileFragment = new ProfileFragment();
        adapter = new cAdapter(getSupportFragmentManager());
        createGame = new CreateGameFragment();
        gameLanding = new GameLandingFragment();

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

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
                } else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: " + position);
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
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            player = new SpotifySocketPlayer(response, this, gameFragment);
            gameFragment.setListener(player);
        }

    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter.addFragment(searchFragment);
        adapter.addFragment(gameLanding);
        viewPager.setAdapter(adapter);
    }
    //Public Methods
    public void startGame() {
            adapter.replaceFragment(gameFragment, 1);
    }
    public void acceptGame(int gameId){
        player.acceptGame(gameId);
    }
    public void setUpProfile(String id){
        this.id = id;
        ProfileFragment pf = ProfileFragment.newInstance(id);
        //adapter.replaceFragment(pf,2);
        adapter.addFragment(pf);
        adapter.notifyDataSetChanged();
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
    }
    //Interfaces
    @Override
    public void onRandom() {
        JSONArray inviteMe = new JSONArray();
        while(id == null){
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        inviteMe.put(id);
        player.initiateGame(inviteMe);
    }
    @Override
    public void onCreate() {
        navigation.setVisibility(View.GONE);
        adapter.replaceFragment(createGame, 1);
    }
    @Override
    public void createGame(JSONArray invitees) {
        player.initiateGame(invitees);
    }
    public class cAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();


        public cAdapter(FragmentManager fm){
            super(fm);
        }
        public Fragment getItem(int i){
            return mFragmentList.get(i);
        }
        @Override
        public int getCount() {
            return mFragmentList.size();
        }
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
        private void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }
        public void replaceFragment(Fragment fragment, int index) {
            mFragmentList.remove(index);
            mFragmentList.add(index, fragment);
            notifyDataSetChanged();
        }
    }
}