package com.jambuzzers.whatsthatjam;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jambuzzers.whatsthatjam.model.User;

import java.util.ArrayList;


public class SearchableAdapter extends RecyclerView.Adapter<SearchableAdapter.ViewHolder> {

    ArrayList<User> users;

    public  SearchableAdapter(ArrayList<User> u){ users=u; }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View searchView = inflater.inflate(R.layout.item_search, viewGroup, false);

        // Return a new holder instance
        SearchableAdapter.ViewHolder viewHolder = new ViewHolder(searchView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.search_bar.setText(users.get(position).username);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView search_bar;

        public ViewHolder(View itemView) {
            super(itemView);
            search_bar = (TextView) itemView.findViewById(R.id.search_bar);
        }

    }

}