package com.driveway.DBHelper;

import com.mobandme.ada.Entity;
import com.mobandme.ada.annotations.Table;
import com.mobandme.ada.annotations.TableField;

import java.io.Serializable;

@Table(name = "tblcars")
public class tblCars extends Entity implements Serializable {

    @TableField(name = "carid",datatype = DATATYPE_STRING)
    public String CarID="0";

    @TableField(name = "reg_number",datatype = DATATYPE_STRING)
    public String CarRegisterNumber="";

    @TableField(name = "model",datatype = DATATYPE_STRING)
    public String CarModel="";

    @TableField(name = "make_year",datatype = DATATYPE_STRING)
    public String CarMakingYear="";

    @TableField(name = "car_img",datatype = DATATYPE_STRING)
    public String CarImage="";

    @TableField(name = "is_default",datatype = DATATYPE_STRING)
    public String is_default="0";

    public int isSelect=0;
}
