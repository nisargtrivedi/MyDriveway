package com.driveway.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.driveway.Component.TTextView;
import com.driveway.R;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;


@EActivity(R.layout.affiliation_detail)
public class AffilityDetail extends BaseActivity {

    @ViewById
    AppCompatImageView img;

    @ViewById
    TTextView ownerName;

    @ViewById
    TTextView companyName;

    @ViewById
    TTextView emailID;

    @ViewById
    TTextView phoneNumber;

    @ViewById
    TTextView website;

    @AfterViews
    public void init(){

        whiteStatusBar();
        Intent intent=getIntent();
        if(intent!=null){
            ownerName.setText(intent.getStringExtra("owner"));
            companyName.setText(intent.getStringExtra("companyname"));
            emailID.setText(intent.getStringExtra("email"));
            phoneNumber.setText(intent.getStringExtra("phone"));
            website.setText(intent.getStringExtra("website"));
            Picasso.with(this).load(intent.getStringExtra("image")).into(img);
        }
    }

    @Click
    public void website(){
        if(!TextUtils.isEmpty(website.getText().toString().trim())){

            if (!website.getText().toString().startsWith("http://") && !website.getText().toString().startsWith("https://")){
                String url=website.getText().toString();
                website.setText( "http://" + url);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website.getText().toString()));
                startActivity(browserIntent);
            }else{
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website.getText().toString()));
                startActivity(browserIntent);
            }
        }
    }
    @Click
    public void phoneNumber(){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity)AffilityDetail.this, new String[]{Manifest.permission.CALL_PHONE}, 100);
        }else{
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber.getText().toString()));
            startActivity(callIntent);
        }
    }

    @Click
    public void emailID(){
        sendEmailIntent(emailID.getText().toString());
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

    private void sendEmailIntent(String email){
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
            Intent chooser = Intent.createChooser(emailIntent, "Mail to");
            if (emailIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(chooser);
            }
        }catch (Exception ex){

        }
    }
}

