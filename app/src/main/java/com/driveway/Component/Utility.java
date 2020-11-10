package com.driveway.Component;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.driveway.Activity.BaseActivity;
import com.driveway.Activity.LoginScreen;
import com.driveway.Activity.SettingsActivity;
import com.driveway.R;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.google.gson.JsonObject;
import com.mobandme.ada.Entity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Utility {

    private static Dialog popupWindow;

    public static boolean checkAustralianZipCode(String zipcode, Activity act) {
        boolean ans = false;
        try {

//            String regex = "\\d{4}";
            String regex = "^\\d{4}$";
           // String regex="(^(0[289][0-9]{2})|([1345689][0-9]{3})|(2[0-8][0-9]{2})|(290[0-9])|(291[0-4])|(7[0-4][0-9]{2})|(7[8-9][0-9]{2})$)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(zipcode);
            ans = matcher.matches();
            System.out.println("Autrialian Zipcode value==="+ans);
        } catch (Exception ex) {

        }
        return ans;
    }

    public static boolean checkUSZipCode(String zipcode, Activity act) {
        boolean ans = false;
        try {

            String regex = "(^[0-9]{5}(?:-[0-9]{4})?$)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(zipcode);
            ans = matcher.matches();

        } catch (Exception ex) {

        }
        return ans;
    }

    public static boolean checkCanedaZipCode(String zipcode, Activity act) {
        boolean ans = false;
        try {
            String regex = "^(^[ABCEGHJKLMNPRSTVXY]\\\\d[A-Z][- ]*\\\\d[A-Z]\\\\d$)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(zipcode);
            ans = matcher.matches();
            System.out.println("Caneda Zipcode value==="+ans);
        } catch (Exception ex) {

        }
        return ans;
    }

    public static void showAlert(Activity act, String msg) {
        try {
            AlertDialog alertDialog = new AlertDialog.Builder(act).create();
            alertDialog.setTitle("");
            alertDialog.setMessage(msg);
            alertDialog.setButton(AlertDialog.BUTTON1, "OK",
                    (dialog, which) -> dialog.dismiss());
            alertDialog.show();
        } catch (Exception ex) {

        }
    }
    public static void showAlertWithEventHandling(Activity act, String msg, DialogInterface.OnClickListener onClickListener) {
        try {
            AlertDialog alertDialog = new AlertDialog.Builder(act).create();
            alertDialog.setTitle("");
            alertDialog.setMessage(msg);
            alertDialog.setButton(AlertDialog.BUTTON1,"OK",onClickListener);
            alertDialog.setButton(AlertDialog.BUTTON2, "CANCEL",
                    (dialog, which) -> dialog.dismiss());
            alertDialog.show();
        } catch (Exception ex) {

        }
    }
    public static void showAlertwithFinish(final Activity act, String msg) {
        try {
            AlertDialog alertDialog = new AlertDialog.Builder(act).create();
            alertDialog.setTitle("");
            alertDialog.setMessage(msg);
            alertDialog.setButton(AlertDialog.BUTTON1, "OK",
                    (dialog, which) -> {
                        dialog.dismiss();
                        act.finish();
                    });
            alertDialog.show();
        } catch (Exception ex) {

        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public static void showProgress(Context context){
        try {
            if (!((Activity) context).isFinishing()) {
                View layout = LayoutInflater.from(context).inflate(R.layout.loader_componenet, null);
                popupWindow = new Dialog(context);
                popupWindow.requestWindowFeature(Window.FEATURE_NO_TITLE);
                popupWindow.setContentView(layout);
                popupWindow.setCancelable(false);
                /* Custom setting to change TextView text,Color and Text Size according to your Preference*/
                if(popupWindow.getWindow() != null)
                    popupWindow.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                if (!((Activity) context).isFinishing()) {
                    popupWindow.show();
                }
            }

        } catch (Exception e)

        {
            e.printStackTrace();
        }

    }

    public static void hideProgress() {
        try {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
                popupWindow=null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    public static String QuotarMonth(String monthName){
        String month="";
        switch (monthName){
            case "january":
                    month="March";
                    break;
            case "february":
                    month="April";
                    break;
            case "march":
                    month="May";
                    break;
            case "april":
                    month="June";
                    break;
            case "may":
                    month="July";
                    break;
            case "june":
                    month="August";
                    break;
            case "july":
                    month="September";
                    break;
            case "august":
                    month="October";
                    break;
            case "september":
                    month="November";
                    break;
            case "october":
                    month="December";
                    break;

        }
        return month;
    }




}
