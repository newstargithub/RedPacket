//
// Created by Administrator on 2020/10/26.
//


#include <jni.h>
#include <pthread.h>
#include <android/log.h>
#include <unistd.h>
#include "com_halo_redpacket_jnicall_Native.h"

#define LOG(...) _android_log_print(ANDROID_LOG_DEBUG, "Native", _VA_ARGS)

JavaVM *gJavaVM;
jobject gJavaObj;

static volatile int gIsThreadExit = 0;

JNIEXPORT void JNICALL Java_com_halo_redpacket_jnicall_Native_initilize
  (JNIEnv *env, jobject thiz) {
    (*env)->GetJavaVM(env, &gJavaVM);
    gJavaObj = (*env)->NewGlobalRef(env, thiz);
}

static void* native_thread_exec(void *arg) {
    JNIEnv *env;
    (*gJavaVM)->AttachCurrentThread(gJavaVM, &env, NULL);
    jclass javaClass = (*env)->GetObjectClass(env, gJavaObj);
    if(javaClass == NULL) {
//        LOG("fail to find javaClass");
        return 0;
    }
    jmethodID javaCallback = (*env)->GetMethodID(env, javaClass, "onNativeCallback", "(I)V");
    if(javaCallback == NULL) {
//        LOG("fail to find method onNativeCallback");
        return 0;
    }
//    LOG("native_thread_exec loop enter");
    int count = 0;
    while(!gIsThreadExit) {
        (*env)->CallVoidMethod(env, gJavaObj, javaCallback, count++);
        sleep(1);
    }
    (*gJavaVM)->DetachCurrentThread(gJavaVM);
//    LOG("native_thread_exec loop leave");
}

JNIEXPORT void JNICALL Java_com_halo_redpacket_jnicall_Native_threadStart
  (JNIEnv *env, jobject thiz) {
    gIsThreadExit = 0;
    pthread_t threadId;
    if(pthread_create(&threadId, NULL, native_thread_exec, NULL) != 0) {
//        LOG("pthread create fail");
        return;
    }
//    LOG("native thread start success");
}

JNIEXPORT void JNICALL Java_com_halo_redpacket_jnicall_Native_threadStop
  (JNIEnv *env, jobject thiz) {
    gIsThreadExit = 0;
//    LOG("native thread stop success");
}






