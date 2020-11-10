package com.driveway.Model;

import java.io.Serializable;

public class Notificationstbl implements Serializable {

    public String NotificationID="0";
    public String NotificationMessage="";

    public String NotificationUserName="";
    public String NotificationDate="";

}

//{
//        "error_code": 0,
//        "error_message": "List Notification",
//        "data": [
//        {
//        "id": 1,
//        "from_user_id": "83",
//        "to_user_id": "54",
//        "message": "Property Booked",
//        "created_date": "2019-11-19 04:14:19",
//        "user_fullname": "John Smith",
//        "notification_date": "2019-11-19 04:14 AM",
//        "users": {
//        "id": 83,
//        "name": "John Smith",
//        "email": "mydrivewayparking20@gmail.com",
//        "phone_no": null,
//        "user_type": 2,
//        "first_name": "John",
//        "last_name": "Smith",
//        "city": null,
//        "bod": "10/16/1986",
//        "fcm_id": null,
//        "gender": "Male",
//        "zip_code": "3169"
//        }
//        },
//        {
//        "id": 2,
//        "from_user_id": "76",
//        "to_user_id": "54",
//        "message": "Succeeded Extend Booking",
//        "created_date": "2019-11-20 04:14:19",
//        "user_fullname": "John Marcom",
//        "notification_date": "2019-11-20 04:14 AM",
//        "users": {
//        "id": 76,
//        "name": "John Marcom",
//        "email": "mydrivewayparking16@gmail.com",
//        "phone_no": null,
//        "user_type": 2,
//        "first_name": "John",
//        "last_name": "Marcom",
//        "city": null,
//        "bod": "04/13/1980",
//        "fcm_id": null,
//        "gender": "Male",
//        "zip_code": "3168"
//        }
//        }
//        ]
//        }