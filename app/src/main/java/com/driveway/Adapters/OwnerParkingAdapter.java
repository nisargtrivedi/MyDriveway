package com.driveway.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.driveway.Component.RoundCornersImageView;
import com.driveway.Component.RoundedCornersTransform;
import com.driveway.Component.TTextView;
import com.driveway.Model.ParkingSpace;
import com.driveway.R;
import com.driveway.listeners.onGetProperty;
import com.driveway.listeners.onParkerDelete;
import com.driveway.listeners.onPropertyDelete;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class OwnerParkingAdapter extends  RecyclerView.Adapter<OwnerParkingAdapter.ViewHolder> implements Filterable {


    public List<ParkingSpace> list;
    public List<ParkingSpace> listSearch;
    Context context;
    public String name;
    public onGetProperty property;

    public OwnerParkingAdapter(Context context, List<ParkingSpace> list) {
        this.context = context;
        this.list = list;
        listSearch=list;
    }

    public void getProperty(onGetProperty property){
        this.property=property;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    listSearch = list;
                } else {
                    List<ParkingSpace> filteredList = new ArrayList<>();
                    for (ParkingSpace row : list) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.ParkingTitle.toLowerCase().contains(charString.toLowerCase()) || row.ParkingAddress.contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    listSearch = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listSearch;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listSearch = (ArrayList<ParkingSpace>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public RoundCornersImageView imgParkingSpace;
        public TTextView tvTitle, tvAddress, tvAvailability;
        public LinearLayout llMain;

        public ViewHolder(View view) {
            super(view);
            imgParkingSpace = view.findViewById(R.id.imgParkingSpace);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvAddress = view.findViewById(R.id.tvAddress);
            tvAvailability = view.findViewById(R.id.tvAvailability);
            llMain=view.findViewById(R.id.llMain);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.owner_dashboard_parking_list_row, parent, false);

        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // setFadeAnimation(holder.itemView);

        final ParkingSpace task = listSearch.get(position);
        try {
            holder.tvTitle.setText(task.ParkingTitle);
            holder.tvAddress.setText(task.ParkingAddress);
            holder.tvAvailability.setText(task.ParkingAvailability.isEmpty()?"0% Available":task.ParkingAvailability+" Available");
            if(!task.ParkingImage.isEmpty())
                Picasso.with(context).load(task.ParkingImage).transform(new RoundedCornersTransform(50,0, RoundedCornersTransform.CornerType.ALL)).into(holder.imgParkingSpace);

            holder.llMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    property.getProperty(task);
                }
            });
        }catch (Exception ex){

        }


    }

    @Override
    public int getItemCount() {
        return listSearch.size();
    }

    public void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        view.startAnimation(anim);
    }

    public void add(ParkingSpace space){
        list.add(space);
        notifyDataSetChanged();
    }
}
