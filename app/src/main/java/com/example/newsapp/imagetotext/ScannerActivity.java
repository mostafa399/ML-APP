package com.example.newsapp.imagetotext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newsapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class ScannerActivity extends AppCompatActivity {
    private TextView textView;
    private ImageView imageView;
    private Bitmap imageBitmap;
    private static final int REQUEST_IMAGE_CAPTURE=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        //widgets
        textView=findViewById(R.id.idTVDetectedText);
        imageView=findViewById(R.id.idIVCaptureImage);


    }
    //open camera
    public void onSnap(View view) {
        //permission to using camera
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
            captureImage();
        }
        else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},200);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==200&&grantResults.length>0){
            captureImage();
        }
    }

    private void captureImage() {
        Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (i.resolveActivity(getPackageManager())!=null){
            startActivityForResult(i,REQUEST_IMAGE_CAPTURE);
        }
        
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_IMAGE_CAPTURE&&resultCode==RESULT_OK){
            assert data != null;
            Bundle extras=data.getExtras();
            imageBitmap= (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }


    //translate image to text
    public void ondetect(View view) {
        detectText();
    }

    private void detectText() {
        TextRecognizer recognizer= TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        InputImage image=InputImage.fromBitmap(imageBitmap,0);
        Task<Text>result=recognizer.process(image)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(Text text) {
                String resultText = text.getText();
                for (Text.TextBlock block : text.getTextBlocks()) {
                    String blockText = block.getText();
                    Point[] blockCornerPoints = block.getCornerPoints();
                    Rect blockFrame = block.getBoundingBox();
                    for (Text.Line line : block.getLines()) {
                        String lineText = line.getText();
                        Point[] lineCornerPoints = line.getCornerPoints();
                        Rect lineFrame = line.getBoundingBox();
                        for (Text.Element element : line.getElements()) {
                            String elementText = element.getText();
                            Point[] elementCornerPoints = element.getCornerPoints();
                            Rect elementFrame = element.getBoundingBox();
                            for (Text.Symbol symbol : element.getSymbols()) {
                                String symbolText = symbol.getText();
                                Point[] symbolCornerPoints = symbol.getCornerPoints();
                                Rect symbolFrame = symbol.getBoundingBox();
                            }
                            textView.setText(blockText);
                        }
                    }

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ScannerActivity.this, "Failed To Detect text from image !! ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}