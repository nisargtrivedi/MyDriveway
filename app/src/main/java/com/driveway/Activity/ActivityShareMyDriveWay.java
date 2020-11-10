package com.driveway.Activity;

import android.content.Intent;

import com.driveway.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_share_inmydriveway)
public class ActivityShareMyDriveWay extends BaseActivity {

    @AfterViews
    public void init(){
        whiteStatusBar();
    }
    @Click
    public void back(){
        finish();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Click
    public void llShareNow(){
        shareData("QPark app is really useful. Try it !!!");
    }

    private void shareData(String msg){
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        }catch (Exception ex){

        }
    }
    @Click
    public void llInvite(){
        startActivity(new Intent(this,ActivityInvite_.class));
    }

}
