package com.driveway.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.driveway.Activity.ParkerBookingScreen;
import com.driveway.Component.RoundCornersImageView;
import com.driveway.Component.RoundedCornersTransform;
import com.driveway.Component.TTextView;
import com.driveway.Model.ParkingSpace;
import com.driveway.Model.SearchPropertyModel;
import com.driveway.R;
import com.driveway.listeners.onGetProperty;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ParkerParkingAdapter extends  RecyclerView.Adapter<ParkerParkingAdapter.ViewHolder> {


    public List<SearchPropertyModel> list;
    Context context;
    public String name;
    public onGetProperty property;

    public ParkerParkingAdapter(Context context, List<SearchPropertyModel> list) {
        this.context = context;
        this.list = list;
    }

    public void getProperty(onGetProperty property){
        this.property=property;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public RoundCornersImageView imgParkingSpace;
        public TTextView tvTitle, tvAddress, tvAvailability,tvKM,tvMin,tvRatings;
        public LinearLayout llMain;

        public ViewHolder(View view) {
            super(view);
            imgParkingSpace = view.findViewById(R.id.imgParkingSpace);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvAddress = view.findViewById(R.id.tvAddress);
            tvAvailability = view.findViewById(R.id.tvAvailability);
            tvRatings=view.findViewById(R.id.tvRatings);
            tvKM=view.findViewById(R.id.tvKM);
            tvMin=view.findViewById(R.id.tvMin);
            llMain=view.findViewById(R.id.llMain);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.parker_dashboard_parking_list_row, parent, false);

        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // setFadeAnimation(holder.itemView);

        final SearchPropertyModel task = list.get(position);
        try {
            holder.tvTitle.setText(task.propertyTitle);
            holder.tvAddress.setText(task.propertyAddress);
            holder.tvAvailability.setText(task.propertyAvailability.isEmpty()?"0% Available":task.propertyAvailability+" Available");
            if(!task.propertyImage.isEmpty())
                Picasso.with(context).load(task.propertyImage).fit().transform(new RoundedCornersTransform(50,0, RoundedCornersTransform.CornerType.ALL)).into(holder.imgParkingSpace);
            holder.tvRatings.setText(task.propertyRating+"");
            holder.tvKM.setText(task.propertyDistance+"");
            holder.tvMin.setText(task.propertyDuration);

            holder.llMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, ParkerBookingScreen.class).putExtra("parking",task));
                }
            });

        }catch (Exception ex){

        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        view.startAnimation(anim);
    }
}
