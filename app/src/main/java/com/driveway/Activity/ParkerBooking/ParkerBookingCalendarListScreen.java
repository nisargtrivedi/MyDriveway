package com.driveway.Activity.ParkerBooking;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.driveway.Activity.BaseActivity;
import com.driveway.Activity.OwnerBookingDetail.BookingList;
import com.driveway.Adapters.ParkerBookingListAdapter;
import com.driveway.Component.CalendarListener;
import com.driveway.Component.DayDecorator;
import com.driveway.Component.DayView;
import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.DBHelper.tblPropertyAvailableTimes;
import com.driveway.DBHelper.tblStay;
import com.driveway.Fragment.OwnerPropertyDetailFragmentThree;
import com.driveway.Model.ParkerBookingList;
import com.driveway.R;
import com.driveway.Utility.AppPreferences;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.driveway.calendarcomponent.CalendarView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

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

public class ParkerBookingCalendarListScreen extends BaseActivity implements View.OnClickListener {

    RecyclerView rvBookings;
    AppPreferences appPreferences;
    List<ParkerBookingList> bookingLists=new ArrayList<>();
    ParkerBookingListAdapter adapter;
    ImageView FilterImage,btnCalendar,btnFilter;
    AppCompatImageView Back;
    com.driveway.Component.CustomCalendarView calendarView;
    TTextView tvMsg;
    HashSet<String> listFilter=new HashSet<>();
    AppCompatCheckBox chkAll,chkOngoing,chkUpcoming,chkCompleted,chkCancelled;
    String ddate="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parker_booking_list_calendar_screen);
        rvBookings=findViewById(R.id.rvBookings);
        FilterImage=findViewById(R.id.FilterImage);
        Back=findViewById(R.id.Back);
        calendarView=findViewById(R.id.calendarView);
        btnCalendar=findViewById(R.id.btnCalendar);
        btnFilter=findViewById(R.id.btnFilter);
        tvMsg=findViewById(R.id.tvMsg);

        appPreferences=new AppPreferences(this);

        adapter=new ParkerBookingListAdapter(this,bookingLists);
        rvBookings.setLayoutManager(new LinearLayoutManager(this));
        rvBookings.setAdapter(adapter);
        listFilter.add("all");
        whiteStatusBar();

        FilterImage.setOnClickListener(this);
        Back.setOnClickListener(this);
        btnCalendar.setOnClickListener(this);
        btnFilter.setOnClickListener(this);

        ddate=DateFormat.format("yyyy-MM-dd", new Date().getTime()).toString();
        loadBookings();
    }


    private void loadBokings(String date) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!Utility.isNetworkAvailable(ParkerBookingCalendarListScreen.this)) {
                        Utility.showAlert(ParkerBookingCalendarListScreen.this, "please check internet connection");
                    } else {
                        Utility.showProgress(ParkerBookingCalendarListScreen.this);
                        WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                        Call<JsonObject> responseBodyCall = apiInterface.getDateBookings(WebUtility.GET_PROPERTY, appPreferences.getString("USERID"),date,android.text.TextUtils.join(",", listFilter));
                        responseBodyCall.enqueue(new Callback<JsonObject>() {
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
                                                    JSONArray array=jsonObject.getJSONArray("data");
                                                    if(array!=null && array.length()>0){
                                                        for(int i=0;i<array.length();i++){
                                                            ParkerBookingList parkerBookingList=new ParkerBookingList();
                                                            parkerBookingList.bookingID=array.optJSONObject(i).optString("id");
                                                            parkerBookingList.userID=array.optJSONObject(i).optString("user_id");
                                                            parkerBookingList.bookingEndTime=array.optJSONObject(i).optString("booking_end_time");
                                                            parkerBookingList.bookingEndDate=array.optJSONObject(i).optString("booking_end_date");
                                                            parkerBookingList.bookingStartDate=array.optJSONObject(i).optString("booking_start_date");
                                                            parkerBookingList.bookingStartTime=array.optJSONObject(i).optString("booking_start_time");
                                                            parkerBookingList.bookingPrice=array.optJSONObject(i).optString("price");
                                                            parkerBookingList.bookingPayment=array.optJSONObject(i).optString("isPayment");
                                                            parkerBookingList.bookingStatus=array.optJSONObject(i).optString("booking_status");
                                                            parkerBookingList.isRated=array.optJSONObject(i).optString("is_rated");

                                                            //System.out.println("VALUE=============>"+array.getJSONObject(i).getJSONObject("property_details").toString().trim().length());
//                                                    if(array.getJSONObject(i).getJSONObject("property_details").isNull("property_details")==false)
//                                                    {
                                                            parkerBookingList.propertID = array.optJSONObject(i).optJSONObject("property_details").optString("id");
                                                            parkerBookingList.bookingPropertyTitle = array.optJSONObject(i).optJSONObject("property_details").optString("title");
                                                            parkerBookingList.bookingProperttyImage = array.optJSONObject(i).optJSONObject("property_details").optString("img_1");
                                                            parkerBookingList.bookingPropertyAddress = array.optJSONObject(i).optJSONObject("property_details").optString("address");
                                                            parkerBookingList.bookingPropertyDistance = array.optJSONObject(i).optJSONObject("property_details").optString("distance");
                                                            parkerBookingList.bookingPropertyDuration = array.optJSONObject(i).optJSONObject("property_details").optString("duration");
                                                            parkerBookingList.bookingPropertyParkingType = array.optJSONObject(i).optJSONObject("property_details").optString("parking_type");
                                                            parkerBookingList.bookingPropertyAvailable = array.optJSONObject(i).optJSONObject("property_details").optString("available");
                                                            parkerBookingList.bookingPropertyRating = array.optJSONObject(i).optJSONObject("property_details").optString("rating");
                                                            parkerBookingList.owneruser.UserID = array.optJSONObject(i).optJSONObject("property_details").optJSONObject("owner_user").optInt("id") + "";
                                                            parkerBookingList.owneruser.FullName = array.optJSONObject(i).optJSONObject("property_details").optJSONObject("owner_user").optString("name");
                                                            parkerBookingList.owneruser.Email = array.optJSONObject(i).optJSONObject("property_details").optJSONObject("owner_user").optString("email");
                                                            parkerBookingList.owneruser.UserType = array.optJSONObject(i).optJSONObject("property_details").optJSONObject("owner_user").optString("user_type");
                                                            parkerBookingList.owneruser.FirstName = array.optJSONObject(i).optJSONObject("property_details").optJSONObject("owner_user").optString("first_name");
                                                            parkerBookingList.owneruser.LastName = array.optJSONObject(i).optJSONObject("property_details").optJSONObject("owner_user").optString("last_name");
                                                            parkerBookingList.owneruser.APIToken = array.optJSONObject(i).optJSONObject("property_details").optJSONObject("owner_user").optString("fcm_id");
                                                            parkerBookingList.owneruser.DeviceToken = array.optJSONObject(i).optJSONObject("property_details").optJSONObject("owner_user").optString("device_token");
                                                            parkerBookingList.bookingLat = array.optJSONObject(i).optJSONObject("property_details").optString("lat");
                                                            parkerBookingList.bookingLng = array.optJSONObject(i).optJSONObject("property_details").optString("lng");


                                                            JSONArray jsonArray = array.optJSONObject(i).optJSONObject("property_details").optJSONArray("rates");
                                                            if (jsonArray!=null && jsonArray.length() > 0) {
                                                                for (int j = 0; j < jsonArray.length(); j++) {
                                                                    tblStay stay = new tblStay();
                                                                    stay.ParkingID = parkerBookingList.propertID;
                                                                    stay.StayMinutes = jsonArray.optJSONObject(j).optString("time");
                                                                    stay.Stay = jsonArray.optJSONObject(j).optString("title");
                                                                    stay.StayPrice = Double.parseDouble(jsonArray.getJSONObject(j).optString("price"));
                                                                    parkerBookingList.stays.add(stay);
                                                                }
                                                            }

                                                            JSONArray jsonAvailability = array.optJSONObject(i).optJSONObject("property_details").optJSONArray("availibility");
                                                            if (jsonAvailability!=null && jsonAvailability.length() > 0) {
                                                                for (int k = 0; k < jsonAvailability.length(); k++) {
                                                                    tblPropertyAvailableTimes times = new tblPropertyAvailableTimes();
                                                                    times.ParkingID = parkerBookingList.propertID;
                                                                    times.DayName = jsonAvailability.optJSONObject(k).optString("title");
                                                                    times.Timing = jsonAvailability.optJSONObject(k).optJSONArray("time").toString();
                                                                    parkerBookingList.availableTimes.add(times);
                                                                }
                                                            }

                                                            // }

                                                            parkerBookingList.user.UserID=array.optJSONObject(i).optJSONObject("parker_user").optInt("id")+"";
                                                            parkerBookingList.user.FullName=array.optJSONObject(i).optJSONObject("parker_user").optString("name");
                                                            parkerBookingList.user.Email=array.optJSONObject(i).optJSONObject("parker_user").optString("email");
                                                            parkerBookingList.user.UserType=array.optJSONObject(i).optJSONObject("parker_user").optString("user_type");
                                                            parkerBookingList.user.FirstName=array.optJSONObject(i).optJSONObject("parker_user").optString("first_name");
                                                            parkerBookingList.user.LastName=array.optJSONObject(i).optJSONObject("parker_user").optString("last_name");
                                                            parkerBookingList.user.APIToken=array.optJSONObject(i).optJSONObject("parker_user").optString("fcm_id");


                                                            parkerBookingList.bookingPropertyStayTitle=array.optJSONObject(i).optJSONObject("rates").optString("title");
                                                            parkerBookingList.bookingPropertyStayTime=array.optJSONObject(i).optJSONObject("rates").optString("time");
                                                            parkerBookingList.bookingPropertyStayPrice=array.optJSONObject(i).optJSONObject("rates").optString("price");

                                                            JSONObject object=array.optJSONObject(i).optJSONObject("card_detail");

                                                            if(object!=null){
                                                                parkerBookingList.card.CardID=object.optString("id");
                                                                parkerBookingList.card.OwnerName=object.optString("title");
                                                                parkerBookingList.card.CardNo=object.optString("number");
                                                                parkerBookingList.card.CardMonth=object.optString("months");
                                                                parkerBookingList.card.CardYear=object.optString("years");
                                                                parkerBookingList.card.CVV=object.optString("cvv");
                                                                parkerBookingList.card.isDefault=object.optString("is_default");
                                                            }
                                                            bookingLists.add(parkerBookingList);
                                                        }
                                                    }
                                                    adapter.notifyDataSetChanged();
                                                    if(bookingLists.size()>0){
                                                        rvBookings.setVisibility(View.VISIBLE);
                                                        tvMsg.setVisibility(View.GONE);
                                                    }else{
                                                        rvBookings.setVisibility(View.GONE);
                                                        tvMsg.setVisibility(View.VISIBLE);
                                                    }
                                                    adapter.notifyDataSetChanged();
                                                    if(bookingLists.size()>0){
                                                        rvBookings.setVisibility(View.VISIBLE);
                                                        tvMsg.setVisibility(View.GONE);
                                                    }else{
                                                        rvBookings.setVisibility(View.GONE);
                                                        tvMsg.setVisibility(View.VISIBLE);
                                                    }
                                                }
                                                else {
                                                    if(jsonObject.getString("error_code").equalsIgnoreCase("10")){
                                                        deleteUser();
                                                    }else
                                                        Utility.showAlert(ParkerBookingCalendarListScreen.this, jsonObject.getString("error_message").toString());
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                adapter.notifyDataSetChanged();
                                if(bookingLists.size()>0){
                                    rvBookings.setVisibility(View.VISIBLE);
                                    tvMsg.setVisibility(View.GONE);

                                }else{
                                    rvBookings.setVisibility(View.GONE);
                                    tvMsg.setVisibility(View.VISIBLE);
                                }

                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                System.out.println("RESPONSE IS=====" + t.getMessage());
                                if(bookingLists.size()>0){
                                    rvBookings.setVisibility(View.VISIBLE);
                                    tvMsg.setVisibility(View.GONE);
                                }else{
                                    rvBookings.setVisibility(View.GONE);
                                    tvMsg.setVisibility(View.VISIBLE);
                                }
                                Utility.hideProgress();
                            }
                        });
                    }
                } catch (Exception ex) {
                }
            }
        });

       // inilizeCalendar();
    }

    private void filterDialog() {
        View v = LayoutInflater.from(this).inflate(R.layout.filter_booking_dialog, null, false);
        BottomSheetDialog dialog = new BottomSheetDialog(ParkerBookingCalendarListScreen.this, R.style.TransparentDialog);
        dialog.setContentView(v);
        AppCompatImageView close = v.findViewById(R.id.close);
        chkCompleted=v.findViewById(R.id.chkCompleted);
        chkUpcoming=v.findViewById(R.id.chkUpcoming);
        chkOngoing=v.findViewById(R.id.chkOngoing);
        chkAll=v.findViewById(R.id.chkAll);
        chkCancelled=v.findViewById(R.id.chkCancelled);

        close.setOnClickListener(v1 -> {
            dialog.dismiss();
            loadBokings(ddate);
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
    private void filterData(){
        if(chkAll.isChecked()){
            listFilter.clear();
            listFilter.add("all");
            setCheckBokColor(chkAll);
            chkAll.setChecked(true);
            chkCompleted.setChecked(true);
            chkCancelled.setChecked(true);
            chkOngoing.setChecked(true);
            chkUpcoming.setChecked(true);
        }else{
            removeFilter("all");
            setCheckBokColor(chkAll);
        }
        if(chkCompleted.isChecked()){

                listFilter.add("completed");
                setCheckBokColor(chkCompleted);

        }else {
            removeFilter("completed");
            setCheckBokColor(chkCompleted);
        }

        if(chkCancelled.isChecked()){

                listFilter.add("cancelled");
                setCheckBokColor(chkCancelled);

        }else {
            removeFilter("cancelled");
            setCheckBokColor(chkCancelled);
        }

        if(chkOngoing.isChecked()){

                listFilter.add("ongoing");
                setCheckBokColor(chkOngoing);

        }
        else {
            removeFilter("ongoing");
            setCheckBokColor(chkOngoing);
        }

        if(chkUpcoming.isChecked()){

                listFilter.add("upcoming");
                setCheckBokColor(chkUpcoming);

        }
        else {
            removeFilter("upcoming");
            setCheckBokColor(chkUpcoming);
        }
    }

    private void deselectAll(AppCompatCheckBox chk){
        if(chk.getId()==R.id.chkOngoing){
            chkUpcoming.setChecked(false);
            chkCompleted.setChecked(false);
            chkCancelled.setChecked(false);
            chkAll.setChecked(false);
        }else if(chk.getId()==R.id.chkCancelled){
            chkUpcoming.setChecked(false);
            chkCompleted.setChecked(false);
            chkUpcoming.setChecked(false);
            chkAll.setChecked(false);
        }else if(chk.getId()==R.id.chkCompleted){
            chkUpcoming.setChecked(false);
            chkCancelled.setChecked(false);
            chkUpcoming.setChecked(false);
            chkAll.setChecked(false);
        }else if(chk.getId()==R.id.chkUpcoming){
            chkUpcoming.setChecked(false);
            chkCancelled.setChecked(false);
            chkCompleted.setChecked(false);
            chkAll.setChecked(false);
        }



    }
    private void removeFilter(String name){
        for(String n:listFilter){
            if(n.equalsIgnoreCase(name)){
                listFilter.remove(name);
                break;
            }
        }
    }
    private void setCheckBokColor(AppCompatCheckBox ch){
        if(ch.isChecked()){
            ch.setTextColor(getResources().getColor(R.color.button_color));
        }else{
            ch.setTextColor(getResources().getColor(R.color.black));

        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.FilterImage:
                filterDialog();
                break;
            case R.id.Back:
                finish();
                overridePendingTransition(R.anim.entry, R.anim.exit);
                break;
            case R.id.btnCalendar:
                break;
            case R.id.btnFilter:
                finish();
                startActivity(new Intent(this,ParkerBookingListScreen.class));
                overridePendingTransition(R.anim.entry, R.anim.exit);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.entry, R.anim.exit);
        super.onBackPressed();

    }

    private void inilizeCalendar(){

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
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                ddate=df.format(date);
                loadBokings(df.format(date));

                // Toast.makeText(CustomisedCalendarActivity.this, df.format(date), Toast.LENGTH_SHORT).show();
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

//        List<DayDecorator> decorators = new ArrayList<>();
//        decorators.add(new DisabledColorDecorator());
//        calendarView.setDecorators(decorators);
//        calendarView.refreshCalendar(currentCalendar);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            List<DayDecorator> decorators = new ArrayList<>();
            decorators.add(new DisabledColorDecorator());
            calendarView.setDecorators(decorators);
            calendarView.refreshCalendar(currentCalendar);

        },300);



    }


    Callback<JsonObject> callback=new Callback<JsonObject>() {
        @Override
        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utility.hideProgress();
                }
            });

            bookingLists.clear();
            if (response.isSuccessful()) {
                try {
                    if (response.body() != null) {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        if (jsonObject != null) {
                            if (jsonObject.getString("error_code").equalsIgnoreCase("0")) {
                                JSONArray array=jsonObject.getJSONArray("data");
                                if(array!=null && array.length()>0){
                                    for(int i=0;i<array.length();i++){
                                        ParkerBookingList parkerBookingList=new ParkerBookingList();
                                        parkerBookingList.bookingID=array.optJSONObject(i).optString("id");
                                        parkerBookingList.userID=array.optJSONObject(i).optString("user_id");
                                        parkerBookingList.bookingEndTime=array.optJSONObject(i).optString("booking_end_time");
                                        parkerBookingList.bookingEndDate=array.optJSONObject(i).optString("booking_end_date");
                                        parkerBookingList.bookingStartDate=array.optJSONObject(i).optString("booking_start_date");
                                        parkerBookingList.bookingStartTime=array.optJSONObject(i).optString("booking_start_time");
                                        parkerBookingList.bookingPrice=array.optJSONObject(i).optString("price");
                                        parkerBookingList.bookingPayment=array.optJSONObject(i).optString("isPayment");
                                        parkerBookingList.bookingStatus=array.optJSONObject(i).optString("booking_status");
                                        parkerBookingList.isRated=array.optJSONObject(i).optString("is_rated");

                                        //System.out.println("VALUE=============>"+array.getJSONObject(i).getJSONObject("property_details").toString().trim().length());
//                                                    if(array.getJSONObject(i).getJSONObject("property_details").isNull("property_details")==false)
//                                                    {
                                        parkerBookingList.propertID = array.optJSONObject(i).optJSONObject("property_details").optString("id");
                                        parkerBookingList.bookingPropertyTitle = array.optJSONObject(i).optJSONObject("property_details").optString("title");
                                        parkerBookingList.bookingProperttyImage = array.optJSONObject(i).optJSONObject("property_details").optString("img_1");
                                        parkerBookingList.bookingPropertyAddress = array.optJSONObject(i).optJSONObject("property_details").optString("address");
                                        parkerBookingList.bookingPropertyDistance = array.optJSONObject(i).optJSONObject("property_details").optString("distance");
                                        parkerBookingList.bookingPropertyDuration = array.optJSONObject(i).optJSONObject("property_details").optString("duration");
                                        parkerBookingList.bookingPropertyParkingType = array.optJSONObject(i).optJSONObject("property_details").optString("parking_type");
                                        parkerBookingList.bookingPropertyAvailable = array.optJSONObject(i).optJSONObject("property_details").optString("available");
                                        parkerBookingList.bookingPropertyRating = array.optJSONObject(i).optJSONObject("property_details").optString("rating");
                                        parkerBookingList.owneruser.UserID = array.optJSONObject(i).optJSONObject("property_details").optJSONObject("owner_user").optInt("id") + "";
                                        parkerBookingList.owneruser.FullName = array.optJSONObject(i).optJSONObject("property_details").optJSONObject("owner_user").optString("name");
                                        parkerBookingList.owneruser.Email = array.optJSONObject(i).optJSONObject("property_details").optJSONObject("owner_user").optString("email");
                                        parkerBookingList.owneruser.UserType = array.optJSONObject(i).optJSONObject("property_details").optJSONObject("owner_user").optString("user_type");
                                        parkerBookingList.owneruser.FirstName = array.optJSONObject(i).optJSONObject("property_details").optJSONObject("owner_user").optString("first_name");
                                        parkerBookingList.owneruser.LastName = array.optJSONObject(i).optJSONObject("property_details").optJSONObject("owner_user").optString("last_name");
                                        parkerBookingList.owneruser.APIToken = array.optJSONObject(i).optJSONObject("property_details").optJSONObject("owner_user").optString("fcm_id");
                                        parkerBookingList.owneruser.DeviceToken = array.optJSONObject(i).optJSONObject("property_details").optJSONObject("owner_user").optString("device_token");
                                        parkerBookingList.bookingLat = array.optJSONObject(i).optJSONObject("property_details").optString("lat");
                                        parkerBookingList.bookingLng = array.optJSONObject(i).optJSONObject("property_details").optString("lng");


                                        JSONArray jsonArray = array.optJSONObject(i).optJSONObject("property_details").optJSONArray("rates");
                                        if (jsonArray!=null && jsonArray.length() > 0) {
                                            for (int j = 0; j < jsonArray.length(); j++) {
                                                tblStay stay = new tblStay();
                                                stay.ParkingID = parkerBookingList.propertID;
                                                stay.StayMinutes = jsonArray.optJSONObject(j).optString("time");
                                                stay.Stay = jsonArray.optJSONObject(j).optString("title");
                                                stay.StayPrice = Double.parseDouble(jsonArray.getJSONObject(j).optString("price"));
                                                parkerBookingList.stays.add(stay);
                                            }
                                        }

                                        JSONArray jsonAvailability = array.optJSONObject(i).optJSONObject("property_details").optJSONArray("availibility");
                                        if (jsonAvailability!=null && jsonAvailability.length() > 0) {
                                            for (int k = 0; k < jsonAvailability.length(); k++) {
                                                tblPropertyAvailableTimes times = new tblPropertyAvailableTimes();
                                                times.ParkingID = parkerBookingList.propertID;
                                                times.DayName = jsonAvailability.optJSONObject(k).optString("title");
                                                times.Timing = jsonAvailability.optJSONObject(k).optJSONArray("time").toString();
                                                parkerBookingList.availableTimes.add(times);
                                            }
                                        }

                                        // }

                                        parkerBookingList.user.UserID=array.optJSONObject(i).optJSONObject("parker_user").optInt("id")+"";
                                        parkerBookingList.user.FullName=array.optJSONObject(i).optJSONObject("parker_user").optString("name");
                                        parkerBookingList.user.Email=array.optJSONObject(i).optJSONObject("parker_user").optString("email");
                                        parkerBookingList.user.UserType=array.optJSONObject(i).optJSONObject("parker_user").optString("user_type");
                                        parkerBookingList.user.FirstName=array.optJSONObject(i).optJSONObject("parker_user").optString("first_name");
                                        parkerBookingList.user.LastName=array.optJSONObject(i).optJSONObject("parker_user").optString("last_name");
                                        parkerBookingList.user.APIToken=array.optJSONObject(i).optJSONObject("parker_user").optString("fcm_id");


                                        parkerBookingList.bookingPropertyStayTitle=array.optJSONObject(i).optJSONObject("rates").optString("title");
                                        parkerBookingList.bookingPropertyStayTime=array.optJSONObject(i).optJSONObject("rates").optString("time");
                                        parkerBookingList.bookingPropertyStayPrice=array.optJSONObject(i).optJSONObject("rates").optString("price");
                                        parkerBookingList.HourTime=array.optJSONObject(i).optJSONObject("rates").optString("hourlydatetime");
                                        JSONObject object=array.optJSONObject(i).optJSONObject("card_detail");

                                        if(object!=null){
                                            parkerBookingList.card.CardID=object.optString("id");
                                            parkerBookingList.card.OwnerName=object.optString("title");
                                            parkerBookingList.card.CardNo=object.optString("number");
                                            parkerBookingList.card.CardMonth=object.optString("months");
                                            parkerBookingList.card.CardYear=object.optString("years");
                                            parkerBookingList.card.CVV=object.optString("cvv");
                                            parkerBookingList.card.isDefault=object.optString("is_default");
                                        }



                                        bookingLists.add(parkerBookingList);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                                if(bookingLists.size()>0){
                                    rvBookings.setVisibility(View.VISIBLE);
                                    tvMsg.setVisibility(View.GONE);
                                }else{
                                    rvBookings.setVisibility(View.GONE);
                                    tvMsg.setVisibility(View.VISIBLE);
                                }
                                adapter.notifyDataSetChanged();
                                if(bookingLists.size()>0){
                                    rvBookings.setVisibility(View.VISIBLE);
                                    tvMsg.setVisibility(View.GONE);
                                }else{
                                    rvBookings.setVisibility(View.GONE);
                                    tvMsg.setVisibility(View.VISIBLE);
                                }
                            }
                            else {
                                if(jsonObject.getString("error_code").equalsIgnoreCase("10")){
                                    deleteUser();
                                }else
                                    Utility.showAlert(ParkerBookingCalendarListScreen.this, jsonObject.getString("error_message").toString());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            inilizeCalendar();
        }

        @Override
        public void onFailure(Call<JsonObject> call, Throwable t) {
            if(bookingLists.size()>0){
                rvBookings.setVisibility(View.VISIBLE);
                tvMsg.setVisibility(View.GONE);
            }else{
                rvBookings.setVisibility(View.GONE);
                tvMsg.setVisibility(View.VISIBLE);
            }
            Utility.hideProgress();
        }
    };
    private void loadBookings() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!Utility.isNetworkAvailable(ParkerBookingCalendarListScreen.this)) {
                        Utility.showAlert(ParkerBookingCalendarListScreen.this, "please check internet connection");
                    } else {
                        Utility.showProgress(ParkerBookingCalendarListScreen.this);
                        WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                        Call<JsonObject> responseBodyCall = apiInterface.getBookings(WebUtility.GET_PROPERTY, appPreferences.getString("USERID"),android.text.TextUtils.join(",", listFilter));
                        responseBodyCall.enqueue(callback);


                    }
                } catch (Exception ex) {
                }
            }
        });

    }
    private class DisabledColorDecorator implements DayDecorator {
        @Override
        public void decorate(DayView dayView) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String dayname = formatter.format(dayView.getDate());

                try {
                    if(bookingLists.size()>0){
                        for(ParkerBookingList obj:bookingLists){
                            if(obj.bookingStartDate.equalsIgnoreCase(dayname)){
                                dayView.setBackgroundResource(R.drawable.border_button_with_fill_calender_three);
                            }
                            if(DateFormat.format("yyyy-MM-dd", new Date().getTime()).toString().equalsIgnoreCase(dayname)){
                                dayView.setBackgroundResource(R.drawable.border_button_with_fill_calendar);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //System.out.println("Date = "+format + "\n Day Name = "+format2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
