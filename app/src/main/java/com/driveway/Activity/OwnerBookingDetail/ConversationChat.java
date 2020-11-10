package com.driveway.Activity.OwnerBookingDetail;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.driveway.Activity.ActivityChatScreen;
import com.driveway.Activity.BaseActivity;
import com.driveway.Activity.ImagePickerActivity;
import com.driveway.Activity.ParkerBooking.BookingChat;
import com.driveway.Adapters.ChatAdapter;
import com.driveway.Adapters.ChatAdapterTwo;
import com.driveway.Component.EEditText;
import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.driveway.Model.ChatModel;
import com.driveway.Model.ConversationModel;
import com.driveway.Model.SearchPropertyModel;
import com.driveway.R;
import com.driveway.Utility.Constants;
import com.driveway.Utility.KeyBoardHandling;
import com.driveway.WebApi.WebUtility;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobandme.ada.exceptions.AdaFrameworkException;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@EActivity(R.layout.activity_chat)
public class ConversationChat extends BaseActivity {

    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference tblConversationmy;
    DatabaseReference tblChatUserFriendList;

    @ViewById
    ListView lvChat;
    @ViewById
    TTextView tvUserName;
    @ViewById
    TTextView tvUserAddress;
    @ViewById
    EEditText edtChat;


    ArrayList<ChatModel> list = new ArrayList<>();
    ChatAdapterTwo adapter;

    String ConversationID;
    String id;
    SearchPropertyModel bookingObj;


    public final int GALLERY_REQUEST_CODE = 1002;
    public final int CAMERA_REQUEST_CODE = 1001;
    private String cameraFilePath;
    File mTempCameraPhotoFile;
    private static final float maxHeight = 1280.0f;
    private static final float maxWidth = 1280.0f;


    String propertyID = "";
    String friendName = "";
    String friendFCM = "";
    String propertyName = "";

    public static boolean isActive = false;

    @AfterViews
    public void init() {

        whiteStatusBar();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        if (getIntent().getExtras() != null) {

            if (getIntent().getStringExtra("property_name") != null) {
                propertyName = getIntent().getStringExtra("property_name");
                friendName = getIntent().getStringExtra("friend_name");
                friendFCM = getIntent().getStringExtra("friend_fcm");
                propertyID = getIntent().getStringExtra("property_id");

                bookingObj=new SearchPropertyModel();
                bookingObj.userDetail.FullName=friendName;
                bookingObj.userDetail.APIToken=friendFCM;
                bookingObj.propertyTitle=propertyName;
                bookingObj.propertyID=propertyID;
                bookingObj.userDetail.DeviceToken=getIntent().getStringExtra("token");
                bookingObj.userID=getIntent().getStringExtra("user_id");

                try {
                    dataContext.tblUserObjectSet.fill();
                } catch (AdaFrameworkException e) {
                    e.printStackTrace();
                }

                tvUserName.setText(friendName);
                tvUserAddress.setText(propertyName);


                adapter = new ChatAdapterTwo(ConversationChat.this, list, bookingObj);
                lvChat.setAdapter(adapter);

                try {
                    dataContext.tblUserObjectSet.fill();
                } catch (AdaFrameworkException e) {
                    e.printStackTrace();
                }

                tblConversationmy = database.getReference().child("Conversations");

                Utility.showProgress(ConversationChat.this);
                database.getReference().child(Constants.CHAT_FRIEND_LIST).child(dataContext.tblUserObjectSet.get(0).APIToken.equalsIgnoreCase(bookingObj.userDetail.APIToken)?bookingObj.userDetail.APIToken:bookingObj.userDetail.APIToken).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                for (DataSnapshot dataSnapshot1 : snapshot.child(Constants.FRIEND_ID).getChildren()) {
                                    if (dataSnapshot1.getKey().equalsIgnoreCase(bookingObj.userDetail.APIToken)) {
                                        //System.out.println("CONVERSATION ID===>" + dataSnapshot1.getChildren().iterator().next().getValue(String.class));
                                        ConversationID = dataSnapshot1.getChildren().iterator().next().getValue(String.class);

                                    } else if (dataSnapshot1.getKey().equalsIgnoreCase(dataContext.tblUserObjectSet.get(0).APIToken)) {
                                        //System.out.println("CONVERSATION ID===>" + dataSnapshot1.getChildren().iterator().next().getValue(String.class));
                                        ConversationID = dataSnapshot1.getChildren().iterator().next().getValue(String.class);

                                    }else{
                                        ConversationID = dataSnapshot1.getChildren().iterator().next().getValue(String.class);

                                    }
                                }
                                loadChatData();
                            }

                        } else {
                            database.getReference().child(Constants.CHAT_FRIEND_LIST).child(bookingObj.userDetail.APIToken).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.exists()){
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                            for (DataSnapshot dataSnapshot1 : snapshot.child(Constants.FRIEND_ID).getChildren()) {
                                                if (dataSnapshot1.getKey().equalsIgnoreCase(dataContext.tblUserObjectSet.get(0).APIToken)) {
                                                    System.out.println("CONVERSATION ID===>" + dataSnapshot1.getChildren().iterator().next().getValue(String.class));
                                                    ConversationID = dataSnapshot1.getChildren().iterator().next().getValue(String.class);

                                                } else if (dataSnapshot1.getKey().equalsIgnoreCase(bookingObj.userDetail.APIToken)) {
                                                    ConversationID = dataSnapshot1.getChildren().iterator().next().getValue(String.class);

                                                }else{
                                                    ConversationID = dataSnapshot1.getChildren().iterator().next().getValue(String.class);

                                                }
                                            }
                                            loadChatData();
                                        }

                                    }else{
                                        tblConversationmy = database.getReference().child(Constants.CONVERSION);
                                        ConversationID = tblConversationmy.push().getKey();
                                        loadChatData();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Utility.hideProgress();
                                }
                            });



                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Utility.hideProgress();
                    }
                });


            }


        }


    }
    @Click
    public void btnBack() {
        finish();
    }
    @Override
    protected void onStart() {
        isActive=true;
        super.onStart();
    }
    @Override
    protected void onStop() {
        isActive=false;
        super.onStop();
    }

    @Click
    public void btnSend() {
        if(ConversationID!=null && !ConversationID.isEmpty()) {
            tblConversationmy = database.getReference().child(Constants.CONVERSION);
            id = tblConversationmy.push().getKey();
            ChatModel model = new ChatModel();
            try {
                dataContext.tblUserObjectSet.fill();
            } catch (AdaFrameworkException e) {
                e.printStackTrace();
            }
            model.sender = dataContext.tblUserObjectSet.get(0).APIToken;
            model.receiver = bookingObj.userDetail.APIToken;
            model.timeStamp = new Date().getTime() / 1000L;
            model.isRead = false;
            model.messageType = "Text";
            model.message = edtChat.getText().toString().trim();
            tblConversationmy.child(ConversationID).child(id).setValue(model);
            //readDBData(ConversationID, id);

            tblChatUserFriendList = database.getReference().child(Constants.CHAT_FRIEND_LIST);
            if (tblChatUserFriendList.getKey().equalsIgnoreCase(model.sender)) {
                if (tblChatUserFriendList.child(model.sender).child(bookingObj.propertyID).child(Constants.FRIEND_ID).getKey().equalsIgnoreCase(model.receiver)) {
                    ConversationModel conversationModel = new ConversationModel();
                    conversationModel.ConversationsID = ConversationID;
                    tblChatUserFriendList.child(model.sender).child(bookingObj.propertyID).child(Constants.FRIEND_ID).child(model.receiver).setValue(conversationModel);
                }
            } else if (tblChatUserFriendList.getKey().equalsIgnoreCase(model.receiver)) {
                if (tblChatUserFriendList.child(model.receiver).child(bookingObj.propertyID).child(Constants.FRIEND_ID).getKey().equalsIgnoreCase(model.sender)) {
                    ConversationModel conversationModel = new ConversationModel();
                    conversationModel.ConversationsID = ConversationID;
                    tblChatUserFriendList.child(model.sender).child(bookingObj.propertyID).child(Constants.FRIEND_ID).child(model.receiver).setValue(conversationModel);
                }
            } else {
                ConversationModel conversationModel = new ConversationModel();
                conversationModel.ConversationsID = ConversationID;
                tblChatUserFriendList.child(model.sender).child(bookingObj.propertyID).child(Constants.FRIEND_ID).child(model.receiver).setValue(conversationModel);
                ConversationModel conversationModel2 = new ConversationModel();
                conversationModel2.ConversationsID = ConversationID;
                database.getReference().child(Constants.CHAT_FRIEND_LIST).child(model.receiver).child(bookingObj.propertyID).child(Constants.FRIEND_ID).child(model.sender).setValue(conversationModel2);
            }

            KeyBoardHandling.hideSoftKeyboard(ConversationChat.this);
            loadChatData();
            //adapter.notifyDataSetChanged();
            edtChat.setText("");
            sendNotification(dataContext.tblUserObjectSet.get(0).FullName,
                    model.message,
                    bookingObj.userDetail.FullName,
                    dataContext.tblUserObjectSet.get(0).APIToken,
                    bookingObj.userDetail.APIToken);
        }
    }


    public void loadChatData() {

        database.getReference().child(Constants.CONVERSION).child(ConversationID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    list.clear();
                    Utility.hideProgress();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ChatModel model = new ChatModel();
                        if (bookingObj.userDetail.APIToken.equalsIgnoreCase(snapshot.child("sender").getValue(String.class)) && dataContext.tblUserObjectSet.get(0).APIToken.equalsIgnoreCase(snapshot.child("receiver").getValue(String.class))) {
                            model.message = snapshot.child("message").getValue(String.class);
                            model.messageType = snapshot.child("messageType").getValue(String.class);
                            model.isRead = snapshot.child("isRead").getValue(Boolean.class);
                            model.timeStamp = snapshot.child("timeStamp").getValue(Long.class);
                            model.sender = snapshot.child("sender").getValue(String.class);
                            model.receiver = snapshot.child("receiver").getValue(String.class);
                            list.add(model);
                            if (snapshot.child("receiver").getValue(String.class).equalsIgnoreCase(dataContext.tblUserObjectSet.get(0).APIToken)) {
                                if (ConversationChat.isActive)
                                    database.getReference().child(Constants.CONVERSION).child(ConversationID).child(snapshot.getKey()).child("isRead").setValue(true);
                            }
                        } else if (dataContext.tblUserObjectSet.get(0).APIToken.equalsIgnoreCase(snapshot.child("sender").getValue(String.class)) && bookingObj.userDetail.APIToken.equalsIgnoreCase(snapshot.child("receiver").getValue(String.class))) {
                            model.message = snapshot.child("message").getValue(String.class);
                            model.messageType = snapshot.child("messageType").getValue(String.class);
                            model.isRead = snapshot.child("isRead").getValue(Boolean.class);
                            model.timeStamp = snapshot.child("timeStamp").getValue(Long.class);
                            model.sender = snapshot.child("sender").getValue(String.class);
                            model.receiver = snapshot.child("receiver").getValue(String.class);
                            list.add(model);
                            if (snapshot.child("receiver").getValue(String.class).equalsIgnoreCase(dataContext.tblUserObjectSet.get(0).APIToken)) {
                                if (ConversationChat.isActive)
                                    database.getReference().child(Constants.CONVERSION).child(ConversationID).child(snapshot.getKey()).child("isRead").setValue(true);
                            }
                        }
//                        if (snapshot.child("receiver").getValue(String.class).equalsIgnoreCase(dataContext.tblUserObjectSet.get(0).APIToken)) {
//                            if (ConversationChat.isActive)
//                                database.getReference().child(Constants.CONVERSION).child(ConversationID).child(snapshot.getKey()).child("isRead").setValue(true);
//                        }

                    }
                } else {
                    tblConversationmy = database.getReference().child(Constants.CONVERSION);
                    ConversationID = tblConversationmy.push().getKey();
                    Utility.hideProgress();
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Utility.hideProgress();
            }
        });

    }

    public void sendImage(String name) {
        if(ConversationID!=null && !ConversationID.isEmpty()) {
            try {
                dataContext.tblUserObjectSet.fill();
            } catch (AdaFrameworkException e) {
                e.printStackTrace();
            }
            tblConversationmy = database.getReference().child(Constants.CONVERSION);
            id = tblConversationmy.push().getKey();

            ChatModel model = new ChatModel();
            model.sender = dataContext.tblUserObjectSet.get(0).APIToken;
            model.receiver = bookingObj.userDetail.APIToken;
            model.timeStamp = new Date().getTime() / 1000L;
            model.isRead = false;
            model.messageType = "Image";
            model.message = name;
            tblConversationmy.child(ConversationID).child(id).setValue(model);


            // readDBData(ConversationID, id);

//        tblChatUserFriendList = database.getReference().child("ChatUserFriendList");
//        if (tblChatUserFriendList.getKey().equalsIgnoreCase(model.sender)) {
//            if (tblChatUserFriendList.child(model.sender).child("FriendID").getKey().equalsIgnoreCase(model.receiver)) {
//                ConversationModel conversationModel = new ConversationModel();
//                conversationModel.ConversationsID = ConversationID;
//                tblChatUserFriendList.child(model.sender).child("FriendID").child(model.receiver).setValue(conversationModel);
//            }
//        } else {
//            ConversationModel conversationModel = new ConversationModel();
//            conversationModel.ConversationsID = ConversationID;
//            tblChatUserFriendList.child(model.sender).child("FriendID").child(model.receiver).setValue(conversationModel);
//        }

            tblChatUserFriendList = database.getReference().child(Constants.CHAT_FRIEND_LIST);
            if (tblChatUserFriendList.getKey().equalsIgnoreCase(model.sender)) {
                if (tblChatUserFriendList.child(model.sender).child(bookingObj.propertyID).child(Constants.FRIEND_ID).getKey().equalsIgnoreCase(model.receiver)) {
                    ConversationModel conversationModel = new ConversationModel();
                    conversationModel.ConversationsID = ConversationID;
                    tblChatUserFriendList.child(model.sender).child(bookingObj.propertyID).child(Constants.FRIEND_ID).child(model.receiver).setValue(conversationModel);
                }
            } else if (tblChatUserFriendList.getKey().equalsIgnoreCase(model.receiver)) {
                if (tblChatUserFriendList.child(model.receiver).child(bookingObj.propertyID).child(Constants.FRIEND_ID).getKey().equalsIgnoreCase(model.sender)) {
                    ConversationModel conversationModel = new ConversationModel();
                    conversationModel.ConversationsID = ConversationID;
                    tblChatUserFriendList.child(model.receiver).child(bookingObj.propertyID).child(Constants.FRIEND_ID).child(model.sender).setValue(conversationModel);
                }
            } else {
                ConversationModel conversationModel = new ConversationModel();
                conversationModel.ConversationsID = ConversationID;
                tblChatUserFriendList.child(model.sender).child(bookingObj.propertyID).child(Constants.FRIEND_ID).child(model.receiver).setValue(conversationModel);
                ConversationModel conversationModel2 = new ConversationModel();
                conversationModel2.ConversationsID = ConversationID;
                database.getReference().child(Constants.CHAT_FRIEND_LIST).child(model.receiver).child(bookingObj.propertyID).child(Constants.FRIEND_ID).child(model.sender).setValue(conversationModel2);
            }

            loadChatData();
            //sendNotification(dataContext.tblUserObjectSet.get(0).FullName,model.message,dataContext.tblUserObjectSet.get(0).APIToken.equalsIgnoreCase(bookingObj.userDetail.APIToken) ? bookingObj.userDetail.FullName : bookingObj.userDetail.FullName);

            sendNotification(dataContext.tblUserObjectSet.get(0).FullName,
                    model.messageType.equalsIgnoreCase("image") ? "Attachment" : model.message,
                    bookingObj.userDetail.FullName,
                    dataContext.tblUserObjectSet.get(0).APIToken,
                    bookingObj.userDetail.APIToken);
        }
    }


    private void logout() {
        if (mAuth != null)
            mAuth.signOut();
    }

    @Override
    public void onBackPressed() {
        logout();
        finish();
    }

    @Override
    protected void onDestroy() {
        logout();
        finish();
        super.onDestroy();
    }

    @Click
    public void btnGallery() {
        selectOption();
    }

    private void selectOption() {
        try {

            ImagePickerActivity.showImagePickerOptions(ConversationChat.this, new ImagePickerActivity.PickerOptionListener() {
                @Override
                public void onTakeCameraSelected() {
                    launchCameraIntent();
                }

                @Override
                public void onChooseGallerySelected() {
                    launchGalleryIntent();
                }
            });
//            String[] colors = {"Take photo","From camera roll"};
//            AlertDialog.Builder builder = new AlertDialog.Builder(ConversationChat.this);
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
        } catch (Exception ex) {
            System.out.println(ex.toString());
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
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File exportDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "mydriveway");
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            mTempCameraPhotoFile = new File(exportDir, "/" + Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            //Log.d(LOG_TAG, "/" + UUID.randomUUID().toString().replaceAll("-", "") + ".jpg");
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTempCameraPhotoFile));
            startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
        }
    }

    public void pickFromGallery() {
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
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(ConversationChat.this, ImagePickerActivity.class);
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
        Intent intent = new Intent(ConversationChat.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    private void onSelectFromGalleryResult(Intent data) {
        try {

            Uri fpath = data.getParcelableExtra("path");
            cameraFilePath=fpath.getPath().toString();
            uploadImage();
            //profilepic.setImageBitmap(null);
//            Uri ImageSelect = data.getData();
//
//            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), ImageSelect);
//            String path = saveImage(bitmap);
//
//            Log.i("GALLAY FILE", path);
//            String[] filePath = {MediaStore.Images.Media.DATA};
//            Cursor cursor = getContentResolver().query(ImageSelect, filePath, null, null, null);
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
//            cursor.moveToFirst();
//
//
//            cameraFilePath = path;
//            // Set the Image in ImageView after decoding the String
//            // img.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
//            cursor.close();
//            //profilepic.setImageBitmap(bitmap);
//            uploadImage();


        } catch (Exception e) {
            Log.e("Error...", e.getMessage());
            Toast.makeText(ConversationChat.this, "Try Again...", Toast.LENGTH_LONG).show();
        }

    }


    private void onCaptureImageResult(Intent data) {
        //profilepic.setImageBitmap(null);
//        String fpath = mTempCameraPhotoFile.getPath();
//        Bitmap thumbnail;
//        //String path = saveImage(thumbnail);
//        Log.i("CAMERA FILE", fpath);
//
//        new ImageCompression().execute(fpath);

        Uri fpath = data.getParcelableExtra("path");
        cameraFilePath=fpath.getPath().toString();
        uploadImage();


        //img.setImageURI(Uri.parse(cameraFilePath));
//            tblPropertyImage image = new tblPropertyImage();
//            image.ImagePath = fpath;
//            image.ImageType="camera";
//            try {
//                //dataContext.propertyImageObjectSet.save(image);
//                adapter.add(image);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            adapter.notifyDataSetChanged();

        //profilepic.setImageBitmap(thumbnail);

//        imgDecodableString = path;
        //appPreferences.set("profile_image",path);

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
            MediaScannerConnection.scanFile(ConversationChat.this,
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

            cameraFilePath = imagePath;
            uploadImage();


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


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    try {
                        onSelectFromGalleryResult(data);
                    } catch (Exception ex) {
                        System.out.println(ex.toString());
                    }
                    break;
                case CAMERA_REQUEST_CODE:
                    try {
                        onCaptureImageResult(data);
                    } catch (Exception ex) {
                        System.out.println(ex.toString());
                    }
                    break;
            }
        }
    }

    private void uploadImage() {
        if (cameraFilePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            // Defining the child of storageReference
            String imageName = UUID.randomUUID().toString() + ".jpg";
            StorageReference ref
                    = FirebaseStorage.getInstance().getReference()
                    .child(
                            "DriveWayChatImage/"
                                    + imageName);

            // adding listeners on upload
            // or failure of image
            ref.putFile(Uri.fromFile(new File(cameraFilePath)))
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Utility.showAlert(ConversationChat.this, "Image uploaded!!");

                                    StorageReference storageRef =
                                            FirebaseStorage.getInstance().getReference();
                                    storageRef.child("DriveWayChatImage/" + imageName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            System.out.println("PATH===>" + uri.toString());
                                            sendImage(uri.toString());
                                        }
                                    });


                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Utility.showAlert(ConversationChat.this, "Failed");

                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int) progress + "%");
                                }
                            });
        }
    }

    public void sendNotification(String sender,String msg,String receiver,String senderid,String receiverid){

        RequestQueue requestQueue;
        JSONObject object=new JSONObject();
        JSONObject jsonObject=new JSONObject();
        JSONObject dataObject=new JSONObject();

        JSONObject data=new JSONObject();
        JSONObject user=new JSONObject();
        //JSONObject maindata=new JSONObject();

        try {
            object.put("to",bookingObj.userDetail.DeviceToken);
            jsonObject.put("title",sender);
            jsonObject.put("body",msg);


            dataContext.tblUserObjectSet.fill();
            dataObject.put("friendId",dataContext.tblUserObjectSet.get(0).UserID);
            //dataObject.put("friendId",bookingObj.userDetail.UserID);
            dataObject.put("parkingId",bookingObj.propertyID);

            dataObject.put("notification_type","chat");


            object.put("notification",jsonObject);
            object.put("data",dataObject);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (AdaFrameworkException e) {
            e.printStackTrace();
        }

        if(!Utility.isNetworkAvailable(ConversationChat.this)){

            Utility.showAlert(this, "please check internet connection");
        }else {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    WebUtility.FCM_LINK, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    System.out.println("Response Notification" + response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("Response Notification Error" + error.toString());
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("Content-Type", "application/json; charset=utf-8");
                    map.put("authorization", "key=AIzaSyBdV0gLCkzgkcWg6O1Rv1RcOQxL5Sdwj0c");
                    return map;
                }
            };

            requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonObjectRequest);

        }
    }



}
