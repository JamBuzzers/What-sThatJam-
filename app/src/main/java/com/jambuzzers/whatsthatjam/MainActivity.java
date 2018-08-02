// SimpleApp
// Created by Spotify on 02/08/17.
// Copyright (c) 2017 Spotify. All rights reserved.
package com.jambuzzers.whatsthatjam;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;

import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements ProfileFragment.ProfileInterface {



//    int PICK_IMAGE_REQUEST = 111;
//    Uri filePath;
//
//    @BindView(R.id.profile)
//    ImageView Profile;
//
//    @BindView(R.id.upload_btn)
//    Button Upload_btn;
//
//
//    //Firebase
//    FirebaseStorage storage;
//    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        FragmentTransaction fragmentTransaction;
        final FragmentManager fragmentManager = getSupportFragmentManager();
        //final Fragment loginFrag = new LoginFragment();
        ProfileFragment pf = ProfileFragment.newInstance("latifaozoya");
        Player mPlayer;

        ButterKnife.bind(this);
       // FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      //  ft.replace(R.id.fragment, loginFrag).commit();

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment, pf).commit();

        //ViewPager pager = findViewById(R.id.view_pager);
        //pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

//        Upload_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onChangeImage();
//            }
//        });
//
//        storage = FirebaseStorage.getInstance();
//        storageReference = storage.getReference();

    }


    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }


    private class MyPagerAdapter extends FragmentPagerAdapter {

        private MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

                //TODO: When the activity gets recreated (e.g. on orientation change) so do the ViewPager's fragments.
                //TODO: Recycler view check... and scrolling
               // case 0: return LoginFragment.newInstance("loginFragment, Instance 1");
                //case 0: return SearchableFragment.newInstance("browseFragment, instance1");
                //case 1: return GameFragment.newInstance("gameFragment, Instance 1");
                //case 2: return ProfileFragment.newInstance("profileFragment, Instance 1");
                default: return GameFragment.newInstance("gameFragment, Instance 2");
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

//    public void onChangeImage() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_PICK);
//        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
//    }
//
//    public void uploadImage() {
//        if(filePath != null)
//        {
//
//            final ProgressDialog progressDialog = new ProgressDialog(this);
//            progressDialog.setTitle("Uploading...");
//            progressDialog.show();
//            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
//
//            ref.putFile(filePath) .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    progressDialog.dismiss();
//                    Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
//                }
//            })
//            .addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    progressDialog.dismiss();
//                    Toast.makeText(MainActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            })
//            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
//                            .getTotalByteCount());
//                    progressDialog.setMessage("Uploaded "+(int)progress+"%");
//                }
//            });
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == LoginFragment.REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
        }
//        else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && intent != null && intent.getData() != null) {
//            filePath = intent.getData();
//            try {
//                //getting image from gallery
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//                //Setting image to ImageView
//                Profile.setImageBitmap(bitmap);
//                uploadImage();  //upload to firebase
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }
}
