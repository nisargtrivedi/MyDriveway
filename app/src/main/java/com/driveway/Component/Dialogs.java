package com.driveway.Component;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.driveway.Utility.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Dialogs {


    public static void openDatePickerDialog(Activity act, final EEditText tv){

        int mYear=0,mMonth=0,mDay=0;

        final Calendar c = Calendar.getInstance();
        if(tv.getText().length()>0){
            SimpleDateFormat df1 = new SimpleDateFormat("dd MMM yyyy");
            Date date1 = null;
            try {
                date1 = df1.parse(tv.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            c.setTime(date1);
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
        }else {
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(act,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        //tv.setText((monthOfYear + 1) + "/" + dayOfMonth+"/"+year);
                        tv.setText(Constants.converDateProfile((monthOfYear + 1) + "/" + dayOfMonth+"/"+year));


                    }
                }, mYear, mMonth, mDay);

        //for set max date selection limit
        datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        datePickerDialog.show();

    }


    public static void openTimePickerDialog(Activity act, final EEditText tv){

        int mYear=0,mMonth=0,mDay=0;

        final Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -18);

        if(tv.getText().length()>0){
            SimpleDateFormat df1 = new SimpleDateFormat("dd MMM yyyy");
            Date date1 = null;
            try {
                date1 = df1.parse(tv.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            c.setTime(date1);
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
        }else {
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(act,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        //tv.setText((monthOfYear + 1) + "/" + dayOfMonth+"/"+year);
                        tv.setText(Constants.converDateProfile((monthOfYear + 1) + "/" + dayOfMonth+"/"+year));


                    }
                }, mYear, mMonth, mDay);

        //for set max date selection limit
        datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        datePickerDialog.show();


    }




}
