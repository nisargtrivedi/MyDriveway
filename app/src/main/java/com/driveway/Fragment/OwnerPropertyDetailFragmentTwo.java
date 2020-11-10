package com.driveway.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.driveway.Activity.OwnerPropertyDetailScreen;
import com.driveway.Adapters.OwnerParkingImageAdapter;
import com.driveway.Component.TTextView;
import com.driveway.DBHelper.tblPropertyImage;
import com.driveway.R;
import com.driveway.Utility.DataContext;
import com.driveway.listeners.onImageDelete;
import com.mobandme.ada.exceptions.AdaFrameworkException;

import java.util.ArrayList;
import java.util.List;

public class OwnerPropertyDetailFragmentTwo extends Fragment {

    RecyclerView rvImage;
    DataContext dataContext;
    OwnerParkingImageAdapter adapter;


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
        View v= inflater.inflate(R.layout.owner_propertydetail_fragment_two, container, false);
        rvImage=v.findViewById(R.id.rvImage);
        dataContext=new DataContext(getActivity());


        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        LogData();
    }

    private void LogData(){
        if(OwnerPropertyDetailScreen.parkingSpace!=null) {

            try {
                ArrayList<tblPropertyImage> images=new ArrayList<>();
                if(!OwnerPropertyDetailScreen.parkingSpace.ParkingImage.isEmpty())
                    images.add(new tblPropertyImage("online",OwnerPropertyDetailScreen.parkingSpace.ParkingImage));

                if(!OwnerPropertyDetailScreen.parkingSpace.ParkingImage_Two.isEmpty())
                    images.add(new tblPropertyImage("online",OwnerPropertyDetailScreen.parkingSpace.ParkingImage_Two));


                adapter=new OwnerParkingImageAdapter(getActivity(), images, new onImageDelete() {
                    @Override
                    public void onDelete(tblPropertyImage image) {

                    }

                    @Override
                    public void onEdit(tblPropertyImage image) {

                    }
                });
                rvImage.setLayoutManager(new LinearLayoutManager(getActivity()));
                rvImage.setAdapter(adapter);

               


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
