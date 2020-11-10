package com.driveway.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.driveway.Activity.AffilityDetail;
import com.driveway.Activity.AffilityDetail_;
import com.driveway.Component.RoundCornersImageView;
import com.driveway.Component.TTextView;
import com.driveway.Model.Notificationstbl;
import com.driveway.Model.tblAffiliation;
import com.driveway.R;
import com.driveway.Utility.Constants;
import com.driveway.listeners.onGetProperty;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AffiliationAdapter extends  RecyclerView.Adapter<AffiliationAdapter.ViewHolder> {


    public List<tblAffiliation> list;
    Context context;
    public String name;

    public AffiliationAdapter(Context context, List<tblAffiliation> list) {
        this.context = context;
        this.list = list;
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        public RoundCornersImageView img;
        public LinearLayout ll;

        public ViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.img);
            ll=view.findViewById(R.id.ll);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.affiliation_row, parent, false);

        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // setFadeAnimation(holder.itemView);

        final tblAffiliation task = list.get(position);
        try {

            Picasso.with(context).load(task.Image).into(holder.img);
            holder.ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, AffilityDetail_.class)
                            .putExtra("website",task.WebURL)
                            .putExtra("owner",task.ClientName)
                            .putExtra("companyname",task.CompanyName)
                            .putExtra("email",task.Email)
                            .putExtra("image",task.Image)
                            .putExtra("phone",task.Phone)

                    );
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
