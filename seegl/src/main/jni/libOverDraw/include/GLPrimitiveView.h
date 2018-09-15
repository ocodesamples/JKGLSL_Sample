//
// Created by linus.yang on 2018/8/29.
//

#ifndef GLSL_ANDROID_GLPRIMITIVE_H
#define GLSL_ANDROID_GLPRIMITIVE_H
#include "GLView.h"

class GLPrimitiveView : public GLView {

private:
    const char const * mVertexShader = "#version 300 es  \n"
            "layout(location = 1)uniform mat4 uMatrix; \n"
            "layout(location = 0)in vec4 a_position;   \n"
            "layout(location = 1)in vec3 a_color;    \n "
            "out vec4 o_color;                      \n"
            "void main()                           \n"
            "{                                    \n"
            "   gl_Position = uMatrix * a_position; \n"
            "   o_color = vec4(a_color, 1.0);       \n"
            "}                                      \n";

    const char const * mFragmentShader = "#version 300 es  \n"
            "precision mediump float;                      \n"
            "in vec4 o_color;                              \n"
            "layout(location = 0) out vec4 o_fragColor;     \n"
            "void main()                                   \n"
            "{                                             \n"
            "          o_fragColor = o_color;              \n"
            "}                                             \n";

protected:
    void init();
    void onChangerSize(bool isChanger, float x, float y, float width, float height);
    void onDraw();
};


#endif //GLSL_ANDROID_GLPRIMITIVE_H
