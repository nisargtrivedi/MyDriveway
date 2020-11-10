package com.driveway.Model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class ChatModel implements Serializable {

    public Long timeStamp;
    public Boolean isRead=false;
    public String message="";
    public String messageType="";
    public String receiver="";
    public String sender="";

}
