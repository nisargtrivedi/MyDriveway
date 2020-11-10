package com.driveway.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BookedByModel implements Serializable {



    public String ID="0";
    public String property_ID="0";
    public String userID="0";
    public String booking_start_date="";
    public String booking_end_date="";
    public String booking_start_time="";
    public String booking_end_time="";

    public String price="";
    public String booking_status="";
    public String isPayment="";

    public Rates rates=new Rates();
    public ParkerUser parkerUser=new ParkerUser();
    public ParkerUser ownerUser=new ParkerUser();


    public class Rates implements Serializable{
        public String title="",time="",price="";
    }
    public class ParkerUser implements Serializable{
        public String id="0",name="",email="",first_name="",last_name="",bod="",gender="",zip_code="",roles="",reward_points="0",fcm_id="",device_token="";
    }
}
//
//"id": 92,
//        "property_id": 5,
//        "user_id": 54,
//        "booking_start_date": "2019-11-10",
//        "booking_end_date": "2019-11-10",
//        "booking_start_time": "12:40 pm",
//        "booking_end_time": "12:55 pm",
//        "price": "10",
//        "booking_status": "Upcoming",
//        "isPayment": 0,
//        "rates": {
//        "title": "short_time",
//        "time": "15 mins",
//        "price": "10"
//        },
//        "parker_user": {
//        "id": 54,
//        "name": "john mark",
//        "email": "mydrivewayparking1@gmail.com",
//        "phone_no": null,
//        "user_type": 2,
//        "first_name": "john",
//        "last_name": "mark",
//        "city": null,
//        "bod": "09/28/1984",
//        "gender": "Male",
//        "zip_code": "0212",
//        "roles": "user",
//        "device_token": "",
//        "reward_points": 1267
//        }