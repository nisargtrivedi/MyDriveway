package com.driveway.DBHelper;

import com.mobandme.ada.Entity;
import com.mobandme.ada.annotations.Table;
import com.mobandme.ada.annotations.TableField;

import java.io.Serializable;

@Table(name = "tbl_property_available_times")
public class tblPropertyAvailableTimes extends Entity implements Serializable {

    @TableField(name = "parkingid",datatype = DATATYPE_STRING)
    public String ParkingID="0";

    @TableField(name = "dayname",datatype = DATATYPE_STRING)
    public String DayName="";

    @TableField(name = "timing",datatype = DATATYPE_STRING)
    public String Timing="";
}
