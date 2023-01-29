package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import util.UserInfo;

public class MainActivity extends AppCompatActivity {
    private EditText mail,pass;
    private Button login,register;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private  FirebaseAuth.AuthStateListener authStateListener ;
    private final FirebaseFirestore db=FirebaseFirestore.getInstance();
    private final CollectionReference collectionReference=db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //widgets
        mail=findViewById(R.id.email);
        pass=findViewById(R.id.passward);
        login=findViewById(R.id.login);
        register=findViewById(R.id.register);

        firebaseAuth=FirebaseAuth.getInstance();

       //Onclick LoginBtn
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=mail.getText().toString();
                String password=pass.getText().toString();

                logInEmailAndPassword(email,password);
            }
        });

        //Onclick Register
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,SignUp.class);
                startActivity(i);
            }
        });

    }
    private void logInEmailAndPassword(String email, String password) {
        //edit text not empty
        if (!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password)){

            firebaseAuth
                    .signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    FirebaseUser user=firebaseAuth.getCurrentUser();
                    //user not null
                    if (user!=null){
                    final String currentUser=user.getUid();
                    //id that found in FireStore named by userId
                    collectionReference
                            .whereEqualTo("userId",currentUser)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error!=null){
                            }
                            assert value!=null;
                            if (!value.isEmpty()){
                                for (QueryDocumentSnapshot snapshot:value){
                                    UserInfo jornalUser= UserInfo.getInstance();
                                    jornalUser.setUserName(snapshot.getString("userName"));
                                    jornalUser.setUserId(snapshot.getString("userId"));
                                    startActivity(new Intent(MainActivity.this,JournalListActivity.class));
                                }
                            }
                        }
                    });



                }}
            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Something Went Wrong"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }else {
            Toast.makeText(this, "Please Enter Email & Password", Toast.LENGTH_SHORT).show();

        }
    }


}