package com.jambuzzers.whatsthatjam.model;


import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.DocumentSnapshot;

@IgnoreExtraProperties
public class User {
    public String username;
    public String id;

    public User(DocumentSnapshot document){
        username = (String)document.getData().get("name");
        id = (String)document.getData().get("id");
    }
}

