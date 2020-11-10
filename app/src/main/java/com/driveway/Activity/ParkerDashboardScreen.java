package com.driveway.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.driveway.Adapters.ParkerSearchPropertyAdapter;
import com.driveway.Component.BButton;
import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.DBHelper.tblCars;
import com.driveway.DBHelper.tblStay;
import com.driveway.Model.PlaceDetails;
import com.driveway.Model.SearchPropertyModel;
import com.driveway.R;
import com.driveway.Utility.Constants;
import com.driveway.Utility.Helper;
import com.driveway.Utility.KeyBoardHandling;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParkerDashboardScreen extends MainNavigationScreen implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    public String TAG = "PARKER DASHBOARD";

    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    private final String API_KEY = WebUtility.GOOGLE_MAP_KEY;


    FrameLayout cl;
    BButton btnApply, btnclear;
    String Type = "";
    TTextView one_star, two_star, three_star, four_star, five_star, tvyourkm, tvsearchkm;
    TTextView Driveway, Carport, Grass;
    TextView tvDate, tvTime, tvMinus, tvPlus, tvAdd2, tvMin2, tvRight, mHigh, mLow, hrLow, hrHigh, dLow, dHigh, wkLow, wkHigh, tvShortStay, tvM, tvWl, tvDl, tvHr;
    TextView sclose, hclose, dclose, wclose, mclose;
    String rating = "";
    String shortStatyPrice = "";
    String shortDailyPrice = "";
    String shortWeeklyPrice = "";
    String shortHourlyPrice = "";
    String shortMonthlyPrice = "";
    String fDate = "", fTime = "";
    String yourLocation = "0", searchLocation = "0";
    double latitude = 0;
    double longitude = 0;

    private GoogleMap mMap;
    public AppCompatImageView hammer;
    public RecyclerView rvCars;
    public ImageView btnFilter, btnSwitch, btnMap;
    public com.jaygoo.widget.RangeSeekBar  seekBar,monthlyseekBar, weeklyseekBar, dailyseekBar, hourseekBar;
    public ListView lvLocation;
    public androidx.appcompat.widget.AppCompatAutoCompleteTextView atvPlaces;

    public ArrayList<tblCars> list = new ArrayList<>();
    public ArrayList<SearchPropertyModel> searchPropertyModels = new ArrayList<>();

    GooglePlacesAutocompleteAdapter placesAutocompleteAdapter;
    ParkerSearchPropertyAdapter parkerSearchPropertyAdapter;

    public Location mLastLocation;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    HashSet<String> ParkingType = new HashSet<>();
    HashSet<String> Ratings = new HashSet<>();
    TextView ssLeft;
    public String placeID = "";
    public static String placeIDD = "";
    public static String placeLocation = "";
    int year = 0, month = 0, day = 0;

    public int countAdd = 0;
    public int countMin = 0;

    @Override
    public void setLayoutView() {
        LayoutInflater.from(this).inflate(R.layout.parker_dashboardscreen, lnrContainer);
    }

    ArrayList<PlaceDetails> resultList = new ArrayList<>();

    WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();


    @Override
    public void initialization() {
        setDrawerState(true);
        setDrawer();

    }

    private void filterDialog() {
        View v = LayoutInflater.from(this).inflate(R.layout.parker_parking_type_filter, null, false);
        BottomSheetDialog dialog = new BottomSheetDialog(ParkerDashboardScreen.this, R.style.TransparentDialog);

        dialog.setContentView(v);
        dialog.setCanceledOnTouchOutside(false);

//        FrameLayout bottomSheet = (FrameLayout)
//                dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
//        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
//        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//        behavior.setPeekHeight(0);
//        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//            @Override
//            public void onStateChanged(@NonNull View view, int i) {
//                if (i == BottomSheetBehavior.STATE_DRAGGING) {
//                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                }
//            }
//
//            @Override
//            public void onSlide(@NonNull View view, float v) {
//
//            }
//        });
        dialog.setCancelable(false);

        AppCompatImageView close = v.findViewById(R.id.close);

        sclose = v.findViewById(R.id.sclose);
        hclose = v.findViewById(R.id.hclose);
        wclose = v.findViewById(R.id.wclose);
        dclose = v.findViewById(R.id.dclose);
        mclose = v.findViewById(R.id.mclose);

        tvM = v.findViewById(R.id.tvM);
        tvHr = v.findViewById(R.id.tvHr);
        tvDl = v.findViewById(R.id.tvDl);
        tvWl = v.findViewById(R.id.tvWl);
        tvM = v.findViewById(R.id.tvM);

        btnclear = v.findViewById(R.id.btnclear);
        tvShortStay = v.findViewById(R.id.tvShortStay);
        dLow = v.findViewById(R.id.dLow);
        dHigh = v.findViewById(R.id.dHigh);

        hrLow = v.findViewById(R.id.hrLow);
        hrHigh = v.findViewById(R.id.hrHigh);

        wkLow = v.findViewById(R.id.wkLow);
        wkHigh = v.findViewById(R.id.wkHigh);

        mLow = v.findViewById(R.id.mLow);
        mHigh = v.findViewById(R.id.mHigh);

        ssLeft = v.findViewById(R.id.ssLeft);
        tvRight = v.findViewById(R.id.tvRight);
        seekBar = v.findViewById(R.id.seekBar);
        hourseekBar = v.findViewById(R.id.hourseekBar);
        weeklyseekBar = v.findViewById(R.id.weeklyseekBar);
        dailyseekBar = v.findViewById(R.id.dailyseekBar);
        monthlyseekBar = v.findViewById(R.id.monthlyseekBar);

        tvMin2 = v.findViewById(R.id.tvMin2);
        tvAdd2 = v.findViewById(R.id.tvAdd2);
        tvyourkm = v.findViewById(R.id.tvyourkm);
        tvsearchkm = v.findViewById(R.id.tvsearchkm);
        tvMinus = v.findViewById(R.id.tvMinus);
        tvPlus = v.findViewById(R.id.tvPlus);
        tvDate = v.findViewById(R.id.tvDate);
        tvTime = v.findViewById(R.id.tvTime);
        one_star = v.findViewById(R.id.one_star);
        two_star = v.findViewById(R.id.two_star);
        three_star = v.findViewById(R.id.three_star);
        four_star = v.findViewById(R.id.four_star);
        five_star = v.findViewById(R.id.five_star);
        Grass = v.findViewById(R.id.Grass);
        Carport = v.findViewById(R.id.Carport);
        Driveway = v.findViewById(R.id.Driveway);
        btnApply = v.findViewById(R.id.btnApply);

        Calendar now = Calendar.getInstance();
        year = now.get(java.util.Calendar.YEAR);
        month = now.get(java.util.Calendar.MONTH);
        day = now.get(java.util.Calendar.DAY_OF_MONTH);

        getMaxPrice();

        tvsearchkm.setText(countMin + " Km");
        tvyourkm.setText(countAdd + " Km");
        tvDate.setText(fDate);
        tvTime.setText(fTime);
        if (ParkingType.contains("Grass")) {
            Grass.setBackgroundResource(R.drawable.add_property_type_bg);
        }
        if (ParkingType.contains("Driveway")) {
            Driveway.setBackgroundResource(R.drawable.add_property_type_bg);
        }
        if (ParkingType.contains("Carport")) {
            Carport.setBackgroundResource(R.drawable.add_property_type_bg);
        }
        if (Ratings.contains("1")) {
            one_star.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_star_orange, 0, 0, 0);
        }
        if (Ratings.contains("2")) {
            two_star.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_star_orange, 0, 0, 0);
        }
        if (Ratings.contains("3")) {
            three_star.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_star_orange, 0, 0, 0);
        }
        if (Ratings.contains("4")) {
            four_star.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_star_orange, 0, 0, 0);
        }
        if (Ratings.contains("5")) {
            five_star.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_star_orange, 0, 0, 0);
        }


        if (!shortStatyPrice.isEmpty()) {
            String[] p1=shortStatyPrice.split(",");
            seekBar.setProgress(Integer.parseInt(p1[0]), Integer.parseInt(p1[1]));
            sclose.setVisibility(View.VISIBLE);
        }

        if (!shortHourlyPrice.isEmpty()) {
            String[] p2=shortHourlyPrice.split(",");
            hourseekBar.setProgress(Integer.parseInt(p2[0]), Integer.parseInt(p2[1]));
            hclose.setVisibility(View.VISIBLE);

        }
        if (!shortDailyPrice.isEmpty()) {
            String[] p2=shortDailyPrice.split(",");
            dailyseekBar.setProgress(Integer.parseInt(p2[0]), Integer.parseInt(p2[1]));
            dclose.setVisibility(View.VISIBLE);

        }
        if (!shortWeeklyPrice.isEmpty()) {
            String[] p2=shortWeeklyPrice.split(",");
            weeklyseekBar.setProgress(Integer.parseInt(p2[0]), Integer.parseInt(p2[1]));
            wclose.setVisibility(View.VISIBLE);

        }
        if (!shortMonthlyPrice.isEmpty()) {
            String[] p2=shortMonthlyPrice.split(",");
            monthlyseekBar.setProgress(Integer.parseInt(p2[0]), Integer.parseInt(p2[1]));
            mclose.setVisibility(View.VISIBLE);

        }

        //currentHour(tvTime);

        seekBar.setIndicatorTextDecimalFormat("0");
        hourseekBar.setIndicatorTextDecimalFormat("0");
        weeklyseekBar.setIndicatorTextDecimalFormat("0");
        monthlyseekBar.setIndicatorTextDecimalFormat("0");
        dailyseekBar.setIndicatorTextDecimalFormat("0");

        tvShortStay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tvShortStay.isSelected()==true){
                    seekBar.setProgress(0,0);
                    tvShortStay.setBackgroundResource(R.drawable.border_button_add_property);
                    tvShortStay.setSelected(false);
                    sclose.setVisibility(View.GONE);
                    shortStatyPrice="";
                }else {
                    tvShortStay.setBackgroundResource(R.drawable.add_property_type_bg);
                    tvShortStay.setSelected(true);
                }
            }
        });
        tvHr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tvHr.isSelected()==true){

                    hourseekBar.setProgress(0,0);
                    tvHr.setBackgroundResource(R.drawable.border_button_add_property);
                    tvHr.setSelected(false);
                    hclose.setVisibility(View.GONE);
                    shortHourlyPrice="";
                }else {
                    tvHr.setBackgroundResource(R.drawable.add_property_type_bg);
                    tvHr.setSelected(true);
                }
            }
        });
        tvDl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tvDl.isSelected()==true){

                    dailyseekBar.setProgress(0,0);
                    tvDl.setBackgroundResource(R.drawable.border_button_add_property);
                    tvDl.setSelected(false);
                    dclose.setVisibility(View.GONE);
                    shortDailyPrice="";


                }else {
                    tvDl.setBackgroundResource(R.drawable.add_property_type_bg);
                    tvDl.setSelected(true);
                }
            }
        });
        tvWl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(tvWl.isSelected()==true){

                    weeklyseekBar.setProgress(0,0);
                    tvWl.setBackgroundResource(R.drawable.border_button_add_property);
                    tvWl.setSelected(false);
                    wclose.setVisibility(View.GONE);
                    shortWeeklyPrice="";

                }else {
                    tvWl.setBackgroundResource(R.drawable.add_property_type_bg);
                    tvWl.setSelected(true);
                }
            }
        });
        tvM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tvM.isSelected()==true){

                    monthlyseekBar.setProgress(0,0);
                    tvM.setBackgroundResource(R.drawable.border_button_add_property);
                    tvM.setSelected(false);
                    mclose.setVisibility(View.GONE);
                    shortMonthlyPrice="";


                }else {
                    tvM.setBackgroundResource(R.drawable.add_property_type_bg);
                    tvM.setSelected(true);
                }
            }
        });

        btnclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fDate = "";
                fTime = "";
                yourLocation="0";
                searchLocation="0";
                seekBar.setProgress(0, 0);
                hourseekBar.setProgress(0, 0);
                dailyseekBar.setProgress(0, 0);
                weeklyseekBar.setProgress(0, 0);
                monthlyseekBar.setProgress(0, 0);

                shortStatyPrice="";
                shortHourlyPrice="";
                shortDailyPrice="";
                shortMonthlyPrice="";
                shortWeeklyPrice="";


                sclose.setVisibility(View.GONE);
                hclose.setVisibility(View.GONE);
                dclose.setVisibility(View.GONE);
                wclose.setVisibility(View.GONE);
                mclose.setVisibility(View.GONE);
                countAdd = 0;
                countMin = 0;
                Type = "";
                rating = "";
                Driveway.setSelected(false);
                Grass.setSelected(false);
                Carport.setSelected(false);
                ParkingType.remove("Driveway");
                ParkingType.remove("Grass");
                ParkingType.remove("Carport");

                Driveway.setBackgroundResource(R.drawable.border_button_add_property);
                Grass.setBackgroundResource(R.drawable.border_button_add_property);
                Carport.setBackgroundResource(R.drawable.border_button_add_property);

                Ratings.remove("1");
                Ratings.remove("2");
                Ratings.remove("3");
                Ratings.remove("4");
                Ratings.remove("5");

                tvShortStay.setBackgroundResource(R.drawable.border_button_add_property);
                tvHr.setBackgroundResource(R.drawable.border_button_add_property);
                tvDl.setBackgroundResource(R.drawable.border_button_add_property);
                tvWl.setBackgroundResource(R.drawable.border_button_add_property);
                tvM.setBackgroundResource(R.drawable.border_button_add_property);

                tvyourkm.setText(countAdd + " Km");
                tvsearchkm.setText(countMin + " Km");
                nearByLocation(placeID);
                dialog.dismiss();
            }
        });
        //seekBar.setIndicatorTextStringFormat("%s%");
        seekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                ssLeft.setText("$ " + (int) leftValue + "");
                tvShortStay.setSelected(true);
                //tvRight.setText("$ "+(int)rightValue+"");
                tvShortStay.setBackgroundResource(R.drawable.add_property_type_bg);
                shortStatyPrice = ((int) leftValue + "," + (int) rightValue);
                sclose.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
        });

        hourseekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                hrLow.setText("$ " + (int) leftValue + "");
                //hrHigh.setText("$ "+(int)rightValue+"");
                tvHr.setSelected(true);
                tvHr.setBackgroundResource(R.drawable.add_property_type_bg);
                shortHourlyPrice = ((int) leftValue + "," + (int) rightValue);
                hclose.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
        });

        dailyseekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                dLow.setText("$ " + (int) leftValue + "");
                //dHigh.setText("$ "+(int)rightValue+"");
                tvDl.setSelected(true);
                tvDl.setBackgroundResource(R.drawable.add_property_type_bg);
                shortDailyPrice = ((int) leftValue + "," + (int) rightValue);
                dclose.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
        });

        weeklyseekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                wkLow.setText("$ " + (int) leftValue + "");
                // wkHigh.setText("$ "+(int)rightValue+"");
                tvWl.setSelected(true);
                tvWl.setBackgroundResource(R.drawable.add_property_type_bg);
                shortWeeklyPrice = ((int) leftValue + "," + (int) rightValue);
                wclose.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
        });

        monthlyseekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                mLow.setText("$ " + (int) leftValue + "");
                // mHigh.setText("$ "+(int)rightValue+"");
                tvM.setSelected(true);
                tvM.setBackgroundResource(R.drawable.add_property_type_bg);
                shortMonthlyPrice = ((int) leftValue + "," + (int) rightValue);
                mclose.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
        });

        sclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar.setProgress(0, 0);
                sclose.setVisibility(View.GONE);
                shortStatyPrice="";
                tvShortStay.setBackgroundResource(R.drawable.border_button_add_property);

            }
        });
        hclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hourseekBar.setProgress(0, 0);
                hclose.setVisibility(View.GONE);
                shortHourlyPrice="";
                tvHr.setBackgroundResource(R.drawable.border_button_add_property);
            }
        });
        dclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dailyseekBar.setProgress(0, 0);
                dclose.setVisibility(View.GONE);
                shortDailyPrice="";
                tvDl.setBackgroundResource(R.drawable.border_button_add_property);
            }
        });
        wclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weeklyseekBar.setProgress(0, 0);
                wclose.setVisibility(View.GONE);
                shortWeeklyPrice="";
                tvWl.setBackgroundResource(R.drawable.border_button_add_property);
            }
        });
        mclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthlyseekBar.setProgress(0, 0);
                mclose.setVisibility(View.GONE);
                shortMonthlyPrice="";
                tvM.setBackgroundResource(R.drawable.border_button_add_property);
            }
        });
        tvPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countAdd++;
                tvyourkm.setText(countAdd + " Km");
                yourLocation=countAdd+"";
            }
        });

        tvMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countAdd == 0) {
                    countAdd = 0;
                } else
                    countAdd--;
                tvyourkm.setText(countAdd + " Km");
                yourLocation=countAdd+"";
            }
        });
        tvAdd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countMin++;
                tvsearchkm.setText(countMin + " Km");
                searchLocation=countMin+"";
            }
        });

        tvMin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countMin == 0) {
                    countMin = 0;
                } else
                    countMin--;
                tvsearchkm.setText(countMin + " Km");
                searchLocation=countMin+"";
            }
        });
        //tvDate.setText(Constants.converDatePDF(day + "/" + (month + 1) + "/" + year));
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(tvDate);
            }
        });
        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimePickerDialog(ParkerDashboardScreen.this, tvTime);
            }
        });
        btnApply.setOnClickListener(v12 -> {


            if (ParkingType.size() > 0) {
                Type = android.text.TextUtils.join(",", ParkingType);
            } else {
                Type = "";
            }
            if (Ratings.size() > 0) {
                rating = android.text.TextUtils.join(",", Ratings);
            } else {
                rating = "";
            }
            nearByLocation(placeID);
            //countAdd=0;
            dialog.dismiss();

        });
        Driveway.setOnClickListener(v13 ->
                {
                    if (!Driveway.isSelected()) {
                        Driveway.setSelected(true);
                        ParkingType.add("Driveway");
                        Driveway.setBackgroundResource(R.drawable.add_property_type_bg);
                    } else {
                        Driveway.setSelected(false);
                        ParkingType.remove("Driveway");
                        Driveway.setBackgroundResource(R.drawable.border_button_add_property);
                    }
                }

        );
        Grass.setOnClickListener(v14 ->
                {
                    if (!Grass.isSelected()) {
                        Grass.setSelected(true);
                        ParkingType.add("Grass");
                        Grass.setBackgroundResource(R.drawable.add_property_type_bg);
                    } else {
                        Grass.setSelected(false);
                        ParkingType.remove("Grass");
                        Grass.setBackgroundResource(R.drawable.border_button_add_property);
                    }
                }
        );
        Carport.setOnClickListener(v15 ->
                {
                    if (!Carport.isSelected()) {
                        Carport.setSelected(true);
                        ParkingType.add("Carport");
                        Carport.setBackgroundResource(R.drawable.add_property_type_bg);
                    } else {
                        Carport.setSelected(false);
                        ParkingType.remove("Carport");
                        Carport.setBackgroundResource(R.drawable.border_button_add_property);
                    }
                }
        );


        one_star.setOnClickListener(v16 -> {
            if (!one_star.isSelected()) {
                one_star.setSelected(true);
                Ratings.add("1");
                one_star.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_star_orange, 0, 0, 0);
            } else {
                one_star.setSelected(false);
                Ratings.remove("1");
                one_star.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_star_black, 0, 0, 0);
            }
        });
        two_star.setOnClickListener(v17 -> {
            if (!two_star.isSelected()) {
                two_star.setSelected(true);
                Ratings.add("2");
                two_star.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_star_orange, 0, 0, 0);
            } else {
                two_star.setSelected(false);
                Ratings.remove("2");
                two_star.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_star_black, 0, 0, 0);
            }
        });
        three_star.setOnClickListener(v18 -> {
            if (!three_star.isSelected()) {
                three_star.setSelected(true);
                Ratings.add("3");
                three_star.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_star_orange, 0, 0, 0);
            } else {
                three_star.setSelected(false);
                Ratings.remove("3");
                three_star.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_star_black, 0, 0, 0);
            }
        });
        four_star.setOnClickListener(v19 -> {
            if (!four_star.isSelected()) {
                four_star.setSelected(true);
                Ratings.add("4");
                four_star.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_star_orange, 0, 0, 0);
            } else {
                four_star.setSelected(false);
                Ratings.remove("4");
                four_star.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_star_black, 0, 0, 0);
            }
        });
        five_star.setOnClickListener(v110 -> {
            if (!five_star.isSelected()) {
                five_star.setSelected(true);
                Ratings.add("5");
                five_star.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_star_orange, 0, 0, 0);
            } else {
                five_star.setSelected(false);
                Ratings.remove("5");
                five_star.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_star_black, 0, 0, 0);
            }
        });
        close.setOnClickListener(v1 -> {
            dialog.dismiss();
            //countAdd = 0;
        });

        dialog.show();
    }

    private void openDatePicker(TextView tv) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(ParkerDashboardScreen.this, R.style.MyAppTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {


                if(!fTime.isEmpty()){
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");

                    if(compareTimes(Constants.converDatePDF(dayOfMonth + "/" + (month + 1) + "/" + year)+" "+fTime,sdf.format(c.getTime())).equalsIgnoreCase("Time is after Date2")){
                        tv.setText(Constants.converDatePDF(dayOfMonth + "/" + (month + 1) + "/" + year));
                        fDate = Constants.converDatePDF(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }else{
                        Utility.showAlert(ParkerDashboardScreen.this,"You cannot select past time");
                        tv.setText("");
                        fDate="";
                        fTime="";
                        tvTime.setText("");

                    }

                }else{
                    tv.setText(Constants.converDatePDF(dayOfMonth + "/" + (month + 1) + "/" + year));
                    fDate = Constants.converDatePDF(dayOfMonth + "/" + (month + 1) + "/" + year);
                }



            }
        }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    public void currentHour(TextView tv) {
        int hourOfDay, mMinutes, mSeconds;
        final Calendar c = Calendar.getInstance();
        hourOfDay = c.get(Calendar.HOUR);
        mMinutes = c.get(Calendar.MINUTE);
        mSeconds = c.get(Calendar.SECOND);

        String format = "";
        if (hourOfDay == 0) {
            hourOfDay += 12;
            format = " AM";
        } else if (hourOfDay == 12) {
            format = " PM";
        } else if (hourOfDay > 11) {
            hourOfDay -= 12;
            if (hourOfDay == 0) {
                hourOfDay = 12;
            }
            format = " PM";
        } else {
            format = " AM";
        }

        tv.setText((String.format("%02d:%02d", hourOfDay, mMinutes)) + format);
    }

    public void openTimePickerDialog(Activity act, TextView tv) {
        int mHour, mMinutes, mSeconds;
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR);
        mMinutes = c.get(Calendar.MINUTE);
        mSeconds = c.get(Calendar.SECOND);


        TimePickerDialog timePickerDialog = new TimePickerDialog(act, R.style.MyAppTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {


                String format = "";
                if (hourOfDay == 0) {

                    hourOfDay += 12;

                    format = " AM";
                } else if (hourOfDay == 12) {

                    format = " PM";

                } else if (hourOfDay > 11) {

                    hourOfDay -= 12;
                    if (hourOfDay == 0) {
                        hourOfDay = 12;
                    }

                    format = " PM";

                } else {

                    format = " AM";
                }


                if(tvDate.getText().length()==0) {
                    tvDate.setText(Constants.converDatePDF(day + "/" + (month + 1) + "/" + year));
                    fDate=Constants.converDatePDF(day + "/" + (month + 1) + "/" + year);
                }else{
                    int hour = hourOfDay;
                    int min = minute;
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
                    //Datetime = sdf.format(c.getTime());

                    System.out.println(compareTimes(fDate+" "+(String.format("%02d:%02d", hourOfDay, minute)+format),sdf.format(c.getTime())));

                    if(compareTimes(fDate+" "+(String.format("%02d:%02d", hourOfDay, minute)+format),sdf.format(c.getTime())).equalsIgnoreCase("Time is after Date2")){
                        tv.setText((String.format("%02d:%02d", hourOfDay, minute)) + format);
                        fTime = (String.format("%02d:%02d", hourOfDay, minute)) + format;
                    }else{
                        Utility.showAlert(ParkerDashboardScreen.this,"You cannot select past time");
                        tv.setText("");
                        fTime="";
                    }
                }
            }
        }, mHour, mMinutes, false);
        timePickerDialog.setTitle("Select time");

        timePickerDialog.show();

    }

    public static String compareTimes(String d1, String d2) {
        String data = "";
        try {
            // If you already have date objects then skip 1

            //1
            // Create 2 dates starts
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
            Date date1 = sdf.parse(d1);
            Date date2 = sdf.parse(d2);

            System.out.println("DATE 1 = "+d1);
            System.out.println("DATE 2 = "+d2);
            System.out.println("DATE 1 = "+sdf.parse((d1)));
            System.out.println("DATE 2 = "+sdf.parse((d2)));

            // Create 2 dates ends
            //1

            // Date object is having 3 methods namely after,before and equals for comparing
            // after() will return true if and only if date1 is after date 2
            if (date1.after(date2)) {
                data = "Time is after Date2";
            }
            // before() will return true if and only if date1 is before date2
            if (date1.before(date2)) {
                data = "Time is before Date2";
            }

            //equals() returns true if both the dates are equal
            if (date1.equals(date2)) {
                data = "Time is equal Date2";
            }


        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        hammer = findViewById(R.id.hammer);
        rvCars = findViewById(R.id.rvCars);
        btnFilter = findViewById(R.id.btnFilter);
        btnSwitch = findViewById(R.id.btnSwitch);
        lvLocation = findViewById(R.id.lvLocation);
        atvPlaces = findViewById(R.id.edtSearch);
        btnMap = findViewById(R.id.btnMap);
        atvPlaces.setThreshold(1);

        transparentStatusBar();
        parkerSearchPropertyAdapter = new ParkerSearchPropertyAdapter(ParkerDashboardScreen.this, searchPropertyModels);
        rvCars.setLayoutManager(new LinearLayoutManager(ParkerDashboardScreen.this, LinearLayoutManager.HORIZONTAL, false));
        rvCars.setAdapter(parkerSearchPropertyAdapter);

        placesAutocompleteAdapter = new GooglePlacesAutocompleteAdapter(this, R.layout.location_address);
        lvLocation.setAdapter(placesAutocompleteAdapter);


        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(appPreferences.getString("lat")) && !TextUtils.isEmpty(appPreferences.getString("lng")))
                {
                    LatLng latLng = new LatLng(Double.parseDouble(appPreferences.getString("lat")), Double.parseDouble(appPreferences.getString("lng")));
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                    mCurrLocationMarker = mMap.addMarker(markerOptions);

                    latitude = Double.parseDouble(appPreferences.getString("lat"));
                    longitude = Double.parseDouble(appPreferences.getString("lng"));

                    //move map camera
                    // mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng),);
                    //mMap.animateCamera(CameraUpdateFactory.zoomTo(14f));

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(Double.parseDouble(appPreferences.getString("lat")), Double.parseDouble(appPreferences.getString("lng"))))
                            .zoom(14F)
                            .build();
                    mMap.setMaxZoomPreference(100f);
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                }
            }
        });
        atvPlaces.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (atvPlaces.getText().toString().length() > 0)
                    nearByLocation(placeID);

            }
            return false;
        });

        atvPlaces.setOnTouchListener((v, event) -> {
            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (atvPlaces.getText().length() > 0) {
                    if (event.getRawX() >= (atvPlaces.getRight() - atvPlaces.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        atvPlaces.setText("");
                        placeID = "";
                        atvPlaces.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.drawable_right_icon_search, 0);
                        KeyBoardHandling.hideSoftKeyboard(ParkerDashboardScreen.this);
                        placesAutocompleteAdapter.getFilter().filter(atvPlaces.getText().toString());
                        return true;
                    }
                }
            }
            return false;
        });
        atvPlaces.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    atvPlaces.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.drawable_red_close, 0);
                    if (atvPlaces.getText().length() > 0) {
                        placesAutocompleteAdapter.getFilter().filter(s.toString());
                    }
                    lvLocation.setVisibility(View.VISIBLE);
                } else {
                    lvLocation.setVisibility(View.GONE);
                    atvPlaces.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.drawable_right_icon_search, 0);
                    placeID = "";
                    placeID = atvPlaces.getText().toString();
                    ParkerDashboardScreen.placeIDD = atvPlaces.getText().toString();
                    if (parkerSearchPropertyAdapter != null) {
                        parkerSearchPropertyAdapter.TEXT = "";
                        parkerSearchPropertyAdapter.PLACEID = "";
                    }

                    if (appPreferences.getString("lat") != null && appPreferences.getString("lng") != null && !appPreferences.getString("lat").isEmpty() && !appPreferences.getString("lng").isEmpty()) {
                        LatLng latLng = new LatLng(Double.parseDouble(appPreferences.getString("lat")), Double.parseDouble(appPreferences.getString("lng")));
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                        if(mCurrLocationMarker!=null)
                        mCurrLocationMarker = mMap.addMarker(markerOptions);

                        latitude = Double.parseDouble(appPreferences.getString("lat"));
                        longitude = Double.parseDouble(appPreferences.getString("lng"));

                        //move map camera
                        // mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng),);
                        //mMap.animateCamera(CameraUpdateFactory.zoomTo(14f));

                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(Double.parseDouble(appPreferences.getString("lat")), Double.parseDouble(appPreferences.getString("lng"))))
                                .zoom(14F)
                                .build();
                        mMap.setMaxZoomPreference(100f);
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    }
                    nearByLocation(placeID);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        hammer.setOnClickListener(v -> {
            KeyBoardHandling.hideSoftKeyboard(ParkerDashboardScreen.this);
            openDrawer();
        });
        btnFilter.setOnClickListener(v -> {

            filterDialog();

            //filterDialogTwo();
        });
        btnSwitch.setOnClickListener(v -> {
            //if (searchPropertyModels.size() > 0) {
            startActivity(new Intent(this, ParkerDashboardScreenTwo.class).putExtra("alldata", searchPropertyModels));
//            } else {
//                Utility.showAlert(this, "No Property Found yet.");
//            }

        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (parkerSearchPropertyAdapter != null) {
            // parkerSearchPropertyAdapter.TEXT="";
            //parkerSearchPropertyAdapter.PLACEID="";
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // mMap.clear();
        //Place current location marker
        if (appPreferences.getString("lat") != null && appPreferences.getString("lng") != null && !appPreferences.getString("lat").isEmpty() && !appPreferences.getString("lng").isEmpty()) {
            LatLng latLng = new LatLng(Double.parseDouble(appPreferences.getString("lat")), Double.parseDouble(appPreferences.getString("lng")));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            mCurrLocationMarker = mMap.addMarker(markerOptions);

            latitude = Double.parseDouble(appPreferences.getString("lat"));
            longitude = Double.parseDouble(appPreferences.getString("lng"));

            //move map camera
            // mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng),);
            //mMap.animateCamera(CameraUpdateFactory.zoomTo(14f));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(Double.parseDouble(appPreferences.getString("lat")), Double.parseDouble(appPreferences.getString("lng"))))
                    .zoom(14F)
                    .build();
            mMap.setMaxZoomPreference(100f);
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                //mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            // mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }


    public ArrayList<PlaceDetails> autocomplete(String input) {
        ArrayList<PlaceDetails> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            System.out.println("URL=====>" + sb.toString());
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
            in.close();
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();

            }

        }
        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            System.out.println("SEARCH RESULT====" + jsonObj.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                PlaceDetails details = new PlaceDetails();
                details.PlaceDescription = predsJsonArray.getJSONObject(i).getString("description") != null ? predsJsonArray.getJSONObject(i).getString("description") : "";
                details.PLaceID = predsJsonArray.getJSONObject(i).getString("place_id") != null ? predsJsonArray.getJSONObject(i).getString("place_id") : "";
                details.PlaceMainText = predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting").getString("main_text") != null ? predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting").getString("main_text") : "";
                //details.PlaceSecondaryText=predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting").getString("secondary_text")!=null?predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting").getString("secondary_text"):"";

//                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
//                System.out.println("============================================================");
                resultList.add(details);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

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
    public void onLocationChanged(Location location) {

        mLastLocation = location;

        appPreferences.set("lat", location.getLatitude() + "");
        appPreferences.set("lng", location.getLongitude() + "");

        latitude = Double.parseDouble(appPreferences.getString("lat"));
        longitude = Double.parseDouble(appPreferences.getString("lng"));

        mMap.clear();
        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        mCurrLocationMarker = mMap.addMarker(markerOptions);

        latitude = Double.parseDouble(appPreferences.getString("lat"));
        longitude = Double.parseDouble(appPreferences.getString("lng"));


        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(14F)
                .build();
        mMap.setMaxZoomPreference(100f);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        nearByLocation(placeID);
        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    private void showPropertyPins() {
        mMap.clear();

        for (SearchPropertyModel model : searchPropertyModels) {
            if (!TextUtils.isEmpty(model.propertyLatitude) && !TextUtils.isEmpty(model.propertyLongitude)) {
                LatLng l = new LatLng(Double.parseDouble(model.propertyLatitude), Double.parseDouble(model.propertyLongitude));
                MarkerOptions m = new MarkerOptions();
                m.title(model.propertyID);
                m.position(l);
                m.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                mMap.addMarker(m);

            }
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (searchPropertyModels != null && searchPropertyModels.size() > 0) {
                    for (int i = 0; i < searchPropertyModels.size(); i++) {
                        if (searchPropertyModels.get(i).propertyID.equalsIgnoreCase(marker.getTitle())) {
                            rvCars.scrollToPosition(i);
                            break;
                        }
                    }
                }
                return true;
            }
        });


        if (atvPlaces.getText().toString().length() > 0) {
            LatLng getLaLongFromAddress = Helper.getLocationFromAddress(ParkerDashboardScreen.this, atvPlaces.getText().toString());
            double lat = 0;
            double lng = 0;
            try {
                if (getLaLongFromAddress != null) {
                    lat = getLaLongFromAddress.latitude;
                    lng = getLaLongFromAddress.longitude;

                    LatLng latLng = new LatLng(lat, lng);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    mMap.addMarker(markerOptions);

                    if (searchPropertyModels.size() > 0) {
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(lat, lng))
                                .zoom(11F)
                                .build();
                        mMap.setMaxZoomPreference(100f);
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                }
            } catch (Exception ex) {

            }
        } else {
            try {
                LatLng latLng = new LatLng(Double.parseDouble(appPreferences.getString("lat")), Double.parseDouble(appPreferences.getString("lng")));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                mMap.addMarker(markerOptions);

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(11F)
                        .build();
                mMap.setMaxZoomPreference(100f);
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            } catch (Exception ex) {

            }
        }


    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter<PlaceDetails> implements Filterable {


        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, R.layout.location_address);
        }

        @Override
        public int getCount() {
            return resultList != null ? resultList.size() : 0;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View v = convertView;
            ViewHolder holder;
            if (v == null) {
                v = LayoutInflater.from(getContext()).inflate(R.layout.location_address, parent, false);
                holder = new ViewHolder(v);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }
            if (resultList != null) {
                if (resultList.size() > 0) {
                    holder.tv.setText(resultList.get(position).PlaceDescription);
                    holder.tv2.setText(resultList.get(position).PlaceMainText);
                    holder.lvItem.setOnClickListener(v1 -> {


                        System.out.println("SIZE OF LIST===>" + resultList.size());
                        //Toast.makeText(getContext(), resultList.get(position).PLaceID, Toast.LENGTH_SHORT).show();
                        atvPlaces.dismissDropDown();
                        KeyBoardHandling.hideSoftKeyboard(ParkerDashboardScreen.this);
                        if (resultList.size() > 0 && parkerSearchPropertyAdapter != null) {
                            try {
                                atvPlaces.setText(resultList.get(position).PlaceDescription);
                                nearByLocation(resultList.get(position).PLaceID);

                                parkerSearchPropertyAdapter.PLACEID = resultList.get(position).PLaceID;
                                parkerSearchPropertyAdapter.TEXT = resultList.get(position).PlaceDescription;

                                ParkerDashboardScreen.placeIDD = resultList.get(position).PLaceID;
                                ParkerDashboardScreen.placeLocation = resultList.get(position).PlaceDescription;
                            } catch (IndexOutOfBoundsException ex) {
                                if (resultList.size() == 1) {
                                    parkerSearchPropertyAdapter.PLACEID = resultList.get(0).PLaceID;
                                    parkerSearchPropertyAdapter.TEXT = resultList.get(0).PlaceDescription;
                                }
                            } catch (Exception ex) {
                                if (resultList.size() == 1) {
                                    parkerSearchPropertyAdapter.PLACEID = resultList.get(0).PLaceID;
                                    parkerSearchPropertyAdapter.TEXT = resultList.get(0).PlaceDescription;
                                }
                            }

                        }
                    });
                }
            }
            return v;
        }

        @Override
        public PlaceDetails getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.

                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }

        class ViewHolder {
            TTextView tv, tv2;
            LinearLayout lvItem;

            public ViewHolder(View v) {
                tv = v.findViewById(R.id.autocompleteText);
                tv2 = v.findViewById(R.id.location_two);
                lvItem = v.findViewById(R.id.lvItem);
            }
        }
    }


    public Callback<JsonObject> callback = new Callback<JsonObject>() {
        @Override
        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
            // hideDialog();
            Utility.hideProgress();
            KeyBoardHandling.hideSoftKeyboard(ParkerDashboardScreen.this);
            if (response.isSuccessful()) {
                try {
                    if (response.body() != null) {
                        searchPropertyModels.clear();
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        if (jsonObject != null) {
                            if (jsonObject.getString("error_code").equalsIgnoreCase("0")) {
                                JSONArray array = jsonObject.getJSONArray("data");
                                if (array != null) {
                                    for (int i = 0; i < array.length(); i++) {
                                        SearchPropertyModel model = new SearchPropertyModel();
                                        model.propertyID = array.getJSONObject(i).optString("id") != null ? array.getJSONObject(i).optString("id") : "0";
                                        model.propertyTitle = array.getJSONObject(i).optString("title") != null ? array.getJSONObject(i).optString("title") : "";
                                        model.propertyAddress = array.getJSONObject(i).optString("address") != null ? array.getJSONObject(i).optString("address") : "";
                                        model.propertyWidth = array.getJSONObject(i).optString("area1") != null ? array.getJSONObject(i).optString("area1") : "";
                                        model.propertyHeight = array.getJSONObject(i).optString("area2") != null ? array.getJSONObject(i).optString("area2") : "";
                                        model.propertyMaxCar = array.getJSONObject(i).optString("max_car") != null ? array.getJSONObject(i).optString("max_car") : "";
                                        model.propertyAbout = array.getJSONObject(i).optString("about") != null ? array.getJSONObject(i).optString("about") : "";
                                        model.propertyParkingType = array.getJSONObject(i).optString("parking_type") != null ? array.getJSONObject(i).optString("parking_type") : "";
                                        model.propertyImage = array.getJSONObject(i).optString("img_1") != null ? array.getJSONObject(i).optString("img_1") : "";
                                        model.propertyImageTwo = array.getJSONObject(i).optString("img_2") != null ? array.getJSONObject(i).optString("img_2") : "";
                                        model.propertySunday = array.getJSONObject(i).optString("sunday") != null ? array.getJSONObject(i).optString("sunday") : "";
                                        model.propertyMonday = array.getJSONObject(i).optString("monday") != null ? array.getJSONObject(i).optString("monday") : "";
                                        model.propertyTuesday = array.getJSONObject(i).optString("tuesday") != null ? array.getJSONObject(i).optString("tuesday") : "";
                                        model.propertyWednesday = array.getJSONObject(i).optString("wednesday") != null ? array.getJSONObject(i).optString("wednesday") : "";
                                        model.propertyThursday = array.getJSONObject(i).optString("thursday") != null ? array.getJSONObject(i).optString("thursday") : "";
                                        model.propertyFriday = array.getJSONObject(i).optString("friday") != null ? array.getJSONObject(i).optString("friday") : "";
                                        model.propertySaturday = array.getJSONObject(i).optString("saturday") != null ? array.getJSONObject(i).optString("saturday") : "";
                                        model.propertyLatitude = array.getJSONObject(i).optString("lat") != null ? array.getJSONObject(i).optString("lat") : "";
                                        model.propertyLongitude = array.getJSONObject(i).optString("lng") != null ? array.getJSONObject(i).optString("lng") : "";
                                        model.propertyDistance = array.getJSONObject(i).optString("distance") != null ? array.getJSONObject(i).optString("distance") : "";
                                        model.propertyAvailability = array.getJSONObject(i).optString("available") != null ? array.getJSONObject(i).optString("available") : "";
                                        model.propertyRating = array.getJSONObject(i).optString("rating") != null ? array.getJSONObject(i).optString("rating") : "";
                                        model.propertyDuration = array.getJSONObject(i).optString("duration") != null ? array.getJSONObject(i).optString("duration") : "";

                                        JSONArray jsonArray = array.optJSONObject(i).optJSONArray("rates");
                                        if (jsonArray != null && jsonArray.length() > 0) {
                                            for (int j = 0; j < jsonArray.length(); j++) {
                                                tblStay stay = new tblStay();
                                                stay.StayMinutes = jsonArray.optJSONObject(j).optString("time");
                                                stay.Stay = jsonArray.optJSONObject(j).optString("title");
                                                stay.StayPrice = Double.parseDouble(jsonArray.optJSONObject(j).getString("price") != null && !jsonArray.optJSONObject(j).optString("price").isEmpty() ? jsonArray.optJSONObject(j).optString("price") : "0.0");
                                                model.list.add(stay);
                                            }
                                        }
                                        JSONObject userArray = array.optJSONObject(i).optJSONObject("owner_user");
                                        if (userArray != null && userArray.length() > 0) {
                                            model.userDetail.UserID = userArray.optString("id") != null ? userArray.optString("id") : "";
                                            model.userDetail.FirstName = userArray.optString("first_name") != null ? userArray.optString("first_name") : "";
                                            model.userDetail.LastName = userArray.optString("last_name") != null ? userArray.optString("last_name") : "";
                                            model.userDetail.FullName = userArray.optString("name") != null ? userArray.optString("name") : "";
                                            model.userDetail.Email = userArray.optString("email") != null ? userArray.optString("email") : "";
                                            model.userDetail.DeviceToken = userArray.optString("device_token") != null ? userArray.optString("device_token") : "";
                                            model.userDetail.ProfileImage = userArray.optString("profile_img") != null ? userArray.optString("profile_img") : "";
                                            model.userDetail.ZipCode = userArray.optString("zip_code") != null ? userArray.optString("zip_code") : "";
                                            model.userDetail.Gender = userArray.optString("gender") != null ? userArray.optString("gender") : "";
                                            model.userDetail.UserType = userArray.optString("user_type") != null ? userArray.optString("user_type") : "";
                                            model.userDetail.APIToken = userArray.optString("fcm_id") != null ? userArray.optString("fcm_id") : "";
                                        }
                                        searchPropertyModels.add(model);
                                    }
                                } else {
                                    Utility.showAlert(ParkerDashboardScreen.this, jsonObject.getString("error_message").toString());
                                }

                                lvLocation.setVisibility(View.GONE);
                                parkerSearchPropertyAdapter.notifyDataSetChanged();
                                rvCars.setVisibility(searchPropertyModels.size() > 0 ? View.VISIBLE : View.GONE);
                                showPropertyPins();


                            } else {
                                parkerSearchPropertyAdapter.notifyDataSetChanged();
                                rvCars.setVisibility(searchPropertyModels.size() > 0 ? View.VISIBLE : View.GONE);
                                showPropertyPins();
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Utility.showAlert(ParkerDashboardScreen.this, e.toString());
                } finally {
                    Utility.hideProgress();
                }
            }

        }

        @Override
        public void onFailure(Call<JsonObject> call, Throwable t) {
            //hideDialog();
            Utility.hideProgress();
            Utility.showAlert(ParkerDashboardScreen.this, t.toString());
        }
    };


    private void nearByLocation(String PlaceID) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!Utility.isNetworkAvailable(ParkerDashboardScreen.this)) {
                        Utility.showAlert(ParkerDashboardScreen.this, "please check internet connection");
                    } else {

                        placeID = PlaceID;
                        Call<JsonObject> responseBodyCall = apiInterface.getNearByProperty(WebUtility.NEAR_BY_PROPERTY,
                                 Type, placeID, rating,
                                latitude + "",
                                longitude + "", fDate.isEmpty() ? null : fDate, fTime.isEmpty() ? null : fTime,
                                shortStatyPrice.isEmpty() ? null : shortStatyPrice, shortHourlyPrice.isEmpty() ? null : shortHourlyPrice, shortDailyPrice.isEmpty() ? null : shortDailyPrice, shortWeeklyPrice.isEmpty() ? null : shortWeeklyPrice, shortMonthlyPrice.isEmpty() ? null : shortMonthlyPrice,
                                yourLocation.equalsIgnoreCase("0")?"0":yourLocation,searchLocation.equalsIgnoreCase("0")?"0":searchLocation
                        );

                        Utility.showProgress(ParkerDashboardScreen.this);

                        //showDialog();
                        responseBodyCall.enqueue(callback);

                    }
                } catch (Exception ex) {
                    Utility.hideProgress();
                }
            }
        });

    }
//    appPreferences.getString("USERID")

    private void getMaxPrice() {
        Call<JsonObject> responseBodyCall = apiInterface.getMaxPrice(WebUtility.GET_MAX_PRICE);
        Utility.showProgress(ParkerDashboardScreen.this);
        responseBodyCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Utility.hideProgress();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.body().toString());
                    int error_code = jsonObject.optInt("error_code");
                    if (error_code == 0) {
                        JSONArray array = jsonObject.optJSONArray("data");
                        if (array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {
                                String title = array.optJSONObject(i).optString("title");
                                if (title.equalsIgnoreCase("shorty_stay")) {
                                    tvRight.setText("$" + array.optJSONObject(i).getInt("maxvalue") + "");
                                    seekBar.setRange(0, array.optJSONObject(i).getInt("maxvalue"));
                                } else if (title.equalsIgnoreCase("hourly_stay")) {
                                    hrHigh.setText("$" + array.optJSONObject(i).getInt("maxvalue") + "");
                                    hourseekBar.setRange(0, array.optJSONObject(i).getInt("maxvalue"));
                                } else if (title.equalsIgnoreCase("daily_stay")) {
                                    dHigh.setText("$" + array.optJSONObject(i).getInt("maxvalue") + "");
                                    dailyseekBar.setRange(0, array.optJSONObject(i).getInt("maxvalue"));
                                } else if (title.equalsIgnoreCase("weekly_stay")) {
                                    wkHigh.setText("$" + array.optJSONObject(i).getInt("maxvalue") + "");
                                    weeklyseekBar.setRange(0, array.optJSONObject(i).getInt("maxvalue"));
                                } else if (title.equalsIgnoreCase("monthly_stay")) {
                                    mHigh.setText("$" + array.optJSONObject(i).getInt("maxvalue") + "");
                                    monthlyseekBar.setRange(0, array.optJSONObject(i).getInt("maxvalue"));
                                }
                            }
                        }
                        if (!shortStatyPrice.isEmpty()) {
                            String[] p1=shortStatyPrice.split(",");
                            seekBar.setProgress(Integer.parseInt(p1[0]), Integer.parseInt(p1[1]));
                            sclose.setVisibility(View.VISIBLE);
                        }

                        if (!shortHourlyPrice.isEmpty()) {
                            String[] p2=shortHourlyPrice.split(",");
                            hourseekBar.setProgress(Integer.parseInt(p2[0]), Integer.parseInt(p2[1]));
                            hclose.setVisibility(View.VISIBLE);

                        }
                        if (!shortDailyPrice.isEmpty()) {
                            String[] p2=shortDailyPrice.split(",");
                            dailyseekBar.setProgress(Integer.parseInt(p2[0]), Integer.parseInt(p2[1]));
                            dclose.setVisibility(View.VISIBLE);

                        }
                        if (!shortWeeklyPrice.isEmpty()) {
                            String[] p2=shortWeeklyPrice.split(",");
                            weeklyseekBar.setProgress(Integer.parseInt(p2[0]), Integer.parseInt(p2[1]));
                            wclose.setVisibility(View.VISIBLE);

                        }
                        if (!shortMonthlyPrice.isEmpty()) {
                            String[] p2=shortMonthlyPrice.split(",");
                            monthlyseekBar.setProgress(Integer.parseInt(p2[0]), Integer.parseInt(p2[1]));
                            mclose.setVisibility(View.VISIBLE);

                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("MAX PRICE DATA------>" + jsonObject.toString());

//                {"error_code":0,"error_message":"Property min-max price listing.","data":[{"title":"shorty_stay","minvalue":1,"maxvalue":66},{"title":"hourly_stay","minvalue":1,"maxvalue":80},{"title":"daily_stay","minvalue":5,"maxvalue":200},{"title":"weekly_stay","minvalue":5,"maxvalue":700},{"title":"monthly_stay","minvalue":5,"maxvalue":3000}]}

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Utility.hideProgress();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            resultList.clear();
        }
    }
}


//ChIJhemuV9Zq1moRwN2MIXVWBAU&rating