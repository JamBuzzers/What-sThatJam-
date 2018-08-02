package com.jambuzzers.whatsthatjam;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jambuzzers.whatsthatjam.model.FirebaseQueries;
import com.jambuzzers.whatsthatjam.model.User;

import org.json.JSONArray;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CreateGameFragment extends Fragment {

    @BindView(R.id.rvUsers)
    RecyclerView rvUsers;
    @BindView(R.id.search_bar)
    SearchView searchView;
    CreateGameAdapter searchableAdapter;
    ArrayList<User> users;
    ArrayList<User> allUsers;
    ArrayList<User> invitees= new ArrayList<User>();
    private OnFragmentInteractionListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_game, container, false);  //Inflate Layout
        ButterKnife.bind(this,view);
        users = new ArrayList<>();
        allUsers = new ArrayList<>();
        searchableAdapter = new CreateGameAdapter(users,invitees);
        rvUsers.setAdapter(searchableAdapter);
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }
    public void onViewCreated(View view, Bundle savedInstanceState) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //TODO Invite using socket player
                JSONArray arr = new JSONArray();
                for(User u : invitees){
                    arr.put(u.username);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s.length() ==1 )
                {
                    FirebaseQueries.getActive(s,new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot document : task.getResult().getDocuments())
                                users.add(new User(document));
                            searchableAdapter.notifyDataSetChanged();
                        }
                    });
                    allUsers.addAll(users);
                }
                else{
                    users.clear();
                    for(User u : allUsers){
                        if(u.username.startsWith(s))
                            users.add(u);
                    }
                }

                return false;
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
