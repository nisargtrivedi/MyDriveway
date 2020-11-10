package com.driveway.Utility;


import java.util.logging.Level;
import java.util.logging.Logger;

public  class AppLogger {

    public static void info(String str) {
        Logger.getLogger("mydriveway").info(str);
    }

    public static void err(String str, Exception ex) {
        Logger.getLogger("mydriveway").log(Level.INFO,str,ex);
    }

    public static void err(Exception ex) {
        Logger.getLogger("mydriveway").log(Level.INFO,"",ex);
    }
}
