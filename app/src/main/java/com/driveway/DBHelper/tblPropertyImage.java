package com.driveway.DBHelper;

import com.mobandme.ada.Entity;
import com.mobandme.ada.annotations.Table;
import com.mobandme.ada.annotations.TableField;

@Table(name ="tblpropertyimage")
public class tblPropertyImage extends Entity {


    @TableField(name = "parkingid",datatype = DATATYPE_STRING)
    public String ParkingID="0";

    @TableField(name = "imagepath",datatype = DATATYPE_STRING)
    public String ImagePath="";

    @TableField(name = "type",datatype = DATATYPE_STRING)
    public String ImageType="";

    public tblPropertyImage(String imageType, String imagePath) {
        ImagePath = imagePath;
        ImageType = imageType;
    }
    public tblPropertyImage() {

    }

}
