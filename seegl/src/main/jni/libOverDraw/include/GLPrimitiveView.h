//
// Created by linus.yang on 2018/8/29.
//

#ifndef GLSL_ANDROID_GLPRIMITIVE_H
#define GLSL_ANDROID_GLPRIMITIVE_H
#include "GLView.h"

class GLPrimitiveView : public GLView {

protected:
    void init();
    void onChangerSize(bool isChanger, float x, float y, float width, float height);
    void onDraw();
};


#endif //GLSL_ANDROID_GLPRIMITIVE_H
