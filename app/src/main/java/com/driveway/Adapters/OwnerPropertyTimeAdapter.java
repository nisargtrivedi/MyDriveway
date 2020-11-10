package com.driveway.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.driveway.Component.RoundedCornersTransform;
import com.driveway.Component.TTextView;
import com.driveway.DBHelper.tblPropertyAvailableTimes;
import com.driveway.Model.ParkingSpace;
import com.driveway.Model.TimeModel;
import com.driveway.R;
import com.driveway.Utility.DataContext;
import com.driveway.listeners.onAvailabilityDelete;
import com.driveway.listeners.onEditTimeListeners;
import com.mobandme.ada.Entity;
import com.mobandme.ada.exceptions.AdaFrameworkException;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OwnerPropertyTimeAdapter extends  RecyclerView.Adapter<OwnerPropertyTimeAdapter.ViewHolder> {


    public List<tblPropertyAvailableTimes> list;
    Context context;
    public String name;

    onAvailabilityDelete delete;
    onEditTimeListeners ediitTimeListeners;
    DataContext dataContext;

    public OwnerPropertyTimeAdapter(Context context, List<tblPropertyAvailableTimes> list) {
        this.context = context;
        this.list = list;
        dataContext=new DataContext(context);
    }


    public void onDeleteTime(onAvailabilityDelete delete){
        this.delete=delete;
    }
    public void onEditTime(onEditTimeListeners edit){
        this.ediitTimeListeners=edit;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {


        public TTextView Time;
        public ImageView imgdelete;

        public ViewHolder(View view) {
            super(view);
            Time = view.findViewById(R.id.Time);
            imgdelete=view.findViewById(R.id.imgdelete);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.time_selection_row, parent, false);

        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // setFadeAnimation(holder.itemView);

        final tblPropertyAvailableTimes task = list.get(position);
        try {
            holder.Time.setText(task.Timing);

            holder.Time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ediitTimeListeners.onEdit(task);
                }
            });

            holder.imgdelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dataContext.propertyAvailableTimesObjectSet.fill("ID = ?",new String[]{task.getID()+""},null);
                        if(dataContext.propertyAvailableTimesObjectSet.size()>0){
                            for(int i=0;i<dataContext.propertyAvailableTimesObjectSet.size();i++)
                                dataContext.propertyAvailableTimesObjectSet.remove(i).setStatus(Entity.STATUS_DELETED);

                            dataContext.propertyAvailableTimesObjectSet.save();
                        }
                        list.remove(position);
                        notifyDataSetChanged();
                        delete.onDelete(task);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
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
    public void add(tblPropertyAvailableTimes model){
        list.add(model);
        notifyDataSetChanged();
    }
    public void delete(tblPropertyAvailableTimes model){
        list.remove(model);
        notifyDataSetChanged();
    }
}
