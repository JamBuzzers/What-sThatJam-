package com.jambuzzers.whatsthatjam;

import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
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
import com.jambuzzers.whatsthatjam.model.User;

import java.util.ArrayList;

public class SearchableFragment extends Fragment {

    RecyclerView rv;

    public SearchableFragment() {
    }

    SearchView searchView;
    SearchableAdapter searchableAdapter;
    ArrayList<User> users;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searchable, container, false);  //Inflate Layout
        rv = (RecyclerView) view.findViewById(R.id.rv);

        users = new ArrayList<>();
        searchableAdapter = new SearchableAdapter(users);
        rv.setAdapter(searchableAdapter);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {

        // Get the intent, verify the action and get the query
        //Intent intent = getIntent();
//       String query = null;
//        if (Intent.ACTION_SEARCH.equals(getActivity())) {
//            query = getActivity().getIntent().getStringExtra(SearchManager.QUERY);
//            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getContext(),
//                    SampleRecentSuggestionsProvider.AUTHORITY, SampleRecentSuggestionsProvider.MODE);
//            suggestions.saveRecentQuery(query, null);
//        }

        searchView = (SearchView) view.findViewById(R.id.search_bar);

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
                                if((String)document.getData().get("username") != null) {
                                    if (((String) document.getData().get("username")).contains(search)) {
                                        users.add(new User((String) document.getData().get("username")));
                                        //Log.d("tag", document.getId() + " => " + document.getData());
                                    }
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
                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getContext(),
                        SampleRecentSuggestionsProvider.AUTHORITY, SampleRecentSuggestionsProvider.MODE);
                suggestions.saveRecentQuery(query, null);
                return false;
            }
        });
    }

//    public boolean onSearchRequested() {
//        Bundle appData = new Bundle();
//        appData.putBoolean(String.valueOf(SearchableFragment.this), true);
//        getActivity().startSearch(null, false, appData, false);
//        return true;
//    }


    public static SearchableFragment newInstance(String text) {
        SearchableFragment frag = new SearchableFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);
        frag.setArguments(b);

        return frag;
    }
}

