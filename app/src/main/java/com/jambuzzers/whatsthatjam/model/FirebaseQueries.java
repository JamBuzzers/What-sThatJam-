package com.jambuzzers.whatsthatjam.model;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class FirebaseQueries {
    final static FirebaseFirestore database = FirebaseFirestore.getInstance();
    final static FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build();
    // YOU MUST CALL THIS METHOD BEFORE ANYOTHER CALLS
    public static void removeError(){
        database.setFirestoreSettings(settings);
    }
    public static void getActive(String search, OnCompleteListener complete){
        String start = search.substring(0,search.length()-1)+(char)((int)search.charAt(search.length()-1)-1);
        String end = search.substring(0,search.length()-1)+(char)((int)search.charAt(search.length()-1)+1);
        CollectionReference users = database.collection("users");
        Query query = users.whereEqualTo("active", true).whereLessThan("name",end).whereGreaterThan("name",start);
        query.get().addOnCompleteListener(complete);
    }
    public static void queryUserName(String username, OnCompleteListener<QuerySnapshot> onCompleteListener){
        database.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(onCompleteListener);
    }
    public static void queryAllUsernames(OnCompleteListener<QuerySnapshot> onCompleteListener){
        database.collection("users")
                .get()
                .addOnCompleteListener(onCompleteListener);
    }


    public static void updatePic(String user, String picUrl) {
        Map<String, Object> update = new HashMap<>();
        update.put("profileurl", picUrl);
        database.collection("users")
                .document(user)
                .set(update, SetOptions.merge());
    }
    public static void getCurrentUser(String acessToken, OnCompleteListener<QuerySnapshot> complete){
        if (acessToken == null) {
            return;
        }
        database.collection("users")
                .whereGreaterThan("token", acessToken.substring(0, acessToken.length() - 2))
                .whereLessThan("token", acessToken +"\uf8ff")
                .get()
                .addOnCompleteListener(complete);
    }
}