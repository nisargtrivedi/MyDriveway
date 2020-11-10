package com.driveway.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.driveway.Component.BButton;
import com.driveway.Component.EEditText;
import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.Model.HowAppWorkModel;
import com.driveway.R;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.google.gson.JsonObject;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EActivity(R.layout.activity_bank)
public class BankActivity extends BaseActivity {

    @ViewById
    EEditText edtBSB;
    @ViewById
    EEditText edtAccountNumber;
    @ViewById
    EEditText edtAccountHolder;
    @ViewById
    BButton btnSubmit;
    @ViewById
    BButton btnTransfer;
    @ViewById
    TTextView tvWalletAmount;


    String bankID="0";
    String walletAmount="0";

    @AfterViews
    public void init(){

        whiteStatusBar();
        getBank();
    }
    @Click
    public void btnSubmit(){
        if(TextUtils.isEmpty(edtBSB.getText().toString().trim())){
            Utility.showAlert(this,"Please enter BSB");
        }
        else if(TextUtils.isEmpty(edtAccountNumber.getText().toString().trim())){
            Utility.showAlert(this,"Please enter account number");

        }
        else if(TextUtils.isEmpty(edtAccountHolder.getText().toString().trim())){
            Utility.showAlert(this,"Please enter account holder name");

        }else{
            addBank();
        }
    }
    @Click
    public void back(){
        if(bankID.equalsIgnoreCase("0")) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage("Property will not be listed until bank details are filled up.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            setResult(Activity.RESULT_OK,getIntent().putExtra("BANK","Done"));
                            finish();
                        }

                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }else{
            setResult(Activity.RESULT_OK,getIntent().putExtra("BANK","Done"));
            finish();
        }

    }

    @Override
    public void onBackPressed() {

        if(bankID.equalsIgnoreCase("0")) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage("Property will not be listed until bank details are filled up.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            setResult(Activity.RESULT_OK,getIntent().putExtra("BANK","Done"));
                            finish();
                        }

                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }else{
            setResult(Activity.RESULT_OK,getIntent().putExtra("BANK","Done"));
            finish();
        }
    }

    @Click
    public void btnTransfer(){
        withdrawAmount();
    }
    private void addBank() {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(BankActivity.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.addBank(
                        WebUtility.BANK,
                        appPreferences.getString("USERID"),
                        edtBSB.getText().toString(),
                        edtAccountHolder.getText().toString(),
                        edtAccountNumber.getText().toString(),
                        bankID
                );
                responseBodyCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                if(jsonObject.optInt("error_code")==0){

                                    setResult(Activity.RESULT_OK,getIntent().putExtra("BANK","Done"));
                                    finish();
                                   // Utility.showAlertwithFinish(BankActivity.this,jsonObject.optString("error_message"));
                                }else{
                                    if(jsonObject.optInt("error_code")==10){
                                        deleteUser();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            System.out.println("ELSE PART CALLED");
                        }
                    }
                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Utility.hideProgress();
                        setResult(Activity.RESULT_OK,getIntent().putExtra("BANK","Done"));
                        finish();
                        System.out.println("FAIL PART CALLED");
                    }
                });
            }
        } catch (Exception ex) {
        }
    }


    private void getBank() {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(BankActivity.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.getBank(
                        WebUtility.GET_BANK,
                        appPreferences.getString("USERID")
                );
                responseBodyCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                if(jsonObject.optInt("error_code")==0){
                                    JSONArray array=jsonObject.optJSONArray("data");
                                    if(array.length()>0){
                                        edtBSB.setText(array.optJSONObject(0).optString("bsb"));
                                        edtAccountHolder.setText(array.optJSONObject(0).optString("account_name"));
                                        edtAccountNumber.setText(array.optJSONObject(0).optString("account_number"));
                                        if(!TextUtils.isEmpty(array.optJSONObject(0).optString("account_number"))){
                                            //Toast.makeText(BankActivity.this,array.optJSONObject(0).optString("account_number").toString().length()+"",Toast.LENGTH_LONG).show();
                                            if(array.optJSONObject(0).optString("account_number").toString().length()>4) {
                                                String mask = array.optJSONObject(0).optString("account_number").replaceAll("\\w(?=\\w{4})", "*");
                                                edtAccountNumber.setText(mask);
                                            }

                                        }
                                        bankID=array.optJSONObject(0).optString("bank_id");
                                        tvWalletAmount.setText("$"+array.optJSONObject(0).optString("wallet_amount"));
                                        walletAmount=array.optJSONObject(0).optString("wallet_amount");

                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if(walletAmount.toString().equalsIgnoreCase("0") | walletAmount.toString().equalsIgnoreCase("0.00")){
                                btnTransfer.setVisibility(View.GONE);
                            }else{
                                btnTransfer.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Utility.hideProgress();
                    }
                });
            }
        } catch (Exception ex) {
        }
    }


    private void withdrawAmount() {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            } else {
                Utility.showProgress(BankActivity.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();
                Call<JsonObject> responseBodyCall = apiInterface.getWithDrawAmount(
                        WebUtility.WITHDRAW,
                        appPreferences.getString("USERID"),
                        walletAmount
                );
                responseBodyCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                if(jsonObject.optInt("error_code")==0){
                                    walletAmount=jsonObject.optString("wallet_amount");
                                    tvWalletAmount.setText(jsonObject.optString("wallet_amount"));
                                    Utility.showAlertwithFinish(BankActivity.this,jsonObject.optString("error_message"));
                                }else{
                                    Utility.showAlertwithFinish(BankActivity.this,jsonObject.optString("error_message"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Utility.hideProgress();
                    }
                });
            }
        } catch (Exception ex) {
        }
    }
}
