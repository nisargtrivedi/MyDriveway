package com.driveway.Utility;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.driveway.DBHelper.CardTbl;
import com.driveway.DBHelper.tblCars;
import com.driveway.DBHelper.tblPropertyAvailableTimes;
import com.driveway.DBHelper.tblPropertyDetail;
import com.driveway.DBHelper.tblPropertyImage;
import com.driveway.DBHelper.tblStay;
import com.driveway.DBHelper.tblUser;
import com.driveway.DBHelper.tbl_count;
import com.driveway.Model.ParkingSpace;
import com.mobandme.ada.ObjectContext;
import com.mobandme.ada.ObjectSet;
import com.mobandme.ada.exceptions.AdaFrameworkException;

import java.io.File;
public class DataContext extends ObjectContext {

    final static String DATABASE_FOLDER  = "%s/mydriveway/";
    final static String DATABASE_NAME    = "mydriveway.db";
    final static int    DATABASE_VERSION = 5;

    public ObjectSet<tblPropertyAvailableTimes> propertyAvailableTimesObjectSet;
    public ObjectSet<tblUser> tblUserObjectSet;
    public ObjectSet<tblStay> shortStayObjectSet;

    public ObjectSet<tblPropertyDetail> propertyDetailObjectSet;
    public ObjectSet<tblPropertyImage> propertyImageObjectSet;

    public ObjectSet<ParkingSpace> parkingSpaceObjectSet;

    public ObjectSet<tblCars> carsObjectSet;

    public ObjectSet<CardTbl> cardTbls;

    public ObjectSet<tbl_count> tbl_counts;

    public SQLiteDatabase database;
    public DataContext(Context pContext) {
      super(pContext, DATABASE_NAME, DATABASE_VERSION);
       // super(pContext, String.format("%s%s", getDataBaseFolder(), DATABASE_NAME), DATABASE_VERSION);
        initializeContext();
        //database=this.getReadableDatabase();
    }
    public SQLiteDatabase getReadDataBase()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        return db;
    }

    @Override
    protected void onPopulate(SQLiteDatabase pDatabase, int action) {
        //database=pDatabase;
        try {
            AppLogger.info("On DB Populate:" + action);
        }
        catch (Exception e) {
            ExceptionsHelper.manage(getContext(), e);
        }
    }

    @Override
    protected void onError(Exception pException) {
        ExceptionsHelper.manage(getContext(), pException);
    }

    private void initializeContext() {
        try {
            initializeObjectSets();

            //Enable DataBase Transactions to be used by the Save process.
            this.setUseTransactions(true);

            //Enable the creation of DataBase table indexes.
            this.setUseTableIndexes(true);

            //Enable LazyLoading capabilities.
            //this.useLazyLoading(true);

            //Set a custom encryption algorithm.
            this.setEncryptionAlgorithm("AES");

            //Set a custom encryption master pass phrase.
            this.setMasterEncryptionKey("com.sms.app.items");

            //Initialize ObjectSets instances.
//            initializeObjectSets();

        } catch (Exception e) {
            ExceptionsHelper.manage(e);
        }
    }

    private static String getDataBaseFolder() {
        String folderPath = "";
        try {
            folderPath = String.format(DATABASE_FOLDER, Environment.getExternalStorageDirectory().getAbsolutePath());
            File dbFolder = new File(folderPath);
            if (!dbFolder.exists()) {
                dbFolder.mkdirs();
            }
        } catch (Exception e) {
            ExceptionsHelper.manage(e);
        }
        return folderPath;
    }

    private void initializeObjectSets() throws AdaFrameworkException {
        propertyAvailableTimesObjectSet = new ObjectSet<tblPropertyAvailableTimes>(tblPropertyAvailableTimes.class,this);
        tblUserObjectSet = new ObjectSet<tblUser>(tblUser.class,this);
        shortStayObjectSet = new ObjectSet<tblStay>(tblStay.class,this);
        propertyDetailObjectSet = new ObjectSet<tblPropertyDetail>(tblPropertyDetail.class,this);
        propertyImageObjectSet=new ObjectSet<>(tblPropertyImage.class,this);
        parkingSpaceObjectSet=new ObjectSet<>(ParkingSpace.class,this);
        carsObjectSet=new ObjectSet<>(tblCars.class,this);
        cardTbls=new ObjectSet<>(CardTbl.class,this);
        tbl_counts=new ObjectSet<>(tbl_count.class,this);
    }
    public void deleteAllTablesRecord(){

        getReadDataBase().execSQL("delete from tbl_property_available_times");
        getReadDataBase().execSQL("delete from tblshortstay");
        getReadDataBase().execSQL("delete from tblpropertydetail");
        getReadDataBase().execSQL("delete from tblpropertyimage");
        getReadDataBase().execSQL("delete from tblparkingspace");
    }

}

