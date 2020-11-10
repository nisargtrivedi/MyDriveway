package com.driveway.Activity;

import android.os.Build;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.Model.Notificationstbl;
import com.driveway.R;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.google.gson.JsonObject;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EActivity(R.layout.terms_condition)
public class TermsAndConditionActivity extends BaseActivity{

    @ViewById
    TTextView tvMsg;
    @ViewById
    TTextView Title;

    String pageName="terms";
    @AfterViews
    public void init(){
        pageName=getIntent().getStringExtra("name");
        if(pageName.equalsIgnoreCase("terms"))
            terms_and_condition();
        else
            privacyPolicy();

        whiteStatusBar();
    }
    private void terms_and_condition() {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(TermsAndConditionActivity.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.terms_and_condition(WebUtility.TERMS_AND_CONDITIONS,"terms");
                responseBodyCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                System.out.println("RESPONSE IS=====" + response.body().toString());
                                if (jsonObject != null) {
                                    String title=jsonObject.getString("page_title");
                                    tvMsg.setText(Html.fromHtml(jsonObject.getString("content")));
                                    tvMsg.setMovementMethod(LinkMovementMethod.getInstance());
                                    Title.setText(title+"");
                                }

                            } catch (JSONException e) {
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

    private void privacyPolicy() {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(TermsAndConditionActivity.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.terms_and_condition(WebUtility.TERMS_AND_CONDITIONS,"privacy");
                responseBodyCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                if (jsonObject != null) {
                                    String title=jsonObject.getString("page_title");
                                    tvMsg.setText(Html.fromHtml(jsonObject.getString("content")));
                                    tvMsg.setMovementMethod(LinkMovementMethod.getInstance());
                                    Title.setText(title+"");
                                }

                            } catch (JSONException e) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Click
    public void back(){
        finish();
    }
}
