//
// Created by 耀祖 on 2016/1/5.
//
#include <stdio.h>
#include <stdlib.h>
#include <jni.h>
#include "test.h"
//com.yaozu.listener
JNIEXPORT jstring JNICALL Java_com_yaozu_listener_HomeMainActivity_fromC
        (JNIEnv * env, jobject obj){
    char* cstr = "c from char";
    return (*env)->NewStringUTF(env,cstr);
}


