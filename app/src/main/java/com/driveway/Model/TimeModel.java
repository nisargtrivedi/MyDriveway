package com.driveway.Model;

import java.io.Serializable;

public class TimeModel implements Serializable {

    private String PropertyTime="";


    public TimeModel(String propertyTime) {
        PropertyTime = propertyTime;
    }


    public String getPropertyTime() {
        return PropertyTime;
    }

    public void setPropertyTime(String propertyTime) {
        PropertyTime = propertyTime;
    }


}
