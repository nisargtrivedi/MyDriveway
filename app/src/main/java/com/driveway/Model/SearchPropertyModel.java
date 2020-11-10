package com.driveway.Model;

import com.driveway.DBHelper.tblStay;
import com.driveway.DBHelper.tblUser;

import java.io.Serializable;
import java.util.ArrayList;

public class SearchPropertyModel implements Serializable {

    public String propertyID="";
    public String propertyTitle="";
    public String userID="0";
    public String propertyAddress="";
    public String propertyWidth="";
    public String propertyHeight="";
    public String propertyMaxCar="";
    public String propertyAbout="";
    public String propertyParkingType="";
    public String propertyImage="";
    public String propertyImageTwo="";
    public String propertySunday="";
    public String propertyMonday="";
    public String propertyTuesday="";
    public String propertyWednesday="";
    public String propertyThursday="";
    public String propertyFriday="";
    public String propertySaturday="";
    public String propertyLatitude="";
    public String propertyLongitude="";
    public String propertyDistance="";
    public String propertyAvailability="";
    public String propertyRating="";
    public String propertyDuration="";

    public ArrayList<tblStay> list=new ArrayList<>();
    public tblUser userDetail=new tblUser();

//    id : 57
//    title : "UWA Car Park 3"
//    user_id : 54
//    address : "Crawley WA, Australia"
//    area1 : "3200"
//    area2 : "7800"
//    max_car : "250"
//    about : "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry’s standard dummy text ever since the 1500s"
//    parking_type : "Grass"
//    img_1 : "http://pwa.guru/images/property/15707293021-IOS_IMG.png"
//    img_2 : ""
//    sunday : "10:55 PM - 10:55 PM"
//    monday : ""
//    tuesday : ""
//    wednesday : ""
//    thursday : ""
//    friday : ""
//    saturday : ""
//    create_at : "2019-10-11 01:26:16"
//    status : "1"
//    short_time : "15 mins | 85"
//    hourly_time : ""
//    daily_time : ""
//    weekly_time : ""
//    monthly_time : ""
//    lat : "-31.9839897"
//    lng : "115.8146549"
//    distance : "4,565.73 KM"
//    available : "100%"
//    rating : "5"
}
