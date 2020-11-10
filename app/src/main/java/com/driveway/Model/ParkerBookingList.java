package com.driveway.Model;

import com.driveway.DBHelper.tblCars;
import com.driveway.DBHelper.tblPropertyAvailableTimes;
import com.driveway.DBHelper.tblStay;
import com.driveway.DBHelper.tblUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ParkerBookingList implements Serializable  {

    public String error_code="0";
    public String error_message="Booking list";
    public String bookingID="0";
    public String propertID="0";
    public String userID="0";
    public String bookingStartDate="0000-00-00";
    public String bookingEndDate="0000-00-00";
    public String bookingStartTime="00:00 am";
    public String bookingEndTime="00:00 am";
    public String bookingStatus="Completed";
    public String bookingPrice="00";
    public String bookingPropertyTitle="";

    public String bookingProperttyImage="";
    public String bookingPropertyAddress="";
    public String bookingPropertyStayTitle="";
    public String bookingPropertyStayTime="";
    public String bookingPropertyStayPrice="";
    public String bookingPropertyParkingType="";
    public String bookingPropertyDistance="";
    public String bookingPropertyDuration="";
    public String bookingPropertyAvailable="";
    public String bookingPropertyRating="";
    public String bookingPayment="0";
    public String bookingLat="0";
    public String bookingLng="0";
    public String isRated="0";


    public String HourTime="0";


    public tblUser user=new tblUser();
    public tblUser owneruser=new tblUser();
    public tblCard card=new tblCard();
    public tblCars cars=new tblCars();
    public List<tblStay> stays=new ArrayList<>();
    public List<tblPropertyAvailableTimes> availableTimes=new ArrayList<>();




}

//    "error_code": 0,
//            "error_message": "Booking list",
//            "data": [
//            {
//            "id": 91,
//            "property_id": 5,
//            "user_id": 69,
//            "stay_category_id": "1",
//            "slot_id": "1",
//            "rate_id": "19",
//            "booking_start_date": "2019-10-27",
//            "booking_end_date": "2019-10-27",
//            "booking_start_time": "06:31 pm",
//            "booking_end_time": "06:46 pm",
//            "status": "0",
//            "created_date": "2019-10-22 23:01:58",
//            "modified_date": "2019-10-22 20:01:58",
//            "property_title": "Clinto parking",
//            "price": "10",
//            "booking_status": "Completed",
//            "property_details": {
//            "id": 5,
//            "title": "Clinto parking",
//            "user_id": 69,
//            "address": "103, wa, australia",
//            "area1": "1000",
//            "area2": "2000",
//            "max_car": "100",
//            "about": "this is dummy property",
//            "parking_type": "Grass",
//            "lat": "-31.94793300",
//            "lng": "115.82347800",
//            "status": "1",
//            "created_date": "2019-10-19 02:53:32",
//            "modified_date": "2019-10-19 05:53:32",
//            "availibility": [
//            {
//            "title": "sunday",
//            "time": [
//            "08:00 am - 09:00 pm"
//            ]
//            },
//            {
//            "title": "monday",
//            "time": []
//            },
//            {
//            "title": "tuesday",
//            "time": []
//            },
//            {
//            "title": "wednesday",
//            "time": []
//            },
//            {
//            "title": "thursday",
//            "time": []
//            },
//            {
//            "title": "friday",
//            "time": []
//            },
//            {
//            "title": "saturday",
//            "time": []
//            }
//            ],
//            "rates": [
//            {
//            "title": "short_time",
//            "time": "15 mins",
//            "price": "10"
//            }
//            ],
//            "img_1": "http://pwa.guru/images/property/15714248121-1571424763802.jpg",
//            "img_2": ""
//            },
//            "rates": {
//            "title": "short_time",
//            "time": "15 mins",
//            "price": "10"
//            },
//            "user": {
//            "id": 69,
//            "name": "Adam Gilchristt",
//            "email": "adam@gmail.com",
//            "phone_no": null,
//            "user_type": 1,
//            "first_name": "Adam",
//            "last_name": "Gilchristt",
//            "city": null,
//            "bod": "9/2/2019",
//            "gender": "Male",
//            "zip_code": "1234",
//            "roles": "user",
//            "fcm_token": "",
//            "device_token": "",
//            "reward_points": 25
//            }
//            },
//            {
//            "id": 88,
//            "property_id": 5,
//            "user_id": 69,
//            "stay_category_id": "1",
//            "slot_id": "1",
//            "rate_id": "19",
//            "booking_start_date": "2019-10-27",
//            "booking_end_date": "2019-10-27",
//            "booking_start_time": "05:05 pm",
//            "booking_end_time": "05:20 pm",
//            "status": "0",
//            "created_date": "2019-10-22 22:34:51",
//            "modified_date": "2019-10-22 19:34:51",
//            "property_title": "Clinto parking",
//            "price": "10",
//            "booking_status": "Completed",
//            "property_details": {
//            "id": 5,
//            "title": "Clinto parking",
//            "user_id": 69,
//            "address": "103, wa, australia",
//            "area1": "1000",
//            "area2": "2000",
//            "max_car": "100",
//            "about": "this is dummy property",
//            "parking_type": "Grass",
//            "lat": "-31.94793300",
//            "lng": "115.82347800",
//            "status": "1",
//            "created_date": "2019-10-19 02:53:32",
//            "modified_date": "2019-10-19 05:53:32",
//            "availibility": [
//            {
//            "title": "sunday",
//            "time": [
//            "08:00 am - 09:00 pm"
//            ]
//            },
//            {
//            "title": "monday",
//            "time": []
//            },
//            {
//            "title": "tuesday",
//            "time": []
//            },
//            {
//            "title": "wednesday",
//            "time": []
//            },
//            {
//            "title": "thursday",
//            "time": []
//            },
//            {
//            "title": "friday",
//            "time": []
//            },
//            {
//            "title": "saturday",
//            "time": []
//            }
//            ],
//            "rates": [
//            {
//            "title": "short_time",
//            "time": "15 mins",
//            "price": "10"
//            }
//            ],
//            "img_1": "http://pwa.guru/images/property/15714248121-1571424763802.jpg",
//            "img_2": ""
//            },
//            "rates": {
//            "title": "short_time",
//            "time": "15 mins",
//            "price": "10"
//            },
//            "user": {
//            "id": 69,
//            "name": "Adam Gilchristt",
//            "email": "adam@gmail.com",
//            "phone_no": null,
//            "user_type": 1,
//            "first_name": "Adam",
//            "last_name": "Gilchristt",
//            "city": null,
//            "bod": "9/2/2019",
//            "gender": "Male",
//            "zip_code": "1234",
//            "roles": "user",
//            "fcm_token": "",
//            "device_token": "",
//            "reward_points": 25
//            }
//            },