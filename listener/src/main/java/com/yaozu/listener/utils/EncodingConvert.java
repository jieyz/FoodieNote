package com.yaozu.listener.utils;

import java.io.UnsupportedEncodingException;

/**
 * Created by jieyz on 2016/1/13.
 */
public class EncodingConvert {
    public static String toUtf(String str){
        if  (str  ==  null)  return  null;
        String  retStr  =  str;
        byte  b[];
        try  {
            b  =  str.getBytes("iso-8859-1");
            if(b[0] < 0){
                retStr  =  new  String(b,  "utf-8");
            }
        }  catch  (UnsupportedEncodingException e)  {

        }
        return  retStr;
    }
}
