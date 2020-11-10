package com.driveway.DBHelper;

import com.mobandme.ada.Entity;
import com.mobandme.ada.annotations.Table;
import com.mobandme.ada.annotations.TableField;

@Table(name = "tblpropertydetail")
public class tblPropertyDetail extends Entity {

    @TableField(name = "propertyid",datatype = DATATYPE_STRING)
    public String PropertyID="0";

    @TableField(name = "propertyname",datatype = DATATYPE_STRING)
    public String PropertyName="";

    @TableField(name = "propertyaddress",datatype = DATATYPE_STRING)
    public String PropertyAddress="";

    @TableField(name = "propertywidth",datatype = DATATYPE_STRING)
    public String PropertyWidth="";

    @TableField(name = "propertyheight",datatype = DATATYPE_STRING)
    public String PropertyHeight="";

    @TableField(name = "propertymaxcars",datatype = DATATYPE_STRING)
    public String PropertyMaxCars="";

    @TableField(name = "propertyabout",datatype = DATATYPE_STRING)
    public String PropertyAbout="";

    @TableField(name = "propertyparkingtype",datatype = DATATYPE_STRING)
    public String PropertyParkingType="";


}
