package com.driveway.Activity;

import android.text.Html;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.driveway.Adapters.AffiliationAdapter;
import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.Model.tblAffiliation;
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

@EActivity(R.layout.affiliation)
public class AffiliationActivity extends BaseActivity{

    @ViewById
    TTextView tvMsg;
    @ViewById
    TTextView Title;
    @ViewById
    RecyclerView rvAffiliations;
    AffiliationAdapter adapter;
    List<tblAffiliation> list=new ArrayList<>();

    @AfterViews
    public void init(){

        whiteStatusBar();

        adapter=new AffiliationAdapter(this,list);
        rvAffiliations.setLayoutManager(new GridLayoutManager(this,2));
        rvAffiliations.setAdapter(adapter);
        listData();
    }
    private void listData() {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(AffiliationActivity.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.affiliation(WebUtility.GET_AFFILIATION);
                responseBodyCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                System.out.println("RESPONSE IS=====" + response.body().toString());
                                if (jsonObject != null) {
                                    String message = jsonObject.getString("error_message");
                                    JSONArray array = jsonObject.getJSONArray("data");
                                    for(int i=0;i<array.length();i++){
                                        tblAffiliation affiliation=new tblAffiliation();
                                        affiliation.AffiliationID=array.getJSONObject(i).getString("id");
                                        affiliation.CompanyName=array.getJSONObject(i).getString("company_name");
                                        affiliation.ClientName=array.getJSONObject(i).getString("client_name");
                                        affiliation.Email=array.getJSONObject(i).getString("email");
                                        affiliation.WebURL=array.getJSONObject(i).getString("web_url");
                                        affiliation.Phone=array.getJSONObject(i).getString("phone_number");
                                        affiliation.Image=array.getJSONObject(i).getString("affilitation_img");
                                        list.add(affiliation);
                                    }
                                    if(list.size()>0)
                                    {
                                        tvMsg.setVisibility(View.GONE);
                                        rvAffiliations.setVisibility(View.VISIBLE);
                                    }else{
                                        tvMsg.setVisibility(View.VISIBLE);
                                        rvAffiliations.setVisibility(View.GONE);
                                    }
                                }
                                adapter.notifyDataSetChanged();


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

    @Click
    public void back(){
        finish();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
