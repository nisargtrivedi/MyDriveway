package com.driveway.FCM;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;

import com.driveway.Activity.ActivityChatScreen;
import com.driveway.Activity.ActivityChatScreen_;
import com.driveway.Activity.LoginScreen;
import com.driveway.Activity.MainNavigationScreen;
import com.driveway.Activity.MainScreen;
import com.driveway.Activity.NotificationChat;
import com.driveway.Activity.NotificationChat_;
import com.driveway.Activity.OwnerDashboardScreen;
import com.driveway.Activity.ParkerBooking.BookingChat;
import com.driveway.Activity.SettingsActivity;
import com.driveway.Component.Utility;
import com.driveway.R;
import com.driveway.Utility.AppPreferences;
import com.driveway.Utility.DataContext;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonObject;
import com.mobandme.ada.Entity;
import com.mobandme.ada.exceptions.AdaFrameworkException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FcmInstanceIDService extends FirebaseMessagingService {

    AppPreferences sharedPref;
    int fcid = 0;
    Context context;
    DataContext dataContext;

    String emoji;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        sharedPref = new AppPreferences(this);
        dataContext = new DataContext(this);
        context = this;
        // String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.i("MESSAGE DETAILS==== ", remoteMessage.getData().toString());

        context = this;
        sharedPref = new AppPreferences(this);
        dataContext = new DataContext(this);

        Date date = new Date();
        fcid = (int) date.getTime();

        String from_uid = "", to_uid = "", title = "", message = "", name = "", type = "";
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("Message data payload: ", remoteMessage.getData().toString());
//            {notification_type=block}
            if (remoteMessage.getData().get("notification_type") != null) {
                try {
                    type = remoteMessage.getData().get("notification_type");
                    if (type.equalsIgnoreCase("block") || type.equalsIgnoreCase("delete")) {

                        if (MainNavigationScreen.getSingletonObject() != null) {
                            MainNavigationScreen.getSingletonObject().logoutSession();
                        }
                    } else if (type.equalsIgnoreCase("chat")) {
                        try {
                            if (!BookingChat.isActive) {
                                if (!ActivityChatScreen.isActive && !NotificationChat.isActive) {
                                    sendNotification(remoteMessage.getData().get("friendId"), remoteMessage.getData().get("parkingId"), remoteMessage.getNotification().getTitle().toString(), remoteMessage.getNotification().getBody().toString(), type);
                                }
                                } else if (!ActivityChatScreen.isActive) {
                                if (!BookingChat.isActive && !NotificationChat.isActive) {
                                    System.out.println("CALLED 2");
                                    sendNotification(remoteMessage.getData().get("friendId"), remoteMessage.getData().get("parkingId"), remoteMessage.getNotification().getTitle().toString(), remoteMessage.getNotification().getBody().toString(), type);
                                }
                            }
                            else if (!NotificationChat.isActive) {
                                if (!BookingChat.isActive && !ActivityChatScreen.isActive) {
                                    System.out.println("CALLED 2");
                                    sendNotification(remoteMessage.getData().get("friendId"), remoteMessage.getData().get("parkingId"), remoteMessage.getNotification().getTitle().toString(), remoteMessage.getNotification().getBody().toString(), type);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        sendNotification("", "", remoteMessage.getNotification().getTitle().toString(), remoteMessage.getNotification().getBody().toString(), type);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (remoteMessage.getNotification() != null) {
            Log.d("Message Notification: ", remoteMessage.getNotification().getBody().toString());
            Log.d("Message Title: ", remoteMessage.getNotification().getTitle().toString());


            try {
                if (type.equalsIgnoreCase("chat")) {
                    sendNotification(remoteMessage.getData().get("friendId"), remoteMessage.getData().get("parkingId"), remoteMessage.getNotification().getTitle().toString(), remoteMessage.getNotification().getBody().toString(), type);
                } else {
                    sendNotification("", "", remoteMessage.getNotification().getTitle().toString(), remoteMessage.getNotification().getBody().toString(), "");
                }
//                if (remoteMessage.getNotification().getTitle().toString().equalsIgnoreCase("You have been blocked")){
//
//                    if(MainNavigationScreen.getSingletonObject()!=null)
//                        MainNavigationScreen.getSingletonObject().callLogout(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
//                }
//                else if(remoteMessage.getNotification().getTitle().toString().equalsIgnoreCase("You have been suspended")){
//
//                    if(MainNavigationScreen.getSingletonObject()!=null)
//                        MainNavigationScreen.getSingletonObject().callLogout(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
//                }
//                else if(remoteMessage.getNotification().getTitle().toString().equalsIgnoreCase("Your account has been deleted"))
//                {
//                    if(MainNavigationScreen.getSingletonObject()!=null)
//                        MainNavigationScreen.getSingletonObject().callLogout(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
//                }


            } catch (Exception ex) {

            }
        }
        super.onMessageReceived(remoteMessage);

    }

    private void sendRegistrationToServer(String token) {
        sharedPref.set("DEVICE_KEY", token);
        Log.i("TOKEN =:", token + "");
        //sharedPref.setOpenAppCounter(SharedPref.MAX_OPEN_COUNTER);
    }


    private void sendNotification(String fromuid, String toid, String title, String message, String type) {

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

                Intent intent;
                if (!type.isEmpty() && type.equalsIgnoreCase("chat")) {
                    intent = new Intent(this, NotificationChat_.class);
                    intent.putExtra("property_id", toid);
                    intent.putExtra("friend_id", fromuid);
                } else {
                    intent = new Intent(this, MainScreen.class);
                }
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

                Intent intent;
                if (!type.isEmpty() && type.equalsIgnoreCase("chat")) {
                    intent = new Intent(this, NotificationChat_.class);
                    intent.putExtra("property_id", toid);
                    intent.putExtra("friend_id", fromuid);
                } else {
                    intent = new Intent(this, MainScreen.class);
                }

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
        } catch (Exception ex) {

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
        } catch (Exception ex) {
        }

        return isInBackground;
    }

}
