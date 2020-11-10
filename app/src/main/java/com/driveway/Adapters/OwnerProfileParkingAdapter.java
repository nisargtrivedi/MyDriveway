package com.driveway.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.driveway.Activity.OwnerEditProperty.OwnerPropertyEditScreen;
import com.driveway.Activity.OwnerPropertyDetailScreen;
import com.driveway.Component.RoundCornersImageView;
import com.driveway.Component.RoundedCornersTransform;
import com.driveway.Component.TTextView;
import com.driveway.Model.ParkingSpace;
import com.driveway.R;
import com.driveway.listeners.onPropertyDelete;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OwnerProfileParkingAdapter extends  RecyclerView.Adapter<OwnerProfileParkingAdapter.ViewHolder> {


    public List<ParkingSpace> list;
    Context context;
    public String name;

    com.driveway.listeners.onPropertyDelete onPropertyDelete;


    public void onDeleteProperty(onPropertyDelete onPropertyDelete){
        this.onPropertyDelete=onPropertyDelete;
    }

    public OwnerProfileParkingAdapter(Context context, List<ParkingSpace> list) {
        this.context = context;
        this.list = list;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public RoundCornersImageView imgParkingSpace;
        public TTextView tvTitle, tvAddress, tvAvailability,tvDelete,tvEdit;
        public CardView menu;

        public ViewHolder(View view) {
            super(view);
            imgParkingSpace = view.findViewById(R.id.imgParkingSpace);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvAddress = view.findViewById(R.id.tvAddress);
            tvAvailability = view.findViewById(R.id.tvAvailability);
            tvDelete=view.findViewById(R.id.tvDelete);
            tvEdit=view.findViewById(R.id.tvEdit);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.owner_profile_property_row, parent, false);

        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // setFadeAnimation(holder.itemView);

        final ParkingSpace task = list.get(position);
        try {
            holder.tvTitle.setText(task.ParkingTitle);
            holder.tvAddress.setText(task.ParkingAddress);
            holder.tvAvailability.setText(task.ParkingAvailability.isEmpty()?"0% Available":task.ParkingAvailability+" Available");
            if(!task.ParkingImage.isEmpty())
                Picasso.with(context).load(task.ParkingImage).fit().transform(new RoundedCornersTransform(50,0, RoundedCornersTransform.CornerType.ALL)).into(holder.imgParkingSpace);

            holder.tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPropertyDelete.onDelete(task);
                }
            });
            holder.tvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, OwnerPropertyEditScreen.class).putExtra("editproperty",task));
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
