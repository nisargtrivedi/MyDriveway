package com.driveway.APIResponse;

import com.driveway.DBHelper.tblUser;
import com.driveway.Model.ParkingSpace;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class GetPropertyResponse extends BaseResponse {


    @SerializedName("data")
    public ArrayList<ParkingSpace> spaceArrayList;


    @Override
    public boolean isValid() {
        boolean ans=false;
        if(ErrorCode!=null && !ErrorCode.isEmpty()) {
            int code = Integer.parseInt(ErrorCode);
            if(code==0)
                ans=true;
            else
                ans=false;
        }
        return ans;
    }



}
