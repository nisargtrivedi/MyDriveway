package com.driveway.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.FileProvider;

import com.driveway.BuildConfig;
import com.driveway.Component.EEditText;
import com.driveway.Component.RoundCornersImageView;
import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.DBHelper.tblCars;
import com.driveway.DBHelper.tblPropertyImage;
import com.driveway.Model.ParkingSpace;
import com.driveway.R;
import com.driveway.WebApi.WebServiceCaller;
import com.driveway.WebApi.WebUtility;
import com.google.gson.JsonObject;
import com.mobandme.ada.Entity;
import com.mobandme.ada.exceptions.AdaFrameworkException;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParkerAddCarScreen extends BaseActivity implements View.OnClickListener {

    EEditText edtModel,edtRegisterNumber;
    Spinner edtMakeYear;
    TTextView btnSave;
    RoundCornersImageView imgCar;
    AppCompatImageView imgUpload,back;
    tblCars cars;
    LinearLayout ll_one;
    AppCompatCheckBox checkDefault;

    int year=1970;
    int currentYear=0;
    public final int GALLERY_REQUEST_CODE = 1000;
    public final int CAMERA_REQUEST_CODE = 1001;
    List<String> years=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parker_add_car_screen);
        bindComponent();
        cars=new tblCars();
        currentYear=Calendar.getInstance().get(Calendar.YEAR);
        bindYear();
        if(getIntent().getExtras()!=null){
            if(cars!=null) {
                cars.CarID = getIntent().getStringExtra("carid");
                cars.CarRegisterNumber = getIntent().getStringExtra("regno");
                cars.CarMakingYear = getIntent().getStringExtra("year");
                cars.CarImage = getIntent().getStringExtra("image");
                cars.CarModel = getIntent().getStringExtra("model");
                cars.is_default=getIntent().getStringExtra("is_default");

                edtModel.setText(cars.CarModel);
                if(cars.is_default.equalsIgnoreCase("1")) {
                    checkDefault.setChecked(true);
                    checkDefault.setEnabled(false);
                }


                for (int i=0;i<=years.size();i++){
                    if(cars.CarMakingYear.equalsIgnoreCase(edtMakeYear.getItemAtPosition(i).toString())){
                        edtMakeYear.setSelection(i);
                        break;
                    }
                }
                edtRegisterNumber.setText(cars.CarRegisterNumber);
                imgCar.setVisibility(View.VISIBLE);
                ll_one.setVisibility(View.GONE);
                Picasso.with(this).load(cars.CarImage).fit().into(imgCar);
            }
        }

    }
    private void bindYear(){
        for (int i=year;i<=currentYear;i++){
            years.add(i+"");
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        years); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        edtMakeYear.setAdapter(spinnerArrayAdapter);
    }

    private void bindComponent(){
        checkDefault=findViewById(R.id.checkDefault);
        edtMakeYear=findViewById(R.id.edtMakeYear);
        edtModel=findViewById(R.id.edtModel);
        edtRegisterNumber=findViewById(R.id.edtRegisterNumber);
        btnSave=findViewById(R.id.btnSave);
        imgCar=findViewById(R.id.imgCar);
        imgUpload=findViewById(R.id.imgUpload);
        ll_one=findViewById(R.id.ll_one);
        back=findViewById(R.id.back);
        edtModel=findViewById(R.id.edtModel);

        btnSave.setOnClickListener(this);
        imgUpload.setOnClickListener(this);
        imgCar.setOnClickListener(this);
        back.setOnClickListener(this);
        ll_one.setOnClickListener(this);
    }

    private void inputData(){
        if(TextUtils.isEmpty(edtModel.getText().toString().trim())){
            Utility.showAlert(this,"Please enter model");
        }
        else if(TextUtils.isEmpty(edtRegisterNumber.getText().toString().trim())){
            Utility.showAlert(this,"Please enter registration number");
        }
        else if(TextUtils.isEmpty(cars.CarImage)){
            Utility.showAlert(this,"Please select your car photo");
        }
        else{
            addcarAPI();
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSave:inputData();break;
            case R.id.imgUpload:
            case R.id.imgCar:
            case R.id.ll_one:
                selectOption();break;
            case R.id.back:finish();break;
        }
    }
    private void addcarAPI(){
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showAlert(this, "please check internet connection");
            }else {
                Utility.showProgress(ParkerAddCarScreen.this);
                WebServiceCaller.ApiInterface apiInterface = WebServiceCaller.getClient();

                RequestBody action =
                        RequestBody.create(MediaType.parse("multipart/form-data"), WebUtility.ADD_EDIT_CAR);
                RequestBody userId =
                        RequestBody.create(MediaType.parse("multipart/form-data"), appPreferences.getString("USERID"));
                RequestBody registerNo =
                        RequestBody.create(MediaType.parse("multipart/form-data"), edtRegisterNumber.getText().toString().trim());
                RequestBody modelNo =
                        RequestBody.create(MediaType.parse("multipart/form-data"), edtModel.getText().toString().trim());
                RequestBody makingYear =
                        RequestBody.create(MediaType.parse("multipart/form-data"), edtMakeYear.getSelectedItem().toString().trim());
                RequestBody carID =
                        RequestBody.create(MediaType.parse("multipart/form-data"), !cars.CarID.isEmpty() ? cars.CarID : "0");

                RequestBody isDefault =
                        RequestBody.create(MediaType.parse("multipart/form-data"), checkDefault.isChecked()?"1":"0");

                if (cars.CarImage!=null&&!cars.CarImage.isEmpty()) {
                    File file = new File(cars.CarImage != null ? cars.CarImage : "");
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    MultipartBody.Part img = MultipartBody.Part.createFormData("car_img", file.getName(), requestFile);

                    Call<JsonObject> responseBodyCall = cars.CarImage.toLowerCase().startsWith("http") ? apiInterface.addCars(action, userId, registerNo, modelNo, makingYear, carID,isDefault) : apiInterface.addCars(action, userId, registerNo, modelNo, makingYear, img, carID,isDefault);

                    responseBodyCall.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            Utility.hideProgress();
                            if (response.isSuccessful()) {
                                try {
                                    if (response.body() != null) {
                                        System.out.println("Error Code==>" + response.body().toString());
                                        JSONObject jsonObject = new JSONObject(response.body().toString());
                                        if (jsonObject != null) {
                                            System.out.println("Error Code==>" + jsonObject.getString("error_code"));
                                            if (jsonObject.getString("error_code").equalsIgnoreCase("0")) {
                                                Utility.showAlertwithFinish(ParkerAddCarScreen.this, jsonObject.getString("error_message"));
                                            } else {
                                                Utility.showAlert(ParkerAddCarScreen.this, jsonObject.getString("error_message"));
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
            }
        }catch (Exception ex){}
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    try {
                        onSelectFromGalleryResult(data);
                    }catch (Exception ex){System.out.println(ex.toString());}
                    break;
                case CAMERA_REQUEST_CODE:
                    try {
                        onCaptureImageResult(data);
                    }catch (Exception ex){System.out.println(ex.toString());}
                    break;
            }
        }
    }
    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    public void pickFromGallery(){
        try {
            //Create an Intent with action as ACTION_PICK
            Intent intent = new Intent(Intent.ACTION_PICK);
            // Sets the type as image/*. This ensures only components of type image are selected
            intent.setType("image/*");
            //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
            String[] mimeTypes = {"image/jpeg", "image/png"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            // Launching the Intent
            startActivityForResult(intent, GALLERY_REQUEST_CODE);
        }catch (Exception ex){System.out.println(ex.toString());}
    }
    private void launchCameraIntent() {
        Intent intent = new Intent(ParkerAddCarScreen.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(ParkerAddCarScreen.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    private void selectOption(){
        try {
//            String[] colors = {"Camera", "Gallery"};
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("Select Option");
//            builder.setItems(colors, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    try {
//                        switch (which) {
//                            case 0:
//                                cameraIntent();
//                                break;
//                            case 1:
//                                pickFromGallery();
//                                break;
//                        }
//                    } catch (Exception ex) {
//                        System.out.println(ex.toString());
//                    }
//                }
//            });
//            builder.show();
            ImagePickerActivity.showImagePickerOptions(ParkerAddCarScreen.this, new ImagePickerActivity.PickerOptionListener() {
                @Override
                public void onTakeCameraSelected() {
                    launchCameraIntent();
                }

                @Override
                public void onChooseGallerySelected() {
                    launchGalleryIntent();
                }
            });
        }catch (Exception ex){
            System.out.println(ex.toString());
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        try {

//            imgCar.setImageBitmap(null);
//            Uri ImageSelect = data.getData();
//
//            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), ImageSelect);
//            String path = saveImage(bitmap);
//
//            Log.i("GALLAY FILE",path);
//            String[] filePath = {MediaStore.Images.Media.DATA};
//            Cursor cursor = getContentResolver().query(ImageSelect,filePath,null,null,null);
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
//            cursor.moveToFirst();
//            ll_one.setVisibility(View.GONE);
//            imgCar.setVisibility(View.VISIBLE);
//            cars.CarImage = path;
//            cursor.close();
//            imgCar.setImageBitmap(bitmap);
            Uri fpath = data.getParcelableExtra("path");

            cars.CarImage=fpath.getPath().toString();
            try {
                //dataContext.propertyImageObjectSet.save(image);
                ll_one.setVisibility(View.GONE);
                imgCar.setVisibility(View.VISIBLE);
               imgCar.setImageBitmap(BitmapFactory.decodeFile(fpath.getPath().toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            Log.e("Error...",e.getMessage());
            Toast.makeText(this,"Try Again...",Toast.LENGTH_LONG).show();
        }

    }


    private void onCaptureImageResult(Intent data) {
//        imgCar.setImageBitmap(null);
//        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
//        String path = saveImage(thumbnail);
//        Log.i("CAMERA FILE",path);
//
//        ll_one.setVisibility(View.GONE);
//        imgCar.setVisibility(View.VISIBLE);
//        cars.CarImage = path;
//        imgCar.setImageBitmap(thumbnail);

        Uri fpath = data.getParcelableExtra("path");

        cars.CarImage=fpath.getPath().toString();
        try {
            //dataContext.propertyImageObjectSet.save(image);
            ll_one.setVisibility(View.GONE);
        imgCar.setVisibility(View.VISIBLE);
            imgCar.setImageBitmap(BitmapFactory.decodeFile(fpath.getPath().toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

//        imgDecodableString = path;

    }


    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File obj = new File(
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/mydriveway/");
        // have the object build the directory structure, if needed.
        if (!obj.exists()) {
            obj.mkdirs();
        }

        try {
            File f = new File(obj, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }
}
