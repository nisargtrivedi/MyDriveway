package com.driveway.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.driveway.APIResponse.ForgotPasswordResponse;
import com.driveway.APIResponse.RegistrationResponse;
import com.driveway.Component.BButton;
import com.driveway.Component.EEditText;
import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.R;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordScreen extends AppCompatActivity implements View.OnClickListener {

    EEditText edtEmail;
    BButton btnSubmit;
    AppCompatImageView Back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpassword_screen);
        bindComponent();

    }
    private void bindComponent(){
        edtEmail=findViewById(R.id.edtEmail);
        btnSubmit=findViewById(R.id.btnSubmit);
        Back=findViewById(R.id.Back);

        Back.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);


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
        edtEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
                case R.id.btnSubmit:validateInput();break;
                case R.id.Back:SignInNavigation();break;

            }
    }
    private void SignInNavigation(){
        try{
            finish();startActivity(new Intent(ForgotPasswordScreen.this,LoginScreen.class));
        }catch (Exception ex){

        }
    }
    private void validateInput(){
        if(TextUtils.isEmpty(edtEmail.getText().toString().trim())){
            Utility.showAlert(this,"Please enter email id");
        }
        else{
            if(!Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()){
                Utility.showAlert(this,"Please enter valid email id");
                edtEmail.setFocusable(true);
            }else{
                forgotPasswordCall();
            }
        }
    }

    private void forgotPasswordCall() {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(ForgotPasswordScreen.this);
                WebServiceCaller.ApiInterface obj = WebServiceCaller.getClient();
                Call<ForgotPasswordResponse> responseCall = obj.forgotPassword(WebUtility.FORGOTPASSWORD, edtEmail.getText().toString().trim());
                responseCall.enqueue(new Callback<ForgotPasswordResponse>() {
                    @Override
                    public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            ForgotPasswordResponse obj = response.body();
                            if (obj.isValid()) {
                                if (obj.ErrorCode.equalsIgnoreCase("0"))
                                    Utility.showAlertwithFinish(ForgotPasswordScreen.this, obj.ErrorMessage);
                                else
                                    Utility.showAlertwithFinish(ForgotPasswordScreen.this, obj.ErrorMessage);

                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                        Utility.hideProgress();
                        Utility.showAlert(ForgotPasswordScreen.this, t.getMessage());
                    }
                });
            }
        } catch (Exception ex) {
            Utility.hideProgress();
            Utility.showAlert(ForgotPasswordScreen.this, ex.getMessage());
        }
    }
}
