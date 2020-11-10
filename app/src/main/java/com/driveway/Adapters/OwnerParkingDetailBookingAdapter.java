package com.driveway.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;

import com.driveway.Activity.OwnerBookingDetail.BookingDetail_;
import com.driveway.Component.RoundedCornersTransform;
import com.driveway.Component.TTextView;
import com.driveway.Model.BookingModel;
import com.driveway.Model.ChatModel;
import com.driveway.Model.ConversationChatModel;
import com.driveway.Model.ParkerBookingList;
import com.driveway.Model.ParkingSpace;
import com.driveway.R;
import com.driveway.Utility.Constants;
import com.driveway.Utility.DataContext;
import com.driveway.listeners.onGetProperty;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobandme.ada.exceptions.AdaFrameworkException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class OwnerParkingDetailBookingAdapter extends  RecyclerView.Adapter<OwnerParkingDetailBookingAdapter.ViewHolder> {


    public List<ParkerBookingList> list;
    Context context;
    public String name;
    public onGetProperty property;
    FirebaseDatabase database;
    DataContext dataContext;
    String ConversationID;
    DatabaseReference tblConversationmy;
    int listcount=0;
    public OwnerParkingDetailBookingAdapter(Context context, List<ParkerBookingList> list) {
        this.context = context;
        dataContext=new DataContext(context);
        this.list = list;
        database = FirebaseDatabase.getInstance();
        try {
            dataContext.tblUserObjectSet.fill();
        } catch (AdaFrameworkException e) {
            e.printStackTrace();
        }
    }

    public void getProperty(onGetProperty property){
        this.property=property;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgParkingSpace;
        public TTextView BookingDay, UserCharacter, UserName,BookingTime,tvCount;
        public RelativeLayout rlMain;

        public ViewHolder(View view) {
            super(view);
            BookingDay = view.findViewById(R.id.BookingDay);
            UserCharacter = view.findViewById(R.id.UserCharacter);
            UserName = view.findViewById(R.id.UserName);
            BookingTime = view.findViewById(R.id.BookingTime);
            rlMain=view.findViewById(R.id.rlMain);
            tvCount=view.findViewById(R.id.tvCount);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.booking_row, parent, false);

        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // setFadeAnimation(holder.itemView);

        final ParkerBookingList task = list.get(position);
        try {
            holder.UserName.setText(task.user.FullName);
            holder.BookingDay.setText(Constants.converDate(task.bookingStartDate)+" -> "+Constants.converDate(task.bookingEndDate));
            holder.BookingTime.setText(task.bookingStartTime+ " -- "+task.bookingEndTime);
            holder.UserCharacter.setText(task.user.FullName.charAt(0)+"");

            getChatCount(task,holder.tvCount);
            holder.rlMain.setOnClickListener(v -> {
                context.startActivity(new Intent(context, BookingDetail_.class).putExtra("detail",task));
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

    @UiThread
    public void loadChatData(String userName,TTextView tvcount) {

        try {
            database.getReference().child(Constants.CONVERSION).addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ChatModel model = new ChatModel();
                        if (dataContext.tblUserObjectSet.get(0).APIToken.equalsIgnoreCase(snapshot.child("receiver").getValue(String.class)) && userName.equalsIgnoreCase(snapshot.child("sender").getValue(String.class))) {
                            model.isRead = snapshot.child("isRead").getValue(Boolean.class);
                            if (model.isRead == false)
                                listcount++;
                        }
                    }
                    if (listcount > 0) {
                        tvcount.setVisibility(View.VISIBLE);
                        tvcount.setText(list.size() + "");
                    } else {
                        tvcount.setVisibility(View.GONE);
                    }
                    listcount=0;
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    list.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ChatModel model = new ChatModel();
                        if (dataContext.tblUserObjectSet.get(0).APIToken.equalsIgnoreCase(snapshot.child("receiver").getValue(String.class))) {
                            model.isRead = snapshot.child("isRead").getValue(Boolean.class);
                            if (model.isRead == false)
                                listcount++;
                        }
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (Exception ex){

        }




    }
    @UiThread
    public void getChatCount(ParkerBookingList bookingObj, TTextView tv){

        try{
            database.getReference().child(Constants.CHAT_FRIEND_LIST)
                    .child(dataContext.tblUserObjectSet.get(0).APIToken)
                    .child(bookingObj.propertID)
                    .child(Constants.FRIEND_ID).
                    child(bookingObj.user.APIToken)
                    .orderByChild("timeStamp").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        ConversationID = dataSnapshot.child("ConversationsID").getValue(String.class);
                        // System.out.println("CONVERSATION ID ELSE PART=====>" + ConversationID + "");
                        database.getReference().child(Constants.CONVERSION).child(ConversationID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    int i=0;
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        ChatModel model = new ChatModel();
                                        if (bookingObj.user.APIToken.equalsIgnoreCase(snapshot.child("sender").getValue(String.class)) && dataContext.tblUserObjectSet.get(0).APIToken.equalsIgnoreCase(snapshot.child("receiver").getValue(String.class))) {
                                            model.message = snapshot.child("message").getValue(String.class);
                                            model.messageType = snapshot.child("messageType").getValue(String.class);
                                            model.isRead = snapshot.child("isRead").getValue(Boolean.class);
                                            model.timeStamp = snapshot.child("timeStamp").getValue(Long.class);
                                            model.sender = snapshot.child("sender").getValue(String.class);
                                            model.receiver = snapshot.child("receiver").getValue(String.class);
                                            if(model.isRead==false)
                                                i++;
                                        }
                                    }
                                    if(i==0){
                                        tv.setVisibility(View.GONE);
                                    }else{
                                        tv.setVisibility(View.VISIBLE);
                                        tv.setText(i+"");
                                    }
                                    System.out.println("COUNT==========>"+i+"---------------");

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    } else {
                        tblConversationmy = database.getReference().child(Constants.CONVERSION);
                        ConversationID = tblConversationmy.push().getKey();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }catch (Exception ex){

        }

    }

}
