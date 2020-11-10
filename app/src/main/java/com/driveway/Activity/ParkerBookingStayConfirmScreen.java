package com.driveway.Activity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.IntentCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.driveway.Activity.ParkerBooking.BookingChat_;
import com.driveway.Adapters.ParkerCarAdapter;
import com.driveway.Component.BButton;
import com.driveway.Component.RoundedCornersTransform;
import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.DBHelper.tblCars;
import com.driveway.Model.SearchPropertyModel;
import com.driveway.Model.tblCard;
import com.driveway.R;
import com.driveway.Utility.Constants;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.driveway.listeners.onCarClick;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonObject;
import com.mobandme.ada.Entity;
import com.mobandme.ada.exceptions.AdaFrameworkException;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ParkerBookingStayConfirmScreen extends BaseActivity implements View.OnClickListener {

    BButton btnMins;
    AppCompatImageView btnQuestion, back, Report, btnEditBooking;
    ImageView imgMap;
    SearchPropertyModel parkingSpace;
    RelativeLayout rlBg;
    LinearLayout llCard;
    TTextView tvPropertyTitle, tvPropertyKM, PropertyAddress, tvMinutes, tvParking, tvRatings, tvAvailability, UserName, btnProcess, addcard;
    TTextView tvEndDate, tvStartDate, tvTime, tvPrice, tvStay,cardHolderName,msg;
    static String stayStartDate = "";
    static String stayEndDate = "";
    static String stayStartTime = "";
    static String stayEndTime = "";


    private Token mToken = null;

    private Stripe mStripe = null;
    private String mStripeCardToken;
    public String BookingID = "0";
    public String cardID = "0";
    public String rating = "0";
    public String property_id = "0";
    public String currentlatlong = "0";
    public String propertylatlong = "0";
    public BButton btnChat;

    tblCard tblCard = null;
    String stayName="";

    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference tblConversationmy;
    DatabaseReference tblChatUserFriendList;

    RecyclerView rvCars;
    public ArrayList<tblCars> carslist = new ArrayList<>();
    ParkerCarAdapter adapter;

    String carID="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parker_booking_confirmation);
        bindComponent();

        if (getIntent().getExtras() != null) {
            parkingSpace = (SearchPropertyModel) getIntent().getSerializableExtra("stay_booking");
            tblCard = (tblCard) getIntent().getSerializableExtra("card");
        }
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        property_id = getIntent().getStringExtra("property_id");
        stayStartDate = getIntent().getStringExtra("BookingStartDate");
        stayEndDate = getIntent().getStringExtra("BookingEndDate");
        stayStartTime = getIntent().getStringExtra("BookingStartTime");
        stayEndTime = getIntent().getStringExtra("BookingEndTime");
        tvStay.setText(getIntent().getStringExtra("CategoryName"));
        stayName=getIntent().getStringExtra("CategoryName");
        btnMins.setText(getIntent().getStringExtra("SlotName"));
        tvPrice.setText(getIntent().getStringExtra("stay_price"));
        BookingID = getIntent().getStringExtra("BookingID");
        rating = getIntent().getStringExtra("rating");

        currentlatlong = getIntent().getStringExtra("currentlocation");
        propertylatlong = getIntent().getStringExtra("propertylocation");

        tvStartDate.setText(stayStartDate.toUpperCase());
        tvEndDate.setText(!stayEndDate.isEmpty() ? stayEndDate.toUpperCase() : "");
        tvTime.setText(stayStartTime + " ----- " + stayEndTime.toUpperCase());

        tvStay.setText(Constants.stayString(stayName));

        if(tblCard!=null){
            if(tblCard.CardNo!=null && !tblCard.CardNo.isEmpty()) {
                cardHolderName.setText(tblCard.OwnerName + "-" + tblCard.CardNo.substring(12, 16));
                if (tblCard.isDefault.equalsIgnoreCase("1"))
                    addcard.setText("DEFAULT CARD ->");
                else if (tblCard.isDefault.equalsIgnoreCase("0")) {
                    if (cardHolderName.getText().length() == 0)
                        addcard.setText("ADD YOUR CARD DETAILS ->");
                    else
                        addcard.setText("SELECTED CARD ->");
                } else
                    addcard.setText("ADD YOUR CARD DETAILS ->");

                cardID = tblCard.CardID;
            }
        }


        try {
            if (parkingSpace != null) {
                tvPropertyTitle.setText(parkingSpace.propertyTitle != null ? parkingSpace.propertyTitle : "");
                tvAvailability.setText(parkingSpace.propertyAvailability != null ? parkingSpace.propertyAvailability + " Available" : "0% Available");
                tvRatings.setText(parkingSpace.propertyRating != null ? parkingSpace.propertyRating : "");
                tvPropertyKM.setText(parkingSpace.propertyDistance != null ? parkingSpace.propertyDistance : "");
                PropertyAddress.setText(parkingSpace.propertyAddress != null ? parkingSpace.propertyAddress : "");
                tvMinutes.setText(parkingSpace.propertyDuration!=null?parkingSpace.propertyDuration:"");
                tvParking.setText(parkingSpace.propertyParkingType!=null?parkingSpace.propertyParkingType:"");
                UserName.setText(parkingSpace.userDetail.FullName);


                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (parkingSpace != null && !parkingSpace.propertyImage.isEmpty()) {
                        Picasso.with(this).load(parkingSpace.propertyImage).transform(new RoundedCornersTransform(10, 0)).into(new Target() {

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

            }
        } catch (Exception ex) {

        }

        try {
            dataContext.tblUserObjectSet.fill();
            if(dataContext.tblUserObjectSet.get(0).UserID.equalsIgnoreCase(parkingSpace.userDetail.UserID)){
                btnChat.setVisibility(View.INVISIBLE);
            }else{
                if(BookingID.equalsIgnoreCase("0")){
                    btnChat.setVisibility(View.INVISIBLE);
                }else
                    btnChat.setVisibility(View.VISIBLE);

            }
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



    @Override
    protected void onStart() {
        super.onStart();
    }

    private void loadData() {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(ParkerBookingStayConfirmScreen.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.getProperty(WebUtility.GETCARS, appPreferences.getString("USERID"));
                responseBodyCall.enqueue(new Callback<JsonObject>() {
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
                                            carslist.clear();
                                            JSONArray array = jsonObject.getJSONArray("data");
                                            for (int i = 0; i < array.length(); i++) {
                                                tblCars cars = new tblCars();
                                                cars.CarID = array.getJSONObject(i).getString("id") != null ? array.getJSONObject(i).getString("id") : "";
                                                cars.CarModel = array.getJSONObject(i).getString("model") != null ? array.getJSONObject(i).getString("model") : "";
                                                cars.CarImage = array.getJSONObject(i).getString("car_img") != null ? array.getJSONObject(i).getString("car_img") : "";
                                                cars.CarRegisterNumber = array.getJSONObject(i).getString("reg_number") != null ? array.getJSONObject(i).getString("reg_number") : "";
                                                cars.CarMakingYear = array.getJSONObject(i).getString("make_year") != null ? array.getJSONObject(i).getString("make_year") : "";
                                                cars.is_default = array.getJSONObject(i).getString("is_default") != null ? array.getJSONObject(i).getString("is_default") : "";

                                                if(cars.is_default!=null && cars.is_default.equalsIgnoreCase("1")){
                                                    carID=cars.CarID;
                                                }
                                                carslist.add(cars);
                                            }
                                            addCars(carslist);
                                            if(carslist!=null && carslist.size()>0){
                                                msg.setVisibility(View.GONE);
                                                rvCars.setVisibility(View.VISIBLE);
                                            }else{
                                                msg.setVisibility(View.VISIBLE);
                                                rvCars.setVisibility(View.GONE);
                                            }
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

        msg=findViewById(R.id.msg);
        rvCars=findViewById(R.id.rvCars);
        llCard=findViewById(R.id.llCard);
        cardHolderName=findViewById(R.id.cardHolderName);

        tvStay = findViewById(R.id.tvStay);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        tvTime = findViewById(R.id.tvTime);
        tvPrice = findViewById(R.id.tvPrice);
        tvStay = findViewById(R.id.tvStay);
        btnEditBooking = findViewById(R.id.btnEditBooking);
        btnChat=findViewById(R.id.btnChat);


        addcard = findViewById(R.id.addcard);
        tvAvailability = findViewById(R.id.tvAvailability);
        tvRatings = findViewById(R.id.tvRatings);
        tvParking = findViewById(R.id.tvParking);
        tvMinutes = findViewById(R.id.tvMinutes);
        tvPropertyTitle = findViewById(R.id.tvPropertyTitle);
        tvPropertyKM = findViewById(R.id.tvPropertyKM);
        PropertyAddress = findViewById(R.id.PropertyAddress);
        UserName = findViewById(R.id.UserName);
        btnProcess = findViewById(R.id.btnProcess);

        rlBg = findViewById(R.id.rlBg);
        btnMins = findViewById(R.id.btnMins);
        btnQuestion = findViewById(R.id.btnQuestion);
        back = findViewById(R.id.back);
        Report = findViewById(R.id.Report);
        imgMap=findViewById(R.id.imgMap);

        btnQuestion.setOnClickListener(this);
        Report.setOnClickListener(this);
        back.setOnClickListener(this);
        btnProcess.setOnClickListener(this);
        addcard.setOnClickListener(this);
        btnEditBooking.setOnClickListener(this);
        imgMap.setOnClickListener(this);
        btnChat.setOnClickListener(this);
        llCard.setOnClickListener(this);
    }


    public void help() {
        View view = getLayoutInflater().inflate(R.layout.help_dialog, null);
        ImageView close = view.findViewById(R.id.close);
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        dialog.show();
        close.setOnClickListener(view1 -> dialog.dismiss());
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
        }catch (Exception ex){

        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnChat:
                startActivity(new Intent(ParkerBookingStayConfirmScreen.this, BookingChat_.class)
                        .putExtra("obj",parkingSpace)
                );
                break;
            case
                 R.id.imgMap:
                        openMapPath(currentlatlong,propertylatlong);
                break;
            case R.id.btnQuestion:
                help();
                break;
            case R.id.back:
                if(BookingID.equalsIgnoreCase("0")){
                    return;
                }else {
                    new androidx.appcompat.app.AlertDialog.Builder(this)
                            .setTitle("")
                            .setCancelable(false)
                            .setMessage("Do you want to exit from this screen ?")
                            .setPositiveButton(android.R.string.yes, (dialog, whichButton) ->
                            {
                                ParkerBookingStayScreen.bookingID=BookingID;
                                finish();
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }
                break;
            case R.id.Report:
                startActivity(new Intent(ParkerBookingStayConfirmScreen.this, ReportScreen.class)
                .putExtra("propertymodel",parkingSpace)
                                .putExtra("bookingID",BookingID)
                );
                break;
            case R.id.btnProcess:
                if(cardID!=null) {
                    if (cardID.equalsIgnoreCase("0")) {
                        Utility.showAlert(this, "please add your card details");
                    } else if (BookingID.equalsIgnoreCase("0")) {
                        Utility.showAlert(this, "please add your card details");
                    } else {
                        btnProcess.setEnabled(false);
                        try {
                            Utility.showProgress(ParkerBookingStayConfirmScreen.this);
                            makePayment();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                    Utility.showAlert(this, "please add your card details");
                }
                break;
            case R.id.llCard:
            case R.id.addcard:
                startActivityForResult(new Intent(ParkerBookingStayConfirmScreen.this, ParkerManageCard.class).putExtra("page","booking"), 100);
                break;
            case R.id.btnEditBooking:
                new androidx.appcompat.app.AlertDialog.Builder(ParkerBookingStayConfirmScreen.this)
                        .setTitle("")
                        .setCancelable(false)
                        .setMessage("Do you want to edit current booking ?")
                        .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                            ParkerBookingStayScreen.bookingID = BookingID;
                            if (stayName.equalsIgnoreCase("short_time")) {
                                ParkerBookingStayScreen.tvShortStay.setSelected(true);


                                ParkerBookingStayScreen.llShortStay.setVisibility(View.VISIBLE);
                                ParkerBookingStayScreen.llHourStay.setVisibility(View.GONE);
                                ParkerBookingStayScreen.llWeeklyStay.setVisibility(View.GONE);
                                ParkerBookingStayScreen.llDailyStay.setVisibility(View.GONE);
                                ParkerBookingStayScreen.llMonthlyStay.setVisibility(View.GONE);

//                                ParkerBookingStayScreen.ll1.setVisibility(View.VISIBLE);
//                                ParkerBookingStayScreen.ll2.setVisibility(View.GONE);
//                                ParkerBookingStayScreen.ll3.setVisibility(View.GONE);
//                                ParkerBookingStayScreen.ll4.setVisibility(View.GONE);
//                                ParkerBookingStayScreen.ll5.setVisibility(View.GONE);
                                    setVisibility();



                                ParkerBookingStayScreen.tvShortStartDate.setText(stayStartDate);
                                ParkerBookingStayScreen.tvShortEndDate.setText(stayEndDate);
                                ParkerBookingStayScreen.tvShortTimePick.setText(stayStartTime);
                                ParkerBookingStayScreen.tvShortEndTimePick.setText(stayEndTime);

                                if (btnMins.getText().toString().equalsIgnoreCase(Constants._15MIN)) {
                                    ParkerBookingStayScreen.tv15.setSelected(true);
                                    ParkerBookingStayScreen.tv30.setSelected(false);
                                    ParkerBookingStayScreen.tv60.setSelected(false);

                                    ParkerBookingStayScreen.tv15.setTextColor(getResources().getColor(R.color.white));
                                    ParkerBookingStayScreen.tv15.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                                    ParkerBookingStayScreen.tv30.setTextColor(getResources().getColor(R.color.black));
                                    ParkerBookingStayScreen.tv30.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                    ParkerBookingStayScreen.tv60.setTextColor(getResources().getColor(R.color.black));
                                    ParkerBookingStayScreen.tv60.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);
                                    ParkerBookingStayScreen.shortStayPrice = tvPrice.getText().toString().trim();
                                } else if (btnMins.getText().toString().equalsIgnoreCase(Constants._30MIN)) {
                                    ParkerBookingStayScreen.tv15.setSelected(false);
                                    ParkerBookingStayScreen.tv30.setSelected(true);
                                    ParkerBookingStayScreen.tv60.setSelected(false);

                                    ParkerBookingStayScreen.tv30.setTextColor(getResources().getColor(R.color.white));
                                    ParkerBookingStayScreen.tv30.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                                    ParkerBookingStayScreen.tv15.setTextColor(getResources().getColor(R.color.black));
                                    ParkerBookingStayScreen.tv15.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                    ParkerBookingStayScreen.tv60.setTextColor(getResources().getColor(R.color.black));
                                    ParkerBookingStayScreen.tv60.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                    ParkerBookingStayScreen.shortStayPrice = tvPrice.getText().toString().trim();
                                } else if (btnMins.getText().toString().equalsIgnoreCase(Constants._45MIN)) {
                                    ParkerBookingStayScreen.tv15.setSelected(false);
                                    ParkerBookingStayScreen.tv30.setSelected(false);
                                    ParkerBookingStayScreen.tv60.setSelected(true);

                                    ParkerBookingStayScreen.tv60.setTextColor(getResources().getColor(R.color.white));
                                    ParkerBookingStayScreen.tv60.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                                    ParkerBookingStayScreen.tv30.setTextColor(getResources().getColor(R.color.black));
                                    ParkerBookingStayScreen.tv30.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                    ParkerBookingStayScreen.tv15.setTextColor(getResources().getColor(R.color.black));
                                    ParkerBookingStayScreen.tv15.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                    ParkerBookingStayScreen.shortStayPrice = tvPrice.getText().toString().trim();
                                }
                            } else if (stayName.equalsIgnoreCase("hourly_time")) {
                                ParkerBookingStayScreen.tvHourStay.setSelected(true);
                                ParkerBookingStayScreen.llShortStay.setVisibility(View.GONE);
                                ParkerBookingStayScreen.llHourStay.setVisibility(View.VISIBLE);
                                ParkerBookingStayScreen.llWeeklyStay.setVisibility(View.GONE);
                                ParkerBookingStayScreen.llDailyStay.setVisibility(View.GONE);
                                ParkerBookingStayScreen.llMonthlyStay.setVisibility(View.GONE);

//                                ParkerBookingStayScreen.ll2.setVisibility(View.VISIBLE);
//                                ParkerBookingStayScreen.ll1.setVisibility(View.GONE);
//                                ParkerBookingStayScreen.ll3.setVisibility(View.GONE);
//                                ParkerBookingStayScreen.ll4.setVisibility(View.GONE);
//                                ParkerBookingStayScreen.ll5.setVisibility(View.GONE);
                                setVisibility();


                                ParkerBookingStayScreen.tvHourStartDate.setText(stayStartDate);
                                ParkerBookingStayScreen.tvHourEndDate.setText(stayEndDate);
                                ParkerBookingStayScreen.tvHourTimePick.setText(stayStartTime);
                                ParkerBookingStayScreen.textViewHourEndTime.setText(stayEndTime);

                                if (btnMins.getText().toString().equalsIgnoreCase(Constants._1_4_HOUR)) {
                                    ParkerBookingStayScreen.tv1_4.setSelected(true);
                                    ParkerBookingStayScreen.tv5_8.setSelected(false);
                                    ParkerBookingStayScreen.tv8.setSelected(false);

                                    ParkerBookingStayScreen.tv1_4.setTextColor(getResources().getColor(R.color.white));
                                    ParkerBookingStayScreen.tv1_4.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                                    ParkerBookingStayScreen.tv5_8.setTextColor(getResources().getColor(R.color.black));
                                    ParkerBookingStayScreen.tv5_8.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                    ParkerBookingStayScreen.tv8.setTextColor(getResources().getColor(R.color.black));
                                    ParkerBookingStayScreen.tv8.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                    ParkerBookingStayScreen.hourStayPrice = tvPrice.getText().toString().trim();
                                } else if (btnMins.getText().toString().equalsIgnoreCase(Constants._4_8_HOUR)) {
                                    ParkerBookingStayScreen.tv1_4.setSelected(false);
                                    ParkerBookingStayScreen.tv5_8.setSelected(true);
                                    ParkerBookingStayScreen.tv8.setSelected(false);

                                    ParkerBookingStayScreen.tv5_8.setTextColor(getResources().getColor(R.color.white));
                                    ParkerBookingStayScreen.tv5_8.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                                    ParkerBookingStayScreen.tv1_4.setTextColor(getResources().getColor(R.color.black));
                                    ParkerBookingStayScreen.tv1_4.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                    ParkerBookingStayScreen.tv8.setTextColor(getResources().getColor(R.color.black));
                                    ParkerBookingStayScreen.tv8.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);
                                    ParkerBookingStayScreen.hourStayPrice = tvPrice.getText().toString().trim();
                                } else if (btnMins.getText().toString().equalsIgnoreCase(Constants._8_PLUS_HOUR)) {
                                    ParkerBookingStayScreen.tv1_4.setSelected(false);
                                    ParkerBookingStayScreen.tv5_8.setSelected(false);
                                    ParkerBookingStayScreen.tv8.setSelected(true);

                                    ParkerBookingStayScreen.tv8.setTextColor(getResources().getColor(R.color.white));
                                    ParkerBookingStayScreen.tv8.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                                    ParkerBookingStayScreen.tv1_4.setTextColor(getResources().getColor(R.color.black));
                                    ParkerBookingStayScreen.tv1_4.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                    ParkerBookingStayScreen.tv5_8.setTextColor(getResources().getColor(R.color.black));
                                    ParkerBookingStayScreen.tv5_8.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);
                                    ParkerBookingStayScreen.hourStayPrice = tvPrice.getText().toString().trim();
                                }
                            } else if (stayName.equalsIgnoreCase("daily_time")) {

                                ParkerBookingStayScreen.tvDailyStay.setSelected(true);
                                ParkerBookingStayScreen.llShortStay.setVisibility(View.GONE);
                                ParkerBookingStayScreen.llHourStay.setVisibility(View.GONE);
                                ParkerBookingStayScreen.llWeeklyStay.setVisibility(View.GONE);
                                ParkerBookingStayScreen.llDailyStay.setVisibility(View.VISIBLE);
                                ParkerBookingStayScreen.llMonthlyStay.setVisibility(View.GONE);

//                                ParkerBookingStayScreen.ll3.setVisibility(View.VISIBLE);
//                                ParkerBookingStayScreen.ll2.setVisibility(View.GONE);
//                                ParkerBookingStayScreen.ll1.setVisibility(View.GONE);
//                                ParkerBookingStayScreen.ll4.setVisibility(View.GONE);
//                                ParkerBookingStayScreen.ll5.setVisibility(View.GONE);
                                setVisibility();

                                ParkerBookingStayScreen.tvDailyStartDate.setText(stayStartDate);
                                ParkerBookingStayScreen.tvDailyEndDate.setText(stayEndDate);
                                ParkerBookingStayScreen.tvDailyTimePick.setText(stayStartTime);
                                ParkerBookingStayScreen.tvDailyEndTime.setText(stayEndTime);

                                if (btnMins.getText().toString().equalsIgnoreCase(Constants._HALFDAY)) {
                                    ParkerBookingStayScreen.tvHalfDay.setSelected(true);
                                    ParkerBookingStayScreen.tvFullDay.setSelected(false);

                                    ParkerBookingStayScreen.tvHalfDay.setTextColor(getResources().getColor(R.color.white));
                                    ParkerBookingStayScreen.tvHalfDay.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                                    ParkerBookingStayScreen.tvFullDay.setTextColor(getResources().getColor(R.color.black));
                                    ParkerBookingStayScreen.tvFullDay.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);
                                    ParkerBookingStayScreen.dailyStayPrice = tvPrice.getText().toString().trim();


                                } else if (btnMins.getText().toString().equalsIgnoreCase(Constants._FULLDAY)) {
                                    ParkerBookingStayScreen.tvHalfDay.setSelected(false);
                                    ParkerBookingStayScreen.tvFullDay.setSelected(true);

                                    ParkerBookingStayScreen.tvFullDay.setTextColor(getResources().getColor(R.color.white));
                                    ParkerBookingStayScreen.tvFullDay.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                                    ParkerBookingStayScreen.tvHalfDay.setTextColor(getResources().getColor(R.color.black));
                                    ParkerBookingStayScreen.tvHalfDay.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);
                                    ParkerBookingStayScreen.dailyStayPrice = tvPrice.getText().toString().trim();
                                }
                            } else if (stayName.equalsIgnoreCase("weekly_time")) {
                                ParkerBookingStayScreen.tvWeeklyStay.setSelected(true);
                                ParkerBookingStayScreen.llShortStay.setVisibility(View.GONE);
                                ParkerBookingStayScreen.llHourStay.setVisibility(View.GONE);
                                ParkerBookingStayScreen.llWeeklyStay.setVisibility(View.VISIBLE);
                                ParkerBookingStayScreen.llDailyStay.setVisibility(View.GONE);
                                ParkerBookingStayScreen.llMonthlyStay.setVisibility(View.GONE);

//                                ParkerBookingStayScreen.ll4.setVisibility(View.VISIBLE);
//                                ParkerBookingStayScreen.ll2.setVisibility(View.GONE);
//                                ParkerBookingStayScreen.ll3.setVisibility(View.GONE);
//                                ParkerBookingStayScreen.ll1.setVisibility(View.GONE);
//                                ParkerBookingStayScreen.ll5.setVisibility(View.GONE);

                                setVisibility();
                                ParkerBookingStayScreen.tvWeeklyStartDate.setText(stayStartDate);
                                ParkerBookingStayScreen.tvWeeklyEndDate.setText(stayEndDate);

                                if (btnMins.getText().toString().equalsIgnoreCase(Constants._3_DAY)) {
                                    ParkerBookingStayScreen.tv3Days.setSelected(true);
                                    ParkerBookingStayScreen.tv5Days.setSelected(false);
                                    ParkerBookingStayScreen.tv7Days.setSelected(false);

                                    ParkerBookingStayScreen.tv3Days.setTextColor(getResources().getColor(R.color.white));
                                    ParkerBookingStayScreen.tv3Days.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                                    ParkerBookingStayScreen.tv5Days.setTextColor(getResources().getColor(R.color.black));
                                    ParkerBookingStayScreen.tv5Days.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                    ParkerBookingStayScreen.tv7Days.setTextColor(getResources().getColor(R.color.black));
                                    ParkerBookingStayScreen.tv7Days.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);
                                    ParkerBookingStayScreen.weeklyStayPrice = tvPrice.getText().toString().trim();

                                } else if (btnMins.getText().toString().equalsIgnoreCase(Constants._5_DAY)) {
                                    ParkerBookingStayScreen.tv3Days.setSelected(false);
                                    ParkerBookingStayScreen.tv5Days.setSelected(true);
                                    ParkerBookingStayScreen.tv7Days.setSelected(false);

                                    ParkerBookingStayScreen.tv5Days.setTextColor(getResources().getColor(R.color.white));
                                    ParkerBookingStayScreen.tv5Days.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                                    ParkerBookingStayScreen.tv3Days.setTextColor(getResources().getColor(R.color.black));
                                    ParkerBookingStayScreen.tv3Days.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                    ParkerBookingStayScreen.tv7Days.setTextColor(getResources().getColor(R.color.black));
                                    ParkerBookingStayScreen.tv7Days.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);
                                    ParkerBookingStayScreen.weeklyStayPrice = tvPrice.getText().toString().trim();
                                } else if (btnMins.getText().toString().equalsIgnoreCase(Constants._7_DAY)) {
                                    ParkerBookingStayScreen.tv3Days.setSelected(false);
                                    ParkerBookingStayScreen.tv5Days.setSelected(false);
                                    ParkerBookingStayScreen.tv7Days.setSelected(true);

                                    ParkerBookingStayScreen.tv7Days.setTextColor(getResources().getColor(R.color.white));
                                    ParkerBookingStayScreen.tv7Days.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                                    ParkerBookingStayScreen.tv5Days.setTextColor(getResources().getColor(R.color.black));
                                    ParkerBookingStayScreen.tv5Days.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);

                                    ParkerBookingStayScreen.tv3Days.setTextColor(getResources().getColor(R.color.black));
                                    ParkerBookingStayScreen.tv3Days.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);
                                    ParkerBookingStayScreen.weeklyStayPrice = tvPrice.getText().toString().trim();
                                }
                            } else if (stayName.equalsIgnoreCase("monthly_time")) {

                                ParkerBookingStayScreen.tvMonthlyStay.setSelected(true);
                                ParkerBookingStayScreen.llShortStay.setVisibility(View.GONE);
                                ParkerBookingStayScreen.llHourStay.setVisibility(View.GONE);
                                ParkerBookingStayScreen.llWeeklyStay.setVisibility(View.GONE);
                                ParkerBookingStayScreen.llDailyStay.setVisibility(View.GONE);
                                ParkerBookingStayScreen.llMonthlyStay.setVisibility(View.VISIBLE);

//                                ParkerBookingStayScreen.ll5.setVisibility(View.VISIBLE);
//                                ParkerBookingStayScreen.ll2.setVisibility(View.GONE);
//                                ParkerBookingStayScreen.ll3.setVisibility(View.GONE);
//                                ParkerBookingStayScreen.ll4.setVisibility(View.GONE);
//                                ParkerBookingStayScreen.ll1.setVisibility(View.GONE);
                                setVisibility();

                                ParkerBookingStayScreen.tvMonthlyStartDate.setText(stayStartDate);
                                ParkerBookingStayScreen.tvMonthlyEndDate.setText(stayEndDate);

                                if (btnMins.getText().toString().equalsIgnoreCase(Constants._14_DAY)) {
                                    ParkerBookingStayScreen.tv14Days.setSelected(true);
                                    ParkerBookingStayScreen.tv30Days.setSelected(false);

                                    ParkerBookingStayScreen.tv14Days.setTextColor(getResources().getColor(R.color.white));
                                    ParkerBookingStayScreen.tv14Days.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                                    ParkerBookingStayScreen.tv30Days.setTextColor(getResources().getColor(R.color.black));
                                    ParkerBookingStayScreen.tv30Days.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);
                                    ParkerBookingStayScreen.monthlyStayPrice = tvPrice.getText().toString().trim();

                                } else if (btnMins.getText().toString().equalsIgnoreCase(Constants._30_DAY)) {
                                    ParkerBookingStayScreen.tv14Days.setSelected(false);
                                    ParkerBookingStayScreen.tv30Days.setSelected(true);

                                    ParkerBookingStayScreen.tv30Days.setTextColor(getResources().getColor(R.color.white));
                                    ParkerBookingStayScreen.tv30Days.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three);

                                    ParkerBookingStayScreen.tv14Days.setTextColor(getResources().getColor(R.color.black));
                                    ParkerBookingStayScreen.tv14Days.setBackgroundResource(R.drawable.border_button_with_fill_booking_row);
                                    ParkerBookingStayScreen.monthlyStayPrice = tvPrice.getText().toString().trim();
                                }

                            }

                            ParkerBookingStayScreen.bookingID=BookingID;
                            finish();
//                            startActivity(new Intent(ParkerBookingStayConfirmScreen.this, ParkerBookingStayScreen.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
//                                    .putExtra("BookingID", BookingID)
//                                    .putExtra("CategoryName", tvStay.getText().toString())
//                                    .putExtra("SlotName", btnMins.getText().toString())
//                                    .putExtra("BookingStartDate", stayStartDate)
//                                    .putExtra("BookingEndDate", stayEndDate)
//                                    .putExtra("BookingStartTime", stayStartTime)
//                                    .putExtra("BookingEndTime", stayEndTime)
//                                    .putExtra("stay_booking", parkingSpace)
//                                    .putExtra("rating", rating)
//                                    .putExtra("property_id", property_id)
//                                    .putExtra("stay_price", tvPrice.getText().toString())
//                            );
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                break;
        }
    }
    private void setVisibility(){

        ParkerBookingStayScreen.ll5.setVisibility(ParkerBookingStayScreen.ll5.getVisibility()==View.VISIBLE?View.VISIBLE:View.GONE);
        ParkerBookingStayScreen.ll2.setVisibility(ParkerBookingStayScreen.ll2.getVisibility()==View.VISIBLE?View.VISIBLE:View.GONE);
        ParkerBookingStayScreen.ll3.setVisibility(ParkerBookingStayScreen.ll3.getVisibility()==View.VISIBLE?View.VISIBLE:View.GONE);
        ParkerBookingStayScreen.ll4.setVisibility(ParkerBookingStayScreen.ll4.getVisibility()==View.VISIBLE?View.VISIBLE:View.GONE);
        ParkerBookingStayScreen.ll1.setVisibility(ParkerBookingStayScreen.ll1.getVisibility()==View.VISIBLE?View.VISIBLE:View.GONE);

    }

    private void makePayment() throws Exception {
        Card card = new Card(tblCard.CardNo, Integer.parseInt(tblCard.CardMonth), Integer.parseInt(tblCard.CardYear), tblCard.CVV);
        //Constants.isValidCard("42222",10,22,"123");
        if (mStripeCardToken == null || mStripeCardToken.equals("")) {
            try {
                mStripe = new Stripe(Constants.PUBLISHABLE_KEY);
                mStripe.createToken(card, new TokenCallback() {
                    @Override
                    public void onError(Exception error) {
                        System.out.println("TOKEN ERROR=====>" + error.toString());
                        Utility.hideProgress();
                        Utility.showAlert(ParkerBookingStayConfirmScreen.this,"card details are not valid");
                        btnProcess.setEnabled(true);
                    }

                    @Override
                    public void onSuccess(Token token) {
                        mToken = token;
                        mStripeCardToken = mToken.getId();
                        System.out.println("TOKEN=====>" + mToken.getId());
                        btnProcess.setEnabled(true);
                        propertyPayment(BookingID, cardID, mToken.getId());
                    }
                });
            } catch (Exception ex) {
                System.out.println("TOKEN ERROR=====>" + ex.toString());
            }
        }
    }

    @Override
    public void onBackPressed() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("")
                .setCancelable(false)
                .setMessage("Do you want to exit from this screen ?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        ParkerBookingStayScreen.bookingID=BookingID;
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            System.out.println("onActivityResult Called");
            if (data != null && data.getExtras() != null) {
                int size=data.getExtras().getInt("size");
                System.out.println("SIZE====>"+size);
                if(size>0) {
                    tblCard = (tblCard) data.getExtras().getSerializable("CARD");
                    if (tblCard == null) {
                        System.out.println("CARD ID IS CALLED ===" + cardID);
                        return;
                    }
                    cardID = tblCard.CardID;
                    cardHolderName.setText(tblCard.OwnerName+"-"+tblCard.CardNo.substring(12, 16));
                    if(tblCard.isDefault.equalsIgnoreCase("1"))
                        addcard.setText("DEFAULT CARD ->");
                    else if(tblCard.isDefault.equalsIgnoreCase("0"))
                        addcard.setText("SELECTED CARD ->");
                    else
                        addcard.setText("ADD YOUR CARD DETAILS ->");


                }else{
                        tblCard=null;
                        cardHolderName.setText("");
                        cardID = "0";
                }
                System.out.println("CARD ID IS ===" + cardID);
            }
            if (tblCard == null) {
                System.out.println("CARD ID IS CALLED ===" + cardID);
                return;
            }

        }
    }

    Callback<JsonObject> callback=new Callback<JsonObject>() {
        @Override
        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
            btnProcess.setEnabled(true);
            Utility.hideProgress();
            if (response.isSuccessful()) {
                try {
                    if (response.body() != null) {
                        System.out.println("Response==>" + response.body().toString());

                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        if (jsonObject != null) {
                            System.out.println("Error Code==>" + jsonObject.getString("error_code"));
                            String message = jsonObject.getString("message") != null ? jsonObject.getString("message") : "";
                            if (jsonObject.getString("error_code").equalsIgnoreCase("0")) {

                              AlertDialog.Builder builder= new AlertDialog.Builder(ParkerBookingStayConfirmScreen.this);
                                AlertDialog alert = builder.create();
                                //Setting the title manually
                                alert.setMessage(message);
                                alert.setCancelable(false);
                                alert.setButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                        Intent intent=new Intent(ParkerBookingStayConfirmScreen.this, ParkerDashboardScreen.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                });
                                alert.show();


//
//                                            if (rating.equalsIgnoreCase("0")) {
//                                                RatingDialog();
//                                            } else {
//                                                finishAffinity();
//                                                startActivity(new Intent(ParkerBookingStayConfirmScreen.this, ParkerDashboardScreen.class));
//                                            }//Utility.showAlertwithFinish(ParkerBookingStayConfirmScreen.this,message);
                            } else {

                                Utility.showAlertwithFinish(ParkerBookingStayConfirmScreen.this, message);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utility.hideProgress();
                }
            }
            Utility.hideProgress();
        }

        @Override
        public void onFailure(Call<JsonObject> call, Throwable t) {
            Utility.hideProgress();
        }
    };
    Call<JsonObject> responseBodyCall=null;
    private void propertyPayment(String BookingID, String cardID, String stripToken) {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                responseBodyCall = apiInterface.makePayment(WebUtility.PAYMENT, BookingID, cardID, stripToken, "0",carID);
                responseBodyCall.enqueue(callback);
            }
        } catch (Exception ex) {
        }
    }







}
//
//    startActivity(new Intent(ParkerBookingStayScreen.this,ParkerBookingStayConfirmScreen.class)
//                                                .putExtra("BookingID",BookingID)
//                                                        .putExtra("PropertyID",PropertyID)
//                                                        .putExtra("CategoryName",CategoryName)
//                                                        .putExtra("SlotName",SlotName)
//                                                        .putExtra("BookingStartDate",BookingStartDate)
//                                                        .putExtra("BookingEndDate",BookingEndDate)
//                                                        .putExtra("BookingStartTime",BookingStartTime)
//                                                        .putExtra("BookingEndTime",BookingEndTime)
//                                                        .putExtra("stay_booking",parkingSpace)
//                                                        .putExtra("rating",rating)
//                                                        .putExtra("property_id",PropertyID)
//                                                        .putExtra("stay_price",StayPrice)
//
//
//                                                        );
//{"error_code":0,"error_message":"Property Payment","message":"Payment Process Succeeded","data":"succeeded"}