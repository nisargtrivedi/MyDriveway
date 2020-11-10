package com.driveway.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.driveway.Adapters.OwnerParkingAdapter;
import com.driveway.Component.EEditText;
import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.DBHelper.tblStay;
import com.driveway.Model.BookedByModel;
import com.driveway.Model.ParkingSpace;
import com.driveway.R;
import com.driveway.Utility.Constants;
import com.driveway.Utility.KeyBoardHandling;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.mobandme.ada.Entity;

import org.androidannotations.annotations.Background;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerDashboardScreen extends MainNavigationScreen {

    RecyclerView RvParking;
    OwnerParkingAdapter adapter;
    FloatingActionButton fab;
    TTextView tvNoPropertyFound;
    EEditText edtSearch;

    ArrayList<ParkingSpace> list = new ArrayList<>();
    ArrayList<tblStay> tblStays = new ArrayList<>();
    WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
    SwipeRefreshLayout pullToRefresh;

    public String isAddBankDetail="false";
    @Override
    public void setLayoutView() {
        LayoutInflater.from(this).inflate(R.layout.owner_dashboardscreen, lnrContainer);
    }

    @Override
    public void initialization() {

        setDrawerState(true);
        setDrawer();


        System.out.println("FIRST =====>");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pullToRefresh=findViewById(R.id.pullToRefresh);
        RvParking=findViewById(R.id.RvParking);
        fab=findViewById(R.id.fab);
        edtSearch=findViewById(R.id.edtSearch);
        tvNoPropertyFound=findViewById(R.id.tvNoPropertyFound);
        fab.setOnClickListener(v ->
        {
//            if(isAddBankDetail.equalsIgnoreCase("false"))
//                startActivityForResult(new Intent(OwnerDashboardScreen.this, OwnerPropertyAddScreen.class),10002);
//            else
//                startActivityForResult(new Intent(OwnerDashboardScreen.this, BankActivity_.class),10001);


            if(appPreferences.getString("USERID").equalsIgnoreCase("0"))
            {
                Utility.showAlertWithEventHandling(this, Constants.LOGIN_MESSAGE, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(new Intent(OwnerDashboardScreen.this,LoginScreen.class));
                    }
                });
            }else {

                if (isAddBankDetail.equalsIgnoreCase("false"))
                    startActivityForResult(new Intent(OwnerDashboardScreen.this, OwnerPropertyAddScreen.class), 10002);
                else
                    startActivityForResult(new Intent(OwnerDashboardScreen.this, OwnerPropertyAddScreen.class), 10001);
            }

        });

        adapter = new OwnerParkingAdapter(OwnerDashboardScreen.this, list);
        RvParking.setLayoutManager(new LinearLayoutManager(OwnerDashboardScreen.this));
        RvParking.setAdapter(adapter);


        pullToRefresh.setOnRefreshListener(() -> {
            loadDummyData();
            pullToRefresh.setRefreshing(false);
        });

        adapter.getProperty(parkingSpace -> {
                    if(parkingSpace!=null)
                        startActivityForResult(new Intent(OwnerDashboardScreen.this,OwnerPropertyDetailScreen.class).putExtra("property",parkingSpace),10003);
                }
            );
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edtSearch.getText().length()>0){
                    edtSearch.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.drawable_red_close,0);
                    adapter.getFilter().filter(edtSearch.getText().toString());
                }else{
                    edtSearch.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.drawable_right_icon_search,0);
                    KeyBoardHandling.hideSoftKeyboard(OwnerDashboardScreen.this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtSearch.setOnTouchListener((v, event) -> {
            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;

            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(edtSearch.getText().length()>0) {
                    if (event.getRawX() >= (edtSearch.getRight() - edtSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        edtSearch.setText("");
                        edtSearch.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.drawable_right_icon_search,0);
                        KeyBoardHandling.hideSoftKeyboard(OwnerDashboardScreen.this);
                        adapter.getFilter().filter(edtSearch.getText().toString());
                        return true;
                    }
                }
            }
            return false;
        });
        loadDummyData();
    }

    @Override
    protected void onResume() {
        System.out.println("THIRD =====>");

        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    public Callback<JsonObject> callback=new Callback<JsonObject>() {
        @Override
        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
            Utility.hideProgress();
            list.clear();
            tblStays.clear();
            if (response.isSuccessful()) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (response!=null) {
                                JSONObject jsonObject=new JSONObject(response.body().toString());
                                if(jsonObject!=null){
                                    isAddBankDetail=jsonObject.getString("isAddBankDetail").toString();
                                    if(jsonObject.getString("error_code").equalsIgnoreCase("0"))
                                        {

                                        JSONArray array=jsonObject.getJSONArray("data");
                                        for(int i=0;i<array.length();i++){
                                            ParkingSpace space=new ParkingSpace();
                                            space.ParkingID=array.optJSONObject(i).optString("id")!=null?array.optJSONObject(i).optString("id"):"";
                                            space.ParkingTitle=array.optJSONObject(i).optString("title")!=null?array.optJSONObject(i).optString("title"):"";
                                            space.ParkingAddress=array.optJSONObject(i).optString("address")!=null?array.optJSONObject(i).optString("address"):"";
                                            space.ParkingAvailability=array.optJSONObject(i).optString("available")!=null?array.optJSONObject(i).optString("available"):"";
                                            space.ParkingImage=array.optJSONObject(i).optString("img_1")!=null?array.optJSONObject(i).optString("img_1"):"";
                                            space.ParkingImage_Two=array.optJSONObject(i).optString("img_2")!=null?array.optJSONObject(i).optString("img_2"):"";
                                            space.AboutParking=array.optJSONObject(i).optString("about")!=null?array.optJSONObject(i).optString("about"):"";
                                            space.MaximumCar=array.optJSONObject(i).optString("max_car")!=null?array.optJSONObject(i).optString("max_car"):"";
                                            space.Width=array.optJSONObject(i).optString("area1")!=null?array.optJSONObject(i).optString("area1"):"";
                                            space.Height=array.optJSONObject(i).optString("area2")!=null?array.optJSONObject(i).optString("area2"):"";
                                            space.ParkingTypes=array.optJSONObject(i).optString("parking_type")!=null?array.optJSONObject(i).optString("parking_type"):"";
                                            space.lat=array.optJSONObject(i).optString("lat")!=null?array.optJSONObject(i).optString("lat"):"";
                                            space.lng=array.optJSONObject(i).optString("lng")!=null?array.optJSONObject(i).optString("lng"):"";
                                            space.propertyRating=array.optJSONObject(i).optString("rating")!=null?array.optJSONObject(i).optString("rating"):"0";

                                            space.sundayAvailability=array.optJSONObject(i).optString("sunday")!=null?array.optJSONObject(i).optString("sunday"):"";
                                            space.mondayAvailability=array.optJSONObject(i).optString("monday")!=null?array.optJSONObject(i).optString("monday"):"";
                                            space.tuesdayAvailability=array.optJSONObject(i).optString("tuesday")!=null?array.optJSONObject(i).optString("tuesday"):"";
                                            space.wednesdayAvailability=array.optJSONObject(i).optString("wednesday")!=null?array.optJSONObject(i).optString("wednesday"):"";
                                            space.thursdayAvailability=array.optJSONObject(i).optString("thursday")!=null?array.optJSONObject(i).optString("thursday"):"";
                                            space.fridayAvailability=array.optJSONObject(i).optString("friday")!=null?array.optJSONObject(i).optString("friday"):"";
                                            space.saturdayAvailability=array.optJSONObject(i).optString("saturday")!=null?array.optJSONObject(i).optString("saturday"):"";


                                            JSONArray arrayrates=array.optJSONObject(i).optJSONArray("rates");
                                            if(arrayrates!=null && arrayrates.length()>0) {
                                                for (int j = 0; j < arrayrates.length(); j++) {
                                                    tblStay stay=new tblStay();
                                                    stay.ParkingID=space.ParkingID;
                                                    stay.Stay=arrayrates.optJSONObject(j).optString("title")!=null?arrayrates.optJSONObject(j).optString("title"):"";
                                                    stay.StayMinutes=arrayrates.optJSONObject(j).optString("time")!=null?arrayrates.optJSONObject(j).optString("time"):"";
                                                    stay.StayPrice=Double.parseDouble(arrayrates.optJSONObject(j).getString("price")!=null?arrayrates.optJSONObject(j).optString("price"):"0");
                                                    space.stays.add(stay);
                                                    tblStays.add(stay);
                                                }
                                            }


                                            JSONArray arraybooked=array.optJSONObject(i).optJSONArray("bookedBy");
                                            if(arraybooked!=null && arraybooked.length()>0){
                                                for (int k = 0; k < arraybooked.length(); k++) {
                                                    BookedByModel model=new BookedByModel();

                                                    model.ID=arraybooked.optJSONObject(k).optString("id")!=null?arraybooked.optJSONObject(k).optString("id"):"0";
                                                    model.property_ID=arraybooked.optJSONObject(k).optString("property_id")!=null?arraybooked.optJSONObject(k).optString("property_id"):"0";
                                                    model.userID=arraybooked.optJSONObject(k).optString("user_id")!=null?arraybooked.optJSONObject(k).optString("user_id"):"0";
                                                    model.booking_start_date=arraybooked.optJSONObject(k).optString("booking_start_date")!=null?arraybooked.optJSONObject(k).optString("booking_start_date"):"";
                                                    model.booking_end_date=arraybooked.optJSONObject(k).optString("booking_end_date")!=null?arraybooked.optJSONObject(k).optString("booking_end_date"):"";
                                                    model.booking_start_time=arraybooked.optJSONObject(k).optString("booking_start_time")!=null?arraybooked.optJSONObject(k).optString("booking_start_time"):"";
                                                    model.booking_end_time=arraybooked.optJSONObject(k).optString("booking_end_time")!=null?arraybooked.optJSONObject(k).optString("booking_end_time"):"";
                                                    model.price=arraybooked.optJSONObject(k).optString("price")!=null?arraybooked.optJSONObject(k).optString("price"):"";
                                                    model.booking_status=arraybooked.optJSONObject(k).optString("booking_status")!=null?arraybooked.optJSONObject(k).optString("booking_status"):"";
                                                    model.isPayment=arraybooked.optJSONObject(k).optString("isPayment")!=null?arraybooked.optJSONObject(k).optString("isPayment"):"0";

                                                    model.rates.title=arraybooked.optJSONObject(k).optJSONObject("rates").optString("title")!=null?arraybooked.optJSONObject(k).optJSONObject("rates").optString("title"):"";
                                                    model.rates.time=arraybooked.optJSONObject(k).optJSONObject("rates").optString("time")!=null?arraybooked.optJSONObject(k).optJSONObject("rates").optString("time"):"";
                                                    model.rates.price=arraybooked.optJSONObject(k).optJSONObject("rates").optString("price")!=null?arraybooked.optJSONObject(k).optJSONObject("rates").optString("price"):"";

                                                    if(arraybooked.optJSONObject(k).optJSONObject("parker_user")!=null) {
                                                        model.parkerUser.name = arraybooked.optJSONObject(k).optJSONObject("parker_user").optString("name") != null ? arraybooked.optJSONObject(k).optJSONObject("parker_user").optString("name") : "";
                                                        model.parkerUser.first_name = arraybooked.optJSONObject(k).optJSONObject("parker_user").optString("first_name") != null ? arraybooked.optJSONObject(k).optJSONObject("parker_user").optString("first_name") : "";
                                                        model.parkerUser.id = arraybooked.optJSONObject(k).optJSONObject("parker_user").optString("id") != null ? arraybooked.optJSONObject(k).optJSONObject("parker_user").optString("id") : "";
                                                        model.parkerUser.email = arraybooked.optJSONObject(k).optJSONObject("parker_user").optString("email") != null ? arraybooked.optJSONObject(k).optJSONObject("parker_user").optString("email") : "";
                                                        model.parkerUser.bod = arraybooked.optJSONObject(k).optJSONObject("parker_user").optString("bod") != null ? arraybooked.optJSONObject(k).optJSONObject("parker_user").optString("bod") : "";
                                                        model.parkerUser.gender = arraybooked.optJSONObject(k).optJSONObject("parker_user").optString("gender") != null ? arraybooked.optJSONObject(k).optJSONObject("parker_user").optString("gender") : "";
                                                        model.parkerUser.reward_points = arraybooked.optJSONObject(k).optJSONObject("parker_user").optString("reward_points") != null ? arraybooked.optJSONObject(k).optJSONObject("parker_user").optString("reward_points") : "";
                                                        model.parkerUser.fcm_id = arraybooked.optJSONObject(k).optJSONObject("parker_user").optString("fcm_id") != null ? arraybooked.optJSONObject(k).optJSONObject("parker_user").optString("fcm_id") : "";
                                                        model.parkerUser.device_token = arraybooked.optJSONObject(k).optJSONObject("parker_user").optString("device_token") != null ? arraybooked.optJSONObject(k).optJSONObject("parker_user").optString("device_token") : "";
                                                    }
                                                    space.bookedByModels.add(model);
                                                }
                                            }

                                            list.add(space);
                                        }
                                        adapter.notifyDataSetChanged();
                                        if(list.size()>0) {
                                            tvNoPropertyFound.setVisibility(View.GONE);
                                            RvParking.setVisibility(View.VISIBLE);
                                            addProperty(list);
                                            addPropertyStay(tblStays);
                                        }

                                    }
                                    else
                                        {
                                        if(jsonObject.getString("error_code").equalsIgnoreCase("10")){
                                            deleteUser();
                                        }else {
                                            Utility.hideProgress();
                                            isAddBankDetail=jsonObject.getString("isAddBankDetail").toString();
                                            //Utility.showAlert(OwnerDashboardScreen.this, jsonObject.getString("error_message").toString());
                                            if (list.size() == 0) {
                                                Utility.hideProgress();
                                                tvNoPropertyFound.setVisibility(View.VISIBLE);
                                                RvParking.setVisibility(View.GONE);
                                            }


                                        }
                                    }

                                }
                            }

                        }
                        catch (Exception e) {
                            Utility.showAlert(OwnerDashboardScreen.this, e.toString());
                            e.printStackTrace();
                        }

                    }
                });

            }
        }

        @Override
        public void onFailure(Call<JsonObject> call, Throwable t) {
            Utility.hideProgress();
        }
    };


    @UiThread
    private void loadDummyData(){

        if(appPreferences.getString("USERID").equalsIgnoreCase("0"))
        {
            if (list.size() == 0) {
                Utility.hideProgress();
                tvNoPropertyFound.setVisibility(View.VISIBLE);
                RvParking.setVisibility(View.GONE);
            }
        }else {

            try {
                if (!Utility.isNetworkAvailable(this)) {
                    Utility.showAlert(this, "please check internet connection");
                } else {
                    Call<JsonObject> responseBodyCall = null;
                    Utility.showProgress(OwnerDashboardScreen.this);
                    responseBodyCall = apiInterface.getProperty(WebUtility.GETPROPERTY,
                            appPreferences.getString("USERID"));
                    if (responseBodyCall.isExecuted())
                        responseBodyCall.enqueue(callback);
                    else
                        responseBodyCall.clone().enqueue(callback);

                }

            } catch (Exception ex) {
                Utility.hideProgress();
            }
        }
    }

    private void addProperty(ArrayList<ParkingSpace> obj){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(obj.size()>0) {
                    try {
                        dataContext.parkingSpaceObjectSet.fill();
                        for (int i = 0; i < dataContext.parkingSpaceObjectSet.size(); i++)
                            dataContext.parkingSpaceObjectSet.get(i).setStatus(Entity.STATUS_DELETED);

                        dataContext.parkingSpaceObjectSet.save();
                        dataContext.parkingSpaceObjectSet.addAll(obj);
                        dataContext.parkingSpaceObjectSet.save();

                    } catch (Exception ex) {

                    }
                }
            }
        });

    }
    private void addPropertyStay(ArrayList<tblStay> obj){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    if(obj.size()>0){
                        dataContext.shortStayObjectSet.fill();
                        for (int i = 0; i < dataContext.shortStayObjectSet.size(); i++)
                            dataContext.shortStayObjectSet.get(i).setStatus(Entity.STATUS_DELETED);


                        dataContext.shortStayObjectSet.addAll(obj);
                        dataContext.shortStayObjectSet.save();
                    }
                }catch (Exception ex){

                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==10001 && resultCode == Activity.RESULT_OK){
            //startActivity(new Intent(OwnerDashboardScreen.this, OwnerPropertyAddScreen.class));
            startActivityForResult(new Intent(OwnerDashboardScreen.this, BankActivity_.class),10002);
        }
        if(requestCode==10002 && resultCode == Activity.RESULT_OK){
            loadDummyData();
        }
        if(requestCode==10003 && resultCode == Activity.RESULT_OK){
            loadDummyData();
        }
    }
}
