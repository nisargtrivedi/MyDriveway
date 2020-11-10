package com.driveway.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.driveway.Activity.ParkerBooking.ParkerBookingDetailScreen;
import com.driveway.Activity.ParkerBookingStayConfirmScreen;
import com.driveway.Activity.ParkerDashboardScreen;
import com.driveway.Component.BButton;
import com.driveway.Component.RoundCornersImageView;
import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.Model.ParkerBookingList;
import com.driveway.R;
import com.driveway.Utility.AppPreferences;
import com.driveway.Utility.Constants;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.driveway.listeners.onGetProperty;
import com.driveway.listeners.onParkkerBookingClick;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParkerBookingListAdapter extends  RecyclerView.Adapter<ParkerBookingListAdapter.ViewHolder> {


    public List<ParkerBookingList> list;
    Context context;
    public String name;
    public onParkkerBookingClick property;
    AppPreferences appPreferences;

    public ParkerBookingListAdapter(Context context, List<ParkerBookingList> list) {
        this.context = context;
        this.list = list;
        appPreferences=new AppPreferences(context);
    }

    public void onBookingClick(onParkkerBookingClick property){
        this.property=property;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TTextView tvStatDate,tvTitle,tvPrice,tvStayTime,tvStatus;
        public RoundCornersImageView imgParking;
        public LinearLayout rlMain;
        public ImageView btnStar;

        public ViewHolder(View view) {
            super(view);
            imgParking=view.findViewById(R.id.imgParking);
            tvStatus = view.findViewById(R.id.tvStatus);
            tvStayTime = view.findViewById(R.id.tvStayTime);
            tvPrice = view.findViewById(R.id.tvPrice);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvStatDate = view.findViewById(R.id.tvStatDate);
            rlMain=view.findViewById(R.id.rlMain);
            btnStar=view.findViewById(R.id.btnStar);


        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.parker_booking_row, parent, false);

        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // setFadeAnimation(holder.itemView);

        final ParkerBookingList task = list.get(position);
        try {
            if(task.bookingPayment.equalsIgnoreCase("0")){
                holder.tvStatus.setText("Unpaid");
                holder.btnStar.setVisibility(View.INVISIBLE);
            }else {
                if(task.isRated.equalsIgnoreCase("0") && task.bookingStatus.equalsIgnoreCase("completed")){
                    holder.btnStar.setVisibility(View.VISIBLE);
                }else{
                    holder.btnStar.setVisibility(View.INVISIBLE);
                }
                holder.tvStatus.setText(task.bookingStatus);
            }

//            holder.tvStatus.setText(task.bookingStatus);
            holder.tvPrice.setText("$ "+task.bookingPropertyStayPrice);
            holder.tvStatDate.setText(Constants.converDate(task.bookingStartDate) + " -> "+Constants.converDate(task.bookingEndDate));
            holder.tvStayTime.setText(task.bookingStartTime + " -- "+task.bookingEndTime);
            holder.tvTitle.setText(task.bookingPropertyTitle);
            if(task.bookingProperttyImage!=null && !task.bookingProperttyImage.isEmpty())
                Picasso.with(context).load(task.bookingProperttyImage).into(holder.imgParking);
            else
                Picasso.with(context).load(R.drawable.car_img).into(holder.imgParking);

            holder.rlMain.setOnClickListener(v -> {
                property.onClick(task);
            });

            holder.btnStar.setOnClickListener(v -> {
                RatingDialog(task.propertID,position);
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

    private void RatingDialog(String pid,int position) {

        AtomicInteger i = new AtomicInteger();
        View v = LayoutInflater.from(context).inflate(R.layout.rating_dialog, null, false);
        AlertDialog dialog = new AlertDialog.Builder(context).setView(v).create();
        AppCompatImageView rateOne, rateTwo, rateThree, rateFour, rateFive;
        BButton btnRate;
        rateOne = v.findViewById(R.id.rateOne);
        rateTwo = v.findViewById(R.id.rateTwo);
        rateThree = v.findViewById(R.id.rateThree);
        rateFour = v.findViewById(R.id.rateFour);
        rateFive = v.findViewById(R.id.rateFive);
        btnRate = v.findViewById(R.id.btnRate);

        rateOne.setOnClickListener(v1 -> {
            i.set(1);
            rateOne.setImageResource(R.drawable.ic_rate_select);
        });


        rateTwo.setOnClickListener(v12 -> {

            i.set(2);
            rateOne.setImageResource(R.drawable.ic_rate_select);
            rateTwo.setImageResource(R.drawable.ic_rate_select);
        });

        rateThree.setOnClickListener(v13 ->
        {
            i.set(3);
            rateOne.setImageResource(R.drawable.ic_rate_select);
            rateTwo.setImageResource(R.drawable.ic_rate_select);
            rateThree.setImageResource(R.drawable.ic_rate_select);
        });

        rateFour.setOnClickListener(v14 ->
                {
                    i.set(4);
                    rateOne.setImageResource(R.drawable.ic_rate_select);
                    rateTwo.setImageResource(R.drawable.ic_rate_select);
                    rateThree.setImageResource(R.drawable.ic_rate_select);
                    rateFour.setImageResource(R.drawable.ic_rate_select);
                }
        );

        rateFive.setOnClickListener(v15 ->
        {
            i.set(5);
            rateOne.setImageResource(R.drawable.ic_rate_select);
            rateTwo.setImageResource(R.drawable.ic_rate_select);
            rateThree.setImageResource(R.drawable.ic_rate_select);
            rateFour.setImageResource(R.drawable.ic_rate_select);
            rateFive.setImageResource(R.drawable.ic_rate_select);
        });

        btnRate.setOnClickListener(v16 -> {

            ratingAPI(pid, i.get(), dialog,position);
        });
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        dialog.show();

    }

    private void ratingAPI(String propertyID, int rating, AlertDialog dialog,int position) {

        Utility.showProgress(context);
        try {
            if (!Utility.isNetworkAvailable(context)) {
                //Utility.showAlert(context, "please check internet connection");
                Toast toast = Toast.makeText(context,"please check internet connection", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.rating(WebUtility.PROPERTY_RATING, propertyID, appPreferences.getString("USERID"), rating + "");
                responseBodyCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            try {
                                if (response.body() != null) {
                                    JSONObject jsonObject = new JSONObject(response.body().toString());
                                    if (jsonObject != null) {
                                        dialog.dismiss();
                                        list.get(position).isRated="1";
                                        notifyDataSetChanged();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Utility.hideProgress();
                    }
                });
            }
        } catch (Exception ex) {
        }
    }
}
