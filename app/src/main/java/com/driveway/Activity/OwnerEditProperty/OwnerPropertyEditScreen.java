package com.driveway.Activity.OwnerEditProperty;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.driveway.Component.TTextView;
import com.driveway.DBHelper.tblPropertyDetail;
import com.driveway.DBHelper.tblPropertyImage;
import com.driveway.Fragment.OwnerPropertyAvailability;
import com.driveway.Fragment.OwnerPropertyDetailsFragment;
import com.driveway.Fragment.OwnerPropertyRates;
import com.driveway.Fragment.OwnerPropertyUploadPhotoFragment;
import com.driveway.Fragment.owner_edit_property.OwnerPropertyEditFragmentFour;
import com.driveway.Fragment.owner_edit_property.OwnerPropertyEditFragmentOne;
import com.driveway.Fragment.owner_edit_property.OwnerPropertyEditFragmentThree;
import com.driveway.Fragment.owner_edit_property.OwnerPropertyEditFragmentTwo;
import com.driveway.Model.ParkingSpace;
import com.driveway.R;
import com.driveway.Utility.DataContext;
import com.mobandme.ada.Entity;
import com.mobandme.ada.exceptions.AdaFrameworkException;

import java.util.ArrayList;
import java.util.List;

public class OwnerPropertyEditScreen extends FragmentActivity {


    ViewPager viewPager;
    FragmentPagerAdapter adapterViewPager;
    AppCompatImageView Back;
    TTextView PropertyDetails,Photo,Availability,Rates;


    DataContext dataContext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_property_addscreen);
        dataContext=new DataContext(this);
        bindComponent();
        getObjectData();
        bindViewPager();
        changeStatusbarColor();
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
                deletePropertyData();
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
                setTextColor(Rates,Availability,PropertyDetails,Photo);
                viewPager.setCurrentItem(3);
            }
        });
        Availability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextColor(Availability,PropertyDetails,Photo,Rates);
                viewPager.setCurrentItem(2);
            }
        });
        Photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextColor(Photo,PropertyDetails,Availability,Rates);
                viewPager.setCurrentItem(1);
            }
        });
    }

    public void getObjectData(){
        if(getIntent()!=null){
            if(getIntent().getSerializableExtra("editproperty")!=null){
                    //parkingSpace= (ParkingSpace) getIntent().getSerializableExtra("editproperty");

            }
        }
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
        deletePropertyData();
        finish();
    }

    private void deletePropertyData() {

        try {
            dataContext.shortStayObjectSet.fill();
            if(dataContext.shortStayObjectSet.size()>0) {
                for (int i=0;i<dataContext.shortStayObjectSet.size();i++)
                    dataContext.shortStayObjectSet.remove(i).setStatus(Entity.STATUS_DELETED);
                dataContext.shortStayObjectSet.save();
            }

            dataContext.propertyAvailableTimesObjectSet.fill();
            if(dataContext.propertyAvailableTimesObjectSet.size()>0) {
                for (int j=0;j<dataContext.propertyAvailableTimesObjectSet.size();j++)
                    dataContext.propertyAvailableTimesObjectSet.remove(j).setStatus(Entity.STATUS_DELETED);
                dataContext.propertyAvailableTimesObjectSet.save();
            }
            dataContext.propertyImageObjectSet.fill();
            if(dataContext.propertyImageObjectSet.size()>0) {
                for (int k=0;k<dataContext.propertyImageObjectSet.size();k++)
                    dataContext.propertyImageObjectSet.remove(k).setStatus(Entity.STATUS_DELETED);
                dataContext.propertyImageObjectSet.save();
            }
            dataContext.propertyDetailObjectSet.fill();
            if(dataContext.propertyDetailObjectSet.size()>0) {
                for (int l=0;l<dataContext.propertyDetailObjectSet.size();l++)
                    dataContext.propertyDetailObjectSet.remove(l).setStatus(Entity.STATUS_DELETED);
                dataContext.propertyDetailObjectSet.save();
            }

            dataContext.shortStayObjectSet.fill();
            if(dataContext.shortStayObjectSet.size()>0) {
                for (int m=0;m<dataContext.shortStayObjectSet.size();m++)
                    dataContext.shortStayObjectSet.remove(m).setStatus(Entity.STATUS_DELETED);

                dataContext.shortStayObjectSet.save();
            }
        } catch (AdaFrameworkException e) {
            e.printStackTrace();
        }

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
                case 0: return new OwnerPropertyEditFragmentOne();
                case 1: return new OwnerPropertyEditFragmentTwo();
                case 2: return new OwnerPropertyEditFragmentThree();
                case 3: return new OwnerPropertyEditFragmentFour();
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




}
