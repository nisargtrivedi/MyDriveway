package com.driveway.background_service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.driveway.Activity.OwnerDashboardScreen;
import com.driveway.Model.ChatModel;
import com.driveway.R;
import com.driveway.Utility.Constants;
import com.driveway.Utility.DataContext;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.mobandme.ada.exceptions.AdaFrameworkException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;

public class ChatService extends Service {

    FirebaseDatabase database;
    DataContext dataContext;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        database = FirebaseDatabase.getInstance();
        dataContext=new DataContext(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            dataContext.tblUserObjectSet.fill();
        } catch (AdaFrameworkException e) {
            e.printStackTrace();
        }
        //loadChatData();
        return super.onStartCommand(intent, flags, startId);

    }

    public class ChatBinder extends Binder {
        public ChatService getService() {
            return ChatService.this;
        }
    }

//    public void loadChatData() {
//
//        database.getReference().child(Constants.CONVERSION).addChildEventListener(new ChildEventListener() {
//
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    ChatModel model = new ChatModel();
//                    if (dataContext.tblUserObjectSet.get(0).APIToken.equalsIgnoreCase(snapshot.child("sender").getValue(String.class)) || dataContext.tblUserObjectSet.get(0).APIToken.equalsIgnoreCase(snapshot.child("receiver").getValue(String.class))) {
//                        model.isRead = snapshot.child("isRead").getValue(Boolean.class);
//                        if(model.isRead==false)
//                           // sendNotification(snapshot.child("sender").getValue(String.class),snapshot.child("message").getValue(String.class));
//
//                    }
//                }
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//
//
//    }

    int fcid=0;
    private void sendNotification(String title, String message) {

        Date date=new Date();
        fcid= (int) date.getTime();

        try {
//            if (!isAppIsInBackground(this)) {
//
//            } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String CHANNEL_ID = "driveway_channel1";// The id of the channel.
                CharSequence name = "driveway";
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
                            .setContentTitle(title)
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
                            .setContentTitle(title)
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
            //     }
        }catch (Exception ex){

        }
    }
}
