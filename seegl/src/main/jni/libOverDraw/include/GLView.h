//
// Created by linus.yang on 2018/8/28.
//

#ifndef GLSL_ANDROID_GLVIEW_H
#define GLSL_ANDROID_GLVIEW_H

#include <iostream>
#include <EGL/egl.h>
#include <malloc.h>
#include <android/log.h>
#include <math.h>
#include <string>


#if __ANDROID_API__ >= 24
#include <GLES3/gl32.h>
#elif __ANDROID_API__ >= 21
#include <GLES3/gl31.h>
#else
#include <GLES3/gl3.h>
#endif

#define DEBUG 1
#define LOG_TAG "GLView"

#define  ALOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#if DEBUG
#define  ALOGV(...) __android_log_print(ANDROID_VERBOSE, LOG_TAG, __VA_ARGS__)
#else
#define ALOGV(...)
#endif



using namespace std;

class GLView {

public:
    GLView();
    void onCreate();
    void onLayout(bool isChanger, float x, float y , float width, float height);

    void onDraw();

protected:
    int loadShader(int type, const GLchar const * shaderCode);
    void checkError();

};

#endif //GLSL_ANDROID_GLVIEW_H

