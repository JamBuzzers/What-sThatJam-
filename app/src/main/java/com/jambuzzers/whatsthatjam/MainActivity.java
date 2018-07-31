// SimpleApp
// Created by Spotify on 02/08/17.
// Copyright (c) 2017 Spotify. All rights reserved.
package com.jambuzzers.whatsthatjam;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;


public class MainActivity extends AppCompatActivity{

    final FragmentManager fragmentManager = getSupportFragmentManager();
    final Fragment loginFrag = new LoginFragment();
    private Player mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                case 0: return new SearchableFragment();
                case 1: return GameFragment.newInstance("gameFragment, Instance 1");
                case 2: return ProfileFragment.newInstance("profileFragment, Instance 1");
                default: return GameFragment.newInstance("gameFragment, Instance 2");
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
// function for adding title to the page view
//        @Override
//        public CharSequence(int position){
//            switch (position){
//                case 0:
//
//            }
//        }
    }

}
