//package com.jambuzzers.whatsthatjam;
//
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//
//import butterknife.BindView;
//
//import static android.app.Activity.RESULT_OK;
//
//
///**
//
// */
//public class SearchProfileFragment extends Fragment {
//    @BindView(R.id.ivFriendPic)
//    ImageView friendPic;
//    @BindView(R.id.upload_btn)
//    Button Upload_btn;
//    @BindView(R.id.tvFriendName)
//    TextView friendName;
//    @BindView(R.id.InvitetoGameBttn)
//    Button Invite_btn;
//
//    int PICK_IMAGE_REQUEST = 111;
//    Uri filePath;
//
//    String username;
//    //Firebase
//    FirebaseStorage storage;
//    StorageReference storageReference;
//    DatabaseReference Ref;
//
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_search_profile, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//    }
//    public static SearchProfileFragment newInstance(String username){
//        SearchProfileFragment frag = new SearchProfileFragment();
//        return frag;
//    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        super.onActivityResult(requestCode, resultCode, intent);
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && intent != null && intent.getData() != null) {
//            filePath = intent.getData();
//            try {
//                //getting image from gallery
//                Uri filePath = intent.getData();
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
//                //Setting image to ImageView
//                friendPic.setImageBitmap(bitmap);
//                uploadImage();  //upload to firebase
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//}
