package com.yaozu.listener.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

import java.io.File;

/**
 * Created by 耀祖 on 2015/12/22.
 */
public class NetUtil {
    /**
     * 上传文件到服务器
     *
     * @param file
     */
    public static void uploadFile(final Context context, final File file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 创建一个httppost的请求
                PostMethod filePost = new PostMethod(
                        "http://120.27.129.229:8080/TestServers2/servlet/UploadServlet3");
                try {
                    // 组拼上传的数据
                    Part[] parts = {new StringPart("source", "695132533"),
                            new StringPart("status", "lisi"),
                            new FilePart("file", file)};
                    filePost.setRequestEntity(new MultipartRequestEntity(parts,
                            filePost.getParams()));
                    HttpClient client = new HttpClient();
                    client.getHttpConnectionManager().getParams()
                            .setConnectionTimeout(5000);
                    int status = client.executeMethod(filePost);
                    if (status == 200) {
                        //Toast.makeText(context, "上传文件成功", Toast.LENGTH_SHORT).show();
                        Log.d("NetUtil", "=====================上传文件成功================");
                    }
                } catch (Exception e) {
                    Log.e("NetUtil", e.getLocalizedMessage(), e);
                    //Toast.makeText(context, "上传文件失败", Toast.LENGTH_SHORT).show();
                    Log.d("NetUtil", "=====================上传文件失败================");
                } finally {
                    filePost.releaseConnection();
                }
            }
        }).start();
    }
}
