package com.driveway.APIResponse;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public abstract class BaseResponse implements Serializable {

    @SerializedName("error_code")
    public String ErrorCode;

    @SerializedName("error_message")
    public String ErrorMessage;

    public abstract boolean isValid();
}
