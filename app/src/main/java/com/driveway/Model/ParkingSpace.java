package com.driveway.Model;

import com.driveway.DBHelper.tblStay;
import com.google.gson.annotations.SerializedName;
import com.mobandme.ada.Entity;
import com.mobandme.ada.annotations.Table;
import com.mobandme.ada.annotations.TableField;

import java.io.Serializable;
import java.util.ArrayList;

@Table(name = "tblparkingspace")
public class ParkingSpace extends Entity implements Serializable {

    @TableField(name = "parkingid",datatype = DATATYPE_STRING)
    public String ParkingID="0";

    @TableField(name = "parkingimageone",datatype = DATATYPE_STRING)
    public String ParkingImage="";

    @TableField(name = "parkingimagetwo",datatype = DATATYPE_STRING)
    public String ParkingImage_Two="";

    @TableField(name = "parkingtitle",datatype = DATATYPE_STRING)
    public String ParkingTitle="";

    @TableField(name = "parkingaddress",datatype = DATATYPE_STRING)
    public String ParkingAddress="";

    @TableField(name = "parkingavailability",datatype = DATATYPE_STRING)
    public String ParkingAvailability="";

    @TableField(name = "area_one",datatype = DATATYPE_STRING)
    public String Width="";

    @TableField(name = "area_two",datatype = DATATYPE_STRING)
    public String Height="";

    @TableField(name = "max_car",datatype = DATATYPE_STRING)
    public String MaximumCar="";

    @TableField(name = "about",datatype = DATATYPE_STRING)
    public String AboutParking="";

    @TableField(name = "parking_type",datatype = DATATYPE_STRING)
    public String ParkingTypes="";

    @TableField(name = "sunday",datatype = DATATYPE_STRING)
    public String sundayAvailability="";

    @TableField(name = "monday",datatype = DATATYPE_STRING)
    public String mondayAvailability="";

    @TableField(name = "tuesday",datatype = DATATYPE_STRING)
    public String tuesdayAvailability="";

    @TableField(name = "wednesday",datatype = DATATYPE_STRING)
    public String wednesdayAvailability="";

    @TableField(name = "thursday",datatype = DATATYPE_STRING)
    public String thursdayAvailability="";

    @TableField(name = "friday",datatype = DATATYPE_STRING)
    public String fridayAvailability="";

    @TableField(name = "saturday",datatype = DATATYPE_STRING)
    public String saturdayAvailability="";

    @TableField(name = "property_latitude",datatype = DATATYPE_STRING)
    public String lat="";

    @TableField(name = "property_longitude",datatype = DATATYPE_STRING)
    public String lng="";

    @TableField(name = "property_rating",datatype = DATATYPE_STRING)
    public String propertyRating="0";


    public ArrayList<tblStay> stays=new ArrayList<>();

    public ArrayList<BookedByModel> bookedByModels=new ArrayList<>();





}
