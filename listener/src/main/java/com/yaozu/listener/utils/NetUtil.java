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

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.yaozu.listener.constant.DataInterface;
import com.yaozu.listener.fragment.social.MyselfFragment;
import com.yaozu.listener.listener.DownLoadIconListener;
import com.yaozu.listener.listener.UploadListener;
import com.yaozu.listener.playlist.model.Song;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

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
    public static Thread uploadFile(final Context context, final Song song, final File file, final UploadListener uploadListener) {
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

        return new Thread(new Runnable() {
            @Override
            public void run() {
                // 创建一个httppost的请求
                PostMethod filePost = new PostMethod(DataInterface.getUpLoadSongUrl());
                NameValuePair songname   = new NameValuePair("songname", song.getTitle());
                NameValuePair singer = new NameValuePair("singer", song.getSinger());
                filePost.setRequestBody(new NameValuePair[]{songname,singer});
                try {
                    filePost.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
                    User user = new User(context);
                    // 组拼上传的数据
                    Part[] parts = {new StringPart("songname", song.getTitle() == null ? "" : song.getTitle(), "UTF-8"),
                            new StringPart("userid", user.getUserAccount(), "UTF-8"),
                            new StringPart("singer", song.getSinger() == null ? "" : song.getSinger(), "UTF-8"),
                            new StringPart("album", song.getAlbum() == null ? "" : song.getAlbum(), "UTF-8"),
                            new StringPart("filename", song.getFileName() == null ? "" : song.getFileName(), "UTF-8"),
                            new FilePart("file", file, null, "UTF-8")};
                    filePost.setRequestEntity(new MultipartRequestEntity(parts,
                            filePost.getParams()));
                    HttpClient client = new HttpClient();
                    client.getHttpConnectionManager().getParams()
                            .setConnectionTimeout(5000);
                    int status = client.executeMethod(filePost);
                    if (status == 200) {
                        //Toast.makeText(context, "上传文件成功", Toast.LENGTH_SHORT).show();
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
                    Message msg = handler.obtainMessage();
                    msg.what = FAILED;
                    handler.sendMessage(msg);
                } finally {
                    filePost.releaseConnection();
                }
            }
        });
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
     * @param isGetfromLocal  是否优先从本地缓存中取
     * @param isOrigin  是否取得原始大小的图片
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
     *
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

    /**
     * 检查是否已在服务器上，而后决定要不要上传
     * @param song
     * @param listener
     * @param errorListener
     */
    public static void uploadSongIfNotExist(final Song song,Response.Listener<JSONObject> listener, Response.ErrorListener errorListener){
        String url = null;
        try {
            url = DataInterface.getHaveSongInServerUrl()+"?songname="+ URLEncoder.encode(song.getTitle(), "Utf-8")+"&singer="+URLEncoder.encode(song.getSinger(), "Utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        VolleyHelper.getRequestQueue().add(new JsonObjectRequest(Request.Method.GET, url,listener, errorListener));
    }
}
