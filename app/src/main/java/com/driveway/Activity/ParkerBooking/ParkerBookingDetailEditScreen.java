package com.driveway.Activity.ParkerBooking;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.driveway.Activity.BaseActivity;
import com.driveway.Activity.ParkerBookingStayScreen;
import com.driveway.Activity.ReportScreen;
import com.driveway.Adapters.HourBookingAdapter;
import com.driveway.Adapters.ShortBookingAdapter;
import com.driveway.Component.CalendarListener;
import com.driveway.Component.DayDecorator;
import com.driveway.Component.DayView;
import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.DBHelper.tblPropertyAvailableTimes;
import com.driveway.DBHelper.tblStay;
import com.driveway.Model.ChatModel;
import com.driveway.Model.EditBookingModel;
import com.driveway.Model.HourTimeModel;
import com.driveway.Model.ParkerBookingList;
import com.driveway.R;
import com.driveway.Utility.Constants;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.driveway.listeners.onBookingListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonObject;
import com.mobandme.ada.exceptions.AdaFrameworkException;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@EActivity(R.layout.parker_booking_edit_screen)
public class ParkerBookingDetailEditScreen extends BaseActivity {

    @ViewById
    AppCompatImageView back;
    @ViewById
    TTextView tvPropertyTitle;
    @ViewById
    TTextView tvPropertyKM;
    @ViewById
    TTextView PropertyAddress;
    @ViewById
    TTextView tvParking;
    @ViewById
    TTextView tvMinutes;
    @ViewById
    TTextView tvRatings;
    @ViewById
    TTextView tvAvailability;
    @ViewById
    TTextView UserName;

    @ViewById
    LinearLayout ll1;
    @ViewById
    TTextView tvShortStay;
    @ViewById
    TTextView tvShortStayDetails;
    @ViewById
    LinearLayout llShortStay;
    @ViewById
    TTextView tv15;
    @ViewById
    TTextView tv30;
    @ViewById
    TTextView tv60;
    @ViewById
    TTextView tvShortStartDate;
    @ViewById
    TTextView tvShortEndDate;
    @ViewById
    com.driveway.Component.CustomCalendarView calendarView;
    @ViewById
    TTextView tvShortTimePick;
    @ViewById
    TTextView tvShortEndTimePick;



    @ViewById
    LinearLayout ll2;
    @ViewById
    TTextView tvHourStay;
    @ViewById
    TTextView tvHourStayDetails;
    @ViewById
    LinearLayout llHourStay;
    @ViewById
    TTextView tv1_4;
    @ViewById
    TTextView tv5_8;
    @ViewById
    TTextView tv8;
    @ViewById
    TTextView tvHourStartDate;
    @ViewById
    TTextView tvHourEndDate;
    @ViewById
    com.driveway.Component.CustomCalendarView calendarViewHour;
    @ViewById
    TTextView tvHourTimePick;
    @ViewById
    TTextView textViewHourEndTime;




    @ViewById
    LinearLayout ll3;
    @ViewById
    TTextView tvDailyStay;
    @ViewById
    TTextView tvDailyStayDetails;
    @ViewById
    LinearLayout llDailyStay;
    @ViewById
    TTextView tvHalfDay;
    @ViewById
    TTextView tvFullDay;
    @ViewById
    LinearLayout llDailySelectDate;
    @ViewById
    TTextView tvDailyStartDate;
    @ViewById
    TTextView tvDailyEndDate;
    @ViewById
    com.driveway.Component.CustomCalendarView calendarViewDaily;
    @ViewById
    TTextView tvDailyTimePick;
    @ViewById
    TTextView tvDailyEndTime;


    @ViewById
    LinearLayout ll4;
    @ViewById
    TTextView tvWeeklyStay;
    @ViewById
    TTextView tvWeeklyStayDetails;
    @ViewById
    LinearLayout llWeeklyStay;
    @ViewById
    TTextView tv3Days;
    @ViewById
    TTextView tv5Days;
    @ViewById
    TTextView tv7Days;
    @ViewById
    TTextView tvWeeklyStartDate;
    @ViewById
    TTextView tvWeeklyTimePick;
    @ViewById
    TTextView tvWeeklyEndDate;
    @ViewById
    TTextView tvWeekEndTime;
    @ViewById
    com.driveway.Component.CustomCalendarView calendarViewWeekly;

    @ViewById
    LinearLayout ll5;
    @ViewById
    TTextView tvMonthlyStay;
    @ViewById
    TTextView tvMonthlyStayDetails;
    @ViewById
    LinearLayout llMonthlyStay;
    @ViewById
    TTextView tv14Days;
    @ViewById
    TTextView tv30Days;
    @ViewById
    TTextView tvMonthlyStartDate;
    @ViewById
    TTextView tvMonthTimePick;
    @ViewById
    TTextView tvMonthlyEndDate;
    @ViewById
    TTextView tvMonthEndTime;
    @ViewById
    com.driveway.Component.CustomCalendarView calendarViewMonthly;

    @ViewById
    TTextView btnBooking;

    @ViewById
    TTextView tv1_hour;

    @ViewById
    TTextView tv2_hour;

    @ViewById
    TTextView tv3_hour;

    @ViewById
    TTextView tv4_hour;


    @ViewById
    RelativeLayout rlBg;

    @ViewById
    LinearLayout llHourStayHourSelection;

    @ViewById
            TTextView tvCount;

    @ViewById
    RecyclerView rvHourTime;
    @ViewById
    RecyclerView rvShortTime;
    @ViewById
    RecyclerView rvDailyTime;

    ParkerBookingList obj;

    public static String shortStayPrice = "0";
    public static String hourStayPrice = "0";
    public static String dailyStayPrice = "0";
    public static String weeklyStayPrice = "0";
    public static String monthlyStayPrice = "0";
    int s1 = 0;
    int s2 = 0;
    int s3 = 0;
    int s4 = 0;
    int s5 = 0;
    int currentBookingSlot=0;

    HashSet<String> weekdate=new HashSet<>();
    HashSet<String> monthdate=new HashSet<>();
    ArrayList<ChatModel> list = new ArrayList<>();
    FirebaseDatabase database;
    public DisabledColorDecorator disabledColorDecorator=new DisabledColorDecorator();
    ArrayList<HourTimeModel> timeModels=new ArrayList<>();
    HourBookingAdapter hourbookingAdapter;
    ShortBookingAdapter shortBookingAdapter;
    @AfterViews
    public void init(){
        database = FirebaseDatabase.getInstance();
        obj = (ParkerBookingList) getIntent().getSerializableExtra("data");
        if (obj != null) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (obj != null && !obj.bookingProperttyImage.isEmpty()) {
                    Picasso.with(this).load(obj.bookingProperttyImage).into(new Target() {

                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            rlBg.setBackground(new BitmapDrawable(getResources(), bitmap));
                        }

                        @Override
                        public void onBitmapFailed(final Drawable errorDrawable) {
                            Log.d("TAG", "FAILED");
                        }

                        @Override
                        public void onPrepareLoad(final Drawable placeHolderDrawable) {
                            Log.d("TAG", "Prepare Load");
                        }
                    });
                }

            }, 00);

            tvPropertyTitle.setText(obj.bookingPropertyTitle);
            PropertyAddress.setText(obj.bookingPropertyAddress);
            tvPropertyKM.setText(obj.bookingPropertyDistance);
            tvAvailability.setText(obj.bookingPropertyAvailable+" Available");
            tvParking.setText(obj.bookingPropertyParkingType);
            tvMinutes.setText(obj.bookingPropertyDuration);
            tvRatings.setText(obj.bookingPropertyRating);
            UserName.setText(obj.owneruser.FullName);



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



        }
        transparentStatusbar();
        setPriceSlots();
        if(obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.SHORT_STAY)){
            tvShortStartDate.setText(Constants.converDate(obj.bookingStartDate));
            tvShortEndDate.setText(Constants.converDate(obj.bookingEndDate));
            tvShortTimePick.setText(obj.bookingStartTime);
            tvShortEndTimePick.setText(obj.bookingEndTime);
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            showShortTimeLog(tvShortStartDate.getText().toString(),obj.propertID);
            //setShortBookingSelected();
            //setShowShortBookingSelected();

            Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
            SimpleDateFormat df1 = new SimpleDateFormat("dd MMM yyyy");
            SimpleDateFormat df2 = new SimpleDateFormat("MMMM");
            Date date1 = null;
            try {
                date1 = df1 .parse(tvShortEndDate.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            currentCalendar.setTime(date1);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                List<DayDecorator> decorators = new ArrayList<>();
                decorators.add(disabledColorDecorator);
                calendarView.setDecorators(decorators);
                calendarView.refreshCalendar(currentCalendar);
            }, 100);


        } else if(obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.HOUR_STAY)){
            tvHourStartDate.setText(Constants.converDate(obj.bookingStartDate));
            tvHourEndDate.setText(Constants.converDate(obj.bookingEndDate));
            tvHourTimePick.setText(obj.bookingStartTime);
            textViewHourEndTime.setText(obj.bookingEndTime);
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            showHourTimeLog(tvHourStartDate.getText().toString(),obj.propertID);

            if(obj.bookingPropertyStayTime.equalsIgnoreCase(Constants._1_4_HOUR)){
                    tv1_4.setSelected(true);
                tv5_8.setSelected(false);
                tv8.setSelected(false);
            }
            else if(obj.bookingPropertyStayTime.equalsIgnoreCase(Constants._4_8_HOUR)){
                tv1_4.setSelected(false);
                tv5_8.setSelected(true);
                tv8.setSelected(false);
            }
            else if(obj.bookingPropertyStayTime.equalsIgnoreCase(Constants._8_PLUS_HOUR)){
                tv8.setSelected(true);
                tv5_8.setSelected(false);
                tv1_4.setSelected(false);
            }

            if(tv1_4.isSelected()) {
                if (obj.HourTime.equalsIgnoreCase("1")) {
                    tv1_hour.setSelected(true);
                    tv2_hour.setSelected(false);
                    tv3_hour.setSelected(false);
                    tv4_hour.setSelected(false);

                    tv1_hour.setTextColor(getResources().getColor(R.color.white));
                    tv1_hour.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                    tv2_hour.setTextColor(getResources().getColor(R.color.black));
                    tv2_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv3_hour.setTextColor(getResources().getColor(R.color.black));
                    tv3_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv4_hour.setTextColor(getResources().getColor(R.color.black));
                    tv4_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);
                }
                else if (obj.HourTime.equalsIgnoreCase("2")) {
                    tv2_hour.setSelected(true);
                    tv1_hour.setSelected(false);
                    tv3_hour.setSelected(false);
                    tv4_hour.setSelected(false);

                    tv2_hour.setTextColor(getResources().getColor(R.color.white));
                    tv2_hour.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                    tv1_hour.setTextColor(getResources().getColor(R.color.black));
                    tv1_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv3_hour.setTextColor(getResources().getColor(R.color.black));
                    tv3_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv4_hour.setTextColor(getResources().getColor(R.color.black));
                    tv4_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);
                }
                else if (obj.HourTime.equalsIgnoreCase("3")) {
                    tv3_hour.setSelected(true);
                    tv2_hour.setSelected(false);
                    tv1_hour.setSelected(false);
                    tv4_hour.setSelected(false);

                    tv3_hour.setTextColor(getResources().getColor(R.color.white));
                    tv3_hour.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                    tv2_hour.setTextColor(getResources().getColor(R.color.black));
                    tv2_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv1_hour.setTextColor(getResources().getColor(R.color.black));
                    tv1_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv4_hour.setTextColor(getResources().getColor(R.color.black));
                    tv4_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);
                }
                else if (obj.HourTime.equalsIgnoreCase("4")) {
                    tv4_hour.setSelected(true);
                    tv2_hour.setSelected(false);
                    tv3_hour.setSelected(false);
                    tv1_hour.setSelected(false);

                    tv4_hour.setTextColor(getResources().getColor(R.color.white));
                    tv4_hour.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                    tv2_hour.setTextColor(getResources().getColor(R.color.black));
                    tv2_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv3_hour.setTextColor(getResources().getColor(R.color.black));
                    tv3_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv1_hour.setTextColor(getResources().getColor(R.color.black));
                    tv1_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);
                }
            }
            else if(tv5_8.isSelected()) {
                if (obj.HourTime.equalsIgnoreCase("5")) {
                    tv1_hour.setSelected(true);
                    tv2_hour.setSelected(false);
                    tv3_hour.setSelected(false);
                    tv4_hour.setSelected(false);

                    tv1_hour.setTextColor(getResources().getColor(R.color.white));
                    tv1_hour.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                    tv2_hour.setTextColor(getResources().getColor(R.color.black));
                    tv2_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv3_hour.setTextColor(getResources().getColor(R.color.black));
                    tv3_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv4_hour.setTextColor(getResources().getColor(R.color.black));
                    tv4_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);
                }
                else if (obj.HourTime.equalsIgnoreCase("6")) {
                    tv2_hour.setSelected(true);
                    tv1_hour.setSelected(false);
                    tv3_hour.setSelected(false);
                    tv4_hour.setSelected(false);

                    tv2_hour.setTextColor(getResources().getColor(R.color.white));
                    tv2_hour.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                    tv1_hour.setTextColor(getResources().getColor(R.color.black));
                    tv1_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv3_hour.setTextColor(getResources().getColor(R.color.black));
                    tv3_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv4_hour.setTextColor(getResources().getColor(R.color.black));
                    tv4_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);
                }
                else if (obj.HourTime.equalsIgnoreCase("7")) {
                    tv3_hour.setSelected(true);
                    tv2_hour.setSelected(false);
                    tv1_hour.setSelected(false);
                    tv4_hour.setSelected(false);

                    tv3_hour.setTextColor(getResources().getColor(R.color.white));
                    tv3_hour.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                    tv2_hour.setTextColor(getResources().getColor(R.color.black));
                    tv2_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv1_hour.setTextColor(getResources().getColor(R.color.black));
                    tv1_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv4_hour.setTextColor(getResources().getColor(R.color.black));
                    tv4_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);
                }
                else if (obj.HourTime.equalsIgnoreCase("8")) {
                    tv4_hour.setSelected(true);
                    tv2_hour.setSelected(false);
                    tv3_hour.setSelected(false);
                    tv1_hour.setSelected(false);

                    tv4_hour.setTextColor(getResources().getColor(R.color.white));
                    tv4_hour.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                    tv2_hour.setTextColor(getResources().getColor(R.color.black));
                    tv2_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv3_hour.setTextColor(getResources().getColor(R.color.black));
                    tv3_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv1_hour.setTextColor(getResources().getColor(R.color.black));
                    tv1_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);
                }
            }
            else if(tv8.isSelected()) {
                if (obj.HourTime.equalsIgnoreCase("9")) {
                    tv1_hour.setSelected(true);
                    tv2_hour.setSelected(false);
                    tv3_hour.setSelected(false);
                    tv4_hour.setSelected(false);

                    tv1_hour.setTextColor(getResources().getColor(R.color.white));
                    tv1_hour.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                    tv2_hour.setTextColor(getResources().getColor(R.color.black));
                    tv2_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv3_hour.setTextColor(getResources().getColor(R.color.black));
                    tv3_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv4_hour.setTextColor(getResources().getColor(R.color.black));
                    tv4_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);
                }
                else if (obj.HourTime.equalsIgnoreCase("10")) {
                    tv2_hour.setSelected(true);
                    tv1_hour.setSelected(false);
                    tv3_hour.setSelected(false);
                    tv4_hour.setSelected(false);

                    tv2_hour.setTextColor(getResources().getColor(R.color.white));
                    tv2_hour.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                    tv1_hour.setTextColor(getResources().getColor(R.color.black));
                    tv1_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv3_hour.setTextColor(getResources().getColor(R.color.black));
                    tv3_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv4_hour.setTextColor(getResources().getColor(R.color.black));
                    tv4_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                }
                else if (obj.HourTime.equalsIgnoreCase("11")) {
                    tv3_hour.setSelected(true);
                    tv2_hour.setSelected(false);
                    tv1_hour.setSelected(false);
                    tv4_hour.setSelected(false);

                    tv3_hour.setTextColor(getResources().getColor(R.color.white));
                    tv3_hour.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                    tv2_hour.setTextColor(getResources().getColor(R.color.black));
                    tv2_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv1_hour.setTextColor(getResources().getColor(R.color.black));
                    tv1_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv4_hour.setTextColor(getResources().getColor(R.color.black));
                    tv4_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                }
                else if (obj.HourTime.equalsIgnoreCase("12")) {
                    tv4_hour.setSelected(true);
                    tv2_hour.setSelected(false);
                    tv3_hour.setSelected(false);
                    tv1_hour.setSelected(false);

                    tv4_hour.setTextColor(getResources().getColor(R.color.white));
                    tv4_hour.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                    tv2_hour.setTextColor(getResources().getColor(R.color.black));
                    tv2_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv3_hour.setTextColor(getResources().getColor(R.color.black));
                    tv3_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv1_hour.setTextColor(getResources().getColor(R.color.black));
                    tv1_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);
                }
            }

            setHourBookingSelected();

            Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
            SimpleDateFormat df1 = new SimpleDateFormat("dd MMM yyyy");
            SimpleDateFormat df2 = new SimpleDateFormat("MMMM");
            Date date1 = null;
            try {
                date1 = df1 .parse(tvHourEndDate.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            currentCalendar.setTime(date1);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                List<DayDecorator> decorators = new ArrayList<>();
                decorators.add(disabledColorDecorator);
                calendarViewHour.setDecorators(decorators);
                calendarViewHour.refreshCalendar(currentCalendar);
            }, 100);


        }else if(obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.DAY_STAY)){
            tvDailyStartDate.setText(Constants.converDate(obj.bookingStartDate));
            tvDailyEndDate.setText(Constants.converDate(obj.bookingEndDate));
            tvDailyTimePick.setText(obj.bookingStartTime);
            tvDailyEndTime.setText(obj.bookingEndTime);
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            showDailyTimeLog(tvDailyStartDate.getText().toString(),obj.propertID);

            setDailyBookingSelected();

            Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
            SimpleDateFormat df1 = new SimpleDateFormat("dd MMM yyyy");
            SimpleDateFormat df2 = new SimpleDateFormat("MMMM");
            Date date1 = null;
            try {
                date1 = df1 .parse(tvDailyEndDate.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            currentCalendar.setTime(date1);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                List<DayDecorator> decorators = new ArrayList<>();
                decorators.add(disabledColorDecorator);
                calendarViewDaily.setDecorators(decorators);
                calendarViewDaily.refreshCalendar(currentCalendar);
            }, 100);

        }else if(obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.WEEK_STAY)){
            tvWeeklyStartDate.setText(Constants.converDate(obj.bookingStartDate));
            tvWeeklyEndDate.setText(Constants.converDate(obj.bookingEndDate));
            tvWeeklyTimePick.setText(obj.bookingStartTime);
            tvWeekEndTime.setText(obj.bookingEndTime);

            weekdate.clear();
            weekdate.addAll(getDates(tvWeeklyStartDate.getText().toString(),tvWeeklyEndDate.getText().toString()));
            Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
            SimpleDateFormat df1 = new SimpleDateFormat("dd MMM yyyy");
            SimpleDateFormat df2 = new SimpleDateFormat("MMMM");
            Date date1 = null;
            Date date2 = null;
            try {
                date1 = df1 .parse(tvWeeklyEndDate.getText().toString());
                date2 = df2 .parse(tvWeeklyEndDate.getText().toString());
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


        }else if(obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.MONTH_STAY)){
            tvMonthlyStartDate.setText(Constants.converDate(obj.bookingStartDate));
            tvMonthlyEndDate.setText(Constants.converDate(obj.bookingEndDate));
            tvMonthTimePick.setText(obj.bookingStartTime);
            tvMonthEndTime.setText(obj.bookingEndTime);

            monthdate.clear();
            monthdate.addAll(getDates(tvMonthlyStartDate.getText().toString(),tvMonthlyEndDate.getText().toString()));
            Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
            SimpleDateFormat df1 = new SimpleDateFormat("dd MMM yyyy");
            Date date1 = null;
            try {
                date1 = df1 .parse(tvMonthlyStartDate.getText().toString());
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
        }
        inilizeCalendar();

        try {
            dataContext.tblUserObjectSet.fill();
            //loadChatData();
        } catch (AdaFrameworkException e) {
            e.printStackTrace();
        }
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

                if(timeModels.get(i).IS_BOOKED==1 && timeModels.get(i).IS_SELECTED==1) {
                    timeModels.get(i).IS_SELECTED = 1;
                }

                if(timeModels.get(i).IS_BOOKED==0)
                    timeModels.get(i).IS_SELECTED=1;
                else {
                    Utility.showAlert(this,"Sorry, selected parking space is not available for booking.");
                    for(int j=0;j<timeModels.size();j++){
                        if(timeModels.get(j).IS_SELECTED==1 && timeModels.get(j).IS_BOOKED==1){
                            timeModels.get(j).IS_SELECTED=0;
                        }
                    }
                    break;
                }
                rvDailyTime.scrollToPosition(i);
            }
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
                if(timeModels.get(i).IS_BOOKED==0) {
                    timeModels.get(i).IS_SELECTED = 1;
                }else {
                    Utility.showAlert(this,"Sorry, selected parking space is not available for booking.");
                    for(int j=0;j<timeModels.size();j++){
                        if(timeModels.get(j).IS_SELECTED==1){
                            timeModels.get(j).IS_SELECTED=0;
                        }
                    }
                    break;
                }
                rvShortTime.scrollToPosition(i);
            }
        }
        if(shortBookingAdapter!=null)
            shortBookingAdapter.notifyDataSetChanged();
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
                    Utility.showAlert(this,"Sorry, selected parking space is not available for booking.");
                    for(int j=0;j<timeModels.size();j++){
                        if(timeModels.get(j).IS_SELECTED==1 && timeModels.get(j).IS_BOOKED==1){
                            timeModels.get(j).IS_SELECTED=0;
                        }
                    }
                    break;
                }
                rvHourTime.scrollToPosition(i);
            }
        }
        if(hourbookingAdapter!=null)
            hourbookingAdapter.notifyDataSetChanged();
    }

    public void setShowShortBookingSelected(){
        for(int i=0;i<timeModels.size();i++){
            if(timeModels.get(i).IS_SELECTED==1){
                timeModels.get(i).IS_SELECTED=0;
            }
        }
        if(shortBookingAdapter!=null)
            shortBookingAdapter.notifyDataSetChanged();

        for(int i=0;i<timeModels.size();i++){
            if(bookingSelectesTimes(tvShortTimePick.getText().toString(),tvShortEndTimePick.getText().toString(),timeModels.get(i).HourStartTime).equalsIgnoreCase("yes")){
                if(timeModels.get(i).IS_BOOKED==0 || timeModels.get(i).IS_BOOKED==1)
                    timeModels.get(i).IS_SELECTED = 1;
                rvShortTime.scrollToPosition(i);
            }
        }
        if(shortBookingAdapter!=null)
            shortBookingAdapter.notifyDataSetChanged();
    }
    public void setShowHourBookingSelected(){
        for(int i=0;i<timeModels.size();i++){
            if(timeModels.get(i).IS_SELECTED==1){
                timeModels.get(i).IS_SELECTED=0;
            }
        }
        if(hourbookingAdapter!=null)
            hourbookingAdapter.notifyDataSetChanged();

        for(int i=0;i<timeModels.size();i++){
            if(bookingSelectesTimes(tvHourTimePick.getText().toString(),textViewHourEndTime.getText().toString(),timeModels.get(i).HourStartTime).equalsIgnoreCase("yes")){
                if(timeModels.get(i).IS_BOOKED==0 || timeModels.get(i).IS_BOOKED==1)
                    timeModels.get(i).IS_SELECTED = 1;
                rvHourTime.scrollToPosition(i);
            }
        }
        if(hourbookingAdapter!=null)
            hourbookingAdapter.notifyDataSetChanged();
    }
    public void setShowDailyBookingSelected(){
        for(int i=0;i<timeModels.size();i++){
            if(timeModels.get(i).IS_SELECTED==1){
                timeModels.get(i).IS_SELECTED=0;
            }
        }
        if(hourbookingAdapter!=null)
            hourbookingAdapter.notifyDataSetChanged();

        for(int i=0;i<timeModels.size();i++){
            if(bookingSelectesTimes(tvDailyTimePick.getText().toString(),tvDailyEndTime.getText().toString(),timeModels.get(i).HourStartTime).equalsIgnoreCase("yes")){
                if(timeModels.get(i).IS_BOOKED==0 || timeModels.get(i).IS_BOOKED==1)
                    timeModels.get(i).IS_SELECTED = 1;
                rvDailyTime.scrollToPosition(i);
            }
        }
        if(hourbookingAdapter!=null)
            hourbookingAdapter.notifyDataSetChanged();
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
                        sdate = tvShortEndDate.getText().toString()+ " " + timeModel.HourStartTime;

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
                            if(shortBookingAdapter!=null)
                                shortBookingAdapter.notifyDataSetChanged();

                            Utility.showAlert(ParkerBookingDetailEditScreen.this, "You cannot select past time");
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
                        sdate = BookingslotName.equalsIgnoreCase("hourly_stay")?tvHourEndDate.getText().toString()+ " " + timeModel.HourStartTime:tvDailyEndDate.getText().toString() + " " + timeModel.HourStartTime;

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
                            Utility.showAlert(ParkerBookingDetailEditScreen.this, "You cannot select past time");
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
    public String BookingslotName="";
    private void getHourBooking(String date,String propertyID,String slotname) {
        try {
            BookingslotName=slotname;
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(ParkerBookingDetailEditScreen.this);
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
                                    model.IS_BOOKED=array.optJSONObject(i).optInt("is_booked");
                                    //model.Status=array.optJSONObject(i).optString("");
                                    timeModels.add(model);
                                }
                            }

                            if(hourbookingAdapter!=null) {
                                hourbookingAdapter.notifyDataSetChanged();
                                if(slotname.equalsIgnoreCase("hourly_stay"))
                                    setShowHourBookingSelected();
                                else if(slotname.equalsIgnoreCase("daily_stay"))
                                    setShowDailyBookingSelected();
                                    //setDailyBookingSelected();
//                                setHourBookingSelected();

                            }
                            if(shortBookingAdapter!=null) {
                                shortBookingAdapter.notifyDataSetChanged();
                                //setShortBookingSelected();
                                setShowShortBookingSelected();
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
    @Override
    protected void onStart() {
        super.onStart();
        s1 = 0;
        s2 = 0;
        s3 = 0;
        s4 = 0;
        s5 = 0;
    }

    @Click
    public void Report(){
        startActivity(new Intent(this, ReportScreen.class)
                .putExtra("booking",obj)
        );

    }

    @UiThread
    public void loadChatData() {
        try {
            database.getReference().child(Constants.CONVERSION).addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ChatModel model = new ChatModel();
                        if (dataContext.tblUserObjectSet.get(0).APIToken.equalsIgnoreCase(snapshot.child("receiver").getValue(String.class))) {
                            model.isRead = snapshot.child("isRead").getValue(Boolean.class);
                            if (model.isRead == false)
                                list.add(model);
                        }

                    }
                    if (list.size() > 0) {
                       // tvCount.setVisibility(View.VISIBLE);
                       // tvCount.setText(list.size() + "");
                    } else {
                       // tvCount.setVisibility(View.INVISIBLE);
                    }
                    System.out.println("SIZE===>" + list.size());

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    list.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ChatModel model = new ChatModel();
                        if (dataContext.tblUserObjectSet.get(0).APIToken.equalsIgnoreCase(snapshot.child("receiver").getValue(String.class))) {
                            model.isRead = snapshot.child("isRead").getValue(Boolean.class);
                            if (model.isRead == false)
                                list.add(model);
                        }

                    }
                    if (list.size() > 0) {
                       // tvCount.setVisibility(View.VISIBLE);
                       // tvCount.setText(list.size() + "");
                    } else {
                        //tvCount.setVisibility(View.INVISIBLE);
                    }
                    System.out.println("SIZE===>" + list.size());

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } catch (Exception ex) {

        }


    }

    @Click
    public void back(){
        finish();
    }

    @Click
    public void btnBooking(){

        if (!shortStayPrice.equalsIgnoreCase("0") || !hourStayPrice.equalsIgnoreCase("0") || !dailyStayPrice.equalsIgnoreCase("0") || !weeklyStayPrice.equalsIgnoreCase("0") || !monthlyStayPrice.equalsIgnoreCase("0")) {
            if (llShortStay.isSelected()) {
                if (tv15.isSelected())
                    editBooking("short_time", "15 mins", tvShortStartDate.getText().toString(), tvShortEndDate.getText().toString(), tvShortTimePick.getText().toString(), tvShortEndTimePick.getText().toString(), obj.bookingPropertyDistance, obj.bookingPropertyDuration, obj.bookingID,"");
                else if (tv30.isSelected())
                    editBooking("short_time", "30 mins", tvShortStartDate.getText().toString(), tvShortEndDate.getText().toString(), tvShortTimePick.getText().toString(), tvShortEndTimePick.getText().toString(), obj.bookingPropertyDistance, obj.bookingPropertyDuration, obj.bookingID,"");
                else if (tv60.isSelected())
                    editBooking("short_time", "45 mins", tvShortStartDate.getText().toString(), tvShortEndDate.getText().toString(), tvShortTimePick.getText().toString(), tvShortEndTimePick.getText().toString(), obj.bookingPropertyDistance, obj.bookingPropertyDuration, obj.bookingID,"");
            } else if (llHourStay.isSelected()) {
                if (tv1_4.isSelected()) {
                    if (tv1_hour.isSelected())
                        editBooking("hourly_time", "1 - 4 hours", tvHourStartDate.getText().toString(), tvHourEndDate.getText().toString(), tvHourTimePick.getText().toString(), textViewHourEndTime.getText().toString(), obj.bookingPropertyDistance, obj.bookingPropertyDuration, obj.bookingID,tv1_hour.getText().toString());
                    else if (tv2_hour.isSelected())
                        editBooking("hourly_time", "1 - 4 hours", tvHourStartDate.getText().toString(), tvHourEndDate.getText().toString(), tvHourTimePick.getText().toString(), textViewHourEndTime.getText().toString(), obj.bookingPropertyDistance, obj.bookingPropertyDuration, obj.bookingID,tv2_hour.getText().toString());
                    else if (tv3_hour.isSelected())
                        editBooking("hourly_time", "1 - 4 hours", tvHourStartDate.getText().toString(), tvHourEndDate.getText().toString(), tvHourTimePick.getText().toString(), textViewHourEndTime.getText().toString(), obj.bookingPropertyDistance, obj.bookingPropertyDuration, obj.bookingID,tv3_hour.getText().toString());
                    else if (tv4_hour.isSelected())
                        editBooking("hourly_time", "1 - 4 hours", tvHourStartDate.getText().toString(), tvHourEndDate.getText().toString(), tvHourTimePick.getText().toString(), textViewHourEndTime.getText().toString(), obj.bookingPropertyDistance, obj.bookingPropertyDuration, obj.bookingID,tv4_hour.getText().toString());
                }else if (tv5_8.isSelected()) {
                    if (tv1_hour.isSelected())
                        editBooking("hourly_time", "5 - 8 hours", tvHourStartDate.getText().toString(), tvHourEndDate.getText().toString(), tvHourTimePick.getText().toString(), textViewHourEndTime.getText().toString(), obj.bookingPropertyDistance, obj.bookingPropertyDuration, obj.bookingID,tv1_hour.getText().toString());
                    else if (tv2_hour.isSelected())
                        editBooking("hourly_time", "5 - 8 hours", tvHourStartDate.getText().toString(), tvHourEndDate.getText().toString(), tvHourTimePick.getText().toString(), textViewHourEndTime.getText().toString(), obj.bookingPropertyDistance, obj.bookingPropertyDuration, obj.bookingID,tv2_hour.getText().toString());
                    else if (tv3_hour.isSelected())
                        editBooking("hourly_time", "5 - 8 hours", tvHourStartDate.getText().toString(), tvHourEndDate.getText().toString(), tvHourTimePick.getText().toString(), textViewHourEndTime.getText().toString(), obj.bookingPropertyDistance, obj.bookingPropertyDuration, obj.bookingID,tv3_hour.getText().toString());
                    else if (tv4_hour.isSelected())
                        editBooking("hourly_time", "5 - 8 hours", tvHourStartDate.getText().toString(), tvHourEndDate.getText().toString(), tvHourTimePick.getText().toString(), textViewHourEndTime.getText().toString(), obj.bookingPropertyDistance, obj.bookingPropertyDuration, obj.bookingID,tv4_hour.getText().toString());
                }else if (tv8.isSelected()) {
                    if (tv1_hour.isSelected())
                        editBooking("hourly_time", "8+ hours", tvHourStartDate.getText().toString(), tvHourEndDate.getText().toString(), tvHourTimePick.getText().toString(), textViewHourEndTime.getText().toString(), obj.bookingPropertyDistance, obj.bookingPropertyDuration, obj.bookingID, tv1_hour.getText().toString());
                    else if (tv2_hour.isSelected())
                        editBooking("hourly_time", "8+ hours", tvHourStartDate.getText().toString(), tvHourEndDate.getText().toString(), tvHourTimePick.getText().toString(), textViewHourEndTime.getText().toString(), obj.bookingPropertyDistance, obj.bookingPropertyDuration, obj.bookingID, tv2_hour.getText().toString());
                    else if (tv3_hour.isSelected())
                        editBooking("hourly_time", "8+ hours", tvHourStartDate.getText().toString(), tvHourEndDate.getText().toString(), tvHourTimePick.getText().toString(), textViewHourEndTime.getText().toString(), obj.bookingPropertyDistance, obj.bookingPropertyDuration, obj.bookingID, tv3_hour.getText().toString());
                    else if (tv4_hour.isSelected())
                        editBooking("hourly_time", "8+ hours", tvHourStartDate.getText().toString(), tvHourEndDate.getText().toString(), tvHourTimePick.getText().toString(), textViewHourEndTime.getText().toString(), obj.bookingPropertyDistance, obj.bookingPropertyDuration, obj.bookingID, tv4_hour.getText().toString());
                }
            } else if (llDailyStay.isSelected()) {
                if (tvHalfDay.isSelected())
                    editBooking("daily_time", "half day (up to 6 hours)", tvDailyStartDate.getText().toString(), tvDailyEndDate.getText().toString(), tvDailyTimePick.getText().toString(), tvDailyEndTime.getText().toString(), obj.bookingPropertyDistance, obj.bookingPropertyDuration, obj.bookingID,"");
                else if (tvFullDay.isSelected())
                    editBooking("daily_time", "full day (up to 12 hours)", tvDailyStartDate.getText().toString(), tvDailyEndDate.getText().toString(), tvDailyTimePick.getText().toString(), tvDailyEndTime.getText().toString(),obj.bookingPropertyDistance, obj.bookingPropertyDuration, obj.bookingID,"");

            } else if (llWeeklyStay.isSelected()) {
                if (tv3Days.isSelected())
                    editBooking("weekly_time", "3 days", tvWeeklyStartDate.getText().toString(), tvWeeklyEndDate.getText().toString(), tvWeeklyTimePick.getText().toString(), tvWeekEndTime.getText().toString(), obj.bookingPropertyDistance, obj.bookingPropertyDuration, obj.bookingID,"");
                else if (tv5Days.isSelected())
                    editBooking("weekly_time", "5 days", tvWeeklyStartDate.getText().toString(), tvWeeklyEndDate.getText().toString(), tvWeeklyTimePick.getText().toString(), tvWeekEndTime.getText().toString(), obj.bookingPropertyDistance, obj.bookingPropertyDuration, obj.bookingID,"");
                else if (tv7Days.isSelected())
                    editBooking("weekly_time", "7 days", tvWeeklyStartDate.getText().toString(), tvWeeklyEndDate.getText().toString(), tvWeeklyTimePick.getText().toString(), tvWeekEndTime.getText().toString(), obj.bookingPropertyDistance, obj.bookingPropertyDuration, obj.bookingID,"");

            } else if (llMonthlyStay.isSelected()) {
                if (tv14Days.isSelected())
                    editBooking("monthly_time", "14 days", tvMonthlyStartDate.getText().toString(), tvMonthlyEndDate.getText().toString(), tvMonthTimePick.getText().toString(), tvMonthEndTime.getText().toString(), obj.bookingPropertyDistance, obj.bookingPropertyDuration, obj.bookingID,"");
                else if (tv30Days.isSelected())
                    editBooking("monthly_time", "30 days", tvMonthlyStartDate.getText().toString(), tvMonthlyEndDate.getText().toString(),  tvMonthTimePick.getText().toString(), tvMonthEndTime.getText().toString(), obj.bookingPropertyDistance, obj.bookingPropertyDuration, obj.bookingID,"");

            } else {
            //    Utility.showAlert(ParkerBookingDetailEditScreen.this, Constants.SELECT_BOOKINGDATE);
            }
        } else {
            Utility.showAlert(ParkerBookingDetailEditScreen.this, Constants.BOOKING_ERROR_MESSAGE);
        }
    }
    @Click
    public void tvShortStay(){
        hideShowStay(llShortStay);
        showShortTimeLog(tvShortStartDate.getText().toString(),obj.propertID);
    }
    @Click
    public void tvHourStay(){
        hideShowStay(llHourStay);
        showHourTimeLog(tvHourStartDate.getText().toString(),obj.propertID);
    }
    @Click
    public void tvDailyStay(){
        hideShowStay(llDailyStay);
        showDailyTimeLog(tvDailyStartDate.getText().toString(),obj.propertID);
    }
    @Click
    public void tvWeeklyStay(){
        hideShowStay(llWeeklyStay);
    }
    @Click
    public void tvMonthlyStay(){
        hideShowStay(llMonthlyStay);
    }
    @Click
    public void tv15(){

        if(Integer.parseInt(getPrice(Constants.SHORT_STAY, Constants._15MIN))<Integer.parseInt(obj.bookingPropertyStayPrice)){
            Utility.showAlert(ParkerBookingDetailEditScreen.this,Constants.EDIT_MESSAGE);
        }else {
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
        }
    }
    @Click
    public void tv30(){
        if(Integer.parseInt(getPrice(Constants.SHORT_STAY, Constants._30MIN))<Integer.parseInt(obj.bookingPropertyStayPrice)){
            Utility.showAlert(ParkerBookingDetailEditScreen.this,Constants.EDIT_MESSAGE);
        }else {
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
        }
    }
    @Click
    public void tv60(){
        if(Integer.parseInt(getPrice(Constants.SHORT_STAY, Constants._45MIN))<Integer.parseInt(obj.bookingPropertyStayPrice)){
            Utility.showAlert(ParkerBookingDetailEditScreen.this,Constants.EDIT_MESSAGE);
        }else {
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
        }
    }
    @Click
    public void tv1_hour(){

        tv1_hour.setSelected(true);
        tv2_hour.setSelected(false);
        tv3_hour.setSelected(false);
        tv4_hour.setSelected(false);

        String str=Constants._1_4_HOUR;

        if(tv1_4.isSelected()){
            str=Constants._1_4_HOUR;
        }else if(tv5_8.isSelected()){
            str=Constants._4_8_HOUR;
        }else if(tv8.isSelected()){
            str=Constants._8_PLUS_HOUR;
        }

        if(Integer.parseInt(getPrice(Constants.HOUR_STAY, str))<Integer.parseInt(obj.bookingPropertyStayPrice)){
            Utility.showAlert(ParkerBookingDetailEditScreen.this,Constants.EDIT_MESSAGE);
        }else {
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
        }
    }

    @Click
    public void tv2_hour(){
        tv1_hour.setSelected(false);
        tv2_hour.setSelected(true);
        tv3_hour.setSelected(false);
        tv4_hour.setSelected(false);

        String str=Constants._1_4_HOUR;

        if(tv1_4.isSelected()){
            str=Constants._1_4_HOUR;
        }else if(tv5_8.isSelected()){
            str=Constants._4_8_HOUR;
        }else if(tv8.isSelected()){
            str=Constants._8_PLUS_HOUR;
        }
        System.out.println("BOOKING PRICE====>"+obj.bookingPrice);
        System.out.println("BOOKING STAY====>"+str);
        System.out.println("SELECTED PRICE====>"+getPrice(Constants.HOUR_STAY, str));

        if(Integer.parseInt(getPrice(Constants.HOUR_STAY, str))<Integer.parseInt(obj.bookingPropertyStayPrice)){
            Utility.showAlert(ParkerBookingDetailEditScreen.this,Constants.EDIT_MESSAGE);
        }else {
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
        }
    }

    @Click
    public void tv3_hour(){
        tv1_hour.setSelected(false);
        tv2_hour.setSelected(false);
        tv3_hour.setSelected(true);
        tv4_hour.setSelected(false);

        String str=Constants._1_4_HOUR;

        if(tv1_4.isSelected()){
            str=Constants._1_4_HOUR;
        }else if(tv5_8.isSelected()){
            str=Constants._4_8_HOUR;
        }else if(tv8.isSelected()){
            str=Constants._8_PLUS_HOUR;
        }
        System.out.println("BOOKING PRICE====>"+obj.bookingPrice);
        System.out.println("BOOKING STAY====>"+str);
        System.out.println("SELECTED PRICE====>"+getPrice(Constants.HOUR_STAY, str));

        if(Integer.parseInt(getPrice(Constants.HOUR_STAY, str))<Integer.parseInt(obj.bookingPropertyStayPrice)){
            Utility.showAlert(ParkerBookingDetailEditScreen.this,Constants.EDIT_MESSAGE);
        }else {
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
        }
    }
    @Click
    public void tv4_hour(){

        tv1_hour.setSelected(false);
        tv2_hour.setSelected(false);
        tv3_hour.setSelected(false);
        tv4_hour.setSelected(true);

        String str=Constants._1_4_HOUR;

        if(tv1_4.isSelected()){
            str=Constants._1_4_HOUR;
        }else if(tv5_8.isSelected()){
            str=Constants._4_8_HOUR;
        }else if(tv8.isSelected()){
            str=Constants._8_PLUS_HOUR;
        }

        if(Integer.parseInt(getPrice(Constants.HOUR_STAY,str))<Integer.parseInt(obj.bookingPropertyStayPrice)){
            Utility.showAlert(ParkerBookingDetailEditScreen.this,Constants.EDIT_MESSAGE);
        }else {
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
        }
    }


    @Click
    public void tv1_4(){
//        if(Integer.parseInt(getPrice(Constants.HOUR_STAY, Constants._1_4_HOUR))<Integer.parseInt(obj.bookingPrice)){
//            Utility.showAlert(ParkerBookingDetailEditScreen.this,Constants.EDIT_MESSAGE);
//        }else {
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
       // }
    }
    @Click
    public void tv5_8(){
//        if(Integer.parseInt(getPrice(Constants.HOUR_STAY, Constants._4_8_HOUR))<Integer.parseInt(obj.bookingPrice)){
//            Utility.showAlert(ParkerBookingDetailEditScreen.this,Constants.EDIT_MESSAGE);
//        }else {
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
        //}
    }
    @Click
    public void tv8(){
//        if(Integer.parseInt(getPrice(Constants.HOUR_STAY, Constants._8_PLUS_HOUR))<Integer.parseInt(obj.bookingPrice)){
//            Utility.showAlert(ParkerBookingDetailEditScreen.this,Constants.EDIT_MESSAGE);
//        }else {
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
       // }
    }
    @Click
    public void tvHalfDay(){
        if(Integer.parseInt(getPrice(Constants.DAY_STAY, Constants._HALFDAY))<Integer.parseInt(obj.bookingPropertyStayPrice)){
            Utility.showAlert(ParkerBookingDetailEditScreen.this,Constants.EDIT_MESSAGE);
        }else {
            tvHalfDay.setTextColor(getResources().getColor(R.color.white));
            tvHalfDay.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);
            tvFullDay.setTextColor(getResources().getColor(R.color.black));
            tvFullDay.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

            tvHalfDay.setSelected(true);
            tvFullDay.setSelected(false);
            dailyStayPrice = getPrice(Constants.DAY_STAY, Constants._HALFDAY);
            setTime("dailytime", tvDailyTimePick.getText().toString());
        }
    }
    @Click
    public void tvFullDay(){
        if(Integer.parseInt(getPrice(Constants.DAY_STAY, Constants._FULLDAY))<Integer.parseInt(obj.bookingPropertyStayPrice)){
            Utility.showAlert(ParkerBookingDetailEditScreen.this,Constants.EDIT_MESSAGE);
        }else {
            tvFullDay.setTextColor(getResources().getColor(R.color.white));
            tvFullDay.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);
            tvHalfDay.setTextColor(getResources().getColor(R.color.black));
            tvHalfDay.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

            tvHalfDay.setSelected(false);
            tvFullDay.setSelected(true);
            dailyStayPrice = getPrice(Constants.DAY_STAY, Constants._FULLDAY);
            setTime("dailytime", tvDailyTimePick.getText().toString());
        }
    }
    @Click
    public void tv3Days(){
        if(Integer.parseInt(getPrice(Constants.WEEK_STAY, Constants._3_DAY))<Integer.parseInt(obj.bookingPropertyStayPrice)){
            Utility.showAlert(ParkerBookingDetailEditScreen.this,Constants.EDIT_MESSAGE);
        }else {
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
            setCalendar(tvWeeklyStartDate, tvWeeklyEndDate, "3days", Constants.converDate_YYYYMMDD(tvWeeklyStartDate.getText().toString()));
        }
    }
    @Click
    public void tv5Days(){
        if(Integer.parseInt(getPrice(Constants.WEEK_STAY, Constants._5_DAY))<Integer.parseInt(obj.bookingPropertyStayPrice)){
            Utility.showAlert(ParkerBookingDetailEditScreen.this,Constants.EDIT_MESSAGE);
        }else {
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
        }
    }
    @Click
    public void tv7Days(){

        if(Integer.parseInt(getPrice(Constants.WEEK_STAY, Constants._7_DAY))<Integer.parseInt(obj.bookingPropertyStayPrice)){
            Utility.showAlert(ParkerBookingDetailEditScreen.this,Constants.EDIT_MESSAGE);
        }else {
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
        }
    }

    @Click
    public void  tv14Days(){
        if(Integer.parseInt(getPrice(Constants.MONTH_STAY, Constants._14_DAY))<Integer.parseInt(obj.bookingPropertyStayPrice)){
            Utility.showAlert(ParkerBookingDetailEditScreen.this,Constants.EDIT_MESSAGE);
        }else {
            tv14Days.setTextColor(getResources().getColor(R.color.white));
            tv14Days.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);
            tv30Days.setTextColor(getResources().getColor(R.color.black));
            tv30Days.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

            tv30Days.setSelected(false);
            tv14Days.setSelected(true);
            monthlyStayPrice = getPrice(Constants.MONTH_STAY, Constants._14_DAY);
            setCalendar(tvMonthlyStartDate, tvMonthlyEndDate, "14days", Constants.converDate_YYYYMMDD(tvMonthlyStartDate.getText().toString()));
        }
    }
    @Click
    public void  tv30Days(){
        if(Integer.parseInt(getPrice(Constants.MONTH_STAY, Constants._30_DAY))<Integer.parseInt(obj.bookingPropertyStayPrice)){
            Utility.showAlert(ParkerBookingDetailEditScreen.this,Constants.EDIT_MESSAGE);
        }else {
            tv30Days.setTextColor(getResources().getColor(R.color.white));
            tv30Days.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

            tv14Days.setTextColor(getResources().getColor(R.color.black));
            tv14Days.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

            tv14Days.setSelected(false);
            tv30Days.setSelected(true);
            monthlyStayPrice = getPrice(Constants.MONTH_STAY, Constants._30_DAY);
            setCalendar(tvMonthlyStartDate, tvMonthlyEndDate, "30days", Constants.converDate_YYYYMMDD(tvMonthlyStartDate.getText().toString()));
        }
    }
    @Click
    public void tvShortTimePick(){
        if (tv15.isSelected() || tv30.isSelected() || tv60.isSelected()) {
            if (tvShortEndDate.getText().length() > 0)
                openendTimePicker(ParkerBookingDetailEditScreen.this, "shorttime");
            else
                Utility.showAlert(ParkerBookingDetailEditScreen.this, "please select date for stay");
        } else {
            Utility.showAlert(ParkerBookingDetailEditScreen.this, "please select stay slot");
        }

    }
    @Click
    public void tvHourTimePick(){
        if (tv1_4.isSelected() || tv5_8.isSelected() || tv8.isSelected()) {
            if (tvHourEndDate.getText().length() > 0)
                openendTimePicker(ParkerBookingDetailEditScreen.this, "hourtime");
            else
                Utility.showAlert(ParkerBookingDetailEditScreen.this, "please select date for stay");
        } else {
            Utility.showAlert(ParkerBookingDetailEditScreen.this, "please select stay slot");
        }
    }
    @Click
    public void tvDailyTimePick(){
        if (tvHalfDay.isSelected() || tvFullDay.isSelected()) {
            if (tvDailyEndDate.getText().length() > 0)
                openendTimePicker(ParkerBookingDetailEditScreen.this, "dailytime");
            else
                Utility.showAlert(ParkerBookingDetailEditScreen.this, "please select date for stay");
        } else {
            Utility.showAlert(ParkerBookingDetailEditScreen.this, "please select stay slot");
        }
    }
    @Click
    public void tvMonthTimePick(){
        if (tv14Days.isSelected() || tv30Days.isSelected()) {
            if (tvMonthlyEndDate.getText().length() > 0)
                openendTimePicker(ParkerBookingDetailEditScreen.this, "monthtime");
            else
                Utility.showAlert(ParkerBookingDetailEditScreen.this, "please select date for stay");
        } else {
            Utility.showAlert(ParkerBookingDetailEditScreen.this, "please select stay slot");
        }
    }
    @Click
    public void tvWeeklyTimePick(){
        if (tv3Days.isSelected() || tv5Days.isSelected() || tv7Days.isSelected()) {
            if (tvWeeklyEndDate.getText().length() > 0)
                openendTimePicker(ParkerBookingDetailEditScreen.this, "weektime");
            else
                Utility.showAlert(ParkerBookingDetailEditScreen.this, "please select date for stay");
        } else {
            Utility.showAlert(ParkerBookingDetailEditScreen.this, "please select stay slot");
        }
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
//                    if (tvShortEndDate.getText().length() > 0) {
//                        sdate = tvShortEndDate.getText().toString() + " " + t;
//                    } else if (tvHourEndDate.getText().length() > 0) {
//                        sdate = tvHourEndDate.getText().toString() + " " + t;
//                    } else if (tvDailyEndDate.getText().length() > 0) {
//                        sdate = tvDailyEndDate.getText().toString() + " " + t;
//                    } else if (tvWeeklyEndDate.getText().length() > 0) {
//                        sdate = tvWeeklyEndDate.getText().toString() + " " + t;
//                    } else if (tvMonthlyEndDate.getText().length() > 0) {
//                        sdate = tvMonthlyEndDate.getText().toString() + " " + t;
//                    }
                    if(textview.equalsIgnoreCase("shorttime")){
                        sdate = tvShortEndDate.getText().toString() + " " + t;
                    }
                    else if(textview.equalsIgnoreCase("hourtime")){
                        sdate = tvHourEndDate.getText().toString() + " " + t;
                    }
                    else if(textview.equalsIgnoreCase("dailytime")){
                        sdate = tvDailyEndDate.getText().toString() + " " + t;
                    }else if(textview.equalsIgnoreCase("weektime")){
                        sdate = tvWeeklyEndDate.getText().toString() + " " + t;
                    }
                    else if(textview.equalsIgnoreCase("monthtime")){
                        sdate = tvMonthlyEndDate.getText().toString() + " " + t;
                    }

                    String data = compareTimes(DateFormat.format("dd MMM yyyy hh:mm a", new Date().getTime()).toString(), sdate);
                    if (data.equalsIgnoreCase("Time is equal Date2")) {
                        setTime(textview, t);
                    } else if (data.equalsIgnoreCase("Time is before Date2")) {
                        setTime(textview, t);
                    } else
                        Utility.showAlert(ParkerBookingDetailEditScreen.this, "You cannot select past time");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, mHour, mMinutes, false);
        timePickerDialog2.show();
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

            System.out.println("Time1=====" + sdf.format(date1));
            System.out.println("Time2=====" + sdf.format(date2));
            System.out.println();

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
        System.out.println("DATA IS=====" + data);
        return data;
    }


    public void hideShowStay(View ll){
        switch (ll.getId()){
            case R.id.llShortStay:
                shoHideLinearLayout(llShortStay,llHourStay,llDailyStay,llWeeklyStay,llMonthlyStay);
                showHideTextView(llShortStay,tvShortStayDetails,tvHourStayDetails,tvDailyStayDetails,tvWeeklyStayDetails,tvMonthlyStayDetails);
                selectedItem(tvShortStay,tvHourStay,tvDailyStay,tvMonthlyStay,tvWeeklyStay);
                llHourStayHourSelection.setVisibility(View.GONE);
                tv4_hour.setVisibility(View.GONE);
                tv3_hour.setVisibility(View.GONE);
                tv2_hour.setVisibility(View.GONE);
                tv1_hour.setVisibility(View.GONE);
                break;
            case R.id.llHourStay:
                shoHideLinearLayout(llHourStay,llShortStay,llDailyStay,llWeeklyStay,llMonthlyStay);
                showHideTextView(llHourStay,tvHourStayDetails,tvShortStayDetails,tvDailyStayDetails,tvWeeklyStayDetails,tvMonthlyStayDetails);
                selectedItem(tvHourStay,tvShortStay,tvDailyStay,tvMonthlyStay,tvWeeklyStay);
                llHourStayHourSelection.setVisibility(View.VISIBLE);
                tv4_hour.setVisibility(View.VISIBLE);
                tv3_hour.setVisibility(View.VISIBLE);
                tv2_hour.setVisibility(View.VISIBLE);
                tv1_hour.setVisibility(View.VISIBLE);
                break;
            case R.id.llDailyStay:
                shoHideLinearLayout(llDailyStay,llShortStay,llHourStay,llWeeklyStay,llMonthlyStay);
                showHideTextView(llDailyStay,tvDailyStayDetails,tvShortStayDetails,tvHourStayDetails,tvWeeklyStayDetails,tvMonthlyStayDetails);
                selectedItem(tvDailyStay,tvHourStay,tvShortStay,tvMonthlyStay,tvWeeklyStay);
                llHourStayHourSelection.setVisibility(View.GONE);
                tv4_hour.setVisibility(View.GONE);
                tv3_hour.setVisibility(View.GONE);
                tv2_hour.setVisibility(View.GONE);
                tv1_hour.setVisibility(View.GONE);
                break;
            case R.id.llWeeklyStay:
                shoHideLinearLayout(llWeeklyStay,llShortStay,llHourStay,llDailyStay,llMonthlyStay);
                showHideTextView(llWeeklyStay,tvWeeklyStayDetails,tvMonthlyStayDetails,tvDailyStayDetails,tvShortStayDetails,tvHourStayDetails);
                selectedItem(tvWeeklyStay,tvDailyStay,tvHourStay,tvShortStay,tvMonthlyStay);
                llHourStayHourSelection.setVisibility(View.GONE);
                tv4_hour.setVisibility(View.GONE);
                tv3_hour.setVisibility(View.GONE);
                tv2_hour.setVisibility(View.GONE);
                tv1_hour.setVisibility(View.GONE);
                break;
            case R.id.llMonthlyStay:
                shoHideLinearLayout(llMonthlyStay,llShortStay,llHourStay,llDailyStay,llWeeklyStay);
                showHideTextView(llMonthlyStay,tvMonthlyStayDetails,tvWeeklyStayDetails,tvDailyStayDetails,tvShortStayDetails,tvHourStayDetails);
                selectedItem(tvMonthlyStay,tvWeeklyStay,tvDailyStay,tvHourStay,tvShortStay);
                llHourStayHourSelection.setVisibility(View.GONE);
                tv4_hour.setVisibility(View.GONE);
                tv3_hour.setVisibility(View.GONE);
                tv2_hour.setVisibility(View.GONE);
                tv1_hour.setVisibility(View.GONE);
                break;
        }

    }
    public void shoHideLinearLayout(LinearLayout l1,LinearLayout l2,LinearLayout l3,LinearLayout l4,LinearLayout l5){
        l1.setVisibility(View.VISIBLE);
        l1.setSelected(true);
        l2.setVisibility(View.GONE);
        l2.setSelected(false);
        l3.setVisibility(View.GONE);
        l3.setSelected(false);
        l4.setVisibility(View.GONE);
        l4.setSelected(false);
        l5.setVisibility(View.GONE);
        l5.setSelected(false);
    }
    public void showHideTextView(LinearLayout l1,TTextView t1,TTextView t2,TTextView t3,TTextView t4,TTextView t5){
        if(l1.getVisibility()==View.VISIBLE) {
            t1.setVisibility(View.GONE);
            t2.setVisibility(View.GONE);
            t3.setVisibility(View.GONE);
            t4.setVisibility(View.GONE);
            t5.setVisibility(View.GONE);
        }
        else {
            t2.setVisibility(View.GONE);
            t3.setVisibility(View.GONE);
            t4.setVisibility(View.GONE);
            t4.setVisibility(View.GONE);
        }

    }
    private void selectedItem(TTextView t1,TTextView t2,TTextView t3,TTextView t4,TTextView t5){
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

    private void setPriceSlots(){

        System.out.println("SLOT NAME ====>"+obj.bookingPropertyStayTitle);
        if(obj.stays.size()>0){
            for(tblStay stay:obj.stays){
                System.out.println("STAY NAME ====>"+stay.Stay);
                if(stay.Stay.equalsIgnoreCase(Constants.SHORT_STAY)){
                    s1 = 1;
                    if(stay.StayMinutes.equalsIgnoreCase(Constants._15MIN)){
                        tv15.setVisibility(View.VISIBLE);
                        tv15.setText(Html.fromHtml("15 mins<br> $ " + "<b>" + String.format("%.0f", stay.StayPrice) + "</b>"));


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

                            selectedItem(tvShortStay, tvHourStay, tvDailyStay, tvWeeklyStay, tvMonthlyStay);

                    }
                    else if(stay.StayMinutes.equalsIgnoreCase(Constants._30MIN)){
                        tv30.setVisibility(View.VISIBLE);
                        tv30.setText(Html.fromHtml("30 mins<br> $ <b>" + String.format("%.0f", stay.StayPrice) + "</b>"));
                        if(tv15.getVisibility()!=View.VISIBLE){
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
                            setTime("shorttime",tvShortTimePick.getText().toString());
                            selectedItem(tvShortStay,tvHourStay,tvDailyStay,tvWeeklyStay,tvMonthlyStay);
                        }else{
                            tv15.setSelected(true);
                        } if(obj.bookingPropertyStayTime.equalsIgnoreCase(Constants._30MIN)){
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
                            setTime("shorttime",tvShortTimePick.getText().toString());
                            selectedItem(tvShortStay,tvHourStay,tvDailyStay,tvWeeklyStay,tvMonthlyStay);
                        }
                    }
                    else if(stay.StayMinutes.equalsIgnoreCase(Constants._45MIN)){
                        llShortStay.setSelected(true);
                        tv60.setVisibility(View.VISIBLE);
                        tv60.setText(Html.fromHtml("45 mins<br> $ <b>" + String.format("%.0f", stay.StayPrice) + "<b>"));

                        if(tv15.getVisibility()!=View.VISIBLE && tv30.getVisibility()!=View.VISIBLE){
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
                            setTime("shorttime",tvShortTimePick.getText().toString());
                            selectedItem(tvShortStay,tvHourStay,tvDailyStay,tvWeeklyStay,tvMonthlyStay);
                        }else{
                            tv15.setSelected(true);
                        }
                        if(obj.bookingPropertyStayTime.equalsIgnoreCase(Constants._45MIN)){
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
                            setTime("shorttime",tvShortTimePick.getText().toString());
                            selectedItem(tvShortStay,tvHourStay,tvDailyStay,tvWeeklyStay,tvMonthlyStay);
                        }
                    }
                }

                else if(stay.Stay.equalsIgnoreCase(Constants.HOUR_STAY)){
                    s2=1;
                    if (stay.StayMinutes.equalsIgnoreCase(Constants._1_4_HOUR)) {
                        tv1_4.setVisibility(View.VISIBLE);
                        tv1_4.setText(Html.fromHtml("1-4 hours<br> $ " + "<b>" + String.format("%.0f", stay.StayPrice) + "/hr</b>"));

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


                        if(!tvShortStay.isSelected())
                            selectedItem(tvHourStay,tvShortStay,tvDailyStay,tvWeeklyStay,tvMonthlyStay);

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



                        setTime("hourtime",tvHourTimePick.getText().toString());



                    }
                    else if (stay.StayMinutes.equalsIgnoreCase(Constants._4_8_HOUR)) {
                        tv5_8.setVisibility(View.VISIBLE);
                        tv5_8.setText(Html.fromHtml("5-8 hours<br> $ <b>" + String.format("%.0f", stay.StayPrice) + "/hr</b>"));

                        if(tv1_4.getVisibility()!=View.VISIBLE){
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

                            if(!tvShortStay.isSelected())
                                selectedItem(tvHourStay,tvShortStay,tvDailyStay,tvWeeklyStay,tvMonthlyStay);
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
                            setTime("hourtime",tvHourTimePick.getText().toString());

                        }
                        else if(obj.bookingPropertyStayTime.equalsIgnoreCase(Constants._4_8_HOUR)){
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

                            if(!tvShortStay.isSelected())
                                selectedItem(tvHourStay,tvShortStay,tvDailyStay,tvWeeklyStay,tvMonthlyStay);

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

                            setTime("hourtime",tvHourTimePick.getText().toString());
                        }
                    }
                    else if (stay.StayMinutes.equalsIgnoreCase(Constants._8_PLUS_HOUR)) {
                        tv8.setVisibility(View.VISIBLE);
                        tv8.setText(Html.fromHtml("8+ hours<br> $ <b>" + String.format("%.0f", stay.StayPrice) + "/hr<b>"));

                        if(tv1_4.getVisibility()!=View.VISIBLE && tv5_8.getVisibility()!=View.VISIBLE){
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

                            if(!tvShortStay.isSelected())
                                selectedItem(tvHourStay,tvShortStay,tvDailyStay,tvWeeklyStay,tvMonthlyStay);

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
                            setTime("hourtime",tvHourTimePick.getText().toString());
                        }
                            if(obj.bookingPropertyStayTime.equalsIgnoreCase(Constants._8_PLUS_HOUR)){

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

                            if(!tvShortStay.isSelected())
                                selectedItem(tvHourStay,tvShortStay,tvDailyStay,tvWeeklyStay,tvMonthlyStay);


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

                            setTime("hourtime",tvHourTimePick.getText().toString());
                        }
                    }

                }

                else if(stay.Stay.equalsIgnoreCase(Constants.DAY_STAY)){
                    s3=1;

                    if (stay.StayMinutes.equalsIgnoreCase(Constants._HALFDAY)) {


                        tvHalfDay.setVisibility(View.VISIBLE);
                        tvHalfDay.setText(Html.fromHtml("Half Day<br> $ " + "<b>" + String.format("%.0f", stay.StayPrice) + "</b>"));

                        tvHalfDay.setTextColor(getResources().getColor(R.color.white));
                        tvHalfDay.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);
                        tvFullDay.setTextColor(getResources().getColor(R.color.black));
                        tvFullDay.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                        tvHalfDay.setSelected(true);
                        tvFullDay.setSelected(false);
                        dailyStayPrice = getPrice(Constants.DAY_STAY, Constants._HALFDAY);
                        setTime("dailytime",tvDailyTimePick.getText().toString());

                        if(!tvShortStay.isSelected() && !tvHourStay.isSelected())
                            selectedItem(tvDailyStay,tvHourStay,tvShortStay,tvWeeklyStay,tvMonthlyStay);
                    }
                    else if (stay.StayMinutes.equalsIgnoreCase(Constants._FULLDAY)) {
                        tvFullDay.setVisibility(View.VISIBLE);
                        tvFullDay.setText(Html.fromHtml("Full Day<br> $ <b>" + String.format("%.0f", stay.StayPrice) + "</b>"));

                        if(tvHalfDay.getVisibility()!=View.VISIBLE){

                            tvFullDay.setTextColor(getResources().getColor(R.color.white));
                            tvFullDay.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                            tvHalfDay.setTextColor(getResources().getColor(R.color.black));
                            tvHalfDay.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                            tvHalfDay.setSelected(false);
                            tvFullDay.setSelected(true);
                            dailyStayPrice = getPrice(Constants.DAY_STAY, Constants._FULLDAY);
                            setTime("dailytime",tvDailyTimePick.getText().toString());
                            if(!tvShortStay.isSelected() && !tvHourStay.isSelected())
                                selectedItem(tvDailyStay,tvHourStay,tvShortStay,tvWeeklyStay,tvMonthlyStay);
                        }else{
                            tvHalfDay.setSelected(true);
                        }

                        if(obj.bookingPropertyStayTime.equalsIgnoreCase(Constants._FULLDAY)){
                            tvFullDay.setTextColor(getResources().getColor(R.color.white));
                            tvFullDay.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                            tvHalfDay.setTextColor(getResources().getColor(R.color.black));
                            tvHalfDay.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                            tvHalfDay.setSelected(false);
                            tvFullDay.setSelected(true);
                            dailyStayPrice = getPrice(Constants.DAY_STAY, Constants._FULLDAY);
                            setTime("dailytime",tvDailyTimePick.getText().toString());
                            if(!tvShortStay.isSelected() && !tvHourStay.isSelected())
                                selectedItem(tvDailyStay,tvHourStay,tvShortStay,tvWeeklyStay,tvMonthlyStay);
                        }
                    }

                }

                else if(stay.Stay.equalsIgnoreCase(Constants.WEEK_STAY)){
                    s4=1;
                    if (stay.StayMinutes.equalsIgnoreCase(Constants._3_DAY)) {

                        tv3Days.setVisibility(View.VISIBLE);
                        tv3Days.setText(Html.fromHtml("3 Days<br> $ " + "<b>" + String.format("%.0f", stay.StayPrice) + "</b>"));


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

                        if(!tvShortStay.isSelected() && !tvHourStay.isSelected() && !tvDailyStay.isSelected())
                            selectedItem(tvWeeklyStay,tvHourStay,tvShortStay,tvDailyStay,tvMonthlyStay);

                        if (tv3Days.isSelected())
                            setCalendar(tvWeeklyStartDate, tvWeeklyEndDate, "3days",DateFormat.format("yyyy-MM-dd", new Date().getTime()).toString());

                    }
                    else if (stay.StayMinutes.equalsIgnoreCase(Constants._5_DAY)) {
                        tv5Days.setVisibility(View.VISIBLE);
                        tv5Days.setText(Html.fromHtml("5 Days<br> $ <b>" + String.format("%.0f", stay.StayPrice) + "</b>"));


                    if(tv3Days.getVisibility()!=View.VISIBLE){
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

                            if(!tvShortStay.isSelected() && !tvHourStay.isSelected() && !tvDailyStay.isSelected())
                                selectedItem(tvWeeklyStay,tvHourStay,tvShortStay,tvDailyStay,tvMonthlyStay);

                            if (tv5Days.isSelected()) {
                                setCalendar(tvWeeklyStartDate, tvWeeklyEndDate, "5days", DateFormat.format("yyyy-MM-dd", new Date().getTime()).toString());
                            }
                        }else{
                        tv3Days.setSelected(true);
                    }
                    if(obj.bookingPropertyStayTime.equalsIgnoreCase(Constants._5_DAY)){

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

                            if(!tvShortStay.isSelected() && !tvHourStay.isSelected() && !tvDailyStay.isSelected())
                                selectedItem(tvWeeklyStay,tvHourStay,tvShortStay,tvDailyStay,tvMonthlyStay);

                            if (tv5Days.isSelected()) {
                                setCalendar(tvWeeklyStartDate, tvWeeklyEndDate, "5days", DateFormat.format("yyyy-MM-dd", new Date().getTime()).toString());
                            }
                        }


                    }
                    else if (stay.StayMinutes.equalsIgnoreCase(Constants._7_DAY)) {
                        tv7Days.setVisibility(View.VISIBLE);
                        tv7Days.setText(Html.fromHtml("7 Days<br> $ <b>" + String.format("%.0f", stay.StayPrice) + "<b>"));


                        if(tv3Days.getVisibility()!=View.VISIBLE && tv5Days.getVisibility()!=View.VISIBLE){
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
                        }
                        else{
                            tv3Days.setSelected(true);
                        }
                        if(obj.bookingPropertyStayTime.equalsIgnoreCase(Constants._7_DAY)){

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
                        }
                    }

                }

                else if(stay.Stay.equalsIgnoreCase(Constants.MONTH_STAY)){
                    s5=1;
                    if (stay.StayMinutes.equalsIgnoreCase(Constants._14_DAY)) {
                        tv14Days.setVisibility(View.VISIBLE);
                        tv14Days.setText(Html.fromHtml("14 Days<br> $ " + "<b>" + String.format("%.0f", stay.StayPrice) + "</b>"));

                        tv14Days.setTextColor(getResources().getColor(R.color.white));
                        tv14Days.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);
                        tv30Days.setTextColor(getResources().getColor(R.color.black));
                        tv30Days.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                        tv30Days.setSelected(false);
                        tv14Days.setSelected(true);
                        monthlyStayPrice = getPrice(Constants.MONTH_STAY, Constants._14_DAY);

                        if(!tvShortStay.isSelected() && !tvHourStay.isSelected() && !tvDailyStay.isSelected() && !tvWeeklyStay.isSelected())
                            selectedItem(tvMonthlyStay,tvDailyStay,tvHourStay,tvShortStay,tvWeeklyStay);

                        if (tv14Days.isSelected())
                            setCalendar(tvMonthlyStartDate, tvMonthlyEndDate, "14days",DateFormat.format("yyyy-MM-dd", new Date().getTime()).toString());


                    }
                    else if (stay.StayMinutes.equalsIgnoreCase(Constants._30_DAY)) {
                        tv30Days.setVisibility(View.VISIBLE);
                        tv30Days.setText(Html.fromHtml("30 Days<br> $ <b>" + String.format("%.0f", stay.StayPrice) + "</b>"));

                        if(tv14Days.getVisibility()!=View.VISIBLE) {

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
                        }else{
                            tv14Days.setSelected(true);
                        }
                        if(obj.bookingPropertyStayTime.equalsIgnoreCase(Constants._30_DAY)){

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

                        }
                    }

                }
            }
            if (s1 == 1) {
                    ll1.setVisibility(View.VISIBLE);
                    tvShortStayDetails.setVisibility(View.GONE);
                    llShortStay.setVisibility(View.VISIBLE);
            }else {
                ll1.setVisibility(View.GONE);
                tvShortStayDetails.setVisibility(View.GONE);
                llShortStay.setVisibility(View.GONE);
            }
            if (s2 == 1) {
                ll2.setVisibility(View.VISIBLE);
                tvHourStayDetails.setVisibility(View.VISIBLE);
                if(s1==0){
                    llHourStay.setVisibility(View.VISIBLE);
                    tvHourStayDetails.setVisibility(View.GONE);
                }
            } else {
                ll2.setVisibility(View.GONE);
                tvHourStayDetails.setVisibility(View.GONE);
            }
            if (s3 == 1) {
                ll3.setVisibility(View.VISIBLE);
                tvDailyStayDetails.setVisibility(View.VISIBLE);
                if(s1==0 && s2==0){
                    llDailyStay.setVisibility(View.VISIBLE);
                    tvDailyStayDetails.setVisibility(View.GONE);
                }
            } else {
                ll3.setVisibility(View.GONE);
                tvDailyStayDetails.setVisibility(View.GONE);
            }
            if (s4 == 1) {
                ll4.setVisibility(View.VISIBLE);
                tvWeeklyStayDetails.setVisibility(View.VISIBLE);
                if(s1==0 && s2==0 && s3==0){
                    llWeeklyStay.setVisibility(View.VISIBLE);
                    tvWeeklyStayDetails.setVisibility(View.GONE);
                }
            } else {
                ll4.setVisibility(View.GONE);
                tvWeeklyStayDetails.setVisibility(View.GONE);
            }
            if (s5 == 1) {
                ll5.setVisibility(View.VISIBLE);
                tvMonthlyStayDetails.setVisibility(View.VISIBLE);
                if(s1==0 && s2==0 && s3==0 && s4==0){
                    llMonthlyStay.setVisibility(View.VISIBLE);
                    tvMonthlyStayDetails.setVisibility(View.GONE);
                }
            } else {
                ll5.setVisibility(View.GONE);
                tvMonthlyStayDetails.setVisibility(View.GONE);
            }


            if(obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.SHORT_STAY)){
                hideShowStay(llShortStay);
            }
            else if(obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.HOUR_STAY)){
                hideShowStay(llHourStay);
            }
            else if(obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.DAY_STAY)){
                hideShowStay(llDailyStay);
            }
            else if(obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.WEEK_STAY)){
                hideShowStay(llWeeklyStay);
            }
            else if(obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.MONTH_STAY)){
                hideShowStay(llMonthlyStay);
            }
        }


    }
    public String getPrice(String stay, String staySlot) {
        String price = "0";
        for (int i = 0; i < obj.stays.size(); i++) {
            if (obj.stays.get(i).Stay.equalsIgnoreCase(stay)) {
                if (obj.stays.get(i).StayMinutes.equalsIgnoreCase(staySlot)) {
                    if(llHourStay.isSelected()) {
                        if (tv1_hour.isSelected()) {
                            price = String.format("%.0f", obj.stays.get(i).StayPrice * Integer.parseInt(tv1_hour.getText().toString()));
                        } else if (tv2_hour.isSelected()) {
                            price = String.format("%.0f", obj.stays.get(i).StayPrice * Integer.parseInt(tv2_hour.getText().toString()));
                        } else if (tv3_hour.isSelected()) {
                            price = String.format("%.0f", obj.stays.get(i).StayPrice *  Integer.parseInt(tv3_hour.getText().toString()));
                        }else if (tv4_hour.isSelected()) {
                            price = String.format("%.0f", obj.stays.get(i).StayPrice * Integer.parseInt(tv4_hour.getText().toString()));
                        }else
                            price = String.format("%.0f", obj.stays.get(i).StayPrice);
                    }else
                        price = String.format("%.0f", obj.stays.get(i).StayPrice);
                }
            }
        }
        return price;
    }
    public void setTime(String textviewName, String t) {
        if (textviewName.equalsIgnoreCase("shorttime")) {
                tvShortTimePick.setText(t);
            if (tv15.isSelected())
                tvShortEndTimePick.setText(Constants.addMinTime(t, 15));
            else if (tv30.isSelected())
                tvShortEndTimePick.setText(Constants.addMinTime(t, 30));
            else if (tv60.isSelected())
                tvShortEndTimePick.setText(Constants.addMinTime(t, 45));

            setShortBookingSelected();
        }
        if (textviewName.equalsIgnoreCase("hourtime")) {
            tvHourTimePick.setText(t);
            if (tv1_4.isSelected()){
                if(tv1_hour.isSelected())
                    textViewHourEndTime.setText(Constants.addMinTime(t, 60));
                else if(tv2_hour.isSelected())
                    textViewHourEndTime.setText(Constants.addMinTime(t, 120));
                else if(tv3_hour.isSelected())
                    textViewHourEndTime.setText(Constants.addMinTime(t, 180));
                else if(tv4_hour.isSelected())
                    textViewHourEndTime.setText(Constants.addMinTime(t, 240));
                setHourBookingSelected();
            }
            else if (tv5_8.isSelected()){
                if (tv1_hour.isSelected())
                    textViewHourEndTime.setText(Constants.addMinTime(t, 300));
                else if (tv2_hour.isSelected())
                    textViewHourEndTime.setText(Constants.addMinTime(t, 360));
                else if (tv3_hour.isSelected())
                    textViewHourEndTime.setText(Constants.addMinTime(t, 420));
                else if (tv4_hour.isSelected())
                    textViewHourEndTime.setText(Constants.addMinTime(t, 480));

            }
            else if (tv8.isSelected()){
                if(tv1_hour.isSelected())
                    textViewHourEndTime.setText(Constants.addMinTime(t, 540));
                else if(tv2_hour.isSelected())
                    textViewHourEndTime.setText(Constants.addMinTime(t, 600));
                else if(tv3_hour.isSelected())
                    textViewHourEndTime.setText(Constants.addMinTime(t, 660));
                else if(tv4_hour.isSelected())
                    textViewHourEndTime.setText(Constants.addMinTime(t, 720));


            }
            setHourBookingSelected();
        }
        if (textviewName.equalsIgnoreCase("dailytime")) {
            tvDailyTimePick.setText(t);
            if (tvHalfDay.isSelected())
                tvDailyEndTime.setText(Constants.addMinTime(t, 360));
            else if (tvFullDay.isSelected())
                tvDailyEndTime.setText(Constants.addMinTime(t, 720));

            setDailyBookingSelected();
        }
        if (textviewName.equalsIgnoreCase("weektime")) {
            tvWeeklyTimePick.setText(t);
            tvWeekEndTime.setText(t);

        }
        if (textviewName.equalsIgnoreCase("monthtime")) {
            tvMonthTimePick.setText(t);
            tvMonthEndTime.setText(t);

        }
    }
    public static String compareDates(String d1, String d2) {
        String data = "";
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = sdf.parse(d1);
            Date date2 = sdf.parse(d2);

            System.out.println("Date1=====" + sdf.format(date1));
            System.out.println("Date2=====" + sdf.format(date2));
            System.out.println();

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
        System.out.println("DATA IS=====" + data);
        return data;
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

    private static List<String> getDates(String dateString1, String dateString2)
    {
        ArrayList<String> dates = new ArrayList<String>();
        SimpleDateFormat df1 = new SimpleDateFormat("dd MMM yyyy");

        SimpleDateFormat df2 = new SimpleDateFormat("dd MMM yyyy");
        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1 .parse(dateString1);
            date2 = df1 .parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while(!cal1.after(cal2))
        {

            dates.add(df2.format(cal1.getTime()));
            cal1.add(Calendar.DATE, 1);
        }

        return dates;
    }

    private void setCalendar(TTextView tv, TTextView tv2, String staytime,String date){

        //  System.out.println("DATE 1 "+DateFormat.format("yyyy-MM-dd", new Date().getTime()).toString() + "");
        //  System.out.println("DATE 2 "+date);
        String data = compareDates(DateFormat.format("yyyy-MM-dd", new Date().getTime()).toString() + "", date);
        if (data.equalsIgnoreCase("Date1 is equal Date2")) {
            if (staytime.equalsIgnoreCase("3days")) {
                tv.setText(Constants.converDate(date));
                tv2.setText(Constants.converDate(setDays(date, 3)));

                weekdate.clear();
                weekdate.addAll(getDates(tv.getText().toString(),tv2.getText().toString()));
                Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
                SimpleDateFormat df1 = new SimpleDateFormat("dd MMM yyyy");
                Date date1 = null;
                try {
                    date1 = df1 .parse(Constants.converDate(date));
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
                setDays(date, 5);
                tv2.setText(Constants.converDate(setDays(date, 5)));
                weekdate.clear();
                weekdate.addAll(getDates(tv.getText().toString(),tv2.getText().toString()));
                Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
                SimpleDateFormat df1 = new SimpleDateFormat("dd MMM yyyy");
                Date date1 = null;
                try {
                    date1 = df1 .parse(Constants.converDate(date));
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
                weekdate.addAll(getDates(tv.getText().toString(),tv2.getText().toString()));
                Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
                SimpleDateFormat df1 = new SimpleDateFormat("dd MMM yyyy");
                Date date1 = null;
                try {
                    date1 = df1 .parse(Constants.converDate(date));
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
                monthdate.addAll(getDates(tv.getText().toString(),tv2.getText().toString()));
                Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
                SimpleDateFormat df1 = new SimpleDateFormat("dd MMM yyyy");
                Date date1 = null;
                try {
                    date1 = df1 .parse(Constants.converDate(date));
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
                monthdate.addAll(getDates(tv.getText().toString(),tv2.getText().toString()));
                Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
                SimpleDateFormat df1 = new SimpleDateFormat("dd MMM yyyy");
                Date date1 = null;
                try {
                    date1 = df1 .parse(Constants.converDate(date));
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
                weekdate.addAll(getDates(tv.getText().toString(),tv2.getText().toString()));
                Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
                SimpleDateFormat df1 = new SimpleDateFormat("dd MMM yyyy");
                Date date1 = null;
                try {
                    date1 = df1 .parse(Constants.converDate(date));
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
                setDays(date, 5);
                tv2.setText(Constants.converDate(setDays(date, 5)));

                weekdate.clear();
                weekdate.addAll(getDates(tv.getText().toString(),tv2.getText().toString()));
                Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
                SimpleDateFormat df1 = new SimpleDateFormat("dd MMM yyyy");
                Date date1 = null;
                try {
                    date1 = df1 .parse(Constants.converDate(date));
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
                weekdate.addAll(getDates(tv.getText().toString(),tv2.getText().toString()));
                Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
                SimpleDateFormat df1 = new SimpleDateFormat("dd MMM yyyy");
                Date date1 = null;
                try {
                    date1 = df1 .parse(Constants.converDate(date));
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
                monthdate.addAll(getDates(tv.getText().toString(),tv2.getText().toString()));
                Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
                SimpleDateFormat df1 = new SimpleDateFormat("dd MMM yyyy");
                Date date1 = null;
                try {
                    date1 = df1 .parse(Constants.converDate(date));
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
                monthdate.addAll(getDates(tv.getText().toString(),tv2.getText().toString()));
                Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
                SimpleDateFormat df1 = new SimpleDateFormat("dd MMM yyyy");
                Date date1 = null;
                try {
                    date1 = df1 .parse(Constants.converDate(date));
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
           // Utility.showAlert(this, "You can not select past date");
        }


    }


    private void editBooking(String stay,
                             String slot,
                             String stayStartDate,
                             String stayEndDate,
                             String stayStartTime,
                             String stayEndTime,
                             String distance,
                             String during,
                             String BookingID,
                             String hour) {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(ParkerBookingDetailEditScreen.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.addBooking(WebUtility.BOOK_PROPERTY,
                        appPreferences.getString("USERID"),
                        obj.propertID,
                        stay,
                        slot,
                        hour,
                        stayStartDate,
                        stayEndDate,
                        stayStartTime,
                        stayEndTime,
                        distance,
                        during,
                        obj.bookingID);
                responseBodyCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Utility.hideProgress();
                        try {
                            JSONObject jsonObject=new JSONObject(response.body().toString());
                            int ErrorCode=jsonObject.optInt("error_code");
                            String Message=jsonObject.optString("error_message");
                            if(ErrorCode==0){
                                EditBookingModel model=new EditBookingModel();
                                model.parkerUser.UserID=jsonObject.optJSONObject("parker_user").optInt("id")+"";
                                model.parkerUser.FullName=jsonObject.optJSONObject("parker_user").optString("name")+"";
                                model.parkerUser.Email=jsonObject.optJSONObject("parker_user").optString("email")+"";
                                model.parkerUser.FirstName=jsonObject.optJSONObject("parker_user").optString("first_name")+"";
                                model.parkerUser.LastName=jsonObject.optJSONObject("parker_user").optString("last_name")+"";
                                model.parkerUser.DeviceToken=jsonObject.optJSONObject("parker_user").optString("fcm_id")+"";
                                model.parkerUser.EarnPoint=jsonObject.optJSONObject("parker_user").optInt("reward_points")+"";
                                model.BookingStartDate=jsonObject.optString("booking_start_date");
                                model.BookingEndDate=jsonObject.optString("booking_end_date");
                                model.BookingStartTime=jsonObject.optString("booking_start_time");
                                model.BookingEndTime=jsonObject.optString("booking_end_time");
                                model.IsPayment=jsonObject.optInt("isPayment");
                                model.IsRated=jsonObject.optInt("is_rated");
                                model.IsEditBooking=jsonObject.optInt("isEditBooking");
                                model.BookingExtendID=jsonObject.optInt("booking_extend_id")+"";
                                model.BookingID=jsonObject.optString("bookind_id");
                                model.categoryName=jsonObject.optString("category_name");
                                model.soltName=jsonObject.optString("slot_name");
                                model.price=getPrice(stay,slot);



                                setResult(Activity.RESULT_OK,getIntent().putExtra("EDITBOOKING",model));
                                Utility.showAlertwithFinish(ParkerBookingDetailEditScreen.this,Message);
                            }
                            else{
                                if(ErrorCode==10){
                                    deleteUser();
                                }else
                                Utility.showAlert(ParkerBookingDetailEditScreen.this,Message);
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
            Utility.hideProgress();
        }
    }




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

        calendarView.setClickable(false);
        calendarViewHour.setClickable(false);
        calendarViewDaily.setClickable(false);
        calendarViewMonthly.setClickable(false);
        calendarViewWeekly.setClickable(false);


        //Handling custom calendar events
        calendarView.setCalendarListener(new CalendarListener() {
            @Override
            public void onDateSelected(Date date) {
                // Toast.makeText(CustomisedCalendarActivity.this, df.format(date), Toast.LENGTH_SHORT).show();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                setCalendar(tvShortStartDate,tvShortEndDate,"",df.format(date));
                showShortTimeLog(df.format(date),obj.propertID);
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

                setCalendar(tvHourStartDate, tvHourEndDate, "",df.format(date));
                showHourTimeLog(df.format(date),obj.propertID);
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
                    setCalendar(tvWeeklyStartDate, tvWeeklyEndDate, "3days",df.format(date));
                else if (tv5Days.isSelected())
                    setCalendar(tvWeeklyStartDate, tvWeeklyEndDate, "5days",df.format(date));
                else if (tv7Days.isSelected())
                    setCalendar(tvWeeklyStartDate, tvWeeklyEndDate, "7days",df.format(date));

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
                    setCalendar(tvMonthlyStartDate, tvMonthlyEndDate, "14days",df.format(date));
                else if (tv30Days.isSelected())
                    setCalendar(tvMonthlyStartDate, tvMonthlyEndDate, "30days",df.format(date));
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

                setCalendar(tvDailyStartDate, tvDailyEndDate, "",df.format(date));
                showDailyTimeLog(df.format(date),obj.propertID);
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
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            List<DayDecorator> decorators = new ArrayList<>();
            decorators.add(disabledColorDecorator);
            calendarView.setDecorators(decorators);
            calendarViewHour.setDecorators(decorators);
            calendarViewDaily.setDecorators(decorators);
            calendarViewMonthly.setDecorators(decorators);
            calendarViewWeekly.setDecorators(decorators);

           // if(!obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.SHORT_STAY)) {
                calendarView.refreshCalendar(currentCalendar);
            //}
            //if(!obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.HOUR_STAY)) {
                calendarViewHour.refreshCalendar(currentCalendar);
            //}
            if(!obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.WEEK_STAY)) {
                calendarViewWeekly.refreshCalendar(currentCalendar);
            }
            if(!obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.MONTH_STAY)) {
                calendarViewMonthly.refreshCalendar(currentCalendar);
            }
            //if(!obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.DAY_STAY)) {
                calendarViewDaily.refreshCalendar(currentCalendar);
            //}

        }, 200);

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
                String day3=formatter3.format(dayView.getDate());
                int color = Color.parseColor("#E8E8E8");
                HashSet<String> strings=new HashSet<>();
                try {
                    if (obj.availableTimes.size() > 0) {
                        for (tblPropertyAvailableTimes obj : obj.availableTimes) {
                            if (obj.DayName.equalsIgnoreCase("sunday") && !obj.Timing.isEmpty() && !obj.Timing.equalsIgnoreCase("[]")) {
                                if (dayname.equalsIgnoreCase("sunday")) {
                                    dayView.setBackgroundResource(R.drawable.rounded_border_white_with_small_radius);
                                    strings.add("sunday");
                                }
                            } else if (obj.DayName.equalsIgnoreCase("monday") && !obj.Timing.isEmpty() && !obj.Timing.equalsIgnoreCase("[]")) {
                                if (dayname.equalsIgnoreCase("monday")) {
                                    dayView.setBackgroundResource(R.drawable.rounded_border_white_with_small_radius);
                                    strings.add("monday");
                                }
                            } else if (obj.DayName.equalsIgnoreCase("tuesday") && !obj.Timing.isEmpty() && !obj.Timing.equalsIgnoreCase("[]")) {
                                if (dayname.equalsIgnoreCase("tuesday")) {
                                    dayView.setBackgroundResource(R.drawable.rounded_border_white_with_small_radius);
                                    strings.add("tuesday");
                                }
                            } else if (obj.DayName.equalsIgnoreCase("wednesday") && !obj.Timing.isEmpty() && !obj.Timing.equalsIgnoreCase("[]")) {
                                if (dayname.equalsIgnoreCase("wednesday")) {
                                    dayView.setBackgroundResource(R.drawable.rounded_border_white_with_small_radius);
                                    strings.add("wednesday");
                                }
                            } else if (obj.DayName.equalsIgnoreCase("thursday") && !obj.Timing.isEmpty() && !obj.Timing.equalsIgnoreCase("[]")) {
                                if (dayname.equalsIgnoreCase("thursday")) {
                                    dayView.setBackgroundResource(R.drawable.rounded_border_white_with_small_radius);
                                    strings.add("thursday");
                                }
                            } else if (obj.DayName.equalsIgnoreCase("friday") && !obj.Timing.isEmpty() && !obj.Timing.equalsIgnoreCase("[]")) {
                                if (dayname.equalsIgnoreCase("friday")) {
                                    dayView.setBackgroundResource(R.drawable.rounded_border_white_with_small_radius);
                                    strings.add("friday");
                                }
                            } else if (obj.DayName.equalsIgnoreCase("saturday") && !obj.Timing.isEmpty() && !obj.Timing.equalsIgnoreCase("[]")) {
                                if (dayname.equalsIgnoreCase("saturday")) {
                                    dayView.setBackgroundResource(R.drawable.rounded_border_white_with_small_radius);
                                    strings.add("saturday");
                                }
                            }
                        }

                        if(DateFormat.format("yyyy-MM-dd", new Date().getTime()).toString().equalsIgnoreCase(dayname2)){
                            dayView.setBackgroundResource(R.drawable.border_button_with_fill_calendar);
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
                            if(!strings.contains(dayname.toLowerCase())){
                                dayView.setOnClickListener(null);
                            }

                            if(llWeeklyStay.isSelected()){
                                if(weekdate.contains(day3)){
                                    System.out.println("DATE ======>"+weekdate.iterator());
                                    dayView.setBackgroundResource(R.drawable.border_button_with_fill_calender_three_selected_day_color);
                                }

                            }else if(llMonthlyStay.isSelected()){
                                if(monthdate.contains(day3)){
                                    dayView.setBackgroundResource(R.drawable.border_button_with_fill_calender_three_selected_day_color);
                                }

                            }else{
                                if(obj.bookingStartDate.equalsIgnoreCase(dayname2)){
                                    dayView.setBackgroundResource(R.drawable.border_button_with_fill_calender_three_selected_day_color);
                                }
                            }

                        } catch (ParseException ex) {
                            ex.printStackTrace();
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
}
