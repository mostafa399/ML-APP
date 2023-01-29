package com.example.newsapp.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.newsapp.R;
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
        String channelId=getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,channelId)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle(getString(R.string.Fcm_Message))
                .setContentText(messageBody)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);

        NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel(channelId,"Channel Human Readable Tittle",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);

        }
        notificationManager.notify(0,builder.build());
        
    }

}
