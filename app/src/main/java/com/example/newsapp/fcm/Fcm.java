package com.example.newsapp.fcm;

import static android.content.ContentValues.TAG;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.newsapp.R;
import com.google.firebase.messaging.FirebaseMessaging;

public class Fcm extends AppCompatActivity {
    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fcm);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            String channelId=getString(R.string.default_notification_channel_id);
            String channelName=getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,channelName,NotificationManager.IMPORTANCE_LOW));
        }
        if (getIntent().getExtras()!=null){
            for (String key: getIntent().getExtras().keySet()){
                Object value=getIntent().getExtras().get(key);
                Log.d(TAG,"key"+key+"value "+value);
            }
        }
    }

    public void onSubscribeToTobic(View view) {
        FirebaseMessaging.getInstance().subscribeToTopic("Weather").addOnCompleteListener(task -> {
            String msg="Subscribed";
            if (!task.isSuccessful()){
                msg="Failed";
            }
            Log.d(TAG,msg);
            Toast.makeText(this, ""+msg, Toast.LENGTH_SHORT).show();
        });
    }

    public void OnReadNotification(View view) {
        FirebaseMessaging .getInstance().getToken().addOnCompleteListener(task -> {
    if (!task.isSuccessful())       {
        Log.v(TAG,"Failed To Register Token",task.getException());
        return;
    }
    String token=task.getResult();
    String msg=getString(R.string.message_token_fmt,token);
    Log.v(TAG,msg);
            Toast.makeText(this, ""+msg, Toast.LENGTH_SHORT).show();
        });
    }
}
