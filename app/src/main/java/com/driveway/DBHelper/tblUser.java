package com.driveway.DBHelper;

import com.google.gson.annotations.SerializedName;
import com.mobandme.ada.Entity;
import com.mobandme.ada.annotations.Table;
import com.mobandme.ada.annotations.TableField;

import java.io.Serializable;

@Table(name = "tbluser")
public class tblUser extends Entity implements Serializable {


    @SerializedName("id")
    @TableField(name = "userid",datatype = DATATYPE_STRING)
    public String UserID="0";

    @SerializedName("name")
    @TableField(name = "fullname",datatype = DATATYPE_STRING)
    public String FullName="";

    @SerializedName("user_type")
    @TableField(name = "user_type",datatype = DATATYPE_STRING)
    public String UserType="2";

    @SerializedName("first_name")
    @TableField(name = "first_name",datatype = DATATYPE_STRING)
    public String FirstName="";

    @SerializedName("last_name")
    @TableField(name = "last_name",datatype = DATATYPE_STRING)
    public String LastName="";

    @SerializedName("profile_img")
    @TableField(name = "profile_img",datatype = DATATYPE_STRING)
    public String ProfileImage="";

    @SerializedName("gender")
    @TableField(name = "gender",datatype = DATATYPE_STRING)
    public String Gender="";

    @SerializedName("zip_code")
    @TableField(name = "zip_code",datatype = DATATYPE_STRING)
    public String ZipCode="";

    @SerializedName("bod")
    @TableField(name = "bod",datatype = DATATYPE_STRING)
    public String BirthDate="";

    @SerializedName("api_token")
    @TableField(name = "api_token",datatype = DATATYPE_STRING)
    public String APIToken="";

    @SerializedName("device_token")
    @TableField(name = "device_token",datatype = DATATYPE_STRING)
    public String DeviceToken="";

    @SerializedName("email")
    @TableField(name = "email",datatype = DATATYPE_STRING)
    public String Email="";

    @SerializedName("phone_no")
    @TableField(name = "phone_no",datatype = DATATYPE_STRING)
    public String PhoneNo="";

    @SerializedName("is_notify")
    @TableField(name = "is_notify",datatype = DATATYPE_STRING)
    public String Notification_On_Off="1";


    @SerializedName("reward_points")
    @TableField(name = "earnpoint",datatype = DATATYPE_STRING)
    public String EarnPoint="0";

    @SerializedName("token_key")
    @TableField(name = "token",datatype = DATATYPE_STRING)
    public String Token="0";

    @SerializedName("fcm_token")
    @TableField(name = "fcm_token",datatype = DATATYPE_STRING)
    public String fcm_token="0";


}

// API RESPONSE
//"id": 54,
//        "name": "John Mark",
//        "email": "mydrivewayparking1@gmail.com",
//        "password": "202cb962ac59075b964b07152d234b70",
//        "remember_token": null,
//        "created_at": "2019-09-20 07:42:19",
//        "updated_at": "2019-09-20 07:42:19",
//        "is_admin": "0",
//        "api_token": null,
//        "user_type": 1,
//        "phone_no": null,
//        "status": "1",
//        "first_name": "John",
//        "last_name": "Mark",
//        "profile_img": "http://pwa.guru/admin-asset/images/profile/no-image.jpg",
//        "city": null,
//        "bod": "01/25/2020",
//        "mobile_password": "202cb962ac59075b964b07152d234b70",
//        "or_pass": null,
//        "gender": "0",     Male 1 , Female 0
//        "zip_code": "0909",
//        "device_token": "",
//        "roles": "user",
//        "is_notify": "0" 0 Means Off , 1 Means On