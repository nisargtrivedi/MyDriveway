package com.driveway.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.driveway.APIResponse.LoginResponse;
import com.driveway.Activity.OwnerBookingDetail.BookingList_;
import com.driveway.Activity.ParkerBooking.ParkerBookingListScreen;

import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.DBHelper.tblUser;
import com.driveway.MainActivity;
import com.driveway.R;
import com.driveway.Utility.AppPreferences;
import com.driveway.Utility.Constants;
import com.driveway.Utility.DataContext;
import com.driveway.Utility.GpsLocationReceiver;
import com.driveway.Utility.KeyBoardHandling;
import com.driveway.Utility.LocationProvider;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.driveway.background_service.ChatService;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Places;
import com.google.gson.JsonObject;
import com.mobandme.ada.Entity;
import com.mobandme.ada.exceptions.AdaFrameworkException;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class MainNavigationScreen extends AppCompatActivity {

    public static MainNavigationScreen mainNavigationScreen;
    public LinearLayout lnrContainer;
    public LinearLayout menu_one, menu_two, rlDetails, mainBg, menu_six, menu_four, menu_five;
    public RelativeLayout menu_three, toolbar, rlSwitch;
    public TTextView tvCurrentUser, tvMenuOne, tvMenuNotification, tvMenuBooking, tvTitle, btnSwitchAccount, username, owner, parker, usercharacter;
    public TTextView tvSettings, tvShare;
    public DrawerLayout drawer_layout;
    private ActionBarDrawerToggle toggle;
    public ImageView Menu, imgClose, imgone;
    public SwitchCompat notification_switch;

    Animation slide_down, slide_up;
    AppPreferences appPreferences;
    DataContext dataContext;
    public static String Token = "";
    public Location mLastLocation;

    ChatService chatService;
    public abstract void setLayoutView();
    public abstract void initialization();

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_screen);
        mainNavigationScreen=this;
        tvShare = findViewById(R.id.tvShare);
        tvSettings = findViewById(R.id.tvSettings);
        tvMenuNotification = findViewById(R.id.tvMenuNotification);
        menu_one = findViewById(R.id.menu_one);
        menu_five = findViewById(R.id.menu_five);
        menu_two = findViewById(R.id.menu_two);
        menu_three = findViewById(R.id.menu_three);
        tvMenuOne = findViewById(R.id.tvMenuOne);
        tvMenuNotification = findViewById(R.id.tvMenuNotification);
        tvMenuBooking = findViewById(R.id.tvMenuBooking);
        Menu = findViewById(R.id.Menu);
        imgClose = findViewById(R.id.imgClose);
        lnrContainer = findViewById(R.id.lnrContainer);
        toolbar = findViewById(R.id.toolbar);
        tvTitle = findViewById(R.id.tvTitle);
        btnSwitchAccount = findViewById(R.id.btnSwitchAccount);
        username = findViewById(R.id.username);
        rlSwitch = findViewById(R.id.rlSwitch);
        rlDetails = findViewById(R.id.rlDetails);
        mainBg = findViewById(R.id.mainBg);
        parker = findViewById(R.id.parker);
        owner = findViewById(R.id.owner);
        drawer_layout = findViewById(R.id.drawer_layout);
        tvCurrentUser = findViewById(R.id.tvCurrentUser);
        usercharacter = findViewById(R.id.UserCharacter);
        notification_switch = findViewById(R.id.notification_switch);
        menu_six = findViewById(R.id.menu_six);
        imgone = findViewById(R.id.imgone);
        menu_four = findViewById(R.id.menu_four);

        appPreferences = new AppPreferences(this);
        dataContext = new DataContext(this);

        appPreferences = new AppPreferences(this);


        toggle = new ActionBarDrawerToggle(this, drawer_layout, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer_layout.setDrawerListener(toggle);
        toggle.syncState();


        bindToken();
        setLayoutView();
        initialization();

        try {
            loadAnimation();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Menu.setOnClickListener(v -> {
            if (rlDetails != null) {
                rlDetails.setVisibility(View.GONE);
                mainBg.setBackgroundResource(R.color.white);
            }
            drawer_layout.openDrawer(Gravity.LEFT);
            checkCurrentUser();
        });

        menu_five.setOnClickListener(v -> {

            if(appPreferences.getString("USERID").equalsIgnoreCase("0"))
            {

                Utility.showAlertWithEventHandling(this, Constants.LOGIN_MESSAGE, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        LoginNavigation();
                    }
                });
            }
            else {
                menu_three.setSelected(false);
                menu_two.setSelected(false);
                menu_one.setSelected(false);
                menu_four.setSelected(false);
                menu_six.setSelected(false);
                menu_five.setSelected(true);

                setTextColor(menu_two, tvMenuBooking);
                setTextColor(menu_one, tvMenuOne);
                setTextColor(menu_three, tvMenuNotification);
                setTextColor(menu_four, tvSettings);
                setTextColor(menu_five, tvShare);


                drawer_layout.closeDrawer(Gravity.LEFT);
                startActivity(new Intent(MainNavigationScreen.this, ActivityShareMyDriveWay_.class));
                overridePendingTransition(R.anim.entry, R.anim.exit);
            }

        });
        menu_four.setOnClickListener(v -> {

//            if(appPreferences.getString("USERID").equalsIgnoreCase("0"))
//            {
//
//                Utility.showAlertWithEventHandling(this, Constants.LOGIN_MESSAGE, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        LoginNavigation();
//                    }
//                });
//            }
//            else {
                menu_three.setSelected(false);
                menu_two.setSelected(false);
                menu_one.setSelected(false);
                menu_four.setSelected(true);
                menu_six.setSelected(false);
                menu_five.setSelected(false);

                setTextColor(menu_two, tvMenuBooking);
                setTextColor(menu_one, tvMenuOne);
                setTextColor(menu_three, tvMenuNotification);
                setTextColor(menu_four, tvSettings);
                setTextColor(menu_five, tvShare);


                drawer_layout.closeDrawer(Gravity.LEFT);
                startActivity(new Intent(MainNavigationScreen.this, SettingsActivity_.class));
                overridePendingTransition(R.anim.entry, R.anim.exit);
           // }
        });
        imgClose.setOnClickListener(v -> drawer_layout.closeDrawers());
        username.setOnClickListener(v -> ProfileNavigation());
        usercharacter.setOnClickListener(v -> ProfileNavigation());

        menu_six.setOnClickListener(v -> {

            if(appPreferences.getString("USERID").equalsIgnoreCase("0"))
            {

                Utility.showAlertWithEventHandling(this, Constants.LOGIN_MESSAGE, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        LoginNavigation();
                    }
                });
            }
            else {
                try {
                    logoutSession();

                } catch (Exception ex) {

                }
            }
        });

        menu_three.setOnClickListener(v ->
                {

                    if(appPreferences.getString("USERID").equalsIgnoreCase("0"))
                    {

                        Utility.showAlertWithEventHandling(this, Constants.LOGIN_MESSAGE, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                LoginNavigation();
                            }
                        });
                    }
                    else {
                        menu_three.setSelected(true);
                        menu_two.setSelected(false);
                        menu_one.setSelected(false);
                        menu_four.setSelected(false);
                        menu_six.setSelected(false);
                        menu_five.setSelected(false);


                        setTextColor(menu_two, tvMenuBooking);
                        setTextColor(menu_one, tvMenuOne);
                        setTextColor(menu_three, tvMenuNotification);
                        setTextColor(menu_four, tvSettings);
                        setTextColor(menu_five, tvShare);


                        drawer_layout.closeDrawer(Gravity.LEFT);
                        startActivity(new Intent(MainNavigationScreen.this, NotificationActivity_.class));
                        overridePendingTransition(R.anim.entry, R.anim.exit);
                    }
                }
        );
        menu_one.setOnClickListener(v -> {

            if(appPreferences.getString("USERID").equalsIgnoreCase("0"))
            {

                Utility.showAlertWithEventHandling(this, Constants.LOGIN_MESSAGE, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        LoginNavigation();
                    }
                });
            }
            else {
                menu_one.setSelected(true);
                menu_two.setSelected(false);
                menu_three.setSelected(false);
                menu_four.setSelected(false);
                menu_six.setSelected(false);
                menu_five.setSelected(false);

                setTextColor(menu_two, tvMenuBooking);
                setTextColor(menu_one, tvMenuOne);
                setTextColor(menu_three, tvMenuNotification);
                setTextColor(menu_four, tvSettings);
                setTextColor(menu_five, tvShare);


                if (tvMenuOne.getText().toString().equalsIgnoreCase("Search Parking Space")) {
                    //if (!(MainNavigationScreen.this instanceof ParkerDashboardScreen)) {
                    Intent i = new Intent(MainNavigationScreen.this, ParkerDashboardScreen.class);
                    startActivity(i);
                    finish();
                    drawer_layout.closeDrawers();
//                } else {
//                    drawer_layout.closeDrawers();
//                }

//                Intent i = new Intent(MainNavigationScreen.this, ParkerDashboardScreen.class);
//                startActivity(i);
//                finish();
                    //drawer_layout.closeDrawers();
                    overridePendingTransition(R.anim.entry, R.anim.exit);
                } else {
                    // if (!(MainNavigationScreen.this instanceof OwnerDashboardScreen)) {
                    Intent i = new Intent(MainNavigationScreen.this, OwnerDashboardScreen.class);
                    startActivity(i);
                    finish();
                    drawer_layout.closeDrawers();
                    //} else
                    //  drawer_layout.closeDrawers();

//                drawer_layout.closeDrawers();
//                Intent i = new Intent(MainNavigationScreen.this, OwnerDashboardScreen.class);
//                startActivity(i);
//                finish();
                    overridePendingTransition(R.anim.entry, R.anim.exit);
                }
            }
        });

        menu_two.setOnClickListener(v -> {

            if(appPreferences.getString("USERID").equalsIgnoreCase("0"))
            {

                Utility.showAlertWithEventHandling(this, Constants.LOGIN_MESSAGE, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        LoginNavigation();
                    }
                });
            }
            else {
                drawer_layout.closeDrawers();

                menu_three.setSelected(false);
                menu_two.setSelected(true);
                menu_one.setSelected(false);
                menu_four.setSelected(false);
                menu_six.setSelected(false);
                menu_five.setSelected(false);


                setTextColor(menu_two, tvMenuBooking);
                setTextColor(menu_one, tvMenuOne);
                setTextColor(menu_three, tvMenuNotification);
                setTextColor(menu_four, tvSettings);
                setTextColor(menu_five, tvShare);


                if (appPreferences.getString("USERTYPE").equalsIgnoreCase("1")) {
                    Intent i = new Intent(MainNavigationScreen.this, ParkerBookingListScreen.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.entry, R.anim.exit);
                } else {
                    Intent i = new Intent(MainNavigationScreen.this, BookingList_.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.entry, R.anim.exit);
                }
            }
        });

        btnSwitchAccount.setOnClickListener(v -> {
            //openDialog();
            if (rlDetails.getVisibility() == View.VISIBLE) {
                rlDetails.setVisibility(View.GONE);
                mainBg.setBackgroundResource(R.color.white);
            } else {
                rlDetails.setVisibility(View.VISIBLE);
                rlDetails.startAnimation(slide_down);
                mainBg.setBackgroundResource(R.color.switch_background);
            }
        });

        notification_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // do something, the isChecked will be
            // true if the switch is in the On position
            if(appPreferences.getString("USERID").equalsIgnoreCase("0"))
            {

                Utility.showAlertWithEventHandling(this, Constants.LOGIN_MESSAGE, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        LoginNavigation();
                    }
                });
            }
            else {
                if (isChecked) {
                    notification_switch.setTrackResource(R.color.switch_color);
                    notification_switch.setBackgroundResource(R.drawable.rounded_switch);
                    notificationApi("1");
                } else {
                    notification_switch.setTrackResource(R.color.switch_background);
                    notification_switch.setBackgroundResource(R.drawable.rounded_switch_two);
                    notificationApi("0");
                }
            }
        });


//        menu_two.setSelected(false);
//        setTextColor(menu_two,tvMenuBooking);
//        menu_three.setSelected(true);
//        setTextColor(menu_three,tvMenuNotification);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.button_color));


//        gpsLocationReceiver = new GpsLocationReceiver();
//        IntentFilter filter2 = new IntentFilter("android.location.PROVIDERS_CHANGED");
//        registerReceiver(gpsLocationReceiver, filter2);

        //startServiceMethod();


    }


    public void LoginNavigation(){
        startActivity(new Intent(MainNavigationScreen.this,LoginScreen.class));
    }
    public void bindToken(){
        try {
            dataContext.tblUserObjectSet.fill();
            if(dataContext.tblUserObjectSet.size()>0) {
                appPreferences.set("TOKEN", dataContext.tblUserObjectSet.get(0).Token);
                Token = appPreferences.getString("TOKEN");
            }else{
                Token="";
            }

        } catch (AdaFrameworkException e) {
            e.printStackTrace();
        }
    }
    public static synchronized MainNavigationScreen getSingletonObject()
    {
        return mainNavigationScreen;
    }
    @Override
    protected void onResume() {
        //checkLocationServices();
        super.onResume();

    }

    public void logoutSession() {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                //Utility.showProgress(MainNavigationScreen.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.logoutSession(WebUtility.LOGOUT_SESSION, dataContext.tblUserObjectSet.get(0).Token,
                        dataContext.tblUserObjectSet.get(0).DeviceToken,
                        appPreferences.getString("USERID"));
                responseBodyCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                       // Utility.hideProgress();
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                System.out.println("RESPONSE IS=====" + response.body().toString());
                                deleteUser();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        System.out.println("RESPONSE IS=====" + t.getMessage());
                    //    Utility.hideProgress();
                    }
                });
            }
        } catch (Exception ex) {
        }
    }

    @Override
    protected void onStart() {

        try {
            checkCurrentUser();

            if(!appPreferences.getString("USERID").equalsIgnoreCase("0"))
            {
                bindProfile();
                usercharacter.setVisibility(View.VISIBLE);
                notification_switch.setVisibility(View.VISIBLE);
            }else{
                usercharacter.setVisibility(View.INVISIBLE);
                notification_switch.setVisibility(View.INVISIBLE);
            }

        } catch (AdaFrameworkException e) {
            e.printStackTrace();
        }

        menu_one.setSelected(true);
        setTextColor(menu_one, tvMenuOne);

        menu_two.setSelected(false);
        setTextColor(menu_two, tvMenuBooking);

        menu_three.setSelected(false);
        setTextColor(menu_three, tvMenuNotification);

        menu_four.setSelected(false);
        setTextColor(menu_four, tvSettings);

        menu_five.setSelected(false);
        setTextColor(menu_five, tvShare);
        checkLocationServices();

        super.onStart();

    }

    public void setDrawerState(boolean isEnabled) {
        if (isEnabled) {
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            toggle.onDrawerStateChanged(DrawerLayout.STATE_IDLE);
            toggle.setDrawerIndicatorEnabled(true);
            toggle.syncState();

        } else {
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toggle.onDrawerStateChanged(DrawerLayout.STATE_IDLE);
            toggle.setDrawerIndicatorEnabled(false);
            toggle.syncState();
        }
    }

    public void openDrawer() {
        drawer_layout.openDrawer(Gravity.LEFT);
        KeyBoardHandling.hideSoftKeyboard(this);
    }

    public void closeDrawer() {
        drawer_layout.closeDrawer(Gravity.LEFT);
    }

    public void setDrawer() {

        if (rlDetails != null && rlDetails.getVisibility() == View.VISIBLE) {
            rlDetails.setVisibility(View.GONE);
            mainBg.setBackgroundResource(R.color.white);
        }

        //drawer.setScrimColor(Color.TRANSPARENT);
        if ((MainNavigationScreen.this instanceof OwnerDashboardScreen)) {
            setFadeAnimation(tvTitle);
            tvTitle.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);
        } else if ((MainNavigationScreen.this instanceof ParkerDashboardScreen)) {
            setFadeAnimation(tvTitle);
            tvTitle.setVisibility(View.GONE);
            toolbar.setVisibility(View.GONE);

        }
        owner.setOnClickListener(v -> {
            if (!(MainNavigationScreen.this instanceof OwnerDashboardScreen)) {
                new Handler().postDelayed(() -> {
                    if (rlDetails != null && rlDetails.getVisibility() == View.VISIBLE) {
                        rlDetails.setVisibility(View.GONE);
                        mainBg.setBackgroundResource(R.color.white);
                    }
                    drawer_layout.closeDrawer(Gravity.LEFT);
                }, 10);

                owner.setBackgroundResource(R.drawable.border_button_with_fill);
                owner.setTextColor(getResources().getColor(R.color.white));
                parker.setBackgroundResource(R.color.white);
                parker.setTextColor(getResources().getColor(R.color.black));
                //appPreferences.set("USERTYPE","2");
                switchAccountCall("2");

            } else {
                new Handler().postDelayed(() -> drawer_layout.closeDrawer(Gravity.LEFT), 100);
                return;
            }

        });
        parker.setOnClickListener(v -> {
            if (!(MainNavigationScreen.this instanceof ParkerDashboardScreen)) {
                new Handler().postDelayed(() -> {
                    if (rlDetails != null && rlDetails.getVisibility() == View.VISIBLE) {
                        rlDetails.setVisibility(View.GONE);
                        mainBg.setBackgroundResource(R.color.white);
                    }
                    rlDetails.setVisibility(View.GONE);
                    drawer_layout.closeDrawer(Gravity.LEFT);
                }, 10);

                parker.setBackgroundResource(R.drawable.border_button_with_fill);
                parker.setTextColor(getResources().getColor(R.color.white));
                owner.setBackgroundResource(R.color.white);
                owner.setTextColor(getResources().getColor(R.color.black));
                //appPreferences.set("USERTYPE","1");
                switchAccountCall("1");


            } else {
                new Handler().postDelayed(() -> drawer_layout.closeDrawer(Gravity.LEFT), 100);
                return;
            }
        });
    }

    private void setTextColor(LinearLayout ll, TTextView tv) {
        if (ll.isSelected()) {
            tv.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.menu_selected_font_color)));
            tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
        } else {
            System.out.println("CALLED=======>");
            tv.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.black)));
            tv.setTypeface(Typeface.createFromAsset(getAssets(), "ProductSansRegular.ttf"));
        }
    }

    private void setTextColor(RelativeLayout ll, TTextView tv) {
        if (ll.isSelected()) {
            tv.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.menu_selected_font_color)));
            tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
        } else {
            tv.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.black)));
            tv.setTypeface(Typeface.createFromAsset(getAssets(), "ProductSansRegular.ttf"));
        }
    }

    public void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        view.startAnimation(anim);

    }

    private void openDialog() {
        PopupWindow popup = new PopupWindow(MainNavigationScreen.this);
        View layout = getLayoutInflater().inflate(R.layout.switch_account_screen, null);
        popup.setContentView(layout);
        // Set content width and height
        popup.setWidth(R.dimen._200sdp);
        popup.setHeight(R.dimen._50sdp);
        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        // Show anchored to button
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAsDropDown(btnSwitchAccount);
    }

    private void loadAnimation() {
        //Load animation
        slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

        slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);
    }

    private void checkCurrentUser() {

        if (appPreferences != null) {
            System.out.println("USERTYPE ========>" + appPreferences.getString("USERTYPE"));
            if (appPreferences.getString("USERTYPE").equalsIgnoreCase("1")) {
                parker.setBackgroundResource(R.drawable.border_button_with_fill);
                parker.setTextColor(getResources().getColor(R.color.white));
                owner.setBackgroundResource(R.color.white);
                owner.setTextColor(getResources().getColor(R.color.black));
                tvCurrentUser.setText("Parker");
                tvMenuOne.setText("Search Parking Space");
                imgone.setImageResource(R.drawable.parker_search);
            } else {
                owner.setBackgroundResource(R.drawable.border_button_with_fill);
                owner.setTextColor(getResources().getColor(R.color.white));
                parker.setBackgroundResource(R.color.white);
                parker.setTextColor(getResources().getColor(R.color.black));
                tvCurrentUser.setText("Owner");
                tvMenuOne.setText("My Parking Space");
                imgone.setImageResource(R.drawable.ic_car);
            }
        }

    }

    private void ProfileNavigation() {
        drawer_layout.closeDrawer(Gravity.LEFT);
        if(appPreferences.getString("USERID").equalsIgnoreCase("0"))
        {

            Utility.showAlertWithEventHandling(this, Constants.LOGIN_MESSAGE, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    LoginNavigation();
                }
            });
        }
        else {
            if (appPreferences.getString("USERTYPE").equalsIgnoreCase("1")) {
                startActivity(new Intent(MainNavigationScreen.this, ParkerProfileScreen.class));
            } else
                startActivity(new Intent(MainNavigationScreen.this, OwnerProfileScreen.class));
        }


    }

    private void bindProfile() throws AdaFrameworkException {
        if(appPreferences.getString("USERID").equalsIgnoreCase("0"))
        {
            usercharacter.setVisibility(View.INVISIBLE);

            Utility.showAlertWithEventHandling(this, Constants.LOGIN_MESSAGE, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    LoginNavigation();
                }
            });
        }
        else {
            dataContext.tblUserObjectSet.fill();
            if (dataContext != null && dataContext.tblUserObjectSet.size() > 0) {
                username.setText(dataContext.tblUserObjectSet.get(0).FullName);
                usercharacter.setVisibility(View.VISIBLE);
                if (dataContext.tblUserObjectSet.get(0).FullName != null)
                    usercharacter.setText(dataContext.tblUserObjectSet.get(0).FullName.charAt(0) + "");

                appPreferences.set("TOKEN", dataContext.tblUserObjectSet.get(0).Token);
                Token = appPreferences.getString("TOKEN");
                if (dataContext.tblUserObjectSet.get(0).Notification_On_Off.equalsIgnoreCase("1")) {
                    notification_switch.setChecked(true);
                } else {
                    notification_switch.setChecked(false);
                }
            }
        }
    }


    private void switchAccountCall(final String type) {

        if(appPreferences.getString("USERID").equalsIgnoreCase("0"))
        {

//            Utility.showAlertWithEventHandling(this, Constants.LOGIN_MESSAGE, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                    LoginNavigation();
//                }
//            });


            appPreferences.set("USERTYPE", type);
            checkCurrentUser();
            if (type.equalsIgnoreCase("2")) {
                Intent i = new Intent(MainNavigationScreen.this, OwnerDashboardScreen.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.entry, R.anim.exit);
            } else if (type.equalsIgnoreCase("1")) {
                Intent i = new Intent(MainNavigationScreen.this, ParkerDashboardScreen.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.entry, R.anim.exit);
            }


        }
        else {
            try {
                if (!Utility.isNetworkAvailable(this)) {
                    Utility.showAlert(this, "please check internet connection");
                } else {
                    Utility.showProgress(MainNavigationScreen.this);
                    WebServiceCaller.ApiInterface obj = WebServiceCaller.getClient();
                    Call<LoginResponse> responseCall = obj.switchAccount(WebUtility.SWITCHACCOUNT, appPreferences.getString("USERID"), type);
                    responseCall.enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                            Utility.hideProgress();
                            if (response.isSuccessful()) {
                                LoginResponse obj = response.body();
                                if (obj.isValid()) {
                                    if (obj.ErrorCode.equalsIgnoreCase("0")) {

                                        try {
                                            if (obj.userModel != null) {
                                                appPreferences.set("USERID", obj.userModel.UserID);
                                                appPreferences.set("USERTYPE", type);
                                                insertUserData(obj.userModel);
                                                checkCurrentUser();
                                                if (type.equalsIgnoreCase("2")) {
                                                    Intent i = new Intent(MainNavigationScreen.this, OwnerDashboardScreen.class);
                                                    startActivity(i);
                                                    finish();
                                                    overridePendingTransition(R.anim.entry, R.anim.exit);
                                                } else if (type.equalsIgnoreCase("1")) {
                                                    Intent i = new Intent(MainNavigationScreen.this, ParkerDashboardScreen.class);
                                                    startActivity(i);
                                                    finish();
                                                    overridePendingTransition(R.anim.entry, R.anim.exit);
                                                }

                                            }
                                        } catch (Exception ex) {
                                        }

                                    } else
                                        Utility.showAlert(MainNavigationScreen.this, obj.ErrorMessage);

                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                            Utility.hideProgress();
                            Utility.showAlert(MainNavigationScreen.this, t.getMessage());
                        }
                    });
                }
            } catch (Exception ex) {
                Utility.hideProgress();
                Utility.showAlert(MainNavigationScreen.this, ex.getMessage());
            }
        }
    }

    private void insertUserData(tblUser user) {
        try {
            dataContext.tblUserObjectSet.fill();
            if (dataContext != null) {
                for (int i = 0; i < dataContext.tblUserObjectSet.size(); i++)
                    dataContext.tblUserObjectSet.remove(i).setStatus(Entity.STATUS_DELETED);

                dataContext.tblUserObjectSet.save();
                dataContext.tblUserObjectSet.add(user);
                dataContext.tblUserObjectSet.save();
                checkCurrentUser();
                bindProfile();
            }
        } catch (Exception ex) {

        }
    }

    public void deleteUser() {

        if(appPreferences.getString("USERID").equalsIgnoreCase("0"))
        {

            Utility.showAlertWithEventHandling(this, Constants.LOGIN_MESSAGE, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    LoginNavigation();
                }
            });
        }
        else {
            try {
                dataContext.tblUserObjectSet.fill();
                if (dataContext != null) {
                    for (int i = 0; i < dataContext.tblUserObjectSet.size(); i++)
                        dataContext.tblUserObjectSet.remove(i).setStatus(Entity.STATUS_DELETED);

                    dataContext.tblUserObjectSet.save();
                    appPreferences.set("USERTYPE", "");
                    appPreferences.set("USERID", "");
                    appPreferences.set("LATITUDE", "");
                    appPreferences.set("LONGITUDE", "");
                    appPreferences.set("TOKEN", "");
                    appPreferences.removeValue("USERTYPE");
                    appPreferences.removeValue("USERID");
                    appPreferences.removeValue("LATITUDE");
                    appPreferences.removeValue("LONGITUDE");
                    appPreferences.removeValue("TOKEN");
                }

                dataContext.parkingSpaceObjectSet.fill();

                for (int i = 0; i < dataContext.parkingSpaceObjectSet.size(); i++)
                    dataContext.parkingSpaceObjectSet.remove(i).setStatus(Entity.STATUS_DELETED);

                dataContext.parkingSpaceObjectSet.save();


                dataContext.carsObjectSet.fill();

                for (int i = 0; i < dataContext.carsObjectSet.size(); i++)
                    dataContext.carsObjectSet.remove(i).setStatus(Entity.STATUS_DELETED);

                dataContext.carsObjectSet.save();


                dataContext.propertyAvailableTimesObjectSet.fill();
                for (int i = 0; i < dataContext.propertyAvailableTimesObjectSet.size(); i++)
                    dataContext.propertyAvailableTimesObjectSet.remove(i).setStatus(Entity.STATUS_DELETED);

                dataContext.propertyAvailableTimesObjectSet.save();

                dataContext.shortStayObjectSet.fill();

                for (int i = 0; i < dataContext.shortStayObjectSet.size(); i++)
                    dataContext.shortStayObjectSet.remove(i).setStatus(Entity.STATUS_DELETED);

                dataContext.shortStayObjectSet.save();


                dataContext.propertyDetailObjectSet.fill();
                for (int i = 0; i < dataContext.propertyDetailObjectSet.size(); i++)
                    dataContext.propertyDetailObjectSet.remove(i).setStatus(Entity.STATUS_DELETED);

                dataContext.propertyDetailObjectSet.save();


                dataContext.propertyImageObjectSet.fill();
                for (int i = 0; i < dataContext.propertyImageObjectSet.size(); i++)
                    dataContext.propertyImageObjectSet.remove(i).setStatus(Entity.STATUS_DELETED);

                dataContext.propertyImageObjectSet.save();

                finish();
                startActivity(new Intent(MainNavigationScreen.this, LoginScreen.class));
//            android.os.Process.killProcess(android.os.Process.myPid());
//            System.exit(1);
            } catch (Exception ex) {
            }
        }
    }

    public void transparentStatusBar() {
        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Application")
                .setMessage("Are you sure you want to close this application?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);

                    }

                })
                .setNegativeButton("No", null)
                .show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public void checkLocationServices() {
        LocationProvider provider=new LocationProvider(this, new LocationProvider.LocationCallback() {
            @Override
            public void handleNewLocation(Location location) {
                mLastLocation=location;
            }
        });
        provider.connect();
//        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        boolean gps_enabled = false;
//        boolean network_enabled = false;
//
//        try {
//            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        } catch (Exception ex) {
//        }
//
//        try {
//            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        } catch (Exception ex) {
//        }
//
//        if (!gps_enabled && !network_enabled) {
//            // notify user
//            new AlertDialog.Builder(MainNavigationScreen.this)
//                    .setCancelable(false)
//                    .setMessage("Location services is disabled by user,please turn on the location services.")
//                    .setPositiveButton("open settings", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//
//                        }
//                    }).show();
//        }


    }

    private void notificationApi(String isnotify) {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(MainNavigationScreen.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.setNotificationOnOff(WebUtility.NOTIFICATION_ON_OFF, appPreferences.getString("USERID"),isnotify);
                responseBodyCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Utility.hideProgress();
                        try {
                            dataContext.tblUserObjectSet.fill();
                            if(isnotify.equalsIgnoreCase("1"))
                                dataContext.tblUserObjectSet.get(0).Notification_On_Off="1";
                            else if(isnotify.equalsIgnoreCase("0"))
                                dataContext.tblUserObjectSet.get(0).Notification_On_Off="0";

                            dataContext.tblUserObjectSet.save();
                        } catch (AdaFrameworkException e) {
                            e.printStackTrace();
                        }
                        if(dataContext.tblUserObjectSet.get(0).Notification_On_Off.equalsIgnoreCase("1")){
                            notification_switch.setChecked(true);
                        }else{
                            notification_switch.setChecked(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Utility.hideProgress();
                    }
                });

            }
        } catch (Exception ex) {
            Utility.hideProgress();
        }
    }

}
