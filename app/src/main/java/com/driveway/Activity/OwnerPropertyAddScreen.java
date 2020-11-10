package com.driveway.Activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.DBHelper.tblPropertyDetail;
import com.driveway.DBHelper.tblPropertyImage;
import com.driveway.Fragment.OwnerPropertyAvailability;
import com.driveway.Fragment.OwnerPropertyDetailsFragment;
import com.driveway.Fragment.OwnerPropertyRates;
import com.driveway.Fragment.OwnerPropertyUploadPhotoFragment;
import com.driveway.R;
import com.driveway.Utility.DataContext;
import com.mobandme.ada.Entity;
import com.mobandme.ada.exceptions.AdaFrameworkException;

import java.util.ArrayList;
import java.util.List;

public class OwnerPropertyAddScreen extends FragmentActivity {


    ViewPager viewPager;
    FragmentPagerAdapter adapterViewPager;
    AppCompatImageView Back;
    TTextView PropertyDetails,Photo,Availability,Rates;
    DataContext dataContext;

    public tblPropertyDetail propertyDetail=new tblPropertyDetail();
    public List<tblPropertyImage> list=new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_property_addscreen);
        bindComponent();
        bindViewPager();
        dataContext=new DataContext(this);

        changeStatusbarColor();
        deletePropertyData();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void changeStatusbarColor(){
        Window window = getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.button_color));
    }
    private void bindComponent(){
        viewPager=findViewById(R.id.viewPager);
        Back=findViewById(R.id.Back);
        Rates=findViewById(R.id.Rates);
        Availability=findViewById(R.id.Availability);
        Photo=findViewById(R.id.Photo);
        PropertyDetails=findViewById(R.id.PropertyDetails);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
        PropertyDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextColor(PropertyDetails,Photo,Availability,Rates);
                viewPager.setCurrentItem(0);
            }
        });
        Rates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dataContext.propertyAvailableTimesObjectSet.fill();
                    if(TextUtils.isEmpty(OwnerPropertyDetailsFragment.edtPropertyName.getText().toString().trim())){
                        Utility.showAlert(OwnerPropertyAddScreen.this,"Please enter property name");
                    }else if(TextUtils.isEmpty(OwnerPropertyDetailsFragment.edtSearch.getText().toString().trim())){
                        Utility.showAlert(OwnerPropertyAddScreen.this,"Please enter property address");
                    }else if(TextUtils.isEmpty(OwnerPropertyDetailsFragment.edtHeight.getText().toString().trim())){
                        Utility.showAlert(OwnerPropertyAddScreen.this,"Please enter property long area");
                    }else if(TextUtils.isEmpty(OwnerPropertyDetailsFragment.edtWidth.getText().toString().trim())){
                        Utility.showAlert(OwnerPropertyAddScreen.this,"Please enter property wide area");
                    }else if(TextUtils.isEmpty(OwnerPropertyDetailsFragment.edtMaxCars.getText().toString().trim())){
                        Utility.showAlert(OwnerPropertyAddScreen.this,"Please select maximum cars");
                    }else if(Integer.parseInt(OwnerPropertyDetailsFragment.edtMaxCars.getText().toString().trim())==0){
                        Utility.showAlert(OwnerPropertyAddScreen.this,"Enter car greater than zero");
                    }else if(TextUtils.isEmpty(OwnerPropertyDetailsFragment.edtAboutProperty.getText().toString().trim())){
                        Utility.showAlert(OwnerPropertyAddScreen.this,"Please enter about property");
                    }else if(TextUtils.isEmpty(propertyDetail.PropertyParkingType)){
                        Utility.showAlert(OwnerPropertyAddScreen.this,"Please select property type");
                    }
                    else if(list.size()==0){
                        Utility.showAlert(OwnerPropertyAddScreen.this,"Please select at least 1 property photo");
                    }else if(dataContext.propertyAvailableTimesObjectSet.size()<=0){
                        Utility.showAlert(OwnerPropertyAddScreen.this,"Please choose any time for availability.");
                    }else{
                        setTextColor(Rates,Availability,PropertyDetails,Photo);
                        viewPager.setCurrentItem(3);
                    }
                }catch (Exception ex){

                }

            }
        });
        Availability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(OwnerPropertyDetailsFragment.edtPropertyName.getText().toString().trim())){
                    Utility.showAlert(OwnerPropertyAddScreen.this,"Please enter property name");
                }else if(TextUtils.isEmpty(OwnerPropertyDetailsFragment.edtSearch.getText().toString().trim())){
                    Utility.showAlert(OwnerPropertyAddScreen.this,"Please enter property address");
                }else if(TextUtils.isEmpty(OwnerPropertyDetailsFragment.edtHeight.getText().toString().trim())){
                    Utility.showAlert(OwnerPropertyAddScreen.this,"Please enter property long area");
                }else if(TextUtils.isEmpty(OwnerPropertyDetailsFragment.edtWidth.getText().toString().trim())){
                    Utility.showAlert(OwnerPropertyAddScreen.this,"Please enter property wide area");
                }else if(TextUtils.isEmpty(OwnerPropertyDetailsFragment.edtMaxCars.getText().toString().trim())){
                    Utility.showAlert(OwnerPropertyAddScreen.this,"Please select maximum cars");
                }else if(Integer.parseInt(OwnerPropertyDetailsFragment.edtMaxCars.getText().toString().trim())==0){
                    Utility.showAlert(OwnerPropertyAddScreen.this,"Enter car greater than zero");
                }else if(TextUtils.isEmpty(OwnerPropertyDetailsFragment.edtAboutProperty.getText().toString().trim())){
                    Utility.showAlert(OwnerPropertyAddScreen.this,"Please enter about property");
                }else if(TextUtils.isEmpty(propertyDetail.PropertyParkingType)){
                    Utility.showAlert(OwnerPropertyAddScreen.this,"Please select property type");
                }
                else if(list.size()==0){
                    Utility.showAlert(OwnerPropertyAddScreen.this,"Please select at least 1 property photo");
                }else {
                    setTextColor(Availability, PropertyDetails, Photo, Rates);
                    viewPager.setCurrentItem(2);
                }
            }
        });
        Photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(OwnerPropertyDetailsFragment.edtPropertyName.getText().toString().trim())){
                    Utility.showAlert(OwnerPropertyAddScreen.this,"Please enter property name");
                }else if(TextUtils.isEmpty(OwnerPropertyDetailsFragment.edtSearch.getText().toString().trim())){
                    Utility.showAlert(OwnerPropertyAddScreen.this,"Please enter property address");
                }else if(TextUtils.isEmpty(OwnerPropertyDetailsFragment.edtHeight.getText().toString().trim())){
                    Utility.showAlert(OwnerPropertyAddScreen.this,"Please enter property long area");
                }else if(TextUtils.isEmpty(OwnerPropertyDetailsFragment.edtWidth.getText().toString().trim())){
                    Utility.showAlert(OwnerPropertyAddScreen.this,"Please enter property wide area");
                }else if(TextUtils.isEmpty(OwnerPropertyDetailsFragment.edtMaxCars.getText().toString().trim())){
                    Utility.showAlert(OwnerPropertyAddScreen.this,"Please select maximum cars");
                }else if(Integer.parseInt(OwnerPropertyDetailsFragment.edtMaxCars.getText().toString().trim())==0){
                    Utility.showAlert(OwnerPropertyAddScreen.this,"Enter car greater than zero");
                }else if(TextUtils.isEmpty(OwnerPropertyDetailsFragment.edtAboutProperty.getText().toString().trim())){
                    Utility.showAlert(OwnerPropertyAddScreen.this,"Please enter about property");
                }else if(TextUtils.isEmpty(propertyDetail.PropertyParkingType)){
                    Utility.showAlert(OwnerPropertyAddScreen.this,"Please select property type");
                }else {
                    setTextColor(Photo, PropertyDetails, Availability, Rates);
                    viewPager.setCurrentItem(1);
                }
            }
        });
    }

    public void navigation(int page){
        viewPager.setCurrentItem(page);
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

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK);
        finish();
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
                    return new OwnerPropertyDetailsFragment();
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return new OwnerPropertyUploadPhotoFragment();
                case 2: // Fragment # 1 - This will show SecondFragment
                    return new OwnerPropertyAvailability();
                case 3: // Fragment # 1 - This will show SecondFragment
                    return new OwnerPropertyRates();
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



    private void deletePropertyData() {
        ((Runnable) () -> {
            try {
                dataContext.deleteAllTablesRecord();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).run();

    }

    @Override
    protected void onDestroy() {
        setResult(Activity.RESULT_OK);
        deletePropertyData();
        super.onDestroy();
    }
}
