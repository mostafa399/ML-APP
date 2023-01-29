package com.example.newsapp.imagetotext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newsapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class TranslatorActivity extends AppCompatActivity {
private Spinner spinnerFrom,spinnerTo;
private TextView textView;
private EditText editText;
private final String[] fromLanguages = {"from","English","Afrikaans","Arabic","Belarusian","Bengali","Catalan","Czech", "Welsh", "Hindi", "Urdu"};
private final String[] toLanguages = {"to","English","Afrikaans","Arabic","Belarusian","Bengali","Catalan","Czech", "Welsh", "Hindi", "Urdu"};
private String  fromLanguageCode, toLanguageCode ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translator);
        //widgets
        spinnerFrom=findViewById(R.id.idFromSpinner);
        spinnerTo=findViewById(R.id.idToSpinner);
        textView=findViewById(R.id.idTvTranslatedTV);
        editText=findViewById(R.id.editTextTextPersonName3);


        //ArrayAdapter to Show Spinner1
        ArrayAdapter addapter = new ArrayAdapter ( this, R. layout. spinner_item, fromLanguages);
        addapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(addapter);

        //ArrayAdapter to Show Spinner2
        ArrayAdapter toAdapter=new ArrayAdapter<>(this,R.layout.spinner_item,toLanguages);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTo.setAdapter(toAdapter);

        //firstSpinner
        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromLanguageCode=getLanguageCode(fromLanguages[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //SecondSpinner
        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toLanguageCode=getLanguageCode(toLanguages[position]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }

    private String getLanguageCode(String Language) {
        String languagecode;
        switch (Language){
            case "English":languagecode= TranslateLanguage.ENGLISH;
            break;
            case "Afrikaans":languagecode= TranslateLanguage.AFRIKAANS;
            break;
            case "Arabic":languagecode= TranslateLanguage.ARABIC;
                break;
            case "Belarusian":languagecode= TranslateLanguage.BELARUSIAN;
                break;
            case "Bengali":languagecode= TranslateLanguage.BENGALI;
                break;
            case "Czech":languagecode= TranslateLanguage.CZECH;
                break;
            case "Catalan":languagecode= TranslateLanguage.CATALAN;
                break;
            case "Welsh":languagecode= TranslateLanguage.WELSH;
                break;
            case "Hindi":languagecode= TranslateLanguage.HINDI;
                break;
            case "Urdu":languagecode= TranslateLanguage.URDU;
                break;
            default:languagecode="";
        }
        return languagecode;


    }

    public void onTranslate(View view) {
        textView.setText("");
        if (editText.getText().toString().isEmpty()||fromLanguageCode.isEmpty()||toLanguageCode.isEmpty()){
            Toast.makeText(this, "Missing input info !", Toast.LENGTH_SHORT).show();}
        else {
            translateText(fromLanguageCode,toLanguageCode,editText.getText().toString());
        }

    }

    private void translateText(String fromLanguageCode, String toLanguageCode, String source) {
        textView.setText("Downloading Language Model");
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(fromLanguageCode)
                        .setTargetLanguage(toLanguageCode)
                        .build();
        final Translator translator =
                Translation.getClient(options);
        DownloadConditions conditions = new DownloadConditions.Builder().build();
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                textView.setText("Translating");
                translator.translate(source).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        textView.setText(s);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TranslatorActivity.this, "Failed to Translate", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TranslatorActivity.this, "Failed to download the language", Toast.LENGTH_SHORT).show();

            }
        });
    }
}