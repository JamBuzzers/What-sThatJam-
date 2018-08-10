package com.jambuzzers.whatsthatjam.model;


import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.DocumentSnapshot;

@IgnoreExtraProperties
public class User {
    public String username;
    public String id;

    public User(DocumentSnapshot document){
        if(document.getData().get("name")!=null)
            username = (String)document.getData().get("name");
        else
            username = document.getId();
        id = document.getId();
    }
}
