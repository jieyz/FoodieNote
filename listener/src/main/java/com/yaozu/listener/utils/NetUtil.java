package com.yaozu.listener.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.yaozu.listener.constant.DataInterface;
import com.yaozu.listener.fragment.social.MyselfFragment;
import com.yaozu.listener.listener.DownLoadIconListener;
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
import java.net.URL;

/**
 * Created by 耀祖 on 2015/12/22.
 */
public class NetUtil {
    public static String USERS_ICON_PATH = FileUtil.getSDPath() + File.separator + "ListenerMusic" + File.separator + "usericons";

    /**
     * 上传文件到服务器
     *
     * @param file
     */
    public static void uploadFile(final Context context, final File file, final UploadListener uploadListener) {
        final int SUCCESS = 1;
        final int FAILED = 0;
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case SUCCESS:
                        if (uploadListener != null) {
                            uploadListener.uploadSuccess();
                        }
                        break;
                    case FAILED:
                        if (uploadListener != null) {
                            uploadListener.uploadFailed();
                        }
                        break;
                }
            }
        };

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
                        Message msg = handler.obtainMessage();
                        msg.what = SUCCESS;
                        handler.sendMessage(msg);
                    } else {
                        Message msg = handler.obtainMessage();
                        msg.what = FAILED;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    Log.e("NetUtil", e.getLocalizedMessage(), e);
                    //Toast.makeText(context, "上传文件失败", Toast.LENGTH_SHORT).show();
                    Log.d("NetUtil", "=====================上传文件失败================");
                    Message msg = handler.obtainMessage();
                    msg.what = FAILED;
                    handler.sendMessage(msg);
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

    public static void downLoadUserIcon(final String iconUrl, final DownLoadIconListener downLoadListener) {
        final int SUCCESS = 1;
        final int FAILED = 0;
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case SUCCESS:
                        if (downLoadListener != null) {
                            Bitmap bmp = (Bitmap) msg.obj;
                            downLoadListener.downLoadSuccess(bmp);
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
                        Message msg = handler.obtainMessage();
                        msg.what = SUCCESS;
                        msg.obj = BitmapFactory.decodeStream(is);
                        handler.sendMessage(msg);
                        is.close();
                    }
                } catch (IOException e) {
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
     * 设置头像
     * 先是从本地取，如果本地没有的话，就从服务器上取，取完之后
     * 再保存到本地，然后再设置头像
     *
     * @param userid
     * @param imageView
     */
    public static void setImageIcon(String userid, final ImageView imageView, boolean isGetfromLocal, boolean isOrigin) {
        final String filePath = USERS_ICON_PATH + File.separator + userid + "_icon";
        final String filePath_origin = USERS_ICON_PATH + File.separator + userid + "_icon_og";
        Bitmap localbitmap = null;
        if (isGetfromLocal) {
            if (isOrigin) {
                localbitmap = getLocalOriginOtherUserIcon(userid);
            } else {
                localbitmap = getLocalOtherUserIcon(userid);
            }
        }
        if (localbitmap != null) {
            if (imageView != null) {
                imageView.setImageBitmap(localbitmap);
            }
        } else {
            downLoadUserIcon(DataInterface.getUserIconUrl(userid), new DownLoadIconListener() {
                @Override
                public void downLoadSuccess(Bitmap bitmap) {
                    if (bitmap != null) {
                        if (imageView != null) {
                            imageView.setImageBitmap(bitmap);
                        }
                        //保存到本地
                        File dir = new File(USERS_ICON_PATH);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        FileUtil.saveOutput(bitmap, filePath_origin);
                        Bitmap smallBitmap = FileUtil.compressUserIcon(200, filePath_origin);
                        FileUtil.saveOutput(smallBitmap, filePath);
                    }
                }

                @Override
                public void downLoadFailed() {

                }
            });
        }
    }

    public static Bitmap getLocalOtherUserIcon(String userid) {
        final String filePath = USERS_ICON_PATH + File.separator + userid + "_icon";
        Bitmap localbitmap = BitmapFactory.decodeFile(filePath);
        return localbitmap;
    }

    /**
     * 原画质
     * @param userid
     * @return
     */
    public static Bitmap getLocalOriginOtherUserIcon(String userid) {
        final String filePath = USERS_ICON_PATH + File.separator + userid + "_icon_og";
        Bitmap localbitmap = BitmapFactory.decodeFile(filePath);
        return localbitmap;
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
