package com.driveway.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.driveway.Activity.OwnerBookingDetail.OwnerBookingProperty_;
import com.driveway.Activity.OwnerBookingDetail.BookingDetail_;
import com.driveway.Activity.OwnerPropertyDetailScreen;
import com.driveway.Adapters.OwnerBookedByAdapter;
import com.driveway.Component.TTextView;
import com.driveway.Model.BookedByModel;
import com.driveway.R;
import com.driveway.Utility.AppPreferences;
import com.driveway.Utility.DataContext;
import com.driveway.listeners.onGetBooking;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OwnerPropertyDetailFragmentOne extends Fragment implements View.OnClickListener {

    TTextView parkingtype,PropertyTitle, PropertyAddress, PropertySize, PropertyAvailability, tvAboutProperty, tvShowMore,tvMsg;
    RecyclerView rvUsers;
    OwnerBookedByAdapter adapter;
    List<BookedByModel> bookingLists = new ArrayList<>();
    AppPreferences appPreferences;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_property_detail_one, container, false);
        PropertyTitle = v.findViewById(R.id.PropertyTitle);
        PropertyAddress = v.findViewById(R.id.PropertyAddress);
        PropertySize = v.findViewById(R.id.PropertySize);
        PropertyAvailability = v.findViewById(R.id.PropertyAvailability);
        tvAboutProperty = v.findViewById(R.id.tvAboutProperty);
        rvUsers = v.findViewById(R.id.rvUsers);
        tvShowMore = v.findViewById(R.id.tvShowMore);
        tvMsg=v.findViewById(R.id.tvMsg);
        parkingtype=v.findViewById(R.id.parkingtype);
        tvShowMore.setOnClickListener(this);
        appPreferences = new AppPreferences(getActivity());
        tvShowMore.setOnClickListener(this);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        LogData();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindAdapter();


    }


    private void LogData() {

        if (OwnerPropertyDetailScreen.parkingSpace != null) {
            try {
                    PropertyTitle.setText(OwnerPropertyDetailScreen.parkingSpace.ParkingTitle);
                    PropertyAddress.setText(OwnerPropertyDetailScreen.parkingSpace.ParkingAddress);
                    PropertyAvailability.setText(OwnerPropertyDetailScreen.parkingSpace.ParkingAvailability+" Available");
                    PropertySize.setText(OwnerPropertyDetailScreen.parkingSpace.Width + "mm x " + OwnerPropertyDetailScreen.parkingSpace.Height + "mm");
                    tvAboutProperty.setText(OwnerPropertyDetailScreen.parkingSpace.AboutParking);
                    parkingtype.setText(OwnerPropertyDetailScreen.parkingSpace.ParkingTypes);

                loadBookings();


                adapter.setOnBooking(bookedByModel ->
                        startActivity(new Intent(getActivity(), BookingDetail_.class)
                        .putExtra("details",bookedByModel)
                        .putExtra("title",OwnerPropertyDetailScreen.parkingSpace.ParkingTitle)
                        .putExtra("address",OwnerPropertyDetailScreen.parkingSpace.ParkingAddress)
                        .putExtra("type",OwnerPropertyDetailScreen.parkingSpace.ParkingTypes)
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void bindAdapter() {
        adapter = new OwnerBookedByAdapter(getActivity(), bookingLists);
        rvUsers.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        rvUsers.setAdapter(adapter);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvShowMore:
                getActivity().startActivity(new Intent(getActivity(), OwnerBookingProperty_.class).
                        putExtra("data",OwnerPropertyDetailScreen.parkingSpace)
                );
                break;
        }
    }

    private void loadBookings() {
        try {
            bookingLists.clear();
            bookingLists.addAll(OwnerPropertyDetailScreen.parkingSpace.bookedByModels);
            adapter.notifyDataSetChanged();
            if(bookingLists.size()>0){
                tvMsg.setVisibility(View.GONE);
                rvUsers.setVisibility(View.VISIBLE);
            }else{
                tvMsg.setVisibility(View.VISIBLE);
                rvUsers.setVisibility(View.GONE);
            }

        } catch (Exception ex) {
        }
    }
}
