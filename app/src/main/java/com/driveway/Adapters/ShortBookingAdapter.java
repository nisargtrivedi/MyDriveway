package com.driveway.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.driveway.Model.HourTimeModel;
import com.driveway.R;
import com.driveway.Utility.AppPreferences;
import com.driveway.listeners.onBookingListener;

import java.util.List;

import static android.view.Gravity.CENTER;
import static android.view.Gravity.RIGHT;

public class ShortBookingAdapter extends  RecyclerView.Adapter<ShortBookingAdapter.ViewHolder> {


    public List<HourTimeModel> list;
    Context context;
    public String name;
    AppPreferences appPreferences;

    onBookingListener bookingListener;
    public void onBookingClick(onBookingListener book){
        bookingListener=book;
    }

    public ShortBookingAdapter(Context context, List<HourTimeModel> list) {
        this.context = context;
        this.list = list;
        appPreferences=new AppPreferences(context);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvHour, status,tv,line,tvHour1,status1;
        public LinearLayout ll3;
        public RelativeLayout rl;

        public ViewHolder(View view) {
            super(view);
            tvHour = view.findViewById(R.id.tvHour);
            status = view.findViewById(R.id.status);
            tv=view.findViewById(R.id.tv);
            line=view.findViewById(R.id.line);
            status1=view.findViewById(R.id.status1);
            tvHour1=view.findViewById(R.id.tvHour1);
            ll3=view.findViewById(R.id.ll3);
            rl=view.findViewById(R.id.rl);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.booking_time_row, parent, false);

        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // setFadeAnimation(holder.itemView);

        final HourTimeModel task = list.get(position);
        try {
            if(position==0) {
                holder.tv.setText("");
                holder.tvHour.setGravity(CENTER);
                holder.tvHour.setTextSize(16f);
                holder.tvHour.setText(task.HourStartTime.contains("AM")?task.HourStartTime.replaceAll("AM",""):task.HourStartTime.replaceAll("PM",""));
                holder.tvHour.setPadding(0,0,0,0);
            }else if(position%4==0) {
                holder.ll3.setVisibility(View.GONE);
                holder.tv.setText("");
                holder.tvHour.setGravity(CENTER);
                holder.tvHour.setTextSize(16f);
                holder.tvHour.setText(task.HourStartTime.contains("AM")?task.HourStartTime.replaceAll("AM",""):task.HourStartTime.replaceAll("PM",""));
                holder.tvHour.setPadding(0,0,0,0);
            }else if(position==list.size()){
                holder.ll3.setVisibility(View.VISIBLE);
                holder.tvHour1.setText(task.HourEndTime.contains("AM")?task.HourEndTime.replaceAll("AM",""):task.HourEndTime.replaceAll("PM",""));
                holder.tvHour.setTextSize(16f);
                holder.tvHour1.setGravity(CENTER);
                holder.tvHour.setGravity(CENTER|RIGHT);
                holder.tvHour.setTextSize(14f);
                holder.tvHour.setText(task.HourStartTime.contains("AM")?task.HourStartTime.replaceAll("AM",""):task.HourStartTime.replaceAll("PM",""));
                holder.tvHour.setPadding(0,0,0,0);
            }else{
                holder.ll3.setVisibility(View.GONE);
                holder.tv.setText("");
                holder.tvHour.setGravity(RIGHT|CENTER);
                holder.tvHour.setTextSize(14f);
                holder.tvHour.setPadding(0,0,5,0);
                holder.tvHour.setText(task.HourStartTime.contains("AM")?task.HourStartTime.replaceAll("AM",""):task.HourStartTime.replaceAll("PM",""));
            }
            if(task.IS_BOOKED==0){

                if(task.IS_SELECTED==0) {
                    holder.status.setBackgroundResource(R.drawable.booking_left_color);
                    holder.status1.setBackgroundResource(R.drawable.booking_left_color);

                    holder.status.setText("");
                    holder.status1.setText("");


                    holder.line.setBackgroundResource(R.drawable.booking_left_color_line);
                }else{
                    holder.status.setBackgroundResource(R.drawable.booking_selected_color);
                    holder.status1.setBackgroundResource(R.drawable.booking_selected_color);
                    holder.status.setText("");
                    holder.status1.setText("");
                    holder.line.setBackgroundResource(R.drawable.booking_selected_color);
                }
            }else{

                if(task.IS_SELECTED==0) {
                    holder.status.setBackgroundResource(R.drawable.booking_left_color_booked);
                    holder.status1.setBackgroundResource(R.drawable.booking_left_color_booked);

//                    holder.status.setText("BOOKED");
//                    holder.status1.setText("BOOKED");

                    if(task.in_time.equalsIgnoreCase("no")){
                        holder.status.setText("");
                        holder.status1.setText("");
                    }else {
                        holder.status.setText("BOOKED");
                        holder.status1.setText("BOOKED");
                    }

                    holder.line.setBackgroundResource(R.drawable.booking_left_color_booked_line);
                }else{
                    holder.status.setBackgroundResource(R.drawable.booking_selected_color);
                    holder.status1.setBackgroundResource(R.drawable.booking_selected_color);
                    holder.status.setText("");
                    holder.status1.setText("");
                    holder.line.setBackgroundResource(R.drawable.booking_selected_color);
                }


//                holder.status1.setText("BOOKED");
//                holder.status1.setBackgroundResource(R.drawable.booking_left_color_booked);
//                    holder.status.setBackgroundResource(R.drawable.booking_left_color_booked);
//                    holder.status.setText("BOOKED");
//                    holder.line.setBackgroundResource(R.drawable.booking_left_color_booked_line);
            }

            holder.rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bookingListener.onBooking(task);
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
