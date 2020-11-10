package com.driveway.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.driveway.Component.RoundCornersImageView;
import com.driveway.Component.RoundedCornersTransform;
import com.driveway.Component.TTextView;
import com.driveway.Model.Notificationstbl;
import com.driveway.Model.SearchPropertyModel;
import com.driveway.R;
import com.driveway.Utility.Constants;
import com.driveway.listeners.onGetProperty;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NotificationAdapter extends  RecyclerView.Adapter<NotificationAdapter.ViewHolder> {


    public List<Notificationstbl> list;
    Context context;
    public String name;
    public onGetProperty property;

    public NotificationAdapter(Context context, List<Notificationstbl> list) {
        this.context = context;
        this.list = list;
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView UserCharacter, UserName, Time,Details,Date;
        public RelativeLayout rlNotifications;
        public View view_border;

        public ViewHolder(View view) {
            super(view);
            Date = view.findViewById(R.id.Date);
            UserCharacter = view.findViewById(R.id.UserCharacter);
            UserName = view.findViewById(R.id.UserName);
            Time = view.findViewById(R.id.Time);
            Details=view.findViewById(R.id.Details);
            rlNotifications=view.findViewById(R.id.rlNotifications);


        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.notification_row, parent, false);

        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // setFadeAnimation(holder.itemView);

        final Notificationstbl task = list.get(position);
        try {

            holder.Details.setText(task.NotificationMessage);
            holder.Date.setText(Constants.convertTwo(task.NotificationDate));
            holder.UserName.setText(task.NotificationUserName);
            holder.UserCharacter.setText(task.NotificationUserName.charAt(0)+"");
            holder.Time.setText(Constants.convertNotification(task.NotificationDate));

            if (position > 0) {
                if (position == 1) {
                    holder.UserCharacter.setTextColor(Color.parseColor("#0080FF"));
                    holder.UserCharacter.setBackgroundResource(R.drawable.round_character_fill_booked_three);
                }else if (position % 5 == 0) {
                    holder.UserCharacter.setTextColor(Color.parseColor("#ffffff"));
                    holder.UserCharacter.setBackgroundResource(R.drawable.round_character_fill_invite_five);
                } else if (position % 4 == 0) {
                    holder.UserCharacter.setTextColor(Color.parseColor("#718ED0"));
                    holder.UserCharacter.setBackgroundResource(R.drawable.round_character_fill_booked_two);
                }else if (position % 3 == 0) {
                    holder.UserCharacter.setTextColor(Color.parseColor("#0080FF"));
                    holder.UserCharacter.setBackgroundResource(R.drawable.round_character_fill_booked_three);
                } else if (position % 2 == 0) {
                    holder.UserCharacter.setTextColor(Color.parseColor("#EA012A"));
                    holder.UserCharacter.setBackgroundResource(R.drawable.round_character_fill_booked_one);
                } else {
                    holder.UserCharacter.setTextColor(context.getColor(R.color.button_color));
                    holder.UserCharacter.setBackgroundResource(R.drawable.round_character_fill_profile);
                }
                if(Constants.convertNotificationTwo(task.NotificationDate).equalsIgnoreCase(Constants.convertNotificationTwo(list.get(position-1).NotificationDate))){
                    holder.Date.setVisibility(View.GONE);

                }else{
                    holder.Date.setVisibility(View.VISIBLE);
                }
            }else if(position==0){

                holder.UserCharacter.setTextColor(context.getColor(R.color.button_color));
                holder.UserCharacter.setBackgroundResource(R.drawable.round_character_fill_profile);
                holder.Date.setVisibility(View.VISIBLE);
            }
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
