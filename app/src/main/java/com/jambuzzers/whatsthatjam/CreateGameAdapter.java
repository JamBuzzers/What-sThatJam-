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

public class CreateGameAdapter extends RecyclerView.Adapter<CreateGameAdapter.ViewHolder> {

    ArrayList<User> users;
    ArrayList<User> invitees;

    public CreateGameAdapter(ArrayList<User> u, ArrayList<User> inv) {
        invitees = inv;
        users = u;
    }

    @Override
    public CreateGameAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View searchView = inflater.inflate(R.layout.item_search, viewGroup, false);

        CreateGameAdapter.ViewHolder viewHolder = new CreateGameAdapter.ViewHolder(searchView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CreateGameAdapter.ViewHolder holder, final int position) {
        holder.name.setText(users.get(position).username);
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                invitees.add(users.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvName)
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
