package com.driveway.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.core.app.ActivityCompat;

import com.driveway.Adapters.InviteAdapter;
import com.driveway.Component.Utility;
import com.driveway.Model.ContactModel;
import com.driveway.R;
import com.driveway.Utility.KeyBoardHandling;
import com.driveway.listeners.onInviteClick;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;


@EActivity(R.layout.activity_invite)
public class ActivityInvite extends BaseActivity {

    @ViewById
    ListView lvFriends;
    @ViewById
    ImageView selectall;
    @ViewById
    ImageView send;
    @ViewById
    EditText edtSearch;


    public final static int RequestPermissionCode=1001;

    Cursor cursor ;
    ArrayList<ContactModel> models=new ArrayList<>();
    InviteAdapter adapter;
    @AfterViews
    public void init(){
            whiteStatusBar();
            EnableRuntimePermission();
            adapter=new InviteAdapter(this,models);
            lvFriends.setAdapter(adapter);
            adapter.LayoutCLickListener(new onInviteClick() {
                @Override
                public void onLongClick(ContactModel model) {
                    selectall.setVisibility(View.VISIBLE);
                    send.setVisibility(View.VISIBLE);
                    model.isSelect=true;
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onClick(ContactModel model) {
                    selectall.setVisibility(View.VISIBLE);
                    send.setVisibility(View.VISIBLE);
                    model.isSelect=true;
                    adapter.notifyDataSetChanged();
                }
            });
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!TextUtils.isEmpty(edtSearch.getText().toString())){
                    adapter.getFilter().filter(s.toString());
                }else{
                    KeyBoardHandling.hideSoftKeyboard(ActivityInvite.this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    @Click
    public void selectall(){
        if(adapter!=null) {
            if(selectall.isSelected()){
                selectall.setSelected(false);
                adapter.deselectAll();
                adapter.notifyDataSetChanged();
            }else {
                selectall.setSelected(true);
                adapter.selecttAll();
                adapter.notifyDataSetChanged();
            }
        }
    }
    @Click
    public void send(){
        try {
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + android.text.TextUtils.join(";", adapter.phoneNumbers())));
            smsIntent.putExtra("sms_body", "QPark app is really useful. Try it !!!");
            startActivity(smsIntent);
        }catch (Exception ex){
            Utility.showAlert(ActivityInvite.this,"default sms app not found");
        }
    }
    public void EnableRuntimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                ActivityInvite.this,
                Manifest.permission.READ_CONTACTS))
        {

            Utility.showAlert(this,"permission needed to access contacts");

        } else {

            ActivityCompat.requestPermissions(ActivityInvite.this,new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);

        }
    }
    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    //Utility.showAlert(this,"permission needed to access contacts");
                    GetContacts();
                } else {
                    Utility.showAlert(this,"Permission Canceled, Now your application cannot access CONTACTS.");
                }
                break;
        }
    }

    public void GetContacts(){

        try {
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

            while (cursor.moveToNext()) {
                ContactModel contactModel = new ContactModel();
                contactModel.Name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                contactModel.Phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contactModel.isSelect = false;
                models.add(contactModel);
            }
            adapter.notifyDataSetChanged();
            cursor.close();
        }catch (Exception ex){

        }
    }
    @Click
    public void back(){
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
