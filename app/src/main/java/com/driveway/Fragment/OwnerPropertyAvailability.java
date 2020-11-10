package com.driveway.Fragment;


import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.driveway.Activity.OwnerPropertyAddScreen;
import com.driveway.Activity.OwnerPropertyDetailScreen;
import com.driveway.Adapters.OwnerPropertyTimeAdapter;
import com.driveway.Component.BButton;
import com.driveway.Component.CalendarListener;
import com.driveway.Component.DayDecorator;
import com.driveway.Component.DayView;
import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.DBHelper.tblPropertyAvailableTimes;
import com.driveway.R;
import com.driveway.Utility.DataContext;
import com.driveway.listeners.onAvailabilityDelete;
import com.driveway.listeners.onEditTimeListeners;
import com.mobandme.ada.Entity;
import com.mobandme.ada.exceptions.AdaFrameworkException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class OwnerPropertyAvailability extends Fragment {


    BButton btnSunday,btnMonday,btnTuesday,btnWednesday,btnThursday,btnFriday,btnSaturday,btnNext;
    TTextView btnShowCalendar,tvSundayChooseTime,tvMondayChooseTime,tvTuesdayChooseTime,tvWednesdayChooseTime,tvThursdayChooseTime,tvFridayChooseTime,tvSaturdayChooseTime;
    com.driveway.Component.CustomCalendarView calendarView;
    RelativeLayout rlCalendar;
    String StartTime="",EndTime="";
    OwnerPropertyTimeAdapter adapter,adapterMonday,adapterTuesday,adapterWednesday,adapterThursday,adapterFriday,adapterSaturday;
    RecyclerView rvSunday,rvMonday,rvTuesday,rvWednesday,rvThursday,rvFriday,rvSaturday;
    DataContext dataContext;
    ArrayList<tblPropertyAvailableTimes> sundayList=new ArrayList<>();
    ArrayList<tblPropertyAvailableTimes> mondayList=new ArrayList<>();
    ArrayList<tblPropertyAvailableTimes> tuesdayList=new ArrayList<>();
    ArrayList<tblPropertyAvailableTimes> wednesdayList=new ArrayList<>();
    ArrayList<tblPropertyAvailableTimes> thursdayList=new ArrayList<>();
    ArrayList<tblPropertyAvailableTimes> fridayList=new ArrayList<>();
    ArrayList<tblPropertyAvailableTimes> saturdayList=new ArrayList<>();
    int day=0;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataContext=new DataContext(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.owner_add_property_fragment_three, container, false);

        calendarView=v.findViewById(R.id.calendarView);
        btnShowCalendar=v.findViewById(R.id.btnShowCalendar);
        rlCalendar=v.findViewById(R.id.rlCalendar);
        tvSundayChooseTime=v.findViewById(R.id.tvSundayChooseTime);
        tvMondayChooseTime=v.findViewById(R.id.tvMondayChooseTime);
        tvTuesdayChooseTime=v.findViewById(R.id.tvTuesdayChooseTime);
        tvWednesdayChooseTime=v.findViewById(R.id.tvWednesdayChooseTime);
        tvThursdayChooseTime=v.findViewById(R.id.tvThursdayChooseTime);
        tvFridayChooseTime=v.findViewById(R.id.tvFridayChooseTime);
        tvSaturdayChooseTime=v.findViewById(R.id.tvSaturdayChooseTime);

        btnNext=v.findViewById(R.id.btnNext);

        btnSunday=v.findViewById(R.id.btnSunday);
        btnMonday=v.findViewById(R.id.btnMonday);
        btnTuesday=v.findViewById(R.id.btnTuesday);
        btnWednesday=v.findViewById(R.id.btnWednesday);
        btnThursday=v.findViewById(R.id.btnThursday);
        btnFriday=v.findViewById(R.id.btnFriday);
        btnSaturday=v.findViewById(R.id.btnSaturday);


        rvSunday=v.findViewById(R.id.rvSunday);
        rvMonday=v.findViewById(R.id.rvMonday);
        rvTuesday=v.findViewById(R.id.rvTuesday);
        rvWednesday=v.findViewById(R.id.rvWednesday);
        rvThursday=v.findViewById(R.id.rvThursday);
        rvFriday=v.findViewById(R.id.rvFriday);
        rvSaturday=v.findViewById(R.id.rvSaturday);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter=new OwnerPropertyTimeAdapter(getActivity(),sundayList);
        rvSunday.setLayoutManager(new GridLayoutManager(getActivity(),2));
        rvSunday.setAdapter(adapter);



        adapterMonday=new OwnerPropertyTimeAdapter(getActivity(),mondayList);
        rvMonday.setLayoutManager(new GridLayoutManager(getActivity(),2));
        rvMonday.setAdapter(adapterMonday);

        adapterTuesday=new OwnerPropertyTimeAdapter(getActivity(),tuesdayList);
        rvTuesday.setLayoutManager(new GridLayoutManager(getActivity(),2));
        rvTuesday.setAdapter(adapterTuesday);

        adapterWednesday=new OwnerPropertyTimeAdapter(getActivity(),wednesdayList);
        rvWednesday.setLayoutManager(new GridLayoutManager(getActivity(),2));
        rvWednesday.setAdapter(adapterWednesday);

        adapterThursday=new OwnerPropertyTimeAdapter(getActivity(),thursdayList);
        rvThursday.setLayoutManager(new GridLayoutManager(getActivity(),2));
        rvThursday.setAdapter(adapterThursday);

        adapterFriday=new OwnerPropertyTimeAdapter(getActivity(),fridayList);
        rvFriday.setLayoutManager(new GridLayoutManager(getActivity(),2));
        rvFriday.setAdapter(adapterFriday);

        adapterSaturday=new OwnerPropertyTimeAdapter(getActivity(),saturdayList);
        rvSaturday.setLayoutManager(new GridLayoutManager(getActivity(),2));
        rvSaturday.setAdapter(adapterSaturday);



        adapter.onDeleteTime(new onAvailabilityDelete() {
            @Override
            public void onDelete(tblPropertyAvailableTimes times) {
                if (adapter.list.size()> 0) {
                    btnSunday.setSelected(true);
                    btnSunday.setBackgroundResource(R.drawable.time_selector);
                    btnSunday.setTextColor(getResources().getColor(R.color.white));
                    rvSunday.setVisibility(View.VISIBLE);
                } else {
                    btnSunday.setSelected(false);
                    btnSunday.setBackgroundResource(R.drawable.time_selector);
                    btnSunday.setTextColor(getResources().getColor(R.color.black));
                    rvSunday.setVisibility(View.GONE);
                }
            }
        });

        adapterMonday.onDeleteTime(new onAvailabilityDelete() {
            @Override
            public void onDelete(tblPropertyAvailableTimes times) {
                if (adapterMonday.list.size()> 0) {
                    btnMonday.setSelected(true);
                    btnMonday.setBackgroundResource(R.drawable.time_selector);
                    btnMonday.setTextColor(getResources().getColor(R.color.white));
                    rvMonday.setVisibility(View.VISIBLE);
                } else {
                    btnMonday.setSelected(false);
                    btnMonday.setBackgroundResource(R.drawable.time_selector);
                    btnMonday.setTextColor(getResources().getColor(R.color.black));
                    rvMonday.setVisibility(View.GONE);
                }
            }
        });

        adapterTuesday.onDeleteTime(new onAvailabilityDelete() {
            @Override
            public void onDelete(tblPropertyAvailableTimes times) {
                if (adapterTuesday.list.size()> 0) {
                    btnTuesday.setSelected(true);
                    btnTuesday.setBackgroundResource(R.drawable.time_selector);
                    btnTuesday.setTextColor(getResources().getColor(R.color.white));
                    rvTuesday.setVisibility(View.VISIBLE);
                } else {
                    btnTuesday.setSelected(false);
                    btnTuesday.setBackgroundResource(R.drawable.time_selector);
                    btnTuesday.setTextColor(getResources().getColor(R.color.black));
                    rvTuesday.setVisibility(View.GONE);
                }
            }
        });

        adapterWednesday.onDeleteTime(new onAvailabilityDelete() {
            @Override
            public void onDelete(tblPropertyAvailableTimes times) {
                if (adapterWednesday.list.size()> 0) {
                    btnWednesday.setSelected(true);
                    btnWednesday.setBackgroundResource(R.drawable.time_selector);
                    btnWednesday.setTextColor(getResources().getColor(R.color.white));
                    rvWednesday.setVisibility(View.VISIBLE);
                } else {
                    btnWednesday.setSelected(false);
                    btnWednesday.setBackgroundResource(R.drawable.time_selector);
                    btnWednesday.setTextColor(getResources().getColor(R.color.black));
                    rvWednesday.setVisibility(View.GONE);
                }
            }
        });

        adapterThursday.onDeleteTime(new onAvailabilityDelete() {
            @Override
            public void onDelete(tblPropertyAvailableTimes times) {
                if (adapterThursday.list.size()> 0) {
                    btnThursday.setSelected(true);
                    btnThursday.setBackgroundResource(R.drawable.time_selector);
                    btnThursday.setTextColor(getResources().getColor(R.color.white));
                    rvThursday.setVisibility(View.VISIBLE);
                } else {
                    btnThursday.setSelected(false);
                    btnThursday.setBackgroundResource(R.drawable.time_selector);
                    btnThursday.setTextColor(getResources().getColor(R.color.black));
                    rvThursday.setVisibility(View.GONE);
                }
            }
        });

        adapterFriday.onDeleteTime(new onAvailabilityDelete() {
            @Override
            public void onDelete(tblPropertyAvailableTimes times) {
                if (adapterFriday.list.size()> 0) {
                    btnFriday.setSelected(true);
                    btnFriday.setBackgroundResource(R.drawable.time_selector);
                    btnFriday.setTextColor(getResources().getColor(R.color.white));
                    rvFriday.setVisibility(View.VISIBLE);
                } else {
                    btnFriday.setSelected(false);
                    btnFriday.setBackgroundResource(R.drawable.time_selector);
                    btnFriday.setTextColor(getResources().getColor(R.color.black));
                    rvFriday.setVisibility(View.GONE);
                }
            }
        });

        adapterSaturday.onDeleteTime(new onAvailabilityDelete() {
            @Override
            public void onDelete(tblPropertyAvailableTimes times) {
                if (adapterSaturday.list.size()> 0) {
                    btnSaturday.setSelected(true);
                    btnSaturday.setBackgroundResource(R.drawable.time_selector);
                    btnSaturday.setTextColor(getResources().getColor(R.color.white));
                    rvSaturday.setVisibility(View.VISIBLE);
                } else {
                    btnSaturday.setSelected(false);
                    btnSaturday.setBackgroundResource(R.drawable.time_selector);
                    btnSaturday.setTextColor(getResources().getColor(R.color.black));
                    rvSaturday.setVisibility(View.GONE);
                }
            }
        });


        adapter.onEditTime(new onEditTimeListeners() {
            @Override
            public void onEdit(tblPropertyAvailableTimes times) {
                String[] stime=times.Timing.split("-");
                day=1;
                if(stime.length>0)
                openEditTimePickerDialog(getActivity(),stime[0],stime[1],times);
            }
        });

        adapterMonday.onEditTime(new onEditTimeListeners() {
            @Override
            public void onEdit(tblPropertyAvailableTimes times) {
                String[] stime=times.Timing.split("-");
                day=2;
                if(stime.length>0)
                    openEditTimePickerDialog(getActivity(),stime[0],stime[1],times);
            }
        });

        adapterTuesday.onEditTime(new onEditTimeListeners() {
            @Override
            public void onEdit(tblPropertyAvailableTimes times) {
                String[] stime=times.Timing.split("-");
                day=3;
                if(stime.length>0)
                    openEditTimePickerDialog(getActivity(),stime[0],stime[1],times);
            }
        });

        adapterWednesday.onEditTime(new onEditTimeListeners() {
            @Override
            public void onEdit(tblPropertyAvailableTimes times) {
                String[] stime=times.Timing.split("-");
                day=4;
                if(stime.length>0)
                    openEditTimePickerDialog(getActivity(),stime[0],stime[1],times);
            }
        });

        adapterThursday.onEditTime(new onEditTimeListeners() {
            @Override
            public void onEdit(tblPropertyAvailableTimes times) {
                String[] stime=times.Timing.split("-");
                day=5;
                if(stime.length>0)
                    openEditTimePickerDialog(getActivity(),stime[0],stime[1],times);
            }
        });
        adapterFriday.onEditTime(new onEditTimeListeners() {
            @Override
            public void onEdit(tblPropertyAvailableTimes times) {
                String[] stime=times.Timing.split("-");
                day=6;
                if(stime.length>0)
                    openEditTimePickerDialog(getActivity(),stime[0],stime[1],times);
            }
        });

        adapterSaturday.onEditTime(new onEditTimeListeners() {
            @Override
            public void onEdit(tblPropertyAvailableTimes times) {
                String[] stime=times.Timing.split("-");
                day=7;
                if(stime.length>0)
                    openEditTimePickerDialog(getActivity(),stime[0],stime[1],times);
            }
        });

    }




    @UiThread
    private void bindCalender() throws AdaFrameworkException {

        dataContext.propertyAvailableTimesObjectSet.fill();
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

            }

            @Override
            public void onMonthChanged(Date date) {
                SimpleDateFormat df = new SimpleDateFormat("MM-yyyy");
                //Toast.makeText(CustomisedCalendarActivity.this, df.format(date), Toast.LENGTH_SHORT).show();
            }
        });

        final Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "ProductSansRegular.ttf");
        if (null != typeface) {
            calendarView.setCustomTypeface(typeface);
            calendarView.refreshCalendar(currentCalendar);
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            List<DayDecorator> decorators = new ArrayList<>();
            decorators.add(new DisabledColorDecorator());
            calendarView.setDecorators(decorators);
            calendarView.refreshCalendar(currentCalendar);

        },10);


    }
    private class DisabledColorDecorator implements DayDecorator {
        @Override
        public void decorate(DayView dayView) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
                SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
                HashSet<String> strings = new HashSet<>();
                String dayname2 = formatter2.format(dayView.getDate());
                String dayname = formatter.format(dayView.getDate());
                int color = Color.parseColor("#E8E8E8");
                try {
                    if(dataContext.propertyAvailableTimesObjectSet.size()>0){
                        for(tblPropertyAvailableTimes obj:dataContext.propertyAvailableTimesObjectSet){
                            if(obj.DayName.equalsIgnoreCase("sunday") && !obj.Timing.isEmpty()){
                                if(dayname.equalsIgnoreCase("sunday")){
                                    dayView.setBackgroundResource(R.drawable.border_button_with_fill_calender_two);
                                    strings.add("sunday");
                                }
                            }
                            else if(obj.DayName.equalsIgnoreCase("monday") && !obj.Timing.isEmpty()){
                                if(dayname.equalsIgnoreCase("monday")){
                                    dayView.setBackgroundResource(R.drawable.border_button_with_fill_calender_two);
                                    strings.add("monday");
                                }
                            }
                            else if(obj.DayName.equalsIgnoreCase("tuesday") && !obj.Timing.isEmpty()){
                                if(dayname.equalsIgnoreCase("tuesday")){
                                    dayView.setBackgroundResource(R.drawable.border_button_with_fill_calender_two);
                                    strings.add("tuesday");
                                }
                            }
                            else if(obj.DayName.equalsIgnoreCase("wednesday") && !obj.Timing.isEmpty()){
                                if(dayname.equalsIgnoreCase("wednesday")){
                                    dayView.setBackgroundResource(R.drawable.border_button_with_fill_calender_two);
                                    strings.add("wednesday");
                                }
                            }
                            else if(obj.DayName.equalsIgnoreCase("thursday") && !obj.Timing.isEmpty()){
                                if(dayname.equalsIgnoreCase("thursday")){
                                    dayView.setBackgroundResource(R.drawable.border_button_with_fill_calender_two);
                                    strings.add("thursday");
                                }
                            }
                            else if(obj.DayName.equalsIgnoreCase("friday") && !obj.Timing.isEmpty()){
                                if(dayname.equalsIgnoreCase("friday")){
                                    dayView.setBackgroundResource(R.drawable.border_button_with_fill_calender_two);
                                    strings.add("friday");
                                }
                            }
                            else if(obj.DayName.equalsIgnoreCase("saturday") && !obj.Timing.isEmpty()){
                                if(dayname.equalsIgnoreCase("saturday")){
                                    dayView.setBackgroundResource(R.drawable.border_button_with_fill_calender_two);
                                    strings.add("saturday");
                                }
                            }
                        }
                        if (!strings.contains(dayname.toLowerCase())) {
                            dayView.setBackgroundResource(R.drawable.border_button_with_fill_calender_two);
                        }else{
                            dayView.setBackgroundResource(R.drawable.rounded_border_white_with_small_radius);
                        }
                        if (DateFormat.format("yyyy-MM-dd", new Date().getTime()).toString().equalsIgnoreCase(dayname2)) {
                            dayView.setBackgroundResource(R.drawable.border_button_with_fill_calendar);
                        }
                    }else{
                        dayView.setBackgroundResource(R.drawable.rounded_border_white_with_small_radius);
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

    private void clearList(){
        sundayList.clear();
        mondayList.clear();
        tuesdayList.clear();
        wednesdayList.clear();
        thursdayList.clear();
        fridayList.clear();
        saturdayList.clear();

    }
    private void bindTime(){
        if(dataContext!=null){
            try {
                dataContext.propertyAvailableTimesObjectSet.fill();
                if(dataContext.propertyAvailableTimesObjectSet.size()>0){
                    clearList();
                    for(int i=0;i<dataContext.propertyAvailableTimesObjectSet.size();i++){
                        System.out.println("DAY NAME ====>"+dataContext.propertyAvailableTimesObjectSet.get(i).DayName);
                        System.out.println("DAY TIME ====>"+dataContext.propertyAvailableTimesObjectSet.get(i).Timing);
                        System.out.println("PARKING ID ====>"+dataContext.propertyAvailableTimesObjectSet.get(i).ParkingID);
                        if(dataContext.propertyAvailableTimesObjectSet.get(i).DayName.equalsIgnoreCase("Sunday")){
                            sundayList.add(dataContext.propertyAvailableTimesObjectSet.get(i));
                        }
                        else if(dataContext.propertyAvailableTimesObjectSet.get(i).DayName.equalsIgnoreCase("Monday")){
                            mondayList.add(dataContext.propertyAvailableTimesObjectSet.get(i));
                        }
                        else if(dataContext.propertyAvailableTimesObjectSet.get(i).DayName.equalsIgnoreCase("Tuesday")){
                            tuesdayList.add(dataContext.propertyAvailableTimesObjectSet.get(i));
                        }
                        else if(dataContext.propertyAvailableTimesObjectSet.get(i).DayName.equalsIgnoreCase("Wednesday")){
                            wednesdayList.add(dataContext.propertyAvailableTimesObjectSet.get(i));
                        }
                        else if(dataContext.propertyAvailableTimesObjectSet.get(i).DayName.equalsIgnoreCase("Thursday")){
                            thursdayList.add(dataContext.propertyAvailableTimesObjectSet.get(i));
                        }
                        else if(dataContext.propertyAvailableTimesObjectSet.get(i).DayName.equalsIgnoreCase("Friday")){
                            fridayList.add(dataContext.propertyAvailableTimesObjectSet.get(i));
                        }
                        else if(dataContext.propertyAvailableTimesObjectSet.get(i).DayName.equalsIgnoreCase("Saturday")){
                            saturdayList.add(dataContext.propertyAvailableTimesObjectSet.get(i));
                        }
                    }

                }

                adapter.notifyDataSetChanged();

            } catch (AdaFrameworkException e) {
                e.printStackTrace();
            }
        }
    }

//    private void bindMondayTime(){
//        if(dataContext!=null){
//            try {
//                dataContext.propertyAvailableTimesObjectSet.fill("dayname = ?",new String[]{"Monday"},null);
//                mondayList.clear();
//                mondayList.addAll(dataContext.propertyAvailableTimesObjectSet);
//                adapterMonday=new OwnerPropertyTimeAdapter(getActivity(),mondayList);
//                rvMonday.setLayoutManager(new GridLayoutManager(getActivity(),2));
//                rvMonday.setAdapter(adapterMonday);
//                if(dataContext.propertyAvailableTimesObjectSet.size()>0){
//                    System.out.println("PROPERTY DETAILS");
//                    System.out.println("-------------------------");
//                    for(tblPropertyAvailableTimes obj:dataContext.propertyAvailableTimesObjectSet){
//
//                        System.out.println("DAY NAME => "+obj.DayName);
//                        System.out.println("TIME NAME => "+obj.Timing);
//                        System.out.println("-------------------------");
//
//                    }
//                    System.out.println("-------------------------");
//                }
//            } catch (AdaFrameworkException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void bindTuesdayTime(){
//        if(dataContext!=null){
//            try {
//                dataContext.propertyAvailableTimesObjectSet.fill("dayname = ?",new String[]{"Tuesday"},null);
//                tuesdayList.clear();
//                tuesdayList.addAll(dataContext.propertyAvailableTimesObjectSet);
//                adapterTuesday=new OwnerPropertyTimeAdapter(getActivity(),tuesdayList);
//                rvTuesday.setLayoutManager(new GridLayoutManager(getActivity(),2));
//                rvTuesday.setAdapter(adapterTuesday);
//                if(dataContext.propertyAvailableTimesObjectSet.size()>0){
//                    System.out.println("PROPERTY DETAILS");
//                    System.out.println("-------------------------");
//                    for(tblPropertyAvailableTimes obj:dataContext.propertyAvailableTimesObjectSet){
//
//                        System.out.println("DAY NAME => "+obj.DayName);
//                        System.out.println("TIME NAME => "+obj.Timing);
//                        System.out.println("-------------------------");
//
//                    }
//                    System.out.println("-------------------------");
//                }
//            } catch (AdaFrameworkException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void bindWednesdayTime(){
//        if(dataContext!=null){
//            try {
//                dataContext.propertyAvailableTimesObjectSet.fill("dayname = ?",new String[]{"Wednesday"},null);
//                wednesdayList.clear();
//                wednesdayList.addAll(dataContext.propertyAvailableTimesObjectSet);
//                adapterWednesday=new OwnerPropertyTimeAdapter(getActivity(),wednesdayList);
//                rvWednesday.setLayoutManager(new GridLayoutManager(getActivity(),2));
//                rvWednesday.setAdapter(adapterWednesday);
//                if(dataContext.propertyAvailableTimesObjectSet.size()>0){
//                    System.out.println("PROPERTY DETAILS");
//                    System.out.println("-------------------------");
//                    for(tblPropertyAvailableTimes obj:dataContext.propertyAvailableTimesObjectSet){
//
//                        System.out.println("DAY NAME => "+obj.DayName);
//                        System.out.println("TIME NAME => "+obj.Timing);
//                        System.out.println("-------------------------");
//
//                    }
//                    System.out.println("-------------------------");
//                }
//            } catch (AdaFrameworkException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void bindThursdayTime(){
//        if(dataContext!=null){
//            try {
//                dataContext.propertyAvailableTimesObjectSet.fill("dayname = ?",new String[]{"Thursday"},null);
//                thursdayList.clear();
//                thursdayList.addAll(dataContext.propertyAvailableTimesObjectSet);
//                adapterThursday=new OwnerPropertyTimeAdapter(getActivity(),thursdayList);
//                rvThursday.setLayoutManager(new GridLayoutManager(getActivity(),2));
//                rvThursday.setAdapter(adapterThursday);
//                if(dataContext.propertyAvailableTimesObjectSet.size()>0){
//                    System.out.println("PROPERTY DETAILS");
//                    System.out.println("-------------------------");
//                    for(tblPropertyAvailableTimes obj:dataContext.propertyAvailableTimesObjectSet){
//
//                        System.out.println("DAY NAME => "+obj.DayName);
//                        System.out.println("TIME NAME => "+obj.Timing);
//                        System.out.println("-------------------------");
//
//                    }
//                    System.out.println("-------------------------");
//                }
//            } catch (AdaFrameworkException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void bindFridayTime(){
//        if(dataContext!=null){
//            try {
//                dataContext.propertyAvailableTimesObjectSet.fill("dayname = ?",new String[]{"Friday"},null);
//                fridayList.clear();
//                fridayList.addAll(dataContext.propertyAvailableTimesObjectSet);
//                adapterFriday=new OwnerPropertyTimeAdapter(getActivity(),fridayList);
//                rvFriday.setLayoutManager(new GridLayoutManager(getActivity(),2));
//                rvFriday.setAdapter(adapterFriday);
//                if(dataContext.propertyAvailableTimesObjectSet.size()>0){
//                    System.out.println("PROPERTY DETAILS");
//                    System.out.println("-------------------------");
//                    for(tblPropertyAvailableTimes obj:dataContext.propertyAvailableTimesObjectSet){
//
//                        System.out.println("DAY NAME => "+obj.DayName);
//                        System.out.println("TIME NAME => "+obj.Timing);
//                        System.out.println("-------------------------");
//
//                    }
//                    System.out.println("-------------------------");
//                }
//            } catch (AdaFrameworkException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void bindSaturdayTime(){
//        if(dataContext!=null){
//            try {
//                dataContext.propertyAvailableTimesObjectSet.fill("dayname = ?",new String[]{"Saturday"},null);
//                saturdayList.clear();
//                saturdayList.addAll(dataContext.propertyAvailableTimesObjectSet);
//                adapterSaturday=new OwnerPropertyTimeAdapter(getActivity(),saturdayList);
//                rvSaturday.setLayoutManager(new GridLayoutManager(getActivity(),2));
//                rvSaturday.setAdapter(adapterSaturday);
//                if(dataContext.propertyAvailableTimesObjectSet.size()>0){
//                    System.out.println("PROPERTY DETAILS");
//                    System.out.println("-------------------------");
//                    for(tblPropertyAvailableTimes obj:dataContext.propertyAvailableTimesObjectSet){
//
//                        System.out.println("DAY NAME => "+obj.DayName);
//                        System.out.println("TIME NAME => "+obj.Timing);
//                        System.out.println("-------------------------");
//
//                    }
//                    System.out.println("-------------------------");
//                }
//            } catch (AdaFrameworkException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        btnShowCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rlCalendar.getVisibility()==View.VISIBLE){
                    rlCalendar.setVisibility(View.GONE);
                }else{
                    try {
                        bindCalender();
                    } catch (AdaFrameworkException e) {
                        e.printStackTrace();
                    }
                    rlCalendar.setVisibility(View.VISIBLE);
                }
            }
        });
        btnSunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnSunday.isSelected()){

                    if(sundayList.size()<=0){
                        btnSunday.setSelected(false);
                        btnSunday.setBackgroundResource(R.drawable.time_selector);
                        btnSunday.setTextColor(getResources().getColor(R.color.black));
                        rvSunday.setVisibility(View.GONE);
                    }else{
                        btnSunday.setSelected(true);
                        btnSunday.setBackgroundResource(R.drawable.time_selector);
                        btnSunday.setTextColor(getResources().getColor(R.color.white));
                    }
                }else{
                    btnSunday.setSelected(true);
                    btnSunday.setBackgroundResource(R.drawable.time_selector);
                    btnSunday.setTextColor(getResources().getColor(R.color.white));
                }
                day=1;
                openTimePickerDialog(getActivity());

            }
        });
        tvSundayChooseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day=1;
                openTimePickerDialog(getActivity());
            }
        });


        btnMonday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnMonday.isSelected()){
                    if(mondayList.size()<=0){
                        btnMonday.setSelected(false);
                        btnMonday.setBackgroundResource(R.drawable.time_selector);
                        btnMonday.setTextColor(getResources().getColor(R.color.black));
                        rvMonday.setVisibility(View.GONE);
                    }else{
                        btnMonday.setSelected(true);
                        btnMonday.setBackgroundResource(R.drawable.time_selector);
                        btnMonday.setTextColor(getResources().getColor(R.color.white));
                    }
                }else{
                    btnMonday.setSelected(true);
                    btnMonday.setBackgroundResource(R.drawable.time_selector);
                    btnMonday.setTextColor(getResources().getColor(R.color.white));
                }
                day=2;
                openTimePickerDialog(getActivity());

            }
        });


        btnTuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnTuesday.isSelected()){
                    if(tuesdayList.size()<=0){
                        btnTuesday.setSelected(false);
                        btnTuesday.setBackgroundResource(R.drawable.time_selector);
                        btnTuesday.setTextColor(getResources().getColor(R.color.black));
                        rvTuesday.setVisibility(View.GONE);
                    }else{
                        btnTuesday.setSelected(true);
                        btnTuesday.setBackgroundResource(R.drawable.time_selector);
                        btnTuesday.setTextColor(getResources().getColor(R.color.white));
                    }
                }else{
                    btnTuesday.setSelected(true);
                    btnTuesday.setBackgroundResource(R.drawable.time_selector);
                    btnTuesday.setTextColor(getResources().getColor(R.color.white));
                }

                day=3;
                openTimePickerDialog(getActivity());

            }
        });

        btnWednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(btnWednesday.isSelected()){
                    if(wednesdayList.size()<=0){
                        btnWednesday.setSelected(false);
                        btnWednesday.setBackgroundResource(R.drawable.time_selector);
                        btnWednesday.setTextColor(getResources().getColor(R.color.black));
                        rvWednesday.setVisibility(View.GONE);
                    }else{
                        btnWednesday.setSelected(true);
                        btnWednesday.setBackgroundResource(R.drawable.time_selector);
                        btnWednesday.setTextColor(getResources().getColor(R.color.white));
                    }
                }else {
                    btnWednesday.setSelected(true);
                    btnWednesday.setBackgroundResource(R.drawable.time_selector);
                    btnWednesday.setTextColor(getResources().getColor(R.color.white));
                }
                day=4;
                openTimePickerDialog(getActivity());

            }
        });

        btnThursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(btnThursday.isSelected()){
                    if(thursdayList.size()<=0){
                        btnThursday.setSelected(false);
                        btnThursday.setBackgroundResource(R.drawable.time_selector);
                        btnThursday.setTextColor(getResources().getColor(R.color.black));
                        rvThursday.setVisibility(View.GONE);
                    }else{
                        btnThursday.setSelected(true);
                        btnThursday.setBackgroundResource(R.drawable.time_selector);
                        btnThursday.setTextColor(getResources().getColor(R.color.white));
                    }
                }else {
                    btnThursday.setSelected(true);
                    btnThursday.setBackgroundResource(R.drawable.time_selector);
                    btnThursday.setTextColor(getResources().getColor(R.color.white));
                }
                day=5;
                openTimePickerDialog(getActivity());

            }
        });

        btnFriday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(btnFriday.isSelected()){
                    if(fridayList.size()<=0){
                        btnFriday.setSelected(false);
                        btnFriday.setBackgroundResource(R.drawable.time_selector);
                        btnFriday.setTextColor(getResources().getColor(R.color.black));
                        rvFriday.setVisibility(View.GONE);
                    }else{
                        btnFriday.setSelected(true);
                        btnFriday.setBackgroundResource(R.drawable.time_selector);
                        btnFriday.setTextColor(getResources().getColor(R.color.white));
                    }
                }else {
                    btnFriday.setSelected(true);
                    btnFriday.setBackgroundResource(R.drawable.time_selector);
                    btnFriday.setTextColor(getResources().getColor(R.color.white));
                }
                day=6;
                openTimePickerDialog(getActivity());

            }
        });

        btnSaturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(btnSaturday.isSelected()){
                    if(saturdayList.size()<=0){
                        btnSaturday.setSelected(false);
                        btnSaturday.setBackgroundResource(R.drawable.time_selector);
                        btnSaturday.setTextColor(getResources().getColor(R.color.black));
                        rvSaturday.setVisibility(View.GONE);
                    }else{
                        btnSaturday.setSelected(true);
                        btnSaturday.setBackgroundResource(R.drawable.time_selector);
                        btnSaturday.setTextColor(getResources().getColor(R.color.white));
                    }
                }else {
                    btnSaturday.setSelected(true);
                    btnSaturday.setBackgroundResource(R.drawable.time_selector);
                    btnSaturday.setTextColor(getResources().getColor(R.color.white));
                }
                day=7;
                openTimePickerDialog(getActivity());

            }
        });

        tvSundayChooseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day=1;
                openTimePickerDialog(getActivity());
            }
        });

        tvMondayChooseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day=2;
                openTimePickerDialog(getActivity());
            }
        });

        tvTuesdayChooseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day=3;
                openTimePickerDialog(getActivity());
            }
        });

        tvWednesdayChooseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day=4;
                openTimePickerDialog(getActivity());
            }
        });

        tvThursdayChooseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day=5;
                openTimePickerDialog(getActivity());
            }
        });

        tvFridayChooseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day=6;
                openTimePickerDialog(getActivity());
            }
        });
        tvSaturdayChooseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day=7;
                openTimePickerDialog(getActivity());
            }
        });

        btnNext.setOnClickListener(v -> {
            try {
                dataContext.propertyAvailableTimesObjectSet.fill();
                if(dataContext.propertyAvailableTimesObjectSet.size()<=0){
                    Utility.showAlert(getActivity(),"Please choose any time for availability.");
                }else if(btnSunday.isSelected() && sundayList.size()==0) {
                    Utility.showAlert(getActivity(),"Please select any time for sunday");
                }else if(btnMonday.isSelected() && mondayList.size()==0) {
                    Utility.showAlert(getActivity(),"Please select any time for monday");
                }else if(btnTuesday.isSelected() && tuesdayList.size()==0) {
                    Utility.showAlert(getActivity(),"Please select any time for tuesday");
                }else if(btnWednesday.isSelected() && wednesdayList.size()==0) {
                    Utility.showAlert(getActivity(),"Please select any time for wednesday");
                }else if(btnThursday.isSelected() && thursdayList.size()==0) {
                    Utility.showAlert(getActivity(),"Please select any time for thursday");
                }else if(btnFriday.isSelected() && fridayList.size()==0) {
                    Utility.showAlert(getActivity(),"Please select any time for friday");
                }else if(btnSaturday.isSelected() && saturdayList.size()==0) {
                    Utility.showAlert(getActivity(),"Please select any time for saturday");
                }else{
                    ((OwnerPropertyAddScreen)getActivity()).navigation(3);
                }
            } catch (AdaFrameworkException e) {
                e.printStackTrace();
            }


        });
    }

    @Override
    public void onStart() {
        super.onStart();
        StartTime="";EndTime="";
        bindDaysColor();
        bindTime();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void openTimePickerDialog(Activity act){
        int mHour,mMinutes,mSeconds;
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR);
        mMinutes = c.get(Calendar.MINUTE);
        mSeconds = c.get(Calendar.SECOND);


        TimePickerDialog timePickerDialog=new TimePickerDialog(act,R.style.MyAppTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {


                String format="";
                if (hourOfDay == 0) {

                    hourOfDay += 12;

                    format = " AM";
                }
                else if (hourOfDay == 12) {

                    format = " PM";

                }
                else if (hourOfDay > 11) {

                    hourOfDay -= 12;
                    if(hourOfDay==0){
                        hourOfDay=12;
                    }

                    format = " PM";

                }
                else {

                    format = " AM";
                }

                    StartTime=(String.format("%02d:%02d", hourOfDay, minute))+format;
                openendTimePicker(getActivity());


            }
        },mHour,mMinutes,false);
        timePickerDialog.setTitle("Select start time");
        timePickerDialog.show();

    }

    public void openendTimePicker(Activity act){
        int mHour,mMinutes,mSeconds;
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR);
        mMinutes = c.get(Calendar.MINUTE);
        mSeconds = c.get(Calendar.SECOND);


        TimePickerDialog timePickerDialog2=new TimePickerDialog(act,R.style.MyAppTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                String format="";
                if (hourOfDay == 0) {

                    hourOfDay += 12;

                    format = " AM";
                }
                else if (hourOfDay == 12) {

                    format = " PM";

                }
                else if (hourOfDay > 11) {

                    hourOfDay -= 12;
                    if(hourOfDay==0){
                        hourOfDay=12;
                    }
                    format = " PM";

                }
                else {
                    if(hourOfDay==0){
                        hourOfDay=12;
                    }
                    format = " AM";
                }



                EndTime=(String.format("%02d:%02d", hourOfDay, minute))+format;
                System.out.println("START DATE SELECT---->"+StartTime);
                System.out.println("END DATE SELECT---->"+EndTime);

                if(adapter!=null && adapterMonday!=null && adapterTuesday!=null && adapterWednesday!=null && adapterThursday!=null && adapterFriday!=null && adapterSaturday!=null) {
                    tblPropertyAvailableTimes times=new tblPropertyAvailableTimes();

                    try {
                        if(day==1)
                            times.DayName="Sunday";
                        else if(day==2)
                            times.DayName="Monday";
                        else if(day==3)
                            times.DayName="Tuesday";
                        else if(day==4)
                            times.DayName="Wednesday";
                        else if(day==5)
                            times.DayName="Thursday";
                        else if(day==6)
                            times.DayName="Friday";
                        else if(day==7)
                            times.DayName="Saturday";

                        if(!StartTime.isEmpty() && !EndTime.isEmpty()) {
                            times.Timing = StartTime + " - " + EndTime;
                            times.setStatus(Entity.STATUS_NEW);
                            dataContext.propertyAvailableTimesObjectSet.add(times);
                            dataContext.propertyAvailableTimesObjectSet.save();
                            if(day==1)
                                adapter.add(times);
                            else if(day==2)
                                adapterMonday.add(times);
                            else if(day==3)
                                adapterTuesday.add(times);
                            else if(day==4)
                                adapterWednesday.add(times);
                            else if(day==5)
                                adapterThursday.add(times);
                            else if(day==6)
                                adapterFriday.add(times);
                            else if(day==7)
                                adapterSaturday.add(times);

                            StartTime = "";
                            EndTime = "";

                            adapter.notifyDataSetChanged();
                            adapterMonday.notifyDataSetChanged();
                            adapterTuesday.notifyDataSetChanged();
                            adapterWednesday.notifyDataSetChanged();
                            adapterThursday.notifyDataSetChanged();
                            adapterFriday.notifyDataSetChanged();
                            adapterSaturday.notifyDataSetChanged();
                            bindTime();
                            bindDaysColor();
                            try {
                                bindCalender();
                            } catch (AdaFrameworkException e) {
                                e.printStackTrace();
                            }

                        }
                        else{
                            Utility.showAlert(getActivity(),"Please select start time or end time");
                        }

                    } catch (AdaFrameworkException e) {
                        e.printStackTrace();
                    }

                }else{
                    System.out.println("ELSE PART CALLED====");
                }
            }
        },mHour,mMinutes,false);
        timePickerDialog2.setTitle("Select end time");
        timePickerDialog2.show();
    }



    public void openEditTimePickerDialog(Activity act,String startTime,String endTime,tblPropertyAvailableTimes times){


        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        Date date = null;
        try {
            date = sdf.parse(startTime);
        } catch (ParseException e) {
        }
        final Calendar c = Calendar.getInstance();
        c.setTime(date);

//        TimePicker picker = new TimePicker(getApplicationContext());
//        picker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
//        picker.setCurrentMinute(c.get(Calendar.MINUTE));


        int mHour,mMinutes,mSeconds;
        //final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR);
        mMinutes = c.get(Calendar.MINUTE);
        mSeconds = c.get(Calendar.SECOND);


        TimePickerDialog timePickerDialog=new TimePickerDialog(act,R.style.MyAppTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {


                String format="";
                if (hourOfDay == 0) {

                    hourOfDay += 12;

                    format = " AM";
                }
                else if (hourOfDay == 12) {

                    format = " PM";

                }
                else if (hourOfDay > 11) {

                    hourOfDay -= 12;
                    if(hourOfDay==0){
                        hourOfDay=12;
                    }

                    format = " PM";

                }
                else {

                    format = " AM";
                }

                StartTime=(String.format("%02d:%02d", hourOfDay, minute))+format;
                openEditendTimePicker(getActivity(),endTime,times);


            }
        },mHour,mMinutes,false);
        timePickerDialog.setTitle("Select start time");
        timePickerDialog.show();

    }
    public void openEditendTimePicker(Activity act,String endTime,tblPropertyAvailableTimes id){

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        Date date = null;
        try {
            date = sdf.parse(endTime);

            final Calendar c = Calendar.getInstance();
            c.setTime(date);

            int mHour,mMinutes,mSeconds;
            //final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR);
            mMinutes = c.get(Calendar.MINUTE);
            mSeconds = c.get(Calendar.SECOND);


            TimePickerDialog timePickerDialog2=new TimePickerDialog(act,R.style.MyAppTheme, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                    String format="";
                    if (hourOfDay == 0) {

                        hourOfDay += 12;

                        format = " AM";
                    }
                    else if (hourOfDay == 12) {

                        format = " PM";

                    }
                    else if (hourOfDay > 11) {

                        hourOfDay -= 12;
                        if(hourOfDay==0){
                            hourOfDay=12;
                        }
                        format = " PM";

                    }
                    else {
                        if(hourOfDay==0){
                            hourOfDay=12;
                        }
                        format = " AM";
                    }



                    EndTime=(String.format("%02d:%02d", hourOfDay, minute))+format;
                    //System.out.println("START DATE SELECT---->"+StartTime);
                    //System.out.println("END DATE SELECT---->"+EndTime);

                    if(adapter!=null && adapterMonday!=null && adapterTuesday!=null && adapterWednesday!=null && adapterThursday!=null && adapterFriday!=null && adapterSaturday!=null) {
                        //tblPropertyAvailableTimes times=null;
//                    try {
//                        dataContext.propertyAvailableTimesObjectSet.fill("id=?",new String[]{id},null);
//                        times=dataContext.propertyAvailableTimesObjectSet.get(0);
//                    } catch (AdaFrameworkException e) {
//                        e.printStackTrace();
//                    }


                        try {


                            if(!StartTime.isEmpty() && !EndTime.isEmpty()) {



                                tblPropertyAvailableTimes times=new tblPropertyAvailableTimes();
                                if(day==1) {
                                    times.DayName = "Sunday";

                                }
                                else if(day==2) {
                                    times.DayName = "Monday";
                                }
                                else if(day==3) {
                                    times.DayName = "Tuesday";
                                }
                                else if(day==4) {
                                    times.DayName = "Wednesday";
                                }
                                else if(day==5) {
                                    times.DayName = "Thursday";
                                }
                                else if(day==6) {
                                    times.DayName = "Friday";
                                }
                                else if(day==7) {
                                    times.DayName = "Saturday";
                                }


//                                id.Timing = StartTime + "-" + EndTime;
//                                dataContext.propertyAvailableTimesObjectSet.remove(id);
//                                dataContext.propertyAvailableTimesObjectSet.save();

                                dataContext.propertyAvailableTimesObjectSet.fill("ID = ?",new String[]{id.getID()+""},null);
                                if(dataContext.propertyAvailableTimesObjectSet.size()>0){
                                    for(int i=0;i<dataContext.propertyAvailableTimesObjectSet.size();i++)
                                        dataContext.propertyAvailableTimesObjectSet.remove(i).setStatus(Entity.STATUS_DELETED);

                                    dataContext.propertyAvailableTimesObjectSet.save();
                                }



                                adapter.delete(id);
                                adapterMonday.delete(id);
                                adapterTuesday.delete(id);
                                adapterWednesday.delete(id);
                                adapterThursday.delete(id);
                                adapterFriday.delete(id);
                                adapterSaturday.delete(id);


                                times.Timing = StartTime + " - " + EndTime;
                                times.setStatus(Entity.STATUS_NEW);
                                dataContext.propertyAvailableTimesObjectSet.save(times);
                                if(day==1)
                                    adapter.add(times);
                                else if(day==2)
                                    adapterMonday.add(times);
                                else if(day==3)
                                    adapterTuesday.add(times);
                                else if(day==4)
                                    adapterWednesday.add(times);
                                else if(day==5)
                                    adapterThursday.add(times);
                                else if(day==6)
                                    adapterFriday.add(times);
                                else if(day==7)
                                    adapterSaturday.add(times);


                                StartTime = "";
                                EndTime = "";

                                adapter.notifyDataSetChanged();
                                adapterMonday.notifyDataSetChanged();
                                adapterTuesday.notifyDataSetChanged();
                                adapterWednesday.notifyDataSetChanged();
                                adapterThursday.notifyDataSetChanged();
                                adapterFriday.notifyDataSetChanged();
                                adapterSaturday.notifyDataSetChanged();
                                //bindTime();
                                bindDaysColor();
                                try {
                                    bindCalender();
                                } catch (AdaFrameworkException e) {
                                    e.printStackTrace();
                                }

                            }
                            else{
                                Utility.showAlert(getActivity(),"Please select start time or end time");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }else{
                        System.out.println("ELSE PART CALLED====");
                    }
                }
            },mHour,mMinutes,false);
            timePickerDialog2.setTitle("Select end time");
            timePickerDialog2.show();
        } catch (ParseException e) {
        }
    }

    private void bindDaysColor(){
        try {
                if (sundayList.size()> 0) {
                    btnSunday.setSelected(true);
                    btnSunday.setBackgroundResource(R.drawable.time_selector);
                    btnSunday.setTextColor(getResources().getColor(R.color.white));
                    rvSunday.setVisibility(View.VISIBLE);
                } else {
                    btnSunday.setSelected(false);
                    btnSunday.setBackgroundResource(R.drawable.time_selector);
                    btnSunday.setTextColor(getResources().getColor(R.color.black));
                    rvSunday.setVisibility(View.GONE);
                }
            if (mondayList.size()> 0) {
                btnMonday.setSelected(true);
                btnMonday.setBackgroundResource(R.drawable.time_selector);
                btnMonday.setTextColor(getResources().getColor(R.color.white));
                rvMonday.setVisibility(View.VISIBLE);
            } else {
                btnMonday.setSelected(false);
                btnMonday.setBackgroundResource(R.drawable.time_selector);
                btnMonday.setTextColor(getResources().getColor(R.color.black));
                rvMonday.setVisibility(View.GONE);
            }

            if (tuesdayList.size()> 0) {
                btnTuesday.setSelected(true);
                btnTuesday.setBackgroundResource(R.drawable.time_selector);
                btnTuesday.setTextColor(getResources().getColor(R.color.white));
                rvTuesday.setVisibility(View.VISIBLE);
            } else {
                btnTuesday.setSelected(false);
                btnTuesday.setBackgroundResource(R.drawable.time_selector);
                btnTuesday.setTextColor(getResources().getColor(R.color.black));
                rvTuesday.setVisibility(View.GONE);
            }

            if (wednesdayList.size()> 0) {
                btnWednesday.setSelected(true);
                btnWednesday.setBackgroundResource(R.drawable.time_selector);
                btnWednesday.setTextColor(getResources().getColor(R.color.white));
                rvWednesday.setVisibility(View.VISIBLE);
            } else {
                btnWednesday.setSelected(false);
                btnWednesday.setBackgroundResource(R.drawable.time_selector);
                btnWednesday.setTextColor(getResources().getColor(R.color.black));
                rvWednesday.setVisibility(View.GONE);
            }



            if (thursdayList.size()> 0) {
                btnThursday.setSelected(true);
                btnThursday.setBackgroundResource(R.drawable.time_selector);
                btnThursday.setTextColor(getResources().getColor(R.color.white));
                rvThursday.setVisibility(View.VISIBLE);
            } else {
                btnThursday.setSelected(false);
                btnThursday.setBackgroundResource(R.drawable.time_selector);
                btnThursday.setTextColor(getResources().getColor(R.color.black));
                rvThursday.setVisibility(View.GONE);
            }

            if (fridayList.size()> 0) {
                btnFriday.setSelected(true);
                btnFriday.setBackgroundResource(R.drawable.time_selector);
                btnFriday.setTextColor(getResources().getColor(R.color.white));
                rvFriday.setVisibility(View.VISIBLE);
            } else {
                btnFriday.setSelected(false);
                btnFriday.setBackgroundResource(R.drawable.time_selector);
                btnFriday.setTextColor(getResources().getColor(R.color.black));
                rvFriday.setVisibility(View.GONE);
            }


            if (saturdayList.size()> 0) {
                btnSaturday.setSelected(true);
                btnSaturday.setBackgroundResource(R.drawable.time_selector);
                btnSaturday.setTextColor(getResources().getColor(R.color.white));
                rvSaturday.setVisibility(View.VISIBLE);
            } else {
                btnSaturday.setSelected(false);
                btnSaturday.setBackgroundResource(R.drawable.time_selector);
                btnSaturday.setTextColor(getResources().getColor(R.color.black));
                rvSaturday.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
