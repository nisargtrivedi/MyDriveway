package com.driveway.Fragment;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.driveway.Activity.ImagePickerActivity;
import com.driveway.Activity.OwnerPropertyAddScreen;
import com.driveway.Adapters.OwnerParkingImageAdapter;
import com.driveway.BuildConfig;
import com.driveway.Component.BButton;
import com.driveway.Component.Utility;
import com.driveway.DBHelper.tblPropertyImage;
import com.driveway.R;
import com.driveway.Utility.DataContext;
import com.driveway.listeners.onImageDelete;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class OwnerPropertyUploadPhotoFragment extends Fragment implements View.OnClickListener {


    AppCompatImageView imgUpload;
    BButton btnNext;
    RecyclerView rvImage;
    LinearLayout ll_one;

    public final int GALLERY_REQUEST_CODE = 1000;
    public final int CAMERA_REQUEST_CODE = 1001;

    //keep track of cropping intent
    public final int PIC_CROP = 3;
    public final int CAMERA_CROP_REQUEST_CODE = 1003;
    private String cameraFilePath;
    private Uri picUri;

    DataContext dataContext;
    OwnerParkingImageAdapter adapter;
    File mTempCameraPhotoFile;

    OwnerPropertyAddScreen activity;
    private static final float maxHeight = 816.0f;
    private static final float maxWidth = 612.0f;


    String path;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.owner_add_property_fragment_two, container, false);
        imgUpload=v.findViewById(R.id.imgUpload);
        rvImage=v.findViewById(R.id.rvImage);
        btnNext=v.findViewById(R.id.btnNext);
        ll_one=v.findViewById(R.id.ll_one);
        dataContext=new DataContext(getActivity());

        imgUpload.setOnClickListener(this);
        ll_one.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity=((OwnerPropertyAddScreen)getActivity());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        bindImage();

    }
    @UiThread
    private void bindImage(){
        try {
            if(activity!=null) {
//                dataContext.propertyImageObjectSet.fill();
//                list.addAll(dataContext.propertyImageObjectSet);

//                if(adapter.list.size()==2){
//                    ll_one.setVisibility(View.GONE);
//                }else{
//                    ll_one.setVisibility(View.VISIBLE);
//                }
                adapter=new OwnerParkingImageAdapter(getActivity(),activity.list , new onImageDelete() {
                    @Override
                    public void onDelete(tblPropertyImage image) {

                        try {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("")
                                    .setCancelable(false)
                                    .setMessage("Do you want delete this image ?")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            try {

                                                adapter.remove(image);
                                                activity.list.remove(image);
                                                adapter.notifyDataSetChanged();
//                                                dataContext.propertyImageObjectSet.fill("id = ?", new String[]{image.getID() + ""}, null);
//                                                if(dataContext.propertyImageObjectSet.size()>0)
//                                                    dataContext.propertyImageObjectSet.remove(0).setStatus(Entity.STATUS_DELETED);
//                                                for(int i=0;i<list.size();i++){
//                                                    if(list.get(i).ImagePath.equalsIgnoreCase(image.ImagePath)){
//                                                        list.remove(i);
//                                                        adapter.notifyDataSetChanged();
//                                                        break;
//                                                    }
//                                                }
//                                                dataContext.propertyImageObjectSet.save();
//                                                adapter.notifyDataSetChanged();
                                                if(adapter.list.size()==2){
                                                    ll_one.setVisibility(View.GONE);
                                                }else{
                                                    ll_one.setVisibility(View.VISIBLE);
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }})
                                    .setNegativeButton(android.R.string.no, null).show();



                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onEdit(tblPropertyImage image) {

                        try {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("")
                                    .setCancelable(false)
                                    .setMessage("Do you want replace this image ?")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            try {
//                                                dataContext.propertyImageObjectSet.fill("id = ?", new String[]{image.getID() + ""}, null);
//                                                if(dataContext.propertyImageObjectSet.size()>0)
//                                                    dataContext.propertyImageObjectSet.remove(0).setStatus(Entity.STATUS_DELETED);
//                                                for(int i=0;i<list.size();i++){
//                                                    if(list.get(i).ImagePath.equalsIgnoreCase(image.ImagePath)){
//                                                        list.remove(i);
//                                                        adapter.notifyDataSetChanged();
//                                                        break;
//                                                    }
//                                                }
//                                                dataContext.propertyImageObjectSet.save();
//                                                adapter.notifyDataSetChanged();
//                                                if(adapter.list.size()==2){
//                                                    ll_one.setVisibility(View.GONE);
//                                                }else{
//                                                    ll_one.setVisibility(View.VISIBLE);
//                                                }
                                                adapter.remove(image);
                                                activity.list.remove(image);
                                                adapter.notifyDataSetChanged();
                                                if(adapter.list.size()==2){
                                                    ll_one.setVisibility(View.GONE);
                                                }else{
                                                    ll_one.setVisibility(View.VISIBLE);
                                                }

                                                selectOption();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }})
                                    .setNegativeButton(android.R.string.no, null).show();



                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                rvImage.setLayoutManager(new LinearLayoutManager(getActivity()));
                rvImage.setAdapter(adapter);

                if(adapter.list.size()==2){
                    ll_one.setVisibility(View.GONE);
                }else{
                    ll_one.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ll_one:
                case R.id.imgUpload:
                        if(activity.list.size()<2){
                            selectOption();
                        }else{
                            Utility.showAlert(getActivity(),"You can add maximum two(2) images.");
                        }
                    break;
                case R.id.btnNext:
                        if(activity.list.size()==0){
                            Utility.showAlert(getActivity(),"Please select at least 1 property photo");
                        }else{
                            ((OwnerPropertyAddScreen)getActivity()).navigation(2);break;
                        }
            }
        }catch (Exception ex){System.out.println(ex.toString());}
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
                    case PIC_CROP:
                    try {
                        Bundle extras = data.getExtras();
                        //get the cropped bitmap
                        Bitmap thePic = (Bitmap) extras.get("data");

                        path= saveImage(thePic);
                        tblPropertyImage image = new tblPropertyImage();
                        image.ImagePath = path;
                        image.ImageType="camera";
                        try {
                            //dataContext.propertyImageObjectSet.save(image);
                            adapter.add(image);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();
                        if(adapter.list.size()==2){
                            ll_one.setVisibility(View.GONE);
                        }else{
                            ll_one.setVisibility(View.VISIBLE);
                        }


                    }catch (Exception ex){System.out.println(ex.toString());
                    }
                    break;

                case CAMERA_CROP_REQUEST_CODE:
                            if(resultCode==Activity.RESULT_OK){
                                path=saveImage((Bitmap) data.getExtras().get("data"));
                                //path=data.getData().getPath();
                                tblPropertyImage image = new tblPropertyImage();
                                image.ImagePath = path;
                                image.ImageType="camera";
                                try {
                                    //dataContext.propertyImageObjectSet.save(image);
                                    adapter.add(image);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                adapter.notifyDataSetChanged();
                                if(adapter.list.size()==2){
                                    ll_one.setVisibility(View.GONE);
                                }else{
                                    ll_one.setVisibility(View.VISIBLE);
                                }
                            }
                                        break;


            }
        }
    }
    private void launchCameraIntent() {
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
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
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }
    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //This is the directory in which the file will be created. This is the default location of Camera photos
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for using again
        cameraFilePath = "file://" + image.getAbsolutePath();
        return image;
    }


//    private void cameraIntent()
//    {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent, CAMERA_REQUEST_CODE);
//    }
    public void captureFromCamera() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", createImageFile()));
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void cameraIntent() {
        // Intent intent = new Intent(ChatActivity.this,CameraActivity.class);
        //startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra("android.intent.extra.quickCapture", true);
        // Intent takePictureIntent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File exportDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()  ,"mydriveway" );
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            mTempCameraPhotoFile = new File(exportDir, "/" + Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            picUri = Uri.fromFile(mTempCameraPhotoFile); // convert path to Uri
            //Log.d(LOG_TAG, "/" + UUID.randomUUID().toString().replaceAll("-", "") + ".jpg");
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTempCameraPhotoFile));
            startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
        }
    }


    public void pickFromGallery(){
        try {
            //Create an Intent with action as ACTION_PICK
            Intent intent = new Intent(Intent.ACTION_PICK);
            // Sets the type as image/*. This ensures only components of type image are selected
            intent.setType("image/*");
            intent.putExtra("crop", "true");
            //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
            String[] mimeTypes = {"image/jpeg", "image/png"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            // Launching the Intent
            startActivityForResult(intent, GALLERY_REQUEST_CODE);
        }catch (Exception ex){System.out.println(ex.toString());}
    }

    private void selectOption(){
//        try {
//            String[] colors = {"Take photo","From camera roll"};
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            builder.setTitle("Select Option");
//            builder.setItems(colors, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    try {
//                        switch (which) {
//                            case 0:
////                                cameraIntent();
//                                launchCameraIntent();
//                                break;
//                            case 1:
//                                launchGalleryIntent();
////                                pickFromGallery();
//                                break;
//                        }
//                    } catch (Exception ex) {
//                        System.out.println(ex.toString());
//                    }
//                }
//            });
//            builder.show();
//        }catch (Exception ex){
//            System.out.println(ex.toString());
//        }
        ImagePickerActivity.showImagePickerOptions(getActivity(), new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void onSelectFromGalleryResult(Intent data) {
        try {
            Uri fpath = data.getParcelableExtra("path");
            tblPropertyImage image = new tblPropertyImage();
            image.ImagePath = fpath.getPath().toString();
            image.ImageType="gallery";
            try {
                //dataContext.propertyImageObjectSet.save(image);
                adapter.add(image);
            } catch (Exception e) {
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();
//            Uri ImageSelect = data.getData();
//
//            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), ImageSelect);
//            String path = saveImage(bitmap);
//
//            Log.i("GALLAY FILE",path);
//            String[] filePath = {MediaStore.Images.Media.DATA};
//            Cursor cursor = getActivity().getContentResolver().query(ImageSelect,filePath,null,null,null);
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
//            cursor.moveToFirst();
//
//
//
//
////            code below crop functionality
//                tblPropertyImage image = new tblPropertyImage();
//                image.ImagePath = path;
//                image.ImageType="gallery";
//                //dataContext.propertyImageObjectSet.save(image);
//                adapter.add(image);
//                adapter.notifyDataSetChanged();
//            if(adapter.list.size()==2){
//                ll_one.setVisibility(View.GONE);
//            }else{
//                ll_one.setVisibility(View.VISIBLE);
//            }
//
//
//            cursor.close();



        }catch (Exception e){
            Log.e("Error...",e.getMessage());
            Toast.makeText(getActivity(),"Try Again...",Toast.LENGTH_LONG).show();
        }

    }


    private void onCaptureImageResult(Intent data) {
        //profilepic.setImageBitmap(null);

        Uri fpath = data.getParcelableExtra("path");
//        String fpath = mTempCameraPhotoFile.getPath();
//        Bitmap thumbnail;
//        String path = saveImage(thumbnail);
       // Log.i("CAMERA FILE",fpath);

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), fpath);
            String path = saveImage(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //new ImageCompression().execute(fpath.toString());
        //img.setImageURI(Uri.parse(cameraFilePath));
            tblPropertyImage image = new tblPropertyImage();
            image.ImagePath = fpath.getPath().toString();
            image.ImageType="camera";
            try {
                //dataContext.propertyImageObjectSet.save(image);
                adapter.add(image);
            } catch (Exception e) {
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();
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
            MediaScannerConnection.scanFile(getActivity(),
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

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public class ImageCompression extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            if (strings.length == 0 || strings[0] == null)
                return null;

            return compressImage(strings[0]);
        }

        protected void onPostExecute(String imagePath) {
            // imagePath is path of new compressed image.

            //picUri=Uri;
            //carry out the crop operation
            //performCrop();

            path=imagePath;
            tblPropertyImage image = new tblPropertyImage();
            image.ImagePath = path;
            image.ImageType="camera";
            try {
                //dataContext.propertyImageObjectSet.save(image);
                adapter.add(image);
            } catch (Exception e) {
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();
            if(adapter.list.size()==2){
                ll_one.setVisibility(View.GONE);
            }else{
                ll_one.setVisibility(View.VISIBLE);
            }

            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
//indicate image type and Uri
            cropIntent.setDataAndType(Uri.fromFile(mTempCameraPhotoFile), "image/*");
//set crop properties
            cropIntent.putExtra("crop", "true");
//indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
//indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
//retrieve data on return
            cropIntent.putExtra("return-data", true);
//start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CAMERA_CROP_REQUEST_CODE);

//            path=imagePath;
//            tblPropertyImage image = new tblPropertyImage();
//            image.ImagePath = path;
//            image.ImageType="camera";
//            try {
//                //dataContext.propertyImageObjectSet.save(image);
//                adapter.add(image);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            adapter.notifyDataSetChanged();
//            if(adapter.list.size()==2){
//                ll_one.setVisibility(View.GONE);
//            }else{
//                ll_one.setVisibility(View.VISIBLE);
//            }


            // appPreferences.set("path",new File(imagePath).getAbsolutePath());
            //img.setImageBitmap(BitmapFactory.decodeFile(new File(imagePath).getAbsolutePath()));

            path=imagePath;
           // tblPropertyImage image = new tblPropertyImage();
            image.ImagePath = path;
            image.ImageType="camera";
            try {
                //dataContext.propertyImageObjectSet.save(image);
                adapter.add(image);
            } catch (Exception e) {
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();
            if(adapter.list.size()==2){
                ll_one.setVisibility(View.GONE);
            }else{
                ll_one.setVisibility(View.VISIBLE);
            }
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }
    public static String compressImage(String imagePath) {
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        float imgRatio = (float) actualWidth / (float) actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

                try {
                    bmp = BitmapFactory.decodeFile(imagePath, options);
                } catch (OutOfMemoryError exception) {
                    exception.printStackTrace();
                }
                try {
                    scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565);
                } catch (OutOfMemoryError exception) {
                    exception.printStackTrace();
                }
        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
//        if (bmp != null) {
//            bmp.recycle();
//        }
        ExifInterface exif;
        try {
            exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        String filepath = imagePath;
        try {
            //new File(imageFilePath).delete();
            out = new FileOutputStream(filepath);

            //write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filepath;
    }

}
