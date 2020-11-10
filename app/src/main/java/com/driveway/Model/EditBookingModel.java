package com.driveway.Model;

import com.driveway.DBHelper.tblUser;

import java.io.Serializable;

public class EditBookingModel implements Serializable {

    public tblUser parkerUser=new tblUser();
    public int IsRated=0;
    public int IsPayment=0;
    public int IsEditBooking=0;
    public String BookingID="0";
    public String BookingExtendID="0";
    public String PropertyID="0";
    public String userID="0";
    public String price="0";
    public String status="0";
    public String categoryName="";
    public String soltName="";
    public String BookingStartDate="";
    public String BookingEndDate="";
    public String BookingStartTime="";
    public String BookingEndTime="";



}
