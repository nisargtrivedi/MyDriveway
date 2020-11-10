package com.driveway.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.driveway.Component.BButton;
import com.driveway.Component.TTextView;
import com.driveway.R;
import com.driveway.Utility.AppPreferences;
import com.driveway.Utility.DataContext;
import com.mobandme.ada.exceptions.AdaFrameworkException;

public class MainScreen extends AppCompatActivity implements View.OnClickListener {

    BButton SignIn,SignUp,Skip;

    private static int SPLASH_TIME_OUT = 3000;
    private static final int REQUEST_WRITE_STORAGE = 1001;
    private static final int INTERNET_REQUEST = 1002;
    private static final int READ_PHONE_REQUEST = 1003;
    private static final int CAMERA = 1004;

    AppPreferences appPreferences;

    public static String TOKEN="";

    DataContext dataContext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
        bindComponent();
        appPreferences=new AppPreferences(this);
        dataContext=new DataContext(this);
        try {
            dataContext.tblUserObjectSet.fill();
            if(dataContext.tblUserObjectSet.size()>0)
                TOKEN=dataContext.tblUserObjectSet.get(0).Token;
        } catch (AdaFrameworkException e) {
            e.printStackTrace();
        }


//            if(key.equalsIgnoreCase("notification_type")){
//                String value = getIntent().getExtras().getString(key);
//                String property_id=getIntent().getExtras().getString("parkingId");
//                startActivity(new Intent(MainScreen.this,NotificationChat_.class)
//                        .putExtra("property_id",property_id)
//                );
//                finish();
//            }
//            if(getIntent().getExtras().getString("notification_type")!=null) {
//                String value = getIntent().getExtras().getString("notification_type");
//
//            }
//





        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED   && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) MainScreen.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainScreen.this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("Permission necessary");
                alertBuilder.setMessage("Write calendar permission is necessary to write event!!!");
                alertBuilder.setPositiveButton(android.R.string.yes, (dialog, which) -> ActivityCompat.requestPermissions((Activity)MainScreen.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_WRITE_STORAGE));
                AlertDialog alert = alertBuilder.create();
                alert.show();

            } else {
                ActivityCompat.requestPermissions((Activity)MainScreen.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_WRITE_STORAGE);
            }
        } else {
            permission();
        }


    }
    private void bindComponent(){
        SignUp=findViewById(R.id.SignUp);
        SignIn=findViewById(R.id.SignIn);
        Skip=findViewById(R.id.Skip);
        SignIn.setOnClickListener(this);
        SignUp.setOnClickListener(this);
        Skip.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.SignIn:SignInNavigation();break;
            case R.id.SignUp:SignUpNavigation();break;
            case R.id.Skip:
                appPreferences.set("USERID","0");
                appPreferences.set("USERTYPE","1");
                ParkerNavigation();
                break;
        }
    }



    private void SignUpNavigation(){
        try{
            finish();
            //overridePendingTransition(R.anim.entry, R.anim.exit);
            startActivity(new Intent(MainScreen.this,SignUpScreen.class));
        }catch (Exception ex){

        }
    }
    private void SignInNavigation(){
        try{
            finish();
            //overridePendingTransition(R.anim.entry, R.anim.exit);
            startActivity(new Intent(MainScreen.this,LoginScreen.class));
        }catch (Exception ex){

        }
    }

    public void permission()
    {

        boolean hasPermission2 = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        boolean hasPermission3 = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        boolean hasPermission34 = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
        boolean hasPermission36 = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);

        System.out.println("PERMISSION 2 => "+hasPermission2);
        System.out.println("PERMISSION 3 => "+hasPermission3);
        System.out.println("PERMISSION 34 => "+hasPermission34);
        System.out.println("PERMISSION 36 => "+hasPermission36);

        Bundle bundle=getIntent().getExtras();
        if (getIntent().getExtras() != null) {

            if (getIntent().getExtras().containsKey("parkingId")) {
                System.out.println("CALLED DEFAULT===>");
                System.out.println("VALUES===>"+bundle.get("parkingId").toString()+"----");
                String value = bundle.get("parkingId").toString();
                String friendid = bundle.get("friendId").toString();
                startActivity(new Intent(MainScreen.this, NotificationChat_.class)
                        .putExtra("property_id", value)
                        .putExtra("friend_id", friendid)
                );
                finish();
            }
            else {
                if (hasPermission2 && hasPermission3 && hasPermission34 && hasPermission36) {
                    if (!TextUtils.isEmpty(appPreferences.getString("USERID")))
                    {
                        if (!TextUtils.isEmpty(appPreferences.getString("USERTYPE"))) {
                            if (appPreferences.getString("USERTYPE").equalsIgnoreCase("2"))
                                MainNavigation();
                            else {
                                ParkerNavigation();
                            }
                        } else {
                            MainNavigation();
                        }
                    }
                } else {
                    System.out.println("PERMISSION ELSE CALLED");
                }
            }
        }else {
            if (hasPermission2 && hasPermission3 && hasPermission34 && hasPermission36) {
                if (!TextUtils.isEmpty(appPreferences.getString("USERID"))  && (!appPreferences.getString("USERID").equalsIgnoreCase("0"))) {
                    if (!TextUtils.isEmpty(appPreferences.getString("USERTYPE"))) {
                        if (appPreferences.getString("USERTYPE").equalsIgnoreCase("2"))
                            MainNavigation();
                        else
                            ParkerNavigation();
                    } else {
                        MainNavigation();
                    }
                }
            } else {
                System.out.println("PERMISSION ELSE CALLED");
            }
        }
    }

    private void MainNavigation(){
        try{
            finish();
            startActivity(new Intent(MainScreen.this,OwnerDashboardScreen.class));
        }catch (Exception ex){

        }
    }
    private void ParkerNavigation(){
        try{
            finish();
            startActivity(new Intent(MainScreen.this,ParkerDashboardScreen.class));
        }catch (Exception ex){

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case REQUEST_WRITE_STORAGE:
            case INTERNET_REQUEST:
            case READ_PHONE_REQUEST:
            case CAMERA:
                permission();
                break;
        }
    }



}
