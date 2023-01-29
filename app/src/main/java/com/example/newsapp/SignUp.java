package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import util.UserInfo;

public class SignUp extends AppCompatActivity {

    private EditText emailCreate,passCreate,userNameCreate;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private final FirebaseFirestore db=FirebaseFirestore.getInstance();
    private final CollectionReference collectionReference=db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //widgets
        emailCreate=findViewById(R.id.emailcreate);
        passCreate=findViewById(R.id.passwordcreate);
        userNameCreate=findViewById(R.id.userName);
        Button signup = findViewById(R.id.signup);

        // FirebaseAuth declaration
        firebaseAuth=FirebaseAuth.getInstance();
        //declare AuthStateListener called when there is a change in the authentication state
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser=firebaseAuth.getCurrentUser();
                if (currentUser!=null){
                    //User already Logged in
                }
                else {
                    //No User Yet

                }
            }
        };
        //Handling Button Sign Up
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emailCreate.getText().toString().trim();
                String password=passCreate.getText().toString().trim();
                String userName=userNameCreate.getText().toString().trim();
                    //editText is not empty
                if (!TextUtils.isEmpty(email) &&!TextUtils.isEmpty(password)&&!TextUtils.isEmpty(userName)){

                    createUserEmailAndPassword(email,password,userName);

                }
                else
                {
                    Toast.makeText(SignUp.this, "Empty Fields!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void createUserEmailAndPassword(String email, String password,String userName) {
        //editText is not empty
        if (!TextUtils.isEmpty(email) &&!TextUtils.isEmpty(password)&&!TextUtils.isEmpty(userName)){

            firebaseAuth
                    .createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        currentUser=firebaseAuth.getCurrentUser();
                        assert currentUser!=null;
                        final String currentUserId=currentUser.getUid();

                            //Save userId && UserName in FireStore
                        Map<String ,String>userObj=new HashMap<>();
                        userObj.put("userId",currentUserId);
                        userObj.put("userName",userName);
                        collectionReference
                                .add(userObj)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (Objects.requireNonNull(task.getResult()).exists()){
                                            String name=task.getResult().getString("UserName");

                                                //singleton Model
                                            UserInfo jornalUser= UserInfo.getInstance();
                                            jornalUser.setUserId(currentUserId);
                                            jornalUser.setUserName(name);

                                            //Send userName and user id to secondActivity
                                            Intent i=new Intent(SignUp.this,AddJornalActivity.class);
                                            i.putExtra("userName",name);
                                            i.putExtra("userId",currentUserId);
                                            startActivity(i);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SignUp.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                                        
                                    }
                                });

                            }
                        });
                    }
                }
            });
        }

        }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser=firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}
