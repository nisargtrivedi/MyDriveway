package com.driveway.Model;

import java.io.Serializable;

public class BookingModel implements Serializable {

    public String BookingPersonName;
    public String BookingDay;
    public String BookingTime;

    public BookingModel(String bookingPersonName, String bookingDay, String bookingTime) {
        BookingPersonName = bookingPersonName;
        BookingDay = bookingDay;
        BookingTime = bookingTime;
    }
}
