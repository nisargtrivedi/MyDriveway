package com.driveway.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.driveway.Activity.OwnerBookingDetail.BookingDetail_;
import com.driveway.Activity.ParkerBooking.ParkerBookingDetailScreen;
import com.driveway.Component.RoundCornersImageView;
import com.driveway.Component.TTextView;
import com.driveway.Model.ParkerBookingList;
import com.driveway.R;
import com.driveway.Utility.Constants;
import com.driveway.listeners.onGetProperty;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

public class OwnerBookingListAdapter extends  RecyclerView.Adapter<OwnerBookingListAdapter.ViewHolder> {


    public List<ParkerBookingList> list;
    Context context;
    public String name;
    public onGetProperty property;

    public OwnerBookingListAdapter(Context context, List<ParkerBookingList> list) {
        this.context = context;
        this.list = list;
    }

    public void getProperty(onGetProperty property){
        this.property=property;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TTextView UserCharacter,BookingTime,BookingDate,BookingAddress,amount,tvStatus,UserName;
        public RelativeLayout rlMain;
        public AppCompatImageView next;

        public ViewHolder(View view) {
            super(view);

            UserName=view.findViewById(R.id.UserName);
            UserCharacter=view.findViewById(R.id.UserCharacter);
            next = view.findViewById(R.id.next);
            amount = view.findViewById(R.id.amount);
            BookingAddress = view.findViewById(R.id.BookingAddress);
            BookingDate = view.findViewById(R.id.BookingDate);
            BookingTime = view.findViewById(R.id.BookingTime);
            tvStatus=view.findViewById(R.id.tvStatus);
            rlMain=view.findViewById(R.id.rlMain);


        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.owner_booking_row, parent, false);

        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // setFadeAnimation(holder.itemView);

        final ParkerBookingList task = list.get(position);
        try {
            if(task!=null) {
                if(task.bookingPayment.equalsIgnoreCase("0")){
                    holder.tvStatus.setText("Unpaid");
                }else
                    holder.tvStatus.setText(task.bookingStatus);

                holder.amount.setText("$" + task.bookingPropertyStayPrice);
                holder.BookingAddress.setText(task.bookingPropertyTitle);
                if (!task.bookingStartDate.isEmpty() && !task.bookingEndDate.isEmpty())
                    holder.BookingDate.setText(Constants.converDate(task.bookingStartDate) + "->" + Constants.converDate(task.bookingEndDate));

                holder.BookingTime.setText(task.bookingStartTime + " --- " + task.bookingEndTime);
                holder.UserName.setText(task.user.FullName);
                if(!task.user.FullName.isEmpty())
                    holder.UserCharacter.setText(task.user.FullName.charAt(0) + "");
            }
            holder.rlMain.setOnClickListener(v -> {
                context.startActivity(new Intent(context, BookingDetail_.class).putExtra("detail",task));
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
