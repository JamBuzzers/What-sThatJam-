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

import butterknife.BindView;
import butterknife.ButterKnife;


public class SearchableAdapter extends RecyclerView.Adapter<SearchableAdapter.ViewHolder> {

    ArrayList<User> users;

    public  SearchableAdapter(ArrayList<User> u){ users=u; }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View searchView = inflater.inflate(R.layout.item_search, viewGroup, false);

        SearchableAdapter.ViewHolder viewHolder = new ViewHolder(searchView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(users.get(position).username);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvName) TextView name;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
