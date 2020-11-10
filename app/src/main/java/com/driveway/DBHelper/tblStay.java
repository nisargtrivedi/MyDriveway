package com.driveway.DBHelper;

import com.mobandme.ada.Entity;
import com.mobandme.ada.annotations.Table;
import com.mobandme.ada.annotations.TableField;

import java.io.Serializable;

@Table(name = "tblshortstay")
public class tblStay extends Entity implements Serializable {

    @TableField(name = "parkingid",datatype = DATATYPE_STRING)
    public String ParkingID="0";

    @TableField(name = "stay",datatype = DATATYPE_STRING)
    public String Stay="";

    @TableField(name = "stay_minutes",datatype = DATATYPE_STRING)
    public String StayMinutes="";

    @TableField(name = "stay_price",datatype = DATATYPE_DOUBLE)
    public double StayPrice=0;

    public tblStay(String stayDay,String stayMinutes, double stayPrice) {
        Stay=stayDay;
        StayMinutes = stayMinutes;
        StayPrice = stayPrice;
    }

    public tblStay() {

    }
}
