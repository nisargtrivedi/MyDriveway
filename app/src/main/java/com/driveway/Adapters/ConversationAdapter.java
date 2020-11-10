package com.driveway.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.driveway.Activity.OwnerBookingDetail.ConversationChat;
import com.driveway.Component.TTextView;
import com.driveway.Model.ChatModel;
import com.driveway.Model.ConversationChatModel;
import com.driveway.Model.Notificationstbl;
import com.driveway.R;
import com.driveway.Utility.Constants;
import com.driveway.Utility.DataContext;
import com.driveway.listeners.onChatClick;
import com.driveway.listeners.onGetProperty;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobandme.ada.exceptions.AdaFrameworkException;

import java.util.Date;
import java.util.List;

public class ConversationAdapter extends  RecyclerView.Adapter<ConversationAdapter.ViewHolder> {


    public List<ConversationChatModel> list;
    Context context;
    public String name;
    public onChatClick chatClick;
    FirebaseDatabase database;
    String ConversationID;
    DatabaseReference tblConversationmy;
    DataContext dataContext;

    public ConversationAdapter(Context context, List<ConversationChatModel> list) {
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

    public void onClick(onChatClick click){
        chatClick=click;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView UserCharacter, UserName, ChatTime,Details,tvMsgCount;
        public RelativeLayout rlMain;

        public ViewHolder(View view) {
            super(view);
            UserCharacter = view.findViewById(R.id.UserCharacter);
            UserName = view.findViewById(R.id.UserName);
            ChatTime = view.findViewById(R.id.ChatTime);
            tvMsgCount=view.findViewById(R.id.tvMsgCount);
            rlMain=view.findViewById(R.id.rlMain);


        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.conversation_row, parent, false);

        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // setFadeAnimation(holder.itemView);

        final ConversationChatModel task = list.get(position);
        try {


            holder.UserName.setText(task.ChatUserName);
            holder.UserCharacter.setText(task.ChatUserName.charAt(0)+"");
            if(!task.LastChatTime.isEmpty())
                holder.ChatTime.setText(DateFormat.format("dd MMM yyyy hh:mm a", new Date(Long.parseLong(task.LastChatTime) * 1000)).toString()+"");

            getChatCount(task,holder.tvMsgCount);




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
                    holder.UserCharacter.setTextColor(context.getResources().getColor(R.color.button_color));
                    holder.UserCharacter.setBackgroundResource(R.drawable.round_character_fill_profile);
                }

            }
            else if(position==0){

                holder.UserCharacter.setTextColor(context.getResources().getColor(R.color.button_color));
                holder.UserCharacter.setBackgroundResource(R.drawable.round_character_fill_profile);
                holder.ChatTime.setText(DateFormat.format("dd MMM yyyy hh:mm a", new Date(Long.parseLong(task.LastChatTime) * 1000)).toString()+"");
            }

            holder.rlMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chatClick.onClick(task);
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

    public void getChatCount(ConversationChatModel bookingObj, TextView tv){

        try{
            database.getReference().child(Constants.CHAT_FRIEND_LIST)
                    .child(dataContext.tblUserObjectSet.get(0).APIToken)
                    .child(bookingObj.propertyID)
                    .child(Constants.FRIEND_ID).
                    child(bookingObj.FCMID)
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
                                        if (bookingObj.FCMID.equalsIgnoreCase(snapshot.child("sender").getValue(String.class)) && dataContext.tblUserObjectSet.get(0).APIToken.equalsIgnoreCase(snapshot.child("receiver").getValue(String.class))) {
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
                                        tv.setVisibility(View.INVISIBLE);
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
