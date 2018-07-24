package com.jambuzzers.whatsthatjam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;


public class BrowseAdapter {

    public class ViewHolder {
        public SearchView search_bar;

        public ViewHolder(View itemView) {
            search_bar = (SearchView) itemView.findViewById(R.id.search_bar);
        }

    }

    public BrowseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View searchView = inflater.inflate(R.layout.item_search, parent, false);

        // Return a new holder instance
        BrowseAdapter.ViewHolder viewHolder = new ViewHolder(searchView);
        return viewHolder;
    }

//    public void onBindViewHolder(BrowseAdapter.ViewHolder holder) {
//        holder.search_bar.setVisibility(View.VISIBLE);
//    }

}
