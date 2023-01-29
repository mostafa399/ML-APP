package com.example.newsapp.qrcode;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.newsapp.R;
import com.example.newsapp.imagetotext.OcrActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.List;

public class FaceDetectionActivity extends AppCompatActivity {
    ImageView imageView;
    Button button;
    TextView textView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detection);
        imageView=findViewById(R.id.cameraImage);
        textView=findViewById(R.id.faceText);
        button=findViewById(R.id.cameraBtn);
        FirebaseApp.initializeApp(this);
        button.setOnClickListener(v -> {
                openFile();

        });

    }


    private void openFile() {
        Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       if (i.resolveActivity(getPackageManager())!=null){
           startActivityForResult(i,200);
       }
       else {
           Toast.makeText(this, "Failed!!!", Toast.LENGTH_SHORT).show();
       }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Bundle bundle=data.getExtras();
        Bitmap bitmap= (Bitmap) bundle.get("data");
        faceDetectionProcess(bitmap);
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void faceDetectionProcess(Bitmap bitmap) {
        textView.setText("FACE DETECTOR IN PROCESS ");
        final StringBuilder builder=new StringBuilder();
        BitmapDrawable drawable= (BitmapDrawable) imageView.getDrawable();
        InputImage image=InputImage.fromBitmap(bitmap,0);
        Bitmap bitmap1=image.getBitmapInternal();
        Glide.with(FaceDetectionActivity.this).load(bitmap1).into(imageView);
        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .enableTracking()
                        .build();
        FaceDetector detector = FaceDetection.getClient(highAccuracyOpts);
        Task<List<Face>>result=detector.process(image);
        result.addOnSuccessListener(faces -> {
          if (faces.size()!=0){
              if (faces.size()==1){
                  builder.append(faces.size()+"Face Detected\n\n");
              } else if (faces.size()>1) {
                  builder.append(faces.size()+"Faces Detected\n\n");

              }
              for (Face face :faces){

                  //Rotating And tilting
                  int id=face.getTrackingId();
                  float rotz=face.getHeadEulerAngleZ();
                  float rotY=face.getHeadEulerAngleY();
                  builder.append("1 .Face Tracking Id ["+id+"]\n");
                  builder.append("2 .Head Rotation To Right ["+String.format("%.2f",rotY)+"deg.]\n");
                  builder.append("2 .Head Tiled SideWays ["+String.format("%.2f",rotz)+"deg.]\n");

                  //Smiling Probability
                  if (face.getSmilingProbability()>0){
                      float smile=face.getSmilingProbability();
                      builder.append("4 .Smiling Probability["+String.format("%.2f",smile)+"]\n");

                  }
                  if (face.getLeftEyeOpenProbability()>0){
                      float leftOpenEye=face.getLeftEyeOpenProbability();
                      builder.append("5 .Open Left Eye Probability["+String.format("%.2f",leftOpenEye)+"]\n");

                  }
                  if (face.getRightEyeOpenProbability()>0){
                      float rightOpenEye=face.getRightEyeOpenProbability();
                      builder.append("6 .Open Right Eye Probability["+String.format("%.2f",rightOpenEye)+"]\n");

                  }
                  builder.append("");


              }
              showDetection("Face Detection",builder,true);
          }
        }).addOnFailureListener(e -> {
            StringBuilder builder1=new StringBuilder();
            builder1.append("Sorry There Is An Error ");
            showDetection("Face Detection",builder1,true);


        });

    }

    private void showDetection(String title, StringBuilder builder, boolean success) {
        if (success==true){
            textView.setText(null);
            textView.setMovementMethod(new ScrollingMovementMethod());
            if (builder.length()!=0){
                textView.append(builder);
                if (title.substring(0,title.indexOf(" ")).equalsIgnoreCase("OCR")){
                    textView.append("\n (Hold the text to copy it !)");

                }else {
                    textView.append("(Hold the text to copy it !)");
                }
                textView.setOnLongClickListener(v -> {
                    ClipboardManager clipboardManager= (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData=ClipData.newPlainText(title,builder);
                    clipboardManager.setPrimaryClip(clipData);
                    return  true;
                });

            }else {
                textView.append(title.substring(0,title.indexOf(" "))+"Failed to find any thing");
            }
        } else if (success==false) {
            textView.setText(null);
            textView.setMovementMethod(new ScrollingMovementMethod());
            textView.append(builder);

        }

    }

    @Override
    protected void onResume() {
        textView.setText("");
        super.onResume();
    }
}
