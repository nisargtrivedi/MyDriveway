package com.driveway.Activity;

import android.text.TextUtils;

import androidx.appcompat.widget.AppCompatImageView;

import com.driveway.Component.EEditText;
import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.R;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.google.gson.JsonObject;


import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EActivity(R.layout.feedback_screen)
public class FeedbackScreen extends BaseActivity {

    @ViewById
    TTextView btnSubmit;
    @ViewById
    AppCompatImageView back;
    @ViewById
    EEditText edtfeature;
    @ViewById
    EEditText edtexperience;


    @AfterViews
    public void init(){
        whiteStatusBar();
    }
    @Click
    public void back(){
        finish();
        overridePendingTransition(R.anim.entry, R.anim.exit);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.entry, R.anim.exit);
    }
    @Click
    public void btnSubmit(){
        if(TextUtils.isEmpty(edtfeature.getText().toString()) && TextUtils.isEmpty(edtexperience.getText().toString())){
            Utility.showAlert(this,"Please enter any one feedback");
        } else{
                addfeedbackAPI();
        }
    }
    private void addfeedbackAPI(){
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            }else {
                Utility.showProgress(FeedbackScreen.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.addFeedback(WebUtility.FEEDBACK,appPreferences.getString("USERID"),edtexperience.getText().toString().trim(),edtfeature.getText().toString().trim());
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
                                            Utility.showAlertwithFinish(FeedbackScreen.this, jsonObject.getString("error_message").toString());
                                        }else {
                                            if(jsonObject.getString("error_code").equalsIgnoreCase("10")){
                                                deleteUser();
                                            }else
                                                Utility.showAlert(FeedbackScreen.this, jsonObject.getString("error_message").toString());
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
