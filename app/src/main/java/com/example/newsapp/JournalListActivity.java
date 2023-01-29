 package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newsapp.fcm.Fcm;
import com.example.newsapp.imagetotext.OcrActivity;
import com.example.newsapp.imagetotext.ScannerActivity;
import com.example.newsapp.imagetotext.TranslatorActivity;
import com.example.newsapp.model.Journel;
import com.example.newsapp.qrcode.FaceDetectionActivity;
import com.example.newsapp.qrcode.QrActivity;
import com.example.newsapp.ui.MyAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import util.UserInfo;

 public class JournalListActivity extends AppCompatActivity {
     private FirebaseAuth firebaseAuth;
     private FirebaseAuth.AuthStateListener authStateListener;
     private FirebaseUser user;
     ArrayList<Journel>journelArrayList;
     private final FirebaseFirestore db=FirebaseFirestore.getInstance();
     private final CollectionReference collectionReference=db.collection("Journal");
     private RecyclerView recyclerView;
     private TextView textView;
     private MyAdapter adapter;




     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);

        firebaseAuth=FirebaseAuth.getInstance();
        user= firebaseAuth.getCurrentUser();
        //widgets
        textView=findViewById(R.id.textView4);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        journelArrayList=new ArrayList<>();


    }

     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.menu,menu);
         return super.onCreateOptionsMenu(menu);

     }

     @Override
     public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         switch (item.getItemId()){
             case R.id.actionAdd:
                 if (user!=null&&firebaseAuth!=null){
                     startActivity(new Intent(JournalListActivity.this,AddJornalActivity.class));

                 }
                 break;
             case R.id.actionSignOut:
                 if (user!=null&&firebaseAuth!=null){
                     firebaseAuth.signOut();
                     startActivity(new Intent(JournalListActivity.this,MainActivity.class));
                 }break;
             case R.id.magetoText:
                 if (user!=null&&firebaseAuth!=null){
                     startActivity(new Intent(JournalListActivity.this, ScannerActivity.class));
                 }break;
             case R.id.Translator:
                 if (user!=null&&firebaseAuth!=null){
                     startActivity(new Intent(JournalListActivity.this, TranslatorActivity.class));
                 }break;
             case R.id.Ocr:
                 if (user!=null&&firebaseAuth!=null){
                     startActivity(new Intent(JournalListActivity.this, OcrActivity.class));
                 }break;
             case R.id.Or:
                 if (user!=null&&firebaseAuth!=null){
                     startActivity(new Intent(JournalListActivity.this, QrActivity.class));
                 }break;
             case R.id.face:
                 if (user!=null&&firebaseAuth!=null){
                     startActivity(new Intent(JournalListActivity.this, FaceDetectionActivity.class));
                 }break;
             case R.id.fcm:
                 if (user!=null&&firebaseAuth!=null){
                     startActivity(new Intent(JournalListActivity.this,Fcm.class));
                 }break;

         }
         return super.onOptionsItemSelected(item);
     }

     @Override
     protected void onStart() {
         super.onStart();
         collectionReference
                 .whereEqualTo("userId", UserInfo.getInstance().getUserId())
                 .get()
                 .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
             @Override
             public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                 if (!queryDocumentSnapshots.isEmpty()){
                     for (QueryDocumentSnapshot snapshot:queryDocumentSnapshots){
                         Journel journel=snapshot.toObject(Journel.class);
                         journelArrayList.add(journel);
                     }
                     adapter=new MyAdapter(getApplicationContext(),journelArrayList);
                     recyclerView.setAdapter(adapter);
                     adapter.notifyDataSetChanged();

                 }
                 else {
                     textView.setVisibility(View.VISIBLE);
                 }

             }
         }).addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         Toast.makeText(JournalListActivity.this, "Oops! Something went wrong!", Toast.LENGTH_SHORT).show();
                     }
                 });
     }
     @Override
     protected void onRestart() {
         super.onRestart();
         finish();
         startActivity(getIntent());
     }
 }