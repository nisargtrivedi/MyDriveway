package com.driveway.Utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;

import com.driveway.Activity.MainNavigationScreen;

import static android.content.Context.LOCATION_SERVICE;

public class GpsLocationReceiver extends BroadcastReceiver {

    AppPreferences appPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        appPreferences=new AppPreferences(context);

        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            try {
                LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                    checkLocationServices(context);
                }
            }catch (Exception ex){

            }
        }
    }

    public void checkLocationServices(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            new AlertDialog.Builder(context)
                    .setCancelable(false)
                    .setMessage("Location services is disabled by user,please turn on the location services.")
                    .setPositiveButton("open settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                        }
                    }).show();
        }
    }
}

