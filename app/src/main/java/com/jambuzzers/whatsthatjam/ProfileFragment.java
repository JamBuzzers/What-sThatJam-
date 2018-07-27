package com.jambuzzers.whatsthatjam;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ProfileFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    public static ProfileFragment newInstance(String text) {
        ProfileFragment frag = new ProfileFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);
        frag.setArguments(b);

        return frag;
    }
}
