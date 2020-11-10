package com.driveway.DBHelper;

import com.mobandme.ada.Entity;
import com.mobandme.ada.annotations.Table;
import com.mobandme.ada.annotations.TableField;

@Table(name = "tblcard")
public class CardTbl extends Entity {

    @TableField(name = "cardid",datatype = DATATYPE_STRING)
    public String CardID="";

    @TableField(name = "cardmonth",datatype = DATATYPE_STRING)
    public String CardMonth="";

    @TableField(name = "cardyear",datatype = DATATYPE_STRING)
    public String CardYear="";

    @TableField(name = "cardcvv",datatype = DATATYPE_STRING)
    public String CardCVV="";

    @TableField(name = "cardowner",datatype = DATATYPE_STRING)
    public String CardOwnerName="";

    @TableField(name = "cardno",datatype = DATATYPE_STRING)
    public String CardNo="";

}

//    public String CardID;
//    public String CardMonth;
//    public String CardYear;
//    public String CVV;
//    public String OwnerName;
//    public String CardNo;
//

