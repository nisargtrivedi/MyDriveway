package com.driveway.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import com.driveway.Component.EEditText;
import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.R;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordScreen extends BaseActivity implements View.OnClickListener {

    TTextView btnSubmit;
    AppCompatImageView back;
    EEditText edtConfirmPassword,edtNewPassword,edtCurrentPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        back=findViewById(R.id.back);
        btnSubmit=findViewById(R.id.btnSubmit);
        edtCurrentPassword=findViewById(R.id.edtCurrentPassword);
        edtNewPassword=findViewById(R.id.edtNewPassword);
        edtConfirmPassword=findViewById(R.id.edtConfirmPassword);

        btnSubmit.setOnClickListener(this);
        back.setOnClickListener(this);
        whiteStatusBar();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSubmit:
                if(TextUtils.isEmpty(edtCurrentPassword.getText().toString().trim())){
                    Utility.showAlert(this,"Please enter current password");
                }else if(TextUtils.isEmpty(edtNewPassword.getText().toString().trim())){
                    Utility.showAlert(this,"Please enter new password");
                }else if(edtNewPassword.getText().length()<6){
                    Utility.showAlert(this,"New Password contain minimum 6 character");
                }else if(TextUtils.isEmpty(edtConfirmPassword.getText().toString().trim())){
                    Utility.showAlert(this,"Please enter confirm password");
                }else if(edtConfirmPassword.getText().length()<6){
                    Utility.showAlert(this,"Confirm password contain minimum 6 character");
                }else if(!edtConfirmPassword.getText().toString().equalsIgnoreCase(edtNewPassword.getText().toString())){
                    Utility.showAlert(this,"Please enter must be same as new password and confirm password");
                }else{
                    apiChangePassword(edtNewPassword.getText().toString().trim(),edtCurrentPassword.getText().toString());
                }
                break;
            case R.id.back:finish();break;
        }
    }

    private void filterDialog(Activity act) {
        View v = LayoutInflater.from(this).inflate(R.layout.change_password_dialog, null, false);
        BottomSheetDialog dialog = new BottomSheetDialog(ChangePasswordScreen.this, R.style.TransparentDialog);
        dialog.setContentView(v);
        TTextView close = v.findViewById(R.id.btnSubmit);
        close.setOnClickListener(v1 -> {
            dialog.dismiss();
            act.finish();

        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.entry, R.anim.exit);
    }
    private void apiChangePassword(String newpassword,String oldpassword) {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(ChangePasswordScreen.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.changePassword(WebUtility.CHANGE_PASSWORD,newpassword,appPreferences.getString("USERID"),oldpassword);
                responseBodyCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                System.out.println("RESPONSE IS=====" + response.body().toString());
                                if (jsonObject != null) {
                                        filterDialog(ChangePasswordScreen.this);
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        System.out.println("RESPONSE IS=====" + t.getMessage());
                        Utility.hideProgress();
                    }
                });
            }
        } catch (Exception ex) {
        }
    }
}
