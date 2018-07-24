package com.jambuzzers.whatsthatjam;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class FragmentController extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_controller);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        // define your fragments here
        final Fragment fragment1 = new SearchableFragment();
        final Fragment fragment2 = new GameFragment();
        final Fragment fragment3 = new ProfileFragment();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        // handle navigation selection
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        switch (item.getItemId()) {
                            case R.id.action_search:
                                fragmentTransaction.replace(R.id.placeholder, fragment1).commit();
                                return true;
                            case R.id.action_game:
                                fragmentTransaction.replace(R.id.placeholder, fragment2).commit();
                                return true;
                            case R.id.action_profile:
                                fragmentTransaction.replace(R.id.placeholder, fragment3).commit();
                                return true;
                        }
                        return false;
                    }
                });
    }
}
