package com.driveway.Activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;


import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;

import com.driveway.Component.EEditText;
import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.Model.tblCard;
import com.driveway.R;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParkerAddNewCard extends BaseActivity {

    EEditText edtCardCVV,edtExpiryDate_Two,edtExpiryDate_One,edtCardNo_One,edtCardNo_Two,edtCardNo_Three,edtCardNo_Four,edtCardHolderName;
    TTextView btnSave;
    AppCompatImageView back;
    tblCard card=new tblCard();
    String CardID="0";
    AppCompatCheckBox checkDefault;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parker_add_new_card_screen);

        checkDefault=findViewById(R.id.checkDefault);
        back=findViewById(R.id.back);
        edtCardCVV=findViewById(R.id.edtCardCVV);
        edtExpiryDate_Two=findViewById(R.id.edtExpiryDate_Two);
        edtExpiryDate_One=findViewById(R.id.edtExpiryDate_One);
        edtCardNo_One=findViewById(R.id.edtCardNo_One);
        edtCardNo_Two=findViewById(R.id.edtCardNo_Two);
        edtCardNo_Three=findViewById(R.id.edtCardNo_Three);
        edtCardNo_Four=findViewById(R.id.edtCardNo_Four);
        edtCardHolderName=findViewById(R.id.edtCardHolderName);
        btnSave=findViewById(R.id.btnSave);
        back=findViewById(R.id.back);

        focusChange();

        btnSave.setOnClickListener(v -> inputData());
        back.setOnClickListener(v-> finish());

        if(getIntent().getExtras()!=null){
            card= (tblCard) getIntent().getSerializableExtra("card");
            if(card!=null){
                edtCardHolderName.setText(card.OwnerName);
                edtCardCVV.setText(card.CVV);
                edtExpiryDate_One.setText(card.CardMonth);
                edtExpiryDate_Two.setText(card.CardYear);
                edtCardNo_One.setText(card.CardNo.substring(0,4));
                edtCardNo_Two.setText(card.CardNo.substring(4,8));
                edtCardNo_Three.setText(card.CardNo.substring(8,12));
                edtCardNo_Four.setText(card.CardNo.substring(12,16));
                CardID=card.CardID;
                if(card.isDefault.equalsIgnoreCase("1")) {
                    checkDefault.setChecked(true);
                    checkDefault.setEnabled(false);
                }

            }
        }

        nextFocus();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    private void setfocusColor(EEditText e1,EEditText e2,EEditText e3,EEditText e4,EEditText e5,EEditText e6,EEditText e7,EEditText e8){
            e1.setBackgroundResource(R.drawable.round_card_editext_focus);
            e2.setBackgroundResource(R.drawable.round_card_editext);
            e3.setBackgroundResource(R.drawable.round_card_editext);
            e4.setBackgroundResource(R.drawable.round_card_editext);
            e5.setBackgroundResource(R.drawable.round_card_editext);
            e6.setBackgroundResource(R.drawable.round_card_editext);
            e7.setBackgroundResource(R.drawable.round_card_editext);
            e8.setBackgroundResource(R.drawable.round_card_editext);
    }

    private void focusChange(){
        edtCardCVV.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                    setfocusColor(edtCardCVV,edtExpiryDate_Two,edtExpiryDate_One,edtCardNo_One,edtCardNo_Two,edtCardNo_Three,edtCardNo_Four,edtCardHolderName);
            }
        });
        edtCardHolderName.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                setfocusColor(edtCardHolderName,edtExpiryDate_Two,edtExpiryDate_One,edtCardNo_One,edtCardNo_Two,edtCardNo_Three,edtCardNo_Four,edtCardCVV);
            }
        });
        edtCardNo_Four.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                setfocusColor(edtCardNo_Four,edtCardHolderName,edtExpiryDate_Two,edtExpiryDate_One,edtCardNo_One,edtCardNo_Two,edtCardNo_Three,edtCardCVV);
            }
        });
        edtCardNo_One.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                setfocusColor(edtCardNo_One,edtCardNo_Four,edtCardHolderName,edtExpiryDate_Two,edtExpiryDate_One,edtCardNo_Two,edtCardNo_Three,edtCardCVV);
            }
        });
        edtCardNo_Three.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                setfocusColor(edtCardNo_Three,edtCardNo_One,edtCardNo_Four,edtCardHolderName,edtExpiryDate_Two,edtExpiryDate_One,edtCardNo_Two,edtCardCVV);
            }
        });
        edtExpiryDate_One.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                setfocusColor(edtExpiryDate_One,edtCardNo_Three,edtCardNo_One,edtCardNo_Four,edtCardHolderName,edtExpiryDate_Two,edtCardNo_Two,edtCardCVV);
            }
        });

        edtExpiryDate_Two.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                setfocusColor(edtExpiryDate_Two,edtExpiryDate_One,edtCardNo_Three,edtCardNo_One,edtCardNo_Four,edtCardHolderName,edtCardNo_Two,edtCardCVV);
            }
        });

        edtCardNo_Two.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                setfocusColor(edtCardNo_Two,edtExpiryDate_Two,edtExpiryDate_One,edtCardNo_Three,edtCardNo_One,edtCardNo_Four,edtCardHolderName,edtCardCVV);
            }
        });
    }

    private void inputData(){
        if(TextUtils.isEmpty(edtCardHolderName.getText().toString().trim())){
                Utility.showAlert(this,"Please enter credit card holder name");
        }else if(TextUtils.isEmpty(edtCardNo_One.getText().toString().trim())){
            Utility.showAlert(this,"Please enter credit card number");
        }else if(TextUtils.isEmpty(edtCardNo_Two.getText().toString().trim())){
            Utility.showAlert(this,"Please enter credit card number");
        }else if(TextUtils.isEmpty(edtCardNo_Three.getText().toString().trim())){
            Utility.showAlert(this,"Please enter credit card number");
        }
        else if(TextUtils.isEmpty(edtCardNo_Four.getText().toString().trim())){
            Utility.showAlert(this,"Please enter credit card number");
        }
        else if(TextUtils.isEmpty(edtExpiryDate_One.getText().toString().trim())){
            Utility.showAlert(this,"Please enter expiry month");
        }
        else if(TextUtils.isEmpty(edtExpiryDate_Two.getText().toString().trim())){
            Utility.showAlert(this,"Please enter expiry year");
        }
        else if(TextUtils.isEmpty(edtCardCVV.getText().toString().trim())){
            Utility.showAlert(this,"Please enter CVV number");
        }else{

            if(edtCardNo_One.getText().length()<4){
                Utility.showAlert(this,"Please Enter proper card number");
            } else if(edtCardNo_Two.getText().length()<4){
                Utility.showAlert(this,"Please Enter proper card number");
            } else if(edtCardNo_Three.getText().length()<4){
                Utility.showAlert(this,"Please Enter proper card number");
            } else if(edtCardNo_Four.getText().length()<4){
                Utility.showAlert(this,"Please Enter proper card number");
            } else if(edtCardNo_Four.getText().length()<2){
                Utility.showAlert(this,"Please enter valid expiry month");
            } else if(edtExpiryDate_One.getText().length()<2){
                Utility.showAlert(this,"Please enter valid expiry year");
            } else{
                if(Integer.parseInt(edtExpiryDate_One.getText().toString())>=1 && Integer.parseInt(edtExpiryDate_One.getText().toString())<=12)
                    addCardAPI(edtCardNo_One.getText().toString().trim()+edtCardNo_Two.getText().toString().trim()+edtCardNo_Three.getText().toString().trim()+edtCardNo_Four.getText().toString().trim(),CardID);
                else
                    Utility.showAlert(this,"Please enter valid expiry month");

            }
        }

    }

    private void addCardAPI(String cardno,String cardID){
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            }else {
                Utility.showProgress(ParkerAddNewCard.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.addCard(WebUtility.ADDCARD,appPreferences.getString("USERID"),edtCardHolderName.getText().toString().trim(),cardno,edtExpiryDate_One.getText().toString().trim(),edtExpiryDate_Two.getText().toString().trim(),edtCardCVV.getText().toString().trim(),cardID,checkDefault.isChecked()?"1":"0");
                responseBodyCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            try {
                                if (response.body() != null) {
                                    System.out.println("Error Code==>"+response.body().toString());
                                    JSONObject jsonObject=new JSONObject(response.body().toString());
                                    if(jsonObject!=null){
                                        System.out.println("Error Code==>"+jsonObject.getString("error_code"));
                                        if(jsonObject.getString("error_code").equalsIgnoreCase("0")){
                                            Utility.showAlertwithFinish(ParkerAddNewCard.this, jsonObject.getString("error_message").toString());
                                        }else {
                                            Utility.showAlert(ParkerAddNewCard.this, jsonObject.getString("error_message").toString());
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
                        Utility.hideProgress();
                    }
                });
            }
        }catch (Exception ex){}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    private void nextFocus(){
        edtCardNo_One.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(edtCardNo_One.getText().length()==4){
                    edtCardNo_Two.requestFocus();
                }
            }
        });
        edtCardNo_Two.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(edtCardNo_Two.getText().length()==4){
                    edtCardNo_Three.requestFocus();
                }
            }
        });
        edtCardNo_Three.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(edtCardNo_Three.getText().length()==4){
                    edtCardNo_Four.requestFocus();
                }
            }
        });
        edtCardNo_Four.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(edtCardNo_Four.getText().length()==4){
                    edtExpiryDate_One.requestFocus();
                }
            }
        });
        edtExpiryDate_One.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(edtExpiryDate_One.getText().length()==2){
                    edtExpiryDate_Two.requestFocus();
                }
            }
        });

        edtExpiryDate_Two.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(edtExpiryDate_Two.getText().length()==2){
                    edtCardCVV.requestFocus();
                }
            }
        });
    }
}
