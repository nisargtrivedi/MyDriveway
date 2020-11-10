package com.driveway.Activity;

import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.driveway.Adapters.HowAppWorkAdapter;
import com.driveway.Component.Utility;
import com.driveway.Model.HowAppWorkModel;
import com.driveway.Model.tblAffiliation;
import com.driveway.R;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EActivity(R.layout.how_app_work)
public class ActivityHowAppWork extends BaseActivity {

    @ViewById
    ViewPager myviewpager;
    @ViewById
    com.tbuonomo.viewpagerdotsindicator.DotsIndicator  dots_indicator;
    @ViewById
    TextView btnSkip;

    ArrayList<HowAppWorkModel> list=new ArrayList<>();
    HowAppWorkAdapter adapter;
    int currentPage=0;
    @AfterViews
    public void init(){

        whiteStatusBar();

        adapter=new HowAppWorkAdapter(this,list);
        myviewpager.setAdapter(adapter);

        listData();
        dots_indicator.setViewPager(myviewpager);
    }

    @Click
    public void btnSkip(){
//       if(currentPage<myviewpager.getAdapter().getCount()){
//           currentPage++;
//            myviewpager.setCurrentItem(currentPage);
//       }else if(currentPage==myviewpager.getAdapter().getCount()){
//            currentPage=0;
//            myviewpager.setCurrentItem(currentPage);
//        }
        finish();

    }
    private void listData() {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(ActivityHowAppWork.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.howAppWork(WebUtility.HOW_APP_WORK);
                responseBodyCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());

                                if(jsonObject.optInt("error_code")==0){

                                    JSONArray array=jsonObject.optJSONArray("data");
                                    if(array.length()>0) {
                                        for (int i = 0; i < array.length(); i++) {
                                            HowAppWorkModel model=new HowAppWorkModel();
                                            model.Name = array.optJSONObject(i).optString("content");
                                            list.add(model);
                                        }
                                    }
                                    adapter.notifyDataSetChanged();
                                }else{
                                    if(jsonObject.optInt("error_code")==10){
                                        deleteUser();
                                    }
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
    @Click
    public void back(){
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
