package com.yaozu.listener.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by 耀祖 on 2015/12/26.
 */
public class FileUtil {
    private static String TAG = FileUtil.class.getSimpleName();
    private String SDPATH;

    /**
     * "/ListenerMusic/lyrics/"
     */
    public String getSDPATH() {
        return SDPATH;
    }

    public FileUtil() {
        //得到当前外部存储设备的目录
        // /SDCARD
        SDPATH = getSDPath() + File.separator + "ListenerMusic" + File.separator + "lyrics" + File.separator;
    }

    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED); //
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//
        }
        if (sdDir == null) {
            return null;
        }
        return sdDir.toString();

    }

    /**
     * 在SD卡上创建文件
     *
     * @throws IOException
     */
    public File creatSDFile(String fileName) throws IOException {
        File file = new File(SDPATH + fileName);
        if (!file.exists()) {
            file.createNewFile();
            Log.d(TAG, "=======创建文件:SDPATH + fileName======>" + SDPATH + fileName);
        }else{
            Log.d(TAG, "=======文件已存在:SDPATH + fileName======>" + SDPATH + fileName);
        }
        return file;
    }

    /**
     * 在SD卡上创建目录
     *
     * @param dirName
     */
    public File creatSDDir(String dirName) {
        File dir = new File(SDPATH + dirName);
        if (!dir.exists()) {
            dir.mkdirs();
            Log.d(TAG,"=======创建文件夹:SDPATH + dirName======>"+SDPATH + dirName);
        } else {
            Log.d(TAG,"=======文件夹已存在:SDPATH + dirName======>"+SDPATH + dirName);
        }
        return dir;
    }

    /**
     * 判断SD卡上的文件夹是否存在
     */
    public boolean isFileExist(String fileName) {
        File file = new File(SDPATH + fileName);
        return file.exists();
    }

    /**
     * 将一个InputStream里面的数据写入到SD卡中
     */
    public void write2SDFromInput(File file, InputStream input) {
        OutputStream output = null;
        try {
/*            creatSDDir(path);
            file = creatSDFile(path + File.separator + fileName);*/
            output = new FileOutputStream(file);
            byte buffer[] = new byte[4 * 1024];
            while ((input.read(buffer)) != -1) {
                output.write(buffer);
            }
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
