package com.driveway.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;

import com.driveway.Component.EEditText;
import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.Model.BookedByModel;
import com.driveway.Model.ParkerBookingList;
import com.driveway.Model.SearchPropertyModel;
import com.driveway.R;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import okio.Utf8;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportScreen extends BaseActivity implements View.OnClickListener {

    TTextView tvReportResident,tvReportIncident;
    LinearLayout llResident,llIncident;
    EEditText edtResident,edtIncident;

    TTextView btnSubmit;
    AppCompatImageView back;

    int resident=0;
    int incident=0;
    String userid="0";
    String bookingid="0";

    BookedByModel model;
    ParkerBookingList obj;
    SearchPropertyModel parkingSpace;
    ParkerBookingList bookingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_screen);
        btnSubmit=findViewById(R.id.btnSubmit);
        edtIncident=findViewById(R.id.edtIncident);
        edtResident=findViewById(R.id.edtResident);
        llResident=findViewById(R.id.llResident);
        llIncident=findViewById(R.id.llIncident);
        tvReportResident=findViewById(R.id.tvReportResident);
        tvReportIncident=findViewById(R.id.tvReportIncident);
        back=findViewById(R.id.back);

        tvReportIncident.setOnClickListener(this);
        tvReportResident.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        back.setOnClickListener(this);
        whiteStatusBar();

        if(getIntent()!=null){
            if(getIntent().getSerializableExtra("objdata")!=null) {
                obj = (ParkerBookingList) getIntent().getSerializableExtra("objdata");
                if(obj!=null && obj.owneruser!=null) {
                    userid = obj.owneruser.UserID;
                    bookingid=obj.bookingID;
                }
            }
            else if(getIntent().getSerializableExtra("modeldata")!=null) {
                model = (BookedByModel) getIntent().getSerializableExtra("modeldata");
                if(model!=null && model.userID!=null) {
                    userid = model.userID;
                    bookingid=model.ID;
                }
            }
            else if(getIntent().getSerializableExtra("propertymodel")!=null) {
                parkingSpace = (SearchPropertyModel) getIntent().getSerializableExtra("propertymodel");
                if(parkingSpace!=null && parkingSpace.userID!=null) {
                    userid = parkingSpace.userID;
                    bookingid=getIntent().getStringExtra("bookingID");

                }
            }
            else if(getIntent().getSerializableExtra("booking")!=null) {
                bookingList = (ParkerBookingList) getIntent().getSerializableExtra("booking");
                if(bookingList!=null && bookingList.owneruser!=null) {
                    userid = bookingList.owneruser.UserID;
                    bookingid=bookingList.bookingID;
                }

            }
            else if(getIntent().getSerializableExtra("booking")!=null) {
                bookingList = (ParkerBookingList) getIntent().getSerializableExtra("booking");
                if(bookingList!=null && bookingList.owneruser!=null) {
                    userid = bookingList.owneruser.UserID;
                    bookingid=bookingList.bookingID;
                }

            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.tvReportIncident:
                incident=1;
                resident=0;
                edtResident.setText("");
                showHideReportIncident();break;
            case R.id.tvReportResident:
                resident=1;
                incident=0;
                edtIncident.setText("");
                showHideReportResident();break;
            case R.id.btnSubmit:intputData();break;
            case R.id.back:finish();break;
        }
    }

    private void intputData(){
        if(incident==0 && resident==0){
            Utility.showAlert(this,"Please select any one report option");
        }else if(resident==1){
                if(TextUtils.isEmpty(edtResident.getText().toString())){
                    Utility.showAlert(this,"Please enter report a resident");
                }else {
                    addReportAPI();
                }
        }else if(incident==1){
            if(TextUtils.isEmpty(edtIncident.getText().toString())) {
                Utility.showAlert(this,"Please enter report an incident");
            }else{
                addReportAPI();
            }
        }
    }

    private void showHideReportIncident(){

        if(llIncident.getVisibility()==View.VISIBLE){
            llIncident.setVisibility(View.GONE);
            tvReportIncident.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three_watch);
            tvReportIncident.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.drawable_arrow_down,0);
        }else{
            tvReportIncident.setBackgroundResource(android.R.color.transparent);
            tvReportIncident.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.drawable_arrow_up,0);
            llIncident.setVisibility(View.VISIBLE);
            llResident.setVisibility(View.GONE);
            tvReportResident.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three_watch);
            tvReportResident.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.drawable_arrow_down,0);

        }
    }

    private void showHideReportResident(){

        if(llResident.getVisibility()==View.VISIBLE){
            llResident.setVisibility(View.GONE);
            tvReportResident.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three_watch);
            tvReportResident.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.drawable_arrow_down,0);
        }else{
            llResident.setVisibility(View.VISIBLE);
            llIncident.setVisibility(View.GONE);
            tvReportResident.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.drawable_arrow_up,0);
            tvReportResident.setBackgroundResource(android.R.color.transparent);

            tvReportIncident.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three_watch);
            tvReportIncident.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.drawable_arrow_down,0);

        }
    }

    private void addReportAPI(){

        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            }else {
                Utility.showProgress(ReportScreen.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall =incident==1?apiInterface.addReport(WebUtility.SEND_REPORT,appPreferences.getString("USERID"),edtResident.getText().toString().trim()+edtIncident.getText().toString().trim(),bookingid):
                        apiInterface.addReport(WebUtility.SEND_REPORT_TWO,appPreferences.getString("USERID"),
                                userid,edtResident.getText().toString().trim()+edtIncident.getText().toString().trim(),bookingid);
                responseBodyCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            try {
                                if (response.body() != null) {
                                    System.out.println("Error Code==>"+response.body().toString());
                                    JSONObject jsonObject=new JSONObject(response.body().toString());
                                    if(jsonObject!=null){
                                        System.out.println("Error Code==>"+jsonObject.getString("error_code"));
                                        if(jsonObject.getString("error_code").equalsIgnoreCase("0")){
                                            Utility.showAlertwithFinish(ReportScreen.this, jsonObject.getString("error_message").toString());
                                        }else {
                                            Utility.showAlert(ReportScreen.this, jsonObject.getString("error_message").toString());
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
        }catch (Exception ex){}
    }
}
