package com.driveway.Activity.OwnerBookingDetail;

import android.graphics.Typeface;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.driveway.Activity.BaseActivity;
import com.driveway.Adapters.OwnerBookingListAdapter;
import com.driveway.Adapters.OwnerParkingDetailBookingAdapter;
import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.Model.ParkerBookingList;
import com.driveway.Model.ParkingSpace;
import com.driveway.R;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.google.gson.JsonObject;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EActivity(R.layout.owner_propertybooking_list_screen)
public class OwnerBookingProperty extends BaseActivity {

    @ViewById
    TTextView tvCompleted;

    @ViewById
    TTextView tvOngoing;

    @ViewById
    TTextView tvUpcoming;
    @ViewById
    TTextView tvMsg;
    @ViewById
    TTextView tvTitle;
    @ViewById
    RecyclerView rvBookings;
    @ViewById
    SwipeRefreshLayout pullToRefresh;



    List<ParkerBookingList> bookingLists = new ArrayList<>();
    OwnerParkingDetailBookingAdapter adapter;
    String PropertyID="0";
    ParkingSpace parkingSpace;
    @AfterViews
    public void init(){

        orangeStatusBar();
        parkingSpace= (ParkingSpace) getIntent().getSerializableExtra("data");

        if(parkingSpace!=null){
            PropertyID=parkingSpace.ParkingID;
            tvTitle.setText(parkingSpace.ParkingTitle);
        }else{
            PropertyID="0";
        }
        adapter=new OwnerParkingDetailBookingAdapter(this,bookingLists);
        rvBookings.setLayoutManager(new LinearLayoutManager(this));
        rvBookings.setAdapter(adapter);

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadBookings("Ongoing");
                setTextColor(tvOngoing,tvUpcoming,tvCompleted);
                pullToRefresh.setRefreshing(false);
            }
        });
        loadBookings("Ongoing");
        setTextColor(tvOngoing,tvUpcoming,tvCompleted);


    }

    @Click
    public void tvOngoing(){
        setTextColor(tvOngoing,tvUpcoming,tvCompleted);
        loadBookings("Ongoing");
    }
    @Click
    public void tvUpcoming(){
        setTextColor(tvUpcoming,tvCompleted,tvOngoing);
        loadBookings("Upcoming");
    }
    @Click
    public void tvCompleted(){
        setTextColor(tvCompleted,tvOngoing,tvUpcoming);
        loadBookings("Completed");
    }

    @Click
    public void Back(){
        finish();
    }

    private void setTextColor(TTextView t1, TTextView t2, TTextView t3){
        t1.setTextColor(getResources().getColor(R.color.white));
        t1.setBackgroundResource(R.drawable.bottom);
        t1.setTypeface(t1.getTypeface(), Typeface.BOLD);

        t2.setTextColor(getResources().getColor(R.color.white));
        t2.setBackgroundResource(R.drawable.bottom_reverse);
        t2.setTypeface(Typeface.createFromAsset(getAssets(),"ProductSansRegular.ttf"));

        t3.setTextColor(getResources().getColor(R.color.white));
        t3.setBackgroundResource(R.drawable.bottom_reverse);
        t3.setTypeface(Typeface.createFromAsset(getAssets(),"ProductSansRegular.ttf"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    Callback<JsonObject> callback=new Callback<JsonObject>() {
        @Override
        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

            Utility.hideProgress();
            if (response.isSuccessful()) {
                try {
                    if (response.body() != null) {
                        //System.out.println("Error Code==>" + response.body().toString());
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        if (jsonObject != null) {
                           // System.out.println("Error Code==>" + jsonObject.getString("error_code"));
                            if (jsonObject.getString("error_code").equalsIgnoreCase("0")) {

                                JSONArray array=jsonObject.getJSONArray("data");
                                bookingLists.clear();
                                if(array!=null && array.length()>0){
                                    for(int i=0;i<array.length();i++){
                                        ParkerBookingList parkerBookingList=new ParkerBookingList();
                                        parkerBookingList.bookingID=array.optJSONObject(i).optString("id");
                                        parkerBookingList.userID=array.optJSONObject(i).optString("user_id");
                                        parkerBookingList.bookingEndTime=array.optJSONObject(i).optString("booking_end_time");
                                        parkerBookingList.bookingEndDate=array.optJSONObject(i).optString("booking_end_date");
                                        parkerBookingList.bookingStartDate=array.optJSONObject(i).optString("booking_start_date");
                                        parkerBookingList.bookingStartTime=array.optJSONObject(i).optString("booking_start_time");
                                        parkerBookingList.bookingPrice=array.optJSONObject(i).optString("price");
                                        parkerBookingList.bookingPayment=array.optJSONObject(i).optString("isPayment");
                                        parkerBookingList.bookingPropertyTitle=array.optJSONObject(i).optJSONObject("property_details").optString("title");
                                        parkerBookingList.bookingStatus=array.optJSONObject(i).optString("booking_status");
                                        parkerBookingList.bookingProperttyImage=array.optJSONObject(i).optJSONObject("property_details").optString("img_1");
                                        parkerBookingList.bookingPropertyAddress=array.optJSONObject(i).optJSONObject("property_details").optString("address");

                                        parkerBookingList.propertID = array.getJSONObject(i).getJSONObject("property_details").getString("id");
                                        parkerBookingList.bookingPropertyDistance=array.optJSONObject(i).optJSONObject("property_details").optString("distance");
                                        parkerBookingList.bookingPropertyDuration=array.optJSONObject(i).optJSONObject("property_details").optString("duration");
                                        parkerBookingList.bookingPropertyParkingType=array.optJSONObject(i).optJSONObject("property_details").optString("parking_type");
                                        parkerBookingList.bookingPropertyAvailable=array.optJSONObject(i).optJSONObject("property_details").optString("available");
                                        parkerBookingList.bookingPropertyRating=array.optJSONObject(i).optJSONObject("property_details").optString("rating");


                                        parkerBookingList.owneruser.UserID=array.optJSONObject(i).optJSONObject("property_details").optJSONObject("owner_user").optInt("id")+"";
                                        parkerBookingList.owneruser.FullName=array.optJSONObject(i).optJSONObject("property_details").optJSONObject("owner_user").optString("name");
                                        parkerBookingList.owneruser.Email=array.optJSONObject(i).optJSONObject("property_details").optJSONObject("owner_user").optString("email");
                                        parkerBookingList.owneruser.UserType=array.optJSONObject(i).optJSONObject("property_details").optJSONObject("owner_user").optString("user_type");
                                        parkerBookingList.owneruser.FirstName=array.optJSONObject(i).optJSONObject("property_details").optJSONObject("owner_user").optString("first_name");
                                        parkerBookingList.owneruser.LastName=array.optJSONObject(i).optJSONObject("property_details").optJSONObject("owner_user").optString("last_name");
                                        parkerBookingList.owneruser.APIToken = array.optJSONObject(i).optJSONObject("property_details").optJSONObject("owner_user").optString("fcm_id");
                                        parkerBookingList.owneruser.DeviceToken = array.optJSONObject(i).optJSONObject("property_details").optJSONObject("owner_user").optString("device_token");

                                        parkerBookingList.user.UserID=array.optJSONObject(i).optJSONObject("parker_user").optInt("id")+"";
                                        parkerBookingList.user.FullName=array.optJSONObject(i).optJSONObject("parker_user").optString("name");
                                        parkerBookingList.user.Email=array.optJSONObject(i).optJSONObject("parker_user").optString("email");
                                        parkerBookingList.user.UserType=array.optJSONObject(i).optJSONObject("parker_user").optString("user_type");
                                        parkerBookingList.user.FirstName=array.optJSONObject(i).optJSONObject("parker_user").optString("first_name");
                                        parkerBookingList.user.LastName=array.optJSONObject(i).optJSONObject("parker_user").optString("last_name");
                                        parkerBookingList.user.APIToken=array.optJSONObject(i).optJSONObject("parker_user").optString("fcm_id");
                                        parkerBookingList.user.DeviceToken=array.optJSONObject(i).optJSONObject("parker_user").optString("device_token");

                                        parkerBookingList.bookingPropertyStayTitle=array.optJSONObject(i).optJSONObject("rates").optString("title");
                                        parkerBookingList.bookingPropertyStayTime=array.optJSONObject(i).optJSONObject("rates").optString("time");
                                        parkerBookingList.bookingPropertyStayPrice=array.optJSONObject(i).optJSONObject("rates").optString("price");

                                        bookingLists.add(parkerBookingList);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                                if(bookingLists.size()>0){
                                    rvBookings.setVisibility(View.VISIBLE);
                                    tvMsg.setVisibility(View.GONE);
                                }else{
                                    rvBookings.setVisibility(View.GONE);
                                    tvMsg.setVisibility(View.VISIBLE);
                                }
                            }
                            else {
                                if(jsonObject.getString("error_code").equalsIgnoreCase("10")){
                                    deleteUser();
                                }else
                                    Utility.showAlert(OwnerBookingProperty.this, jsonObject.getString("error_message").toString());
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
            if(bookingLists.size()>0){
                rvBookings.setVisibility(View.VISIBLE);
                tvMsg.setVisibility(View.GONE);
            }else{
                rvBookings.setVisibility(View.GONE);
                tvMsg.setVisibility(View.VISIBLE);
            }
            Utility.hideProgress();
        }
    };
    private void loadBookings(String filter) {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(OwnerBookingProperty.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.getBookingsList(WebUtility.GET_PROPERTY,PropertyID,filter);
                responseBodyCall.enqueue(callback);
            }
        } catch (Exception ex) {
        }
    }
}
