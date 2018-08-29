//
// Created by linus.yang on 2018/8/27.
//

#include <jni.h>
#include <android/log.h>
#include "GLPrimitiveView.h"


GLView * mView;

extern "C" JNIEXPORT void
JNICALL
Java_com_seegl_ui_GLNativeRenderer_onSurfaceCreated(
        JNIEnv *env,
        jobject /* this */) {
    mView = new GLPrimitiveView();
    mView->onCreate();
}


extern "C" JNIEXPORT void
JNICALL
Java_com_seegl_ui_GLNativeRenderer_onSurfaceChanged(
        JNIEnv *env,
        jobject obj, jint width, jint height) {
    mView->onLayout(false, 0, 0, width, height);
}

extern "C" JNIEXPORT void
JNICALL
Java_com_seegl_ui_GLNativeRenderer_onDrawFrame(
        JNIEnv *env,
        jobject obj) {
    mView->onDrawFrame();
}