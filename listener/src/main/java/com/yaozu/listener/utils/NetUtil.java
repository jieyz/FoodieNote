package com.yaozu.listener.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.yaozu.listener.constant.DataInterface;
import com.yaozu.listener.fragment.social.MyselfFragment;
import com.yaozu.listener.listener.DownLoadListener;
import com.yaozu.listener.listener.UploadListener;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by 耀祖 on 2015/12/22.
 */
public class NetUtil {
    /**
     * 上传文件到服务器
     *
     * @param file
     */
    public static void uploadFile(final Context context, final File file, final UploadListener uploadListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 创建一个httppost的请求
                PostMethod filePost = new PostMethod(
                        "http://120.27.129.229:8080/TestServers/servlet/UploadServlet3");
                try {
                    User user = new User(context);
                    // 组拼上传的数据
                    Part[] parts = {new StringPart("source", "695132533"),
                            new StringPart("userid", user.getUserAccount()),
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
                        if (uploadListener != null) {
                            uploadListener.uploadSuccess();
                        }
                    } else {
                        if (uploadListener != null) {
                            uploadListener.uploadFailed();
                        }
                    }
                } catch (Exception e) {
                    Log.e("NetUtil", e.getLocalizedMessage(), e);
                    //Toast.makeText(context, "上传文件失败", Toast.LENGTH_SHORT).show();
                    Log.d("NetUtil", "=====================上传文件失败================");
                    if (uploadListener != null) {
                        uploadListener.uploadFailed();
                    }
                } finally {
                    filePost.releaseConnection();
                }
            }
        }).start();
    }

    /**
     * 上传头像到服务器
     *
     * @param file
     */
    public static void uploadIconFile(final Context context, final File file, final UploadListener uploadListener) {
        // 创建一个httppost的请求
        PostMethod filePost = new PostMethod(DataInterface.getUploadIconUrl());
        try {
            User user = new User(context);
            // 组拼上传的数据
            Part[] parts = {new StringPart("source", "695132533"),
                    new StringPart("userid", user.getUserAccount()),
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
                if (uploadListener != null) {
                    uploadListener.uploadSuccess();
                }
            } else {
                if (uploadListener != null) {
                    uploadListener.uploadFailed();
                }
            }
        } catch (Exception e) {
            Log.e("NetUtil", e.getLocalizedMessage(), e);
            //Toast.makeText(context, "上传文件失败", Toast.LENGTH_SHORT).show();
            Log.d("NetUtil", "=====================上传文件失败================");
            if (uploadListener != null) {
                uploadListener.uploadFailed();
            }
        } finally {
            filePost.releaseConnection();
        }
    }

    public static void downLoadUserIcon(final String iconUrl, final DownLoadListener downLoadListener) {
        final int SUCCESS = 1;
        final int FAILED = 0;
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case SUCCESS:
                        if (downLoadListener != null) {
                            downLoadListener.downLoadSuccess();
                        }
                        break;
                    case FAILED:
                        if (downLoadListener != null) {
                            downLoadListener.downLoadFailed();
                        }
                        break;
                }
            }
        };
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                URL url;
                try {
                    url = new URL(iconUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    InputStream is = conn.getInputStream();
                    if (is == null) {
                        Message msg = handler.obtainMessage();
                        msg.what = FAILED;
                        handler.sendMessage(msg);
                    } else {
                        // 保存文件到sd卡
                        File file = new File(MyselfFragment.ICON_PATH);
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        FileOutputStream fos = new FileOutputStream(file);
                        byte[] bytes = new byte[1024];
                        int len = -1;
                        while ((len = is.read(bytes)) != -1) {
                            fos.write(bytes, 0, len);
                        }
                        is.close();
                        fos.close();
                        Message msg = handler.obtainMessage();
                        msg.what = SUCCESS;
                        handler.sendMessage(msg);
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                    Message msg = handler.obtainMessage();
                    msg.what = FAILED;
                    handler.sendMessage(msg);
                }
            }
        };
        new Thread(runnable).start();
    }

    /**
     * 获得网络连接是否可用
     *
     * @param context
     * @return
     * @author 揭耀祖
     */
    public static boolean hasNetwork(Context context) {
        ConnectivityManager con = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo workinfo = con.getActiveNetworkInfo();
        if (workinfo == null || !workinfo.isAvailable()) {
            //Toast.makeText(context, R.string.net_error, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
