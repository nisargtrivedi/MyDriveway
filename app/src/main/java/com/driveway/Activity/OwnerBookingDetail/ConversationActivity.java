package com.driveway.Activity.OwnerBookingDetail;

import android.content.Intent;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.driveway.Activity.BaseActivity;
import com.driveway.Adapters.ConversationAdapter;
import com.driveway.Adapters.NotificationAdapter;
import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.Model.ChatModel;
import com.driveway.Model.ChatUsersModel;
import com.driveway.Model.ConversationChatModel;
import com.driveway.Model.ConversationModel;
import com.driveway.Model.Notificationstbl;
import com.driveway.Model.ParkerBookingList;
import com.driveway.Model.ParkingSpace;
import com.driveway.R;
import com.driveway.Utility.Constants;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.driveway.listeners.onChatClick;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;
import com.mobandme.ada.exceptions.AdaFrameworkException;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EActivity(R.layout.activity_conversation)
public class ConversationActivity extends BaseActivity {

    @ViewById
    TTextView tvTitle;
    @ViewById
    AppCompatImageView Back;
    @ViewById
    TTextView tvAllConversation;
    @ViewById
    RecyclerView lvConversation;
    @ViewById
    TTextView tvMsg;
    String ConversationID;
    DatabaseReference tblConversationmy;
    FirebaseDatabase database;
    List<ConversationChatModel> list = new ArrayList<>();
    List<ChatUsersModel> bookingLists = new ArrayList<>();
    ConversationAdapter adapter;
    String propertyID = "0";
    String time = "";
    int i = 0;

    @AfterViews
    public void init() {
        database = FirebaseDatabase.getInstance();
        orangeStatusBar();
        try {
            if (getIntent().getExtras() != null) {
                tvTitle.setText(getIntent().getStringExtra("title"));
                propertyID = getIntent().getStringExtra("propertyid");

                adapter = new ConversationAdapter(this, list);
                lvConversation.setLayoutManager(new LinearLayoutManager(this));
                lvConversation.setAdapter(adapter);

                adapter.onClick(new onChatClick() {
                    @Override
                    public void onClick(ConversationChatModel conversationChatModel) {
                        startActivity(new Intent(ConversationActivity.this, ConversationChat_.class)
                                .putExtra("property_id", propertyID)
                                .putExtra("property_name", tvTitle.getText().toString())
                                .putExtra("friend_name", conversationChatModel.ChatUserName)
                                .putExtra("friend_fcm", conversationChatModel.FCMID)
                                .putExtra("token", conversationChatModel.TOKEN)
                                .putExtra("user_id", conversationChatModel.USERID)
                        );
                    }
                });
                try {
                    dataContext.tblUserObjectSet.fill();
                } catch (AdaFrameworkException e) {
                    e.printStackTrace();
                }


                //loadChatData();
                //bindChat();
                adapter.notifyDataSetChanged();
            }
        } catch (Exception ex) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Utility.showProgress(ConversationActivity.this);
                    database.getReference().child(Constants.ALL_USERS).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                                bookingLists.clear();
                                while (iterator.hasNext()) {
                                    ChatUsersModel model = new ChatUsersModel();
                                    DataSnapshot snapshot = (DataSnapshot) iterator.next();
                                    model.Name = snapshot.child("fullname").getValue(String.class);
                                    model.FCMID = snapshot.child("fcm_id").getValue(String.class);
                                    model.TOKEN = snapshot.child("devicetoken").getValue(String.class);
                                    model.USERID = snapshot.child("user_id").getValue(Long.class);

                                    bookingLists.add(model);
                                }
                                bindChat();
                            } else {
                                Utility.hideProgress();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } catch (Exception ex) {

                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }



    public void bindChat() {
        list.clear();
        for (ChatUsersModel bookingList : bookingLists) {
            if (!bookingList.Name.isEmpty()) {
                database.getReference().child(Constants.CHAT_FRIEND_LIST).
                        child(dataContext.tblUserObjectSet.get(0).APIToken)
                        .child(propertyID)
                        .child(Constants.FRIEND_ID)
                        .child(bookingList.FCMID)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    if (dataSnapshot.getKey().equalsIgnoreCase(bookingList.FCMID)) {
                                        ConversationChatModel model = new ConversationChatModel();
                                        model.ChatUserName = bookingList.Name;
                                        model.FCMID = bookingList.FCMID;
                                        model.propertyID = propertyID;
                                        model.TOKEN = bookingList.TOKEN;
                                        model.USERID = bookingList.USERID + "";
                                        database.getReference().child(Constants.CHAT_FRIEND_LIST).
                                                child(dataContext.tblUserObjectSet.get(0).APIToken)
                                                .child(propertyID)
                                                .child(Constants.FRIEND_ID)
                                                .child(bookingList.FCMID).child("ConversationsID").
                                                addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        database.getReference().child(Constants.CONVERSION).child(dataSnapshot.getValue(String.class)).orderByChild("timeStamp").limitToLast(1).addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                Utility.hideProgress();
                                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                    System.out.println("CALLED SECOND---------->");
                                                                    time = snapshot.child("timeStamp").getValue(Long.class) + "";
                                                                    if (!snapshot.child("isRead").getValue(Boolean.class)) {
                                                                        i++;
                                                                        model.MessageCount = i + "";
                                                                    }
                                                                    if (list.size() > 0) {
                                                                        System.out.println("CALLED SECOND---------->");
                                                                        for (int i = 0; i < list.size(); i++) {
                                                                            if (!list.get(i).ChatUserName.equalsIgnoreCase(bookingList.Name)) {
                                                                                model.LastChatTime = time;
                                                                                list.add(model);
                                                                            }
                                                                        }
                                                                    } else {
                                                                        model.LastChatTime = time;
                                                                        list.add(model);
                                                                    }
                                                                    tvMsg.setVisibility(View.GONE);
                                                                    lvConversation.setVisibility(View.VISIBLE);


                                                                }
                                                                adapter.notifyDataSetChanged();
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                Utility.hideProgress();
                                                            }
                                                        });

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        Utility.hideProgress();
                                                    }
                                                });

                                    }
                                } else {
                                    Utility.hideProgress();
                                    if (list.size() > 0) {
                                        tvMsg.setVisibility(View.GONE);
                                        lvConversation.setVisibility(View.VISIBLE);
                                    }else {
                                        tvMsg.setVisibility(View.VISIBLE);
                                        lvConversation.setVisibility(View.GONE);
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Click
    public void Back() {
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }


}
