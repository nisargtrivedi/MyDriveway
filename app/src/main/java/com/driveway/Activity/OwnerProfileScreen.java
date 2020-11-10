package com.driveway.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.driveway.APIResponse.LoginResponse;
import com.driveway.Adapters.OwnerProfileParkingAdapter;
import com.driveway.Component.BButton;
import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.Model.ParkingSpace;
import com.driveway.R;
import com.driveway.Utility.Constants;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.driveway.listeners.onPropertyDelete;
import com.google.gson.JsonObject;
import com.mobandme.ada.Entity;
import com.mobandme.ada.exceptions.AdaFrameworkException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerProfileScreen extends BaseActivity implements View.OnClickListener {

    AppCompatImageView back;
    TTextView tvUserName,Email,tvAge,Gender,tvEarnedPoint,tvMsg;
    BButton btnEditProfile,btnAddProperty;
    RecyclerView rvProperty;
    OwnerProfileParkingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_profile);

        bindComponent();


        whiteStatusBar();
    }
    @Override
    protected void onStart() {
        super.onStart();
//        try {
//            bindProfile();
//        } catch (AdaFrameworkException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onResume() {
        getProfileAPI();
        super.onResume();
    }

    private void bindComponent(){

        tvMsg=findViewById(R.id.tvMsg);
        tvUserName=findViewById(R.id.tvUserName);
        Email=findViewById(R.id.Email);
        tvAge=findViewById(R.id.tvAge);
        Gender=findViewById(R.id.Gender);
        tvEarnedPoint=findViewById(R.id.tvEarnedPoint);
        back=findViewById(R.id.back);
        btnEditProfile=findViewById(R.id.btnEditProfile);
        rvProperty=findViewById(R.id.rvProperty);
        btnAddProperty=findViewById(R.id.btnAddProperty);

        back.setOnClickListener(this);
        btnEditProfile.setOnClickListener(this);
        btnAddProperty.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:finish();break;
            case R.id.btnEditProfile:startActivity(new Intent(this,OwnerProfileEditScreen.class));break;
            case R.id.btnAddProperty:startActivity(new Intent(this,OwnerPropertyAddScreen.class));break;
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void bindProfile() throws AdaFrameworkException {
        dataContext.tblUserObjectSet.fill();
        if(dataContext!=null && dataContext.tblUserObjectSet.size()>0){
            tvUserName.setText(dataContext.tblUserObjectSet.get(0).FullName);
            Email.setText(dataContext.tblUserObjectSet.get(0).Email);
            tvEarnedPoint.setText(dataContext.tblUserObjectSet.get(0).EarnPoint);
            tvAge.setText(Constants.getAge(dataContext.tblUserObjectSet.get(0).BirthDate)+"");
            if(dataContext.tblUserObjectSet.get(0).Gender!=null){
                if(dataContext.tblUserObjectSet.get(0).Gender.equalsIgnoreCase("1")){
                    Gender.setText("Male");
                }
                else if(dataContext.tblUserObjectSet.get(0).Gender.equalsIgnoreCase("Male")){
                    Gender.setText("Male");
                }
                else{
                    Gender.setText("Female");
                }
            }

        }
    }
    private void getProperty(){
        try{
            dataContext.parkingSpaceObjectSet.fill();
            if(dataContext.parkingSpaceObjectSet.size()>0){
                tvMsg.setVisibility(View.GONE);
                rvProperty.setVisibility(View.VISIBLE);
            }else{
                tvMsg.setVisibility(View.VISIBLE);
                rvProperty.setVisibility(View.GONE);
            }
            if(dataContext!=null){
                adapter=new OwnerProfileParkingAdapter(this,dataContext.parkingSpaceObjectSet);
                rvProperty.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
                rvProperty.setAdapter(adapter);
                adapter.onDeleteProperty(parkingSpace -> new AlertDialog.Builder(OwnerProfileScreen.this)
                        .setMessage("Are you sure want to delete "+ parkingSpace.ParkingTitle +" property ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                if(dataContext!=null && parkingSpace!=null){
                                    try {
                                        dataContext.parkingSpaceObjectSet.fill("id = ?",new String[]{parkingSpace.getID()+""},null);
                                        if(dataContext.parkingSpaceObjectSet.size()>0){
                                            for(int i=0;i<dataContext.parkingSpaceObjectSet.size();i++)
                                                dataContext.parkingSpaceObjectSet.get(i).setStatus(Entity.STATUS_DELETED);
                                            dataContext.parkingSpaceObjectSet.save();
                                            adapter.list.remove(parkingSpace);
                                            adapter.notifyDataSetChanged();
                                            deletePropertyData(parkingSpace.ParkingID);
                                        }
                                    } catch (AdaFrameworkException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }})
                        .setNegativeButton(android.R.string.no, null).show());


            }
        }catch (Exception ex){

        }
    }

    private void deletePropertyData(String ID){
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            }else {
                Utility.showProgress(OwnerProfileScreen.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.deleteProperty(WebUtility.DELETEPROPERTY, appPreferences.getString("USERID"),ID);
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
                                            Utility.showAlert(OwnerProfileScreen.this, jsonObject.getString("error_message").toString());
                                        }else {
                                            if(jsonObject.getString("error_code").equalsIgnoreCase("10")){
                                                deleteUser();
                                            }else
                                                Utility.showAlert(OwnerProfileScreen.this, jsonObject.getString("error_message").toString());
                                        }
                                    }
                                    adapter.notifyDataSetChanged();

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

    private void getProfileAPI() {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(OwnerProfileScreen.this);
                WebServiceCaller.ApiInterface obj = WebServiceCaller.getClient();
                Call<JsonObject> responseCall = obj.getProfile(WebUtility.GET_PROFILE,appPreferences.getString("USERID"));
                responseCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Utility.hideProgress();
                        JSONObject object= null;
                        try {
                            object = new JSONObject(response.body().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (response.isSuccessful()) {
                            int errorCode=object.optInt("error_code");
                            if(errorCode==0){
                                JSONObject userobj=object.optJSONObject("data");

                                tvUserName.setText(userobj.optString("name"));
                                Email.setText(userobj.optString("email"));
                                tvEarnedPoint.setText(userobj.optString("reward_points"));
                                if(!TextUtils.isEmpty(userobj.optString("bod")))
                                    tvAge.setText(Constants.getAge(userobj.optString("bod"))+"");
                                else
                                    tvAge.setText("0");

                                Gender.setText(userobj.optString("gender"));
                            }else{
                                if(errorCode==10){
                                    deleteUser();
                                }
                            }


                            getProperty();
                        }
                    }
                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Utility.hideProgress();
                        Utility.showAlert(OwnerProfileScreen.this, t.getMessage());
                    }
                });
            }
        } catch (Exception ex) {
            Utility.hideProgress();
            Utility.showAlert(OwnerProfileScreen.this, ex.getMessage());
        }
    }
}


//{"error_code":0,"error_message":"User Detail.","data":{"id":23,"name":"Demo 2 Demo 2","email":"demodemo1384+2@gmail.com","phone_no":null,"user_type":2,"first_name":"Demo 2","last_name":"Demo 2","city":null,"bod":"12\/30\/2008","gender":"Female","zip_code":"4008","roles":"user","device_token":"","reward_points":731}}

