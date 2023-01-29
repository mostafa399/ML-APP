package com.example.newsapp.qrcode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.newsapp.R;

public class ResultDialog extends DialogFragment {
    Button button;
    TextView textView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_result_dialog,container,false);
        String text="";
        button=view.findViewById(R.id.ok_btn);
        textView=view.findViewById(R.id.dialogText);
        // Getting the bundle:
        Bundle bundle = getArguments ();
        text = bundle .getString (  "RESULT_TEXT");
        textView. setText(text);
        button.setOnClickListener(v -> dismiss());
        return view;
    }
}
