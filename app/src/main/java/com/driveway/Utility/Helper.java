package com.driveway.Utility;


import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Helper {

    public String GetAddress(Activity act,double lat, double lon){
        Geocoder geocoder;
        List<Address> addresses;
        String address="";
        String postalCode="";
        String country="";
        String city="";
        geocoder = new Geocoder(act, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            Log.i("ADDRESS--->",addresses.toString());
            //Toast.makeText(MapsActivity.this,address,Toast.LENGTH_LONG).show();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return address+"/"+postalCode+"/"+city;
    }
    public static LatLng getLocationFromAddress(Activity act,String strAddress){

        Geocoder coder = new Geocoder(act,Locale.getDefault());
        List<Address> address;
        String str=null;
        LatLng p1 = null;
        Address location=null;
        try {
            address = coder.getFromLocationName(strAddress,1);
            if (address==null && address.size()==0) {
                return null;
            }
            if(address!=null && address.size()>0) {
                location = address.get(0);
                p1=new LatLng(location.getLatitude() ,location.getLongitude());
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return p1;
    }

    public double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}
