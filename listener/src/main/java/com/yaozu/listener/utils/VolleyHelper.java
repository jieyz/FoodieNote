package com.yaozu.listener.utils;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by 耀祖 on 2016/2/1.
 */
public class VolleyHelper {
    private static RequestQueue mRequestQueue;

    public static RequestQueue getRequestQueue(){
        if(mRequestQueue != null){
            return mRequestQueue;
        }
        return null;
    }

    public static void init(Context context){
        mRequestQueue = Volley.newRequestQueue(context);
    }
}
