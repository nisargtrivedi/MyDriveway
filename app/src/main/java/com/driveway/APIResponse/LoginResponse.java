package com.driveway.APIResponse;

import com.driveway.DBHelper.tblUser;
import com.google.gson.annotations.SerializedName;

public class LoginResponse extends BaseResponse {


    @SerializedName("data")
    public tblUser userModel;


    @Override
    public boolean isValid() {
        boolean ans=false;
        if(ErrorCode!=null && !ErrorCode.isEmpty()) {
            int code = Integer.parseInt(ErrorCode);
            if(code>=0 && userModel!=null)
                ans=true;
            else
                ans=false;
        }
        return ans;
    }
}
