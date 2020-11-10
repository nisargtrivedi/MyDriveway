package com.driveway.Component;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.driveway.Activity.ParkerBookingStayScreen;
import com.driveway.R;
import com.driveway.Utility.Constants;

import java.util.Calendar;
import java.util.Date;

import static com.driveway.Activity.ParkerBookingStayScreen.compareTimes;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    public static String t="00:00 AM";
    public static String textviewName="tv";
    public TimePickerFragment(String tv){
        textviewName=tv;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        // Get a Calendar instance
        final Calendar calendar = Calendar.getInstance();
        // Get the current hour and minute

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        /*
            Creates a new time picker dialog with the specified theme.

                TimePickerDialog(Context context, int themeResId,
                    TimePickerDialog.OnTimeSetListener listener,
                    int hourOfDay, int minute, boolean is24HourView)
         */

        // TimePickerDialog Theme : THEME_HOLO_LIGHT
        TimePickerDialog tpd = new TimePickerDialog(getActivity(),
                R.style.MyAppTheme,this,hour,minute,false);



        // Return the TimePickerDialog
        return tpd;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
        // Set a variable to hold the current time AM PM Status
        // Initially we set the variable value to AM
        String status = "AM";

        if(hourOfDay > 11)
        {
            // If the hour is greater than or equal to 12
            // Then the current AM PM status is PM
            status = "PM";
        }

        // Initialize a new variable to hold 12 hour format hour value
        int hour_of_12_hour_format;

        if(hourOfDay > 11){

            // If the hour is greater than or equal to 12
            // Then we subtract 12 from the hour to make it 12 hour format time
            hour_of_12_hour_format = hourOfDay - 12;
        }
        else {
            hour_of_12_hour_format = hourOfDay;
        }


        t=String.format("%02d:%02d",hour_of_12_hour_format,minute)+" "+status;

        String sdate="";
        if(ParkerBookingStayScreen.tvShortEndDate.getText().length()>0){
            sdate=ParkerBookingStayScreen.tvShortEndDate.getText().toString()+" "+t;
        }
        else if(ParkerBookingStayScreen.tvHourEndDate.getText().length()>0){
            sdate=ParkerBookingStayScreen.tvHourEndDate.getText().toString()+" "+t;
        }
        else if(ParkerBookingStayScreen.tvDailyEndDate.getText().length()>0){
            sdate=ParkerBookingStayScreen.tvDailyEndDate.getText().toString()+" "+t;
        }
        else if(ParkerBookingStayScreen.tvWeeklyEndDate.getText().length()>0){
            sdate=ParkerBookingStayScreen.tvWeeklyEndDate.getText().toString()+" "+t;
        }
        else if(ParkerBookingStayScreen.tvMonthlyEndDate.getText().length()>0){
            sdate=ParkerBookingStayScreen.tvMonthlyEndDate.getText().toString()+" "+t;
        }

        String data=compareTimes(DateFormat.format("dd MMM yyyy hh:mm a", new Date().getTime()).toString(),sdate);
        if(data.equalsIgnoreCase("Time is equal Date2")){
            setTime();
        }
        else if(data.equalsIgnoreCase("Time is before Date2")){
            setTime();
        }
        else
            Utility.showAlert(getActivity(),"You cannot select past time");

        // Get the calling activity TextView reference
        // Display the 12 hour format time in app interface


    }

    public void setTime(){
        if(textviewName.equalsIgnoreCase("shorttime")){
            ParkerBookingStayScreen.tvShortTimePick.setText(t);
            if(ParkerBookingStayScreen.tv15.isSelected())
                ParkerBookingStayScreen.tvShortEndTimePick.setText(Constants.addMinTime(t,15));
            if(ParkerBookingStayScreen.tv30.isSelected())
                ParkerBookingStayScreen.tvShortEndTimePick.setText(Constants.addMinTime(t,30));
            if(ParkerBookingStayScreen.tv60.isSelected())
                ParkerBookingStayScreen.tvShortEndTimePick.setText(Constants.addMinTime(t,45));
        }
        if(textviewName.equalsIgnoreCase("hourtime")){
            ParkerBookingStayScreen.tvHourTimePick.setText(t);
            if(ParkerBookingStayScreen.tv1_4.isSelected())
                ParkerBookingStayScreen.textViewHourEndTime.setText(Constants.addMinTime(t,240));
            if(ParkerBookingStayScreen.tv5_8.isSelected())
                ParkerBookingStayScreen.textViewHourEndTime.setText(Constants.addMinTime(t,480));
            if(ParkerBookingStayScreen.tv8.isSelected())
                ParkerBookingStayScreen.textViewHourEndTime.setText(Constants.addMinTime(t,480));
        }
        if(textviewName.equalsIgnoreCase("dailytime")){
            ParkerBookingStayScreen.tvDailyTimePick.setText(t);
            if(ParkerBookingStayScreen.tvHalfDay.isSelected())
                ParkerBookingStayScreen.tvDailyEndTime.setText(Constants.addMinTime(t,360));
            if(ParkerBookingStayScreen.tvFullDay.isSelected())
                ParkerBookingStayScreen.tvDailyEndTime.setText(Constants.addMinTime(t,720));
        }
    }

}
