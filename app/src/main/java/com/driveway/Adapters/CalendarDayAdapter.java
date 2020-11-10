package com.driveway.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.annimon.stream.Stream;
import com.driveway.Component.TTextView;
import com.driveway.DBHelper.tblPropertyAvailableTimes;
import com.driveway.R;
import com.driveway.Utility.DataContext;
import com.driveway.calendarcomponent.CalendarProperties;
import com.driveway.calendarcomponent.CalendarView;
import com.driveway.calendarcomponent.DateUtils;
import com.driveway.calendarcomponent.DayColorsUtils;
import com.driveway.calendarcomponent.EventDayUtils;
import com.driveway.calendarcomponent.ImageUtils;
import com.driveway.calendarcomponent.SelectedDay;
import com.mobandme.ada.exceptions.AdaFrameworkException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * This class is responsible for loading a one day cell.
 * <p>
 * Created by Mateusz Kornakiewicz on 24.05.2017.
 */

public class CalendarDayAdapter extends ArrayAdapter<Date> {
    private CalendarPageAdapter mCalendarPageAdapter;
    private LayoutInflater mLayoutInflater;
    private int mPageMonth;
    private Calendar mToday = DateUtils.getCalendar();

    private CalendarProperties mCalendarProperties;
    DataContext dataContext;

    public static int i=0;
    CalendarDayAdapter(CalendarPageAdapter calendarPageAdapter, Context context, CalendarProperties calendarProperties,
                       ArrayList<Date> dates, int pageMonth) {
        super(context, calendarProperties.getItemLayoutResource(), dates);
        mCalendarPageAdapter = calendarPageAdapter;
        mCalendarProperties = calendarProperties;
        mPageMonth = pageMonth < 0 ? 11 : pageMonth;
        mLayoutInflater = LayoutInflater.from(context);
        dataContext=new DataContext(context);
        System.out.println("CLASS NAME====>"+context.getClass().getName());
        if(context.getClass().getName()!=null) {
            if (!context.getClass().getName().equalsIgnoreCase("com.driveway.Activity.ParkerBookingStayScreen")) {
                try {
                    if(i==0) {
                            dataContext.propertyAvailableTimesObjectSet.fill();
                        System.out.println("CALLED TIME====>" + i++);
                    }
                } catch (AdaFrameworkException e) {
                    e.printStackTrace();
                }
            }
            else if (context.getClass().getName().equalsIgnoreCase("com.driveway.Activity.ParkerBookingStayScreen")) {
                try {
                    if(dataContext.propertyAvailableTimesObjectSet.size()==0)
                            dataContext.propertyAvailableTimesObjectSet.fill();


                } catch (AdaFrameworkException e) {
                    e.printStackTrace();
                }
            }
        }
        calendarPageAdapter.refreshCalender();

    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            view = mLayoutInflater.inflate(mCalendarProperties.getItemLayoutResource(), parent, false);
        }

        TTextView dayLabel = (TTextView) view.findViewById(R.id.dayLabel);
        ImageView dayIcon = (ImageView) view.findViewById(R.id.dayIcon);

        Calendar day = new GregorianCalendar();
        day.setTime(getItem(position));

        // Loading an image of the event
        if (dayIcon != null) {
            loadIcon(dayIcon, day);
        }

        setLabelColors(dayLabel, day);

        dayLabel.setText(String.valueOf(day.get(Calendar.DAY_OF_MONTH)));


        String days[] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
//
//
        int dayIndex = day.get(Calendar.DAY_OF_WEEK);
//

        //For Availability Match Data
        try {
            if(dataContext.propertyAvailableTimesObjectSet.size()>0){
                for(tblPropertyAvailableTimes obj:dataContext.propertyAvailableTimesObjectSet){
                    if(obj.DayName.equalsIgnoreCase("sunday") && !obj.Timing.isEmpty()){
                        if(days[dayIndex - 1].equalsIgnoreCase("sunday")){
                            setAvailableDayColor(dayLabel,day);
                        }
                    }
                    if(obj.DayName.equalsIgnoreCase("monday") && !obj.Timing.isEmpty()){
                        if(days[dayIndex - 1].equalsIgnoreCase("monday")){
                            setAvailableDayColor(dayLabel,day);
                        }
                    }
                    if(obj.DayName.equalsIgnoreCase("tuesday") && !obj.Timing.isEmpty()){
                        if(days[dayIndex - 1].equalsIgnoreCase("tuesday")){
                            setAvailableDayColor(dayLabel,day);
                        }
                    }
                    if(obj.DayName.equalsIgnoreCase("wednesday") && !obj.Timing.isEmpty()){
                        if(days[dayIndex - 1].equalsIgnoreCase("wednesday")){
                            setAvailableDayColor(dayLabel,day);
                        }
                    }
                    if(obj.DayName.equalsIgnoreCase("thursday") && !obj.Timing.isEmpty()){
                        if(days[dayIndex - 1].equalsIgnoreCase("thursday")){
                            setAvailableDayColor(dayLabel,day);
                        }
                    }
                    if(obj.DayName.equalsIgnoreCase("friday") && !obj.Timing.isEmpty()){
                        if(days[dayIndex - 1].equalsIgnoreCase("friday")){
                            setAvailableDayColor(dayLabel,day);
                        }
                    }
                    if(obj.DayName.equalsIgnoreCase("saturday") && !obj.Timing.isEmpty()){
                        if(days[dayIndex - 1].equalsIgnoreCase("saturday")){
                            setAvailableDayColor(dayLabel,day);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }


    public String getNameOfDay(int year, int dayOfYear) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
        String days[] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);

        return days[dayIndex - 1];
    }

    private void setLabelColors(TextView dayLabel, Calendar day) {
        // Setting not current month day color
        if (!isCurrentMonthDay(day)) {
            DayColorsUtils.setDayColors(dayLabel, mCalendarProperties.getAnotherMonthsDaysLabelsColor(),
                    Typeface.BOLD, android.R.color.transparent);
            return;
        }

        // Setting view for all SelectedDays
        if (isSelectedDay(day)) {
            Stream.of(mCalendarPageAdapter.getSelectedDays())
                    .filter(selectedDay -> selectedDay.getCalendar().equals(day))
                    .findFirst().ifPresent(selectedDay -> selectedDay.setView(dayLabel));

            DayColorsUtils.setSelectedDayColors(dayLabel, mCalendarProperties);
            return;
        }

        // Setting disabled days color
        if (!isActiveDay(day)) {
            DayColorsUtils.setDayColors(dayLabel, mCalendarProperties.getDisabledDaysLabelsColor(),
                    Typeface.BOLD, android.R.color.transparent);
            return;
        }

        // Setting custom label color for event day
//        if (isEventDayWithLabelColor(day)) {
//            DayColorsUtils.setCurrentMonthDayColors(day, mToday, dayLabel, mCalendarProperties);
//            return;
//        }

        // Setting current month day color
        DayColorsUtils.setCurrentMonthDayColors(day, mToday, dayLabel, mCalendarProperties);
    }

    private boolean isSelectedDay(Calendar day) {
        return mCalendarProperties.getCalendarType() != CalendarView.CLASSIC && day.get(Calendar.MONTH) == mPageMonth
                && mCalendarPageAdapter.getSelectedDays().contains(new SelectedDay(day));
    }

    private boolean isEventDayWithLabelColor(Calendar day) {
        return EventDayUtils.isEventDayWithLabelColor(day, mCalendarProperties);
    }

    private boolean isCurrentMonthDay(Calendar day) {
        return day.get(Calendar.MONTH) == mPageMonth &&
                !((mCalendarProperties.getMinimumDate() != null && day.before(mCalendarProperties.getMinimumDate()))
                        || (mCalendarProperties.getMaximumDate() != null && day.after(mCalendarProperties.getMaximumDate())));
    }

    private boolean isActiveDay(Calendar day) {
        return !mCalendarProperties.getDisabledDays().contains(day);
    }

    private void loadIcon(ImageView dayIcon, Calendar day) {
        if (mCalendarProperties.getEventDays() == null || !mCalendarProperties.getEventsEnabled()) {
            dayIcon.setVisibility(View.GONE);
            return;
        }

        Stream.of(mCalendarProperties.getEventDays()).filter(eventDate ->
                eventDate.getCalendar().equals(day)).findFirst().executeIfPresent(eventDay -> {

            ImageUtils.loadImage(dayIcon, eventDay.getImageDrawable());

            // If a day doesn't belong to current month then image is transparent
            if (!isCurrentMonthDay(day) || !isActiveDay(day)) {
                dayIcon.setAlpha(0.12f);
            }

        });
    }

    private void setAvailableDayColor(TextView tv,Calendar day){
            mCalendarProperties.setTodayColor(R.color.black);
            DayColorsUtils.setDayColors(tv, R.color.black,
                    Typeface.BOLD, R.drawable.calender_available_day_color);
        return;
    }
}
