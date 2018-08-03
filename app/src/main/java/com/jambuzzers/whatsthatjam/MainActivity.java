// SimpleApp
// Created by Spotify on 02/08/17.
// Copyright (c) 2017 Spotify. All rights reserved.
package com.jambuzzers.whatsthatjam;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jambuzzers.whatsthatjam.model.FirebaseQueries;
import com.jambuzzers.whatsthatjam.model.SocketPlayer;
import com.jambuzzers.whatsthatjam.model.SpotifySocketPlayer;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Spotify;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SocketPlayer.SocketPlayerListener,GameLandingFragment.GameLandingListener,CreateGameFragment.CreateGameListener {

    private BottomNavigationView navigation;
    private ViewPager viewPager;
    cAdapter adapter;
    //define fragments
    SearchableFragment searchFragment;
    GameFragment gameFragment;
    ProfileFragment profileFragment;
    GameLandingFragment gameLanding;
    CreateGameFragment createGame;
    MenuItem prevMenuItem;
    Fragment loginFrag;

    SocketPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        loginFrag = new LoginFragment();
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
        FirebaseQueries.getActive("cal", new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot document : task.getResult().getDocuments())
                    Log.d("HERE", document.getId());
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
        }

    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new cAdapter(getSupportFragmentManager());
        if (gameFragment == null) gameFragment = new GameFragment();
        if (searchFragment == null) searchFragment = new SearchableFragment();
        if (profileFragment == null) profileFragment = new ProfileFragment();

        createGame = new CreateGameFragment();
        gameLanding = new GameLandingFragment();

        adapter.addFragment(searchFragment);
        adapter.addFragment(gameLanding);
        adapter.addFragment(profileFragment);
        viewPager.setAdapter(adapter);

    }

    //Interfaces
    public void onRandom() {
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
                adapter.replaceFragment(gameFragment,1);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogTheme);
                        // Add the buttons
                        builder.setMessage("You've been invited to play a game");
                        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK button
                                Toast.makeText(getApplicationContext(), "You Accepted game invite", Toast.LENGTH_SHORT).show();
                                player.acceptGame(gameId);
                                dialog.cancel();
                            }
                        });
                        builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                Toast.makeText(getApplicationContext(), "You Declined game invite", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        });
                        // Create the AlertDialog
                        AlertDialog dialog = builder.create();
//        dialog.getWindow().setGravity(Gravity.TOP);
                        dialog.show();

            }
        });

    }

    public void onCreate() {
        adapter.replaceFragment(createGame, 1);

    }

    public void createGame(JSONArray invitees) {
        Toast.makeText(this, "creating game", Toast.LENGTH_SHORT).show();
        player.initiateGame(invitees);
    }
    private class cAdapter extends FragmentStatePagerAdapter {
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
            // Causes adapter to reload all Fragments when
            // notifyDataSetChanged is called
            return POSITION_NONE;
        }
        private void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }
        public void replaceFragment(Fragment fragment, int index) {
            //fm.beginTransaction().replace(mFragmentList.get((index)),fragment).commit();
            mFragmentList.remove(index);
            mFragmentList.add(index, fragment);
            notifyDataSetChanged();
        }
    }
}

