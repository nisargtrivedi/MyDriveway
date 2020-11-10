package com.driveway.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.loader.content.AsyncTaskLoader;

import com.driveway.Component.EEditText;
import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.DBHelper.tblStay;
import com.driveway.Model.LocationData;
import com.driveway.Model.ParkingSpace;
import com.driveway.Model.SearchPropertyModel;
import com.driveway.R;
import com.driveway.Utility.AppPreferences;
import com.driveway.Utility.Constants;
import com.driveway.Utility.LocationProvider;
import com.driveway.WebApi.WebUtility;
import com.driveway.helper.FetchURL;
import com.driveway.helper.TaskLoadedCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ParkerBookingScreen  extends BaseActivity implements View.OnClickListener, OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, TaskLoadedCallback {

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_DETAIL = "/details";
    private static final String OUT_JSON = "/json";
    private final String API_KEY = WebUtility.GOOGLE_MAP_KEY;
    private GoogleMap mMap;
    EEditText Location,YourLocation;
    AppCompatImageView back;

    TTextView ParkingTitle,tvKM,PropertyAddress,tvMinutes,tvAvailability,tvRatings,tvType,tvPrice,bookNow;
    com.driveway.Component.RoundCornersImageView imgone,imgtwo;

    SearchPropertyModel parkingSpace;
    GoogleApiClient mGoogleApiClient;

    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    public String placeID="";
    public String stringtext="";
    private Polyline currentPolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parker_property_booking_screen);
        Location=findViewById(R.id.Location);
        YourLocation=findViewById(R.id.YourLocation);
        back=findViewById(R.id.back);
        ParkingTitle=findViewById(R.id.ParkingTitle);
        tvKM=findViewById(R.id.tvKM);
        PropertyAddress=findViewById(R.id.PropertyAddress);
        tvMinutes=findViewById(R.id.tvMinutes);
        tvAvailability=findViewById(R.id.tvAvailability);
        tvRatings=findViewById(R.id.tvRatings);
        tvType=findViewById(R.id.tvType);
        tvPrice=findViewById(R.id.tvPrice);
        bookNow=findViewById(R.id.bookNow);
        imgone=findViewById(R.id.imgone);
        imgtwo=findViewById(R.id.imgtwo);


        //locationProvider = new LocationProvider(getApplicationContext(), this);
        //locationProvider.connect();

        parkingSpace=new SearchPropertyModel();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(getIntent().getExtras()!=null){
            placeID=getIntent().getStringExtra("placeid");
            stringtext=getIntent().getStringExtra("text");

            if(stringtext.length()==0){
//                if(!ParkerDashboardScreen.placeLocation.isEmpty()){
//                    YourLocation.setText(ParkerDashboardScreen.placeLocation);
//                    placeID=ParkerDashboardScreen.placeIDD;
//                }else
                    YourLocation.setText("Your location");
            }else{
                YourLocation.setText(stringtext);
            }
            parkingSpace= (SearchPropertyModel) getIntent().getSerializableExtra("parking");
            if(parkingSpace!=null){
                Location.setText(parkingSpace.propertyTitle);
                ParkingTitle.setText(parkingSpace.propertyTitle);
                PropertyAddress.setText(parkingSpace.propertyAddress);
                tvKM.setText(parkingSpace.propertyDistance);
                tvAvailability.setText(parkingSpace.propertyAvailability.contains("Available")?parkingSpace.propertyAvailability:parkingSpace.propertyAvailability+" Available");
                tvRatings.setText(parkingSpace.propertyRating);
                tvMinutes.setText(parkingSpace.propertyDuration);
                tvType.setText(parkingSpace.propertyParkingType);
                if(parkingSpace.propertyImage!=null&&!parkingSpace.propertyImage.isEmpty()){
                    Picasso.with(this).load(parkingSpace.propertyImage).fit().into(imgone);
                }
                if(parkingSpace.propertyImageTwo!=null&&!parkingSpace.propertyImageTwo.isEmpty()){
                    Picasso.with(this).load(parkingSpace.propertyImageTwo).fit().into(imgtwo);
                }else{
                    imgone.setVisibility(View.INVISIBLE);
                    Picasso.with(this).load(parkingSpace.propertyImage).fit().into(imgtwo);
                }
                priceCompare();
                if(parkingSpace.list.size()>0)
                    tvPrice.setText("$"+String.format("%.0f",parkingSpace.list.get(0).StayPrice)+" - $"+String.format("%.0f",parkingSpace.list.size()>0?parkingSpace.list.get(parkingSpace.list.size()-1).StayPrice:"0"));
                else
                    tvPrice.setText("$0 - $0");
            }
        }

        whiteStatusBar();
        back.setOnClickListener(this);
        bookNow.setOnClickListener(this);
    }
    @Override
    protected void onStart() {
        super.onStart();
        bookNow.setEnabled(true);
        //hideBottomBar();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:finish();break;
            case R.id.bookNow:
                if(appPreferences.getString("USERID").equalsIgnoreCase("0"))
                {

                    Utility.showAlertWithEventHandling(this, Constants.LOGIN_MESSAGE, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            //finish();
                            startActivity(new Intent(ParkerBookingScreen.this,LoginScreen.class)
                                    .putExtra("booking_details", parkingSpace)
                            );
                        }
                    });
                }
                else {
                    startActivity(new Intent(ParkerBookingScreen.this, ParkerBookingStayScreen.class)
                            .putExtra("booking_details", parkingSpace));
                    bookNow.setEnabled(false);
                }
                break;
        }
    }

    private void priceCompare(){
        if(parkingSpace.list!=null && parkingSpace.list.size()>0)
            Collections.sort(parkingSpace.list, (lhs, rhs) -> (int) (lhs.StayPrice - rhs.StayPrice));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(Double.parseDouble(appPreferences.getString("lat")),Double.parseDouble(appPreferences.getString("lng"))))
                .zoom(14F)
                .build();
        mMap.setMaxZoomPreference(100f);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
               // mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            //mMap.setMyLocationEnabled(true);
        }
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }



//    @Override
//    public void handleNewLocation(android.location.Location location) {
//        locationData.setLang(location.getLongitude() + "");
//        locationData.setLat(location.getLatitude() + "");
//    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        mLastLocation = location;
        appPreferences.set("lat",location.getLatitude()+"");
        appPreferences.set("lng",location.getLongitude()+"");
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraPosition cameraPosition2 = new CameraPosition.Builder()
                .target(latLng)
                .zoom(12.5F)
                .build();
        mMap.setMaxZoomPreference(100f);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition2));

        //      MarkerOptions markerOptions = new MarkerOptions();
        //markerOptions.position(latLng);
        //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//        mCurrLocationMarker = mMap.addMarker(markerOptions);


        if(!placeID.isEmpty()) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            ArrayList<Double> data = placeDetail((placeID));
            if (data.size() > 0) {

                ArrayList<LatLng> points = new ArrayList<>();
                PolylineOptions lineOptions = new PolylineOptions();
                lineOptions.color(R.color.button_color);

                Double lat =data.get(0);
                Double lng =data.get(1);
                LatLng position = new LatLng(lat, lng);
                LatLng position2 = new LatLng(Double.parseDouble(parkingSpace.propertyLatitude), Double.parseDouble(parkingSpace.propertyLongitude));


               // points.add(position);
               // points.add(position2);
               // lineOptions.width(4);
               // lineOptions.geodesic(true);
               // lineOptions.addAll(points);

                if (lineOptions != null) {
                 //   mMap.addPolyline(lineOptions);

                    //move map camera
                    mMap.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).flat(true));
                    mMap.addMarker(new MarkerOptions().position(position2).icon(BitmapDescriptorFactory.fromResource(R.drawable.location_black)).flat(true));
                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    //mMap.animateCamera(CameraUpdateFactory.zoomTo(11));


                    new FetchURL(ParkerBookingScreen.this).execute(getUrl(position, position2, "driving"), "driving");

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(lat,lng))
                            .zoom(12.5F)
                            .build();
                    mMap.setMaxZoomPreference(100f);
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                } else {
                    Log.d("onPostExecute", "without Polylines drawn");
                }

            }
        }
        else{

              LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
              LatLng position2 = new LatLng(Double.parseDouble(parkingSpace.propertyLatitude), Double.parseDouble(parkingSpace.propertyLongitude));
                mMap.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).flat(true));
                mMap.addMarker(new MarkerOptions().position(position2).icon(BitmapDescriptorFactory.fromResource(R.drawable.location_black)).flat(true));

            new FetchURL(ParkerBookingScreen.this).execute(getUrl(position, position2, "driving"), "driving");

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(),location.getLongitude()))
                    .zoom(12.5F)
                    .build();
            mMap.setMaxZoomPreference(100f);
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

//    private class DownloadTask extends AsyncTask<String,String,String> {
//
//        @Override
//        protected String doInBackground(String... url) {
//
//            String data = "";
//
//            try {
//                data = downloadUrl(url[0]);
//            } catch (Exception e) {
//                Log.d("Background Task", e.toString());
//            }
//            return data;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//
//            ParserTask parserTask = new ParserTask();
//
//
//            parserTask.execute(result);
//
//        }
//
//    }
//
//    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>>> {
//
//        // Parsing the data in non-ui thread
//        @Override
//        protected List<List<HashMap<String,String>>> doInBackground(String... jsonData) {
//
//            JSONObject jObject;
//            List<List<HashMap<String,String>>> routes = null;
//
//            try {
//                jObject = new JSONObject(jsonData[0]);
//                DataParser parser = new DataParser();
//
//                routes = parser.parse(jObject);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return routes;
//        }
//
//
//        @Override
//        protected void onPostExecute(List<List<HashMap<String,String>>> result) {
//            ArrayList<LatLng> points;
//            PolylineOptions lineOptions = null;
//            double lat=0;
//            double lng=0;
//
//            // Traversing through all the routes
//            for (int i = 0; i < result.size(); i++) {
//                points = new ArrayList<>();
//                lineOptions = new PolylineOptions();
//
//                // Fetching i-th route
//                List<HashMap<String, String>> path = result.get(i);
//                // Fetching all the points in i-th route
//                for (int j = 0; j < path.size(); j++) {
//                    HashMap<String, String> point = path.get(j);
//
//                    lat = Double.parseDouble(point.get("lat"));
//                    lng = Double.parseDouble(point.get("lng"));
//                    LatLng position = new LatLng(lat, lng);
//
//                    points.add(position);
//                }
//
//                // Adding all the points in the route to LineOptions
//                lineOptions.addAll(points);
//                lineOptions.width(10);
//                lineOptions.color(Color.RED);
//
//                Log.d("onPostExecute","onPostExecute lineoptions decoded");
//
//            }
//
//            // Drawing polyline in the Google Map for the i-th route
//            if(lineOptions != null) {
//                mMap.addPolyline(lineOptions);
//          //      mMap.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).flat(true));
//           // mMap.addMarker(new MarkerOptions().position(position2).icon(BitmapDescriptorFactory.fromResource(R.drawable.location_black)).flat(true));
//            //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//            //mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
//
//            CameraPosition cameraPosition = new CameraPosition.Builder()
//                    .target(new LatLng(lat,lng))
//                    .zoom(12F)
//                    .build();
//            mMap.setMaxZoomPreference(100f);
//            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//
//
//            }
//            else {
//                Log.d("onPostExecute","without Polylines drawn");
//            }
//        }
//    }
//
private String getUrl(LatLng origin, LatLng dest, String directionMode) {
    // Origin of route
    String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
    // Destination of route
    String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
    // Mode
    String mode = "mode=" + directionMode;
    // Building the parameters to the web service
    String parameters = str_origin + "&" + str_dest + "&" + mode;
    // Output format
    String output = "json";
    // Building the url to the web service
    String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + API_KEY;
    return url;
}


    public ArrayList<Double> placeDetail(String input) {
        ArrayList<Double> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_DETAIL + OUT_JSON);
            sb.append("?placeid=" + URLEncoder.encode(input, "utf8"));
            sb.append("&key=" + API_KEY);
            URL url = new URL(sb.toString());
            //Log.e("url", url.toString());
            System.out.println("URL: "+url);
            //Log.e("nous sommes entrai de test la connexion au serveur", "test to connect to the api");
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader inp = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = inp.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
            inp.close();
            System.out.println("le json result"+jsonResults.toString());
        } catch (MalformedURLException e) {
            //Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            //Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }

        }

        try {
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            //JSONArray predsJsonArray = jsonObj.getJSONArray("html_attributions");
            JSONObject result = jsonObj.getJSONObject("result").getJSONObject("geometry").getJSONObject("location");

            Double longitude  = result.getDouble("lng");
            Double latitude =  result.getDouble("lat");

            System.out.println("longitude et latitude "+ longitude+latitude);

            resultList = new ArrayList<Double>(result.length());
            resultList.add(result.getDouble("lat"));
            resultList.add(result.getDouble("lng"));
        } catch (JSONException e) {
            ///Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }


//
//
//    public class DataParser {
//
//        List<List<HashMap<String,String>>> parse(JSONObject jObject){
//
//            List<List<HashMap<String, String>>> routes = new ArrayList<>() ;
//            JSONArray jRoutes;
//            JSONArray jLegs;
//            JSONArray jSteps;
//
//            try {
//
//                jRoutes = jObject.getJSONArray("routes");
//
//                /** Traversing all routes */
//                for(int i=0;i<jRoutes.length();i++){
//                    jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
//                    List path = new ArrayList<>();
//
//                    /** Traversing all legs */
//                    for(int j=0;j<jLegs.length();j++){
//                        jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");
//
//                        /** Traversing all steps */
//                        for(int k=0;k<jSteps.length();k++){
//                            String polyline = "";
//                            polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
//                            List<LatLng> list = decodePoly(polyline);
//
//                            /** Traversing all points */
//                            for(int l=0;l<list.size();l++){
//                                HashMap<String, String> hm = new HashMap<>();
//                                hm.put("lat", Double.toString((list.get(l)).latitude) );
//                                hm.put("lng", Double.toString((list.get(l)).longitude) );
//                                path.add(hm);
//                            }
//                        }
//                        routes.add(path);
//                    }
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }catch (Exception e){
//            }
//
//
//            return routes;
//        }
//
//
//        /**
//         * Method to decode polyline points
//         * */
//        private List<LatLng> decodePoly(String encoded) {
//
//            List<LatLng> poly = new ArrayList<>();
//            int index = 0, len = encoded.length();
//            int lat = 0, lng = 0;
//
//            while (index < len) {
//                int b, shift = 0, result = 0;
//                do {
//                    b = encoded.charAt(index++) - 63;
//                    result |= (b & 0x1f) << shift;
//                    shift += 5;
//                } while (b >= 0x20);
//                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
//                lat += dlat;
//
//                shift = 0;
//                result = 0;
//                do {
//                    b = encoded.charAt(index++) - 63;
//                    result |= (b & 0x1f) << shift;
//                    shift += 5;
//                } while (b >= 0x20);
//                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
//                lng += dlng;
//
//                LatLng p = new LatLng((((double) lat / 1E5)),
//                        (((double) lng / 1E5)));
//                poly.add(p);
//            }
//
//            return poly;
//        }
//    }
}
