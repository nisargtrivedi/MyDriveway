package com.driveway.Activity;

import android.app.Application;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.driveway.Utility.AppPreferences;
import com.driveway.Utility.DataContext;
import com.google.firebase.database.FirebaseDatabase;

public class MyDriveWayApplication extends Application {



    AppPreferences appPreferences;

    public static double Lat=0;
    public static double Lng=0;

    @Override
    public void onCreate() {
        super.onCreate();
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }



}
