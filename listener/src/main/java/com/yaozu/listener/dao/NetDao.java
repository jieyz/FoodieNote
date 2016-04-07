package com.yaozu.listener.dao;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.yaozu.listener.constant.DataInterface;
import com.yaozu.listener.playlist.model.Song;
import com.yaozu.listener.utils.VolleyHelper;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by jieyaozu on 2016/3/14.
 */
public class NetDao {
    /**
     * 获得歌曲在服务器上编过码的文件名
     *
     * @param song
     * @param listener
     * @param errorListener
     */
    public static void getPlaySongEncodeFileName(final Song song, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        String url = null;
        try {
            url = DataInterface.getHaveSongInServerUrl() + "?songname=" + URLEncoder.encode(song.getTitle(), "Utf-8") + "&singer=" + URLEncoder.encode(song.getSinger(), "Utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        VolleyHelper.getRequestQueue().add(new JsonObjectRequest(Request.Method.GET, url, listener, errorListener));
    }

    /**
     * 获得用记的状态
     *
     * @param userid
     * @param listener
     * @param errorListener
     */
    public static void getUserState(String userid, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        String url = DataInterface.getUserStateUrl() + "?userid=" + userid;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, listener, errorListener);
        VolleyHelper.getRequestQueue().add(jsonObjectRequest);
    }

    /**
     * 短信验证码
     * @param phoneNumber
     * @param listener
     * @param errorListener
     */
    public static void getSmsPhoneCode(String phoneNumber, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        String url = DataInterface.getSmsCodeUrl() + "?phonenumber=" + phoneNumber;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, listener, errorListener);
        VolleyHelper.getRequestQueue().add(jsonObjectRequest);
    }
}
