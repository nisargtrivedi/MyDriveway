package com.driveway.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import com.driveway.APIResponse.RegistrationResponse;
import com.driveway.Component.Dialogs;
import com.driveway.Component.EEditText;
import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.DBHelper.tblUser;
import com.driveway.Model.SearchPropertyModel;
import com.driveway.R;
import com.driveway.Utility.AppPreferences;
import com.driveway.Utility.Constants;
import com.driveway.Utility.DataContext;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.mobandme.ada.Entity;
import com.mobandme.ada.exceptions.AdaFrameworkException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpScreen extends AppCompatActivity implements View.OnClickListener {

   public EEditText edtFirstName, edtLastName, edtDOB, edtPostalcode, edtEmail, edtPassword, edtConfirmPassword;
   public TTextView tvMale, tvFemale,tvOther, btnSignUp, linkSignIn,tv,tv2;
    public CheckBox chkTerms;
    String gender = "male";
    AppPreferences appPreferences;
    DataContext dataContext;
    String selectedDate;
    SearchPropertyModel parkingSpace;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singup_screen);
        appPreferences = new AppPreferences(this);
        dataContext = new DataContext(this);
        bindComponent();

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void bindComponent() {
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        edtDOB = findViewById(R.id.edtDOB);
        edtPostalcode = findViewById(R.id.edtPostalcode);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        tvMale = findViewById(R.id.tvMale);
        tvFemale = findViewById(R.id.tvFemale);
        tvOther=findViewById(R.id.tvOther);
        btnSignUp = findViewById(R.id.btnSignUp);
        linkSignIn = findViewById(R.id.linkSignIn);
        chkTerms=findViewById(R.id.chkTerms);
        tv=findViewById(R.id.tv);
        tv2=findViewById(R.id.tv2);
        linkSignIn.setOnClickListener(this);
        edtDOB.setOnClickListener(this);
        tvMale.setOnClickListener(this);
        tvFemale.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        tvOther.setOnClickListener(this);
//        WebUtility.BASE_URL+"getPageContent.php?pagecode=terms
//        <a style='color:#ffffff;' href='"+WebUtility.BASE_URL+"getPageContent.php?pagecode=privacy'><b><u>Privacy Policy</u></b></a>
        tv.setText(Html.fromHtml("I agree to the <b><u>Terms & Conditions</u></b> and "));
        tv2.setText(Html.fromHtml("<b><u>Privacy Policy</u></b>"));

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpScreen.this,TermsAndConditionActivity_.class).putExtra("name","terms"));
            }
        });
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpScreen.this,TermsAndConditionActivity_.class).putExtra("name","privacy"));
            }
        });



        edtFirstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setBackgroundColorEdittext(edtFirstName);
                } else {
                    setBackgroundEdittext(edtFirstName);
                }
            }
        });
        edtLastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setBackgroundColorEdittext(edtLastName);
                } else {
                    setBackgroundEdittext(edtLastName);
                }
            }
        });

        edtPostalcode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setBackgroundColorEdittext(edtPostalcode);
                } else {
                    setBackgroundEdittext(edtPostalcode);
                }
            }
        });

        edtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setBackgroundColorEdittext(edtEmail);
                } else {
                    setBackgroundEdittext(edtEmail);
                }
            }
        });

        edtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setBackgroundColorEdittext(edtPassword);
                } else {
                    setBackgroundEdittext(edtPassword);
                }
            }
        });


        edtConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setBackgroundColorEdittext(edtConfirmPassword);
                } else {
                    setBackgroundEdittext(edtConfirmPassword);
                }
            }
        });

        edtDOB.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setBackgroundColorEdittext(edtDOB);
                } else {
                    setBackgroundEdittext(edtDOB);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linkSignIn:
                SignInNavigation();
                break;
            case R.id.edtDOB:
                Dialogs.openTimePickerDialog(SignUpScreen.this, edtDOB);

                break;
            case R.id.tvMale:
                maleSelector();
                break;
            case R.id.tvFemale:
                femaleSelector();
                break;
            case R.id.tvOther:
                otherSelector();
                break;
            case R.id.btnSignUp:
                validateInput();
                break;
        }
    }

    private void SignInNavigation() {
        try {
            finish();
            //startActivity(new Intent(SignUpScreen.this, LoginScreen.class));
        } catch (Exception ex) {

        }
    }

    private void maleSelector() {
        tvMale.setBackgroundResource(R.drawable.rounded_border);
        tvMale.setTextColor(getResources().getColor(R.color.white));
        tvFemale.setBackgroundResource(R.drawable.rounded_border_white);
        tvFemale.setTextColor(getResources().getColor(R.color.female_text_color));
        tvOther.setBackgroundResource(R.drawable.rounded_border_white);
        tvOther.setTextColor(getResources().getColor(R.color.female_text_color));
        gender = "male";
    }

    private void femaleSelector() {
        tvFemale.setBackgroundResource(R.drawable.rounded_border);
        tvFemale.setTextColor(getResources().getColor(R.color.white));
        tvMale.setBackgroundResource(R.drawable.rounded_border_white);
        tvMale.setTextColor(getResources().getColor(R.color.female_text_color));
        tvOther.setBackgroundResource(R.drawable.rounded_border_white);
        tvOther.setTextColor(getResources().getColor(R.color.female_text_color));
        gender = "female";
    }

    private void otherSelector() {
        tvOther.setBackgroundResource(R.drawable.rounded_border);
        tvOther.setTextColor(getResources().getColor(R.color.white));
        tvMale.setBackgroundResource(R.drawable.rounded_border_white);
        tvMale.setTextColor(getResources().getColor(R.color.female_text_color));
        tvFemale.setBackgroundResource(R.drawable.rounded_border_white);
        tvFemale.setTextColor(getResources().getColor(R.color.female_text_color));
        gender = "other";
    }

    private void setBackgroundColorEdittext(EEditText e1) {
        try {
            e1.setBackgroundResource(R.drawable.edit_text_bg);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    private void setBackgroundEdittext(EEditText e1) {
        try {
            e1.setBackgroundResource(R.drawable.edit_text_bg_with_transparent);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }


    }

    private void validateInput() {
        if (TextUtils.isEmpty(edtFirstName.getText().toString())) {
            Utility.showAlert(this, "Please enter first name");
            edtFirstName.requestFocus();
        }else if (!edtFirstName.getText().toString().matches("[a-zA-Z ]+")) {
            Utility.showAlert(this, "Please enter valid first name");
            edtFirstName.requestFocus();
        } else if (TextUtils.isEmpty(edtLastName.getText().toString())) {
            Utility.showAlert(this, "Please enter last name");
            edtLastName.requestFocus();
        }else if (!edtLastName.getText().toString().matches("[a-zA-Z ]+")) {
            Utility.showAlert(this, "Please enter valid last name");
            edtLastName.requestFocus();
        }else if (TextUtils.isEmpty(edtDOB.getText().toString())) {
            Utility.showAlert(this, "Please enter date of birth");
            edtDOB.requestFocus();
            Dialogs.openTimePickerDialog(SignUpScreen.this, edtDOB);
        } else if (TextUtils.isEmpty(edtPostalcode.getText().toString())) {
            Utility.showAlert(this, "Please enter postcode");
            edtPostalcode.requestFocus();
        }else if (Utility.checkAustralianZipCode(edtPostalcode.getText().toString().trim(), this) == false) {
            Utility.showAlert(this, "Please enter valid postcode");
            edtPostalcode.requestFocus();
        }else if (TextUtils.isEmpty(edtEmail.getText().toString())) {
            Utility.showAlert(this, "Please enter email id");
            edtEmail.requestFocus();
        }else if (!Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()) {
            Utility.showAlert(this, "Please enter valid email id");
            edtEmail.requestFocus();
        }else if (TextUtils.isEmpty(edtPassword.getText().toString())) {
            Utility.showAlert(this, "Please enter password");
            edtPassword.requestFocus();
        }else if (TextUtils.isEmpty(edtConfirmPassword.getText().toString())) {
            Utility.showAlert(this, "Please enter confirm password");
            edtConfirmPassword.requestFocus();
        }else if(edtPassword.getText().length()<6){
            Utility.showAlert(this,"Password contain minimum 6 character");
            edtPassword.requestFocus();
        }else if(edtConfirmPassword.getText().length()<6){
            Utility.showAlert(this,"Confirm Password contain minimum 6 character");
            edtConfirmPassword.requestFocus();
        }else if (!edtPassword.getText().toString().trim().equalsIgnoreCase(edtConfirmPassword.getText().toString().trim())) {
            Utility.showAlert(this, "Password and confirm password must be same");
            edtConfirmPassword.requestFocus();
        }else if (!chkTerms.isChecked()) {
            Utility.showAlert(this, "Please agree to the terms & conditions and privacy policy");
        }else {
            RegistrationCall();
        }
    }

    private void RegistrationCall() {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(SignUpScreen.this);
                WebServiceCaller.ApiInterface obj = WebServiceCaller.getClient();
                Call<RegistrationResponse> responseCall = obj.register(WebUtility.REGISTRATION,
                        edtFirstName.getText().toString().trim(),
                        edtLastName.getText().toString().trim(),
                        gender,
                        edtDOB.getText().length()>0?Constants.converDateProfileTwo(edtDOB.getText().toString()):"",
                        appPreferences.getString("DEVICE_KEY"),
                        edtPassword.getText().toString().trim(),
                        edtPostalcode.getText().toString().trim(),
                        edtEmail.getText().toString().trim());
                responseCall.enqueue(new Callback<RegistrationResponse>() {
                    @Override
                    public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            RegistrationResponse obj = response.body();
                            if (obj.isValid()) {
                                if (obj.ErrorCode.equalsIgnoreCase("0")) {
                                    //Utility.showAlert(SignUpScreen.this, obj.ErrorMessage);
                                    //MainNavigation();
                                    try {
                                        if (obj.userModel != null) {
                                           // appPreferences.set("USERID", obj.userModel.UserID);
                                            //appPreferences.set("USERTYPE", obj.userModel.UserType);
                                            //insertUserData(obj.userModel,obj.ErrorMessage);
                                            MainNavigation(obj.ErrorMessage);
                                        }

                                    } catch (Exception ex) {

                                    }

                                } else if (obj.ErrorCode.equalsIgnoreCase("2")) {
                                    Utility.showAlert(SignUpScreen.this, obj.ErrorMessage);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                        Utility.hideProgress();
                        Utility.showAlert(SignUpScreen.this, t.getMessage());
                    }
                });
            }
        } catch (Exception ex) {
            Utility.hideProgress();
            Utility.showAlert(SignUpScreen.this, ex.getMessage());
        }
    }

    private void insertUserData(tblUser user,String msg) {
        try {
            dataContext.tblUserObjectSet.fill();
            if (dataContext != null) {
                for (int i = 0; i < dataContext.tblUserObjectSet.size(); i++)
                    dataContext.tblUserObjectSet.remove(i).setStatus(Entity.STATUS_DELETED);

                dataContext.tblUserObjectSet.save();
                dataContext.tblUserObjectSet.add(user);
                dataContext.tblUserObjectSet.save();
                MainNavigation(msg);

                //logData();
            }
        } catch (Exception ex) {

        }
    }

    private void MainNavigation(String message) {
        try {

            AlertDialog.Builder builder= new AlertDialog.Builder(SignUpScreen.this);
            AlertDialog alert = builder.create();
            alert.setMessage(message);
            alert.setCancelable(false);
            alert.setButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    //startActivity(new Intent(SignUpScreen.this, LoginScreen.class));
                }
            });
            alert.show();


        } catch (Exception ex) {

        }
    }

    private void logData() {
        try {
            dataContext.tblUserObjectSet.fill();
            if (dataContext != null) {
                for (int i = 0; i < dataContext.tblUserObjectSet.size(); i++) {
                    System.out.println("CURRENT USER ID = " + dataContext.tblUserObjectSet.get(i).UserID);
                    System.out.println("CURRENT USER FULL NAME = " + dataContext.tblUserObjectSet.get(i).FullName);
                    System.out.println("CURRENT USER TYPE = " + dataContext.tblUserObjectSet.get(i).UserType);
                    System.out.println("CURRENT USER GENDER = " + dataContext.tblUserObjectSet.get(i).Gender);
                }

            }
        } catch (AdaFrameworkException e) {
            e.printStackTrace();
        }

    }

}
