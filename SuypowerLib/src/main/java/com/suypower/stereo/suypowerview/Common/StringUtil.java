package com.suypower.stereo.suypowerview.Common;

/**
 * Created by Stereo on 16/8/25.
 */
public class StringUtil {
    public static Boolean isNull(String str){
        return str == null || str.equals("null");
    }

    public static Boolean isNullOrEmpty(String str){
        return isNull(str) || str.length() == 0;
    }
}
