package com.jambuzzers.whatsthatjam;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jambuzzers.whatsthatjam.model.FirebaseQueries;
import com.jambuzzers.whatsthatjam.model.User;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment  {


    @BindView(R.id.profile)
    ImageView Profile;
    @BindView(R.id.upload_btn)
    Button Upload_btn;
    @BindView(R.id.name)
    TextView Name;
    @BindView(R.id.logout_btn)
    Button Logout_btn;

    int PICK_IMAGE_REQUEST = 111;
    Uri filePath;

    String username;
    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference Ref;

    User user;

    public static ProfileFragment newProfFrag(User u){
        ProfileFragment pFrag = new ProfileFragment();
        pFrag.user = u;
        return pFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    ProfileInterface listener;
    public interface ProfileInterface {
        //void onChangeImage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this,view);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(getArguments() != null)
        {
            username = getArguments().getString("username");
            Name.setText(username);
        }
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {

        Upload_btn = view.findViewById(R.id.upload_btn);
        Upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChangeImage();
            }
        });

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        Logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something
            }
        });


        Ref = FirebaseDatabase.getInstance().getReference().child("users");
        FirebaseQueries.userById(username, new OnCompleteListener<DocumentSnapshot>() {
            @Override

            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String TAG = "BY ID";
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        String url = document.get("profileurl").toString();
                        GlideApp.with(getContext())
                                .load(url)
                                .into(Profile);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    public static ProfileFragment newInstance(String username) {
        ProfileFragment frag = new ProfileFragment();
        Bundle b = new Bundle();
        b.putString("username", username);
        frag.setArguments(b);
        return frag;
    }

    public void onChangeImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    public void uploadImage() {
        if(filePath != null)
        {
            final ProgressDialog progressDialog =new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());

            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String photoUrl = uri.toString();
                            progressDialog.dismiss();
                            FirebaseQueries.updatePic(username,photoUrl); // TODO: get user
                            Toast.makeText(getActivity(),"Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            })
            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                            .getTotalByteCount());
                    progressDialog.setMessage("Uploaded "+(int)progress+"%");
                }

            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && intent != null && intent.getData() != null) {
            filePath = intent.getData();
            try {
                //getting image from gallery
                Uri filePath = intent.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                //Setting image to ImageView
                Profile.setImageBitmap(bitmap);
                uploadImage();  //upload to firebase
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
