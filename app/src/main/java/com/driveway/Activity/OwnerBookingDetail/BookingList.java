package com.driveway.Activity.OwnerBookingDetail;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.driveway.Activity.BaseActivity;
import com.driveway.Activity.ParkerBooking.ParkerBookingCalendarListScreen;
import com.driveway.Activity.ParkerBooking.ParkerBookingListScreen;
import com.driveway.Adapters.OwnerBookingListAdapter;
import com.driveway.Adapters.ParkerBookingListAdapter;
import com.driveway.Component.BButton;
import com.driveway.Component.CalendarListener;
import com.driveway.Component.CalendarUtils;
import com.driveway.Component.CustomCalendarView;
import com.driveway.Component.DayDecorator;
import com.driveway.Component.DayView;
import com.driveway.Component.EEditText;
import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.Model.ParkerBookingList;
import com.driveway.R;
import com.driveway.Utility.AppPreferences;
import com.driveway.Utility.Constants;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import com.mobandme.ada.exceptions.AdaFrameworkException;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EActivity(R.layout.owner_booking)
public class BookingList extends BaseActivity {


    @ViewById
    RelativeLayout llView;
    @ViewById
    RelativeLayout ll;
    @ViewById
    LinearLayout llOne;
    @ViewById
    CustomCalendarView calendarView;
    @ViewById
    LinearLayout llTwo;
    @ViewById
    LinearLayout llThree;
    @ViewById
    LinearLayout llFour;
    @ViewById
    LinearLayout llFive;


    @ViewById
    TTextView menu_Settings;
    @ViewById
    AppCompatImageView Back;
    @ViewById
    RelativeLayout header;
    @ViewById
    RelativeLayout rlCalendar;
    @ViewById
    RecyclerView rvBookings;
    @ViewById
    TTextView tvMsg;

    @ViewById
    AppCompatCheckBox chkAll;
    @ViewById
    AppCompatCheckBox
            chkOngoing;
    @ViewById
    AppCompatCheckBox
            chkUpcoming;
    @ViewById
    AppCompatCheckBox
            chkCompleted;
    @ViewById
    AppCompatCheckBox
            chkCancelled;
    @ViewById
    TTextView tvFilter;
    @ViewById
    TTextView tvEmailMe;
    @ViewById
    SwipeRefreshLayout pullToRefresh;



    TTextView tvQuater;
    TTextView tvMonth;
    TTextView edtMonth;
    TTextView edtSelectStartMonth;
    Spinner edtSelectEndMonth;
    TTextView tvDate;
    TTextView edtStartDate;
    TTextView edtEndDate;
    TTextView btnProcess;
    TTextView tvSelectedData;
    Spinner edtYears;


    List<ParkerBookingList> bookingLists = new ArrayList<>();
    OwnerBookingListAdapter adapter;
    AppPreferences appPreferences;
    HashSet<String> listFilter = new HashSet<>();

    int year = 0, month = 0, day = 0;
    String selectedfilter = "date";
    String datee = "";

    @AfterViews
    public void init() {
        appPreferences = new AppPreferences(this);

        listFilter.add("all");
        orangeStatusBar();

        adapter = new OwnerBookingListAdapter(this, bookingLists);
        rvBookings.setLayoutManager(new LinearLayoutManager(this));
        rvBookings.setAdapter(adapter);
        listFilter.add("all");
        //datee=DateFormat.format("yyyy-MM-dd", new Date().getTime()).toString();

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadBokings(datee);
                pullToRefresh.setRefreshing(false);
            }
        });
        loadBokings(datee);

        Calendar now = Calendar.getInstance();
        year = now.get(java.util.Calendar.YEAR);
        month = now.get(java.util.Calendar.MONTH);
        day = now.get(java.util.Calendar.DAY_OF_MONTH);


    }

    @Click
    public void Back() {
        finish();
    }

    @Click
    public void menu_Settings() {
        if (llView.getVisibility() == View.VISIBLE) {
            llView.setVisibility(View.GONE);
        } else {
            llView.setVisibility(View.VISIBLE);
        }
    }

    @Click
    public void ll() {
        if (llView.getVisibility() == View.VISIBLE) {
            llView.setVisibility(View.GONE);
        }
    }

    @Click
    public void llOne() {
        rlCalendar.setVisibility(View.GONE);
        orangeStatusBar();
        menu_Settings.setTextColor(getResources().getColor(R.color.white));
        header.setBackgroundResource(R.color.button_color);
        Back.setImageResource(R.drawable.ic_back_white);
        llView.setVisibility(View.GONE);
        menu_Settings.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.drawable_settings, 0);
        datee="";
        loadBokings(datee);
    }

    @Click
    public void llTwo() {
        rlCalendar.setVisibility(View.VISIBLE);
        whiteStatusBar();
        menu_Settings.setTextColor(getResources().getColor(R.color.black));
        Back.setImageResource(R.drawable.back_black);
        header.setBackgroundResource(R.color.white);
        llView.setVisibility(View.GONE);
        llView.setTranslationZ((float) 10.0);
        menu_Settings.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.drawable_settings_black, 0);
    }
    @Click
    public void llThree()
    {
        exportfilterDialog();
    }
    @Click
    public void llFour()
    {
        emailfilterDialog();
    }
    @Click
    public void llFive()
    {
        filterDialog();
    }

    @Click
    public void tvFilter() {
        filterDialog();
    }

    @Click
    public void tvEmailMe() {
        emailfilterDialog();
    }

    @Click
    public void tvExportView() {
        exportfilterDialog();
    }

    private void tvDate() {
        tvDate.setSelected(true);
        tvMonth.setSelected(false);
        tvQuater.setSelected(false);

        edtMonth.setEnabled(false);
        edtEndDate.setEnabled(true);
        edtStartDate.setEnabled(true);
        edtSelectStartMonth.setEnabled(false);
        tvDate.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_left_fill_circle, 0, 0, 0);
        tvMonth.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_left_normal_circle, 0, 0, 0);
        tvQuater.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_left_normal_circle, 0, 0, 0);

        selectedfilter = "date";

        tvSelectedData.setText(DateFormat.format("dd MMM yyyy", Constants.getWeekStartDate().getTime()) + " -> "+ (DateFormat.format("dd MMM yyyy", Constants.getWeekEndDate().getTime())));

    }

    private void tvMonth() {
        tvDate.setSelected(false);
        tvMonth.setSelected(true);
        tvQuater.setSelected(false);

        edtMonth.setEnabled(true);
        edtEndDate.setEnabled(false);
        edtStartDate.setEnabled(false);
        edtSelectStartMonth.setEnabled(false);
        tvMonth.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_left_fill_circle, 0, 0, 0);
        tvDate.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_left_normal_circle, 0, 0, 0);
        tvQuater.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_left_normal_circle, 0, 0, 0);

        selectedfilter = "monthly";

        edtMonth.setText(Constants.getCurrentMonthName());

        tvSelectedData.setText(edtMonth.getText().toString() + " "+edtYears.getSelectedItem().toString());

    }

    private void tvQuater() {
        tvDate.setSelected(false);
        tvMonth.setSelected(false);
        tvQuater.setSelected(true);

       // edtSelectEndMonth.setEnabled(false);
        edtSelectStartMonth.setEnabled(true);
        edtMonth.setEnabled(false);
        edtEndDate.setEnabled(false);
        edtStartDate.setEnabled(false);
        tvQuater.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_left_fill_circle, 0, 0, 0);
        tvDate.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_left_normal_circle, 0, 0, 0);
        tvMonth.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drawable_left_normal_circle, 0, 0, 0);

        selectedfilter = "quartly";

        int quarter=Constants.currentQuarter();
        if(quarter==1){
            edtSelectStartMonth.setText("Jan-Mar");
        }else if(quarter==2){
            edtSelectStartMonth.setText("Apr-Jun");
        }else if(quarter==3){
            edtSelectStartMonth.setText("Jul-Sep");
        }else if(quarter==4){
            edtSelectStartMonth.setText("Oct-Dec");
        }

        tvSelectedData.setText(edtSelectStartMonth.getText().toString() + " "+edtSelectEndMonth.getSelectedItem().toString());
    }

    private void inilizeCalendar() {

        //Initialize calendar with date
        Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());

        //Show monday as first date of week
        calendarView.setFirstDayOfWeek(Calendar.SUNDAY);

        //Show/hide overflow days of a month
        calendarView.setShowOverflowDate(false);


        //Handling custom calendar events
        calendarView.setCalendarListener(new CalendarListener() {
            @Override
            public void onDateSelected(Date date) {
                // Toast.makeText(CustomisedCalendarActivity.this, df.format(date), Toast.LENGTH_SHORT).show();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                datee = df.format(date);
                loadBokings(df.format(date));

            }

            @Override
            public void onMonthChanged(Date date) {
                SimpleDateFormat df = new SimpleDateFormat("MM-yyyy");
                //Toast.makeText(CustomisedCalendarActivity.this, df.format(date), Toast.LENGTH_SHORT).show();
            }
        });

        final Typeface typeface = Typeface.createFromAsset(getAssets(), "ProductSansBold.ttf");
        if (null != typeface) {
            calendarView.setCustomTypeface(typeface);
            calendarView.refreshCalendar(currentCalendar);
        }

        //call refreshCalendar to update calendar the view
        calendarView.refreshCalendar(currentCalendar);


        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            List<DayDecorator> decorators = new ArrayList<>();
            decorators.add(new DisabledColorDecorator());
            calendarView.setDecorators(decorators);
            calendarView.refreshCalendar(currentCalendar);

        }, 300);



    }

    private class DisabledColorDecorator implements DayDecorator {
        @Override
        public void decorate(DayView dayView) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String dayname = formatter.format(dayView.getDate());
                int color = Color.parseColor("#E8E8E8");
                try {
                    if (bookingLists.size() > 0) {
                        for (ParkerBookingList obj : bookingLists) {
                            if (obj.bookingStartDate.equalsIgnoreCase(dayname)) {
                                dayView.setBackgroundResource(R.drawable.border_button_with_fill_calender_two);
                            }
                            if(DateFormat.format("yyyy-MM-dd", new Date().getTime()).toString().equalsIgnoreCase(dayname)){
                                dayView.setBackgroundResource(R.drawable.border_button_with_fill_calendar);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    Callback<JsonObject> callbacktwo = new Callback<JsonObject>() {
        @Override
        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
            Utility.hideProgress();
            bookingLists.clear();
            if (response.isSuccessful()) {
                try {
                    if (response.body() != null) {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        if (jsonObject != null) {

                            if (jsonObject.getString("error_code").equalsIgnoreCase("0")) {

                                JSONArray array = jsonObject.getJSONArray("data");

                                if (array != null && array.length() > 0) {
                                    for (int i = 0; i < array.length(); i++) {
                                        ParkerBookingList parkerBookingList = new ParkerBookingList();
                                        parkerBookingList.bookingID = array.getJSONObject(i).getString("id");
                                        parkerBookingList.userID = array.getJSONObject(i).getString("user_id");
                                        parkerBookingList.bookingEndTime = array.getJSONObject(i).getString("booking_end_time");
                                        parkerBookingList.bookingEndDate = array.getJSONObject(i).getString("booking_end_date");
                                        parkerBookingList.bookingStartDate = array.getJSONObject(i).getString("booking_start_date");
                                        parkerBookingList.bookingStartTime = array.getJSONObject(i).getString("booking_start_time");
                                        parkerBookingList.bookingPrice = array.getJSONObject(i).getString("price");
                                        parkerBookingList.bookingPayment = array.getJSONObject(i).getString("isPayment");

                                        parkerBookingList.propertID = array.getJSONObject(i).getJSONObject("property_details").getString("id");
                                        parkerBookingList.bookingPropertyTitle = array.getJSONObject(i).getJSONObject("property_details").getString("title");
                                        parkerBookingList.bookingStatus = array.getJSONObject(i).getString("booking_status");
                                        parkerBookingList.bookingProperttyImage = array.getJSONObject(i).getJSONObject("property_details").getString("img_1");
                                        parkerBookingList.bookingPropertyAddress = array.getJSONObject(i).getJSONObject("property_details").getString("address");

                                        parkerBookingList.bookingPropertyDistance = array.getJSONObject(i).getJSONObject("property_details").getString("distance");
                                        parkerBookingList.bookingPropertyDuration = array.getJSONObject(i).getJSONObject("property_details").getString("duration");
                                        parkerBookingList.bookingPropertyParkingType = array.getJSONObject(i).getJSONObject("property_details").getString("parking_type");
                                        parkerBookingList.bookingPropertyAvailable = array.getJSONObject(i).getJSONObject("property_details").getString("available");
                                        parkerBookingList.bookingPropertyRating = array.getJSONObject(i).getJSONObject("property_details").getString("rating");


                                        parkerBookingList.owneruser.UserID = array.getJSONObject(i).getJSONObject("property_details").getJSONObject("owner_user").getInt("id") + "";
                                        parkerBookingList.owneruser.FullName = array.getJSONObject(i).getJSONObject("property_details").getJSONObject("owner_user").getString("name");
                                        parkerBookingList.owneruser.Email = array.getJSONObject(i).getJSONObject("property_details").getJSONObject("owner_user").getString("email");
                                        parkerBookingList.owneruser.UserType = array.getJSONObject(i).getJSONObject("property_details").getJSONObject("owner_user").getString("user_type");
                                        parkerBookingList.owneruser.FirstName = array.getJSONObject(i).getJSONObject("property_details").getJSONObject("owner_user").getString("first_name");
                                        parkerBookingList.owneruser.LastName = array.getJSONObject(i).getJSONObject("property_details").getJSONObject("owner_user").getString("last_name");
                                        parkerBookingList.owneruser.APIToken = array.optJSONObject(i).optJSONObject("property_details").optJSONObject("owner_user").optString("fcm_id");
                                        parkerBookingList.owneruser.DeviceToken = array.optJSONObject(i).optJSONObject("property_details").optJSONObject("owner_user").optString("device_token");

                                        parkerBookingList.user.UserID = array.getJSONObject(i).getJSONObject("parker_user").getInt("id") + "";
                                        parkerBookingList.user.FullName = array.getJSONObject(i).getJSONObject("parker_user").getString("name");
                                        parkerBookingList.user.Email = array.getJSONObject(i).getJSONObject("parker_user").getString("email");
                                        parkerBookingList.user.UserType = array.getJSONObject(i).getJSONObject("parker_user").getString("user_type");
                                        parkerBookingList.user.FirstName = array.getJSONObject(i).getJSONObject("parker_user").getString("first_name");
                                        parkerBookingList.user.LastName = array.getJSONObject(i).getJSONObject("parker_user").getString("last_name");
                                        parkerBookingList.user.APIToken=array.optJSONObject(i).optJSONObject("parker_user").optString("fcm_id");
                                        parkerBookingList.user.DeviceToken=array.optJSONObject(i).optJSONObject("parker_user").optString("device_token");

                                        parkerBookingList.bookingPropertyStayTitle = array.getJSONObject(i).getJSONObject("rates").getString("title");
                                        parkerBookingList.bookingPropertyStayTime = array.getJSONObject(i).getJSONObject("rates").getString("time");
                                        parkerBookingList.bookingPropertyStayPrice = array.getJSONObject(i).getJSONObject("rates").getString("price");

                                        bookingLists.add(parkerBookingList);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                                if (bookingLists.size() > 0) {
                                    rvBookings.setVisibility(View.VISIBLE);
                                    tvMsg.setVisibility(View.GONE);
                                } else {
                                    rvBookings.setVisibility(View.GONE);
                                    tvMsg.setVisibility(View.VISIBLE);
                                }
                            } else {
                                if(jsonObject.getString("error_code").equalsIgnoreCase("10")){
                                    deleteUser();
                                }else
                                    Utility.showAlert(BookingList.this, jsonObject.getString("error_message").toString());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            adapter.notifyDataSetChanged();
            if (bookingLists.size() > 0) {
                rvBookings.setVisibility(View.VISIBLE);
                inilizeCalendar();
                tvMsg.setVisibility(View.GONE);
            } else {
                rvBookings.setVisibility(View.GONE);
                tvMsg.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onFailure(Call<JsonObject> call, Throwable t) {
            if (bookingLists.size() > 0) {
                rvBookings.setVisibility(View.VISIBLE);
                tvMsg.setVisibility(View.GONE);
            } else {
                rvBookings.setVisibility(View.GONE);
                tvMsg.setVisibility(View.VISIBLE);
            }
            Utility.hideProgress();
        }
    };

    private void loadBokings(String date) {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(BookingList.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.getDateBookings(
                        WebUtility.GET_PROPERTY,
                        appPreferences.getString("USERID"),
                        date,
                        android.text.TextUtils.join(",", listFilter));
                responseBodyCall.enqueue(callbacktwo);

            }
        } catch (Exception ex) {
        }
    }

    private void filterDialog() {
        if (llView.getVisibility() == View.VISIBLE) {
            llView.setVisibility(View.GONE);
        }
        View v = LayoutInflater.from(this).inflate(R.layout.filter_booking_dialog, null, false);
        BottomSheetDialog dialog = new BottomSheetDialog(BookingList.this, R.style.TransparentDialog);
        dialog.setContentView(v);
        AppCompatImageView close = v.findViewById(R.id.close);

        chkCompleted = v.findViewById(R.id.chkCompleted);
        chkUpcoming = v.findViewById(R.id.chkUpcoming);
        chkOngoing = v.findViewById(R.id.chkOngoing);
        chkAll = v.findViewById(R.id.chkAll);
        chkCancelled = v.findViewById(R.id.chkCancelled);


        close.setOnClickListener(v1 -> {
            dialog.dismiss();
            loadBokings(datee);
        });

        dialog.show();

        chkAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            filterData();
        });
        chkCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {

            filterData();
        });
        chkUpcoming.setOnCheckedChangeListener((buttonView, isChecked) -> {
            filterData();
        });
        chkOngoing.setOnCheckedChangeListener((buttonView, isChecked) -> {
            filterData();
        });
        chkCancelled.setOnCheckedChangeListener((buttonView, isChecked) -> {
            filterData();
        });

    }

    private void filterData() {
        if (chkAll.isChecked()) {
            listFilter.clear();
            listFilter.add("all");
            setCheckBokColor(chkAll);
        } else {
            removeFilter("all");
            setCheckBokColor(chkAll);
        }
        if (chkCompleted.isChecked()) {
            removeFilter("all");
            listFilter.add("completed");
            setCheckBokColor(chkCompleted);
        } else {
            removeFilter("completed");
            setCheckBokColor(chkCompleted);
        }
        if (chkCancelled.isChecked()) {
            removeFilter("all");
            listFilter.add("cancelled");
            setCheckBokColor(chkCancelled);
        } else {
            removeFilter("cancelled");
            setCheckBokColor(chkCancelled);
        }
        if (chkOngoing.isChecked()) {
            removeFilter("all");
            listFilter.add("ongoing");
            setCheckBokColor(chkOngoing);
        } else {
            removeFilter("ongoing");
            setCheckBokColor(chkOngoing);
        }
        if (chkUpcoming.isChecked()) {
            removeFilter("all");
            listFilter.add("upcoming");
            setCheckBokColor(chkUpcoming);
        } else {
            removeFilter("upcoming");
            setCheckBokColor(chkUpcoming);
        }
//        for(String data:listFilter){
//            System.out.println("Selected CheckBox=====>"+data);
//        }
    }

    private void removeFilter(String name) {
        for (String n : listFilter) {
            if (n.equalsIgnoreCase(name)) {
                listFilter.remove(name);
                break;
            }
        }
    }

    private void setCheckBokColor(AppCompatCheckBox ch) {
        if (ch.isChecked()) {
            ch.setTextColor(getResources().getColor(R.color.button_color));
        } else {
            ch.setTextColor(getResources().getColor(R.color.black));

        }
    }

    private void emailfilterDialog() {
        if (llView.getVisibility() == View.VISIBLE) {
            llView.setVisibility(View.GONE);
        }
        View v = LayoutInflater.from(this).inflate(R.layout.email_booking_dialog, null, false);
        BottomSheetDialog dialog = new BottomSheetDialog(BookingList.this, R.style.DialogStyle);
        dialog.setContentView(v);
        AppCompatImageView close = v.findViewById(R.id.close);
        EEditText edtEmail = v.findViewById(R.id.edtEmail);
        BButton btnSend = v.findViewById(R.id.btnSend);

        try {
            dataContext.tblUserObjectSet.fill();
        } catch (AdaFrameworkException e) {
            e.printStackTrace();
        }
        edtEmail.setText(dataContext.tblUserObjectSet.get(0).Email);
        close.setOnClickListener(v1 -> {
            listFilter.clear();
            dialog.dismiss();
        });
        btnSend.setOnClickListener(v12 -> {
            if (!TextUtils.isEmpty(edtEmail.getText().toString().trim()))
                exportEmail(edtEmail.getText().toString().trim(), dialog);
            else
                Utility.showAlert(BookingList.this, "Enter Email");
        });
        dialog.show();

    }

    private void exportfilterDialog() {
        if (llView.getVisibility() == View.VISIBLE) {
            llView.setVisibility(View.GONE);
        }
        View v = LayoutInflater.from(this).inflate(R.layout.export_booking_dialog, null, false);
        BottomSheetDialog dialog = new BottomSheetDialog(BookingList.this, R.style.DialogStyle);
        dialog.setContentView(v);
        AppCompatImageView close = v.findViewById(R.id.close);
        edtStartDate = v.findViewById(R.id.edtStartDate);
        edtEndDate = v.findViewById(R.id.edtEndDate);
        edtSelectStartMonth = v.findViewById(R.id.edtSelectStartMonth);
        edtSelectEndMonth = v.findViewById(R.id.edtSelectEndMonth);
        tvQuater = v.findViewById(R.id.tvQuater);
        tvMonth = v.findViewById(R.id.tvMonth);
        tvDate = v.findViewById(R.id.tvDate);
        edtMonth = v.findViewById(R.id.edtMonth);
        btnProcess = v.findViewById(R.id.btnProcess);
        tvSelectedData = v.findViewById(R.id.tvSelectedData);
        edtYears=v.findViewById(R.id.edtYears);


        close.setOnClickListener(v1 -> {
            listFilter.clear();
            dialog.dismiss();
        });

        edtStartDate.setText(DateFormat.format("dd MMM yyyy", Constants.getWeekStartDate().getTime()) + "");
        edtEndDate.setText(DateFormat.format("dd MMM yyyy", Constants.getWeekEndDate().getTime())+"");

        edtMonth.setText(Constants.getCurrentMonthName());
        tvSelectedData.setText(edtStartDate.getText().toString()+" -> "+edtEndDate.getText().toString());
        int quarter=Constants.currentQuarter();
        if(quarter==1){
            edtSelectStartMonth.setText("Jan-Mar");
        }else if(quarter==2){
            edtSelectStartMonth.setText("Apr-Jun");
        }else if(quarter==3){
            edtSelectStartMonth.setText("Jul-Sep");
        }else if(quarter==4){
            edtSelectStartMonth.setText("Oct-Dec");
        }


        edtStartDate.setOnClickListener(v12 -> openDatePicker(edtStartDate));
        tvDate.setOnClickListener(v13 -> tvDate());
        tvMonth.setOnClickListener(v14 -> tvMonth());
        tvQuater.setOnClickListener(v15 -> tvQuater());
        edtEndDate.setOnClickListener(v16 -> openDatePicker(edtEndDate));

        edtMonth.setOnClickListener(v17 -> {
            PopupMenu popup = new PopupMenu(BookingList.this, edtMonth);
            //Inflating the Popup using xml file
            popup.getMenuInflater()
                    .inflate(R.menu.month_popup, popup.getMenu());
            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(item -> {
                edtMonth.setText(item.getTitle() + "");
                tvSelectedData.setText(item.getTitle() + " "+edtYears.getSelectedItem().toString());
                return true;
            });

            popup.show();
        });

        getYears();



        edtSelectStartMonth.setOnClickListener(v17 -> {
            PopupMenu popup = new PopupMenu(BookingList.this, edtMonth);
            //Inflating the Popup using xml file
            popup.getMenuInflater()
                    .inflate(R.menu.month_popup_two, popup.getMenu());
            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(item -> {
                edtSelectStartMonth.setText(item.getTitle() + "");
                //edtSelectEndMonth.setText(Utility.QuotarMonth(item.getTitle().toString().toLowerCase() + ""));
                tvSelectedData.setText(item.getTitle() + " " + edtSelectEndMonth.getSelectedItem().toString());
                return true;
            });

            popup.show();
        });


        btnProcess.setOnClickListener(v18 -> {

            if (selectedfilter.equalsIgnoreCase("date")) {
                exportPDFEmail("date", Constants.converDate_YYYYMMDD(edtStartDate.getText().toString().trim()), Constants.converDate_YYYYMMDD(edtEndDate.getText().toString().trim()), dialog);
            } else if (selectedfilter.equalsIgnoreCase("monthly")) {
                exportPDFEmail("monthly", edtMonth.getText().toString().trim(),edtYears.getSelectedItem().toString(), dialog,"");
            } else if (selectedfilter.equalsIgnoreCase("quartly")) {
                exportPDFEmailMonthly("quartly", edtSelectStartMonth.getText().toString().trim(),edtSelectEndMonth.getSelectedItem().toString(),dialog);
            }
        });
        dialog.show();

    }


    private void openDatePicker(TTextView tv) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(BookingList.this, R.style.MyAppTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                tv.setText(Constants.converDatePDF(dayOfMonth + "/" + (month + 1) + "/" + year));
                tvSelectedData.setText(edtStartDate.getText().toString() + " -> " + edtEndDate.getText().toString());
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void exportEmail(String Email, BottomSheetDialog dialog) {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(BookingList.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.exportEmail(WebUtility.EMAIL_BOOKING_HISTORY, appPreferences.getString("USERID"), Email);
                responseBodyCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            try {
                                if (response.body() != null) {
                                    System.out.println("Error Code==>" + response.body().toString());
                                    JSONObject jsonObject = new JSONObject(response.body().toString());
                                    if (jsonObject != null) {
                                        System.out.println("Error Code==>" + jsonObject.getString("error_code"));
                                        if (jsonObject.getString("error_code").equalsIgnoreCase("0")) {
                                            dialog.dismiss();
                                            //Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(jsonObject.getString("pdfurl")));
                                            //startActivity(i);
                                            // Utility.showAlert(BookingList.this, jsonObject.getString("error_message").toString());

                                        } else {
                                            if(jsonObject.getString("error_code").equalsIgnoreCase("10")){
                                                deleteUser();
                                            }else
                                                Utility.showAlert(BookingList.this, jsonObject.getString("error_message").toString());
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        System.out.println("RESPONSE IS=====" + t.getMessage());
                        Utility.hideProgress();
                    }
                });
            }
        } catch (Exception ex) {
        }
    }

    private void exportPDFEmail(String Filter, String startDate, String endDate, BottomSheetDialog dialog) {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(BookingList.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.exportPDF(WebUtility.EXPORT_PDF, appPreferences.getString("USERID"), Filter, startDate, endDate);
                responseBodyCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            try {
                                if (response.body() != null) {
                                    System.out.println("Error Code==>" + response.body().toString());
                                    JSONObject jsonObject = new JSONObject(response.body().toString());
                                    if (jsonObject != null) {
                                //        System.out.println("Error Code==>" + jsonObject.getString("error_code"));
                                        if (jsonObject.getString("error_code").equalsIgnoreCase("0")) {
                                            dialog.dismiss();
                                            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(jsonObject.getString("pdfurl")));
                                            startActivity(i);
                                            //Utility.showAlert(BookingList.this, jsonObject.getString("error_message").toString());

                                        } else {
                                            if(jsonObject.getString("error_code").equalsIgnoreCase("10")){
                                                deleteUser();
                                            }else
                                                Utility.showAlert(BookingList.this, jsonObject.getString("error_message").toString());
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        System.out.println("RESPONSE IS=====" + t.getMessage());
                        Utility.hideProgress();
                    }
                });
            }
        } catch (Exception ex) {
        }
    }

    private void exportPDFEmailMonthly(String Filter, String startMonth, String endMonth, BottomSheetDialog dialog) {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(BookingList.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.exportPDFMonth(WebUtility.EXPORT_PDF, appPreferences.getString("USERID"), Filter, startMonth, endMonth);
                responseBodyCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            try {
                                if (response.body() != null) {
                                    System.out.println("Error Code==>" + response.body().toString());
                                    JSONObject jsonObject = new JSONObject(response.body().toString());
                                    if (jsonObject != null) {
                                        System.out.println("Error Code==>" + jsonObject.getString("error_code"));
                                        if (jsonObject.getString("error_code").equalsIgnoreCase("0")) {
                                            dialog.dismiss();
                                            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(jsonObject.getString("pdfurl")));
                                            startActivity(i);

//                                            new DownloadOperation().execute(jsonObject.getString("pdfurl").toString());
                                        } else {
                                            if(jsonObject.getString("error_code").equalsIgnoreCase("10")){
                                                deleteUser();
                                            }else
                                                Utility.showAlert(BookingList.this, jsonObject.getString("error_message").toString());
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        System.out.println("RESPONSE IS=====" + t.getMessage());
                        Utility.hideProgress();
                    }
                });
            }
        } catch (Exception ex) {
        }
    }

    public void getYears() {

        ArrayList<String> years=Constants.getPreviousYears();
        Collections.reverse(years);
        ArrayAdapter<String> langAdapter = new ArrayAdapter<String>(BookingList.this, R.layout.year_spinner, years);
        langAdapter.setDropDownViewResource(R.layout.year_dropdown);
        edtYears.setAdapter(langAdapter);
        edtSelectEndMonth.setAdapter(langAdapter);

        edtYears.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(tvMonth.isSelected())
                    tvSelectedData.setText(edtMonth.getText().toString() + " " + parent.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        edtSelectEndMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(tvQuater.isSelected())
                    tvSelectedData.setText(edtSelectStartMonth.getText().toString() + " " + parent.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void exportPDFEmail(String Filter, String monthName,String Year, BottomSheetDialog dialog,String test) {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(BookingList.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.exportPDFYear(WebUtility.EXPORT_PDF,
                        appPreferences.getString("USERID"),
                        Filter,
                        monthName,Year);
                responseBodyCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            try {
                                if (response.body() != null) {
                                    System.out.println("Error Code==>" + response.body().toString());
                                    JSONObject jsonObject = new JSONObject(response.body().toString());
                                    if (jsonObject != null) {
                                        System.out.println("Error Code==>" + jsonObject.getString("error_code"));
                                        if (jsonObject.getString("error_code").equalsIgnoreCase("0")) {
                                            dialog.dismiss();
                                            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(jsonObject.getString("pdfurl")));
                                            startActivity(i);

//                                            new DownloadOperation().execute(jsonObject.getString("pdfurl").toString());
                                        } else {
                                            if(jsonObject.getString("error_code").equalsIgnoreCase("10")){
                                                deleteUser();
                                            }else
                                                Utility.showAlert(BookingList.this, jsonObject.getString("error_message").toString());
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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



//    public class DownloadOperation extends AsyncTask<String, Void, String> {
//
//        String Filepath;
//
//        @Override
//        protected String doInBackground(String... params)
//        {
//            try {
//                File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/mydriveway/");
//                folder.mkdir();
//                Filepath = "mydriveway-" + new Date().getTime()+".pdf";
//                File file = new File(folder, Filepath);
//                try {
//                    file.createNewFile();
//                }
//                catch (IOException e1)
//                {
//                    e1.printStackTrace();
//                }
//                Constants.DownloadFile
//                        (params[0], file);//Paste your url here
//            }
//            catch (Exception e) {}
//            return Filepath;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//
//            //ProgressClass.progressClose();
//
//            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/mydriveway/" + result);
//
//            PackageManager packageManager = getPackageManager();
//            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
//            pdfIntent .setType("application/pdf");
//            List list = packageManager.queryIntentActivities(pdfIntent ,
//                    PackageManager.MATCH_DEFAULT_ONLY);
//            Intent intent = new Intent();
//            intent.setAction(Intent.ACTION_VIEW);
//            Uri uri = Uri.fromFile(file);
//            intent.setDataAndType(uri, "application/pdf");
//            startActivity(intent);
//
//        }
//
//        @Override
//        protected void onPreExecute() {
//
//           // ProgressClass.progressShow(downloadPDF.this, "Connecting");
//        }
//
//    }

}
