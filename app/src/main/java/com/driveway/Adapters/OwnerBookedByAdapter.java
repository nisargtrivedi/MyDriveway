package com.driveway.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;


import com.driveway.Component.TTextView;
import com.driveway.Model.BookedByModel;
import com.driveway.R;
import com.driveway.listeners.onGetBooking;
import java.util.List;

public class OwnerBookedByAdapter extends  RecyclerView.Adapter<OwnerBookedByAdapter.ViewHolder> {


    public List<BookedByModel> list;
    Context context;
    public String name;
    public onGetBooking book;

    public OwnerBookedByAdapter(Context context, List<BookedByModel> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnBooking(onGetBooking model){
        this.book=model;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TTextView UserName,UserCharacter;
        public LinearLayout llMain;

        public ViewHolder(View view) {
            super(view);
            UserName = view.findViewById(R.id.UserName);
            UserCharacter = view.findViewById(R.id.UserCharacter);
            llMain=view.findViewById(R.id.llMain);


        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.booked_user_row, parent, false);

        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // setFadeAnimation(holder.itemView);

        final BookedByModel task = list.get(position);
        System.out.println("MODEL SIZE==>"+list.get(position).parkerUser.name);
        try {
            if(task!=null) {
                if(task.parkerUser.first_name.isEmpty()){
                    holder.UserName.setText(task.parkerUser.name);
                }else {
                    holder.UserName.setText(task.parkerUser.first_name);
                }
                holder.UserCharacter.setText(task.parkerUser.name.charAt(0) + "");
                if (position > 0) {
                    if (position == 1) {
                        holder.UserCharacter.setTextColor(Color.parseColor("#0080FF"));
                        holder.UserCharacter.setBackgroundResource(R.drawable.round_character_fill_booked_three);
                    } else if (position % 4 == 0) {
                        holder.UserCharacter.setTextColor(Color.parseColor("#718ED0"));
                        holder.UserCharacter.setBackgroundResource(R.drawable.round_character_fill_booked_two);
                    } else if (position % 2 == 0) {
                        holder.UserCharacter.setTextColor(Color.parseColor("#EA012A"));
                        holder.UserCharacter.setBackgroundResource(R.drawable.round_character_fill_booked_one);
                    } else if (position % 3 == 0) {
                        holder.UserCharacter.setTextColor(Color.parseColor("#0080FF"));
                        holder.UserCharacter.setBackgroundResource(R.drawable.round_character_fill_booked_three);
                    } else {
                        holder.UserCharacter.setTextColor(context.getColor(R.color.button_color));
                        holder.UserCharacter.setBackgroundResource(R.drawable.round_character_fill_profile);
                    }
                }else if(position==0){
                    holder.UserCharacter.setTextColor(context.getColor(R.color.button_color));
                    holder.UserCharacter.setBackgroundResource(R.drawable.round_character_fill_profile);
                }
                holder.llMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        book.setBookedByModel(task);
                    }
                });
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
