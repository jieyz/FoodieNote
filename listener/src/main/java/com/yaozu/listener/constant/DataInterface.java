package com.yaozu.listener.constant;

/**
 * Created by 耀祖 on 2016/1/30.
 */
public class DataInterface {
    public static String APP_HOST = "http://120.27.129.229:8080/";

    public static String getLoginUrl() {
        return APP_HOST + "TestServers/servlet/LoginServlet";
    }

    public static String getRegisterUrl() {
        return APP_HOST + "TestServers/servlet/RegisterServlet";
    }

    public static String getIsOtherLoginUrl() {
        return APP_HOST + "TestServers/servlet/IsOtherLoginServlet";
    }

    public static String getUploadIconUrl() {
        return APP_HOST + "TestServers/servlet/UploadIconServlet";
    }

    public static String getUserIconUrl(String userid) {
        return APP_HOST + "/TestServers/usericon/" + userid + "_icon";
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    public static String getCheckUserInfoUrl() {
        return APP_HOST + "/TestServers/servlet/CheckUserInfoServlet";
    }

    /**
     * 更新用户信息
     *
     * @return
     */
    public static String getUpdateUserInfoUrl() {
        return APP_HOST + "/TestServers/servlet/UpdateUserInfoServlet";
    }

    /**
     *同意成为好友
     * @return
     */
    public static String getAgreeTobeFriendUrl() {
        return APP_HOST + "TestServers/servlet/AgreeTobeFriendServlet";
    }

    /**
     * 获取好友列表
     * @return
     */
    public static String getFriendIdsUrl() {
        return APP_HOST + "TestServers/servlet/getFriendsIdServlet";
    }

    public static String getUpdatePersonStateUrl(){
        return APP_HOST + "TestServers/servlet/UpdatePersonStateServlet";
    }
}
