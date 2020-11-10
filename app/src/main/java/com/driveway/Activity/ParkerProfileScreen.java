package com.driveway.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.driveway.Adapters.OwnerProfileParkingAdapter;
import com.driveway.Adapters.ParkerCarAdapter;
import com.driveway.Component.BButton;
import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.DBHelper.tblCars;
import com.driveway.R;
import com.driveway.Utility.Constants;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.driveway.listeners.onParkerDelete;
import com.driveway.listeners.onParkerEdit;
import com.google.gson.JsonObject;
import com.mobandme.ada.Entity;
import com.mobandme.ada.exceptions.AdaFrameworkException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParkerProfileScreen extends BaseActivity implements View.OnClickListener {

    AppCompatImageView back;
    TTextView tvUserName, Email, tvAge, Gender, tvEarnedPoint;
    BButton btnEditProfile, btnAddCars;
    RecyclerView rvCars;
    ParkerCarAdapter adapter;
    public ArrayList<tblCars> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parker_profile);

        bindComponent();

        //getProperty();

        whiteStatusBar();
    }

    @Override
    protected void onResume() {
        loadData();
        super.onResume();
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

    private void bindComponent() {
        tvUserName = findViewById(R.id.tvUserName);
        Email = findViewById(R.id.Email);
        tvAge = findViewById(R.id.tvAge);
        Gender = findViewById(R.id.Gender);
        tvEarnedPoint = findViewById(R.id.tvEarnedPoint);
        back = findViewById(R.id.back);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        rvCars = findViewById(R.id.rvCars);
        btnAddCars = findViewById(R.id.btnAddCars);

        back.setOnClickListener(this);
        btnEditProfile.setOnClickListener(this);
        btnAddCars.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btnEditProfile:
                startActivity(new Intent(this, OwnerProfileEditScreen.class));
                break;
            case R.id.btnAddCars:
                startActivity(new Intent(this, ParkerAddCarScreen.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void bindProfile() throws AdaFrameworkException {
        dataContext.tblUserObjectSet.fill();
        if (dataContext != null && dataContext.tblUserObjectSet.size() > 0) {
            tvUserName.setText(dataContext.tblUserObjectSet.get(0).FullName);
            Email.setText(dataContext.tblUserObjectSet.get(0).Email);
            tvAge.setText(Constants.getAge(dataContext.tblUserObjectSet.get(0).BirthDate)+"");

            tvEarnedPoint.setText(dataContext.tblUserObjectSet.get(0).EarnPoint);
            if (dataContext.tblUserObjectSet.get(0).Gender != null)
                Gender.setText(dataContext.tblUserObjectSet.get(0).Gender.equalsIgnoreCase("1") || dataContext.tblUserObjectSet.get(0).Gender.equalsIgnoreCase("Male") ? "Male" : "Female");
        }
    }

    private void getProperty() {
        try {
            dataContext.carsObjectSet.fill();
            if (dataContext != null) {
                adapter = new ParkerCarAdapter(this, list);
                rvCars.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
                rvCars.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                adapter.onDeleteCars(cars -> new AlertDialog.Builder(ParkerProfileScreen.this)
                        .setMessage("Are you sure want to delete "+ cars.CarModel +" car ?")
                        .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {




                                if (dataContext != null && cars != null) {
                                    try {
                                        dataContext.carsObjectSet.fill("id = ?", new String[]{cars.getID() + ""}, null);
                                        if (dataContext.carsObjectSet.size() > 0) {
                                            for (int i = 0; i < dataContext.carsObjectSet.size(); i++)
                                                dataContext.carsObjectSet.get(i).setStatus(Entity.STATUS_DELETED);
                                            dataContext.carsObjectSet.save();
                                            adapter.list.remove(cars);
                                            adapter.notifyDataSetChanged();
                                            deleteCarsData(cars.CarID);
                                        }
                                    } catch (AdaFrameworkException e) {
                                        e.printStackTrace();
                                    }
                                }



                        })
                        .setNegativeButton(android.R.string.no, null).show());

                adapter.onEditCar(cars -> {
                    Intent i = new Intent(ParkerProfileScreen.this, ParkerAddCarScreen.class);
                    i.putExtra("model", cars.CarModel);
                    i.putExtra("year", cars.CarMakingYear);
                    i.putExtra("carid", cars.CarID);
                    i.putExtra("image", cars.CarImage);
                    i.putExtra("regno", cars.CarRegisterNumber);
                    i.putExtra("is_default", cars.is_default);
                    startActivity(i);
                });
            }
        } catch (Exception ex) {

        }
    }
    private void deleteCarsData(String carID) {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(ParkerProfileScreen.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.deleteCars(WebUtility.DELETECARS, appPreferences.getString("USERID"), carID);
                responseBodyCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            try {
                                if (response.body() != null) {
                                    System.out.println("Error Code==>" + response.body().toString());
                                    JSONObject jsonObject = new JSONObject(response.body().toString());
                                    if (jsonObject != null) {
                                        System.out.println("Error Code==>" + jsonObject.getString("error_code"));
                                        if (jsonObject.getString("error_code").equalsIgnoreCase("0")) {

                                        } else {
                                            Utility.showAlert(ParkerProfileScreen.this, jsonObject.getString("error_message").toString());
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
        } catch (Exception ex) {
        }
    }

    private void addCars(ArrayList<tblCars> obj) {
        if(obj.size()>0) {
            try {
                dataContext.carsObjectSet.fill();
                if (dataContext != null) {
                    for (int i = 0; i < dataContext.carsObjectSet.size(); i++)
                        dataContext.carsObjectSet.remove(i).setStatus(Entity.STATUS_DELETED);

                    dataContext.carsObjectSet.save();
                    dataContext.carsObjectSet.addAll(obj);
                    dataContext.carsObjectSet.save();
                }
            } catch (Exception ex) {

            }
        }
        getProfileAPI();
    }

    private void getProfileAPI() {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
               // Utility.showProgress(ParkerProfileScreen.this);
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
                            }


                            getProperty();
                        }
                    }
                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Utility.hideProgress();
                        Utility.showAlert(ParkerProfileScreen.this, t.getMessage());
                    }
                });
            }
        } catch (Exception ex) {
            Utility.hideProgress();
            Utility.showAlert(ParkerProfileScreen.this, ex.getMessage());
        }
    }

    private void loadData() {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(ParkerProfileScreen.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.getProperty(WebUtility.GETCARS, appPreferences.getString("USERID"));
                responseBodyCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                       // Utility.hideProgress();
                        if (response.isSuccessful()) {
                            try {
                                if (response.body() != null) {
                                    System.out.println("Error Code==>" + response.body().toString());
                                    JSONObject jsonObject = new JSONObject(response.body().toString());
                                    if (jsonObject != null) {
                                        System.out.println("Error Code==>" + jsonObject.getString("error_code"));
                                        if (jsonObject.getString("error_code").equalsIgnoreCase("0")) {
                                            list.clear();
                                            JSONArray array = jsonObject.getJSONArray("data");
                                            for (int i = 0; i < array.length(); i++) {
                                                tblCars cars = new tblCars();
                                                cars.CarID = array.getJSONObject(i).getString("id") != null ? array.getJSONObject(i).getString("id") : "";
                                                cars.CarModel = array.getJSONObject(i).getString("model") != null ? array.getJSONObject(i).getString("model") : "";
                                                cars.CarImage = array.getJSONObject(i).getString("car_img") != null ? array.getJSONObject(i).getString("car_img") : "";
                                                cars.CarRegisterNumber = array.getJSONObject(i).getString("reg_number") != null ? array.getJSONObject(i).getString("reg_number") : "";
                                                cars.CarMakingYear = array.getJSONObject(i).getString("make_year") != null ? array.getJSONObject(i).getString("make_year") : "";
                                                cars.is_default = array.getJSONObject(i).getString("is_default") != null ? array.getJSONObject(i).getString("is_default") : "";


                                                list.add(cars);
                                            }
                                            addCars(list);
//                                            if(adapter!=null)
//                                                adapter.notifyDataSetChanged();

                                        } else {
                                            //Utility.showAlert(ParkerDashboardScreen.this, jsonObject.getString("error_message").toString());
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
                        //Utility.hideProgress();
                    }
                });
            }
        } catch (Exception ex) {
        }
    }

}


