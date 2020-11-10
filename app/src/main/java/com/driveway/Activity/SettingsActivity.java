package com.driveway.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;

import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.R;
import com.driveway.Utility.Constants;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.google.gson.JsonObject;
import com.mobandme.ada.Entity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EActivity(R.layout.activity_settings)
public class SettingsActivity extends BaseActivity {

    @ViewById
    LinearLayout llAffiliation;
    @ViewById
    LinearLayout llTerms;
    @ViewById
    LinearLayout llprivacy;
    @ViewById
    LinearLayout llChangePassword;
    @ViewById
    LinearLayout llBank;
    @ViewById
    LinearLayout llFeedback;
    @ViewById
    LinearLayout llHowAppWork;
    @ViewById
    AppCompatImageView imgBank;
    @ViewById
    LinearLayout llLogout;
    @ViewById
    TTextView tvBank;


    @AfterViews
    public void init(){
        transparentStatusbar();

        if(appPreferences.getString("USERID").equalsIgnoreCase("0"))
        {

            llLogout.setVisibility(View.INVISIBLE);
            if (appPreferences.getString("USERTYPE").equalsIgnoreCase("1")) {
                tvBank.setText("Manage Cards");
                imgBank.setImageResource(R.drawable.setting_card);
                llAffiliation.setVisibility(View.VISIBLE);
            } else {
                tvBank.setText("Manage Bank Details");
                imgBank.setImageResource(R.drawable.ic_bank);
                llAffiliation.setVisibility(View.VISIBLE);
            }

        }
        else {
            llLogout.setVisibility(View.VISIBLE);
            try {
                dataContext.tblUserObjectSet.fill();
                if (dataContext.tblUserObjectSet.size() > 0) {
                    if (dataContext.tblUserObjectSet.get(0).UserType.equalsIgnoreCase("1")) {
                        tvBank.setText("Manage Cards");
                        imgBank.setImageResource(R.drawable.setting_card);
                        llAffiliation.setVisibility(View.VISIBLE);
                    } else {
                        tvBank.setText("Manage Bank Details");
                        imgBank.setImageResource(R.drawable.ic_bank);
                        llAffiliation.setVisibility(View.VISIBLE);
                    }
                }
            } catch (Exception ex) {

            }
        }
    }
    @Click
    public void llAffiliation(){
        startActivity(new Intent(this,AffiliationActivity_.class));

    }
    @Click
    public void llHowAppWork(){
        startActivity(new Intent(this,ActivityHowAppWork_.class));
    }
    @Click
    public void llTerms(){
        startActivity(new Intent(this,TermsAndConditionActivity_.class)
        .putExtra("name","terms")
        );

    }
    @Click
    public void llprivacy(){
        startActivity(new Intent(this,TermsAndConditionActivity_.class)
                .putExtra("name","privacy")
        );


    }
    @Click
    public void llChangePassword(){

            if(appPreferences.getString("USERID").equalsIgnoreCase("0"))
            {
                Utility.showAlertWithEventHandling(this, Constants.LOGIN_MESSAGE, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(new Intent(SettingsActivity.this,LoginScreen.class)
                        .putExtra("pagename","setting")
                        );
                    }
                });
            }
            else {
                startActivity(new Intent(this, ChangePasswordScreen.class));
            }

    }

    @Click
    public void llBank(){

        if(appPreferences.getString("USERID").equalsIgnoreCase("0"))
        {
            Utility.showAlertWithEventHandling(this, Constants.LOGIN_MESSAGE, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    if (tvBank.getText().toString().equalsIgnoreCase("Manage Cards")) {
                        startActivity(new Intent(SettingsActivity.this, LoginScreen.class)
                                .putExtra("pagename", "card")
                        );
                    } else {
                        startActivity(new Intent(SettingsActivity.this, LoginScreen.class)
                                .putExtra("pagename", "bank")
                        );
                    }
                }
            });
        }
        else {
            if (tvBank.getText().toString().equalsIgnoreCase("Manage Cards")) {
                startActivity(new Intent(this, ParkerManageCard.class)
                        .putExtra("page","setting")
                );
            } else
                startActivity(new Intent(this, BankActivity_.class));
        }

    }
    @Click
    public void llFeedback(){
        if(appPreferences.getString("USERID").equalsIgnoreCase("0"))
        {
            Utility.showAlertWithEventHandling(this, Constants.LOGIN_MESSAGE, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    startActivity(new Intent(SettingsActivity.this,LoginScreen.class)
                            .putExtra("pagename","setting")
                    );
                }
            });
        }
        else {
            startActivity(new Intent(this, FeedbackScreen_.class));
        }

    }
    @Click
    public void llLogout(){
        logoutSession();
    }
    @Click
    public void  back(){
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void logoutSession() {

        Utility.showAlertWithEventHandling(this, "Are you sure want to logout ?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    if (!Utility.isNetworkAvailable(SettingsActivity.this)) {
                        Utility.showAlert(SettingsActivity.this, "please check internet connection");
                    } else {
                        Utility.showProgress(SettingsActivity.this);
                        WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                        Call<JsonObject> responseBodyCall = apiInterface.logoutSession(WebUtility.LOGOUT_SESSION,
                                dataContext.tblUserObjectSet.get(0).Token,
                                dataContext.tblUserObjectSet.get(0).DeviceToken,
                                appPreferences.getString("USERID"));
                        responseBodyCall.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                Utility.hideProgress();
                                if (response.isSuccessful()) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.body().toString());
                                        System.out.println("RESPONSE IS=====" + response.body().toString());
                                        deleteUser();

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
        });

    }
    public void deleteUser(){
        try{
            dataContext.tblUserObjectSet.fill();
            if(dataContext!=null){
                for(int i=0;i<dataContext.tblUserObjectSet.size();i++)
                    dataContext.tblUserObjectSet.remove(i).setStatus(Entity.STATUS_DELETED);

                dataContext.tblUserObjectSet.save();
                appPreferences.set("USERTYPE","");
                appPreferences.set("USERID","");
                appPreferences.set("LATITUDE","");
                appPreferences.set("LONGITUDE","");
                appPreferences.removeValue("USERTYPE");
                appPreferences.removeValue("USERID");
                appPreferences.removeValue("LATITUDE");
                appPreferences.removeValue("LONGITUDE");
            }

            dataContext.parkingSpaceObjectSet.fill();

            for(int i=0;i<dataContext.parkingSpaceObjectSet.size();i++)
                dataContext.parkingSpaceObjectSet.remove(i).setStatus(Entity.STATUS_DELETED);

            dataContext.parkingSpaceObjectSet.save();


            dataContext.carsObjectSet.fill();

            for(int i=0;i<dataContext.carsObjectSet.size();i++)
                dataContext.carsObjectSet.remove(i).setStatus(Entity.STATUS_DELETED);

            dataContext.carsObjectSet.save();


            dataContext.propertyAvailableTimesObjectSet.fill();
            for(int i=0;i<dataContext.propertyAvailableTimesObjectSet.size();i++)
                dataContext.propertyAvailableTimesObjectSet.remove(i).setStatus(Entity.STATUS_DELETED);

            dataContext.propertyAvailableTimesObjectSet.save();

            dataContext.shortStayObjectSet.fill();

            for(int i=0;i<dataContext.shortStayObjectSet.size();i++)
                dataContext.shortStayObjectSet.remove(i).setStatus(Entity.STATUS_DELETED);

            dataContext.shortStayObjectSet.save();


            dataContext.propertyDetailObjectSet.fill();
            for(int i=0;i<dataContext.propertyDetailObjectSet.size();i++)
                dataContext.propertyDetailObjectSet.remove(i).setStatus(Entity.STATUS_DELETED);

            dataContext.propertyDetailObjectSet.save();


            dataContext.propertyImageObjectSet.fill();
            for(int i=0;i<dataContext.propertyImageObjectSet.size();i++)
                dataContext.propertyImageObjectSet.remove(i).setStatus(Entity.STATUS_DELETED);

            dataContext.propertyImageObjectSet.save();

            finishAffinity();
            startActivity(new Intent(SettingsActivity.this,MainScreen.class));
//            android.os.Process.killProcess(android.os.Process.myPid());
//            System.exit(1);
        }catch (Exception ex){

        }
    }
}
