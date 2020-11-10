package com.driveway.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.driveway.Activity.OwnerBookingDetail.ConversationActivity_;
import com.driveway.Activity.OwnerEditProperty.OwnerPropertyEditScreen;
import com.driveway.Adapters.CalendarDayAdapter;
import com.driveway.Adapters.CalendarPageAdapter;
import com.driveway.Component.RoundedCornersTransform;
import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.DBHelper.tblPropertyImage;
import com.driveway.Fragment.OwnerPropertyDetailFragmentOne;
import com.driveway.Fragment.OwnerPropertyDetailFragmentFour;
import com.driveway.Fragment.OwnerPropertyDetailFragmentThree;
import com.driveway.Fragment.OwnerPropertyDetailFragmentTwo;
import com.driveway.Model.ParkingSpace;
import com.driveway.R;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.google.gson.JsonObject;
import com.mobandme.ada.Entity;
import com.mobandme.ada.exceptions.AdaFrameworkException;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerPropertyDetailScreen extends BaseActivity {

    RelativeLayout rlBg;
    TTextView PropertyDetails,Photo,Availability,Rates,tvPropertyTitle;
    ViewPager viewPager;
    FragmentPagerAdapter adapterViewPager;
    AppCompatImageView back,Report,imgEditProperty,imgDelete,imgConversation;

    public static ParkingSpace parkingSpace;
    public static List<tblPropertyImage> list=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_property_detail);
        rlBg=findViewById(R.id.rlBg);
        Rates=findViewById(R.id.Rates);
        Availability=findViewById(R.id.Availability);
        Photo=findViewById(R.id.Photo);
        PropertyDetails=findViewById(R.id.PropertyDetails);
        viewPager=findViewById(R.id.viewPager);
        tvPropertyTitle=findViewById(R.id.tvPropertyTitle);
        back=findViewById(R.id.back);
        Report=findViewById(R.id.Report);
        imgEditProperty=findViewById(R.id.imgEditProperty);
        imgDelete=findViewById(R.id.imgDelete);
        imgConversation=findViewById(R.id.imgConversation);

        bindProperty();

        new Handler().postDelayed(() -> {
        if(parkingSpace!=null && !parkingSpace.ParkingImage.isEmpty()) {
            Picasso.with(this).load(parkingSpace.ParkingImage).into(new Target() {

                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    rlBg.setBackground(new BitmapDrawable(getResources(), bitmap));
                }

                @Override
                public void onBitmapFailed(final Drawable errorDrawable) {
                    Log.d("TAG", "FAILED");
                }

                @Override
                public void onPrepareLoad(final Drawable placeHolderDrawable) {
                    Log.d("TAG", "Prepare Load");
                    //rlBg.setBackground(placeHolderDrawable);
                }
            });
        }

        }, 200);


        imgConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OwnerPropertyDetailScreen.this, ConversationActivity_.class)
                                .putExtra("title",parkingSpace.ParkingTitle)
                                .putExtra("propertyid",parkingSpace.ParkingID)
                );
            }
        });
        imgDelete.setOnClickListener(v -> new AlertDialog.Builder(OwnerPropertyDetailScreen.this)
                .setMessage("Do you really want to delete this property ?")
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {

                    if(dataContext!=null && parkingSpace!=null){
                        try {
                            dataContext.parkingSpaceObjectSet.fill("id = ?",new String[]{parkingSpace.getID()+""},null);
                            if(dataContext.parkingSpaceObjectSet.size()>0){
                                for(int i=0;i<dataContext.parkingSpaceObjectSet.size();i++)
                                    dataContext.parkingSpaceObjectSet.get(i).setStatus(Entity.STATUS_DELETED);
                                dataContext.parkingSpaceObjectSet.save();
                            }
                            deletePropertyData(parkingSpace.ParkingID);
                        } catch (AdaFrameworkException e) {
                            e.printStackTrace();
                        }
                    }

                })
                .setNegativeButton(android.R.string.no, null).show());

        PropertyDetails.setOnClickListener(v -> {
            setTextColor(PropertyDetails,Photo,Availability,Rates);
            viewPager.setCurrentItem(0);
        });
        Rates.setOnClickListener(v -> {
            setTextColor(Rates,Availability,PropertyDetails,Photo);
            viewPager.setCurrentItem(3);
        });
        Availability.setOnClickListener(v -> {
            setTextColor(Availability,PropertyDetails,Photo,Rates);
            viewPager.setCurrentItem(2);
        });
        Photo.setOnClickListener(v -> {
            setTextColor(Photo,PropertyDetails,Availability,Rates);
            viewPager.setCurrentItem(1);
        });

        back.setOnClickListener(v -> {setResult(Activity.RESULT_OK);finish();});
        Report.setOnClickListener(v -> startActivity(new Intent(OwnerPropertyDetailScreen.this,ReportScreen.class)));

        imgEditProperty.setOnClickListener(v -> {
            startActivity(new Intent(OwnerPropertyDetailScreen.this, OwnerPropertyEditScreen.class).putExtra("editproperty",parkingSpace));
        });
        bindViewPager();
        transparentStatusbar();
        //hideStatusbar();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void bindProperty(){
        if(getIntent()!=null) {
            parkingSpace = (ParkingSpace) getIntent().getSerializableExtra("property");
        }
    }

    @Override
    protected void onResume() {
        if (OwnerPropertyDetailScreen.parkingSpace != null) {
            tvPropertyTitle.setText(OwnerPropertyDetailScreen.parkingSpace.ParkingTitle);
        }
        super.onResume();
        CalendarDayAdapter.i=0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CalendarDayAdapter.i=0;
        finish();
    }

    private void deletePropertyData(String ID){
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            }else {
                Utility.showProgress(OwnerPropertyDetailScreen.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.deleteProperty(WebUtility.DELETEPROPERTY, appPreferences.getString("USERID"),ID);
                responseBodyCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            try {
                                if (response.body() != null) {
                                    JSONObject jsonObject=new JSONObject(response.body().toString());
                                    if(jsonObject!=null){
                                        if(jsonObject.getString("error_code").equalsIgnoreCase("0")){
                                            setResult(Activity.RESULT_OK);
                                            Utility.showAlertwithFinish(OwnerPropertyDetailScreen.this, jsonObject.getString("error_message").toString());
                                        }else {
                                            Utility.showAlert(OwnerPropertyDetailScreen.this, jsonObject.getString("error_message").toString());
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
        }catch (Exception ex){
            Utility.hideProgress();
        }
    }
    private void setTextColor(TTextView t1,TTextView t2,TTextView t3,TTextView t4){
        t1.setTextColor(getResources().getColor(R.color.selected_property_color));
        t1.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,R.drawable.round_selected_property);
        t2.setTextColor(getResources().getColor(R.color.property_color));
        t2.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0);
        t3.setTextColor(getResources().getColor(R.color.property_color));
        t3.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0);
        t4.setTextColor(getResources().getColor(R.color.property_color));
        t4.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0);
    }

    private void bindViewPager(){
        try{
            adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(adapterViewPager);
            setTextColor(PropertyDetails,Photo,Availability,Rates);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                // This method will be invoked when a new page becomes selected.
                @Override
                public void onPageSelected(int position) {
                    switch (position){
                        case 0:setTextColor(PropertyDetails,Photo,Availability,Rates);break;
                        case 1:setTextColor(Photo,PropertyDetails,Availability,Rates);break;
                        case 2:setTextColor(Availability,PropertyDetails,Photo,Rates);break;
                        case 3:setTextColor(Rates,Availability,PropertyDetails,Photo);break;
                    }
                }

                // This method will be invoked when the current page is scrolled
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    // Code goes here
                }

                // Called when the scroll state changes:
                // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
                @Override
                public void onPageScrollStateChanged(int state) {
                    // Code goes here
                }
            });
        }catch (Exception ex){}
    }
    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 4;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return new OwnerPropertyDetailFragmentOne();
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return new OwnerPropertyDetailFragmentTwo();
                case 2: // Fragment # 1 - This will show SecondFragment
                    return new OwnerPropertyDetailFragmentThree();
                case 3: // Fragment # 1 - This will show SecondFragment
                    return new OwnerPropertyDetailFragmentFour();
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK);
        finish();
    }
}
