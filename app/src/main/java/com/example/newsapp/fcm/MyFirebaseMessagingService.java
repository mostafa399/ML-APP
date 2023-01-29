package com.example.newsapp.fcm;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String TAG="FirebaseCloudMessaging";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        Log.v(TAG,"From :"+message.getFrom());
        if (message.getData().size()>0){
            Log.v(TAG,"Message Data Payload:"+message.getFrom());
            if (true){
                sceduleJob();
            }
            else {
                handleNow();
            }
        }
        //check Payload
        if (message.getNotification()!=null){
            Log.v(TAG,"Message Notification Body :" +message.getNotification().getBody());
        }

    }



    @Override
    public void onNewToken(@NonNull String token) {
        Log.v(TAG,"On New Token :"+token);
        sendRegisterationToServer(token);

    }



    private void sceduleJob() {
        OneTimeWorkRequest workRequest=new OneTimeWorkRequest.Builder(MyWorker.class).build();
        WorkManager.getInstance(this).beginWith(workRequest).enqueue();

    }
    private void handleNow() {
        Log.v(TAG,"Short Lived Data Is Done !");
    }
    private void sendRegisterationToServer(String token) {

    }
    private void sendNotification(String messageBody){
        Intent i=new Intent(this,Fcm.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi= PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_ONE_SHOT);
        
    }

}
