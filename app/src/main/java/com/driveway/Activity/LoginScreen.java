package com.driveway.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;

import com.driveway.APIResponse.ForgotPasswordResponse;
import com.driveway.APIResponse.LoginResponse;
import com.driveway.Component.EEditText;
import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.DBHelper.tblUser;
import com.driveway.Model.SearchPropertyModel;
import com.driveway.R;
import com.driveway.Utility.AppPreferences;
import com.driveway.Utility.DataContext;
import com.driveway.Utility.LocationProvider;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.mobandme.ada.Entity;
import com.mobandme.ada.exceptions.AdaFrameworkException;

import org.androidannotations.annotations.Background;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginScreen extends AppCompatActivity implements View.OnClickListener, LocationProvider.LocationCallback {

    EEditText edtEmail,edtPassword;
    TTextView btnSignIn,linkSignUp;
    TTextView linkForgotPassword;
    AppPreferences appPreferences;
    DataContext dataContext;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    LocationProvider provider;
    SearchPropertyModel parkingSpace;

    String PageRedirection="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        appPreferences=new AppPreferences(this);
        dataContext=new DataContext(this);
        bindComponent();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        provider=new LocationProvider(this,this);
        provider.connect();
        appPreferences.set("lat","0");
        appPreferences.set("lng","0");

        if(getIntent().getExtras()!=null){
            if(getIntent().getSerializableExtra("booking_details")!=null){
                parkingSpace =new SearchPropertyModel();
                parkingSpace= (SearchPropertyModel) getIntent().getSerializableExtra("booking_details");
            }else if(getIntent().getStringExtra("pagename")!=null){
                PageRedirection=getIntent().getStringExtra("pagename");
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
//        if(!TextUtils.isEmpty(appPreferences.getString("USERID"))){
//            System.out.println("USERID======="+appPreferences.getString("USERID"));
//
//            if(!TextUtils.isEmpty(appPreferences.getString("USERTYPE"))){
//                if(appPreferences.getString("USERTYPE").equalsIgnoreCase("2"))
//                    MainNavigation();
//                else
//                    ParkerNavigation();
//            }else{
//                MainNavigation();
//            }
//        }
    }

    private void bindComponent(){
        edtEmail=findViewById(R.id.edtEmail);
        edtPassword=findViewById(R.id.edtPassword);
        btnSignIn=findViewById(R.id.btnSignIn);
        linkSignUp=findViewById(R.id.linkSignUp);
        linkForgotPassword=findViewById(R.id.linkForgotPassword);
        btnSignIn.setOnClickListener(this);
        linkSignUp.setOnClickListener(this);
        linkForgotPassword.setOnClickListener(this);

        edtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    setBackgroundColorEdittext(edtEmail);
                } else {
                    setBackgroundEdittext(edtEmail);
                }
            }
        });

        edtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    setBackgroundColorEdittext(edtPassword);
                } else {
                    setBackgroundEdittext(edtPassword);
                }
            }
        });

        edtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    validateInput();
                    return true;
                }
                return false;
            }
        });

    }
    private void setBackgroundColorEdittext(EEditText e1){
        try {
                e1.setBackgroundResource(R.drawable.edit_text_bg);
        }catch (Exception ex){
            System.out.println(ex.toString());
        }
    }

    private void setBackgroundEdittext(EEditText e1){
        try {
            e1.setBackgroundResource(R.drawable.edit_text_bg_with_transparent);
        }catch (Exception ex){
            System.out.println(ex.toString());
        }


    }

    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.linkSignUp:SignUpNavigation();break;
                case R.id.btnSignIn:validateInput();break;
                case R.id.linkForgotPassword:ForgotPasswordNavigation();break;
            }
    }
    private void SignUpNavigation(){
        try{
            startActivity(new Intent(LoginScreen.this,SignUpScreen.class));
        }catch (Exception ex){

        }
    }
    private void MainNavigation(){
        try{
            finish();
            startActivity(new Intent(LoginScreen.this,OwnerDashboardScreen.class));
        }catch (Exception ex){

        }
    }
    private void ParkerNavigation(){
        try{
            finish();
            startActivity(new Intent(LoginScreen.this,ParkerDashboardScreen.class));
        }catch (Exception ex){

        }
    }
    private void ForgotPasswordNavigation(){
        try{
            finish();
            overridePendingTransition(R.anim.entry, R.anim.exit);
            startActivity(new Intent(LoginScreen.this,ForgotPasswordScreen.class));
        }catch (Exception ex){

        }
    }

    private void validateInput(){
        if(TextUtils.isEmpty(edtEmail.getText().toString().trim())){
            Utility.showAlert(this,"Please enter email id");
        }else if(!Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()){
            Utility.showAlert(this,"Please enter valid email id");
        }else if(TextUtils.isEmpty(edtPassword.getText().toString().trim())){
                Utility.showAlert(this,"Please enter password");
        }else{
            loginCall();
        }
    }

    private void loginCall() {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(LoginScreen.this);
                WebServiceCaller.ApiInterface obj = WebServiceCaller.getClient();
                Call<LoginResponse> responseCall = obj.login(WebUtility.LOGIN, edtEmail.getText().toString().trim(),edtPassword.getText().toString().trim(),appPreferences.getString("DEVICE_KEY"));
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
                                            appPreferences.set("USERID",obj.userModel.UserID);
                                            appPreferences.set("USERTYPE",obj.userModel.UserType);
                                            appPreferences.set("TOKEN",obj.userModel.Token);
                                            MainNavigationScreen.Token=obj.userModel.Token;
                                            signInWithToken(obj.userModel);

                                        }
                                    }catch (Exception ex){}

                                }
                                else
                                    Utility.showAlert(LoginScreen.this, obj.ErrorMessage);
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Utility.hideProgress();
                        Utility.showAlert(LoginScreen.this, t.getMessage());
                    }
                });
            }
        } catch (Exception ex) {
            Utility.hideProgress();
            Utility.showAlert(LoginScreen.this, ex.getMessage());
        }
    }

    @UiThread
    public void signInWithToken(tblUser obj) {
        mAuth.signInWithCustomToken(obj.fcm_token)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCustomToken:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                           // System.out.println("FIREBAASE USER ===>" + user.getEmail());
                            //System.out.println("FIREBAASE USER ID ===>" + user.getUid());
                            //System.out.println("FIREBAASE USER NAME ===>" + user.getDisplayName());
                            obj.APIToken = user.getUid();
                            insertUserData(obj);

                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                            Toast.makeText(LoginScreen.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }

    private void insertUserData(tblUser user){
        try{
            dataContext.tblUserObjectSet.fill();
            if(dataContext!=null){
                for(int i=0;i<dataContext.tblUserObjectSet.size();i++)
                    dataContext.tblUserObjectSet.remove(i).setStatus(Entity.STATUS_DELETED);

                dataContext.tblUserObjectSet.save();
                dataContext.tblUserObjectSet.add(user);
                dataContext.tblUserObjectSet.save();
                //MainNavigation();

                if(parkingSpace!=null){
                    finish();
                    startActivity(new Intent(LoginScreen.this, ParkerBookingStayScreen.class)
                            .putExtra("booking_details", parkingSpace));
                }else if(!PageRedirection.isEmpty()){
                    if(PageRedirection.equalsIgnoreCase("setting")){
                        finish();
                        startActivity(new Intent(LoginScreen.this, SettingsActivity.class));
                    }else if(PageRedirection.equalsIgnoreCase("card")){
                        finish();
                        startActivity(new Intent(LoginScreen.this, ParkerManageCard.class));
                    }else if(PageRedirection.equalsIgnoreCase("bank")){
                        finish();
                        startActivity(new Intent(LoginScreen.this, BankActivity_.class));
                    }
                }
                else {
                    if (!TextUtils.isEmpty(user.UserType)) {
                        if (user.UserType.equalsIgnoreCase("2"))
                            MainNavigation();
                        else
                            ParkerNavigation();
                    } else {
                        MainNavigation();
                    }
                }

                //logData();
            }
        }catch (Exception ex){

        }
    }

    private void logData(){
        try {
            dataContext.tblUserObjectSet.fill();
            if(dataContext!=null) {
                for (int i = 0; i < dataContext.tblUserObjectSet.size(); i++){
                    System.out.println("CURRENT USER ID = "+dataContext.tblUserObjectSet.get(i).UserID);
                    System.out.println("CURRENT USER FULL NAME = "+dataContext.tblUserObjectSet.get(i).FullName);
                    System.out.println("CURRENT USER TYPE = "+dataContext.tblUserObjectSet.get(i).UserType);
                    System.out.println("CURRENT USER GENDER = "+dataContext.tblUserObjectSet.get(i).Gender);
                    System.out.println("CURRENT TOKEN = "+dataContext.tblUserObjectSet.get(i).Token);
                }

            }
        } catch (AdaFrameworkException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(provider!=null){
            provider.disconnect();
        }
    }

    @Override
    public void handleNewLocation(Location location) {

        try {
            appPreferences.set("lat",location.getLatitude()+"");
            appPreferences.set("lng",location.getLongitude()+"");
            //MyDriveWayApplication.Lat = location.getLatitude();
            //MyDriveWayApplication.Lng=location.getLongitude();
        }catch (Exception ex){

        }

    }
}
