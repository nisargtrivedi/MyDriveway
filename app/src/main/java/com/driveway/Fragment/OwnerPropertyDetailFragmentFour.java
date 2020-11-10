package com.driveway.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.driveway.Activity.OwnerPropertyDetailScreen;
import com.driveway.Component.TTextView;
import com.driveway.DBHelper.tblStay;
import com.driveway.R;
import com.driveway.Utility.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;


public class OwnerPropertyDetailFragmentFour extends Fragment {

    TTextView tvShortTime, tvHourTime, tvDailyTime, tvMonthlyTime, tvWeeklyTime;

    LinearLayout llWeekly, llMonthly, llDaily, llHour, llshort;
    ArrayList<tblStay> stayArrayList = new ArrayList<>();
    HashSet<String> time = new HashSet<>();
    View viewshort, viewhour, viewdaily, viewweekly, viewmonth;

    int minlock=0;
    int hourlock=0;
    int weeklock=0;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.propertydetail_fragement_four, container, false);
        tvShortTime = v.findViewById(R.id.tvShortTime);
        tvHourTime = v.findViewById(R.id.tvHourTime);
        tvDailyTime = v.findViewById(R.id.tvDailyTime);
        tvMonthlyTime = v.findViewById(R.id.tvMonthlyTime);
        tvWeeklyTime = v.findViewById(R.id.tvWeeklyTime);

        llWeekly = v.findViewById(R.id.llWeekly);
        llMonthly = v.findViewById(R.id.llMonthly);
        llDaily = v.findViewById(R.id.llDaily);
        llHour = v.findViewById(R.id.llHour);
        llshort = v.findViewById(R.id.llshort);


        viewshort = v.findViewById(R.id.viewshort);
        viewhour = v.findViewById(R.id.viewhour);
        viewdaily = v.findViewById(R.id.viewdaily);
        viewweekly = v.findViewById(R.id.viewweekly);
        viewmonth = v.findViewById(R.id.viewmonth);


        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        minlock=0;
        hourlock=0;
        weeklock=0;
        bindData();
    }

    private void bindData() {
        if (OwnerPropertyDetailScreen.parkingSpace != null) {

            if (OwnerPropertyDetailScreen.parkingSpace.stays.size() > 0) {
                stayArrayList.addAll(OwnerPropertyDetailScreen.parkingSpace.stays);
                time.clear();
                for (int i = 0; i < stayArrayList.size(); i++) {
                    if (stayArrayList.get(i).Stay.equalsIgnoreCase("short_time") && stayArrayList.get(i).StayMinutes.equalsIgnoreCase(Constants._15MIN)) {
                        tvShortTime.setText("$" + String.format("%.2f",stayArrayList.get(i).StayPrice));
                        time.add("short_time");
                    } else if (stayArrayList.get(i).Stay.equalsIgnoreCase("short_time") && stayArrayList.get(i).StayMinutes.equalsIgnoreCase(Constants._30MIN)) {
                        minlock=1;
                        String data = tvShortTime.getText().length() > 0 ? tvShortTime.getText() + " - " : "";
                        tvShortTime.setText(data + "$" + String.format("%.2f",stayArrayList.get(i).StayPrice));
                        time.add("short_time");
                    } else if (stayArrayList.get(i).Stay.equalsIgnoreCase("short_time") && stayArrayList.get(i).StayMinutes.equalsIgnoreCase(Constants._45MIN)) {
                        if(minlock==1){
                            if(tvShortTime.getText().length()>0) {
                                String[] splitdata = tvShortTime.getText().toString().split("-");
                                tvShortTime.setText(splitdata[0]);
                            }
                        }
                        String data = tvShortTime.getText().length() > 0 ? tvShortTime.getText() + " - " : "";
                        tvShortTime.setText(data + " $" + String.format("%.2f",stayArrayList.get(i).StayPrice));
                        time.add("short_time");
                    } else if (stayArrayList.get(i).Stay.equalsIgnoreCase("hourly_time") && stayArrayList.get(i).StayMinutes.equalsIgnoreCase(Constants._1_4_HOUR)) {
                        tvHourTime.setText("$" + String.format("%.2f",stayArrayList.get(i).StayPrice));
                        time.add("hourly_time");
                    } else if (stayArrayList.get(i).Stay.equalsIgnoreCase("hourly_time") && stayArrayList.get(i).StayMinutes.equalsIgnoreCase(Constants._4_8_HOUR)) {
                        hourlock=1;
                        String data = tvHourTime.getText().length() > 0 ? tvHourTime.getText() + " - " : "";
                        tvHourTime.setText(data + "$" + String.format("%.2f",stayArrayList.get(i).StayPrice));
                        time.add("hourly_time");
                    } else if (stayArrayList.get(i).Stay.equalsIgnoreCase("hourly_time") && stayArrayList.get(i).StayMinutes.equalsIgnoreCase(Constants._8_PLUS_HOUR)) {
                        if(hourlock==1){
                            if(tvHourTime.getText().length()>0){
                                String[] splitdata = tvHourTime.getText().toString().split("-");
                                tvHourTime.setText(splitdata[0]);
                            }
                        }
                        String data = tvHourTime.getText().length() > 0 ? tvHourTime.getText() + " - " : "";
                        tvHourTime.setText(data + "$" + String.format("%.2f",stayArrayList.get(i).StayPrice));
                        time.add("hourly_time");
                    } else if (stayArrayList.get(i).Stay.equalsIgnoreCase("daily_time") && stayArrayList.get(i).StayMinutes.equalsIgnoreCase(Constants._HALFDAY)) {
                        tvDailyTime.setText("$" + String.format("%.2f",stayArrayList.get(i).StayPrice));
                        time.add("daily_time");
                    } else if (stayArrayList.get(i).Stay.equalsIgnoreCase("daily_time") && stayArrayList.get(i).StayMinutes.equalsIgnoreCase(Constants._FULLDAY)) {
                        String data = tvDailyTime.getText().length() > 0 ? tvDailyTime.getText() + " - " : "";
                        tvDailyTime.setText(data + "$" + String.format("%.2f",stayArrayList.get(i).StayPrice));
                        time.add("daily_time");
                    } else if (stayArrayList.get(i).Stay.equalsIgnoreCase("weekly_time") && stayArrayList.get(i).StayMinutes.equalsIgnoreCase(Constants._3_DAY)) {
                        tvWeeklyTime.setText("$" + String.format("%.2f",stayArrayList.get(i).StayPrice));
                        time.add("weekly_time");
                    } else if (stayArrayList.get(i).Stay.equalsIgnoreCase("weekly_time") && stayArrayList.get(i).StayMinutes.equalsIgnoreCase(Constants._5_DAY)) {
                        weeklock=1;
                        String data = tvWeeklyTime.getText().length() > 0 ? tvWeeklyTime.getText() + " - " : "";
                        tvWeeklyTime.setText(data + "$" + String.format("%.2f",stayArrayList.get(i).StayPrice));
                        time.add("weekly_time");
                    } else if (stayArrayList.get(i).Stay.equalsIgnoreCase("weekly_time") && stayArrayList.get(i).StayMinutes.equalsIgnoreCase(Constants._7_DAY)) {
                        if(weeklock==1){
                            if(tvWeeklyTime.getText().length()>0){
                                String[] splitdata = tvWeeklyTime.getText().toString().split("-");
                                tvWeeklyTime.setText(splitdata[0]);
                            }
                        }
                        String data = tvWeeklyTime.getText().length() > 0 ? tvWeeklyTime.getText() + " - " : "";
                        tvWeeklyTime.setText(data + "$" + String.format("%.2f",stayArrayList.get(i).StayPrice));
                        time.add("weekly_time");
                    } else if (stayArrayList.get(i).Stay.equalsIgnoreCase("monthly_time") && stayArrayList.get(i).StayMinutes.equalsIgnoreCase(Constants._14_DAY)) {
                        tvMonthlyTime.setText("$" + String.format("%.2f",stayArrayList.get(i).StayPrice));
                        time.add("monthly_time");
                    } else if (stayArrayList.get(i).Stay.equalsIgnoreCase("monthly_time") && stayArrayList.get(i).StayMinutes.equalsIgnoreCase(Constants._30_DAY)) {
                        String data = tvMonthlyTime.getText().length() > 0 ? tvMonthlyTime.getText() + " - " : "";
                        tvMonthlyTime.setText(data + "$" + String.format("%.2f",stayArrayList.get(i).StayPrice));
                        time.add("monthly_time");
                    }
                }
                if (time.contains("short_time")) {
                    llshort.setVisibility(View.VISIBLE);
                    viewshort.setVisibility(View.VISIBLE);
                } else {
                    llshort.setVisibility(View.GONE);
                    viewshort.setVisibility(View.GONE);
                }
                if (time.contains("monthly_time")) {
                    llMonthly.setVisibility(View.VISIBLE);
                    viewmonth.setVisibility(View.VISIBLE);
                } else {
                    llMonthly.setVisibility(View.GONE);
                    viewmonth.setVisibility(View.GONE);
                }
                if (time.contains("weekly_time")) {
                    llWeekly.setVisibility(View.VISIBLE);
                    viewweekly.setVisibility(View.VISIBLE);
                } else {
                    llWeekly.setVisibility(View.GONE);
                    viewweekly.setVisibility(View.GONE);
                }
                if (time.contains("daily_time")) {
                    llDaily.setVisibility(View.VISIBLE);
                    viewdaily.setVisibility(View.VISIBLE);
                } else {
                    llDaily.setVisibility(View.GONE);
                    viewdaily.setVisibility(View.GONE);
                }
                if (time.contains("hourly_time")) {
                    llHour.setVisibility(View.VISIBLE);
                    viewhour.setVisibility(View.VISIBLE);
                } else {
                    llHour.setVisibility(View.GONE);
                    viewhour.setVisibility(View.GONE);
                }

            }
        }
    }
}
