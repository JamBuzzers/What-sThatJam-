package com.jambuzzers.whatsthatjam;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class GameFragment extends Fragment {

    private Button stopBtn;
    private EditText etSongGuess;
    protected Integer userScore;
    protected String guessTxt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        stopBtn = view.findViewById(R.id.guess_btn);
        etSongGuess = view.findViewById(R.id.etGuess);

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initGuessAccess();
                //TODO: Stop music after designated time...call function that handles that...
            }
        });
        etSongGuess.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if(i == EditorInfo.IME_ACTION_DONE) guessTxt = textView.getText().toString();
                return handled;
            }
        });

    }

    public void initGuessAccess() {
        stopBtn.setEnabled(false);
        etSongGuess.setEnabled(true);
    }
}
