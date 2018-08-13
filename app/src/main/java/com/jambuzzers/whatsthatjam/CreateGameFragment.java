package com.jambuzzers.whatsthatjam;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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

    @BindView(R.id.hscroll)
    LinearLayout mHscroll;


    private CreateGameListener mListener;

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
                    arr.put(u.id);
                }
                mListener.createGame(arr);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                users.clear();
                if(s.length() ==1 )
                {
                    allUsers.clear();
                    FirebaseQueries.getActive(s,new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot document : task.getResult().getDocuments())
                                users.add(new User(document));
                            searchableAdapter.notifyDataSetChanged();
                            allUsers.addAll(users);
                        }
                    });
                }
                else{
                    for(User u : allUsers){
                        if(u.username.startsWith(s))
                            users.add(u);
                    }
                }
                searchableAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CreateGameListener) {
            mListener = (CreateGameListener) context;
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


    public interface CreateGameListener {
        void createGame(JSONArray invitees);
    }

    public class CreateGameAdapter extends RecyclerView.Adapter<CreateGameAdapter.ViewHolder> {

        ArrayList<User> users;
        ArrayList<User> invitees;
        Context context;

        FirebaseStorage storage;
        StorageReference storageReference;
        DatabaseReference Ref;

        public CreateGameAdapter(ArrayList<User> u, ArrayList<User> inv) {
            invitees = inv;
            users = u;
        }

        @Override
        public CreateGameAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            context = viewGroup.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View searchView = inflater.inflate(R.layout.item_search, viewGroup, false);

            CreateGameAdapter.ViewHolder viewHolder = new CreateGameAdapter.ViewHolder(searchView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull final CreateGameAdapter.ViewHolder holder, final int position) {
            holder.name.setText(users.get(position).username);
            String murl = users.get(position).url;
            GlideApp.with(context)
                    .load(murl)
                    .centerCrop()
                    .transform(new RoundedCorners(100))
                    .circleCrop()
                    .into(holder.profSearchPic);



            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    invitees.add(users.get(position));
                    View child = getChildView(users.get(position));
                    mHscroll.addView(child);

                    Toast.makeText(getContext(),"adding user: "+users.get(position).username,Toast.LENGTH_SHORT).show();
                }
            });

        }

        private View getChildView(User user) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_invited, null);
            TextView textView = view.findViewById(R.id.tvName);
            ImageView imageView = view.findViewById(R.id.ivSearchProfPic);
            textView.setText(user.username);

            GlideApp.with(context)
                    .load(user.url)
                    .centerCrop()
                    .transform(new RoundedCorners(100))
                    .circleCrop()
                    .into(imageView);
            return view;
        }


        @Override
        public int getItemCount() {
            return users.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tvName)
            TextView name;

            @BindView(R.id.ivSearchProfPic)
            ImageView profSearchPic;



            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

        }
    }
}
