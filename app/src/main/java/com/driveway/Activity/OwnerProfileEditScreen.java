package com.driveway.Activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import com.driveway.APIResponse.LoginResponse;
import com.driveway.Component.BButton;
import com.driveway.Component.Dialogs;
import com.driveway.Component.EEditText;
import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.DBHelper.tblUser;
import com.driveway.R;
import com.driveway.Utility.Constants;
import com.driveway.Utility.DataContext;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.mobandme.ada.Entity;
import com.mobandme.ada.exceptions.AdaFrameworkException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerProfileEditScreen extends BaseActivity implements View.OnClickListener {

    AppCompatImageView back;
   public EEditText edtFirstName,edtLastName,edtDOB,edtPostalcode,edtEmail;
   public TTextView UserName,UserCharacter,tvMale,tvFemale,tvOther,UserType,btnSave;


    DataContext dataContext;
    String gender="Male";
    String selectDate="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_profile_editscreen);
        dataContext=new DataContext(this);
        bindComponent();
        whiteStatusBar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindData();
    }

    private void bindComponent(){
        edtFirstName=findViewById(R.id.edtFirstName);
        edtLastName=findViewById(R.id.edtLastName);
        UserName=findViewById(R.id.UserName);
        UserCharacter=findViewById(R.id.UserCharacter);
        tvMale=findViewById(R.id.tvMale);
        tvFemale=findViewById(R.id.tvFemale);
        tvOther=findViewById(R.id.tvOther);
        edtEmail=findViewById(R.id.edtEmail);
        edtPostalcode=findViewById(R.id.edtPostalcode);
        edtDOB=findViewById(R.id.edtDOB);
        UserType=findViewById(R.id.UserType);
        back=findViewById(R.id.back);
        btnSave=findViewById(R.id.btnSave);

        back.setOnClickListener(this);
        edtDOB.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        edtFirstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                    setFocusColor(edtFirstName);
            }
        });
        edtLastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                setFocusColor(edtLastName);
            }
        });
        edtPostalcode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                setFocusColor(edtPostalcode);
            }
        });
        edtDOB.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                setFocusColor(edtDOB);
            }
        });
        edtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                setFocusColor(edtEmail);
            }
        });

        tvMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender="Male";
                tvMale.setBackgroundResource(R.drawable.border_button_with_fill);
                tvMale.setTextColor(getResources().getColor(R.color.white));
                tvFemale.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three_watch);
                tvFemale.setTextColor(getResources().getColor(R.color.black));
                tvOther.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three_watch);
                tvOther.setTextColor(getResources().getColor(R.color.black));
            }
        });

        tvFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender="Female";
                tvMale.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three_watch);
                tvMale.setTextColor(getResources().getColor(R.color.black));
                tvFemale.setBackgroundResource(R.drawable.border_button_with_fill);
                tvFemale.setTextColor(getResources().getColor(R.color.white));
                tvOther.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three_watch);
                tvOther.setTextColor(getResources().getColor(R.color.black));
            }
        });
        tvOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender="Other";
                tvOther.setBackgroundResource(R.drawable.border_button_with_fill);
                tvOther.setTextColor(getResources().getColor(R.color.white));

                tvFemale.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three_watch);
                tvFemale.setTextColor(getResources().getColor(R.color.black));

                tvMale.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three_watch);
                tvMale.setTextColor(getResources().getColor(R.color.black));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:finish();break;
            case R.id.edtDOB:
                Dialogs.openTimePickerDialog(OwnerProfileEditScreen.this, edtDOB);
                break;
            case R.id.btnSave:validateInput();break;
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    private void setFocusColor(EEditText e1){
        if(e1.hasFocus()){
            e1.setTextColor(getResources().getColor(R.color.button_color));
            e1.setTypeface(e1.getTypeface(), Typeface.BOLD);

        }else{
            e1.setTypeface(e1.getTypeface(), Typeface.NORMAL);
            e1.setTextColor(getResources().getColor(R.color.edittext_profile_not_focus_color));

        }
    }

    private void bindData(){
        try {
            if(dataContext!=null) {
                dataContext.tblUserObjectSet.fill();
                if (dataContext.tblUserObjectSet.size() > 0) {
                    edtFirstName.setText(dataContext.tblUserObjectSet.get(0).FirstName + "");
                    edtLastName.setText(dataContext.tblUserObjectSet.get(0).LastName + "");
                    UserName.setText(dataContext.tblUserObjectSet.get(0).FullName + "");
                    UserCharacter.setText(dataContext.tblUserObjectSet.get(0).FullName.charAt(0) + "");
                    UserType.setText(dataContext.tblUserObjectSet.get(0).UserType.equalsIgnoreCase("2")?"Owner":"Parker");
                    if(!dataContext.tblUserObjectSet.get(0).BirthDate.isEmpty())
                        edtDOB.setText(Constants.converDateProfile(dataContext.tblUserObjectSet.get(0).BirthDate)+"");

//                    try {
//                        edtDOB.setText(dataContext.tblUserObjectSet.get(0).BirthDate+"");
//                    }catch (Exception ex){
//
//                    }


                    edtPostalcode.setText(dataContext.tblUserObjectSet.get(0).ZipCode + "");
                    edtEmail.setText(dataContext.tblUserObjectSet.get(0).Email+"");
                    if(dataContext.tblUserObjectSet.get(0).Gender.equalsIgnoreCase("1")){
                        gender="Male";
                        tvMale.setBackgroundResource(R.drawable.border_button_with_fill);
                        tvMale.setTextColor(getResources().getColor(R.color.white));
                        tvFemale.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three_watch);
                        tvFemale.setTextColor(getResources().getColor(R.color.black));
                        tvOther.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three_watch);
                        tvOther.setTextColor(getResources().getColor(R.color.black));
                    }
                    else if(dataContext.tblUserObjectSet.get(0).Gender.equalsIgnoreCase("Male")){
                        gender="Male";
                        tvMale.setBackgroundResource(R.drawable.border_button_with_fill);
                        tvMale.setTextColor(getResources().getColor(R.color.white));
                        tvFemale.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three_watch);
                        tvFemale.setTextColor(getResources().getColor(R.color.black));
                        tvOther.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three_watch);
                        tvOther.setTextColor(getResources().getColor(R.color.black));
                    }else if(dataContext.tblUserObjectSet.get(0).Gender.equalsIgnoreCase("female")){
                        gender="Female";
                        tvMale.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three_watch);
                        tvMale.setTextColor(getResources().getColor(R.color.black));
                        tvFemale.setBackgroundResource(R.drawable.border_button_with_fill);
                        tvFemale.setTextColor(getResources().getColor(R.color.white));
                        tvOther.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three_watch);
                        tvOther.setTextColor(getResources().getColor(R.color.black));
                    }
                    else{
                        gender="other";
                        tvMale.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three_watch);
                        tvMale.setTextColor(getResources().getColor(R.color.black));
                        tvFemale.setBackgroundResource(R.drawable.border_button_with_fill_fragment_three_watch);
                        tvFemale.setTextColor(getResources().getColor(R.color.black));
                        tvOther.setBackgroundResource(R.drawable.border_button_with_fill);
                        tvOther.setTextColor(getResources().getColor(R.color.white));

                    }
                }
            }
        } catch (AdaFrameworkException e) {
            e.printStackTrace();
        }
    }

    private void validateInput(){
        if(TextUtils.isEmpty(edtFirstName.getText().toString().trim())){
            Utility.showAlert(this,"Please enter first name");
        }else if(TextUtils.isEmpty(edtLastName.getText().toString().trim())){
            Utility.showAlert(this,"Please enter last name");
        }else if(TextUtils.isEmpty(edtPostalcode.getText().toString().trim())){
            Utility.showAlert(this,"Please enter postcode");
        }else if(Utility.checkAustralianZipCode(edtPostalcode.getText().toString().trim(),this) ==false) {
            Utility.showAlert(this, "Please enter valid postcode");
        }else if(TextUtils.isEmpty(edtEmail.getText().toString().trim())){
            Utility.showAlert(this,"Please enter email id");
        }else if (!Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()) {
            Utility.showAlert(this, "Please enter valid email id");
        }else{
                 editProfileCall();
        }
    }

    private void editProfileCall() {

        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(OwnerProfileEditScreen.this);
                WebServiceCaller.ApiInterface obj = WebServiceCaller.getClient();
                Call<LoginResponse> responseCall = obj.editProfile(WebUtility.EDITPROFILE,
                        edtFirstName.getText().toString().trim(),edtLastName.getText().toString().trim(),gender,
                        Constants.converDateProfileTwo(edtDOB.getText().toString()),
                        edtPostalcode.getText().toString().trim(),
                        appPreferences.getString("USERID"));
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
                                            insertUserData(obj.userModel);
                                            Utility.showAlertwithFinish(OwnerProfileEditScreen.this,obj.ErrorMessage);
                                        }
                                    }catch (Exception ex){}

                                }
                                else {
                                    if(obj.ErrorCode.equalsIgnoreCase("10")){
                                        deleteUser();
                                    }else
                                        Utility.showAlert(OwnerProfileEditScreen.this, obj.ErrorMessage);
                                }

                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Utility.hideProgress();
                        Utility.showAlert(OwnerProfileEditScreen.this, t.getMessage());
                    }
                });
            }
        } catch (Exception ex) {
            Utility.hideProgress();
            Utility.showAlert(OwnerProfileEditScreen.this, ex.getMessage());
        }
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

            }
        }catch (Exception ex){

        }
    }



}


