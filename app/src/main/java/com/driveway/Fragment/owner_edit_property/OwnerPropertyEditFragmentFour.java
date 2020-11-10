package com.driveway.Fragment.owner_edit_property;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.driveway.Activity.OwnerEditProperty.OwnerPropertyEditScreen;
import com.driveway.Activity.OwnerPropertyDetailScreen;
import com.driveway.Component.EEditText;
import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.DBHelper.tblPropertyDetail;
import com.driveway.DBHelper.tblStay;
import com.driveway.R;
import com.driveway.Utility.AppPreferences;
import com.driveway.Utility.Constants;
import com.driveway.Utility.DataContext;
import com.driveway.Utility.Helper;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonElement;
import com.mobandme.ada.Entity;
import com.mobandme.ada.exceptions.AdaFrameworkException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerPropertyEditFragmentFour extends Fragment implements View.OnClickListener {


    DataContext dataContext;
    TTextView ShortStayPrice, tv_three, tv_two, tv_one, HourPrice, tv_Hourone, tv_Hourtwo, tv_Hourthree, DailyPrice, tv_Dailyone, tv_Dailytwo, MonthlyPrice, tv_Monthlyone, tv_Monthlytwo, WeeklyPrice, tv_Weeklythree, tv_Weeklytwo, tv_Weeklyone, btnSave;
    LinearLayout llShortStayPrice, llHourStayPrice, llDailyPrice, llMonthlyPrice, llWeeklyPrice;
    EEditText edtShortStayPrice, edtHourPrice, edtDailyPrice, edtMonthlyPrice, edtWeeklyPrice;
    ArrayList<tblStay> stay = new ArrayList<>();
    AppPreferences appPreferences;

    List<String> dayList = new ArrayList<>();
    List<String> stayList = new ArrayList<>();
    ImageView btn;
    double lat = 0;
    double lng = 0;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataContext = new DataContext(context);
        appPreferences = new AppPreferences(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void openDialog(){
        View view = getLayoutInflater().inflate(R.layout.dialog_price, null);
        ImageView close = view.findViewById(R.id.close);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        dialog.show();
        close.setOnClickListener(view1 -> dialog.dismiss());
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.owner_add_property_fragment_four, container, false);

        ShortStayPrice = v.findViewById(R.id.ShortStayPrice);
        HourPrice = v.findViewById(R.id.HourPrice);
        DailyPrice = v.findViewById(R.id.DailyPrice);
        MonthlyPrice = v.findViewById(R.id.MonthlyPrice);
        WeeklyPrice = v.findViewById(R.id.WeeklyPrice);
        btn=v.findViewById(R.id.btn);


        llShortStayPrice = v.findViewById(R.id.llShortStayPrice);
        tv_three = v.findViewById(R.id.tv_three);
        tv_two = v.findViewById(R.id.tv_two);
        tv_one = v.findViewById(R.id.tv_one);

        llHourStayPrice = v.findViewById(R.id.llHourStayPrice);
        tv_Hourone = v.findViewById(R.id.tv_Hourone);
        tv_Hourtwo = v.findViewById(R.id.tv_Hourtwo);
        tv_Hourthree = v.findViewById(R.id.tv_Hourthree);


        llDailyPrice = v.findViewById(R.id.llDailyPrice);
        tv_Dailyone = v.findViewById(R.id.tv_Dailyone);
        tv_Dailytwo = v.findViewById(R.id.tv_Dailytwo);


        llWeeklyPrice = v.findViewById(R.id.llWeeklyPrice);
        tv_Weeklyone = v.findViewById(R.id.tv_Weeklyone);
        tv_Weeklytwo = v.findViewById(R.id.tv_Weeklytwo);
        tv_Weeklythree = v.findViewById(R.id.tv_Weeklythree);

        llMonthlyPrice = v.findViewById(R.id.llMonthlyPrice);
        tv_Monthlyone = v.findViewById(R.id.tv_Monthlyone);
        tv_Monthlytwo = v.findViewById(R.id.tv_Monthlytwo);

        edtShortStayPrice = v.findViewById(R.id.edtShortStayPrice);
        edtHourPrice = v.findViewById(R.id.edtHourPrice);
        edtDailyPrice = v.findViewById(R.id.edtDailyPrice);
        edtWeeklyPrice = v.findViewById(R.id.edtWeeklyPrice);
        edtMonthlyPrice = v.findViewById(R.id.edtMonthlyPrice);

        btnSave = v.findViewById(R.id.btnSave);


        ShortStayPrice.setOnClickListener(this);
        tv_one.setOnClickListener(this);
        tv_two.setOnClickListener(this);
        tv_three.setOnClickListener(this);


        HourPrice.setOnClickListener(this);
        tv_Hourone.setOnClickListener(this);
        tv_Hourtwo.setOnClickListener(this);
        tv_Hourthree.setOnClickListener(this);

        DailyPrice.setOnClickListener(this);
        tv_Dailyone.setOnClickListener(this);
        tv_Dailytwo.setOnClickListener(this);

        WeeklyPrice.setOnClickListener(this);
        tv_Weeklyone.setOnClickListener(this);
        tv_Weeklytwo.setOnClickListener(this);
        tv_Weeklythree.setOnClickListener(this);


        MonthlyPrice.setOnClickListener(this);
        tv_Monthlytwo.setOnClickListener(this);
        tv_Monthlyone.setOnClickListener(this);

        btnSave.setOnClickListener(this);
        btn.setOnClickListener(this);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        try {
//            dataContext.propertyDetailObjectSet.fill();
//
//        } catch (AdaFrameworkException e) {
//            e.printStackTrace();

//        }
        clearData();
        LatLng getLaLongFromAddress=null;


            if (OwnerPropertyDetailScreen.parkingSpace.ParkingAddress.length() > 0) {
                getLaLongFromAddress = Helper.getLocationFromAddress(getActivity(), OwnerPropertyDetailScreen.parkingSpace.ParkingAddress);
            }


        try {
            if (getLaLongFromAddress != null) {
                lat = getLaLongFromAddress.latitude;
                lng = getLaLongFromAddress.longitude;
            }
        } catch (Exception ex) {

        }


        edtShortStayPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!TextUtils.isEmpty(edtShortStayPrice.getText().toString())) {
                        double price = 0;
                        price = Double.parseDouble(edtShortStayPrice.getText().toString());
                        if (tv_one.isSelected() == true)
                            updateData(Constants.SHORT_STAY, Constants._15MIN, price);
                        else if (tv_two.isSelected() == true)
                            updateData(Constants.SHORT_STAY, Constants._30MIN, price);
                        else if (tv_three.isSelected() == true)
                            updateData(Constants.SHORT_STAY, Constants._45MIN, price);
                    }
                } else {
                    if (!TextUtils.isEmpty(edtShortStayPrice.getText().toString())) {
                        double price = 0;
                        price = Double.parseDouble(edtShortStayPrice.getText().toString());
                        if (price == 0) {
                            edtShortStayPrice.setText("");
                        }
                    }
                }
            }
        });
        edtShortStayPrice.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!TextUtils.isEmpty(edtShortStayPrice.getText().toString())) {
                        double price = 0;
                        price = Double.parseDouble(edtShortStayPrice.getText().toString());
                        if (tv_one.isSelected() == true)
                            updateData(Constants.SHORT_STAY, Constants._15MIN, price);
                        else if (tv_two.isSelected() == true)
                            updateData(Constants.SHORT_STAY, Constants._30MIN, price);
                        else if (tv_three.isSelected() == true)
                            updateData(Constants.SHORT_STAY, Constants._45MIN, price);
                    }
                }
                return false;
            }
        });

        edtHourPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!TextUtils.isEmpty(edtHourPrice.getText().toString())) {
                        double price = 0;
                        price = Double.parseDouble(edtHourPrice.getText().toString());
                        if (tv_Hourone.isSelected())
                            updateData(Constants.HOUR_STAY, Constants._1_4_HOUR, price);
                        else if (tv_Hourtwo.isSelected())
                            updateData(Constants.HOUR_STAY, Constants._4_8_HOUR, price);
                        else if (tv_Hourthree.isSelected())
                            updateData(Constants.HOUR_STAY, Constants._8_PLUS_HOUR, price);
                    }
                } else {
                    if (!TextUtils.isEmpty(edtHourPrice.getText().toString())) {
                        double price = 0;
                        price = Double.parseDouble(edtHourPrice.getText().toString());
                        if (price == 0) {
                            edtHourPrice.setText("");
                        }
                    }
                }
            }
        });
        edtHourPrice.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!TextUtils.isEmpty(edtHourPrice.getText().toString())) {
                        double price = 0;
                        price = Double.parseDouble(edtHourPrice.getText().toString());
                        if (tv_Hourone.isSelected())
                            updateData(Constants.HOUR_STAY, Constants._1_4_HOUR, price);
                        else if (tv_Hourtwo.isSelected())
                            updateData(Constants.HOUR_STAY, Constants._4_8_HOUR, price);
                        else if (tv_Hourthree.isSelected())
                            updateData(Constants.HOUR_STAY, Constants._8_PLUS_HOUR, price);
                    }
                }
                return false;
            }
        });

        edtDailyPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!TextUtils.isEmpty(edtDailyPrice.getText().toString())) {
                        double price = 0;
                        price = Double.parseDouble(edtDailyPrice.getText().toString());
                        if (tv_Dailyone.isSelected())
                            updateData(Constants.DAY_STAY, Constants._HALFDAY, price);
                        else if (tv_Dailytwo.isSelected())
                            updateData(Constants.DAY_STAY, Constants._FULLDAY, price);
                    }
                } else {
                    if (!TextUtils.isEmpty(edtDailyPrice.getText().toString())) {
                        double price = 0;
                        price = Double.parseDouble(edtDailyPrice.getText().toString());
                        if (price == 0) {
                            edtDailyPrice.setText("");
                        }
                    }
                }
            }
        });
        edtDailyPrice.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!TextUtils.isEmpty(edtDailyPrice.getText().toString())) {
                        double price = 0;
                        price = Double.parseDouble(edtDailyPrice.getText().toString());
                        if (tv_Dailyone.isSelected())
                            updateData(Constants.DAY_STAY, Constants._HALFDAY, price);
                        else if (tv_Dailytwo.isSelected())
                            updateData(Constants.DAY_STAY, Constants._FULLDAY, price);
                    }
                }
                return false;
            }
        });

        edtWeeklyPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!TextUtils.isEmpty(edtWeeklyPrice.getText().toString())) {
                        double price = 0;
                        price = Double.parseDouble(edtWeeklyPrice.getText().toString());
                        if (tv_Weeklyone.isSelected())
                            updateData(Constants.WEEK_STAY, Constants._3_DAY, price);
                        else if (tv_Weeklytwo.isSelected())
                            updateData(Constants.WEEK_STAY, Constants._5_DAY, price);
                        else if (tv_Weeklythree.isSelected())
                            updateData(Constants.WEEK_STAY, Constants._7_DAY, price);
                    }
                } else {
                    if (!TextUtils.isEmpty(edtWeeklyPrice.getText().toString())) {
                        double price = 0;
                        price = Double.parseDouble(edtWeeklyPrice.getText().toString());
                        if (price == 0) {
                            edtWeeklyPrice.setText("");
                        }
                    }
                }
            }
        });
        edtWeeklyPrice.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!TextUtils.isEmpty(edtWeeklyPrice.getText().toString())) {
                        double price = 0;
                        price = Double.parseDouble(edtWeeklyPrice.getText().toString());
                        if (tv_Weeklyone.isSelected())
                            updateData(Constants.WEEK_STAY, Constants._3_DAY, price);
                        else if (tv_Weeklytwo.isSelected())
                            updateData(Constants.WEEK_STAY, Constants._5_DAY, price);
                        else if (tv_Weeklythree.isSelected())
                            updateData(Constants.WEEK_STAY, Constants._7_DAY, price);
                    }
                }
                return false;
            }
        });

        edtMonthlyPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!TextUtils.isEmpty(edtMonthlyPrice.getText().toString())) {
                        double price = 0;
                        price = Double.parseDouble(edtMonthlyPrice.getText().toString());
                        if (tv_Monthlyone.isSelected())
                            updateData(Constants.MONTH_STAY, Constants._14_DAY, price);
                        else if (tv_Monthlytwo.isSelected())
                            updateData(Constants.MONTH_STAY, Constants._30_DAY, price);
                    }
                } else {
                    if (!TextUtils.isEmpty(edtMonthlyPrice.getText().toString())) {
                        double price = 0;
                        price = Double.parseDouble(edtMonthlyPrice.getText().toString());
                        if (price == 0) {
                            edtMonthlyPrice.setText("");
                        }
                    }
                }
            }
        });
        edtMonthlyPrice.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!TextUtils.isEmpty(edtMonthlyPrice.getText().toString())) {
                        double price = 0;
                        price = Double.parseDouble(edtMonthlyPrice.getText().toString());
                        if (tv_Monthlyone.isSelected())
                            updateData(Constants.MONTH_STAY, Constants._14_DAY, price);
                        else if (tv_Monthlytwo.isSelected())
                            updateData(Constants.MONTH_STAY, Constants._30_DAY, price);
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onStart() {


        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void showFilledData(){
        try {

            ArrayList<tblStay> stays = OwnerPropertyDetailScreen.parkingSpace.stays != null ? OwnerPropertyDetailScreen.parkingSpace.stays : null;

            for(tblStay stay:stays){

                System.out.println("STAY = "+stay.Stay);
                System.out.println("MINTES = "+stay.StayMinutes);
                System.out.println("PRICE = "+stay.StayPrice);

                if(stay.Stay.equalsIgnoreCase(Constants.SHORT_STAY)){
                    if(llShortStayPrice.getVisibility() == View.GONE)
                    showShortStay();

                        if(stay.StayMinutes.equalsIgnoreCase(Constants._15MIN)){

                            tv_one.setSelected(true);
                            tv_two.setSelected(false);
                            tv_three.setSelected(false);
                            edtShortStayPrice.setEnabled(true);
                            double p = filerData(Constants._15MIN);
                            edtShortStayPrice.setText(String.format("%.0f",p) + "");
                            if (p == 0) {
                                edtShortStayPrice.setText("");
                            }
                            showTextViewSelection(tv_one, tv_two, tv_three);

                        }else if(stay.StayMinutes.equalsIgnoreCase(Constants._30MIN)){


                            if(tv_one.isSelected()==false) {
                                tv_one.setSelected(false);
                                tv_two.setSelected(true);
                                tv_three.setSelected(false);
                                edtShortStayPrice.setEnabled(true);
                                double p = filerData(Constants._30MIN);
                                edtShortStayPrice.setText(String.format("%.0f", p) + "");
                                if (p == 0) {
                                    edtShortStayPrice.setText("");
                                }
                                showTextViewSelection(tv_two, tv_one, tv_three);
                            }

                        }else if(stay.StayMinutes.equalsIgnoreCase(Constants._45MIN)){

                            if(tv_one.isSelected()==false && tv_two.isSelected()==false) {
                                tv_one.setSelected(false);
                                tv_two.setSelected(false);
                                tv_three.setSelected(true);
                                edtShortStayPrice.setEnabled(true);
                                double p = filerData(Constants._45MIN);
                                edtShortStayPrice.setText(String.format("%.0f", p) + "");
                                if (p == 0) {
                                    edtShortStayPrice.setText("");
                                }
                                showTextViewSelection(tv_three,tv_two, tv_one);
                            }
                        }
                }
                else if(stay.Stay.equalsIgnoreCase(Constants.HOUR_STAY)){
                    if(llHourStayPrice.getVisibility() == View.GONE)
                    showHourStay();

                    if(stay.StayMinutes.equalsIgnoreCase(Constants._1_4_HOUR))
                    {

                        tv_Hourone.setSelected(true);
                        tv_Hourtwo.setSelected(false);
                        tv_Hourthree.setSelected(false);
                        edtHourPrice.setEnabled(true);
                        double p1 = filerData(Constants._1_4_HOUR);
                        edtHourPrice.setText(String.format("%.0f",p1) + "");
                        if (p1 == 0) {
                            edtHourPrice.setText("");
                        }
                        showTextViewSelection(tv_Hourone, tv_Hourtwo, tv_Hourthree);

                    }
                    else if(stay.StayMinutes.equalsIgnoreCase(Constants._4_8_HOUR)){

                        if(tv_Hourone.isSelected()==false){
                            tv_Hourone.setSelected(false);
                            tv_Hourtwo.setSelected(true);
                            tv_Hourthree.setSelected(false);
                            edtHourPrice.setEnabled(true);
                            double p1 = filerData(Constants._4_8_HOUR);
                            edtHourPrice.setText(String.format("%.0f",p1) + "");
                            if (p1 == 0) {
                                edtHourPrice.setText("");
                            }
                            showTextViewSelection(tv_Hourtwo,tv_Hourone, tv_Hourthree);
                        }

                    }else if(stay.StayMinutes.equalsIgnoreCase(Constants._8_PLUS_HOUR)){

                        if(tv_Hourone.isSelected()==false && tv_Hourtwo.isSelected()==false){
                            tv_Hourone.setSelected(false);
                            tv_Hourtwo.setSelected(false);
                            tv_Hourthree.setSelected(true);
                            edtHourPrice.setEnabled(true);
                            double p1 = filerData(Constants._8_PLUS_HOUR);
                            edtHourPrice.setText(String.format("%.0f",p1) + "");
                            if (p1 == 0) {
                                edtHourPrice.setText("");
                            }
                            showTextViewSelection(tv_Hourthree,tv_Hourtwo,tv_Hourone);
                        }
                    }

                }
                else if(stay.Stay.equalsIgnoreCase(Constants.DAY_STAY)){
                    if(llDailyPrice.getVisibility() == View.GONE)
                        showDailyStay();

                    if(stay.StayMinutes.equalsIgnoreCase(Constants._HALFDAY)){

                        tv_Dailyone.setSelected(true);
                        tv_Dailytwo.setSelected(false);
                        edtDailyPrice.setEnabled(true);
                        double half = filerData(Constants._HALFDAY);
                        edtDailyPrice.setText(String.format("%.0f",half) + "");
                        if (half == 0) {
                            edtDailyPrice.setText("");
                        }
                        showTextViewSelection(tv_Dailyone, tv_Dailytwo, null);

                    }else if(stay.StayMinutes.equalsIgnoreCase(Constants._FULLDAY)){

                        if(tv_Dailyone.isSelected()==false) {
                            tv_Dailyone.setSelected(false);
                            tv_Dailytwo.setSelected(true);
                            edtDailyPrice.setEnabled(true);
                            double full = filerData(Constants._FULLDAY);
                            edtDailyPrice.setText(String.format("%.0f", full) + "");
                            if (full == 0) {
                                edtDailyPrice.setText("");
                            }
                            showTextViewSelection(tv_Dailytwo,tv_Dailyone, null);
                        }
                    }

                }
                else if(stay.Stay.equalsIgnoreCase(Constants.WEEK_STAY)){
                    if(llWeeklyPrice.getVisibility() == View.GONE)
                        showWeeklyStay();

                    if(stay.StayMinutes.equalsIgnoreCase(Constants._3_DAY)){

                        tv_Weeklyone.setSelected(true);
                        tv_Weeklytwo.setSelected(false);
                        tv_Weeklythree.setSelected(false);
                        edtWeeklyPrice.setEnabled(true);
                        double days = filerData(Constants._3_DAY);
                        edtWeeklyPrice.setText(String.format("%.0f",days) + "");
                        if (days == 0) {
                            edtWeeklyPrice.setText("");
                        }
                        showTextViewSelection(tv_Weeklyone, tv_Weeklytwo, tv_Weeklythree);
                    }
                    else if(stay.StayMinutes.equalsIgnoreCase(Constants._5_DAY)){

                        if(tv_Weeklyone.isSelected()==false){
                            tv_Weeklyone.setSelected(false);
                            tv_Weeklytwo.setSelected(true);
                            tv_Weeklythree.setSelected(false);
                            edtWeeklyPrice.setEnabled(true);
                            double days = filerData(Constants._5_DAY);
                            edtWeeklyPrice.setText(String.format("%.0f",days) + "");
                            if (days == 0) {
                                edtWeeklyPrice.setText("");
                            }
                            showTextViewSelection(tv_Weeklytwo,tv_Weeklyone, tv_Weeklythree);
                        }
                    }else if(stay.StayMinutes.equalsIgnoreCase(Constants._7_DAY)){
                        if(tv_Weeklyone.isSelected()==false && tv_Weeklytwo.isSelected()==false){
                            tv_Weeklyone.setSelected(false);
                            tv_Weeklytwo.setSelected(false);
                            tv_Weeklythree.setSelected(true);
                            edtWeeklyPrice.setEnabled(true);
                            double days = filerData(Constants._7_DAY);
                            edtWeeklyPrice.setText(String.format("%.0f",days) + "");
                            if (days == 0) {
                                edtWeeklyPrice.setText("");
                            }
                            showTextViewSelection(tv_Weeklythree,tv_Weeklyone, tv_Weeklytwo);
                        }
                    }
                }
               else if(stay.Stay.equalsIgnoreCase(Constants.MONTH_STAY)){
                    if(llMonthlyPrice.getVisibility() == View.GONE)
                        showMonthlyStay();
                    if(stay.StayMinutes.equalsIgnoreCase(Constants._14_DAY)){
                        tv_Monthlyone.setSelected(true);
                        tv_Monthlytwo.setSelected(false);
                        edtMonthlyPrice.setEnabled(true);
                        double days14 = filerData(Constants._14_DAY);
                        edtMonthlyPrice.setText(String.format("%.0f",days14) + "");
                        if (days14 == 0) {
                            edtMonthlyPrice.setText("");
                        }
                        showTextViewSelection(tv_Monthlyone, tv_Monthlytwo, null);
                    }else if(stay.StayMinutes.equalsIgnoreCase(Constants._30_DAY)){

                        if(tv_Monthlyone.isSelected()==false){
                            tv_Monthlyone.setSelected(false);
                            tv_Monthlytwo.setSelected(true);
                            edtMonthlyPrice.setEnabled(true);
                            double days14 = filerData(Constants._30_DAY);
                            edtMonthlyPrice.setText(String.format("%.0f",days14) + "");
                            if (days14 == 0) {
                                edtMonthlyPrice.setText("");
                            }
                            showTextViewSelection(tv_Monthlytwo,tv_Monthlyone, null);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error Mesage====>"+e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:openDialog();break;
            case R.id.ShortStayPrice:
                showShortStay();
                edtShortStayPrice.setEnabled(false);
                break;
            case R.id.tv_one:
                edtShortStayPrice.requestFocus();
                if (!TextUtils.isEmpty(edtShortStayPrice.getText().toString())) {
                    double price = 0;
                    price = Double.parseDouble(edtShortStayPrice.getText().toString());
                    if (tv_one.isSelected() == true)
                        updateData(Constants.SHORT_STAY, Constants._15MIN, price);
                    else if (tv_two.isSelected() == true)
                        updateData(Constants.SHORT_STAY, Constants._30MIN, price);
                    else if (tv_three.isSelected() == true)
                        updateData(Constants.SHORT_STAY, Constants._45MIN, price);
                }
                tv_one.setSelected(true);
                tv_two.setSelected(false);
                tv_three.setSelected(false);
                edtShortStayPrice.setEnabled(true);
                double p = filerData(Constants._15MIN);
                edtShortStayPrice.setText(String.format("%.0f",p) + "");
                if (p == 0) {
                    edtShortStayPrice.setText("");
                }
                showTextViewSelection(tv_one, tv_two, tv_three);
                break;
            case R.id.tv_two:
                edtShortStayPrice.requestFocus();
                if (!TextUtils.isEmpty(edtShortStayPrice.getText().toString())) {
                    double price = 0;
                    price = Double.parseDouble(edtShortStayPrice.getText().toString());
                    if (tv_one.isSelected() == true)
                        updateData(Constants.SHORT_STAY, Constants._15MIN, price);
                    else if (tv_two.isSelected() == true)
                        updateData(Constants.SHORT_STAY, Constants._30MIN, price);
                    else if (tv_three.isSelected() == true)
                        updateData(Constants.SHORT_STAY, Constants._45MIN, price);
                }
                tv_one.setSelected(false);
                tv_two.setSelected(true);
                tv_three.setSelected(false);
                edtShortStayPrice.setEnabled(true);
                double p30 = filerData(Constants._30MIN);
                edtShortStayPrice.setText(String.format("%.0f",p30) + "");
                if (p30 == 0) {
                    edtShortStayPrice.setText("");
                }
                showTextViewSelection(tv_two, tv_one, tv_three);
                break;

            case R.id.tv_three:
                edtShortStayPrice.requestFocus();
                if (!TextUtils.isEmpty(edtShortStayPrice.getText().toString())) {
                    double price = 0;
                    price = Double.parseDouble(edtShortStayPrice.getText().toString());
                    if (tv_one.isSelected() == true)
                        updateData(Constants.SHORT_STAY, Constants._15MIN, price);
                    else if (tv_two.isSelected() == true)
                        updateData(Constants.SHORT_STAY, Constants._30MIN, price);
                    else if (tv_three.isSelected() == true)
                        updateData(Constants.SHORT_STAY, Constants._45MIN, price);
                }
                tv_one.setSelected(false);
                tv_two.setSelected(false);
                tv_three.setSelected(true);
                edtShortStayPrice.setEnabled(true);
                double p45 = filerData(Constants._45MIN);
                edtShortStayPrice.setText(String.format("%.0f",p45) + "");
                if (p45 == 0) {
                    edtShortStayPrice.setText("");
                }
                showTextViewSelection(tv_three, tv_two, tv_one);
                break;

            case R.id.HourPrice:
                showHourStay();
                edtHourPrice.setEnabled(false);
                break;

            case R.id.tv_Hourone:
                edtHourPrice.requestFocus();
                if (!TextUtils.isEmpty(edtHourPrice.getText().toString())) {
                    double price = 0;
                    price = Double.parseDouble(edtHourPrice.getText().toString());
                    if (tv_Hourone.isSelected())
                        updateData(Constants.HOUR_STAY, Constants._1_4_HOUR, price);
                    else if (tv_Hourtwo.isSelected())
                        updateData(Constants.HOUR_STAY, Constants._4_8_HOUR, price);
                    else if (tv_Hourthree.isSelected())
                        updateData(Constants.HOUR_STAY, Constants._8_PLUS_HOUR, price);
                }
                tv_Hourone.setSelected(true);
                tv_Hourtwo.setSelected(false);
                tv_Hourthree.setSelected(false);
                edtHourPrice.setEnabled(true);
                double p1 = filerData(Constants._1_4_HOUR);
                edtHourPrice.setText(String.format("%.0f",p1) + "");
                if (p1 == 0) {
                    edtHourPrice.setText("");
                }
                showTextViewSelection(tv_Hourone, tv_Hourtwo, tv_Hourthree);
                break;

            case R.id.tv_Hourtwo:
                edtHourPrice.requestFocus();
                if (!TextUtils.isEmpty(edtHourPrice.getText().toString())) {
                    double price = 0;
                    price = Double.parseDouble(edtHourPrice.getText().toString());
                    if (tv_Hourone.isSelected())
                        updateData(Constants.HOUR_STAY, Constants._1_4_HOUR, price);
                    else if (tv_Hourtwo.isSelected())
                        updateData(Constants.HOUR_STAY, Constants._4_8_HOUR, price);
                    else if (tv_Hourthree.isSelected())
                        updateData(Constants.HOUR_STAY, Constants._8_PLUS_HOUR, price);
                }
                tv_Hourone.setSelected(false);
                tv_Hourtwo.setSelected(true);
                tv_Hourthree.setSelected(false);
                edtHourPrice.setEnabled(true);
                double p_48 = filerData(Constants._4_8_HOUR);
                edtHourPrice.setText(String.format("%.0f",p_48) + "");
                if (p_48 == 0) {
                    edtHourPrice.setText("");
                }
                showTextViewSelection(tv_Hourtwo, tv_Hourone, tv_Hourthree);
                break;

            case R.id.tv_Hourthree:
                edtHourPrice.requestFocus();
                if (!TextUtils.isEmpty(edtHourPrice.getText().toString())) {
                    double price = 0;
                    price = Double.parseDouble(edtHourPrice.getText().toString());
                    if (tv_Hourone.isSelected())
                        updateData(Constants.HOUR_STAY, Constants._1_4_HOUR, price);
                    else if (tv_Hourtwo.isSelected())
                        updateData(Constants.HOUR_STAY, Constants._4_8_HOUR, price);
                    else if (tv_Hourthree.isSelected())
                        updateData(Constants.HOUR_STAY, Constants._8_PLUS_HOUR, price);
                }
                tv_Hourone.setSelected(false);
                tv_Hourtwo.setSelected(false);
                tv_Hourthree.setSelected(true);
                edtHourPrice.setEnabled(true);
                double p_8 = filerData(Constants._8_PLUS_HOUR);
                edtHourPrice.setText(String.format("%.0f",p_8) + "");
                if (p_8 == 0) {
                    edtHourPrice.setText("");
                }
                showTextViewSelection(tv_Hourthree, tv_Hourtwo, tv_Hourone);
                break;


            case R.id.DailyPrice:
                showDailyStay();
                edtDailyPrice.setEnabled(false);
                break;
            case R.id.tv_Dailyone:
                edtDailyPrice.requestFocus();
                if (!TextUtils.isEmpty(edtDailyPrice.getText().toString())) {
                    double price = 0;
                    price = Double.parseDouble(edtDailyPrice.getText().toString());
                    if (tv_Dailyone.isSelected())
                        updateData(Constants.DAY_STAY, Constants._HALFDAY, price);
                    else if (tv_Dailytwo.isSelected())
                        updateData(Constants.DAY_STAY, Constants._FULLDAY, price);
                }
                tv_Dailyone.setSelected(true);
                tv_Dailytwo.setSelected(false);
                edtDailyPrice.setEnabled(true);
                double half = filerData(Constants._HALFDAY);
                edtDailyPrice.setText(String.format("%.0f",half) + "");
                if (half == 0) {
                    edtDailyPrice.setText("");
                }
                showTextViewSelection(tv_Dailyone, tv_Dailytwo, null);
                break;
            case R.id.tv_Dailytwo:
                edtDailyPrice.requestFocus();
                if (!TextUtils.isEmpty(edtDailyPrice.getText().toString())) {
                    double price = 0;
                    price = Double.parseDouble(edtDailyPrice.getText().toString());
                    if (tv_Dailyone.isSelected())
                        updateData(Constants.DAY_STAY, Constants._HALFDAY, price);
                    else if (tv_Dailytwo.isSelected())
                        updateData(Constants.DAY_STAY, Constants._FULLDAY, price);
                }
                tv_Dailyone.setSelected(false);
                tv_Dailytwo.setSelected(true);
                edtDailyPrice.setEnabled(true);
                double full = filerData(Constants._FULLDAY);
                edtDailyPrice.setText(String.format("%.0f",full) + "");
                if (full == 0) {
                    edtDailyPrice.setText("");
                }
                showTextViewSelection(tv_Dailytwo, tv_Dailyone, null);
                break;


            case R.id.WeeklyPrice:
                showWeeklyStay();
                edtWeeklyPrice.setEnabled(false);
                break;
            case R.id.tv_Weeklyone:
                edtWeeklyPrice.requestFocus();
                if (!TextUtils.isEmpty(edtWeeklyPrice.getText().toString())) {
                    double price = 0;
                    price = Double.parseDouble(edtWeeklyPrice.getText().toString());
                    if (tv_Weeklyone.isSelected())
                        updateData(Constants.WEEK_STAY, Constants._3_DAY, price);
                    else if (tv_Weeklytwo.isSelected())
                        updateData(Constants.WEEK_STAY, Constants._5_DAY, price);
                    else if (tv_Weeklythree.isSelected())
                        updateData(Constants.WEEK_STAY, Constants._7_DAY, price);
                }
                tv_Weeklyone.setSelected(true);
                tv_Weeklytwo.setSelected(false);
                tv_Weeklythree.setSelected(false);
                edtWeeklyPrice.setEnabled(true);
                double days = filerData(Constants._3_DAY);
                edtWeeklyPrice.setText(String.format("%.0f",days) + "");
                if (days == 0) {
                    edtWeeklyPrice.setText("");
                }
                showTextViewSelection(tv_Weeklyone, tv_Weeklytwo, tv_Weeklythree);
                break;

            case R.id.tv_Weeklytwo:
                edtWeeklyPrice.requestFocus();
                if (!TextUtils.isEmpty(edtWeeklyPrice.getText().toString())) {
                    double price = 0;
                    price = Double.parseDouble(edtWeeklyPrice.getText().toString());
                    if (tv_Weeklyone.isSelected())
                        updateData(Constants.WEEK_STAY, Constants._3_DAY, price);
                    else if (tv_Weeklytwo.isSelected())
                        updateData(Constants.WEEK_STAY, Constants._5_DAY, price);
                    else if (tv_Weeklythree.isSelected())
                        updateData(Constants.WEEK_STAY, Constants._7_DAY, price);
                }
                tv_Weeklyone.setSelected(false);
                tv_Weeklytwo.setSelected(true);
                tv_Weeklythree.setSelected(false);
                edtWeeklyPrice.setEnabled(true);
                double days5 = filerData(Constants._5_DAY);
                edtWeeklyPrice.setText(String.format("%.0f",days5) + "");
                if (days5 == 0) {
                    edtWeeklyPrice.setText("");
                }
                showTextViewSelection(tv_Weeklytwo, tv_Weeklyone, tv_Weeklythree);
                break;

            case R.id.tv_Weeklythree:
                edtWeeklyPrice.requestFocus();
                if (!TextUtils.isEmpty(edtWeeklyPrice.getText().toString())) {
                    double price = 0;
                    price = Double.parseDouble(edtWeeklyPrice.getText().toString());
                    if (tv_Weeklyone.isSelected())
                        updateData(Constants.WEEK_STAY, Constants._3_DAY, price);
                    else if (tv_Weeklytwo.isSelected())
                        updateData(Constants.WEEK_STAY, Constants._5_DAY, price);
                    else if (tv_Weeklythree.isSelected())
                        updateData(Constants.WEEK_STAY, Constants._7_DAY, price);
                }
                tv_Weeklyone.setSelected(false);
                tv_Weeklytwo.setSelected(false);
                tv_Weeklythree.setSelected(true);
                edtWeeklyPrice.setEnabled(true);
                double days7 = filerData(Constants._7_DAY);
                edtWeeklyPrice.setText(String.format("%.0f",days7) + "");
                if (days7 == 0) {
                    edtWeeklyPrice.setText("");
                }
                showTextViewSelection(tv_Weeklythree, tv_Weeklytwo, tv_Weeklyone);
                break;

            case R.id.MonthlyPrice:
                showMonthlyStay();
                edtMonthlyPrice.setEnabled(false);
                break;

            case R.id.tv_Monthlyone:
                edtMonthlyPrice.requestFocus();
                if (!TextUtils.isEmpty(edtMonthlyPrice.getText().toString())) {
                    double price = 0;
                    price = Double.parseDouble(edtMonthlyPrice.getText().toString());
                    if (tv_Monthlyone.isSelected())
                        updateData(Constants.MONTH_STAY, Constants._14_DAY, price);
                    else if (tv_Monthlytwo.isSelected())
                        updateData(Constants.MONTH_STAY, Constants._30_DAY, price);
                }
                tv_Monthlyone.setSelected(true);
                tv_Monthlytwo.setSelected(false);
                edtMonthlyPrice.setEnabled(true);
                double days14 = filerData(Constants._14_DAY);
                edtMonthlyPrice.setText(String.format("%.0f",days14) + "");
                if (days14 == 0) {
                    edtMonthlyPrice.setText("");
                }
                showTextViewSelection(tv_Monthlyone, tv_Monthlytwo, null);
                break;
            case R.id.tv_Monthlytwo:
                edtMonthlyPrice.requestFocus();
                if (!TextUtils.isEmpty(edtMonthlyPrice.getText().toString())) {
                    double price = 0;
                    price = Double.parseDouble(edtMonthlyPrice.getText().toString());
                    if (tv_Monthlyone.isSelected())
                        updateData(Constants.MONTH_STAY, Constants._14_DAY, price);
                    else if (tv_Monthlytwo.isSelected())
                        updateData(Constants.MONTH_STAY, Constants._30_DAY, price);
                }
                tv_Monthlyone.setSelected(false);
                tv_Monthlytwo.setSelected(true);
                edtMonthlyPrice.setEnabled(true);
                double days30 = filerData(Constants._30_DAY);
                edtMonthlyPrice.setText(String.format("%.0f",days30) + "");
                if (days30 == 0) {
                    edtMonthlyPrice.setText("");
                }
                showTextViewSelection(tv_Monthlytwo, tv_Monthlyone, null);
                break;

            case R.id.btnSave:
                saveData();
                break;
        }
    }

    private void showShortStay() {
        if (llShortStayPrice.getVisibility() == View.VISIBLE) {
            llShortStayPrice.setVisibility(View.GONE);
            ShortStayPrice.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three_watch);
            ShortStayPrice.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.drawable_arrow_down, 0);
        } else {
            llShortStayPrice.setVisibility(View.VISIBLE);
            ShortStayPrice.setBackgroundResource(android.R.color.transparent);
            ShortStayPrice.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.drawable_arrow_up, 0);
        }
    }

    private void showHourStay() {
        if (llHourStayPrice.getVisibility() == View.VISIBLE) {
            llHourStayPrice.setVisibility(View.GONE);
            HourPrice.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three_watch);
            HourPrice.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.drawable_arrow_down, 0);
        } else {
            llHourStayPrice.setVisibility(View.VISIBLE);
            HourPrice.setBackgroundResource(android.R.color.transparent);
            HourPrice.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.drawable_arrow_up, 0);
        }
    }

    private void showDailyStay() {
        if (llDailyPrice.getVisibility() == View.VISIBLE) {
            llDailyPrice.setVisibility(View.GONE);
            DailyPrice.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three_watch);
            DailyPrice.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.drawable_arrow_down, 0);
        } else {
            llDailyPrice.setVisibility(View.VISIBLE);
            DailyPrice.setBackgroundResource(android.R.color.transparent);
            DailyPrice.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.drawable_arrow_up, 0);
        }
    }

    private void showWeeklyStay() {
        if (llWeeklyPrice.getVisibility() == View.VISIBLE) {
            llWeeklyPrice.setVisibility(View.GONE);
            WeeklyPrice.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three_watch);
            WeeklyPrice.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.drawable_arrow_down, 0);
        } else {
            llWeeklyPrice.setVisibility(View.VISIBLE);
            WeeklyPrice.setBackgroundResource(android.R.color.transparent);
            WeeklyPrice.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.drawable_arrow_up, 0);
        }
    }

    private void showMonthlyStay() {
        if (llMonthlyPrice.getVisibility() == View.VISIBLE) {
            llMonthlyPrice.setVisibility(View.GONE);
            MonthlyPrice.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three_watch);
            MonthlyPrice.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.drawable_arrow_down, 0);
        } else {
            llMonthlyPrice.setVisibility(View.VISIBLE);
            MonthlyPrice.setBackgroundResource(android.R.color.transparent);
            MonthlyPrice.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.drawable_arrow_up, 0);
        }
    }

    private void showTextViewSelection(TTextView tv1, TTextView t2, TTextView t3) {

        tv1.setBackgroundResource(R.drawable.border_button_with_fill);
        tv1.setTextColor(getResources().getColor(R.color.white));
        t2.setBackgroundResource(android.R.color.transparent);
        t2.setTextColor(getResources().getColor(R.color.owner_fragment_four_font_color));
        if (t3 != null) {
            t3.setBackgroundResource(android.R.color.transparent);
            t3.setTextColor(getResources().getColor(R.color.owner_fragment_four_font_color));
        }

    }


    private void addData() {
        try {
            ArrayList<tblStay> stays = OwnerPropertyDetailScreen.parkingSpace.stays != null ? OwnerPropertyDetailScreen.parkingSpace.stays : null;
            if (stays != null) {
                for (tblStay stay : stays) {
                    updateData(stay.Stay, stay.StayMinutes, stay.StayPrice);
                }
            }
            showFilledData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void clearData() {
        try {
            dataContext.shortStayObjectSet.fill();
            if (dataContext.shortStayObjectSet.size() > 0) {
                for (int i = 0; i < dataContext.shortStayObjectSet.size(); i++)
                    dataContext.shortStayObjectSet.remove(i).setStatus(Entity.STATUS_DELETED);
                dataContext.shortStayObjectSet.save();
            }
            addData();

        } catch (AdaFrameworkException e) {
            e.printStackTrace();
        }
    }

    private double filerData(String stayMinutes) {
        double price = 0;
        try {
            dataContext.shortStayObjectSet.fill("stay_minutes=?", new String[]{stayMinutes}, null);
            if (dataContext.shortStayObjectSet.size() > 0) {
                price = dataContext.shortStayObjectSet.get(0).StayPrice;
            }

        } catch (AdaFrameworkException e) {
            e.printStackTrace();
        }
        return price;
    }

    private void updateData(String stay, String stayMinutes, double price) {
        try {
            dataContext.shortStayObjectSet.fill("stay_minutes=?", new String[]{stayMinutes}, null);
            if (dataContext.shortStayObjectSet.size() > 0) {
                tblStay obj = dataContext.shortStayObjectSet.get(0);
                if (obj != null) {
                    obj.StayMinutes = stayMinutes;
                    obj.StayPrice = price;
                    obj.setStatus(Entity.STATUS_UPDATED);
                    dataContext.shortStayObjectSet.save(obj);
                }
            } else {
                tblStay obj = new tblStay();
                obj.ParkingID = OwnerPropertyDetailScreen.parkingSpace.ParkingID;
                obj.Stay = stay;
                obj.StayMinutes = stayMinutes;
                obj.StayPrice = price;
                obj.setStatus(Entity.STATUS_NEW);
                dataContext.shortStayObjectSet.save(obj);
            }

        } catch (AdaFrameworkException e) {
            e.printStackTrace();
        }
    }

    private void saveData() {
        try {
            dataContext.propertyAvailableTimesObjectSet.fill();
            dataContext.shortStayObjectSet.fill();

            if (OwnerPropertyDetailScreen.parkingSpace==null) {
                Utility.showAlert(getActivity(), "Enter Property Details");
            }else if (OwnerPropertyDetailScreen.list.size()<= 0) {
                Utility.showAlert(getActivity(), "Enter Property Image");
            }else if (dataContext.propertyAvailableTimesObjectSet.size() <= 0) {
                Utility.showAlert(getActivity(), "Enter Property Availability Detail");
            }else if (dataContext.shortStayObjectSet.size() <= 0) {
                Utility.showAlert(getActivity(), "Enter Property Rates");
            } else {
                inputDetail();
            }
        } catch (AdaFrameworkException e) {
            e.printStackTrace();
        }


    }

    private void saveRateData() {

        if (!TextUtils.isEmpty(edtShortStayPrice.getText().toString())) {
            double price = 0;
            price = Double.parseDouble(edtShortStayPrice.getText().toString());
            if (tv_one.isSelected() == true)
                updateData(Constants.SHORT_STAY, Constants._15MIN, price);
            else if (tv_two.isSelected() == true)
                updateData(Constants.SHORT_STAY, Constants._30MIN, price);
            else if (tv_three.isSelected() == true)
                updateData(Constants.SHORT_STAY, Constants._45MIN, price);
        }
        if (!TextUtils.isEmpty(edtHourPrice.getText().toString())) {
            double price = 0;
            price = Double.parseDouble(edtHourPrice.getText().toString());
            if (tv_Hourone.isSelected() == true)
                updateData(Constants.HOUR_STAY, Constants._1_4_HOUR, price);
            else if (tv_Hourtwo.isSelected() == true)
                updateData(Constants.HOUR_STAY, Constants._4_8_HOUR, price);
            else if (tv_Hourthree.isSelected() == true)
                updateData(Constants.HOUR_STAY, Constants._8_PLUS_HOUR, price);
        }
        if (!TextUtils.isEmpty(edtDailyPrice.getText().toString())) {
            double price = 0;
            price = Double.parseDouble(edtDailyPrice.getText().toString());
            if (tv_Dailyone.isSelected() == true)
                updateData(Constants.DAY_STAY, Constants._HALFDAY, price);
            else if (tv_Dailytwo.isSelected() == true)
                updateData(Constants.DAY_STAY, Constants._FULLDAY, price);
        }
        if (!TextUtils.isEmpty(edtWeeklyPrice.getText().toString())) {
            double price = 0;
            price = Double.parseDouble(edtWeeklyPrice.getText().toString());
            if (tv_Weeklyone.isSelected() == true)
                updateData(Constants.WEEK_STAY, Constants._3_DAY, price);
            else if (tv_Weeklytwo.isSelected() == true)
                updateData(Constants.WEEK_STAY, Constants._5_DAY, price);
            else if (tv_Weeklythree.isSelected() == true)
                updateData(Constants.WEEK_STAY, Constants._7_DAY, price);
        }
        if (!TextUtils.isEmpty(edtMonthlyPrice.getText().toString())) {
            double price = 0;
            price = Double.parseDouble(edtMonthlyPrice.getText().toString());
            if (tv_Monthlyone.isSelected() == true)
                updateData(Constants.MONTH_STAY, Constants._14_DAY, price);
            else if (tv_Monthlytwo.isSelected() == true)
                updateData(Constants.MONTH_STAY, Constants._30_DAY, price);
        }

        try {
            OwnerPropertyDetailScreen.parkingSpace.stays.clear();
            dataContext.shortStayObjectSet.fill();
            OwnerPropertyDetailScreen.parkingSpace.stays.addAll(dataContext.shortStayObjectSet);
        } catch (AdaFrameworkException e) {
            e.printStackTrace();
        }



    }

    private void inputDetail() {
        try {

            saveRateData();
            RequestBody propertyID =
                    RequestBody.create(MediaType.parse("multipart/form-data"), OwnerPropertyDetailScreen.parkingSpace != null ? OwnerPropertyDetailScreen.parkingSpace.ParkingID : "0");
            RequestBody userid =
                    RequestBody.create(MediaType.parse("multipart/form-data"), appPreferences != null ? appPreferences.getString("USERID").trim() : "0");
            RequestBody action =
                    RequestBody.create(MediaType.parse("multipart/form-data"), WebUtility.ADDPROPERTY);



            RequestBody sundayTime =
                    RequestBody.create(MediaType.parse("multipart/form-data"), bindAvailableTime("Sunday") + "");
            RequestBody mondayTime =
                    RequestBody.create(MediaType.parse("multipart/form-data"), bindAvailableTime("Monday") + "");
            RequestBody tuesdayTime =
                    RequestBody.create(MediaType.parse("multipart/form-data"), bindAvailableTime("Tuesday") + "");
            RequestBody wednesdayTime =
                    RequestBody.create(MediaType.parse("multipart/form-data"), bindAvailableTime("Wednesday") + "");
            RequestBody thursdayTime =
                    RequestBody.create(MediaType.parse("multipart/form-data"), bindAvailableTime("Thursday") + "");
            RequestBody fridayTime =
                    RequestBody.create(MediaType.parse("multipart/form-data"), bindAvailableTime("Friday") + "");
            RequestBody saturdayTime =
                    RequestBody.create(MediaType.parse("multipart/form-data"), bindAvailableTime("Saturday") + "");


            RequestBody shortTime =
                    RequestBody.create(MediaType.parse("multipart/form-data"), bindRate(Constants.SHORT_STAY));
            RequestBody hourTime =
                    RequestBody.create(MediaType.parse("multipart/form-data"), bindRate(Constants.HOUR_STAY));
            RequestBody dailyTime =
                    RequestBody.create(MediaType.parse("multipart/form-data"), bindRate(Constants.DAY_STAY));
            RequestBody weeklyTime =
                    RequestBody.create(MediaType.parse("multipart/form-data"), bindRate(Constants.WEEK_STAY));
            RequestBody monthlyTime =
                    RequestBody.create(MediaType.parse("multipart/form-data"), bindRate(Constants.MONTH_STAY));



            RequestBody latitude =
                    RequestBody.create(MediaType.parse("multipart/form-data"), lat+"");

            RequestBody longitude =
                    RequestBody.create(MediaType.parse("multipart/form-data"), lng+"");

            WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
            RequestBody requestFile = null, requestFile2 = null;
            MultipartBody.Part body = null, body2 = null;


            if (OwnerPropertyDetailScreen.list.size() == 1) {
                if (!OwnerPropertyDetailScreen.list.get(0).ImageType.equalsIgnoreCase("online")) {
                    File file = new File(OwnerPropertyDetailScreen.list.get(0).ImagePath);
                    requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    body = MultipartBody.Part.createFormData("img_1", file.getName(), requestFile);
                }
            } else if (OwnerPropertyDetailScreen.list.size() == 2) {

                if (!OwnerPropertyDetailScreen.list.get(0).ImageType.equalsIgnoreCase("online")) {
                    File file = new File(OwnerPropertyDetailScreen.list.get(0).ImagePath);
                    requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    body = MultipartBody.Part.createFormData("img_1", file.getName(), requestFile);
                }
                if (!OwnerPropertyDetailScreen.list.get(1).ImageType.equalsIgnoreCase("online")) {
                    File file2 = new File(OwnerPropertyDetailScreen.list.get(1).ImagePath);
                    requestFile2 = RequestBody.create(MediaType.parse("multipart/form-data"), file2);
                    body2 = MultipartBody.Part.createFormData("img_2", file2.getName(), requestFile2);
                }
            }

//            dataContext.propertyDetailObjectSet.fill();
//
//
//            RequestBody PropertyTitle =
//                    RequestBody.create(MediaType.parse("multipart/form-data"),dataContext.propertyDetailObjectSet.get(0).PropertyName);
//            RequestBody PropertyAddress =
//                    RequestBody.create(MediaType.parse("multipart/form-data"), dataContext.propertyDetailObjectSet.get(0).PropertyAddress);
//            RequestBody PropertyArea1 =
//                    RequestBody.create(MediaType.parse("multipart/form-data"), dataContext.propertyDetailObjectSet.get(0).PropertyWidth);
//            RequestBody PropertyArea2 =
//                    RequestBody.create(MediaType.parse("multipart/form-data"), dataContext.propertyDetailObjectSet.get(0).PropertyHeight);
//            RequestBody MaxCar =
//                    RequestBody.create(MediaType.parse("multipart/form-data"), dataContext.propertyDetailObjectSet.get(0).PropertyMaxCars);
//            RequestBody AboutProperty =
//                    RequestBody.create(MediaType.parse("multipart/form-data"), dataContext.propertyDetailObjectSet.get(0).PropertyAbout);
//            RequestBody ParkingType =
//                    RequestBody.create(MediaType.parse("multipart/form-data"), dataContext.propertyDetailObjectSet.get(0).PropertyParkingType);

            RequestBody PropertyTitle =
                    RequestBody.create(MediaType.parse("multipart/form-data"),OwnerPropertyDetailScreen.parkingSpace.ParkingTitle);
            RequestBody PropertyAddress =
                    RequestBody.create(MediaType.parse("multipart/form-data"), OwnerPropertyDetailScreen.parkingSpace.ParkingAddress);
            RequestBody PropertyArea1 =
                    RequestBody.create(MediaType.parse("multipart/form-data"),OwnerPropertyDetailScreen.parkingSpace.Width );
            RequestBody PropertyArea2 =
                    RequestBody.create(MediaType.parse("multipart/form-data"), OwnerPropertyDetailScreen.parkingSpace.Height);
            RequestBody MaxCar =
                    RequestBody.create(MediaType.parse("multipart/form-data"), OwnerPropertyDetailScreen.parkingSpace.MaximumCar);
            RequestBody AboutProperty =
                    RequestBody.create(MediaType.parse("multipart/form-data"), OwnerPropertyDetailScreen.parkingSpace.AboutParking);
            RequestBody ParkingType =
                    RequestBody.create(MediaType.parse("multipart/form-data"), OwnerPropertyDetailScreen.parkingSpace.ParkingTypes);


            Utility.showProgress(getContext());
            Call<JsonElement> responseBodyCall = null;

            if (OwnerPropertyDetailScreen.list.size() == 1) {
                if (OwnerPropertyDetailScreen.list.get(0).ImageType.equals("online")) {
                    OwnerPropertyDetailScreen.parkingSpace.ParkingImage=OwnerPropertyDetailScreen.list.get(0).ImagePath;
                    OwnerPropertyDetailScreen.parkingSpace.ParkingImage_Two="";
                    responseBodyCall = apiInterface.addProperty(action, userid, OwnerPropertyDetailScreen.list.get(0).ImagePath, "", PropertyTitle, PropertyAddress, PropertyArea1, PropertyArea2, MaxCar, AboutProperty, ParkingType, sundayTime, mondayTime, tuesdayTime, wednesdayTime, thursdayTime, fridayTime, saturdayTime, shortTime, hourTime, dailyTime, weeklyTime, monthlyTime, latitude, longitude, propertyID);
                } else {
                    OwnerPropertyDetailScreen.parkingSpace.ParkingImage=OwnerPropertyDetailScreen.list.get(0).ImagePath;
                    OwnerPropertyDetailScreen.parkingSpace.ParkingImage_Two="";
                    responseBodyCall = apiInterface.addProperty(action, userid, body, PropertyTitle, PropertyAddress, PropertyArea1, PropertyArea2, MaxCar, AboutProperty, ParkingType, sundayTime, mondayTime, tuesdayTime, wednesdayTime, thursdayTime, fridayTime, saturdayTime, shortTime, hourTime, dailyTime, weeklyTime, monthlyTime, latitude, longitude, propertyID);
                }
            } else if (OwnerPropertyDetailScreen.list.size() == 2) {
                if (OwnerPropertyDetailScreen.list.get(0).ImageType.equals("online") && OwnerPropertyDetailScreen.list.get(1).ImageType.equals("online")) {
                    OwnerPropertyDetailScreen.parkingSpace.ParkingImage=OwnerPropertyDetailScreen.list.get(0).ImagePath;
                    OwnerPropertyDetailScreen.parkingSpace.ParkingImage_Two=OwnerPropertyDetailScreen.list.get(1).ImagePath;
                    responseBodyCall = apiInterface.addProperty(action, userid, OwnerPropertyDetailScreen.list.get(0).ImagePath, OwnerPropertyDetailScreen.list.get(1).ImagePath, PropertyTitle, PropertyAddress, PropertyArea1, PropertyArea2, MaxCar, AboutProperty, ParkingType, sundayTime, mondayTime, tuesdayTime, wednesdayTime, thursdayTime, fridayTime, saturdayTime, shortTime, hourTime, dailyTime, weeklyTime, monthlyTime, latitude, longitude, propertyID);
                } else if (OwnerPropertyDetailScreen.list.get(0).ImageType.equals("online") && OwnerPropertyDetailScreen.list.get(1).ImageType.equals("gallery")) {
                    OwnerPropertyDetailScreen.parkingSpace.ParkingImage=OwnerPropertyDetailScreen.list.get(0).ImagePath;
                    OwnerPropertyDetailScreen.parkingSpace.ParkingImage_Two=OwnerPropertyDetailScreen.list.get(1).ImagePath;
                    responseBodyCall = apiInterface.addProperty(action, userid, OwnerPropertyDetailScreen.list.get(0).ImagePath, body2, PropertyTitle, PropertyAddress, PropertyArea1, PropertyArea2, MaxCar, AboutProperty, ParkingType, sundayTime, mondayTime, tuesdayTime, wednesdayTime, thursdayTime, fridayTime, saturdayTime, shortTime, hourTime, dailyTime, weeklyTime, monthlyTime, latitude, longitude, propertyID);
                } else if (OwnerPropertyDetailScreen.list.get(0).ImageType.equals("gallery") && OwnerPropertyDetailScreen.list.get(1).ImageType.equals("online")) {
                    OwnerPropertyDetailScreen.parkingSpace.ParkingImage=OwnerPropertyDetailScreen.list.get(0).ImagePath;
                    OwnerPropertyDetailScreen.parkingSpace.ParkingImage_Two=OwnerPropertyDetailScreen.list.get(1).ImagePath;
                    responseBodyCall = apiInterface.addProperty(action, userid, body, OwnerPropertyDetailScreen.list.get(1).ImagePath, PropertyTitle, PropertyAddress, PropertyArea1, PropertyArea2, MaxCar, AboutProperty, ParkingType, sundayTime, mondayTime, tuesdayTime, wednesdayTime, thursdayTime, fridayTime, saturdayTime, shortTime, hourTime, dailyTime, weeklyTime, monthlyTime, latitude, longitude, propertyID);
                } else {
                    OwnerPropertyDetailScreen.parkingSpace.ParkingImage=OwnerPropertyDetailScreen.list.get(0).ImagePath;
                    OwnerPropertyDetailScreen.parkingSpace.ParkingImage_Two=OwnerPropertyDetailScreen.list.get(1).ImagePath;
                    responseBodyCall = apiInterface.addProperty(action, userid, body, body2, PropertyTitle, PropertyAddress, PropertyArea1, PropertyArea2, MaxCar, AboutProperty, ParkingType, sundayTime, mondayTime, tuesdayTime, wednesdayTime, thursdayTime, fridayTime, saturdayTime, shortTime, hourTime, dailyTime, weeklyTime, monthlyTime, latitude, longitude, propertyID);
                }

            }
            responseBodyCall.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    Utility.hideProgress();
                    if (response.isSuccessful()) {
                        try {
                            if (response.body() != null) {
                                if (response.body().getAsJsonObject().get("error_code").getAsString().equalsIgnoreCase("0")) {
                                    getActivity().setResult(Activity.RESULT_OK);
                                    Utility.showAlertwithFinish(getActivity(), response.body().getAsJsonObject().get("error_message").getAsString());
                                    new Handler().post(new Runnable() {
                                        @Override
                                        public void run() {

                                            deletePropertyData();
                                        }
                                    });
                                } else {
                                    Utility.showAlert(getActivity(), response.body().getAsJsonObject().get("error_message").getAsString());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    Utility.hideProgress();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String bindAvailableTime(String dayname) {
        String day = "";
        if (dataContext != null) {
            try {
                dataContext.propertyAvailableTimesObjectSet.fill("dayname = ?", new String[]{dayname}, null);
                dayList.clear();
                for (int i = 0; i < dataContext.propertyAvailableTimesObjectSet.size(); i++) {
                    dayList.add(dataContext.propertyAvailableTimesObjectSet.get(i).Timing);
                }
                if (dayList.size() > 0) {
                    day = TextUtils.join(",", dayList);
                }
            } catch (AdaFrameworkException e) {
                e.printStackTrace();
            }
        }
        System.out.println("BIND TIME = " + day);
        return day;
    }

    private String bindRate(String dayname) {
        String rate = "";
        if (dataContext != null) {
            try {
                dataContext.shortStayObjectSet.fill("stay = ?", new String[]{dayname}, null);
                stayList.clear();
                for (int i = 0; i < dataContext.shortStayObjectSet.size(); i++) {
                    stayList.add(dataContext.shortStayObjectSet.get(i).StayMinutes + "|" + dataContext.shortStayObjectSet.get(i).StayPrice);
                    System.out.println("RATE  = " + dataContext.shortStayObjectSet.get(i).StayPrice);
                }
                if (stayList.size() > 0) {
                    rate = TextUtils.join(",", stayList);
                }
            } catch (AdaFrameworkException e) {
                e.printStackTrace();
            }
            System.out.println("RATE  = " + rate);
        }

        return rate;

    }

    private void deletePropertyData() {

        try {
            dataContext.shortStayObjectSet.fill();
            if (dataContext.shortStayObjectSet.size() > 0) {
                for (int i = 0; i < dataContext.shortStayObjectSet.size(); i++)
                    dataContext.shortStayObjectSet.remove(i).setStatus(Entity.STATUS_DELETED);
                dataContext.shortStayObjectSet.save();
            }

            dataContext.propertyAvailableTimesObjectSet.fill();
            if (dataContext.propertyAvailableTimesObjectSet.size() > 0) {
                for (int j = 0; j < dataContext.propertyAvailableTimesObjectSet.size(); j++)
                    dataContext.propertyAvailableTimesObjectSet.remove(j).setStatus(Entity.STATUS_DELETED);
                dataContext.propertyAvailableTimesObjectSet.save();
            }

           // OwnerPropertyEditFragmentOne.edtSearch.setText("");

        } catch (AdaFrameworkException e) {
            e.printStackTrace();
        }

    }


}
