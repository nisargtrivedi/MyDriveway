package com.driveway.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.driveway.Activity.ParkerBookingScreen;
import com.driveway.Activity.ParkerDashboardScreen;
import com.driveway.Component.BButton;
import com.driveway.Component.RoundedCornersTransform;
import com.driveway.Component.TTextView;
import com.driveway.DBHelper.tblCars;
import com.driveway.Model.SearchPropertyModel;
import com.driveway.R;
import com.driveway.listeners.onParkerDelete;
import com.driveway.listeners.onParkerEdit;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ParkerSearchPropertyAdapter extends  RecyclerView.Adapter<ParkerSearchPropertyAdapter.ViewHolder> {


    public List<SearchPropertyModel> list;
    Context context;
    public String name;

    public String PLACEID="";
    public String TEXT="";
    onParkerDelete onParkerDelete;
    onParkerEdit onParkerEdit;
    public ParkerSearchPropertyAdapter(Context context, List<SearchPropertyModel> list) {
        this.context = context;
        this.list = list;
    }

    public void onDeleteCars(onParkerDelete onParkerDelete){
        this.onParkerDelete=onParkerDelete;
    }
    public void onEditCar(onParkerEdit onParkerEdit){
        this.onParkerEdit=onParkerEdit;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView car_img,tvDelete;
        public TTextView tvTitle, tvKM,PropertyAddress,tvMinutes,tvAvailability,tvRatings,tvType;
        public AppCompatImageView tvEdit;
        public CardView BookingCard;
        public BButton btnBook;

        public ViewHolder(View view) {
            super(view);
            car_img = view.findViewById(R.id.car_img);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvKM = view.findViewById(R.id.tvKM);
            PropertyAddress = view.findViewById(R.id.PropertyAddress);

            tvMinutes = view.findViewById(R.id.tvMinutes);
            tvAvailability = view.findViewById(R.id.tvAvailability);

            tvRatings = view.findViewById(R.id.tvRatings);

            tvType = view.findViewById(R.id.tvType);
            BookingCard=view.findViewById(R.id.BookingCard);

            tvDelete=view.findViewById(R.id.tvDelete);
            tvEdit=view.findViewById(R.id.tvEdit);
            btnBook=view.findViewById(R.id.btnBook);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.parker_dashboard_row, parent, false);

        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // setFadeAnimation(holder.itemView);

        final SearchPropertyModel task = list.get(position);
        try {
            if(list.size()>0){
            if(task!=null){
                holder.tvTitle.setText(task.propertyTitle);
                holder.PropertyAddress.setText(task.propertyAddress);
                holder.tvKM.setText(task.propertyDistance);
                holder.tvAvailability.setText(task.propertyAvailability+" Available");
                holder.tvRatings.setText(task.propertyRating);
                holder.tvType.setText(task.propertyParkingType);
                holder.tvMinutes.setText(task.propertyDuration);
                if(!task.propertyImage.isEmpty()){
                    Picasso.with(context).load(task.propertyImage).into(holder.car_img);
                }

                holder.BookingCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, ParkerBookingScreen.class).putExtra("parking",task)
                        .putExtra("placeid",PLACEID)
                                .putExtra("text",TEXT)
                        );
                    }
                });
                holder.btnBook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, ParkerBookingScreen.class).putExtra("parking",task)
                                .putExtra("placeid",PLACEID)
                                .putExtra("text",TEXT)

                        );
                        PLACEID="";
                    }
                });
            }
            }
        }catch (Exception ex){

        }


    }

    @Override
    public int getItemCount() {
        return list.size()>0?list.size():0;
    }

    public void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        view.startAnimation(anim);
    }
}
