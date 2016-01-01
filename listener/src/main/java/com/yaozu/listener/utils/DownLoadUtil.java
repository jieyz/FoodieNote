package com.yaozu.listener.utils;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by 耀祖 on 2015/12/25.
 */
public class DownLoadUtil {
    /**
     * @param url           请求的URL
     * @param file     保存下载文件的路径
     * @return
     * @Description: 文件下载工具
     * @date Apr 21, 2014 3:49:16 PM
     */
    public static File download(FileUtil fileUtil, String url, File file) {
        try {
            // 创建一个URL对象，urlStr指的是网络IP地址
            URL neturl = new URL(url);
            // 创建一个HttpURLConnection连接
            HttpURLConnection urlConn = (HttpURLConnection) neturl.openConnection();
            urlConn.setConnectTimeout(5000);
            urlConn.setRequestMethod("GET");

            InputStream input = null;
            input = urlConn.getInputStream();
            fileUtil.write2SDFromInput(file, input);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public static String baiduDownLoadLrc(String netUrl){
        URL url = null;
        try {
            url = new URL(netUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                //byte[] data = getBytes(is);
                //String message = new String(data, "gbk");
                String message = getLrcid(is);
                return message;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 把一个inputstream里面的内容 写到一个byte[] 数组里面
     * @param is
     * @return
     */
    public static byte[] getBytes(InputStream is) throws Exception{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while( (len = is.read(buffer))!=-1){
            bos.write(buffer, 0, len);
        }
        is.close();
        return  bos.toByteArray();
    }

    /**
     * 解析服务器返回的inputstream
     *
     * @param is
     * @return 如果是空 代表的是获取数据失败
     */
    public static String getLrcid(InputStream is) {
        String lrcid = null;
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(is, "utf-8");
            int type = parser.getEventType();

            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("lrcid".equals(parser.getName())) {
                            lrcid = parser.nextText();
                            return lrcid;
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        break;
                }

                type = parser.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
