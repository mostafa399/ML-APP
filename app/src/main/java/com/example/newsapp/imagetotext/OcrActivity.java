package com.example.newsapp.imagetotext;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.BuildCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;

import com.bumptech.glide.Glide;
import com.example.newsapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.Locale;

//@BuildCompat.PrereleaseSdkCheck
public class OcrActivity extends AppCompatActivity {
    private ImageView imageView;
private TextView textView;
private TextToSpeech textToSpeech;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);
        //widgets
        imageView=findViewById(R.id.imageGallery);
        textView=findViewById(R.id.textfromGallery);


        textToSpeech=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status!=TextToSpeech.ERROR){
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });
        

    }

    public void onChooseImage(View view) {
        openGallery();

    }

    private void openGallery() {
        Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/");
        Intent pickIntent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/");
        Intent chooserIntent=Intent.createChooser(galleryIntent,"Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,new Intent[]{pickIntent});
        startActivityForResult(chooserIntent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1){
            if (data!=null){
                TextRecognizer  recognizer= TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

                try {

                    InputImage inputImage = InputImage.fromFilePath(this, data.getData());
                    Bitmap imageUri= inputImage.getBitmapInternal();
                    Glide.with(OcrActivity.this).load(imageUri).into(imageView);

                    //Process th image
                    Task<Text>result=recognizer.process(inputImage).addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text text) {
                            String resultText=text.getText();
                            for (Text.TextBlock block:text.getTextBlocks()){
                                String blockText=block.getText();
                                textView.append("\n");
                                Point[]blockCornerPoint=block.getCornerPoints();
                                Rect blockFrame=block.getBoundingBox();
                                for (Text.Line line:block.getLines()){
                                    String lineText=line.getText();
                                    Point[]lineCornerPoint=line.getCornerPoints();
                                    Rect lineFrame=line.getBoundingBox();
                                    for (Text.Element element:line.getElements()){
                                        textView.append("");
                                        String elementText=element.getText();
                                        textView.append(elementText);
                                        Point[]elementCornerPoints=element.getCornerPoints();
                                        Rect elementFrame=element.getBoundingBox();

                                    }}}
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }


    public void onReadText(View view) {
        textToSpeech.speak(textView.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);

    }

    @Override
    protected void onPause() {
        if (!textToSpeech.isSpeaking()){
            super.onPause();
        }
    }

    @Override
    protected void onResume() {
        textView.setText("");
        super.onResume();
    }
}