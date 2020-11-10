package com.driveway.Activity.ParkerBooking;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.driveway.Activity.ActivityChatScreen_;
import com.driveway.Activity.BaseActivity;
import com.driveway.Activity.OwnerDashboardScreen;
import com.driveway.Activity.ParkerBookingStayConfirmScreen;
import com.driveway.Activity.ParkerBookingStayScreen;
import com.driveway.Activity.ParkerDashboardScreen;
import com.driveway.Activity.ParkerManageCard;
import com.driveway.Activity.ReportScreen;
import com.driveway.Adapters.ParkerCarAdapter;
import com.driveway.Component.BButton;
import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.DBHelper.tblCars;
import com.driveway.DBHelper.tblStay;
import com.driveway.Model.ChatModel;
import com.driveway.Model.EditBookingModel;
import com.driveway.Model.ParkerBookingList;
import com.driveway.Model.tblCard;
import com.driveway.R;
import com.driveway.Utility.Constants;
import com.driveway.Utility.LocationProvider;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.driveway.listeners.onCarClick;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import com.mobandme.ada.Entity;
import com.mobandme.ada.exceptions.AdaFrameworkException;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.androidannotations.annotations.Background;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParkerBookingDetailScreen extends BaseActivity implements View.OnClickListener, LocationProvider.LocationCallback {

    RelativeLayout rlBg;
    LinearLayout llCard;
    AppCompatImageView back, Report, btnQuestion, btnEditBooking;
    TTextView tvCount, btnSave, btnExtend, tvPropertyTitle, tvPropertyKM, PropertyAddress, tvMinutes, tvParking, tvRatings, tvAvailability, UserName, tvStay, tvStartDate, tvEndDate, tvTime, tvPricee, btnCancel, btnProcess, addcard, cardHolderName;

    TTextView tv1_hour, tv2_hour, tv3_hour, tv4_hour;
    ImageView imgMap;
    BButton btnMins, btnChatNow;
    ParkerBookingList obj;

    public String cardID = "0";
    tblCard tblCard = null;
    private String mStripeCardToken;
    private Stripe mStripe = null;
    String extendBookingID = "0";

    double lat = 0;
    double lng = 0;
    LocationProvider provider;

    FirebaseDatabase database;
    ArrayList<ChatModel> list = new ArrayList<>();
    String ConversationID;
    int total = 0;

    RecyclerView rvCars;
    public ArrayList<tblCars> carslist = new ArrayList<>();
    ParkerCarAdapter adapter;

    String carID="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parker_booking_detail_screen);
        bindComponent();
        database = FirebaseDatabase.getInstance();
        obj = (ParkerBookingList) getIntent().getSerializableExtra("data");

        if (obj.card != null) {
            tblCard = obj.card;
            cardHolderName.setText(tblCard.OwnerName+"-"+tblCard.CardNo.substring(12, 16));

            if(tblCard.isDefault.equalsIgnoreCase("1"))
                addcard.setText("DEFAULT CARD ->");
            else if(tblCard.isDefault.equalsIgnoreCase("0"))
                addcard.setText("SELECTED CARD ->");
            else
                addcard.setText("ADD YOUR CARD DETAILS ->");

            cardID = tblCard.CardID;
        }

        //System.out.println("CAR ID----->"+obj.cars.CarID+"");


        if (obj != null) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (obj != null && !obj.bookingProperttyImage.isEmpty()) {
                    Picasso.with(this).load(obj.bookingProperttyImage).memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).into(new Target() {

                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            rlBg.setBackground(new BitmapDrawable(getResources(), bitmap));
                        }

                        @Override
                        public void onBitmapFailed(final Drawable errorDrawable) {
                            Log.d("TAG", "FAILED");
                        }

                        @Override
                        public void onPrepareLoad(final Drawable placeHolderDrawable) {
                            Log.d("TAG", "Prepare Load");
                        }
                    });
                }

            }, 20);

            tvPropertyTitle.setText(obj.bookingPropertyTitle);
            PropertyAddress.setText(obj.bookingPropertyAddress);
            tvStartDate.setText(Constants.converDate(obj.bookingStartDate));
            tvEndDate.setText(Constants.converDate(obj.bookingEndDate));
            tvTime.setText(obj.bookingStartTime + " -> " + obj.bookingEndTime);
            tvPricee.setText(obj.bookingPropertyStayPrice);
            tvStay.setText(obj.bookingPropertyStayTitle);
            btnMins.setText(obj.bookingPropertyStayTime);
            tvPropertyKM.setText(obj.bookingPropertyDistance);
            tvAvailability.setText(obj.bookingPropertyAvailable + " Available");
            tvParking.setText(obj.bookingPropertyParkingType);
            tvMinutes.setText(obj.bookingPropertyDuration);
            tvRatings.setText(obj.bookingPropertyRating);
            UserName.setText(obj.owneruser.FullName);

            if (obj.bookingPayment.equalsIgnoreCase("0")) {
                btnExtend.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                btnSave.setVisibility(View.GONE);
                btnProcess.setVisibility(View.VISIBLE);
                addcard.setVisibility(View.VISIBLE);
                llCard.setVisibility(View.VISIBLE);
            } else if (obj.bookingStatus.equalsIgnoreCase("upcoming")) {
                btnExtend.setVisibility(View.GONE);
                btnCancel.setVisibility(View.VISIBLE);
            } else if (obj.bookingStatus.equalsIgnoreCase("ongoing")) {
                btnExtend.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.GONE);
                btnEditBooking.setVisibility(View.INVISIBLE);
            } else if (obj.bookingStatus.equalsIgnoreCase("completed") || obj.bookingStatus.equalsIgnoreCase("cancelled")) {
                btnExtend.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                btnEditBooking.setVisibility(View.INVISIBLE);
            }
            tvStay.setText(Constants.stayString(obj.bookingPropertyStayTitle));
        }
        try {
            if (obj.userID.equalsIgnoreCase(obj.owneruser.UserID)) {
                btnChatNow.setVisibility(View.INVISIBLE);
                //tvCount.setVisibility(View.INVISIBLE);
            } else {
                btnChatNow.setVisibility(View.VISIBLE);
                //tvCount.setVisibility(View.VISIBLE);
            }
            dataContext.tblUserObjectSet.fill();
            //loadChatData();
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            dataContext.carsObjectSet.fill();
            if (dataContext != null) {
                adapter = new ParkerCarAdapter(this, carslist);
                rvCars.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
                rvCars.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                adapter.onCar(new onCarClick() {
                    @Override
                    public void oncarClick(tblCars cars) {
                        carID=cars.CarID;
                        onCLickDesign(cars);

                    }
                });
            }
        }catch (Exception ex){

        }

        loadData();
        // transparentStatusbar();

    }
    public void onCLickDesign(tblCars cars){
        for(int i=0;i<carslist.size();i++){
            if(cars.CarID.equalsIgnoreCase(carslist.get(i).CarID)){
                carslist.get(i).is_default="1";
            }else{
                carslist.get(i).is_default="0";
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void help() {
        View view = getLayoutInflater().inflate(R.layout.help_dialog, null);
        ImageView close = view.findViewById(R.id.close);
        final android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                .setView(view)
                .create();
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        dialog.show();
        close.setOnClickListener(view1 -> dialog.dismiss());
    }

    @Override
    protected void onStart() {
        super.onStart();
        provider = new LocationProvider(ParkerBookingDetailScreen.this, this);
        provider.connect();


    }

    @UiThread
    public void loadChatData() {
        try {
            database.getReference().child(Constants.CONVERSION).addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ChatModel model = new ChatModel();
                        if (dataContext.tblUserObjectSet.get(0).APIToken.equalsIgnoreCase(snapshot.child("receiver").getValue(String.class))) {
                            model.isRead = snapshot.child("isRead").getValue(Boolean.class);
                            if (model.isRead == false)
                                list.add(model);
                        }

                    }
                    if (list.size() > 0) {
                       // tvCount.setVisibility(View.VISIBLE);
                       // tvCount.setText(list.size() + "");
                    } else {
                        //tvCount.setVisibility(View.INVISIBLE);
                    }
                    System.out.println("SIZE===>" + list.size());

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    list.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ChatModel model = new ChatModel();
                        if (dataContext.tblUserObjectSet.get(0).APIToken.equalsIgnoreCase(snapshot.child("receiver").getValue(String.class))) {
                            model.isRead = snapshot.child("isRead").getValue(Boolean.class);
                            if (model.isRead == false)
                                list.add(model);
                        }

                    }
                    if (list.size() > 0) {
                       // tvCount.setVisibility(View.VISIBLE);
                       // tvCount.setText(list.size() + "");
                    } else {
                        //tvCount.setVisibility(View.INVISIBLE);
                    }
                    System.out.println("SIZE===>" + list.size());

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

        } catch (Exception ex) {

        }


    }

    private void addCars(ArrayList<tblCars> obj) {
        if(obj.size()>0) {
            try {

                dataContext.carsObjectSet.fill();
                if (dataContext != null) {
                    for (int i = 0; i < dataContext.carsObjectSet.size(); i++)
                        dataContext.carsObjectSet.remove(i).setStatus(Entity.STATUS_DELETED);

                    dataContext.carsObjectSet.save();
                    dataContext.carsObjectSet.addAll(obj);
                    dataContext.carsObjectSet.save();
                }

                    adapter.notifyDataSetChanged();
            } catch (Exception ex) {

            }
        }
    }
    private void bindComponent() {

        rvCars=findViewById(R.id.rvCars);
        llCard = findViewById(R.id.llCard);
        cardHolderName = findViewById(R.id.cardHolderName);
        tvCount = findViewById(R.id.tvCount);
        btnChatNow = findViewById(R.id.btnChatNow);
        btnEditBooking = findViewById(R.id.btnEditBooking);
        btnQuestion = findViewById(R.id.btnQuestion);
        rlBg = findViewById(R.id.rlBg);
        back = findViewById(R.id.back);
        Report = findViewById(R.id.Report);
        tvPropertyTitle = findViewById(R.id.tvPropertyTitle);
        tvPropertyKM = findViewById(R.id.tvPropertyKM);
        PropertyAddress = findViewById(R.id.PropertyAddress);
        tvMinutes = findViewById(R.id.tvMinutes);
        tvParking = findViewById(R.id.tvParking);
        tvRatings = findViewById(R.id.tvRatings);
        tvAvailability = findViewById(R.id.tvAvailability);
        imgMap = findViewById(R.id.imgMap);
        UserName = findViewById(R.id.UserName);
        tvStay = findViewById(R.id.tvStay);
        btnMins = findViewById(R.id.btnMins);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        tvTime = findViewById(R.id.tvTime);
        tvPricee = findViewById(R.id.tvPrice);
        btnCancel = findViewById(R.id.btnCancel);
        btnExtend = findViewById(R.id.btnExtend);
        btnSave = findViewById(R.id.btnSave);
        addcard = findViewById(R.id.addcard);
        btnProcess = findViewById(R.id.btnProcess);

        btnCancel.setOnClickListener(this);
        back.setOnClickListener(this);
        btnExtend.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnQuestion.setOnClickListener(this);
        Report.setOnClickListener(this);
        imgMap.setOnClickListener(this);

        btnProcess.setOnClickListener(this);
        addcard.setOnClickListener(this);
        btnEditBooking.setOnClickListener(this);
        btnChatNow.setOnClickListener(this);
        llCard.setOnClickListener(this);
    }

    private void loadData() {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(ParkerBookingDetailScreen.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.getProperty(WebUtility.GETCARS, appPreferences.getString("USERID"));
                responseBodyCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            try {
                                if (response.body() != null) {
                                    //System.out.println("Error Code==>" + response.body().toString());
                                    JSONObject jsonObject = new JSONObject(response.body().toString());
                                    if (jsonObject != null) {
                                        //System.out.println("Error Code==>" + jsonObject.getString("error_code"));
                                        if (jsonObject.getString("error_code").equalsIgnoreCase("0")) {
                                            carslist.clear();
                                            JSONArray array = jsonObject.getJSONArray("data");
                                            int isSelect=0;
                                            for (int i = 0; i < array.length(); i++) {
                                                tblCars cars = new tblCars();
                                                cars.CarID = array.getJSONObject(i).getString("id") != null ? array.getJSONObject(i).getString("id") : "";
                                                cars.CarModel = array.getJSONObject(i).getString("model") != null ? array.getJSONObject(i).getString("model") : "";
                                                cars.CarImage = array.getJSONObject(i).getString("car_img") != null ? array.getJSONObject(i).getString("car_img") : "";
                                                cars.CarRegisterNumber = array.getJSONObject(i).getString("reg_number") != null ? array.getJSONObject(i).getString("reg_number") : "";
                                                cars.CarMakingYear = array.getJSONObject(i).getString("make_year") != null ? array.getJSONObject(i).getString("make_year") : "";
                                                cars.is_default = array.getJSONObject(i).getString("is_default") != null ? array.getJSONObject(i).getString("is_default") : "";


                                                if(obj.cars!=null) {
                                                    if (cars.CarID.equalsIgnoreCase(obj.cars.CarID)) {
                                                        cars.is_default = "1";
                                                        carID=cars.CarID;
                                                        cars.isSelect=1;
                                                        System.out.println("CALLED MATCHED IF----->");
                                                    }
//                                                    else{
//                                                        //cars.is_default = "0";
//                                                        //System.out.println("CALLED MATCHED ELSE----->");
//                                                    }
                                                }else{
                                                    //cars.is_default = "1";
                                                    carID=cars.CarID;
                                                    cars.isSelect=0;
                                                    System.out.println("ELSE CALLED----->");
                                                }
                                                carslist.add(cars);
                                            }
//                                            for (int i = 0; i < carslist.size(); i++){
//                                                if (carslist.get(i).is_default.equalsIgnoreCase("1") && carID.equalsIgnoreCase("0")){
//                                                    carslist.get(i).is_default = "1";
//                                                    carID=carslist.get(i).CarID;
//                                                }
//                                            }
                                            addCars(carslist);
//                                            if(adapter!=null)
//                                                adapter.notifyDataSetChanged();

                                        } else {
                                            //Utility.showAlert(ParkerDashboardScreen.this, jsonObject.getString("error_message").toString());
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        System.out.println("RESPONSE IS=====" + t.getMessage());
                        //Utility.hideProgress();
                    }
                });
            }
        } catch (Exception ex) {
        }
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK);
        if (extendBookingID.equalsIgnoreCase("0")) {
            finish();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("")
                    .setCancelable(false)
                    .setMessage("Do you want to cancel extend the booking ?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            cancelExtendBookings();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        }
    }


    private void cancelBookings() {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(ParkerBookingDetailScreen.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.cancelBookings(WebUtility.CANCEL_BOOKING, obj != null ? obj.bookingID : "0");
                responseBodyCall.enqueue(callback);

            }
        } catch (Exception ex) {
        }
    }


    private void cancelExtendBookings() {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(ParkerBookingDetailScreen.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.cancelExtendBookings(WebUtility.CANCEL_EXTEND_BOOKING, extendBookingID);
                responseBodyCall.enqueue(cancelCallBack);

            }
        } catch (Exception ex) {
        }
    }

    private void openMapPath(String from, String path) {
        try {
            String mapPath = "https://www.google.com/maps/dir/?api=1&origin=" + from + "&destination=" + path + "&dir_action=navigate";
            //String mapPath="https://www.google.com/maps/dir/?api=1&waypoints=133+V+STREET+BAKERSFIELD+CA+93304|525+OAK+STREET+BAKERSFIELD+CA+93304|341+PACIFIC+AVE+SHAFTER+CA+93263|29488+MERCED+AVE+SHAFTER+CA+93563&destination=3119+S+Willow+Ave+Fresno%2C+CA+93725&dir_action=navigate";
            Log.e("map data", mapPath);
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse(mapPath));
            intent.setPackage("com.google.android.apps.maps");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Utility.showAlert(this, "Map application is not installed");
            }
        } catch (Exception ex) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnChatNow:
                startActivity(new Intent(ParkerBookingDetailScreen.this, ActivityChatScreen_.class)
                        .putExtra("obj", obj)
                );
                break;
            case R.id.btnEditBooking:
                startActivityForResult(new Intent(ParkerBookingDetailScreen.this,
                        ParkerBookingDetailEditScreen_.class)
                        .putExtra("data", obj), 1000
                );
                break;
            case R.id.imgMap:
                openMapPath(lat + "," + lng, obj.bookingLat + "," + obj.bookingLng);
                break;
            case R.id.Report:
                startActivity(new Intent(ParkerBookingDetailScreen.this, ReportScreen.class)
                        .putExtra("booking", obj)
                );
                break;
            case R.id.btnCancel:
                new AlertDialog.Builder(this)
                        .setTitle("")
                        .setCancelable(false)
                        .setMessage("Do you want to cancel the booking ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                cancelBookings();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();

                break;
            case R.id.btnQuestion:
                help();
                break;
            case R.id.btnExtend:
                extendDialog();
                break;
            case R.id.back:
                setResult(Activity.RESULT_OK);
                if (extendBookingID.equalsIgnoreCase("0")) {
                    finish();
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("")
                            .setCancelable(false)
                            .setMessage("Do you want to cancel extend the booking ?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    cancelExtendBookings();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }
                break;
            case R.id.btnSave:
                if (tblCard != null)
                    makePayment();
                else
                    Utility.showAlert(this, "please select card for make payment");
                break;
            case R.id.btnProcess:
                if (cardID.equalsIgnoreCase("0")) {
                    Utility.showAlert(this, "please select card");
                } else if (obj.bookingID.equalsIgnoreCase("0")) {
                    Utility.showAlert(this, "booking id not set properly");
                } else {
                    btnProcess.setEnabled(false);
                    try {
                        makePayment();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.addcard:
            case R.id.llCard:
                startActivityForResult(new Intent(ParkerBookingDetailScreen.this, ParkerManageCard.class), 100);
                break;
        }
    }

    private void extendDialog() {
        View v = LayoutInflater.from(this).inflate(R.layout.extend_booking_dialog, null, false);
        BottomSheetDialog dialog = new BottomSheetDialog(ParkerBookingDetailScreen.this, R.style.TransparentDialog);
        dialog.setContentView(v);
        LinearLayout llOne = v.findViewById(R.id.llOne);
        AppCompatImageView close = v.findViewById(R.id.close);

        TTextView tvExtendTime = v.findViewById(R.id.tvExtendTime);

        TTextView tvPrice = v.findViewById(R.id.tvPrice);
        TTextView tv15 = v.findViewById(R.id.tv15);
        TTextView tv30 = v.findViewById(R.id.tv30);
        TTextView tv60 = v.findViewById(R.id.tv60);

        tv1_hour = v.findViewById(R.id.tv1_hour);
        tv2_hour = v.findViewById(R.id.tv2_hour);
        tv3_hour = v.findViewById(R.id.tv3_hour);
        tv4_hour = v.findViewById(R.id.tv4_hour);
        BButton btnExtend = v.findViewById(R.id.btnExtend);


        if (obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.WEEK_STAY)) {
            tvExtendTime.setText(obj.bookingStartDate + " -> " + obj.bookingEndDate);
        } else if (obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.MONTH_STAY)) {
            tvExtendTime.setText(obj.bookingStartDate + " -> " + obj.bookingEndDate);
        } else {
            tvExtendTime.setText(obj.bookingStartTime + " -> " + obj.bookingEndTime);
        }

        tvPrice.setText(obj.bookingPrice);
        setExtendText(obj.bookingPropertyStayTitle, tv15, tv30, tv60, obj.bookingPropertyStayTime);


        for (int i = 0; i < obj.stays.size(); i++) {

            System.out.println("STAY NAME = " + obj.stays.get(i).StayMinutes);
            if (obj.stays.get(i).Stay.equalsIgnoreCase(obj.bookingPropertyStayTitle)) {
                if (obj.stays.get(i).StayMinutes.equalsIgnoreCase(Constants._15MIN)) {
                    llOne.setVisibility(View.GONE);
                    tv15.setVisibility(View.VISIBLE);
                    tv15.setText(obj.stays.get(i).StayMinutes);
                    tv15.setSelected(true);
                    setSelectedBG(tv15, tv30, tv60);
                }
                if (obj.stays.get(i).StayMinutes.equalsIgnoreCase(Constants._30MIN)) {
                    llOne.setVisibility(View.GONE);
                    tv30.setVisibility(View.VISIBLE);
                    tv30.setText(obj.stays.get(i).StayMinutes);
                    if (!tv15.isSelected()) {
                        tv30.setSelected(true);
                        setSelectedBG(tv30, tv15, tv60);
                    }
                }
                if (obj.stays.get(i).StayMinutes.equalsIgnoreCase(Constants._45MIN)) {
                    llOne.setVisibility(View.GONE);
                    tv60.setVisibility(View.VISIBLE);
                    tv60.setText(obj.stays.get(i).StayMinutes);
                    if (!tv15.isSelected() && !tv30.isSelected()) {
                        tv60.setSelected(true);
                        setSelectedBG(tv60, tv15, tv30);
                    }
                }
            }
            if (obj.stays.get(i).Stay.equalsIgnoreCase(obj.bookingPropertyStayTitle)) {
                if (obj.stays.get(i).StayMinutes.equalsIgnoreCase(Constants._1_4_HOUR)) {
                    llOne.setVisibility(View.VISIBLE);
                    tv1_hour.setVisibility(View.VISIBLE);
                    tv2_hour.setVisibility(View.VISIBLE);
                    tv4_hour.setVisibility(View.VISIBLE);
                    tv3_hour.setVisibility(View.VISIBLE);


                    tv1_hour.setTextColor(getResources().getColor(R.color.white));
                    tv1_hour.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                    tv2_hour.setTextColor(getResources().getColor(R.color.black));
                    tv2_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv3_hour.setTextColor(getResources().getColor(R.color.black));
                    tv3_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv4_hour.setTextColor(getResources().getColor(R.color.black));
                    tv4_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv1_hour.setSelected(true);
                    tv2_hour.setSelected(false);
                    tv3_hour.setSelected(false);
                    tv4_hour.setSelected(false);

                    tv1_hour.setText("1");
                    tv2_hour.setText("2");
                    tv3_hour.setText("3");
                    tv4_hour.setText("4");

                    tv15.setVisibility(View.VISIBLE);
                    tv15.setText(obj.stays.get(i).StayMinutes);
                    tv15.setSelected(true);
                }
                if (obj.stays.get(i).StayMinutes.equalsIgnoreCase(Constants._4_8_HOUR)) {
                    llOne.setVisibility(View.VISIBLE);
                    tv1_hour.setVisibility(View.VISIBLE);
                    tv2_hour.setVisibility(View.VISIBLE);
                    tv4_hour.setVisibility(View.VISIBLE);
                    tv3_hour.setVisibility(View.VISIBLE);
                    tv1_hour.setTextColor(getResources().getColor(R.color.white));
                    tv1_hour.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                    tv2_hour.setTextColor(getResources().getColor(R.color.black));
                    tv2_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv3_hour.setTextColor(getResources().getColor(R.color.black));
                    tv3_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv4_hour.setTextColor(getResources().getColor(R.color.black));
                    tv4_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv1_hour.setSelected(true);
                    tv2_hour.setSelected(false);
                    tv3_hour.setSelected(false);
                    tv4_hour.setSelected(false);
                    tv30.setVisibility(View.VISIBLE);
                    tv30.setText(obj.stays.get(i).StayMinutes);
                    if (tv15.getVisibility() != View.VISIBLE) {
                        tv30.setSelected(true);
                        tv1_hour.setText("5");
                        tv2_hour.setText("6");
                        tv3_hour.setText("7");
                        tv4_hour.setText("8");
                    } else {
                        tv15.setSelected(true);
                    }


                }
                if (obj.stays.get(i).StayMinutes.equalsIgnoreCase(Constants._8_PLUS_HOUR)) {
                    llOne.setVisibility(View.VISIBLE);
                    tv1_hour.setVisibility(View.VISIBLE);
                    tv2_hour.setVisibility(View.VISIBLE);
                    tv4_hour.setVisibility(View.VISIBLE);
                    tv3_hour.setVisibility(View.VISIBLE);

                    tv1_hour.setTextColor(getResources().getColor(R.color.white));
                    tv1_hour.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                    tv2_hour.setTextColor(getResources().getColor(R.color.black));
                    tv2_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv3_hour.setTextColor(getResources().getColor(R.color.black));
                    tv3_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv4_hour.setTextColor(getResources().getColor(R.color.black));
                    tv4_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                    tv1_hour.setSelected(true);
                    tv2_hour.setSelected(false);
                    tv3_hour.setSelected(false);
                    tv4_hour.setSelected(false);
                    tv60.setVisibility(View.VISIBLE);
                    tv60.setText(obj.stays.get(i).StayMinutes);
                    if (tv15.getVisibility() != View.VISIBLE && tv30.getVisibility() != View.VISIBLE) {
                        tv60.setSelected(true);
                        tv1_hour.setText("9");
                        tv2_hour.setText("10");
                        tv3_hour.setText("11");
                        tv4_hour.setText("12");
                    } else {
                        tv15.setSelected(true);
                    }

                }
            }
            if (obj.stays.get(i).Stay.equalsIgnoreCase(obj.bookingPropertyStayTitle)) {
                if (obj.stays.get(i).StayMinutes.equalsIgnoreCase(Constants._HALFDAY)) {
                    llOne.setVisibility(View.GONE);
                    tv15.setVisibility(View.VISIBLE);
                    tv15.setText(obj.stays.get(i).StayMinutes);
                    tv15.setSelected(true);
                }
                if (obj.stays.get(i).StayMinutes.equalsIgnoreCase(Constants._FULLDAY)) {
                    llOne.setVisibility(View.GONE);
                    tv30.setVisibility(View.VISIBLE);
                    tv30.setText(obj.stays.get(i).StayMinutes);
                    if (!tv15.isSelected()) {
                        tv30.setSelected(true);
                    }
                }

            }
            if (obj.stays.get(i).Stay.equalsIgnoreCase(obj.bookingPropertyStayTitle)) {
                if (obj.stays.get(i).StayMinutes.equalsIgnoreCase(Constants._3_DAY)) {
                    llOne.setVisibility(View.GONE);
                    tv15.setVisibility(View.VISIBLE);
                    tv15.setText(obj.stays.get(i).StayMinutes);
                    tv15.setSelected(true);
                }
                if (obj.stays.get(i).StayMinutes.equalsIgnoreCase(Constants._5_DAY)) {
                    llOne.setVisibility(View.GONE);
                    tv30.setVisibility(View.VISIBLE);
                    tv30.setText(obj.stays.get(i).StayMinutes);
                    if (!tv15.isSelected()) {
                        tv30.setSelected(true);
                    }
                }
                if (obj.stays.get(i).StayMinutes.equalsIgnoreCase(Constants._7_DAY)) {
                    llOne.setVisibility(View.GONE);
                    tv60.setVisibility(View.VISIBLE);
                    tv60.setText(obj.stays.get(i).StayMinutes);
                    if (!tv15.isSelected() && !tv30.isSelected()) {
                        tv60.setSelected(true);
                    }
                }
            }
            if (obj.stays.get(i).Stay.equalsIgnoreCase(obj.bookingPropertyStayTitle)) {
                if (obj.stays.get(i).StayMinutes.equalsIgnoreCase(Constants._14_DAY)) {
                    llOne.setVisibility(View.GONE);
                    tv15.setVisibility(View.VISIBLE);
                    tv15.setText(obj.stays.get(i).StayMinutes);
                    tv15.setSelected(true);
                }
                if (obj.stays.get(i).StayMinutes.equalsIgnoreCase(Constants._30_DAY)) {
                    llOne.setVisibility(View.GONE);
                    tv30.setVisibility(View.VISIBLE);
                    tv30.setText(obj.stays.get(i).StayMinutes);
                    if (!tv15.isSelected()) {
                        tv30.setSelected(true);
                    }
                }
            }
        }


        tv1_hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv1_hour.setTextColor(getResources().getColor(R.color.white));
                tv1_hour.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                tv2_hour.setTextColor(getResources().getColor(R.color.black));
                tv2_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv3_hour.setTextColor(getResources().getColor(R.color.black));
                tv3_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv4_hour.setTextColor(getResources().getColor(R.color.black));
                tv4_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv1_hour.setSelected(true);
                tv2_hour.setSelected(false);
                tv3_hour.setSelected(false);
                tv4_hour.setSelected(false);


                if (tv15.isSelected()) {
                    double p = 0, p2 = 0, total = 0;
                    if (!obj.bookingPropertyStayPrice.isEmpty()) {
                        p = Double.parseDouble(obj.bookingPropertyStayPrice);
                    }
                    p2 = Double.parseDouble(getPrice(tv15.getText().toString(), obj.bookingPropertyStayTitle));
                    total = p + p2;
                    tvPrice.setText(String.format("%.0f", total) + "");
                    tvExtendTime.setText(obj.bookingStartTime + " -> " + setEndTime(obj.bookingEndTime, tv15.getText().toString()));
                }
                if (tv30.isSelected()) {
                    double p = 0, p2 = 0, total = 0;
                    if (!obj.bookingPropertyStayPrice.isEmpty()) {
                        p = Double.parseDouble(obj.bookingPropertyStayPrice);
                    }
                    p2 = Double.parseDouble(getPrice(tv30.getText().toString(), obj.bookingPropertyStayTitle));
                    total = p + p2;
                    tvPrice.setText(String.format("%.0f", total) + "");
                    tvExtendTime.setText(obj.bookingStartTime + " -> " + setEndTime(obj.bookingEndTime, tv30.getText().toString()));
                }
                if (tv60.isSelected()) {
                    double p = 0, p2 = 0, total = 0;
                    if (!obj.bookingPropertyStayPrice.isEmpty()) {
                        p = Double.parseDouble(obj.bookingPropertyStayPrice);
                    }
                    p2 = Double.parseDouble(getPrice(tv60.getText().toString(), obj.bookingPropertyStayTitle));
                    total = p + p2;
                    tvPrice.setText(String.format("%.0f", total) + "");
                    tvExtendTime.setText(obj.bookingStartTime + " -> " + setEndTime(obj.bookingEndTime, tv60.getText().toString()));
                }


            }
        });
        tv2_hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tv2_hour.setTextColor(getResources().getColor(R.color.white));
                tv2_hour.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                tv1_hour.setTextColor(getResources().getColor(R.color.black));
                tv1_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv3_hour.setTextColor(getResources().getColor(R.color.black));
                tv3_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv4_hour.setTextColor(getResources().getColor(R.color.black));
                tv4_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv1_hour.setSelected(false);
                tv2_hour.setSelected(true);
                tv3_hour.setSelected(false);
                tv4_hour.setSelected(false);

                if (tv15.isSelected()) {
                    double p = 0, p2 = 0, total = 0;
                    if (!obj.bookingPropertyStayPrice.isEmpty()) {
                        p = Double.parseDouble(obj.bookingPropertyStayPrice);
                    }
                    p2 = Double.parseDouble(getPrice(tv15.getText().toString(), obj.bookingPropertyStayTitle));
                    total = p + p2;
                    tvPrice.setText(String.format("%.0f", total) + "");
                    tvExtendTime.setText(obj.bookingStartTime + " -> " + setEndTime(obj.bookingEndTime, tv15.getText().toString()));
                }
                if (tv30.isSelected()) {
                    double p = 0, p2 = 0, total = 0;
                    if (!obj.bookingPropertyStayPrice.isEmpty()) {
                        p = Double.parseDouble(obj.bookingPropertyStayPrice);
                    }
                    p2 = Double.parseDouble(getPrice(tv30.getText().toString(), obj.bookingPropertyStayTitle));
                    total = p + p2;
                    tvPrice.setText(String.format("%.0f", total) + "");
                    tvExtendTime.setText(obj.bookingStartTime + " -> " + setEndTime(obj.bookingEndTime, tv30.getText().toString()));
                }
                if (tv60.isSelected()) {
                    double p = 0, p2 = 0, total = 0;
                    if (!obj.bookingPropertyStayPrice.isEmpty()) {
                        p = Double.parseDouble(obj.bookingPropertyStayPrice);
                    }
                    p2 = Double.parseDouble(getPrice(tv60.getText().toString(), obj.bookingPropertyStayTitle));
                    total = p + p2;
                    tvPrice.setText(String.format("%.0f", total) + "");
                    tvExtendTime.setText(obj.bookingStartTime + " -> " + setEndTime(obj.bookingEndTime, tv60.getText().toString()));
                }


            }
        });
        tv3_hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tv3_hour.setTextColor(getResources().getColor(R.color.white));
                tv3_hour.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                tv1_hour.setTextColor(getResources().getColor(R.color.black));
                tv1_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv2_hour.setTextColor(getResources().getColor(R.color.black));
                tv2_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv4_hour.setTextColor(getResources().getColor(R.color.black));
                tv4_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv1_hour.setSelected(false);
                tv2_hour.setSelected(false);
                tv3_hour.setSelected(true);
                tv4_hour.setSelected(false);

                if (tv15.isSelected()) {
                    double p = 0, p2 = 0, total = 0;
                    if (!obj.bookingPropertyStayPrice.isEmpty()) {
                        p = Double.parseDouble(obj.bookingPropertyStayPrice);
                    }
                    p2 = Double.parseDouble(getPrice(tv15.getText().toString(), obj.bookingPropertyStayTitle));
                    total = p + p2;
                    tvPrice.setText(String.format("%.0f", total) + "");
                    tvExtendTime.setText(obj.bookingStartTime + " -> " + setEndTime(obj.bookingEndTime, tv15.getText().toString()));
                }
                if (tv30.isSelected()) {
                    double p = 0, p2 = 0, total = 0;
                    if (!obj.bookingPropertyStayPrice.isEmpty()) {
                        p = Double.parseDouble(obj.bookingPropertyStayPrice);
                    }
                    p2 = Double.parseDouble(getPrice(tv30.getText().toString(), obj.bookingPropertyStayTitle));
                    total = p + p2;
                    tvPrice.setText(String.format("%.0f", total) + "");
                    tvExtendTime.setText(obj.bookingStartTime + " -> " + setEndTime(obj.bookingEndTime, tv30.getText().toString()));
                }
                if (tv60.isSelected()) {
                    double p = 0, p2 = 0, total = 0;
                    if (!obj.bookingPropertyStayPrice.isEmpty()) {
                        p = Double.parseDouble(obj.bookingPropertyStayPrice);
                    }
                    p2 = Double.parseDouble(getPrice(tv60.getText().toString(), obj.bookingPropertyStayTitle));
                    total = p + p2;
                    tvPrice.setText(String.format("%.0f", total) + "");
                    tvExtendTime.setText(obj.bookingStartTime + " -> " + setEndTime(obj.bookingEndTime, tv60.getText().toString()));
                }


            }
        });
        tv4_hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tv4_hour.setTextColor(getResources().getColor(R.color.white));
                tv4_hour.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                tv1_hour.setTextColor(getResources().getColor(R.color.black));
                tv1_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv3_hour.setTextColor(getResources().getColor(R.color.black));
                tv3_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv2_hour.setTextColor(getResources().getColor(R.color.black));
                tv2_hour.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                tv1_hour.setSelected(false);
                tv2_hour.setSelected(false);
                tv3_hour.setSelected(false);
                tv4_hour.setSelected(true);

                if (tv15.isSelected()) {
                    double p = 0, p2 = 0, total = 0;
                    if (!obj.bookingPropertyStayPrice.isEmpty()) {
                        p = Double.parseDouble(obj.bookingPropertyStayPrice);
                    }
                    p2 = Double.parseDouble(getPrice(tv15.getText().toString(), obj.bookingPropertyStayTitle));
                    total = p + p2;
                    tvPrice.setText(String.format("%.0f", total) + "");
                    tvExtendTime.setText(obj.bookingStartTime + " -> " + setEndTime(obj.bookingEndTime, tv15.getText().toString()));
                }
                if (tv30.isSelected()) {
                    double p = 0, p2 = 0, total = 0;
                    if (!obj.bookingPropertyStayPrice.isEmpty()) {
                        p = Double.parseDouble(obj.bookingPropertyStayPrice);
                    }
                    p2 = Double.parseDouble(getPrice(tv30.getText().toString(), obj.bookingPropertyStayTitle));
                    total = p + p2;
                    tvPrice.setText(String.format("%.0f", total) + "");
                    tvExtendTime.setText(obj.bookingStartTime + " -> " + setEndTime(obj.bookingEndTime, tv30.getText().toString()));
                }
                if (tv60.isSelected()) {
                    double p = 0, p2 = 0, total = 0;
                    if (!obj.bookingPropertyStayPrice.isEmpty()) {
                        p = Double.parseDouble(obj.bookingPropertyStayPrice);
                    }
                    p2 = Double.parseDouble(getPrice(tv60.getText().toString(), obj.bookingPropertyStayTitle));
                    total = p + p2;
                    tvPrice.setText(String.format("%.0f", total) + "");
                    tvExtendTime.setText(obj.bookingStartTime + " -> " + setEndTime(obj.bookingEndTime, tv60.getText().toString()));
                }
            }
        });
        tv15.setOnClickListener(v12 -> {
            tv15.setSelected(true);
            tv30.setSelected(false);
            tv60.setSelected(false);
            setSelectedBG(tv15, tv30, tv60);
            double p = 0, p2 = 0, total = 0;
            if (!obj.bookingPropertyStayPrice.isEmpty()) {
                p = Double.parseDouble(obj.bookingPropertyStayPrice);
            }
            p2 = Double.parseDouble(getPrice(tv15.getText().toString(), obj.bookingPropertyStayTitle));
            total = p + p2;
            tvPrice.setText(String.format("%.0f", total) + "");
            tvExtendTime.setText("");
            if (obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.WEEK_STAY)) {
                tvExtendTime.setText(obj.bookingStartDate + " -> " + setEndDate(obj.bookingEndDate, tv15.getText().toString()));
            } else if (obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.MONTH_STAY)) {
                tvExtendTime.setText(obj.bookingStartDate + " -> " + setEndDate(obj.bookingEndDate, tv15.getText().toString()));
            } else
                tvExtendTime.setText(obj.bookingStartTime + " -> " + setEndTime(obj.bookingEndTime, tv15.getText().toString()));


            if (obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.HOUR_STAY)) {
                tv1_hour.setText("1");
                tv2_hour.setText("2");
                tv3_hour.setText("3");
                tv4_hour.setText("4");
            }
        });
        tv30.setOnClickListener(v13 -> {
            tv15.setSelected(false);
            tv30.setSelected(true);
            tv60.setSelected(false);
            setSelectedBG(tv30, tv15, tv60);

            double p = 0, p2 = 0, total = 0;
            if (!obj.bookingPropertyStayPrice.isEmpty()) {
                p = Double.parseDouble(obj.bookingPropertyStayPrice);
            }
            p2 = Double.parseDouble(getPrice(tv30.getText().toString(), obj.bookingPropertyStayTitle));
            total = p + p2;
            tvPrice.setText(String.format("%.0f", total) + "");


            //tvPrice.setText(getPrice(tv30.getText().toString(), obj.bookingPropertyStayTitle));
            tvExtendTime.setText("");
            if (obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.WEEK_STAY)) {
                tvExtendTime.setText(obj.bookingEndDate + " -> " + setEndDate(obj.bookingEndDate, tv30.getText().toString()));
            } else if (obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.MONTH_STAY)) {
                tvExtendTime.setText(obj.bookingEndDate + " -> " + setEndDate(obj.bookingEndDate, tv30.getText().toString()));
            } else
                tvExtendTime.setText(obj.bookingStartTime + " -> " + setEndTime(obj.bookingEndTime, tv30.getText().toString()));


            if (obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.HOUR_STAY)) {
                tv1_hour.setText("5");
                tv2_hour.setText("6");
                tv3_hour.setText("7");
                tv4_hour.setText("8");
            }
        });
        tv60.setOnClickListener(v14 -> {
            tv15.setSelected(false);
            tv30.setSelected(false);
            tv60.setSelected(true);
            setSelectedBG(tv60, tv15, tv30);

            double p = 0, p2 = 0, total = 0;
            if (!obj.bookingPropertyStayPrice.isEmpty()) {
                p = Double.parseDouble(obj.bookingPropertyStayPrice);
            }
            p2 = Double.parseDouble(getPrice(tv60.getText().toString(), obj.bookingPropertyStayTitle));
            total = p + p2;
            tvPrice.setText(String.format("%.0f", total) + "");


            //tvPrice.setText(getPrice(tv60.getText().toString(), obj.bookingPropertyStayTitle));
            tvExtendTime.setText("");
            if (obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.WEEK_STAY)) {
                tvExtendTime.setText(obj.bookingEndDate + " -> " + setEndDate(obj.bookingEndDate, tv60.getText().toString()));
            } else if (obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.MONTH_STAY)) {
                tvExtendTime.setText(obj.bookingEndDate + " -> " + setEndDate(obj.bookingEndDate, tv60.getText().toString()));
            } else
                tvExtendTime.setText(obj.bookingEndTime + " -> " + setEndTime(obj.bookingEndTime, tv60.getText().toString()));


            if (obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.HOUR_STAY)) {
                tv1_hour.setText("9");
                tv2_hour.setText("10");
                tv3_hour.setText("11");
                tv4_hour.setText("12");
            }
        });


        if (tv15.isSelected()) {

            double p = 0, p2 = 0, total = 0;
            if (!obj.bookingPropertyStayPrice.isEmpty()) {
                p = Double.parseDouble(obj.bookingPropertyStayPrice);
            }
            p2 = Double.parseDouble(getPrice(tv15.getText().toString(), obj.bookingPropertyStayTitle));
            total = p + p2;
            tvPrice.setText(String.format("%.0f", total) + "");
            tvExtendTime.setText("");
            if (obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.WEEK_STAY)) {
                tvExtendTime.setText(obj.bookingStartDate + " -> " + setEndDate(obj.bookingStartDate, tv15.getText().toString()));
            } else if (obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.MONTH_STAY)) {
                tvExtendTime.setText(obj.bookingStartDate + " -> " + setEndDate(obj.bookingStartDate, tv15.getText().toString()));
            } else
                tvExtendTime.setText(obj.bookingStartTime + " -> " + setEndTime(obj.bookingEndTime, tv15.getText().toString()));

        } else if (tv30.isSelected()) {

            double p = 0, p2 = 0, total = 0;
            if (!obj.bookingPropertyStayPrice.isEmpty()) {
                p = Double.parseDouble(obj.bookingPropertyStayPrice);
            }
            p2 = Double.parseDouble(getPrice(tv30.getText().toString(), obj.bookingPropertyStayTitle));
            total = p + p2;
            tvPrice.setText(String.format("%.0f", total) + "");


            //tvPrice.setText(getPrice(tv30.getText().toString(), obj.bookingPropertyStayTitle));
            tvExtendTime.setText("");
            if (obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.WEEK_STAY)) {
                tvExtendTime.setText(obj.bookingEndDate + " -> " + setEndDate(obj.bookingStartDate, tv30.getText().toString()));
            } else if (obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.MONTH_STAY)) {
                tvExtendTime.setText(obj.bookingEndDate + " -> " + setEndDate(obj.bookingStartDate, tv30.getText().toString()));
            } else
                tvExtendTime.setText(obj.bookingStartTime + " -> " + setEndTime(obj.bookingEndTime, tv30.getText().toString()));


        } else if (tv60.isSelected()) {
            double p = 0, p2 = 0, total = 0;
            if (!obj.bookingPropertyStayPrice.isEmpty()) {
                p = Double.parseDouble(obj.bookingPropertyStayPrice);
            }
            p2 = Double.parseDouble(getPrice(tv60.getText().toString(), obj.bookingPropertyStayTitle));
            total = p + p2;
            tvPrice.setText(String.format("%.0f", total) + "");


            //tvPrice.setText(getPrice(tv60.getText().toString(), obj.bookingPropertyStayTitle));
            tvExtendTime.setText("");
            if (obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.WEEK_STAY)) {
                tvExtendTime.setText(obj.bookingEndDate + " -> " + setEndDate(obj.bookingStartDate, tv60.getText().toString()));
            } else if (obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.MONTH_STAY)) {
                tvExtendTime.setText(obj.bookingEndDate + " -> " + setEndDate(obj.bookingStartDate, tv60.getText().toString()));
            } else
                tvExtendTime.setText(obj.bookingEndTime + " -> " + setEndTime(obj.bookingEndTime, tv60.getText().toString()));
        }


        btnExtend.setOnClickListener(v15 -> {
            if (tv15.isSelected()) {
                if (obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.WEEK_STAY)) {
                    extendDaysBookings(setEndDate(obj.bookingEndDate, tv15.getText().toString()), tv15.getText().toString(), dialog);
                } else if (obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.MONTH_STAY)) {
                    extendDaysBookings(setEndDate(obj.bookingEndDate, tv15.getText().toString()), tv15.getText().toString(), dialog);
                } else
                    extendBookings(setEndTime(obj.bookingEndTime, tv15.getText().toString()), tv15.getText().toString(), dialog);

            } else if (tv30.isSelected()) {
                if (obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.WEEK_STAY)) {
                    extendDaysBookings(setEndDate(obj.bookingEndDate, tv30.getText().toString()), tv30.getText().toString(), dialog);
                } else if (obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.MONTH_STAY)) {
                    extendDaysBookings(setEndDate(obj.bookingEndDate, tv30.getText().toString()), tv30.getText().toString(), dialog);
                } else
                    extendBookings(setEndTime(obj.bookingEndTime, tv30.getText().toString()), tv30.getText().toString(), dialog);

            } else if (tv60.isSelected()) {
                if (obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.WEEK_STAY)) {
                    extendDaysBookings(setEndDate(obj.bookingEndDate, tv60.getText().toString()), tv60.getText().toString(), dialog);
                } else if (obj.bookingPropertyStayTitle.equalsIgnoreCase(Constants.MONTH_STAY)) {
                    extendDaysBookings(setEndDate(obj.bookingEndDate, tv60.getText().toString()), tv60.getText().toString(), dialog);
                } else
                    extendBookings(setEndTime(obj.bookingEndTime, tv60.getText().toString()), tv60.getText().toString(), dialog);

            }
            tvPricee.setText(tvPrice.getText().toString());

        });
        close.setOnClickListener(v1 -> dialog.dismiss());
        dialog.show();
    }

    public String getPrice(String stay, String staySlot) {
        System.out.println("Stay =" + stay);
        System.out.println("Stay Slot =" + staySlot);
        String price = "0";
        for (int i = 0; i < obj.stays.size(); i++) {
            if (obj.stays.get(i).Stay.equalsIgnoreCase(staySlot)) {
                if (obj.stays.get(i).StayMinutes.equalsIgnoreCase(stay)) {
                    if (tv1_hour.isSelected()) {
                        price = String.format("%.0f", obj.stays.get(i).StayPrice * Integer.parseInt(tv1_hour.getText().toString()));
                    } else if (tv2_hour.isSelected()) {
                        price = String.format("%.0f", obj.stays.get(i).StayPrice * Integer.parseInt(tv2_hour.getText().toString()));
                    } else if (tv3_hour.isSelected()) {
                        price = String.format("%.0f", obj.stays.get(i).StayPrice * Integer.parseInt(tv3_hour.getText().toString()));
                    } else if (tv4_hour.isSelected()) {
                        price = String.format("%.0f", obj.stays.get(i).StayPrice * Integer.parseInt(tv4_hour.getText().toString()));
                    } else
                        price = String.format("%.0f", obj.stays.get(i).StayPrice);
                    break;
                }
            }
        }
        return price.isEmpty() ? "0" : price;
    }

    public void setSelectedBG(TTextView t1, TTextView t2, TTextView t3) {
        t1.setTextColor(getResources().getColor(R.color.white));
        t1.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

        t2.setTextColor(getResources().getColor(R.color.black));
        t2.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

        t3.setTextColor(getResources().getColor(R.color.black));
        t3.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);
    }

    public void setExtendText(String type, TTextView t1, TTextView t2, TTextView t3, String minutes) {
        if (type.equalsIgnoreCase(Constants.SHORT_STAY)) {
            t1.setText(Constants._15MIN);
            t2.setText(Constants._30MIN);
            t3.setText(Constants._45MIN);
            if (minutes.equalsIgnoreCase(Constants._15MIN)) {
                setSelectedBG(t1, t2, t3);
            } else if (minutes.equalsIgnoreCase(Constants._30MIN)) {
                setSelectedBG(t2, t1, t3);
            } else if (minutes.equalsIgnoreCase(Constants._45MIN)) {

                setSelectedBG(t3, t2, t1);
            }
        }
        if (type.equalsIgnoreCase(Constants.HOUR_STAY)) {
            t1.setText(Constants._1_4_HOUR);
            t2.setText(Constants._4_8_HOUR);
            t3.setText(Constants._8_PLUS_HOUR);
            if (minutes.equalsIgnoreCase(Constants._1_4_HOUR)) {

                setSelectedBG(t1, t2, t3);
            } else if (minutes.equalsIgnoreCase(Constants._4_8_HOUR)) {

                setSelectedBG(t2, t1, t3);
            } else if (minutes.equalsIgnoreCase(Constants._8_PLUS_HOUR)) {

                setSelectedBG(t3, t2, t1);
            }
        }
        if (type.equalsIgnoreCase(Constants.DAY_STAY)) {
            t1.setText(Constants._HALFDAY);
            t2.setText(Constants._FULLDAY);
            t3.setVisibility(View.INVISIBLE);
            if (minutes.equalsIgnoreCase(Constants._HALFDAY)) {

                setSelectedBG(t1, t2, t3);
            } else if (minutes.equalsIgnoreCase(Constants._FULLDAY)) {

                setSelectedBG(t2, t1, t3);
            }
        }
        if (type.equalsIgnoreCase(Constants.WEEK_STAY)) {
            t1.setText(Constants._3_DAY);
            t2.setText(Constants._5_DAY);
            t3.setText(Constants._7_DAY);
            if (minutes.equalsIgnoreCase(Constants._3_DAY)) {

                setSelectedBG(t1, t2, t3);
            } else if (minutes.equalsIgnoreCase(Constants._5_DAY)) {

                setSelectedBG(t2, t1, t3);
            } else if (minutes.equalsIgnoreCase(Constants._7_DAY)) {

                setSelectedBG(t3, t2, t1);
            }
        }
        if (type.equalsIgnoreCase(Constants.MONTH_STAY)) {
            t1.setText(Constants._14_DAY);
            t2.setText(Constants._30_DAY);
            t3.setVisibility(View.INVISIBLE);
            if (minutes.equalsIgnoreCase(Constants._14_DAY)) {
                setSelectedBG(t1, t2, t3);
            } else if (minutes.equalsIgnoreCase(Constants._30_DAY)) {

                setSelectedBG(t2, t1, t3);
            }
        }

    }

    public String setEndTime(String t, String t1) {
        String time = "";
        if (Constants._15MIN.equalsIgnoreCase(t1))
            time = Constants.addMinTime(t, 15);
        else if (Constants._30MIN.equalsIgnoreCase(t1))
            time = Constants.addMinTime(t, 30);
        else if (Constants._45MIN.equalsIgnoreCase(t1))
            time = Constants.addMinTime(t, 45);

        else if (Constants._1_4_HOUR.equalsIgnoreCase(t1)) {
            if (tv1_hour.isSelected())
                time = Constants.addMinTime(t, 60);
            else if (tv2_hour.isSelected())
                time = Constants.addMinTime(t, 120);
            else if (tv3_hour.isSelected())
                time = Constants.addMinTime(t, 180);
            else if (tv4_hour.isSelected())
                time = Constants.addMinTime(t, 240);

        } else if (Constants._4_8_HOUR.equalsIgnoreCase(t1)) {
            if (tv1_hour.isSelected())
                time = Constants.addMinTime(t, 300);
            else if (tv2_hour.isSelected())
                time = Constants.addMinTime(t, 360);
            else if (tv3_hour.isSelected())
                time = Constants.addMinTime(t, 420);
            else if (tv4_hour.isSelected())
                time = Constants.addMinTime(t, 480);

        } else if (Constants._8_PLUS_HOUR.equalsIgnoreCase(t1)) {
            if (tv1_hour.isSelected())
                time = Constants.addMinTime(t, 540);
            else if (tv2_hour.isSelected())
                time = Constants.addMinTime(t, 600);
            else if (tv3_hour.isSelected())
                time = Constants.addMinTime(t, 660);
            else if (tv4_hour.isSelected())
                time = Constants.addMinTime(t, 720);
        } else if (Constants._HALFDAY.equalsIgnoreCase(t1))
            time = Constants.addMinTime(t, 360);
        else if (Constants._FULLDAY.equalsIgnoreCase(t1))
            time = Constants.addMinTime(t, 720);

        System.out.println("Time = >" + time);
        return time;
    }

    public String setEndDate(String t, String t1) {
        String time = "";
        if (Constants._14_DAY.equalsIgnoreCase(t1))
            time = Constants.addDays(t, 14);
        else if (Constants._30_DAY.equalsIgnoreCase(t1))
            time = Constants.addDays(t, 30);
        else if (Constants._3_DAY.equalsIgnoreCase(t1))
            time = Constants.addDays(t, 3);
        else if (Constants._5_DAY.equalsIgnoreCase(t1))
            time = Constants.addDays(t, 5);
        else if (Constants._7_DAY.equalsIgnoreCase(t1))
            time = Constants.addDays(t, 7);


        System.out.println("Date = >" + time);
        return time;
    }


    private void extendBookings(String endTime, String stayTimes, BottomSheetDialog dialog) {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(ParkerBookingDetailScreen.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.extendBooking(WebUtility.EXTEND_BOOKING,
                        obj.propertID,
                        appPreferences.getString("USERID"),
                        obj.bookingPropertyStayTitle,
                        stayTimes,
                        obj.bookingStartDate,
                        obj.bookingEndDate,
                        obj.bookingStartTime,
                        endTime,
                        obj.bookingPropertyDistance,
                        obj.bookingPropertyDuration,
                        obj.bookingID, 1);

                dialog.dismiss();
                tvTime.setText(obj.bookingStartTime + "->" + endTime);
                System.out.println("PRICE ===>" + getPrice(stayTimes, obj.bookingPropertyStayTitle));
                responseBodyCall.enqueue(callExtendBooking);

            }
        } catch (Exception ex) {
        }
    }

    private void extendDaysBookings(String endTime, String stayTimes, BottomSheetDialog dialog) {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(ParkerBookingDetailScreen.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.extendBooking(WebUtility.EXTEND_BOOKING,
                        obj.propertID,
                        appPreferences.getString("USERID"),
                        obj.bookingPropertyStayTitle,
                        stayTimes,
                        obj.bookingStartDate,
                        endTime,
                        obj.bookingStartTime,
                        obj.bookingEndTime,
                        obj.bookingPropertyDistance,
                        obj.bookingPropertyDuration,
                        obj.bookingID, 1);

                dialog.dismiss();
                //  System.out.println("PRICE ===>"+getPrice(stayTimes, obj.bookingPropertyStayTitle));
                responseBodyCall.enqueue(callbackExtendDay);


            }
        } catch (Exception ex) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                tblCard = (tblCard) data.getExtras().getSerializable("CARD");

                if (tblCard != null) {
                    cardID = tblCard.CardID;
                    cardHolderName.setText(tblCard.OwnerName+"-"+tblCard.CardNo.substring(12, 16));
                    if(tblCard.isDefault.equalsIgnoreCase("1"))
                        addcard.setText("DEFAULT CARD ->");
                    else if(tblCard.isDefault.equalsIgnoreCase("0"))
                        addcard.setText("SELECTED CARD ->");
                    else
                        addcard.setText("ADD YOUR CARD DETAILS ->");

                    if (addcard.getVisibility() == View.VISIBLE) {
                        llCard.setVisibility(View.VISIBLE);
                        btnSave.setVisibility(View.INVISIBLE);
                        btnExtend.setVisibility(View.GONE);
                        btnCancel.setVisibility(View.GONE);
                    } else {
                        btnSave.setVisibility(View.VISIBLE);
                        btnExtend.setVisibility(View.GONE);
                        btnCancel.setVisibility(View.GONE);
                        llCard.setVisibility(View.GONE);
                    }
                }

            }
            if (tblCard == null) {
                btnSave.setVisibility(View.INVISIBLE);
                btnExtend.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.GONE);
                return;
            }

        } else if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            EditBookingModel model;
            if (data != null && data.getExtras() != null) {
                model = (EditBookingModel) data.getExtras().getSerializable("EDITBOOKING");
                if (model != null) {
                    if (model.BookingStartDate.equalsIgnoreCase(obj.bookingStartDate) && model.BookingEndDate.equalsIgnoreCase(obj.bookingEndDate) && model.BookingStartTime.equalsIgnoreCase(obj.bookingStartTime) && model.BookingEndTime.equalsIgnoreCase(obj.bookingEndTime)) {
                    } else {
                        if (model.IsPayment == 0) {
                            //System.out.println("CALLEDDDDD EDIT BOOKING---->");

                            tvStartDate.setText(model.BookingStartDate);
                            tvEndDate.setText(model.BookingEndDate);
                            tvTime.setText(model.BookingStartTime + " -> " + model.BookingEndTime);
                            //tvPrice.setText(obj.bookingPropertyStayPrice);
                            tvPricee.setText(model.price);
                            btnMins.setText(model.soltName);
                            tvStay.setText(Constants.stayString(model.categoryName));
                            addcard.setVisibility(View.VISIBLE);
                            llCard.setVisibility(View.VISIBLE);
                            btnProcess.setVisibility(View.VISIBLE);
                            btnSave.setVisibility(View.GONE);
                            btnExtend.setVisibility(View.GONE);
                            btnCancel.setVisibility(View.GONE);
                        } else {
                            tvStartDate.setText(model.BookingStartDate);
                            tvEndDate.setText(model.BookingEndDate);

                            tvTime.setText(model.BookingStartTime + " -> " + model.BookingEndTime);

                            //tvPrice.setText(obj.bookingPropertyStayPrice);
                            tvPricee.setText(model.price);
                            btnMins.setText(model.soltName);
                            tvStay.setText(Constants.stayString(model.categoryName));

                            btnSave.setVisibility(View.INVISIBLE);
                            btnExtend.setVisibility(View.GONE);
                            btnCancel.setVisibility(View.VISIBLE);

                        }
                    }
                }
            }
        }
    }

    private void makePayment() {
        Utility.showProgress(ParkerBookingDetailScreen.this);
        try {
            Card card = new Card(tblCard.CardNo, Integer.parseInt(tblCard.CardMonth), Integer.parseInt(tblCard.CardYear), tblCard.CVV);
            //Constants.isValidCard("42222",10,22,"123");
            if (mStripeCardToken == null || mStripeCardToken.equals("")) {
                try {
                    mStripe = new Stripe(Constants.PUBLISHABLE_KEY);
                    mStripe.createToken(card, new TokenCallback() {
                        @Override
                        public void onError(Exception error) {
                           // System.out.println("TOKEN ERROR=====>" + error.toString());
                            Utility.showAlert(ParkerBookingDetailScreen.this, "card details are not valid");
                            btnProcess.setEnabled(true);
                        }

                        @Override
                        public void onSuccess(Token token) {
                            mStripeCardToken = token.getId();
                            propertyPayment(cardID, token.getId());
                        }
                    });
                } catch (Exception ex) {
                   // System.out.println("TOKEN ERROR=====>" + ex.toString());
                }
            }
        } catch (Exception ex) {

        }
    }

    private void propertyPayment(String cardID, String stripToken) {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.makePayment(WebUtility.PAYMENT, obj.bookingID, cardID, stripToken, extendBookingID,carID);
                responseBodyCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            try {
                                if (response.body() != null) {
                                    JSONObject jsonObject = new JSONObject(response.body().toString());
                                    if (jsonObject != null) {
                                        String message = jsonObject.getString("message") != null ? jsonObject.getString("message") : "";
                                        if (jsonObject.getString("error_code").equalsIgnoreCase("0")) {

                                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ParkerBookingDetailScreen.this);
                                            android.app.AlertDialog alert = builder.create();
                                            //Setting the title manually
                                            alert.setMessage(message);
                                            alert.setCancelable(false);
                                            alert.setButton("ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    setResult(Activity.RESULT_OK);
                                                    finish();
                                                    //startActivity(new Intent(ParkerBookingDetailScreen.this, ParkerBookingList.class));
                                                }
                                            });
                                            alert.show();
                                        } else {
                                            setResult(Activity.RESULT_OK);
                                            Utility.showAlertwithFinish(ParkerBookingDetailScreen.this, message);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                setResult(Activity.RESULT_OK);
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        //System.out.println("RESPONSE IS=====" + t.getMessage());
                        Utility.hideProgress();
                    }
                });
            }
        } catch (Exception ex) {
        }
    }

    Callback<JsonObject> callExtendBooking = new Callback<JsonObject>() {
        @Override
        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
            Utility.hideProgress();
            if (response.isSuccessful()) {
                try {
                    if (response.body() != null) {
                        System.out.println("Error Code==>" + response.body().toString());
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        if (jsonObject != null) {
                            System.out.println("Error Code==>" + jsonObject.getString("error_code"));
                            if (jsonObject.getString("error_code").equalsIgnoreCase("0")) {
                                extendBookingID = jsonObject.getString("booking_extend_id");
                                startActivityForResult(new Intent(ParkerBookingDetailScreen.this, ParkerManageCard.class), 100);
                                //Utility.showAlertwithFinish(ParkerBookingDetailScreen.this, jsonObject.getString("error_message").toString());
                            } else {
                                Utility.showAlert(ParkerBookingDetailScreen.this, jsonObject.getString("error_message").toString());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(Call<JsonObject> call, Throwable t) {

        }
    };
    Callback<JsonObject> callbackExtendDay = new Callback<JsonObject>() {
        @Override
        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
            Utility.hideProgress();
            if (response.isSuccessful()) {
                try {
                    if (response.body() != null) {
                        System.out.println("Error Code==>" + response.body().toString());
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        if (jsonObject != null) {
                            System.out.println("Error Code==>" + jsonObject.getString("error_code"));
                            if (jsonObject.getString("error_code").equalsIgnoreCase("0")) {
                                extendBookingID = jsonObject.getString("booking_extend_id");
                                startActivityForResult(new Intent(ParkerBookingDetailScreen.this, ParkerManageCard.class), 100);
                            } else {
                                Utility.showAlert(ParkerBookingDetailScreen.this, jsonObject.getString("error_message").toString());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(Call<JsonObject> call, Throwable t) {

        }
    };
    Callback<JsonObject> cancelCallBack = new Callback<JsonObject>() {
        @Override
        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
            Utility.hideProgress();
            if (response.isSuccessful()) {
                try {
                    if (response.body() != null) {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        if (jsonObject != null) {
                            System.out.println("Error Code==>" + jsonObject.getString("error_code"));
                            if (jsonObject.getString("error_code").equalsIgnoreCase("0")) {
                                finish();
                            } else {
                                if (jsonObject.getString("error_code").equalsIgnoreCase("10")) {
                                    deleteUser();
                                } else
                                    Utility.showAlert(ParkerBookingDetailScreen.this, jsonObject.getString("error_message").toString());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(Call<JsonObject> call, Throwable t) {

        }
    };
    Callback<JsonObject> callback = new Callback<JsonObject>() {
        @Override
        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
            Utility.hideProgress();
            if (response.isSuccessful()) {
                try {
                    if (response.body() != null) {
                        System.out.println("Error Code==>" + response.body().toString());
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        if (jsonObject != null) {
                            System.out.println("Error Code==>" + jsonObject.getString("error_code"));
                            if (jsonObject.getString("error_code").equalsIgnoreCase("0")) {
                                setResult(Activity.RESULT_OK);
                                finish();

                            } else {
                                if (jsonObject.getString("error_code").equalsIgnoreCase("10")) {
                                    deleteUser();
                                } else
                                    Utility.showAlert(ParkerBookingDetailScreen.this, jsonObject.getString("error_message").toString());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(Call<JsonObject> call, Throwable t) {
            Utility.hideProgress();
        }
    };

    @Override
    public void handleNewLocation(Location location) {
        try {
            lat = location.getLatitude();
            lng = location.getLongitude();
        } catch (Exception ex) {

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (provider != null) {
                provider.disconnect();
            }
        } catch (Exception ex) {

        }
    }

}
