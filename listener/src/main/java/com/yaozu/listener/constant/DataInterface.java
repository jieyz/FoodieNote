package com.yaozu.listener.constant;

/**
 * Created by 耀祖 on 2016/1/30.
 */
public class DataInterface {
    public static String APP_HOST = "http://120.27.129.229:8080/";
    public static String getLoginUrl(){
        return APP_HOST+"TestServers/servlet/LoginServlet";
    }

    public static String getIsOtherLoginUrl(){
        return APP_HOST+"TestServers/servlet/IsOtherLoginServlet";
    }

    public static String getUploadIconUrl(){
        return APP_HOST+"TestServers/servlet/UploadIconServlet";
    }
}
