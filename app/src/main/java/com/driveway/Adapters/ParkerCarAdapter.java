package com.driveway.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.driveway.Activity.ParkerProfileScreen;
import com.driveway.Component.RoundedCornersTransform;
import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.DBHelper.tblCars;
import com.driveway.Model.ParkingSpace;
import com.driveway.R;
import com.driveway.Utility.AppPreferences;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.driveway.listeners.onCarClick;
import com.driveway.listeners.onParkerDelete;
import com.driveway.listeners.onParkerEdit;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParkerCarAdapter extends  RecyclerView.Adapter<ParkerCarAdapter.ViewHolder> {


    public List<tblCars> list;
    Context context;
    public String name;

    onParkerDelete onParkerDelete;
    onParkerEdit onParkerEdit;
    AppPreferences appPreferences;

    onCarClick carClick;

    public void onCar(onCarClick carClick){
        this.carClick=carClick;
    }
    public ParkerCarAdapter(Context context, List<tblCars> list) {
        this.context = context;
        this.list = list;
        appPreferences=new AppPreferences(context);
    }

    public void onDeleteCars(onParkerDelete onParkerDelete){
        this.onParkerDelete=onParkerDelete;
    }
    public void onEditCar(onParkerEdit onParkerEdit){
        this.onParkerEdit=onParkerEdit;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgParkingSpace,tvDelete;
        public TTextView tvRegistrationNo, tvYear,tvModel,tvVisible;
        public AppCompatImageView tvEdit;
        public CardView cv;
        public RelativeLayout rl;

        public ViewHolder(View view) {
            super(view);
            imgParkingSpace = view.findViewById(R.id.imgParkingSpace);
            tvModel = view.findViewById(R.id.tvModel);
            tvYear = view.findViewById(R.id.tvYear);
            tvRegistrationNo = view.findViewById(R.id.tvRegistrationNo);
            tvDelete=view.findViewById(R.id.tvDelete);
            tvEdit=view.findViewById(R.id.tvEdit);
            tvVisible=view.findViewById(R.id.tvVisible);
            cv=view.findViewById(R.id.cv);
            rl=view.findViewById(R.id.rl);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView ;
        System.out.println("CLASS NAME-->"+context.getClass().getSimpleName());

        if(context.getClass().getSimpleName().equalsIgnoreCase("ParkerBookingStayConfirmScreen")) {
            itemView = LayoutInflater.from(context)
                    .inflate(R.layout.parker_cars_row_two, parent, false);
        }else if(context.getClass().getSimpleName().equalsIgnoreCase("ParkerBookingDetailScreen")) {
            itemView = LayoutInflater.from(context)
                    .inflate(R.layout.parker_cars_row_two, parent, false);
        }else{
            itemView = LayoutInflater.from(context)
                    .inflate(R.layout.parker_cars_row, parent, false);
        }


        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // setFadeAnimation(holder.itemView);

        final tblCars task = list.get(position);
        try {
            holder.tvModel.setText(task.CarModel);
            holder.tvRegistrationNo.setText(task.CarRegisterNumber);
            holder.tvYear.setText(task.CarMakingYear);
            if(!context.getClass().getSimpleName().equalsIgnoreCase("ParkerBookingStayConfirmScreen") && !context.getClass().getSimpleName().equalsIgnoreCase("ParkerBookingDetailScreen")) {
                holder.tvVisible.setVisibility(task.is_default.equalsIgnoreCase("1") ? View.VISIBLE : View.INVISIBLE);
            }else{

                    if(task.is_default.equalsIgnoreCase("1") && task.isSelect==1){
                        if(holder.rl!=null){
                            holder.rl.setBackground(context.getResources().getDrawable(R.drawable.round_car_book));
                        }else{
                            holder.rl.setBackground(null);
                        }
                    }else if(checkDefault(task)==1){
                        if(holder.rl!=null){
                            holder.rl.setBackground(context.getResources().getDrawable(R.drawable.round_car_book));
                        }else{
                            holder.rl.setBackground(null);
                        }
                    }else if(checkDefault2(task)==1){
                        if(holder.rl!=null){
                            holder.rl.setBackground(context.getResources().getDrawable(R.drawable.round_car_book));
                        }else{
                            holder.rl.setBackground(null);
                        }
                    }else {
                        if (holder.rl != null) {
                            holder.rl.setBackground(null);
                        }
                    }
                holder.tvVisible.setVisibility(View.GONE);
            }
            //holder.tvAvailability.setText(task.CarRegisterNumber.isEmpty()?"0% Available":task.CarRegisterNumber+" Available");
            Glide.with(context).load(task.CarImage).fitCenter().into(holder.imgParkingSpace);

            holder.tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if(task.is_default.equalsIgnoreCase("1")){
                        new AlertDialog.Builder(context)
                                .setTitle("")
                                .setCancelable(false)
                                .setMessage("This is default car.so you can't delete this car.Please set another default car then delete this car")
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null).show();

                    }else {
                        onParkerDelete.onDelete(task);
                    }


                }
            });
            holder.tvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onParkerEdit.onEdit(task);
                }
            });


            holder.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!context.getClass().getSimpleName().equalsIgnoreCase("ParkerBookingStayConfirmScreen") && !context.getClass().getSimpleName().equalsIgnoreCase("ParkerBookingDetailScreen")) {
                        }else
                            carClick.oncarClick(task);
//                    if(holder.rl!=null){
//                        holder.rl.setBackground(context.getResources().getDrawable(R.drawable.round_car_book));
//                    }else{
//                        holder.rl.setBackground(null);
//                    }
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


    private void setDefault(String carID) {
        try {
            if (!Utility.isNetworkAvailable(context)) {
                //Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(context);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.setDefaultCar(WebUtility.SET_DEFAULT_CAR, appPreferences.getString("USERID"), carID);
                responseBodyCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            try {
                                if (response.body() != null) {
                                    System.out.println("Error Code==>" + response.body().toString());
                                    JSONObject jsonObject = new JSONObject(response.body().toString());
                                    if (jsonObject != null) {
                                        System.out.println("Error Code==>" + jsonObject.getString("error_code"));
                                        if (jsonObject.getString("error_code").equalsIgnoreCase("0")) {
                                            notifyDataSetChanged();
                                        } else {
                                            //Utility.showAlert(context., jsonObject.getString("error_message").toString());
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        System.out.println("RESPONSE IS=====" + t.getMessage());
                        Utility.hideProgress();
                    }
                });
            }
        } catch (Exception ex) {
        }
    }

    private int checkDefault(tblCars cars){
        int data=0;
        for(int i=0;i<list.size();i++){
            if(list.get(i).isSelect==1){
                if(cars.CarID.equalsIgnoreCase(list.get(i).CarID)) {
                    data = 1;
                    break;
                }
            }
        }
        return data;
    }private int checkDefault3(){
        int data=0;
        for(int i=0;i<list.size();i++){
            if(list.get(i).isSelect==1){
                    data = 1;
                    break;
            }
        }
        return data;
    }
    private int checkDefault2(tblCars cars){
        int data=0;
        for(int i=0;i<list.size();i++){
            if(list.get(i).is_default.equalsIgnoreCase("1") && list.get(i).isSelect==0){
                if(cars.CarID.equalsIgnoreCase(list.get(i).CarID)) {
                    if(checkDefault3()==0)
                        data = 1;
                    break;
                }
            }
        }
        return data;
    }
}
