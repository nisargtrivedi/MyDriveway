package com.driveway.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.driveway.Activity.OwnerBookingDetail.BookingList;
import com.driveway.Activity.OwnerEditProperty.OwnerPropertyEditScreen;
import com.driveway.Activity.OwnerPropertyDetailScreen;
import com.driveway.Activity.ParkerBookingStayScreen;
import com.driveway.Adapters.OwnerParkingDetailBookingAdapter;
import com.driveway.Component.CalendarListener;
import com.driveway.Component.DayDecorator;
import com.driveway.Component.DayView;
import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.DBHelper.tblPropertyAvailableTimes;
import com.driveway.DBHelper.tblStay;
import com.driveway.Model.BookingModel;
import com.driveway.Model.ParkerBookingList;
import com.driveway.R;
import com.driveway.Utility.AppPreferences;
import com.driveway.Utility.Constants;
import com.driveway.Utility.DataContext;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.driveway.calendarcomponent.CalendarView;
import com.driveway.calendarcomponent.DateUtils;
import com.driveway.calendarcomponent.DrawableUtils;
import com.driveway.calendarcomponent.EventDay;
import com.driveway.calendarcomponent.OnDayClickListener;
import com.driveway.calendarcomponent.OutOfDateRangeException;
import com.google.gson.JsonObject;
import com.mobandme.ada.Entity;
import com.mobandme.ada.exceptions.AdaFrameworkException;

import org.json.JSONArray;
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


public class OwnerPropertyDetailFragmentThree extends Fragment {

    OwnerParkingDetailBookingAdapter adapter;
    RecyclerView recyclerView;

    ImageView btn;
    TTextView tvMsg;
    com.driveway.Component.CustomCalendarView calendarView;

    ArrayList<tblPropertyAvailableTimes> sundayList=new ArrayList<>();
    ArrayList<tblPropertyAvailableTimes> mondayList=new ArrayList<>();
    ArrayList<tblPropertyAvailableTimes> tuesdayList=new ArrayList<>();
    ArrayList<tblPropertyAvailableTimes> wednesdayList=new ArrayList<>();
    ArrayList<tblPropertyAvailableTimes> thursdayList=new ArrayList<>();
    ArrayList<tblPropertyAvailableTimes> fridayList=new ArrayList<>();
    ArrayList<tblPropertyAvailableTimes> saturdayList=new ArrayList<>();
    DataContext dataContext;
    AppPreferences appPreferences;
    List<ParkerBookingList> bookingLists=new ArrayList<>();
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_propertydetail_three, container, false);

        calendarView = (com.driveway.Component.CustomCalendarView) v.findViewById(R.id.calendarView);
        recyclerView=v.findViewById(R.id.rvBookings);
        tvMsg=v.findViewById(R.id.tvMsg);
        btn=v.findViewById(R.id.btn);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataContext=new DataContext(getActivity());
        appPreferences=new AppPreferences(getActivity());

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

    }
    public void openDialog(){
        View view = getLayoutInflater().inflate(R.layout.legend_dialog, null);
        ImageView close = view.findViewById(R.id.close);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        dialog.show();
        close.setOnClickListener(view1 -> dialog.dismiss());
    }

    @Override
    public void onStart() {
        super.onStart();
        loadData();
    }

    private void loadData(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deleteAvailableTimes();
                bindSundayTime();
                bindMondayTime();
                bindTuesdayTime();
                bindWednesdayTime();
                bindThursdayTime();
                bindFridayTime();
                bindSaturdayTime();
                bindCalender();
                bindBooking();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @UiThread
    private void bindCalender(){

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
                loadDateBookings(df.format(date));


            }

            @Override
            public void onMonthChanged(Date date) {
                SimpleDateFormat df = new SimpleDateFormat("MM-yyyy");
                //Toast.makeText(CustomisedCalendarActivity.this, df.format(date), Toast.LENGTH_SHORT).show();
            }
        });

        final Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "ProductSansBold.ttf");
        if (null != typeface) {
            calendarView.setCustomTypeface(typeface);
            calendarView.refreshCalendar(currentCalendar);
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            List<DayDecorator> decorators = new ArrayList<>();
            decorators.add(new DisabledColorDecorator());
            calendarView.setDecorators(decorators);
            calendarView.refreshCalendar(currentCalendar);

        },300);


    }
    private class DisabledColorDecorator implements DayDecorator {
        @Override
        public void decorate(DayView dayView) {
            try {
                SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
                String dayname = formatter.format(dayView.getDate());
                String dayname2 = formatter2.format(dayView.getDate());
                HashSet<String> strings=new HashSet<>();
                int color = Color.parseColor("#E8E8E8");
                try {
                    if(dataContext.propertyAvailableTimesObjectSet.size()>0){
                        for(tblPropertyAvailableTimes obj:dataContext.propertyAvailableTimesObjectSet){
                            if(obj.DayName.equalsIgnoreCase("sunday") && !obj.Timing.isEmpty()){
                                if(dayname.equalsIgnoreCase("sunday")){
                                    dayView.setBackgroundResource(R.drawable.rounded_border_white_with_small_radius);
                                    strings.add("sunday");
                                }
                            }
                            else if(obj.DayName.equalsIgnoreCase("monday") && !obj.Timing.isEmpty()){
                                if(dayname.equalsIgnoreCase("monday")){
                                    dayView.setBackgroundResource(R.drawable.rounded_border_white_with_small_radius);
                                    strings.add("monday");
                                }
                            }
                            else if(obj.DayName.equalsIgnoreCase("tuesday") && !obj.Timing.isEmpty()){
                                if(dayname.equalsIgnoreCase("tuesday")){
                                    dayView.setBackgroundResource(R.drawable.rounded_border_white_with_small_radius);
                                    strings.add("tuesday");
                                }
                            }
                            else if(obj.DayName.equalsIgnoreCase("wednesday") && !obj.Timing.isEmpty()){
                                if(dayname.equalsIgnoreCase("wednesday")){
                                    dayView.setBackgroundResource(R.drawable.rounded_border_white_with_small_radius);
                                    strings.add("wednesday");
                                }
                            }
                            else if(obj.DayName.equalsIgnoreCase("thursday") && !obj.Timing.isEmpty()){
                                if(dayname.equalsIgnoreCase("thursday")){
                                    dayView.setBackgroundResource(R.drawable.rounded_border_white_with_small_radius);
                                    strings.add("thursday");
                                }
                            }
                            else if(obj.DayName.equalsIgnoreCase("friday") && !obj.Timing.isEmpty()){
                                if(dayname.equalsIgnoreCase("friday")){
                                    dayView.setBackgroundResource(R.drawable.rounded_border_white_with_small_radius);
                                    strings.add("friday");

                                }
                            }
                            else if(obj.DayName.equalsIgnoreCase("saturday") && !obj.Timing.isEmpty()){
                                if(dayname.equalsIgnoreCase("saturday")){
                                    dayView.setBackgroundResource(R.drawable.rounded_border_white_with_small_radius);
                                    strings.add("saturday");
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
                            if(!strings.contains(dayname.toLowerCase())){
                                dayView.setOnClickListener(null);
                            }
                            if(DateFormat.format("yyyy-MM-dd", new Date().getTime()).toString().equalsIgnoreCase(dayname2)){
                                dayView.setBackgroundResource(R.drawable.border_button_with_fill_calendar);
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

    @Override
    public void onStop() {
        super.onStop();
        deleteAvailableTimes();
    }

    Callback<JsonObject> callback=new Callback<JsonObject>() {
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

                                JSONArray array=jsonObject.getJSONArray("data");
                                bookingLists.clear();
                                if(array!=null && array.length()>0){
                                    for(int i=0;i<array.length();i++){
                                        ParkerBookingList parkerBookingList=new ParkerBookingList();
                                        parkerBookingList.bookingID=array.getJSONObject(i).getString("id");
                                        parkerBookingList.userID=array.getJSONObject(i).getString("user_id");
                                        parkerBookingList.bookingEndTime=array.getJSONObject(i).getString("booking_end_time");
                                        parkerBookingList.bookingEndDate=array.getJSONObject(i).getString("booking_end_date");
                                        parkerBookingList.bookingStartDate=array.getJSONObject(i).getString("booking_start_date");
                                        parkerBookingList.bookingStartTime=array.getJSONObject(i).getString("booking_start_time");
                                        parkerBookingList.bookingPrice=array.getJSONObject(i).getString("price");
                                        parkerBookingList.bookingPayment=array.getJSONObject(i).getString("isPayment");
                                        parkerBookingList.bookingPropertyTitle=array.getJSONObject(i).getJSONObject("property_details").getString("title");
                                        parkerBookingList.bookingStatus=array.getJSONObject(i).getString("booking_status");
                                        parkerBookingList.bookingProperttyImage=array.getJSONObject(i).getJSONObject("property_details").getString("img_1");
                                        parkerBookingList.bookingPropertyAddress=array.getJSONObject(i).getJSONObject("property_details").getString("address");

                                        parkerBookingList.bookingPropertyDistance=array.getJSONObject(i).getJSONObject("property_details").getString("distance");
                                        parkerBookingList.bookingPropertyDuration=array.getJSONObject(i).getJSONObject("property_details").getString("duration");
                                        parkerBookingList.bookingPropertyParkingType=array.getJSONObject(i).getJSONObject("property_details").getString("parking_type");
                                        parkerBookingList.bookingPropertyAvailable=array.getJSONObject(i).getJSONObject("property_details").getString("available");
                                        parkerBookingList.bookingPropertyRating=array.getJSONObject(i).getJSONObject("property_details").getString("rating");


                                        parkerBookingList.owneruser.UserID=array.getJSONObject(i).getJSONObject("property_details").getJSONObject("owner_user").getInt("id")+"";
                                        parkerBookingList.owneruser.FullName=array.getJSONObject(i).getJSONObject("property_details").getJSONObject("owner_user").getString("name");
                                        parkerBookingList.owneruser.Email=array.getJSONObject(i).getJSONObject("property_details").getJSONObject("owner_user").getString("email");
                                        parkerBookingList.owneruser.UserType=array.getJSONObject(i).getJSONObject("property_details").getJSONObject("owner_user").getString("user_type");
                                        parkerBookingList.owneruser.FirstName=array.getJSONObject(i).getJSONObject("property_details").getJSONObject("owner_user").getString("first_name");
                                        parkerBookingList.owneruser.LastName=array.getJSONObject(i).getJSONObject("property_details").getJSONObject("owner_user").getString("last_name");


                                        parkerBookingList.user.UserID=array.getJSONObject(i).getJSONObject("parker_user").getInt("id")+"";
                                        parkerBookingList.user.FullName=array.getJSONObject(i).getJSONObject("parker_user").getString("name");
                                        parkerBookingList.user.Email=array.getJSONObject(i).getJSONObject("parker_user").getString("email");
                                        parkerBookingList.user.UserType=array.getJSONObject(i).getJSONObject("parker_user").getString("user_type");
                                        parkerBookingList.user.FirstName=array.getJSONObject(i).getJSONObject("parker_user").getString("first_name");
                                        parkerBookingList.user.LastName=array.getJSONObject(i).getJSONObject("parker_user").getString("last_name");


                                        parkerBookingList.bookingPropertyStayTitle=array.getJSONObject(i).getJSONObject("rates").getString("title");
                                        parkerBookingList.bookingPropertyStayTime=array.getJSONObject(i).getJSONObject("rates").getString("time");
                                        parkerBookingList.bookingPropertyStayPrice=array.getJSONObject(i).getJSONObject("rates").getString("price");

                                        bookingLists.add(parkerBookingList);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                                if(bookingLists.size()>0){
                                    recyclerView.setVisibility(View.VISIBLE);
                                    tvMsg.setVisibility(View.GONE);
                                }else{
                                    recyclerView.setVisibility(View.GONE);
                                    tvMsg.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Utility.showAlert(getActivity(), jsonObject.getString("error_message").toString());
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
            if(bookingLists.size()>0){
                recyclerView.setVisibility(View.VISIBLE);
                tvMsg.setVisibility(View.GONE);
            }else{
                recyclerView.setVisibility(View.GONE);
                tvMsg.setVisibility(View.VISIBLE);
            }
            Utility.hideProgress();
        }
    };

    Callback<JsonObject> callbackDate=new Callback<JsonObject>() {
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

                                JSONArray array=jsonObject.getJSONArray("data");
                                bookingLists.clear();
                                if(array!=null && array.length()>0){
                                    for(int i=0;i<array.length();i++){
                                        ParkerBookingList parkerBookingList=new ParkerBookingList();
                                        parkerBookingList.bookingID=array.getJSONObject(i).getString("id");
                                        parkerBookingList.userID=array.getJSONObject(i).getString("user_id");
                                        parkerBookingList.bookingEndTime=array.getJSONObject(i).getString("booking_end_time");
                                        parkerBookingList.bookingEndDate=array.getJSONObject(i).getString("booking_end_date");
                                        parkerBookingList.bookingStartDate=array.getJSONObject(i).getString("booking_start_date");
                                        parkerBookingList.bookingStartTime=array.getJSONObject(i).getString("booking_start_time");
                                        parkerBookingList.bookingPrice=array.getJSONObject(i).getString("price");
                                        parkerBookingList.bookingPropertyTitle=array.getJSONObject(i).getJSONObject("property_details").getString("title");
                                        parkerBookingList.bookingStatus=array.getJSONObject(i).getString("booking_status");
                                        parkerBookingList.bookingProperttyImage=array.getJSONObject(i).getJSONObject("property_details").getString("img_1");
                                        parkerBookingList.bookingPropertyAddress=array.getJSONObject(i).getJSONObject("property_details").getString("address");

                                        parkerBookingList.bookingPropertyDistance=array.getJSONObject(i).getJSONObject("property_details").getString("distance");
                                        parkerBookingList.bookingPropertyDuration=array.getJSONObject(i).getJSONObject("property_details").getString("duration");
                                        parkerBookingList.bookingPropertyParkingType=array.getJSONObject(i).getJSONObject("property_details").getString("parking_type");
                                        parkerBookingList.bookingPropertyAvailable=array.getJSONObject(i).getJSONObject("property_details").getString("available");
                                        parkerBookingList.bookingPropertyRating=array.getJSONObject(i).getJSONObject("property_details").getString("rating");


                                        parkerBookingList.owneruser.UserID=array.getJSONObject(i).getJSONObject("property_details").getJSONObject("owner_user").getInt("id")+"";
                                        parkerBookingList.owneruser.FullName=array.getJSONObject(i).getJSONObject("property_details").getJSONObject("owner_user").getString("name");
                                        parkerBookingList.owneruser.Email=array.getJSONObject(i).getJSONObject("property_details").getJSONObject("owner_user").getString("email");
                                        parkerBookingList.owneruser.UserType=array.getJSONObject(i).getJSONObject("property_details").getJSONObject("owner_user").getString("user_type");
                                        parkerBookingList.owneruser.FirstName=array.getJSONObject(i).getJSONObject("property_details").getJSONObject("owner_user").getString("first_name");
                                        parkerBookingList.owneruser.LastName=array.getJSONObject(i).getJSONObject("property_details").getJSONObject("owner_user").getString("last_name");


                                        parkerBookingList.user.UserID=array.getJSONObject(i).getJSONObject("parker_user").getInt("id")+"";
                                        parkerBookingList.user.FullName=array.getJSONObject(i).getJSONObject("parker_user").getString("name");
                                        parkerBookingList.user.Email=array.getJSONObject(i).getJSONObject("parker_user").getString("email");
                                        parkerBookingList.user.UserType=array.getJSONObject(i).getJSONObject("parker_user").getString("user_type");
                                        parkerBookingList.user.FirstName=array.getJSONObject(i).getJSONObject("parker_user").getString("first_name");
                                        parkerBookingList.user.LastName=array.getJSONObject(i).getJSONObject("parker_user").getString("last_name");


                                        parkerBookingList.bookingPropertyStayTitle=array.getJSONObject(i).getJSONObject("rates").getString("title");
                                        parkerBookingList.bookingPropertyStayTime=array.getJSONObject(i).getJSONObject("rates").getString("time");
                                        parkerBookingList.bookingPropertyStayPrice=array.getJSONObject(i).getJSONObject("rates").getString("price");

                                        bookingLists.add(parkerBookingList);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                                if(bookingLists.size()>0){
                                    recyclerView.setVisibility(View.VISIBLE);
                                    tvMsg.setVisibility(View.GONE);
                                }else{
                                    recyclerView.setVisibility(View.GONE);
                                    tvMsg.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Utility.showAlert(getActivity(), jsonObject.getString("error_message").toString());
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
            if(bookingLists.size()>0){
                recyclerView.setVisibility(View.VISIBLE);
                tvMsg.setVisibility(View.GONE);
            }else{
                recyclerView.setVisibility(View.GONE);
                tvMsg.setVisibility(View.VISIBLE);
            }
            Utility.hideProgress();
        }
    };


    private void loadBookings(String filter) {
        try {
            if (!Utility.isNetworkAvailable(getActivity())) {
                Utility.showAlert(getActivity(), "please check internet connection");
            } else {
                Utility.showProgress(getActivity());
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.getBookingsList(WebUtility.GET_PROPERTY, ((OwnerPropertyDetailScreen)getActivity()).parkingSpace.ParkingID,filter);
                if(!responseBodyCall.isExecuted())
                    responseBodyCall.enqueue(callback);
                else{
                    responseBodyCall.clone().enqueue(callback);
                }
            }
        } catch (Exception ex) {
        }
    }
    private void loadDateBookings(String date) {
        try {
            if (!Utility.isNetworkAvailable(getActivity())) {
                Utility.showAlert(getActivity(), "please check internet connection");
            } else {
                Utility.showProgress(getActivity());
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.getDateBookings(WebUtility.GET_PROPERTY, ((OwnerPropertyDetailScreen) getActivity()).parkingSpace.ParkingID, date,"all");
                if(!responseBodyCall.isExecuted()){
                    responseBodyCall.enqueue(callbackDate);
                }else {
                    responseBodyCall.clone().enqueue(callbackDate);
                }

            }
        }catch (Exception ex) {
        }
    }
    private void bindBooking(){
            adapter=new OwnerParkingDetailBookingAdapter(getActivity(),bookingLists);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
            loadBookings("all");
    }

    private void bindSundayTime(){
        if(OwnerPropertyDetailScreen.parkingSpace!=null){
            try {
                sundayList.clear();
                String data[]=OwnerPropertyDetailScreen.parkingSpace.sundayAvailability.split(",");
                if(data.length>0) {
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

    private void bindMondayTime(){
        if(((OwnerPropertyDetailScreen)getActivity()).parkingSpace!=null){
            try {
                mondayList.clear();
                String data[]=OwnerPropertyDetailScreen.parkingSpace.mondayAvailability.split(",");
                if(data.length>0) {
                    for (int i = 0; i < data.length; i++) {
                        if(data[i]!=null&&!data[i].isEmpty()) {
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

    private void bindTuesdayTime(){
        if(OwnerPropertyDetailScreen.parkingSpace!=null){
            try {
                tuesdayList.clear();
                String data[]=OwnerPropertyDetailScreen.parkingSpace.tuesdayAvailability.split(",");
                if(data.length>0) {
                    for (int i = 0; i < data.length; i++) {
                        if(data[i]!=null&&!data[i].isEmpty()) {
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
    //
    private void bindWednesdayTime(){
        if(OwnerPropertyDetailScreen.parkingSpace!=null){
            try {
                wednesdayList.clear();
                String data[]=OwnerPropertyDetailScreen.parkingSpace.wednesdayAvailability.split(",");
                if(data.length>0) {
                    for (int i = 0; i < data.length; i++) {
                        if(data[i]!=null&&!data[i].isEmpty()) {
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

    private void bindThursdayTime(){
        if(OwnerPropertyDetailScreen.parkingSpace!=null){
            try {

                thursdayList.clear();
                String data[]=OwnerPropertyDetailScreen.parkingSpace.thursdayAvailability.split(",");
                if(data.length>0) {
                    for (int i = 0; i < data.length; i++) {
                        if(data[i]!=null&&!data[i].isEmpty()) {
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

    private void bindFridayTime(){
        if(OwnerPropertyDetailScreen.parkingSpace!=null){
            try {

                fridayList.clear();
                String data[]=OwnerPropertyDetailScreen.parkingSpace.fridayAvailability.split(",");
                if(data.length>0) {
                    for (int i = 0; i < data.length; i++) {
                        if(data[i]!=null&&!data[i].isEmpty()) {
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

    private void bindSaturdayTime(){
        if(OwnerPropertyDetailScreen.parkingSpace!=null){
            try {

                saturdayList.clear();
                String data[]=OwnerPropertyDetailScreen.parkingSpace.saturdayAvailability.split(",");
                if(data.length>0) {
                    for (int i = 0; i < data.length; i++) {
                        if(data[i]!=null&&!data[i].isEmpty()) {
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

    private void deleteAvailableTimes(){
        try {
            dataContext.propertyAvailableTimesObjectSet.fill();
            for (int j=0;j<dataContext.propertyAvailableTimesObjectSet.size();j++)
                dataContext.propertyAvailableTimesObjectSet.remove(j).setStatus(Entity.STATUS_DELETED);

            dataContext.propertyAvailableTimesObjectSet.save();

        } catch (AdaFrameworkException e) {
            e.printStackTrace();
        }
    }

}
