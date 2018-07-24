package com.jambuzzers.whatsthatjam.model;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

@IgnoreExtraProperties
public class User {

    public String username;

    final static FirebaseFirestore database = FirebaseFirestore.getInstance();

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username) {
        this.username = username;
    }

    private void writeNewUser(String userId, String name) {
        User user = new User(name);
//        mDatabase.child("users").child(userId).child("username").setValue(name);
    }

    public static void queryUserName(String username, OnCompleteListener<QuerySnapshot> onCompleteListener){
        database.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(onCompleteListener);
    }

}

