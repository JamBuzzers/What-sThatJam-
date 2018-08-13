package com.jambuzzers.whatsthatjam.model;


import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.DocumentSnapshot;

@IgnoreExtraProperties
public class User {
    public String username;
    public String id;
    public String url;
    public Boolean online;

    public User(DocumentSnapshot document){
        if(document.getData().get("name")!=null)
            username = (String)document.getData().get("name");
        else
            username = document.getId();
        if(document.getData().get("profileurl") != null)
            url =(String) document.getData().get("profileurl");
        else
            url="";
        id = document.getId();
        if(document.getData().get("active") != null)
            online = (Boolean) document.getData().get("active");
        else
            online = false;
    }
}
