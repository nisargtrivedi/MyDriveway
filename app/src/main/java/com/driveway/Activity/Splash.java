package com.driveway.Activity;

import android.content.Intent;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.driveway.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.splash)
public class Splash extends AppCompatActivity {

    Runnable r=new Runnable() {
        @Override
        public void run() {
                finish();startActivity(new Intent(Splash.this,MainScreen.class));
        }
    };
    Handler handler;
    @AfterViews
    public void init(){
        handler=new Handler();
        handler.postDelayed(r,3000);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if(handler!=null){
            handler.removeCallbacks(r);
            finish();
        }
    }
}
