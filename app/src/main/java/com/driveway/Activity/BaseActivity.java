package com.driveway.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.driveway.Component.Utility;
import com.driveway.R;
import com.driveway.Utility.AppPreferences;
import com.driveway.Utility.DataContext;
import com.driveway.Utility.GpsLocationReceiver;
import com.driveway.Utility.LocationProvider;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.google.gson.JsonObject;
import com.mobandme.ada.Entity;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseActivity extends AppCompatActivity {


    public AppPreferences appPreferences;
    public DataContext dataContext;
    GpsLocationReceiver gpsLocationReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appPreferences = new AppPreferences(this);
        dataContext = new DataContext(this);
        gpsLocationReceiver = new GpsLocationReceiver();

     //   IntentFilter filter2 = new IntentFilter("android.location.PROVIDERS_CHANGED");
       // registerReceiver(gpsLocationReceiver, filter2);
    }

    @Override
    protected void onStart() {
        //checkLocationServices();
        super.onStart();
    }

    public void whiteStatusBar() {
        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
    }

    public void orangeStatusBar() {
        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.button_color));
    }

    public void transparentStatusbar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        }
    }


    public void showBottomBar() {
        // Show status bar
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void hideBottomBar() {
        // Hide status bar
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


    }

    public void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }


    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    public void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }


    //Hide Bottom Navigationbar
    public void hideBottom() {
        View decorView = getWindow().getDecorView();
// Hide both the navigation bar and the status bar.
// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
// a general rule, you should design your app to hide the status bar whenever you
// hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }


    //Hide Bottom Navigationbar
    public void hideStatusbar() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // if (gpsLocationReceiver != null)
         //   unregisterReceiver(gpsLocationReceiver);
    }

    public void checkLocationServices() {
//        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        boolean gps_enabled = false;
//        boolean network_enabled = false;
//
//        try {
//            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        } catch (Exception ex) {
//        }
//
//        try {
//            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        } catch (Exception ex) {
//        }
//
//        if (!gps_enabled && !network_enabled) {
//            // notify user
//            new AlertDialog.Builder(BaseActivity.this)
//                    .setMessage("Location services is disabled by user,please turn on location services.")
//                    .setPositiveButton("open settings", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//
//                        }
//                    }).show();
//        }
        LocationProvider provider=new LocationProvider(this, new LocationProvider.LocationCallback() {
            @Override
            public void handleNewLocation(Location location) {

            }
        });
        provider.connect();
    }


    public void deleteUser(){
        try{
            dataContext.tblUserObjectSet.fill();
            if(dataContext!=null){
                for(int i=0;i<dataContext.tblUserObjectSet.size();i++)
                    dataContext.tblUserObjectSet.remove(i).setStatus(Entity.STATUS_DELETED);

                dataContext.tblUserObjectSet.save();
                appPreferences.set("USERTYPE","");
                appPreferences.set("USERID","");
                appPreferences.set("LATITUDE","");
                appPreferences.set("LONGITUDE","");
                appPreferences.removeValue("USERTYPE");
                appPreferences.removeValue("USERID");
                appPreferences.removeValue("LATITUDE");
                appPreferences.removeValue("LONGITUDE");
            }

            dataContext.parkingSpaceObjectSet.fill();

            for(int i=0;i<dataContext.parkingSpaceObjectSet.size();i++)
                dataContext.parkingSpaceObjectSet.remove(i).setStatus(Entity.STATUS_DELETED);

            dataContext.parkingSpaceObjectSet.save();


            dataContext.carsObjectSet.fill();

            for(int i=0;i<dataContext.carsObjectSet.size();i++)
                dataContext.carsObjectSet.remove(i).setStatus(Entity.STATUS_DELETED);

            dataContext.carsObjectSet.save();


            dataContext.propertyAvailableTimesObjectSet.fill();
            for(int i=0;i<dataContext.propertyAvailableTimesObjectSet.size();i++)
                dataContext.propertyAvailableTimesObjectSet.remove(i).setStatus(Entity.STATUS_DELETED);

            dataContext.propertyAvailableTimesObjectSet.save();

            dataContext.shortStayObjectSet.fill();

            for(int i=0;i<dataContext.shortStayObjectSet.size();i++)
                dataContext.shortStayObjectSet.remove(i).setStatus(Entity.STATUS_DELETED);

            dataContext.shortStayObjectSet.save();


            dataContext.propertyDetailObjectSet.fill();
            for(int i=0;i<dataContext.propertyDetailObjectSet.size();i++)
                dataContext.propertyDetailObjectSet.remove(i).setStatus(Entity.STATUS_DELETED);

            dataContext.propertyDetailObjectSet.save();


            dataContext.propertyImageObjectSet.fill();
            for(int i=0;i<dataContext.propertyImageObjectSet.size();i++)
                dataContext.propertyImageObjectSet.remove(i).setStatus(Entity.STATUS_DELETED);

            dataContext.propertyImageObjectSet.save();

            finishAffinity();
            startActivity(new Intent(BaseActivity.this, LoginScreen.class));
//            android.os.Process.killProcess(android.os.Process.myPid());
//            System.exit(1);
        }catch (Exception ex){

        }
    }
}
