package com.jambuzzers.whatsthatjam;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jambuzzers.whatsthatjam.model.FirebaseQueries;
import com.jambuzzers.whatsthatjam.model.User;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchableFragment extends Fragment {

    @BindView(R.id.rv) RecyclerView rv;
    @BindView(R.id.search_bar) SearchView searchView;
    SearchableAdapter searchableAdapter;
    ArrayList<User> users;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searchable, container, false);  //Inflate Layout
        ButterKnife.bind(this,view);
        users = new ArrayList<>();
        searchableAdapter = new SearchableAdapter(users);
        rv.setAdapter(searchableAdapter);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }
    public void onViewCreated(View view, Bundle savedInstanceState) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(final String search) {
                FirebaseQueries.queryAllUsernames(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                                Log.d("tag", "it was successful");
                                users.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if(document.getData().get("name")!= null && ((String) document.getData().get("name")).contains(search))
                                        users.add(new User(document));
                                    else if(document.getId().contains(search))
                                        users.add(new User(document));
                                }
                                searchableAdapter.notifyDataSetChanged();
                            } else {
                                Log.d("tag", "Error getting document: ", task.getException());
                        }
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }
        });
    }

}

