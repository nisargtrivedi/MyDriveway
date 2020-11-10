package com.driveway.Activity;

import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.driveway.Adapters.NotificationAdapter;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EActivity(R.layout.owner_notification)
public class NotificationActivity extends BaseActivity {

    @ViewById
    RecyclerView rvNotifications;
    @ViewById
    TTextView tvMsg;
    @ViewById
    SwipeRefreshLayout pullToRefresh;

    NotificationAdapter adapter;

    List<Notificationstbl> list=new ArrayList<>();
    @AfterViews
    public void init(){
        adapter=new NotificationAdapter(this,list);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvNotifications.setAdapter(adapter);

        whiteStatusBar();

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNotification(appPreferences.getString("USERID"));
                pullToRefresh.setRefreshing(false);
            }
        });
        try{
            getNotification(appPreferences.getString("USERID"));
            //getNotification("54");
        }catch (Exception ex){

        }
    }

    @Click
    public void back(){
        finish();
    }

    private void getNotification(String userID) {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(NotificationActivity.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.getNotification(WebUtility.NOTIFICATION, userID);
                responseBodyCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                if (jsonObject != null) {
                                    String message=jsonObject.getString("error_message");
                                    if (jsonObject.getString("error_code").equalsIgnoreCase("0")) {
                                        JSONArray array=jsonObject.getJSONArray("data");
                                        list.clear();
                                        for(int i=0;i<array.length();i++){
                                            if(array.optJSONObject(i)!=null) {
                                                Notificationstbl notificationstbl = new Notificationstbl();
                                                notificationstbl.NotificationID = array.optJSONObject(i).optString("id");
                                                notificationstbl.NotificationUserName = array.optJSONObject(i).optString("user_fullname");
                                                notificationstbl.NotificationDate = array.optJSONObject(i).optString("notification_date");
                                                notificationstbl.NotificationMessage = array.optJSONObject(i).optString("message");
                                                list.add(notificationstbl);
                                            }
                                        }
                                        adapter.notifyDataSetChanged();
                                        if(list.size()>0){
                                            tvMsg.setVisibility(View.GONE);
                                            rvNotifications.setVisibility(View.VISIBLE);
                                        }else{
                                            tvMsg.setVisibility(View.VISIBLE);
                                            rvNotifications.setVisibility(View.GONE);
                                        }
                                    }else{
                                        Utility.showAlert(NotificationActivity.this,message);
                                        if(list.size()>0){
                                            tvMsg.setVisibility(View.GONE);
                                            rvNotifications.setVisibility(View.VISIBLE);
                                        }else{
                                            tvMsg.setVisibility(View.VISIBLE);
                                            rvNotifications.setVisibility(View.GONE);
                                        }

                                        return;
                                    }


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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
