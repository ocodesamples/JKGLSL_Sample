//
// Created by linus.yang on 2018/8/27.
//

#include <jni.h>
#include <string>
#include <android/log.h>

extern "C" JNIEXPORT void
JNICALL
Java_com_seegl_ui_GLNativeRenderer_onSurfaceCreated(
        JNIEnv *env,
        jobject /* this */) {

}


extern "C" JNIEXPORT void
JNICALL
Java_com_seegl_ui_GLNativeRenderer_onSurfaceChanged(
        JNIEnv *env,
        jobject obj, jint width, jint height) {
}

extern "C" JNIEXPORT void
JNICALL
Java_com_seegl_ui_GLNativeRenderer_onDrawFrame(
        JNIEnv *env,
        jobject obj) {
}