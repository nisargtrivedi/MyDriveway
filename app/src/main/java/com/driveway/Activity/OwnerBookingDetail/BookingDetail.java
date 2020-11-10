package com.driveway.Activity.OwnerBookingDetail;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.AppCompatImageView;

import com.driveway.Activity.ActivityChatScreen_;
import com.driveway.Activity.BaseActivity;
import com.driveway.Activity.ParkerBooking.ParkerBookingDetailScreen;
import com.driveway.Activity.ReportScreen;
import com.driveway.Component.BButton;
import com.driveway.Component.TTextView;
import com.driveway.Model.BookedByModel;
import com.driveway.Model.ChatModel;
import com.driveway.Model.ConversationChatModel;
import com.driveway.Model.ParkerBookingList;
import com.driveway.R;
import com.driveway.Utility.Constants;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobandme.ada.exceptions.AdaFrameworkException;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

@EActivity(R.layout.owner_property_bookingdetail_screen)
public class BookingDetail extends BaseActivity {

    @ViewById
    TTextView tvUserName;
    @ViewById
    TTextView tvEmail;
    @ViewById
    TTextView PropertTitle;
    @ViewById
    TTextView tvAddress;
    @ViewById
    TTextView tvBookingDate;
    @ViewById
    TTextView tvTime;
    @ViewById
    TTextView tvParkingType;
    @ViewById
    TTextView tvStatus;
    @ViewById
    TTextView tvPrice;
    @ViewById
    AppCompatImageView Report;
    @ViewById
    TTextView tvChat;
    @ViewById
    TTextView tvCount;

    BookedByModel model;
    ParkerBookingList obj;

    String proprtyTitle="";
    String proprtyAddress="";
    String proprtyType="";
    FirebaseDatabase database;
    String ConversationID;
    ArrayList<ChatModel> list = new ArrayList<>();
    @AfterViews
    public void init(){
        whiteStatusBar();
        database = FirebaseDatabase.getInstance();
        try {
            dataContext.tblUserObjectSet.fill();
        } catch (AdaFrameworkException e) {
            e.printStackTrace();
        }
        bindData();
        loadChatData();
    }

    @Click
    public void Report(){
        if(obj!=null) {
            startActivity(new Intent(this, ReportScreen.class)
                    .putExtra("objdata", obj!=null?obj:null));
        }
        else if(model!=null){
                startActivity(new Intent(this, ReportScreen.class)
                        .putExtra("modeldata", model!=null?model:null)
                        .putExtra("title", proprtyTitle)
                );
        }
    }
    @Click
    public void Back(){
        finish();
        overridePendingTransition(R.anim.entry,R.anim.exit);
    }
    @UiThread
    public void loadChatData() {
        try{
            database.getReference().child(Constants.CHAT_FRIEND_LIST)
                    .child(dataContext.tblUserObjectSet.get(0).APIToken)
                    .child(obj!=null?obj.propertID:model.property_ID)
                    .child(Constants.FRIEND_ID).
                    child(obj!=null?obj.user.APIToken:model.parkerUser.fcm_id)
                    .orderByChild("timeStamp").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        String user=obj!=null?obj.user.APIToken:model.parkerUser.fcm_id;
                        ConversationID = dataSnapshot.child("ConversationsID").getValue(String.class);
                        // System.out.println("CONVERSATION ID ELSE PART=====>" + ConversationID + "");
                        database.getReference().child(Constants.CONVERSION).child(ConversationID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    int i=0;
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        ChatModel model = new ChatModel();
                                        if (user.equalsIgnoreCase(snapshot.child("sender").getValue(String.class)) && dataContext.tblUserObjectSet.get(0).APIToken.equalsIgnoreCase(snapshot.child("receiver").getValue(String.class))) {
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
                                        tvCount.setVisibility(View.INVISIBLE);
                                    }else{
                                        tvCount.setVisibility(View.VISIBLE);
                                        tvCount.setText(i+"");
                                    }
                                    System.out.println("COUNT==========>"+i+"---------------");

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }catch (Exception ex){

        }

    }


    private void bindData(){
        if(getIntent().getExtras()!=null){
            if(getIntent().getSerializableExtra("details")==null){
                obj= (ParkerBookingList) getIntent().getSerializableExtra("detail");
                if(obj!=null){
                    tvUserName.setText(obj.user.FullName);
                    tvEmail.setText(obj.user.Email);
                    tvBookingDate.setText(Constants.converDate(obj.bookingStartDate)+" -> "+Constants.converDate(obj.bookingEndDate));
                    tvTime.setText(obj.bookingStartTime.toUpperCase()+" -> "+obj.bookingEndTime.toUpperCase());

                    tvAddress.setText(obj.bookingPropertyAddress);
                    PropertTitle.setText(obj.bookingPropertyTitle);
                    tvParkingType.setText(obj.bookingPropertyParkingType);

                    String paidOrNot=obj.bookingPayment.equalsIgnoreCase("0")?"Unpaid":"Paid";
                    if(paidOrNot.equalsIgnoreCase("Unpaid")){
                        tvPrice.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        tvStatus.setText("Unpaid");
                    }else{
                        tvStatus.setText(obj.bookingStatus);
                        tvPrice.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.drawable_right_sign, 0);
                    }
                    tvPrice.setText("$"+obj.bookingPropertyStayPrice + " "+paidOrNot);

                    try {
                        dataContext.tblUserObjectSet.fill();
                        if(dataContext.tblUserObjectSet.get(0).UserID.equalsIgnoreCase(obj.user.UserID)){
                            tvChat.setVisibility(View.GONE);
                           // tvCount.setVisibility(View.INVISIBLE);
                        }else{
                            tvChat.setVisibility(View.VISIBLE);
                            //tvCount.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
            else{
                model= (BookedByModel) getIntent().getSerializableExtra("details");
                proprtyTitle=getIntent().getStringExtra("title");
                proprtyAddress=getIntent().getStringExtra("address");
                proprtyType=getIntent().getStringExtra("type");
                if(model!=null){
                    tvUserName.setText(model.parkerUser.name);
                    tvEmail.setText(model.parkerUser.email);
                    tvBookingDate.setText(Constants.converDate(model.booking_start_date)+" -> "+Constants.converDate(model.booking_end_date));
                    tvTime.setText(model.booking_start_time.toUpperCase()+" -> "+model.booking_end_time.toUpperCase());
                    tvAddress.setText(proprtyAddress);
                    PropertTitle.setText(proprtyTitle);
                    tvParkingType.setText(proprtyType);
                    if(model.isPayment.equalsIgnoreCase("0")) {
                        tvPrice.setText("$" + model.rates.price + " Unpaid");
                        tvStatus.setText("Unpaid");
                        tvPrice.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    }
                    else {
                        tvStatus.setText(model.booking_status);
                        tvPrice.setText("$" + model.rates.price + " Paid");
                        tvPrice.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.drawable_right_sign, 0);
                    }

                    try {
                        dataContext.tblUserObjectSet.fill();
                        if(dataContext.tblUserObjectSet.get(0).UserID.equalsIgnoreCase(model.parkerUser.id)){
                            tvChat.setVisibility(View.GONE);
                        }else{
                            tvChat.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    //tvPrice.setText("$ "+model.price);
                }
            }


        }
    }

    @Click
    public void tvChat(){
        if(obj!=null) {
            startActivity(new Intent(BookingDetail.this, ActivityChatScreen_.class)
                    .putExtra("obj", obj)
            );
        }else if(model!=null){
            startActivity(new Intent(BookingDetail.this, ActivityChatScreen_.class)
                    .putExtra("bookedobj", model)
                    .putExtra("title", proprtyTitle)
            );
        }
    }


    @Override
    public void onBackPressed() {
        finish();
    }
}
