package com.driveway.Activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.driveway.Adapters.OwnerParkingAdapter;
import com.driveway.Adapters.ParkerParkingAdapter;
import com.driveway.Component.TTextView;
import com.driveway.Model.SearchPropertyModel;
import com.driveway.R;

import java.util.ArrayList;

public class ParkerDashboardScreenTwo extends BaseActivity implements View.OnClickListener {

    public RecyclerView RvParking;
    public TTextView tvNoPropertyFound;
    public AppCompatImageView Back;

    ParkerParkingAdapter  adapter;
    ArrayList<SearchPropertyModel> models=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parker_dashboardscreen_two);
        Back = findViewById(R.id.Back);
        tvNoPropertyFound = findViewById(R.id.tvNoPropertyFound);
        RvParking = findViewById(R.id.RvParking);

        models = (ArrayList<SearchPropertyModel>) getIntent().getSerializableExtra("alldata");
        if (models.size() > 0) {
            loadData();
            tvNoPropertyFound.setVisibility(View.GONE);
        }
        else {
            RvParking.setVisibility(View.GONE);
            tvNoPropertyFound.setVisibility(View.VISIBLE);
        }


        Back.setOnClickListener(this);
        orangeStatusBar();

    }
    private void loadData(){
        if(models.size()>0) {
            adapter = new ParkerParkingAdapter(ParkerDashboardScreenTwo.this, models);
            RvParking.setLayoutManager(new LinearLayoutManager(ParkerDashboardScreenTwo.this));
            RvParking.setAdapter(adapter);
            RvParking.setVisibility(View.VISIBLE);
            tvNoPropertyFound.setVisibility(View.GONE);
        }else{
            RvParking.setVisibility(View.GONE);
            tvNoPropertyFound.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Back:finish();break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
