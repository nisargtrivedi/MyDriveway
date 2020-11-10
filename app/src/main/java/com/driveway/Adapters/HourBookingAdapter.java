package com.driveway.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.DBHelper.tblCars;
import com.driveway.Model.HourTimeModel;
import com.driveway.R;
import com.driveway.Utility.AppPreferences;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.driveway.listeners.onBookingListener;
import com.driveway.listeners.onParkerDelete;
import com.driveway.listeners.onParkerEdit;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HourBookingAdapter extends  RecyclerView.Adapter<HourBookingAdapter.ViewHolder> {


    public List<HourTimeModel> list;
    Context context;
    public String name;
    AppPreferences appPreferences;
    onBookingListener bookingListener;
    public HourBookingAdapter(Context context, List<HourTimeModel> list) {
        this.context = context;
        this.list = list;
        appPreferences=new AppPreferences(context);
    }
    public void onBookingClick(onBookingListener book){
        bookingListener=book;
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
            ll3=view.findViewById(R.id.ll3);
            rl=view.findViewById(R.id.rl);
            status1=view.findViewById(R.id.status1);
            tvHour1=view.findViewById(R.id.tvHour1);

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
            if(position==list.size()){
                holder.ll3.setVisibility(View.VISIBLE);
                holder.tvHour1.setText(task.HourEndTime.contains("AM")?task.HourEndTime.replaceAll("AM",""):task.HourEndTime.replaceAll("PM",""));
                //holder.tvHour.setText(task.HourEndTime);
                holder.tvHour.setText(task.HourStartTime.contains("AM")?task.HourStartTime.replaceAll("AM",""):task.HourStartTime.replaceAll("PM",""));
            }else{
                holder.ll3.setVisibility(View.GONE);
                //holder.tvHour.setText(task.HourStartTime);
                holder.tvHour.setText(task.HourStartTime.contains("AM")?task.HourStartTime.replaceAll("AM",""):task.HourStartTime.replaceAll("PM",""));
            }
            if(task.IS_BOOKED==0){

                if(task.IS_SELECTED==0)
                {
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
