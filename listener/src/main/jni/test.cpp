//
// Created by 耀祖 on 2016/1/5.
//
#include "mediascanner.h"
#include <stdio.h>
#include <stdlib.h>
#include <jni.h>
#include <android/log.h>
#define LOG_TAG "System.out"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
//com.yaozu.listener
namespace android {
    extern "C"
    {
    MediaScanner *mMediaScanner = 0;

    char *Jstring2CStr(JNIEnv *env, jstring jstr) {
        char *rtn = NULL;
        jclass clsstring = (env)->FindClass("java/lang/String");
        jstring strencode = (env)->NewStringUTF("UTF-8");
        jmethodID mid = (env)->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
        jbyteArray barr = (jbyteArray) (env)->CallObjectMethod(jstr, mid,
                                                               strencode); // String .getBytes("GB2312");
        jsize alen = (env)->GetArrayLength(barr);
        jbyte *ba = (env)->GetByteArrayElements(barr, JNI_FALSE);
        if (alen > 0) {
            rtn = (char *) malloc(alen + 1);         //"\0"
            memcpy(rtn, ba, alen);
            rtn[alen] = 0;
        }
        (env)->ReleaseByteArrayElements(barr, ba, 0);  //
        return rtn;
    }

    JNIEXPORT jstring JNICALL Java_com_yaozu_listener_HomeMainActivity_fromC
            (JNIEnv *env, jobject obj, jstring path) {
        if (!mMediaScanner) {
            mMediaScanner = new MediaScanner();
        }
        char *cpath = Jstring2CStr(env, path);
        mMediaScanner->processFile(cpath);
        const char *cstr = ": c from char";
        return (env)->NewStringUTF(strcat(cpath, cstr));
    }
    }
}

