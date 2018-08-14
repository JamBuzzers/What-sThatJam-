package com.jambuzzers.whatsthatjam;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class EndGameFragment extends Fragment {

    private EndListener mListener;
    public  interface EndListener{
        void rematch();
        void reset();
    }

    public static EndGameFragment newInstance() {
        EndGameFragment fragment = new EndGameFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @BindView(R.id.btRematch)
    Button btRematch;
    @BindView(R.id.btHome)
    Button btHome;
    @BindView(R.id.rvScores)
    RecyclerView rvScores;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.fragment_end_game, container, false);
        ButterKnife.bind(this,view);
        btRematch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.rematch();
            }
        });
        btHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.reset();
            }
        });
        scoreAdapter adapter= new scoreAdapter((ArrayList<Pair<String,String>>)getArguments().getSerializable("arr"));
        rvScores.setAdapter(adapter);
        rvScores.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EndListener) {
            mListener = (EndListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    public static EndGameFragment newInstance(ArrayList<Pair<String,String>> standing){
        EndGameFragment eg = new EndGameFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("arr",standing);
        eg.setArguments(bundle);
        return eg;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public class scoreAdapter extends RecyclerView.Adapter<scoreAdapter.ViewHolder> {
        ArrayList<Pair<String,String>> standing;

        public scoreAdapter(ArrayList<Pair<String,String>> standing) {
            this.standing = standing;
        }

        @Override
        public scoreAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            Context context = viewGroup.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View item = inflater.inflate(R.layout.item_score, viewGroup, false);
            scoreAdapter.ViewHolder viewHolder = new scoreAdapter.ViewHolder(item);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull scoreAdapter.ViewHolder holder, final int position) {
            holder.name.setText(standing.get(position).first);
            holder.tvScore.setText(standing.get(position).second);

        }

        @Override
        public int getItemCount() {
            return standing.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tvName)
            TextView name;
            @BindView(R.id.tvScore) TextView tvScore;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

        }
    }

}
