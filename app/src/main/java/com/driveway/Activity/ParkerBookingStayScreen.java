package com.driveway.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import androidx.annotation.UiThread;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.driveway.Activity.OwnerBookingDetail.BookingDetail;
import com.driveway.Activity.OwnerBookingDetail.BookingList;
import com.driveway.Activity.ParkerBooking.BookingChat;
import com.driveway.Activity.ParkerBooking.BookingChat_;
import com.driveway.Adapters.CalendarDayAdapter;
import com.driveway.Adapters.HourBookingAdapter;
import com.driveway.Adapters.ShortBookingAdapter;
import com.driveway.Component.BButton;
import com.driveway.Component.CalendarListener;
import com.driveway.Component.DayDecorator;
import com.driveway.Component.DayView;
import com.driveway.Component.RoundedCornersTransform;
import com.driveway.Component.TTextView;
import com.driveway.Component.TimePickerFragment;
import com.driveway.Component.Utility;
import com.driveway.DBHelper.tblPropertyAvailableTimes;
import com.driveway.Model.HourTimeModel;
import com.driveway.Model.LocationData;
import com.driveway.Model.SearchPropertyModel;
import com.driveway.Model.tblCard;
import com.driveway.R;
import com.driveway.Utility.Constants;
import com.driveway.Utility.LocationProvider;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.driveway.listeners.onBookingListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonObject;
import com.mobandme.ada.Entity;
import com.mobandme.ada.exceptions.AdaFrameworkException;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ParkerBookingStayScreen extends BaseActivity implements View.OnClickListener, LocationProvider.LocationCallback {

    public static TTextView tvShortTimePick, tvDailyTimePick, tvHourTimePick, tvShortEndTimePick, tvDailyEndTime, textViewHourEndTime, tvMonthTimePick, tvMonthEndTime, tvWeeklyTimePick, tvWeekEndTime;
    public static TTextView tvParking, tvShortStay, tvHourStay, tvDailyStay, tvWeeklyStay, tvMonthlyStay, tvPropertyTitle, tvPropertyKM, PropertyAddress, tvMinutes, tvRatings, tvAvailability, UserName;
    public static LinearLayout llShortStay, llHourStay, llMonthlyStay, llWeeklyStay, llDailyStay, llShortStayDateSelect, llHourSelectDate, llDailySelectDate, llWeekselectDate, llMonthselectDate, llHourStayHourSelection;
    public static TTextView tvShortStartDate, tvShortEndDate, tvHourStartDate, tvHourEndDate, tvDailyStartDate, tvDailyEndDate, tvWeeklyStartDate, tvWeeklyEndDate, tvMonthlyEndDate, tvMonthlyStartDate;
    public static TTextView tv15, tv30, tv60, tv1_4, tv5_8, tv8, tvHalfDay, tvFullDay, tv3Days, tv5Days, tv7Days, tv14Days, tv30Days, tv1_hour, tv2_hour, tv3_hour, tv4_hour;
    public static String bookingID = "0";
    public static LinearLayout ll1, ll2, ll3, ll4, ll5;
    public static String shortStayPrice = "0";
    public static String hourStayPrice = "0";
    public static String dailyStayPrice = "0";
    public static String weeklyStayPrice = "0";
    public static String monthlyStayPrice = "0";
    TTextView tvShortStayDetails, tvHourStayDetails, tvDailyStayDetails, tvWeeklyStayDetails, tvMonthlyStayDetails;
    TTextView btnBooking, tvUserType;
    ImageView imgMap;
    SearchPropertyModel parkingSpace;
    LocationData locationData = new LocationData();
    LocationProvider provider;
    com.driveway.Component.CustomCalendarView calendarView, calendarViewHour, calendarViewDaily, calendarViewWeekly, calendarViewMonthly;
    AppCompatImageView back, Report;
    RelativeLayout rlBg;
    BButton btnChat;
    ArrayList<tblPropertyAvailableTimes> sundayList = new ArrayList<>();
    ArrayList<tblPropertyAvailableTimes> mondayList = new ArrayList<>();
    ArrayList<tblPropertyAvailableTimes> tuesdayList = new ArrayList<>();
    ArrayList<tblPropertyAvailableTimes> wednesdayList = new ArrayList<>();
    ArrayList<tblPropertyAvailableTimes> thursdayList = new ArrayList<>();
    ArrayList<tblPropertyAvailableTimes> fridayList = new ArrayList<>();
    ArrayList<tblPropertyAvailableTimes> saturdayList = new ArrayList<>();
    int s1 = 0;
    int s2 = 0;
    int s3 = 0;
    int s4 = 0;
    int s5 = 0;
    HashSet<String> weekdate = new HashSet<>();
    HashSet<String> monthdate = new HashSet<>();
    public DisabledColorDecorator disabledColorDecorator = new DisabledColorDecorator();
    FirebaseDatabase database;
    Bitmap bmp;
    HourBookingAdapter hourbookingAdapter;
    ShortBookingAdapter shortBookingAdapter;
    RecyclerView rvHourTime,rvShortTime,rvDailyTime;
    ArrayList<HourTimeModel> timeModels=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parker_booking_stay);
        bindComponent();
        database = FirebaseDatabase.getInstance();
        provider = new LocationProvider(this, this);
        provider.connect();

        try {
            if (getIntent().getExtras() != null) {
                parkingSpace = (SearchPropertyModel) getIntent().getSerializableExtra("booking_details");
                if (parkingSpace != null) {
                    tvPropertyTitle.setText(parkingSpace.propertyTitle != null ? parkingSpace.propertyTitle : "");
                    tvAvailability.setText(parkingSpace.propertyAvailability != null ? parkingSpace.propertyAvailability + " Available" : "0% Available");
                    tvRatings.setText(parkingSpace.propertyRating != null ? parkingSpace.propertyRating : "");
                    tvPropertyKM.setText(parkingSpace.propertyDistance != null ? parkingSpace.propertyDistance : "");
                    PropertyAddress.setText(parkingSpace.propertyAddress != null ? parkingSpace.propertyAddress : "");
                    tvMinutes.setText(parkingSpace.propertyDuration != null ? parkingSpace.propertyDuration : "");
                    tvParking.setText(parkingSpace.propertyParkingType != null ? parkingSpace.propertyParkingType : "");

                    new Handler().postDelayed(() -> {
                        if (parkingSpace != null && !parkingSpace.propertyImage.isEmpty()) {
                            Picasso.with(this).load(parkingSpace.propertyImage).transform(new RoundedCornersTransform(30, 0)).into(new Target() {

                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    rlBg.setBackground(new BitmapDrawable(getResources(), bitmap));
                                    bmp=bitmap;
                                }

                                @Override
                                public void onBitmapFailed(final Drawable errorDrawable) {
                                    Log.d("TAG", "FAILED");
                                }

                                @Override
                                public void onPrepareLoad(final Drawable placeHolderDrawable) {
                                    Log.d("TAG", "Prepare Load");
                                    //rlBg.setBackground(placeHolderDrawable);
                                }
                            });

                        }
                    }, 20);
                    if(bmp!=null)
                        rlBg.setBackground(new BitmapDrawable(getResources(), bmp));

                    if (parkingSpace.list != null) {
                        for (int i = 0; i < parkingSpace.list.size(); i++) {
                            if (parkingSpace.list.get(i).Stay.equalsIgnoreCase(Constants.SHORT_STAY)) {
                                s1 = 1;
                                if (parkingSpace.list.get(i).StayMinutes.equalsIgnoreCase(Constants._15MIN)) {
                                    llShortStay.setSelected(true);
                                    tv15.setVisibility(View.VISIBLE);
                                    tv15.setText(Html.fromHtml("15 mins<br> $ " + "<b>" + String.format("%.0f", parkingSpace.list.get(i).StayPrice) + "</b>"));

                                    tv15.setTextColor(getResources().getColor(R.color.white));
                                    tv15.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                                    tv30.setTextColor(getResources().getColor(R.color.black));
                                    tv30.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                    tv60.setTextColor(getResources().getColor(R.color.black));
                                    tv60.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                    tv15.setSelected(true);
                                    tv30.setSelected(false);
                                    tv60.setSelected(false);
                                    shortStayPrice = getPrice(Constants.SHORT_STAY, Constants._15MIN);
                                    //openendTimePicker(ParkerBookingStayScreen.this, "shorttime");
                                    setTime("shorttime", tvShortTimePick.getText().toString());

                                    selectedItem(tvShortStay, tvHourStay, tvDailyStay, tvWeeklyStay, tvMonthlyStay);



                                } else if (parkingSpace.list.get(i).StayMinutes.equalsIgnoreCase(Constants._30MIN)) {
                                    tv30.setVisibility(View.VISIBLE);
                                    tv30.setText(Html.fromHtml("30 mins<br> $ <b>" + String.format("%.0f", parkingSpace.list.get(i).StayPrice) + "</b>"));
                                    if (tv15.getVisibility() != View.VISIBLE) {
                                        tv30.setTextColor(getResources().getColor(R.color.white));
                                        tv30.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                                        tv15.setTextColor(getResources().getColor(R.color.black));
                                        tv15.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                        tv60.setTextColor(getResources().getColor(R.color.black));
                                        tv60.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                        tv15.setSelected(false);
                                        tv30.setSelected(true);
                                        tv60.setSelected(false);
                                        shortStayPrice = getPrice(Constants.SHORT_STAY, Constants._30MIN);
                                        setTime("shorttime", tvShortTimePick.getText().toString());
                                        selectedItem(tvShortStay, tvHourStay, tvDailyStay, tvWeeklyStay, tvMonthlyStay);
                                    } else {
                                        tv15.setSelected(true);
                                        setTime("shorttime", tvShortTimePick.getText().toString());
                                    }

                                } else if (parkingSpace.list.get(i).StayMinutes.equalsIgnoreCase(Constants._45MIN)) {
                                    tv60.setVisibility(View.VISIBLE);
                                    tv60.setText(Html.fromHtml("45 mins<br> $ <b>" + String.format("%.0f", parkingSpace.list.get(i).StayPrice) + "<b>"));
                                    if (tv15.getVisibility() != View.VISIBLE && tv30.getVisibility() != View.VISIBLE) {
                                        tv60.setTextColor(getResources().getColor(R.color.white));
                                        tv60.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                                        tv15.setTextColor(getResources().getColor(R.color.black));
                                        tv15.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                        tv30.setTextColor(getResources().getColor(R.color.black));
                                        tv30.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                        tv15.setSelected(false);
                                        tv30.setSelected(false);
                                        tv60.setSelected(true);
                                        shortStayPrice = getPrice(Constants.SHORT_STAY, Constants._45MIN);
                                        setTime("shorttime", tvShortTimePick.getText().toString());
                                        selectedItem(tvShortStay, tvHourStay, tvDailyStay, tvWeeklyStay, tvMonthlyStay);
                                    } else {
                                        tv15.setSelected(true);
                                        setTime("shorttime", tvShortTimePick.getText().toString());
                                    }

                                }
                            } else if (parkingSpace.list.get(i).Stay.equalsIgnoreCase(Constants.HOUR_STAY)) {
                                s2 = 1;
                                if (parkingSpace.list.get(i).StayMinutes.equalsIgnoreCase(Constants._1_4_HOUR))
                                {

                                    if (!llShortStay.isSelected()) {
                                        llHourStay.setSelected(true);
                                    }
                                    tv1_4.setVisibility(View.VISIBLE);
                                    tv1_4.setText(Html.fromHtml("1-4 hours<br> $ " + "<b>" + String.format("%.0f", parkingSpace.list.get(i).StayPrice) + "/hr</b>"));

                                    tv1_4.setTextColor(getResources().getColor(R.color.white));
                                    tv1_4.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                                    tv5_8.setTextColor(getResources().getColor(R.color.black));
                                    tv5_8.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                    tv8.setTextColor(getResources().getColor(R.color.black));
                                    tv8.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                    tv1_4.setSelected(true);
                                    tv5_8.setSelected(false);
                                    tv8.setSelected(false);

                                    hourStayPrice = getPrice(Constants.HOUR_STAY, Constants._1_4_HOUR);

                                    if (!tvShortStay.isSelected())
                                        selectedItem(tvHourStay, tvShortStay, tvDailyStay, tvWeeklyStay, tvMonthlyStay);

                                    tv1_hour.setTextColor(getResources().getColor(R.color.white));
                                    tv1_hour.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                                    tv2_hour.setTextColor(getResources().getColor(R.color.black));
                                    tv2_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                    tv3_hour.setTextColor(getResources().getColor(R.color.black));
                                    tv3_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                    tv4_hour.setTextColor(getResources().getColor(R.color.black));
                                    tv4_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                    tv1_hour.setSelected(true);
                                    tv2_hour.setSelected(false);
                                    tv3_hour.setSelected(false);
                                    tv4_hour.setSelected(false);

                                    tv1_hour.setText("1");
                                    tv2_hour.setText("2");
                                    tv3_hour.setText("3");
                                    tv4_hour.setText("4");

                                    setTime("hourtime", tvHourTimePick.getText().toString());

                                }
                                else if (parkingSpace.list.get(i).StayMinutes.equalsIgnoreCase(Constants._4_8_HOUR))
                                {
                                    tv5_8.setVisibility(View.VISIBLE);
                                    tv5_8.setText(Html.fromHtml("5-8 hours<br> $ <b>" + String.format("%.0f", parkingSpace.list.get(i).StayPrice) + "/hr</b>"));
                                    if (!llShortStay.isSelected()) {
                                        llHourStay.setSelected(true);
                                    }

                                    if (tv1_4.getVisibility() != View.VISIBLE) {
                                        tv5_8.setTextColor(getResources().getColor(R.color.white));
                                        tv5_8.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                                        tv1_4.setTextColor(getResources().getColor(R.color.black));
                                        tv1_4.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                        tv8.setTextColor(getResources().getColor(R.color.black));
                                        tv8.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                        tv1_4.setSelected(false);
                                        tv5_8.setSelected(true);
                                        tv8.setSelected(false);

                                        hourStayPrice = getPrice(Constants.HOUR_STAY, Constants._4_8_HOUR);

                                        if (!tvShortStay.isSelected())
                                            selectedItem(tvHourStay, tvShortStay, tvDailyStay, tvWeeklyStay, tvMonthlyStay);


                                        tv1_hour.setTextColor(getResources().getColor(R.color.white));
                                        tv1_hour.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                                        tv2_hour.setTextColor(getResources().getColor(R.color.black));
                                        tv2_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                        tv3_hour.setTextColor(getResources().getColor(R.color.black));
                                        tv3_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                        tv4_hour.setTextColor(getResources().getColor(R.color.black));
                                        tv4_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                        tv1_hour.setSelected(true);
                                        tv2_hour.setSelected(false);
                                        tv3_hour.setSelected(false);
                                        tv4_hour.setSelected(false);

                                        tv1_hour.setText("5");
                                        tv2_hour.setText("6");
                                        tv3_hour.setText("7");
                                        tv4_hour.setText("8");

                                        setTime("hourtime", tvHourTimePick.getText().toString());

                                    } else {
                                        tv1_4.setSelected(true);
                                        setTime("hourtime", tvHourTimePick.getText().toString());

                                    }
                                }
                                else if (parkingSpace.list.get(i).StayMinutes.equalsIgnoreCase(Constants._8_PLUS_HOUR)) {
                                    tv8.setVisibility(View.VISIBLE);
                                    tv8.setText(Html.fromHtml("8+ hours<br> $ <b>" + String.format("%.0f", parkingSpace.list.get(i).StayPrice) + "/hr<b>"));
                                    if (!llShortStay.isSelected()) {
                                        llHourStay.setSelected(true);
                                    }
                                    if (tv1_4.getVisibility() != View.VISIBLE && tv5_8.getVisibility() != View.VISIBLE) {
                                        tv8.setTextColor(getResources().getColor(R.color.white));
                                        tv8.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                                        tv1_4.setTextColor(getResources().getColor(R.color.black));
                                        tv1_4.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                        tv5_8.setTextColor(getResources().getColor(R.color.black));
                                        tv5_8.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                        tv1_4.setSelected(false);
                                        tv5_8.setSelected(false);
                                        tv8.setSelected(true);

                                        hourStayPrice = getPrice(Constants.HOUR_STAY, Constants._8_PLUS_HOUR);

                                        if (!tvShortStay.isSelected())
                                            selectedItem(tvHourStay, tvShortStay, tvDailyStay, tvWeeklyStay, tvMonthlyStay);

                                        tv1_hour.setTextColor(getResources().getColor(R.color.white));
                                        tv1_hour.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                                        tv2_hour.setTextColor(getResources().getColor(R.color.black));
                                        tv2_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                        tv3_hour.setTextColor(getResources().getColor(R.color.black));
                                        tv3_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                        tv4_hour.setTextColor(getResources().getColor(R.color.black));
                                        tv4_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                        tv1_hour.setSelected(true);
                                        tv2_hour.setSelected(false);
                                        tv3_hour.setSelected(false);
                                        tv4_hour.setSelected(false);

                                        tv1_hour.setText("9");
                                        tv2_hour.setText("10");
                                        tv3_hour.setText("11");
                                        tv4_hour.setText("12");

                                        setTime("hourtime", tvHourTimePick.getText().toString());

                                    } else {
                                        tv1_4.setSelected(true);
                                        setTime("hourtime", tvHourTimePick.getText().toString());
                                    }
                                }
                            } else if (parkingSpace.list.get(i).Stay.equalsIgnoreCase(Constants.DAY_STAY)) {
                                s3 = 1;
                                if (parkingSpace.list.get(i).StayMinutes.equalsIgnoreCase(Constants._HALFDAY)) {

                                    if (!llShortStay.isSelected() && !llHourStay.isSelected()) {
                                        llDailyStay.setSelected(true);
                                    }
                                    tvHalfDay.setVisibility(View.VISIBLE);
                                    tvHalfDay.setText(Html.fromHtml("Half Day<br> $ " + "<b>" + String.format("%.0f", parkingSpace.list.get(i).StayPrice) + "</b>"));

                                    tvHalfDay.setTextColor(getResources().getColor(R.color.white));
                                    tvHalfDay.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);
                                    tvFullDay.setTextColor(getResources().getColor(R.color.black));
                                    tvFullDay.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                    tvHalfDay.setSelected(true);
                                    tvFullDay.setSelected(false);

                                    dailyStayPrice = getPrice(Constants.DAY_STAY, Constants._HALFDAY);

                                    setTime("dailytime", tvDailyTimePick.getText().toString());
                                    if (!tvShortStay.isSelected() && !tvHourStay.isSelected())
                                        selectedItem(tvDailyStay, tvHourStay, tvShortStay, tvWeeklyStay, tvMonthlyStay);


                                }
                                else if (parkingSpace.list.get(i).StayMinutes.equalsIgnoreCase(Constants._FULLDAY)) {
                                    tvFullDay.setVisibility(View.VISIBLE);
                                    tvFullDay.setText(Html.fromHtml("Full Day<br> $ <b>" + String.format("%.0f", parkingSpace.list.get(i).StayPrice) + "</b>"));

                                    if (!llShortStay.isSelected() && !llHourStay.isSelected()) {
                                        llDailyStay.setSelected(true);
                                    }
                                    if (tvHalfDay.getVisibility() != View.VISIBLE) {

                                        tvFullDay.setTextColor(getResources().getColor(R.color.white));
                                        tvFullDay.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                                        tvHalfDay.setTextColor(getResources().getColor(R.color.black));
                                        tvHalfDay.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                        tvHalfDay.setSelected(false);
                                        tvFullDay.setSelected(true);
                                        dailyStayPrice = getPrice(Constants.DAY_STAY, Constants._FULLDAY);

                                        if (!tvShortStay.isSelected() && !tvHourStay.isSelected())
                                            selectedItem(tvDailyStay, tvHourStay, tvShortStay, tvWeeklyStay, tvMonthlyStay);

                                        setTime("dailytime", tvDailyTimePick.getText().toString());
                                    }else{
                                        ParkerBookingStayScreen.tvHalfDay.setSelected(true);
                                        setTime("dailytime", tvDailyTimePick.getText().toString());
                                    }

                                }

                            } else if (parkingSpace.list.get(i).Stay.equalsIgnoreCase(Constants.WEEK_STAY)) {
                                s4 = 1;
                                if (parkingSpace.list.get(i).StayMinutes.equalsIgnoreCase(Constants._3_DAY)) {

                                    if (!llShortStay.isSelected() && !llHourStay.isSelected() && !llDailyStay.isSelected()) {
                                        llWeeklyStay.setSelected(true);
                                    }
                                    tv3Days.setVisibility(View.VISIBLE);
                                    tv3Days.setText(Html.fromHtml("3 Days<br> $ " + "<b>" + String.format("%.0f", parkingSpace.list.get(i).StayPrice) + "</b>"));

                                    tv3Days.setTextColor(getResources().getColor(R.color.white));
                                    tv3Days.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                                    tv5Days.setTextColor(getResources().getColor(R.color.black));
                                    tv5Days.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                    tv7Days.setTextColor(getResources().getColor(R.color.black));
                                    tv7Days.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                    tv3Days.setSelected(true);
                                    tv5Days.setSelected(false);
                                    tv7Days.setSelected(false);
                                    weeklyStayPrice = getPrice(Constants.WEEK_STAY, Constants._3_DAY);

                                    if (!tvShortStay.isSelected() && !tvHourStay.isSelected() && !tvDailyStay.isSelected())
                                        selectedItem(tvWeeklyStay, tvHourStay, tvShortStay, tvDailyStay, tvMonthlyStay);

                                    if (tv3Days.isSelected())
                                        setCalendar(tvWeeklyStartDate, tvWeeklyEndDate, "3days", DateFormat.format("yyyy-MM-dd", new Date().getTime()).toString());

                                } else if (parkingSpace.list.get(i).StayMinutes.equalsIgnoreCase(Constants._5_DAY)) {
                                    tv5Days.setVisibility(View.VISIBLE);
                                    tv5Days.setText(Html.fromHtml("5 Days<br> $ <b>" + String.format("%.0f", parkingSpace.list.get(i).StayPrice) + "</b>"));
                                    if (!llShortStay.isSelected() && !llHourStay.isSelected() && !llDailyStay.isSelected()) {
                                        llWeeklyStay.setSelected(true);
                                    }

                                    if (tv3Days.getVisibility() != View.VISIBLE) {
                                        tv5Days.setTextColor(getResources().getColor(R.color.white));
                                        tv5Days.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                                        tv3Days.setTextColor(getResources().getColor(R.color.black));
                                        tv3Days.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                        tv7Days.setTextColor(getResources().getColor(R.color.black));
                                        tv7Days.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                        tv5Days.setSelected(true);
                                        tv3Days.setSelected(false);
                                        tv7Days.setSelected(false);
                                        weeklyStayPrice = getPrice(Constants.WEEK_STAY, Constants._5_DAY);

                                        if (!tvShortStay.isSelected() && !tvHourStay.isSelected() && !tvDailyStay.isSelected())
                                            selectedItem(tvWeeklyStay, tvHourStay, tvShortStay, tvDailyStay, tvMonthlyStay);

                                        if (tv5Days.isSelected()) {
                                            setCalendar(tvWeeklyStartDate, tvWeeklyEndDate, "5days", DateFormat.format("yyyy-MM-dd", new Date().getTime()).toString());
                                        }
                                    } else {
                                        tv3Days.setSelected(true);
                                    }

                                } else if (parkingSpace.list.get(i).StayMinutes.equalsIgnoreCase(Constants._7_DAY)) {
                                    tv7Days.setVisibility(View.VISIBLE);
                                    tv7Days.setText(Html.fromHtml("7 Days<br> $ <b>" + String.format("%.0f", parkingSpace.list.get(i).StayPrice) + "<b>"));

                                    if (!llShortStay.isSelected() && !llHourStay.isSelected() && !llDailyStay.isSelected()) {
                                        llWeeklyStay.setSelected(true);
                                    }
                                    if (tv3Days.getVisibility() != View.VISIBLE && tv5Days.getVisibility() != View.VISIBLE) {
                                        tv7Days.setTextColor(getResources().getColor(R.color.white));
                                        tv7Days.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                                        tv3Days.setTextColor(getResources().getColor(R.color.black));
                                        tv3Days.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                        tv5Days.setTextColor(getResources().getColor(R.color.black));
                                        tv5Days.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                        tv7Days.setSelected(true);
                                        tv3Days.setSelected(false);
                                        tv5Days.setSelected(false);
                                        weeklyStayPrice = getPrice(Constants.WEEK_STAY, Constants._7_DAY);

                                        if (!tvShortStay.isSelected() && !tvHourStay.isSelected() && !tvDailyStay.isSelected())
                                            selectedItem(tvWeeklyStay, tvHourStay, tvShortStay, tvDailyStay, tvMonthlyStay);

                                        if (tv7Days.isSelected())
                                            setCalendar(tvWeeklyStartDate, tvWeeklyEndDate, "7days", DateFormat.format("yyyy-MM-dd", new Date().getTime()).toString());
                                    } else {
                                        tv3Days.setSelected(true);
                                    }
                                }
                            } else if (parkingSpace.list.get(i).Stay.equalsIgnoreCase(Constants.MONTH_STAY)) {
                                s5 = 1;
                                if (parkingSpace.list.get(i).StayMinutes.equalsIgnoreCase(Constants._14_DAY)) {
                                    tv14Days.setVisibility(View.VISIBLE);
                                    tv14Days.setText(Html.fromHtml("14 Days<br> $ " + "<b>" + String.format("%.0f", parkingSpace.list.get(i).StayPrice) + "</b>"));


                                    if (!llShortStay.isSelected() && !llHourStay.isSelected() && !llDailyStay.isSelected() && !llWeeklyStay.isSelected()) {
                                        llMonthlyStay.setSelected(true);
                                    }
                                    tv14Days.setTextColor(getResources().getColor(R.color.white));
                                    tv14Days.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);
                                    tv30Days.setTextColor(getResources().getColor(R.color.black));
                                    tv30Days.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                    tv30Days.setSelected(false);
                                    tv14Days.setSelected(true);
                                    monthlyStayPrice = getPrice(Constants.MONTH_STAY, Constants._14_DAY);

                                    if (!tvShortStay.isSelected() && !tvHourStay.isSelected() && !tvDailyStay.isSelected() && !tvWeeklyStay.isSelected())
                                        selectedItem(tvMonthlyStay, tvDailyStay, tvHourStay, tvShortStay, tvWeeklyStay);

                                    if (tv14Days.isSelected())
                                        setCalendar(tvMonthlyStartDate, tvMonthlyEndDate, "14days", DateFormat.format("yyyy-MM-dd", new Date().getTime()).toString());


                                }
                                if (parkingSpace.list.get(i).StayMinutes.equalsIgnoreCase(Constants._30_DAY)) {
                                    tv30Days.setVisibility(View.VISIBLE);
                                    tv30Days.setText(Html.fromHtml("30 Days<br> $ <b>" + String.format("%.0f", parkingSpace.list.get(i).StayPrice) + "</b>"));
                                    if (!llShortStay.isSelected() && !llHourStay.isSelected() && !llDailyStay.isSelected() && !llWeeklyStay.isSelected()) {
                                        llMonthlyStay.setSelected(true);
                                    }
                                    if (tv14Days.getVisibility() != View.VISIBLE) {
                                        tv30Days.setTextColor(getResources().getColor(R.color.white));
                                        tv30Days.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);
                                        tv14Days.setTextColor(getResources().getColor(R.color.black));
                                        tv14Days.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                        tv30Days.setSelected(true);
                                        tv14Days.setSelected(false);
                                        monthlyStayPrice = getPrice(Constants.MONTH_STAY, Constants._30_DAY);
                                        if (!tvShortStay.isSelected() && !tvHourStay.isSelected() && !tvDailyStay.isSelected() && !tvWeeklyStay.isSelected())
                                            selectedItem(tvMonthlyStay, tvDailyStay, tvHourStay, tvShortStay, tvWeeklyStay);

                                        if (tv30Days.isSelected())
                                            setCalendar(tvMonthlyStartDate, tvMonthlyEndDate, "30days", DateFormat.format("yyyy-MM-dd", new Date().getTime()).toString());
                                    } else {
                                        tv14Days.setSelected(true);
                                    }

                                }
                            }
                        }
                        if (s1 == 1) {
                            ll1.setVisibility(View.VISIBLE);
                            tvShortStayDetails.setVisibility(View.GONE);
                            llShortStay.setVisibility(View.VISIBLE);

                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            showShortTimeLog(sdf.format(c.getTime()),parkingSpace.propertyID);


                        } else {
                            ll1.setVisibility(View.GONE);
                            tvShortStayDetails.setVisibility(View.GONE);
                            llShortStay.setVisibility(View.GONE);
                        }
                        if (s2 == 1) {
                            ll2.setVisibility(View.VISIBLE);
                            tvHourStayDetails.setVisibility(View.GONE);
                            if (s1 == 0) {
                                llHourStay.setVisibility(View.VISIBLE);
                                llHourStayHourSelection.setVisibility(View.VISIBLE);
                                tv4_hour.setVisibility(View.VISIBLE);
                                tv3_hour.setVisibility(View.VISIBLE);
                                tv2_hour.setVisibility(View.VISIBLE);
                                tv1_hour.setVisibility(View.VISIBLE);
                                tvHourStayDetails.setVisibility(View.GONE);
                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                showHourTimeLog(sdf.format(c.getTime()),parkingSpace.propertyID);

                            }

                        } else {
                            ll2.setVisibility(View.GONE);
                            tvHourStayDetails.setVisibility(View.GONE);
                        }
                        if (s3 == 1) {
                            ll3.setVisibility(View.VISIBLE);
                            tvDailyStayDetails.setVisibility(View.GONE);
                            if (s1 == 0 && s2 == 0) {
                                llDailyStay.setVisibility(View.VISIBLE);
                                tvDailyStayDetails.setVisibility(View.GONE);
                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                showDailyTimeLog(sdf.format(c.getTime()),parkingSpace.propertyID);
                            }
                        } else {
                            ll3.setVisibility(View.GONE);

                            tvDailyStayDetails.setVisibility(View.GONE);
                        }
                        if (s4 == 1) {
                            ll4.setVisibility(View.VISIBLE);
                            tvWeeklyStayDetails.setVisibility(View.GONE);
                            if (s1 == 0 && s2 == 0 && s3 == 0) {
                                llWeeklyStay.setVisibility(View.VISIBLE);
                                tvWeeklyStayDetails.setVisibility(View.GONE);
                            }
                        } else {
                            ll4.setVisibility(View.GONE);
                            tvWeeklyStayDetails.setVisibility(View.GONE);
                        }
                        if (s5 == 1) {
                            ll5.setVisibility(View.VISIBLE);
                            tvMonthlyStayDetails.setVisibility(View.GONE);
                            if (s1 == 0 && s2 == 0 && s3 == 0 && s4 == 0) {
                                llMonthlyStay.setVisibility(View.VISIBLE);
                                tvMonthlyStayDetails.setVisibility(View.GONE);
                            }
                        } else {
                            ll5.setVisibility(View.GONE);
                            tvMonthlyStayDetails.setVisibility(View.GONE);
                        }
                    }

                    try {
                        if (parkingSpace.userDetail != null) {
                            UserName.setText(parkingSpace.userDetail.FullName != null ? parkingSpace.userDetail.FullName : "");
                            tvUserType.setText("Park Owner");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            transparentStatusbar();

        }
        catch (Exception ex) {

        }
        inilizeCalendar();
    }


    public void ShortAdapterItemClick(){
        if(shortBookingAdapter!=null){
            shortBookingAdapter.onBookingClick(new onBookingListener() {
                @Override
                public void onBooking(HourTimeModel timeModel) {
                    if(timeModel.IS_BOOKED==0) {

                        for(int i=0;i<timeModels.size();i++){
                            if(timeModels.get(i).IS_SELECTED==1){
                                timeModels.get(i).IS_SELECTED=0;
                            }
                        }
                        shortBookingAdapter.notifyDataSetChanged();
                        if(timeModel.IS_SELECTED==1){
                            timeModel.IS_SELECTED=0;
                        }else
                            timeModel.IS_SELECTED = 1;

                        shortBookingAdapter.notifyDataSetChanged();

                        String sdate = "";
                        sdate = ParkerBookingStayScreen.tvShortEndDate.getText().toString()+ " " + timeModel.HourStartTime;

                        String data = compareTimes(DateFormat.format("dd MMM yyyy hh:mm a", new Date().getTime()).toString(), sdate);
                        if (data.equalsIgnoreCase("Time is equal Date2")) {
                            setTime("shorttime", timeModel.HourStartTime);
                        } else if (data.equalsIgnoreCase("Time is before Date2")) {
                            setTime("shorttime", timeModel.HourStartTime);
                        } else {
                            for(int i=0;i<timeModels.size();i++){
                                if(timeModels.get(i).IS_SELECTED==1){
                                    timeModels.get(i).IS_SELECTED=0;
                                }
                            }
                            shortBookingAdapter.notifyDataSetChanged();
                            Utility.showAlert(ParkerBookingStayScreen.this, "You cannot select past time");
                        }
                        for(int i=0;i<timeModels.size();i++){
                                if(bookingSelectesTimes(tvShortTimePick.getText().toString(),tvShortEndTimePick.getText().toString(),timeModels.get(i).HourStartTime).equalsIgnoreCase("yes")){
                                    timeModels.get(i).IS_SELECTED=1;
                                }
                        }
                        shortBookingAdapter.notifyDataSetChanged();
                    }
                }
            });
        }

    }
    public void AdapterItemClick(){
        if(hourbookingAdapter!=null){
            hourbookingAdapter.onBookingClick(new onBookingListener() {
                @Override
                public void onBooking(HourTimeModel timeModel) {
                    if(timeModel.IS_BOOKED==0) {

                        for(int i=0;i<timeModels.size();i++){
                            if(timeModels.get(i).IS_SELECTED==1){
                                timeModels.get(i).IS_SELECTED=0;
                            }
                        }
                        hourbookingAdapter.notifyDataSetChanged();
                        if(timeModel.IS_SELECTED==1){
                            timeModel.IS_SELECTED=0;
                        }else
                            timeModel.IS_SELECTED = 1;

                        hourbookingAdapter.notifyDataSetChanged();

                        String sdate = "";
                        sdate = BookingslotName.equalsIgnoreCase("hourly_stay")?ParkerBookingStayScreen.tvHourEndDate.getText().toString()+ " " + timeModel.HourStartTime:ParkerBookingStayScreen.tvDailyEndDate.getText().toString() + " " + timeModel.HourStartTime;

                        String data = compareTimes(DateFormat.format("dd MMM yyyy hh:mm a", new Date().getTime()).toString(), sdate);
                        if (data.equalsIgnoreCase("Time is equal Date2")) {
                            setTime(BookingslotName.equalsIgnoreCase("hourly_stay")?"hourtime":"dailytime", timeModel.HourStartTime);
                        } else if (data.equalsIgnoreCase("Time is before Date2")) {
                            setTime(BookingslotName.equalsIgnoreCase("hourly_stay")?"hourtime":"dailytime", timeModel.HourStartTime);
                        } else {
                            for(int i=0;i<timeModels.size();i++){
                                if(timeModels.get(i).IS_SELECTED==1){
                                    timeModels.get(i).IS_SELECTED=0;
                                }
                            }
                            hourbookingAdapter.notifyDataSetChanged();
                            Utility.showAlert(ParkerBookingStayScreen.this, "You cannot select past time");
                        }
                        for(int i=0;i<timeModels.size();i++){
                           if(BookingslotName.equalsIgnoreCase("hourly_stay")){
                               if(bookingSelectesTimes(tvHourTimePick.getText().toString(),textViewHourEndTime.getText().toString(),timeModels.get(i).HourStartTime).equalsIgnoreCase("yes")){
                                       timeModels.get(i).IS_SELECTED=1;
                               }
                           }
                           else if(BookingslotName.equalsIgnoreCase("daily_stay")){
                                if(bookingSelectesTimes(tvDailyTimePick.getText().toString(),tvDailyEndTime.getText().toString(),timeModels.get(i).HourStartTime).equalsIgnoreCase("yes")){
                                    timeModels.get(i).IS_SELECTED=1;
                                }
                            }

                        }
                        hourbookingAdapter.notifyDataSetChanged();
                    }

                }
            });
        }

    }

    private void bindComponent() {

        rvDailyTime=findViewById(R.id.rvDailyTime);
        rvShortTime=findViewById(R.id.rvShortTime);
        rvHourTime=findViewById(R.id.rvHourTime);
        tv1_hour = findViewById(R.id.tv1_hour);
        tv2_hour = findViewById(R.id.tv2_hour);
        tv3_hour = findViewById(R.id.tv3_hour);
        tv4_hour = findViewById(R.id.tv4_hour);
        llHourStayHourSelection = findViewById(R.id.llHourStayHourSelection);
        btnChat = findViewById(R.id.btnChat);
        ll1 = findViewById(R.id.ll1);
        ll2 = findViewById(R.id.ll2);
        ll3 = findViewById(R.id.ll3);
        ll4 = findViewById(R.id.ll4);
        ll5 = findViewById(R.id.ll5);
        tvParking = findViewById(R.id.tvParking);
        tvShortEndTimePick = findViewById(R.id.tvShortEndTimePick);

        rlBg = findViewById(R.id.rlBg);
        back = findViewById(R.id.back);
        Report = findViewById(R.id.Report);

        tvUserType = findViewById(R.id.tvUserType);

        llShortStayDateSelect = findViewById(R.id.llShortStayDateSelect);
        llHourSelectDate = findViewById(R.id.llHourSelectDate);
        llDailySelectDate = findViewById(R.id.llDailySelectDate);
        llWeekselectDate = findViewById(R.id.llWeekselectDate);
        llMonthselectDate = findViewById(R.id.llMonthselectDate);

        tvMonthlyStartDate = findViewById(R.id.tvMonthlyStartDate);
        tvMonthlyEndDate = findViewById(R.id.tvMonthlyEndDate);

        tvWeeklyStartDate = findViewById(R.id.tvWeeklyStartDate);
        tvWeeklyEndDate = findViewById(R.id.tvWeeklyEndDate);

        tvDailyStartDate = findViewById(R.id.tvDailyStartDate);
        tvDailyEndDate = findViewById(R.id.tvDailyEndDate);

        tvShortEndDate = findViewById(R.id.tvShortEndDate);
        tvShortStartDate = findViewById(R.id.tvShortStartDate);

        tvHourStartDate = findViewById(R.id.tvHourStartDate);
        tvHourEndDate = findViewById(R.id.tvHourEndDate);


        llShortStay = findViewById(R.id.llShortStay);
        llHourStay = findViewById(R.id.llHourStay);

        llMonthlyStay = findViewById(R.id.llMonthlyStay);
        llWeeklyStay = findViewById(R.id.llWeeklyStay);
        llDailyStay = findViewById(R.id.llDailyStay);

        tvShortStay = findViewById(R.id.tvShortStay);
        tvHourStay = findViewById(R.id.tvHourStay);
        tvDailyStay = findViewById(R.id.tvDailyStay);
        tvWeeklyStay = findViewById(R.id.tvWeeklyStay);
        tvMonthlyStay = findViewById(R.id.tvMonthlyStay);


        tvShortTimePick = findViewById(R.id.tvShortTimePick);
        tvDailyTimePick = findViewById(R.id.tvDailyTimePick);
        tvHourTimePick = findViewById(R.id.tvHourTimePick);

        textViewHourEndTime = findViewById(R.id.textViewHourEndTime);
        tvDailyEndTime = findViewById(R.id.tvDailyEndTime);

        tvShortStayDetails = findViewById(R.id.tvShortStayDetails);
        tvHourStayDetails = findViewById(R.id.tvHourStayDetails);
        tvDailyStayDetails = findViewById(R.id.tvDailyStayDetails);
        tvWeeklyStayDetails = findViewById(R.id.tvWeeklyStayDetails);
        tvMonthlyStayDetails = findViewById(R.id.tvMonthlyStayDetails);

        imgMap = findViewById(R.id.imgMap);
        tvAvailability = findViewById(R.id.tvAvailability);
        tvRatings = findViewById(R.id.tvRatings);
        tvPropertyTitle = findViewById(R.id.tvPropertyTitle);
        tvPropertyKM = findViewById(R.id.tvPropertyKM);
        PropertyAddress = findViewById(R.id.PropertyAddress);
        tvMinutes = findViewById(R.id.tvMinutes);
        UserName = findViewById(R.id.UserName);

        calendarView = findViewById(R.id.calendarView);
        calendarViewHour = findViewById(R.id.calendarViewHour);
        calendarViewDaily = findViewById(R.id.calendarViewDaily);
        calendarViewWeekly = findViewById(R.id.calendarViewWeekly);
        calendarViewMonthly = findViewById(R.id.calendarViewMonthly);

        btnBooking = findViewById(R.id.btnBooking);

        tv15 = findViewById(R.id.tv15);
        tv30 = findViewById(R.id.tv30);
        tv60 = findViewById(R.id.tv60);

        tv7Days = findViewById(R.id.tv7Days);
        tv5Days = findViewById(R.id.tv5Days);
        tv3Days = findViewById(R.id.tv3Days);

        tv1_4 = findViewById(R.id.tv1_4);
        tv5_8 = findViewById(R.id.tv5_8);
        tv8 = findViewById(R.id.tv8);


        tvHalfDay = findViewById(R.id.tvHalfDay);
        tvFullDay = findViewById(R.id.tvFullDay);

        tv30Days = findViewById(R.id.tv30Days);
        tv14Days = findViewById(R.id.tv14Days);

        tvMonthTimePick = findViewById(R.id.tvMonthTimePick);
        tvWeeklyTimePick = findViewById(R.id.tvWeeklyTimePick);

        tvWeekEndTime = findViewById(R.id.tvWeekEndTime);
        tvMonthEndTime = findViewById(R.id.tvMonthEndTime);


        back.setOnClickListener(this);
        Report.setOnClickListener(this);
        tvShortStay.setOnClickListener(this);
        tvHourStay.setOnClickListener(this);
        tvDailyStay.setOnClickListener(this);
        tvWeeklyStay.setOnClickListener(this);
        tvMonthlyStay.setOnClickListener(this);

        tvShortTimePick.setOnClickListener(this);
        tvHourTimePick.setOnClickListener(this);
        tvDailyTimePick.setOnClickListener(this);
        tvWeeklyTimePick.setOnClickListener(this);
        tvMonthTimePick.setOnClickListener(this);


        tv15.setOnClickListener(this);
        tv30.setOnClickListener(this);
        tv60.setOnClickListener(this);

        tv1_4.setOnClickListener(this);
        tv5_8.setOnClickListener(this);
        tv8.setOnClickListener(this);

        tvHalfDay.setOnClickListener(this);
        tvFullDay.setOnClickListener(this);

        tv3Days.setOnClickListener(this);
        tv5Days.setOnClickListener(this);
        tv7Days.setOnClickListener(this);

        tv30Days.setOnClickListener(this);
        tv14Days.setOnClickListener(this);

        btnBooking.setOnClickListener(this);
        imgMap.setOnClickListener(this);

        llShortStayDateSelect.setOnClickListener(this);
        llHourSelectDate.setOnClickListener(this);
        llDailySelectDate.setOnClickListener(this);
        llWeekselectDate.setOnClickListener(this);
        llMonthselectDate.setOnClickListener(this);

        tv1_hour.setOnClickListener(this);
        tv2_hour.setOnClickListener(this);
        tv3_hour.setOnClickListener(this);
        tv4_hour.setOnClickListener(this);

        btnChat.setOnClickListener(this);

        ParkerBookingStayScreen.tv15.setSelected(false);
        ParkerBookingStayScreen.tv30.setSelected(false);
        ParkerBookingStayScreen.tv60.setSelected(false);

        ParkerBookingStayScreen.tv3Days.setSelected(false);
        ParkerBookingStayScreen.tv5Days.setSelected(false);
        ParkerBookingStayScreen.tv7Days.setSelected(false);

        ParkerBookingStayScreen.tvHalfDay.setSelected(false);
        ParkerBookingStayScreen.tvFullDay.setSelected(false);

        ParkerBookingStayScreen.tv1_4.setSelected(false);
        ParkerBookingStayScreen.tv5_8.setSelected(false);
        ParkerBookingStayScreen.tv8.setSelected(false);

        ParkerBookingStayScreen.tv14Days.setSelected(false);
        ParkerBookingStayScreen.tv30Days.setSelected(false);

        initilize();


    }


    private void initilize() {

        if (!ParkerBookingStayScreen.bookingID.equalsIgnoreCase("0")) {
            try {
                dataContext.tblUserObjectSet.fill();
                if (dataContext.tblUserObjectSet.get(0).UserID.equalsIgnoreCase(parkingSpace.userDetail.UserID)) {
                    btnChat.setVisibility(View.INVISIBLE);
                } else {
//                    if (ParkerBookingStayScreen.bookingID.equalsIgnoreCase("0")) {
//                        btnChat.setVisibility(View.INVISIBLE);
//                    } else
//                        btnChat.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        } else {
            tvShortStartDate.setText(DateFormat.format("dd MMM yyyy", new Date().getTime()).toString() + "");
            tvShortEndDate.setText(DateFormat.format("dd MMM yyyy", new Date().getTime()).toString() + "");

            tvHourStartDate.setText(DateFormat.format("dd MMM yyyy", new Date().getTime()).toString() + "");
            tvHourEndDate.setText(DateFormat.format("dd MMM yyyy", new Date().getTime()).toString() + "");

            tvDailyStartDate.setText(DateFormat.format("dd MMM yyyy", new Date().getTime()).toString() + "");
            tvDailyEndDate.setText(DateFormat.format("dd MMM yyyy", new Date().getTime()).toString() + "");

            tvWeeklyStartDate.setText(DateFormat.format("dd MMM yyyy", new Date().getTime()).toString() + "");
            tvWeeklyEndDate.setText("");

            tvMonthlyStartDate.setText(DateFormat.format("dd MMM yyyy", new Date().getTime()).toString() + "");
            tvMonthlyEndDate.setText("");


            tvShortTimePick.setText(DateFormat.format("hh:mm a", new Date().getTime()).toString() + "");
            tvHourTimePick.setText(DateFormat.format("hh:mm a", new Date().getTime()).toString() + "");
            tvDailyTimePick.setText(DateFormat.format("hh:mm a", new Date().getTime()).toString() + "");

            tvWeeklyTimePick.setText(DateFormat.format("hh:mm a", new Date().getTime()).toString() + "");
            tvMonthTimePick.setText(DateFormat.format("hh:mm a", new Date().getTime()).toString() + "");

            tvWeekEndTime.setText(DateFormat.format("hh:mm a", new Date().getTime()).toString() + "");
            tvMonthEndTime.setText(DateFormat.format("hh:mm a", new Date().getTime()).toString() + "");


//            tvShortEndTimePick.setText("");
//            tvDailyEndTime.setText("");
//            textViewHourEndTime.setText("");
        }
    }

    @Override
    protected void onResume() {

        if (!ParkerBookingStayScreen.bookingID.equalsIgnoreCase("0")) {
            try {
                dataContext.tblUserObjectSet.fill();
                if (dataContext.tblUserObjectSet.get(0).UserID.equalsIgnoreCase(parkingSpace.userDetail.UserID)) {
                    btnChat.setVisibility(View.INVISIBLE);
                } else {
//                    if (ParkerBookingStayScreen.bookingID.equalsIgnoreCase("0")) {
//                        btnChat.setVisibility(View.INVISIBLE);
//                    } else
//                        btnChat.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        loadData();
//        if(parkingSpace.userDetail.UserID.equalsIgnoreCase(appPreferences.getString("USERID"))){
//            btnChat.setVisibility(View.INVISIBLE);
//        }else{
//            btnChat.setVisibility(View.VISIBLE);
//        }


        //  System.out.println("USER ID==>"+dataContext.tblUserObjectSet.get(0).UserID);
        // System.out.println("USER ID==>"+parkingSpace.userDetail.UserID);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        finish();
        ParkerBookingStayScreen.bookingID = "0";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public String getPrice(String stay, String staySlot) {
        String price = "0";
        for (int i = 0; i < parkingSpace.list.size(); i++) {
            if (parkingSpace.list.get(i).Stay.equalsIgnoreCase(stay)) {
                if (parkingSpace.list.get(i).StayMinutes.equalsIgnoreCase(staySlot)) {
                    price = String.format("%.0f", parkingSpace.list.get(i).StayPrice);
                }
            }
        }
        return price;
    }



    public void showHourTimeLog(String date,String propertyID){
        timeModels.clear();
        hourbookingAdapter=new HourBookingAdapter(this,timeModels);
        rvHourTime.setLayoutManager(new LinearLayoutManager(this));
        rvHourTime.setAdapter(hourbookingAdapter);
        getHourBooking(date,propertyID,"hourly_stay");
        AdapterItemClick();
    }
    public void showDailyTimeLog(String date,String propertyID){
        timeModels.clear();
        hourbookingAdapter=new HourBookingAdapter(this,timeModels);
        rvDailyTime.setLayoutManager(new LinearLayoutManager(this));
        rvDailyTime.setAdapter(hourbookingAdapter);
        getHourBooking(date,propertyID,"daily_stay");
        AdapterItemClick();
    }
    public void showShortTimeLog(String date,String propertyID){
        timeModels.clear();
        shortBookingAdapter=new ShortBookingAdapter(this,timeModels);
        rvShortTime.setLayoutManager(new LinearLayoutManager(this));
        rvShortTime.setAdapter(shortBookingAdapter);
        getHourBooking(date,propertyID,"short_stay");
        ShortAdapterItemClick();

    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tv1_hour:
                tv1_hour.setTextColor(getResources().getColor(R.color.white));
                tv1_hour.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                tv2_hour.setTextColor(getResources().getColor(R.color.black));
                tv2_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv3_hour.setTextColor(getResources().getColor(R.color.black));
                tv3_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv4_hour.setTextColor(getResources().getColor(R.color.black));
                tv4_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv1_hour.setSelected(true);
                tv2_hour.setSelected(false);
                tv3_hour.setSelected(false);
                tv4_hour.setSelected(false);

                setTime("hourtime", tvHourTimePick.getText().toString());
                break;
            case R.id.tv2_hour:
                tv2_hour.setTextColor(getResources().getColor(R.color.white));
                tv2_hour.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                tv1_hour.setTextColor(getResources().getColor(R.color.black));
                tv1_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv3_hour.setTextColor(getResources().getColor(R.color.black));
                tv3_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv4_hour.setTextColor(getResources().getColor(R.color.black));
                tv4_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv1_hour.setSelected(false);
                tv2_hour.setSelected(true);
                tv3_hour.setSelected(false);
                tv4_hour.setSelected(false);
                setTime("hourtime", tvHourTimePick.getText().toString());
                break;
            case R.id.tv3_hour:
                tv3_hour.setTextColor(getResources().getColor(R.color.white));
                tv3_hour.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                tv2_hour.setTextColor(getResources().getColor(R.color.black));
                tv2_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv1_hour.setTextColor(getResources().getColor(R.color.black));
                tv1_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv4_hour.setTextColor(getResources().getColor(R.color.black));
                tv4_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv1_hour.setSelected(false);
                tv2_hour.setSelected(false);
                tv3_hour.setSelected(true);
                tv4_hour.setSelected(false);
                setTime("hourtime", tvHourTimePick.getText().toString());
                break;
            case R.id.tv4_hour:

                tv4_hour.setTextColor(getResources().getColor(R.color.white));
                tv4_hour.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                tv2_hour.setTextColor(getResources().getColor(R.color.black));
                tv2_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv3_hour.setTextColor(getResources().getColor(R.color.black));
                tv3_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv1_hour.setTextColor(getResources().getColor(R.color.black));
                tv1_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv1_hour.setSelected(false);
                tv2_hour.setSelected(false);
                tv3_hour.setSelected(false);
                tv4_hour.setSelected(true);
                setTime("hourtime", tvHourTimePick.getText().toString());
                break;
            case R.id.btnChat:
                startActivity(new Intent(ParkerBookingStayScreen.this, BookingChat_.class)
                        .putExtra("obj", parkingSpace)
                );
                // Utility.showAlert(ParkerBookingStayScreen.this,"Under development");
                break;
            case R.id.Report:
                startActivity(new Intent(ParkerBookingStayScreen.this, ReportScreen.class)
                        .putExtra("propertymodel", parkingSpace)
                );
                break;
            case R.id.back:
                finish();
                break;
            case R.id.tvShortStay:
                int i = 0;
                if (tv15.getVisibility() == View.VISIBLE) {
                    i = 1;
                }
                if (tv30.getVisibility() == View.VISIBLE) {
                    i = 1;
                }
                if (tv60.getVisibility() == View.VISIBLE) {
                    i = 1;
                }
                if (i == 1)
                    setVisibilityOfCalendar(llShortStay);
                else
                    Utility.showAlert(ParkerBookingStayScreen.this, Constants.ERROR_MESSAGE);
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                showShortTimeLog(sdf.format(c.getTime()),parkingSpace.propertyID);
                break;
            case R.id.tvHourStay:
                int j = 0;
                if (tv1_4.getVisibility() == View.VISIBLE) {
                    j = 1;
                }
                if (tv5_8.getVisibility() == View.VISIBLE) {
                    j = 1;
                }
                if (tv8.getVisibility() == View.VISIBLE) {
                    j = 1;
                }
                if (j == 1)
                    setVisibilityOfCalendar(llHourStay);
                else
                    Utility.showAlert(ParkerBookingStayScreen.this, Constants.ERROR_MESSAGE);
                Calendar c1 = Calendar.getInstance();
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                showHourTimeLog(sdf1.format(c1.getTime()),parkingSpace.propertyID);
                break;
            case R.id.tvWeeklyStay:
                int k = 0;
                if (tv3Days.getVisibility() == View.VISIBLE) {
                    k = 1;
                }
                if (tv5Days.getVisibility() == View.VISIBLE) {
                    k = 1;
                }
                if (tv7Days.getVisibility() == View.VISIBLE) {
                    k = 1;
                }

                if (k == 1)
                    setVisibilityOfCalendar(llWeeklyStay);
                else
                    Utility.showAlert(ParkerBookingStayScreen.this, Constants.ERROR_MESSAGE);


                break;
            case R.id.tvDailyStay:
                int l = 0;
                if (tvHalfDay.getVisibility() == View.VISIBLE) {
                    l = 1;
                }
                if (tvFullDay.getVisibility() == View.VISIBLE) {
                    l = 1;
                }
                if (l == 1) {
                    setVisibilityOfCalendar(llDailyStay);
                } else
                    Utility.showAlert(ParkerBookingStayScreen.this, Constants.ERROR_MESSAGE);

                Calendar c2 = Calendar.getInstance();
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                showDailyTimeLog(sdf2.format(c2.getTime()),parkingSpace.propertyID);
                break;
            case R.id.tvMonthlyStay:
                int m = 0;
                if (tv14Days.getVisibility() == View.VISIBLE) {
                    m = 1;
                }
                if (tv30Days.getVisibility() == View.VISIBLE) {
                    m = 1;
                }
                if (m == 1) {
                    setVisibilityOfCalendar(llMonthlyStay);
                } else
                    Utility.showAlert(ParkerBookingStayScreen.this, Constants.ERROR_MESSAGE);

                break;

            case R.id.imgMap:
                if (parkingSpace != null && parkingSpace.propertyLatitude != null && parkingSpace.propertyLongitude != null && !parkingSpace.propertyLatitude.isEmpty() && !parkingSpace.propertyLongitude.isEmpty()) {
                    if (locationData != null && locationData.getLang() != null && locationData.getLat() != null && !locationData.getLat().isEmpty() && !locationData.getLang().isEmpty())
                        openMapPath(locationData.getLat() + "," + locationData.getLang(), parkingSpace.propertyLatitude + "," + parkingSpace.propertyLongitude);
                } else
                    Utility.showAlert(this, "Parking Address is empty");
                break;

            case R.id.tvShortTimePick:
                if (tv15.isSelected() || tv30.isSelected() || tv60.isSelected()) {
                    if (tvShortEndDate.getText().length() > 0)
                        openendTimePicker(ParkerBookingStayScreen.this, "shorttime");
                    else
                        Utility.showAlert(ParkerBookingStayScreen.this, "please select date for stay");
                } else {
                    Utility.showAlert(ParkerBookingStayScreen.this, "please select stay slot");
                }
                break;
            case R.id.tvHourTimePick:
                if (tv1_4.isSelected() || tv5_8.isSelected() || tv8.isSelected()) {
                    if (tvHourEndDate.getText().length() > 0)
                        openendTimePicker(ParkerBookingStayScreen.this, "hourtime");
                    else
                        Utility.showAlert(ParkerBookingStayScreen.this, "please select date for stay");
                } else {
                    Utility.showAlert(ParkerBookingStayScreen.this, "please select stay slot");
                }
                break;

            case R.id.tvDailyTimePick:
                if (tvHalfDay.isSelected() || tvFullDay.isSelected()) {
                    if (tvDailyEndDate.getText().length() > 0)
                        openendTimePicker(ParkerBookingStayScreen.this, "dailytime");
                    else
                        Utility.showAlert(ParkerBookingStayScreen.this, "please select date for stay");
                } else {
                    Utility.showAlert(ParkerBookingStayScreen.this, "please select stay slot");
                }
                break;

            case R.id.tvWeeklyTimePick:
                if (tv3Days.isSelected() || tv5Days.isSelected() || tv7Days.isSelected()) {
                    if (tvWeeklyEndDate.getText().length() > 0)
                        openendTimePicker(ParkerBookingStayScreen.this, "weektime");
                    else
                        Utility.showAlert(ParkerBookingStayScreen.this, "please select date for stay");
                } else {
                    Utility.showAlert(ParkerBookingStayScreen.this, "please select stay slot");
                }
                break;

            case R.id.tvMonthTimePick:
                if (tv14Days.isSelected() || tv30Days.isSelected()) {
                    if (tvMonthlyEndDate.getText().length() > 0)
                        openendTimePicker(ParkerBookingStayScreen.this, "monthtime");
                    else
                        Utility.showAlert(ParkerBookingStayScreen.this, "please select date for stay");
                } else {
                    Utility.showAlert(ParkerBookingStayScreen.this, "please select stay slot");
                }
                break;

            case R.id.tv15:

                tv15.setTextColor(getResources().getColor(R.color.white));
                tv15.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                tv30.setTextColor(getResources().getColor(R.color.black));
                tv30.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv60.setTextColor(getResources().getColor(R.color.black));
                tv60.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv15.setSelected(true);
                tv30.setSelected(false);
                tv60.setSelected(false);
                shortStayPrice = getPrice(Constants.SHORT_STAY, Constants._15MIN);
                setTime("shorttime", tvShortTimePick.getText().toString());
                break;

            case R.id.tv30:

                tv30.setTextColor(getResources().getColor(R.color.white));
                tv30.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                tv15.setTextColor(getResources().getColor(R.color.black));
                tv15.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv60.setTextColor(getResources().getColor(R.color.black));
                tv60.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv30.setSelected(true);
                tv15.setSelected(false);
                tv60.setSelected(false);
                shortStayPrice = getPrice(Constants.SHORT_STAY, Constants._30MIN);
                setTime("shorttime", tvShortTimePick.getText().toString());
                break;

            case R.id.tv60:


                tv60.setTextColor(getResources().getColor(R.color.white));
                tv60.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                tv15.setTextColor(getResources().getColor(R.color.black));
                tv15.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv30.setTextColor(getResources().getColor(R.color.black));
                tv30.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv60.setSelected(true);
                tv30.setSelected(false);
                tv15.setSelected(false);
                shortStayPrice = getPrice(Constants.SHORT_STAY, Constants._45MIN);
                setTime("shorttime", tvShortTimePick.getText().toString());
                break;

            case R.id.tv1_4:

                tv1_4.setTextColor(getResources().getColor(R.color.white));
                tv1_4.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                tv5_8.setTextColor(getResources().getColor(R.color.black));
                tv5_8.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv8.setTextColor(getResources().getColor(R.color.black));
                tv8.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv1_4.setSelected(true);
                tv5_8.setSelected(false);
                tv8.setSelected(false);

                hourStayPrice = getPrice(Constants.HOUR_STAY, Constants._1_4_HOUR);
                setTime("hourtime", tvHourTimePick.getText().toString());
                tv1_hour.setText("1");
                tv2_hour.setText("2");
                tv3_hour.setText("3");
                tv4_hour.setText("4");
                break;
            case R.id.tv5_8:

                tv5_8.setTextColor(getResources().getColor(R.color.white));
                tv5_8.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                tv1_4.setTextColor(getResources().getColor(R.color.black));
                tv1_4.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv8.setTextColor(getResources().getColor(R.color.black));
                tv8.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv5_8.setSelected(true);
                tv1_4.setSelected(false);
                tv8.setSelected(false);
                hourStayPrice = getPrice(Constants.HOUR_STAY, Constants._4_8_HOUR);
                setTime("hourtime", tvHourTimePick.getText().toString());
                tv1_hour.setText("5");
                tv2_hour.setText("6");
                tv3_hour.setText("7");
                tv4_hour.setText("8");
                break;

            case R.id.tv8:

                tv8.setTextColor(getResources().getColor(R.color.white));
                tv8.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                tv1_4.setTextColor(getResources().getColor(R.color.black));
                tv1_4.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv5_8.setTextColor(getResources().getColor(R.color.black));
                tv5_8.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv8.setSelected(true);
                tv1_4.setSelected(false);
                tv5_8.setSelected(false);
                hourStayPrice = getPrice(Constants.HOUR_STAY, Constants._8_PLUS_HOUR);
                setTime("hourtime", tvHourTimePick.getText().toString());
                tv1_hour.setText("9");
                tv2_hour.setText("10");
                tv3_hour.setText("11");
                tv4_hour.setText("12");
                break;

            case R.id.tvHalfDay:

                tvHalfDay.setTextColor(getResources().getColor(R.color.white));
                tvHalfDay.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);
                tvFullDay.setTextColor(getResources().getColor(R.color.black));
                tvFullDay.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tvHalfDay.setSelected(true);
                tvFullDay.setSelected(false);
                dailyStayPrice = getPrice(Constants.DAY_STAY, Constants._HALFDAY);
                setTime("dailytime", tvDailyTimePick.getText().toString());
                break;

            case R.id.tvFullDay:

                tvFullDay.setTextColor(getResources().getColor(R.color.white));
                tvFullDay.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);
                tvHalfDay.setTextColor(getResources().getColor(R.color.black));
                tvHalfDay.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tvHalfDay.setSelected(false);
                tvFullDay.setSelected(true);
                dailyStayPrice = getPrice(Constants.DAY_STAY, Constants._FULLDAY);
                setTime("dailytime", tvDailyTimePick.getText().toString());
                break;

            case R.id.tv3Days:
                tv3Days.setTextColor(getResources().getColor(R.color.white));
                tv3Days.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                tv5Days.setTextColor(getResources().getColor(R.color.black));
                tv5Days.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv7Days.setTextColor(getResources().getColor(R.color.black));
                tv7Days.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv3Days.setSelected(true);
                tv5Days.setSelected(false);
                tv7Days.setSelected(false);
                weeklyStayPrice = getPrice(Constants.WEEK_STAY, Constants._3_DAY);
//                setCalendar(tvWeeklyStartDate, tvWeeklyEndDate, "3days",Constants.converDate(tvWeeklyStartDate.getText().toString())DateFormat.format("yyyy-MM-dd", new Date().getTime()).toString());
                setCalendar(tvWeeklyStartDate, tvWeeklyEndDate, "3days", Constants.converDate_YYYYMMDD(tvWeeklyStartDate.getText().toString()));
                break;

            case R.id.tv5Days:
                tv5Days.setTextColor(getResources().getColor(R.color.white));
                tv5Days.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                tv3Days.setTextColor(getResources().getColor(R.color.black));
                tv3Days.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv7Days.setTextColor(getResources().getColor(R.color.black));
                tv7Days.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv5Days.setSelected(true);
                tv3Days.setSelected(false);
                tv7Days.setSelected(false);
                weeklyStayPrice = getPrice(Constants.WEEK_STAY, Constants._5_DAY);
                setCalendar(tvWeeklyStartDate, tvWeeklyEndDate, "5days", Constants.converDate_YYYYMMDD(tvWeeklyStartDate.getText().toString()));
                break;
            case R.id.tv7Days:
                tv7Days.setTextColor(getResources().getColor(R.color.white));
                tv7Days.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                tv3Days.setTextColor(getResources().getColor(R.color.black));
                tv3Days.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv5Days.setTextColor(getResources().getColor(R.color.black));
                tv5Days.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv7Days.setSelected(true);
                tv3Days.setSelected(false);
                tv5Days.setSelected(false);
                weeklyStayPrice = getPrice(Constants.WEEK_STAY, Constants._7_DAY);
                setCalendar(tvWeeklyStartDate, tvWeeklyEndDate, "7days", Constants.converDate_YYYYMMDD(tvWeeklyStartDate.getText().toString()));
                break;

            case R.id.tv14Days:
                tv14Days.setTextColor(getResources().getColor(R.color.white));
                tv14Days.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);
                tv30Days.setTextColor(getResources().getColor(R.color.black));
                tv30Days.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv30Days.setSelected(false);
                tv14Days.setSelected(true);
                monthlyStayPrice = getPrice(Constants.MONTH_STAY, Constants._14_DAY);
                setCalendar(tvMonthlyStartDate, tvMonthlyEndDate, "14days", Constants.converDate_YYYYMMDD(tvMonthlyStartDate.getText().toString()));
                break;

            case R.id.tv30Days:
                tv30Days.setTextColor(getResources().getColor(R.color.white));
                tv30Days.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                tv14Days.setTextColor(getResources().getColor(R.color.black));
                tv14Days.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv14Days.setSelected(false);
                tv30Days.setSelected(true);
                monthlyStayPrice = getPrice(Constants.MONTH_STAY, Constants._30_DAY);
                setCalendar(tvMonthlyStartDate, tvMonthlyEndDate, "30days", Constants.converDate_YYYYMMDD(tvMonthlyStartDate.getText().toString()));
                break;

            case R.id.btnBooking:
                int book=0;
                for(int t1=0;t1<timeModels.size();t1++){
                    if(timeModels.get(t1).IS_SELECTED==1){
                        book=1;
                        break;
                    }
                }
                if(book==1) {
                    if (!shortStayPrice.equalsIgnoreCase("0") || !hourStayPrice.equalsIgnoreCase("0") || !dailyStayPrice.equalsIgnoreCase("0") || !weeklyStayPrice.equalsIgnoreCase("0") || !monthlyStayPrice.equalsIgnoreCase("0")) {
                        if (llShortStay.isSelected()) {
                            if (tv15.isSelected())
                                addBooking("short_time", "15 mins", tvShortStartDate.getText().toString(), tvShortEndDate.getText().toString(), tvShortTimePick.getText().toString(), tvShortEndTimePick.getText().toString(), parkingSpace.propertyDistance, parkingSpace.propertyDuration, bookingID, "");
                            else if (tv30.isSelected())
                                addBooking("short_time", "30 mins", tvShortStartDate.getText().toString(), tvShortEndDate.getText().toString(), tvShortTimePick.getText().toString(), tvShortEndTimePick.getText().toString(), parkingSpace.propertyDistance, parkingSpace.propertyDuration, bookingID, "");
                            else if (tv60.isSelected())
                                addBooking("short_time", "45 mins", tvShortStartDate.getText().toString(), tvShortEndDate.getText().toString(), tvShortTimePick.getText().toString(), tvShortEndTimePick.getText().toString(), parkingSpace.propertyDistance, parkingSpace.propertyDuration, bookingID, "");
                        } else if (llHourStay.isSelected()) {
                            if (tv1_4.isSelected()) {
                                if (tv1_hour.isSelected())
                                    addBooking("hourly_time", "1 - 4 hours", tvHourStartDate.getText().toString(), tvHourEndDate.getText().toString(), tvHourTimePick.getText().toString(), textViewHourEndTime.getText().toString(), parkingSpace.propertyDistance, parkingSpace.propertyDuration, bookingID, tv1_hour.getText().toString());
                                else if (tv2_hour.isSelected())
                                    addBooking("hourly_time", "1 - 4 hours", tvHourStartDate.getText().toString(), tvHourEndDate.getText().toString(), tvHourTimePick.getText().toString(), textViewHourEndTime.getText().toString(), parkingSpace.propertyDistance, parkingSpace.propertyDuration, bookingID, tv2_hour.getText().toString());
                                else if (tv3_hour.isSelected())
                                    addBooking("hourly_time", "1 - 4 hours", tvHourStartDate.getText().toString(), tvHourEndDate.getText().toString(), tvHourTimePick.getText().toString(), textViewHourEndTime.getText().toString(), parkingSpace.propertyDistance, parkingSpace.propertyDuration, bookingID, tv3_hour.getText().toString());
                                else if (tv4_hour.isSelected())
                                    addBooking("hourly_time", "1 - 4 hours", tvHourStartDate.getText().toString(), tvHourEndDate.getText().toString(), tvHourTimePick.getText().toString(), textViewHourEndTime.getText().toString(), parkingSpace.propertyDistance, parkingSpace.propertyDuration, bookingID, tv4_hour.getText().toString());
                            } else if (tv5_8.isSelected()) {
                                if (tv1_hour.isSelected())
                                    addBooking("hourly_time", "5 - 8 hours", tvHourStartDate.getText().toString(), tvHourEndDate.getText().toString(), tvHourTimePick.getText().toString(), textViewHourEndTime.getText().toString(), parkingSpace.propertyDistance, parkingSpace.propertyDuration, bookingID, tv1_hour.getText().toString());
                                else if (tv2_hour.isSelected())
                                    addBooking("hourly_time", "5 - 8 hours", tvHourStartDate.getText().toString(), tvHourEndDate.getText().toString(), tvHourTimePick.getText().toString(), textViewHourEndTime.getText().toString(), parkingSpace.propertyDistance, parkingSpace.propertyDuration, bookingID, tv2_hour.getText().toString());
                                else if (tv3_hour.isSelected())
                                    addBooking("hourly_time", "5 - 8 hours", tvHourStartDate.getText().toString(), tvHourEndDate.getText().toString(), tvHourTimePick.getText().toString(), textViewHourEndTime.getText().toString(), parkingSpace.propertyDistance, parkingSpace.propertyDuration, bookingID, tv3_hour.getText().toString());
                                else if (tv4_hour.isSelected())
                                    addBooking("hourly_time", "5 - 8 hours", tvHourStartDate.getText().toString(), tvHourEndDate.getText().toString(), tvHourTimePick.getText().toString(), textViewHourEndTime.getText().toString(), parkingSpace.propertyDistance, parkingSpace.propertyDuration, bookingID, tv4_hour.getText().toString());

                            } else if (tv8.isSelected()) {
                                if (tv1_hour.isSelected())
                                    addBooking("hourly_time", "8+ hours", tvHourStartDate.getText().toString(), tvHourEndDate.getText().toString(), tvHourTimePick.getText().toString(), textViewHourEndTime.getText().toString(), parkingSpace.propertyDistance, parkingSpace.propertyDuration, bookingID, tv1_hour.getText().toString());
                                else if (tv2_hour.isSelected())
                                    addBooking("hourly_time", "8+ hours", tvHourStartDate.getText().toString(), tvHourEndDate.getText().toString(), tvHourTimePick.getText().toString(), textViewHourEndTime.getText().toString(), parkingSpace.propertyDistance, parkingSpace.propertyDuration, bookingID, tv2_hour.getText().toString());
                                else if (tv3_hour.isSelected())
                                    addBooking("hourly_time", "8+ hours", tvHourStartDate.getText().toString(), tvHourEndDate.getText().toString(), tvHourTimePick.getText().toString(), textViewHourEndTime.getText().toString(), parkingSpace.propertyDistance, parkingSpace.propertyDuration, bookingID, tv3_hour.getText().toString());
                                else if (tv4_hour.isSelected())
                                    addBooking("hourly_time", "8+ hours", tvHourStartDate.getText().toString(), tvHourEndDate.getText().toString(), tvHourTimePick.getText().toString(), textViewHourEndTime.getText().toString(), parkingSpace.propertyDistance, parkingSpace.propertyDuration, bookingID, tv4_hour.getText().toString());
                            }
                        } else if (llDailyStay.isSelected()) {
                            if (tvHalfDay.isSelected())
                                addBooking("daily_time", "half day (up to 6 hours)", tvDailyStartDate.getText().toString(), tvDailyEndDate.getText().toString(), tvDailyTimePick.getText().toString(), tvDailyEndTime.getText().toString(), parkingSpace.propertyDistance, parkingSpace.propertyDuration, bookingID, "");
                            else if (tvFullDay.isSelected())
                                addBooking("daily_time", "full day (up to 12 hours)", tvDailyStartDate.getText().toString(), tvDailyEndDate.getText().toString(), tvDailyTimePick.getText().toString(), tvDailyEndTime.getText().toString(), parkingSpace.propertyDistance, parkingSpace.propertyDuration, bookingID, "");

                        } else if (llWeeklyStay.isSelected()) {
                            if (tv3Days.isSelected())
                                addBooking("weekly_time", "3 days", tvWeeklyStartDate.getText().toString(), tvWeeklyEndDate.getText().toString(), tvWeeklyTimePick.getText().toString(), tvWeekEndTime.getText().toString(), parkingSpace.propertyDistance, parkingSpace.propertyDuration, bookingID, "");
                            else if (tv5Days.isSelected())
                                addBooking("weekly_time", "5 days", tvWeeklyStartDate.getText().toString(), tvWeeklyEndDate.getText().toString(), tvWeeklyTimePick.getText().toString(), tvWeekEndTime.getText().toString(), parkingSpace.propertyDistance, parkingSpace.propertyDuration, bookingID, "");
                            else if (tv7Days.isSelected())
                                addBooking("weekly_time", "7 days", tvWeeklyStartDate.getText().toString(), tvWeeklyEndDate.getText().toString(), tvWeeklyTimePick.getText().toString(), tvWeekEndTime.getText().toString(), parkingSpace.propertyDistance, parkingSpace.propertyDuration, bookingID, "");

                        } else if (llMonthlyStay.isSelected()) {
                            if (tv14Days.isSelected())
                                addBooking("monthly_time", "14 days", tvMonthlyStartDate.getText().toString(), tvMonthlyEndDate.getText().toString(), tvMonthTimePick.getText().toString(), tvMonthEndTime.getText().toString(), parkingSpace.propertyDistance, parkingSpace.propertyDuration, bookingID, "");
                            else if (tv30Days.isSelected())
                                addBooking("monthly_time", "30 days", tvMonthlyStartDate.getText().toString(), tvMonthlyEndDate.getText().toString(), tvMonthTimePick.getText().toString(), tvMonthEndTime.getText().toString(), parkingSpace.propertyDistance, parkingSpace.propertyDuration, bookingID, "");

                        } else {
                            Utility.showAlert(ParkerBookingStayScreen.this, Constants.SELECT_BOOKINGDATE);
                        }
                    } else {
                        Utility.showAlert(ParkerBookingStayScreen.this, Constants.BOOKING_ERROR_MESSAGE);
                    }
                }else{
                    Utility.showAlert(ParkerBookingStayScreen.this, "Please select booking time");
                }
                break;

            case R.id.llShortStayDateSelect:
                //selectDate(tvShortStartDate, tvShortEndDate, "");

                break;

            case R.id.llHourSelectDate:
//                selectDate(tvHourStartDate, tvHourEndDate, "");
                break;

            case R.id.llDailySelectDate:
//                selectDate(tvDailyStartDate, tvDailyEndDate, "");
                break;

            case R.id.llWeekselectDate:
//                if (tv3Days.isSelected())
//                    selectDate(tvWeeklyStartDate, tvWeeklyEndDate, "3days");
//                if (tv5Days.isSelected())
//                    selectDate(tvWeeklyStartDate, tvWeeklyEndDate, "5days");
//                if (tv7Days.isSelected())
//                    selectDate(tvWeeklyStartDate, tvWeeklyEndDate, "7days");
                break;
            case R.id.llMonthselectDate:
//                if (tv14Days.isSelected())
//                    selectDate(tvMonthlyStartDate, tvMonthlyEndDate, "14days");
//                if (tv30Days.isSelected())
//                    selectDate(tvMonthlyStartDate, tvMonthlyEndDate, "30days");


                break;
        }
    }


    private void setLinerLayoutVisibility(LinearLayout ll1, LinearLayout ll2, LinearLayout ll3, LinearLayout ll4, LinearLayout ll5) {

        ll1.setVisibility(View.VISIBLE);
        ll1.setSelected(true);

        ll2.setVisibility(View.GONE);
        ll2.setSelected(false);

        ll3.setVisibility(View.GONE);
        ll3.setSelected(false);

        ll4.setVisibility(View.GONE);
        ll4.setSelected(false);

        ll5.setVisibility(View.GONE);
        ll5.setSelected(false);

    }

    private void setVisibilityOfCalendar(LinearLayout ll) {

        if (ll.getId() == R.id.llShortStay) {
            setLinerLayoutVisibility(llShortStay, llHourStay, llWeeklyStay, llDailyStay, llMonthlyStay);
            selectedItem(tvShortStay, tvHourStay, tvDailyStay, tvMonthlyStay, tvWeeklyStay);

            tvShortStayDetails.setVisibility(View.GONE);
            tvDailyStayDetails.setVisibility(View.GONE);
            tvHourStayDetails.setVisibility(View.GONE);
            tvWeeklyStayDetails.setVisibility(View.GONE);
            tvMonthlyStayDetails.setVisibility(View.GONE);

            llHourStayHourSelection.setVisibility(View.GONE);
            tv4_hour.setVisibility(View.GONE);
            tv3_hour.setVisibility(View.GONE);
            tv2_hour.setVisibility(View.GONE);
            tv1_hour.setVisibility(View.GONE);

        } else if (ll.getId() == R.id.llHourStay) {

            setLinerLayoutVisibility(llHourStay, llShortStay, llWeeklyStay, llDailyStay, llMonthlyStay);
            selectedItem(tvHourStay, tvShortStay, tvDailyStay, tvMonthlyStay, tvWeeklyStay);
            tvShortStayDetails.setVisibility(View.GONE);
            tvDailyStayDetails.setVisibility(View.GONE);
            tvHourStayDetails.setVisibility(View.GONE);
            tvWeeklyStayDetails.setVisibility(View.GONE);
            tvMonthlyStayDetails.setVisibility(View.GONE);

            llHourStayHourSelection.setVisibility(View.VISIBLE);
            tv4_hour.setVisibility(View.VISIBLE);
            tv3_hour.setVisibility(View.VISIBLE);
            tv2_hour.setVisibility(View.VISIBLE);
            tv1_hour.setVisibility(View.VISIBLE);


        } else if (ll.getId() == R.id.llWeeklyStay) {


            setLinerLayoutVisibility(llWeeklyStay, llHourStay, llShortStay, llDailyStay, llMonthlyStay);
            selectedItem(tvWeeklyStay, tvHourStay, tvShortStay, tvDailyStay, tvMonthlyStay);
            tvShortStayDetails.setVisibility(View.GONE);
            tvDailyStayDetails.setVisibility(View.GONE);
            tvHourStayDetails.setVisibility(View.GONE);
            tvWeeklyStayDetails.setVisibility(View.GONE);
            tvMonthlyStayDetails.setVisibility(View.GONE);

            llHourStayHourSelection.setVisibility(View.GONE);
            tv4_hour.setVisibility(View.GONE);
            tv3_hour.setVisibility(View.GONE);
            tv2_hour.setVisibility(View.GONE);
            tv1_hour.setVisibility(View.GONE);

        } else if (ll.getId() == R.id.llDailyStay) {


            ParkerBookingStayScreen.tvHalfDay.setSelected(true);
            setLinerLayoutVisibility(llDailyStay, llWeeklyStay, llHourStay, llShortStay, llMonthlyStay);
            selectedItem(tvDailyStay, tvWeeklyStay, tvHourStay, tvShortStay, tvMonthlyStay);

            tvShortStayDetails.setVisibility(View.GONE);
            tvDailyStayDetails.setVisibility(View.GONE);
            tvHourStayDetails.setVisibility(View.GONE);
            tvWeeklyStayDetails.setVisibility(View.GONE);
            tvMonthlyStayDetails.setVisibility(View.GONE);

            llHourStayHourSelection.setVisibility(View.GONE);
            tv4_hour.setVisibility(View.GONE);
            tv3_hour.setVisibility(View.GONE);
            tv2_hour.setVisibility(View.GONE);
            tv1_hour.setVisibility(View.GONE);


        } else if (ll.getId() == R.id.llMonthlyStay) {

            setLinerLayoutVisibility(llMonthlyStay, llDailyStay, llWeeklyStay, llHourStay, llShortStay);
            selectedItem(tvMonthlyStay, tvDailyStay, tvWeeklyStay, tvHourStay, tvShortStay);

            tvShortStayDetails.setVisibility(View.GONE);
            tvDailyStayDetails.setVisibility(View.GONE);
            tvHourStayDetails.setVisibility(View.GONE);
            tvWeeklyStayDetails.setVisibility(View.GONE);
            tvMonthlyStayDetails.setVisibility(View.GONE);

            llHourStayHourSelection.setVisibility(View.GONE);
            tv4_hour.setVisibility(View.GONE);
            tv3_hour.setVisibility(View.GONE);
            tv2_hour.setVisibility(View.GONE);
            tv1_hour.setVisibility(View.GONE);

        }

    }

    private void selectedItem(TTextView t1, TTextView t2, TTextView t3, TTextView t4, TTextView t5) {
//        t1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_left_fill_circle, 0, R.drawable.drawable_arrow_up, 0);
//        t2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_left_normal_circle, 0, R.drawable.drawable_arrow_down, 0);
//        t3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_left_normal_circle, 0, R.drawable.drawable_arrow_down, 0);
//        t4.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_left_normal_circle, 0, R.drawable.drawable_arrow_down, 0);
//        t5.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_left_normal_circle, 0, R.drawable.drawable_arrow_down, 0);

        t1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.drawable_arrow_up, 0);
        t2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.drawable_arrow_down, 0);
        t3.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.drawable_arrow_down, 0);
        t4.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.drawable_arrow_down, 0);
        t5.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.drawable_arrow_down, 0);

        t1.setSelected(true);
        t2.setSelected(false);
        t3.setSelected(false);
        t4.setSelected(false);
        t5.setSelected(false);
    }

    private void openMapPath(String from, String path) {
        try {
            String mapPath = "https://www.google.com/maps/dir/?api=1&origin=" + from + "&destination=" + path + "&dir_action=navigate";
            //String mapPath="https://www.google.com/maps/dir/?api=1&waypoints=133+V+STREET+BAKERSFIELD+CA+93304|525+OAK+STREET+BAKERSFIELD+CA+93304|341+PACIFIC+AVE+SHAFTER+CA+93263|29488+MERCED+AVE+SHAFTER+CA+93563&destination=3119+S+Willow+Ave+Fresno%2C+CA+93725&dir_action=navigate";
            Log.e("map data", mapPath);
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse(mapPath));
            intent.setPackage("com.google.android.apps.maps");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Utility.showAlert(this, "Map application is not installed");
            }
        } catch (Exception ex) {

        }
    }

    @Override
    public void handleNewLocation(Location location) {
        locationData.setLang(location.getLongitude() + "");
        locationData.setLat(location.getLatitude() + "");

    }


    @UiThread
    private void loadData() {
        runOnUiThread(() -> {
            deleteAvailableTimes();
            bindSundayTime();
            bindMondayTime();
            bindTuesdayTime();
            bindWednesdayTime();
            bindThursdayTime();
            bindFridayTime();
            bindSaturdayTime();

        });
    }

    private void bindSundayTime() {
        if (parkingSpace != null) {
            try {
                sundayList.clear();
                String data[] = (parkingSpace.propertySunday.split(","));
                if (data.length > 0) {
                    for (int i = 0; i < data.length; i++) {
                        tblPropertyAvailableTimes s1 = new tblPropertyAvailableTimes();
                        s1.DayName = "Sunday";
                        s1.Timing = data[i];
                        sundayList.add(s1);
                    }
                    dataContext.propertyAvailableTimesObjectSet.addAll(sundayList);
                    dataContext.propertyAvailableTimesObjectSet.save();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void bindMondayTime() {
        if (parkingSpace != null) {
            try {
                mondayList.clear();
                String data[] = (parkingSpace.propertyMonday.split(","));
                if (data.length > 0) {
                    for (int i = 0; i < data.length; i++) {
                        if (data[i] != null && !data[i].isEmpty()) {
                            tblPropertyAvailableTimes s1 = new tblPropertyAvailableTimes();
                            s1.DayName = "Monday";
                            s1.Timing = data[i];
                            mondayList.add(s1);
                        }
                    }
                    dataContext.propertyAvailableTimesObjectSet.addAll(mondayList);
                    dataContext.propertyAvailableTimesObjectSet.save();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void bindTuesdayTime() {
        if (parkingSpace != null) {
            try {
                tuesdayList.clear();
                String data[] = (parkingSpace.propertyTuesday.split(","));
                if (data.length > 0) {
                    for (int i = 0; i < data.length; i++) {
                        if (data[i] != null && !data[i].isEmpty()) {
                            tblPropertyAvailableTimes s1 = new tblPropertyAvailableTimes();
                            s1.DayName = "Tuesday";
                            s1.Timing = data[i];
                            tuesdayList.add(s1);
                        }
                    }
                    dataContext.propertyAvailableTimesObjectSet.addAll(tuesdayList);
                    dataContext.propertyAvailableTimesObjectSet.save();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void bindWednesdayTime() {
        if (parkingSpace != null) {
            try {
                wednesdayList.clear();
                String data[] = (parkingSpace.propertyWednesday.split(","));
                if (data.length > 0) {
                    for (int i = 0; i < data.length; i++) {
                        if (data[i] != null && !data[i].isEmpty()) {
                            tblPropertyAvailableTimes s1 = new tblPropertyAvailableTimes();
                            s1.DayName = "Wednesday";
                            s1.Timing = data[i];
                            wednesdayList.add(s1);
                        }
                    }
                    dataContext.propertyAvailableTimesObjectSet.addAll(wednesdayList);
                    dataContext.propertyAvailableTimesObjectSet.save();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void bindThursdayTime() {
        if (parkingSpace != null) {
            try {

                thursdayList.clear();
                String data[] = parkingSpace.propertyThursday.split(",");
                if (data.length > 0) {
                    for (int i = 0; i < data.length; i++) {
                        if (data[i] != null && !data[i].isEmpty()) {
                            tblPropertyAvailableTimes s1 = new tblPropertyAvailableTimes();
                            s1.DayName = "Thursday";
                            s1.Timing = data[i];
                            thursdayList.add(s1);
                        }
                    }
                    dataContext.propertyAvailableTimesObjectSet.addAll(thursdayList);
                    dataContext.propertyAvailableTimesObjectSet.save();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void bindFridayTime() {
        if (parkingSpace != null) {
            try {

                fridayList.clear();
                String data[] = parkingSpace.propertyFriday.split(",");
                if (data.length > 0) {
                    for (int i = 0; i < data.length; i++) {
                        if (data[i] != null && !data[i].isEmpty()) {
                            tblPropertyAvailableTimes s1 = new tblPropertyAvailableTimes();
                            s1.DayName = "Friday";
                            s1.Timing = data[i];
                            fridayList.add(s1);
                        }
                    }
                    dataContext.propertyAvailableTimesObjectSet.addAll(fridayList);
                    dataContext.propertyAvailableTimesObjectSet.save();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void bindSaturdayTime() {
        if (parkingSpace != null) {
            try {

                saturdayList.clear();
                String data[] = parkingSpace.propertySaturday.split(",");
                if (data.length > 0) {
                    for (int i = 0; i < data.length; i++) {
                        if (data[i] != null && !data[i].isEmpty()) {
                            tblPropertyAvailableTimes s1 = new tblPropertyAvailableTimes();
                            s1.DayName = "Saturday";
                            s1.Timing = data[i];
                            saturdayList.add(s1);
                        }
                    }
                    dataContext.propertyAvailableTimesObjectSet.addAll(saturdayList);
                    dataContext.propertyAvailableTimesObjectSet.save();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

    }

    private void deleteAvailableTimes() {
        try {
            dataContext.propertyAvailableTimesObjectSet.fill();
            for (int j = 0; j < dataContext.propertyAvailableTimesObjectSet.size(); j++)
                dataContext.propertyAvailableTimesObjectSet.remove(j).setStatus(Entity.STATUS_DELETED);
            dataContext.propertyAvailableTimesObjectSet.save();

        } catch (AdaFrameworkException e) {
            e.printStackTrace();
        }
    }

    private void setCalendar(TTextView tv, TTextView tv2, String staytime, String date) {

        String data = compareDates(DateFormat.format("yyyy-MM-dd", new Date().getTime()).toString() + "", date);
        //System.out.println("DATE 1 "+DateFormat.format("yyyy-MM-dd", new Date().getTime()).toString() + "");
        //System.out.println("DATE 2 "+date);

        if (data.equalsIgnoreCase("Date1 is equal Date2")) {
            if (staytime.equalsIgnoreCase("3days")) {
                tv.setText(Constants.converDate(date));
                tv2.setText(Constants.converDate(setDays(date, 3)));
                weekdate.clear();
                weekdate.addAll(getDates(tv.getText().toString(), tv2.getText().toString()));
                Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
                SimpleDateFormat df1 = new SimpleDateFormat("dd MMM yyyy");
                Date date1 = null;
                try {
                    date1 = df1.parse(Constants.converDate(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                currentCalendar.setTime(date1);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    List<DayDecorator> decorators = new ArrayList<>();
                    decorators.add(disabledColorDecorator);
                    calendarViewWeekly.setDecorators(decorators);
                    calendarViewWeekly.refreshCalendar(currentCalendar);
                }, 100);
            } else if (staytime.equalsIgnoreCase("5days")) {
                tv.setText(Constants.converDate(date));
                tv2.setText(Constants.converDate(setDays(date, 5)));
                weekdate.clear();
                weekdate.addAll(getDates(tv.getText().toString(), tv2.getText().toString()));
                Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
                SimpleDateFormat df1 = new SimpleDateFormat("dd MMM yyyy");
                Date date1 = null;
                try {
                    date1 = df1.parse(Constants.converDate(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                currentCalendar.setTime(date1);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    List<DayDecorator> decorators = new ArrayList<>();
                    decorators.add(disabledColorDecorator);
                    calendarViewWeekly.setDecorators(decorators);
                    calendarViewWeekly.refreshCalendar(currentCalendar);
                }, 100);
            } else if (staytime.equalsIgnoreCase("7days")) {
                tv.setText(Constants.converDate(date));
                tv2.setText(Constants.converDate(setDays(date, 7)));
                weekdate.clear();
                weekdate.addAll(getDates(tv.getText().toString(), tv2.getText().toString()));
                Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
                SimpleDateFormat df1 = new SimpleDateFormat("dd MMM yyyy");
                Date date1 = null;
                try {
                    date1 = df1.parse(Constants.converDate(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                currentCalendar.setTime(date1);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    List<DayDecorator> decorators = new ArrayList<>();
                    decorators.add(disabledColorDecorator);
                    calendarViewWeekly.setDecorators(decorators);
                    calendarViewWeekly.refreshCalendar(currentCalendar);
                }, 100);

            } else if (staytime.equalsIgnoreCase("14days")) {
                tv.setText(Constants.converDate(date));
                tv2.setText(Constants.converDate(setDays(date, 14)));
                monthdate.clear();
                monthdate.addAll(getDates(tv.getText().toString(), tv2.getText().toString()));
                Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
                SimpleDateFormat df1 = new SimpleDateFormat("dd MMM yyyy");
                Date date1 = null;
                try {
                    date1 = df1.parse(Constants.converDate(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                currentCalendar.setTime(date1);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    List<DayDecorator> decorators = new ArrayList<>();
                    decorators.add(disabledColorDecorator);
                    calendarViewMonthly.setDecorators(decorators);
                    calendarViewMonthly.refreshCalendar(currentCalendar);
                }, 100);
            } else if (staytime.equalsIgnoreCase("30days")) {
                tv.setText(Constants.converDate(date));
                tv2.setText(Constants.converDate(setDays(date, 30)));
                monthdate.clear();
                monthdate.addAll(getDates(tv.getText().toString(), tv2.getText().toString()));
                Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
                SimpleDateFormat df1 = new SimpleDateFormat("dd MMM yyyy");
                Date date1 = null;
                try {
                    date1 = df1.parse(Constants.converDate(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                currentCalendar.setTime(date1);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    List<DayDecorator> decorators = new ArrayList<>();
                    decorators.add(disabledColorDecorator);
                    calendarViewMonthly.setDecorators(decorators);
                    calendarViewMonthly.refreshCalendar(currentCalendar);
                }, 100);
            } else {
                tv.setText(Constants.converDate(date));
                tv2.setText(Constants.converDate(date));
            }
        } else if (data.equalsIgnoreCase("Date1 is before Date2")) {
            if (staytime.equalsIgnoreCase("3days")) {
                tv.setText(Constants.converDate(date));
                tv2.setText(Constants.converDate(setDays(date, 3)));
                weekdate.clear();
                weekdate.addAll(getDates(tv.getText().toString(), tv2.getText().toString()));
                Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
                SimpleDateFormat df1 = new SimpleDateFormat("dd MMM yyyy");
                Date date1 = null;
                try {
                    date1 = df1.parse(Constants.converDate(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                currentCalendar.setTime(date1);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    List<DayDecorator> decorators = new ArrayList<>();
                    decorators.add(disabledColorDecorator);
                    calendarViewWeekly.setDecorators(decorators);
                    calendarViewWeekly.refreshCalendar(currentCalendar);
                }, 100);
            } else if (staytime.equalsIgnoreCase("5days")) {
                tv.setText(Constants.converDate(date));
                tv2.setText(Constants.converDate(setDays(date, 5)));
                weekdate.clear();
                weekdate.addAll(getDates(tv.getText().toString(), tv2.getText().toString()));
                Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
                SimpleDateFormat df1 = new SimpleDateFormat("dd MMM yyyy");
                Date date1 = null;
                try {
                    date1 = df1.parse(Constants.converDate(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                currentCalendar.setTime(date1);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    List<DayDecorator> decorators = new ArrayList<>();
                    decorators.add(disabledColorDecorator);
                    calendarViewWeekly.setDecorators(decorators);
                    calendarViewWeekly.refreshCalendar(currentCalendar);
                }, 100);
            } else if (staytime.equalsIgnoreCase("7days")) {
                tv.setText(Constants.converDate(date));
                tv2.setText(Constants.converDate(setDays(date, 7)));
                weekdate.clear();
                weekdate.addAll(getDates(tv.getText().toString(), tv2.getText().toString()));
                Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
                SimpleDateFormat df1 = new SimpleDateFormat("dd MMM yyyy");
                Date date1 = null;
                try {
                    date1 = df1.parse(Constants.converDate(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                currentCalendar.setTime(date1);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    List<DayDecorator> decorators = new ArrayList<>();
                    decorators.add(disabledColorDecorator);
                    calendarViewWeekly.setDecorators(decorators);
                    calendarViewWeekly.refreshCalendar(currentCalendar);
                }, 100);
            } else if (staytime.equalsIgnoreCase("14days")) {
                tv.setText(Constants.converDate(date));
                tv2.setText(Constants.converDate(setDays(date, 14)));
                monthdate.clear();
                monthdate.addAll(getDates(tv.getText().toString(), tv2.getText().toString()));
                Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
                SimpleDateFormat df1 = new SimpleDateFormat("dd MMM yyyy");
                Date date1 = null;
                try {
                    date1 = df1.parse(Constants.converDate(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                currentCalendar.setTime(date1);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    List<DayDecorator> decorators = new ArrayList<>();
                    decorators.add(disabledColorDecorator);
                    calendarViewMonthly.setDecorators(decorators);
                    calendarViewMonthly.refreshCalendar(currentCalendar);
                }, 100);
            } else if (staytime.equalsIgnoreCase("30days")) {
                tv.setText(Constants.converDate(date));
                tv2.setText(Constants.converDate(setDays(date, 30)));
                monthdate.clear();
                monthdate.addAll(getDates(tv.getText().toString(), tv2.getText().toString()));
                Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
                SimpleDateFormat df1 = new SimpleDateFormat("dd MMM yyyy");
                Date date1 = null;
                try {
                    date1 = df1.parse(Constants.converDate(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                currentCalendar.setTime(date1);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    List<DayDecorator> decorators = new ArrayList<>();
                    decorators.add(disabledColorDecorator);
                    calendarViewMonthly.setDecorators(decorators);
                    calendarViewMonthly.refreshCalendar(currentCalendar);
                }, 100);
            } else {
                tv.setText(Constants.converDate(date));
                tv2.setText(Constants.converDate(date));
            }
        } else {
            //Utility.showAlert(ParkerBookingStayScreen.this, "You can not select past date");
        }


    }

    private String setDays(String staydate, int days) {
        String dateInString = staydate;  // Start date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(dateInString));

            c.add(Calendar.DATE, days);
            sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date resultdate = new Date(c.getTimeInMillis());
            dateInString = sdf.format(resultdate);
            System.out.println("String date:" + dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateInString;

    }

    Callback<JsonObject> callback = new Callback<JsonObject>() {

        @Override
        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
            Utility.hideProgress();
            if (response.isSuccessful()) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    System.out.println("RESPONSE IS=====" + response.body().toString());
                    if (jsonObject != null) {
                        String message = jsonObject.getString("error_message");
                        if (jsonObject.getString("error_code").equalsIgnoreCase("0")) {
                            //Utility.showAlert(ParkerBookingStayScreen.this,message);

                            String BookingID = jsonObject.getString("bookind_id") != null ? jsonObject.getString("bookind_id") : "0";
                            String PropertyID = jsonObject.getString("property_id") != null ? jsonObject.getString("property_id") : "0";
                            String CategoryName = jsonObject.getString("category_name") != null ? jsonObject.getString("category_name") : "";
                            String SlotName = jsonObject.getString("slot_name") != null ? jsonObject.getString("slot_name") : "";
                            String BookingStartDate = jsonObject.getString("booking_start_date") != null ? jsonObject.getString("booking_start_date") : "";
                            String BookingEndDate = jsonObject.getString("booking_end_date") != null ? jsonObject.getString("booking_end_date") : "";

                            String BookingStartTime = jsonObject.getString("booking_start_time") != null ? jsonObject.getString("booking_start_time") : "";
                            String BookingEndTime = jsonObject.getString("booking_end_time") != null ? jsonObject.getString("booking_end_time") : "";
                            String StayPrice = jsonObject.getString("price") != null ? jsonObject.getString("price") : "0";

                            String MaxCars = jsonObject.getString("max_cars") != null ? jsonObject.getString("max_cars") : "";
                            String RemainCars = jsonObject.getString("remain_parking_slot") != null ? jsonObject.getString("remain_parking_slot") : "";

                            String rating = jsonObject.getString("is_rated") != null ? jsonObject.getString("is_rated") : "";

                            JSONObject object = jsonObject.optJSONObject("card_detail");
                            tblCard card = new tblCard();
                            if (object != null) {
                                card.CardID = object.optString("id");
                                card.OwnerName = object.optString("title");
                                card.CardNo = object.optString("number");
                                card.CardMonth = object.optString("months");
                                card.CardYear = object.optString("years");
                                card.CVV = object.optString("cvv");
                                card.isDefault = object.optString("is_default");
                            }
//                            parkingSpace.userDetail.FullName=jsonObject.optJSONObject("parker_user").optString("name");
//                            parkingSpace.userDetail.UserID=jsonObject.optJSONObject("parker_user").optString("id");
//                            parkingSpace.userDetail.APIToken=jsonObject.optJSONObject("parker_user").optString("fcm_id");
//                            parkingSpace.userDetail.DeviceToken=jsonObject.optJSONObject("parker_user").optString("device_token");

                            startActivity(new Intent(ParkerBookingStayScreen.this, ParkerBookingStayConfirmScreen.class)
                                    .putExtra("BookingID", BookingID)
                                    .putExtra("CategoryName", CategoryName)
                                    .putExtra("SlotName", SlotName)
                                    .putExtra("BookingStartDate", BookingStartDate)
                                    .putExtra("BookingEndDate", BookingEndDate)
                                    .putExtra("BookingStartTime", BookingStartTime)
                                    .putExtra("BookingEndTime", BookingEndTime)
                                    .putExtra("stay_booking", parkingSpace)
                                    .putExtra("rating", rating)
                                    .putExtra("property_id", PropertyID)
                                    .putExtra("stay_price", StayPrice)
                                    .putExtra("currentlocation", locationData.getLat() + "," + locationData.getLang())
                                    .putExtra("propertylocation", parkingSpace.propertyLatitude + "," + parkingSpace.propertyLongitude)
                                    .putExtra("card", card)


                            );

                        } else {
                            onStop();
                            Utility.showAlert(ParkerBookingStayScreen.this, message);
                            return;
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(Call<JsonObject> call, Throwable t) {
            //Utility.hideProgress();
            //Utility.showAlert(ParkerBookingScreen.this,t.toString());
        }
    };

    private void addBooking(String stay, String slot, String stayStartDate, String stayEndDate, String stayStartTime, String stayEndTime, String distance, String during, String BookingID, String hourDateTime) {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(ParkerBookingStayScreen.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.addBooking(WebUtility.BOOK_PROPERTY,
                        appPreferences.getString("USERID"),
                        parkingSpace.propertyID,
                        stay,
                        slot,
                        hourDateTime,
                        stayStartDate,
                        stayEndDate,
                        stayStartTime,
                        stayEndTime,
                        distance,
                        during,
                        bookingID.equalsIgnoreCase("0") ? BookingID : bookingID);
                responseBodyCall.enqueue(callback);

            }
        } catch (Exception ex) {
            Utility.showAlert(this,ex.toString());
        }
    }

    private static List<String> getDates(String dateString1, String dateString2) {
        ArrayList<String> dates = new ArrayList<String>();
        SimpleDateFormat df1 = new SimpleDateFormat("dd MMM yyyy");

        SimpleDateFormat df2 = new SimpleDateFormat("dd MMM yyyy");
        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1.parse(dateString1);
            date2 = df1.parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while (!cal1.after(cal2)) {

            dates.add(df2.format(cal1.getTime()));
            cal1.add(Calendar.DATE, 1);
        }

        return dates;
    }

    @UiThread
    private void inilizeCalendar() {


        //Initialize calendar with date
        Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());

        //Show monday as first date of week
        calendarView.setFirstDayOfWeek(Calendar.SUNDAY);
        calendarViewHour.setFirstDayOfWeek(Calendar.SUNDAY);
        calendarViewDaily.setFirstDayOfWeek(Calendar.SUNDAY);
        calendarViewWeekly.setFirstDayOfWeek(Calendar.SUNDAY);
        calendarViewMonthly.setFirstDayOfWeek(Calendar.SUNDAY);

        //Show/hide overflow days of a month
        calendarView.setShowOverflowDate(false);
        calendarViewHour.setShowOverflowDate(false);
        calendarViewWeekly.setShowOverflowDate(false);
        calendarViewDaily.setShowOverflowDate(false);
        calendarViewMonthly.setShowOverflowDate(false);

        //Handling custom calendar events
        calendarView.setCalendarListener(new CalendarListener() {
            @Override
            public void onDateSelected(Date date) {
                // Toast.makeText(CustomisedCalendarActivity.this, df.format(date), Toast.LENGTH_SHORT).show();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                setCalendar(tvShortStartDate, tvShortEndDate, "", df.format(date));
                showShortTimeLog(df.format(date),parkingSpace.propertyID);

            }

            @Override
            public void onMonthChanged(Date date) {


                SimpleDateFormat df = new SimpleDateFormat("MM-yyyy");
                //Toast.makeText(CustomisedCalendarActivity.this, df.format(date), Toast.LENGTH_SHORT).show();
            }
        });


        calendarViewHour.setCalendarListener(new CalendarListener() {
            @Override
            public void onDateSelected(Date date) {
                // Toast.makeText(CustomisedCalendarActivity.this, df.format(date), Toast.LENGTH_SHORT).show();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                System.out.println("Hour Date" + df.format(date));
                setCalendar(tvHourStartDate, tvHourEndDate, "", df.format(date));
                showHourTimeLog(df.format(date),parkingSpace.propertyID);
            }

            @Override
            public void onMonthChanged(Date date) {
                SimpleDateFormat df = new SimpleDateFormat("MM-yyyy");
                //Toast.makeText(CustomisedCalendarActivity.this, df.format(date), Toast.LENGTH_SHORT).show();
            }
        });

        calendarViewWeekly.setCalendarListener(new CalendarListener() {
            @Override
            public void onDateSelected(Date date) {
                // Toast.makeText(CustomisedCalendarActivity.this, df.format(date), Toast.LENGTH_SHORT).show();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                //setCalendar(tvHourStartDate, tvHourEndDate, "",df.format(date));
                if (tv3Days.isSelected())
                    setCalendar(tvWeeklyStartDate, tvWeeklyEndDate, "3days", df.format(date));
                else if (tv5Days.isSelected())
                    setCalendar(tvWeeklyStartDate, tvWeeklyEndDate, "5days", df.format(date));
                else if (tv7Days.isSelected())
                    setCalendar(tvWeeklyStartDate, tvWeeklyEndDate, "7days", df.format(date));

            }

            @Override
            public void onMonthChanged(Date date) {
                SimpleDateFormat df = new SimpleDateFormat("MM-yyyy");
                //Toast.makeText(CustomisedCalendarActivity.this, df.format(date), Toast.LENGTH_SHORT).show();


            }
        });

        calendarViewMonthly.setCalendarListener(new CalendarListener() {
            @Override
            public void onDateSelected(Date date) {
                // Toast.makeText(CustomisedCalendarActivity.this, df.format(date), Toast.LENGTH_SHORT).show();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                if (tv14Days.isSelected())
                    setCalendar(tvMonthlyStartDate, tvMonthlyEndDate, "14days", df.format(date));
                else if (tv30Days.isSelected())
                    setCalendar(tvMonthlyStartDate, tvMonthlyEndDate, "30days", df.format(date));
                //setCalendar(tvHourStartDate, tvHourEndDate, "",df.format(date));
            }

            @Override
            public void onMonthChanged(Date date) {
                SimpleDateFormat df = new SimpleDateFormat("MM-yyyy");
                //Toast.makeText(CustomisedCalendarActivity.this, df.format(date), Toast.LENGTH_SHORT).show();
//                if (tv3Days.isSelected())
//                    selectDate(tvWeeklyStartDate, tvWeeklyEndDate, "3days");
//                if (tv5Days.isSelected())
//                    selectDate(tvWeeklyStartDate, tvWeeklyEndDate, "5days");
//                if (tv7Days.isSelected())
//                    selectDate(tvWeeklyStartDate, tvWeeklyEndDate, "7days");

            }
        });


        calendarViewDaily.setCalendarListener(new CalendarListener() {
            @Override
            public void onDateSelected(Date date) {
                // Toast.makeText(CustomisedCalendarActivity.this, df.format(date), Toast.LENGTH_SHORT).show();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                System.out.println("Daily DAte" + df.format(date));
                setCalendar(tvDailyStartDate, tvDailyEndDate, "", df.format(date));
                showDailyTimeLog(df.format(date),parkingSpace.propertyID);
            }

            @Override
            public void onMonthChanged(Date date) {
                SimpleDateFormat df = new SimpleDateFormat("MM-yyyy");
            }
        });


        final Typeface typeface = Typeface.createFromAsset(getAssets(), "ProductSansBold.ttf");
        if (null != typeface) {
            calendarView.setCustomTypeface(typeface);
            calendarView.refreshCalendar(currentCalendar);


            calendarViewHour.setCustomTypeface(typeface);
            calendarViewHour.refreshCalendar(currentCalendar);

            calendarViewDaily.setCustomTypeface(typeface);
            calendarViewDaily.refreshCalendar(currentCalendar);

            calendarViewWeekly.setCustomTypeface(typeface);
            calendarViewWeekly.refreshCalendar(currentCalendar);

            calendarViewMonthly.setCustomTypeface(typeface);
            calendarViewMonthly.refreshCalendar(currentCalendar);
        }

        try {
            dataContext.propertyAvailableTimesObjectSet.fill();
        } catch (AdaFrameworkException e) {
            e.printStackTrace();
        }


        //call refreshCalendar to update calendar the view
        currentrefreshCalender(currentCalendar);

    }

    public void currentrefreshCalender(Calendar currentCalendar) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            List<DayDecorator> decorators = new ArrayList<>();
            decorators.add(disabledColorDecorator);
            calendarView.setDecorators(decorators);
            calendarViewHour.setDecorators(decorators);
            calendarViewDaily.setDecorators(decorators);
            calendarViewMonthly.setDecorators(decorators);
            calendarViewWeekly.setDecorators(decorators);

            calendarView.refreshCalendar(currentCalendar);
            calendarViewHour.refreshCalendar(currentCalendar);
            calendarViewWeekly.refreshCalendar(currentCalendar);
            calendarViewMonthly.refreshCalendar(currentCalendar);
            calendarViewDaily.refreshCalendar(currentCalendar);

        }, 100);
    }


    public void openendTimePicker(Activity act, String textview) {
        int mHour, mMinutes, mSeconds;
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR);
        mMinutes = c.get(Calendar.MINUTE);
        mSeconds = c.get(Calendar.SECOND);


        TimePickerDialog timePickerDialog2 = new TimePickerDialog(act, R.style.MyAppTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String t = "00:00 AM";
                try {

                    String status = "AM";

                    if (hourOfDay > 11) {
                        // If the hour is greater than or equal to 12
                        // Then the current AM PM status is PM
                        status = "PM";
                    }

                    // Initialize a new variable to hold 12 hour format hour value
                    int hour_of_12_hour_format;

                    if (hourOfDay > 11) {

                        // If the hour is greater than or equal to 12
                        // Then we subtract 12 from the hour to make it 12 hour format time
                        hour_of_12_hour_format = hourOfDay - 12;
                        if (hour_of_12_hour_format == 0) {
                            hour_of_12_hour_format = 12;
                        }
                    } else {
                        if (hourOfDay == 0) {
                            hour_of_12_hour_format = 12;
                        } else
                            hour_of_12_hour_format = hourOfDay;
                    }


                    t = String.format("%02d:%02d", hour_of_12_hour_format, minute) + " " + status;

                    String sdate = "";

                    if (textview.equalsIgnoreCase("shorttime")) {
                        sdate = ParkerBookingStayScreen.tvShortEndDate.getText().toString() + " " + t;
                    } else if (textview.equalsIgnoreCase("hourtime")) {
                        sdate = ParkerBookingStayScreen.tvHourEndDate.getText().toString() + " " + t;
                    } else if (textview.equalsIgnoreCase("dailytime")) {
                        sdate = ParkerBookingStayScreen.tvDailyEndDate.getText().toString() + " " + t;
                    } else if (textview.equalsIgnoreCase("weektime")) {
                        sdate = ParkerBookingStayScreen.tvWeeklyEndDate.getText().toString() + " " + t;
                    } else if (textview.equalsIgnoreCase("monthtime")) {
                        sdate = ParkerBookingStayScreen.tvMonthlyEndDate.getText().toString() + " " + t;
                    }
//                    if (ParkerBookingStayScreen.tvShortEndDate.getText().length() > 0) {
//
//                    } else if (ParkerBookingStayScreen.tvHourEndDate.getText().length() > 0) {
//
//                    } else if (ParkerBookingStayScreen.tvDailyEndDate.getText().length() > 0) {
//
//                    } else if (ParkerBookingStayScreen.tvWeeklyEndDate.getText().length() > 0) {
//                        sdate = ParkerBookingStayScreen.tvWeeklyEndDate.getText().toString() + " " + t;
//                    } else if (ParkerBookingStayScreen.tvMonthlyEndDate.getText().length() > 0) {
//                        sdate = ParkerBookingStayScreen.tvMonthlyEndDate.getText().toString() + " " + t;
//                    }


                    String data = compareTimes(DateFormat.format("dd MMM yyyy hh:mm a", new Date().getTime()).toString(), sdate);
                    if (data.equalsIgnoreCase("Time is equal Date2")) {
                        setTime(textview, t);
                    } else if (data.equalsIgnoreCase("Time is before Date2")) {
                        setTime(textview, t);
                    } else
                        Utility.showAlert(ParkerBookingStayScreen.this, "You cannot select past time");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, mHour, mMinutes, false);
        timePickerDialog2.show();
    }

    public void setHourBookingSelected(){

        for(int i=0;i<timeModels.size();i++){
            if(timeModels.get(i).IS_SELECTED==1){
                timeModels.get(i).IS_SELECTED=0;
            }
        }
        if(hourbookingAdapter!=null)
            hourbookingAdapter.notifyDataSetChanged();

        for(int i=0;i<timeModels.size();i++){
            if(bookingSelectesTimes(tvHourTimePick.getText().toString(),textViewHourEndTime.getText().toString(),timeModels.get(i).HourStartTime).equalsIgnoreCase("yes")){
                if(timeModels.get(i).IS_BOOKED==0)
                    timeModels.get(i).IS_SELECTED=1;
                else {
                    for(int j=0;j<timeModels.size();j++){
                        if(timeModels.get(j).IS_SELECTED==1){
                            timeModels.get(j).IS_SELECTED=0;
                        }
                    }
                    if(hourbookingAdapter!=null)
                        hourbookingAdapter.notifyDataSetChanged();
                    Utility.showAlert(this,"Sorry, selected parking space is not available for booking.");
                    break;
                }
            }
            rvHourTime.scrollToPosition(i);
        }
        if(hourbookingAdapter!=null)
            hourbookingAdapter.notifyDataSetChanged();
    }
    public void setDailyBookingSelected(){

        for(int i=0;i<timeModels.size();i++){
            if(timeModels.get(i).IS_SELECTED==1){
                timeModels.get(i).IS_SELECTED=0;
            }
        }
        if(hourbookingAdapter!=null)
            hourbookingAdapter.notifyDataSetChanged();
        for(int i=0;i<timeModels.size();i++){
            if(bookingSelectesTimes(tvDailyTimePick.getText().toString(),tvDailyEndTime.getText().toString(),timeModels.get(i).HourStartTime).equalsIgnoreCase("yes")){
                if(timeModels.get(i).IS_BOOKED==0)
                    timeModels.get(i).IS_SELECTED=1;
                else {
                    for(int j=0;j<timeModels.size();j++){
                        if(timeModels.get(j).IS_SELECTED==1){
                            timeModels.get(j).IS_SELECTED=0;
                        }
                    }
                    if(hourbookingAdapter!=null)
                        hourbookingAdapter.notifyDataSetChanged();
                    Utility.showAlert(this,"Sorry, selected parking space is not available for booking.");
                    break;
                }
            }
            rvDailyTime.scrollToPosition(i);
        }
        if(hourbookingAdapter!=null)
            hourbookingAdapter.notifyDataSetChanged();
    }
    public void setShortBookingSelected(){
        for(int i=0;i<timeModels.size();i++){
            if(timeModels.get(i).IS_SELECTED==1){
                timeModels.get(i).IS_SELECTED=0;
            }
        }
        if(shortBookingAdapter!=null)
            shortBookingAdapter.notifyDataSetChanged();
        for(int i=0;i<timeModels.size();i++){
            if(bookingSelectesTimes(tvShortTimePick.getText().toString(),tvShortEndTimePick.getText().toString(),timeModels.get(i).HourStartTime).equalsIgnoreCase("yes")){
                if(timeModels.get(i).IS_BOOKED==0)
                    timeModels.get(i).IS_SELECTED=1;
                else {
                    for(int j=0;j<timeModels.size();j++){
                        if(timeModels.get(j).IS_SELECTED==1){
                            timeModels.get(j).IS_SELECTED=0;
                        }
                    }
                    if(shortBookingAdapter!=null)
                        shortBookingAdapter.notifyDataSetChanged();
                    Utility.showAlert(this, "Sorry, selected parking space is not available for booking.");
                    break;
                }
                rvShortTime.scrollToPosition(i);
            }
        }
        if(shortBookingAdapter!=null)
            shortBookingAdapter.notifyDataSetChanged();
    }

    public void setTime(String textviewName, String t) {
        if (textviewName.equalsIgnoreCase("shorttime")) {
            ParkerBookingStayScreen.tvShortTimePick.setText(t);
            if (ParkerBookingStayScreen.tv15.isSelected())
                ParkerBookingStayScreen.tvShortEndTimePick.setText(Constants.addMinTime(t, 15));
            else if (ParkerBookingStayScreen.tv30.isSelected())
                ParkerBookingStayScreen.tvShortEndTimePick.setText(Constants.addMinTime(t, 30));
            else if (ParkerBookingStayScreen.tv60.isSelected())
                ParkerBookingStayScreen.tvShortEndTimePick.setText(Constants.addMinTime(t, 45));

            setShortBookingSelected();
        }
        if (textviewName.equalsIgnoreCase("hourtime")) {
            ParkerBookingStayScreen.tvHourTimePick.setText(t);
            if (ParkerBookingStayScreen.tv1_4.isSelected()) {
                if (tv1_hour.isSelected())
                    ParkerBookingStayScreen.textViewHourEndTime.setText(Constants.addMinTime(t, 60));
                else if (tv2_hour.isSelected())
                    ParkerBookingStayScreen.textViewHourEndTime.setText(Constants.addMinTime(t, 120));
                else if (tv3_hour.isSelected())
                    ParkerBookingStayScreen.textViewHourEndTime.setText(Constants.addMinTime(t, 180));
                else if (tv4_hour.isSelected())
                    ParkerBookingStayScreen.textViewHourEndTime.setText(Constants.addMinTime(t, 240));

                setHourBookingSelected();
            } else if (ParkerBookingStayScreen.tv5_8.isSelected()) {
                if (tv1_hour.isSelected())
                    ParkerBookingStayScreen.textViewHourEndTime.setText(Constants.addMinTime(t, 300));
                else if (tv2_hour.isSelected())
                    ParkerBookingStayScreen.textViewHourEndTime.setText(Constants.addMinTime(t, 360));
                else if (tv3_hour.isSelected())
                    ParkerBookingStayScreen.textViewHourEndTime.setText(Constants.addMinTime(t, 420));
                else if (tv4_hour.isSelected())
                    ParkerBookingStayScreen.textViewHourEndTime.setText(Constants.addMinTime(t, 480));

                setHourBookingSelected();

            } else if (ParkerBookingStayScreen.tv8.isSelected()) {
                if (tv1_hour.isSelected())
                    ParkerBookingStayScreen.textViewHourEndTime.setText(Constants.addMinTime(t, 540));
                else if (tv2_hour.isSelected())
                    ParkerBookingStayScreen.textViewHourEndTime.setText(Constants.addMinTime(t, 600));
                else if (tv3_hour.isSelected())
                    ParkerBookingStayScreen.textViewHourEndTime.setText(Constants.addMinTime(t, 660));
                else if (tv4_hour.isSelected())
                    ParkerBookingStayScreen.textViewHourEndTime.setText(Constants.addMinTime(t, 720));

                setHourBookingSelected();
            }
        }
        if (textviewName.equalsIgnoreCase("dailytime")) {
            ParkerBookingStayScreen.tvDailyTimePick.setText(t);
            if (ParkerBookingStayScreen.tvHalfDay.isSelected())
                ParkerBookingStayScreen.tvDailyEndTime.setText(Constants.addMinTime(t, 360));
            else if (ParkerBookingStayScreen.tvFullDay.isSelected())
                ParkerBookingStayScreen.tvDailyEndTime.setText(Constants.addMinTime(t, 720));

            setDailyBookingSelected();
        }
        if (textviewName.equalsIgnoreCase("weektime")) {
            ParkerBookingStayScreen.tvWeeklyTimePick.setText(t);
            ParkerBookingStayScreen.tvWeekEndTime.setText(t);

        }
        if (textviewName.equalsIgnoreCase("monthtime")) {
            ParkerBookingStayScreen.tvMonthTimePick.setText(t);
            ParkerBookingStayScreen.tvMonthEndTime.setText(t);

        }
    }

    private class DisabledColorDecorator implements DayDecorator {
        @Override
        public void decorate(DayView dayView) {
            try {
                SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
                SimpleDateFormat formatter3 = new SimpleDateFormat("dd MMM yyyy");
                String dayname = formatter.format(dayView.getDate());
                String dayname2 = formatter2.format(dayView.getDate());
                String day3 = formatter3.format(dayView.getDate());
                HashSet<String> strings = new HashSet<>();
                int color = Color.parseColor("#E8E8E8");
                try {
                    if (dataContext.propertyAvailableTimesObjectSet.size() > 0) {
                        for (tblPropertyAvailableTimes obj : dataContext.propertyAvailableTimesObjectSet) {
                            if (obj.DayName.equalsIgnoreCase("sunday") && !obj.Timing.isEmpty()) {
                                if (dayname.equalsIgnoreCase("sunday")) {
                                    dayView.setBackgroundResource(R.drawable.rounded_border_white_with_small_radius);
                                    strings.add("sunday");
                                }
                            } else if (obj.DayName.equalsIgnoreCase("monday") && !obj.Timing.isEmpty()) {
                                if (dayname.equalsIgnoreCase("monday")) {
                                    dayView.setBackgroundResource(R.drawable.rounded_border_white_with_small_radius);
                                    strings.add("monday");
                                }
                            } else if (obj.DayName.equalsIgnoreCase("tuesday") && !obj.Timing.isEmpty()) {
                                if (dayname.equalsIgnoreCase("tuesday")) {
                                    dayView.setBackgroundResource(R.drawable.rounded_border_white_with_small_radius);
                                    strings.add("tuesday");
                                }
                            } else if (obj.DayName.equalsIgnoreCase("wednesday") && !obj.Timing.isEmpty()) {
                                if (dayname.equalsIgnoreCase("wednesday")) {
                                    dayView.setBackgroundResource(R.drawable.rounded_border_white_with_small_radius);
                                    strings.add("wednesday");
                                }
                            } else if (obj.DayName.equalsIgnoreCase("thursday") && !obj.Timing.isEmpty()) {
                                if (dayname.equalsIgnoreCase("thursday")) {
                                    dayView.setBackgroundResource(R.drawable.rounded_border_white_with_small_radius);
                                    strings.add("thursday");
                                }
                            } else if (obj.DayName.equalsIgnoreCase("friday") && !obj.Timing.isEmpty()) {
                                if (dayname.equalsIgnoreCase("friday")) {
                                    dayView.setBackgroundResource(R.drawable.rounded_border_white_with_small_radius);
                                    strings.add("friday");
                                }
                            } else if (obj.DayName.equalsIgnoreCase("saturday") && !obj.Timing.isEmpty()) {
                                if (dayname.equalsIgnoreCase("saturday")) {
                                    dayView.setBackgroundResource(R.drawable.rounded_border_white_with_small_radius);
                                    strings.add("saturday");
                                    //dayView.setBackgroundResource(R.drawable.border_button_with_fill_calender_two);
                                }
                            }
                        }

                        try {

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            Date date1 = sdf.parse(dayname2);
                            Date date2 = sdf.parse(DateFormat.format("yyyy-MM-dd", new Date().getTime()).toString());


                            // Create 2 dates ends
                            //1

                            // Date object is having 3 methods namely after,before and equals for comparing
                            // after() will return true if and only if date1 is after date 2
                            // before() will return true if and only if date1 is before date2
                            if (date1.before(date2)) {
                                dayView.setBackgroundResource(R.drawable.border_button_with_fill_calender_two);
                                dayView.setClickable(false);
                            }
//                            if(date1.after(date2)){
//
//                            }
                            if (!strings.contains(dayname.toLowerCase())) {
                                dayView.setOnClickListener(null);
                            }

                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                        if (llWeeklyStay.isSelected()) {
                            if (weekdate.contains(day3)) {
                                dayView.setBackgroundResource(R.drawable.border_button_with_fill_calender_three_selected_day_color);
                            }

                        }
                        if (llMonthlyStay.isSelected()) {
                            if (monthdate.contains(day3)) {
                                dayView.setBackgroundResource(R.drawable.border_button_with_fill_calender_three_selected_day_color);
                            }

                        }

                        if (DateFormat.format("yyyy-MM-dd", new Date().getTime()).toString().equalsIgnoreCase(dayname2)) {
                            dayView.setBackgroundResource(R.drawable.border_button_with_fill_calendar);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //System.out.println("Date = "+format + "\n Day Name = "+format2);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            if (CalendarUtils.isPastDay(dayView.getDate())) {
//                int color = Color.parseColor("#a9afb9");
//                dayView.setBackgroundColor(color);
//            }
        }
    }
    public static String compareDates(String d1, String d2) {
        String data = "";
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = sdf.parse(d1);
            Date date2 = sdf.parse(d2);

            // Create 2 dates ends
            //1

            // Date object is having 3 methods namely after,before and equals for comparing
            // after() will return true if and only if date1 is after date 2
            if (date1.after(date2)) {
                data = "Date1 is after Date2";
            }
            // before() will return true if and only if date1 is before date2
            if (date1.before(date2)) {
                data = "Date1 is before Date2";
            }

            //equals() returns true if both the dates are equal
            if (date1.equals(date2)) {
                data = "Date1 is equal Date2";
            }


        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return data;
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


    public static String bookingSelectesTimes(String d1, String d2,String d3) {
        String data = "";
        try {
            // If you already have date objects then skip 1

            //1
            // Create 2 dates starts
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            Date date1 = sdf.parse(d1);
            Date date2 = sdf.parse(d2);
            Date date3 = sdf.parse(d3);


            // Create 2 dates ends
            //1

            // Date object is having 3 methods namely after,before and equals for comparing
            // after() will return true if and only if date1 is after date 2
            if (date3.after(date1) && date3.before(date2)) {
                data = "yes";
            }
            else if (date3.equals(date1) || date3.equals(date1)) {
                data = "yes";
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return data;
    }

    @Override
    protected void onStart() {
        super.onStart();
        s1 = 0;
        s2 = 0;
        s3 = 0;
        s4 = 0;
        s5 = 0;
    }




    public String BookingslotName="";
    private void getHourBooking(String date,String propertyID,String slotname) {
        try {
            BookingslotName=slotname;
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(ParkerBookingStayScreen.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.getBookedCarList(WebUtility.GET_BOOKED_CAR_LIST,date,propertyID,slotname);
                responseBodyCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Utility.hideProgress();

                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());
                            int CODE=jsonObject.getInt("error_code");
                            String msg=jsonObject.optString("error_message");
                            if(CODE==0){
                                JSONArray array=jsonObject.optJSONArray("data");
                                for(int i=0;i<array.length();i++){
                                    HourTimeModel model=new HourTimeModel();
                                    model.HourStartTime=array.optJSONObject(i).optString("start_time");
                                    model.HourEndTime=array.optJSONObject(i).optString("end_time");
                                    model.in_time=array.optJSONObject(i).optString("in_time");
                                    model.IS_BOOKED=array.optJSONObject(i).optInt("is_booked");
                                    //model.Status=array.optJSONObject(i).optString("");
                                    timeModels.add(model);
                                }
                            }

                            if(hourbookingAdapter!=null) {
                                hourbookingAdapter.notifyDataSetChanged();
//                                if(slotname.equalsIgnoreCase("daily_stay")){
//                                    setDailyBookingSelected();
//                                }else{
//                                    setHourBookingSelected();
//
//                                }
                            }
                            if(shortBookingAdapter!=null) {
                                shortBookingAdapter.notifyDataSetChanged();
                                //setShortBookingSelected();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Utility.hideProgress();
                    }
                });

            }
        } catch (Exception ex) {
        }
    }

}

//        else {
//                llShortStay.setVisibility(View.GONE);
//                llHourStay.setVisibility(View.GONE);
//                llWeeklyStay.setVisibility(View.GONE);
//                llDailyStay.setVisibility(View.GONE);
//                llMonthlyStay.setVisibility(View.GONE);
//
//                tvMonthlyStay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_left_normal_circle, 0, R.drawable.drawable_arrow_down, 0);
//                tvShortStay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_left_normal_circle, 0, R.drawable.drawable_arrow_down, 0);
//                tvWeeklyStay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_left_normal_circle, 0, R.drawable.drawable_arrow_down, 0);
//                tvDailyStay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_left_normal_circle, 0, R.drawable.drawable_arrow_down, 0);
//                tvHourStay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_left_normal_circle, 0, R.drawable.drawable_arrow_down, 0);
//
//                tvShortStayDetails.setVisibility(View.VISIBLE);
//                tvDailyStayDetails.setVisibility(View.VISIBLE);
//                tvHourStayDetails.setVisibility(View.VISIBLE);
//                tvWeeklyStayDetails.setVisibility(View.VISIBLE);
//                tvMonthlyStayDetails.setVisibility(View.VISIBLE);
//
//                }
