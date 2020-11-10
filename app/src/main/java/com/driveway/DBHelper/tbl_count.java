package com.driveway.DBHelper;

import com.google.gson.annotations.SerializedName;
import com.mobandme.ada.Entity;
import com.mobandme.ada.annotations.Table;
import com.mobandme.ada.annotations.TableField;

import java.io.Serializable;

import static com.mobandme.ada.Entity.DATATYPE_STRING;

@Table(name = "msg_count")
public class tbl_count extends Entity implements Serializable {

    @TableField(name = "receiver_id",datatype = DATATYPE_STRING)
    public String receiver_id;

    @TableField(name = "count",datatype = DATATYPE_INTEGER)
    public int count=0;
}
