package com.driveway.Utility;

import android.app.DatePickerDialog;

import com.driveway.Activity.ParkerBookingStayScreen;
import com.driveway.Component.TTextView;
import com.driveway.Component.Utility;
import com.stripe.android.model.Card;


import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Constants {


    //Firebase Database URL
    public static String FirebaseURL ="https://drivewaychat-9aac4.firebaseio.com/";
    //Payment Keys
//    public static String PUBLISHABLE_KEY="pk_test_WpJ6982AirVKrVznN0DCDwgI00exDnAGGk";


    public static String PUBLISHABLE_KEY="pk_test_LAk0pIO6oeqwjROuMzjZBabA00wFA4Xfpr";

    public static String SECRET_KEY="sk_test_HAxLnGn8IgpT6sNdS8PFT2RG00nEojM9gD";


    //Chat Fields
    public static String CHAT_FRIEND_LIST="ChatUserFriendList";
    public static String FRIEND_ID="FriendID";
    public static String CONVERSION="Conversations";
    public static String ALL_USERS="AppUser";


    public static String SHORT_STAY="short_time";
    public static String _15MIN = "15 mins";
    public static String _30MIN = "30 mins";
    public static String _45MIN = "45 mins";

    public static String HOUR_STAY="hourly_time";
    public static String _1_4_HOUR = "1 - 4 hours";
    public static String _4_8_HOUR = "5 - 8 hours";
    public static String _8_PLUS_HOUR = "8+ hours";

    public static String DAY_STAY="daily_time";
    public static String _HALFDAY = "half day (up to 6 hours)";
    public static String _FULLDAY = "full day (up to 12 hours)";

    public static String WEEK_STAY="weekly_time";
    public static String _3_DAY = "3 days";
    public static String _5_DAY = "5 days";
    public static String _7_DAY = "7 days";

    public static String MONTH_STAY="monthly_time";
    public static String _14_DAY = "14 days";
    public static String _30_DAY = "30 days";


    public static String SHORT_STAY_TEXT = "Short stay includes 15 min, 30 min, 60 min stay";
    public static String HOUR_STAY_TEXT = "Hourly stay includes 1-4 hours, 5-8 hours, 8+ hours stay";
    public static String DAILY_STAY_TEXT = "Daily Stay includes Half Day (up to 6 hours), Full day (up to 12 hours) stay";
    public static String WEEKLY_STAY_TEXT = "Weekly Stay includes 3 days, 5 days, 7 days stay";
    public static String MONTHLY_STAY_TEXT = "Monthly Stay includes 14 days, 30 days stay";

    public static String ERROR_MESSAGE = "This stay is not available in this parking";
    public static String BOOKING_ERROR_MESSAGE = "Please select any stay with slot-time";
    public static String SELECT_BOOKINGDATE = "please select booking date";
    public static String EDIT_MESSAGE="You can not select less time slot than current time slot.";

    public static String LOGIN_MESSAGE="Please login to go ahead.";

    private static DateFormat dateFormatFirst = new SimpleDateFormat("yyyy-MM-dd");
    private static DateFormat dateFormatPDF = new SimpleDateFormat("dd/MM/yyyy");
    private static DateFormat dateFormatSecond = new SimpleDateFormat("dd MMM yyyy");
    private static DateFormat dateFormatThird = new SimpleDateFormat("MM/dd/yyyy");

    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");

    private static DateFormat dateFormat_notification = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private static DateFormat dateFormatTwo = new SimpleDateFormat("hh:mm a");
    private static DateFormat dateFormatThree = new SimpleDateFormat("dd MMM yyyy");


    private static DateFormat dd_mm_yyyy = new SimpleDateFormat("dd/MM/yyyy");



    public static String converDate(String currentDate){
        Date myDate = null;
        try {
            myDate = dateFormatFirst.parse(currentDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFormatSecond.format(myDate);
    }
    public static String converDatePDF(String currentDate){
        Date myDate = null;
        try {
            myDate = dateFormatPDF.parse(currentDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFormatSecond.format(myDate);
    }

    public static String converDate_YYYYMMDD(String currentDate){
        Date myDate = null;
        try {
            myDate = dateFormatSecond.parse(currentDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFormatFirst.format(myDate);
    }

    public static String converDateProfile(String currentDate){
        Date myDate = null;
        try {
            myDate = dateFormatThird.parse(currentDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFormatSecond.format(myDate);
    }

    public static String converDateProfileTwo(String currentDate){
        Date myDate = null;
        try {
            myDate = dateFormatSecond.parse(currentDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFormatThird.format(myDate);
    }


    public static String convert(String currentDate){
        Date myDate = null;
        try {
            myDate = dateFormat.parse(currentDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFormatTwo.format(myDate);
    }
    public static String convertNotification(String currentDate){
        Date myDate = null;
        try {
            myDate = dateFormat.parse(currentDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFormatTwo.format(myDate);
    }

    public static String convertNotificationTwo(String currentDate){
        Date myDate = null;
        try {
            myDate = dateFormat.parse(currentDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFormatFirst.format(myDate);
    }

    public static String convertTwo(String currentDate){
        Date myDate = null;
        try {
            myDate = dateFormat.parse(currentDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFormatThree.format(myDate);
    }


    public static String addMinTime(String time,int amount){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
        Date d = null;
        try {
            d = df.parse(time);
            cal.setTime(d);
            cal.add(Calendar.MINUTE, amount);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String newTime = df.format(cal.getTime());
        return newTime;
    }

    public static String addDays(String time,int amount){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date d = null;
        try {
            d = df.parse(time);
            cal.setTime(d);
            cal.add(Calendar.DAY_OF_MONTH, amount);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String newTime = df.format(cal.getTime());
        return newTime;
    }

    public static String addDaysBooking(String time,int amount){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date d = null;
        try {
            d = df.parse(time);
            cal.setTime(d);
            cal.add(Calendar.DAY_OF_MONTH, amount);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String newTime = df.format(cal.getTime());
        return newTime;
    }

    public static String cuurentTime(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
        Date d = cal.getTime();
        try {
            d = df.parse(cal.getTime().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String newTime = df.format(d);
        return newTime;
    }

    public static boolean isValidCard(String cardNumber,int month,int year,String cvv){
        Card card=new Card(cardNumber,month,year,cvv);
        if(!card.validateCard()){
           return true;
        }else
            return false;
    }

    public static int getAge(String dobString){

        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        try {
            date = sdf.parse(dobString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(date == null) return 0;

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.setTime(date);

        int year = dob.get(Calendar.YEAR);
        int month = dob.get(Calendar.MONTH);
        int day = dob.get(Calendar.DAY_OF_MONTH);

        dob.set(year, month+1, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        return age>0?age:0;
    }

    public static String stayString(String name){
        String data="";
        if(name.equalsIgnoreCase(Constants.SHORT_STAY)){
            data="Short Stay";
        }
        else if(name.equalsIgnoreCase(Constants.HOUR_STAY)){
            data="Hour Stay";
        }
        else if(name.equalsIgnoreCase(Constants.DAY_STAY)){
            data="Daily Stay";
        }
        else if(name.equalsIgnoreCase(Constants.WEEK_STAY)){
            data="Weekly Stay";
        }
        else if(name.equalsIgnoreCase(Constants.MONTH_STAY)){
            data="Monthly Stay";
        }
        return data;
    }
    public static String getChatFormatDate(long time){
        Date date = new Date(time*1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a"); // the format of your date
        sdf.setTimeZone(TimeZone.getDefault());

        return sdf.format(date);
    }



    public static Date getWeekStartDate() {
        Calendar calendar = Calendar.getInstance();
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DATE, -1);
        }
        return calendar.getTime();
    }

    public static Date getWeekEndDate() {
        Calendar calendar = Calendar.getInstance();
        if(calendar.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY){
            while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                calendar.add(Calendar.DATE, 1);
            }
            //calendar.add(Calendar.DATE, 1);
        }else if(calendar.get(Calendar.DAY_OF_WEEK)!=Calendar.MONDAY){
            while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                calendar.add(Calendar.DATE, 1);
            }
            calendar.add(Calendar.DATE, -1);
        }

        return calendar.getTime();
    }

    public static ArrayList<String> getPreviousYears() {
        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 2000; i <= thisYear; i++) {
            years.add(Integer.toString(i));
        }
        return years;
    }

    public static String getCurrentMonthName() {
        String month_name="";
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
             month_name= month_date.format(cal.getTime());
        }catch (Exception ex){
            System.out.println("getCurrentMonthName()===>"+ex.toString());
        }
        return month_name;
    }

    public static int currentQuarter(){
        int quarter=0;
        try {
            Calendar cal = Calendar.getInstance(Locale.US);
            int month = cal.get(Calendar.MONTH);
             quarter= (month / 3) + 1;
            System.out.println("Quarter = " + quarter);
        }catch (Exception ex){
            System.out.println("Current Quarter Error"+ex.toString());
        }
        return quarter;
    }

    public static boolean validateJavaDate(String strDate)
    {
        /* Check if date is 'null' */
        if (strDate.trim().equals(""))
        {
            return true;
        }
        /* Date is not 'null' */
        else
        {
            /*
             * Set preferred date format,
             * For example MM-dd-yyyy, MM.dd.yyyy,dd.MM.yyyy etc.*/
            SimpleDateFormat sdfrmt = new SimpleDateFormat("MM/dd/yyyy");
            sdfrmt.setLenient(false);
            /* Create Date object
             * parse the string into date
             */
            try
            {
                Date javaDate = sdfrmt.parse(strDate);
                System.out.println(strDate+" is valid date format");
            }
            /* Date format is invalid */
            catch (ParseException e)
            {
                System.out.println(strDate+" is Invalid Date format");
                return false;
            }
            /* Return true if date format is valid */
            return true;
        }
    }

    public static int GET_MONTH(String name){
        int i=0;
        switch (name){
            case "january":i=1;break;
            case "fabruary":i=2;break;
            case "march":i=3;break;
            case "april":i=4;break;
            case "may":i=5;break;
            case "june":i=6;break;
            case "july":i=7;break;
            case "august":i=8;break;
            case "september":i=9;break;
            case "octomber":i=10;break;
            case "november":i=11;break;
            case "december":i=12;break;
        }
        return i;
    }

}
