package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newsapp.model.Journel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

import util.UserInfo;
public class AddJornalActivity extends AppCompatActivity {
private EditText tittleEt,descriptionEt;
private TextView titleTv,descriptionTv;
private Button savePost;
private ProgressBar progressBar;
private ImageView imageView,addPhotoButton;
private String currentUserId;
private String currentUserName;
private FirebaseAuth firebaseAuth;
private FirebaseAuth.AuthStateListener authStateListener;
private FirebaseUser user;
private FirebaseFirestore db=FirebaseFirestore.getInstance();
private StorageReference storageReference;
private final CollectionReference collectionReference=db.collection("Journal");
private Uri imageUri;
    private static final int Gallery_Code=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_jornal);

        storageReference= FirebaseStorage.getInstance().getReference();
        firebaseAuth=FirebaseAuth.getInstance();

        //Widgets
        progressBar=findViewById(R.id.progressBar);
        savePost=findViewById(R.id.button);
        imageView=findViewById(R.id.image1);
        addPhotoButton=findViewById(R.id.imageView);
        tittleEt=findViewById(R.id.editTextTextPersonName);
        descriptionEt=findViewById(R.id.editTextTextPersonName2);
        titleTv=findViewById(R.id.postTitle);
        descriptionTv=findViewById(R.id.postData);

        //there are data in Singleton
        //make text =username
        if (UserInfo.getInstance()!=null){
            currentUserId= UserInfo.getInstance().getUserId();
            currentUserName= UserInfo.getInstance().getUserName();
            titleTv.setText(currentUserName);
        }




            //declaration authStateListener
        authStateListener=new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (user!=null){

                }
            }
        };

        // Btn that send data to firestore and Storage
        savePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveJournal();
            }
        });
        //onClick Photo to capture an image
        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Gallery_Code);

            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Gallery_Code&&resultCode==RESULT_OK){
            if (data!=null){
                imageUri=data.getData();
                imageView.setImageURI(imageUri);

            }
        }
    }

    //function host Data
    private void saveJournal() {
        final String title=tittleEt.getText().toString().trim();
        final String description=descriptionEt.getText().toString().trim();
        if (!TextUtils.isEmpty(title)&&!TextUtils.isEmpty(description)&&imageUri!=null){
            // store image in StorageReference
            final StorageReference filePath=storageReference
                    .child("Journal_images")
                    .child("my_image_"+ Timestamp.now().getSeconds());
            filePath
                    .putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath
                            .getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl=uri.toString();
                            Journel journel=new Journel();
                            journel.setTitle(title);
                            journel.setDescription(description);
                            journel.setImageUrl(imageUrl);
                            journel.setTimeAdded(new Timestamp(new Date()));
                            journel.setUserName(currentUserName);
                            journel.setUserId(currentUserId);
                            //store jornal in fireStore
                            collectionReference
                                    .add(journel)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    startActivity(new Intent(AddJornalActivity.this,
                                            JournalListActivity.class));
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddJornalActivity.this, "Failure!"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
        else {
            progressBar.setVisibility(View.VISIBLE);

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        user=firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuth!=null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}