package com.driveway.FCM;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.driveway.Activity.OwnerDashboardScreen;
import com.driveway.R;
import com.driveway.Utility.AppPreferences;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;

/**
 * Created by nisarg on 3/08/19.
 */

public class FcmMessagingService extends FirebaseMessagingService {

    int fcid=0;
    AppPreferences appPreferences;
    Context context;

    String emoji;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        appPreferences=new AppPreferences(this);

        //Log.i("MESSAGE DETAILS==== " , remoteMessage.getData().toString());
        //Log.i("MESSAGE DETAILS==== " , remoteMessage.getData().toString());





            //sendNotification(from_uid,to_uid,title,name,type);


       // if (remoteMessage.getNotification() != null) {
        //    Log.d("Message Notification: " ,remoteMessage.toString());


       // }
        sendNotification("","","","","");

    }

    private void sendNotification(String fromuid, String toid, String title, String message, String type) {

        try {
            if (!isAppIsInBackground(this)) {

            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String CHANNEL_ID = "my_channel_01";// The id of the channel.
                    CharSequence name = "DOCUMENT App";
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                    mChannel.setLightColor(Color.GREEN);
                    mChannel.enableLights(true);
                    mChannel.enableVibration(true);
                    mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

                    Intent intent = new Intent(this, OwnerDashboardScreen.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder notificationBuilder = null;
                    try {
                        notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("MyDriveway")
                                .setContentText(URLDecoder.decode(message, "utf-8"))
                                .setContentIntent(pendingIntent)
                                .setColor(255)
                                .setAutoCancel(true);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.createNotificationChannel(mChannel);
                    notificationManager.notify(fcid, notificationBuilder.build());


                } else {
                    //Intent intent = new Intent(this, AnnouncementDetails_.class);

                    Intent intent = new Intent(this, OwnerDashboardScreen.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    NotificationCompat.Builder notificationBuilder = null;
                    try {
                        notificationBuilder = new NotificationCompat.Builder(this)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("MyDriveway")
                                .setContentText(URLDecoder.decode(message, "utf-8"))
                                .setContentIntent(pendingIntent)
                                .setDefaults(Notification.DEFAULT_ALL)
                                .setPriority(Notification.PRIORITY_HIGH)
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    notificationManager.notify(fcid, notificationBuilder.build());

                }
            }
        }catch (Exception ex){

        }
    }


    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
                for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (String activeProcess : processInfo.pkgList) {
                            if (activeProcess.equals(context.getPackageName())) {
                                isInBackground = false;
                                Log.i("isInBackground", "No");
                            }
                        }
                    }
                }
            } else {
                List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
                ComponentName componentInfo = taskInfo.get(0).topActivity;
                if (componentInfo.getPackageName().equals(context.getPackageName())) {
                    isInBackground = false;
                    Log.i("isInBackground", "No");
                }
            }
        }catch (Exception ex){}

        return isInBackground;
    }



}
