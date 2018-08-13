package com.jambuzzers.whatsthatjam;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jambuzzers.whatsthatjam.model.FirebaseQueries;
import com.jambuzzers.whatsthatjam.model.User;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SearchableAdapter extends RecyclerView.Adapter<SearchableAdapter.ViewHolder> {

    ArrayList<User> users;

    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference Ref;

    Context context;

    public SearchableAdapter(ArrayList<User> u) {
        users = u;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View searchView = inflater.inflate(R.layout.item_search, viewGroup, false);

        SearchableAdapter.ViewHolder viewHolder = new ViewHolder(searchView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.name.setText(users.get(position).username);
        String murl = users.get(position).url;
        GlideApp.with(context)
                .load(murl)
                .centerCrop()
                .transform(new RoundedCorners(100))
                .circleCrop()
                .into(holder.profPic);
    if(users.get(position).online) {
        holder.activeGreen.setVisibility(View.VISIBLE);
    }else{
        holder.activeGreen.setVisibility(View.GONE);
    }
    }



    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvName) TextView name;
        @BindView(R.id.ivSearchProfPic) ImageView profPic;
        @BindView(R.id.ivOnline) ImageView activeGreen;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
