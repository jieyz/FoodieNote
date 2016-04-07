package com.yaozu.listener.constant;

/**
 * Created by 耀祖 on 2016/1/30.
 */
public class DataInterface {
    //生产地址
    public static String APP_HOST = "http://120.27.129.229:8080/";
    //测试地址
    //public static String APP_HOST = "http://192.168.1.20:8080/";

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
     * 同意成为好友
     *
     * @return
     */
    public static String getAgreeTobeFriendUrl() {
        return APP_HOST + "TestServers/servlet/AgreeTobeFriendServlet";
    }

    /**
     * 获取好友列表
     *
     * @return
     */
    public static String getFriendIdsUrl() {
        return APP_HOST + "TestServers/servlet/getFriendsIdServlet";
    }

    /**
     * 通知更新当前用户状态
     *
     * @return
     */
    public static String getUpdatePersonStateUrl() {
        return APP_HOST + "TestServers/servlet/UpdatePersonStateServlet";
    }

    /**
     * 下载歌曲
     *
     * @return
     */
    public static String getDownLoadSongUrl() {
        return APP_HOST + "TestServers/servlet/DownLoadSongServlet";
    }

    /**
     * 上传歌曲
     *
     * @return
     */
    public static String getUpLoadSongUrl() {
        return APP_HOST + "TestServers/servlet/UploadSongServlet";
    }

    public static String getHaveSongInServerUrl() {
        return APP_HOST + "TestServers/servlet/HaveSongServlet";
    }

    public static String getPlaySongEncodeUrl() {
        return APP_HOST + "TestServers/storesong/";
    }

    public static String getTestPlaySongEncodeUrl() {
        return "http://192.168.0.110:8080/" + "TestServers/storesong/";
    }

    public static String getUserStateUrl() {
        return APP_HOST + "TestServers/servlet/getUserStateServlet";
    }

    /**
     * 获取验证码
     * @return
     */
    public static String getSmsCodeUrl() {
        return APP_HOST + "TestServers/servlet/getSmsCodeServlet";
    }
}
