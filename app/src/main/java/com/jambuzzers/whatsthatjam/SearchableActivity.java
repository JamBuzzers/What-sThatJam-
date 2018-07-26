package com.jambuzzers.whatsthatjam;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jambuzzers.whatsthatjam.model.User;

import java.util.ArrayList;

public class SearchableActivity extends AppCompatActivity {

    RecyclerView rv;
    SearchableAdapter searchableAdapter;
    ArrayList<User> users;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        rv = (RecyclerView) findViewById(R.id.rv);
        users = new ArrayList<>();
        searchableAdapter = new SearchableAdapter(users);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(searchableAdapter);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        String query = null;
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SampleRecentSuggestionsProvider.AUTHORITY, SampleRecentSuggestionsProvider.MODE);
            suggestions.saveRecentQuery(query, null);

        }

        SearchView searchView = findViewById(R.id.search_bar);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(final String search) {
                User.queryAllUsernames(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.d("tag", "it was successful");
                                    users.clear();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if(((String)document.getData().get("username")).contains(search)) {

                                            users.add(new User((String)document.getData().get("username")));

                                            //Log.d("tag", document.getId() + " => " + document.getData());
                                        }
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
                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(SearchableActivity.this,
                        SampleRecentSuggestionsProvider.AUTHORITY, SampleRecentSuggestionsProvider.MODE);
                suggestions.saveRecentQuery(query, null);
                return false;
            }
        });
    }

    @Override
    public boolean onSearchRequested() {
        Bundle appData = new Bundle();
        appData.putBoolean(String.valueOf(SearchableActivity.this), true);
        startSearch(null, false, appData, false);
        return true;
    }
}
